if (typeof JRJ == "undefined" || !JRJ) {
	var JRJ = {};
}if (typeof JRJ.Trade == "undefined" || !JRJ.Trade) { JRJ.Trade = {} }
JRJ.Trade.Other={
	stocks:[],
	inited:false,
	check:function(code,callback){
		var stockCode=$("#"+code).val();
		var stockInfo=this.stocks[stockCode];
		if(stockInfo!=undefined){
			var stock={};
			stock.other=1;
			stock.code=stockInfo.code;
			stock.name=stockInfo.sname;
			stock.stat=stockInfo.status;
			stock.stid=stockInfo.innerCode;
			switch (stockInfo.marktType)
			{
			case 1:
				stock.mrkt="cn.sz";
				break;
			case 2:
				stock.mrkt="cn.sh";
				break;
			default:
				stock.mrkt="";
				break;
			}
			if(stockInfo.type==7){
				stock.type="r";
			}
			if(callback){
				callback(stock);
			}
		}
	},
	init:function(){
		var _this=this;
		$.getScript("/stock/trade/stocks.jspa",
		  	function(data){
			_this.inited=true;
			_this.stocks=JRJ.Trade.STOCKS;
		  	}
		);
	}
}
$(function(){
	JRJ.Trade.Other.init();
})