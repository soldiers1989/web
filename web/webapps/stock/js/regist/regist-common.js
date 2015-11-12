$(function(){
	/* 开户流程右侧动画  ----  start */
	
	/*
	 * 原始方案
	 * 
	 * $('.xjb-login-content-r-inner ul li').mouseenter(function(){
		var _this = $(this);
		$('.xjb-login-content-r-inner ul li').stop().animate({'margin-bottom':'10px','padding-bottom':'10px'}, 500, function(){});
		_this.find('p.dot').stop().animate({'height':'120px'}, 500, function(){});

	}).mouseleave(function(){
		var _this = $(this);
		_this.find('p.dot').stop().animate({'height':'30px'}, 500, function(){});
		_this.stop().animate({'margin-bottom':'20px','padding-bottom':'20px'}, 500,function(){});
	});
	$('.xjb-login-content-r-inner ul').mouseleave(function(){
		$('.xjb-login-content-r-inner ul li').stop().animate({'margin-bottom':'20px','padding-bottom':'20px'}, 500, function(){});
		$('.xjb-login-content-r-inner ul li').find('p.dot').stop().animate({'height':'30px'}, 500, function(){});
	});*/
	
	
	$('.xjb-login-content-r-inner ul li').mouseenter(function(){
		var _this = $(this);
		_this.find('p.dot').stop().animate({'height':'120px'}, 500, function(){});

	}).mouseleave(function(){
		var _this = $(this);
		_this.find('p.dot').stop().animate({'height':'30px'}, 500, function(){});
	});
	$('.xjb-login-content-r-inner ul').mouseleave(function(){
		$('.xjb-login-content-r-inner ul li').find('p.dot').stop().animate({'height':'30px'}, 500, function(){});
	});
	
	/* 开户流程右侧动画  ----  end */
	
});