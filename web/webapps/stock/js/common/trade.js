$(function(){
	var cashRecharge = "/cash/cashRecharge.jspa";
	var cashTake= "/cash/cashTake.jspa"
	//存款
$('#menuSave').click(function(){
	showDialogMC({
		title : "存款",
		step : [ '填写存款信息', '确认', '完成' ],
		ifrSrc : cashRecharge,
		isFixed : false,
		content : [
				'<ol>',
				'<li>资金存入盈利宝等于购买了鹏华货币A基金，盈利宝免收任何手续费。</li>',
				'<li>交易日15:00之前存款，下一个交易日开始计息；15:00之后存款，下两个交易日开始计息，遇节假日顺延。</li>',
				'</ol>' ].join('')
	});
});


// 取现
$('#menuDraw').click(function() {
		var tips = [
			'<ol>',
			'<li style="line-height: 16px;">当日累计取款金额(包含取款、转账及还款计划)5万以内可实时到帐；超出5万或取出全部可用余额执行货币基金正常赎回流程(交易日15:00前操作，资金下一个交易日到帐；交易日15:00后操作，资金下两个交易日到帐)，且未付收益将一并取至银行卡；</li>',
			'<li style="line-height: 16px;">可用余额不包含未付收益、在途及冻结资产，且上述资产不可实时提取。</li>',
			'</ol>' ].join('');
		var quxian = document.createElement('div');
		$(quxian).load('/protocol/quxian.html', function() {
		showDialogMC({
			title : "取款",
			step : [ '填写取款信息', '确认', '完成' ],
			ifrSrc : cashTake,
			isFixed : false,
			content : tips,
			title : '《快速取现用户服务协议》',
			content:tips,
            protocolHtml:{
            		title:'《快速取现用户服务协议》',
                 	content:$(quxian).html()
           }
		});

	});
  })
});
