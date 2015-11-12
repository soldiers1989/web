﻿/**
 * @fileOverview JRJ登录对话框
 * @copyright (c) 2009 (jrj.com)
 * @author jianjun.wang@jrj.com.cn
 * @version 1.0.1
 * @depend jquery.js mask.js
 */

/*jrj mask*/ 
if(typeof JRJ=="undefined"||!JRJ){var JRJ={}}if(typeof JRJ.ui=="undefined"||!JRJ.ui){JRJ.ui={}}(function(a){JRJ.ui.Mask=function(){this.maskOpacity=0.6;this.maskColor="#000";this.mask=null};JRJ.ui.Mask.prototype={show:function(c){var b={maskOpacity:this.maskOpacity,maskColor:this.maskColor};a.extend(b,c);this.mask=a('<div class="jrj-dialog-mask"></div>');this.mask.appendTo("BODY");this.mask.css({position:"absolute",zIndex:9998,top:"0px",left:"0px",width:a(window).width(),height:a(document).height(),background:b.maskColor,opacity:b.maskOpacity});if(navigator.appVersion.indexOf("MSIE 6.0")!=-1){this.mask.bgiframe()}this.winResizeHandler=function(d){var e=d.data.obj;e._resize()};a(window).bind("resize",{obj:this},this.winResizeHandler)},close:function(){this.mask.remove();a(window).unbind("resize",this.winResizeHandler)},_resize:function(){this.mask.css({width:a(window).width(),height:a(document).height()})}}})(jQuery);
/**动态加载 css js**/
$.extend({
	includePath: '',
    include: function(file)
		{
	var files = typeof file == "string" ? [file]:file;
        for (var i = 0; i < files.length; i++) {
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            var tag = isCSS ? "link" : "script";
			var srct=isCSS ? "href" : "src";
			if ($(tag+"["+srct+"="+$.includePath + name+"]").length == 0){
			 var styleTag = document.createElement(tag);
			 var type=isCSS ? "text/css" : "text/javascript";
			 styleTag.setAttribute('type', type);
			 if(isCSS)
				styleTag.setAttribute('rel', 'stylesheet');
			 styleTag.setAttribute(srct, $.includePath + name);
			 $("head")[0].appendChild(styleTag);
			}
	}
    }
});
(function($){
	var styles = [];
	$.include(styles);
	/**
	 * 登录对话框
	 * @class JRJ登录对话框
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
	JRJ.ui.LoginDialog = function(spid,lhref,isVerifyCode,returnUrl,registerReturnUrl,isAddTicket,formTarget,callback,sinaReturnUrl,qqReturnUrl,charset,closeable,closeTarget){
		 this.spid = spid;
		 this.locationHref = lhref;//当前页地址，心远说目前sso已不用此参数
		 this.isVerifyCode = isVerifyCode;//图片验证码，应为false
		 this.returnUrl = returnUrl; //SSO返回认证结果时需要重定向到的URL
		 this.registerUrl = "http://i.jrj.com.cn/register.faces?ReturnURL=";
		 this.registerReturnUrl = registerReturnUrl;//SSO注册成功重定向到的URL
		 this.charset = charset;
		 this.isAddTicket = true;
		 this.closeable=true;
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
		 if( typeof(closeable) != "undefined" ){
				this.closeable = closeable;
			 }
		 this.closeTarget=null;
		 if(typeof(closeTarget)!="undefined"){
			 this.closeTarget=closeTarget;
		 }
		 this.mask = null;
		 this.nameNullAlert = "用户名不能为空";
		 this.pwdNullAlert = "密码不能为空";		 
	}
	JRJ.ui.LoginDialog.prototype = {
		/**
		 * @private
		 */
		_init:function(){
			$(".jrj-ld").remove();//target="blank",且页面存在多个对话框实例时，登录时hide对话框有问题
//			this._addStyle("jrj-ld-style",styles);
			this.mask = new JRJ.ui.Mask();			
			
			var charsetStr = this.charset ? '<input type="hidden" name="charset" value="'+this.charset+'" />' : '';
			
			var  dialogHtml = ['<div class="jrj-ld">',
				 '<div class="jrj-ld-bar"><span>用户登录</span><a class="jrj-ld-close"  href="javascript:;" title="关闭" target="_self"></a></div>',
				 '<div class="jrj-ld-body">',
				 '    <div class="jrj-ld-left">',
				 '        <form action="//sso.jrj.com.cn/sso/ssologin" onsubmit="$(this).attr(\'action\',\'http://sso.jrj.com.cn/sso/ssologin\');"  method="post" target="_self">',
				 '            <input value="'+this.isVerifyCode+'" name="isVerifyCode" type="hidden" />',
				 '            <input value="'+this.spid+'" name="SPID" type="hidden" />',
				 '            <input value="'+this.locationHref+'" name="locationHref" type="hidden" />',
				 '            <input value="'+this.returnUrl+'" name="ReturnURL" type="hidden" />',
               
	             '<input type="hidden" value="'+this.isAddTicket+'" name="isAddTicket"/>',
	             charsetStr,
	            
				 '            <div class="jrj-ld-alert">',
				 '<i class="alert-icon"></i><span></span>',
				 '            </div>',
				 '            <div class="jrj-ld-line">',
				 '                <label>用户名:</label>',
				 '                <input class="jrj-ld-text" name="LoginID" type="text"/>',
				 '                <a class="jrj-ld-clear" title="清除用户名" href="javascript:void(0)" tabindex="20" target="_self"></a> ',
				 '            </div>',
				 '            <div class="jrj-ld-line">',
				 '                <label>密　码:</label>',
				 '                <input class="jrj-ld-text" name="Passwd" type="password" />',
				 '            </div>',		                
				 '            <div class="jrj-ld-line"> <a class="jrj-ld-btn" href="javascript:;" target="_self"><span>登 录</span></a><a class="ml10" target="_blank" href="http://i.jrj.com.cn/retrievePWD.faces?refer=https://t.jrj.com/stock/login.jspa">忘记密码</a> </div>',
				 '        </form>',
				 '    </div>',
				 '    <div class="jrj-ld-right">',
				 '        <div class="jrj-ld-reg"><span>还没有金融界帐号？</span><a target="_top" href="'+this.registerUrl+this.registerReturnUrl+'">5秒注册</a></div>',
				 //<a href="javascript:;" class="renren"><i class="logo-icon"></i>用人人帐号登录</a>
				 '        <div class="jrj-ld-othor"> <a href="http://sso.jrj.com.cn/sina/sinalogin'+ (this.sinaReturnUrl ? "?continueUrl=" + this.sinaReturnUrl : "") +'" class="sina"><i class="logo-icon"></i>用新浪微博帐号登录</a>',
				 //        <a href="http://sso.jrj.com.cn/tencent/t/login.action'+ (this.qqReturnUrl ? "?continueUrl=" + this.qqReturnUrl : "") +'" class="qq"><i class="logo-icon"></i>用腾讯微博帐号登录</a> ',
				 '        </div>',
				 '    </div>',
				 '</div>',
				 '</div>'].join("");
						
			this.dialogDiv = $( dialogHtml );
			this.dialogDiv.appendTo( $("body") );
			
			this.form = this.dialogDiv.find("form");
			this.nameInput = this.form.find("input[name=LoginID]");			
			this.pwdInput = this.form.find("input[name=Passwd]");			
			this.loginBtn = this.dialogDiv.find(".jrj-ld-btn");			
			this.closeBtn = this.dialogDiv.find(".jrj-ld-close");
			this.clearBtn = this.dialogDiv.find(".jrj-ld-clear");
			this.alertDiv = this.dialogDiv.find(".jrj-ld-alert");
			this.autoCheckbox = this.dialogDiv.find("#ld-auto-login");
			this.autoExp = this.dialogDiv.find(".auto-exp");
			
			this.alertDiv.css("visibility", "hidden");
			this._bindEvent();
		},
		_bindEvent: function(){
			var self = this;
			self.nameInput.bind("input", function(event){
                self._toggleClear();
            });
            self.nameInput.bind("propertychange", function(event){
                self._toggleClear();
            });
			self.nameInput.bind("focus", function(event){
				$(this).addClass("jrj-ld-text-focus");
                self._toggleClear();
            });
            self.nameInput.bind("blur", function(event){
				$(this).removeClass("jrj-ld-text-focus");
                self._toggleClear();

	            self._hideAllPrompt();
            });
			
            self.pwdInput.bind("keyup", function(event){
                if(event.keyCode == 13){
                   self._submit();
                }
            });
			self.pwdInput.bind("focus", function(event){
                $(this).addClass("jrj-ld-text-focus");
            });
            self.pwdInput.bind("blur", function(event){
                $(this).removeClass("jrj-ld-text-focus");
                self._hideAllPrompt();
            });
			
            self.loginBtn.bind("click", function(event){
                self._submit();
                return false;
            });
			if(self.closeable){
	            self.closeBtn.bind("click", function(event){
	                self.close();
	                if(self.closeTarget){
	                	window.location=self.closeTarget;
	                }
	                return false;
	            });
			}else{
				self.closeBtn.hide();
			}
			self.clearBtn.bind("click", function(event){
                self.nameInput.val("");
				self.clearBtn.hide();
				self._checkForm();
            });

            self.autoCheckbox.bind("mouseenter", function(){
				self.autoExp.show();
			})
			self.autoCheckbox.bind("mouseleave", function(){
                self.autoExp.fadeOut("fast");
            })
			self.autoCheckbox.bind("click", function(){
                self.autoExp.hide();
            })
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
			this._init();
			this._setPosition();
			this.mask.show();
			this.dialogDiv.show();
			JRJ.ui.LoginDialog.focusInstance = this;
			this.nameInput.focus();
		},
		/**
		 * 关闭登录对话框
		 */
		close:function(){
			this.dialogDiv.remove();
			this.mask.close();
			JRJ.ui.LoginDialog.focusInstance = null;
		},
		_toggleClear: function(){
			var self = this;
			if(self.nameInput.val()!==""){
                self.clearBtn.show();
            }else{
                self.clearBtn.hide();
            }
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
			if( !this._checkForm() ) return false;
			if(this.callback){
                var name = this.nameInput.val();
                var pwd = this.pwdInput.val();
				var isAutoLogin = this.autoCheckbox[0].checked;
                var loginUrl = "http://sso.jrj.com.cn/sso/json/userSession.jsp?LoginID=" + name + "&Passwd=" + _hex_md5_(pwd.toLowerCase()) +"&p=JRJ_USER_INFO"+"&spid="+this.spid + (isAutoLogin ? "&isPersist=1" :"");
                $.getScript(loginUrl,function(){
                    var obj = JRJ.ui.LoginDialog.focusInstance;
                    if(!obj) return ;
                    if(!JRJ_USER_INFO){
                        return ;
                    }else{
                        switch(JRJ_USER_INFO.state){
                            case "0":                             
                              obj.close();
                              if($.isFunction(obj.callback)){
                                  obj.callback(JRJ_USER_INFO);
                                }
                              break;
                            case "1":
                              obj._showPrompt("您的用户名或密码错误");
                              break;
                            case "-1":
                              obj._showPrompt("您的用户名或密码存在非法参数");
                              break;
                            case "5":
                              obj._showPrompt("服务器内部错误，请稍后重试");
                              break;
                        }
                    }
                });
            }else{
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
			this.alertDiv.find("span").html(msg);
			this.alertDiv.css("visibility", "");
			if(input){	
                input.focus();
				input.addClass("jrj-ld-text-error");
			}
		},
		/**
         * @private
         */
        _hidePrompt : function(input){
            this.alertDiv.css("visibility", "hidden");
			if(input){
                input.removeClass("jrj-ld-text-error");
            }
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
			var top = (($(window).height() / 2) - (this.dialogDiv.outerHeight() / 2))-50;
			var left = (($(window).width() / 2) - (this.dialogDiv.outerWidth() / 2));
			if( top < 0 ) top = 0;
			if( left < 0 ) left = 0;			
			// IE6 fix
			if( $.browser.msie && parseInt($.browser.version) <= 6 ) top = top + $(window).scrollTop();			
//			this.dialogDiv.css({
//				top: top + 'px',
//				left: left + 'px'
//			});
			var winxx = $(window).width();
			var winyy = $(window).height();
			var fc_h = $('.jrj-ld').height();
//			$(".bgtcbig").css({ width: winxx, height: winyy });
			$('.jrj-ld').css({'margin-top':-fc_h/2});
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
	JRJ.ui.LoginDialog.focusInstance = null;
	window.JRJLoginDialog = JRJ.ui.LoginDialog;	
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


