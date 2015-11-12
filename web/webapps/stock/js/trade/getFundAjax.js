/**
 * 
 */
$(document).ready(function(){
	updatePageStatus();
	//自定义--日期输入控制
	$("#seltitle").hide();
	$("#cal-1").hide();
	$("#cal-2").hide();
	$("#seltitle2").hide();
	$("#selectBtn").hide();
	$('input:radio').click(function (){ 
		if($('input[name="Radio1"]:checked').val()!="custom"){
			//$('#cal-1').attr('disabled',true);
			//$('#cal-2').attr('disabled',true);
			
			$("#seltitle").hide();
			$("#cal-1").hide();
			$("#cal-2").hide();
			$("#seltitle2").hide();
			$("#selectBtn").hide();
			
			$("#cal-1").val("");
			$("#cal-2").val("");
			var startdate=$("#cal-1").val().replace(/-/g,"");
			var enddate =$("#cal-2").val().replace(/-/g,"");
			
			var selUrl="";
			var selectType =$('input[name="Radio1"]:checked').val();
			
			if(selectType=="" || selectType==null){
				JRJ.Alerts.alert({
					title: "提示",  //标题
					width:400,
					autoClose:false,
					message: "请选择查询条件"     //提示语
				  });
				  return false;
			} 
		
			selUrl="/stock/"+_accountId+"/getFundPage.jspa";
			
			///alert(selUrl+"====="+startdate+"=="+selectType+"==="+enddate);
			getContent(selUrl,1,selectType,startdate,enddate);
			
		}else{
			$("#seltitle").show();
			$("#cal-1").show();
			$("#cal-2").show();
			$("#seltitle2").show();
			$("#selectBtn").show();
			
			//$('#cal-1').removeAttr('disabled');
			//$('#cal-2').removeAttr('disabled');

			var now = new Date();
	       
	        var year = now.getFullYear();       //年
	        var month = now.getMonth() + 1;     //月
	        var day = now.getDate();            //日
	        if(month<10){
	        	month="0"+month;
	        }
	        if(day<10){
	        	day="0"+day;
	        }
	        //alert(year+"-"+month+"-"+day);
	        $("#cal-2").val(year+"-"+month+"-"+day);
	        now.setDate(now.getDate()-30);
	        
	        year = now.getFullYear();       //年
	        month = now.getMonth() + 1;     //月
	        day = now.getDate();            //日
	        if(month<10){
	        	month="0"+month;
	        }
	        if(day<10){
	        	day="0"+day;
	        }
	        $("#cal-1").val(year+"-"+month+"-"+day);
	        //alert(year+"-"+month+"-"+day);
	        var startdate=$("#cal-1").val().replace(/-/g,"");
			var enddate =$("#cal-2").val().replace(/-/g,"");
	        var selUrl="/stock/"+_accountId+"/getFundPage.jspa";
	        var selectType =$('input[name="Radio1"]:checked').val();
	        getContent(selUrl,1,selectType,startdate,enddate);
		}
	}); 
//	//求两个时间的天数差
//	function daysBetween(DateOne,DateTwo)  
//	{   
//	    var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-'));  
//	    var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1);  
//	    var OneYear = DateOne.substring(0,DateOne.indexOf ('-'));  
//	  
//	    var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ('-'));  
//	    var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ('-')+1);  
//	    var TwoYear = DateTwo.substring(0,DateTwo.indexOf ('-'));  
//	  
//	    var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000);   
//	    return Math.abs(cha);  
//	}  
	
	//绑定日期控件
    new JRJ.ui.Calendar("cal-1", {
        //closable: false,
        validType: 'blur',//'blur','input'
        onPicked: function (d) {
        	var d1=$("#cal-2").val();
        	var d2=d;
        	var d2 = d.format("yyyy-MM-dd");

        	d1Arr=d1.split('-');
        	d2Arr=d2.split('-');
        	v1=new Date(d1Arr[0],d1Arr[1]-1,d1Arr[2]);
        	v2=new Date(d2Arr[0],d2Arr[1]-1,d2Arr[2]);
        	var d=new Date(Date.parse(d2));
        	var curDate=new Date();
        	if(d > curDate){
        		JRJ.Alerts.alert({
					title: "错误提示",  //标题
					width:400,
					autoClose:false,
					message: "起始时间不能大于当前时间"     //提示语
				  });
    			$("#cal-1").val("");
    			return;
        	}
        	if(d1Arr!="" && d2Arr !=""){
        		if(v2>v1){
        			JRJ.Alerts.alert({
        				title: "错误提示",  //标题
        				width:450,
        				autoClose:false,
        				message: "起始时间不能大于结束时间，请重新选择查询时间"     //提示语
        			  });
        			$("#cal-1").val("");
        			return;
        		}
//        		var _begin = new Date($("#cal-1").val());
//				var _end = new Date($("#cal-2").val());
//				var dateCount = (_end - _begin)/86400000;
				if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>30){
					JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						autoClose:false,
						message: "查询起止时间应在30天以内，请重新选择查询时间"     //提示语
					  });
					$("#cal-1").val("");
				}
        	}
        }
    });
    new JRJ.ui.Calendar("cal-2", {
        //closable: false,
        validType: 'blur',//'blur','input'
        onPicked: function (d) {
            //alert(d)
        	var d1=$("#cal-1").val();
        	var d2=d;
        	
        	var d2 = d.format("yyyy-MM-dd");
        	d1Arr=d1.split('-');
        	d2Arr=d2.split('-');
        	v1=new Date(d1Arr[0],d1Arr[1]-1,d1Arr[2]);
        	v2=new Date(d2Arr[0],d2Arr[1]-1,d2Arr[2]);
        	//alert(v2+"===="+new Date());
        	var d=new Date(Date.parse(d2));
        	var curDate=new Date();
        	if(d > curDate){
        		JRJ.Alerts.alert({
					title: "错误提示",  //标题
					width:400,
					autoClose:false,
					message: "结束时间不能大于当前时间"     //提示语
				  });
    			$("#cal-2").val("");
    			return;
        	}

        	if(d1Arr!="" && d2Arr !=""){
        		//alert(v2<v1);
        		if(v2<v1){
        			JRJ.Alerts.alert({
        				title: "错误提示",  //标题
        				width:450,
        				autoClose:false,
        				message: "起始时间不能大于结束时间，请重新选择查询时间"     //提示语
        			 });
        			$("#cal-2").val("");
        			return;
        		}
        		
//        		var _begin = new Date($("#cal-1").val());
//				var _end = new Date($("#cal-2").val());
//				var dateCount = (_end - _begin)/86400000;
				if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>30){
					JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						autoClose:false,
						message: "查询起止时间应在30天以内，请重新选择查询时间"     //提示语
					  });
					$("#cal-2").val("");
				}
        	}
        }
    });
	
	//查询按钮
	$("#selectBtn").click(function(){
		var selUrl="";
		var selectType =$('input[name="Radio1"]:checked').val();
		var startdate=$("#cal-1").val().replace(/-/g,"");
		var enddate =$("#cal-2").val().replace(/-/g,"");
		if(selectType=="" || selectType==null){
			JRJ.Alerts.alert({
				title: "提示",  //标题
				width:400,
				autoClose:false,
				message: "请选择查询条件"     //提示语
			  });
			  return false;
		} 
		if(selectType=="custom"){
			if(startdate=="" || enddate==""){//没选择起止时间
				JRJ.Alerts.alert({
					title: "提示",  //标题
					width:400,
					autoClose:false,
					message: "请输入查询时间"     //提示语
				  });
				  return false;
			}
//			var _begin = new Date($("#cal-1").val());
//			var _end = new Date($("#cal-2").val());
//			var dateCount = (_end - _begin)/86400000;
			//alert(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val()));
			if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>30 || JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())<0){
				
				JRJ.Alerts.alert({
					title: "错误提示",  //标题
					width:500,
					autoClose:false,
					message: "查询起止时间应在30天以内，请重新选择查询时间"     //提示语
				  });
				  return false;
			}
			selUrl="/stock/"+_accountId+"/getFundPage.jspa";
		}else{
			selUrl="/stock/"+_accountId+"/getFundPage.jspa";
		}
		getContent(selUrl,1,selectType,startdate,enddate);
	}); 	
	
	$("#next").click(function(){
		var nextUrl="";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(curSize<15){
			return;
		}
		
		var selectType =$('input[name="Radio1"]:checked').val();
		var startdate=$("#cal-1").val().replace(/-/g,"");
		var enddate =$("#cal-2").val().replace(/-/g,"");
		
		var selectT=$("#selectT").val();
		var sData=$("#sData").val();
		var eData=$("#eData").val();
		
		if(selectType=="" || selectType==null){
			JRJ.Alerts.alert({
				title: "提示",  //标题
				width:400,
				autoClose:false,
				message: "请选择查询条件"     //提示语
			  });
			  return false;
		}else{
			if(selectType=="custom"){//自定义
				if(startdate=="" || enddate==""){//没选择起止时间
					JRJ.Alerts.alert({
						title: "提示",  //标题
						width:400,
						autoClose:false,
						message: "请输入查询时间"     //提示语
					  });
					  return false;
				}
//				var _begin = new Date($("#cal-1").val());
//				var _end = new Date($("#cal-2").val());
//				var dateCount = (_end - _begin)/86400000;
				if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>30 || JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())<1){
					JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						autoClose:false,
						message: "查询起止时间应在30天以内，请重新选择查询时间"     //提示语
					  });
					  return false;
				}
				nextUrl="/stock/"+_accountId+"/getFundPage.jspa";
			}else{
				nextUrl="/stock/"+_accountId+"/getFundPage.jspa";
			}
	
			if(selectType==selectT && startdate==sData && enddate==eData){//判断查询条件是否改变
				getContent(nextUrl,Number(pageNum) + 1,selectType,startdate,enddate);
			}else{
				getContent(nextUrl,1,selectType,startdate,enddate);
			}
		}
	});
	
	$("#prev").click(function(){
		var nextUrl="";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(pageNum<=1){
			return;
		}
		
		var selectType =$('input[name="Radio1"]:checked').val();
		var startdate=$("#cal-1").val().replace(/-/g,"");
		var enddate =$("#cal-2").val().replace(/-/g,"");
		
		var selectT=$("#selectT").val();
		var sData=$("#sData").val();
		var eData=$("#eData").val();
		
		if(selectType=="" || selectType==null){
			JRJ.Alerts.alert({
				title: "提示",  //标题
				width:400,
				autoClose:false,
				message: "请选择查询条件"     //提示语
			  });
			  return false;
		}else{
			if(selectType=="custom"){//自定义
				if(startdate=="" || enddate==""){//没选择起止时间
					JRJ.Alerts.alert({
						title: "提示",  //标题
						width:400,
						autoClose:false,
						message: "请输入查询时间"     //提示语
					  });
					  return false;
				}
//				var _begin = new Date($("#cal-1").val());
//				var _end = new Date($("#cal-2").val());
//				var dateCount = (_end - _begin)/86400000;
				if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>30 || JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())<1){
					JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						autoClose:false,
						message: "查询起止时间应在30天以内，请重新选择查询时间"     //提示语
					  });
					  return false;
				}
				nextUrl="/stock/"+_accountId+"/getFundPage.jspa";
			}else{
				nextUrl="/stock/"+_accountId+"/getFundPage.jspa";
			}
	
			if(selectType==selectT && startdate==sData && enddate==eData){//判断查询条件是否改变
				getContent(nextUrl,Number(pageNum) -1 ,selectType,startdate,enddate);
			}else{
				getContent(nextUrl,1,selectType,startdate,enddate);
			}
		}
	})

})
		
function getContent(url,pageNum,selectType,startdate,enddate){
	showLoading('nextContent');
	$.ajax({
		type : "GET",
		url : url,
		data:{
			"pageNum" :pageNum,
			"selecttype" :selectType,
			"startDate" : startdate,
			"endDate" :enddate
		},
		dataType : "html",
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
//function getdate(bd,ed){
//	var _begin = new Date(bd);
//	var _end = new Date(ed);
//	alert("--------------------"+_begin > _end);
//	if(_begin > _end){
//		JRJ.Alerts.alert({
//			title: "错误提示",  //标题
//			width:450,
//			autoClose:false,
//			message: "开始时间大于结束时间，请重新选择查询时间"     //提示语
//				
//		  });
//		  return false;
//	}		
//	var dateCount = (_end - _begin)/86400000;
//	if(dateCount>90 || dateCount<1){
//		JRJ.Alerts.alert({
//			title: "错误提示",  //标题
//			width:400,
//			message: "查询起止时间应在90以内，请重新选择查询时间"     //提示语
//		  });
//		  return false;
//	}
//	return true;
//}












