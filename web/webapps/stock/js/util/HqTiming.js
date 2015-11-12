var HQ_SERVER = "/stock/q/";
var URL_SHARE = "http://stock.jrj.com.cn/share,[#stock_id#].shtml";
var RED_CLASS = "red" , GRE_CLASS = "green";
/* 公共函数 */
//计算数据对应的class
function getColorCls(numb , compare){
	var num = parseFloat(numb);
	var compare = compare === undefined ? 0 : compare;
	return num>compare ? RED_CLASS : ( num==compare ? "" : GRE_CLASS );
}
//包装数字，参数：num 数字 decimal 小数位 unit 单位
function wrapNum(num,decimal,unit){
	var num = parseFloat(num);
	return isNaN(num) ? "--" : (decimal ? num.toFixed(decimal) : num) + (unit ? unit : "");
}
//交换颜色，用于港股、美股
function swapColor(cls){
	return cls == RED_CLASS ? GRE_CLASS : RED_CLASS ;
}

//设置颜色class
function setDomCls(jqObject,cls){
	jqObject.removeClass(RED_CLASS).removeClass(GRE_CLASS).addClass(cls);
}
//创建股票链接
function createStockA(stockId,text,title,target){
	var tit = text , tar = "_blank"; 
	if(title){ tit = text;}
	if(target){ tar = target; }
	return '<a href="' + URL_SHARE.replace("[#stock_id#]",stockId) + '" title="'+tit+'"' + ' target="'+tar+'">' + text + '</a>';
}
var HqLoader = {
	Hq_Server : HQ_SERVER ,
	CN_Status_Server : HQ_SERVER + "?q=cn|i&n={%obj_name%}&i=_hqstatus" ,
	// HK_Status_Server : HQ_SERVER + "?q=hk|i&n={%obj_name%}&i=_hqstatus" ,
	tradingDays : [1,2,3,4,5],	
	cnTradingTime : [930,1130,1300,1500], // A股：上午 9:30am 11:30am 下午 1:00pm
											// 3:00pm
	hkTradingTime : [1000,1245,1430,1615], // H股：上午 10:00am 12:30pm 下午 2:30pm
											// 4:00pm
	
	timeOffset : 18,
	
	cnMkStatus : false ,
	hkMkStatus : false,
	
	getMKStatDone : false ,

  initMkStatus : function(){
		var oUrl = this.CN_Status_Server;
		oUrl = oUrl.replace("{%obj_name%}","HqLoader_CN_Stat");
		$.getScript(oUrl , HqLoader._statCallback);
  },
  _statCallback : function(){
  	var clientTime = new Date().getTime();
  	var thisObj = HqLoader;
  	var weekDay , isWeekEnd ;
  	
  	if(HqLoader_CN_Stat){
			var dtime = thisObj.fomatHqTime( HqLoader_CN_Stat.Summary.bdtime );
			thisObj.timeDiff = dtime[1].getTime() - clientTime; // 服务器返回客户端的时间忽略不计
			weekDay = dtime[1].getDay();
      isWeekEnd = $.inArray(weekDay,thisObj.tradingDays) < 0 ;
     
		  if( !isWeekEnd ){		  	
				thisObj.isCnMkDay = true;
				thisObj.isHkMkDay = true;	
		  	thisObj._calcMkStatus();
		  }						
  	}
  	
  	thisObj.getMKStatDone = true;
		if(isWeekEnd){ return ;}
	  setInterval(function(){thisObj._calcMkStatus()},30000);
  },	
	_calcMkStatus : function(){		
		var sysCurTime = new Date();
		sysCurTime.setTime(sysCurTime.getTime() + this.timeDiff);
		var hm = sysCurTime.getHours()*100 + sysCurTime.getMinutes();
		var m = this.timeOffset;// 开盘时间偏移
		
		if((hm>=this.cnTradingTime[0]-m && hm<=this.cnTradingTime[1]+m) || (hm>=this.cnTradingTime[2] - 40 -m && hm<=this.cnTradingTime[3]+m)){
			this.cnMkStatus = true;
	  }else{
	  	this.cnMkStatus = false;
	  }

		if((hm>=this.hkTradingTime[0] - 40 -m && hm<=this.hkTradingTime[1]+m) || (hm>=this.hkTradingTime[2]-m && hm<=this.hkTradingTime[3]+m)){
			this.hkMkStatus = true;
		}else{
			this.hkMkStatus = false;
		}
  },
  load : function(src, callback, charset){
	  var script = document.createElement('script');  
	  script.setAttribute('type', 'text/javascript'); 
	  
	  if (charset) {  
	  	script.setAttribute('charset', charset);  
	  }			    
	  script.setAttribute('src', src);
	  
	  var head = document.getElementsByTagName("head")[0] || document.documentElement;		    	  
	  var done = false;

	  script.onload = script.onreadystatechange = function() {
			if ( !done && (!this.readyState ||
					this.readyState === "loaded" || this.readyState === "complete") ) {
				done = true;
				if(callback){
					callback();
				}
				// Handle memory leak in IE
				script.onload = script.onreadystatechange = null;				
				if ( head && script.parentNode ) {
					head.removeChild( script );
				}
			}
		}
		head.insertBefore( script, head.firstChild );  	
  },
	createTimestamp : function(){
		var now = new Date();
		var h = now.getHours();
		var m = now.getMinutes();
		var s = now.getSeconds();
		if(h<=9){h="0"+h}
		if(m<=9){m="0"+m}
		if(s<=9){s="0"+s}
		return h+""+m+""+s;
  },
  /**
	 * 格式化行情时间
	 * 
	 * @param {string}
	 *            time 行情时间字符串，年月日时分秒，长度为14位，20100208091050
	 * @return {array} ["2010-02-08 09:10:50",Date]
	 */
  fomatHqTime : function(time){
  	if(!time||time.length!=14){
  		return ;	
  	}
		var dtime = time;
		var date = new Date();
		var regex = /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/;
		if(regex.test(time)) {
			date.setYear(RegExp.$1);
			date.setMonth(parseInt(RegExp.$2,10) - 1);
			date.setDate(RegExp.$3);
			date.setHours(RegExp.$4);
			date.setMinutes(RegExp.$5);
			date.setSeconds(RegExp.$6);
			dtime=RegExp.$1+"-"+RegExp.$2+"-"+RegExp.$3+" "+RegExp.$4+":"+RegExp.$5+":"+RegExp.$6;
		}
		return [dtime,date];
  }
};
(function($){
	var HqTiming = function(oUrl , config){
		this.oUrl = oUrl ;
		this.config = {
			hqName : null,  // 请求中n参数值
			mkType : "cn" , // 市场类型，默认为cn，可选hk
			loop : true ,   // 是否轮询
			isJudgeMkStatus : true,  // 是否根据开盘状态加载行情
			immediate : true,  // 第一次加载行情
			flushTime : 10000,  // 行情刷新时间
			charset:null
		}
		$.extend(this.config,config);
		this.loader = HqLoader;
	};
	HqTiming.prototype = {
		// 添加callback
		on : function(callback , scope){
			this.callback = {fn: callback , scope: scope||null};
			return this;
		},
		_onBeforeLoad : function(){
			// 判断是否加载
			var cfg = this.config;
			if( !cfg.isJudgeMkStatus ){
				return true;
			}
			if( this.loader.getMKStatDone ){
				if(cfg.mkType == "cn"){				
					if(this.loader.isCnMkDay && this.loader.cnMkStatus){
						return true;
					}else{
						return false;
					}
				}else if(cfg.mkType == "hk"){				
					if(this.loader.isHkMkDay && this.loader.hkMkStatus){
						return true;
					}else{
						return false;
					}
				}
			}else{
				return true;
			}
		},
		start : function(){
			// 第一次始终加载
			var self = this;
			var cfg = self.config;
			function fn(){
				if(self._onBeforeLoad()){
					self._load();
				}
			}
			if(cfg.loop){
				self.timer = setInterval(fn , cfg.flushTime);
				if(cfg.immediate){// 启动时立即加载
					self._load();
				}
			}else{
				self._load();
			}
			
			return this;
		},
		stop : function(){
			if(this.timer){
				clearInterval(this.timer);
			}
			this.timer = null;
		},
		_load : function(){
			var self = this;
			var cfg = self.config;
			var url = self.oUrl;
			if(self.oUrl.indexOf("?")==-1)
				url +="?";
			else 
				url += "&";
			url += "_="+HqLoader.createTimestamp();
			HqLoader.load(url,function(){

				var cb = self.callback;
				cb.fn.call(cb.scope,self.config.hqName);
	
			},cfg.charset);
		}
	};
	window.HqTiming = HqTiming;
})(jQuery);