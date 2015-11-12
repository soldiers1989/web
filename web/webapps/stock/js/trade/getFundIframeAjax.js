/**
 * 
 */
$(document).ready(function(){
	
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
			
			var selUrl="/stock/"+_accountId+"/searchIframeFund.jspa";
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
			
			//alert(selUrl+"====="+startdate+"=="+selectType+"==="+enddate);
			getContent(selUrl,1,selectType,startdate,enddate);
		}else{
			$("#seltitle").show();
			$("#cal-1").show();
			$("#cal-2").show();
			$("#seltitle2").show();
			$("#selectBtn").show();

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
	        //alert(year+"-"+month+"-"+day);
	        var startdate=$("#cal-1").val().replace(/-/g,"");
			var enddate =$("#cal-2").val().replace(/-/g,"");
	        var selUrl="/stock/"+_accountId+"/searchIframeFund.jspa";
	        var selectType =$('input[name="Radio1"]:checked').val();
	        getContent(selUrl,1,selectType,startdate,enddate);
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
					message: "时间不能大于当前时间"     //提示语
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
        				message: "开始时间大于结束时间，请重新选择查询时间"     //提示语
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
        				message: "开始时间大于结束时间，请重新选择查询时间"     //提示语
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
		var selUrl="/stock/"+_accountId+"/searchIframeFund.jspa";
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

			if(JRJ.date.getDateDiff($("#cal-1").val(),$("#cal-2").val())>90){
				JRJ.Alerts.alert({
					title: "错误提示",  //标题
					width:500,
					autoClose:false,
					message: "查询起止时间应在90天以内，请重新选择查询时间"     //提示语
				  });
				  return false;
			}
		}
		///alert(selUrl+"====="+startdate+"=="+selectType+"==="+enddate);
		getContent(selUrl,1,selectType,startdate,enddate);
	});	
	
})
		
function getContent(url,pageNum,selectType,startdate,enddate){
	$.ajax({
		type : "get",
		url : url,
		data:{
			"pageNum" :pageNum,
			"selecttype" :selectType,
			"startDate" : startdate,
			"endDate" :enddate
		},
		dataType : "json",
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
				  $("#win").attr("src",data.url);
			  }				  
		 }
	});
}

function getStandardDate(date){
	return date.replace(/\-/g, '/');
}
