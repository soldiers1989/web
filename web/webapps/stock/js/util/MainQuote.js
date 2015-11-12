// 类继承
function inherits(subClass,superClass){
	var F = function(){};
	F.prototype = superClass.prototype;
	subClass.prototype = new F();
	subClass.superClass_ = superClass.prototype;
	
	subClass.prototype.constructor = subClass;
}

/* 公共组件 */
(function($){	
	function Tab(navsId,config){
		var navs = $("#" + navsId);
		this.config = {
			panelPre : "",  // panel id前缀
			triggerType : "mouseover"  , // 切换tab的事件 click
			activeCls : "hover" ,   // tab激活状态的class
			activeIndex : 0     // 激活tab index
		}
		$.extend(this.config , config);
		this.triggers = navs.find("li");
		this.listeners = [];
		this._init();
	}
	Tab.prototype = {
		_init : function(){
			var self = this;
			var cfg = self.config;
			
			self.activedIndex = cfg.activeIndex;
			self.actived = self.triggers[self.activedIndex];
			
			self.triggers.each(function(index){
				$(this).bind(cfg.triggerType,function(){
  				self._onFocusTrigger(index);
  			});
			});
			return self;
		},
		_onFocusTrigger : function(index){
			var cfg = this.config;
			
			if(index != this.activedIndex){
				var toTrigger = this.triggers[index];
				var toPanelName = toTrigger.getAttribute("for");
				$.each(this.listeners,function(i){
					// 给回调函数绑定两个参数，toPanelName:切换到面板的名称为li的for属性,index:面板序号
					this.fn.call(this.scope,toPanelName,index)
				});
				
				$(this.actived).removeClass(cfg.activeCls);				
				$("#" + cfg.panelPre + this.actived.getAttribute("for")).hide();
								
				$(toTrigger).addClass(cfg.activeCls);				
				$("#" + cfg.panelPre + toPanelName).show();
				
				this.actived = toTrigger;
				this.activedIndex = index;
			}
		},
		onSwitch : function(callback , scope){
			this.listeners.push({fn:callback , scope:scope || this});
			return this;
		}
	};
	window.Tab = Tab;
})(jQuery);
(function($){
	function Board(boardId , config){
		this.boardId = boardId;
		this.isstart=false;
		this.config = {
			isLazyLoad : false, // 是否懒加载Board，默认不懒加载
			filter : null ,  // 行情数据过滤器
			callback : null  // 加载成功后的回调函数
		}
		$.extend(this.config,config);
		this._init();
	}
	Board.prototype = {
		_init : function(){
			this.board = $("#"+this.boardId);
			this.body = this.board.find("tbody");
		},
		bind : function(hqloader){
			var cfg = this.config;
			this.hqloader = hqloader;			
			if(cfg.isLazyLoad){				
				DataLazyLoad.lazyLoadBoards[this.boardId] = { "instance" : this , "status" : false };
			}
			// this.hqloader.add();
			this.hqloader.on(this.repaint , this); // 绑定处理函数
			return this;
		},
		isBegin : function(){
			return this.isstart;
		},
		start : function(){
			// 防止启动多次
			if(!this.isBegin() && this.hqloader){
				this.hqloader.start();		
				this.isstart=true;
			}
			return this;
		},
		stop : function(){
			if(this.hqloader){
				this.hqloader.stop();	
				this.isstart=false;
			}
			return this;
		},
		repaint : function(hqName){
			var data = window[hqName] , cfg = this.config;
			if(!data.HqData){
				// alert("请求行情失败");
				return ;
			}
			var column = data.Column;
			data = data.HqData;
			if(cfg.filter){
				data = cfg.filter(data);
			}
			
			var oFrag = document.createDocumentFragment();
			for(var i=0; i<data.length; i++){
				if(data[i][0]){
					oFrag.appendChild(this.draw(i,data[i],column));
				}
			}
			this.board.removeClass("process");
			this.body.empty().append( oFrag );
			
			if(cfg.callback){
				cfg.callback(data);
			}
		},
		draw : function(i,data,column){
			var code = data[column["code"]] , pl = data[column["pl"]];
			var cls = getColorCls(pl , 0);
			var tr = $("<tr>");		
			$("<td>").html( createStockA(code,data[column["name"]]) ).appendTo( tr );
			$('<td class="jrj-tr">').html( wrapNum(data[column["np"]],2) ).addClass(cls).appendTo( tr );
			$('<td class="jrj-tr">').html( wrapNum(pl,2,"%") ).addClass(cls).appendTo( tr );
			if(i%2){
				tr.addClass("cur");
			}
			return tr[0];		
		}
	};
	window.Board = Board;
})(jQuery);

