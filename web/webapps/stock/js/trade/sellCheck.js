/**
 * 
 */
var stockLimit_url = "/stock/"+ _accountId +"/sellLimit.jspa";
var jrjcfoinput;
var jrjamountinput;
var _stockType="";
$(document).ready(function(){
	//txtbox-up-down
	jrjcfoinput=new JRJ.cfo.adjustINPUT({
		id : 'price',
		step : 0.01,
		percision : 2,
		callBack : function(){
			highLightPrice($('#price').val());
		}
	});
	jrjamountinput=new JRJ.cfo.adjustINPUT({
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
		if(quart==1){
			$('#entrustAmount').val(enableSell);
			return;
		}
		var val = Math.floor((enableSell/quart)/100)*100;
//		if(val < 100){return;}
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
		XJB.Utils.clearNoNum(this,2);
		clearError('price');
	}).blur(function(){
		highLightPrice($('#price').val());
	}).change(function(){
		updateCost("sell",$('#price').val(),$('#enableSell').val(),null,_stockType);
	});
	$("#entrustAmount").keyup(function(){
		XJB.Utils.clearNoNum(this,-1);
		clearError('entrustAmount');
	});

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
			  if(data.errMsg){
				showError('stockCode',data.errMsg);  
				clearInfo();
			  }else{
				  stockAble=true;
				  $("#enableSell").attr("data" ,data.enableAmount).val(data.enableAmount);
				  $("#stockName").attr("data" ,data.stockName).empty().html(data.stockName);
				  //$("#price").val(data.lastPrice);
				  $("#entrustAmount").focus();
				  clearError('stockCode');
				  updateCost("sell",$('#price').val(),$('#enableSell').val(),null,_stockType);
				  highLightPrice($('#price').val());
			  }				  
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
	if(isNaN(parseFloat(sellPrice))){
		showError('price','价格输入错误');
		return false;
	}
	
	if("" ==sellCount || !/^[0-9]*$/.test(sellCount) ||sellCount<1){
		showError('entrustAmount','数量填写错误');
		return false;
	}
	if((sellCount  - enableCount)>0){
		showError('entrustAmount','卖出数量不能大于最大可卖');
		return false;
	}
	//$("#sellCheckForm").submit();
	clearError();
	$.ajax({
		type : "post",
		url : "/stock/" + _accountId + "/ajaxSellStep2.jspa",
		data : {stockCode : stockCode, entrustPrice : sellPrice, entrustAmount : sellCount},
		dataType : "json",
	  	timeout : 5000,
	  	cache : false,
		success : submitCallBack
	});
}
function clearInfo(){
	$("#enableSell").attr("data" ,0).val('');
	//$("#marketValue").empty().html("");
	//$("#stockName").attr("data" ,"").empty().html("");
	$("#price").val("");
	$('#entrustAmount').val('');
}
function initType(callback){
	var stockCode =$("#stockCode").val()
    $.getScript("/stock/code?1=1&inc=utf8&otc=utf-8&type=cn_sa,fc&key="+stockCode ,function(){
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
		XJB.HQ.searchBox("stockCode_show",null,hq_call_back,flag_autoFocus,true);
    	checkSell();
    	callback(_stockCode);
    });	
}
function changepricelimit(step,percision){
	jrjcfoinput.setStep(step);
	jrjcfoinput.setPercision(percision);
	$("#price").unbind('keyup');
	$("#price").keyup(function(){
		XJB.Utils.clearNoNum(this,percision);
		clearError('price');
	});
	
}
var amountUnit="股";
function changeAmountStep(stock){
	if(stock.type.indexOf("r")!=-1){
		if(stock.mrkt=="cn.sz"){
			jrjamountinput.setStep(10);
		}else{
			jrjamountinput.setStep(100);
		}
		$(".amountunit").text("张");
		amountUnit="张";
	}else{
		jrjamountinput.setStep(100);
		$(".amountunit").text("股");
		amountUnit="股";
	}
}