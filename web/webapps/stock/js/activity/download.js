/**
 * 长江开户活动下载
 * @author haijun.sun(孙海军)
 * @date 2015年4月8日 下午4:44:22 
 * else if(browser.versions.ios != true&&is_weixn() == true)//如果为非ios且是微信访问 则提示弹出窗口
		alert("android and weixin");
 */
$(document).ready(function(){
		var browser={versions:function(){var u = navigator.userAgent;return {ios:!!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)};}()}
		function is_weixn(){var ua = navigator.userAgent.toLowerCase();if(ua.match(/MicroMessenger/i)=="micromessenger" && ua.match(/Android/i)=="android") { return true; } else { return false; } };
		function is_weixn_ios(){
			var ua = navigator.userAgent.toLowerCase();
			if(ua.match(/MicroMessenger/i)=="micromessenger" && browser.versions.ios == true) 
			{return true;}else{return false;}
		};
		/*	if(is_weixn()||is_weixn_ios()){//是否为微信访问
			 $.getScript("/stock/js/activity/WeiXinShare.js",function(){
					new WeiXinShare('http://www.smzdm.com/','金融界携手长江证券打造便捷手机开户模式，即日起开户长江证券即可获60元话费+长江热点投顾组合','金融界-长江证券手机开户可获60元话费+长江热点投顾组合','http://i0.jrjimg.cn/focus/2015/phoneDownload/wx.png');		 
			 });
		}
		*/
		$(".btn_load").bind('touchstart', function (){
			//触发下载之前需要更新活动记录
			//var mobile = $("#mobile").attr("value");
/*			$.ajax({
		    	 url : "/openAccount/activity/modfiytInfo.jspa",
		    	 data :{"mobile":mobile},
		    	 dataType:"json",
		    	 type:"post",
		    	 success:function(data){
		    	 	 if(data.retcode==0){
		    	 	 	if(is_weixn()||is_weixn_ios()){//是否为微信访问
							setTimeout(function(){$(".pop,.tsbox,.bgbig").show()},300);
						}
						
		    	 	 	if(browser.versions.ios == true)
		    	 	 		window.location.href="itms-services://?action=download-manifest&url=https://wap.95579.com/cjekh/jrj/001/ios/plist/cjeh_https.plist";
		    	 	 	else
		    	 	 		window.location.href= "http://wap.95579.com/cjekh/jrj/001/android/cjekh_jrj_20150204.apk";
		    	 	 }else{
		    	 	 	 alert("系统繁忙，请稍后重试.");//提示错误信息
		    	 	 }
		    	 }
			})*/
			
			if(is_weixn()||is_weixn_ios()){//是否为微信访问
				setTimeout(function(){$(".pop,.tsbox,.bgbig").show()},300);
			}
			
	 	 	if(browser.versions.ios == true)
	 	 		window.location.href="itms-services://?action=download-manifest&url=https://wap.95579.com/cjekh/jrj/001/ios/plist/cjeh_https.plist";
	 	 	else
	 	 		window.location.href= "http://wap.95579.com/cjekh/jrj/001/android/cjekh_jrj_20150204.apk";
		})
})