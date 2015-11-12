/* 
 * dialog组件
 * @depend jquery.js
 * @author zihua.wang@jrj.com.cn
 **/

if(typeof JRJ == "undefined"){
	JRJ = {};
}

if(typeof JRJ.MultiCards=="undefined" || !JRJ.MultiCards){
	JRJ.MultiCards = {};
}; 

(function($){
	JRJ.MultiCards.Mask = function(config){
		var config = config || {};
		this.config = {
			opacity: 0.1,
			bgColor: "#000",
			isBodyHidden:true,
			zIndex: 9400
		};
		$.extend(this.config, config);
	};

	JRJ.MultiCards.Mask.prototype = {
		show: function(){
			var self = this, cfg = this.config;
			self.mask = $('<div class="multicards-mask"></div>');
			self.mask.css({
				position: "absolute",
				zIndex: cfg.zIndex,
				top: "0px",
				left: "0px",
				width: $(window).width(),
				height: $(document).height(),
				backgroundColor: cfg.bgColor,
				opacity: cfg.opacity
			});

			self.mask.bgiframe({
			    conditional: true,
			    width: $(window).width() + 'px',
			    height: $(document).height() + 'px'
			});

			self.resizeHandler = function(event){
				event.data.obj._resize();
			}

			self.mask.appendTo("body");
			if(cfg.isBodyHidden){
				$('body').css({'overflow':'hidden'});
			}

			$(window).bind("resize", {obj: self}, self.resizeHandler);
		},
		close: function(){
			this.mask.remove();
			$(window).unbind("resize", this.resizeHandler);
			$('body').css({'overflow':'auto'});
		},
		_resize: function(){
			this.mask.css({
				width: $(window).width(),
				height: $(document).height()
			});
			this.mask.bgiframe({
			    conditional: true,
			    width: $(window).width() + 'px',
			    height: $(document).height() + 'px'
			});
		}
	}
})(jQuery);

(function($){
	JRJ.MultiCards.DialogBase = function(config){
		this.config = {
			opacity: 0.5,
			bgColor: "#000",
			className: null,
			html: null,
			width:null,
			hasMask: true,     //是否需要罩层
			isBodyHidden:true,
			isFixed: true,     //是否fixed定位
			isSetPosition: true,   //是否使用默认的方式设置位置
			zIndex: 9400
		}
		$.extend(this.config, config);
		this.init();
	}

	JRJ.MultiCards.DialogBase.prototype = {
		init: function(){
			var self = this, cfg = this.config;
			
			self.outerDiv = $('<div class="multicards-dialog"><div class="mc-container"></div></div>');
			self.container = self.outerDiv.find(".mc-container");
			if(cfg.className){
				self.outerDiv.addClass(cfg.className); 
			}
			if(cfg.html){
				self.container.html(cfg.html);
			}
			if(cfg.width){
				self.outerDiv.css("width",cfg.width);
			}
			this.resetPositionHandler = function(event){
				event.data.obj.resetPosition();
			}

			if(cfg.isFixed){
				if($.browser.msie && parseInt($.browser.version) <= 6 ){
					$(window).bind("scroll", {obj: self}, self.resetPositionHandler);
				}else{
					self.outerDiv.css("position", "fixed");
				}
				$(window).bind("resize", {obj: self}, self.resetPositionHandler);
			}

			if(cfg.hasMask){
				self.mask = new JRJ.MultiCards.Mask({opacity: cfg.opacity, bgColor: cfg.bgColor, zIndex: cfg.zIndex,isBodyHidden:cfg.isBodyHidden});	
			}
			
			self.outerDiv.appendTo("body");
			
			//是否设置位置
			if(cfg.isSetPosition || cfg.isFixed){
				self.resetPosition();
			}
		},
		show: function(){
			var self = this;
			if(self.mask){
				self.mask.show();
			}
			self.outerDiv.show();		
		},
		close: function(){
			var self = this;
			self.outerDiv.remove();
			if(self.mask){				
				self.mask.close();
			}
			if(self.isFixed){
				$(window).unbind(self.resetPositionHandler);
			}
			var cfg=self.config;
			if(cfg!=undefined&&cfg.enableKeyCtrl){
				$(document).unbind("keyup");
			}
		},
		getEntity: function(){
			return this.outerDiv;
		},
		getContainer: function(){
			return this.container;
		},
		setHTML: function(html){
			var self = this;
			self.container.html(html);
		},
		setPosition: function(left, top){		
			this.outerDiv.css({
				top: top + 'px',
				left: left + 'px'
			});
		
		},
		resetPosition: function (isauto) {
		    var top = 10;//(($(window).height()/2) - (this.outerDiv.outerHeight()/2))-50;
			var left = ($(window).width()/2) - (this.outerDiv.outerWidth()/2) ;
			if(this.outerDiv.find('.mc-standard-titlebar').length > 0){
				if(isauto){
					var _w = $(window).height(),
						_div = this.outerDiv.outerHeight();
					top= (_w>_div)? (($(window).height()/2) - (this.outerDiv.outerHeight()/2))-50:100;
				}else{
					top=100;
				}
			}
			
			// IE6 fixed
			//if( $.browser.msie && parseInt($.browser.version) <= 6 ) top = top + $(window).scrollTop();			
			this.setPosition(left, top);
		}
	}
})(jQuery);

