//if(!$.browser.msie || $.browser.version > 8){window.sr = new scrollReveal({after:"0s",enter:"bottom",move:"24px",over:"0.66s",easing:"ease-in-out",vFactor:0.33,reset:false,init:true});}
$(function () {
	//watermark
	$('#txtSearch').watermark('代码/名称/简拼');
	//tooltip
	$('.icon-tooltip').tooltipster({
       theme: 'tooltipster-light'
    });
	if(_accountId==''){
		_accountId = $('.site-menu-cnt').find('.account-menu-block:first').data('accountid');
	}
	function seok(){
		//行情
		var param1 = "&item=10&areapri=cn|hk|us&typepri=s|w|i|f";
		var sb1 = new SearchBox("txtSearch", "btntop01", null, param1, null, null, {
			historyCallback: function (stock, event) {
				//console.log(stock);
				//console.log(event);
			},
			clearInput:false
		});
	}
	seok();
	//site-top 用户信息
	var _timer = null;
	var dropDownListEvent = function (op, obj) {
		window.clearTimeout(_timer);
		_timer = setTimeout(function () {
			if (op == 'open') {
				$(obj).find('.trigger').addClass('trigger-drop');
				$(obj).find('.cnt').show();
			} else {
				$(obj).find('.trigger').removeClass('trigger-drop');
				$(obj).find('.cnt').hide();
			}
		}, 300);
	};
	
	$('.site-top .userinfo').mouseenter(function () {
		dropDownListEvent('open', this);
	}).mouseleave(function () {
		dropDownListEvent('close', this);
	});
	//site-top 用户信息
	//menu
	$('div.site-menu-title').click(function () {
		if (!$(this).parent().hasClass('site-menu-expand')) {
			$(this).parent().addClass('site-menu-expand');
			$(this).parent().stop().animate({ height: $(this).parent().outerHeight() + $(this).next().outerHeight() + 'px' }, 200);
		} else {
			$(this).parent().removeClass('site-menu-expand');
			$(this).parent().stop().animate({ height: '47px' }, 200);
		}
	});
	$('.site-menu-title .stop').click(function (e) {
		e.stopPropagation();
	});
	$('.site-menu-cur').find('.site-menu-title').click();
	$('.site-menu').mouseenter(function () {
		$(this).addClass('site-menu-hover');
	}).mouseleave(function () {
		$(this).removeClass('site-menu-hover');
	});
	
});

