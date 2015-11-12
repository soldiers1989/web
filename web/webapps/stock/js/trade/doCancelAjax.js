var _CancelDlg = jQuery.extend(true,{}, JRJ.Dialogs);
function confirmCancel(data){
	var stockCode=data.stockCode,
	stockName=data.stockName,
	direction=data.direction,
	price=data.price,
	amount=data.amount,
	entrustNo=data.entrustNo;
	var priceUnit="元/股";
	var amountUnit="股";
	if(data.stype!=undefined&&data.stype==7){
		priceUnit="";
		amountUnit="张";
	}
	_CancelDlg.standardDialog({
        title: "确认撤单",
        width:350,
        content: ['<div class="dialog-cnt">',
                        '<table class="table-p5">',
                            '<tr>',
                                '<td style="width:70px;">委托股票</td>',
                                '<td>'+stockName+'('+stockCode+')</td>',
                            '</tr>',
                            '<tr>',
                                '<td>委托方向</td>',
                                '<td>'+direction+'</td>',
                            '</tr>',
                            '<tr>',
	                            '<td>委托价格</td>',
	                            '<td>'+formatNumber(price,2,"0.00")+priceUnit+'</td>',
	                        '</tr>',
                            '<tr>',
                                '<td>委托数量</td>',
                                '<td>'+formatNumber(amount,0,"0")+amountUnit+'</td>',
                            '</tr>',
                            '<tr>',
                                '<td class="field">交易密码</td>',
                                '<td>',
								'<div id="inputtext"></div>',
								'<div class="error-msg"></div>',
								'</td>',
                            '</tr>',
                        '</table>',
                  '</div>'].join(''),
        okBtnText: "确认",
        cancelBtnText: "取消",
        hasCloseBtn: true,
        hasOkBtn: true,
        hasCancelBtn: true,
        okCallback: function () { 
        	//alert(entrustNo);
        	var password;
        	var userMac;
        	var url="/stock/"+_accountId+"/cancelStep4.jspa";
        	jrjpassword.check(function(data){
        		if(data == 0){//检查成功
        			password=jrjpassword.value();
        			userMac=jrjpassword.mac();
        			doCancelAjax(url,stockName,password,userMac,entrustNo,jrjpassword._disk,jrjpassword._nic);
        		}else if(data == -100){
					showError('inputtext','密码不能为空');
					//JRJ.Dialogs.close();
				}else if(data == -99){
					showError('inputtext','为了您的交易安全，请安装安全控件');
				}else{
					showError('inputtext','您输入的密码有误');
				}
        	});
        	return true;
        },
        cancelCallback: function () {
			jrjpassword.destory(); 
		},
        isFixed: true
    });
    //调用密码输入框安全插件    inputtext  密码框域id
    jrjpassword=new JRJZQTPassword("#inputtext",_accountId,JRJZQTPassword.Action.CANCEL); 
}
function doCancelAjax(url,stockName,password,userMac,entrustNo,disk,nic){
	$.ajax({
		type : "POST",
		url : url,
		data:{
			"stockName" :stockName,
			"password" :password,
			"type":jrjpassword.type,
			"local_network":userMac,
			"local_disk":disk,
			"local_cpu":nic,
			"entrustNo":entrustNo,
			"xjb.token.name":getTokenName(),
			"xjb.token":getToken()
		},
		dataType : "json",
	    timeout : 5000,
		cache : false,
		success:function(data){
			resetToken(data.token);
			if(typeof data.mutiple_request_error != 'undefined'){
				window.location.reload();
			}else if("-406"==data.errCode){
				showTradePwdDlg(_accountId);
				return;
			}else if(typeof data.errMsg != 'undefined'){
				  //撤单失败
				  showError('inputtext',"撤单失败，"+data.errMsg);
			}else{
				  //撤单成功
				  showSuccessMsg('撤单委托已提交，','查询撤单状态？',function(){
					window.location.href='/stock/'+_accountId+'/entrust.jspa';  
					});
				  _CancelDlg.close();
				if(jrjpassword!=null){
					jrjpassword.destory(); 
				}
				if(typeof getContent != 'undefined' && $.isFunction(getContent) && typeof nextUrl != 'undefined'){
					setTimeout(function(){getContent(nextUrl,1,'today');},300);
				}
			  }				  
		 }
	});
}