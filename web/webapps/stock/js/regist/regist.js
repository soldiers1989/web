
var isIDCard2=/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[A-Z])$/; 

var VALID={
	RN:false,
	IN:false,
	MB:false,
	UN:false,
	PW:false,
	RW:false,
	IC:false,
	CR:false
};

var LAST_VALUE = {
	RN_LAST:"",
	IN_LAST:"",
	MB_LAST:"",
	UN_LAST:"",
	PW_LAST:"",
	IC_LAST:""
};

var RN_MSG = {
	M01 : "账户须实名，请填写真实姓名。",
	M02 : "真实姓名必须是中文。",
	M03 : "请填写真实姓名格式不正确。"
};
var IN_MSG = {
	M01 : "账户实名身份认证，请正确填写真实姓名和证件号。",
	M02 : "请输入18位身份证号。",
	M03 : "请填写正确的身份证号。",
	M04 : '此身份证号已注册盈利宝,请直接  <a href="javascript:void(0)" class="link" onclick="bind_zqt(\'s\');"><button onclick="return false" style="background-color: #c3c3c3;width: 40px;height: 25px;line-height: 15px;">授权</button></a>',
	M05 : "身份验证失败，请正确填写您的身份信息。",
	M06 : "此身份证无效"
};
var MB_MSG = {
	M01 : "手机号将用于接收账户交易信息及找回密码，请正确填写。",
	M02 : "手机号将用于接收账户交易信息及找回密码，请正确填写。",
	M03 : '<span>此手机号已被占用，请重新输入或&nbsp;&nbsp;</span><a href="http://crm2.qq.com/page/portalpage/wpa.php?uin=800089887&aty=0&a=0&curl=&ty=1" class="link" target="_blank">联系客服</a>',
	M04 : "验证失败",
	M05 : "手机号将用于接收账户交易信息及找回密码"
};
var UN_MSG = {
	M01 : "请填写用户名。",
	M02 : "用户名必须以6-16位字母或者字母和数字组合，首位不能为数字。",
	M03 : "该用户名已被注册。",
	M04 : "验证失败"
};
var PW_MSG = {
	M01 : "密码用于交易，需以8位以上数字字母混合组成，请正确填写。",
	M02 : "密码用于交易，需以8位以上数字字母混合组成，请正确填写。",
	M03 : "密码用于交易，需以8位以上数字字母混合组成"
};
var RW_MSG = {
	M01 : "请正确填写重复密码。",
	M02 : "请正确填写重复密码。"
};
var IC_MSG = {
	M01 : "手机号将用于接收账户交易信息及找回密码，请正确填写。",
	M02 : "请填写验证码。",
	M03 : "获取验证码失败。",
	M04 : "请正确填写验证码。",
	M05 : "验证码获取过于频繁"
};

//获取验证码手机
var SEND_MOBILE="";
//验证码过期时间
var VALIDCODE_DATE="";
//
var YESNO_YLB=false;

//验证姓名
function _validatorRealName() {
	checkRealName();
}
//验证手机号
function _validatorMobile(mobile) {
	if (mobile == undefined) {
		mobile = $("#mobile").val();
		mobile = mobile.trim();
		$("#mobile").val(mobile);
	}
	if(LAST_VALUE.MB_LAST != mobile || LAST_VALUE.MB_LAST == ""){
		hideErr("#mobile",MB_MSG.M05);
		VALID.MB=false;
		LAST_VALUE.MB_LAST = mobile;
		$("#validateCode").val('');
		if (mobile.bLength() == 0) {
			showErr("#mobile",MB_MSG.M05);
			VALID.MB=false;
		}else if (!/^1[\d]{10}$/.test(mobile)) {
			showErr("#mobile",MB_MSG.M02);
			VALID.MB=false;
		}else{
			checkMobile(mobile);
		}
	}
}

function _validatorIN(idNumber){
	if(idNumber==undefined){
		idNumber = $("#idNumber").val().trim();
		$("#idNumber").val(idNumber);
	}
	var name = $("#realName").val();
	if(name ==undefined || name==""){
		showErr("#realName","账户须实名，请填写真实姓名");
		return;
	}
	if(LAST_VALUE.IN_LAST != idNumber || LAST_VALUE.IN_LAST == ""){
		hideErr("#idNumber","");
		VALID.IN=false;
		LAST_VALUE.IN_LAST = idNumber;
		if(idNumber.bLength()==0){
			showErr("#idNumber",IN_MSG.M03)
			VALID.IN=false;
			return;		
		}else if(!(idNumber.bLength()==18)){
			showErr("#idNumber",IN_MSG.M02);
			VALID.IN=false;
			return;	
		}else if(idNumber.bLength()==18 && !(isIDCard2.test(idNumber))){
			showErr("#idNumber",IN_MSG.M03)
			VALID.IN=false;
			return;	
		}else{
			checkIDNumber(idNumber,name);
		}
	}
}

