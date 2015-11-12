(function(){
	//1分钟加载一次
	var URL = {
			 msg : '/stock/messageNum.jspa'  //消息
			,exit : '/stock/logout.jspa' //?RetrunURL=XXX&action=logout  //退出
			,userLink : 'http://i.jrj.com.cn/main' //个人中心
		}
		,T = {
			 login : {}
			,reg : {}
			,itgtxt : ['我的投顾空间','申请投顾入驻']
			,init : function(){
				var  login = $('#loginAfter')
					,reg = $('#loginBefore')
					,alink
				
				T.login = {
					 elem : login
					,name : login.find('.userinfo .middle span')
					,exit : login.find('.userinfo .logout')
					,img : login.find('.userinfo img')
					,msg : login.find('.userop-msg')
					,msgNum : login.find('.msg-num')
					,msgList : login.find('.userop-msg .userop-msg-block li span') 
					,itg : login.find('.link-studio')
					,more : login.find('.userop-msg-block .more')
				}
				
				T.reg = {
					 elem : reg
					,login : reg.find('.userop a').eq(0)
					,reg : reg.find('.userop a').eq(1)
					,itg : reg.find('.userop .link-studio')
				}
				T.reg.reg.attr('href','https://sso.jrj.com.cn/sso/passportRegister/register.jsp?ReturnURL='+window.location.href)
			
				T.reg.login.removeAttr('href')
				T.login.img.parent().attr({'href':URL.userLink,'target':'_blanks'})
			
				if(T.login.msgNum.length > 0 ){ //个人空间页面
					T.login.msgList =  T.login.elem.find('.userop-msg .userop-msg-block li .notify')
					T.login.msgNum.show()
					T.login.msghover = T.login.elem.find('.userop-msg-block')
				}
				T.login.msgList.text('0')
			}
			,cookies : function(){
				var  pwd = $.cookie('JRJ.SSOPASSPORT')
					,md5 = $.cookie('zqt_md5')
					,userid = $.cookie('zqt_userid')
					,olduserid = Member.getUserInfo().userId;
				if( pwd && md5 && userid && olduserid && olduserid == userid ){
					return true;
				}else{
					return false;
				}
			}
			,ajax : function( fn , url , data , datatype , cntenttype , type ){
				datatype = datatype || "jsonp"
				cntenttype = cntenttype || "application/json"
				type = type || 'GET'
				$.ajax({
					type: type,
					url: url,
					async: false,
					contentType: cntenttype,
					dataType: datatype,
					data : data,
					success: function (_data) {
						fn(_data);
					},
					error: function (e) {
						//console.log(e.message);
					}
				});
			}
		}
		,M = { //msg处理
			dealData : function( _data ){
				
				var n1=0,n2=0,n3=0,n4=0;
				//_data = {'t_1':'1','t_2':'2','t_3':'3','t_4':'2','t_5':'4','t_6':'5','t_7':'6','t_8':'7','t_9':'8','t_10':'6','t_11':'0','t_12':'0'}
				
				for( var item in _data ){
					var value = parseInt(_data[item])
					
					if( item == 't_3' ||  item == 't_6' ||  item == 't_10' ||  item == 't_11' ){ //您收到N个新回答
						n1 += value
					}
					if( item == 't_13' ){ //投资组合有N个新操作
						n2 += value
					}
					if( item == 't_2' ){ //自选股有N个新提醒
						n3 += value
					}
					if( item == 't_1' ||  item == 't_5' ||  item == 't_8' ||  item == 't_9' ||  item == 't_12' ){ //您收到N条系统信息
						n4 += value
					}
				}
				n1 = n1>99?'99+':n1
				n2 = n2>99?'99+':n2
				n3 = n3>99?'99+':n3
				n4 = n4>99?'99+':n4
				return [n1,n2,n3,n4];
			}
		}
	,Member = {

		/**
		* 判断用户是否已经登录
		*/
		init: function () {
			T.init();
			Member.update();
			Member.loginBtn = T.reg.login
		}
		,update : function(){ //通过cookie 判断如果已退出刷新，如果 用户ID 不一样，则刷新
			Member.updateState(basicUserInfo);
			if(window.userMembershipCatch) return;  //用于本地测试，测试完后要删除
			var cookie = T.cookies();
			
			if(cookie === false){
				if(basicUserInfo && basicUserInfo.userId){
					location.reload();
				}else{
					Member.updateState(null);
				}
			}else{
				if(!basicUserInfo || !basicUserInfo.userId){
					location.reload();
				}else{
					Member.updateState(basicUserInfo);
				}
			}
		}
		,getUserInfo : function(){
			return basicUserInfo;
		}
		/**
		* @param userInfo
		*            { "userId": "140725010174211817", "sid": "9306", "cid": "2" } 登录后或者退出后更新页面显示状态
		*/
		,updateState: function (userInfo) {
			var  self = Member
				,_ele = T.login
				,login = _ele.elem
				,img = _ele.img
				,name = _ele.name
				,reg =  T.reg.elem
				,_text = ''
				,_href = ['http://itougu.jrj.com.cn/view/myPointlist.jspa?tabid=1','http://itougu.jrj.com.cn/account/applyAdviser.jspa'] //''0注册 1进入投顾空间
				,_src = '';
				
			T.login.elem = login;
			T.reg.elem = reg;
			
			self.userInfo = userInfo;

			if (self.isLogin(userInfo)) {
				_text = parseInt(userInfo.isAdviser) == 1? T.itgtxt[0] : T.itgtxt[1]
				_src = parseInt(userInfo.isAdviser) == 1? _href[0] : _href[1]
				
				name.text(userInfo.userName)
				img.attr('src',userInfo.headImage)
				_ele.itg.text(_text).attr('href',_src);
				
				reg.hide();
				login.show();
				
				self.setMsg(); //获取消息
				self.logout();
				
			} else {
				T.reg.itg.text(T.itgtxt[1]).attr('href',_href[1]);
				//console.log('out')
				login.hide();
				reg.show();
			}
		},
		/**
		* 退出系统
		*/
		logout: function (callback) {
			if(!T.form){
				T.form = true;
				var form = $('<form action='+URL.exit+' method="post">'
						+'<input type=text id="ReturnURL" name="ReturnURL" value='+window.location.href+'>'
						+'<input type=text id="action" name="action" value="logout"></form>')
				form.css({display:'none'})
				$('body').append(form);
				 T.login.exit.click(function(){
					form.submit()
				});
			}
		},
		isLogin: function (userInfo) {

			if (typeof (userInfo) != 'undefined' && userInfo != null && userInfo.userName && userInfo.userName != '' &&  userInfo.userName.length>1 ) {
				return true;
			}
			return false;
		}
		/**
		*更新信息提示
		**/
		,setMsg : function(){
			var link = [
				'http://itougu.jrj.com.cn/message/list/ask.jspa'                //回答
				,'http://itougu.jrj.com.cn/message/list/portfolio.jspa'         //操作
				,'http://itougu.jrj.com.cn/message/list/stockremind.jspa'       //提醒
				,'http://itougu.jrj.com.cn/message/list/sys.jspa'               //消息
			]                                                                  
			 //查看全部
			T.ajax(function( _data ){
				var _d = M.dealData(_data) , newsmsg = false , all_num = 0;
				
				for(var i=0; i<_d.length;i++){
					(function(i){
						if( parseInt(_d[i]) > 0 ){ newsmsg=true; }
						all_num += parseInt(_d[i])
						T.login.msgList.eq(i).text(_d[i]).parent().click(function(){
							window.location.href = (link[i])
						})
					})(i);
				}
				T.login.more.click(function(){
					window.location.href = (link[0])
				})
				all_num = all_num>99?'99+':all_num
				if(T.login.msgNum.length > 0 ){ //个人空间页面
					T.login.msgNum.text('消息('+all_num+')')
					var timer;
					if(!T.ishover){
						T.ishover = true;
						T.login.msg.hover(function(){
							T.login.msghover.show()
						},function(){
							timer = setTimeout(function(){T.login.msghover.hide()},200)
						});
						T.login.msghover.mouseenter(function(){
							clearTimeout(timer)
						})
					}
				}else{
					var cls = ['','']
					if(newsmsg){
						cls = ['userop-msg-notify','userop-msg-hover-newmsg']
					}
					T.login.msg.removeClass('userop-msg-notify userop-msg-hover-newmsg').addClass(cls[0])
					.unbind().hover(function(){
						$(this).children().first().addClass(cls[1]);
						$(this).children().css({display:"block"});
					},function(){
						$(this).children().first().removeClass(cls[1]);
						$(this).children().css({display:"none"});
					});
				}
				
			},URL.msg)
		}
	};

	window.membership = Member;
	//调用 membership.update()
})();
$(function(){
	var member = membership;
	$(document).mouseenter(function(){
		member.update();
	})
	member.init();
	member.loginBtn.click(function(){
		JRJ.ui.isLogin();
	})
})

