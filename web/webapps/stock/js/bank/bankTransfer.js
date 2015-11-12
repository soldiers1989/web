/**
 * 
 */
var  issubmit = true;
var successs_content='转账成功，流水号：{entrustNo}，您可点击确认查询流水记录';
$(document).ready(function(){
	$(".bank_pwd").mouseover(function(){
		$(this).children().first().show();
	});
	$(".fund_pwd").mouseover(function(){
		$(this).children().first().show();
	});
	$(".trade_pwd").mouseover(function(){
		$(this).children().first().show();
	});
	$(".no-safety-controller").mouseout(function(){
		$(".no-safety-controller-tip").hide();
	});
	
	$("#balance").keyup(function(){
		XJB.Utils.clearNoNum(this,2);
	});
	$("#transferType").change(function(){
		issubmit = false;
		var value = $(this).val();
		if(value==1){//转入
			$("#transfer_submit").removeClass("out borker-btn-in").addClass("borker-btn-in");
		}else if(value==2){//转出
			$("#transfer_submit").removeClass("out borker-btn-in").addClass("out");
		}
		
		getpassword();
	})
	$("#bankNo").change(function(){
		issubmit = false;
		getpassword();
	})
	//$("#transfer_submit").click(function(){
	//	if(validFrom()){
	//		transferSubmit();
	//	}		
	//});
})
function submitForm(accountId){
	if(validFrom()){
		transferSubmit(accountId);
	}
}
function validBalance(){
	var bankNo = $("#bankNo").val();
	if(bankNo == ""){
		//隐藏页面上的密码控制
		//$("#bankpass").hide();
	    JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				autoClose:false,
				message: "请选择银行!"     //提示语
			  });
		return false;
	}
	var balance = $("#balance").val();
	if(isNaN(parseFloat(balance))||parseFloat(balance)<=0){
		$("#bankpass").hide();
		JRJ.Alerts.alert({
			title: "消息提示",  //标题
			message: "转账金额输入错误",    //提示语
		    width:400,
		    autoClose:false,
		    okCallback: callback, //ok回调
			cancelCallback: callback //cancel回调，关闭时也触发  
		  });
		return false;
	}
	return true;
}
function callback(){
	$("#bankpass").show();
}

function validFrom(){
	if(!issubmit){
		return false;
	}
	
	return validBalance();
	
}

function transferSubmit(accountId){
	$("#bankpass_error").html("");
	var transferType =$("#transferType").val();
	var bankNo = $("#bankNo").val();
	var bankAcc = $("#bankAcc").val();
	var balance = $("#balance").val();
	var bankPassword = "";
	var fundPassword = "";
	var tradePassword = "";
	var userMac ="";
	bankpasswords.check(function (states){
        if(states == 0){
        	bankPassword = bankpasswords.value("bankPassword");
        	fundPassword = bankpasswords.value("fundPassword");
        	tradePassword = bankpasswords.value("tradePassword");
        	
        	$.ajax({
        		type : "post",
        		url : "/stock/"+accountId+"/bankTransfer.jspa",
        		data:{
        			"transferType":transferType,
        			"bankNo" :bankNo,
        			"bankAcc":bankAcc,
        			"balance" :balance,
        			"bankPassword" :bankPassword,
        			"fundPassword" :fundPassword,
        			"tradePassword" :tradePassword,
        			"userMac":userMac,
        			"local_network":bankpasswords._mac,
        			"local_disk":bankpasswords._disk,
        			"local_cpu":bankpasswords.nic
        		},
        		dataType : "json",
        		timeout : 5000,
        		cache : false,
        		success : function(data){
        			 $("#bankpass").hide();
        			 if(data.errCode&&"-406"==data.errCode){
     					showTradePwdDlg(accountId,function(){
     						transferSubmit(accountId);
     					},function(){
     						$("#bankpass").show();
     					});
     					return;
        			 }else if(data.errMsg){
        				 JRJ.Alerts.alert({
        						title: "消息提示",  //标题
        						width:400,
        						message: data.errMsg,     //提示语
        						autoClose:false,
        					    okCallback: callback, //ok回调
        						cancelCallback: callback //cancel回调，关闭时也触发  
        					  }); 
        			  }else{
        				  var content =successs_content.replace("{entrustNo}",data.entrustNo);
						  
        				  JRJ.Alerts.alert({
        						title: "消息提示",  //标题
        						width:400,
        						message: content,     //提示语
        						autoClose:false,
        					    okCallback: callBack_success, //ok回调
        						cancelCallback: callBack_success //cancel回调，关闭时也触发  
        					  }); 
        			  }				  
        		 },
				error : function(XMLHttpRequest, textStatus, errorThrown){
					JRJ.Alerts.alert({
						title: "消息提示",  //标题
						width:400,
						message: errorThrown,     //提示语
						autoClose:false,
						okCallback: callback, //ok回调
						cancelCallback: callback //cancel回调，关闭时也触发  
					  });
				}
        	});
			
		}else if(states == -99){
        	$("#bankpass").hide();
        	JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "为了您的交易安全，请安装安全控件!",     //提示语
				autoClose:false,
			    okCallback: callback, //ok回调
				cancelCallback: callback //cancel回调，关闭时也触发  
			  });  
        }else if(states == -100){
        	//$("#bankpass_error").html("密码不能为空!");
		    // return false;
        	$("#bankpass").hide();
        	JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "交易密码不能为空!",     //提示语
				autoClose:false,
			    okCallback: callback, //ok回调
				cancelCallback: callback //cancel回调，关闭时也触发  
			  });  
        }else if(states == -101){
        	//$("#bankpass_error").html("密码不能为空!");
		    // return false;
        	$("#bankpass").hide();
        	JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "资金密码不能为空!",     //提示语
				autoClose:false,
			    okCallback: callback, //ok回调
				cancelCallback: callback //cancel回调，关闭时也触发  
			  });  
        }else if(states == -102){
        	//$("#bankpass_error").html("密码不能为空!");
		    // return false;
        	$("#bankpass").hide();
        	JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "银行密码不能为空!",     //提示语
				autoClose:false,
			    okCallback: callback, //ok回调
				cancelCallback: callback //cancel回调，关闭时也触发  
			  });  
        }
    });
}

