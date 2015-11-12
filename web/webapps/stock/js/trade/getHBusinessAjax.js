/**
 * 
 */
$(document).ready(function(){
	var nowDate = new Date();
	var beginDate = getFormatDateByLong(nowDate.getTime()-604800000)
	var endDate =getFormatDateByLong(nowDate.getTime()-86400000)
	$("#beginTime").val(beginDate);
	$("#endTime").val(endDate);
	
	var s = new JRJ.ui.Calendar("beginTime", {
		yearRange : [ 2013, 2099 ],
		validType : 'blur',// 'blur','input'
		triggerType : "focus",
		onPicked : function(s) {
		}
	});

	var e = new JRJ.ui.Calendar("endTime", {
		yearRange : [ 2013, 2099 ],
		validType : 'blur',// 'blur','input'
		triggerType : "focus",
		onPicked : function(d) {
		}
	});
	
	$('#date-list-s').click(function (ee) {
        ee.preventDefault();
        ee.stopPropagation();
        s.toggle();
    });
	
    $('#date-list-e').click(function (ee) {
        ee.preventDefault();
        ee.stopPropagation();
        e.toggle();
    });

	$("#next").click(function(){
		var nextUrl="/stock/getHistoryBusinessPage.jspa";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		var beginTime = $("#beginTime").val().replace(/\-/g, '');
		var endTime = $("#endTime").val().replace(/\-/g, '');
		if(curSize<15){
			return;
		}
		getContent(nextUrl,beginTime,endTime,Number(pageNum)+1);
	});
	
	$("#prev").click(function(){
		var nextUrl="/stock/getHistoryBusinessPage.jspa";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		var beginTime = $("#beginTime").val().replace(/\-/g, '');
		var endTime = $("#endTime").val().replace(/\-/g, '');
		if(pageNum<=1){
			return;
		}
		getContent(nextUrl,beginTime,endTime,Number(pageNum)-1);
	})
	
	$("#search-hbusiness").click(function(){
		var nextUrl="/stock/searchHBusiness.jspa";
		var bd = getStandardDate($("#beginTime").val());
		var ed = getStandardDate($("#endTime").val());
		var _begin = new Date(bd);
		var _end = new Date(ed);
		if(_begin > _end){
			JRJ.Alerts.alert({
				title: "错误提示",  //标题
				width:400,
				message: "开始时间大于结束时间，请重新选择查询时间"     //提示语
			  });
			  return ;
		}		
		var dateCount = (_end - _begin)/86400000;
		if(dateCount>90 || dateCount<0){
			JRJ.Alerts.alert({
				title: "错误提示",  //标题
				width:400,
				message: "查询起止时间应在90天以内，请重新选择查询时间"     //提示语
			  });
			  return ;
		}		
		var beginTime = $("#beginTime").val().replace(/\-/g, '');
		var endTime = $("#endTime").val().replace(/\-/g, '');
		getContent(nextUrl,beginTime,endTime,1);
	})	

})
		
function getContent(url,beginTime,endTime,pageNum){
	showLoading('nextContent');
	$.ajax({
		type : "get",
		url : url,
		data:{
			"startDate" :beginTime,
			"endDate" :endTime,
			"pageNum" :pageNum
		},
		dataType : "html",
		timeout : 5000,
		cache : false,
		success:function(data){
			  if(data.errMsg){
				  JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						message:data.errMsg     //提示语
					  });  
			  }else{
				  $("#nextContent").empty().html(data);
				  updatePageStatus();
			  }				  
		 }		  
	});
}

function updatePageStatus(){
	var curSize =$("#countsize").val();
	var pageNum = $("#pageNum").val();
	if(curSize<15){
	if(!$("#next").hasClass("btn-pager-disable")){
		$("#next").addClass("btn-pager-disable");	
	}	
	$("#next").attr("title","最后一页");		
	}else{
		if($("#next").hasClass("btn-pager-disable")){
			$("#next").removeClass("btn-pager-disable");	
		}
		$("#next").attr("title","下一页");
	}
	
	if(pageNum>1){
	if($("#prev").hasClass("btn-pager-disable")){
		$("#prev").removeClass("btn-pager-disable");	
			
	}	
	$("#prev").attr("title","上一页");		
	}else{
		if(!$("#prev").hasClass("btn-pager-disable")){
			$("#prev").addClass("btn-pager-disable");
		}
		$("#prev").attr("title","第一页");
	}	
}


function getStandardDate(date){
	return date.replace(/\-/g, '/');
}









