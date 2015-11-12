//打开邀请码页面
function openYqCode(){
	//邀请码获取
	var tgyqcode = getPara('tgyqcode');
            	JRJ.Dialogs.standardDialog({
                    title: "输入邀请码",
                    width: 450,
                        content: [
                            '<div class="dialog-cnt clearfix">',
                                '<table class="table-form">',
                                    '<tr>',
                                        '<td class="field" style="width:60px;">邀请码</td>',
                                        '<td class="value">',
                                            '<div><input id="invitecode" name="invitecode" type="text" class="txtbox txtbox-2" style="width:200px;" maxlength="11"/></div>',
                                            '<div id="invitecode_msg" name="invitecode_msg" class="error-msg">（可选）</div>',
                                        '</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td class="field" style="width:60px;">&nbsp;</td>',
                                        '<td class="value">',
                                            '<div><a class="btn btn-123-40 mr10" style="width:100px;float:left;" onclick="addYqCode();">确定</a><a class="btn btn-123-40 mr20" style="width:100px;float:left;" onclick="javascript:stepUserinfoNext();">跳过</a></div>',
                                        '</td>',
                                    '</tr>',
                                '</table>',
                            '</div>',
                        ].join(''),
                    okBtnText: "",
                    cancelBtnText: "取消",
                    hasCloseBtn: true,
                    hasBtn: false,
                    hasOkBtn: false,
                    hasCancelBtn: false,
                    okCallback: function () { },
                    cancelCallback: function () {
                    },
                    isFixed: true
                });
            	//邀请码初始化
            	if(tgyqcode != ""){
            		$("#invitecode").val(tgyqcode);
                	$("#invitecode").attr("disabled",true);
            	}
}
//添加邀请码
function addYqCode(){
	var retType = getBorkerType();
    var invitecode = $("#invitecode").val().trim();
    if(invitecode == ""){
    	$("#invitecode_msg").html("请添写验证码!");
    	$("#invitecode_msg").addClass("error-msg");
		return false;
    }
    $.ajax({
	    type : "post",
		url : "/stock/ajaxInviteCode.jspa",
		data :{
			"retType" : retType,
			"invitecode" : invitecode
		},
		dataType : "json",
		timeout : 5000,
		async:false,
		cache : false,
		success:function(data){
		    if(data.retcode == 0){
		    	stepUserinfoto();
			}else if(data.retcode == -1){
			    $("#invitecode_msg").html(data.msg);
			    $("#invitecode_msg").addClass("error-msg");
			}		  
		}		  
	});
   
}