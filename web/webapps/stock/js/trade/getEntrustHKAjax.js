/**
 * 
 */
$(document).ready(function(){
nextUrl="/stock/hk/"+_accountId+"/getEntrustPage.jspa";
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
			
			getContent(nextUrl,1);
			
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
	        now.setDate(now.getDate()-90);
	        
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
	      
			getContent(netxUrl,1);
		}
	}); 
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

				if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>90){
					JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						autoClose:false,
						message: "查询起止时间应在90天以内，请重新选择查询时间"     //提示语
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
				if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>90){
					JRJ.Alerts.alert({
						title: "错误提示",  //标题
						width:400,
						autoClose:false,
						message: "查询起止时间应在90天以内，请重新选择查询时间"     //提示语
					  });
					$("#cal-2").val("");
				}
        	}
        }
    });
	
	//查询按钮
	$("#selectBtn").click(function(){
		getContent(nextUrl,1);
	}); 	
	
	$("#next").click(function(){
		//var nextUrl="";
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(curSize<15){
			return;
		}
		
		getContent(nextUrl,Number(pageNum) + 1);
	});
	
	$("#prev").click(function(){
		var curSize =$("#countsize").val();
		var pageNum = $("#pageNum").val();
		if(pageNum<=1){
			return;
		}
		getContent(nextUrl,Number(pageNum) -1);
	});

})
		
function getContent(url,pageNum){
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

			if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>90){
				JRJ.Alerts.alert({
					title: "错误提示",  //标题
					width:400,
					autoClose:false,
					message: "查询起止时间应在90天以内，请重新选择查询时间"     //提示语
				  });
				  return false;
			}
			url="/stock/hk/"+_accountId+"/getHistoryEntrustPage.jspa";
		}else if(selectType=="today"){//当日
			url="/stock/hk/"+_accountId+"/getEntrustPage.jspa";
		}else{
			url="/stock/hk/"+_accountId+"/getHistoryEntrustPage.jspa";
		}

		if(selectType!=selectT || startdate!=sData || enddate!=eData){//判断查询条件是否改变
			pageNum=1;
		}
	}
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
//计算天数差的函数，通用  
function  DateDiff(sDate1,  sDate2){    //sDate1和sDate2是2006-12-18格式  
    var  aDate,  oDate1,  oDate2,  iDays  
    aDate  =  sDate1.split("-")  
    oDate1  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0])    //转换为12-18-2006格式  
    aDate  =  sDate2.split("-")  
    oDate2  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0])  
    iDays  =  parseInt(Math.abs(oDate1  -  oDate2)  /  1000  /  60  /  60  /24)    //把相差的毫秒数转换为天数  
    return  iDays  
}    












