/**
 * 
 */
var stock_ok=false;
var stockCheck_url = '/stock/hk/' + _accountId + '/stockCheck.jspa';
var stockLimit_url = '/stock/hk/' + _accountId + '/stockLimit.jspa';
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
	//txtbox-up-down
	
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
		var canBuy = $('#canBuy').val();
		var lotsz=$('#lotsz').text();
		if(typeof lotsz != 'undefined' && !isNaN(lotsz)){
			lotsz=parseInt(lotsz);
		}else{
			return;
		}
		var val = Math.floor((canBuy/quart)/lotsz)*lotsz;
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
	});
	$("#entrustAmount").keyup(function(){
		XJB.Utils.clearNoNum(this,-1);
		clearError('entrustAmount');
	});
	$("#price").bind("change",function(){
		if(!validStockCode()){
			return ;
		}
		stockLimit();		
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
	
	$('.error-msg-bar-wrap i').click(function(){
		$(this).parent().hide().find('span').text('');
	});
	$("#btnSubmit").click(function(){
		buyCheckSubmit();
		return false;
	});
})
function stockCheck(){
	var stockCode = $("#stockCode").val(),
		marketType =$("#marketType").val(),
		stockType =$("#stockType").val();
	clearError();
	if(stockCode==undefined || stockCode==""){
		return false;
	}
	$.ajax({
		type : "get",
		url : stockCheck_url,
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
			  if(typeof data.errMsg != 'undefined'){
				  showError('stockCode',data.errMsg);
				  clearInfo();	
			  }else{
				  $("#canFund").empty().html(data.enableBalance);
				  //$("#stockName").empty().html(data.stockName);
				  //$("#price").val(data.lastPrice);
				  if($('#price').attr('disabled')=='disabled'){
					$("#price").val('竞价交易');
				  }else{
					$("#price").val('0.00').focus().select();
				  }
				  $('#lotsz').text(data.lotsz);
				  entrustAmountInput.setStep(parseInt(data.lotsz));
				  priceInput.setStep(parseFloat(data.sasr));
				  if($('#price').attr('disabled')=='disabled'){
					stockLimit();
				  }
			  }				  
		 },
		error : function(){
			window.location.reload();
		}
	});
}

function stockLimit(){
	clearError();
	var stockCode =$("#stockCode").val();
	var marketType =$("#marketType").val();
	var stockType =$("#stockType").val();
	var entrustPrice =$("#price").val();
	var params={
			"stockCode" : stockCode,
			"marketType" : marketType,
			"stockType" : stockType
		}
	if($('#price').attr('disabled')!='disabled'){
		$.extend(params,{"entrustPrice" : entrustPrice});
	}
	$.ajax({
		type : "get",
		url : stockLimit_url,
		data : params,
	  	dataType : "json",
	  	timeout : 5000,
	  	cache : false,
		async : true,
	  	success:function(data){
			if(typeof data.hk_password_error != 'undefined'){
				window.location.href = '/stock/hk/' + _accountId + '/hkPasswords.jspa?returnUrl=' + window.location.pathname;
			}
			if(data== undefined || data.errMsg){
				stock_ok = false;
				if(data.errMsg){
					showError('price',data.errMsg);
				}else{
					showGloableError('获取限制信息失败');
				}
			}else{
				stock_ok= true;
				$("#canBuy").attr("data",data.availableQuantity);
				$("#canBuy").val(data.availableQuantity);	
				$("#entrustAmount").focus();
				updateCost("buy",$('#price').val(),$('#canBuy').val(),$('#lotsz').text());
				highLightPrice($('#price').val());
			}	
		},
		error : function(){
			window.location.replace('/stock');
		}
	})
	
}

function buyCheckSubmit(){
	var stockCode = $("#stockCode").val().trim();
	var enableCount =$("#canBuy").attr("data");
	var buyCount =$("#entrustAmount").val();
	var buyPrice = $("#price").val();
	var stockCode_show=$('#stockCode_show').val().split(' ')[0];
	var lotsz = $('#lotsz').text();
	if(stockCode == "" || stockCode != stockCode_show){
		showError('stockCode','请输入正确的股票代码');
		return false;	
	}
	if($('#price').attr('disabled')!='disabled' && !stock_ok){
		showError('price','您输入的委托价格有误，请检查');
		return false;
	}
	if(""==buyCount  || !/^[0-9]*$/.test(buyCount) || buyCount<1){
		showError('entrustAmount','数量填写错误');
		return false;
	}
	if(buyCount%lotsz !=0){
		showError('entrustAmount','购买数量必须是'+lotsz+'的整数');
		return false;
	}
	
	if($('#price').attr('disabled')!='disabled' && (isNaN(parseFloat(buyPrice)) || parseFloat(buyPrice) <= 0)){
		showError('price','价格输入错误');
		return false;
	}
	
	if(!/^[0-9]*$/.test(buyCount)){
		showError('entrustAmount','数量填写错误');
		return false;
	}
	if($('#price').attr('disabled')!='disabled' && (buyCount  - enableCount)>0){
		showError('entrustAmount','买入数量不能大于最大可买数');
		return false;
	}
	
	var matchingType = $('#matchingType').val();
	//$("#buyCheckForm").submit();
	clearError();
	$.ajax({
		type : "post",
		url : "/stock/hk/" + _accountId + "/ajaxBuyStep2.jspa",
		data : {stockCode : stockCode, entrustPrice : buyPrice, entrustAmount : buyCount,"matchingType" : matchingType},
		dataType : "json",
	  	timeout : 5000,
	  	cache : false,
		success : submitCallBack,
		error : function(){window.location.reload();}
	});
}
function validStockCode(){
	var stockCode = $("#stockCode").val();	
	if(stockCode=="代码/简称/拼音" ||stockCode == undefined || stockCode ==""){
		return false;
	}
	return true;
}
function  clearInfo(){
	  //$("#canFund").empty().html("");
	  //$("#stockName").empty().html("");
	  $("#price").val("");
	  $("#canBuy").attr("data",0);
	  $("#canBuy").val("");
	  $('#entrustAmount').val('');
}

function initType(){
	var stockCode =$("#stockCode").val();
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
    	stockCheck();
    });	
}