/**
 * @fileOverview 主行情脚本，指数行情，大单明细，最近访问股票，自选股，行情图比较
 */
var hasSetWarning = false;
var MainQuote = {
	hq:null,
	init : function(){
		this.hq = new HqTiming("/stock/q/?q=cn|s&i="+stock_id+"&n=mainQuote&c=l");
		this.hq.on(function(){			
			MainQuote.hqCallback();
		}).start();	
		// 初始化实时报价tab
		new Tab("real_trade",{
    	panelPre:"real_trade_"
		}).onSwitch(function(tabname){				
				switch(tabname){
					case "five" :
					//	GeneralTrade.stop();
					    GeneralTrade.start();
						Detail.currBoard.start();
						break ;
					case "general" :
					  	Detail.currBoard.stop();
					  	GeneralTrade.stop();
					//	GeneralTrade.start();
					  break ;				
			  }
		});
	},
	stop : function(){
		this.hq.stop();
	},
	hqCallback : function(){		
		if(!mainQuote){
			return ;
		}
		/*
		 * main报价数据
		 */
		var hqcon = $("#quote_con1"); 
		var price = hqcon.find("#quote_np"), hlp = hqcon.find("#quote_hlp"),time = hqcon.find("#quote_time"),
		op = hqcon.find("#quote_op"),hp = hqcon.find("#quote_hp"),lp = hqcon.find("#quote_lp"),
		ape = hqcon.find("#quote_ape"),ta = hqcon.find("#quote_ta"),tr = hqcon.find("#quote_tr"),
		outa = hqcon.find("#quote_outa"),apb = hqcon.find("#quote_apb"),lcp = hqcon.find("#quote_lcp"),
		sl = hqcon.find("#quote_sl"),inna = hqcon.find("#quote_inna"),cwv = hqcon.find("#quote_cwv"),
		pl = hqcon.find("#quote_pl"),
        name=hqcon.find("#quote_nn"),
        code=hqcon.find("#quote_code")
		var summary = mainQuote.Summary;
		var columns = mainQuote.Column;
	    var	data = mainQuote.HqData[0];
	  	if(!data||data==null||data.length==0) {
	  		price.html("停牌");
	  		setDomCls(price,RED_CLASS);
	  		return;
	  	}
	    var mstat = summary.mstat;  // 开盘状态 0：休市，1：开盘，2：集合竞价 3：中午休市

	    var prePrice = data[columns["lcp"]]; // 昨收价
	    var nowPrice = data[columns["np"]];
	    	// 停盘需要特殊处理
	    var tingpai =data[columns["stp"]];   //停牌标志
	    if(1==tingpai && nowPrice==0){
	    	if(prePrice == 0)
	    		price.html("未上市");
	    	else
	    		price.html("停牌");
	    	setDomCls(price,RED_CLASS);
	    }else{
	    	// 开盘状态
	    	price.html(wrapNum(nowPrice, 2));
			setDomCls(price,getColorCls(data[columns["hlp"]],0));   //现价 颜色设置    涨跌额>0 红  <0绿
	    }
	    if(1==tingpai && nowPrice==0 && prePrice == 0){
	    	pl.html("--");
	    	hlp.html("--");
	    	op.html("--");
	    	hp.html("--");
	    	lp.html("--");
	    	ape.html("--");
	    	ta.html("--");
	    	tr.html("--");
	    	outa.html("--");
	    	apb.html("--");
	    	lcp.html("--");
	    	sl.html("--");
	    	inna.html("--");
	    	cwv.html("--");
	    	name.html("--");
	    	code.html("--")
	    }else{
	    	name.html(data[columns["name"]]);
	    	code.html(data[columns["code"]]);
	    	pl.html( wrapNum( data[columns["pl"]], 2, "%" ) );
		//hlp.html(wrapNum( data[columns["hlp"]], 3, "")	);
                var _codeU = data[columns["code"]];
		//200或者900开头的为B股
		if (_codeU.indexOf("200") == 0 || _codeU.indexOf("900") == 0) {
			hlp.html(wrapNum( data[columns["hlp"]], 3, ""));
		} else {
			hlp.html(wrapNum( data[columns["hlp"]], 2, ""));
		}
		time.html(HqLoader.fomatHqTime(mainQuote.Summary.hqtime)[0]);
		op.html( wrapNum( data[columns["op"]], 2, "元") );
		hp.html( wrapNum( data[columns["hp"]], 2, "元") );
		lp.html( wrapNum( data[columns["lp"]], 2, "元") );
		if(data[columns["ape"]]<0){
			ape.html("亏损");
		}
		else{
			ape.html( wrapNum(data[columns["ape"]],2)); 
		}
		ta.html( wrapNum( data[columns["ta"]], 0, "手") );
		tr.html( wrapNum( data[columns["tr"]], 2, "%") );
		outa.html( wrapNum( data[columns["outa"]], 0, "手") );
		apb.html(wrapNum(data[columns["apb"]],2));
		lcp.html( wrapNum( data[columns["lcp"]], 2, "元") );
		sl.html( wrapNum( data[columns["sl"]], 2, "%") );
		inna.html( wrapNum( data[columns["inna"]], 0, "手") );
		cwv.html(wrapNum( data[columns["cmv"]]/10000, 2, "亿元") ); 
		setDomCls(pl,getColorCls(data[columns["pl"]]));
		setDomCls(hlp,getColorCls(data[columns["hlp"]]));
		setDomCls(op,getColorCls(data[columns["op"]],prePrice));
		setDomCls(hp,getColorCls(data[columns["hp"]],prePrice));   //与昨收比
		setDomCls(lp,getColorCls(data[columns["lp"]],prePrice));	//与昨收比
	    }
		/*
		 * 实时报价
		 */ 
			var realData = [],html = "";
			realData.push([data[columns["sp5"]],data[columns["sa5"]]]);
			realData.push([data[columns["sp4"]],data[columns["sa4"]]]);
			realData.push([data[columns["sp3"]],data[columns["sa3"]]]);
			realData.push([data[columns["sp2"]],data[columns["sa2"]]]);
			realData.push([data[columns["sp1"]],data[columns["sa1"]]]);
			
			realData.push([data[columns["np"]],data[columns["pa"]]]);
			
			realData.push([data[columns["bp1"]],data[columns["ba1"]]]);
			realData.push([data[columns["bp2"]],data[columns["ba2"]]]);
			realData.push([data[columns["bp3"]],data[columns["ba3"]]]);
			realData.push([data[columns["bp4"]],data[columns["ba4"]]]);
			realData.push([data[columns["bp5"]],data[columns["ba5"]]]);
			
			realData.push([data[columns["cot"]],data[columns["cat"]]]);
			
			var compare=data[columns["lcp"]];
			// 设置时间
			var real_trade_five_time=$("#real_trade_five_time");
			real_trade_five_time.html(HqLoader.fomatHqTime(mainQuote.Summary.hqtime)[0].substring(11,HqLoader.fomatHqTime(mainQuote.Summary.hqtime)[0].length));
			var real =$("#real_trade_five");
			for(var i=0;i<realData.length;i++){
				var realData1=realData[i][0],realData2=Math.round(realData[i][1]/100);
				switch(i){
				case 0: 
					real_price_5hq_data(real.find("#sell_five"),real.find("#sell_five_quantity"),realData1,realData2,compare);break;
				case 1:
					real_price_5hq_data(real.find("#sell_four"),real.find("#sell_four_quantity"),realData1,realData2,compare);break;
				case 2:
					real_price_5hq_data(real.find("#sell_three"),real.find("#sell_three_quantity"),realData1,realData2,compare);break;
				case 3: 
					real_price_5hq_data(real.find("#sell_two"),real.find("#sell_two_quantity"),realData1,realData2,compare);break;
				case 4:
					real_price_5hq_data(real.find("#sell_one"),real.find("#sell_one_quantity"),realData1,realData2,compare);break;
				case 5:
					var chengjiao =real.find("#chengjiao"),xianshou=real.find("#xianshou");
					chengjiao.html(wrapNum(realData[i][0],2));
					xianshou.html(Math.round(realData[i][1]));
					setDomCls(chengjiao,getColorCls(wrapNum(realData[i][0],2),compare));
					setDomCls(xianshou,getColorCls(wrapNum(realData[i][0],2),compare));
					break;
				case 6:
					real_price_5hq_data(real.find("#buy_one"),real.find("#buy_one_quantity"),realData1,realData2,compare);break;
				case 7:
					real_price_5hq_data(real.find("#buy_two"),real.find("#buy_two_quantity"),realData1,realData2,compare);break;
				case 8:
					real_price_5hq_data(real.find("#buy_three"),real.find("#buy_three_quantity"),realData1,realData2,compare);break;
				case 9:
					real_price_5hq_data(real.find("#buy_four"),real.find("#buy_four_quantity"),realData1,realData2,compare);break;
				case 10:
					real_price_5hq_data(real.find("#buy_five"),real.find("#buy_five_quantity"),realData1,realData2,compare);break;
				case 11:
					var weibi =real.find("#weibi"),liangbi=real.find("#liangbi");
					weibi.html(wrapNum(realData[i][0],2));
					liangbi.html("量比 <span>"+Math.round(realData[i][1])+"</span>");
					break;
				}
			}
			if(true==hasSetWarning){
				EalyWarning.warning(parseFloat(nowPrice,10));
			}
    }
}
/**
 * 实时报价
 * @param id1 价格<td>object
 * @param id2 万手(数量)<td>objec
 * @param realData1 价格
 * @param realData2  万手
 * @param compare   当前价
 * @return
 */
