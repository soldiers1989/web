/**
 * 
 * @fileOverview JRJ证券通交易密码框
 * @copyright (c) 2014 (jrj.com)
 * @author yuanlong.wang
 * @version 1.0.0
 * @depend jquery.js jquery.cookie.js
 */
if (typeof JRJ == "undefined" || !JRJ) { var JRJ = {} } if (typeof JRJ.ui == "undefined" || !JRJ.ui) { JRJ.ui = {} }
var browser = (function(){	var ua = window.navigator.userAgent;
	// 浏览器类型检测
	var opera = typeof(window.opera)=="object";
	var ie = !opera && ua.indexOf("MSIE")>0;
	// 浏览器版本检测
	var re,version;	 if( ie ) {	re = /MSIE( )(\d+(\.\d+)?)/;	}		
	if( "undefined" != typeof( re ) && re.test( ua ) )
	  version = parseFloat(RegExp.$2);
	return{	ie: ie,version: version}
})();
function get_time() {
	return new Date().getTime();
}
function getQueryString(source,name) {var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = source.match(reg);if (r != null) return unescape(r[2]); return null;}
function randomString(len) {
	　　len = len || 32;var $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890';    
	　　var maxPos = $chars.length;	　　var rd = '';　for (i = 0; i < len; i++) {
	　　　　rd += $chars.charAt(Math.floor(Math.random() * maxPos));}	　　return rd;}
