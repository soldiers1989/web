if (typeof JRJ == "undefined" || !JRJ) { var JRJ = {} } if (typeof JRJ.ui == "undefined" || !JRJ.ui) { JRJ.ui = {} } (function (a) { JRJ.ui.Mask = function () { this.maskOpacity = 0.1; this.maskColor = "#000"; this.mask = null }; JRJ.ui.Mask.prototype = { show: function (c) { var b = { maskOpacity: this.maskOpacity, maskColor: this.maskColor }; a.extend(b, c); this.mask = a('<div class="jrj-dialog-mask"></div>'); this.mask.appendTo("BODY"); this.mask.css({ position: "absolute", zIndex: 9000, top: "0px", left: "0px", width: a(window).width(), height: a(document).height(), background: b.maskColor, opacity: b.maskOpacity }); if (navigator.appVersion.indexOf("MSIE 6.0") != -1) { this.mask.bgiframe() } this.winResizeHandler = function (d) { var e = d.data.obj; e._resize() }; a(window).bind("resize", { obj: this }, this.winResizeHandler) }, close: function () { this.mask.remove(); a(window).unbind("resize", this.winResizeHandler) }, _resize: function () { this.mask.css({ width: a(window).width(), height: a(document).height() }) } } })(jQuery);
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
/**
 * @fileOverview JRJ登录对话框
 * @copyright (c) 2009 (jrj.com)
 * @author jianjun.wang@jrj.com.cn
 * @version 1.0.1
 * @depend jquery.js mask.js
 */
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
	 * @param {Object} styleObj 补充样式对象
	 */
	JRJ.ui.LoginDialog = function(spid,lhref,isVerifyCode,returnUrl,registerReturnUrl,isAddTicket,formTarget,callback,styleObj,loginTarget){
		 this.spid = spid;
		 this.locationHref = lhref;//当前页地址，心远说目前sso已不用此参数
		 this.isVerifyCode = isVerifyCode;//图片验证码，应为false
		 this.returnUrl = returnUrl; //SSO返回认证结果时需要重定向到的URL
		 this.registerUrl = "http://i.jrj.com.cn/register.faces?slap=1&ReturnURL=";
		 this.forgetUrl="http://i.jrj.com.cn/retrievePWD.faces";
		 this.registerReturnUrl = registerReturnUrl;//SSO注册成功重定向到的URL
		 this.formTarget = "_self";
		 if( typeof(formTarget) != "undefined" ){
		 		this.formTarget = formTarget;	
		 }
		 this.callback = null;
		 if( typeof(callback) != "undefined" ){
		 		this.callback = callback;	
		 }
		 this.mask = null;
		 this.styleObj = styleObj?styleObj:{};
		 this.nameNullAlert = "用户名不能为空";
		 this.pwdNullAlert = "密码不能为空";

		 this.loginTarget = "_self";
		 if (typeof (loginTarget) != "undefined") {
		     this.loginTarget = loginTarget;
		 }
	}
	JRJ.ui.LoginDialog.prototype = {
		host:"",
		/**
		 * @private
		 */
		_init:function(){
			$(".win-lgn").remove();//target="blank",且页面存在多个对话框实例时，登录时hide对话框有问题
//			this.mask = new JRJ.ui.Mask();			
			
			var  dialogHtml = ['<div class="bgtcbig"></div>',
				 '<div class="fc_jrj_login"><p class="clear_btn_jrj pngfixer"></p><div class="box pngfixer"><div class="bor">',
				 '<strong class="head_login"><b class="logo_icon"></b></strong>',
				 '<form id="lgnform" action="http://sso.jrj.com.cn/sso/ssologin" method="post" target="_self">',
				 '            <input value="'+this.isVerifyCode+'" name="isVerifyCode" type="hidden" />',
				 '            <input value="'+this.spid+'" name="SPID" type="hidden" />',
				 '            <input value="'+this.locationHref+'" name="locationHref" type="hidden" />',
				 '            <input value="'+this.returnUrl+'" name="ReturnURL" type="hidden" />',
				 '			<input type="hidden" value="'+this.isAddTicket+'" name="isAddTicket"/>',
				 '			<input type="hidden" name="charset" value="UTF-8">',
				 '<div class="from-main-tc sign_in_three" >',
				 '			<div class="form-s1">',
				 '			<p><i>账　户：</i><span class="input_text_one"><input tabindex="1" id="usernameIpt" name="LoginID" type="text" value="" /></span><i class="cw name-tip-i" >&nbsp;</i></p>',
				 '			<p><i>密　码：</i><span class="input_text_one"><input tabindex="2" name="Passwd" type="password" value="" /></span><span class="rembx clearfix">',
				 '          <a href="'+this.forgetUrl+'" class="wjmm">忘记密码</a></span><i class="cw pwd-tip-i">&nbsp;</i></p>',
				 '			<p class="btn_fomr_end"><input type="button" id="jrj-lgn-btn" value="登　录" />',
				 '			<span class="clearfix"><a href="'+this.registerUrl+encodeURIComponent(this.registerReturnUrl)+'">5秒快速注册</a>还没有账号？</span></p>',
				 '			</div><div class="signin_third"><p><span>Copyright © 2014 JRJ.COM All Rights Reserved.</span></p></div>',
				 '     </div>',
				 '        </form>',
				 '    </div>',
				 ' </div> ',
				 '   <div class="ftc pngfixer"></div>',
				 '</div>'].join("");
			
				
			
			this.dialogDiv = $( dialogHtml );
			this.dialogDiv.appendTo( $("body") );
			
			this.form = this.dialogDiv.find("#lgnform");
			this.nameInput = this.form.find("input[name=LoginID]");			
			this.pwdInput = this.form.find("input[name=Passwd]");			
			this.loginBtn = this.dialogDiv.find("#jrj-lgn-btn");			
			this.autoCheckbox = this.dialogDiv.find("#ld-auto-login");
			
			this.tipDiv=this.dialogDiv.find(".err_tip");
			this.J_lgn_tip=this.dialogDiv.find("#j_lgn_tip");
//			this.J_close=this.dialogDiv.find("#W_close");
			
			this._bindEvent();
		},
		_bindEvent: function(){
			var self = this;
			self.nameInput.bind("input", function(event){
            });
            self.nameInput.bind("propertychange", function(event){
            });
			self.nameInput.bind("focus", function(event){
				$(this).parent('.input_text_one').addClass('ito_fous')
				//$(this).addClass("txt_focus");
            });
            self.nameInput.bind("blur", function(event){
            	$('.input_text_one').removeClass('ito_fous')
	            self._hideAllPrompt();
            });
			
            self.pwdInput.bind("keyup", function(event){
                if(event.keyCode == 13){
                   self._submit();
                }
            });
			self.pwdInput.bind("focus", function(event){
				$(this).parent('.input_text_one').addClass('ito_fous')
            });
            self.pwdInput.bind("blur", function(event){
            	$('.input_text_one').removeClass('ito_fous')
                self._hideAllPrompt();
            });
			
            self.loginBtn.bind("click", function(event){
            	$(this).attr("disabled",true);
                self._submit();
                $(this).attr("disabled",false);
                return false;
            });
			
//            self.closeBtn.bind("click", function(event){
//                self.close();
//                return false;
//            });
//            self.J_close.bind("click",function(event){
//            	self.J_lgn_tip.remove();
//            	return false;
//            });
			self.autoCheckbox.bind("click", function(){
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
//			this.mask.show(this.styleObj);
			this.dialogDiv.show();
			JRJ.ui.LoginDialog.focusInstance = this;
			this.nameInput.focus();
			 $('#usernameIpt').watermark('用户名');
			 $('input[name=Password]').watermark('请输入登录密码');
		},
		/**
		 * 关闭登录对话框
		 */
		close:function(){
			this.dialogDiv.remove();
//			this.mask.close();
			JRJ.ui.LoginDialog.focusInstance = null;
		},
		/**
		 * @private
		 */
		_checkForm:function(){
			var self = this, name = self.nameInput.val();
			self._hideAllPrompt();
            if(name==""){
                self._showPrompt(self.nameNullAlert, self.nameInput,".name-tip-i");
				return false;
            }
			var pwd = self.pwdInput.val();
            if(pwd==""){
                self._showPrompt(self.pwdNullAlert, self.pwdInput,".pwd-tip-i");
				return false;
            }
            
			return true;
		},
		_submit: function(){
			if( !this._checkForm() ) return false;
              this.dialogDiv.hide();  //本应是remove，但remove后又找不到#jrj_ld_form
//              this.mask.close();
              this.form.submit();
              this.dialogDiv.remove();
            return true;
		},
		/**
		 * @private
		 */
		_showPrompt : function(msg, input,clazz){
			$(clazz).html(msg);
			if(input){	
                input.focus();
			}
		},
		_showAllPrompt:function(msg){
//			this.tipDiv.find(".tip_false").html(msg);
		},
		/**
         * @private
         */
        _hidePrompt : function(input,clazz){
        	$(clazz).html("&nbsp;");
        },
		_hideAllPrompt: function(){
	        var self = this;
	        self._hidePrompt(self.nameInput,".name-tip-i");
	        self._hidePrompt(self.pwdInput,".pwd-tip-i");
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
			var fc_h = $('.fc_jrj_login').height();
			$('.bgtcbig').css({'width':winxx,'height':winyy,})
			$('.fc_jrj_login').css({'margin-top':-fc_h/2})
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

/*
	Watermark v3.1.3 (March 22, 2011) plugin for jQuery
	http://jquery-watermark.googlecode.com/
	Copyright (c) 2009-2011 Todd Northrop
	http://www.speednet.biz/
	Dual licensed under the MIT or GPL Version 2 licenses.
*/
(function(a,h,y){var w="function",v="password",j="maxLength",n="type",b="",c=true,u="placeholder",i=false,t="watermark",g=t,f="watermarkClass",q="watermarkFocus",l="watermarkSubmit",o="watermarkMaxLength",e="watermarkPassword",d="watermarkText",k=/\r/g,s="input:data("+g+"),textarea:data("+g+")",m="input:text,input:password,input[type=search],input:not([type]),textarea",p=["Page_ClientValidate"],r=i,x=u in document.createElement("input");a.watermark=a.watermark||{version:"3.1.3",runOnce:c,options:{className:t,useNative:c,hideBeforeUnload:c},hide:function(b){a(b).filter(s).each(function(){a.watermark._hide(a(this))})},_hide:function(a,r){var p=a[0],q=(p.value||b).replace(k,b),l=a.data(d)||b,m=a.data(o)||0,i=a.data(f);if(l.length&&q==l){p.value=b;if(a.data(e))if((a.attr(n)||b)==="text"){var g=a.data(e)||[],c=a.parent()||[];if(g.length&&c.length){c[0].removeChild(a[0]);c[0].appendChild(g[0]);a=g}}if(m){a.attr(j,m);a.removeData(o)}if(r){a.attr("autocomplete","off");h.setTimeout(function(){a.select()},1)}}i&&a.removeClass(i)},show:function(b){a(b).filter(s).each(function(){a.watermark._show(a(this))})},_show:function(g){var p=g[0],u=(p.value||b).replace(k,b),h=g.data(d)||b,s=g.attr(n)||b,t=g.data(f);if((u.length==0||u==h)&&!g.data(q)){r=c;if(g.data(e))if(s===v){var m=g.data(e)||[],l=g.parent()||[];if(m.length&&l.length){l[0].removeChild(g[0]);l[0].appendChild(m[0]);g=m;g.attr(j,h.length);p=g[0]}}if(s==="text"||s==="search"){var i=g.attr(j)||0;if(i>0&&h.length>i){g.data(o,i);g.attr(j,h.length)}}t&&g.addClass(t);p.value=h}else a.watermark._hide(g)},hideAll:function(){if(r){a.watermark.hide(m);r=i}},showAll:function(){a.watermark.show(m)}};a.fn.watermark=a.fn.watermark||function(p,o){var t="string";if(!this.length)return this;var s=i,r=typeof p===t;if(r)p=p.replace(k,b);if(typeof o==="object"){s=typeof o.className===t;o=a.extend({},a.watermark.options,o)}else if(typeof o===t){s=c;o=a.extend({},a.watermark.options,{className:o})}else o=a.watermark.options;if(typeof o.useNative!==w)o.useNative=o.useNative?function(){return c}:function(){return i};return this.each(function(){var B="dragleave",A="dragenter",z=this,i=a(z);if(!i.is(m))return;if(i.data(g)){if(r||s){a.watermark._hide(i);r&&i.data(d,p);s&&i.data(f,o.className)}}else{if(x&&o.useNative.call(z,i)&&(i.attr("tagName")||b)!=="TEXTAREA"){r&&i.attr(u,p);return}i.data(d,r?p:b);i.data(f,o.className);i.data(g,1);if((i.attr(n)||b)===v){var C=i.wrap("<span>").parent(),t=a(C.html().replace(/type=["']?password["']?/i,'type="text"'));t.data(d,i.data(d));t.data(f,i.data(f));t.data(g,1);t.attr(j,p.length);t.focus(function(){a.watermark._hide(t,c)}).bind(A,function(){a.watermark._hide(t)}).bind("dragend",function(){h.setTimeout(function(){t.blur()},1)});i.blur(function(){a.watermark._show(i)}).bind(B,function(){a.watermark._show(i)});t.data(e,i);i.data(e,t)}else i.focus(function(){i.data(q,1);a.watermark._hide(i,c)}).blur(function(){i.data(q,0);a.watermark._show(i)}).bind(A,function(){a.watermark._hide(i)}).bind(B,function(){a.watermark._show(i)}).bind("dragend",function(){h.setTimeout(function(){a.watermark._show(i)},1)}).bind("drop",function(e){var c=i[0],a=e.originalEvent.dataTransfer.getData("Text");if((c.value||b).replace(k,b).replace(a,b)===i.data(d))c.value=a;i.focus()});if(z.form){var w=z.form,y=a(w);if(!y.data(l)){y.submit(a.watermark.hideAll);if(w.submit){y.data(l,w.submit);w.submit=function(c,b){return function(){var d=b.data(l);a.watermark.hideAll();if(d.apply)d.apply(c,Array.prototype.slice.call(arguments));else d()}}(w,y)}else{y.data(l,1);w.submit=function(b){return function(){a.watermark.hideAll();delete b.submit;b.submit()}}(w)}}}}a.watermark._show(i)})};if(a.watermark.runOnce){a.watermark.runOnce=i;a.extend(a.expr[":"],{data:function(c,d,b){return!!a.data(c,b[3])}});(function(c){a.fn.val=function(){var e=this;if(!e.length)return arguments.length?e:y;if(!arguments.length)if(e.data(g)){var f=(e[0].value||b).replace(k,b);return f===(e.data(d)||b)?b:f}else return c.apply(e,arguments);else{c.apply(e,arguments);a.watermark.show(e);return e}}})(a.fn.val);p.length&&a(function(){for(var b,c,d=p.length-1;d>=0;d--){b=p[d];c=h[b];if(typeof c===w)h[b]=function(b){return function(){a.watermark.hideAll();return b.apply(null,Array.prototype.slice.call(arguments))}}(c)}});a(h).bind("beforeunload",function(){a.watermark.options.hideBeforeUnload&&a.watermark.hideAll()})}})(jQuery,window);