function real_price_5hq_data(id1,id2,realData1,realData2,compare){
	var cls=getColorCls(realData1,compare);
	id1.html(wrapNum(realData1,2));
	setDomCls(id1,cls);
	id2.html(realData2+"");
	setDomCls(id2,cls);
}
 
function DetailBoard(boardId , config){
	Board.apply(this,[boardId,config]);	
}
inherits(DetailBoard , Board);
// 重写repaint
DetailBoard.prototype.repaint=function(hqName){
	var data = window[hqName] , cfg = this.config;
	if(!data.DetailData){
		// alert("请求行情失败");
		return ;
	}
	var data_Summary = data.Summary;
	data = data.DetailData;
	if(cfg.filter){
		data = cfg.filter(data);
	}
	var oFrag = document.createDocumentFragment();

	for(var i=0; i<4; i++){
		//if(data[i]){
			oFrag.appendChild(this.draw(data[i],data_Summary));
		//}
	}
	this.board.removeClass("process");
	this.body.empty().append( oFrag );
	
	if(cfg.callback){
		cfg.callback(data);
	}
}
/*
 * 大单
 */
function DetailBigBoard(boardId , config){
	Board.apply(this,[boardId,config]);
}
inherits(DetailBigBoard , DetailBoard);
DetailBigBoard.prototype.draw=function(data,data_Summary){
	if(!data){
		var tr = $("<tr>");
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );	
		return tr[0];
	}
	var zuoshou_price =data_Summary.A4;
	var dadan_time = data.A5 , dadan_price = data.A1,dadan_sum =data.A2;
	var cls1 =getColorCls(parseInt(data.A6,10),0);
	var cls2 = getColorCls(dadan_price , zuoshou_price);
	var tr = $("<tr>");		
	$("<td>").html( dadan_time ).appendTo( tr );
	$('<td>').html( wrapNum(dadan_price,2) ).addClass(cls2).appendTo( tr );
	$('<td class="jrj-tr">').html( Math.round(dadan_sum/100)  ).addClass(cls1).appendTo( tr );
	$('<td >').html('<a href="http://stock.jrj.com.cn/share,'+stock_id+',dadan.shtml"><span class="'+(parseInt(data.A6,10)> 0 ? "jrj-red":"jrj-gre")+'">'+(parseInt(data.A6,10)> 0 ? "买盘" : "卖盘")+'</span></a>').appendTo( tr );
	return tr[0];	
}
/*
 * 分时
 */
