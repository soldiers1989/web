/**
 * 长江开户活动
 * @author haijun.sun(孙海军)
 * @date 2015年4月7日 下午4:44:22 
 */
var ti = 0;

$(document).ready(function(){
	$("#subOpen").bind("touchstart",submitstep);//绑定参与按钮事件
	$("#getVcode").bind("touchstart",getIdentifyingCode);//绑定获取验证事件
	
	//获取访问设备终端型号
	var browser={versions:function(){var u = navigator.userAgent;return {ios:!!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)};}()}
	if(browser.versions.ios == true)
		$("#terminalType").val("IOS");
	else
		$("#terminalType").val("android");
	
	
	//提交参与
	function submitstep(){
		if(passValiMobile()&&passValiCode()){
			var mobile = $("#mobile").attr("value");
			var vcode = $("#vcode").attr("value");
			var qdcode = $("#qdcode").attr("value");
			var yqcode = $("#yqcode").attr("value");
			var terminalType = $("#terminalType").attr("value");
			
			$("#subOpen").unbind("touchstart").removeAttr('ontouchstart');
			var params = {mobile:mobile,vcode:vcode,qdcode:qdcode,yqcode:yqcode,terminalType:terminalType};
			$.ajax({
		    	 url : "/openAccount/activity/addinfo.jspa",
		    	 data :params,
		    	 dataType:"json",
		    	 type:"post",
		    	 cache : false,
		    	 success:function(data){
		    	 	 if(data.retcode==0){
		    		 	 window.location.href='/openAccount/activity/download.jspa?mobile='+mobile+'&qdcode='+qdcode;//跳转到软件下载链接
		    	 	 }else if(data.retcode==-1){
		    	 	 	 alert("您输入的验证码不正确!");//提示错误信息
		    	 	 }else
		    	 	 	 alert("系统繁忙，请稍后重试！");//提示错误信息
		    	 },complete: function(XMLHttpRequest, textStatus){
					  $("#subOpen").bind("touchstart",submitstep);
				 }
		    })
		}
	};
})

//手机号码验证
function passValiMobile(){
	//手机号码验证
	var mobile = $("#mobile").attr("value");
	if(mobile==""){
		alert("手机号不能为空");
		return false;
	}
	var reg = /^1[34578]\d{9}$/;
	if(!reg.test(mobile)){
		alert("请输入正确的手机号");
		return false;
	}
	return true;
}
//验证码验证
function passValiCode(){
	var vcode = $("#vcode").attr("value");
	if(vcode==""){
		alert("请输入验证码");
		return false;
	}
	if(vcode.length!=4){
		alert("请输入完整验证码");
		return false;
	}
	return true;
}

//获取验证码
function getIdentifyingCode() {
	if(ti>0){
		alert("获取频繁请稍后再试！");
        return false;
    }
	if(passValiMobile()){
		$("#getVcode").unbind("touchstart").removeAttr('ontouchstart');
		$.ajax({
	    	 url : "/openAccount/activity/getCode.jspa",
	    	 data :{"mobile" : $("#mobile").attr("value")},
	    	 dataType:"json",
	    	 type:"post",
	    	 cache : false,
	    	 success:function(data){
	    	 	 if (typeof(data) != 'undefined' && data != null && data != 'null') {	
		    	 	 	if(data.status == 1&&data.resultCode== 0){
		    	 	 		 alert("验证码已发送至您手机！");
				    		 if (ti <= 0) {
				    		 	ti = 60;
				    		 	var _inter = setInterval(function () {
						            if (ti <= 0) {
						            	window.clearInterval(_inter);
						            	$("#getVcode").html("获取验证码");
						            } else {
						                ti--;
						                $("#getVcode").html(ti + "秒后重新获取");
						            }
			          			}, 1000);
				    		 }
		    	 	 	}else if(data.status == 1&&data.resultCode== 10217){
		    	 	 		alert("不能频繁获取验证码");
		    	 	 	}else
		    	 	 		alert("系统繁忙，请稍后重试！");//提示错误信息
	    	 	 }
	    	 },complete: function(XMLHttpRequest, textStatus){
				  $("#getVcode").bind("touchstart",getIdentifyingCode);
			 }
	    })
	}
}