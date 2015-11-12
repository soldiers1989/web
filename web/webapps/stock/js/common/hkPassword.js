function hkpwd_submit(accountId){
	//获取密码
	var password = $("#password").val();
	$.ajax({
		type : "post",
		url :"/stock/hk/"+accountId+"/ajaxHkLogin.jspa",
		data :{
			"password" : password
		  },
		dataType : "json",
		timeout : 5000,
		async:false,
		cache : false,
		success:function(data){
			  if(data != undefined){
				  if(data.retcode == 0){
					  //调用刷新页面方法
					 /*  JRJ.Alerts.alert({
    						title: "消息提示",  //标题
    						width:400,
    						autoClose:false,
    					    okCallback: callbackRe, //ok回调
    						cancelCallback: callbackRe, //cancel回调，关闭时也触发
    						message: data.msg     //提示语
    					  }); */
						  
					returnUrl=$('#input_returnUrl').val();
					window.location.replace(returnUrl);
				  }else{
					  JRJ.Alerts.alert({
  						title: "消息提示",  //标题
  						width:400,
  						autoClose:false,
  						message: data.msg     //提示语
  					  });
				  }
			  }				  
		 }		  
	});
}