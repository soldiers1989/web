if(typeof JRJ=="undefined" || !JRJ){
	var JRJ = {};
} 

if(typeof JRJ.ui=="undefined" || !JRJ.ui){
	JRJ.ui = {};
} 

/*JRJ.Mask*/
if (typeof JRJ == "undefined") { JRJ = {} } (function ($) { JRJ.Mask = function (config) { var config = config || {}; this.config = { opacity: 0.1, bgColor: "#000", zIndex: 9400 }; $.extend(this.config, config) }; JRJ.Mask.prototype = { show: function () { var self = this, cfg = this.config; self.mask = $('<div class="album-mask"></div>'); self.mask.css({ position: "absolute", zIndex: cfg.zIndex, top: "0px", left: "0px", width: $(window).width(), height: $(document).height(), backgroundColor: cfg.bgColor, opacity: cfg.opacity }); if (navigator.appVersion.indexOf("MSIE 6.0") != -1) { self.mask.bgiframe() } self.resizeHandler = function (event) { event.data.obj._resize() }; self.mask.appendTo("body"); $(window).bind("resize", { obj: self }, self.resizeHandler) }, close: function () { this.mask.remove(); $(window).unbind("resize", this.resizeHandler) }, _resize: function () { this.mask.css({ width: $(window).width(), height: $(document).height() }) } } })(jQuery);