function DetailCapitalBoard(boardId , config){
	Board.apply(this,[boardId,config]);
}
inherits(DetailCapitalBoard , DetailBoard);
DetailCapitalBoard.prototype.draw=function(data,data_Summary){
	if(!data){
		var tr = $("<tr>");
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );
		return tr[0];
	}
	var zuoshou_price =data_Summary.A4;
	var fenshi_time = data.A5 , fenshi_price = data.A1,fenshi_sum =data.A2;
	var cls = getColorCls(fenshi_price , zuoshou_price);
	var tr = $("<tr>");		
	$("<td>").html( fenshi_time ).appendTo( tr );
	$('<td>').html( wrapNum(fenshi_price,2) ).addClass(cls).appendTo( tr );
	$('<td class="jrj-tr">').html( Math.round(fenshi_sum/100) ).addClass(cls).appendTo( tr );
	return tr[0];	
} 
/*
 * 明细
 */
function DetailEachBoard(boardId , config){
	Board.apply(this,[boardId,config]);
}
inherits(DetailEachBoard , DetailBoard);
DetailEachBoard.prototype.draw=function(data,data_Summary){
	if(!data){
		var tr = $("<tr>");
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );
		$("<td>").html("-").appendTo( tr );
		return tr[0];
	}
	var zuoshou_price =data_Summary.A4;
	var mingxi_time = data.A5 , mingxi_price = data.A1,mingxi_sum =data.A2;
	var cls1 =getColorCls(parseInt(data.A6),0);
	var cls2= getColorCls(mingxi_price , zuoshou_price);
	var tr = $("<tr>");		
	$("<td>").html( mingxi_time ).appendTo( tr );
	$('<td>').html( wrapNum(mingxi_price,2) ).addClass(cls2).appendTo( tr );
	$('<td class="jrj-tr">').html( Math.round(mingxi_sum/100)+(cls1=="jrj-red"?"↑":"↓")).addClass(cls1).appendTo( tr );
	return tr[0];
} 
  var Detail = {
		currBoard : null,
		bigBoard : null,
		capBoard : null,
		eachBoard : null,
		init : function(){
		  // 初始化个board
			new Tab("detail",{
				panelPre:"detail_"				
			}).onSwitch(function(tabname){				
				Detail.currBoard.stop();
				switch(tabname){
					case "big" :
						Detail.bigBoard.start();
						Detail.currBoard = Detail.bigBoard;
						break ;
					case "capital" :
					  Detail.capBoard.start();
						Detail.currBoard = Detail.capBoard;
					  break ;
					case "each" :
					  Detail.eachBoard.start();
						Detail.currBoard = Detail.eachBoard;
					  break ;					
			  }
			});
			 // 大单
			 var oUrl = "/stock/qmx/dd.do?code="+stock_id+"&page=1&size=4";
			 var hqLoader1 = new HqTiming(oUrl,{hqName:"DetailBig_"+stock_id});
			 this.bigBoard = new DetailBigBoard("detail_big");
			 this.bigBoard.bind(hqLoader1);
			 // 分时
			 var oUr2 = "/stock/qmx/fs.do?code="+stock_id+"&page=1&size=4";
			 var hqLoader2 = new HqTiming(oUr2,{hqName:"DetailMin_"+stock_id});
			 this.capBoard = new DetailCapitalBoard("detail_capital");
			 this.capBoard.bind(hqLoader2);
			 // 明细
			 var oUr3 = "/stock/qmx/mx.do?code="+stock_id+"&page=1&size=4";
			 var hqLoader3 = new HqTiming(oUr3,{hqName:"DetailAll_"+stock_id});
			 this.eachBoard = new DetailEachBoard("detail_each");
			 this.eachBoard.bind(hqLoader3);
			 Detail.currBoard = Detail.bigBoard;
			 Detail.currBoard.start();
	  }

	}
  /*
	 * 成交概况
	 */
