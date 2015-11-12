if (typeof XJB == "undefined" || !XJB) {
	var XJB = {};
}
XJB.Utils={
       md5:function(str){
//			return $.md5(str+XJB.Const.MD5KEY);
			return $.md5(str);
		},
		md5Pwd:function(sel){
			var pw = $(sel).val();
			pw = XJB.Utils.md5(pw);
			$(sel).val(pw);
		},
		checkPwd:function (password,callback){
			var re=true;
			if(password==undefined){
				re=false;
			}
			password=XJB.Utils.md5(password);
			$.ajax({
				type : "post",
				url : XJB.Urls.checkPassword,
				data :{
					"password" : password
					  },
				dataType : "json",
				timeout : 5000,
				cache : false,
				async:false,
				success : function(data, textStatus) {
					if (typeof(data) != 'undefined' && data != null && data != 'null') {
						if(data.status == 1){
							if (data.retcode == 0) {
								re=true;
								if(callback!=undefined){
									callback();
								}
							}else{
								re=false;
							}
						}else{
							re=false;
						}
					}else{
						re=false;
					}
				}
			});
			return re;
			//return true;
		},
		isCardId:function(socialNo){
				  if(socialNo == "")
				  {
				    return (false);
				  }
				  if (socialNo.length != 18)//socialNo.length != 15 && 暂时去掉对15位身份证判断
				  {
				    return (false);
				  }
					
				 var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}; 
				   if(area[parseInt(socialNo.substr(0,2))]==null) {
				    	return (false);
				   } 
				  if (socialNo.length == 15)
				  {
				     pattern= /^\d{15}$/;
				     if (pattern.exec(socialNo)==null){
						return (false);
				    }
					var birth = parseInt("19" + socialNo.substr(6,2));
					var month = socialNo.substr(8,2);
					var day = parseInt(socialNo.substr(10,2));
					switch(month) {
						case '01':
						case '03':
						case '05':
						case '07':
						case '08':
						case '10':
						case '12':
							if(day>31) {
								return false;
							}
							break;
						case '04':
						case '06':
						case '09':
						case '11':
							if(day>30) {
								return false;
							}
							break;
						case '02':
							if((birth % 4 == 0 && birth % 100 != 0) || birth % 400 == 0) {
								if(day>29) {
									return false;
								}
							} else {
								if(day>28) {
									return false;
								}
							}
							break;
						default:
							return false;
					}
					var nowYear = new Date().getFullYear();
					if(nowYear - parseInt(birth)<15 || nowYear - parseInt(birth)>100) {
						return false;
					}
				    return (true);
				  }
				  var Wi = new Array(
				            7,9,10,5,8,4,2,1,6,
				            3,7,9,10,5,8,4,2,1
				            );
				  var   lSum        = 0;
				  var   nNum        = 0;
				  var   nCheckSum   = 0;
				  
				    for (i = 0; i < 17; ++i)
				    {
				        if ( socialNo.charAt(i) < '0' || socialNo.charAt(i) > '9' )
				        {
				            return (false);
				        }
				        else
				        {
				            nNum = socialNo.charAt(i) - '0';
				        }
				         lSum += nNum * Wi[i];
				    }

				  
//				    if( socialNo.charAt(17) == 'X' || socialNo.charAt(17) == 'x')末位请大写X
				    if( socialNo.charAt(17) == 'X')
				    {
				        lSum += 10*Wi[17];
				    }
				    else if ( socialNo.charAt(17) < '0' || socialNo.charAt(17) > '9' || socialNo.charAt(17) == 'x')
				    {
				        return (false);
				    }
				    else
				    {
				        lSum += ( socialNo.charAt(17) - '0' ) * Wi[17];
				    }
				    if ( (lSum % 11) == 1 )
				    {
				        return true;
				    }
				    else
				    {
				        return (false);
				    }
					
		},
		clearNoNum:function(obj,len){
			var nvalue = obj.value.replace(/[^\d\.]/g,"");
			nvalue = nvalue.replace(/^\./g,"");
			nvalue = nvalue.replace(/\.{2,}/g,".");
			nvalue = nvalue.replace("\.","$#$").replace(/\./g,"").replace("$#$",".");
			                 var strs = "";
			                 var midd = "";
			                 var count = -2;
			               for(var i=0;i<nvalue.length;i++){
				  if(nvalue.charAt(i) === "."){
				     midd = "start";
				  }
				  if(midd === "start"){
				    count++;
				}
				  if(count == len){
				     break;
				  }
				  strs += nvalue.charAt(i);
				}
	               if(obj.value!=strs){
	            	   obj.value = strs;
	               }
				return obj.value;
		}
};


