var jrj_SDC_code=' <!-- START OF SDC Advanced Tracking Code --> \n <SCRIPT LANGUAGE="Javascript"><!-- \n var gService = true; \n var gTimeZone = +8; \n // Code section for Enable First-Party Cookie Tracking \n function dcsCookie(){ \n 	if (typeof(dcsOther)=="function"){ \n 		dcsOther(); \n 	} \n 	else if (typeof(dcsPlugin)=="function"){ \n 		dcsPlugin(); \n 	} \n 	else if (typeof(dcsFPC)=="function"){ \n 		dcsFPC(gTimeZone); \n 	} \n } \n function dcsGetCookie(name){ \n 	var pos=document.cookie.indexOf(name+"="); \n 	if (pos!=-1){ \n 		var start=pos+name.length+1; \n 		var end=document.cookie.indexOf(";",start); \n 		if (end==-1){ \n 			end=document.cookie.length; \n 		} \n 		return unescape(document.cookie.substring(start,end)); \n 	} \n 	return null; \n } \n function dcsGetCrumb(name,crumb){ \n 	var aCookie=dcsGetCookie(name).split(":"); \n 	for (var i=0;i<aCookie.length;i++){ \n 		var aCrumb=aCookie[i].split("="); \n 		if (crumb==aCrumb[0]){ \n 			return aCrumb[1]; \n 		} \n 	} \n 	return null; \n } \n function dcsFPC(offset){ \n 	if (typeof(offset)=="undefined"){ \n 		return; \n 	} \n 	var name=gFpc; \n 	var dCur=new Date(); \n 	dCur.setTime(dCur.getTime()+(dCur.getTimezoneOffset()*60000)+(offset*3600000)); \n 	var dExp=new Date(dCur.getTime()+315360000000); \n 	var dSes=new Date(dCur.getTime()); \n 	if (document.cookie.indexOf(name+"=")!=-1){ \n 		var id=dcsGetCrumb(name,"id"); \n 		var lv=parseInt(dcsGetCrumb(name,"lv")); \n 		var ss=parseInt(dcsGetCrumb(name,"ss")); \n 		if ((id==null)||(id=="null")||isNaN(lv)||isNaN(ss)){ \n 			return; \n 		} \n 		WT.co_f=id; \n 		WT.vt_f="0"; \n 		WT.vt_f_a="0"; \n 		var dLst=new Date(lv); \n 		dSes.setTime(ss); \n 		if ((dCur.getTime()>(dLst.getTime()+1800000))||(dCur.getTime()>(dSes.getTime()+28800000))){ \n 			dSes.setTime(dCur.getTime()); \n 			WT.vt_f_s="1"; \n 		} \n 		if ((dCur.getDay()!=dLst.getDay())||(dCur.getMonth()!=dLst.getMonth())||(dCur.getYear()!=dLst.getYear())){ \n 			WT.vt_f_d="1"; \n 		} \n 	} \n 	else{ \n 		var tmpname=name+"_TMP="; \n 		document.cookie=tmpname+"1"; \n 		if (document.cookie.indexOf(tmpname)!=-1){ \n 			document.cookie=tmpname+"; expires=Thu, 01-Jan-1970 00:00:01 GMT"; \n 			if ((typeof(gWtId)!="undefined")&&(gWtId!="")){ \n 				WT.co_f=gWtId; \n 				WT.vt_f="0"; \n 			} \n 			else if ((typeof(gTempWtId)!="undefined")&&(gTempWtId!="")){ \n 				WT.co_f=gTempWtId; \n 				WT.vt_f="1"; \n 			} \n 			else{ \n 				WT.co_f="2"; \n 				var cur=dCur.getTime().toString(); \n 				for (var i=2;i<=(32-cur.length);i++){ \n 					WT.co_f+=Math.floor(Math.random()*16.0).toString(16); \n 				} \n 				WT.co_f+=cur; \n 				WT.vt_f="1"; \n 			} \n 			WT.vt_f_a=(typeof(gWtAccountRollup)!="undefined")?"0":"1"; \n 			WT.vt_f_s="1"; \n 			WT.vt_f_d="1"; \n 		} \n 		else{ \n 			WT.vt_f="2"; \n 			WT.vt_f_a="2"; \n 			return; \n 		} \n 	} \n 	WT.co_f=escape(WT.co_f); \n 	WT.vt_sid=WT.co_f+"."+dSes.getTime(); \n 	var expiry="; expires="+dExp.toGMTString(); \n 	document.cookie=name+"="+"id="+WT.co_f+":lv="+dCur.getTime().toString()+":ss="+dSes.getTime().toString()+expiry+"; path=/"+(((typeof(gFpcDom)!="undefined")&&(gFpcDom!=""))?("; domain="+gFpcDom):("")); \n } \n // Code section for Use the new first-party cookie generated with this tag. \n var gFpc="WT_FPC"; \n var gWtId=""; \n var gTempWtId=""; \n if (gService){ \n 	var gConvert=true; \n } \n function dcsMultiTrack(){ \n 	for (var i=0;i<arguments.length;i++){ \n 		if (arguments[i].indexOf(\'WT.\')==0){ \n 				WT[arguments[i].substring(3)]=arguments[i+1]; \n 				i++; \n 		} \n 		if (arguments[i].indexOf(\'DCS.\')==0){ \n 				DCS[arguments[i].substring(4)]=arguments[i+1]; \n 				i++; \n 		} \n 		if (arguments[i].indexOf(\'DCSext.\')==0){ \n 				DCSext[arguments[i].substring(7)]=arguments[i+1]; \n 				i++; \n 		} \n 	} \n 	var dCurrent=new Date(); \n 	DCS.dcsdat=dCurrent.getTime(); \n 	dcsTag(); \n } \n function dcsAdv(){ \n 	dcsFunc("dcsCookie"); \n } \n //--> \n </SCRIPT> \n <!-- END OF SDC Advanced Tracking Code --> \n <!-- START OF SmartSource Data Collector TAG --> \n <SCRIPT LANGUAGE="JavaScript"><!-- \n var gImages=new Array; \n var gIndex=0; \n var DCS=new Object(); \n var WT=new Object(); \n var DCSext=new Object(); \n var gQP=new Array(); \n var gDomain="sdcs.jrj.com.cn"; \n var gDcsId="dcs0kbnws6cwv3t3c80mwlr23_8j4c"; \n // Add customizations here \n if ((typeof(gConvert)!="undefined")&&gConvert&&(document.cookie.indexOf(gFpc+"=")==-1)){ \n 	//document.write("<SCR"+"IPT Language=\'JavaScript\' SRC=\'"+"http"+(window.location.protocol.indexOf(\'https:\')==0?\'s\':\'\')+"://"+gDomain+"/"+gDcsId+"/wtid.js"+"\'></SCR"+"IPT>"); \n } \n //--> \n </SCRIPT> \n <SCRIPT LANGUAGE="Javascript"><!-- \n function dcsVar(){ \n 	var dCurrent=new Date(); \n 	WT.tz=dCurrent.getTimezoneOffset()/60*-1; \n 	if (WT.tz==0){ \n 		WT.tz="0"; \n 	} \n 	WT.bh=dCurrent.getHours(); \n 	WT.ul=navigator.appName=="Netscape"?navigator.language:navigator.userLanguage; \n 	if (typeof(screen)=="object"){ \n 		WT.cd=navigator.appName=="Netscape"?screen.pixelDepth:screen.colorDepth; \n 		WT.sr=screen.width+"x"+screen.height; \n 	} \n 	if (typeof(navigator.javaEnabled())=="boolean"){ \n 		WT.jo=navigator.javaEnabled()?"Yes":"No"; \n 	} \n 	if (document.title){ \n 		WT.ti=document.title; \n 	} \n 	WT.fi="No"; \n 	if (navigator.plugins && navigator.plugins.length) {\n 		for (var ii=0;ii<navigator.plugins.length;ii++) {\n 		if (navigator.plugins[ii].name.indexOf(\'Shockwave Flash\')!=-1) {\n 			WT.fv=navigator.plugins[ii].description.split(\'Shockwave Flash \')[1];\n 			WT.fi="Yes"; \n 			break;\n 		}\n 	}\n 	} else if (window.ActiveXObject) {\n 	var swf = new ActiveXObject(\'ShockwaveFlash.ShockwaveFlash\');\n 	 	if(swf) {\n 	 	 	try{WT.fv=swf.GetVariable(\'$version\').split(\' \')[1].replace(/,/ig,\'.\');WT.fi=\'Yes\';}catch(e){}\n 	 	}\n 	}\n 	if(WT.fi==\'Yes\'){\n 	    WT.fv=WT.fv.substring(0,WT.fv.indexOf(\'.\')+2); \n 	 }\n 	DCS.dcsdat=dCurrent.getTime(); \n 	DCS.dcssip=window.location.hostname; \n 	DCS.dcsuri=window.location.pathname; \n 	if (window.location.search){ \n 		DCS.dcsqry=window.location.search; \n 		if (gQP.length>0){ \n 			for (var i=0;i<gQP.length;i++){ \n 				var pos=DCS.dcsqry.indexOf(gQP[i]); \n 				if (pos!=-1){ \n 					var front=DCS.dcsqry.substring(0,pos); \n 					var end=DCS.dcsqry.substring(pos+gQP[i].length,DCS.dcsqry.length); \n 					DCS.dcsqry=front+end; \n 				} \n 			} \n 		} \n 	} \n 	if ((window.document.referrer!="")&&(window.document.referrer!="-")){ \n 		if (!(navigator.appName=="Microsoft Internet Explorer"&&parseInt(navigator.appVersion)<4)){ \n 			DCS.dcsref=window.document.referrer; \n 		} \n 	} \n } \n function A(N,V){ \n 	return "&"+N+"="+dcsEscape(V); \n } \n function dcsEscape(S){ \n 	if (typeof(RE)!="undefined"){ \n 		var retStr = new String(S); \n 		for (R in RE){ \n 			retStr = retStr.replace(RE[R],R); \n 		} \n 		return retStr; \n 	} \n 	else{ \n 		return escape(S); \n 	} \n } \n function dcsLoadHref(evt){ \n 	if ((typeof(gHref)!="undefined")&&(gHref.length>0)){ \n 		window.location=gHref; \n 		gHref=""; \n 	} \n } \n function dcsCreateImage(dcsSrc){ \n 	if (document.images){ \n 		gImages[gIndex]=new Image; \n 		if ((typeof(gHref)!="undefined")&&(gHref.length>0)){ \n 			gImages[gIndex].onload=gImages[gIndex].onerror=dcsLoadHref; \n 		} \n 		gImages[gIndex].src=dcsSrc; \n 		gIndex++; \n 	} \n 	else{ \n 		document.write(\'<IMG BORDER="0" NAME="DCSIMG" WIDTH="1" HEIGHT="1" SRC="\'+dcsSrc+\'">\'); \n 	} \n } \n function dcsMeta(){ \n 	var elems; \n 	if (document.all){ \n 		elems=document.all.tags("meta"); \n 	} \n 	else if (document.documentElement){ \n 		elems=document.getElementsByTagName("meta"); \n 	} \n 	if (typeof(elems)!="undefined"){ \n 		for (var i=1;i<=elems.length;i++){ \n 			var meta=elems.item(i-1); \n 			if (meta.name){ \n 				if (meta.name.indexOf(\'WT.\')==0){ \n 					WT[meta.name.substring(3)]=meta.content; \n 				} \n 				else if (meta.name.indexOf(\'DCSext.\')==0){ \n 					DCSext[meta.name.substring(7)]=meta.content; \n 				} \n 				else if (meta.name.indexOf(\'DCS.\')==0){ \n 					DCS[meta.name.substring(4)]=meta.content; \n 				} \n 			} \n 		} \n 	} \n } \n function dcsTag(){ \n 	var P="http"+(window.location.protocol.indexOf(\'https:\')==0?\'s\':\'\')+"://"+gDomain+(gDcsId==""?\'\':\'/\'+gDcsId)+"/dcs.gif?"; \n 	for (N in DCS){ \n 		if (DCS[N]) { \n 			P+=A(N,DCS[N]); \n 		} \n 	} \n 	for (N in WT){ \n 		if (WT[N]) { \n 			P+=A("WT."+N,WT[N]); \n 		} \n 	} \n 	for (N in DCSext){ \n 		if (DCSext[N]) { \n 			P+=A(N,DCSext[N]); \n 		} \n 	} \n 	if (P.length>2048&&navigator.userAgent.indexOf(\'MSIE\')>=0){ \n 		P=P.substring(0,2040)+"&WT.tu=1"; \n 	} \n 	dcsCreateImage(P); \n } \n function dcsFunc(func){ \n 	if (typeof(window[func])=="function"){ \n 		window[func](); \n 	} \n } \n dcsVar(); \n //dcsMeta(); \n dcsFunc("dcsAdv"); \n dcsTag(); \n //--> \n </SCRIPT> ';document.write(jrj_SDC_code);var ifr='<iframe width="0" height="0" frameborder="0" scrolling="no" marginwidth="0" marginheight="0" src="http'+(window.location.protocol.indexOf('https:')==0?'s':'')+'://sdcs.jrj.com.cn:'+(window.location.protocol.indexOf('https:')==0?'8443':'8080')+'/setshareid.jsp" ></iframe>';document.write(ifr);
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-55731371-1', 'auto');
ga('send', 'pageview');

