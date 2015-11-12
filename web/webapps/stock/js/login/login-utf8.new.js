document.writeln('<div class="enter">');
document.writeln('<div class="w1000 jrj-clear">');
document.writeln('<div class="left"><p><a href="http://www.jrj.com.cn/" class="hover" target="_blank">\u53bb\u91d1\u878d\u754c\u9996\u9875\u770b\u770b</a><i>|</i><a href="http://www.m.jrj.com.cn/" target="_blank">\u624b\u673a\u91d1\u878d\u754c</a><i>|</i><a href="http://emoney.jrj.com.cn/index.aspx" target="_blank">\u90ae\u4ef6\u8ba2\u9605</a><i>|</i><a href="http://www.jrj.com.cn/sitemap.shtml" target="_blank">\u7f51\u7ad9\u5730\u56fe</a><b>\u6295\u8d44\u670d\u52a1\uff1a</b><a href="http://itougu.jrj.com.cn/?tgqdcode=38DF753J" target="_blank">\u7231\u6295\u987e</a><i>|</i><a href="http://t.jrj.com.cn/?tgqdcode=38DF753J" target="_blank">\u8bc1\u5238\u901a</a><i>|</i><a href="http://8.jrj.com.cn/?tgqdcode=38DF753J" target="_blank"  rel="nofollow">\u76c8\u5229\u5b9d</a></p></div>');
document.writeln('<div class="right jrj-clear">');
document.writeln('<ul class="jrj-clear">');
document.writeln('<li><span class="iphone" style="display:none;"></span></li>');
document.writeln('<li><span class="share_weixin" id="swxBtn"></span><i>|</i><span class="share_sina"><a href="http://weibo.com/jrjnews/" target="_blank"></a></span><i>|</i></li>');
document.writeln("</ul>");
document.writeln('<div class="login" id="loginbefore"><ul><li><a href="http://i.jrj.com.cn/register.faces?ReturnURL='
				+ location.href
				+ "\" target=\"_blank\" onClick=\"ga('send', 'event', { eventCategory: 'jrj', eventAction: 'register'});\">\u6ce8\u518c</a><i>|</i><a href=\"http://sso.jrj.com.cn/sso/ssologin?ReturnURL="
				+ location.href
				+ '" class="hover" target="_self">\u767b\u5f55</a></li></ul></div>');
document.writeln('<div class="loginnew" id="loginafter">');
document.writeln("</div>");
document.writeln("</div>");
document.writeln("</div>");
document.writeln("</div>");
$(function() {
	var a = '<div class="fcwx"><div class="aww"></div><img src="/stock/images/v2015/jrj_wx.jpg" /></div>';
	$("#swxBtn").hover(function() {
		var b = $(this).offset().left;
		$(".enter").append(a);
		$(".fcwx").css("left", b - 85 + "px")
	}, function() {
		return false
	});
	$(".enter").hover(function() {
		return false
	}, function() {
		$(".fcwx").remove()
	})
});