(function($){
	var ALERT_TMPL = '<div class="mc-content">{content}</div><div class="mc-btn-panel"><input class="mc-ok" type="button" value="确定" /></div>';
	var COMFIRM_TMPL = '<div class="mc-content">{content}</div><div class="mc-btn-panel"><input class="mc-ok" type="button" value="确定" /><input class="mc-cancel" type="button" value="取消" /></div>';
	var PROMPT_TMPL = '<div class="mc-content">{content}</div>';

	var DEFAULT_CONFIG = {
		message: "",
		trigger: null,
		hasMask: false,
		isFixed: false,   
		isSetPosition: false,
		hasAnimate: true
	};	
	JRJ.MultiCards.MiniAlerts = {
		/*
		 * @param {object}
		   config{
				message: "",  //提示语
				trigger: null, //触发锚点DOM
				hasAnimate: true  //是否要show hide动画效果
		   }
		 **/
		alert: function(config){		
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);

			self.config.hasMask = false;  //确保alert没有mask
			self.config.className = "mc-mini-alert";
			self.config.html = ALERT_TMPL.replace("{content}", self.config.message);

			self._init("alert");
		},
		/*
		 * @param {object}
		   config{
				message: "",  //提示语
				trigger: null, //触发锚点DOM
				hasAnimate: true  //是否要show hide动画效果
		   }
		 **/
		confirm: function(config){
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);

			self.config.hasMask = false;  //确保confirm没有mask
			self.config.className = "mc-mini-confirm";
			self.config.html = COMFIRM_TMPL.replace("{content}", self.config.message);

			self._init("confirm");
		},
		/*
		 * @param {object}
		   config{
				message: "",  //提示语
				trigger: null, //触发锚点DOM
				hasMask: false, //是否要罩层，默认为false
				hasAnimate: true  //是否要show hide动画效果
		   }
		 **/
		prompt: function(config){
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);

			self.config.className = "mc-mini-prompt";
			self.config.html = PROMPT_TMPL.replace("{content}", self.config.message);

			self._init("prompt");
		},
		_init: function(type){
			var self = this, cfg = self.config;

			self.trigger = cfg.trigger ? $(cfg.trigger) : null;

			if(self.isShow){//防多次点击
				self.close();
			}

			self.dlg = new JRJ.MultiCards.DialogBase( cfg );
			self.entity = self.dlg.getEntity();

			switch(type){
				case "alert":
					self.entity.find(".mc-ok").click(function(){
						if( cfg.callback ) cfg.callback();
						self._hide();
					});
					break ;
				case "confirm":
					self.entity.find(".mc-ok").click(function(){
						if( cfg.callback ) cfg.callback();
						self._hide();
					});
					self.entity.find(".mc-cancel").click(function(){
						self._hide();
					});
					break ;
				case "prompt":

					break ;
			}
			
			self._show(type);
		},
		_show: function(type){
			var self = this, offset;

			self.animateArgs = {};
			if(self.trigger){
				offset = self.trigger.offset();
				self.animateArgs.height = self.entity.height();
				self.animateArgs.left = offset.left;
				self.animateArgs.top = offset.top + self.trigger.height();
				self.animateArgs.startTop = offset.top;
			}else{
				self.animateArgs.height = self.entity.height();
				self.animateArgs.left = $(window).width()/2 - self.entity.outerWidth()/2;
				self.animateArgs.top = $(window).height()/2 - self.entity.outerHeight()/2 + $(window).scrollTop();
				self.animateArgs.startTop = $(window).height()/2 + self.entity.outerHeight()/2 + $(window).scrollTop();
			}
			
			//特殊，覆盖至编辑区域
			if(self.trigger && self.config.bCover){
				self.animateArgs.height = self.entity.height();
				self.animateArgs.left = $(self.trigger).offset().left + $(self.trigger).width()/2 - self.entity.outerWidth()/2;
				self.animateArgs.top = $(self.trigger).height()/2 - self.entity.outerHeight()/2 + $(self.trigger).offset().top;
				self.animateArgs.startTop = $(self.trigger).height()/2 + self.entity.outerHeight()/2 + $(self.trigger).offset().top;
			}
			
			if(self.dlg.mask){
				self.dlg.mask.show();
			}

			if(self.config.hasAnimate){
				self.dlg.setPosition(self.animateArgs.left, self.animateArgs.startTop);
				self.entity.css("height", 0).show();
				self.entity.animate({top: self.animateArgs.top, height: self.animateArgs.height}, 
					200,
					function(){});			
			}else{
				self.dlg.setPosition(self.animateArgs.left, self.animateArgs.top);
				self.entity.show();
			}
			self.isShow = true;
			if( type == "prompt" || type == "alert"){
				setTimeout(function(){self._hide();}, 3000);
			}
		},
		_hide: function(cfg){
			var self = this;

			if(self.config.hasAnimate){
				self.entity.animate({top:self.animateArgs.startTop, height: 0}, 
					200,
					function(){
						//if(self.config && self.config.callback ){
						//	self.config.callback();
						//}
						self.close();
					});			
			}else{
				//if(self.config && self.config.callback ){
				//	self.config.callback();
				//}
				self.close();
			}
		},
		close: function(){
			var self = this;
			if(self.dlg){
				if(self.dlg.mask){
					self.dlg.mask.close();
				}
				self.dlg.close();
				self.isShow = false;	
				
				if(self.trigger && self.config.bCover){
					self.trigger.focus();
				}
			}
		}
	}
})(jQuery);