function callBack_success(){
	window.location.href="/stock/"+_accountId+"/fundQuery.jspa";
}

function getpassword(){
	var type = $("#transferType").val();
	var bankNo = $("#bankNo").val();
	$.ajax({
		type : "get",
		url : "/stock/getPasswordAjax.jspa",
		data:{
			"type":type,
			"bankNo" :bankNo			
		},
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success:function(data){
			  if(data != undefined){			  
				 if(data.bankPassword != undefined && data.bankPassword ==1){
					 $("#bank_password").show();
					 $("#bank_password").data("password",1);
				 }else{
					 $("#bank_password").hide();
					 $("#bank_password").data("password",0);
				 }
				 
				 if(data.fundPassword != undefined && data.fundPassword ==1){
					 $("#fund_password").show();
					 $("#fund_password").data("password",1);
				 }else{
					 $("#fund_password").hide();
					 $("#fund_password").data("password",0);
				 }
				 
				 if(data.tradePassword != undefined && data.tradePassword ==1){
					 $("#trade_password").show();
					 $("#trade_password").data("password",1);
				 }else{
					 $("#trade_password").hide();
					 $("#trade_password").data("password",0);
				 }
				 $("#bank_password").val("");
				 $("#fund_password").val("");
				 $("#trade_password").val("");
				 issubmit=true;
			  }			  
		 }		  
	});
}
//给bankNo赋值班
function setBankNo(bankNo,bankAcc,zType,qsFlag){
	$("#bankpass").html("");
	$("#bankNo").val(bankNo);
	$("#bankAcc").val(bankAcc);
	$("#moneyValueDiv").hide();
	$("#moneys").html("");
	$("#balanceButton").show();
	if(bankpasswords != null && bankpasswords!="" && bankpasswords!="undefined"){
		bankpasswords.destory();
	}
	var height = 170;
	if(qsFlag == 'ZJZQ'){
		height = 47;
	}
	bankpasswords = new JRJZQTPassword("#bankpass",_accountId,zType,bankNo,300, height);
	//动态查询左侧密码框提示
	getpasswords();
}
/**
 * 动态读取文本框名字提示
 */
function getpasswords(){
	var type = $("#transferType").val();
	var bankNo = $("#bankNo").val();
	$.ajax({
		type : "get",
		url : "/stock/"+_accountId+"/getPasswordAjax.jspa",
		data:{
			"type":type,
			"bankNo" :bankNo			
		},
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success:function(data){
			  if(data != undefined){	
				  //判断是否显示
				  if(data.fundPassword != undefined && data.fundPassword ==1){
					 $("#fund_password_div").show();
				  }else{
					 $("#fund_password_div").hide();
				  }
				  //判断样式
				  if(data.fundPassword !=1){
				     // $("#bank_password_div").attr("class","");
				  }
				 if(data.bankPassword != undefined && data.bankPassword ==1){
					 $("#bank_password_div").show();
				 }else{
					 $("#bank_password_div").hide();
				 }
				//判断样式
				  if(data.fundPassword !=1 && data.bankPassword !=1 ){
				      //$("#trade_password_div").attr("class","");
				  }
				 if(data.tradePassword != undefined && data.tradePassword ==1){
					 $("#trade_password_div").show();
				 }else{
					 $("#trade_password_div").hide();
				 }
			  }			  
		 }		  
	});
}






