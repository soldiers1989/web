/**
 * 
 */
var  issubmit = true;
var query_rs_content ="查询银行余额成功，请到查询记录中查询(合同号：{serialNo})。"
var balance_url ="/stock/bank_balance_Query.jspa";
$(document).ready(function(){
	$("#moneyValueDiv").hide();
	$("#moneys").hide();
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
	
	$("#bankNo").change(function(){
		issubmit = false;
		getpassword();
	}) ;
	$("#balance_query").click(function(){
		balance_query();

	});
	
})
function balance_query(accountId,balancePassword,_dialog){
	//设置查询余额密码的错误提示为红色
	$("#errorMsg").attr("class","error-msg");
	if(!validPassword()){
		return false;
	}	
	var bankNo = $("#bankNo").val();
	var bankAcc= $("#bankAcc").val();
	var bankPassword = "";
	var fundPassword = "";
	var tradePassword = "";
	var userMac ="";
	balancePassword.check(function (states){
        if(states == 0){
        	bankPassword = balancePassword.value("bankPassword");
        	fundPassword = balancePassword.value("fundPassword");
        	tradePassword = balancePassword.value("tradePassword");
        	var balance_url ="/stock/"+accountId+"/bank_balance_Query.jspa";
        	$.ajax({
        		type : "post",
        		url : balance_url,
        		data :{
        			"bankNo" : bankNo,
        			"bankAcc":bankAcc,
        			"bankPassword" :bankPassword,
        			"fundPassword" :fundPassword,
        			"tradePassword" :tradePassword,
        			"local_network":balancePassword._mac,
        			"local_disk":balancePassword._disk,
        			"local_cpu":balancePassword._nic
        		  },
        		dataType : "json",
        		timeout : 5000,
        		cache : false,
        		success:function(data){
        			  //data.errMsg="";data.serialNo="— —";
        			  if(data.errMsg){
        				  $("#errorMsg").html(data.errMsg);
        			  }else{
        				  //JRJ.Dialogs.close();
        				  if(_dialog!=undefined){
        					  _dialog.close();
        				  }
        				  balancePassword.destory();
        				  $("#balanceButton").hide();
        				  $("#moneys").show();
        				  if(data.serialNo==0){
        					  $("#bankpass").show();
        					  $("#moneys").html("￥"+data.occurBalance+"元");
        				  }else{
        				  $("#moneys").html('<img src="/stock/images/loading1.gif" width="16" height="11" alt="loading..." />');
        				  //根据流水号异步调用余额
        				  setTimeout(function(){balance_query_money(data.serialNo)}, 3000);
        				  }
        			  }		
        		 }		  
        	});
        
        }else if(states == -99){
		     $("#errorMsg").html("为了您的交易安全，请安装安全控件!");
		     return false;
        }else if(states == -100){
		     $("#errorMsg").html("交易密码不能为空!");
		     return false;
        }else if(states == -101){
		     $("#errorMsg").html("资金密码不能为空!");
		     return false;
        }else if(states == -102){
		     $("#errorMsg").html("银行密码不能为空!");
		     return false;
        }
    });
}

/*function callBack(){
	window.location.href="/stock/fundQuery.jspa";
}*/
function validp(obj,pName){
	if(document.all.bankPassword ==undefined 
			||document.all.fundPassword ==undefined 
			|| document.all.tradePassword== undefined 
			|| document.all.bankPassword.PwdStr ==undefined 
			|| document.all.fundPassword.PwdStr==undefined 
			|| document.all.tradePassword.PwdStr==undefined){
		JRJ.Alerts.alert({
			title: "错误提示",  //标题
			width:400,
			message: "为了您的交易安全，请安装安全控件"     //提示语
		  });
		return false;
	}
	var password ="";
	if(obj =="bankPassword"){
		password=document.all.bankPassword.PwdStr;
	}else if(obj =="fundPassword"){
		password=document.all.fundPassword.PwdStr;
	}else if(obj =="tradePassword"){
		password=document.all.tradePassword.PwdStr;
	}
	if(password==undefined || password==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: pName+"密码不能为空"     //提示语
			  });
		return false;
	}
	return true;
}
function validPassword(){
	var bankPassword = $("#bank_password");
	var fundPassword = $("#fund_password");
	var tradePassword = $("#trade_password");
	var flag_b=true;
	var flag_f=true;
	var flag_t=true;
	if(bankPassword.data("password") ==1){
		flag_b = validp("bankPassword","银行");
	}
	if(fundPassword.data("password") ==1){
		flag_f = validp("fundPassword","资金");
	}
	if(tradePassword.data("password") ==1){
		flag_t = validp("tradePassword","交易");
	}
	
	return (flag_b && flag_f && flag_t);
}

