/* 
* 类名称：common.js
* 类描述：通用脚本
* 创建人：yu.hongda
* 创建时间：2013-06-25
* 修改人：
* 修改时间：
* 修改备注：
* @version 1.0
*/

$(function () {
	$.ajaxSetup({
		 dataFilter:function(data){ 
			 if(typeof data!="object"&&data=='_login'){
				 window.location.href="/stock/login.jspa";
			 }else{
				 return data;
			 }
		 }
		});
    $('.tooltip').live('mouseenter',function () {
        var _left = $(this).position().left + $(this).width(),
            _top = $(this).position().top+5;
        $(this).find('.tooltip-content').css({ left: _left + 'px', top: _top + 'px' }).fadeIn(300, function () {
            var self = this;
//            setTimeout(function () {
//                $(self).hide();
//            }, 3000);
        });
        $(this).find('i').addClass('block-title-info-cur');
    }).live('mouseleave',function () {
        $(this).find('.tooltip-content').hide();
        $(this).find('i').removeClass('block-title-info-cur');
    });
    
    $('.tooltip-2' ).live('mouseenter' ,function () {
        var _left = $( this).position().left + $(this).width()+10,
            _top = $( this).position().top;
        $(this).find('.tooltip-content').css({ left: _left + 'px', top: _top + 'px' }).fadeIn(300, function () {
            var self = this;
        });
        $(this).find('i').addClass('block-title-info-cur');
    });
    $(document).mousedown(function (e) {
     e.stopPropagation();
        if (!($(e.target).parents( ".tooltip-content").length > 0)) {
           $('.tooltip-content').hide();
           $('.block-title-info').removeClass('block-title-info-cur');
        }
    });
 

    $('.tooltip-bottom').live('click', function (e) {
    	e.stopPropagation();
        //$('.tooltip-content').hide();

        var _left = $(this).position().left - $(this).find('.tooltip-content').width() + 12,
            _top = $(this).position().top + 30;
        $(this).find('.tooltip-content').css({ left: _left + 'px', top: _top + 'px' }).fadeIn(300, function () {
            var self = this;
        });
    });
    $(document).mousedown(function (e) {
    	e.stopPropagation();
        if (!($(e.target).parents(".tooltip-content").length > 0)) {
        	$('.tooltip-content').hide();
        }
    });

    $('.popup-note-more').live('click', function () {
        var cls = 'popup-note-expand',
            clsCur = 'popup-note-more-cur';
        if ($(this).parent().hasClass(cls)) {
            $(this).parent().removeClass(cls);
            $(this).removeClass(clsCur);
        } else {
            $(this).parent().addClass(cls);
            $(this).addClass(clsCur);
        }
    });

    var from=getPara('from'	);
    $("._xjb-from").each(function(i,v){
    	
    	if(from!=null||from!=""){
    		$(v).attr('href',urlAppend($(v).attr('href'),'from='+from));
    	}
		
    	
//    	if(!isEmpty($(v).attr('href')) && !isEmpty(from) && $(v).attr('href').indexOf('from')<0){
//    		$.cookie('_from', from, {path:'/'});
//    		$(v).attr('href',urlAppend($(v).attr('href'),'from='+from));
//    	}else{
//    		var _from = $.cookie('_from');
//    		if(!isEmpty($(v).attr('href')) && !isEmpty(_from) && $(v).attr('href').indexOf('from')<0){
//        		$(v).attr('href',urlAppend($(v).attr('href'),'from='+_from));
//        	}
//    	}
    });
    
	$(".xjb-trade-form").bind("submit",function(){
		XJB.Utils.md5Pwd(".xjb-trade-pwd");
		var dlg = frameElement._thisDialog;
//    		dlg.showLoading();
	});
});
(function($){
	JRJ.Dialogs.currStep==1;
	JRJ.Dialogs.totalStep=1;
	JRJ.Dialogs.setStep = function(step){
		if(step==='last'){
			JRJ.Dialogs.currStep = $(".popup-title-step").length;
			$(".popup-title-step-cur").removeClass("popup-title-step-cur");
			$(".popup-title-step:last").addClass("popup-title-step-cur");
		}else{
			JRJ.Dialogs.currStep=step;
			$(".popup-title-step-cur").removeClass("popup-title-step-cur");
			$(".popup-title-step[index="+step+"]").addClass("popup-title-step-cur");
		}
		if(JRJ.Dialogs.currStep>1){
			this.hideTip();
		}else{
			this.showTip();
		}
	};
	JRJ.MultiCards.Dialogs.currStep = 1;
	JRJ.MultiCards.Dialogs.totalStep = 1;
	JRJ.MultiCards.Dialogs.setStep = function(step){
		if(step==='last'){
			JRJ.MultiCards.Dialogs.currStep = $(".popup-title-step-mc").length;
			$(".popup-title-step-mc-cur").removeClass("popup-title-step-mc-cur");
			$(".popup-title-step-mc:last").addClass("popup-title-step-mc-cur");
		}else{
			JRJ.MultiCards.Dialogs.currStep=step;
			$(".popup-title-step-mc-cur").removeClass("popup-title-step-mc-cur");
			$(".popup-title-step-mc[index="+step+"]").addClass("popup-title-step-mc-cur");
		}
		if(JRJ.MultiCards.Dialogs.currStep>1){
			this.hideTip();
		}else{
			this.showTip();
		}
	};
})(jQuery);
function showPwdError(config){
	var defaultCf={
			prevDiv:null,
			content:"很抱歉，您的密码输入错误，请重新输入",
			prevSrc:"",
			heightCss:""
	};
	defaultCf=$.extend({},defaultCf,config);
	var tpl=['<div class="popup-wrap-finish _errordiv">',
        '<div class="'+defaultCf.heightCss+'">',
    	'<div class="site-cnt-note jrj-tc" >',
        '    <div class="middle">',
        '        <i class="icon step-error"></i>',
        '        <span>'+defaultCf.content+'</span>',
        '    </div>',
        '</div>',
        '</div>',
        '<div class="jrj-tc btn-wrap"><a class="btn btn-back-mc" id="_prev"></a></div>',
		'</div>'].join("");
	if(defaultCf.prevDiv!=null){
		$(defaultCf.prevDiv).hide();
	}
	$(defaultCf.prevDiv).after(tpl);
	var dlg = frameElement._thisDialog;
	$("#_prev").live('click',function(){
		$("._errordiv").remove();
		$(defaultCf.prevDiv).show();
		dlg.setStep(1);
		dlg.resizeIfrH();
	})
	dlg.setStep(2);
	dlg.resizeIfrH();
};

