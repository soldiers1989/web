var type = "";
var source = "";
var btype= "1";
$(function () {
	
	  $('.icon-tooltip').tooltipster({
          theme: 'tooltipster-light'
      });
      $('.wblock input').click(function(event){
           if($(this).attr("checked")){
               $(this).attr("checked",'true');     
             }else{
               $(this).removeAttr("checked");            
             }
             event.stopPropagation();
      });
      $('.wblock').click(function(){
          $(this).parent().parent('.stock-comp-popup').show();
          return false;
      });
	//move-handler
	$('.my-account-item').mouseenter(function () {
		$(this).addClass('my-account-item-hover');
	}).mouseleave(function () {
		$(this).removeClass('my-account-item-hover');
	})
	var isAnimated = false;
	$('.move-handler i').mouseenter(function () {
		$(this).addClass($(this).data('hover'));
	}).mouseleave(function () {
		$(this).removeClass($(this).data('hover'));
	}).click(function () {
		if (!isAnimated) {
			var self = $(this),
				item = self.parents('.my-account-item:first');
			function move(objUp,upOffset,objDown,downOffset) {
				objUp.animate({ 'top': '-=' + upOffset }, 300, function () { });
				
				objDown.animate({ 'top': '+=' + downOffset }, 300, function () {
					objDown.find('.my-account-item-left i').remove();
					setTimeout(function () {
						objUp.insertBefore(objDown);
						objDown.css({ top: 0 });
						objUp.css({ top: 0 });
						isAnimated = false;
						if(objUp.prev('.my-account-item').length == 0){
							objUp.find('.my-account-item-left').append('<i></i>'); 
						}
						ajaxChangeSort();
					}, 300);
				});
			}
			function onMove(item, direction){
				//console.log(item.data('id')+'-->'+direction);
			}
			switch (self.data('direction')) {
				case 'up':
					if (item.prev('.my-account-item').length > 0) {
						var prevItem = item.prev(),
							moveUpHeight = prevItem.outerHeight() + parseInt(prevItem.css('margin-bottom').replace('px', ''), 10),
							moveDownHeight = item.outerHeight() + parseInt(item.css('margin-bottom').replace('px', ''), 10);

						isAnimated = true;
						move(item, moveUpHeight, prevItem, moveDownHeight);
						onMove(item,'up');
					}
					break;
				case 'down':
					if (item.next('.my-account-item').length > 0) {
						var nextItem = item.next(),
							moveUpHeight = nextItem.outerHeight() + parseInt(nextItem.css('margin-bottom').replace('px', ''), 10),
							moveDownHeight = item.outerHeight() + parseInt(item.css('margin-bottom').replace('px', ''), 10);

						isAnimated = true;
						move(nextItem, moveDownHeight, item, moveUpHeight);
						onMove(item,'down');
					}
					break;
				default:
					break;
			}
		}
	});
	//move-handler

    $('.my-account-item-right .times').click(function(){
        var seconds =30;
         var timer=null;
         var Tthis=$(this);
         var id = Tthis.attr('id');
         
//         Tthis.addClass('timess');      
//         timer = setInterval(CountDowns,1000); 
//         CountDowns();
//        function CountDowns(){  
//                    if(seconds>0){   
//                       Tthis.text(seconds);     
//                        seconds--;
//                        Tthis.attr('disabled','disabled'); 
//                    }   
//                    else{   
//                       clearInterval(timer); 
//                        Tthis.removeClass('timess').text("点击刷新"); 
//                        Tthis.removeAttr('disabled');    
//                    }   
//            }
        ajaxRefreshBindStatus(id);
    });
            ///////
    $('.stock-comp-list li').mousedown(function(e){
        if(3==e.which){return false;}
     });
     $('.stock-comp-list li:nth-child(4n)').css('margin-right','0px');
     $('.con_2 .stock-comp-list li').click(function(){
    	 setBrokerInfo($(this).attr("broker"));
    		 if ($(this).hasClass('cur')) {
                 $('.stock-comp-list li').css('marginTop','0px');
                 $('.stock-comp-list li').removeClass('cur');
                 $(".stock-comp-popup").parents('.con_3').next().css('marginTop',0);
                 $('.stock-comp-popup').hide();
                 var num=parseInt($(this).index()/4);
                 var sum;
                 
     		     if(($(this).siblings().length+1)%4==0){
     				 sum=parseInt(($(this).siblings().length+1)/4)-1;
     			}else{
     				 sum=parseInt(($(this).siblings().length+1)/4);
     			}
                  if(num==sum){
                 	 $(this).parents('.con_2').next().css('marginTop','0px');
                   }
             } else{
            	 $('.stock-comp-list li').css('marginTop','0px');
                 $('.stock-comp-list li').removeClass('cur');
                 $(".stock-comp-popup").parents('.con_3').next().css('marginTop',0);
                 $('.stock-comp-popup').hide();
                 $(this).addClass('cur');
                 var num=parseInt($(this).index()/4);
                 $(this).parent().parent().next('.con_2 .stock-comp-popup').css('top',(104+95*num)+'px').show();
                 $('.con_2 .stock-comp-list li').slice((num+1)*4,(num+2)*4).css('marginTop',460);
                 var sum;
	     		 if(($(this).siblings().length+1)%4==0){
	     			sum=parseInt(($(this).siblings().length+1)/4)-1;
	     		 }else{
	     				 sum=parseInt(($(this).siblings().length+1)/4);
	     			  }
	             if(num==sum){
	             	 $(this).parents('.con_2').next().css('marginTop','460px');
	             }
	             if(num%2==1){
	             }else{
	            	  $('#wcon_2 .con_2').next('p.blue').css('marginTop','auto');
	                  }
             }
         
         $("#idBindOld").val($(this).attr("isbind"));
         $("#brokerId").val($(this).attr("broker"));
         $("#isTxPwd").val($(this).data("txpwd"));
         return false;
     });
     $('.con_3 .stock-comp-list li').click(function(){
         if ($(this).hasClass('cur')) {
             $('.stock-comp-list li').css('marginTop','0px');
             $('.stock-comp-list li').removeClass('cur');
             $(".stock-comp-popup").hide().parents('.con_2').next().css('marginTop',0);
             $(".stock-comp-popup").hide().parents('.con_3').next().css('marginTop',0);
             $('.stock-comp-popup').hide();

         } else  {
             $('.stock-comp-list li').css('marginTop','0px');
             $('.stock-comp-list li').removeClass('cur');
             $(".stock-comp-popup").hide().parents('.con_2').next().css('marginTop',0);
             $('.stock-comp-popup').hide();
             $(this).addClass('cur');
             var num=parseInt($(this).index()/4);
             $(this).parent().parent().next('.con_3 .stock-comp-popup').css('top',(104+95*num)+'px').show();
             if($(this).siblings.length>(num+1)*4){
                  $('.con_3 .stock-comp-list li').slice((num+1)*4,(num+2)*4).css('marginTop',$('.con_3 .stock-comp-popup').height()+10);
             } else{
                 	$(this).parents('.con_3').next().css('marginTop',$('.con_3 .stock-comp-popup').height()+10); 
                   }
         }
         $("#idBindOld").val($(this).attr("isbind"));
         $("#brokerId").val($(this).attr("broker"));
         $("#isTxPwd").val($(this).data("txpwd"));
         setBrokerInfo($(this).attr("broker"));
          return false;
         
     });
     $('#wcon_3 #open_account .stock-comp-list li').click(function(){
         if($(this).hasClass('cur')){
             $('.stock-comp-popup').hide();
             $('.stock-comp-list li').css('marginTop','0px');
             $('.stock-comp-list li').removeClass('cur');

             var num=parseInt($(this).index()/4);
             var sum;
		     if(($(this).siblings().length+1)%4==0){
				 sum=parseInt(($(this).siblings().length+1)/4)-1;
			 }else{
				 	sum=parseInt(($(this).siblings().length+1)/4);
			      }
             if(num==sum){
            	 $('#wcon_3 .stock-comp-popup').next('div').css('marginTop','30px');
             }
         } else {
             $('.stock-comp-popup').hide();
             $('.stock-comp-list li').css('marginTop','0px');
             $('.stock-comp-list li').removeClass('cur');
             $(this).addClass('cur');
             var num=parseInt($(this).index()/4);
             $(this).parent().parent().next('#wcon_3 .stock-comp-popup').css('top',(225+95*num)+'px').css('left','20px').show();
             $('#wcon_3 .stock-comp-list li').slice((num+1)*4,(num+2)*4).css('marginTop',$('#wcon_3 .stock-comp-popup').height()+10);
             $('#wcon_3 .con_4 .stock-comp-list li').css('marginTop',0);
             var sum;
            
		     if(($(this).siblings().length+1)%4==0){
				 sum=parseInt(($(this).siblings().length+1)/4)-1;
			}else{
				 sum=parseInt(($(this).siblings().length+1)/4);
			}
             if(num==sum){
            	 $('#wcon_3 .stock-comp-popup').next('div').css('marginTop',$('#wcon_3 .stock-comp-popup').height()+10);
              }
         }
         $("#agreeint").attr("checked",false);
         $("#idBindOld").val($(this).attr("isbind"));
         $("#brokerId").val($(this).attr("broker"));
         $("#isTxPwd").val($(this).data("txpwd"));
         setBrokerInfo($(this).attr("broker"));
          return false;
         
     });
     $('.con_4 .stock-comp-list li').click(function(){
         if ($(this).hasClass('cur')) {
             $('.stock-comp-list li').css('marginTop','0px');
             $('.stock-comp-list li').removeClass('cur');
             $(".stock-comp-popup").hide().parents('.con_2').next().css('marginTop',0);
             $(".stock-comp-popup").hide().parents('.con_4').next().css('marginTop',0);
             $('.stock-comp-popup').hide();
             var num=parseInt($(this).index()/4);
            // $(this).parent().parent().next('.con_4 .stock-comp-popup').css({'top':(104+95*num)+'px','left':'0px'}).show();
             var sum;
		     if(($(this).siblings().length+1)%4==0){
				 sum=parseInt(($(this).siblings().length+1)/4)-1;
		     }else{
				 	sum=parseInt(($(this).siblings().length+1)/4);
		     	  }
             if(num==sum){
            	 $(this).parents('.con_4').next().css('marginTop','30px');
              }

         } else  {
             $('.stock-comp-list li').css('marginTop','0px');
             $('.stock-comp-list li').removeClass('cur');
             $(".stock-comp-popup").hide().parents('.con_2').next().css('marginTop',0);
             $('.stock-comp-popup').hide();
             $(this).addClass('cur');
             var num=parseInt($(this).index()/4);
             $(this).parent().parent().next('.con_4 .stock-comp-popup').css({'top':(104+95*num)+'px','left':'0px'}).show();
             $('.con_4 .stock-comp-list li').slice((num+1)*4,(num+2)*4).css('marginTop',$('.con_4 .stock-comp-popup').height()+10);
             var sum;
             
		     if(($(this).siblings().length+1)%4==0){
				 sum=parseInt(($(this).siblings().length+1)/4)-1;
			 }else{
				 	sum=parseInt(($(this).siblings().length+1)/4);
			      }
             if(num==sum){
            	 $(this).parents('.con_4').next().css('marginTop',$('.con_4 .stock-comp-popup').height()+10);
              }   
         }
         
         $("#agreeint2").attr("checked",false);
         $("#idBindOld").val($(this).attr("isbind"));
         $("#brokerId").val($(this).attr("broker"));
         $("#isTxPwd").val($(this).data("txpwd"));
         setBrokerInfo($(this).attr("broker"));
          return false;
         
     });
     
     
/*     $('.stock-comp-popup').click(function(){
          return false;
     });*/
     $('.stock-comp-popup').mousedown(function(e){
        if(3==e.which){
             $(this).show();                   
              return false;
        }
     });
     //点击其他区域，关闭开转户浮层
     $(".site-main-right").bind("click",function(e){
         var target  = $(e.target);
         if(target.closest(".stock-comp-popup").length == 0){
          $(".stock-comp-popup").hide().parents('.con_3').next().css('marginTop',0);
          $(".stock-comp-popup").hide().parents('.con_2').next().css('marginTop',0);
          $('.stock-comp-list li').removeClass('cur');
          $('.stock-comp-list li').css('marginTop',0);
          $(".stock-comp-popup").hide().next('#bindzh').css('marginTop','30px');
          $(".stock-comp-popup").hide().parents('.con_4').next().css('marginTop','30px');
         } 
         
       });
	
            //弹出添加账户窗口
            $('.bindOrderUser').click(function () {
            	type = "";
            	source = "bindOrders";
            	getUserInfoIsPerfect("","bindOrders");
            });
            //开户按钮事件
            $('.kaihu_div').click(function () {
            	type = "1";
            	source = "khorzh";
            	btype = "1";
            	getUserInfoIsPerfect("1","khorzh");
            });
            //转户按钮事件
           /* $('.zhuan_div').click(function () {
            	type = "2";
            	source = "khorzh";
            	btype = "1";
            	getUserInfoIsPerfect("2","khorzh");
            });*/
            //绑户按钮事件
            $('.btnbind_div').click(function () {
            	type = "3";
            	source = "bindQs";
            	if(checkBindType()){
            		getUserInfoIsPerfect("3","bindQs");
            	}
            });
            $(".ITN_btnbind_open").click(function(){
            	var brokerId=$(this).data("brokerid");
            	$("#wmenu_3").click();
            	$('#wcon_3 #open_account .stock-comp-list li[broker='+brokerId+']').click();
            })
            //ITN绑户按钮事件
            $('.ITN_btnbind_div').click(function () {
            	_dobind("agreeint")
            });
            function _dobind(agree){
            	if(!$("#"+agree).attr("checked")){
	       			JRJ.Alerts.alert({
	       				title: "消息提示",  //标题
	       				width:400,
	       				autoClose:false,
	       				message: "请先认真阅读相关协议"     //提示语
	       			  });
	       			return false;
	       		}
            	type = "3";
            	source = "bindQs";
            	if(checkBindType()){
            		getUserInfoIsPerfect("3","bindQs");
            	}
            }
			
			//获取参数中broker值，控制显示开户券商
			var b = getPara('broker');
			if(b != ''){
				setBrokerInfo(b);
				$('#wmenu_2').trigger("click");
				if($('ul.stock-comp-list li[broker="' + b + '"]:visible').length>0){
					$('ul.stock-comp-list li[broker="' + b + '"]:visible').trigger('click');
					//滑动到页面  开户  处
					var hh=$('ul.stock-comp-list li[broker="' + b + '"]').offset().top;
					$(document).scrollTop(hh-65);
				}
			   
			}
			//$('#div1').children('span').length;
			var j=$('.my-account-item').size();//获得标签的数目
			//alert(j);
			var gotopage =getPara('go');
			//alert(gotopage)
			if(gotopage=="kaihu"){
				gobindTob('2');
			}else if(gotopage=="bind"){
				gobindTob('3');
			}else{
				if(j==0){
					gobindTob('2');
				}
			}
        });
		//授权列表排序
		function ajaxInitAccountInfo(){
			var ids = $('input[name="accountIdAs"]');
			 for(var i = 0;i<ids.length;i++){
		         var accountId=ids[i].value;
		         $.ajax({
						type : "post",
						url :"/stock/ajaxInitAccountInfo.jspa",
						data :{
							"accountId" : accountId
						  },
						dataType : "json",
						timeout : 5000,
						async:false,
						cache : false,
						success:function(data){
							if(data.retcode == "0"){
								$('#'+accountId+'_trueName').html(data.trueName)
								$('#'+accountId+'_fundAccount').html(data.fundAccount)
								$('#'+accountId+'_shstockAccount').html(data.shstockAccount)
								$('#'+accountId+'_szstockAccount').html(data.szstockAccount)
							}
						}		  
				});
			}
		}
        //打开绑其它人账户窗口
        function openBindOrders(){
        	//添加账户
            JRJ.Dialogs.standardDialog({
                title: "添加账户",
                width: 423,
                content: [
                    '<div class="dialog-cnt clearfix">',
                        '<p class="f18 tc">绑定其他人账户</p>',
                        '<table class="table-form mt20">',
                            '<tr>',
                                '<td class="field" style="width:60px;">姓名</td>',
                                '<td class="value">',
                                    '<div><input id="realname" name="realname" type="text" class="txtbox txtbox-2" style="width:280px;" /></div>',
                                    '<div id="realname_msg" class="note f12">真实姓名只能是中文</div>',
                                '</td>',
                            '</tr>',
                            '<tr>',
                                '<td class="field">身份证号</td>',
                                '<td class="value">',
                                    '<div><input id="idnumber" name="idnumber" type="text" class="txtbox txtbox-2" style="width:280px;" /></div>',
                                    '<div id="idnumber_msg" class="note f12">身份证号只能是18位</div>',
                                '</td>',
                            '</tr>',
                        '</table>',
                        '<div class="mt30 tc"><a onclick="submitAuthorize()" class="btn btn-123-40">确认提交</a></div>',
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
        }
      //打开完善信息窗口
        function openuserinfo(type,brokerId){
        	//邀请码获取
        	var tgyqcode = getPara('tgyqcode');
        	 //完善个人信息
            JRJ.Dialogs.standardDialog({
                title: "完善个人信息&nbsp;<font class='f14'>(确保内容的真实性和准确性)</font>",
                width: 423,
                content: [
                          '<div class="dialog-cnt clearfix">',
                              '<table class="table-form">',
                                  '<tr id="trrealname">',
                                      '<td class="field" style="width:60px;padding:0 8px">姓名</td>',
                                      '<td class="value" style="position:relative;padding:0">',
                                          '<div><input id="realname" name="realname" type="text" class="txtbox txtbox-2" style="width:280px;" onblur="checkRealName();"/></div>',
                                          '<p class="info_tip" style="padding:0" id="realname_msg">请填写 <em> 本人 </em> 真实姓名</p>',                                  
                                      '</td>',
                                  '</tr>',
                                  '<tr id="tridnumber">',
                                      '<td class="field" style="padding:0 8px">身份证号</td>',
                                      '<td class="value" style="position:relative;padding:0">',
                                          '<div><input id="idnumber" name="idnumber" type="text" class="txtbox txtbox-2" style="width:280px;" onblur="checkIdNumber();"/></div>',
                                          '<p class="info_tip" id="idnumber_msg" style="padding:0">有效期内 <em> 本人 </em> 二代身份证</p>',
                                          '<span class="info_poptip" style="width:297px;right:8px">请填写本人身份证号，以便顺利完成开户<i></i></span>',
                                      '</td>',
                                  '</tr>',
                                  '<tr>',
                                      '<td class="field" style="width:60px;padding:0 8px">手机号</td>',
                                      '<td class="value" style="position:relative;padding:0">',
                                          '<div class="middle">',
                                          '<input id="mobile" name="mobile" type="text" class="txtbox" style="width:172px;" value="" placeholder="手机号" onblur="checkMobile();"/><em class="required">*</em>',
                                          '<a href="javascript:void(0)" id="getValidateCode" onclick="getIdentifyingCode();" class="btn btn-getcode <!--btn-getcode-disable--> ml5">获取验证码</a>',
                                          '<a href="http://i.jrj.com.cn/main/personalinfo.jspa" id="remobile" class="btn btn-getcode <!--btn-getcode-disable--> ml5" target="_blank" style="display:none">重置手机号</a>',
                                          '</div>',
                                          '<div id="mobile_msg" name="mobile_msg" class="error-msg" style="height:20px"></div>',
                                          '<span class="info_poptip" style="width:297px;right:8px">请填写本人手机号码，以便顺利完成开户<i></i></span>',
                                      '</td>',
                                  '</tr>',
                                  '<tr id="validcodeId">',
                                      '<td id="yzFile" class="field" style="padding:0 8px">验证码</td>',
                                      '<td class="value" style="padding:0">',
                                          '<div><input id="validcode" name="validcode" type="text" class="txtbox txtbox-2" style="width:100px;" /></div>',
                                          '<div id="validcode_msg" name="validcode_msg" class="error-msg" style="width:280px;height:20px;"></div>',
                                      '</div></td>',
                                  '</tr>',
                                  '<tr id="emailId">',
                                  '<td class="field"  style="padding:0 8px">邮箱</td>',
                                  '<td class="value"  style="padding:0">',
                                      '<div class="middle">',
                                      '<input id="email" name="email" type="text" class="txtbox" style="width:172px;" value="" placeholder="邮箱" onblur="checkEmail();"/><em class="required">*</em>',
                                      '<a href="javascript:void(0)" id="getEmailCode" onclick="getEmailsCode();" class="btn btn-getcode <!--btn-getcode-disable--> ml5">验证邮箱</a>',
                                      '</div>',
                                      '<div id="email_msg" name="email_msg" class="error-msg" style="height:20px"></div>',
                                  '</td>',
                              '</tr>',
                              '<tr id="emailcodeId">',
                                  '<td id="yzFile" class="field"  style="padding:0 8px">验证码</td>',
                                  '<td class="value"  style="padding:0">',
                                      '<div><input id="emailcode" name="emailcode" type="text" class="txtbox txtbox-2" style="width:100px;" /></div>',
                                      '<div id="emailcode_msg" name="emailcode_msg" class="error-msg" style="width:280px;height:20px;"></div>',
                                  '</div></td>',
                              '</tr>',
                              '<tr id="inviteId">',
                                  '<td id="inviteFile" class="field"  style="padding:0 8px">邀请码</td>',
                                  '<td class="value"  style="padding:0">',
                                      '<div><input id="invitecode" name="invitecode" type="text" class="txtbox txtbox-2" style="width:100px;" maxlength="11"/>(可选)</div>',
                                      '<div id="invitecode_msg" name="invitecode_msg" class="error-msg" style="width:280px;height:20px;"></div>',
                                  '</div></td>',
                              '</tr>',
                              '<tr>',
                          '</tr>',
                              '</table>',
                          '<p class="tc mt60"><a class="btn_submit" id="submit">确定信息</a> <a href="javascript:void(0);" title="" id="redirectenter" class="f14 link" onclick="redirectEnter(2,\''+brokerId+'\','+type+')">港澳台及外籍人入口</a></p>',
                          '<div class="warninfo" style="display:none;">',
                              '<p class="warn_tip mt10">如果您准备使用证券通开户，请务必在此处填写您准备用来开户的身份证与手机号，以便顺利完成开户过程，并尊享金融界为您提供的专属服务。<b class=" bblue" style="font-style:italic;">以上信息一旦提交，将无法更改！</b></p>',
                              '<p class="mt20 tc"><a class="btn_submit" id="tj" onclick="submitUserinfo(\''+brokerId+'\','+type+')">提交</a><a id="mody" class="btn_submit ml40">修改</a></p>',
                          '</div>',
                          //     '<div class="mt10 tc btn-wrap-relative"><a onclick="submitUserinfo(\''+brokerId+'\','+type+')" class="btn btn-123-40">确认提交</a><a href="javascript:void(0);" title="" id="redirectenter" class="f14 link" onclick="redirectEnter(2,\''+brokerId+'\','+type+')">港澳台及外籍人入口</a></div>',
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
            
            function numHide(){
                $('#idnumber').parentsUntil('tr').find('span.info_poptip').hide(500);
                }
            function telHide(){
                $('#mobile').parentsUntil('tr').find('span.info_poptip').hide(500);
            }
           
            $('#submit').click(function(){
            	
            	 var realname = $("#realname").val();
                 var idnumber = $("#idnumber").val();
                 var mobile = $("#mobile").val();
                 var validcode = $("#validcode").val();
                 if(btype!='0'){
                	 //验证姓名
                     if(!checkRealName()){
                         return false;
                     }
                     //验证身份证
                     if(!checkIdNumber()){
                         return false;
                     }
                 }
                 //验证手机号
                 if(!checkMobile()){
                     return false;
                 }
                 //验证验证码
                 if(!checkValidcode()){
                 	return false;
                 }
                $('.warninfo').show();
                $(this).parent().hide();
                $('.dialog-cnt input').attr("disabled",true);
                })
            $('#mody').click(function(){
                $('.warninfo').hide();
                $('.dialog-cnt input').removeAttr("disabled");
                $('#submit').parent().show();
            })
            $('.mask').width($(window).width());
            $('.mask').height($(window).height());
        	
            //邀请码初始化
            if(tgyqcode != ""){
        		$("#invitecode").val(tgyqcode);
            	$("#invitecode").attr("disabled",true);
        	}
           optionTracking(brokerId,type,2);
        }
        
        
        //打开港澳台完善信息窗口
        function openhmtuserinfo(type,brokerId){
            //完善个人信息
            JRJ.Dialogs.standardDialog({
                title: "完善个人信息&nbsp;<font class='f14'>(确保内容的真实性和准确性)</font>",
                width: 450,
                content: [
                          '<div class="dialog-cnt clearfix">',
                          '<ul class="custom-checkbox custom-checkbox-133 clearfix">',
                          '<li class="checked" ttype="HK">中国香港</li>',
                          '<li ttype="MT">台湾、澳门及海外</li>',
                          '</ul>',
                      '<table class="table-form mt10">',
                          '<tr>',
                              '<td class="field" style="width:70px">中文名 </td>',
                              '<td class="value">',
                                 '<div><input id="hmtrealname" name="hmtrealname" class="txtbox ml15" type="text" style="width:222px;"></div>',
                             '</td>',
                          '</tr>',
                          '<tr>',
                              '<td class="field" style="width:70px">',
                                 '<div><span class="red">*</span> 英文名</div>', 
                              '</td>',
                              '<td class="value">',
                                 '<div><input id="englishname"  name="englishname" class="txtbox ml15" type="text" style="width:222px;" onblur="checkEnglishName();"></div>', 
                                 '<div id="englishname_msg" name="englishname_msg" class="note"></div>',
                              '</td>',
                          '</tr>',
                          '<tr>',
                          '<td class="field" >',
                             '</td>',
                              '<td class="value">',
                                  '<div class="middle fl ml10 idtype">',
                                      '<input id="Radio1" class="radio" type="radio" name="radio-group" value="IdCard" checked>',
                                      '<label for="Radio1" class="f14 color66">身份证号</label>',
                                  '</div>',
                                  '<div class="middle fl idtype" style="margin-left:100px;">',
                                      '<input id="Radio2" class="radio" type="radio" name="radio-group" value="Passport">',
                                      '<label for="Radio2" class="f14 color66">护照号码</label>',
                                  '</div>', 
                              '</td>',
                          '</tr>',
                          '<tr>',
                              '<td class="field" id="idname">证件号码</td>',
                              '<td class="value">',
                                 '<div><input id="hmtidnumber" name="hmtidnumber" class="txtbox ml15" type="text" style="width:222px;"></div>',
                                 '<div id="idnumber_msg" class="note"></div>',
                              '</td>',
                          '</tr>',
                          '<tr>',
                              '<td class="field">',
                                 '手机号', 
                              '</td>',
                              '<td class="value">',
                                 '<div><input id="hmtmobile" name="hmtmobile" class="txtbox ml15 mr10" type="text" style="width:222px;"></div>',
                                 '<div id="mobile_msg" name="mobile_msg" class="error-msg" style="height:20px"></div>',
                              '</td>',
                          '</tr>',
                          '<tr>',
                              '<td class="field"><div><span class="red">*</span>邮箱</div>', 
                              '</td>',
                             '<td class="value">',
                              '<div class="middle">',
                              '<input id="hmtemail" name="hmtemail" type="text" class="txtbox" style="width:179px;" value="" placeholder="邮箱" onblur="checkHMTEmail();"/>',
                              '<a href="javascript:void(0);" id="getEmailCode" onclick="getEmailsCode();" class="btn btn-getcode ml5">验证邮箱</a>',
                              '</div>',
                              '<div id="email_msg" name="email_msg" class="error-msg" style="height:20px"></div>',
                              '</td>',
                          '</tr>',
                          '<tr id="emailcodeId">',
                              '<td class="field">验证码</td>',
                              '<td class="value">',
                              '<div><input id="hmtemailcode" name="hmtemailcode" type="text" class="txtbox txtbox-2" style="width:100px;" /></div>',
                              '<div id="emailcode_msg" name="emailcode_msg" class="error-msg" style="width:280px;height:20px;"></div>',
                          '</div></td>',
                          '</tr>',
                          '</table>',
                          '<div class="mt10 tc btn-wrap-relative"><a onclick="submitHMTUserinfo(\''+brokerId+'\','+type+')" class="btn btn-123-40">确认提交</a><a href="javascript:void(0);" title="" id="redirectenter" class="f14 link" onclick="redirectEnter(1,\''+brokerId+'\','+type+')">中国大陆人士入口</a></div>',
                          '<input type="hidden" id="HMTTYPE" name="HMTTYPE" value="HK"/>'
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
            
			$('.custom-checkbox li').mouseenter(function () {
                if (!$(this).hasClass('checked')) {
                    $(this).addClass('hover');
                }
            }).mouseleave(function () {
                $(this).removeClass('hover');
            }).click(function () {
                $(this).addClass('checked').siblings().removeClass('checked');
                $('#HMTTYPE').val($(this).attr("ttype"));
                if($(this).attr("ttype")=="MT"){
                	$(".idtype").hide();
                	$("#idname").html("护照号码");
                }else{
                	$(".idtype").show();
                	$("#idname").html("证件号码");
                }
            });

           optionTracking(brokerId,type,2);
        }
        
        
        
        //添加用户信息
        function submitUserinfo(brokerId,type){
        	optionTracking(brokerId,type,3);
			
            var realname = $("#realname").val();
            var idnumber = $("#idnumber").val();
            var mobile = $("#mobile").val();
            var validcode = $("#validcode").val();
            var isValid = $("#isValid").val();
            var retType = getBorkerType();
            var email = $("#email").val();
            var emailcode = $("#emailcode").val();
            var invitecode = $("#invitecode").val();
            if(btype!='0'){
            	 //验证姓名
                if(!checkRealName()){
                    return false;
                }
                //验证身份证
                if(!checkIdNumber()){
                    return false;
                }
            }
            //验证手机号
            if(!checkMobile()){
                return false;
            }
            //验证验证码
            if(!checkValidcode()){
            	return false;
            }
            //验证邮箱
            if(!checkEmail()){
                return false;
            }
            //验证邮箱验证码
            if(!checkEmailCode()){
            	return false;
            }
            $.ajax({
			    type : "post",
				url : "/stock/ajaxCreateUserInfo.jspa",
				data :{
					"realname" : realname,
					"idnumber" : idnumber,
					"mobile" : mobile,
					"validcode" : validcode,
					"retType" : retType,
					"isValid" : isValid,
					"email" : email,
					"emailcode" : emailcode,
					"invitecode" : invitecode,
					"btype" : btype
				},
				dataType : "json",
				timeout : 5000,
				async:false,
				cache : false,
				success:function(data){
				    if(data.retcode == 0){
					   //重新刷新页面
					   //window.location.reload();
					   stepUserinfoto();
					}else if(data.retcode == -1){
					   $("#validcode_msg").addClass("error-msg");
					   $("#validcode_msg").html(data.msg);
					}else if(data.retcode == -2){
					   $("#idnumber_msg").html(data.msg);
					   $("#idnumber_msg").addClass("error-msg");
					}else if(data.retcode == -3){
					   $("#mobile_msg").html(data.msg);
					   $("#mobile_msg").addClass("error-msg");
					}else if(data.retcode == -4){
					   $("#emailcode_msg").html(data.msg);
					   $("#emailcode_msg").addClass("error-msg");
					}else if(data.retcode == -5){
					   $("#invitecode_msg").html(data.msg);
					   $("#invitecode_msg").addClass("error-msg");
					}		  
				}		  
			});
           
        }
        
        
        //添加用户信息
        function submitHMTUserinfo(brokerId,type){
        	optionTracking(brokerId,type,3);
			
			var englishname = $("#hmtenglishname").val();
            var realname = $("#hmtrealname").val();
            var idnumber = $("#hmtidnumber").val();
            var mobile = $("#hmtmobile").val();
            var retType = getBorkerType();
            var email = $("#hmtemail").val();
            var emailcode = $("#hmtemailcode").val();
            var HMTTYPE = $("#HMTTYPE").val();
            var idtype=$('input[name="radio-group"]:checked').val();
            //验证英文姓名
            if(!checkEnglishName()){
                return false;
            }
            //验证邮箱
            if(!checkHMTEmail()){
                return false;
            }
            
            //验证邮箱验证码
            if(!checkHMTEmailCode()){
            	return false;
            }
            $.ajax({
			    type : "post",
				url : "/stock/ajaxCreateHMTUserInfo.jspa",
				data :{
					"realname" : realname,
					"idnumber" : idnumber,
					"mobile" : mobile,
					"englishname" : englishname,
					"retType" : retType,
					"email" : email,
					"emailcode" : emailcode,
					"HMTTYPE" : HMTTYPE,
					"idtype" : idtype
				},
				dataType : "json",
				timeout : 5000,
				async:false,
				cache : false,
				success:function(data){
				    if(data.retcode == 0){
					   //重新刷新页面
					   //window.location.reload();
					   stepUserinfoto();
					}else if(data.retcode == -1){
						   $("#emailcode_msg").addClass("error-msg");
						   $("#emailcode_msg").html(data.msg);
						}
				    else if(data.retcode == -2){
					   $("#idnumber_msg").html(data.msg);
					   $("#idnumber_msg").addClass("error-msg");
					}else if(data.retcode == -3){
					   $("#mobile_msg").html(data.msg);
					   $("#mobile_msg").addClass("error-msg");
					}else if(data.retcode == -4){
					   $("#emailcode_msg").html(data.msg);
					   $("#emailcode_msg").addClass("error-msg");
					}	
				    return false;
				}		  
			});
           
        }
        
        function redirectEnter(direction,brokerId,type)
        {
        	JRJ.Dialogs.close();
        	if(direction==1){
        		openuserinfo(type,brokerId);
        		$("#inviteId").hide();
        		  //赋值
		    	  if($("#realnamecache").val()!=null && $("#realnamecache").val()!="null" && $("#realnamecache").val()!=""){
		    		 $("#realname").val($("#realnamecache").val());
		    		 $("#realname").attr('disabled','true');
		    	  }
		    	 if($("#idnumbercache").val()!=null && $("#idnumbercache").val()!="null" && $("#idnumbercache").val()!=""){
		    		$("#idnumber").val($("#idnumbercache").val());
		    		$("#idnumber").attr('disabled','true');
		    	 }
		    	 if($("#mobilecache").val()!=null && $("#mobilecache").val()!="null" && $("#mobilecache").val()!=""){
		    		//$("#mobile").attr('disabled','true');
		    		$("#mobile").val($("#mobilecache").val());
		    		//如果存在，不需要验证码-隐藏
		    		//$("#validcodeId").hide();
		    		//$("#getValidateCode").hide();
		    		//$("#isValid").val("false");
		    		//$("#remobile").show();
		    	 }
		    	if($("#emailcache").val()!=null && $("#emailcache").val()!="null" && $("#emailcache").val()!=""){
		    		$("#email").val($("#emailcache").val());
		    		//$("#isEmailValid").val("false");
		    		//$("#emailcodeId").hide();
		    		//$("#email").attr('disabled','true');
		    		//$("#getEmailCode").hide();
		    	 }
        		
        		
        	}else{
        		openhmtuserinfo(type,brokerId);
        		if($("#emailcache").val()!=null && $("#emailcache").val()!="null" && $("#emailcache").val()!=""){
			    		$("#hmtemail").val($("#emailcache").val());
			    		$("#isEmailValid").val("false");
			    		$("#emailcodeId").hide();
			    		$("#hmtemail").attr('disabled','true');
			    		$("#getEmailCode").hide();
			    	 }
        		 if($("#realnamecache").val()!=null && $("#realnamecache").val()!="null" && $("#realnamecache").val()!=""){
		    		 $("#hmtrealname").val($("#realnamecache").val());
		    	  }
		    	 if($("#mobilecache").val()!=null && $("#mobilecache").val()!="null" && $("#mobilecache").val()!=""){
			    		$("#hmtmobile").val($("#mobilecache").val());
			    	 }
        		
        	}
        }
        
            function submitAuthorize(){
                var realname = $("#realname").val();
                var idnumber = $("#idnumber").val();
                var brokerId = $("#brokerId").val();
                //验证姓名
                if(!checkRealName()){
                    return false;
                }
                //验证身份证
                if(!checkIdNumber()){
                    return false;
                }
                $.ajax({
					type : "post",
					url : "/stock/ajaxAuthorize.jspa",
					data :{
						"realname" : realname,
						"idnumber" : idnumber,
						"brokerId" : brokerId
					  },
					dataType : "json",
					timeout : 5000,
					async:false,
					cache : false,
					success:function(data){
					   if(data.retcode == 0){
					       //重新刷新页面
					       window.location.reload();
					   }else if(data.retcode == -1){
						   $("#idnumber_msg").addClass("error-msg");
					       $("#idnumber_msg").html(data.msg);
					   }	  
					}		  
				});
            }
            //验证真实姓名
            function checkRealName(){
				var realName=$("#realname").val();
				realName = realName.trim();
				realName=realName.replace(/ /g,'');
				$("#realname").val(realName);
				if(realName.length==0||(!/^[\u4e00-\u9fa5]{2,15}$/.test(realName))||realName.bLength()<4||realName.bLength()>30){
					$("#realname_msg").addClass("error-msg");
					$("#realname_msg").text("真实姓名只能是中文，请正确填写");
					return false;
				}else{
					$("#realname_msg").removeClass("error-msg");
					$("#realname_msg").text("真实姓名只能是中文");
					return true;
				}
			}
            //验证英文名字
            function checkEnglishName(){
            	var englishname=$("#englishname").val();
            	englishname = englishname.trim();
            	//englishname=englishname.replace(/ /g,'');
				$("#englishname").val(englishname);
				if(englishname.length==0||!/^[a-zA-Z ]{1,20}$/.test(englishname)){
					$("#englishname_msg").addClass("error-msg");
					$("#englishname_msg").text("英文名错误，请正确填写");
					return false;
				}else{
					$("#englishname_msg").removeClass("error-msg");
					$("#englishname_msg").text("");
					return true;
				}
            }
            //验证验证码
            function checkValidcode(){
            	//判断验证码是否需要验证
            	if($("#isValid").val() == "false"){
            		return true;
            	}
				var validcode=$("#validcode").val();
				validcode = validcode.trim();
				validcode=validcode.replace(/ /g,'');
				$("#validcode").val(validcode);
				if(validcode.length==0){
					$("#validcode_msg").text("请输入验证码！");
					return false;
				}else{
					$("#validcode_msg").text("");
					return true;
				}
			}
            //验证邮箱验证码
            function checkEmailCode(){
            	//不需要验证email
            	if($("#isEmailValid").val() == "false"){
            		return true;
            	}
				var emailcode=$("#emailcode").val();
				emailcode = emailcode.trim();
				emailcode=emailcode.replace(/ /g,'');
				$("#emailcode").val(emailcode);
				if(emailcode.length==0){
					$("#emailcode_msg").text("请输入验证码！");
					return false;
				}else{
					$("#emailcode_msg").text("");
					return true;
				}
			}
            
          //验证邮箱验证码
            function checkHMTEmailCode(){
            	//不需要验证email
            	if($("#isEmailValid").val() == "false"){
            		return true;
            	}
				var emailcode=$("#hmtemailcode").val();
				emailcode = emailcode.trim();
				emailcode=emailcode.replace(/ /g,'');
				$("#hmtemailcode").val(emailcode);
				if(emailcode.length==0){
					$("#emailcode_msg").text("请输入验证码！");
					return false;
				}else{
					$("#emailcode_msg").text("");
					return true;
				}
			}
            
            
			//验证身份证号
			function checkIdNumber(){
				//小写字母转大写
				var idnumber = $("#idnumber").val().toUpperCase();
				if(idnumber.length==0||!(XJB.Utils.isCardId(idnumber))){
					$("#idnumber_msg").addClass("error-msg");
					$("#idnumber_msg").text("身份证号只能是18位，请正确填写");
					return false;
				}else{
					$("#idnumber_msg").removeClass("error-msg");
					$("#idnumber_msg").text("身份证号只能是18位");
					return true;
				}
			}
			//验证手机号是否已注册
            function checkMobile(){
				var mobile = $("#mobile").val();
				mobile = mobile.trim();
				$("#mobile").val(mobile);
                if($("#mobile").val().length==0 || $("#mobile").val().length!=11){
					$("#mobile_msg").html("请正确添写手机号！");
					return false;
				}else{
				    $("#mobile_msg").html("");
					return true;
				}
            }
            //验证邮箱
            function checkEmail(){
            	//不需要验证email
            	if($("#isEmailValid").val() == "false"){
            		return true;
            	}
				var email = $("#email").val();
				email = email.trim();
				$("#email").val(email);
                if($("#email").val().length==0 || !testEmail(email)){
					$("#email_msg").html("请正确添写邮箱！");
					return false;
				}else{
				    $("#email_msg").html("");
					return true;
				}
            }
            
            //验证邮箱
            function checkHMTEmail(){
            	//不需要验证email
            	if($("#isEmailValid").val() == "false"){
            		return true;
            	}
				var email = $("#hmtemail").val();
				email = email.trim();
				$("#hmtemail").val(email);
                if($("#hmtemail").val().length==0 || !testEmail(email)){
					$("#email_msg").html("请正确添写邮箱！");
					return false;
				}else{
				    $("#email_msg").html("");
					return true;
				}
            }
            
            
            function testEmail(str){
            	var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
            	if(reg.test(str)){
            	    return true;
            	}else{
            	    return false;
            	}
            }
            //查询用户是否完善信息
            //type 开户还是转户  source 哪个按钮处发的事件   retType A股还是港股
            function getUserInfoIsPerfect(type,source){
            	var brokerId = $("#brokerId").val();
            	//根据券商id判断是A港还是港股
            	var retType = getBorkerType();
            	$.ajax({
    			    type : "post",
    				url : "/stock/ajaxUserInfoIsPerfect.jspa",
    				data :{
            			"retType" : retType,
            			"btype" : btype
            		  },
    				dataType : "json",
    				timeout : 5000,
    				async:false,
    				cache : false,
    				success:function(data){
    					if(data.retcode == 0){
    						//alert(data.flag);
    					   //未完善信息
 					       if(data.flag == "1"){
 					    	   openuserinfo(type,brokerId);
 					    	  //赋值
 					    	  if(data.trueName!=null && data.trueName!="null" && data.trueName!=""){
 					    		 $("#realname").val(data.trueName);
 					    		 $("#realnamecache").val(data.trueName);
 					    		 $("#realname").attr('disabled','true');
 					    	  }else{
 					    		 $("#realnamecache").val("");
 					    	  }
 					    	 if(data.idCard!=null && data.idCard!="null" && data.idCard!=""){
 					    		$("#idnumber").val(data.idCard);
 					    		$("#idnumbercache").val(data.idCard);
 					    		$("#idnumber").attr('disabled','true');
 					    	 }else{
 					    		$("#idnumbercache").val("");
 					    	 }
 					    	 if(data.mobile!=null && data.mobile!="null" && data.mobile!=""){
 					    		//$("#mobile").attr('disabled','true');
 					    		$("#mobile").val(data.mobile);
 					    		$("#mobilecache").val(data.mobile);
 					    		//如果存在，不需要验证码-隐藏
 					    		//$("#validcodeId").hide();
 					    		//$("#getValidateCode").hide();
 					    		//$("#isValid").val("false");
 					    		//$("#remobile").show();
 					    	 }else{
 					    		$("#mobilecache").val("");
 					    	 }
  					    	if(data.email!=null && data.email!="null" && data.email!=""){
 					    		$("#email").val(data.email);
 					    		$("#emailcache").val(data.email);
 					    		//$("#isEmailValid").val("false");
 					    		//$("#emailcodeId").hide();
 					    		//$("#email").attr('disabled','true');
 					    		//$("#getEmailCode").hide();
 					    	 }else{
 					    		$("#emailcache").val("");
 					    	 }
 					    	 //判断是港还是A港,如果是A则隐藏邮箱及邮箱验证码
  					    	 if(retType == "1"){
  					    		$("#emailId").hide();
  					    		$("#emailcodeId").hide();
  					    		$("#redirectenter").hide();
  					    		$("#isEmailValid").val("false");
  					    	 }else{
  					    		$("#inviteId").hide();
  					    	 }
  					    	 if(btype=="0"){
  					    		$("#trrealname").hide();
  					    		$("#tridnumber").hide();
  					    	 }else if(btype=="1"){
  					    		$("#trrealname").show();
  					    		$("#tridnumber").show();
  					    	 }
 					       }else{//已完善信息
 					    	   //A股弹邀请码窗口
 					    	   if(retType == "1"){
 					    		   //未添写邀请码
 					    		  if(data.yqcode == "0"){
 	 					    		  openYqCode();
 	 					    	   }else{
 	 					    		  stepUserinfoNext();
 	 					    	   }
 					    	   }else{
 					    		  stepUserinfoNext();
 					    	   }
 					       }
 					   }else if(data.retcode == -1){
 					       $("#validcode_msg").html(data.msg);
 					   }
    				}		  
    			});
            }
            //打开开户页面
            function kaihu(type, style){
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
            	var brokerId =$("#brokerId").val();
            	if(brokerId==""){
            		 JRJ.Alerts.alert({
            				title: "消息提示",  //标题
            				width:400,
            				message: "请选择券商",     //提示语
            				autoClose:false	
            			  });
            		return false;
            	}
				optionTracking(brokerId,type,style);
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
            				  if(data.kaihuUrl==null || data.kaihuUrl==""){
            					 
            				  }else{
            					  window.open(data.kaihuUrl,"ZHKH");
            				  }
            				 
            				  if(type == "1" || type == "2"){//只有开户时才刷页面
            					  window.location.reload();
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
            //通过验证
            function ajaxAcceptRequest(accountId){
            	$.ajax({
            		type : "post",
            		url :"/stock/ajaxAcceptRequest.jspa",
            		data :{
            			"accountId" : accountId
            		  },
            		dataType : "json",
            		timeout : 5000,
            		async:false,
            		cache : false,
            		success:function(data){
            			  if(data != undefined){
            				  if(data.retcode == 0){
            					  //调用刷新页面方法
            					  JRJ.Alerts.alert({
                						title: "消息提示",  //标题
                						width:400,
                						autoClose:false,
                					    okCallback: callbackRe, //ok回调
                						cancelCallback: callbackRe, //cancel回调，关闭时也触发
                						message: data.msg     //提示语
                					  });
            				  }else{
            					  JRJ.Alerts.alert({
              						title: "消息提示",  //标题
              						width:400,
              						autoClose:false,
              						message: data.msg     //提示语
              					  });
            				  }
            			  }				  
            		 }		  
            	});
            }
            //拒绝
            function ajaxRefuseRequest(accountId){
            	$.ajax({
            		type : "post",
            		url :"/stock/ajaxRefuseRequest.jspa",
            		data :{
            			"accountId" : accountId
            		  },
            		dataType : "json",
            		timeout : 5000,
            		async:false,
            		cache : false,
            		success:function(data){
            			if(data != undefined){
	          				  if(data.retcode == 0){
	          					  //调用刷新页面方法
	          					  JRJ.Alerts.alert({
	              						title: "消息提示",  //标题
	              						width:400,
	              						autoClose:false,
                					    okCallback: callbackRe, //ok回调
                						cancelCallback: callbackRe, //cancel回调，关闭时也触发
	              						message: data.msg     //提示语
	              					  });
	          				  }else{
	          					  JRJ.Alerts.alert({
	            						title: "消息提示",  //标题
	            						width:400,
	            						autoClose:false,
	            						message: data.msg     //提示语
	            					  });
	          				  }
          			     }			  
            		 }		  
            	});
            }
            //拒绝
            function ajaxCancelRequest(accountId){
				JRJ.Alerts.confirm({
					title: "消息提示",  //标题
					width:400,
					message: '您是否确认要解除绑定？',     //提示语
					okCallback:function(){
            	$.ajax({
            		type : "post",
            		url :"/stock/ajaxCancelRequest.jspa",
            		data :{
            			"accountId" : accountId
            		  },
            		dataType : "json",
            		timeout : 5000,
            		async:true,
            		cache : false,
            		success:function(data){
            			if(data != undefined){
	          				  if(data.retcode == 0){
	          					  //调用刷新页面方法
	          					  JRJ.Alerts.alert({
	              						title: "消息提示",  //标题
	              						width:400,
	              						autoClose:false,
                					    okCallback: callbackRe, //ok回调
                						cancelCallback: callbackRe, //cancel回调，关闭时也触发
	              						message: data.msg     //提示语
	              					  });
	          				  }else{
	          					  JRJ.Alerts.alert({
	            						title: "消息提示",  //标题
	            						width:400,
	            						autoClose:false,
	            						message: data.msg     //提示语
	            					  });
	          				  }
        			     }				  
            		 }		  
            	});
				}
				  });
            }
            //重新加载页面
            function callbackRe(){
            	window.location.reload();
            }
            //授权列表排序
            function ajaxChangeSort(){
            	var ids = $('input[name="accountIds"]');
            	var accountIds = "";
            	 for(var i = 0;i<ids.length;i++){
            	     if(i!=0){
            	    	 accountIds=accountIds+",";
            	     }
                     accountIds=accountIds+ids[i].value;
            	} 
            	$.ajax({
            		type : "post",
            		url :"/stock/ajaxChangeSort.jspa",
            		data :{
            			"accountIds" : accountIds
            		  },
            		dataType : "json",
            		timeout : 5000,
            		async:false,
            		cache : false,
            		success:function(data){
            			if(data != undefined){
	          				  if(data.retcode == 0){
	          				  }else{
	          				  }
        			     }				  
            		 }		  
            	});
            }
			
		function ajaxDeleteAuthRecord(accountId,e){
			var target = $(e).parents('div.my-account-item');
			JRJ.Alerts.confirm({
					title: "消息提示",  //标题
					width:400,
					message: '您是否确认要删除此记录？',     //提示语
					okCallback:function(){
            	$.ajax({
            		type : "post",
            		url :"/stock/ajaxDeleteAuthRecord.jspa",
            		data :{
            			"accountId" : accountId
            		  },
            		dataType : "json",
            		timeout : 5000,
            		async:true,
            		cache : false,
					context: target,
            		success:function(data){
            			if(data != undefined){
	          				  if(data.retcode == 0){
	          					  //调用刷新页面方法
								$(this).fadeOut('slow');
	          				  }else{
	          					  JRJ.Alerts.alert({
	            						title: "消息提示",  //标题
	            						width:400,
	            						autoClose:false,
	            						message: data.msg     //提示语
	            					  });
	          				  }
        			     }				  
            		 }		  
            	});
				}
			});
		}
            var ti = 0;
          var tie = 0;
          //获取验证码
          function getIdentifyingCode() {
          	// 获取验证码------------------------------start
          	if(ti>0){
          		var codevalue = $('#getValidateCode').text();
          		if(codevalue == "获取验证码" || codevalue == "再次获取"){
          			$("#validcode_msg").html("获取频繁请稍后再试！");
          		}
              	return false;
            }
            // 获取验证码------------------------------end
          	var mobile = $("#mobile").val();
          	var captcha = ""+$("#captcha").val();
          	$.ajax({
          		type : "post",
          		url : "/stock/getCode.jspa",
          		data :{
          			"mobile" : mobile
          			  },
          		dataType : "json",
          		timeout : 5000,
          		cache : false,
          		success : function(data, textStatus) {
          			if (typeof(data) != 'undefined' && data != null && data != 'null') {
          				if(data.status == 1){
          					if(data.resultCode == 0){
          						var tmpl = '<img border="0" src="/stock/images/regist/register-popup-getcode.png" alt="waiting" class="popup-waiting" />';
          						$(tmpl).css({'position':'absolute',top:'310px',left:'50%','margin-left':'-241px', 'display':'none', 'z-index':'200'}).appendTo($('.xjb-login-content')).fadeIn(function(){
          							var _this = $(this);
          							setTimeout(function(){
          								_this.fadeOut().remove();
          							},3000);
          						});

          						if (ti <= 0) {
          					        ti = 300;
          					        var self = $('#getValidateCode');
          					        self.unbind("click");
          					        self.text(ti+"秒重新获取");
          					        $('#mobile').attr("disabled",true); 
          					        var _inter = setInterval(function () {
          					            if (ti <= 0) {
          					            	$('#mobile').attr("disabled",false); 
          					            	self.text("再次获取");
          					            	//changeCaptcha();
          					            	//self.click(function(){
          					            	//	getIdentifyingCode();
          					            	//});
          					                //window.clearInterval(_inter);
          					               // hideErr("#validateCode","图片验证码将用于手机号码加密");
          					            } else {
          					                self.text(ti+"秒重新获取");
          					                ti--;
          					            }
          					        }, 1000);
          					    }
          						
          					}else if(data.resultCode == 1){
          						$("#validcode_msg").html(data.resultMsg);
          						return false;
          					}else{
          						$("#validcode_msg").html(data.resultMsg);
          						return false;
          					}
          				}else{
          					$("#validcode_msg").html(data.resultMsg);
          					return false;
          				}					
          			}else{
          				$("#validcode_msg").html(data.resultMsg);
          				return false;
          			}
          		}
          	});
          }
        //获取邮箱验证码
          function getEmailsCode() {
          	// 获取验证码------------------------------start
          	if(tie>0){
          		var codevalue = $('#getEmailCode').text();
          		if(codevalue == "验证邮箱" || codevalue == "再次验证"){
          			$("#emailcode_msg").html("获取频繁请稍后再试！");
          		}
              	return false;
            }
            // 获取验证码------------------------------end
          	var email = $("#email").val();
          	var captcha = ""+$("#captcha").val();
          	$.ajax({
          		type : "post",
          		url : "/stock/getEmailCode.jspa",
          		data :{
          			"email" : email
          			  },
          		dataType : "json",
          		timeout : 5000,
          		cache : false,
          		success : function(data, textStatus) {
          			if (typeof(data) != 'undefined' && data != null && data != 'null') {
          				if(data.status == 1){
          					if(data.resultCode == 0){
          						var tmpl = '<img border="0" src="/stock/images/regist/register-popup-getcode.png" alt="waiting" class="popup-waiting" />';
          						$(tmpl).css({'position':'absolute',top:'310px',left:'50%','margin-left':'-241px', 'display':'none', 'z-index':'200'}).appendTo($('.xjb-login-content')).fadeIn(function(){
          							var _this = $(this);
          							setTimeout(function(){
          								_this.fadeOut().remove();
          							},3000);
          						});

          						if (tie <= 0) {
          							tie = 300;
          					        var self = $('#getEmailCode');
          					        self.unbind("click");
          					        self.text(tie+"秒重新获取");
          					        $('#email').attr("disabled",true); 
          					        var _inter = setInterval(function () {
          					            if (tie <= 0) {
          					            	$('#email').attr("disabled",false); 
          					            	self.text("再次验证");
          					            	//changeCaptcha();
          					            	//self.click(function(){
          					            	//	getIdentifyingCode();
          					            	//});
          					                //window.clearInterval(_inter);
          					               // hideErr("#validateCode","图片验证码将用于手机号码加密");
          					            } else {
          					                self.text(tie+"秒重新获取");
          					                tie--;
          					            }
          					        }, 1000);
          					    }
          						
          					}else if(data.resultCode == 1){
          						$("#emailcode_msg").html(data.resultMsg);
          						return false;
          					}else{
          						$("#emailcode_msg").html(data.resultMsg);
          						return false;
          					}
          				}else{
          					$("#emailcode_msg").html(data.resultMsg);
          					return false;
          				}					
          			}else{
          				$("#emailcode_msg").html(data.resultMsg);
          				return false;
          			}
          		}
          	});
          }
          function changeCaptcha(){
        		var captchaUrl=XJB.Urls.captcha+"?"+new Date().getTime();
        		$("img.captcha-Img").attr("src",captchaUrl);
        		$("#captcha").val("");
        	}
          function flFoZx(brokerId,count){
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
        	  goZX();
          }
          function goZX(){
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
        		initBind(brokerId);		
        	}
          //ITN绑户
          function goITNbind(){
        	  var brokerId =$("#brokerId").val();
        	  var txPwd=$("#isTxPwd").val();
        	  var aa=brokerId.substring(0,4);
	    		if(aa!="ITN_"){//处理brokerid不一致问题
	    			brokerId="ITN_"+brokerId;
	    		}
	    		//JRJ.Dialogs.close();
        	  //ITN绑定弹层
              JRJ.Dialogs.standardDialog({
                  title: "账户绑定&nbsp;<font class='f14'>(确保内容的准确性)</font>",
                  width: 423,
                  content: [
                      '<div class="dialog-cnt clearfix">',
                          '<table class="table-form mt10">',
                              '<tr>',
                                  '<td class="field" style="width:60px;">资金账号</td>',
                                  '<td class="value">',
                                      '<div><input id="capitalAccount" name="capitalAccount" type="text" class="txtbox txtbox-2" style="width:280px;" onblur="checkCapitalAccount();"/></div>',
                                      '<div id="capitalAccount_msg" name="capitalAccount_msg" class="note">请填写资金账户</div>',
                                  '</td>',
                              '</tr>',
                              '<tr>',
                                  '<td class="field">交易密码'+(txPwd==1?'<br/><span style="margin-top:30px;display:block;">通讯密码</span>':'')+'</td>',
                                  '<td class="value">',
                                      '<div id="inputtext"></div>',
                                      '<div id="password_msg" class="note">请填写密码</div>',
                                  '</td>',
                              '</tr>',
                          '<tr>',
                      '</tr>',
                          '</table>',
                          '<div class="mt10 tc btn-wrap-relative"><a onclick="submitBindITN(\''+brokerId+'\','+type+')" class="btn btn-123-40">确认提交</a></div>',
                      '</div>',
                  ].join(''),
                  okBtnText: "",
                  cancelBtnText: "取消",
                  hasCloseBtn: true,
                  hasBtn: false,
                  hasOkBtn: false,
                  hasCancelBtn: false,
                  okCallback: function () { },
                  cancelCallback: function () {},
                  isFixed: true
              });
              jrjpassword=new JRJZQTPassword("#inputtext",brokerId,JRJZQTPassword.Action.BIND); 
              
//              $('#capitalAccount').mouseenter(function(){
//                 	$("#capitalAccount_msg").text('');
//                 	$("#capitalAccount_msg").removeclass("error-msg");
//              	
//              });
//              $('#inputtext').mouseenter(function(){
//                 	$("#password_msg").text('');
//                 	$("#password_msg").removeclass("error-msg");
//              });
              
          }
        //绑定itn
          function submitBindITN(brokerId,type){
              var capital_account = $("#capitalAccount").val();
              //验证资金账户
              if(!checkCapitalAccount()){
                  return false;
              }
              jrjpassword.check(function(data){
          		if(data == 0){//检查成功
          			optionTracking(brokerId,type,3);
          			password=jrjpassword.value();
          			txpwd=jrjpassword.value("txPassword");
          			userMac=jrjpassword.mac();
          			 $.ajax({
           			    type : "post",
           				url : "/stock/ajaxBindITN.jspa",
           				data :{
           					"capitalAccount" : capital_account,
           					"password" : password,
           					"txPassword":txpwd,
           					"brokerId" : brokerId,
           					"local_network":userMac,
           					"local_disk":jrjpassword._disk,
           					"local_cpu":jrjpassword._nic
           				},
           				dataType : "json",
           				timeout : 5000,
           				async:false,
           				cache : false,
           				success:function(data){
           				    if(data.retcode == 0){
           				    	JRJ.Dialogs.close();
           					   //重新刷新页面
           				    	JRJ.Alerts.alert({
           							title: "消息提示", 
           							width:400,
           							message: "绑定成功！" ,
           							autoClose:false,
           							okCallback: callbackreload, //ok回调
           	  						cancelCallback: callbackreload //cancel回调，关闭时也触发
           						  });
           					}else if(data.retcode == -1){
           					   $("#capitalAccount_msg").addClass("error-msg");
           					   $("#capitalAccount_msg").html(data.msg);
           					}else if(data.retcode == -2){
           					   $("#password_msg").html(data.msg);
           					   $("#password_msg").addClass("error-msg");
           					}else if(data.retcode == -3){
           					   $("#password_msg").html("券商为空！");
           					   $("#password_msg").addClass("error-msg");
           					}else{
           						$("#password_msg").html(data.msg);
           						$("#password_msg").addClass("error-msg");
           					   //$("#invitecode_msg").html(data.msg);
           					   //$("#invitecode_msg").addClass("error-msg");
           					}		  
           				}		  
           			});
          		}else if(data == -100){
  					$("#password_msg").text('交易密码不能为空');
  				}else if(data == -103){
  					$("#password_msg").text('通讯密码不能为空');
  				}else if(data == -99){
  					$("#password_msg").text('为了您的交易安全，请安装安全控件');
  				}else{
  					$("#password_msg").text('您输入的密码有误');
  				}
          	});
          }
        //绑定页面
          function initBind(){
          	var brokerId = $("#brokerId").val();
          	$.ajax({
          		type : "get",
          		url :"/stock/checkBrokerSys.jspa",
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
          	var dialog = JRJ.Dialogs.standardDialog({
          	    width: 750,
          	    hasOkBtn: false,
          	    title: '登录券商授权页进行绑定',
				content : ['<div class="popup-wrap popup-wrap-padding">',
							'<div class="stock-register-popup">',
								'<h1 class="jrj-tc">您的证券通账户：<span class="highlight">'+_selfUserName+'</span></h1>',
								'<p class="jrj-tc mt5">您现在正在绑定券商账户，请您在确认绑定完成后关闭此窗口。</p>',
							'</div>',
							'<div class="dahsedline-3 jrj-tr qq-wrap mt30"></div>',
							'<div class="jrj-tc mt30 stock-register-popup-phone middle">',
								'<i></i><span>有问题请您联系客服： </span><span class="highlight">400-166-1188</span>',
							'</div>',
						'</div>'].join(''),
          	    hasCancelBtn: false,
          	    enableKeyCtrl: true,
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
          		        window.location.reload();
          		    }
          		}
          }
          function checkBind(){
        		/*var brokers = $("input[name='brokerId_bind']");
        		var brokerId ="";
        		for(var i=0;i<brokers.length;i++){
        			if(brokers[i].checked){
        				brokerId=brokers[i].value;			
        			}
        		}*/
        		var brokerId =$("#brokerId").val();
        		if(brokerId==""){
        			 JRJ.Alerts.alert({
        					title: "消息提示",  //标题
        					width:400,
        					message: "请选择券商"     //提示语
        				  });
        			return ;
        		}
        		//var url ="/stock/bindRS.jspa?brokerId="+brokerId+"&redirect="+ (typeof redirect != "undefined" ?encodeURIComponent(redirect) :"");
        		//window.location.href=url;
        	}
          //列表中我要开户点击事件
          function openkhFromList(brokerId,type){
        	  $("#brokerId").val(brokerId);
        	  kaihu(type);
          }
          
          //自动加载账户信息
          ajaxInitAccountInfo();
          
          //删除未开户记录
          function delFailAccount(strBrokerId,e){
			var target = $(e).parents('div.my-account-item');
        		$.ajax({
        			type : "get",
        			url :"/stock/delKaihu.jspa",
        			data :{
        				"brokerId" : strBrokerId
        			  },
        			dataType : "json",
        			timeout : 5000,
        			async:false,
        			cache : false,
					context : target,
        			success:function(data){
        				  if(data &&data !=undefined){
        					  if(data.error && data.error ==-1){
            					  JRJ.Alerts.alert({
            							title: "消息提示",  // 标题
            							autoClose: false,
            							width:400,
            							message: "用户信息不完整！"     // 提示语
            						  });
            					  return false;
            				  }
        					  else if(data.error && data.error ==-2){
            					  JRJ.Alerts.alert({
            							title: "消息提示",  // 标题
            							autoClose: false,
            							width:400,
            							message: "删除失败！"     // 提示语
            						  });
            					  return false;
            				  }else{
            					  $(this).fadeOut('slow');
            				  }
        					  
        			 }else{
        				 JRJ.Alerts.alert({
        						title: "消息提示",  // 标题
        						autoClose: false,
        						width:400,
        						message: "系统错误"     // 提示语
        					  });
        			 }	  
        		}
        		});
          }
          function optionTracking(brokerId,type,style){
			var styleStr='';
			switch(style){
			case 1://已完善资料
				styleStr='';
				break;
			case 2://未完善资料
				styleStr='-WSZL';
				break;
			case 3://完善资料提交
				styleStr='-WSZLCF';
				break;
			default:
			}
			 var iframe=$('<iframe width="0" height="0" frameborder="0" scrolling="no" marginwidth="0" marginheight="0" id="iframe_dcs" style="display:none;" src="/stock/click.html?oa=' + brokerId + '-' + type + styleStr + '-20141120"></iframe>');
			$('body').append(iframe);
		 }
		function optionTracking(brokerId,type,style){
			var styleStr='';
			switch(style){
			case 1://已完善资料
				styleStr='';
				break;
			case 2://未完善资料
				styleStr='-WSZL';
				break;
			case 3://完善资料提交
				styleStr='-WSZLCF';
				break;
			case 4://非手机开户
				styleStr='-FSJKZH';
				break;
			default:
			}
			var iframe=$('<iframe width="0" height="0" frameborder="0" scrolling="no" marginwidth="0" marginheight="0" id="iframe_dcs" style="display:none;" src="/stock/click.html?oa=' + brokerId + '-' + type + styleStr +'-20141120"></iframe>');
			$('body').append(iframe);
		 }
          
function showMobileTip(type,brokerId){
	JRJ.Dialogs.iframeDialog({
        content: [''].join(''),
        loadingImg: '',
        width: 500,
        hasBtn: false,
        hasOkBtn: false,
        title: '温馨提示',
        titleRight: '',
        hasCancelBtn: false,
        enableKeyCtrl: true,
        ifrSrc: '/stock/mobileTip.jspa?type='+type+'&brokerId='+brokerId,
        ifrReHeight: true,
        isFixed: true
    });
	optionTracking(brokerId,type);
}
//填充券商展示层信息
function setBrokerInfo(brokerId){
	//alert(brokerId+"-----------"+mod);
	$("#mod1_p").text("");
	$("#mod1_bank").attr("src",""); 
	$("#mod2_p").text("");
	//$("#mod2_bank").attr("src","");
	$.each(jsonbrokerlist.brokerlist, function(index, content){
		if(brokerId==content.brokerId){
			if(content.brokerType='0' && content.type=='1'){
				$("#mod1_p").text(content.ext1);
				$("#mod1_bank").attr("src", "images/banks_"+content.brokerId+".png"); 
			}else if(content.brokerType='0' && content.type=='2'){
				$("#mod2_p").text(content.ext1);
				//$("#mod2_bank").attr("src", "images/banks_"+content.brokerId+".png"); 
			}
		}
		///alert(content.brokerId);
	});
}
//验证资金账户
function checkCapitalAccount(){
	var capitalAccount=$("#capitalAccount").val();
	capitalAccount = capitalAccount.trim();
	capitalAccount=capitalAccount.replace(/ /g,'');
	$("#capitalAccount").val(capitalAccount);
	if(capitalAccount.length==0){
		$("#capitalAccount_msg").addClass("error-msg");
		$("#capitalAccount_msg").text("请正确输入资金账户");
		return false;
	}else{
		$("#capitalAccount_msg").removeClass("error-msg");
		$("#capitalAccount_msg").text("请正确输入资金账户");
		return true;
	}
}
function xieyi(){
	var brokerId =$("#brokerId").val();
	dlgProtocol(brokerId,"2")
}
function gobindTob(wmenu){
	if(wmenu=="2"){
		$('#wmenu_2').trigger("click");
	}
	if(wmenu=="3"){
		$('#wmenu_3').trigger("click");
		var b = getPara('broker');
		if(b!=""){
			try{$('#wcon_3 #open_account .stock-comp-list li[broker='+b+']').click();}catch(e){}
		}
	}
	  $('body,html').animate({
	      scrollTop: 0
	  },
	  1000);
	  
		
	  return false;
	
}


$('#idNum').keydown(function(){
	if($('#idNum').val().length==17){
	$('#idNum').parent().find('span.info_poptip').show(500);
	setTimeout(numHide,10000);
	}
	
})
function numHide(){
	$('#idNum').parent().find('span.info_poptip').hide(500);
	}
function telHide(){
	$('#idTel').parent().find('span.info_poptip').hide(500);
}
$('#idTel').keydown(function(){
	if($('#idTel').val().length==10){
		$('#idTel').parent().find('span.info_poptip').show();
		setTimeout(telHide,10000);
		}
})
function isCardNo(){	
// 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X  	
	var idNum = $("#idNum").val();
	if (idNum!=""){
		var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/; 
		if(!reg.test(idNum)){
			return false;
		}
		else{
			return true;
			}
	}else{
		alert("为空");
		return false;
	}
}
function isTelNo(){	
// 11位手机号或者正确的手机号前七位  	
	var telNum = $("#idTel").val();
	if (telNum!=""){
		var reg = /^1[3|4|5|8][0-9]\d{4,8}$/; 
		if(!reg.test(telNum)){
			return false;
		}
		else{
			return true;
			}
	}else{
		alert("为空");
		return false;
	}
}

//刷新开户状态
function ajaxRefreshBindStatus(brokerId){
		$.ajax({
			type : "post",
			url :"/stock/ajaxRefreshBindStatus.jspa",
			data :{
				"brokerId" : brokerId
			  },
			dataType : "json",
			timeout : 5000,
			async:false,
			cache : false,
			success:function(data){
			if(data &&data !=undefined){
				if(data.errorno && data.errorno =='0'){
				   JRJ.Alerts.alert({
						title: "消息提示",  // 标题
						autoClose: false,
						width:400,
						message: "刷新成功！",     // 提示语
						okCallback: callbackreload, //ok回调
  						cancelCallback: callbackreload //cancel回调，关闭时也触发
					 });
				   //重新刷新页面
  					return false;
  				  }else{
  					 JRJ.Alerts.alert({
							title: "消息提示",  // 标题
							autoClose: false,
							width:400,
							message: "刷新失败！"     // 提示语
					  });
  				  }
			 }else{
				 JRJ.Alerts.alert({
						title: "消息提示",  // 标题
						autoClose: false,
						width:400,
						message: "系统错误"     // 提示语
					  });
			 }	  
		}
		});
}
function callbackreload(){
	window.location.reload();
}