var GeneralTrade={
	hq : null,
	init : function(){
		this.hq = new HqTiming("/stock/qmx/fj.do?code="+stock_id+"&page=1&size=5",{hqName:"DetailPrice_"+stock_id});
		this.hq.on(function(hqName){	
			GeneralTrade.GeneralTradeCallback(hqName);
		});
	},
	start : function(){
		this.hq.start();
	},
	stop : function(){
		this.hq.stop();
	},
	GeneralTradeCallback : function(hqName){
		if(!hqName){
			return ;
		}
		/*
		 * 成交概况数据(分价)
		 */
		var summary = window[hqName].Summary;
		var data = window[hqName].DetailData;
		var zuoshou_price =summary.A4;
		if(data && data.length>0){
		/*
		 * 取得最大值
		 */
			var max=null;
		switch(data.length){
		case 1 :{
			max=data[0].A2/10000;
			var fenjia_1 =$("#fenjia_1");
			var str1 = "<span class="+getColorCls(data[0].A1,zuoshou_price)+">"+wrapNum(data[0].A1,2)+"</span>";
			    str1+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em></i>";
			    str1+=wrapNum(data[0].A2/10000,2);
			 fenjia_1.html(str1);
			break;
			}
		case 2 :{
			max=data[0].A2/10000>data[1].A2/10000?data[0].A2/10000:data[1].A2/10000;
			var fenjia_1 =$("#fenjia_1"),fenjia_2 =$("#fenjia_2")
			var str1 = "<span class="+getColorCls(data[0].A1,zuoshou_price)+">"+wrapNum(data[0].A1,2)+"</span>";
			    str1+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em></i>";
			    str1+=wrapNum(data[0].A2/10000,2);
			var str2 = "<span class="+getColorCls(data[1].A1,zuoshou_price)+">"+wrapNum(data[1].A1,2)+"</span>";
			    str2+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em></i>";
			    str2+=wrapNum(data[1].A2/10000,2);
			fenjia_1.html(str1);
			fenjia_2.html(str2);
			break;
			}
		case 3 :{
		var max1=data[0].A2/10000>data[1].A2/10000?data[0].A2/10000:data[1].A2/10000;
			max=data[2].A2/10000>max1?data[2].A2/10000:max1;
		var fenjia_1 =$("#fenjia_1"),fenjia_2 =$("#fenjia_2"),fenjia_3=$("#fenjia_3")
		var str1 =  "<span class="+getColorCls(data[0].A1,zuoshou_price)+">"+wrapNum(data[0].A1,2)+"</span>";
		    str1+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em></i>";
		    str1+=wrapNum(data[0].A2/10000,2);
		var str2 = "<span class="+getColorCls(data[1].A1,zuoshou_price)+">"+wrapNum(data[1].A1,2)+"</span>";
		    str2+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em></i>";
		    str2+=wrapNum(data[1].A2/10000,2);
		var str3 = "<span class="+getColorCls(data[2].A1,zuoshou_price)+">"+wrapNum(data[2].A1,2)+"</span>";
			str3+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[2].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[2].A2/10000/max*60)+"px\"></em></i>";
			str3+=wrapNum(data[2].A2/10000,2);
		fenjia_1.html(str1);
		fenjia_2.html(str2);
		fenjia_3.html(str3);
		break;
			}
		case 4 :{
			var fenjia_1 =$("#fenjia_1"),fenjia_2 =$("#fenjia_2"),fenjia_3=$("#fenjia_3"),fenjia_4=$("#fenjia_4")
			var max1=data[0].A2/10000>data[1].A2/10000?data[0].A2/10000:data[1].A2/10000;
			var max2=data[2].A2/10000>data[3].A2/10000?data[2].A2/10000:data[3].A2/10000;
				max=max1>max2?max1:max2;
			var str1 =  "<span class="+getColorCls(data[0].A1,zuoshou_price)+">"+wrapNum(data[0].A1,2)+"</span>";
			    str1+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em></i>";
			    str1+=wrapNum(data[0].A2/10000,2);
			var str2 = "<span class="+getColorCls(data[1].A1,zuoshou_price)+">"+wrapNum(data[1].A1,2)+"</span>";
			    str2+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em></i>";
			    str2+=wrapNum(data[1].A2/10000,2);
			var str3 = "<span class="+getColorCls(data[2].A1,zuoshou_price)+">"+wrapNum(data[2].A1,2)+"</span>";
				str3+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[2].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[2].A2/10000/max*60)+"px\"></em></i>";
				str3+=wrapNum(data[2].A2/10000,2);
			var str4 = "<span class="+getColorCls(data[3].A1,zuoshou_price)+">"+wrapNum(data[3].A1,2)+"</span>";
				str4+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[3].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[3].A2/10000/max*60)+"px\"></em></i>";
				str4+=wrapNum(data[3].A2/10000,2);
			fenjia_1.html(str1);
			fenjia_2.html(str2);
			fenjia_3.html(str3);
			fenjia_4.html(str4);
			}
		case 5 :{
			try{
			var max1=data[0].A2/10000>data[1].A2/10000?data[0].A2/10000:data[1].A2/10000;
			var max2=data[2].A2/10000>data[3].A2/10000?data[2].A2/10000:data[3].A2/10000;
			var max3=max1>max2?max1:max2;
			    max =max3>data[4].A2/10000?max3:data[4].A2/10000;
			var fenjia_1 =$("#fenjia_1"),fenjia_2 =$("#fenjia_2"),fenjia_3=$("#fenjia_3"),fenjia_4=$("#fenjia_4"),fenjia_5=$("#fenjia_5")
			var str1 =  "<span class="+getColorCls(data[0].A1,zuoshou_price)+">"+wrapNum(data[0].A1,2)+"</span>";
			    str1+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[0].A2/10000/max*60)+"px\"></em></i>";
			    str1+=wrapNum(data[0].A2/10000,2);
			var str2 = "<span class="+getColorCls(data[1].A1,zuoshou_price)+">"+wrapNum(data[1].A1,2)+"</span>";
			    str2+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[1].A2/10000/max*60)+"px\"></em></i>";
			    str2+=wrapNum(data[1].A2/10000,2);
			var str3 = "<span class="+getColorCls(data[2].A1,zuoshou_price)+">"+wrapNum(data[2].A1,2)+"</span>";
				str3+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[2].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[2].A2/10000/max*60)+"px\"></em></i>";
				str3+=wrapNum(data[2].A2/10000,2);
			var str4 = "<span class="+getColorCls(data[3].A1,zuoshou_price)+">"+wrapNum(data[3].A1,2)+"</span>";
				str4+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[3].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[3].A2/10000/max*60)+"px\"></em></i>";
				str4+=wrapNum(data[3].A2/10000,2);
			var str5 = "<span class="+getColorCls(data[4].A1,zuoshou_price)+">"+wrapNum(data[4].A1,2)+"</span>";
				str5+="<i class=\"bar\"><em class=\"out\" style=\"width:"+parseInt(data[4].A2/10000/max*60)+"px\"></em><em class=\"in\" style=\"width:"+parseInt(data[4].A2/10000/max*60)+"px\"></em></i>";
				str5+=wrapNum(data[4].A2/10000,2);
			fenjia_1.html(str1);
			fenjia_2.html(str2);
			fenjia_3.html(str3);
			fenjia_4.html(str4);
			fenjia_5.html(str5);	
			}catch(e){}
			break;
				}
			}
		}
	}
  }
  
  $(function(){
	  MainQuote.init();
	  Detail.init();
	  GeneralTrade.init();
	  GeneralTrade.start();
  })
	  
