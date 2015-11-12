//头部
(function($){
	var URL = {
			 msg : '/stock/itougu/message/num.jspa'
		}
		,T = {
			ajax : function( fn , url , data , datatype , cntenttype , type ){
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
		},
		Member = {
			setMsg : function(){				                                                              
				 //查看全部
				T.ajax(function( _data ){
					var _d = M.dealData(_data) , newsmsg = false , all_num = 0;
					
					for(var i=0; i<_d.length;i++){
						(function(i){
							if( parseInt(_d[i]) > 0 ){ newsmsg=true; }
							all_num += parseInt(_d[i])
							T.login.msgList.eq(i).text(_d[i]).parent();
							
						})(i);
					}
					
					all_num = all_num>99?'99+':all_num
					
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
					
					
				},URL.msg)
			}
		};
		window.membership = Member;
})(jQuery);
$(function () {
	var member = membership;
	member.setMsg();
})