function showDialog(config){
	var defaultCf={
			width:750,
			title:"",
			step:[],
			ifrSrc:"",
			content:"",
			isFixed:true,
			hasDashedline:true,
			protocolHtml:'',
			callback:null,
			okBtnText: '确 认',
			hasOkBtn: false,
			okCallback: function () {
		            //JRJ.Dialogs.showLoading();
		            JRJ.Dialogs.close();
		            return true;
		        }
	
	};
	defaultCf=$.extend({},defaultCf,config);
	var tright="";
	JRJ.Dialogs.totalStep=defaultCf.step.length;
	for(var i=0;i<defaultCf.step.length;i++){
		if(i!=0){
			tright=tright+'<span class="popup-title-arrow"></span>';
		}
		tright=tright+'<span class="popup-title-step '+(i==0?"popup-title-step-cur":"")+'" index="'+(i+1)+'"><i>'+(i+1)+'</i><span>'+defaultCf.step[i]+'</span></span>';
	}
	
	var bottomContent = '';
	if(defaultCf.content!=""){
		bottomContent=[(defaultCf.hasDashedline?'':''),
                       '<div class="popup-note">',
                       '<p class="popup-note-title">温馨提示</p>',
                       //'<a class="link popup-note-more middle"><span>了解更多</span><i></i></a>',
                       defaultCf.content,
                   '</div>'].join('');
	}
	
	
	JRJ.Dialogs.iframeDialog({
        content: [''].join(''),
        loadingImg: '<div class="popup-mask"></div>',
        width: defaultCf.width,
        hasOkBtn: defaultCf.hasOkBtn,
        okBtnText: defaultCf.okBtnText,
        title:defaultCf.title,
        titleRight: ['<div class="middle">',
                     tright,
                    '</div>'].join(''),
        bottomContent: bottomContent,
        protocolHtml: defaultCf.protocolHtml,
        hasCancelBtn: false,
        enableKeyCtrl: true,
        ifrSrc: defaultCf.ifrSrc,
        ifrReHeight: true,
        isFixed:true,//defaultCf.isFixed,
        okCallback: function(){
        	defaultCf.okCallback();
        },
        cancelCallback: function () {
//        	location.reload();
        	if(JRJ.Dialogs.currStep>=1&&JRJ.Dialogs.currStep<JRJ.Dialogs.totalStep){
        		cancelConfirm();
	        }else{
	        	if(defaultCf.callback!=null){
	        		return defaultCf.callback();
	        	}else{
	        		window.location=location.protocol+"//"+location.host+location.pathname+location.search;
//	        	location.reload();
	        	}
	        }
           return true;
        }
    });
}

function cancelConfirm(){
	if(!JRJ.Alerts.isShow){
		JRJ.Alerts.confirm({message:"确定取消本次操作吗？",okCallback:function(){
			JRJ.Dialogs.dlg.close();
			JRJ.Dialogs.dlg.isShow = false;
			JRJ.Alerts.isShow = false;
		}});
	}
	return true;
}

