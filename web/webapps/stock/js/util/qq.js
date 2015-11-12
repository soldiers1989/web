$(function(){
	
	$.getScript( "/user/ajaxgetQQStatus.jspa", function(data) {
		$('.qq-account-inner').empty();
		var qqs= '2968898015:1724819341'.split(':');
	    for(var i=0;i<online.length;i++)
	    {
	        if (online[i]==0){
	        	$('.qq-account-inner').append('<a class="qq-btn qq-btn-3" target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin='+qqs[i]+'&site=qq&menu=yes" title="客服不在线，请留言"></a>');
	        }
	        else
	        {
	        	$('.qq-account-inner').append('<a class="qq-btn qq-btn-'+(i+1)+'" target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin='+qqs[i]+'&site=qq&menu=yes" title="客服中心工作时间：8：30-20：30，请问有什么可以帮您！"></a>');
	        }
	    }	
	});
});   
