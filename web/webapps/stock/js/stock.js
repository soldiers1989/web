$(function()
{
	$('.prompt-close').bind('click',function(){
		$(this).parents('.prompt').hide();
	});
	checkMsgCnt();
	/*
	$('.entrust-down').bind('click',function(){
		// $('.entrust-down-info').css('display','block');
		// var iH=$('.entrust-down-info').outerHeight(true);
		// $('.entrust-down-info').css('height','0px');
		// $('.entrust-down-info').stop().animate({
		// 	'height':iH
		// },399);
		$('.entrust-down-info').slideDown();
		$(this).hide();
		$(this).siblings('em').text('点击收起，暂不查看股票信息');
		$('.entrust-up').show();
	});
	$('.entrust-up').bind('click',function(){
		$('.entrust-down-info').slideUp();
		$(this).hide();
		$(this).siblings('em').text('点击下拉，查看更多股票信息')
		$('.entrust-down').show();
	});*/
})
function checkMsgCnt(){
	$.ajax({
		type : "get",
		url : "/stock/userMsgCnt.jspa",
		dataType : "json",
		timeout : 5000,
		cache : false,
		success:function(data){
			if(data!=undefined&&data.msgCnt!=undefined){
				$("#home_lgt_user_info_num").text(data.msgCnt);
			}
		}
	});
}