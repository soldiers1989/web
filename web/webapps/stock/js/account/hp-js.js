	$(function(){
		$(function(){setTimeout(function(){$(".lcts").hide()},10000)});
		function winxy(){
			var wx = $(window).width();
			var wy = $(window).height();
			$(".bgbig").css({"width":wx,"height":wy})
		};
		winxy();
		$(window).resize(function(){winxy()});
		$("#bcone").click(function(){$("#fcbc01").show();$(".bgbig").show()});
		$("#bctwo").click(function(){$("#fcbc02").show();$(".bgbig").show()});
		$("#bcthree").click(function(){$("#fcbc03").show();$(".bgbig").show()});
		$("#btnienone").click(function(){
			if(!msie()){$("#fc-ie,.bgbig").show();}else{kaihu(1);}
		});
		$("#btnientwo").click(function(){
			if(!msie()){$("#fc-ie-two,.bgbig").show();}else{kaihu(2);}
		});
		$("#btnienthree").click(function(){
			if(!msie()){$("#fc-ie-three,.bgbig").show();}else{kaihu(1);}
		});
		$(".fc-ie .clear-btn-ie,.btnie input,.fcbc .btn-ok a").click(function(){$(".fc-ie,.bgbig").hide();});
		var ie6Fixed = JRJ.util.ie6Fixed;
		if($("#fcbc01").length>0) ie6Fixed.fixed($("#fcbc01"),"lt");
        if($("#fcbc02").length>0) ie6Fixed.fixed($("#fcbc02"),"lt");
        if($("#fcbc03").length>0) ie6Fixed.fixed($("#fcbc03"),"lt");
        if($("#fc-ie").length>0) ie6Fixed.fixed($("#fc-ie"),"lt");
        if($(".bgbig").length>0) ie6Fixed.fixed($(".bgbig"),"lt");
	})
	
	function msie() {
		return $.browser.msie || (/Trident\/7\./).test(navigator.userAgent);
	}
	function msie2() {
		//判断是否为谷歌
		var chrome = $.browser.webkit && !!window.chrome;
		return $.browser.version > 7 || chrome || $.browser.mozilla;
	}