function getpassword(){
	var type = 3;
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
					 $("#bank_password").attr("data-password",1);
				 }else{
					 $("#bank_password").hide();
					 $("#bank_password").attr("data-password",0);
				 }
				 
				 if(data.fundPassword != undefined && data.fundPassword ==1){
					 $("#fund_password").show();
					 $("#fund_password").attr("data-password",1);
				 }else{
					 $("#fund_password").hide();
					 $("#fund_password").attr("data-password",0);
				 }
				 
				 if(data.tradePassword != undefined && data.tradePassword ==1){
					 $("#trade_password").show();
					 $("#trade_password").attr("data-password",1);
				 }else{
					 $("#trade_password").hide();
					 $("#trade_password").attr("data-password",0);
				 }
				 $("#bank_password").val("");
				 $("#fund_password").val("");
				 $("#trade_password").val("");
				 issubmit=true;
			  }			  
		 }		  
	});
}

/**
 * 动态读取文本框名字提示
 */
function getpasswords_Balance(){
	var type = "3";
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
					 $("#fund_password_bal_div").show();
				  }else{
					 $("#fund_password_bal_div").hide();
				  }
				  //判断样式
				  if(data.fundPassword !=1){
				     // $("#bank_password_bal_div").attr("class","");
				  }
				 if(data.bankPassword != undefined && data.bankPassword ==1){
					 $("#bank_password_bal_div").show();
				 }else{
					 $("#bank_password_bal_div").hide();
				 }
				//判断样式
				  if(data.fundPassword !=1 && data.bankPassword !=1 ){
				     // $("#trade_password_bal_div").attr("class","");
				  }
				 if(data.tradePassword != undefined && data.tradePassword ==1){
					 $("#trade_password_bal_div").show();
				 }else{
					 $("#trade_password_bal_div").hide();
				 }
			  }			  
		 }		  
	});
}
/**
 * 查询是否需要密码输入
 */
function getNeedpasswords(){
	var type = "3";
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
				  //判断是否需要密码
				  if((data.fundPassword != undefined && data.fundPassword ==1) ||
						  (data.bankPassword != undefined && data.bankPassword ==1) ||
						  (data.tradePassword != undefined && data.tradePassword ==1)
				      ){
					  //打开密码框
					  showBankMoney();
				  }else if(data.pass==-1){
					  JRJ.Alerts.alert({
							title: "消息提示",  //标题
							width:400,
							autoClose:false,
							message: "该银行不支持余额查询"     //提示语
						  });
				  }else{
					  //直接查询余额
					  balance_query_nopass();
				  }
			  }else{
				  //显示密码框
				  showBankMoney();
			  }			  
		 }		  
	});
}
//不需要查询密码查询余额
function balance_query_nopass(){	
	var bankNo = $("#bankNo").val();
	var bankPassword = "";
	var fundPassword = "";
	var tradePassword = "";
	var userMac ="";
	var balance_url ="/stock/"+_accountId+"/bank_balance_Query.jspa";
	$.ajax({
		type : "post",
		url : balance_url,
		data :{
			"bankNo" : bankNo,
			"bankPassword" :bankPassword,
			"fundPassword" :fundPassword,
			"tradePassword" :tradePassword,
			"userMac":userMac
		  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		success:function(data){
			  if(data.errMsg){
				  //$("#errorMsg").html(data.errMsg);
				  JRJ.Alerts.alert({
						title: "消息提示",  //标题
						width:400,
						autoClose:false,
						message: data.errMsg     //提示语
					  });
				return false;
			  }else{
				  //显示页面上的密码控制
				  $("#bankpass").show();
				  $("#balanceButton").hide();
				  $("#moneys").show();
				  if(data.serialNo==0){
					  $("#moneys").html("￥"+data.occurBalance+"元");
				  }else{
				  $("#moneys").html('<img src="/stock/images/loading1.gif" width="16" height="11" alt="loading..." />');
				  //根据流水号异步调用余额
				  setTimeout(function(){balance_query_money(data.serialNo)}, 3000);
				  }
			  }		
		 }		  
	});
}
//根据流水号异步查余额
function balance_query_money(serialNo){
	
	var balance_url ="/stock/"+_accountId+"/queryBankMoney.jspa";
	$.ajax({
		type : "get",
		url : balance_url,
		data :{
			"serialNo" :serialNo,
			"tradePassword" :""
		  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		success:function(data){
			  if(data.errMsg){
				  //$("#errorMsg").html(data.errMsg);
				  JRJ.Alerts.alert({
						title: "消息提示",  //标题
						width:400,
						autoClose:false,
						message: data.errMsg     //提示语
					  });
				return false;
			  }else{
				  if(data.occurBalance==undefined || data.occurBalance == "— —" || data.occurBalance == "0"){
					  $("#moneys").html("— —");
					  var content =query_rs_content.replace("{serialNo}",serialNo);
					  $("#moneyValue").html(content);
					  $("#moneyValueDiv").show();
			          setTimeout(function () {
			              $("#moneyValueDiv").hide();
			          }, 3000);
				  }else{
					  $("#moneys").html("￥"+data.occurBalance+"元");
				  }
				  //显示页面上的密码控制
				  $("#bankpass").show();
			  }		
		 }		  
	});
}