(function($){
	/**
	 * 登录对话框
	 * @class 登录对话框
	 * @link <a target="_blank" href="../../src-new/ui/LoginDialog/example.html">例子</a>
	 * @param {string} spid 由SSO分配的应用id
	 * @param {string} lhref 当前页地址，心远说目前sso已不用此参数
	 * @param {string} isVerifyCode 图片验证码，应为false
	 * @param {string} returnUrl SSO返回认证结果时需要重定向到的URL
	 * @param {string} registerReturnUrl SSO注册成功重定向到的URL
	 * @param {boolean} isAddTicket 是否要票据，默认为true
	 * @param {string} formTarget form表单target属性，默认为_self
	 * @param {function} callback ajax登录的回调函数，有callback参数就认为启动ajax登录
	 * @param {string} sinaReturnUrl 新浪微博登录返回的url，默认不加返回地址
	 * @param {string} qqReturnUrl 腾讯微博登录返回的url，默认不加返回地址
	 * @param {string} charset 登录接口接收的编码
	 */
	JRJ.ui.LoginDialog = function(spid,lhref,isVerifyCode,returnUrl,registerReturnUrl,isAddTicket,formTarget,callback,sinaReturnUrl,qqReturnUrl,charset){
		 this.spid = spid;
		 this.locationHref = lhref;//当前页地址
		 this.isVerifyCode = isVerifyCode;//图片验证码，应为false
		 this.returnUrl = returnUrl; //SSO返回认证结果时需要重定向到的URL
		 this.registerUrl = "https://sso.jrj.com.cn/sso/passportLogin";
		 this.registerReturnUrl = registerReturnUrl;//SSO注册成功重定向到的URL
		 this.charset = charset;
		 this.isAddTicket = true;
		 if( typeof(isAddTicket) != "undefined" && !isAddTicket){
		 		this.isAddTicket = false;	
		 }
		 this.formTarget = "_self";
		 if( typeof(formTarget) != "undefined" ){
		 		this.formTarget = formTarget;	
		 }
		 this.callback = null;
		 if( typeof(callback) != "undefined" ){
		 		this.callback = callback;	
		 }
		 this.sinaReturnUrl = null;
		 if( typeof(sinaReturnUrl) != "undefined" ){
			this.sinaReturnUrl = sinaReturnUrl;
		 }
		 this.qqReturnUrl = null;
		 if( typeof(qqReturnUrl) != "undefined" ){
			this.qqReturnUrl = qqReturnUrl;
		 }
		 this.mask = null;
		 this.nameNullAlert = "用户名不能为空";
		 this.pwdNullAlert = "密码不能为空";		 
		 this.loginFailedAlert = "用户名或密码不正确";		 
	}
	JRJ.ui.LoginDialog.prototype = {
		/**
		 * @private
		 */
		_init:function(){
			this.mask = new JRJ.Mask();			
			
			var  charsetStr = this.charset ? '<input type="hidden" name="charset" value="'+this.charset+'" />' : ''
				,regLink = 'https://sso.jrj.com.cn/sso/passportRegister/register.jsp'
				,retPwd = 'https://sso.jrj.com.cn/sso/retrievePassport/retrieveByMobile.jsp'
				,dialogHtml = ['<div class="album-dialog dl_album" style="width: 640px;">',
                        '<div class="md-container">',
                            '<i id="ld-btnClose" class="md-close" title="关闭">&nbsp;</i><div class="md-titlebar dl_titlebar">欢迎登录</div>',
                            '<div class="md-body">',
                                '<div class="md-content dl_md">',
                                    '<div class="dlg-login clearfix">',
                                    //'<form action="http://sso.jrj.com.cn/sso/passportLogin"  method="POST" class="dlg-login clearfix">',
										// '<input type="hidden" id="ReturnURL" name="ReturnURL" value="'+window.location.href+'">',
	                                    // charsetStr,
	                                     '<div class="dl_left">',
                                                '<h2>用金融界账户登录</h2>',
                                                '<p class="dl_text">',
                                                   '<label id="user">用户名：</label><input id="dl_blur" type="text" name="LoginID" value=""/>',
                                                '</p>',
                                                '<span id="ld-Alert" class="dl_red"></span>',
                                                '<p class="dl_text">',
                                                   '<label id="pass">密 码：</label><input type="password" name="Passwd" value=""/>',
                                                '</p>',
                                                '<p class="dl_check">',
                                                   /* '<input name="login" type="checkbox" id="ld-auto-login" /><label>自动登录</label>', */
                                                   '<a href="'+retPwd+'" title="">找回密码？</a>',
                                                '</p>',
                                                '<p class="dl_but">',
                                                    '<a href="#" title="" id="ld-btnLogin">登录</a>',
                                                '</p>',

	                                     '</div>',
	                                     '<div class="dl_right">',
                                              '<h2>使用第三方账户的登录</h2>',
                                              '<a class="dl_sina0" href="#" title="">新浪微博</a>',
                                              '<a class="dl_sina1" href="#" title="">腾讯QQ</a>',
                                              '<a class="dl_sina2" href="#" title="">腾讯微博</a>',
                                              '<a class="dl_sina3" href="#" title="">人人网</a>',
	                                     '</div>',
                                    '</div>',
                                '</div>',
                                
                            '</div>',
                            '<div class="dl_foot">',
	                                          '<a class="dl_id" href="'+regLink+'" title>还没有账号？赶快注册！开始i投顾</a>',
	                                          '<a class="dl_zhuc" href="'+regLink+'" title>免费注册</a>',

	                        '</div>',
                        '</div>',
                    '</div>'].join('');
						
			this.dialogDiv = $( dialogHtml );
			this.dialogDiv.appendTo($("body"));

			
			this.form = this.dialogDiv.find(".dlg-login");
			this.nameInput = this.form.find("input[name=LoginID]");			
			this.pwdInput = this.form.find("input[name=Passwd]");			
			this.loginBtn = this.dialogDiv.find("#ld-btnLogin");			
			this.closeBtn = this.dialogDiv.find("#ld-btnClose");
			this.alertDiv = this.dialogDiv.find("#ld-Alert");
			this.autoCheckbox = this.dialogDiv.find("#ld-auto-login");
			
			this.alertDiv.html("");
			this._bindEvent();
		},
		_bindEvent: function(){
			var self = this;
            self.nameInput.bind("blur", function(event){
	            self._hideAllPrompt();
            });
			
            self.pwdInput.bind("keyup", function(event){
                if(event.keyCode == 13){
                   self._submit();
                }
            });
			self.pwdInput.bind("focus", function(event){
            });
            self.pwdInput.bind("blur", function(event){
                self._hideAllPrompt();
            });
			
            self.loginBtn.bind("click", function(event){
				self._submit();
            });
			
            self.closeBtn.bind("click", function(event){
                self.close();
                return false;
            });

            $(window).bind("resize", function(event){                 
                self._setPosition();
            });
            $(window).bind("scroll", function(event){
                self._setPosition();
            });
		},
		/**
		 * 打开登录对话框
		 */
		show:function(){
            var self = this;
			this._init();
			this._setPosition();
			self.mask.show();
            self.dialogDiv.addClass('album-dialog-show');
			JRJ.ui.LoginDialog.focusInstance = this;
			this.nameInput.focus();
		},
		/**
		 * 关闭登录对话框
		 */
		close:function(){
            var self = this;
			self.dialogDiv.removeClass('album-dialog-show');
			setTimeout(function () {self.dialogDiv.remove(); },300);
			this.mask.close();
			JRJ.ui.LoginDialog.focusInstance = null;
		},
		/**
		 * @private
		 */
		_checkForm:function(){
			var self = this, name = self.nameInput.val();
			self._hideAllPrompt();
            if(name==""){
                self._showPrompt(self.nameNullAlert, self.nameInput);
				return false;
            }
			var pwd = self.pwdInput.val();
            if(pwd==""){
                self._showPrompt(self.pwdNullAlert, self.pwdInput);
				return false;
            }
			return true;
		},
		_submit: function(){
            var self = this;
			if( !this._checkForm() ) return false;
			if(this.callback){ //
				var id = self.nameInput.val() , pwd = self.pwdInput.val();
				//alert('sdfsdf')
				$.ajax({  //'post'登录   'get'心跳 参数 passportId
					type: 'GET',
					url: 'https://sso.jrj.com.cn/sso/passportLogin?LoginID='+id+'&Passwd='+pwd+'&ReturnURL='+window.location.href,
					contentType: "application/json",
					dataType: 'jsonp',
					success: function (_data) {
						self._hideAllPrompt();
						switch( String(_data.result) ){
							case '0' : { //成功 刷用户
								window.membership.update();
								location.reload();
								break;
							}
							case '1' : { //1：用户名包含无效字符
								self._showPrompt(self.loginFailedAlert, self.loginBtn);
								break;
							}
							case '2' : { //2：用户名与密码均不能为空
								self._showPrompt(self.loginFailedAlert, self.loginBtn);
								break;
							}
							case '3' : { //3：用户不存在
								self._showPrompt(self.loginFailedAlert, self.loginBtn);
								break;
							}
							case '4' : { //4：密码不正确
								self._showPrompt(self.loginFailedAlert, self.loginBtn);
								break;
							}
							case '5' : { //5：ip黑名单
								self._showPrompt('此帐户已被禁用', self.loginBtn);
								break;
							}
							default:{}
						}
					},
					error: function (e) {
						//console.log(e.message);
					}
				});
				//var isAutoLogin = this.autoCheckbox[0].checked;
            }
			else{
				this.dialogDiv.hide();  //本应是remove，但remove后又找不到#jrj_ld_form
				this.mask.close();
				var _self = this;
				if($.browser.safari){
				  setTimeout(function (){
					  _self.form.submit();
				  }, 0);
				}else{
				  this.form.submit();
				}

				this.dialogDiv.remove();
            }
            return true;
		},
		/**
		 * @private
		 */
		_showPrompt : function(msg, input){
			this.alertDiv.html(msg);
			if(input){	
                input.focus();
			}
		},
		/**
         * @private
         */
        _hidePrompt : function(input){
            this.alertDiv.html("");
        },
		_hideAllPrompt: function(){
	        var self = this;
	        self._hidePrompt(self.nameInput);
	        self._hidePrompt(self.pwdInput);
	    },
		/**
		 * @private
		 */
		_setPosition: function() {
            var self = this;
			var top = (($(window).height() / 2) - (this.dialogDiv.outerHeight() / 2))-50;
			var left = (($(window).width() / 2) - (this.dialogDiv.outerWidth() / 2));
			if( top < 0 ) top = 0;
			if( left < 0 ) left = 0;			
			// IE6 fix
			if ($.browser.msie && parseInt($.browser.version) <= 6) top = top + $(window).scrollTop();

			this.dialogDiv.css({ 'margin': '-' + this.dialogDiv.height() / 2 + 'px 0 0 -' + this.dialogDiv.width() / 2 + 'px' });

			//if ($.browser.msie && parseInt($.browser.version) < 9) {
			//    this.dialogDiv.css({
			//        top: top + 'px',
			//        left: left + 'px'
			//    });
			//}

				self.dialogDiv.css("position", "fixed");
		},
		/**
		 * @private
		 */
		_addStyle : function(id,css,doc){
			var styleEl = $("#"+id)[0];
			if (styleEl) return; // 防止多个实例时重复添加
			var doc = doc || document;
			var style = doc.createElement("style");
			style.id = id;
			style.type="text/css";
			var head = doc.getElementsByTagName("head")[0];
			//head.insertBefore(style, head.firstChild);
			head.appendChild(style);
			if(style.styleSheet){
                style.styleSheet.cssText=css;
			}else{
                style.appendChild(doc.createTextNode(css));
			}
		}
	}
	JRJ.ui.isLogin = function( fn ){
		var userInfo = membership.getUserInfo(); //membership.js 接口
		if( fn != undefined && userInfo && userInfo.userId){
			fn && fn(userInfo);
		}else{
			new JRJ.ui.LoginDialog('zhibo', window.location.href, false, window.location.href, window.location.href, false, null, function () { window.membership.init(); }, window.location.href, window.location.href, 'utf8').show();
		}
	}
	
	JRJ.ui.LoginDialog.focusInstance = null;
	JRJ.ui.getUserInfo = function(){
		return membership.getUserInfo(); //membership.js 接口
	}
	/* $(membership.loginBtn).click(function(){ //membership.js 接口
		JRJ.ui.isLogin();
	}) */
})(jQuery);

