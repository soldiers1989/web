/**
 * 
 */
var  issubmit = true;
function initpwd_submit(accountId){
	if(validFrom()){
		initpwdSubmit(accountId);
	}
}

function validFrom(){
	return issubmit;	
}

function initpwdSubmit(accountId){
	$("#errmsg").html("").hide();
	
	initPwd.check(function (states,errorinfo){
		var password = "";
        if(states == 0){
        	password = initPwd.value();
        	txpwd=initPwd.value("txPassword");
        	$.ajax({
        		type : "post",
        		url : "/stock/"+accountId+"/initQsPwdResult.jspa",
        		data:{
        			"password":password,
        			"txPassword":txpwd,
        			"accountid":accountId,
        			"local_network":initPwd._mac,
        			"local_disk":initPwd._disk,
        			"local_cpu":initPwd._nic
        		},
        		dataType : "json",
        		timeout : 5000,
        		cache : false,
        		success : function(data){
        			  if(data.errMsg){
        				   $("#errmsg").html(data.errMsg).show();
        			  }else{
        				  if(returnUrl != ''){
							window.location.replace(returnUrl);
						  }else{
							window.location.replace('/stock/');
						  }
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
			
        }else if(states == -100){
        	$("#errmsg").html("交易密码不能为空!").show();
        }else if(states == -103){
        	$("#errmsg").html("通讯密码不能为空!").show();
        }else{
        	if(errorinfo!=undefined){
        		$("#errmsg").empty().text(errorinfo).show();
        	}
        }
    });
}