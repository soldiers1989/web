/**
 * 
 */
$(document).ready(function(){

	$("#kaihu").bind("click",function(){
		if(!isIE()){
			JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "您即将离开金融界网站，进入中信证券的开户流程 </br>为保障您的开户顺利，请使用IE浏览器" ,    //提示语
				okCallback: null, //ok回调
				cancelCallback: null //cancel回调，关闭时也触发
			  });

			return false;
		}else{
				JRJ.Alerts.alert({
					title: "消息提示",  //标题
					width:400,
					message: "您即将离开金融界网站，进入中信证券的开户流程" ,    //提示语
					okCallback: kaihu, //ok回调
					cancelCallback: kaihu //cancel回调，关闭时也触发
				  });
		}
	
	})
	
	$('.agreement_user').click(function () {
        JRJ.Dialogs.iframeDialog({
        	width:600,
            content: [''].join(''),
            hasOkBtn: true,
            okBtnText: '已经阅读',
            title: '证券通用户协议',
            titleRight : '',
            hasCancelBtn: false,
            enableKeyCtrl: true,
            ifrSrc: XJB.Urls.protocolOpen,
            ifrReHeight: true,
            okCallback: function () {
            	$("#agree").attr("checked","checked");
            	$("#agree_error").hide();
                JRJ.Dialogs.close();
                return true;
            }
        });
    });
	
$('.agreement_trade').click(function () {
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
$('.agreement_zxzq').click(function () {
    JRJ.Dialogs.iframeDialog({
    	width:600,
        content: [''].join(''),
        hasOkBtn: true,
        okBtnText: '已经阅读',
        title: '中信证券股份有限公司客户互联网第三方交易系统使用风险告知书',
        titleRight : '',
        hasCancelBtn: false,
        enableKeyCtrl: true,
        ifrSrc: "/stock/protocol/zx.jsp",
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
	
	$("#nextBtn").click(function(){
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
	
	$('#btnbind').click(goZS);
})
function initBind(brokerId){
	$("#brokerId_bind").val(brokerId);
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
	var dialog = JRJ.Dialogs.iframeDialog({
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
	var ret = window.open("/stock/initBroker.jspa?brokerId="+brokerId+"& _="+Math.random(),"ZXBD", "height=570,width=760,scrollbars=no,location=no,left="+left2s );						
	var intervalFunc = setInterval(reloadPage,500);
	function reloadPage(){
		    if(ret.closed){
		        window.clearInterval(intervalFunc);
		        JRJ.Dialogs.close();
		    }
		}
}
function goZX(){
	//var brokers = $("input[name='brokerId_bind']");
	//var brokerId ="";
	//for(var i=0;i<brokers.length;i++){
	//	if(brokers[i].checked){
	//		brokerId=brokers[i].value;			
	//	}
	//}
	var brokerId =$("#brokerId_bind").val();
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	}
	initBind(brokerId);		
}

function goZS(){
	var brokerId=$(this).data('brokerid');
	$("#brokerId_bind").val(brokerId);
	initBind(brokerId);
}
function checkBind(){
	/*var brokers = $("input[name='brokerId_bind']");
	var brokerId ="";
	for(var i=0;i<brokers.length;i++){
		if(brokers[i].checked){
			brokerId=brokers[i].value;			
		}
	}*/
	var brokerId =$("#brokerId_bind").val();
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	}
	var url ="/stock/bindRS.jspa?brokerId="+brokerId+"&redirect="+ (typeof redirect != "undefined" ?encodeURIComponent(redirect) :"");
	window.location.href=url;
}
function kaihu(type){
	if (type == undefined){
		type = 1;
	}
	/*
	var brokers = $("input[name='brokerId_kaihu']");
	var brokerId ="";
	for(var i=0;i<brokers.length;i++){
		if(brokers[i].checked){
			brokerId=brokers[i].value;			
		}
	}*/
	var brokerId =$("#brokerId_bind").val();
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	}
	$.ajax({
		type : "get",
		url :"/stock/kaihu.jspa",
		data :{
			"brokerId" : brokerId,
			"type" : type
		  },
		dataType : "json",
		timeout : 5000,
		async:false,
		cache : false,
		success:function(data){
			  if(data &&data !=undefined){
				  if(data.error && data.error ==-2){
					  JRJ.Alerts.alert({
							title: "消息提示",  //标题
							width:400,
							message: "用户信息不完整，请完善姓名、身份证、手机号等信息！"     //提示语
						  });
					  return false;
				  }
				  if(data.error && data.error ==-1){
					  JRJ.Alerts.alert({
							title: "消息提示",  //标题
							width:400,
							message: "请选择券商"     //提示语
						  });
				  }else if(data.error && data.error !=-1){
					  JRJ.Alerts.alert({
							title: "消息提示",  //标题
							width:400,
							message: data.error     //提示语
						  });
				  }
				  window.open(data.kaihuUrl,"ZHKH");
				  if(type == "1" || type == "2"){//只有开户时才刷页面
					  window.parent.location.reload();
				  }
			  }else{
				  JRJ.Alerts.alert({
						title: "消息提示",  //标题
						width:400,
						message: "系统错误"     //提示语
					  });
			  }
			  
		 }		  
	});
}
function updatekaihuState(flag,state,url){
	var brokerId =$("#brokerId_bind").val();
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	}
	$.ajax({
		type : "get",
		url :"/stock/updatekaihuState.jspa",
		data :{
			"brokerId" : brokerId,
			"state" : state
		  },
		dataType : "json",
		timeout : 5000,
		async:false,
		cache : false,
		success:function(data){
			  if(data &&data !=undefined){
				  if(data.error && data.error ==-1){
					  JRJ.Alerts.alert({
							title: "消息提示",  //标题
							width:400,
							message: "请选择券商"     //提示语
						  });
				  }else if(data.error && data.error !=-1){
					  JRJ.Alerts.alert({
							title: "消息提示",  //标题
							width:400,
							message: data.error     //提示语
						  });
				  }else{
					  if(flag == "rekaihu"){
						  window.open(data.kaihuUrl,"ZHKH");
						  window.location.reload(); 
					  }else if(flag == 'reload' && typeof url != 'undefined'){
						  window.location.href = url;
					  }else{
						  window.location.reload();
					  }
				  }
			  }else{
				  JRJ.Alerts.alert({
						title: "消息提示",  //标题
						width:400,
						message: "系统错误"     //提示语
					  });
			  }
			  
		 }		  
	});
}

function isIE(){
		return ("ActiveXObject" in window);
}
function openkaihu(){
	$(".msg-block").hide();
	window.open("https://kh.cs.ecitic.com/webtrade/zxkh/login.jsp?promote_type=branch&promote_code=ODMy&promote_wbjg=1002","ZHKH");	
}