/*md5.js*/
(function(){
	var hexcase=0;
	var b64pad="";
	var chrsz=8;
	function hex_md5(s){return binl2hex(core_md5(str2binl(s),s.length*chrsz));}
	function b64_md5(s){return binl2b64(core_md5(str2binl(s),s.length*chrsz));}
	function str_md5(s){return binl2str(core_md5(str2binl(s),s.length*chrsz));}
	function hex_hmac_md5(key,data){return binl2hex(core_hmac_md5(key,data));}
	function b64_hmac_md5(key,data){return binl2b64(core_hmac_md5(key,data));}
	function str_hmac_md5(key,data){return binl2str(core_hmac_md5(key,data));}
	function md5_vm_test(){return hex_md5("abc")=="900150983cd24fb0d6963f7d28e17f72";}
	function core_md5(x,len){x[len>>5]|=0x80<<((len)%32);x[(((len+64)>>>9)<<4)+14]=len;var a=1732584193;var b=-271733879;var c=-1732584194;var d=271733878;for(var i=0;i<x.length;i+=16)
	{var olda=a;var oldb=b;var oldc=c;var oldd=d;a=md5_ff(a,b,c,d,x[i+0],7,-680876936);d=md5_ff(d,a,b,c,x[i+1],12,-389564586);c=md5_ff(c,d,a,b,x[i+2],17,606105819);b=md5_ff(b,c,d,a,x[i+3],22,-1044525330);a=md5_ff(a,b,c,d,x[i+4],7,-176418897);d=md5_ff(d,a,b,c,x[i+5],12,1200080426);c=md5_ff(c,d,a,b,x[i+6],17,-1473231341);b=md5_ff(b,c,d,a,x[i+7],22,-45705983);a=md5_ff(a,b,c,d,x[i+8],7,1770035416);d=md5_ff(d,a,b,c,x[i+9],12,-1958414417);c=md5_ff(c,d,a,b,x[i+10],17,-42063);b=md5_ff(b,c,d,a,x[i+11],22,-1990404162);a=md5_ff(a,b,c,d,x[i+12],7,1804603682);d=md5_ff(d,a,b,c,x[i+13],12,-40341101);c=md5_ff(c,d,a,b,x[i+14],17,-1502002290);b=md5_ff(b,c,d,a,x[i+15],22,1236535329);a=md5_gg(a,b,c,d,x[i+1],5,-165796510);d=md5_gg(d,a,b,c,x[i+6],9,-1069501632);c=md5_gg(c,d,a,b,x[i+11],14,643717713);b=md5_gg(b,c,d,a,x[i+0],20,-373897302);a=md5_gg(a,b,c,d,x[i+5],5,-701558691);d=md5_gg(d,a,b,c,x[i+10],9,38016083);c=md5_gg(c,d,a,b,x[i+15],14,-660478335);b=md5_gg(b,c,d,a,x[i+4],20,-405537848);a=md5_gg(a,b,c,d,x[i+9],5,568446438);d=md5_gg(d,a,b,c,x[i+14],9,-1019803690);c=md5_gg(c,d,a,b,x[i+3],14,-187363961);b=md5_gg(b,c,d,a,x[i+8],20,1163531501);a=md5_gg(a,b,c,d,x[i+13],5,-1444681467);d=md5_gg(d,a,b,c,x[i+2],9,-51403784);c=md5_gg(c,d,a,b,x[i+7],14,1735328473);b=md5_gg(b,c,d,a,x[i+12],20,-1926607734);a=md5_hh(a,b,c,d,x[i+5],4,-378558);d=md5_hh(d,a,b,c,x[i+8],11,-2022574463);c=md5_hh(c,d,a,b,x[i+11],16,1839030562);b=md5_hh(b,c,d,a,x[i+14],23,-35309556);a=md5_hh(a,b,c,d,x[i+1],4,-1530992060);d=md5_hh(d,a,b,c,x[i+4],11,1272893353);c=md5_hh(c,d,a,b,x[i+7],16,-155497632);b=md5_hh(b,c,d,a,x[i+10],23,-1094730640);a=md5_hh(a,b,c,d,x[i+13],4,681279174);d=md5_hh(d,a,b,c,x[i+0],11,-358537222);c=md5_hh(c,d,a,b,x[i+3],16,-722521979);b=md5_hh(b,c,d,a,x[i+6],23,76029189);a=md5_hh(a,b,c,d,x[i+9],4,-640364487);d=md5_hh(d,a,b,c,x[i+12],11,-421815835);c=md5_hh(c,d,a,b,x[i+15],16,530742520);b=md5_hh(b,c,d,a,x[i+2],23,-995338651);a=md5_ii(a,b,c,d,x[i+0],6,-198630844);d=md5_ii(d,a,b,c,x[i+7],10,1126891415);c=md5_ii(c,d,a,b,x[i+14],15,-1416354905);b=md5_ii(b,c,d,a,x[i+5],21,-57434055);a=md5_ii(a,b,c,d,x[i+12],6,1700485571);d=md5_ii(d,a,b,c,x[i+3],10,-1894986606);c=md5_ii(c,d,a,b,x[i+10],15,-1051523);b=md5_ii(b,c,d,a,x[i+1],21,-2054922799);a=md5_ii(a,b,c,d,x[i+8],6,1873313359);d=md5_ii(d,a,b,c,x[i+15],10,-30611744);c=md5_ii(c,d,a,b,x[i+6],15,-1560198380);b=md5_ii(b,c,d,a,x[i+13],21,1309151649);a=md5_ii(a,b,c,d,x[i+4],6,-145523070);d=md5_ii(d,a,b,c,x[i+11],10,-1120210379);c=md5_ii(c,d,a,b,x[i+2],15,718787259);b=md5_ii(b,c,d,a,x[i+9],21,-343485551);a=safe_add(a,olda);b=safe_add(b,oldb);c=safe_add(c,oldc);d=safe_add(d,oldd);}
	return Array(a,b,c,d);}
	function md5_cmn(q,a,b,x,s,t)
	{return safe_add(bit_rol(safe_add(safe_add(a,q),safe_add(x,t)),s),b);}
	function md5_ff(a,b,c,d,x,s,t)
	{return md5_cmn((b&c)|((~b)&d),a,b,x,s,t);}
	function md5_gg(a,b,c,d,x,s,t)
	{return md5_cmn((b&d)|(c&(~d)),a,b,x,s,t);}
	function md5_hh(a,b,c,d,x,s,t)
	{return md5_cmn(b^c^d,a,b,x,s,t);}
	function md5_ii(a,b,c,d,x,s,t)
	{return md5_cmn(c^(b|(~d)),a,b,x,s,t);}
	function core_hmac_md5(key,data)
	{var bkey=str2binl(key);if(bkey.length>16)bkey=core_md5(bkey,key.length*chrsz);var ipad=Array(16),opad=Array(16);for(var i=0;i<16;i++)
	{ipad[i]=bkey[i]^0x36363636;opad[i]=bkey[i]^0x5C5C5C5C;}
	var hash=core_md5(ipad.concat(str2binl(data)),512+data.length*chrsz);return core_md5(opad.concat(hash),512+128);}
	function safe_add(x,y)
	{var lsw=(x&0xFFFF)+(y&0xFFFF);var msw=(x>>16)+(y>>16)+(lsw>>16);return(msw<<16)|(lsw&0xFFFF);}
	function bit_rol(num,cnt)
	{return(num<<cnt)|(num>>>(32-cnt));}
	function str2binl(str)
	{var bin=Array();var mask=(1<<chrsz)-1;for(var i=0;i<str.length*chrsz;i+=chrsz)
	bin[i>>5]|=(str.charCodeAt(i/chrsz)&mask)<<(i%32);return bin;}
	function binl2str(bin)
	{var str="";var mask=(1<<chrsz)-1;for(var i=0;i<bin.length*32;i+=chrsz)
	str+=String.fromCharCode((bin[i>>5]>>>(i%32))&mask);return str;}
	function binl2hex(binarray)
	{var hex_tab=hexcase?"0123456789ABCDEF":"0123456789abcdef";var str="";for(var i=0;i<binarray.length*4;i++)
	{str+=hex_tab.charAt((binarray[i>>2]>>((i%4)*8+4))&0xF)+
	hex_tab.charAt((binarray[i>>2]>>((i%4)*8))&0xF);}
	return str;}
	function binl2b64(binarray)
	{var tab="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";var str="";for(var i=0;i<binarray.length*4;i+=3)
	{var triplet=(((binarray[i>>2]>>8*(i%4))&0xFF)<<16)|(((binarray[i+1>>2]>>8*((i+1)%4))&0xFF)<<8)|((binarray[i+2>>2]>>8*((i+2)%4))&0xFF);for(var j=0;j<4;j++)
	{if(i*8+j*6>binarray.length*32)str+=b64pad;else str+=tab.charAt((triplet>>6*(3-j))&0x3F);}}
	return str;}
	window._hex_md5_ = hex_md5;
})();

/**
http://sso.jrj.com.cn/sso/passport/userOnlineStatus.jsp   //在线状态  赵士伟
数据项目说明
请求参数：
数据项	说明	备注
passportIds	通行证Id(用户id),可以多个以逗号分隔	String(必传)
返回值：
数据项	说明	备注
passportId	通行证id	String
isOnline	是否在线	String（0: 否 1:是）

返回值格式：
{"passportId":"21021119821218141"," isOnline ":"0","passportId":"21021119821218141"," isOnline ":"1"}


http://sso.jrj.com.cn/sso/passportLogin   用户登录  赵士伟
数据项目说明
请求参数：
数据项	说明	备注
LoginID	登陆名	String(必传)
Passwd  密码
ReturnURL
charset （utf-8/gbk）

返回值：
数据项	说明	备注
result	结果	String（0：成功 1：用户名包含无效字符 2：用户名与密码均不能为空 3：用户不存在 4：密码不正确 5：ip黑名单）
LoginID	登录名	String
isComplete 是否需要完善用户名
isResetPwd 是否需要完善密码

POST方式
*/

      

