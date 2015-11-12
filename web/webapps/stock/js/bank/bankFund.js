/**
 * 
 */
$(document).ready(function(){
	updatePageStatus();
	
    $("#next").click(function(){
		var nextUrl="";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(curSize < 15){
			return;
		}
		nextUrl="/stock/"+_accountId+"/bankFundPage.jspa";
		getContent(nextUrl,Number(pageNum) + 1);
	});
	
    $("#prev").click(function(){
		var nextUrl="";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(pageNum<=1){
			return;
		}
		nextUrl="/stock/"+_accountId+"/bankFundPage.jspa";
		getContent(nextUrl,Number(pageNum) -1 );
	})
	
})


function getContent(url,pageNum){
	$.ajax({
		type : "GET",
		url : url,
		data:{
			"pageNum" :pageNum
			
		},
		dataType : "html",
		///timeout : 5000,
		cache : false,
		beforeSend:function(){ //loading 
			
        }, 
		success:function(data){
			  if(data.errMsg){
				  JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						message:data.errMsg     //提示语
					  });
			  }else{
				  $("#bankFundContent").empty().html(data);
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
function getStandardDate(date){
	return date.replace(/\-/g, '/');
}
