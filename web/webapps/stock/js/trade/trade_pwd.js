function showTradePwdDlg(_accountId,callback,cancelCallBack){
	var TPWDlg=jQuery.extend(true,{}, JRJ.Dialogs);
	TPWDlg.standardDialog({
        title: "请输入交易密码",
        width: 450,
            content: [
                '<div class="dialog-cnt clearfix">',
                    '<table class="table-form">',
                        '<tr>',
                            '<td class="field" style="width:60px;">交易密码<br/><span style="margin-top: 30px;display: none;" id="txPwdSpan">通讯密码</span></td>',
                            '<td class="value">',
                                '<div id="trade_pwd_div"></div>',
                                '<div id="tradepwd_msg" name="tradepwd_msg" class="error-msg"></div>',
                            '</td>',
                        '</tr>',
                    '</table>',
                '</div>',
            ].join(''),
        okBtnText: "确认",
        cancelBtnText: "取消",
        hasCloseBtn: true,
        hasBtn: true,
        hasOkBtn: true,
        hasCancelBtn: true,
        okCallback: function () {_checkTradePwd();return true;},
        cancelCallback: function () {
        	if(cancelCallBack){cancelCallBack();}
        },
        isFixed: true
    });
	var __tradePWD = new JRJZQTPassword('#trade_pwd_div',_accountId,JRJZQTPassword.Action.INIT,null,null,null,function(data){
		if(data!=undefined&&data.txpwd!=undefined){
			if(data.txpwd==1){
				$("#txPwdSpan").css("display","block");
				}
		}
	});
	function _checkTradePwd(){
		__tradePWD.check(function(data,errorinfo){
			if(data==0){
				password = __tradePWD.value();
				txpwd=__tradePWD.value("txPassword");
	        	$.ajax({
	        		type : "post",
	        		url : "/stock/"+_accountId+"/initQsPwdResult.jspa",
	        		data:{
	        			"password":password,
	        			"txPassword":txpwd,
	        			"accountid":_accountId,
	        			"local_network":__tradePWD._mac,
	        			"local_disk":__tradePWD._disk,
	        			"local_cpu":__tradePWD._nic
	        		},
	        		dataType : "json",
	        		timeout : 5000,
	        		async:false,
	        		cache : false,
	        		success : function(data){
	        			  if(data.errMsg){
	        				   $("#tradepwd_msg").html(data.errMsg).show();
	        			  }else{
	        				  if(callback){callback();}
	        				  TPWDlg.close();
	        			  }				  
	        		 }
	        	});
			}else if(data == -100){
				$("#tradepwd_msg").html("交易密码不能为空!").show();
			}else if(data == -99){
				$("#tradepwd_msg").html('为了您的交易安全，请安装安全控件');
			}else if(data == -103){
				$("#tradepwd_msg").html("通讯密码不能为空!").show();
			}else{
		        	if(errorinfo!=undefined){
		        		$("#tradepwd_msg").empty().text(errorinfo).show();
		        	}
			}
		});
	}
}