//多卡弹窗
function showDialogMC(config){
	var defaultCf={
			width:800,
			title:"",
			step:[],
			ifrSrc:"",
			content:"",
			isFixed:true,
			hasDashedline:true,
			protocolHtml:'',
			callback:null
	};
	defaultCf=$.extend({},defaultCf,config);
	var _titleSteps="";
	JRJ.MultiCards.Dialogs.totalStep=defaultCf.step.length;
	for(var i=0;i<defaultCf.step.length;i++){
		if(i!=0){
			_titleSteps=_titleSteps+'<span class="popup-title-arrow-mc"></span>';
		}
		_titleSteps=_titleSteps+'<span class="popup-title-step-mc '+(i==0?"popup-title-step-mc-cur":"")+'" index="'+(i+1)+'"><i>'+(i+1)+'</i><span>'+defaultCf.step[i]+'</span></span>';
	}
	
	var bottomContent = '';
	if(defaultCf.content!=""){
		bottomContent=['<div class="popup-note-mc">',
                       '<p class="popup-note-title-mc">温馨提示:</p>',
                       defaultCf.content,
                   '</div>'].join('');
	}
	
	
	JRJ.MultiCards.Dialogs.iframeDialog({
        content: [''].join(''),
        loadingImg: '<div class="popup-mask"></div>',
        width: defaultCf.width,
        hasOkBtn: false,
        okBtnText: '确 认',
        title:defaultCf.title,
//        titleRight: ['<div class="middle">',
//                     tright,
//                    '</div>'].join(''),
        titleSteps: ['<div class="middle">',
                     _titleSteps,
                    '</div>'].join(''),
        bottomContent: bottomContent,
        protocolHtml: defaultCf.protocolHtml,
        hasCancelBtn: false,
        enableKeyCtrl: true,
        ifrSrc: defaultCf.ifrSrc,
        ifrReHeight: true,
        isFixed:true,//defaultCf.isFixed,
        okCallback: function () {
            //JRJ.Dialogs.showLoading();
            JRJ.MultiCards.Dialogs.close();
            return true;
        },
        cancelCallback: function () {
//        	location.reload();
        	if(JRJ.MultiCards.Dialogs.currStep>=1&&JRJ.MultiCards.Dialogs.currStep<JRJ.MultiCards.Dialogs.totalStep){
        		cancelConfirmMC();
	        }else{
	        	if(defaultCf.callback!=null){
	        		return defaultCf.callback();
	        	}else{
	        		window.location=location.protocol+"//"+location.host+location.pathname+location.search;
//	        	location.reload();
	        	}
	        }
           return true;
        }
    });
}

function cancelConfirmMC(){
	if(!JRJ.MultiCards.Alerts.isShow){
		JRJ.MultiCards.Alerts.confirm({message:"确定取消本次操作吗？",okCallback:function(){
			JRJ.MultiCards.Dialogs.dlg.close();
			JRJ.MultiCards.Dialogs.dlg.isShow = false;
			JRJ.MultiCards.Dialogs.scrollbar = null;
			JRJ.MultiCards.Alerts.isShow = false;
		}});
	}
	return true;
}

function showErr(select,message){
	$(select).addClass("txtbox-error");
	if($(select).nextAll(".table-error-mc").length > 0){
		$(select).nextAll(".table-error-mc").addClass("table-error");
		if(message!=undefined){
			$(select).nextAll(".table-error-mc").empty().html(message);
		}
	}else{
		$(select).parent().next(".table-note").addClass("table-error");
		if(message!=undefined){
			$(select).parent().next(".table-note").empty().html(message);
		}
	}
	
	$(select).nextAll(".validator").empty().html('<i class="icon error"></i>');
}
function hideErr(select,message){
	$(select).removeClass("txtbox-error");
	
	if($(select).nextAll(".table-error-mc").length > 0){
		$(select).nextAll(".table-error-mc").removeClass("table-error");
		if(message!=undefined){
			$(select).nextAll(".table-error-mc").empty().html(message);
		}
	}else{
		$(select).parent().next(".table-note").removeClass("table-error");
		if(message!=undefined){
			$(select).parent().next(".table-note").empty().text(message);
		}
	}

	$(select).nextAll(".validator").empty();
}


(function(window){
	var Confirm=function(targetid, title, rows, formid){
		var content=[];
		content.push('<div class="popup-height-saveM">');
		content.push('<h2 class="popup-wrap-mc-title dashline-mc">'+title+'</h2>');
		content.push('<table class="table-3 table-3-padding5">');
		$.each(rows,function(i,v){
			content.push('<tr >');
			content.push('<td class="jrj-tr" style="width:150px">'+v.name+'</td>');
			content.push('<td class="jrj-tl">'+v.value+'</td>');
			content.push('</tr>');
		});
		content.push('</table>');
		content.push('</div>');
		content.push('<div class="jrj-tr btn-wrap"><a class="btn btn-next" onclick="$(\'#'+formid+'\').submit(); return false;"></a></div>');
		$('#'+targetid).html(content.join(''));
		return $('#'+targetid);
	};
	window.Confirm=Confirm;
})(window);

function ajaxCheckPassword(password){
	var re=true;
	if(password==undefined){
		re=false;
	}
	password=XJB.Utils.md5(password);
	$.ajax({
		type : "post",
		url : XJB.Urls.checkPassword,
		data :{
			"password" : password
			  },
		dataType : "json",
		timeout : 5000,
		cache : false,
		async:false,
		success : function(data, textStatus) {
			if (typeof(data) != 'undefined' && data != null && data != 'null') {
				if(data.status == 1){
					if (data.retcode == 0) {
						re=true;
					}else{
						re=false;
					}
				}else{
					re=false;
				}
			}else{
				re=false;
			}
		}
	});
	return re;
}


