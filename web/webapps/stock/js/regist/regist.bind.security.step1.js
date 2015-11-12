/**
 * 
 */
$(document).ready(function(){
	$("#kaihu").bind("click",function(){
		$(".msg-block").show();
	})
	//$("#btn_noie").bind("click",function(){
	//	openkaihu();
	//})
	$('.agreement').click(function () {
        JRJ.Dialogs.iframeDialog({
        	width:600,
            content: [''].join(''),
            hasOkBtn: true,
            okBtnText: '已经阅读',
            title: '用户服务协议（网上证券交易信息服务）',
            titleRight : '',
            hasCancelBtn: false,
            enableKeyCtrl: true,
            ifrSrc: "/stock/protocol/stockTrade.jsp",
            ifrReHeight: true,
            okCallback: function () {
            	$("#agree").attr("checked","checked");
            	$("#agree_error").hide();
                JRJ.Dialogs.close();
                return true;
            }
        });
    });
	
	$("#changeBroker").bind("click",function(){	
			JRJ.Dialogs.iframeDialog({
		            width: 750,
		            content: [''].join(''),
		            hasOkBtn: false,
		            title: '三步完成转户 享受低佣金快捷交易',
		            titleRight : '',
		            hasCancelBtn: false,
		            enableKeyCtrl: true,
		            ifrSrc: '/stock/changeBroker.jspa',
		            ifrReHeight: true,
		            okCallback: function () {
		                
		            }
		        });
		});
	
	$("#nextBtn").bind("click",function(){	
	if(!$("#agree").attr("checked")){
		JRJ.Alerts.alert({
			title: "消息提示",  //标题
			width:400,
			message: "请先认真阅读相关协议"     //提示语
		  });
		return false;
	}
		 
		 goZX();
	});
	
})

function goZX(){
	var sysStatus = "0";
	var brokers = $("input[name='brokerId']");
	var brokerId ="";
	for(var i=0;i<brokers.length;i++){
		if(brokers[i].checked){
			brokerId=brokers[i].value;			
		}
	}
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	};
	
	$.ajax({
		type : "get",
		url :XJB.Urls.checkBrokerSys,
		data :{
			"brokerId" : brokerId
		  },
		dataType : "json",
		timeout : 5000,
		async:false,
		cache : false,
		success:function(data){
			  if(data != undefined){
				  sysStatus = data; 
			  }				  
		 }		  
	});
	if(sysStatus == "0"){
		window.location.href="/stock/sysError.jspa";
		return false;
	}
	JRJ.Dialogs.iframeDialog({
	    width: 750,
	    hasOkBtn: false,
	    title: '登录券商授权页进行绑定',
        titleRight : '',
	    hasCancelBtn: false,
	    enableKeyCtrl: true,
	    ifrSrc: '/stock/binding.jspa',
	    ifrReHeight: true,
	    cancelCallback:checkBind
	});
	var ua = navigator.userAgent.toLowerCase();	
	var left2s = (window.document.body.offsetWidth -960)/2;
	var ret = window.open("/stock/initBroker.jspa?brokerId="+brokerId+"& _="+Math.random(),"ZXBD", "height=870,width=960,scrollbars=no,location=no,left="+left2s );			
	var intervalFunc = setInterval(reloadPage,500);
	function reloadPage(){
		    if(ret.closed){
		        window.clearInterval(intervalFunc);
		        JRJ.Dialogs.close();
		    }
		}
}

function checkBind(){
	var brokers = $("input[name='brokerId']");
	var brokerId ="";
	for(var i=0;i<brokers.length;i++){
		if(brokers[i].checked){
			brokerId=brokers[i].value;			
		}
	}
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	}
	var url ="/stock/rbindRS.jspa?brokerId="+brokerId;
	window.location.href=url;
}

function openkaihu(){
	$(".msg-block").hide();
	window.open("https://kh.cs.ecitic.com/webtrade/zxkh/login.jsp?promote_type=branch&promote_code=ODMy&promote_wbjg=1002","ZHKH");	
}
