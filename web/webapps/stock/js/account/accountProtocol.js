function dlgProtocol(brokerId,count){
	$("#agree"+count).attr("checked","true");
	JRJ.Dialogs.iframeDialog({
        content: [''].join(''),
        loadingImg: '',
        width: 712,
        hasBtn: false,
        hasOkBtn: false,
        okBtnText: '',
        title: '用户协议',
        titleRight: '',
        bottomContent: '',
        protocolHtml: '',
        hasCancelBtn: false,
        enableKeyCtrl: true,

        ifrSrc: '/stock/dlgProtocol.jspa?brokerId='+brokerId,
        ifrReHeight: true,
        isFixed: true,//defaultCf.isFixed,
        okCallback: function () {
            defaultCf.okCallback();
        },
        cancelCallback: function () {

            return true;
        }
    });
}