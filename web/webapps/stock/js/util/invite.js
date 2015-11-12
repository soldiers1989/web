$(document).ready(function(){
	//复制到剪切板
    var clip = new ZeroClipboard(document.getElementById("copy-button"), {
        moviePath: "/stock/stockFlash/ZeroClipboard.swf"
    });
    clip.on('complete', function (client, args) {
        alert('以下内容已经复制到剪切板 ' + args.text);
    });
    var invite_code = $('.invite_url').data('invitecode');
	var baesurl = window.location.protocol+"//"+window.location.host+"/stock/invite.jspa?ic="+invite_code;
	$('.invite_url').val(baesurl);
	$('#copy-button').attr('data-clipboard-text',baesurl);	
	var myDate = new Date();
	var y = myDate.getFullYear(); 
	var m = myDate.getMonth()+1;   
	var hour = myDate.getHours();
	var time = '';
	if(hour < 6){time = "凌晨好！";} 
	else if (hour < 9){time = "早上好！";} 
	else if (hour < 12){time = "上午好！";} 
	else if (hour < 14){time = "中午好！";} 
	else if (hour < 17){time = "下午好！";} 
	else if (hour < 19){time = "傍晚好！";} 
	else if (hour < 22){time = "晚上好！";} 
	else {time ="夜里好！";} 
	$("#sayhi").html(time);
	$("#securityhelp").attr({"href":"http://"+window.location.host+"/help/help_qa.html"});
})