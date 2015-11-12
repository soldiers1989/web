var HQOTHERINFO ={
	dadan:"http://stock.jrj.com.cn/share,[#stockCode#],dadan.shtml",
	fenshi:"http://stock.jrj.com.cn/share,[#stockCode#],fenshi.shtml",
	jialiang:"http://stock.jrj.com.cn/share,[#stockCode#],fenjia.shtml",
	mingxi:"http://stock.jrj.com.cn/share,[#stockCode#],mingxi.shtml"
	};
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
var MainQuote = {
		hq:null,
		stock_id:null,
		inited:false,
		init : function(stock_id){
			this.inited=true;
			this.stock_id=stock_id;
			this._init();
			this.start();
		},
		_init:function(){//http://hkus.hqquery.jrj.com.cn/hkhq.do?ids=00465,02312&tpl=all&vname=aaa
			this.hq = new HqTiming("/stock/hkus/hkhq.do?ids="+this.stock_id+"&tpl=all&vname=mainQuote");
			this.hq.on(function(){			
				MainQuote.hqCallback();
			});
		},
		refresh:function(stock_id){
			this.stop();
			this.stock_id=stock_id;
			this._init();
			this.start();
		},
		start:function(){
			this.hq.start();	
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
			var sp1 = hqcon.find('#quote_sp1'),
			bp1 = hqcon.find('#quote_bp1'),
			ba1 = hqcon.find('#quote_ba1'),
			sa1 = hqcon.find('#quote_sa1'),
			price = hqcon.find("#quote_np"), 
//			hlp = hqcon.find("#quote_hlp"),
//			time = hqcon.find("#quote_time"),
			op = hqcon.find("#quote_op"),
			hp = hqcon.find("#quote_hp"),
			lp = hqcon.find("#quote_lp"),
			hhp = hqcon.find("#quote_hhp"),
			llp = hqcon.find("#quote_llp"),
			ape = hqcon.find("#quote_ape"),
			ta = hqcon.find("#quote_ta"),
			tr = hqcon.find("#quote_tr"),
			tm = hqcon.find('#quote_tm'),
			cot = hqcon.find('#quote_cot'),
			cat = hqcon.find('#quote_cat'),
//			outa = hqcon.find("#quote_outa"),
			apb = hqcon.find("#quote_apb"),
			lcp = hqcon.find("#quote_lcp"),
			tmv = hqcon.find('#quote_tmv'),
//			sl = hqcon.find("#quote_sl"),
//			inna = hqcon.find("#quote_inna"),
//			cwv = hqcon.find("#quote_cwv"),
//			pl = hqcon.find("#quote_pl"),
	        name=hqcon.find("#quote_nn"),
	        code=hqcon.find("#quote_code");
			var summary = mainQuote.Summary;
			//var columns = mainQuote.Column;
		    var	data = mainQuote.StockHq[0];
		  	if(!data||data==null||data.length==0) {
		  		price.html("停牌");
		  		setDomCls(price,RED_CLASS);
		  		return;
		  	}
		    var mstat = summary.mstat;  // 开盘状态 0：休市，1：开盘，2：集合竞价 3：中午休市

		    var prePrice = data["lcp"]; // 昨收价
		    var nowPrice = data["np"];
		    	// 停盘需要特殊处理
		    var tingpai =data["stp"];   //停牌标志
		    if(1==tingpai && nowPrice==0){
		    	if(prePrice == 0)
		    		price.html("未上市");
		    	else
		    		price.html("停牌");
		    	setDomCls(price,RED_CLASS);
		    }else{
		    	// 开盘状态
		    	price.html(wrapNum(nowPrice, 2));
				setDomCls(price,getColorCls(data["hlp"],0));   //现价 颜色设置    涨跌额>0 红  <0绿
		    }
		    if(1==tingpai && nowPrice==0 && prePrice == 0){
				sp1.html('--');
				bp1.html('--');
				sa1.html('--');
				ba1.html('--');
//		    	pl.html("--");
//		    	hlp.html("--");
		    	op.html("--");
		    	hp.html("--");
		    	lp.html("--");
				hhp.html("--");
				llp.html("--");
		    	ape.html("--");
		    	ta.html("--");
		    	tr.html("--");
				tm.html('--');
				cot.html('--');
				cat.html('--');
//		    	outa.html("--");
		    	apb.html("--");
		    	lcp.html("--");
//		    	sl.html("--");
//		    	inna.html("--");
//		    	cwv.html("--");
		    	name.html("--");
		    	code.html("--");
				tmv.html('--');
		    }else{
		    	name.html(data["name"]);
		    	code.html(data["code"]);
//		    	pl.html( wrapNum( data["pl"], 2, "%" ) );
			//hlp.html(wrapNum( data["hlp"], 3, "")	);
	                var _codeU = data["code"];
			//200或者900开头的为B股
//			if (_codeU.indexOf("200") == 0 || _codeU.indexOf("900") == 0) {
//				hlp.html(wrapNum( data["hlp"], 3, ""));
//			} else {
//				hlp.html(wrapNum( data["hlp"], 2, ""));
//			}
//			time.html(HqLoader.fomatHqTime(mainQuote.Summary.hqtime)[0]);
			sp1.html(wrapNum( data["sp1"], 2, ""));
			bp1.html(wrapNum( data["bp1"], 2, ""));
			sa1.html(wrapNum( data["sa1"], 0, "股"));
			ba1.html(wrapNum( data["ba1"], 0, "股"));
			op.html( wrapNum( data["op"], 2, "元") );
			hp.html( wrapNum( data["hp"], 2, "元") );
			lp.html( wrapNum( data["lp"], 2, "元") );
			hhp.html( wrapNum( data["hhp"], 2, "元") );
			llp.html( wrapNum( data["llp"], 2, "元") );
			if(data["ape"]<0){
				ape.html("亏损");
			}
			else{
				ape.html( wrapNum(data["ape"],2)); 
			}
			ta.html( wrapNum( data["ta"], 0, "股") );
			tr.html( wrapNum( data["tr"], 2, "%") );
			tm.html(wrapNum( data['tm'],2,'万元'));
			cot.html(wrapNum(data['cot'],2,''));
			cat.html(wrapNum(data['cat'],2,''));
//			outa.html( wrapNum( data["outa"], 0, "手") );
			apb.html(wrapNum(data["apb"],2));
			lcp.html( wrapNum( data["lcp"], 2, "元") );
//			sl.html( wrapNum( data["sl"], 2, "%") );
//			inna.html( wrapNum( data["inna"], 0, "手") );
//			cwv.html(wrapNum( data["cmv"]/10000, 2, "亿元") ); 
			tmv.html(wrapNum(data['tmv'],2,''));
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
(function($){
	function Board(boardId , config){
		this.boardId = boardId;
		this.isstart=false;
		this.config = {
			isLazyLoad : false, // 是否懒加载Board，默认不懒加载
			filter : null ,  // 行情数据过滤器
			callback : null,  // 加载成功后的回调函数
			stock_id:null
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
			if(!data || !data.HqData){
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
function DetailBoard(boardId , config){
	Board.apply(this,[boardId,config]);	
}
inherits(DetailBoard , Board);
// 重写repaint
DetailBoard.prototype.repaint=function(hqName){
	var data = window[hqName] , cfg = this.config;
	if(!data || !data.DetailData){
		// alert("请求行情失败");
		return ;
	}
	var data_Summary = data.Summary;
	data = data.DetailData;
	if(cfg.filter){
		data = cfg.filter(data);
	}
	var oFrag = document.createDocumentFragment();

	for(var i=0; i<data.length; i++){
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
	var cfg = this.config;
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
	$('<td >').html('<a href="http://stock.jrj.com.cn/share,'+cfg.stock_id+',dadan.shtml"><span class="'+(parseInt(data.A6,10)> 0 ? "red":"green")+'">'+(parseInt(data.A6,10)> 0 ? "买盘" : "卖盘")+'</span></a>').appendTo( tr );
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
	//var zuoshou_price =data_Summary.A4;
	var fenshi_time = data.A5 , fenshi_price = data.A1,fenshi_sum =data.A2;
	//var cls = getColorCls(fenshi_price , zuoshou_price);
	var cls='';
	if(data.A6 > 0){
		cls=RED_CLASS;
	}else if(data.A6 < 0){
		cls=GRE_CLASS;
	}
	var arrow='';
	/* if(cls==RED_CLASS){
		arrow = '↑';
	}else if(cls==GRE_CLASS){
		arrow = '↓';
	} */
	var tr = $("<tr>"),td1 = $('<td>'),td2 = $('<td>'),td3 = $('<td>');
	$("<span>").html( fenshi_time ).appendTo( td1 );
	$('<span>').html( wrapNum(fenshi_price,2) ).addClass(cls).appendTo( td2 );
	$('<span>').html( Math.round(fenshi_sum/100) + arrow ).addClass(cls).appendTo( td3 );
	td1.appendTo(tr);
	td2.appendTo(tr);
	td3.appendTo(tr);
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
		stock_id : null,
		init : function(stock_id){
		  // 初始化个board
			/*new Tab("detail",{
				panelPre:"detail_"				
			}); .onSwitch(function(tabname){				
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
			}); */
			this.stock_id=stock_id;
			this._init();
			Detail.currBoard = Detail.capBoard;
			this.start();
	  },_init:function(){
		var stock_id=this.stock_id;
		  
		// 分时 http://hkus.qmx.jrjimg.cn/mx.do?code=00001&page=1&size=5
		var oUr2 = "/stock/qmx/hkus/mx.do?code="+stock_id+"&page=1&size=5";
		var hqLoader2 = new HqTiming(oUr2,{hqName:"DetailAll_"+stock_id,charset:"GBK"});
		this.capBoard = new DetailCapitalBoard("detail_capital");
		this.capBoard.bind(hqLoader2);
		Detail.currBoard = Detail.capBoard;

	  },refresh:function(stock_id){
		  this.stop();
		  this.stock_id=stock_id;
		  this._init();
		  this.start();
	  },start:function(){
		  Detail.currBoard.start();
	  },stop:function(){
		  Detail.currBoard.stop();
	  }
	};
  /*
	 * 价量
	 */
var GeneralTrade={
	hq : null,
	stock_id:null,
	init : function(stock_id){
		this.stock_id=stock_id;
		this._init();
		this.start();
	},
	_init:function(){
		var stock_id=this.stock_id;
		this.hq = new HqTiming("/stock/qmx/fj.do?code="+stock_id+"&page=1&size=5",{hqName:"DetailPrice_"+stock_id,charset:"GBK"});
		this.hq.on(function(hqName){	
			GeneralTrade.GeneralTradeCallback(hqName);
		});
	},
	refresh:function(stock_id){
		this.stop();
		this.stock_id=stock_id;
		this._init();
		this.start();
	},
	start : function(){
		this.hq.start();
	},
	stop : function(){
		this.hq.stop();
	},
	GeneralTradeCallback : function(hqName){
		if(!hqName || ! window[hqName]){
			return ;
		}
		/*
		 * 价量数据(分价)
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
		}else{
			$("#fenjia li").empty();
		}
	}
  };
var Conclude={
		stock_id:null,
		init:function(stock_id){
			this.stock_id=stock_id;
			this._init();
		},_init:function(){
			$("#ddimage").attr("src","/stock/share/"+this.stock_id+"/nd/dd.png?_="+new Date())
		},refresh:function(stock_id){
			this.stock_id=stock_id;
			this._init();
		}
		
}

if (typeof XJB == "undefined" || !XJB) {
	var XJB = {};
}
XJB.HQ={
		inited:false,
		hqFlash:function(stock_id,id,width,height){
			$("#"+id).empty().html('<div id="FlashHQSmall"><span class="load2">数据加载中......</span></div>');
			
			var flashvars = {
				stock_code: stock_id // 股票代码
			};
			var params = {   
				menu: "false",
				scale: "noScale",
				AllowScriptAccess : "always",
				wmode:"transparent",
				allowFullScreen:"true"
			};
			var attributes = {
				id:"stock_flash"
			};
			var flashURL="/stock/stockFlash/H_StockHQLite.swf"; 
			
			swfobject.embedSWF(flashURL, "FlashHQSmall", width, height, "10.0.0", "",flashvars,params,attributes);
		},
		showHq:function(stock_id){
			if(this.inited){
				MainQuote.refresh(stock_id);
				Detail.refresh(stock_id);
				//GeneralTrade.refresh(stock_id);
				//Conclude.refresh(stock_id);
			}else{
				this.inited=true;
			  MainQuote.init(stock_id);
			  Detail.init(stock_id);
			  //GeneralTrade.init(stock_id);	
			  //Conclude.init(stock_id);
			}
		},
		searchBox:function(input_box,input_btn,callback,isAutoFocus){
			var sb={};
			var params2 = "&item=10&inc=utf8&otc=utf8&type=hk"
			 var sb = new JRJ.ui.SearchBox(input_box,input_btn,com_combox_callback,params2,null,null,{
				 inputFocusClass: "sb-input-focus",
				 autoFocus: isAutoFocus == undefined ? true:isAutoFocus,
				 clearInput: false,
				 hasHistory:false
				});
			//sb.muti=true;
			sb.blurFunc = inputblur;
			var st=false;
			var codeUseAble=false;
			function inputblur (){  //一般blur事件不进行股票操作
				try{
					if(sb.slist != undefined && sb.slist.length > 0 && st == false){
						st=true;
						codeUseAble=true;
						stock_callback(sb.slist[this.selectedIndex]);
					}
				}catch(e){}
				}
			function com_combox_callback(s1){
				codeUseAble=true;
				sb.hide();
				if(!st){
					stock_callback(s1[0]);
				}
			}
			function stock_callback(stock){
				sb.hide();
				if(callback!=undefined){
					callback(stock);
				}
			}
			$("#"+input_box).bind("focus",function(){st=false});
		}
};
function isReady(){
    return true;
}