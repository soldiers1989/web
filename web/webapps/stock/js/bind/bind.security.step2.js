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

	$("#cuscode").keyup(function(){
		XJB.Utils.clearNoNum(this,-1);
	})
	
	$("#cuscode").bind("blur", function() {
		validcuscode();
	});
	
	$("#password").bind("blur", function() {
		validpassword();
	});
	
	$("#bind-btn-step2").click(function(){
		if(document.all.TestAX==undefined ||document.all.TestAX.PwdStr == undefined){
			JRJ.Alerts.alert({
				title: "错误提示",  //标题
				width:400,
				message: "为了您的交易安全，请先安装安全控件"     //提示语
			  });
			return false;
		}
		if(validForm()){
			$("#bindForm").submit();
		}
	});
	
	$('.agreement').click(function () {
	    var type = $(this).data('type');
	
	    JRJ.Dialogs.iframeDialog({
	    	width:600,
	        content: [''].join(''),
	        hasOkBtn: true,
	        okBtnText: '已经阅读',
	        title: "金融界证券交易提示书",
	        titleRight : '',
	        hasCancelBtn: false,
	        enableKeyCtrl: true,
	        ifrSrc: XJB.Urls.protocolKnow ,
	        ifrReHeight: true,
	        okCallback: function () {
	        	$("#agree").attr("checked","checked");
	        	$("#agree_error").hide();
	            JRJ.Dialogs.close();
	            return true;
	        }
	    });
	});
})

function validForm(){
	
	if(!$("#agree").attr("checked")){
		$("#agree").focus();
		$("#agree_error").show();
		return false;
	}
	if(validcuscode()){
		return validpassword();
	}
	
}

function validcuscode(){
	var cuscode=$("#cuscode").val();
	if(cuscode.trim()==""){
		showErr("#cuscode","资金帐号不能为空");
		return false;
	}
	if(!/^[0-9]*$/.test(cuscode)){
		showErr("#cuscode","资金账号只能为数字");
		return false;
	}
	 hideErr("#cuscode","");
	 return true;
}

function validpassword(){ 
	 var pwd = document.all.TestAX.PwdStr;
	if(pwd.trim() ==""){
		showErr("#password","交易密码不能为空")
		return false;
	}
		hideErr("#password","");
		$("#password").val(document.all.TestAX.PwdStr);
		$("#userMac").val(document.all.TestAX.MacStr);
		return true;
}



