jQuery(function (){
	//顶部下拉列表
    jQuery('.dropdownlist').mouseenter(function () {
        jQuery(this).addClass('dropdownlist-drop');
        jQuery(this).find('ul').show();
    }).mouseleave(function () {
        jQuery(this).removeClass('dropdownlist-drop');
        jQuery(this).find('ul').hide();
    });

	//导航下拉菜单开始
	jQuery('.nav_new_header li:gt(0)').hover(function(){
		jQuery('.nav_fc').hide();
		jQuery(this).children('.nav_fc').show()
		jQuery('.nav_new_header li').removeClass('hover')
		jQuery(this).addClass('hover')	
	},function(){
		jQuery('.nav_fc').hide();
		jQuery('.nav_new_header li').removeClass('hover')
	})

	//导航下拉菜单结束
	
	//city效果开始
	function cl01(){jQuery('.c_all').hide()}
	jQuery('.c_all').children('b').click(function(){
		jQuery('.c_all').children('b').removeClass('cur')
		jQuery(this).addClass('cur')
		jQuery(this).parent('.c_all').prev('.allcity').children('i').text(jQuery(this).text())
	})
	jQuery('.allcity').hover(function(){
		jQuery(this).next('.c_all').children('b:last').addClass('last')
		jQuery(this).next('.c_all').show()
		var leftpos = jQuery(this).position().left;
		var toppos = jQuery(this).position().top;
		jQuery(this).next('.c_all').css('left',leftpos)
		jQuery(this).next('.c_all').css('top',toppos+19)
	},function(){cl01()})
	jQuery('.c_all').hover(function(){jQuery(this).show()},function(){jQuery(this).hide()})
	//city效果结束
	
	
	jQuery('#search_2').focus(function(){
		var va01 = $(this).val()
		if(va01 == '请输入关键词'){
			$(this).val('')
		}
	}).blur(function(){
		var va01 = $(this).val()
		if(va01 == ''){
			$(this).val('请输入关键词')
		}		
	})
	
	function seok(){
            //行情
            var param1 = "&item=10&areapri=cn|hk|us&typepri=s|w|i|f";
            var sb1 = new SearchBox("search_1", "btntop01", null, param1, null, null, {
                historyCallback: function (stock, event) {
                    console.log(stock);
                    console.log(event);
                },
				clearInput:false
            });
			
			

            //股吧
            var param3 = "&item=10&areapri=cn|hk|us&typepri=s|w|i|f";
            var sb3 = new SearchBox("search_3", "btntop03", null, param3, [
                new JRJ.ui.Pattern("cn.i", "http://istock.jrj.com.cn/list,dpzw.html"),//个股指数
                new JRJ.ui.Pattern("cn.s", "http://istock.jrj.com.cn/list,[code].html"),//个股股票
                new JRJ.ui.Pattern("cn.f", "http://[code].ifund.jrj.com.cn/forum[code].html"),//基金
                new JRJ.ui.Pattern("cn.w", "http://istock.jrj.com.cn/list,[code].html"),//权证
                new JRJ.ui.Pattern("hk.s", "http://istock.jrj.com.cn/list,hk[code].html")//港股吧
            ], null, {
                historyCallback: function (stock, event) {
                    console.log(stock);
                    console.log(event);
                },
				clearInput:false
            });
			
	}

	//搜索效果开始
	function op02(){jQuery('.tab_top_one ul').show()}
	function cl02(){jQuery('.tab_top_one ul').hide()}
	jQuery('.tab_top_one span').hover(function(){op02()},function(){cl02()})
	jQuery('.tab_top_one ul').hover(function(){op02()},function(){cl02()})
	jQuery('.tab_top_one ul li').hover(function(){
		jQuery('.tab_top_one ul li').removeClass('cur')
		jQuery(this).addClass('cur')
	},function(){
		jQuery('.tab_top_one ul li').removeClass('cur')
	})
	jQuery('.tab_top_one ul li').click(function(){
		var liid =jQuery(this).attr('id')
		var li_id_num = liid.split("_");
		var con_id = "#cn_" + li_id_num[1];
		jQuery(con_id).show().siblings('.con_input').hide();
		jQuery('.sh_box .tab_top_one span').text(jQuery(this).text())
		jQuery(this).parent('ul').hide()
		seok()
	})
	//搜索效果结束
	
	//我的jrj效果开始
	function op03(){jQuery('.mjshow').show();jQuery('.mjbtn .mjn').addClass('mjbon')}
	function cl03(){jQuery('.mjshow').hide();jQuery('.mjbtn .mjn').removeClass('mjbon')}
	jQuery('.mjbtn').hover(function(){op03()},function(){cl03()});
	jQuery('.mjshow').hover(function(){op03()},function(){cl03()});
	//我的jrj效果结束
	jQuery('.loginnew .username').next('span').hide();
	seok()
	jQuery('.nav_fc').bgiframe();
})