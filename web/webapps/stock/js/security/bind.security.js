/**
 * 
 */
var urls={
          bindstep1 = "",
          step2 = "";
};
$(document).ready(function(){
	$("#nextBtn").click(function(){
		//var brokerId = $("#txtChannelId_hide").val();
		var securityId = $("input:radio[name='radio_channel'][checked]").val();
	}
	$.ajax({
		type : "post",
		url : XJB.Urls.checkMobileRegist,
		data :{
			"mobile" : mobile
			  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success : function(data, textStatus) {
			if (typeof(data) != 'undefined' && data != null && data != 'null') {
				if(data.retcode == 0){
					if (!data.msg) {
						hideErr("#mobile",MB_MSG.M05 + data.retcode);
						VALID.MB=true;
						enableInput();
					}else{
						showErr("#mobile",MB_MSG.M03);
						VALID.MB='EXIST';//手机号存在，当点击获取验证码时显示。old:VALID.MB=false;
						disableInput();
					}
				}else{
					showErr("#mobile",MB_MSG.M04);
					VALID.MB=false;
				}
			}else{
				showErr("#mobile",MB_MSG.M04);
				VALID.MB=false;
			}
		},
		error:function(xhr, status, error){
			console.log(xhr);
		}
	});
	)
})