//数组自动去重
Array.prototype._unique = function()
{
	if(this.length==0 || typeof this.length == 'undefined')return this;
	this.sort();
	var re=[this[0]];
	for(var i = 1; i < this.length; i++)
	{
		if( this[i] !== re[re.length-1])
		{
			re.push(this[i]);
		}
	}
	return re;
}
Array.prototype.contains = function (elem) {
	for (var i = 0; i < this.length; i++) {
	if (this[i] == elem) {
	return true;
	}
	}
	return false;
} 
Array.prototype.sub=function(start,end){
	if ((this.length-1<start)||(start>end))return [];
	if (start<0)start=0;
	if (end>this.length-1)end=this.length-1;
	var re=[];
	for(var i=start;i<=end;i++){
		re.push(this[i]);
	}
	return re;
}
//判断对象是不是数组
//Object.prototype.isArray=function () {  
//	  return Object.prototype.toString.call(this) === '[object Array]';   
//}
//针对热股榜的改进
function formatBalanceAdv(val) {
	if (formatBalance(val)=='0') {
		return '0.0000';
	} else {
		return formatBalance(val);
	}
}
//将数字转换成三位逗号分隔的字符串
function formatNum(num){
	if(!/^(\+|-)?(\d+)(\.\d+)?$/.test(num)){
	  		return num;
	   	}
	   	var a = RegExp.$1, b = RegExp.$2, c = RegExp.$3;	
	  	var re = new RegExp("(\\d)(\\d{3})(,|$)");
	  	while(re.test(b)){		
	  		b = b.replace(re, "$1,$2$3");
	 	}
	   	return a +""+ b +""+ c;
}
//四舍五入，小数位不够则补0，如果等于0 直接替换成0.00  专门为排行榜使用。
function formatRankNumber(val, num)  {
	if (val==null || (val==0 && num==0)) {
		return '0';
	}
	if (val=="0" && num>0) {
		return '0';
	}
	return JRJ.util.roundAndPolish(Number(val), num);
}

//格式化金额：规则：1.0230 ,21.230 ,105.36
function formatBalance(val) {
	if (val==null||val=='') {
		return '0';
	}
	var num = getNumberDigits(val);
	return JRJ.util.roundAndPolish(Number(val), num<=5?5-num:0);
}
//四舍五入，小数位不够则补0
function formatNumber(val, num, defaultV)  {
	if (val==null || val==undefined || val=='') {
		return defaultV;
	}
	return JRJ.util.roundAndPolish(Number(val), num);
}

//得到小数点左边位数
function getNumberDigits(num) {
	var reg = /\d+\./;
	var str = reg.exec(num);
	if(str != null) {
	  return str.toString().length - 1;
	} else {
	  return num.toString().length;
	}
}
/*定义简单封装方法*/
var jrjvt ={};
jrjvt.format=function(myNumber, numLength) {
		var myValue = 0;
		try {
			myValue = parseFloat(myNumber);
			if (isNaN(myValue))
				myValue = 0;
		} catch (e) {
			myValue = 0;
		}
		if (!numLength)
			numLength = 2;
		return myValue.toFixed(numLength);
}

function jrjAlert(msg) {
	var alt = new JRJ.ui.Alerts();
	alt.alert({message:"<div>"+msg+"</div>",title:"<b>提示</b>"});	
}

