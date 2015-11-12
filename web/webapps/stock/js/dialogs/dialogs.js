/* 
 * dialog组件
 * @depend jquery.js
 * @author zihua.wang@jrj.com.cn
 **/

if(typeof JRJ == "undefined"){
	JRJ = {}
}

(function($){
	JRJ.Mask = function(config){
		var config = config || {};
		this.config = {
			opacity: 0.1,
			bgColor: "#000",
			isBodyHidden:true,
			zIndex: 9400
		}
		$.extend(this.config, config);
	}

	JRJ.Mask.prototype = {
		show: function(){
			var self = this, cfg = this.config;
			self.mask = $('<div class="album-mask"></div>');
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
	JRJ.DialogBase = function(config){
		this.config = {
			opacity: 0.5,
			bgColor: "#000",
			className: null,
			html: null,
			hasMask: true,     //是否需要罩层
			isBodyHidden:true,
			isFixed: true,     //是否fixed定位
			isSetPosition: true,   //是否使用默认的方式设置位置
			zIndex: 9400
		}
		$.extend(this.config, config);
		this.init();
	}

	JRJ.DialogBase.prototype = {
		init: function(){
			var self = this, cfg = this.config;
			
			self.outerDiv = $('<div class="album-dialog"><div class="md-container"></div></div>');
			self.container = self.outerDiv.find(".md-container");
			if(cfg.className){
				self.outerDiv.addClass(cfg.className); 
			}
			if(cfg.html){
				self.container.html(cfg.html);
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
				self.mask = new JRJ.Mask({opacity: cfg.opacity, bgColor: cfg.bgColor, zIndex: cfg.zIndex,isBodyHidden:cfg.isBodyHidden});	
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
			if(this.outerDiv.find('.md-standard-titlebar').length > 0){
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
	var ALERT_TMPL = '<div class="md-content">{content}</div><div class="md-btn-panel"><input class="md-ok" type="button" value="确定" /></div>';
	var COMFIRM_TMPL = '<div class="md-content">{content}</div><div class="md-btn-panel"><input class="md-ok" type="button" value="确定" /><input class="md-cancel" type="button" value="取消" /></div>';
	var PROMPT_TMPL = '<div class="md-content">{content}</div>';

	var DEFAULT_CONFIG = {
		message: "",
		trigger: null,
		hasMask: false,
		isFixed: false,   
		isSetPosition: false,
		hasAnimate: true
	};	
	JRJ.MiniAlerts = {
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
			self.config.className = "md-mini-alert";
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
			self.config.className = "md-mini-confirm";
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

			self.config.className = "md-mini-prompt";
			self.config.html = PROMPT_TMPL.replace("{content}", self.config.message);

			self._init("prompt");
		},
		_init: function(type){
			var self = this, cfg = self.config;

			self.trigger = cfg.trigger ? $(cfg.trigger) : null;

			if(self.isShow){//防多次点击
				self.close();
			}

			self.dlg = new JRJ.DialogBase( cfg );
			self.entity = self.dlg.getEntity();

			switch(type){
				case "alert":
					self.entity.find(".md-ok").click(function(){
						if( cfg.callback ) cfg.callback();
						self._hide();
					});
					break ;
				case "confirm":
					self.entity.find(".md-ok").click(function(){
						if( cfg.callback ) cfg.callback();
						self._hide();
					});
					self.entity.find(".md-cancel").click(function(){
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
	var ALERT_TMPL = '<i class="md-close" title="关闭">&nbsp;</i>'
			+'		<div class="md-standard-titlebar middle"><i class="note"></i>{title}</div>'
			+'		<div class="md-standard-body">'
			+'		<div class="md-standard-content">{content}</div>'
			+'		<div class="md-btn-panel">'
			+'			<input class="md-ok" type="button" value="确定" />'
			+'		</div>'
			+'	</div>'
			+'	</div>'
			+'</div>';
	var CONFIRM_TMPL = '<i class="md-close" title="关闭">&nbsp;</i>'
			+'		<div class="md-standard-titlebar middle"><i class="note"></i>{title}</div>'
			+'		<div class="md-standard-body">'
			+'			<div class="md-standard-content">{content}</div>'
			+'			<div class="md-btn-panel">'
			+'				<input class="confirm-ok btn_confirm" type="button" value="{sure}" /><input class="confirm-cancel btn_confirm" type="button" value="{cancel}" />'
			+'			</div>'
			+'		</div>'
			+'	</div>'
			+'</div>';
	var DEFAULT_CONFIG = {
		title: "提示",
		message: "",  //提示语
		width: null,
		zIndex: 9500,
		autofocus: true,
		okCallback: null, //ok回调
		cancelCallback: null //cancel回调，关闭时也触发
	}
	JRJ.Alerts = {
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

			self.config.className = "md-alert";
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

			self.config.className = "md-confirm";
			temp = CONFIRM_TMPL.replace("{title}", self.config.title || "确认提示")
								.replace("{sure}", self.config.okButton || "确定")
								.replace("{cancel}", self.config.cancelButton || "取消");
			
			self.config.html = temp.replace("{content}", self.config.message);

			self._init("confirm");
		},
		_init: function(type){
			var self = this, cfg = self.config;
			
			self.dlg = new JRJ.DialogBase( cfg );
			self.entity = self.dlg.getEntity();

			if(cfg.width != null){
				self.entity.width(cfg.width);
				self.dlg.resetPosition(true);
			}
			
			var okBtn, cancelBtn, closeBtn;
			closeBtn = self.entity.find(".md-close");

			switch(type){
				case "alert":
					okBtn = self.entity.find(".md-ok");
					
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
			if (!self.config.autofocus) {
			    okBtn.blur();
			}
			
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
        ifrReHeight:false
    };
	JRJ.Dialogs = {
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
		    html += ['<div class="md-mask">',
		             '<div class="md-mask-inner middle">',
		             	'<img src="../image/loading.gif" class="mr10" /><span>&nbsp;</span>',
		             '</div>',	
		             '<div class="md-mask-inner-finish" style="display:none;">',
		             	'<p class="md-mask-title">您的申请已经提交</p>',
		             	'<p class="md-mask-cnt">请关闭此窗口，回到账户首页，5分钟后刷新页面</p>',
		             	'<p class="btn-wrap"><a class="btn btn-4" id="md-mask-btn">完成</a></p>',
		             '</div>',
		             '</div>'].join('');
			if (self.config.hasCloseBtn) {
				html += '<i class="md-close" title="关闭">&nbsp;</i>';
			}
			html += '<div class="md-standard-titlebar middle">' + self.config.titleIcon + '<span>' + self.config.title + '</span></div>';
			html += '<div class="md-standard-body">';
			html += '	<div class="md-standard-content" style="width:' + self.config.width + 'px">';
			html += self.config.content;
			html += '	</div>';
			html += '	<div class="md-btn-panel '+self.config.btnPanelClass+'">';
			if(self.config.hasCancelBtn){
				html += '<input class="md-cancel" type="button" value="'+ self.config.cancelBtnText +'" />';
			}
			if(self.config.hasOkBtn){
				html += '<input class="md-ok" type="button" value="'+ self.config.okBtnText +'" />';
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
			
			self.config.html = '<i class="md-close" title="关闭">&nbsp;</i><div class="md-content">'+ self.config.content +'</div>';

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
			self.config.html = '<div class="md-content">'+ self.config.content +'</div>';

			self._init("mini");
		},
		iframeDialog: function (config) {
		    var self = this;
		    self.config = $.extend({}, DEFAULT_CONFIG, config);

		    var html = '';
		    html += ['<div class="md-mask">',
		             '<div class="md-mask-inner middle">',
		             	'<img src="../image/loading.gif" class="mr10" /><span>&nbsp;</span>',
		             '</div>',	
		             '<div class="md-mask-inner-finish" style="display:none;">',
		             	'<p class="md-mask-title">您的申请已经提交</p>',
		             	'<p class="md-mask-cnt">请关闭此窗口，回到账户首页，5分钟后刷新页面</p>',
		             	'<p class="btn-wrap"><a class="btn btn-4" id="md-mask-btn">完成</a></p>',
		             '</div>',
		             '</div>'].join('');
		    if(self.config.protocolHtml){
		    	html+=['<div class="md-protocol">',
		    	       '<a class="protocol-read has-read"></a>',
	    	       		'<h1>'+self.config.protocolHtml.title+'</h1>',
	    	       		'<div class="protocol-cnt">'+self.config.protocolHtml.content+'</div>',
	    	       		'<div class="jrj-tr mr30 protocol-btn"><a class="btn btn-4 has-read">已阅读</a></div>',
		    	       '</div>'].join('');
		    }
		    if (self.config.hasCloseBtn) {
		        html += '<i class="md-close" title="关闭">&nbsp;</i>';
		    }
		    html += '<div class="md-titlebar jrj-clear" style="width:' + self.config.width + 'px">';
		    html += '<span class="jrj-fl middle">' + self.config.title + '</span>';
		    html += '<div class="md-titlebar-r jrj-fr">' + self.config.titleRight + '</div>';
		    html += '</div>';
		    html += '<div class="md-body">';
		    html += '	<div class="md-content" style="width:' + self.config.width + 'px">';
		    html += '	<div class="md-content-inner">';
		    html += self.config.content;
		    html += '	</div>';
		    html += '	</div>';
		    html += '	<div class="md-btn-panel">';
		    if (self.config.hasCancelBtn) {
		        html += '<input class="md-cancel" type="button" value="' + self.config.cancelBtnText + '" />'
		    }
		    if (self.config.hasOkBtn) {
		        html += '<input class="md-ok" type="button" value="' + self.config.okBtnText + '" />'
		    }
		    html += '	</div>';
		    html += '</div>';

		    self.config.html = html;

		    self._init("iframeDialog");
		},
		_init: function(type){
			var self = this, cfg = self.config, trigger, offset;
			
			self.dlg = new JRJ.DialogBase( cfg );
			self.entity = self.dlg.getEntity();

			var okBtn, cancelBtn, closeBtn;
			okBtn = self.entity.find(".md-ok");
			cancelBtn = self.entity.find(".md-cancel");
			closeBtn = self.entity.find(".md-close");

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
			        self.contentIframe = $('<iframe frameborder="0" allowtransparency="true"></iframe>');
			        //给iframe绑定this，供其子页调用对话框方法。
			        self.contentIframe[0]._thisDialog = self;
			        self.contentIframe.attr("scrolling", cfg.ifrScroll);
			        self.contentIframe.attr("src", cfg.ifrSrc);
			        self.contentIframe.css({ "width": self.entity.find('.md-content').width(), backgroundColor: "transparent","height":'457px' });//, 'height': cfg.ifrHeight
			        if (cfg.ifrClass) { self.contentIframe.addClass(cfg.ifrClass); }

			        if (cfg.ifrReHeight) {
			            this.contentIframe.bind("load", { obj: self }, function (event) {
			                var thisObj = event.data.obj;
			                thisObj.resizeIfrH();
			            });
			        }
			        self.entity.find(".md-content-inner").append(self.contentIframe).append(self.config.bottomContent);
			        
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
			this.entity.find(".md-titlebar").html(title);
		},
		setContent: function(content){
			this.entity.find(".md-content").html(content);
		},
		showLoading: function () {
		    var self = this;
			this.entity.find(".md-mask").show();
			this.entity.find(".md-mask .md-mask-inner-finish").hide();
		    setTimeout(function(){
		    	self.entity.find(".md-mask span").removeClass('f14').text('正在提交,请稍候');
		    	setTimeout(function(){
		    		self.entity.find(".md-mask span").text('正在提交中,请继续等待');
		    		setTimeout(function(){
		    			self.entity.find(".md-mask .md-mask-inner").hide();
		    			self.entity.find(".md-mask").css({top:0});
		    			self.entity.find(".md-mask .md-mask-inner-finish").show();
		    			self.setStep('last');
		    			self.entity.find(".md-mask .md-mask-inner-finish").find('#md-mask-btn').click(function(){
		    				self.close();
		    			});
		    		},7000);
		    	},5000);
		    },3000);
		},
		waiting: function () {
		    var self = this;
			this.entity.find(".md-mask").show();
			self.entity.find(".md-mask span").addClass('f14').html('信息已提交，请稍后。<br/>提交期间请勿刷新此页面。');
		},
		hideLoading: function () {
		    this.entity.find(".md-mask").hide();
		},
		showProtocol: function () {
			var _div = this.entity.find(".md-protocol");
			this.entity.find(".protocol-cnt").css({height:this.entity.height()-144});
		    _div.css({left:_div.width()}).show().animate({left:0},1000,function(){});
		},
		hideProtocol: function () {
			var _div = this.entity.find(".md-protocol");
		    _div.animate({left:_div.width()},500,function(){
		    	$(this).hide();
		    });
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
		    if (doc.compatMode == 'CSS1Compat') { height = doc.documentElement.scrollHeight; }
		    else if (doc.body) { height = doc.body.scrollHeight; }
		    this.contentIframe.height(height);
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
