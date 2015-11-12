/**
 * 
 */
$(document).ready(function(){
	updatePageStatus();
	 //股票行情图
    var _timer = null;
    var popupStockImgEvent = function (obj) {
        window.clearTimeout(_timer);
        _timer = setTimeout(function () {
            $('.popStockImg').remove();

            var _code = $(obj).attr('data-code'),
            _url = '/stock/chart/pngdata/minpic/pic160/' + _code + '.png?' + (new Date().getTime()),
            imgTmpl = '<div class="popStockImg"><img src="/stock/images/loading.gif" alt="" style="width:50px;height:50px;" /></div>',
            _top = $(obj).offset().top,
            _left = $(obj).offset().left + $(obj).width();
            $(imgTmpl).css({ 'position': 'absolute', top: _top + 'px', left: _left + 'px' }).appendTo($('body'));

            var img = new Image();
            img.onload = function () {
                $('.popStockImg').find('img').attr({ src: _url }).css({width:'160px',height:'110px'});
            }
            img.src = _url;
        }, 300);
    };

    $('.img-trend').live('mouseenter',function () {
        popupStockImgEvent(this);
    }).live('mouseleave',function () {
        $('.popStockImg').remove();
    });
    $(document).click(function () {
        $('.popStockImg').remove();
    });

	
    $(".prompt-close").click(function(){
		$(this).parent(".prompt-txt").fadeOut();
	});
	$("#next").click(function(){
		var nextUrl="/stock/"+_accountId+"/getPositionPage.jspa";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(curSize<15){
			return;
		}
		getContent(nextUrl,Number(pageNum) + 1);
	});
	
	$("#prev").click(function(){
		var nextUrl="/stock/"+_accountId+"/getPositionPage.jspa";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(pageNum<=1){
			return;
		}
		getContent(nextUrl,Number(pageNum) - 1);
	})
	var totalWidth = 0;
        $('.assets').find('span').each(function () {
            totalWidth += $(this).outerWidth();
        });
        if ($('.assets').outerWidth() < totalWidth) {
            $('.assets').addClass('assets-s2').find('span:odd').addClass('noline');
        }
})
		
function getContent(url,pageNum){
	showLoading('positionContent');
	$.ajax({
		type : "GET",
		url : url,
		data:{
			"pageNum":pageNum
		},
		dataType : "html",
		//timeout : 5000,
		cache : false,
		success:function(data){
			  if(data.errMsg){
				  JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						message:data.errMsg     //提示语
					  }); 
			  }else{
				  $("#positionContent").empty().html(data);	
				  updatePageStatus();
			  }				  
		 }		  
	});
}

function updatePageStatus(){
	var curSize =$("#countsize").val();
	var pageNum = $("#pageNum").val();
	if(curSize<15){
		if(!$("#next").hasClass("next-disable")){
			$("#next").addClass("next-disable");	
			$("#next").css("cursor","default");
		}	
		$("#next").attr("title","最后一页");		
	}else{
		if($("#next").hasClass("next-disable")){
			$("#next").removeClass("next-disable");	
			$("#next").css("cursor","pointer");
		}
		$("#next").attr("title","下一页");
	}
	if(pageNum>1){
	if($("#prev").hasClass("prev-disable")){
		$("#prev").removeClass("prev-disable");	
		$("#prev").css("cursor","pointer");
	}	
	$("#prev").attr("title","上一页");		
	}else{
		if(!$("#prev").hasClass("prev-disable")){
			$("#prev").addClass("prev-disable");
			$("#prev").css("cursor","default");
		}
		$("#prev").attr("title","第一页");
	}
}