//全局替换
String.prototype.replaceAll = stringReplaceAll; 
function stringReplaceAll(AFindText,ARepText) { 
	var raRegExp = new RegExp(AFindText.replace(/([\(\)\[\]\{\}\^\$\+\-\*\?\.\"\'\|\/\\])/g,"\\$1"),"ig"); 
	return this.replace(raRegExp,ARepText); 
} 
 //去掉空格
String.prototype.trim = function() {
	return this.replace(/^(\u3000|\s|\t|\u00A0)*|(\u3000|\s|\t|\u00A0)*$/g, "");
}
//截取字符串
String.prototype.sub=function(n){
	var r=/[^\x00-\xff]/g;
	if(this.replace(r,"mm").length<=n)return this;
	var m=Math.floor(n/2);
	for(var i=m;i<this.length;i++){
		if(this.substring(0,i).replace(r,"mm").length>=n){
		return this.substring(0,i)+"...";
		}
	}
	return this;
}
String.prototype.bLength = function() {
	if (!this) {
		return 0;
	}
	var b = this.match(/[^\x00-\xff]/g);
	return (this.length + (!b ? 0 : b.length));
}
function StringBuffer(){
	this.data_ = [];
}
StringBuffer.prototype.append = function(){
	this.data_.push(arguments[0]);
	return this;
}
StringBuffer.prototype.toString = function(){
	return this.data_.join("");
} 

//加法函数，用来得到精确的加法结果 
//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。 
//调用：accAdd(arg1,arg2) 
//返回值：arg1加上arg2的精确结果 
function accAdd(arg1, arg2) {
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch(e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch(e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2)); 
    var v1 = parseInt(accMul(arg1, m));
    var v2 = parseInt(accMul(arg2, m));
    var val = parseInt(v1 + v2) / m;
    return val;
}

//减法函数，用来得到精确的减法结果 
//说明：javascript的减法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的减法结果。 
//调用：accSubtr(arg1,arg2) 
//返回值：arg1减去arg2的精确结果 
function accSubtr(arg1, arg2) {
    var r1, r2, m, n;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch(e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch(e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    //动态控制精度长度
    n = (r1 >= r2) ? r1: r2;
    var v1 = parseInt(accMul(arg1, m));
    var v2 = parseInt(accMul(arg2, m));
    return ((v1 - v2) / m).toFixed(n);
}

//除法函数，用来得到精确的除法结果 
//说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。 
//调用：accDiv(arg1,arg2) 
//返回值：arg1除以arg2的精确结果 

function accDiv(arg1, arg2) {
    var t1 = 0,
    t2 = 0,
    r1, r2;
    try {
        t1 = arg1.toString().split(".")[1].length
    } catch(e) {}
    try {
        t2 = arg2.toString().split(".")[1].length
    } catch(e) {}
    with(Math) {
        r1 = Number(arg1.toString().replace(".", "")); r2 = Number(arg2.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
}
//乘法函数，用来得到精确的乘法结果 
//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。 
//调用：accMul(arg1,arg2) 
//返回值：arg1乘以arg2的精确结果 
function accMul(arg1, arg2) {
    var m = 0,
    s1 = arg1.toString(),
    s2 = arg2.toString();
    try {
        m += s1.split(".")[1].length
    } catch(e) {}
    try {
        m += s2.split(".")[1].length
    } catch(e) {}
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
}

/*定义简单封装方法*/
jrjvt.get=function(id){
	return document.getElementById(id);
}
jrjvt.hide=function(id){
	if(jrjvt.get(id))
	jrjvt.get(id).style.display='none';
}
jrjvt.show=function(id){
	if(jrjvt.get(id))
	jrjvt.get(id).style.display='block';
}
jrjvt.fill=function(id,c){
	if(jrjvt.get(id))
	jrjvt.get(id).innerHTML=c;
}

jrjvt.string2json=function(str){
	return JSON.parse(str, function (key, value) {
    return value;
});
}
jrjvt.json2string=function(json){
	return JSON.stringify(json, function (key, value) {
    return value;
});
}
function    HTMLEnCode(str)   
{   
	  var    s    =    "";   
	  if(!str || str.length==0) return s ;
      
      if    (str.length    ==    0)    return    "";   
      s    =    str.replace(/&/g,    "&amp;");   
      s    =    s.replace(/</g,        "&lt;");   
      s    =    s.replace(/>/g,        "&gt;");   
      s    =    s.replace(/    /g,        "&nbsp;");   
      s    =    s.replace(/\'/g,      "&#39;");   
      s    =    s.replace(/\"/g,      "&quot;");   
     // s    =    s.replace(/\n/g,      "<br>");   
      return    s;   
}

function    HTMLDeCode(str)   
{   
	  var    s    =    "";   
	  if(!str || str.length==0) return s ;
      
      if    (str.length    ==    0)    return    "";   
      s    =    str.replace(/&amp;/g,    "&");   
      s    =    s.replace(/\&lt;/g,        "<");   
      s    =    s.replace(/\&gt;/g,        ">");   
      s    =    s.replace(/\&nbsp;/g,        "    ");   
      s    =    s.replace(/\&#39;/g,      "'");   
      s    =    s.replace(/\&quot;/g,      "\"");   
      s    =    s.replace(/<br>/ig,      "\n");  
      s    =    s.replace(/<br\/>/ig,      "\n");  
      return    s;   
}

function getHtmlContent(str) {
	if(!str || str.length==0) return s ;
	str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
	str.value = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
	//str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
	return str;
	}

function getarg()
{
	var url = unescape(window.location.href);
	var allargs = url.split("?")[1];
    if(allargs){
	var args = allargs.split("&");
	for(var i=0; i<args.length; i++)
	{
		var arg = args[i].split("=");
		eval('this.'+arg[0]+'="'+arg[1]+'";');
	}
	}
	
} 

function codeLength(str)
{
	len2=0;
	for (var ci=0;ci<str.length;ci=ci+1)
	{
		var code= str.charCodeAt(ci);
		if (code<128)
		        len1=1;
		else
			len1=2;
		len2=len2+len1;
    }
	return len2;
}

function substr(str, len) {     
	if(!str || !len) { return ''; }     
	 //预期计数：中文2字节，英文1字节    
	  var a = 0;      
	  //循环计数     
	  var i = 0;      
	  //临时字串    
	   var temp = '';      
	   for (i=0;i<str.length;i++)     
	   {         
	   if (str.charCodeAt(i)>128)          
	   {             
	   //按照预期计数增加2             
	   a+=2;         
	   }         
	   else         
	   {            
		a++;         
		}        
		 //如果增加计数后长度大于限定长度，就直接返回临时字符串         
		 if(a > len) { return temp; }          
		 //将当前内容加到临时字符串         
		 temp += str.charAt(i);     
		 }     
		 //如果全部是单字节字符，就直接返回源字符串    
		  return str; 
}//

function isNum(str) {
    var len = 0;
    len = str.length;
    var i = 0;
    for( i=0; i< len; i++) {
        temp = str.substring(i,i+1);
        if (temp >="0" && temp<="9") {
            continue;
        }
        else {
            return false;
        }
    }
    return true;
}

function dovStockmc(rank){
	if(rank ==null || !isNum(rank) || rank>10000 ){
		return '大于<i>10000</i>';
	}
	return '第<i class="imp">'+rank+'</i>';
}
Date.prototype.format = function(format)
{
 var o = {
 "M+" : this.getMonth()+1, //month
 "d+" : this.getDate(),    //day
 "h+" : this.getHours(),   //hour
 "m+" : this.getMinutes(), //minute
 "s+" : this.getSeconds(), //second
 "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
 "S" : this.getMilliseconds() //millisecond
 }
 if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
 (this.getFullYear()+"").substr(4 - RegExp.$1.length));
 for(var k in o)if(new RegExp("("+ k +")").test(format))
 format = format.replace(RegExp.$1,
 RegExp.$1.length==1 ? o[k] :
 ("00"+ o[k]).substr((""+ o[k]).length));
 return format;
}
String.prototype.startWith=function(str){     
	  var reg=new RegExp("^"+str);     
	  return reg.test(this);        
	} 
/* 
//转换成中文大写数字 
*/ 
Number.prototype.toChinese = function() 
{ 
  var num = this; 
  if(!/^\d*(\.\d*)?$/.test(num)){alert("请输入有效数值！"); return "";} 

  var AA = new Array("零","壹","贰","叁","肆","伍","陆","柒","捌","玖"); 
  var BB = new Array("","拾","佰","仟","萬","億","点","");  
  var a = (""+ num).replace(/(^0*)/g, "").split("."), k = 0, re = ""; 

  for(var i=a[0].length-1; i>=0; i--) 
  { 
    switch(k) 
    { 
      case 0 : re = BB[7] + re; break; 
      case 4 : if(!new RegExp("0{4}\\d{"+ (a[0].length-i-1) +"}$").test(a[0])) 
            re = BB[4] + re; break; 
      case 8 : re = BB[5] + re; BB[7] = BB[5]; k = 0; break; 
    } 
    if(k%4 == 2 && a[0].charAt(i+2) != 0 && a[0].charAt(i+1) == 0) re = AA[0] + re; 
    if(a[0].charAt(i) != 0) re = AA[a[0].charAt(i)] + BB[k%4] + re; k++; 
  } 
  
  if(a.length>1) // 加上小数部分(如果有小数部分)
  { 
    re += BB[6]; 
    for(var i=0; i<a[1].length; i++) re += AA[a[1].charAt(i)]; 
  } 
  return re; 
}

/*
 * //保留小数点位数
 */ 
/*Number.prototype.toFixed=function(len) 
{ 

 if(isNaN(len)||len==null) 
 { 
  len = 0; 
 } 
 else 
 { 
  if(len<0) 
  { 
  len = 0; 
  } 
 } 
  return Math.round(this * Math.pow(10,len)) / Math.pow(10,len);
}*/

 

/*
 * //转换成大写金额
 */ 
Number.prototype.toMoney = function() 
{ 
 // Constants:
 var MAXIMUM_NUMBER = 99999999999.99; 
 // Predefine the radix characters and currency symbols for output:
 var CN_ZERO= "零"; 
 var CN_ONE= "壹"; 
 var CN_TWO= "贰"; 
 var CN_THREE= "叁"; 
 var CN_FOUR= "肆"; 
 var CN_FIVE= "伍"; 
 var CN_SIX= "陆"; 
 var CN_SEVEN= "柒"; 
 var CN_EIGHT= "捌"; 
 var CN_NINE= "玖"; 
 var CN_TEN= "拾"; 
 var CN_HUNDRED= "佰"; 
 var CN_THOUSAND = "仟"; 
 var CN_TEN_THOUSAND= "万"; 
 var CN_HUNDRED_MILLION= "亿"; 
 var CN_SYMBOL= ""; 
 var CN_DOLLAR= "圆"; 
 var CN_TEN_CENT = "角"; 
 var CN_CENT= "分"; 
 var CN_INTEGER= "整"; 
 
 // Variables:
 var integral; // Represent integral part of digit number.
 var decimal; // Represent decimal part of digit number.
 var outputCharacters; // The output result.
 var parts; 
 var digits, radices, bigRadices, decimals; 
 var zeroCount; 
 var i, p, d; 
 var quotient, modulus; 
 
 if (this > MAXIMUM_NUMBER) 
 { 
  return ""; 
 } 
 
 // Process the coversion from currency digits to characters:
 // Separate integral and decimal parts before processing coversion:

 parts = (this + "").split("."); 
 if (parts.length > 1) 
 { 
  integral = parts[0]; 
  decimal = parts[1]; 
  // Cut down redundant decimal digits that are after the second.
  decimal = decimal.substr(0, 2); 
 } 
 else 
 { 
  integral = parts[0]; 
  decimal = ""; 
 } 
 // Prepare the characters corresponding to the digits:
 digits= new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE); 
 radices= new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND); 
 bigRadices= new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION); 
 decimals= new Array(CN_TEN_CENT, CN_CENT); 
 
 
 // Start processing:
 outputCharacters = ""; 
 // Process integral part if it is larger than 0:
 if (Number(integral) > 0) 
 { 
  zeroCount = 0; 
  for (i = 0; i < integral.length; i++) 
  { 
   p = integral.length - i - 1; 
   d = integral.substr(i, 1); 
   quotient = p / 4; 
   modulus = p % 4; 
   if (d == "0") 
   { 
    zeroCount++; 
   } 
   else 
   { 
    if (zeroCount > 0) 
    { 
     outputCharacters += digits[0]; 
    } 
    zeroCount = 0; 
    outputCharacters += digits[Number(d)] + radices[modulus]; 
   } 
   if (modulus == 0 && zeroCount < 4) 
   { 
    outputCharacters += bigRadices[quotient]; 
   } 
  } 
  outputCharacters += CN_DOLLAR; 
 } 
 
 // Process decimal part if there is:
 if (decimal != "") 
 { 
  for (i = 0; i < decimal.length; i++) 
  { 
   d = decimal.substr(i, 1); 
   if (d != "0") 
   { 
   outputCharacters += digits[Number(d)] + decimals[i]; 
   } 
  } 
 } 

 // Confirm and return the final output string:
 if (outputCharacters == "") 
 { 
  outputCharacters = CN_ZERO + CN_DOLLAR; 
 } 
 if (decimal == "") 
 { 
  outputCharacters += CN_INTEGER; 
 } 
 outputCharacters = CN_SYMBOL + outputCharacters; 
 return outputCharacters; 
};

Number.prototype.toImage = function() 
{ 
 var num = Array( 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x5,0x5,0x5,0xF}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0x4,0x4,0x4,0x4,0x4}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x4,0xF,0x1,0xF}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x4,0xF,0x4,0xF}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0x5,0x5,0xF,0x4,0x4}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x1,0xF,0x4,0xF}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x1,0xF,0x5,0xF}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x4,0x4,0x4,0x4}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x5,0xF,0x5,0xF}", 
 "#define t_width 3\n#define t_height 5\nstatic char t_bits[] = {0xF,0x5,0xF,0x4,0xF}" 
 ); 

 var str = this + ""; 
 var iIndex; 
 var result="";
 for(iIndex=0;iIndex<str.length;iIndex++) 
 { 
  result +="<img src='javascript:"+ num(iIndex)+ "'>" ;
 } 
 return result; 
};
if(typeof JRJ=="undefined" || !JRJ){
	var JRJ = {};
 }