(function($){
	var ALERT_TMPL = '<i class="mc-close" title="关闭">&nbsp;</i>'
			+'		<div class="mc-standard-titlebar middle"><i class="note"></i>{title}</div>'
			+'		<div class="mc-standard-body">'
			+'		<div class="mc-standard-content">{content}</div>'
			+'		<div class="mc-btn-panel">'
			+'			<input class="mc-ok" type="button" value="确定" />'
			+'		</div>'
			+'	</div>'
			+'	</div>'
			+'</div>';
	var CONFIRM_TMPL = '<i class="mc-close" title="关闭">&nbsp;</i>'
			+'		<div class="mc-standard-titlebar middle"><i class="note"></i>{title}</div>'
			+'		<div class="mc-standard-body">'
			+'			<div class="mc-standard-content">{content}</div>'
			+'			<div class="mc-btn-panel">'
			+'				<input class="confirm-ok btn_confirm" type="button" value="{sure}" /><input class="confirm-cancel btn_confirm" type="button" value="{cancel}" />'
			+'			</div>'
			+'		</div>'
			+'	</div>'
			+'</div>';
	var DEFAULT_CONFIG = {
		title: "提示",
		message: "",  //提示语
		zIndex: 9500,
		okCallback: null, //ok回调
		cancelCallback: null //cancel回调，关闭时也触发
	}
	JRJ.MultiCards.Alerts = {
		/*
		 * @param {object}
		   config{
				title: "提示",  //标题
				message: ""      //提示语
				okCallback: null, //ok回调
				cancelCallback: null //cancel回调，关闭时也触发
		   }
		 **/
		alert: function(config){
			var self = this, temp;
			self.config = $.extend({}, DEFAULT_CONFIG, config);

			self.config.className = "mc-alert";
			temp = ALERT_TMPL.replace("{title}", self.config.title || "提示");
			self.config.html = temp.replace("{content}", self.config.message);

			self._init("alert");
		},
		/*
		 * @param {object}
		   config{
				title: "提示框",  //标题
				message: "",      //提示语
				okCallback: null, //ok回调
				cancelCallback: null //cancel回调，关闭时也触发
		   }
		 **/
		confirm: function(config){
			var self = this, temp;
			self.config = $.extend({}, DEFAULT_CONFIG, config);
			//ie6 固定宽度
			self.config.width = "250";
			self.config.className = "mc-confirm";
			temp = CONFIRM_TMPL.replace("{title}", self.config.title || "确认提示")
								.replace("{sure}", self.config.okButton || "确定")
								.replace("{cancel}", self.config.cancelButton || "取消");
			
			self.config.html = temp.replace("{content}", self.config.message);

			self._init("confirm");
		},
		_init: function(type){
			var self = this, cfg = self.config;
			
			self.dlg = new JRJ.MultiCards.DialogBase( cfg );
			self.entity = self.dlg.getEntity();

			var okBtn, cancelBtn, closeBtn;
			closeBtn = self.entity.find(".mc-close");

			switch(type){
				case "alert":
					okBtn = self.entity.find(".mc-ok");
					
					okBtn.click(function(){
						if( cfg.okCallback ) cfg.okCallback();
						self._hide();
					});
					okBtn.keypress( function(e) {
						//enter esc退出
						if( e.keyCode == 13 || e.keyCode == 27 ) okBtn.trigger('click');
					});
					break ;
				case "confirm":
					okBtn = self.entity.find(".confirm-ok");
					cancelBtn = self.entity.find(".confirm-cancel");
					
					okBtn.click(function(){
						if( cfg.okCallback ) cfg.okCallback();
						self._hide();
					});
					cancelBtn.click(function(){
						self.close();
					});
					okBtn.keypress( function(e) {
						if( e.keyCode == 13 ) okBtn.trigger('click');
						else if( e.keyCode == 27 ) cancelBtn.trigger('click');
					});
					cancelBtn.keypress( function(e) {
						if( e.keyCode == 13 || e.keyCode == 27 )
						  cancelBtn.trigger('click');
					});
					break ;
			}

			closeBtn.click(function(){
				self.close();
			});
			
			self.isShow = true;
			self.dlg.show();
			okBtn.focus();	
			
			if(type == "alert"){
				//setTimeout(function(){self._hide();}, 3000);
			}
		},
		_hide: function(){
			this.dlg.close();
		},
		close: function(){
			var self = this, cfg = self.config;
			if( cfg.cancelCallback ) cfg.cancelCallback();
			self.isShow = false;	
			self.dlg.close();
		}
	}
})(jQuery);

