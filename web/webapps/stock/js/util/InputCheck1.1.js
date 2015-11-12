/*
自定义输入框检查类
	author:yuan.cheng
	date:2013-6-4
	version:1.1
e.g
<script>
$(document).ready(function(e) {
	//参数：要检查的input的id，配置
    input=new XJB.InputCheck('test',{
		error:'test-err',//显示错误信息的容器id，默认为target+'-err',此处可不设置
		trigger:'blur';//触发事件（默认）
		required:true;//是否必填（默认）
		blankmsg:'不能为空！';//为空时的提示信息（默认）
		defaultTemplate:'<b>{msg}</b>',//错误信息显示模板，{msg}会被替换为错误提示消息
		validators:[{//检查器组，可以定义多个检查器
			validator:function(v, me){//定义检查规则，参数为输入框的值
				me.showMSG('123');
				return v>10;
			},
			msg:'请输入大于10的数字'//错误提示信息
		}]
	});
});
function check(){
	alert(input.check());//也可外部调用
}
</script>
</head>

<body>
<input id="test" type="text" />
<span id="test-err" style="display:none;"></span>
<input type="button" value="提交" onclick="check();"/>
</body>

*/
(function(window){
	var InputCheck=function(target,config){
		if($('#'+target).length==0 || !$('#'+target).is('input'))return;
		this.trigger='blur';
		this.required=true;
		this.blankmsg='不能为空！';
		this.error=target+'-err';
		this.pass=target+'-pass';
		this.loading=target+'-loading';
		this.errorIcon=target+'-err-icon';
		this.passIcon=target+'-pass-icon';
		this.errorClass='txtbox-error';
		$.extend(this,config);
		this.target=$('#'+target);
		this.error=$('#'+this.error);
		this.pass=$('#'+this.pass);
		this.loading=$('#'+this.loading);
		this.errorIcon=$('#'+this.errorIcon);
		this.passIcon=$('#'+this.passIcon);
		if(null == this.validators){
			this.validators=new Array();
		}
		if(this.required){
			this.validators.splice(0,0,{validator:'blank',msg:this.blankmsg,template:this.defaultTemplate});
		}
		this.bindEvent();
	};
	InputCheck.prototype={
		bindEvent:function(){
			var self=this;
			this.target.bind(this.trigger,function(e){
				self.check();
			});
		},
		check:function(){
			if(this.target.length==0 || !this.target.is('input'))return true;
			var value=$.trim(this.target.val());
			var re=true;
			var self=this;
			var ajaxValidator=null;
			$.each(this.validators,function(i,v){
				if('ajax'==v.type && null!=v.url){
					ajaxValidator=v;
				}else if(($.isFunction(v.validator) && !v.validator(value,self.target,self))||
				($.isFunction(window.$inputcheck.utils._validators[v.validator]) && 
				!window.$inputcheck.utils._validators[v.validator](value))){
					self.showMSG(v.msg,v.template);
					re=false;
					return false;
				}
			});
			if(re && null!=ajaxValidator){
				this.showLoading();
				var parameters={};
					parameters[ajaxValidator.parameterName]=value;
					$.getJson(ajaxValidator.url,parameters,function(data){
						if($.isFunction(ajaxValidator.validator)){
							if(!ajaxValidator.validator(data)){
								self.showMSG(ajaxValidator.msg,ajaxValidator.template);
							}else{
								self.showPass();
							}
						}
					});
			}else if(re){
				this.showPass();
			}
			return re;
		},
		showMSG:function(m, t){
			this.pass.hide();
			this.passIcon.hide();
			this.loading.hide();
			this.error.html(this.message(m,t)).show();
			this.errorIcon.show();
			if(null!=this.errorClass){
				this.target.addClass(this.errorClass);
			}
		},
		showPass:function(){
			this.error.hide();
			this.errorIcon.hide();
			this.loading.hide();
			this.pass.show();
			this.passIcon.show();
			if(null!=this.errorClass){
				this.target.removeClass(this.errorClass);
			}
		},
		showLoading:function(){
			this.error.hide();
			this.errorIcon.hide();
			this.pass.hide();
			this.passIcon.hide();
			this.loading.show();
			if(null!=this.errorClass){
				this.target.removeClass(this.errorClass);
			}
		},
		message:function(m, t){
			if(null == m){
				m='错误信息未定义';
			}
			if(null != t){
				return t.replace('{msg}',m);
			}else if(null != this.defaultTemplate){
				return this.defaultTemplate.replace('{msg}',m);
			}else{
				return m;
			}
		}
	};
	var utils={
		_validators:{
			blank:function(v){
				return ''!=v;
			},
			email:function(v){
				var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
				return reg.test(v);
			},
			mobile:function(v){
				var reg = /^1[3-8]{1}\d{9}$/g;
				return reg.test(v);
			},
			phone:function(v){
				var reg = /^(\d{3,4}-?)?\d{7,8}([\-转]\d{3,5})?$/g;
				return reg.test(v);
			},
			number:function(v){
				return this.integer(v)||this.float(v);
			},
			integer:function(v){
				var reg = /^-?(0|[1-9]\d*)$/;
				return reg.test(v);
			},
			float:function(v){
				var reg=/^-?(0|[1-9]\d*)\.\d+$/;
				return reg.test(v);
			},
			url:function(v){
				var strRegex = "^((https|http|ftp|rtsp|mms)?://)" 
					+ "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@ 
					+ "(([0-9]{1,3}.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184 
					+ "|" // 允许IP和DOMAIN（域名）
					+ "([0-9a-z_!~*'()-]+.)*" // 域名- www. 
					+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]." // 二级域名 
					+ "[a-z]{2,6})" // first level domain- .com or .museum 
					+ "(:[0-9]{1,4})?" // 端口- :80 
					+ "((/?)|" // a slash isn't required if there is no file name 
					+ "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"; 
				var re=new RegExp(strRegex); 
				return re.test(v);
			},
			ID:function(v){
				return IdCardValidate(v);
			}
		}
	};
	var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ];// 加权因子   
	var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ];// 身份证验证位值.10代表X   

	function IdCardValidate(idCard) {   
	    idCard = trim(idCard.replace(/ /g, ""));   
	    if (idCard.length == 15) {   
	        return isValidityBrithBy15IdCard(idCard);   
	    } else if (idCard.length == 18) {   
	        var a_idCard = idCard.split("");// 得到身份证数组   
	        if(isValidityBrithBy18IdCard(idCard)&&isTrueValidateCodeBy18IdCard(a_idCard)){   
	            return true;   
	        }else {   
	            return false;   
	        }   
	    } else {   
	        return false;   
	    }   
	}   
	/**  
	 * 判断身份证号码为18位时最后的验证位是否正确  
	 * @param a_idCard 身份证号码数组  
	 * @return  
	 */  
	function isTrueValidateCodeBy18IdCard(a_idCard) {   
	    var sum = 0; // 声明加权求和变量   
	    if (a_idCard[17].toLowerCase() == 'x') {   
	        a_idCard[17] = 10;// 将最后位为x的验证码替换为10方便后续操作   
	    }   
	    for ( var i = 0; i < 17; i++) {   
	        sum += Wi[i] * a_idCard[i];// 加权求和   
	    }   
	    valCodePosition = sum % 11;// 得到验证码所位置   
	    if (a_idCard[17] == ValideCode[valCodePosition]) {   
	        return true;   
	    } else {   
	        return false;   
	    }   
	}   
	/**  
	 * 通过身份证判断是男是女  
	 * @param idCard 15/18位身份证号码   
	 * @return 'female'-女、'male'-男  
	 */  
	function maleOrFemalByIdCard(idCard){   
	    idCard = trim(idCard.replace(/ /g, ""));// 对身份证号码做处理。包括字符间有空格。   
	    if(idCard.length==15){   
	        if(idCard.substring(14,15)%2==0){   
	            return 'female';   
	        }else{   
	            return 'male';   
	        }   
	    }else if(idCard.length ==18){   
	        if(idCard.substring(14,17)%2==0){   
	            return 'female';   
	        }else{   
	            return 'male';   
	        }   
	    }else{   
	        return null;   
	    }   
	}   
	 /**  
	  * 验证18位数身份证号码中的生日是否是有效生日  
	  * @param idCard 18位书身份证字符串  
	  * @return  
	  */  
	function isValidityBrithBy18IdCard(idCard18){   
	    var year =  idCard18.substring(6,10);   
	    var month = idCard18.substring(10,12);   
	    var day = idCard18.substring(12,14);   
	    var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
	    // 这里用getFullYear()获取年份，避免千年虫问题   
	    if(temp_date.getFullYear()!=parseFloat(year)   
	          ||temp_date.getMonth()!=parseFloat(month)-1   
	          ||temp_date.getDate()!=parseFloat(day)){   
	            return false;   
	    }else{   
	        return true;   
	    }   
	}   
	  /**  
	   * 验证15位数身份证号码中的生日是否是有效生日  
	   * @param idCard15 15位书身份证字符串  
	   * @return  
	   */  
	  function isValidityBrithBy15IdCard(idCard15){   
	      var year =  idCard15.substring(6,8);   
	      var month = idCard15.substring(8,10);   
	      var day = idCard15.substring(10,12);   
	      var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
	      // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法   
	      if(temp_date.getYear()!=parseFloat(year)   
	              ||temp_date.getMonth()!=parseFloat(month)-1   
	              ||temp_date.getDate()!=parseFloat(day)){   
	                return false;   
	        }else{   
	            return true;   
	        }   
	  }   
	//去掉字符串头尾空格   
	function trim(str) {   
	    return str.replace(/(^\s*)|(\s*$)/g, "");   
	} 
	window.$inputcheck={};
	window.$inputcheck.utils=utils;
	window.InputCheck=InputCheck;
})( window );
$.fn.extend({
	InputCheck:function(config){
		this.each(function(i,e){
			$(e).data('_checker',new InputCheck($(e).attr('id'),config));
		});
		return this;
	},
	check:function(shortCut){
		var re=true;
		this.each(function(i,e){
			if(!$(e).data('_checker').check()){
				re=false;
				if(shortCut){
					return false;
				}
			}
		});
		return re;
	}
});
if (typeof XJB == "undefined" || !XJB) {
	var XJB = {};
}
XJB.InputCheck=window.InputCheck;