JRJ.util = {
		isNumberStr : function(n){
			if(!n){return false;}
		 		return !isNaN(n);
		 	}
};
JRJ.util.getDecimalDigits = function(num){
	 	var reg = /\.\d*$/;
	   var str = reg.exec(num);
	   if(str != null) {
     return str.toString().length - 1;
	   } else {
	     return 0;
	   }
	 };
JRJ.util.roundAndPolish = function(val, num){
	if (!JRJ.util.isNumberStr(val)) {
		      return val;
		   }
		   var addZero = function(num, str){
		 	    if (num > 0) {
		 	        return addZero(--num, str + "0");
		 	    } else {
		 	        return str;
		 	    }
		   };
		   var p = Math.pow(10, num);
		   val = (parseInt(Math.round(val * p)) / p).toString();
		   var l = JRJ.util.getDecimalDigits(val);
		   if (l == num) {
		       return val;
		   } else if (l < num) {
		       if (l == 0) {
		           val += '.';
		       }
		       return addZero(num - l, val);
		   }
		   return val;
};
function getPara(parameter) {
	if (isEmpty(QueryString[parameter])) {
		return $.cookie('_' + parameter) || "";
	} else {
		$.cookie('_' + parameter, QueryString[parameter], {
			path : '/'
		});
		return QueryString[parameter];
	}
	return QueryString[parameter];
}

