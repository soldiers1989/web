if(typeof JRJ=="undefined"||!JRJ)var JRJ={};if(typeof JRJ.ui=="undefined"||!JRJ.ui)JRJ.ui={};
(function(e){var f="jrj-accordion-";JRJ.ui.Accordion=function(a,b){this.container=e("#"+a);this.config={triggerCls:f+"trigger",panelCls:f+"panel",triggerActiveCls:f+"active",triggerType:"click",multiple:false,activeIndex:0,animation:false,animationSpeed:500,onSwitch:null};e.extend(this.config,b);this.init()};JRJ.ui.Accordion.prototype={init:function(){var a=this,b=a.config;a.activeIndex=b.activeIndex;a.triggers=a.container.children("."+b.triggerCls);a.panels=a.container.children("."+b.panelCls);a.animation=
b.animation;a.animationSpeed=b.animationSpeed;a._bindTriggers();a.switchTo(a.activeIndex,0,false)},_bindTriggers:function(){var a=this,b=a.config;a.triggers.each(function(c){var d=e(this);if(b.triggerType==="click")d.bind("click",function(){a._onClickTrigger(c)});else{d.bind("mouseenter",function(){a._onMouseEnterTrigger(c)});d.bind("mouseleave",function(){a._onMouseLeaveTrigger()})}})},_onClickTrigger:function(a){this._triggerIsValid(a)&&this.switchTo(a,this.activeIndex,this.animation)},_onMouseEnterTrigger:function(a){var b=
this;if(b._triggerIsValid(a))b.switchTimer=setTimeout(function(){b.switchTo(a,b.activeIndex,b.animation)},300)},_onMouseLeaveTrigger:function(){if(this.switchTimer){clearTimeout(this.switchTimer);this.switchTimer=undefined}},_triggerIsValid:function(a){if(this.activeIndex!=a||this.config.multiple)return true;return false},switchTo:function(a,b,c){if(!this._triggerIsValid())return this;b=this.triggers[typeof b!="undefined"?b:this.activeIndex];var d=this.triggers[a];this._switchTrigger(b,d);b=this.panels[this.activeIndex];
d=this.panels[a];this._switchView(b,d,c);this.activeIndex=a;this.config.onSwitch&&this.config.onSwitch(a)},_switchTrigger:function(a,b){var c=this;c=c.config;a=e(a);b=e(b);if(c.multiple)b.hasClass(c.triggerActiveCls)?b.removeClass(c.triggerActiveCls):b.addClass(c.triggerActiveCls);else{a.removeClass(c.triggerActiveCls);b.addClass(c.triggerActiveCls)}},_switchView:function(a,b,c){var d=this;d=d.config;a=e(a);b=e(b);if(c)if(d.multiple)b.slideToggle();else{a.height();b.height();a.slideUp();b.slideDown()}else if(d.multiple)!b.is(":visible")?
b.show():b.hide();else{a.hide();b.show()}}}})(jQuery);