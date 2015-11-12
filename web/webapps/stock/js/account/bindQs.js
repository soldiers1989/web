/**
 * 
 */
$(document).ready(function(){
	
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
})

