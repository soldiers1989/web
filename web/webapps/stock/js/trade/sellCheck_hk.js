/**
 * 
 */
var stockLimit_url = "/stock/hk/"+ _accountId +"/sellLimit.jspa";
$(document).ready(function(){
	//txtbox-up-down
	priceInput = new JRJ.cfo.adjustINPUT({
		id : 'price',
		step : 0.01,
		percision : 3,
		callBack : function(){
			highLightPrice($('#price').val());
		}
	});
	entrustAmountInput = new JRJ.cfo.adjustINPUT({
		id : 'entrustAmount',
		step : 100,
		percision : 0
	});
	//--txtbox-up-down
	
	//checkbox
	$('.custom-checkbox li').mouseenter(function () {
		if (!$(this).hasClass('checked') && !$(this).hasClass('disable')) {
			$(this).addClass('hover');
		}
	}).mouseleave(function () {
		$(this).removeClass('hover');
	}).click(function () {
		//$(this).siblings('li').removeClass('checked');
		//$(this).addClass('checked');
		if($(this).hasClass('disable'))return;
		var quart = parseInt($(this).data('quart'));
		var enableSell = $('#enableSell').val();
		var lotsz=$('#lotsz').text();
		if(typeof lotsz != 'undefined' && !isNaN(lotsz)){
			lotsz=parseInt(lotsz);
		}else{
			return;
		}
		var val = Math.floor((enableSell/quart)/lotsz)*lotsz;
		if(val < lotsz){return;}
		$('#entrustAmount').val(val);
	});
	//--checkbox
	//table
	$('.col-2').find('td').mouseenter(function () {
		$(this).parent().addClass('hover');
	}).mouseleave(function () {
		$(this).parent().removeClass('hover');
	}).click(function () {
		if (!$(this).parent().hasClass('title')) {
			$('.col-2 table tr').removeClass('cur');
			var price = $(this).parent().addClass('cur').find('td:first>span').text();
			if(price!='' && !isNaN(price)){
				$('#price').val(price).focus().change();
			}
		}
	});
	
	$('.col-3').find('td').mouseenter(function () {
		$(this).parent().addClass('hover');
	}).mouseleave(function () {
		$(this).parent().removeClass('hover');
	});
	//--table
	$('#stockCode').keyup(function(){
		clearError('stockCode');
	});
	
	$("#price").keyup(function(){
		XJB.Utils.clearNoNum(this,3);
		clearError('price');
	}).blur(function(){
		highLightPrice($('#price').val());
	}).change(function(){
		updateCost("sell",$('#price').val(),$('#enableSell').val(),$('#lotsz').text());
	});
	$("#entrustAmount").keyup(function(){
		XJB.Utils.clearNoNum(this,-1);
		clearError('entrustAmount');
	});
	$('#matchingType').change(function(){
		var value=$(this).val();
		if(value=='AO'){
			$('#price').attr('disabled','disabled');
		}else{
			$('#price').removeAttr('disabled')
		}
		clearInfo();
		clearError();
	});
	$('#matchingType').trigger('change');
	$("#btnSubmit").click(function(){
		checkSubmit();
		return false;
	});
		
});
function checkSell(){
	stockAble= false;
	var stockCode = $("#stockCode").val();
	$.ajax({
		type : "get",
		url : stockLimit_url,
		data :{
			"stockCode" : stockCode
		  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		success:function(data){
			if(typeof data.hk_password_error != 'undefined'){
				window.location.href = '/stock/hk/' + _accountId + '/hkPasswords.jspa?returnUrl=' + window.location.pathname;
			}
			  if(data.errMsg){
				showError('stockCode',data.errMsg);  
				clearInfo();
			  }else{
				  stockAble=true;
				  $("#enableSell").attr("data" ,data.availableQuantity).val(data.availableQuantity);
				  //$("#marketValue").empty().html(data.marketValue);
				  //$("#stockName").attr("data" ,data.instrumentName).empty().html(data.instrumentName);
				  //$("#price").val(data.lastPrice);
				  if($('#price').attr('disabled')=='disabled'){
					$("#price").val('竞价交易');
					$("#entrustAmount").focus();
				  }else{
					$("#price").val('0.00').focus().select();
				  }
				  $('#lotsz').text(data.lotsz);
				  entrustAmountInput.setStep(parseInt(data.lotsz));
				  priceInput.setStep(parseFloat(data.sasr));
				  //
				  clearError('stockCode');
				  //updateCost("sell",$('#price').val(),$('#enableSell').val(),data.lotsz);
				  //highLightPrice($('#price').val());
			  }				  
		 },
		error : function(){
			window.location.reload();
		}
	});	
}
function checkSubmit(){
	var stockCode = $("#stockCode").val().trim();
	var enableCount =$("#enableSell").attr("data");
	var sellCount =$("#entrustAmount").val();
	var sellPrice = $("#price").val();
	var stockCode_show=$('#stockCode_show').val();
	if(!stockAble || stockCode ==undefined || stockCode_show=="代码/简称/拼音" || stockCode=="" || stockCode != stockCode_show.split(' ')[0]){
		showError('stockCode','请输入正确的股票代码');
		return false;	
	}
	if($('#price').attr('disabled')!='disabled' && isNaN(parseFloat(sellPrice))){
		showError('price','价格输入错误');
		return false;
	}
	
	if("" ==sellCount || !/^[0-9]*$/.test(sellCount) ||sellCount<1){
		showError('entrustAmount','数量填写错误');
		return false;
	}
	if($('#price').attr('disabled')!='disabled' && (sellCount  - enableCount)>0){
		showError('entrustAmount','卖出数量不能大于最大可卖');
		return false;
	}
	//$("#sellCheckForm").submit();
	var matchingType = $('#matchingType').val();
	clearError();
	$.ajax({
		type : "post",
		url : "/stock/hk/" + _accountId + "/ajaxSellStep2.jspa",
		data : {stockCode : stockCode, entrustPrice : sellPrice, entrustAmount : sellCount, "matchingType" : matchingType},
		dataType : "json",
	  	timeout : 5000,
	  	cache : false,
		success : submitCallBack,
		error : function(){window.location.reload();}
	});
}
function clearInfo(){
	$("#enableSell").attr("data" ,0).val('');
	//$("#marketValue").empty().html("");
	//$("#stockName").attr("data" ,"").empty().html("");
	$("#price").val("");
	$('#entrustAmount').val('');
}
function initType(){
	var stockCode =$("#stockCode").val()
    $.getScript("/stock/code?1=1&inc=utf8&otc=utf-8&type=hk&key="+stockCode ,function(){
    	if(!SCodeData || !SCodeData.CodeData || SCodeData.CodeData.length <1){
    		$("#marketType").val("");
        	$("#stockType").val("");
			$("#stockCode").val('');
    	}else{
    		$("#marketType").val(SCodeData.CodeData[0].mrkt);
        	$("#stockType").val(SCodeData.CodeData[0].type);
			$("#stockCode_show").val(SCodeData.CodeData[0].code+ '  ' + SCodeData.CodeData[0].name);
    	}
		var _stockCode = $("#stockCode").val();
		var flag_autoFocus = true;
		if(_stockCode !="代码/简称/拼音" && _stockCode !=""){
				flag_autoFocus =false;
		}
		XJB.HQ.searchBox("stockCode_show",null,hq_call_back,flag_autoFocus);
    	checkSell();
    });	
}

