(function($){
    var DEFAULT_CONFIG = {
        title: "对话框",
        titleIcon: '<i class="note"></i>',
        titleRight: '',
        bottomContent:'',
        content: "",
        loadingImg: '<div class="popup-mask"></div>',
        protocolHtml:null,
        btnPanelClass:'',
        okBtnText: "确定",
        cancelBtnText: "取消",
        hasCloseBtn: true,
        hasOkBtn: true,
        hasCancelBtn: true,
        okCallback: null,
        cancelCallback: null,
        trigger: null,
        offsetLeft: 0,
        offsetTop: 0,
        enableKeyCtrl: false,
        width:420,
        ifrScroll: 'no',
        ifrSrc: '',
        ifrClass: '',
        ifrHeight:null,
        ifrReHeight:false,
        scrollbar:null
    };
	JRJ.MultiCards.Dialogs = {
		/*
		 * @param {object}
		   config{
			    title: "对话框",
				content: "",
				okBtnText: "确定",
				cancelBtnText: "取消",
				hasCloseBtn: true,
				hasOkBtn: true,
				hasCancelBtn: true,
				okCallback: function,
				cancelCallback: function,
				isFixed: false  //默认为true
		   }
		 **/
		standardDialog: function(config){
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);

			var html = '';
		    html += ['<div class="mc-mask">',
		             '<div class="mc-mask-inner middle">',
		             	'<img src="../image/loading.gif" class="mr10" /><span>&nbsp;</span>',
		             '</div>',	
		             '<div class="mc-mask-inner-finish" style="display:none;">',
		             	'<p class="mc-mask-title">您的申请已经提交</p>',
		             	'<p class="mc-mask-cnt">请关闭此窗口，回到账户首页，5分钟后刷新页面</p>',
		             	'<p class="btn-wrap"><a class="btn btn-4" id="mc-mask-btn">完成</a></p>',
		             '</div>',
		             '</div>'].join('');
			if (self.config.hasCloseBtn) {
				html += '<i class="mc-close" title="关闭">&nbsp;</i>';
			}
			html += '<div class="mc-standard-titlebar middle">' + self.config.titleIcon + '<span>' + self.config.title + '</span></div>';
			html += '<div class="mc-standard-body">';
			html += '	<div class="mc-standard-content" style="width:' + self.config.width + 'px">';
			html += self.config.content;
			html += '	</div>';
			html += '	<div class="mc-btn-panel '+self.config.btnPanelClass+'">';
			if(self.config.hasCancelBtn){
				html += '<input class="mc-cancel" type="button" value="'+ self.config.cancelBtnText +'" />';
			}
			if(self.config.hasOkBtn){
				html += '<input class="mc-ok" type="button" value="'+ self.config.okBtnText +'" />';
			}
			html += '	</div>';
			html += '</div>';
			
			self.config.html = html;

			self._init("standard");
		},
		/*
		 * @param {object}
		   config{
				content: "",  //对话框内容
				hasCloseBtn: true,  //是否要关闭按钮
				isFixed: false  //是否fixed定位，默认为true
		   }
		 **/
		customDialog: function(config){
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);
			
			self.config.html = '<i class="mc-close" title="关闭">&nbsp;</i><div class="mc-content">'+ self.config.content +'</div>';

			self._init("custom");
		},
		/*
		 * @param {object}
		   config{
				content: "",  //对话框内容
				trigger: , //锚点DOM选择器
				offsetLeft: 0, //对话框定位的left偏移值
				offsetTop: 0   //对话框定位的top偏移值
		   }
		 **/
		miniDialog: function (config) {
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);
			
			self.config.hasMask = false;
			self.config.isFixed = false;
			self.config.isSetPosition = false;
			self.config.html = '<div class="mc-content">'+ self.config.content +'</div>';

			self._init("mini");
		},
		iframeDialog: function (config) {
		    var self = this;
		    self.config = $.extend({}, DEFAULT_CONFIG, config);

		    var html = '';
		    html += ['<div class="mc-mask">',
		             '<div class="mc-mask-inner middle">',
		             	'<img src="../image/loading.gif" class="mr10" /><span>&nbsp;</span>',
		             '</div>',	
		             '<div class="mc-mask-inner-finish" style="display:none;">',
		             	'<p class="mc-mask-title">您的申请已经提交</p>',
		             	'<p class="mc-mask-cnt">请关闭此窗口，回到账户首页，5分钟后刷新页面</p>',
		             	'<p class="btn-wrap"><a class="btn btn-4" id="mc-mask-btn">完成</a></p>',
		             '</div>',
		             '</div>'].join('');
		    if(self.config.protocolHtml){
		    	html+=['<div class="mc-protocol">',
		    	       '<a class="protocol-read has-read"></a>',
	    	       		'<h1>'+self.config.protocolHtml.title+'</h1>',
	    	       		'<div class="protocol-cnt">'+self.config.protocolHtml.content+'</div>',
	    	       		'<div class="jrj-tr mr30 protocol-btn"><a class="btn btn-4 has-read">已阅读</a></div>',
		    	       '</div>'].join('');
		    }
		    if (self.config.hasCloseBtn) {
		        html += '<i class="mc-close mc-close-new" title="关闭">&nbsp;</i>';
		    }
		    html += '<div class="mc-titlebar jrj-clear middle" style="width:' + (self.config.width-30) + 'px">';
		    html += '<i class="mc-title-icon jrj-fl"></i><span class="jrj-fl middle">' + self.config.title + '</span><span class="jrj-fl" style="font-weight:normal;font-size:14px;color:#808080;">（客服电话 400-166-1188）</span>';
		    //html += '<div class="mc-titlebar-r jrj-fr">' + self.config.titleRight + '</div>';
		    html += '</div>';
		    html += '<div class="mc-titleSteps jrj-tc">' + self.config.titleSteps + '</div>';
		    html += self.config.bottomContent;
		    html += '<div id="scrollbar-mc" class="mc-body">';
		    html += '	<div class="scrollbar scrollbar-mc"><div class="track"><div class="thumb"><div class="end"></div></div></div></div>';
		    html += '	<div class="mc-content viewport" style="width:' + self.config.width + 'px">';
		    html += '	<div class="mc-content-inner overview">';
		    html += self.config.content;
		    html += '	</div>';
		    html += '	</div>';
		    html += '	<div class="mc-btn-panel">';
		    if (self.config.hasCancelBtn) {
		        html += '<input class="mc-cancel" type="button" value="' + self.config.cancelBtnText + '" />'
		    }
		    if (self.config.hasOkBtn) {
		        html += '<input class="mc-ok" type="button" value="' + self.config.okBtnText + '" />'
		    }
		    html += '	</div>';
		    html += '</div>';

		    self.config.html = html;

		    self._init("iframeDialog");
		},
		_init: function(type){
			var self = this, cfg = self.config, trigger, offset;
			
			self.dlg = new JRJ.MultiCards.DialogBase( cfg );
			self.entity = self.dlg.getEntity();

			var okBtn, cancelBtn, closeBtn;
			okBtn = self.entity.find(".mc-ok");
			cancelBtn = self.entity.find(".mc-cancel");
			closeBtn = self.entity.find(".mc-close");

			switch(type){
				case "standard":
					okBtn.click(function(){				
						var notClose = false;
						if( cfg.okCallback ) notClose = cfg.okCallback();
						//1.返回true不关闭对话框，2.无返回或返回false则关闭
						if(!notClose){
							self._hide();
						}
					});
					cancelBtn.click(function(){
						self.close();
					});
					okBtn.keypress( function(e) {
						if( e.keyCode == 13 ) okBtn.trigger('click');
						else if( e.keyCode == 27 ) cancelBtn.trigger('click');
					});
					cancelBtn.keypress( function(e) {
						if( e.keyCode == 13 || e.keyCode == 27 )
						  cancelBtn.trigger('click');
					});
					closeBtn.click(function(){
						self.close();
					});

					if (cfg.enableKeyCtrl) {
					    $(document).keyup(function (e) {
					        if (self.isShow) {
					            if (e.keyCode == 27) { // esc keycode
					                self.close();
					            } else if (e.keyCode == 13) {// enter keycode
					                okBtn.trigger('click');
					            }
					        }
					    });
					}
					
					if(cfg.isSetPosition){
						self.dlg.resetPosition(true);
					}
					self._thisDialog = self;
					break ;
				case "custom": 
					closeBtn.click(function(){
						self.close();
					});
					break ;
				case "mini":
					trigger = $(cfg.trigger);
					offset = trigger.offset();
					self.dlg.setPosition(offset.left - self.entity.outerWidth()/2 + trigger.outerWidth()/2 + cfg.offsetLeft, offset.top - self.entity.outerHeight() + cfg.offsetTop);
					break;
			    case "iframeDialog":
			        //ie6设style="overflow-x: hidden; overflow-y: visible;"还是有横滚。
			        self.contentIframe = $('<iframe frameborder="0" scrolling="no" allowtransparency="true"></iframe>');
			        //给iframe绑定this，供其子页调用对话框方法。
			        self.contentIframe[0]._thisDialog = self;
			        self.contentIframe.attr("scrolling", cfg.ifrScroll);
			        self.contentIframe.attr("src", cfg.ifrSrc);
			        self.contentIframe.css({ "width": self.entity.find('.mc-content').width(), backgroundColor: "transparent"});//, 'height': cfg.ifrHeight
			        if (cfg.ifrClass) { self.contentIframe.addClass(cfg.ifrClass); }

			        if (cfg.ifrReHeight) {
			            this.contentIframe.bind("load", { obj: self }, function (event) {
			                var thisObj = event.data.obj;
			                thisObj.resizeIfrH();
			            });
			        }
			        self.entity.find(".mc-content-inner").append(self.contentIframe);
			        
			        
			        self.dlg.resetPosition();

                    
			        okBtn.click(function () {
			            var notClose = false;
			            if (cfg.okCallback) notClose = cfg.okCallback();
			            //1.返回true不关闭对话框，2.无返回或返回false则关闭
			            if (!notClose) {
			                self._hide();
			            }
			        });
			        cancelBtn.click(function () {
			            self.close();
			        });
			        okBtn.keypress(function (e) {
			            if (e.keyCode == 13) okBtn.trigger('click');
			            else if (e.keyCode == 27) cancelBtn.trigger('click');
			        });
			        cancelBtn.keypress(function (e) {
			            if (e.keyCode == 13 || e.keyCode == 27)
			                cancelBtn.trigger('click');
			        });
			        closeBtn.click(function () {
			            self.close();
			        });
			        
			        self.entity.find(".has-read").click(function(){
			        	self.hideProtocol();
			        });

			        if (cfg.enableKeyCtrl) {
			            $(document).keyup(function (e) {
			                if (self.isShow) {
			                    if (e.keyCode == 27) { // esc keycode
			                        self.close();
			                    } else if (e.keyCode == 13) {// enter keycode
			                        okBtn.trigger('click');
			                    }
			                }
			            });
			        }

			        break;
			}

			self.dlg.show();
			self.isShow = true;
			okBtn.focus();
		},
		_hide: function(){
			this.dlg.close();
		},
		close: function(){
			var self = this, cfg = self.config, notClose = false;
			if(cfg.cancelCallback){
				notClose=cfg.cancelCallback();
			}
			if(!notClose){
			self.dlg.close();
			self.isShow = false;
			}
		},
		setTitle: function(title){
			this.entity.find(".mc-titlebar").html(title);
		},
		setContent: function(content){
			this.entity.find(".mc-content").html(content);
		},
		showLoading: function () {
		    var self = this;
			this.entity.find(".mc-mask").show();
			this.entity.find(".mc-mask .mc-mask-inner-finish").hide();
		    setTimeout(function(){
		    	self.entity.find(".mc-mask span").removeClass('f14').text('正在提交,请稍候');
		    	setTimeout(function(){
		    		self.entity.find(".mc-mask span").text('正在提交中,请继续等待');
		    		setTimeout(function(){
		    			self.entity.find(".mc-mask .mc-mask-inner").hide();
		    			self.entity.find(".mc-mask").css({top:0});
		    			self.entity.find(".mc-mask .mc-mask-inner-finish").show();
		    			self.setStep('last');
		    			self.entity.find(".mc-mask .mc-mask-inner-finish").find('#mc-mask-btn').click(function(){
		    				self.close();
		    			});
		    		},7000);
		    	},5000);
		    },3000);
		},
		waiting: function () {
		    var self = this;
			this.entity.find(".mc-mask").show();
			self.entity.find(".mc-mask span").addClass('f14').html('信息已提交，请稍后。<br/>提交期间请勿刷新此页面。');
		},
		hideLoading: function () {
		    this.entity.find(".mc-mask").hide();
		},
		showProtocol: function () {
			var _div = this.entity.find(".mc-protocol");
			this.entity.find(".protocol-cnt").css({height:this.entity.height()-144});
		    _div.css({left:_div.width()}).show().animate({left:0},1000,function(){});
		},
		hideProtocol: function () {
			var _div = this.entity.find(".mc-protocol");
		    _div.animate({left:_div.width()},500,function(){
		    	$(this).hide();
		    });
		},
		setBottomContent: function (html) {
			this.entity.find(".popup-note-mc").find('ol').html(html);
		},
		showTip:function(){
			this.entity.find(".popup-note").show();
		},
		hideTip:function (){
			this.entity.find(".popup-note").hide();
		},
	    /**
		 * 自适应iframe对话框高度
		 */
		resizeIfrH: function () {
		    var win = this.contentIframe[0].contentWindow;
		    var doc = win.document;
		    var height = "auto";
		    if (doc.compatMode == 'CSS1Compat') { height = $(doc.documentElement).find('body').outerHeight(); }
		    else if (doc.body) { height = $(doc.body).outerHeight(); }
		    this.contentIframe.height(height);


		    if(height<409){
		    	this.entity.find(".mc-content").css({'height':height});	
		    	if(this.scrollbar != null){
		    		this.scrollbar.tinyscrollbar_update({trackSize:height}); 
		    	}else{
		    		this.scrollbar = $('#scrollbar-mc').tinyscrollbar({trackSize:height});
		    	} 	
		    }else{
		    	this.entity.find(".mc-content").css({'height':409});
		    	if(this.scrollbar != null){
		    		this.scrollbar = $('#scrollbar-mc').tinyscrollbar_update({trackSize:409});
		    	}else{
		    		this.scrollbar = $('#scrollbar-mc').tinyscrollbar({trackSize:409});
		    	}
		    }
		    
		},
	    /**
		 * 获取iframe对话框的iframe元素
		 */
		getIfrDlgWin: function () {
		    if (this.type == "iframeDialog") {
		        return this.contentIframe;
		    }
		    return null;
		}
	}
})(jQuery);