var QueryString = function() {
	var query_string = {};
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (typeof query_string[pair[0]] === "undefined") {
			query_string[pair[0]] = pair[1];
		} else if (typeof query_string[pair[0]] === "string") {
			var arr = [ query_string[pair[0]], pair[1] ];
			query_string[pair[0]] = arr;
		} else {
			query_string[pair[0]].push(pair[1]);
		}
	}
	return query_string;
}();

function getHashPara(paraName) {
    var str = window.location.hash;
    if (str.indexOf(paraName) != -1) {
        var pos_start = str.indexOf(paraName) + paraName.length + 1;
        var pos_end = str.indexOf("#", pos_start);
        if (pos_end == -1) {
            return str.substring(pos_start);
        } else {
            return str.substring(pos_start, pos_end);
        }
    }else {
        return '';
    }
}

function setHashPara(str,paraName,value){
	if(str.indexOf(paraName)!=-1){
		reg=new RegExp('^(.*#'+paraName+'=)[^#]+(#?.*)$');
		str=str.replace(reg, '$1'+value+'$2');
		return str;
	}else{
		return hashAppend(str,paraName+'='+value);
	}
}
function delHashPara(str,paraName){
	reg=new RegExp('^(.*)#+'+paraName+'=[^#]+(#?.*)$');
	str=str.replace(reg,'$1$2');
	if(str.indexOf('#')==-1){
		str+='###';
	}
	return str;
}
function urlAppend(url, s) {
	if (!isEmpty(s)) {
		return url + (url.indexOf('&') === -1 ? (url.indexOf('?') == -1 ? '?':'&') : '&') + s;
	}
	return url;
}

function hashAppend(url,s){
	if (!isEmpty(s)) {
		return url.replace('###','') +'#' + s;
	}
	return url;
}

function isEmpty(v, allowBlank) {
	return v === null || v === undefined ||  !v.length
			|| (!allowBlank ? v === '' : false);
}