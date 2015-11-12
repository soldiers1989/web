function confirmCancel(data){
	var stockCode=data.stockCode,
	stockName=data.stockName,
	direction=data.direction,
	price=data.price,
	amount=data.amount,
	entrustNo=data.entrustNo;
	JRJ.Dialogs.standardDialog({
        title: "确认撤单",
        width:350,
        content: ['<div class="dialog-cnt">',
                        '<table class="table-p5">',
                            '<tr>',
                                '<td style="width:70px;">委托股票</td>',
                                '<td>'+stockName+'('+stockCode+')</td>',
                            '</tr>',
                            '<tr>',
                                '<td>委托方向</td>',
                                '<td>'+direction+'</td>',
                            '</tr>',
                            '<tr>',
	                            '<td>委托价格</td>',
	                            '<td>'+price+'元/股</td>',
	                        '</tr>',
                            '<tr>',
                                '<td>委托数量</td>',
                                '<td>'+amount+'股</td>',
                            '</tr>',
                        '</table>',
                  '</div>'].join(''),
        okBtnText: "确认",
        cancelBtnText: "取消",
        hasCloseBtn: true,
        hasOkBtn: true,
        hasCancelBtn: true,
        okCallback: function () {
        	var url="/stock/hk/"+_accountId+"/cancelStep4.jspa";
        	doCancelAjax(url,stockName,entrustNo);
        	return true;
        },
        isFixed: true
    });
}

function confirmModify(data){
	var stockCode=data.stockCode,
	stockName=data.stockName,
	direction=data.direction,
	price=data.price,
	amount=data.amount,
	entrustNo=data.entrustNo;
	matchingType = data.matchingType;
	$.ajax({
		type : "GET",
		url : '/stock/hk/' + _accountId + '/stockInfo.jspa',
		data:{
			"stockCode" :stockCode
		},
		dataType : "json",
	    timeout : 5000,
		cache : false,
		success : function(data){
			if(typeof data.errMsg == 'undefined'){
			JRJ.Dialogs.standardDialog({
				title: "确认改单",
				width: 350,
				content: ['<div class="dialog-cnt">',
								'<table class="table-p5">',
									'<tr>',
										'<td style="width:70px;">证券名称</td>',
										'<td>' + stockName + '（' + stockCode + '）</td>',
									'</tr>',
									'<tr>',
										'<td>委托类型</td>',
										'<td><select id="matchingType" value="' + matchingType + '" disabled="true">',
										'<option value="AO">竞价盘</option>',
										'<option value="ALO">竞价限价盘</option>',
										'<option value="LO">限价盘</option>',
										'<option value="ELO">高级限价盘</option>',
										'<option value="SLO">特别限价盘</option></select></td>',
									'</tr>',
									'<tr>',
										'<td>委托价格</td>',
										'<td><div class="txtbox txtbox-up-down txtbox-up-down-155 fl">',
											'<span class="fr">',
												'<i class="down"></i>',
												'<i class="up"></i>',
											'</span>',
											'<input id="price" name="price" type="text" class="txtbox" value="' + price + '" '+(matchingType=='AO'?'disabled="disabled"':'')+' />',
										'</div></td>',
									'</tr>',
									'<tr>',
										'<td>每手股数</td>',
										'<td>' + data.lotsz + '股</td>',
									'</tr>',
									'<tr>',
										'<td class="field">委托数量</td>',
										'<td><div class="txtbox txtbox-up-down txtbox-up-down-155 fl">',
											'<span class="fr">',
												'<i class="down"></i>',
												'<i class="up"></i>',
											'</span>',
											'<input id="entrustAmount" name="entrustAmount" type="text" class="txtbox" value="' + amount + '" />',
										'</div>',
											'<div class="error-msg"></div>',
										'</td>',
									'</tr>',
								'</table>',
						  '</div>'].join(''),
				okBtnText: "确认",
				cancelBtnText: "取消",
				hasCloseBtn: true,
				hasOkBtn: true,
				hasCancelBtn: true,
				okCallback: function () {
					var url='/stock/hk/' + _accountId + '/modifyOrder.jspa';
					doModifyOrder(url,stockName,entrustNo, $('#price').val(), $('#entrustAmount').val());
					return true;
				},
				isFixed: true
			});
			$('#matchingType option[value="'+matchingType+'"]').attr('selected','selected');
			//txtbox-up-down
			new JRJ.cfo.adjustINPUT({
				id : 'price',
				step : data.sasr,
				percision : 2,
				callBack : function(){
					highLightPrice($('#price').val());
				}
			});
			new JRJ.cfo.adjustINPUT({
				id : 'entrustAmount',
				step : data.lotsz,
				percision : 0
			});
			
			$("#price").keyup(function(){
				XJB.Utils.clearNoNum(this,2);
				clearError('price');
			});
			$("#entrustAmount").keyup(function(){
				XJB.Utils.clearNoNum(this,-1);
				clearError('entrustAmount');
			});
			//--txtbox-up-down
			}else{
				showGlobalError(data.errMsg);
			}
		},
		error:function(){
			window.location.reload();
		}
	});
	
}

function doCancelAjax(url,stockName,entrustNo){
	$.ajax({
		type : "POST",
		url : url,
		data:{
			"stockName" :stockName,
			"entrustNo":entrustNo,
			"xjb.token.name":getTokenName(),
			"xjb.token":getToken()
		},
		dataType : "json",
	    timeout : 5000,
		cache : false,
		success:function(data){
			resetToken(data.token);
			if(typeof data.mutiple_request_error != 'undefined'){
				window.location.reload();
			}else if(typeof data.errMsg != 'undefined'){
				  //撤单失败
				  showGlobalError("撤单失败，"+data.errMsg);
			}else{
				  //撤单成功
				  showSuccessMsg('撤单委托已提交，','查询撤单状态？',function(){
					window.location.href='/stock/hk/'+_accountId+'/entrust.jspa';  
					});
				if(typeof getContent != 'undefined' && $.isFunction(getContent) && typeof nextUrl != 'undefined'){
					setTimeout(function(){getContent(nextUrl,1,'today');},300);
				}
			}
			JRJ.Dialogs.close();			
		 },
		 error : function(){
			window.location.reload();
		 }
	});
}
function doModifyOrder(url,stockName,orderNo,price,quantity){
	$.ajax({
		url : url,
		type : 'POST',
		data : {
			"stockName" : stockName,
			"orderNo" : orderNo,
			"price" : price,
			"quantity" : quantity,
			"xjb.token.name":getTokenName(),
			"xjb.token":getToken()
		},
		dataType : "json",
	    timeout : 5000,
		cache : false,
		success : function(data){
			resetToken(data.token);
			if(typeof data.mutiple_request_error != 'undefined'){
				window.location.reload();
			}else if(typeof data.errMsg != 'undefined'){
				  //改单失败
				  showGlobalError("改单失败，"+data.errMsg);
			  }else{
				  //改单成功
				  showSuccessMsg('委托单修改成功，','查委托单状态？',function(){
					window.location.href='/stock/hk/'+_accountId+'/entrust.jspa';  
					});
				
				if(typeof getContent != 'undefined' && $.isFunction(getContent) && typeof nextUrl != 'undefined'){
					setTimeout(function(){getContent(nextUrl,1,'today');},300);
				}
			  }
			JRJ.Dialogs.close();			  
		},
		error : function(){
			window.location.reload();
		}
	});
}