//验证密码
function _validatorPassword(password) {
	if (password == undefined) {
		password = $("#password").val();
		password = password.trim();
		$("#password").val(password);
	}
	
	if(LAST_VALUE.PW_LAST != password || LAST_VALUE.PW_LAST == ""){
		hideErr("#password",PW_MSG.M03);
		VALID.PW=false;
		LAST_VALUE.PW_LAST = password;
		
		if (password.bLength() == 0) {
			showErr("#password",PW_MSG.M01);
			VALID.PW=false;
		}else if (!/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$/.test(password)) {
			showErr("#password",PW_MSG.M02);
			VALID.PW=false;
		}else{
			VALID.PW=true;
		}
	}
}
//验证确认密码
function _validatorRePassword(repassword) {
	if (repassword == undefined) {
		repassword = $("#repassword").val();
		repassword = repassword.trim();
		$("#repassword").val(repassword);
	}
	
	hideErr("#repassword","");
	VALID.RW=false;
	if (repassword.bLength() == 0) {
		showErr("#repassword",RW_MSG.M01);
		VALID.RW=false;
	}else if (repassword != $("#password").val()) {
		showErr("#repassword",RW_MSG.M02);
		VALID.RW=false;
	}else{
		VALID.RW=true;
	}
}

//验证验证码
function _validatorIdentifyingCode(validateCode) {
	if (validateCode == undefined) {
		validateCode = $("#validateCode").val();
		validateCode = validateCode.trim();
		$("#validateCode").val(validateCode);
	}
	
	if(LAST_VALUE.IC_LAST != validateCode || LAST_VALUE.IC_LAST == ""){
		hideErr("#validateCode","");
		VALID.IC=false;
		LAST_VALUE.IC_LAST = validateCode;
		
		if (validateCode.bLength() == 0) {
			showErr("#validateCode",IC_MSG.M02);
			VALID.IC=false;
		}else{
			checkValidateCode(validateCode);
		}
	}
}

function changeMsg(key,value,clazz){
	var html = '';
	if(value==''||value==undefined||value==null){
		html = '<span class="'+ clazz +'"></span>';
	}else{
		html = '<span class="'+ clazz +'"><i></i><em></em><span>'+ value +'</span></span>';
	}
	$("#"+key).html(html);
}

function _validatorForm() {
	
	_validatorMobile();
	checkRealName();
	_validatorIdNumber();
	_validatorIdentifyingCode();
//	_validatorPassword();
//	_validatorRePassword();

	if((VALID.MB&&VALID.RN&&VALID.IN&&VALID.IC)){
		return true;
	}
	setFocus();
	return false;
}

function setFocus(){
	if(!VALID.RN){
		$("#realname").focus();
	}else if(!VALID.IN){
		$("#idnumber").focus();
	}else if(!VALID.MB){
		$("#mobile").focus();
	}else if(!VALID.IC){
		$("#validateCode").focus();
	}else{
		$("#shouquan_pwd").focus();
	}
}

