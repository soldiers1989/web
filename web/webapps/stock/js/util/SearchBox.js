/**
 * @fileOverview 新版股票基金查询框
 * @copyright (c) 2009 (jrj.com)
 * @author jianjun.wang@jrj.com.cn
 * @version 2.0
 * @change 2010.4.15 增加股指期货类型
 */

if (typeof JRJ == "undefined" || !JRJ) {
    var JRJ = {};
}
if (typeof JRJ.ui == "undefined" || !JRJ.ui) {
    JRJ.ui = {};
}
(function () {
    var defaultStyles = ".jrj_sb{position:absolute;margin:0;padding:0;border:1px solid #d6d6d6;background-color:#fff;font-size:12px;z-index:99999;overflow:hidden; width: 340px; }";// min-width: 340px; width:auto !important;
    defaultStyles += ".jrj_sb *{margin:0;padding:0;}";
    defaultStyles += ".jrj_sb_mask{display:block;position:absolute;z-index:-1;filter:Alpha(Opacity=\'0\');}";
    //defaultStyles += "   top:expression(((parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1)+\'px\');";
    //defaultStyles += "   left:expression(((parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1)+\'px\');";
    //defaultStyles += "   width:expression(this.parentNode.offsetWidth+\'px\');";
    //defaultStyles += "   height:expression(this.parentNode.offsetHeight+\'px\');}";
    defaultStyles += ".jrj_sb tr{cursor:pointer;white-space:nowrap;}";
    defaultStyles += ".jrj_sb tr.jrj_sb_on{background:#f8f8f8;}";
    defaultStyles += ".jrj_sb td{padding:0 10px 0 8px;height:35px;line-height:35px; white-space:nowrap;}";
    defaultStyles += ".jrj_sb td .market-item{display:inline-block;height:20px;line-height:20px;background:#d7d7d7;text-align:center;color:#fff;width:43px;}";
    defaultStyles += ".jrj_sb tr b{font-weight:bold;color:#000;}";
    defaultStyles += ".jrj_sb .sb_error{color:#ff0000;padding:3px 10px;line-height:12px}";
    defaultStyles += ".jrj_sb .no-result{height:30px;line-height:30px;font-size:14px;padding:0 20px;color:#999;text-align:center;}";
    defaultStyles += ".jrj_sb .sb_muti_prompt{border-top:1px solid #cee0f1;margin-top:5px;padding:8px 12px 4px;background-color:#f0f8ff;}";
    defaultStyles += ".jrj_sb .sb_muti_prompt .sb_muti_line1{color:#ff0000;line-height:12px}";
    defaultStyles += ".jrj_sb .sb_muti_prompt .sb_muti_line2{padding-top:3px;color:#c1c0c0;line-height:12px}";
    defaultStyles += ".jrj_sb table{margin:0;color:#666;border-collapse:collapse;border-spacing:0;line-height:21px;background-color:#fff;z-index:100;width:100%;}";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li { float:left;height:36px; }";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li a{ display:inline-block;width:340px;height:36px; }";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li a:hover{ background:#f8f8f8; }";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li a span{ display:inline-block;float:left;height:30px;line-height:30px;padding:3px 0;font-size:14px; }";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li a span.col-1{ width:60px; padding-left: 10px;}";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li a span.col-2{ width:100px; }";
    defaultStyles += ".jrj_sb ul.jrj_sb_tougu_list li a span.col-3{ width:160px;padding-right:10px; }";
    defaultStyles += ".jrj_sb img{margin-right:2px;}";
    defaultStyles += ".jrj_sb .jrj_sb_title{font-size:14px;margin:0 10px 0 0;background:url(/stock/images/line-491.png) 0 50% repeat-x;height:30px;line-height:30px;}";
    defaultStyles += ".jrj_sb .jrj_sb_title span{ display:inline-block;height:30px;padding:0 10px; background:#fff;}";
    /**
	 * 股票基金查询框
	 * @link <a target="_blank" href="../../src-new/ui/SearchBox/example.html">例子</a>
	 * @class 股票基金查询框
	 * @param {string} inputId input框id
	 * @param {string} btnId 查询按钮id
	 * @param {function} func 查询结果的处理函数，可以缺省
	 * @param {string} param 查询参数，可以缺省
	 * @param {string} patterns 自定义的匹配对象，可以缺省
	 * @param {string} styles 自定义的样式字符串，可以缺省
	 */
    JRJ.ui.SearchBox = function (inputId, btnId, func, param, patterns, styles, _config) {
        this.textInput = Tools.$(inputId);
        if (!this.textInput) {
            return;
        }
        this.button = btnId ? Tools.$(btnId) : null;
        /**
		 * @public
		 * @field 查询结果的处理函数
		 * @name func
		 * @type function
		 */
        this.func = func;
        /**
		 * 查询参数，创建对象后也可修改
		 * @type string
		 */
        this.param = param ? param : "&item=10";
        /**
		 * 市场类型和url匹配对象数组
		 * @type function
		 */
        this.patterns = patterns;
        this.defaultPatterns = [
			new JRJ.ui.Pattern("cn.i", "http://stock.jrj.com.cn/share,[stid].shtml"),//个股指数			
			new JRJ.ui.Pattern("cn.s", "http://stock.jrj.com.cn/share,[code].shtml"),//个股股票
			new JRJ.ui.Pattern("cn.f", "http://fund.jrj.com.cn/archives/[code]/index.shtml"),//基金
			new JRJ.ui.Pattern("cn.w", "http://warrant.jrj.com.cn/StockInfo.aspx?gpdm=[code]"),//权证
			new JRJ.ui.Pattern("cn.qh.if", "http://gzqh.jrj.com.cn/quote/[code]"),//股指期货 
			new JRJ.ui.Pattern("hk", "http://hk.jrj.com.cn/bmp/[code]/"),//港股
			new JRJ.ui.Pattern("us.s", "http://usstock.jrj.com.cn/share/search.shtml?stock=[code]"),//美股
			new JRJ.ui.Pattern("cn.bk.bk1", "http://summary.jrj.com.cn/zb/Agu_[code].shtml"),//证监会板块
			new JRJ.ui.Pattern("cn.bk.bk2", "http://summary.jrj.com.cn/gb/Agu_[code].shtml"),//全球行业
			new JRJ.ui.Pattern("cn.bk.bk3", "http://summary.jrj.com.cn/rb/Agu_[code].shtml"),//地域板块
			new JRJ.ui.Pattern("cn.bk.bk5", "http://summary.jrj.com.cn/cb/Agu_[code].shtml"),//概念板块
			new JRJ.ui.Pattern("cn.bk.bk7", "http://summary.jrj.com.cn/ib/Agu_[code].shtml")//新财富行业
        ];
        /**
		 * 查询数据源
		 * @type string
		 */
        this.url = "/stock/code?1=1";  //url
        
        this.otherUrl="/stock/trade/stocks.jspa";//其他交易股票接口
        this.otherSearch=null;//其他交易股票查询接口
        
        this.query = ""; //查询字符串

        this.selectedItem = null; //当前选中列表项，为tr
        this.slist = new Array(); //返回的结果列表
        /**
		 * 已选中项，一个代码查询只取this.clist[0]<br/>
		 * 数据格式：[{stid:"sz200002",code:"200002",name:"万科B",shrt:"WKB",mrkt:"cn.sz",type:"s.sb",stat:"1"},{},{}]
		 * @type Array 
		 */
        this.clist = new Array();

        //请求相关 
        this.charset = null;
        this._dataCache = {};
        this._timer = null;
        this._isRunning = false;
        this.timerDelay = "50"; //查询延时
        this._latestScriptTime = "";  //最新script的时间戳，用于判断请求是否过时

        //配置项
        /**
             * 是否多选，默认为false
             * @type boolean
             */
        this.muti = false;
        this.adjustPos = false;
        this.topshow = false;  //在输入框上方显示弹出列表
        this.offsetLeft = 0; //left定位的偏移量
        this.offsetTop = 0;  //top定位的偏移量    
        /**
             * 弹出列表中的股票倒序排列(从下到上)，默认为false
             * @type boolean
             */
        this.reverse = false;
        /**
		 * 显示市场类型，默认为true
		 * @type boolean
		 */
        this.showMarket = true;
        /**
		 * 初始化后自动定焦	，默认为false
		 * @type boolean
		 */
        this.autoFocus = false;
        /**
		 * 查询后情况input，默认为true
		 * @type boolean
		 */
        this.clearInput = true;
        /**
		 * 设置输入框的有效性，true：有效，false：无效，false自动查询失效，默认为true
		 * @type boolean
		 */
        this.iptEffective = true;
        /**
		 * 设置查询按钮的有效性，true：有效，false：无效，点btn不进行结果处理，默认为true
		 * @type boolean
		 */
        this.btnEffective = true;
        /**
		 * 设置回车有效性，false：不执行股票查询框中的回车操作，默认为true
		 * @type boolean
		 */
        this.enterEffective = true;  //
        /**
		 * 设置鼠标点击列表项有效性，false：不执行点击查询股票操作，默认为true
		 * @type boolean
		 */
        this.clickEffective = true;

        //打开窗口处理
        /**
		 * 强制打开新窗口所用的form id
		 * @type string
		 */
        this.forceForm = null;
        this.submitForm = false;

        //提示
        this.input_prompt = this.textInput.value ? this.textInput.value : String.fromCharCode(20195, 30721, 47, 31616, 31216, 47, 25340, 38899);//代码/简称/拼音		
        this.no_reslut_prompt = String.fromCharCode(27809, 26377, 31526, 21512, 26465, 20214, 30340, 32467, 26524);//没有符合条件的结果
        this.muti_choice_prompt = String.fromCharCode(35831, 20351, 29992, 36887, 21495, 8220, 44, 8221, 20998, 38548, 22810, 21482, 32929, 31080);//请使用逗号',' 分隔多只股票
        this.error_prompt = String.fromCharCode(35831, 36755, 20837, 27491, 30830, 30340, 20195, 30721, 25110, 31616, 31216);//请输入正确的代码或简称
        /**
		 * 回调函数执行时clist为空是否返回，某些特殊情况下使用
		 * @type boolean
		 */
        this.errorReturn = true;
        this.styles = styles ? styles : defaultStyles;
        /**
		 * input获得焦点后的class
		 * @type string
		 */
        this.inputFocusClass = null;
        /**
		 * input blur事件处理函数
		 * @type string
		 */
        this.blurFunc = null;
        /*
         * 搜索投顾
         */
        this.titleTmpl = '<div class="jrj_sb_title"><span>{{{title}}}</span></div>',
        this.stockRowTmpl = '<tr><td style="display:none;">{{{col0}}}</td><td><span class="market-item">{{{col1}}}</span></td><td>{{{col2}}}</td><td>{{{col3}}}</td><td>{{{col4}}}</td></tr>',
        this.stockRowTmpl_US = '<tr><td style="display:none;">{{{col0}}}</td><td><span class="market-item">{{{{col1}}}</span></td><td colspan="2">{{{col2}}}</td><td>{{{col4}}}</td></tr>';

        this.config = $.extend({
            hasOther: false,
            clearInput:true,
            size:10
        }, _config);

        this.clearInput = this.config.clearInput;
        this.hasOther=this.config.hasOther;
        this.size=this.config.size;
        this._init();
    }

    JRJ.ui.SearchBox.prototype = {
        /**
		 * @inner
		 */
        _init: function () {
            this._initInput();
            this._initContainer();
            var instance = this;
            if (this.button) {
                Event.on(this.button, "mouseup", function (event) {
                    if (!instance.btnEffective) {
                        return;
                    }
                    instance._searchHandler(event);
                });
            }
            //window event			

            Event.on(window, "load", function () {
                //ie刷页面时input不恢复成默认值，需要强设下
                //在body.onload里设没效果
                instance.textInput.value = instance.input_prompt;
            });
            //添加样式，不重复添加
            Tools.addStyle("jrj_sb_style", this.styles);
            
            if(this.hasOther){
            	this._initOther();
            }
        },
        _initInput: function () {
            var instance = this;
            this.textInput.value = this.input_prompt;
            this.textInput.setAttribute("autocomplete", "off");
            Event.on(this.textInput, "focus", function () {
                if (!instance.iptEffective) {
                    return;
                }
                if (Tools.trim(instance.textInput.value) == instance.input_prompt) {
                    instance.textInput.value = "";
                }
                //自定义input焦点样式
                if (instance.inputFocusClass) {
                    Tools.addClass(instance.textInput, instance.inputFocusClass);
                }
                instance.start();
            });

            Event.on(this.textInput, "blur", function () {
                if (!instance.iptEffective) {
                    return;
                }
                if (Tools.trim(instance.textInput.value) == "") {
                    instance.textInput.value = instance.input_prompt;
                }
                //删除焦点样式
                if (instance.inputFocusClass) {
                    Tools.removeClass(instance.textInput, instance.inputFocusClass);
                }

                instance.stop();
                instance.hide();
                if (instance.blurFunc) {
                    instance.blurFunc();
                }
            });

            var pressingCount = 0;
            Event.on(this.textInput, "keydown", function (event) {
                if (!instance.iptEffective) {
                    return;
                }
                var keyCode = event.keyCode;

                switch (keyCode) {
                    case 27:
                        instance.hide();
                        break;
                    case 13:
                        instance._enterHandler(event);
                        break;
                    case 40://Down键
                    case 38://up键
                        //选择列表项，按住不动，延时处理
                        if (pressingCount++ == 0) {
                            if (instance._isRunning) instance.stop();
                            instance.selectItem(keyCode == 40);
                        } else if (pressingCount == 3) {
                            pressingCount = 0;
                        }
                        break;
                }
                if (keyCode != 40 && keyCode != 38) {
                    if (!instance._isRunning) {
                        // 1. 当网速较慢，js还未下载完时，用户可能就已经开始输入
                        //    这时，focus事件已经不会触发，需要在keydown里触发定时器
                        // 2. 非DOWN/UP键时，需要激活定时器
                        instance.start();
                    }
                }
            });

            Event.on(this.textInput, "keyup", function (event) {
                if (!instance.iptEffective) {
                    return;
                }
                //取消keydown延迟
                pressingCount = 0;
                //根据输入框中的数据，整理已选代码表，删除不匹配项
                if (instance.muti) {
                    if (event.keyCode > 32 && event.keyCode < 46) {//这些keyup不需要整理
                        return;
                    }
                    var inputVal = Tools.trim(instance.textInput.value);
                    var valArr = inputVal.split(",");
                    var keyVal = valArr.pop();

                    for (var i = 0; i < instance.clist.length; i++) {
                        var tmp = instance.clist[i];
                        var j;
                        for (j = 0; j < valArr.length; j++) {
                            if (tmp.code == valArr[j] || tmp.name == valArr[j] || tmp.shrt == valArr[j]) {
                                break;
                            }
                        }
                        if (j == valArr.length) {//如果没有比配，则从已选列表中删除
                            instance.clist.splice(i, 1);
                            i--;
                        }
                    }
                    //修复多选手动输入无效的bug  188 = ","
                    if (event.keyCode == 188) {
                        if (instance.slist && instance.slist.length > 0) {
                            var v = instance.textInput.value;
                            instance.textInput.value = v.substring(0, v.length - 1);
                            instance.chooseOne(instance.container.getElementsByTagName("tr")[0], true);
                        }
                    }
                }
            });

            if (this.autoFocus) {
                this.textInput.focus();
            }
        },
        _enterHandler: function (event) {
            var items = this.container.getElementsByTagName("tr");
            this.updateInputAndClist(items[this.selectedIndex], true);
            if (!this.enterEffective) {
                this.hide();
                return;
            }
            //单只查询 
            if (!this.muti) {
                //提交查询或执行自定处理方法
                this._searchHandler(event);
            } else {
                this.container.innerHTML = "";
                return;
            }
        },
        _searchHandler: function (event) {
            if (!this.iptEffective) {//input不可用，不执行查询
                return;
            }
            //用户没有选择，把列表选中项作为默认查询代码
            if (this.clist.length == 0 && this.slist.length != 0) {
                this.clist[0] = this.slist[this.selectedIndex];
            }
            try{
            	$(this.textInput).blur(); // 这一句还可以阻止掉浏览器的默认提交事件
            }catch(e){}
            if (this.clist.length == 0 && this.errorReturn) {
                this.textInput.focus();
                var content = '<p class="sb_error"><img src="/stock/images/sb-alert.gif"/>' + this.error_prompt + '</p>';
                this._fillContainer(content);
                this._displayContainer();

                return false;
            }
            //设置cookie
            var ts = Tools.formatDate(new Date());
            var exp = new Date();
            exp.setTime(exp.getTime() + 30 * 1000);
            Tools.setCookie("JRJ_QUERY_COOKIE", ts + "," + this.textInput.id + "," + encodeURI(window.location), exp, "/", "jrj.com.cn");
            if (!this.muti) {
                if (typeof (this.func) == "function") {
                    this.func(this.clist, event);
                } else {
                    this._defaultSearch(this.clist[0], event);
                }
            } else {
                if (typeof (this.func) == "function") {
                    this.func(this.clist, event);
                }
                else {
                    //循环调用默认处理函数，打开多个窗口
                    for (var i = 0; i < this.clist.length; i++) {
                        this._defaultSearch(this.clist[i], event);
                    }
                }
            }

            this.reset(this.clearInput);
        },
        _defaultSearch: function (obj, event) {
            var long_kw = "." + obj.mrkt + "." + obj.type + "." + obj.stat + ".";
            var url = "";
            if (this.patterns) {
                url = this.matchPattern(obj, this.patterns);
            }
            if (url == "" && this.defaultPatterns) {
                url = this.matchPattern(obj, this.defaultPatterns);
            }
            if (url != "") {
                this.openWin(url, event);
            }
            return false;
        },
        _initContainer: function () {
            var container = Tools.createElement("div");
            container.className = "jrj_sb";
            container.style.visibility = "hidden";

            this.container = container;
            this._initContainerEvent();
            document.body.insertBefore(this.container, document.body.firstChild);
        },
        _setContainerPos: function () {
            try {
                var pos = Tools.getOffsetPosition(this.textInput);
                if (!this.topshow) {
                    Tools.setElementStyles(this.container, { "left": pos.left + this.offsetLeft + "px", "top": pos.top + this.textInput.offsetHeight + this.offsetTop + "px" });
                } else {
                    //Tools.setElementStyles(this.container,{"left":pos.left+this.offsetLeft+"px","top":pos.top-this.container.offsetHeight+this.offsetTop+"px"});
                    //修改muti时topshow定位问题
                    var doc = document, docEle = doc.documentElement;
                    var dh = docEle && docEle.clientHeight || doc.body.clientHeight;
                    var scrollTop = docEle && docEle.scrollTop || doc.body.scrollTop;
                    //$("#debug").html($("#debug").html()+"pos.top::"+ pos.top+"<br/>")
                    //$("#debug").html($("#debug").html()+"this.offsetTop::"+ this.offsetTop+"<br/>")
                    //$("#debug").html($("#debug").html()+"bottom::"+ (dh-pos.top-this.offsetTop)+"<br/>")
                    var b = dh - pos.top - this.offsetTop;
                    //Tools.$("debug").innerHTML = Tools.$("debug").innerHTML+"-----::"+ b+"<br/>"
                    b = Tools.ie6 ? b + scrollTop : b;
                    //Tools.$("debug").innerHTML = Tools.$("debug").innerHTML+"+++++::"+ b+"<br/>"
                    Tools.setElementStyles(this.container, { "left": pos.left + this.offsetLeft + "px", "bottom": b + "px" });
                    //Tools.$("debug").innerHTML = Tools.$("debug").innerHTML+this.container.style.bottom +"<br/>"
                }
            } catch (e) {

            }
        },
        _initContainerEvent: function () {
            var instance = this;
            /**
             * @ignore
             */
            function getTR(ele) {
                var tr = null;
                if (ele.tagName.toLowerCase() == "td") {
                    tr = ele.parentNode;
                }
                if (ele.tagName.toLowerCase() == "tr") {
                    tr = ele;
                }
                return tr;
            }
            Event.on(this.container, "mouseover", function (event) {
                var target = Event.getTarget(event);
                target = getTR(target);

                if (target && target != instance.selectedItem) {
                    instance._removeSelectedItem();
                    instance._setSelectedItem(target);
                }

            });

            var mouseDownItem = null;
            Event.on(this.container, "mousedown", function (event) {
                var target = Event.getTarget(event);
                mouseDownItem = getTR(target);
                //鼠标按下时输入框保持焦点
                // 1. for ie onbeforedeactive返回false，则不会激发目标元素的事件
                /** 
                     * @inner
                     */
                instance.textInput.onbeforedeactivate = function (event) {
                    window.event.returnValue = false;
                    instance.textInput.onbeforedeactivate = null;
                }
                // 2. for W3C
                Event.stop(event);
                return false;
            });
            Event.on(this.container, "mouseup", function (event) {
                var exy = Event.pageXY(event);
                var pos = Tools.getOffsetPosition($(instance.container).find('#jrj_sb_stock_block')[0]);
                var l = pos.left;
                var t = pos.top;
                var r = pos.left + $(instance.container).find('#jrj_sb_stock_block')[0].offsetWidth;
                var b = pos.top + $(instance.container).find('#jrj_sb_stock_block')[0].offsetHeight;
                //当mousedown在提示层内，up在提示层外，为无效点击
                if (!(exy.left >= l && exy.left <= r && exy.top >= t && exy.top <= b)) {
                    return;
                }
                var target = Event.getTarget(event);
                target = getTR(target);
                if (target != mouseDownItem) { return; }
                //更新input value
                //instance.updateInputAndClist(target,true);
                //单只查询，调用处理函数前 先隐藏提示层，停止计时器
                if (!instance.muti) {
                    //提交查询或执行自定处理方法

                    if (!instance.clickEffective) {
                        instance.hide();
                        return;
                    }
                    instance._searchHandler(event);
                }
            });
        },
        /**
           * @ignore
           */
        start: function () {
            JRJ.ui.SearchBox.focusInstance = this;

            var instance = this;
            this._timer = setTimeout(function () {
                instance.updateData();
                instance._timer = setTimeout(arguments.callee, instance.timerDelay);
            }, instance.timerDelay);

            this._isRunning = true;
        },
        /**
           * @ignore
           */
        stop: function () {
            JRJ.ui.SearchBox.focusInstance = null;
            if (this._timer) {
                clearTimeout(this._timer);
                this._timer = null;
            }
            this._isRunning = false;
        },
        /**
           * @ignore
           */
        updateInputAndClist: function (item, compart) {
            if (!item) { return; }
            if (this.muti) {
                //多选时compart:true 加逗号， false不加逗号	
                this.chooseOne(item, compart);
            } else {
                this.textInput.value = item.firstChild.innerHTML;
                this._setClist(this.slist[item.rowIndex]);
            }
            this.query = item.firstChild.innerHTML;
        },
        /**
           * @ignore
           */
        getInputQuery: function () {
            var query = "";
            //var val = this.textInput.value;
            var inputVal = Tools.toDBC(this.textInput.value);
			inputVal = inputVal.split(' ')[0];
            if (!this.muti) {
                query = inputVal;
            } else {
                query = inputVal.substr(inputVal.lastIndexOf(",") + 1);
            }
            return query;
        },
        /**
           * @ignore
           * 选中一项，点击 enter选择和down up选择不一样，确定选择最后要加"," 
           */
        chooseOne: function (item, compart) {
            var obj = this.slist[item.rowIndex];
            var inputValue = this.textInput.value;
            var keywords = inputValue.split(",");
            if (keywords.length > 0) {
                keywords.length--;
            }
            //排重
            var keyword = item.cells[0].innerHTML;
            if (compart) {
                var i;
                for (i = 0; i < this.clist.length; i++) {
                    var obj2 = this.clist[i];
                    if (obj.code == obj2.code && obj.name == obj2.name && obj.shrt == obj2.shrt) {
                        break;
                    }
                }
                if (i == this.clist.length) {
                    this._setClist(obj);
                    keywords.push(keyword);
                }
                this.textInput.value = keywords.join(",") + ",";
                this._setSelectedItem(null);
                this.query = "";
            } else {
                keywords.push(keyword);
                this.textInput.value = keywords.join(",");
            }
        },
        /**
           * @ignore
           * up down键盘选择
           */
        selectItem: function (down) {
            if (this.muti && this.getInputQuery() == "") {
                return;
            }

            var items = this.container.getElementsByTagName("tr");
            if (items.length == 0) {
                return;
            }
            if (this.container.style.visibility == "hidden") {
                this.show();
                return;
            }

            var newIndex = null;
            var len = items.length;
            var index = this.selectedIndex;
            if (down) {
                newIndex = (index == len - 1 ? 0 : index + 1);
            } else {
                newIndex = (index == 0 ? len - 1 : index - 1);
            }

            this._removeSelectedItem();
            var newSelectedItem = items[newIndex];
            if (newSelectedItem) {
                this._setSelectedItem(newSelectedItem);
                this.updateInputAndClist(newSelectedItem, false);
            }
        },
        _removeSelectedItem: function () {
            Tools.removeClass(this.selectedItem, "jrj_sb_on");
            this.selectedItem = null;
            this.selectedIndex = 0;
        },
        _setSelectedItem: function (item) {
            if (item) {
                Tools.addClass(item, "jrj_sb_on");
                this.selectedItem = item;
                this.selectedIndex = item.rowIndex;
            } else {
                this.selectedIndex = 0;
                this.selectedItem = null;
            }
        },
        _setClist: function (obj) {
            if (!this.muti) {
                this.clist[0] = obj;
            } else {
                this.clist.push(obj);
            }
        },
        /**
             * @ignore
             */
        show: function () {
            var instance = this;
            this.resizeHandler = Event.on(window, "resize", function () {
                instance._setContainerPos();
            });
            this.scrollHandler = Event.on(window, "scroll", function () {
                instance._setContainerPos();
            });
            instance.container.style.visibility = "";
        },
        /**
           * @ignore
           */
        hide: function () {
            this.container.style.visibility = "hidden";
            if (this.resizeHandler && this.scrollHandler) {
                Event.remove(this.resizeHandler);
                Event.remove(this.scrollHandler);
            }
        },
        /**
           * @ignore
           */
        updateData: function () {
            if (!this.iptEffective || !this.needUpdate()) {
                return;
            }
            //this.textInput.value = Tools.toDBC(this.textInput.value);
            this.query = this.getInputQuery();

            var q = Tools.trim(this.query);
            //去除空格后长度为0，不显示	
            if (q.length == 0) {
                this._fillContainer("");
                this.hide();
                return;
            }
            if (typeof this._dataCache[this.query] != "undefined") {
                this._fillContainer(this._dataCache[this.query].html);
                this.slist = this._dataCache[this.query].slist;
                this._setSelectedItem(this.container.getElementsByTagName("tr")[0]);
                this._createMutiPrompt();
                this._displayContainer();
            } else {
                this.requestData();
            }
        },
        /**
           * @ignore
           * 判断是否需要更新list
           */
        needUpdate: function () {
            var inputQuery = this.getInputQuery();
            //length>0且inputQuery为空说明是多选
            if (inputQuery == "" && this.textInput.value.length > 0) {
                this.hide();
                return false;
            }
            //如果用户选择过某项 ，后点空白出隐藏查询列表，再用键盘选择时，直接显示 	
            if (inputQuery != this.query) return true;
            return false;
        },
        /**
           * @ignore
           */
        requestData: function () {
            var script = document.createElement('script');
            script.setAttribute('type', 'text/javascript');

            if (this.charset) {
                script.setAttribute('charset', this.charset);
            }
            //encodeURIComponent
            var oSrc = this.url + this.param + "&key=" + this.query + "&d=" + Tools.createTimeStamp();
            script.setAttribute('src', oSrc);
            var t = new Date().getTime();
            this._latestScriptTime = t;
            script.setAttribute("time", t);
            var head = document.getElementsByTagName("head")[0] || document.documentElement;

            /** 
             * @ignore
             */
            script.onload = script.onreadystatechange = function () {
                if (!this.readyState || this.readyState === "loaded" || this.readyState === "complete") {
                    var obj = JRJ.ui.SearchBox.focusInstance;
                    if (!obj) return;
                    var scriptDataIsOut = script.getAttribute("time") != obj._latestScriptTime;
                    if (!scriptDataIsOut) {// 抛弃过期数据，否则会导致bug：1. 缓存key值不对； 2. 过期数据导致的闪屏
                        obj.handleResponse(SCodeData);
                    }
                    // Handle memory leak in IE
                    script.onload = script.onreadystatechange = null;
                    if (head && script.parentNode) {
                        head.removeChild(script);
                    }
                }
            }
            head.insertBefore(script, head.firstChild);
        },
        /**
             * @ignore
             */
        handleResponse: function (data) {
            if (!JRJ.ui.SearchBox.focusInstance) return;
            if (!data) {
                return;
            }
            try {//把返回的结果保存在s.slist中
                this.slist = data.CodeData;
                if (this.reverse) {//倒序				
                    this.slist.reverse();
                }
            } catch (e) {
                this.slist = new Array();
            }
            
            if(this.hasOther){
            	var others=this.fetchOther();
            	if(others){
            	this.slist=this.slist.concat(others);
            	 if (this.reverse) {//倒序				
                     this.slist.reverse();
                 }
            	}
            }
            
            
            var list = this.slist;

            //填充数据
            var content = "";
                

            content = $('<div></div>');
            content.append($(Mustache.render(this.titleTmpl, { title: '品种' })));
            content.append($('<div id="jrj_sb_stock_block"></div>'));


            if (list.length == 0) {
                content.find('#jrj_sb_stock_block').append('<div class="no-result">' + this.no_reslut_prompt + '</div>');
                //content = '<p class="sb_error"><img src="/stock/images/sb-alert.gif"/>' + this.no_reslut_prompt + '</p>';
                this.selectedItem = null;
                this.selectedIndex = 0;
                this._fillContainer(content[0]);
            } else {
                content.find('#jrj_sb_stock_block').append($('<table></table>'));
                this.selectedIndex = 0;
                var q = this.query;
                for (var i = 0; i < list.length; i++) {
                    var item = list[i],
                        obj = { col0: '', col1: '', col2: '', col3: '', col4: '' },
                        keyItem = '';

                    if (Tools.contains(item.shrt, q)) {
                        keyItem = item.shrt;
                    } else if (Tools.contains(item.code, q)) {
                        keyItem = item.code;
                    } else if (Tools.contains(item.name, q)) {
                        keyItem = item.name;
                    }

                    obj.col0 = keyItem;
                    obj.col1 = Tools.getTag(item.mrkt, item.type);
                    obj.col2 = Tools.bold(item.name, q);
                    obj.col3 = Tools.bold(item.code, q);
                    obj.col4 = Tools.bold(item.shrt, q);

                    if (Tools.isUS(item.mrkt)) {
                        content.find('#jrj_sb_stock_block').find('table').append($(Mustache.render(this.stockRowTmpl_US, obj)));
                    } else {
                        content.find('#jrj_sb_stock_block').find('table').append($(Mustache.render(this.stockRowTmpl, obj)));
                    }
                    if(i>=this.size-1)break;
                }

                this._setSelectedItem(content.find('table tr:first')[0]);
                //this._setClist(this.slist[this.selectedIndex]);
                this._fillContainer(content[0]);
            }

            //如果缓存dom对象，IE下填充容器为空，innerHTML和slist都需要缓存
            this._dataCache[this.query] = { html: this.container.innerHTML, slist: list };
            this._createMutiPrompt();
            this._displayContainer();
        },
        _fillContainer: function (content) {
            if (content.nodeType == 1) {
                this.container.innerHTML = "";
                this.container.appendChild(content);
            } else {
                this.container.innerHTML = content;
            }

            if (Tools.ie6) {
                this.container.insertBefore(Tools.createIframeMask(), this.container.firstChild);
            }
            //每次填充时重新定位，如果只在下方显示可以在_initContainer中初始化
            this._setContainerPos();
        },
        _displayContainer: function () {
            if (Tools.trim(this.container.innerHTML)) {
                this.show();
            } else {
                this.hide();
            }
        },
        _createMutiPrompt: function () {
            if (this.slist.length > 0 && this.muti) {
                var prompt = Tools.createElement("div");
                Tools.addClass(prompt, "sb_muti_prompt");
                prompt.innerHTML = "<p class='sb_muti_line1'><img src='/stock/images/sb-alert.gif'/>[" + this.muti_choice_prompt + "]</p><p class='sb_muti_line2'><img src='/stock/images/sb-dot.gif'/>" + String.fromCharCode(20363, 22914, 65306) + "601858,600123,000001</p>";
                this.container.appendChild(prompt);
            }
        },
        _otherLoaded:false,
        _initOther:function(){
        	if(this._otherLoaded)return;
        	this._otherLoaded=true;
        	var instance=this;
        	$.getScript(this.otherUrl,
        		  	function(data){
        			_otherLoading=false;
        				if(JRJ.Trade.STOCKS){
        					instance.otherSearch=new JRJ.ui.SearchText(JRJ.Trade.STOCKS.CodeData, {
        						matchContains: true,
        						formatItem: function(row, i, max, term) {
        							return row.name+row.code+row.shrt;
        						},
        						formatResult: function(row) {
        							return row;
        						}
        					})
        				}
        		  	}
        		);
        },
        fetchOther: function(){
        	var result=new Array();
        	if(this.otherSearch!=null){
        		var data= this.otherSearch.load(this.query);
        		if( data && data.length ) {
    				for (var i=0; i < data.length; i++) {
    					if( data[i].result ) {
    						result.push(data[i].result);
    					}
    				}
    			}
        	}
        	return result;
        },
        /**
             * @ignore
             */
        reset: function (clearInput) {
            this.selectedItem = null;
            this.selectedIndex = 0;
            if (clearInput) {
                this.query = "";
                this.textInput.value = "";
            }
            this.slist = new Array();
            this.clist = new Array();
        },
        /**
		 * reset查询框，清除查询缓存，切换查询类型时应调用以便得到正确的数据
		 */
        clear: function () {
            this.reset(true);
            this.textInput.value = this.input_prompt;
            this._dataCache = {};
        },
        /**
		 * 删除查询框容器DOM，特殊情况使用。某一操作频繁创建查询框时清除不使用的div
		 */
        remove: function () {
            if (this.container && this.container.parentNode) {
                this.container.parentNode.removeChild(this.container);
            }
        },
        /**
		 * @ignore
		 */
        openWin: function (url, event) {
            //ie下window.open，无法带referer，所以都用submit
            //if(event.type=="mouseup"){				
            //	window.open(url);
            //}else{			
            try {
                this.submitForm = true;
                if (this.forceForm != null) {
                    var fw = new JRJ.ui.ForceWin(this.forceForm);
                    if (event.type == "mouseup") {	//			
                        fw.open2(url);
                    } else {
                        if (Tools.isWebkit) { //chrome safari使用submit提交不了
                            fw.open2(url);
                        } else {
                            fw.open(url);
                        }
                    }
                } else {
                    window.open(url);
                }
            } catch (e) {
                window.open(url);
            }
            //}
        },
        /**
		 * @ignore
		 * 使用checkSubmit是保证 enterEffective == false时，不提交表单
		 */
        checkSubmit: function () {
            try {
                if (this.submitForm) {
                    return true;
                } else {
                    return false;
                }
            } catch (e) {
                return false;
            } finally {
                this.submitForm = false;
            }
        },
        /**
		 * @ignore
		 */
        matchPattern: function (obj, patterns) {
            var url = "";
            for (var i = 0; i < patterns.length; i++) {
                if (typeof (patterns[i]) == "object") {
                    url = patterns[i].readURL(obj);
                    if (url != "") {
                        return url;
                    }
                }
            }
            return "";
        }
    }

    JRJ.ui.SearchBox.focusInstance = null;

    /**
	 * 查询类型关键字和其对应的url，在SearchBox中使用
	 * @class 查询类型关键字和其对应的url，在SearchBox中使用
	 * @param {string} kw 关键字
	 * @param {string} urlPattern 关键字对应的url
	 */
    JRJ.ui.Pattern = function (kw, urlPattern) {
        this.kw = kw;
        this.urlPattern = urlPattern;
    }
    JRJ.ui.Pattern.prototype = {
        /**
           * @ignore
           */
        readURL: function (obj) {
            if (!obj) return "";
            var longKeyWord = ".";
            for (var p in obj) {
                longKeyWord += obj[p] + ".";
            }
            var kwArray = this.kw.split(".");
            for (var i = 0; i < kwArray.length; i++) {
                var pos = Tools.trim(longKeyWord).indexOf("." + kwArray[i] + ".");
                if (pos == -1) {
                    return "";
                } else {
                    longKeyWord = longKeyWord.replace("." + kwArray[i] + ".", ".");
                }
            }
            return this.replaceURL(obj);
        },
        /**
		 * @ignore
		 */
        replaceURL: function (obj) {
            var url = this.urlPattern;
            if (obj) {
                for (var p in obj) {
                    var reg = new RegExp("\\[" + p + "\\]", "g");
                    url = url.replace(reg, obj[p]);
                }
            }
            return url;
        }
    }

    /**
	 * @ignore
	 * 强制打开窗口，用form提交打开窗口
	 */
    JRJ.ui.ForceWin = function (formId) {
        if (formId && document.getElementById(formId)) {
            this.forceForm = document.getElementById(formId);
            if (!this.forceForm.url) {
                return;
            }
            this.forceForm.target = "_blank";
            this.forceForm.method = "get";
            this.forceForm.action = "http://sq.share.jrj.com.cn/jump";
            this.btn = document.getElementById(formId + "_submitBtn");
        }
        /**
		 * @ignore
		 */
        this.open = function (sUrl) {
            this.forceForm.url.value = sUrl;
            this.btn.focus();
            //点击查询时用focus提交失败
            //focus和submit只能用一个，否则回车时ie6打开两个窗口
            //回车使用submit提交，ie还会触发查询框后的button的onclick
            //回车使用submit提交，ff抛异常
        }
        this.open2 = function (sUrl) {
            this.forceForm.url.value = sUrl;
            this.forceForm.submit();//firefox有时提交不了,why?
        }
    }

    var Event = {
        on: function (ele, evt, fcn) {
            if (Tools.isDefined(ele, "attachEvent"))
                ele.attachEvent("on" + evt, function (event) { fcn(event); });

            else if (Tools.isDefined(ele, "addEventListener"))
                ele.addEventListener(evt, fcn, false);
            return [ele, evt, fcn];
        },

        remove: function (args) {
            if (Tools.isDefined(args[0], "detachEvent"))
                args[0].detachEvent("on" + args[1], args[2]);
            else if (Tools.isDefined(args[0], "removeEventListener")) {
                args[0].removeEventListener(args[1], args[2], false);
            }
        },
        //on(ele, evt, bindAsEvent(object,method,param))
        //绑定事件注册，可传递参数
        bindAsEvent: function (object, method, param) {
            return function (event) {
                method.apply(object, [event || window.event].concat(param));
            }
        },

        //获取event对象目标元素
        getTarget: function (event) {
            if (event == null)
                return null;

            if (typeof (event.srcElement) != "undefined")
                return event.srcElement;
            else
                return event.target;
        },
        //停止事件冒泡
        stop: function (evt) {
            if (document.all) {
                evt.returnValue = false;
                evt.cancelBubble = true;
            }
            else {
                evt.preventDefault();
                evt.stopPropagation();
            }
        },
        pageXY: function (event) {
            var l = event.pageX || (event.clientX + (document.documentElement.scrollLeft || document.body.scrollLeft));
            var t = event.pageY || (event.clientY + (document.documentElement.scrollTop || document.body.scrollTop));
            return { left: l, top: t };
        }
    };
    var userAgent = navigator.userAgent.toLowerCase();
    var Tools = {
        ie: /*@cc_on!@*/false,
        ie6: navigator.appVersion.indexOf("MSIE 6.0") != -1,
        isWebkit: /applewebkit/.test(userAgent),
        $: function (id, docObj) {
            if (!docObj) {
                var doc = document;
            }
            return doc.getElementById(id);
        },
        isDefined: function (obj, field) {
            return typeof (obj[field]) != "undefined";
        },
        createElement: function (tName, docObj) {
            if (!docObj) {
                var doc = document;
            }
            return doc.createElement(tName);
        },
        createIframeMask: function () {
            var mask = Tools.createElement("iframe");
            mask.src = "about:blank";
            mask.frameBorder = "0";
            mask.className = "jrj_sb_mask";
            mask.style.setExpression("top", 'eval("(parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1") + "px"');
            mask.style.setExpression("left", 'eval("(parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1") + "px"');
            mask.style.setExpression("width", 'eval("this.parentNode.offsetWidth") + "px"');
            mask.style.setExpression("height", 'eval("this.parentNode.offsetHeight") + "px"');
            return mask;
        },
        setElementStyles: function (element, styles) {
            var style = element.style;
            for (var name in styles) {
                style[name] = styles[name];
            }
        },
        addStyle: function (id, css, doc) {
            var styleEl = this.$(id);
            if (styleEl) return; // 防止多个实例时重复添加
            var doc = doc || document;
            var style = doc.createElement("style");
            style.id = id;
            style.type = "text/css";
            var head = doc.getElementsByTagName("head")[0];
            head.insertBefore(style, head.firstChild);
            if (style.styleSheet) {
                style.styleSheet.cssText = css;
            } else {
                style.appendChild(doc.createTextNode(css));
            }
        },
        getOffsetPosition: function (elem) {
            if (!elem) return { left: 0, top: 0 };
            var top = 0, left = 0;
            if ("getBoundingClientRect" in document.documentElement) {
                //jquery方法
                var box = elem.getBoundingClientRect(),
				doc = elem.ownerDocument,
				body = doc.body,
				docElem = doc.documentElement,
				clientTop = docElem.clientTop || body.clientTop || 0,
				clientLeft = docElem.clientLeft || body.clientLeft || 0,
				top = box.top + (self.pageYOffset || docElem && docElem.scrollTop || body.scrollTop) - clientTop,
				left = box.left + (self.pageXOffset || docElem && docElem.scrollLeft || body.scrollLeft) - clientLeft;
            } else {
                do {
                    top += elem.offsetTop || 0;
                    left += elem.offsetLeft || 0;
                    elem = elem.offsetParent;
                } while (elem);
            }
            return { left: left, top: top };
        },
        trim: function (str) {
            return str.replace(/(^\s*)|(\s*$)/g, "");
        },
        contains: function (str1, str2) {
            //按小写匹配
            return str1.toLowerCase().indexOf(str2.toLowerCase()) > -1;
        },
        hasClass: function (ele, cls) {
            return ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
        },
        addClass: function (ele, cls) {
            if (!this.hasClass(ele, cls)) ele.className += " " + cls;
        },
        removeClass: function (ele, cls) {
            if (this.hasClass(ele, cls)) {
                var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
                ele.className = ele.className.replace(reg, ' ');
            }
        },
        isUS: function (mrkt) {
            return -1 != ("." + mrkt + ".").indexOf(".us.");
        },
        //加粗
        bold: function (str, key) {//如果str包含key，则加粗str匹配key的部分。
            if (this.contains(str, key)) {
                var begin = str.toLowerCase().indexOf(key.toLowerCase());
                return str.substring(0, begin) + "<b>" + str.substring(begin, key.length + begin) + "</b>" + str.substring(key.length + begin, str.length);
            } else {
                return str;
            }
        },
        //市场及类型
        getTag: function (market, type) {
            var tag = "";
            switch (market) {
                case "us.nys":
                case "us.nas":
                case "us.uss":
                case "us": {
                    tag = String.fromCharCode(32654, 32929);//美股
                    break;
                }
                case "hk": {
                    tag = String.fromCharCode(28207, 32929);//港股
                    break;
                }
                case "cn.sz": {
                    tag = String.fromCharCode(28145, 24066);//深市
                    break;
                }
                case "cn.sh": {
                    tag = String.fromCharCode(27818, 24066);//沪市
                    break;
                }
                case "cn": {
                    tag = String.fromCharCode(27818, 28145);//沪深
                    break;
                }
            }
            if (("." + type + ".").indexOf(".f.") >= 0) {
                tag = String.fromCharCode(22522, 37329);//基金
            }
            if (("." + type + ".").indexOf(".qh.") >= 0) {
                tag = String.fromCharCode(26399, 36135);//期货
            }
            return tag;
        },
        //全角转半角
        toDBC: function (str) {
            var DBCStr = "";
            for (var i = 0; i < str.length; i++) {
                var c = str.charCodeAt(i);  //指定位置处的字符的 Unicode 编码
                if (c == 12288) {
                    DBCStr += String.fromCharCode(32);  //全角空格转半角
                    continue;
                }
                if (c > 65280 && c < 65375) {
                    DBCStr += String.fromCharCode(c - 65248);
                    continue;
                }
                DBCStr += String.fromCharCode(c);
            }
            return DBCStr;
        },
        createTimeStamp: function () {
            var nowDate = new Date();
            var h = nowDate.getHours();
            var m = nowDate.getMinutes();
            var s = nowDate.getSeconds();
            if (h <= 9) { h = "0" + h }
            if (m <= 9) { m = "0" + m }
            if (s <= 9) { s = "0" + s }
            return h + "" + m + "" + s;
        },
        formatDate: function (d) {
            var str = "yyMMddhhmmss";
            var month = d.getMonth() + 1;

            str = str.replace(/yy/, (d.getYear() % 100) > 9 ? (d.getYear() % 100).toString() : '0' + (d.getYear() % 100));
            str = str.replace(/MM/, month > 9 ? month.toString() : '0' + month);
            str = str.replace(/dd/, d.getDate() > 9 ? d.getDate().toString() : '0' + d.getDate());
            str = str.replace(/hh/, d.getHours() > 9 ? d.getHours().toString() : '0' + d.getHours());
            str = str.replace(/mm/, d.getMinutes() > 9 ? d.getMinutes().toString() : '0' + d.getMinutes());
            str = str.replace(/ss/, d.getSeconds() > 9 ? d.getSeconds().toString() : '0' + d.getSeconds());

            return str;
        },
        setCookie: function (name, value, expires, path, domain, secure) {
            document.cookie = name + "=" + escape(value) +
		    ((expires) ? "; expires=" + expires.toGMTString() : "") +
		    ((path) ? "; path=" + path : "") +
		    ((domain) ? "; domain=" + domain : "") +
		    ((secure) ? "; secure" : "");
        }
    };
    window.SearchBox = JRJ.ui.SearchBox;
    window.Pattern = JRJ.ui.Pattern;
    
    
    
    JRJ.ui.SearchText = function(StoreData,options) {
		options = $.extend({}, JRJ.ui.SearchText.defaults, {
			data: StoreData,
			max: options && !options.scroll ? 10 : 150
		}, options);
		
		// if the formatMatch option is not specified, then use formatItem for backwards compatibility
		options.formatMatch = options.formatMatch || options.formatItem;
		
	var data = {};
	var length = 0;
	
	function matchSubset(s, sub) {
		if (!options.matchCase) 
			s = s.toLowerCase();
		var i = s.indexOf(sub);
		if (options.matchContains == "word"){
			i = s.toLowerCase().search("\\b" + sub.toLowerCase());
		}
		if (i == -1) return false;
		return i == 0 || options.matchContains;
	};
	
	function add(q, value) {
		if (length > options.cacheLength){
			flush();
		}
		if (!data[q]){ 
			length++;
		}
		data[q] = value;
	}
	
	function populate(){
		if( !options.data ) return false;
		// track the matches
		var stMatchSets = {},
			nullData = 0;

		// no url was specified, we need to adjust the cache length to make sure it fits the local data store
		if( !options.url ) options.cacheLength = 1;
		
		// track all options for minChars = 0
		stMatchSets[""] = [];
		
		// loop through the array and create a lookup structure
		for ( var i = 0, ol = options.data.length; i < ol; i++ ) {
			var rawValue = options.data[i];
			// if rawValue is a string, make an array otherwise just reference the array
			rawValue = (typeof rawValue == "string") ? [rawValue] : rawValue;
			
			var value = options.formatMatch(rawValue, i+1, options.data.length);
			if ( value === false )
				continue;
				
			var firstChar = value.charAt(0).toLowerCase();
			// if no lookup array for this character exists, look it up now
			if( !stMatchSets[firstChar] ) 
				stMatchSets[firstChar] = [];

			// if the match is a string
			var row = {
				value: value,
				data: rawValue,
				result: options.formatResult && options.formatResult(rawValue) || value
			};
			
			// push the current match into the set list
			stMatchSets[firstChar].push(row);

			// keep track of minChars zero items
			if ( nullData++ < options.max ) {
				stMatchSets[""].push(row);
			}
		};

		// add the data items to the cache
		$.each(stMatchSets, function(i, value) {
			// increase the cache size
			options.cacheLength++;
			// add to the cache
			add(i, value);
		});
	}
	
	// populate any existing data
	setTimeout(populate, 25);
	
	function flush(){
		data = {};
		length = 0;
	}
	
	return {
		flush: flush,
		add: add,
		populate: populate,
		load: function(q) {
			if (!options.cacheLength || !length)
				return null;
			/* 
			 * if dealing w/local data and matchContains than we must make sure
			 * to loop through all the data collections looking for matches
			 */
			if( !options.url && options.matchContains ){
				// track all matches
				var csub = [];
				// loop through all the data grids for matches
				for( var k in data ){
					// don't search through the stMatchSets[""] (minChars: 0) cache
					// this prevents duplicates
					if( k.length > 0 ){
						var c = data[k];
						$.each(c, function(i, x) {
							// if we've got a match, add it to the array
							if (matchSubset(x.value, q)) {
								csub.push(x);
							}
						});
					}
				}				
				return csub;
			} else 
			// if the exact item exists, use it
			if (data[q]){
				return data[q];
			} else
			if (options.matchSubset) {
				for (var i = q.length - 1; i >= options.minChars; i--) {
					var c = data[q.substr(0, i)];
					if (c) {
						var csub = [];
						$.each(c, function(i, x) {
							if (matchSubset(x.value, q)) {
								csub[csub.length] = x;
							}
						});
						return csub;
					}
				}
			}
			return null;
		}
	};
};
JRJ.ui.SearchText.defaults = {
	minChars: 1,
	delay: 400,
	matchCase: false,
	matchSubset: true,
	matchContains: false,
	cacheLength: 10,
	max: 100,
	mustMatch: false,
	extraParams: {},
	formatItem: function(row) { return (typeof row == "string")?[row]:row[0]; },
	formatMatch: null,
    scroll: true
};
})();

/**
  后记：
      开始想的很完美，做的过程中要平衡复杂需求、浏览器兼容性，完美是不可能的，只能合理取舍尽量平衡处理。
    1.平衡取舍        
      1)  本来想把up down切换做成google suggest一样（input作为切换的起点终点），但加上多选股票，操作逻辑就复杂多了。
      最后去掉这一功能，默认选中结果列表第一项，如果用户没有手动选择，则默认以第一项查询，也符合股票软件用户使用习惯。      
      2)  去掉列表的最小宽度，ie6不支持。
      3)  暂时去掉列表在input上方的定位。
      4)  搜索时不去掉字符串首尾空格。
      5)  在对话框中的查询框，当页面滚动如何处理，最好是一起滚，但有点闪。
      6)  缓存已查询的结果，相同不再请求。判断是否需要请求要考虑多选和各种用户操作造成的变化。
      
    2.选择正确的事件做正确的事
      1)  input的focus/blur+键盘事件(输入键 esc enter up down)
      2)  列表的鼠标和键盘事件
      3)  window的scroll resize事件
      
    3.写case list，反复测试 疯狂测试
   
    未修复bug：
    1.直接点按钮的提示无法隐藏，问题在于用input blur事件来隐藏容器，正确应该用body的click事件
    
    曾今遇到的问题：
    1.按shift键等一些功能键时，重新查询
    2.input在fixed定位的元素里，页面滚动时如何定位偏差大。 
    3.判断是否需要更新很绕，最后简化了up down切换。
    
 */