(function($){
	/**
	 * 密码输入框控件
	 * 
	 * @class JRJ密码输入框控件
	 * @param {string}
	 *            selector 页面选择器 最终填充的div
	 * @param {long}
	 *            accountId 账户ID
	 * @param {int}
	 *            action 操作行为 交易/撤单/银转证/证转银
	 * @param {array}
	 *            variables 变量名(数组)
	 * @param {int}
	 *            bankNo 银行代码
	 * @param {int}
	 *            width 样式宽度
	 * @param {int}
	 *            height 样式高度
	 */
	JRJ.ui.ZQTPassword=function(selector,accountId,action,bankNo,width,height,initCallback){
		this.selector=selector;
		this.accountId=accountId;
		this.action=action;
		this.width=(width==null||width==undefined)?200:width;
		this.height=(height==null||height==undefined)?47:height;
		this.bankNo=bankNo;
		this.values=[];
		this._mac="";
		this.Ifm_lastmessage=null;
		if(initCallback!=undefined){
			this.initCallback=initCallback;
		}else{
			this.initCallback=function(){};
		}
		this.init();
	};
	JRJ.ui.ZQTPassword.Action={
			INIT:0, // 初始化
			TRADE:1,// 交易
			CANCEL:2,// 撤单
			YTOZ:3,// 银转证
			ZTOY:4,// 证转银
			QUERY:5,// 银行余额查询
			BIND:6
			
	};
	JRJ.ui.ZQTPassword.prototype={
			id:"",
			url:"/stock/#1/password/view.jspa",
			url2:"/stock/password/#1/view.jspa",
			type:null,
			html:"",
			iframe_src:"",
			iter:null,
			rand_num:"",
			_disk:"",
			_nic:"",
			var_rdm:"",
			init:function(){
				var _url=this.url.replace("#1", this.accountId);
				if(this.action==JRJ.ui.ZQTPassword.Action.BIND){
					_url=this.url2.replace("#1", this.accountId);
				}
				var _this=this;
				$.ajax({
						type : "POST",
						url : _url,
						data:{
							"action":_this.action,
							"bankNo" :_this.bankNo
						},
						dataType : "text",
						timeout : 5000,
						cache : false,
						success:function(data){
							data=eval("("+data+")");
							  if(data&&data.status==0){
								  _this.type=data.type;
								  if(_this.type==1){
									  _this.iframe_src=data.iframe_src;
								  }
								  _this.var_rdm=data.random;
								  _this.html=unescape(data.data);
								  _this.show();
								  _this.initCallback(data);
							  }else if(data&&data.status==1){
								  _this.type=data.type;
								  $(_this.selector).parents("tr").find("td:first-child").empty();
							  }else{
								  JRJ.Alerts.alert({
										title: "消息提示",  //标题
										width:400,
										message: "服务器忙"     //提示语
									  });
							  }	
							
						 },
						 error:function(e){
							 
						 }
				});
			},
			setCommonData:function(activex){
				if(activex!=undefined){
				this._mac=activex.machineNetwork();
				this._disk=activex.machineDisk();
				this._nic= activex.machineCPU();
				}
			},
			randomCode:function(){
				var _this=this;
				$.ajax( {
					url : "/stock/password/prandnum.jspa?" + get_time(),
					type : "POST",
					async : false,
					success : function(srand_num) {
						_this.rand_num=srand_num;
					}
				});
			},
			_setPwd:function(pwdAx,pwdVar,emptyErr){
				var _this=this;
				if(pwdAx!=undefined){
					pwdAx.pwdSetSk(_this.rand_num);
					var password =pwdAx.pwdResult();
					_this.setCommonData(pwdAx);
					if (password ==undefined){
						_this.Ifm_callback(-99);//密码控件未安装
						return false;
					}else if(password==""){
						_this.values[pwdVar]="";
						_this.Ifm_callback(emptyErr);//通讯密码不能为空
						return false;
					}else{
						_this.values[pwdVar]=password;
						return true;
					}
				}else{
					_this.Ifm_callback(-99);//密码控件未安装
					return false;
				}
			},
			// TODO 检验
			check:function(callback){
				this.values=[];
				if(callback){
					this.Ifm_callback=callback;
				}
				var _this=this;
				if(this.type==1){
					this.id=randomString(20);
					var hash="id="+this.id+"&action=submit";
					$(this.selector+" .ZQT_password_iframe").attr("src",_this.iframe_src+"#"+hash);
					this.Ifm_back(this.id,callback);
				}else if(this.type==3){
					_this.Ifm_callback(0);//无密
				}else{
					this.randomCode();
					var AC=JRJ.ui.ZQTPassword.Action;
					switch(this.action){
					case AC.INIT:
					case AC.BIND:
						var txPassword=$(_this.selector+" .txPassword");
						if(txPassword!=undefined&&txPassword.data("password") ==1){
							var _txpwd=eval("(_zqttxpwd_"+_this.var_rdm+")");
							if(!_this._setPwd(_txpwd,"txPassword",-103)){//通讯密码不能为空
								return;
							}
						}
					case AC.TRADE:
					case AC.CANCEL:
						var _zqtpwd=eval("(_zqtpwd_"+_this.var_rdm+")");
						if(_this._setPwd(_zqtpwd,"password",-100)){//交易密码不能为空
							_this.Ifm_callback(0);
						}
						return;
					case AC.YTOZ:
					case AC.ZTOY:
					case AC.QUERY:
						var bankPassword = $(_this.selector+" .bankPassword");
						var fundPassword = $(_this.selector+" .fundPassword");
						var tradePassword = $(_this.selector+" .tradePassword");
						if(fundPassword.data("password") ==1){
							var _fundpwd=eval("(_fundpwd_"+_this.var_rdm+")");
							if(!_this._setPwd(_fundpwd,"fundPassword",-101)){//资金密码不能为空
								return;
							}
						}
						if(bankPassword.data("password") ==1){
							var _bankpwd=eval("(_bankpwd_"+_this.var_rdm+")");
							if(!_this._setPwd(_bankpwd,"bankPassword",-102)){//银行密码不能为空
								return;
							}
						}
						if(tradePassword.data("password") ==1){
							var _tradepwd=eval("(_tradepwd_"+_this.var_rdm+")");
							if(!_this._setPwd(_tradepwd,"tradePassword",-100)){//交易密码不能为空
								return;
							}
						}
						_this.Ifm_callback(0);
						break;
					}
				}
			},
			// TODO 获取最终结果
			value:function(variable){
				if(this.type==1){
					if(this.values.length>0)
					return this.values[0];
					return "";
				}else if(this.type==3){
					return "";
				}else{
						if(variable!=undefined){
							return this.values[variable]==undefined?"":this.values[variable];
						}else{
							if(this.values.length>0){
								return this.values[0];
							}else if(this.values["password"]!=undefined){
								return this.values["password"];
							}else{
								return "";
							}
						}
				}
			},
			mac:function(){
				return this._mac;
			},
			//TODO ======IFRAME相关开始=======================================================
			Ifm_callback:function(){},//-100 交易密码不能为空 -101 资金密码不能为空 -102 银行密码不能为空
			Ifm_data:{},
			Ifm_handlerData:function(data){
				if(data!=""){
					if(this.Ifm_lastmessage!=data){
						this.Ifm_lastmessage=data;
						//console.log(data);
						this.Ifm_data={};
						var para = data.split('&');
						for(i=0;i<para.length;i++) {
							 // 获取等号位置 
					        pos = para[i].indexOf('=');
					        if (pos == -1) {
					            continue;
					        }
					        // 获取name 和 value 
					        paraName = para[i].substring(0, pos);
					        paraValue = para[i].substring(pos + 1);					        
					        this.Ifm_data[paraName] = paraValue;
						}
						var authresult=this.Ifm_data["authresult"];
						if(authresult!=undefined){
							if("true"==authresult){
								this.values=[];
								var usertoken=this.Ifm_data["usertoken"];
								this.values[0]=usertoken;
								this._mac="00-00-00-00-00";
								this.Ifm_callback(0);
							}else{
								this.Ifm_callback(this.Ifm_data["errorno"],this.Ifm_data["errorinfo"]);//验证失败
							}
						}else{
							this.Ifm_callback(-1);//服务器忙
						}
					}
				}
			},
			Ifm_Event:null,
			add_Event:function(){
				if (window.addEventListener) {
					window.addEventListener('message',this.Ifm_Event , false);
				} else if (window.attachEvent) {
					window.attachEvent('onmessage', this.Ifm_Event);
				}
			},
			remove_Event:function(){
				if (window.removeEventListener) {
					window.removeEventListener('message', this.Ifm_Event,false);
				} else if (window.detachEvent) {
					window.detachEvent('onmessage', this.Ifm_Event);
				}
				this.Ifm_Event=null;
			},
			Ifm_back:function(id){
				var _this=this;
				try {
					if (window.postMessage) {
						this.Ifm_Event=function(e){
							_this.Ifm_handlerData(e.data + "");
							_this.remove_Event();
						};
						this.add_Event();
					} else {
						if(browser.ie&&browser.version<8){
							if(browser.version==6){
								clearInterval(this.iter);
								this.iter=setInterval(function () {
									var hash = window.frames["ZQT_password_iframe"].frames["xsiteframe"].location.hash.substr(1);
									_this.Ifm_handlerData(hash);
								}, 150);
							}else{
								clearInterval(this.iter);
								this.iter=setInterval(function () {
								 var arrStr = document.cookie.split("; ");
									for(var i = 0;i < arrStr.length;i ++){
										var temp = arrStr[i].split("=");
										if(temp[0] == "h_"+id) {
										   var exp = new Date(); 
										    exp.setTime(exp.getTime() - 1); 
										    document.cookie= temp[0] + "="+temp[1]+";expires="+exp.toGMTString(); 
											_this.Ifm_handlerData(unescape(temp[1]));
										}
								   }
								},150);
							}
						}else{
							clearInterval(this.iter);
							this.iter=setInterval(function () {
								var hash = window.location.hash.substr(1);
								_this.Ifm_handlerData(hash);
							}, 150);
						}
					}
				} catch (ey) {}
			},
			//TODO ======IFRAME相关结束=======================================================
			show:function(){
				$(this.selector).empty().html(this.html);
				if(this.type==1){
					$(this.selector+" .ZQT_password_iframe").width(this.width);
					$(this.selector+" .ZQT_password_iframe").height(this.height);
				}
			},
			destory:function(){
				var _this=this;
				clearInterval(this.iter);
				try{
				if (window.postMessage &&this.Ifm_Event!=null) {
					this.remove_Event();
				} }catch(xy){}
				$(this.selector).empty();
				this.values=[];
				
			}
	}
	window.JRJZQTPassword = JRJ.ui.ZQTPassword;
})(jQuery);