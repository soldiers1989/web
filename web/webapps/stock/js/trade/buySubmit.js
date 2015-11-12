/**
 * 
 */
$(document).ready(function(){
	$(".no-safety-controller").mouseover(function(){
		$(".no-safety-controller-tip").show();
	});
	$(".no-safety-controller").mouseout(function(){
		$(".no-safety-controller-tip").hide();
	});
	$("#buysubmit").click(function(){
		if(document.all.TestAX ==undefined || document.all.TestAX.PwdStr == undefined){
			JRJ.Alerts.alert({
				title: "错误提示",  //标题
				width:400,
				message: "为了您的交易安全，请安装安全控件"     //提示语
			  });
			return false;
		}
		var password =document.all.TestAX.PwdStr;
		if(password ==undefined || password==""){
			JRJ.Alerts.alert({
				title: "错误提示",  //标题
				width:400,
				message: "交易密码不能为空"     //提示语
			  });
			return;
		}else{
			$("#password").val(password);
			$("#userMac").val(document.all.TestAX.MacStr);
			$("#submitForm").submit();
		}
	});
	
})