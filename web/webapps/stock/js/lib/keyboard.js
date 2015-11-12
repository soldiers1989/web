(function ($) {
    $.keyboard = {

        //Public Methods
        go: function (params) {
            var settings = $.extend({
                txtboxId: "",
                triggerObj: null,
                offsetValue: {x:0,y:0},
                callback: null
            }, params);

            var $writeBox = $(settings.txtboxId),
	            shift = false,
	            capslock = false;

            $.keyboard.init(settings);
            
         	// Button Ok and Close
            $('#keyboard li').find('span.keyboard-ok,span.keyboard-close').click(function(){
            	$('#keyboard').hide().remove();
            });
            
            $('#keyboard li').unbind().hover(function () {
            	if(!$(this).hasClass('control-btn')){
                    $(this).addClass('hover');
            	}
            }, function () {
            	if(!$(this).hasClass('control-btn')){
            		$(this).removeClass('hover');
            	}
            }).click(function () {
                var $this = $(this),
                    charater = $this.html();

                // 一键两意
                //if ($this.hasClass('symbol')) charater = $('span:visible', $this).html();

                // Button detele 
                if ($this.hasClass('delete')) {
                	if($(settings.txtboxId).get(0).tagName == 'INPUT'){
                		var val = $(settings.txtboxId).val();
                		$(settings.txtboxId).val(val.substring(0, val.length - 1));
                	}else{
                        var html = $(settings.txtboxId).html();
                        $(settings.txtboxId).html(html.substring(0, html.length - 1));
                	}
                 //   return false;
                };
                
                // 'control-btn' region 
                if ($this.hasClass('control-btn')||$this.hasClass('none')) {
                    return false;
                };

                // Button tab
                if ($this.hasClass('tab')) charater = '\t';

                // Button capslock
                if ($this.hasClass('capslock')) {
                    $('.letter').toggleClass('uppercase');
                    capslock = true;
                    return false;
                };

                // Button enter
                if ($this.hasClass('enter')) charater = '\n';

                // Button shift
                if ($this.hasClass('shift')) {
                    $('.letter').toggleClass('uppercase');
                    //$('.symbol span').toggle();

                    shift = (shift === true) ? false : true;
                    capslock = false;
                    return false;
                };

                // Button space
                if ($this.hasClass('space')) charater = ' ';

                // 转换为大写
                if ($this.hasClass('uppercase')) charater = charater.toUpperCase();

                // 输出所按的键值
                if($(settings.txtboxId).get(0).tagName == 'INPUT'){
                	$(settings.txtboxId).val($(settings.txtboxId).val() + charater);
            	}else{
            		$(settings.txtboxId).html($(settings.txtboxId).html() + charater);
            	}
                
                if(settings.callback){
                	settings.callback();
                }
                

            });
        },

        init: function (settings) {
            var tmpl = ['<ul id="keyboard">',
        		        '<li class="control-btn jrj-clear"><span class="keyboard-ok jrj-fl">确 定</span><span class="keyboard-close jrj-fr">关 闭</span></li>',
        		        
//		        '<li class="symbol">~</li>',
//		        '<li class="symbol">!</li>',
//		        '<li class="symbol">@</li>',
//		        '<li class="symbol">#</li>',
//		        '<li class="symbol">$</li>',
//		        '<li class="symbol">%</li>',
//		        '<li class="symbol">^</li>',
//		        '<li class="symbol">&amp;</li>',
//		        '<li class="symbol">*</li>',
//		        '<li class="symbol">(</li>',
//		        '<li class="symbol">)</li>',
//		        '<li class="symbol">_</li>',
//		        '<li class="symbol">+</li>',
//		        '<li class="symbol">|</li>',
//		        '<li class="symbol">-</li>',

//		        '<li class="symbol">`</li>',
		        '<li class="symbol">5</li>',
		        '<li class="symbol">6</li>',
		        '<li class="symbol">9</li>',
		        '<li class="symbol">8</li>',
		        '<li class="symbol">3</li>',
		        '<li class="symbol">2</li>',
		        '<li class="symbol">7</li>',
		        '<li class="symbol">4</li>',
		        '<li class="symbol">1</li>',
		        '<li class="symbol lastitem">0</li>',
//		        '<li class="symbol">=</li>',
//		        '<li class="symbol">\\</li>',
//		        '<li class="symbol">{</li>',
//		        '<li class="symbol">}</li>',
		        
		        '<li class="letter">q</li>',
		        '<li class="letter">w</li>',
		        '<li class="letter">e</li>',
		        '<li class="letter">r</li>',
		        '<li class="letter">t</li>',
		        '<li class="letter">y</li>',
		        '<li class="letter">u</li>',
		        '<li class="letter">i</li>',
		        '<li class="letter">o</li>',
		        '<li class="letter lastitem">p</li>',
//		        '<li class="symbol">[</li>',
//		        '<li class="symbol">]</li>',
//		        '<li class="symbol">&lt;</li>',
//		        '<li class="symbol">&gt;</li>',
//		        '<li class="symbol lastitem">?</li>',
		        
		        '<li class="letter">a</li>',
		        '<li class="letter">s</li>',
		        '<li class="letter">d</li>',
		        '<li class="letter">f</li>',
		        '<li class="letter">g</li>',
		        '<li class="letter">h</li>',
		        '<li class="letter">j</li>',
		        '<li class="letter">k</li>',
		        '<li class="letter">l</li>',
//		        '<li class="symbol">&lsquo;</li>',
//		        '<li class="symbol">,</li>',
//		        '<li class="symbol">.</li>',
//		        '<li class="symbol">/</li>',
		        '<li class="delete lastitem"></li>',
//		        '<li class="none lastitem"></li>',
		        
                '<li class="letter keyboard-clear">z</li>',
                '<li class="letter">x</li>',
                '<li class="letter">c</li>',
                '<li class="letter">v</li>',
                '<li class="letter">b</li>',
                '<li class="letter">n</li>',
                '<li class="letter">m</li>',
//                '<li class="symbol">:</li>',
//                '<li class="symbol">&quot;</li>',
//                '<li class="symbol">;</li>',
//                '<li class="space"></li>',
                '<li class="shift lastitem">切换大/小写</li>',

            '</ul>'].join('');
            if ($('#keyboard').length == 0 || typeof $('#keyboard').length == 'undefined') {
                $('body').append($(tmpl));
                $('#keyboard').css({ top: settings.triggerObj.offset().top + settings.offsetValue.y, left: settings.triggerObj.offset().left + settings.triggerObj.width()+10 + settings.offsetValue.x }).show();
                return true;
            } else {
                $('#keyboard').hide().css({ top: settings.triggerObj.offset().top + settings.offsetValue.y, left: settings.triggerObj.offset().left + settings.triggerObj.width()+10 + settings.offsetValue.x }).show();
                return false;
            }
            
        }
    };
    

    $(document).mousedown(function (e) {
    	e.stopPropagation();
    	if($('#keyboard:visible').length>0){
	        if (!$.contains($("#keyboard")[0],e.target) && $(e.target).attr('id')!='keyboard') {
	        	$('#keyboard').hide().remove();
	        	_validatorPassword();
            	_validatorRePassword();
	        }
    	}
    });
})(jQuery);