$(function(){
	//menu
	var options = {
		useEasing : true, // toggle easing
		useGrouping : true, // 1,000,000 vs 1000000
		separator : ',', // character to use as a separator
		decimal : '.', // character to use as a decimal
		defaultStr : '--.--'
	},optionPercent = {
		useEasing : true, // toggle easing
		useGrouping : true, // 1,000,000 vs 1000000
		separator : ',', // character to use as a separator
		decimal : '.', // character to use as a decimal
		prefix: '',
		suffix: '%',
		defaultStr : '0.00'
	},optionRMB = {
		useEasing : true, // toggle easing
		useGrouping : true, // 1,000,000 vs 1000000
		separator : ',', // character to use as a separator
		decimal : '.', // character to use as a decimal
		prefix: '￥',
		defaultStr : '0.00'
	}
	
	//left.vm
	$('.site-menu-cnt').find('.account-menu-block').each(function(i,v){
		var accountId = $(v).data('accountid');
		if($(v).find('em.hk').length>0){
			fullHKFundInfo(accountId);
		}else{
			fullFundInfo(accountId);
		}
	});
	function fullFundInfo(accountId){
		$.ajax({
			type : 'get',
			url : '/stock/'+ _accountId + '/ajaxAccountList.jspa',
			data : {accountId:accountId},
			dataType : 'json',
			timeout : 10000,
			async:true,
			cache : false,
			success : function(data){
				//console.log(data);
				var assetBalance='',percent='',todayGenLose='',isMain=false;
				
				if($('#account_summary').length != 0 && typeof _accountId != 'undefined' && data.accountId == _accountId){
					isMain = true;
				}
				var target=$('#account_'+data.accountId);
				if(typeof data.clientName != 'undefined'){
					target.find('.clientName').empty().text(data.clientName);
				}else{
					target.find('.clientName').empty().text('--');
				}
				if(typeof data.updateTime != 'undefined'){
					target.find('.updateTime').empty().text(new Date(data.updateTime).format('MM-dd hh:mm:ss'));
				}else{
					target.find('.updateTime').empty().text(new Date().format('MM-dd hh:mm:ss'));
				}
				if(typeof data.errMsg_fund == 'undefined' && typeof data.fundAll.assetBalance != 'undefined'){				
					assetBalance=formatNum(data.fundAll.assetBalance);
					target.find('.assetBalance').empty().text('￥'+assetBalance);
					//显示主账户资产
					if(isMain){
						//$('#account_summary').find('.assetBalance').empty().text(formatNum(data.fundAll.assetBalance));
						new countUp($('#account_summary .assetBalance').get(0), 0, data.fundAll.assetBalance, 2, 0.66, options).start();
						//$('#account_summary').find('.marketValue').empty().text(formatNum(data.fundAll.marketValue));	
						new countUp($('#account_summary .marketValue').get(0), 0, data.fundAll.marketValue, 2, 0.66, options).start();
					}
					if(typeof data.errMsg_todayGenLose == 'undefined' && typeof data.todayGenLose != 'undefined'){
						//percent=(data.todayGenLose/data.fundAll.assetBalance)*100;
						//percent=percent.toFixed(2);
						todayGenLose=formatNum((data.todayGenLose).toFixed(2));
						var arrow = '';
						if(data.todayGenLose > 0){
							arrow = '<i class="icon icon-up"></i>';
							target.find('.todayGenLose').addClass('red');
						}else if(data.todayGenLose < 0){
							target.find('.todayGenLose').addClass('green');
							arrow = '<i class="icon icon-down"></i>';
						}
						target.find('.todayGenLose').empty().html(arrow + todayGenLose + '%');
						if(isMain){
							var arrow1 = '';
							if(data.todayGenLose > 0){
								arrow1 = '<i class="icon icon-up-14 mr5"></i>';
								$('#account_summary').find('.todayGenLose').addClass('red');
							}else if(data.todayGenLose < 0){
								arrow1 = '<i class="icon icon-down-14 mr5"></i>';
								$('#account_summary').find('.todayGenLose').addClass('green');
							}
							//$('#account_summary').find('.todayGenLose').empty().html(arrow1 + todayGenLose + '(' + percent + '%)');
							$('#account_summary').find('.todayGenLose').empty().append(arrow1).append('<span></span>');
							//new countUp($('#account_summary .todayGenLose span:first').get(0), 0, data.todayGenLose, 2, 0.66, options).start();
							new countUp($('#account_summary .todayGenLose span').get(0), 0, data.todayGenLose, 2, 0.66, optionPercent).start();
						}
					}else{
						target.find('.todayGenLose').empty().html('0.00%');
						if(isMain){
							$('#account_summary').find('.todayGenLose').empty().html('0.00%');
						}
					}
					
				}else{
					target.find('.assetBalance').empty().text('￥--.--');
					target.find('.todayGenLose').empty().html('0.00%');
					if(isMain){
						$('#account_summary').find('.assetBalance').empty().text('--.--');
						$('#account_summary').find('.marketValue').empty().text('--.--');
						$('#account_summary').find('.todayGenLose').empty().html('0.00%');
					}
				}
			},
			error : onError
		});	
	}
	function fullHKFundInfo(accountId){
		$.ajax({
			type : 'get',
			url : '/stock/hk/'+ _accountId + '/ajaxAccountList.jspa',
			data : {accountId:accountId},
			dataType : 'json',
			timeout : 10000,
			async:true,
			cache : false,
			success : function(data){
				if(typeof data.hk_password_error != 'undefined'){
					onError();
					return;
				}
				var assetBalance='',percent='',todayGenLose='',isMain=false;
				
				if($('#account_summary').length != 0 && typeof _accountId != 'undefined' && data.accountId == _accountId){
					isMain = true;
				}
				var target=$('#account_'+data.accountId);
				if(typeof data.clientName != 'undefined'){
					target.find('.clientName').empty().text(data.clientName);
				}else{
					target.find('.clientName').empty().text('--');
				}
				if(typeof data.updateTime != 'undefined'){
					target.find('.updateTime').empty().text(new Date(data.updateTime).format('MM-dd hh:mm:ss'));
				}else{
					target.find('.updateTime').empty().text(new Date().format('MM-dd hh:mm:ss'));
				}
				if(typeof data.errMsg_fund == 'undefined' && typeof data.fundAll.buyingPower != 'undefined'){				
					assetBalance=formatNum(data.fundAll.buyingPower);
					target.find('.assetBalance').empty().text('HKD'+assetBalance);
					//显示主账户资产
					if(isMain){
						var target_summary=$('#account_summary');
						//target_summary.find('.assetBalance').attr('title','折合人民币:￥' + data.fundAll.cnyBuyingPower);
						new countUp(target_summary.find('.assetBalance').get(0), 0, data.fundAll.buyingPower, 2, 0.66, options).start();
						//new countUp($('#account_summary .marketValue').get(0), 0, data.fundAll.marketValue, 2, 0.66, options).start();
						new countUp(target_summary.find('.enableBalance').get(0), 0, data.fundAll.availableBal, 2, 0.66, options).start();
						new countUp(target_summary.find('.fetchBalance').get(0), 0, data.fundAll.fetchBalance, 2, 0.66, options).start();
					}
				}else{
					target.find('.assetBalance').empty().text('HKD--.--');
					target.find('.todayGenLose').empty().html('0.00%');
					if(isMain){
						$('#account_summary').find('.assetBalance').empty().text('--.--');
						$('#account_summary').find('.marketValue').empty().text('--.--');
						$('#account_summary').find('.todayGenLose').empty().html('0.00%');
						$('#account_summary').find('.enableBalance').empty().text('--.--');
						$('#account_summary').find('.fetchBalance').empty().text('--.--');
					}
				}
			},
			error : onError
		});
	}
	function onError(){
		var target =  $('div.account-menu-block span:has(img)').parents('div.account-menu-block');
		target.find('.assetBalance').empty().text('--.--');
		target.find('.todayGenLose').empty().html('0.00%');
		if($('#account_summary').find('.assetBalance').has('img').length>0){
			$('#account_summary').find('.assetBalance').empty().text('--.--');
		}
		if($('#account_summary').find('.marketValue').has('img').length>0){
			$('#account_summary').find('.marketValue').empty().text('--.--');
		}
		if($('#account_summary').find('.todayGenLose').has('img').length>0){
			$('#account_summary').find('.todayGenLose').empty().html('0.00%');
		}
	}
	//left.vm
	//balance
	if($('#account_summary').length != 0 && $('#account_summary').data('flag') != 'HK'){
		var url = '/stock/' + _accountId + '/ajaxFundInfo.jspa'
		$.ajax({
			type:'get',
			url:url,
			data:{'accountId':_accountId},
			cache:false,
			dataType:'json',
			timeout:5000,
			success:function(data){
				var target=$('#account_summary');
				if(typeof data.errMsg_fundInfo == 'undefined'){
					if(target.find('.enableBalance').length>0){
						target.find('.enableBalance').empty();//.text(formatNum(data.fund.enableBalance));
						new countUp(target.find('.enableBalance').get(0), 0, data.fund.enableBalance, 2, 0.66, options).start();
					}
					if(target.find('.fetchBalance').length>0){
						target.find('.fetchBalance').empty();//.text(formatNum(data.fund.fetchBalance));
						new countUp(target.find('.fetchBalance').get(0), 0, data.fund.fetchBalance, 2, 0.66, options).start();
					}
				}else{
					target.find('.enableBalance').empty().text('--.--');
					target.find('.fetchBalance').empty().text('--.--');
				}
			},
			error:function(){
				var target=$('#account_summary');
				target.find('.enableBalance').empty().text('--.--');
				target.find('.fetchBalance').empty().text('--.--');
			}
		});
	}
	//--balance
});