//验证手机号是否已注册
function checkMobile(mobile){
	if(mobile==undefined){
		showErr("#mobile",MB_MSG.M01);
		VALID.MB=false;
	}
	$.ajax({
		type : "post",
		url : XJB.Urls.checkMobileRegist,
		data :{
			"mobile" : mobile
			  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success : function(data, textStatus) {
			if (typeof(data) != 'undefined' && data != null && data != 'null') {
				if(data.retcode == 0){
					$('#getValidateCode').removeAttr('disabled').removeClass('btn-5-disable').addClass('btn-5');
						hideErr("#mobile",MB_MSG.M05);
						VALID.MB=true;
						enableInput();
				}else if(data.retcode =10105){
					if($("#mobile").attr('disabled')){//通过输入框状体判断是否是盈利宝用户手机
						$('#getValidateCode').removeAttr('disabled').removeClass('btn-5-disable').addClass('btn-5');
						hideErr("#mobile",MB_MSG.M05);
						VALID.MB=true;
						enableInput();
					}else{
						showErr("#mobile",MB_MSG.M03);
						VALID.MB='EXIST';//手机号存在，当点击获取验证码时显示。old:VALID.MB=false;
						disableInput();
					}
					
				}else{
					showErr("#mobile",MB_MSG.M04);
					VALID.MB=false;
				}
			}else{
				showErr("#mobile",MB_MSG.M04);
				VALID.MB=false;
			}
		},
		error:function(xhr, status, error){
			console.log(xhr);
		}
	});
}

//验证验证码是否有效
function checkValidateCode(validateCode){
	SEND_MOBILE=LAST_VALUE.MB_LAST;
	if(validateCode==undefined){
		showErr("#validateCode",IC_MSG.M02);
		VALID.IC=false;
		return false;
	}else if(SEND_MOBILE==''){
		showErr("#validateCode",IC_MSG.M04);
		VALID.IC=false;
		return false;
	}
	$.ajax({
		type : "post",
		url : XJB.Urls.checkValidateCode,
		data :{
			"validateCode" : validateCode,
			"mobile" : SEND_MOBILE
			  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success : function(data, textStatus) {
			if (typeof(data) != 'undefined' && data != null && data != 'null') {
				if (data.status == 1) {
					if(data.retcode == 0){
						hideErr("#validateCode","");
						VALID.IC=true;
					}else{
						showErr("#validateCode",IC_MSG.M04);
						VALID.IC=false;
					}
				}else{
					showErr("#validateCode",IC_MSG.M04);
					VALID.IC=false;
				}
			}else{
				showErr("#validateCode",IC_MSG.M04);
				VALID.IC=false;
			}
		}
	});
}

var ti = 0;

//获取验证码
function getIdentifyingCode() {
	$("#mobile").trigger('blur');
	//if(!VALID.CR) return false;
	if(!VALID.MB){
		VALID.IC=false;
		if(VALID.MB==='EXIST'){
			showErr("#mobile",MB_MSG.M03);
		}else{
			showErr("#mobile",IC_MSG.M01);
		}
		
		return false;
	}

	// 获取验证码------------------------------start
	
	if(ti>0){
    	return false;
    }
  
    // 获取验证码------------------------------end
	var mobile = LAST_VALUE.MB_LAST;
	var captcha = ""+$("#captcha").val();
	var idNumber=$("#idnumber").val();
	$.ajax({
		type : "post",
		url : XJB.Urls.identifyingCode,
		data :{
			"mobile" : mobile
			  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		success : function(data, textStatus) {
			if (typeof(data) != 'undefined' && data != null && data != 'null') {
				if(data.status == 1){
					if(data.retcode == 0){
						VALIDCODE_DATE = data.expiredtime;
						SEND_MOBILE = mobile;
						
						/*JRJ.Dialogs.standardDialog({
				            content: ['<div style="width:300px;" class="account-info-popup">',
				                '<p class="jrj-tc mb30">验证码已经发送到手机</p>',
				                '</div>'].join(''),
				            hasOkBtn: true,
				            title: "获取验证码",
				            hasCancelBtn: false,
				            okCallback: function () {
				                JRJ.Dialogs.close();
				                return true;
				            }
				        });*/
						
						var tmpl = '<img border="0" src="/stock/images/regist/register-popup-getcode.png" alt="waiting" class="popup-waiting" />';
						$(tmpl).css({'position':'absolute',top:'310px',left:'50%','margin-left':'-241px', 'display':'none', 'z-index':'200'}).appendTo($('.xjb-login-content')).fadeIn(function(){
							var _this = $(this);
							setTimeout(function(){
								_this.fadeOut().remove();
							},3000);
						});
						
						hideErr("#validateCode","短信验证码已发送，请您在5分钟内填写");
						if (ti <= 0) {
					        ti = 300;
					        var self = $('#getValidateCode');
					        self.unbind("click");
					        self.text(ti+"秒后可重新获取");
					        var _inter = setInterval(function () {
					            if (ti <= 0) {
					            	self.text("再次发送验证码");
					            	//changeCaptcha();
					            	self.click(function(){
					            		getIdentifyingCode();
					            	});
					                window.clearInterval(_inter);
					               // hideErr("#validateCode","图片验证码将用于手机号码加密");
					            } else {
					                self.text(ti+"秒后可重新获取");
					                ti--;
					            }
					        }, 1000);
					    }
						
					}else if(data.retcode == 10204){
						showErr("#validateCode",data.msg);
						return false;
					}else if(data.retcode == 10217){
						showErr("#validateCode",IC_MSG.M05);
						return false;
					}else{
						showErr("#validateCode",IC_MSG.M03);
						return false;
					}
				}else if(data.status == -2){
					showErr("#validateCode",IC_MSG.M05);
					changeCaptcha();
					$('#captcha').focus();
					return false;
				}else if(data.status == -3){
					showErr("#captcha","验证码错误");
					$('#getValidateCode').attr('disabled',true).removeClass('btn-5').addClass('btn-5-disable');
					changeCaptcha();
					$('#captcha').focus();
					return false;
				}else{
					showErr("#validateCode",IC_MSG.M03);
					return false;
				}					
			}else{
				showErr("#validateCode",IC_MSG.M03);
				return false;
			}
		}
	});
}

function enableInput(){
	$('#getValidateCode').removeAttr('disabled');
	$('#register-btn').removeAttr('disabled');
	$('#validateCode').removeAttr('disabled');
	$('#password').removeAttr('disabled');
	$('#repassword').removeAttr('disabled');
	$('#captcha').removeAttr('disabled');
}
function disableInput(){
	$('#getValidateCode').attr('disabled',true).removeClass('btn-5').addClass('btn-5-disable');
	$('#register-btn').attr('disabled',true);
	$('#validateCode').attr('disabled',true);
	$('#password').attr('disabled',true);
	$('#repassword').attr('disabled',true);
	$('#captcha').attr('disabled',true);
}
function changeCaptcha(){
	var captchaUrl=XJB.Urls.captcha+"?"+new Date().getTime();
	$("img.captcha-Img").attr("src",captchaUrl);
	$("#captcha").val("");
}
function checkCode(){
		var captcha=$("#captcha").val();
		if(captcha.bLength()==0){
			$('#getValidateCode').attr('disabled',true).removeClass('btn-5').addClass('btn-5-disable');
			showErr("#captcha","验证码不能为空");
			VALID.CR=false;
			return false;
		}else{
		if(!VALID.MB) {
			VALID.CR=false;
			return false;
		}else{
			$.ajax({
				type : "get",
				url : XJB.Urls.validCaptcha,
				data :{
					"captchaCode" : captcha
					  },
				dataType : "json",
				timeout : 5000,
				cache : false,
				async:false,
				success : function(data, textStatus) {
					if (typeof(data) != 'undefined' && data != null && data != 'null') {
						if (data.status == 1) {
							$('#getValidateCode').removeAttr('disablfed').removeClass('btn-5-disable').addClass('btn-5');
							hideErr("#captcha","图片验证码将用于手机号码加密");
							VALID.CR=true;
							return true;
						}else{
							showErr("#captcha","验证码错误");
							VALID.CR=false;
							return false;
						}
					}else{
						showErr("#captcha","验证码错误");
						VALID.CR=false;
						return false;
					}
				}
			});

		}
	}
}
String.prototype.bLength = function() {
	if (!this) {
		return 0;
	}
	var b = this.match(/[^\x00-\xff]/g);
	return (this.length + (!b ? 0 : b.length));
};
String.prototype.trim = function() {
	return this.replace(/^(\u3000|\s|\t|\u00A0)*|(\u3000|\s|\t|\u00A0)*$/g, "");
};

/** ------------- 合并S2 -------------- **/

function regist(){
	if (_validatorForm()){
		//密码MD5加密
//		XJB.Utils.md5Pwd("#password");
//		XJB.Utils.md5Pwd("#repassword");
		$("#registForm").submit();
	}
}

function update() {
	if(!checkRealName()){
		return false;
	}
	
	if(!_validatorIdNumber()){
		return false;
	}

	$("#updateForm").submit();
}

function checkRealName(){
	var realName=$("#realname").val();
	realName = realName.trim();
	realName=realName.replace(/ /g,'');
	$("#realname").val(realName);
	if(realName.length==0||(!/^[\u4e00-\u9fa5]{2,15}$/.test(realName))||realName.bLength()<4||realName.bLength()>30){
		showErr("#realname","真实姓名只能是中文，请正确填写");
		VALID.RN=false;
		return false;
	}else{
		hideErr("#realname","真实姓名只能是中文");
		VALID.RN=true;
		return true;
	}
}

function _validatorIdNumber(){
	//初始化页面
	showylb_mobile("mobileshow");
	if($("#idnumber").val().length==0||!(XJB.Utils.isCardId($("#idnumber").val()))){
		showErr("#idnumber","身份证号只能是18位且末位如为X需大写，请正确填写");
		return false;
	}else{
			var idNumber=$("#idnumber").val();
			if(LAST_VALUE.IN_LAST != idNumber || LAST_VALUE.IN_LAST == ""){
				hideErr("#idnumber","请填写18位身份证号，如末位是X，请输入大写X");
				VALID.IN=false;
				LAST_VALUE.IN_LAST = idNumber;
				checkIdNumber(idNumber);
			}
			return VALID.IN;
	}
}
function _vaildIdNumber(){
	if(VALID.RN&&VALID.IN){
		if(!VALID.MB){
			$("#mobile").val("");
		}
		$("form[name=registForm]").submit();
	}
}
var IN_MSG = {
		M01 : "理财账户实名身份认证，请正确填写证件号。",
		M02 : "请输入18位身份证号。",
		M03 : "请填写正确的身份证号。",
		M04 : "此身份证号已注册。",
		M05 : "身份验证失败，请正确填写您的身份信息。",
		M06 : "此身份证无效",
		M07 : '盈利宝登录超时，请重新登录,<a href="/user/login.jspa" class="link" target="_blank">登录</a>'
	};
var VALID={
		RN:false,
		IN:false,
		MB:false,
		UN:false,
		PW:false,
		RW:false,
		IC:false
	};

	var LAST_VALUE = {
		RN_LAST:"",
		IN_LAST:"",
		MB_LAST:"",
		UN_LAST:"",
		PW_LAST:"",
		IC_LAST:""
	};
var IV_MB_MSG = {
		M01 : "如果是好友邀请您来开户的，请填写他的手机号或邀请码。",
		M02 : "请填写正确的手机号或邀请码。",
		M03 : '该手机号已经开户',
		M04 : "验证失败",
		M05 : "手机号将用于接收账户交易信息及找回密码"
	};
//验证身份证是否已注册
function checkIdNumber(idNumber){
	if(idNumber==undefined){
		showErr("#idnumber",IN_MSG.M01);
		VALID.IN=false;
	}
	
	if($('#idNumber_hide').val()==idNumber){
		hideErr("#idnumber","请填写18位身份证号，如末位是X，请输入大写X");
		VALID.IN=true;
		return;
	}
	$.ajax({
		type : "post",
		url : XJB.Urls.vaildIdNumber,
		data :{
			"idNumber" : idNumber
			  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success : function(data, textStatus) {
			if (typeof(data) != 'undefined' && data != null && data != 'null') {
				if(data.status == 1){
					if (data.retcode == 0) {
						hideErr("#idnumber","请填写18位身份证号，如末位是X，请输入大写X");
						VALID.IN=true;
					}else{
						VALID.IN=false;
						if(data.msg!=undefined&&data.msg!=""){
							showErr("#idnumber",data.msg);
						}else{
							showErr("#idnumber",IN_MSG.M03);
						}
					}
				}else if(data.status == -2){
					showErr("#idnumber",IN_MSG.M07);
					VALID.IN=false;
				}else if(data.status == -3){
					if(data.retcode == 0){
						hideErr("#idnumber","请填写18位身份证号，如末位是X，请输入大写X");
						VALID.IN=true;
					}else if(data.retcode == 10104){
						//showErr("#idnumber",'此身份证号已注册盈利宝,请直接<a href="javascript:void(0)" class="link" onclick="bind_zqt(\'s\');">授权</a>');
						//身份证已注册盈利宝，隐藏下面的手机号，显示盈利宝密码框
						showErr("#idnumber",'您已经是金融界盈利宝的用户，请直接绑定');
						YESNO_YLB=true;
						//显示盈利宝密码输入框
						showylb_mobile("ylbPassshow");
						$("#mobile").val(data.mobileno);
						checkMobile(data.mobileno);//验证手机号码
						//自动设置帐号
						$('#userInfo').val(idNumber);
						//修改输入框架不可用
						
						VALID.IN=false;
					}else{
						showErr("#idnumber",data.msg);
						VALID.IN=false;
					}
					
				}else if(data.status == -4){
					showErr("#idnumber",'您已经是金融界证券通的用户，请直接登录');
				}else{
					showErr("#idnumber",IN_MSG.M05);
					VALID.IN=false;
				}
			}else{
				showErr("#idnumber",IN_MSG.M05);
				VALID.IN=false;
			}
		}
	});
}
//显示和隐密码框
function showylb_mobile(tyle){
	 $(".hide_one").show();
    if(tyle == "mobileshow"){
        $(".hide_one").show();
        //$(".hide_two").hide();
        //按钮
        $("#registylb").show();
        //按钮
        $("#sqylb").hide();
        //初始化参数
        $('#userInfo').val('');
        //手机号框得到焦点
        //$("#mobile").focus();
        $("#mobile").attr("disabled",false);
        ///$("#mobile").val("");
        //初始化全局参数
        initParam();
    }else if(tyle == "ylbPassshow"){
        //$(".hide_one").hide();
        //$(".hide_two").show();
        //按钮
        $("#registylb").hide();
        //按钮
        $("#sqylb").show();
        //$("#shouquan_pwd").val('');
        //盈利宝密码框得到焦点
        //$("#shouquan_pwd").focus();
        
        $("#mobile").attr("disabled",true);
    }
    
}
//初始化参数
var initParam = function(){
	LAST_VALUE.RN_LAST="";
	LAST_VALUE.IN_LAST="";
}
function IsNum(e) {
    var k = window.event ? e.keyCode : e.which;
    if (((k >= 48) && (k <= 57)) || k == 8 || k == 0) {
    } else {
        if (window.event) {
            window.event.returnValue = false;
        }
        else {
            e.preventDefault(); 
        }
    }
}

//验证手机号或者邀请码
function _validatorInviteMobile(mobile) {
	if (mobile == undefined) {
		mobile = $("#invite_mobile").val();
		mobile = mobile.trim();
		$("#invite_mobile").val(mobile);
	}
	
	if(LAST_VALUE.MB_LAST != mobile || LAST_VALUE.MB_LAST == ""){
		hideErr("#invite_mobile",IV_MB_MSG.M01);
		VALID.MB=false;
		LAST_VALUE.MB_LAST = mobile;
		if(mobile.length<4&&mobile.length>0){
			showErr("#invite_mobile",IV_MB_MSG.M02);	
			$("#invitetype").val(0);
		}else if(mobile.length==4){
			if(!/^[A-Za-z0-9]{4}$/.test(mobile)){
				$("#invitetype").val(0);
				showErr("#invite_mobile",IV_MB_MSG.M02);
				VALID.MB=false;
			}else{
				$("#invitetype").val(2);
				hideErr("#invite_mobile",IV_MB_MSG.M01);
				VALID.MB=true;
			}
		}else if(mobile.length==0){
			$("#invitetype").val(0);
			hideErr("#invite_mobile",IV_MB_MSG.M01);
		}else{
			if (!/^1[\d]{10}$/.test(mobile)) {
				showErr("#invite_mobile",IV_MB_MSG.M02);
				$("#invitetype").val(0);
				VALID.MB=false;
			}else{
				$("#invitetype").val(1);
				hideErr("#invite_mobile");
				VALID.MB=true;
			}
		}
		
	}
}
/** ----------- all -------------*/
	
function bindTozqt(sign){	
	//var pwd = $("#shouquan_pwd").val();
	var validateCode = $("#validateCode").val();
	var realname = $("#realname").val();
	if(validateCode ==""){
		showErr("#validateCode","验证码不能为空");
		return false;
	}
	//var pwdMd5 =XJB.Utils.md5(pwd);
	var userInfo = $("#userInfo").val();
	$.ajax({
		type : "get",
		url :"/stock/Authylb.jspa",
		data :{
			"userInfo" : userInfo,
			"validateCode" :validateCode,
			"realname" :realname,
			"sign" :sign
		  },
		dataType : "json",
		timeout : 5000,
		async:false,
		cache : false,
		success:function(data){
			  if(data &&data !=undefined){
				  if(data.error){
					  if(data.error =="_login"){
						  window.location.href="/stock/login.jspa";
					  }
					  else if(data.error=="用户名与身份证谁失败"){//验证失败转换其它 div
						  $("#idNumberError").show();
						  $("#zqtinfo").hide();
					  }else{
						  $("#pwd_error").html(data.error);
					  }
				  }else{
					window.location.href="/stock/accountIndex.jspa?t="+Math.random();  
				  }	
			  }else{
				  $("#pwd_error").html("系统错误");
			  }
			  
		 }		  
	});
}
function setZqtInfo(){
	$("#idNumberError").hide();
	$("#zqtinfo").show();
}
function bind_zqt(sign){
	var accountInfo ="";
	if(sign =='s'){
		accountInfo = $("#idnumber").val();
	}else if(sign = 'm'){
		accountInfo = $("#mobile").val();
	}
	
	if(accountInfo ==""){
		return false;
	}
	var authHtml = "<div class='dlg-tip-zc dlg-w1' style='height:122px;'> "+
						"<p class='dlg-fgrey'>您的账号已注册盈利宝，请授权开通证券通</p> "+
						"<div class='dlg-frm-zc'>"+
							"<p>账号<span class='dlg-txt' style='display:inline-block;background:#eee;margin-left:5px'>"+accountInfo+"</span></p>"+
							"<input type ='hidden' id='userInfo' value='"+accountInfo+"'/>"+
							"<p>密码<input type='password' class='dlg-txt' id='shouquan_pwd'/></p>"+
						"</div>"+
						"<p class='dlg-tip-error' id='pwd_error'></p>"+
					  "</div>"+
					  "<p class='jrj-tc mt'><input class='dlg-btn' type=‘button' value='确定' onclick='bindTozqt(\" "+sign+"\");'><a href='https://t.jrj.com/user/forget.jspa' target='_blank' style='padding-left: 10px;color: blue;font-size: 12px;'>忘记密码</a></p>";
					  
	JRJ.Dialogs.standardDialog({
		title: "账户授权",
		width:280,
		content: authHtml,
		hasOkBtn: false,
		hasCancelBtn: false,

		cancelCallback: function(){
		}
	})
}

$(document).ready(function() {
	var mobileval = $("#mobile").val()
	if (mobileval.bLength() != 0&&!/^1[\d]{10}$/.test(mobileval) && !/^0\d{2,4}-?\d{7,8}$/.test(mobileval)) {
			showErr("#mobile",MB_MSG.M02);
			VALID.MB=false;
	} 
	$("#agree").bind("click",function(){
		if($("#agree").attr("checked")){
			$("#agree_error").hide();
		}
	});
//	$("#captcha").bind("blur", function() {
//		checkCode();
//	});
	$("#mobile").bind("blur", function() {
		_validatorMobile();
	});	
//	$("#password").bind("blur", function() {
//		_validatorPassword();
//	});
//	$("#repassword").bind("blur", function() {
//		_validatorRePassword();
//	});
	$("#validateCode").bind("blur", function() {
		_validatorIdentifyingCode();
	});
	$("#getValidateCode").click(function(){
		if(!$(this).attr('disabled')){		
			getIdentifyingCode();
		}
	});
	$("#idnumber").bind("blur",function(){
	   	_validatorIdNumber();
	});
	$("#realname").bind("blur",function(){
		_validatorRealName();
	});
//	$("#invite_mobile").bind("blur",function(){
//	    _validatorInviteMobile();
//    });
	
	$('.choose_agree').click(function () {
        var type = $(this).data('type');

        JRJ.Dialogs.iframeDialog({
        	width:600,
            content: [''].join(''),
            hasOkBtn: true,
            okBtnText: '已经阅读',
            title: ("证券通用户协议"),
            titleRight : '',
            hasCancelBtn: false,
            enableKeyCtrl: true,
            ifrSrc: (type == 'open-account' ? XJB.Urls.protocolOpen :XJB.Urls.protocolKnow ),
            ifrReHeight: true,
            okCallback: function () {
            	$("#agree").attr("checked","checked");
            	$("#agree_error").hide();
                JRJ.Dialogs.close();
                return true;
            }
        });
    });
	
	$("#register-btn").click(function(){
		if(!$(this).attr('disabled')){
			regist();
		}
		return false;
	});
	
	$("#update-btn").click(function(){
       	if(!$(this).attr('disabled')){
			update();
		}
       	return false;
    });
});

function testAttribute(element, attribute) {
   var test = document.createElement(element);
   if (attribute in test) {
    return true;
  }
else 
  return false;
}

$(function(){
	if (!testAttribute('input', 'autofocus'))
  	document.getElementById('realname').focus(); 
	//for browser has no autofocus support, set focus to Text2.
})

