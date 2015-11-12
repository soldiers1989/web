function updateCost(type,price,max,lotsz,stockType){
	if(isNaN(price) || isNaN(max)){return}
	if(typeof lotsz !='undefined' && !isNaN(lotsz)){
		lotsz=parseInt(lotsz);
	}else{
		lotsz=100;
	}
	price=parseFloat(price);
	var all = Math.floor(max/lotsz)*lotsz*price,
	four = Math.floor((max/4)/lotsz)*lotsz*price,
	three = Math.floor((max/3)/lotsz)*lotsz*price,
	two = Math.floor((max/2)/lotsz)*lotsz*price;
	if("sell"==type){
		all= Math.floor(max)*price;
	}
	var tipAble=true;
	if(stockType!=undefined&&stockType.indexOf("r")!=-1){
		tipAble=false;
	}
	setToolTip('#cost_four',four,tipAble);
	setToolTip('#cost_three',three,tipAble);
	setToolTip('#cost_two',two,tipAble);
	setToolTip('#cost_all',all,tipAble);
}

function setToolTip(id,num,tipAble){
	if(num>0){
		if(tipAble==false){
			$(id).tooltipster('disable').removeClass('disable');
		}else{
			$(id).tooltipster('content', num.toFixed(2) + '元').tooltipster('enable').removeClass('disable');
		}
	}else{
		$(id).tooltipster('disable').addClass('disable');
	}
}

function highLightPrice(price){
	$('#real_trade_table tr:not(".title")').removeClass('cur').each(function(i,v){
		if(price == $(v).find('td span:first').text()){
			$(v).addClass('cur');
			return false;
		}
	});
}
function showError(element,msg){
	element=$('#' + element);
	var error = element.parents('td').find('.error-msg');
	error.text(msg);
}
function clearError(targetId){
	if(typeof targetId != 'undefined'){
		$('#' + targetId).parents('td').find('.error-msg').text('');
	}else{
		$('.error-msg').text('');
	}
}
function showGlobalError(msg){
	$('.error-msg-bar-wrap').find('.error-msg-bar').show().find('span').text(msg);
	setTimeout(clearGlobalError,6000);
}
function clearGlobalError(){
	$('.error-msg-bar-wrap').find('.error-msg-bar').fadeOut('slow',function(){$(this).find('span').text('')});
}
function showSuccessMsg(msg,link,action,data){
	$('.error-msg-bar-wrap').find('.error-msg-bar-wrap-right').show().find('.text>span').text(msg);
	if(typeof link != 'undefined'){
		$('.error-msg-bar-wrap').find('.error-msg-bar-wrap-right').show().find('.text>a.link').text(link);
	}
	if(typeof link != 'undefined' && $.isFunction(action)){
		$('.error-msg-bar-wrap').find('.error-msg-bar-wrap-right').show().find('.text>a.link').unbind().click(function(){
			clearSuccessMsg();	
			action(data);
			return false;
		});
	}
	setTimeout(clearSuccessMsg,3000);
}
function clearSuccessMsg(){
	$('.error-msg-bar-wrap').find('.error-msg-bar-wrap-right').fadeOut('slow',function(){$(this).find('.text>span').text('')});
}
function getTokenName(){
	return $('input[name="xjb.token.name"]').val();
}
function getToken(){
	return $('input[name="xjb.token"]').val();
}
function resetToken(token){
	$('input[name="xjb.token"]').val(token);
}
function showLoading(id){
	$('#'+id).html('<div class="tc"><img width="40" src="/stock/images/loading.gif" alt="loading" /></div>');
}
(function($){
	var DEFAULT_CONFIG = {
		//id : 
		min : 0,
		//max : ,
		step : 1,
		percision : 2
		//callBack : function(){}
	}
	JRJ.cfo = {
		adjustINPUT : function(config){
			var self = this;
			self.config = $.extend({}, DEFAULT_CONFIG, config);
			if(typeof self.config.id == 'undefined')return;
			self.target = $('#' + self.config.id).parent();
			self.target.attr('data-step',self.config.step);
			self.target.attr('percision',self.config.percision);
			self.target.find('i').click(function (e) {
				if(typeof _t != 'undefined')clearTimeout(_t);
				var step = parseFloat(self.target.attr('data-step')),
					percision = parseInt(self.target.attr('percision')),				
					callBack = self.config.callBack,
					_parent = $(this).parents('.txtbox-up-down:first'),
					_input = _parent.find('input'),
					num = _input.val();
				if(_input.attr('disabled')){return;}
				if(isNaN(num) || num == ''){num=0;}
				
				num = parseFloat(num);
				if ($(this).hasClass('up')) {
					_input.attr({ 'value': (step + num).toFixed(percision)});
				} else if ($(this).hasClass('down') && num > 0) {
					if(num < step){
						_input.attr({ 'value':  numStr});
					}else{
						_input.attr({ 'value': (num - step).toFixed(percision)});
					}
				}
				_input.focus();
				_t = setTimeout(function(){
					$('#' + self.config.id).change();
				},300);
				if(typeof callBack != 'undefined' && $.isFunction(callBack)){
					callBack();
				}
			});
			self.target.find('input').focus(function () {
				$(this).parent().addClass('txtbox-up-down-focus');
			});
			self.target.find('input').blur(function () {
				$(this).parent().removeClass('txtbox-up-down-focus');
			});
			this.setStep=function(step){
				this.target.attr('data-step',step);
			};
			this.setPercision=function(percision){
				 this.target.attr('percision',percision);
			};
		}
	}
	
})(jQuery);
