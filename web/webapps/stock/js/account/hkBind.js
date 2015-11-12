//判断是A港还是港股
function getBorkerType() {
	var brokerId = $("#brokerId").val();
	// 港股
	if (brokerId == "HKZXGJ"||brokerId == "HKCITIC") {
		return 2;
	} else {// A股
		return 1;
	}
	
}
// 完善信息后继续以前的操作，不用用户再点一次了
function stepUserinfoto() {
	// 关闭完善信息窗口
	JRJ.Dialogs.close();
	stepUserinfoNext();
}
//点击按钮，如果完善信息后弹出相应的窗口
function stepUserinfoNext() {
	
	var retType = getBorkerType();
	// 关闭完善信息窗口后执行后续逻辑
	if (retType == 2) {
		// 港股
		stepUserinfotoHk();
	} else {
		// A股
		stepUserinfotoA();
	}
}
// 完善信息后继续以前的操作，不用用户再点一次了
function stepUserinfotoA() {
	var brokerId = $("#brokerId").val();
	// 开户或转户过来的
	if (source == "khorzh") {// 开户转户
		// 判断只有中山和中信弹二维码提示，其它券商直接开户
		if (brokerId == "ZXZQ" || brokerId == "ZSZQ" || brokerId == "HTZQ" || brokerId == "CJZQ" 
			|| brokerId == "CCZQ"|| brokerId == "HAITZQ" || brokerId=="PAZQ") {
//			videcode(type, brokerId);
			showMobileTip(type, brokerId);
		} else {
			kaihu(type);
		}
	} else if (source == "bindQs") {
		if(btype=='0'){
			goITNbind();
		}else if(btype=='1'){
			goZX();
		}
	} else if (source == "bindOrders") {//绑定其它人
		openBindOrders();
	}
}
//判断绑户流程 //0:int流程   1:自有券商流程    2:已绑定  3:未知错误
function checkBindType(){
  	var brokerId = $("#brokerId").val();
  	var rbindType
  	var bindType;
  	if(getBorkerType()==2){//港股
  		btype="1";
  		return true;
  	}
  	$.ajax({
  		type : "get",
  		url :"/stock/checkBindType.jspa",
  		data :{
  			"brokerId" : brokerId
  		  },
  		dataType : "json",
  		timeout : 5000,
  		async:false,
  		cache : false,
  		success:function(data){
  			  if(data != undefined){
  				bindType = data; 
  			  }				  
  		 }		  
  	});
  	var idBindOld =$("#idBindOld").val();
	if(bindType=='0'){
   	  	var aa=brokerId.substring(0,4);
		if(aa!="ITN_"){//处理brokerid不一致问题
			brokerId="ITN_"+brokerId;
			var aa='1';
			$.each(jsonbrokerlist.brokerlist, function(index, content){
				if(brokerId==content.brokerId){
					aa='0';
				}
			});
			if(aa=='1'){
				if(idBindOld=="0"){
					 rbindType="1";
				}else{
					//重新刷新页面
			    	JRJ.Alerts.alert({
						title: "消息提示", 
						width:400,
						message: "此券商暂不支持老用户绑定！" ,
						autoClose:false
					  });
			    	return false;
				}
			}else{
				rbindType="0";
			}
		}else{
			rbindType="0";
		}
	}else if(bindType=='1'){
		rbindType="1";
	}else if(bindType=='2'){
		JRJ.Alerts.alert({
			title: "消息提示", 
			width:400,
			autoClose:false,
			message: "已绑定券商不能重复绑定！" 
		  });
		return false;
	}else if(bindType=='3'){
		JRJ.Alerts.alert({
			title: "消息提示",
			width:400,
			autoClose:false,
			message: "未知错误，请联系客服！"
		  });
		return false;
	}else{
		JRJ.Alerts.alert({
			title: "消息提示",
			width:400,
			autoClose:false,
			message: "未知错误，请联系客服！"
		  });
		return false;
	}
	btype=rbindType;
  	return true;
}
function stepUserinfotoHk() {
	var brokerId = $("#brokerId").val();
	//开户或转户过来的
	if (source == "khorzh") {//开户转户
		kaihu(type);
	} else if (source == "bindQs") {
		//goZX();
		hkBind();
	}
}
//港股弹出绑定页面
function hkBind(){
	var brokerId =$("#brokerId").val();
	if(brokerId==""){
		 JRJ.Alerts.alert({
				title: "消息提示",  //标题
				width:400,
				message: "请选择券商"     //提示语
			  });
		return ;
	}
	optionTracking(brokerId,'3');
	initHkBind();
}
//绑定页面
function initHkBind(){
	var brokerId = $("#brokerId").val();
	JRJ.Dialogs.iframeDialog({
        content: [''].join(''),
        loadingImg: '',
        width:470,
        hasBtn: false,
        hasOkBtn: false,
        okBtnText: '',
        title: '绑定证券通',
        titleRight: '',
        bottomContent: '',
        protocolHtml: '',
        hasCancelBtn: false,
        enableKeyCtrl: true,

        ifrSrc: '/stock/hk/hkBind.jspa?brokerId='+brokerId,
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
//检查账户编号
function checkAccountNo(){
	var accountNo = $("#accountNo").val().trim();
	if(accountNo == ""){
		$("#errorMsg").html("账户编号不能为空！");
		return false;
	}
	return true;
}
//检查账户名称
function checkAccountName(){
	/*var accountName = $("#accountName").val().trim();
	if(accountName == ""){
		$("#errorMsg").html("账户名称不能为空！");
		return false;
	}*/
	return true;
}
//检查旧密码
function checkOldPass(){
	var oldPass = $("#oldPass").val().trim();
	if(oldPass == ""){
		$("#errorMsg").html("旧密码不能为空！");
		return false;
	}
	return true;
}
//检查新密码
function checkNewPass(){
	var newPass = $("#newPass").val().trim();
	if(newPass == ""){
		$("#errorMsg").html("新密码不能为空！");
		return false;
	}
	return true;
}
//检查重复新密码
function checkRenewPass(){
	var reNewPass = $("#reNewPass").val().trim();
	if(reNewPass == ""){
		$("#errorMsg").html("重复新密码不能为空！");
		return false;
	}
	return true;
}
//检查重复密码是否一至
function checkPasswordIsSame(){
	var newPass = $("#newPass").val().trim();
	var reNewPass = $("#reNewPass").val().trim();
	if(newPass != reNewPass){
		$("#errorMsg").html("两次输入的新密码不一致！");
		return false;
	}
	return true;
}
//港股绑定流程
function flFoZxHk(brokerId,count){
	  $("#brokerId").val(brokerId);
	  if(!$("#agree"+count).attr("checked")){
		JRJ.Alerts.alert({
			title: "消息提示",  //标题
			width:400,
			autoClose:false,
			message: "请先认真阅读相关协议"     //提示语
		  });
		return false;
	  }
	  goHkZX();
}
function goHkZX(){
		var brokerId =$("#brokerId").val();
		if(brokerId==""){
			 JRJ.Alerts.alert({
					title: "消息提示",  //标题
					width:400,
					message: "请选择券商"     //提示语
				  });
			return ;
		}
		//changeHkPass();
		hkBind();
	}
//修改密码页面
function changeHkPass(brokerId){
	//var brokerId = $("#brokerId").val();
	JRJ.Dialogs.iframeDialog({
        content: [''].join(''),
        loadingImg: '',
        width:470,
        hasBtn: false,
        hasOkBtn: false,
        okBtnText: '',
        title: '修改初始密码',
        titleRight: '',
        bottomContent: '',
        protocolHtml: '',
        hasCancelBtn: false,
        enableKeyCtrl: true,

        ifrSrc: '/stock/hk/hkChangePass.jspa?brokerId='+brokerId,
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
//港股修改密码
function doChangehkPass(){
	var accountNo = $("#accountNo").val();
	var accountName = $("#accountName").val();
	var oldPass = $("#oldPass").val();
	var newPass = $("#newPass").val();
	var brokerId = $("#brokerId").val();
	//验证账户编号
	if(!checkAccountNo()){
		return false;
	}
	//验证账户名称
	if(!checkAccountName()){
		return false;
	}
	//检查旧密码
	if(!checkOldPass()){
		return false;
	}
	//检查新密码
	if(!checkNewPass()){
		return false;
	}
	//检查重复新密码
	if(!checkRenewPass()){
		return false;
	}
	//检查两次密码是否一至
	if(!checkPasswordIsSame()){
		return false;
	}
	document.getElementById("changePassForm").submit()
}
function clearHkChangePass(accountNo,accountName){
	if(accountNo == null || accountNo == ""){
		$("#accountNo").val("");
	}
    if(accountName == null || accountName == ""){
    	$("#accountName").val("");
	}
    $("#oldPass").val("");
    $("#newPass").val("");
    $("#reNewPass").val("");
    $("#errorMsg").html("");
}
function closeHkWin(){
	frameElement._thisDialog.close();
}
//港股修改密码
function ajaxhkBind(){
	var accountNo = $("#accountNo").val();
	var password = $("#password").val();
	//验证账户编号
	if(!checkAccountNo()){
		return false;
	}
	//验证密码
	if(!checkPassword()){
		return false;
	}
	document.getElementById("bindForm").submit()
}
//检查重复新密码
function checkPassword(){
	var password = $("#password").val().trim();
	if(password == ""){
		$("#errorMsg").html("密码不能为空！");
		return false;
	}
	return true;
}
