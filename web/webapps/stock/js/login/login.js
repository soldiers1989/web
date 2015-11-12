if (typeof XJB == "undefined" || !XJB) {
	var XJB = {};
}
XJB.Login=function(config){
	this.args = {
	 			usernameId:"username",//用户输入框ID
	 			passwordId:"password",//登录密码框ID
	 			codeId:"captcha",//验证码ID
	 			tokenId:"xjb.token",//TokenID
	 			logbtnId:"loginBtn",//登录按钮
	 			errorId:"error",//错误提示框
	 			code:false,//是否需要验证码
	 			formId:"loginForm",
	 			redirect:"",
	 			from:""
	 	};
	 	for (property in config) {
	 		this.args[property] = config[property];
	 	}
	 	this.usernameId=this.args.usernameId;
	 	this.passwordId=this.args.passwordId;
	 	this.codeId=this.args.codeId;
	 	this.tokenId=this.args.tokenId;
	 	this.errorId=this.args.errorId;
	 	this.code=this.args.code;
	 	this.loginBtnId=this.args.logbtnId;
	 	this.formId=this.args.formId;
	 	this.redirect=this.args.redirect;
	 	this.from = this.args.from;
	 	this._init();
};
XJB.Login.prototype={
         _data:{},
        _MESSAGE:{
				M0001:"账户名不可空",
				M0002:"登录密码不可为空",
				M0003:"验证码不可为空",
				M1001:"账户名或登录密码不正确",
				M1002:"验证码不正确",
				M1003:"登录超时",
				M1004:"服务器忙",
				M1005:"账户名或登录密码不正确，还有$count次机会。您还可以：<a href='"+XJB.Urls.forgetPassword+"' style='color: #0088CC'>找回登录密码 </a>",
				M1006:"您的账户已经被锁定，请30分钟以后重新登录。"
		},
        _init:function(){
			this._bindEvent();
		},
		_bindEvent:function(){
			var _this=this;
			$("#"+this.codeId).bind("blur",function(){_this._checkCode();});
			$("#"+this.loginBtnId).bind("click",function(){_this._checkSubmit();});
			$("."+this.codeId+"-Img").bind("click",function(){
				_this._changeCaptcha();
			});
			//光标锁定
			$("#"+_this.usernameId).focus();
			
			$(document).keyup(function (event) {
			    if (event.keyCode == 13) {
			    	_this._checkSubmit();
			    }
			});
		},
		_clearError:function(id){
			if(id!=undefined){
				$("#"+id+"Tip").hide();
//			$("#"+id).parent().next(".table-error").empty();
			}else{
				$("#"+this.errorId).empty().text("").hide();
			}
		},
		_showError:function(id,msg){
			if(id!=undefined&&msg!=undefined){
				$("#"+id+"Tip").removeClass("right").addClass("error").show();
				$("#"+this.errorId).empty().html('<i class="icon login-error-icon"></i><span>'+msg+"</span>").show();
			}else{
				$("#"+this.errorId).empty().html('<i class="icon login-error-icon"></i><span>'+id+"</span>").show();
			}
		},
		_showOk:function(id){
			$("#"+id+"Tip").removeClass("error");
//			$("#"+id).parent().next(".table-error").empty();
		},
		_checkUsername:function(){
			this._clearError(this.usernameId);
			var username=""+$("#"+this.usernameId).val();
			var username=username.trim();
			$("#"+this.usernameId).val(username);
			if(username.bLength()==0){
				this._showError(this.usernameId,this._MESSAGE.M0001);
				return false;
			}
			this._showOk(this.usernameId);
			this._data.username=username;
			return true;
		},
		_checkPassword:function(){
			this._clearError(this.passwordId);
			var password=""+$("#"+this.passwordId).val();
			var password=password.trim();
			$("#"+this.passwordId).val(password);
			if(password.bLength()==0){
				this._showError(this.passwordId,this._MESSAGE.M0002);
				return false;
			}
			this._showOk(this.passwordId);
//			this._data.password=password;
			this._data.password=XJB.Utils.md5(password);
			return true;
		},
		_checkCode:function(){
			this._clearError(this.codeId);
			if(this.code){
				var captcha=""+$("#"+this.codeId).val();
				if(captcha.bLength()==0){
					this._showError(this.codeId,this._MESSAGE.M0003);
					return false;
				}
				this._data.captcha=captcha;
			}
			return true;
		},
		_checkSubmit:function(){	
			if(this._checkUsername()&&this._checkPassword()&&this._checkCode()){
				this._doSubmit();
			}
		},
		_changeCaptcha:function(){
				var captchaUrl=XJB.Urls.captcha+"?"+new Date().getTime();
				$("img."+this.codeId+"-Img").attr("src",captchaUrl);
				$("#"+this.codeId).val("");
			},
		_doSubmit:function(){
			this._data.jsClient=1;
			var _this=this;
			this._data["xjb.token.name"]="xjb.token";
			this._data["xjb.token"]=$("input[name="+this.tokenId+"]").val();
			this._data["from"]=this.from;
			$.ajax({
				type:"post",
				url:'/stock/login.jspa',
				data:this._data,
//				data:$("#"+this.formId).serialize(),
				dataType:"json",
				success:function(data){
					if(data!=undefined){
						if(data.code!=undefined&&data.code==true){
							$("#"+_this.codeId+"-div").show();
							_this.code=true;
							_this._changeCaptcha();
						}
						if(data.token!=undefined){
							$("input[name="+_this.tokenId+"]").val(data.token);
						}
						_this._clearError(this.usernameId);
						_this._clearError(this.passwordId);
						switch(data.errcode){
						case "0":
							if(_this.redirect.replace(/(^\s*)|(\s*$)/g, "")==""){
								window.location.replace(XJB.Urls.cashAccount);
							}else{
								window.location.replace(_this.redirect);
							}
							break;
						case "M1001":
							_this._showError(_this._MESSAGE[data.errcode]);
							break;
						case "M1002":
							_this._changeCaptcha();
							_this._showError(_this._MESSAGE[data.errcode]);
							break;
						case "M1003":
							_this._showError(_this._MESSAGE[data.errcode]);
							window.location.reload();
							break;
						case "M1005":
							_this._showError(_this._MESSAGE[data.errcode].replaceAll("$count",data.count));
							break;
						case "M1006":
							_this._showError(_this._MESSAGE[data.errcode]);
							break;
						default:
							_this._showError(_this._MESSAGE[data.errcode]);
							break;
						}
					}else{
						_this._showError(_this._MESSAGE.M1004);
					}
				}
			}); 
		}
};
String.prototype.trim = function() {
	return this.replace(/^(\u3000|\s|\t|\u00A0)*|(\u3000|\s|\t|\u00A0)*$/g, "");
};
String.prototype.bLength = function() {
	if (!this) {
		return 0;
	}
	var b = this.match(/[^\x00-\xff]/g);
	return (this.length + (!b ? 0 : b.length));
};
