if(typeof JRJ=="undefined"||!JRJ){var JRJ={}}if(typeof JRJ.ui=="undefined"||!JRJ.ui){JRJ.ui={}}(function(){var defaultStyles=".jrj_sb{position:absolute;margin:0;padding:0;border:1px solid #999;background-color:#fff;font-size:12px;z-index:99999;overflow:hidden}";defaultStyles+=".jrj_sb *{margin:0;padding:0;}";defaultStyles+=".jrj_sb_mask{display:block;position:absolute;z-index:-1;filter:Alpha(Opacity='0');}";defaultStyles+=".jrj_sb tr{cursor:pointer;white-space:nowrap;}";defaultStyles+=".jrj_sb tr.jrj_sb_on{background:#ddf1fa;}";defaultStyles+=".jrj_sb td{padding:0 10px 0 8px;white-space:nowrap;height: 24px;line-height: inherit;background: none;border-right: none;border-bottom: none;}";defaultStyles+=".jrj_sb tr b{font-weight:bold;color:#000;}";defaultStyles+=".jrj_sb .sb_error{color:#ff0000;padding:3px 10px;line-height:12px}";defaultStyles+=".jrj_sb .sb_muti_prompt{border-top:1px solid #cee0f1;margin-top:5px;padding:8px 12px 4px;background-color:#f0f8ff;}";defaultStyles+=".jrj_sb .sb_muti_prompt .sb_muti_line1{color:#ff0000;line-height:12px}";defaultStyles+=".jrj_sb .sb_muti_prompt .sb_muti_line2{padding-top:3px;color:#c1c0c0;line-height:12px}";defaultStyles+=".jrj_sb table{margin:0 2px;color:#666;border-collapse:collapse;border-spacing:0;line-height:21px;background-color:#fff;z-index:100;border: none;table-layout:auto;width:auto;}";defaultStyles+=".jrj_sb img{margin-right:2px;}";JRJ.ui.SearchBox=function(inputId,btnId,func,param,patterns,styles,config){this.textInput=Tools.$(inputId);if(!this.textInput){return}this.button=btnId?Tools.$(btnId):null;this.func=func;this.param=param?param:"&item=10";this.patterns=patterns;this.defaultPatterns=[new JRJ.ui.Pattern("cn.i","http://stock.jrj.com.cn/share,[stid].shtml"),new JRJ.ui.Pattern("cn.s","http://stock.jrj.com.cn/share,[code].shtml"),new JRJ.ui.Pattern("cn.f","http://fund.jrj.com.cn/archives,[code].shtml"),new JRJ.ui.Pattern("cn.w","http://warrant.jrj.com.cn/StockInfo.aspx?gpdm=[code]"),new JRJ.ui.Pattern("cn.qh.if","http://gzqh.jrj.com.cn/quote/[code]"),new JRJ.ui.Pattern("hk","http://hk.jrj.com.cn/share/[code]/"),new JRJ.ui.Pattern("cn.bk.bk1","http://summary.jrj.com.cn/zjhhy/[code].shtml"),new JRJ.ui.Pattern("cn.bk.bk2","http://summary.jrj.com.cn/qqhy/[code].shtml"),new JRJ.ui.Pattern("cn.bk.bk3","http://summary.jrj.com.cn/dybk/[code].shtml"),new JRJ.ui.Pattern("cn.bk.bk5","http://summary.jrj.com.cn/gnbk/[code].shtml"),new JRJ.ui.Pattern("cn.bk.bk7","http://summary.jrj.com.cn/hybk/[code].shtml"),new JRJ.ui.Pattern("bar.s","http://istock.jrj.com.cn/list,[code].html"),new JRJ.ui.Pattern("bar.f","http://[code].ifund.jrj.com.cn/")];this.url="/stock/code?1=1";this.query="";this.selectedItem=null;this.slist=new Array();this.clist=new Array();this.charset=null;this._dataCache={};this._timer=null;this._isRunning=false;this.timerDelay="50";this._latestScriptTime="";this.config={muti:false,adjustPos:false,topshow:false,reverse:false,showMarket:true,autoFocus:false,clearInput:true,iptEffective:true,btnEffective:true,enterEffective:true,clickEffective:true,forceForm:null,submitForm:false,errorReturn:true,styles:styles?styles:defaultStyles,inputFocusClass:null,blurFunc:null};Tools.extend(this.config,config||{});this.muti=this.config.muti;this.adjustPos=this.config.adjustPos;this.topshow=this.config.topshow;this.offsetLeft=0;this.offsetTop=0;this.reverse=this.config.reverse;this.showMarket=this.config.showMarket;this.autoFocus=this.config.autoFocus;this.clearInput=this.config.clearInput;this.iptEffective=this.config.iptEffective;this.btnEffective=this.config.btnEffective;this.enterEffective=this.config.enterEffective;this.clickEffective=this.config.clickEffective;this.forceForm=this.config.forceForm;this.submitForm=this.config.submitForm;this.input_prompt=this.textInput.value?this.textInput.value:String.fromCharCode(20195,30721,47,31616,31216,47,25340,38899);this.no_reslut_prompt=String.fromCharCode(27809,26377,31526,21512,26465,20214,30340,32467,26524);this.muti_choice_prompt=String.fromCharCode(35831,20351,29992,36887,21495,8220,44,8221,20998,38548,22810,21482,32929,31080);this.error_prompt=String.fromCharCode(35831,36755,20837,27491,30830,30340,20195,30721,25110,31616,31216);this.errorReturn=this.config.errorReturn;this.styles=this.config.styles;this.inputFocusClass=this.config.inputFocusClass;this.blurFunc=this.config.blurFunc;this._init()};JRJ.ui.SearchBox.prototype={_init:function(){this._initInput();this._initContainer();var instance=this;if(this.button){Event.on(this.button,"mouseup",function(event){if(!instance.btnEffective){return}instance._searchHandler(event)})}Event.on(window,"load",function(){if(!instance.autoFocus){instance.textInput.value=instance.input_prompt}});Tools.addStyle("jrj_sb_style",this.styles)},_initInput:function(){var instance=this;this.textInput.value=this.input_prompt;this.textInput.setAttribute("autocomplete","off");Event.on(this.textInput,"focus",function(event){if(!instance.iptEffective){return}if(Tools.trim(instance.textInput.value)==instance.input_prompt){instance.textInput.value=""}if(instance.inputFocusClass){Tools.addClass(instance.textInput,instance.inputFocusClass)}instance.keepFocus=true;Event.stop(event)});Event.on(this.textInput,"blur",function(){if(!instance.iptEffective){return}if(Tools.trim(instance.textInput.value)==""){instance.textInput.value=instance.input_prompt}if(instance.inputFocusClass){Tools.removeClass(instance.textInput,instance.inputFocusClass)}instance.stop();if(!instance.keepFocus){instance.hide()}if(instance.blurFunc){instance.blurFunc()}});var pressingCount=0;Event.on(this.textInput,"keydown",function(event){if(!instance.iptEffective){return}var keyCode=event.keyCode;switch(keyCode){case 27:instance.hide();break;case 13:instance._enterHandler(event);break;case 40:case 38:if(pressingCount++==0){if(instance._isRunning){instance.stop()}instance.selectItem(keyCode==40)}else{if(pressingCount==3){pressingCount=0}}break}if(keyCode!=40&&keyCode!=38&&keyCode!=13){if(!instance._isRunning){instance.start()}}});Event.on(this.textInput,"keyup",function(event){if(!instance.iptEffective){return}pressingCount=0;if(instance.muti){if(event.keyCode>32&&event.keyCode<46){return}var inputVal=Tools.trim(instance.textInput.value);var valArr=inputVal.split(",");var keyVal=valArr.pop();for(var i=0;i<instance.clist.length;i++){var tmp=instance.clist[i];var j;for(j=0;j<valArr.length;j++){if(tmp.code==valArr[j]||tmp.name==valArr[j]||tmp.shrt==valArr[j]){break}}if(j==valArr.length){instance.clist.splice(i,1);i--}}if(event.keyCode==188){if(instance.slist&&instance.slist.length>0){var v=instance.textInput.value;instance.textInput.value=v.substring(0,v.length-1);instance.chooseOne(instance.container.getElementsByTagName("tr")[0],true)}}}});if(this.autoFocus){this.textInput.focus()}},_enterHandler:function(event){var items=this.container.getElementsByTagName("tr");this.updateInputAndClist(items[this.selectedIndex],true);if(!this.enterEffective){this.hide();return}if(!this.muti){this._searchHandler(event)}else{this.container.innerHTML="";return}},_searchHandler:function(event){if(!this.iptEffective){return}if(this.clist.length==0&&this.slist.length!=0){this.clist[0]=this.slist[this.selectedIndex]}this.textInput.blur();if(this.clist.length==0&&this.errorReturn){this.textInput.focus();var content='<p class="sb_error"><img src="/stock/images/trade/sb-alert.gif"/>'+this.error_prompt+"</p>";this._fillContainer(content);this._displayContainer();return false}var ts=Tools.formatDate(new Date());var exp=new Date();exp.setTime(exp.getTime()+30*1000);Tools.setCookie("JRJ_QUERY_COOKIE",ts+","+this.textInput.id+","+encodeURI(window.location),exp,"/","jrj.com.cn");if(!this.muti){if(typeof(this.func)=="function"){this.func(this.clist,event)}else{this._defaultSearch(this.clist[0],event)}}else{if(typeof(this.func)=="function"){this.func(this.clist,event)}else{for(var i=0;i<this.clist.length;i++){this._defaultSearch(this.clist[i],event)}}}this.reset(this.clearInput)},_defaultSearch:function(obj,event){var long_kw="."+obj.mrkt+"."+obj.type+"."+obj.stat+".";var url="";if(this.patterns){url=this.matchPattern(obj,this.patterns)}if(url==""&&this.defaultPatterns){url=this.matchPattern(obj,this.defaultPatterns)}if(url!=""){this.openWin(url,event)}return false},_initContainer:function(){var container=Tools.createElement("div");container.className="jrj_sb";container.style.display="none";this.container=container;this._initContainerEvent();document.body.insertBefore(this.container,document.body.firstChild)},_setContainerPos:function(){try{var pos=Tools.getOffsetPosition(this.textInput);if(!this.topshow){Tools.setElementStyles(this.container,{left:pos.left+this.offsetLeft+"px",top:pos.top+this.textInput.offsetHeight+this.offsetTop+"px"})}else{var doc=document,docEle=doc.documentElement;var dh=docEle&&docEle.clientHeight||doc.body.clientHeight;var scrollTop=docEle&&docEle.scrollTop||doc.body.scrollTop;var b=dh-pos.top-this.offsetTop;b=Tools.ie6?b+scrollTop:b;Tools.setElementStyles(this.container,{left:pos.left+this.offsetLeft+"px",bottom:b+"px"})}}catch(e){}},_initContainerEvent:function(){var instance=this;function getTR(ele){var tr=null;if(ele.tagName.toLowerCase()=="td"){tr=ele.parentNode}if(ele.tagName.toLowerCase()=="tr"){tr=ele}return tr}Event.on(this.container,"mouseover",function(event){var target=Event.getTarget(event);target=getTR(target);if(target&&target!=instance.selectedItem){instance._removeSelectedItem();instance._setSelectedItem(target)}});var mouseDownItem=null;Event.on(document.body,"mousedown",function(event){instance.hide()});Event.on(this.container,"mousedown",function(event){var target=Event.getTarget(event);mouseDownItem=getTR(target);if(navigator.appName=="Microsoft Internet Explorer"&&navigator.appVersion.split(";")[1].replace(/[ ]/g,"")!="MSIE9.0"){instance.textInput.onbeforedeactivate=function(event){window.event.returnValue=false;instance.textInput.onbeforedeactivate=null};instance.keepFocus=true}Event.stop(event);return false});Event.on(this.container,"mouseup",function(event){instance.textInput.focus();var exy=Event.pageXY(event);var pos=Tools.getOffsetPosition(instance.container);var l=pos.left;var t=pos.top;var r=pos.left+instance.container.offsetWidth;var b=pos.top+instance.container.offsetHeight;if(!(exy.left>=l&&exy.left<=r&&exy.top>=t&&exy.top<=b)){return}var target=Event.getTarget(event);target=getTR(target);if(target!=mouseDownItem){return}instance.updateInputAndClist(target,true);if(!instance.muti){if(instance.clickEffective){instance.hide()}instance._searchHandler(event)}instance.keepFocus=false})},start:function(){JRJ.ui.SearchBox.focusInstance=this;var instance=this;this._timer=setTimeout(function(){instance.updateData();instance._timer=setTimeout(arguments.callee,instance.timerDelay)},instance.timerDelay);this._isRunning=true},stop:function(){JRJ.ui.SearchBox.focusInstance=null;if(this._timer){clearTimeout(this._timer);this._timer=null}this._isRunning=false},updateInputAndClist:function(item,compart){if(!item){return}if(this.muti){this.chooseOne(item,compart)}else{this.textInput.value=item.firstChild.innerHTML;this._setClist(this.slist[item.rowIndex])}this.query=item.firstChild.innerHTML},getInputQuery:function(){var query="";var inputVal=Tools.toDBC(this.textInput.value);if(!this.muti){query=inputVal}else{query=inputVal.substr(inputVal.lastIndexOf(",")+1)}return query},chooseOne:function(item,compart){var obj=this.slist[item.rowIndex];var inputValue=this.textInput.value;var keywords=inputValue.split(",");if(keywords.length>0){keywords.length--}var keyword=item.cells[0].innerHTML;if(compart){var i;for(i=0;i<this.clist.length;i++){var obj2=this.clist[i];if(obj.code==obj2.code&&obj.name==obj2.name&&obj.shrt==obj2.shrt){break}}if(i==this.clist.length){this._setClist(obj);keywords.push(keyword)}this.textInput.value=keywords.join(",")+",";this._setSelectedItem(null);this.query=""}else{keywords.push(keyword);this.textInput.value=keywords.join(",")}},selectItem:function(down){if(this.muti&&this.getInputQuery()==""){return}var items=this.container.getElementsByTagName("tr");if(items.length==0){return}if(this.container.style.display=="none"){this.show();return}var newIndex=null;var len=items.length;var index=this.selectedIndex;if(down){newIndex=(index==len-1?0:index+1)}else{newIndex=(index==0?len-1:index-1)}this._removeSelectedItem();var newSelectedItem=items[newIndex];if(newSelectedItem){this._setSelectedItem(newSelectedItem);this.updateInputAndClist(newSelectedItem,false)}},_removeSelectedItem:function(){Tools.removeClass(this.selectedItem,"jrj_sb_on");this.selectedItem=null;this.selectedIndex=0},_setSelectedItem:function(item){if(item){Tools.addClass(item,"jrj_sb_on");this.selectedItem=item;this.selectedIndex=item.rowIndex}else{this.selectedIndex=0;this.selectedItem=null}},_setClist:function(obj){if(!this.muti){this.clist[0]=obj}else{this.clist.push(obj)}},show:function(){var instance=this;this.resizeHandler=Event.on(window,"resize",function(){instance._setContainerPos()});this.scrollHandler=Event.on(window,"scroll",function(){instance._setContainerPos()});instance.container.style.display="block"},hide:function(){this.container.style.display="none";if(this.resizeHandler&&this.scrollHandler){Event.remove(this.resizeHandler);Event.remove(this.scrollHandler)}},updateData:function(){if(!this.iptEffective||!this.needUpdate()){return}this.query=this.getInputQuery();var q=Tools.trim(this.query);if(q.length==0){this._fillContainer("");this.hide();return}if(typeof this._dataCache[this.query]!="undefined"){this._fillContainer(this._dataCache[this.query].html);this.slist=this._dataCache[this.query].slist;this._setSelectedItem(this.container.getElementsByTagName("tr")[0]);this._createMutiPrompt();this._displayContainer()}else{this.requestData()}},needUpdate:function(){var inputQuery=this.getInputQuery();if(inputQuery==""&&this.textInput.value.length>0){this.hide();return false}if(inputQuery!=this.query){return true}return false},requestData:function(){var script=document.createElement("script");script.setAttribute("type","text/javascript");if(this.charset){script.setAttribute("charset",this.charset)}var oSrc=this.url+this.param+"&key="+this.query+"&d="+Tools.createTimeStamp();script.setAttribute("src",oSrc);var t=new Date().getTime();this._latestScriptTime=t;script.setAttribute("time",t);var head=document.getElementsByTagName("head")[0]||document.documentElement;script.onload=script.onreadystatechange=function(){if(!this.readyState||this.readyState==="loaded"||this.readyState==="complete"){var obj=JRJ.ui.SearchBox.focusInstance;if(!obj){return}var scriptDataIsOut=script.getAttribute("time")!=obj._latestScriptTime;if(!scriptDataIsOut){obj.handleResponse(SCodeData)}script.onload=script.onreadystatechange=null;if(head&&script.parentNode){head.removeChild(script)}}};head.insertBefore(script,head.firstChild)},handleResponse:function(data){if(!JRJ.ui.SearchBox.focusInstance){return}if(!data){return}try{this.slist=data.CodeData;if(this.reverse){this.slist.reverse()}}catch(e){this.slist=new Array()}var list=this.slist;var content="";if(list.length==0){content='<p class="sb_error"><img src="/stock/images/trade/sb-alert.gif"/>'+this.no_reslut_prompt+"</p>";this.selectedItem=null;this.selectedIndex=0;this._fillContainer(content)}else{content=Tools.createElement("table");this.selectedIndex=0;var q=this.query;for(var i=0;i<list.length;i++){var tr=content.insertRow(content.rows.length);var item=list[i];var keyItem="";if(Tools.contains(item.shrt,q)){keyItem=item.shrt}else{if(Tools.contains(item.code,q)){keyItem=item.code}else{if(Tools.contains(item.name,q)){keyItem=item.name}}}var td1=tr.insertCell(tr.cells.length);td1.style.display="none";td1.innerHTML=keyItem;var td2=tr.insertCell(tr.cells.length);td2.innerHTML=Tools.bold(item.code,q);if(Tools.isUS(item.mrkt)){var td3=tr.insertCell(tr.cells.length);td3.colSpan=2;td3.innerHTML=Tools.bold(item.name,q)}else{var td3=tr.insertCell(tr.cells.length);td3.innerHTML=Tools.bold(item.name,q);var td4=tr.insertCell(tr.cells.length);td4.innerHTML=Tools.bold(item.shrt,q)}if(this.showMarket){var td5=tr.insertCell(tr.cells.length);td5.innerHTML=Tools.getTag(item.mrkt,item.type)}}this._setSelectedItem(content.getElementsByTagName("tr")[0]);this._fillContainer(content)}this._dataCache[this.query]={html:this.container.innerHTML,slist:list};this._createMutiPrompt();this._displayContainer()},_fillContainer:function(content){if(content.nodeType==1){this.container.innerHTML="";this.container.appendChild(content)}else{this.container.innerHTML=content}if(Tools.ie6){this.container.insertBefore(Tools.createIframeMask(),this.container.firstChild)}this._setContainerPos()},_displayContainer:function(){if(Tools.trim(this.container.innerHTML)){this.show()}else{this.hide()}},_createMutiPrompt:function(){if(this.slist.length>0&&this.muti){var prompt=Tools.createElement("div");Tools.addClass(prompt,"sb_muti_prompt");prompt.innerHTML="<p class='sb_muti_line1'><img src='/stock/images/trade/sb-alert.gif'/>["+this.muti_choice_prompt+"]</p><p class='sb_muti_line2'><img src='/stock/images/trade/sb-dot.gif'/>"+String.fromCharCode(20363,22914,65306)+"601858,600123,000001</p>";this.container.appendChild(prompt)}},reset:function(clearInput){this.selectedItem=null;this.selectedIndex=0;if(clearInput){this.query="";this.textInput.value=""}this.slist=new Array();this.clist=new Array()},clear:function(){this.reset(true);this.textInput.value=this.input_prompt;this._dataCache={}},remove:function(){if(this.container&&this.container.parentNode){this.container.parentNode.removeChild(this.container)}},openWin:function(url,event){try{this.submitForm=true;if(this.forceForm!=null){var fw=new JRJ.ui.ForceWin(this.forceForm);if(event.type=="mouseup"){fw.open2(url)}else{if(Tools.isWebkit){fw.open2(url)}else{fw.open(url)}}}else{window.open(url)}}catch(e){window.open(url)}},checkSubmit:function(){try{if(this.submitForm){return true}else{return false}}catch(e){return false}finally{this.submitForm=false}},matchPattern:function(obj,patterns){var url="";for(var i=0;i<patterns.length;i++){if(typeof(patterns[i])=="object"){url=patterns[i].readURL(obj);if(url!=""){return url}}}return""}};JRJ.ui.SearchBox.focusInstance=null;JRJ.ui.Pattern=function(kw,urlPattern){this.kw=kw;this.urlPattern=urlPattern};JRJ.ui.Pattern.prototype={readURL:function(obj){if(!obj){return""}var longKeyWord=".";for(var p in obj){longKeyWord+=obj[p]+"."}var kwArray=this.kw.split(".");for(var i=0;i<kwArray.length;i++){var pos=Tools.trim(longKeyWord).indexOf("."+kwArray[i]+".");if(pos==-1){return""}else{longKeyWord=longKeyWord.replace("."+kwArray[i]+".",".")}}return this.replaceURL(obj)},replaceURL:function(obj){var url=this.urlPattern;if(obj){for(var p in obj){var reg=new RegExp("\\["+p+"\\]","g");url=url.replace(reg,obj[p])}}return url}};JRJ.ui.ForceWin=function(formId){if(formId&&document.getElementById(formId)){this.forceForm=document.getElementById(formId);if(!this.forceForm.url){return}this.forceForm.target="_blank";this.forceForm.method="get";this.forceForm.action="http://sq.share.jrj.com.cn/jump";this.btn=document.getElementById(formId+"_submitBtn")}this.open=function(sUrl){this.forceForm.url.value=sUrl;this.btn.focus()};this.open2=function(sUrl){this.forceForm.url.value=sUrl;this.forceForm.submit()}};var Event={on:function(ele,evt,fcn){if(Tools.isDefined(ele,"attachEvent")){ele.attachEvent("on"+evt,function(event){fcn(event)})}else{if(Tools.isDefined(ele,"addEventListener")){ele.addEventListener(evt,fcn,false)}}return[ele,evt,fcn]},remove:function(args){if(Tools.isDefined(args[0],"detachEvent")){args[0].detachEvent("on"+args[1],args[2])}else{if(Tools.isDefined(args[0],"removeEventListener")){args[0].removeEventListener(args[1],args[2],false)}}},bindAsEvent:function(object,method,param){return function(event){method.apply(object,[event||window.event].concat(param))}},getTarget:function(event){if(event==null){return null}if(typeof(event.srcElement)!="undefined"){return event.srcElement}else{return event.target}},stop:function(evt){if(document.all){evt.returnValue=false;evt.cancelBubble=true}else{evt.preventDefault();evt.stopPropagation()}},pageXY:function(event){var l=event.pageX||(event.clientX+(document.documentElement.scrollLeft||document.body.scrollLeft));var t=event.pageY||(event.clientY+(document.documentElement.scrollTop||document.body.scrollTop));return{left:l,top:t}}};var userAgent=navigator.userAgent.toLowerCase();var Tools={ie:
/*@cc_on!@*/
false, ie6: navigator.appVersion.indexOf("MSIE 6.0") != -1, isWebkit: /applewebkit/.test(userAgent), $: function (id, docObj) { if (!docObj) { var doc = document } return doc.getElementById(id) }, isDefined: function (obj, field) { return typeof (obj[field]) != "undefined" }, extend: function (a, b) { for (var i in b) { a[i] = b[i] } return a }, createElement: function (tName, docObj) { if (!docObj) { var doc = document } return doc.createElement(tName) }, createIframeMask: function () { var mask = Tools.createElement("iframe"); mask.src = "/stock/blank.jsp"; mask.frameBorder = "0"; mask.className = "jrj_sb_mask"; mask.style.setExpression("top", 'eval("(parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1") + "px"'); mask.style.setExpression("left", 'eval("(parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1") + "px"'); mask.style.setExpression("width", 'eval("this.parentNode.offsetWidth") + "px"'); mask.style.setExpression("height", 'eval("this.parentNode.offsetHeight") + "px"'); return mask }, setElementStyles: function (element, styles) { var style = element.style; for (var name in styles) { style[name] = styles[name] } }, addStyle: function (id, css, doc) { var styleEl = this.$(id); if (styleEl) { return } var doc = doc || document; var style = doc.createElement("style"); style.id = id; style.type = "text/css"; var head = doc.getElementsByTagName("head")[0]; head.insertBefore(style, head.firstChild); if (style.styleSheet) { style.styleSheet.cssText = css } else { style.appendChild(doc.createTextNode(css)) } }, getOffsetPosition: function (elem) { if (!elem) { return { left: 0, top: 0 } } var top = 0, left = 0; if ("getBoundingClientRect" in document.documentElement) { var box = elem.getBoundingClientRect(), doc = elem.ownerDocument, body = doc.body, docElem = doc.documentElement, clientTop = docElem.clientTop || body.clientTop || 0, clientLeft = docElem.clientLeft || body.clientLeft || 0, top = box.top + (self.pageYOffset || docElem && docElem.scrollTop || body.scrollTop) - clientTop, left = box.left + (self.pageXOffset || docElem && docElem.scrollLeft || body.scrollLeft) - clientLeft } else { do { top += elem.offsetTop || 0; left += elem.offsetLeft || 0; elem = elem.offsetParent } while (elem) } return { left: left, top: top } }, trim: function (str) { return str.replace(/(^\s*)|(\s*$)/g, "") }, contains: function (str1, str2) { return str1.toLowerCase().indexOf(str2.toLowerCase()) > -1 }, hasClass: function (ele, cls) { return ele ? ele.className.match(new RegExp("(\\s|^)" + cls + "(\\s|$)")) : "" }, addClass: function (ele, cls) { if (!this.hasClass(ele, cls)) { ele.className += " " + cls } }, removeClass: function (ele, cls) { if (this.hasClass(ele, cls)) { var reg = new RegExp("(\\s|^)" + cls + "(\\s|$)"); ele.className = ele.className.replace(reg, " ") } }, isUS: function (mrkt) { return -1 != ("." + mrkt + ".").indexOf(".us.") }, bold: function (str, key) { if (this.contains(str, key)) { var begin = str.toLowerCase().indexOf(key.toLowerCase()); return str.substring(0, begin) + "<b>" + str.substring(begin, key.length + begin) + "</b>" + str.substring(key.length + begin, str.length) } else { return str } }, getTag: function (market, type) { var tag = ""; switch (market) { case "us.nys": case "us.nas": case "us.uss": case "us": tag = String.fromCharCode(32654, 32929); break; case "hk": tag = String.fromCharCode(28207, 32929); break; case "cn.sz": tag = String.fromCharCode(28145, 24066); break; case "cn.sh": tag = String.fromCharCode(27818, 24066); break; case "cn": tag = String.fromCharCode(27818, 28145); break; case "bar": if (("." + type + ".").indexOf(".s.") >= 0) { tag = String.fromCharCode(29233, 32929) } else { if (("." + type + ".").indexOf(".f.") >= 0) { tag = String.fromCharCode(29233, 22522) } else { tag = String.fromCharCode(21543) } } break } if (("." + type + ".").indexOf(".f.") >= 0) { tag = String.fromCharCode(22522, 37329) } if (("." + type + ".").indexOf(".qh.") >= 0) { tag = String.fromCharCode(26399, 36135) } return tag }, toDBC: function (str) { var DBCStr = ""; for (var i = 0; i < str.length; i++) { var c = str.charCodeAt(i); if (c == 12288) { DBCStr += String.fromCharCode(32); continue } if (c > 65280 && c < 65375) { DBCStr += String.fromCharCode(c - 65248); continue } DBCStr += String.fromCharCode(c) } return DBCStr }, createTimeStamp: function () { var nowDate = new Date(); var h = nowDate.getHours(); var m = nowDate.getMinutes(); var s = nowDate.getSeconds(); if (h <= 9) { h = "0" + h } if (m <= 9) { m = "0" + m } if (s <= 9) { s = "0" + s } return h + "" + m + "" + s }, formatDate: function (d) { var str = "yyMMddhhmmss"; var month = d.getMonth() + 1; str = str.replace(/yy/, (d.getYear() % 100) > 9 ? (d.getYear() % 100).toString() : "0" + (d.getYear() % 100)); str = str.replace(/MM/, month > 9 ? month.toString() : "0" + month); str = str.replace(/dd/, d.getDate() > 9 ? d.getDate().toString() : "0" + d.getDate()); str = str.replace(/hh/, d.getHours() > 9 ? d.getHours().toString() : "0" + d.getHours()); str = str.replace(/mm/, d.getMinutes() > 9 ? d.getMinutes().toString() : "0" + d.getMinutes()); str = str.replace(/ss/, d.getSeconds() > 9 ? d.getSeconds().toString() : "0" + d.getSeconds()); return str }, setCookie: function (name, value, expires, path, domain, secure) { document.cookie = name + "=" + escape(value) + ((expires) ? "; expires=" + expires.toGMTString() : "") + ((path) ? "; path=" + path : "") + ((domain) ? "; domain=" + domain : "") + ((secure) ? "; secure" : "") }
}; window.SearchBox = JRJ.ui.SearchBox; window.Pattern = JRJ.ui.Pattern
})();
