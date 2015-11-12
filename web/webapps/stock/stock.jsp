<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta content="IE=EmulateIE7" http-equiv="X-UA-Compatible"/>
<script src="/stock/js/jquery/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="/stock/js/util/swfobject-2.0.js"></script>
<script type="text/javascript" src="/stock/js/util/SearchBox-OldStyle.js"></script>
<script type="text/javascript" src="/stock/js/util/HqTiming.js"></script>
<script type="text/javascript" src="/stock/js/util/MainQuote.js"></script>
<style>
/*reset*/
body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,form,input,textarea,p,table,th,td,iframe{margin:0;padding:0;}table{border-collapse:collapse;border-spacing:0;}img{border:0;vertical-align:top;}em,i{font-style:normal;}a{text-decoration:none;}a:hover{color:#c00;}li{list-style:none;}input[type="button"],input[type="submit"],input[type="reset"]{cursor:pointer;}body{font-size:12px;line-height:24px;background:#fff;}
/*grid*/
.jrj-wrap,.jrj-where{width:950px;margin:0 auto;}
.jrj-w240{width:230px;}
.jrj-w360{width:350px;}
.jrj-w720{width:710px;}
/*common style*/
a{color:#039;text-decoration:none;}
a:visited{color:#800080;}
a:hover{color:#c00;text-decoration:underline;}
body{font-size:12px;font-family:"宋体";line-height:24px;}
.jrj-tl{text-align:left;}
.jrj-tc{text-align:center;}
.jrj-tr{text-align:right;}
.jrj-red{color:#f00;}
.jrj-gre{color:#090;}
.jrj-fl{float:left;}
.jrj-fr{float:right;}
.jrj-f14{font-size:14px;}
.oprt li,.freshnew .newset,.addbtn,.mtab li,.mtab li.tbw2 a,.kyyz .btn1,.tit h3,.jrj-l1 li,.btn,.star,.star i,.close,.table i,.gzico,.kdico,.kzico,.calendar span a,.arwico,.arwico2,button,.inps span{background:url() no-repeat;}
.freshnew,.info .mtit,.tit1,.graphbox .ssbj th,.nemu,.popbar,.ppbox .t1,.ppbox .t2,.popbt,.calendar th,.yuce{background:url() repeat-x;}
.info{border:1px solid #cdd7e6;margin-bottom:10px;overflow:hidden;zoom:1;}
.info .jrj-fl{width:640px;border-right:1px solid #cdd7e6;}
.info .jrj-fr{width:300px;}
.info .mtit{height:36px;background-position:0 -33px;padding:4px 10px 0 5px;}
.info h2{float:left;font:20px/30px "黑体";padding:0 10px;}
.info .mtit em{float:left;line-height:30px;}
.info .mtit .addbtn{float:left;width:70px;height:25px;padding-left:30px;margin:0 50px 0 10px;background-position:0 -40px;cursor:pointer;color:#000;}
.info .mtit .addbtn:hover{color:#000;text-decoration:none;}
.info .mtit span{float:right;*margin-top:-25px;}
.info .num{float:left;width:140px;border-right:1px dotted #ccc;text-align:center;}
.info .num strong{font:33px/40px "黑体";}
.info .num b{font-size:16px;padding:0 8px;}
.info .num .time{color:#999;}
.info .data{float:right;width:475px;margin-top:6px;}
.info .jrj-fr th{text-align:left;font-weight:normal;line-height:25px;width:76px;}
.info .jrj-fr td{text-align:right;}
.table{line-height:28px;width:100%;}
.table th{background:url() repeat-x left bottom;font-weight:normal;line-height:18px;}
.table th span{display:block;background:url() no-repeat right bottom;padding:5px 0;}
.table th span.yh1{line-height:36px;}
.table td{padding:0 5px;}
.table .cur{background:#F6F9FC;}
.graphbox{height:442px;overflow:hidden;zoom:1;position:relative;margin-bottom:10px;}
.graphbox .jrj-fl{width:480px;}
.graphbox .opr{position:absolute;left:455px;top:0;line-height:20px;_top:-3px;z-index:9499;height:20px;border-bottom:1px solid #CCCCCC;}
.graphbox .opr .btn,.yuce .btn{display:inline-block;width:85px;height:24px;line-height:24px;background-position:-110px -40px;text-align:center;}
.graphbox .opr .btn:hover,.yuce .btn:hover{color:#039;text-decoration:none;}
.graphbox .opr .btn{margin-left:25px;height:19px;line-height:19px;background-position:-110px -80px;}
.graphbox .flash{width:478px;height:441px;overflow:hidden;border-left:1px solid #B5CDE5;border-bottom:1px solid #B5CDE5;}
.graphbox .flash1{width:707px;height:441px;overflow:hidden;border-left:1px solid #B5CDE5;border-bottom:1px solid #B5CDE5;border-right:1px solid #B5CDE5;}
.graphbox .bmup{margin-top:-70px;padding:4px;line-height:30px;position:relative;}
.graphbox .bmup p{padding:0 20px;overflow:hidden;zoom:1;}
.graphbox .bmup p span{float:right;color:#666;*margin-top:-30px;}
.pos-img{position:absolute;top:30px;top:30px\9;*top:30px;_top:28px;left:-101px;}
.to10{margin-left:35px;_margin:3px 0 0 20px;_display:inline-block}
.gjyj-gray2{height:19px;overflow:hidden;display:inline;vertical-align:bottom;}
.bmup .fbg{background:#f4f8ff;padding-left:20px;}
.bmup .fbg .jrj-fr{width:247px;padding:2px 0 0 10px;}
.bmup .fbg .jrj-fr a{float:left;}
.bmup .fbg .jrj-fr .star{margin:8px 0 0 20px;}
.tit1{height:22px;background-position:0 -73px;border-bottom:1px solid #329cc6;padding:3px 10px 0;margin-bottom:2px;}
.tit1 span{float:right;*margin-top:-22px;}
.graphbox .ssbj{height:417px;width:230px;overflow:hidden;padding:25px 0 0}
.graphbox .ssbj .cont{height:385px;overflow:hidden;}
.graphbox .ssbj table{line-height:18px;text-align:center;margin:5px 0;_margin:3px 0;}
.graphbox .ssbj table,x:-moz-any-link, x:default {margin:3px 0;*margin:5px 0;}
.graphbox .ssbj th{background-position:0 -73px;border:1px solid #329cc6;border-width:1px 0;height:25px;line-height:25px;font-weight:bold;}
.mtab{height:30px;}
.mtab li{float:left;width:114px;line-height:30px;background-position:0 -321px;margin-right:2px;position:relative;bottom:-1px;font-size:14px;text-align:center;cursor:pointer}
.mtab li.hover{background-position:0 -280px;font-weight:bold;color:#000}
.mtab li a{color:#000;}
.mtab li.last{margin-right:0;}
.mtab li.tbw2{width:85px;}
.mtab li.tbw2 .tbw2{display:block;height:30px;background-position:0 -480px;}
.mtab li.hover .tbw2{background-position:0 -440px;}

.mtab2{height:26px;padding:2px 15px 0;}
.mtab2 li{width:98px;height:26px;line-height:26px;background-position:0 -400px;font-size:12px;}
.mtab2 li.hover{background-position:0 -360px;}
.nemu{height:30px;border-top:1px solid #b0cae3;background-position:0 -97px;}
.nemu li{float:left;width:75px;font-weight:bold;line-height:30px;text-align:center;border-right:1px solid #b0cae3;cursor:pointer;}
.nemu li.last{border:0;}
.nemu li.hover{background:#fff;}
.ssbj .tbh9{height:107px; overflow:hidden}
.ssbj .tit1{border-top:1px solid #329CC6; margin-top:3px; padding:1px 10px 2px;}
.ssbj .jltu{margin:7px 0 0 10px;}
.ssbj .pd5{padding:0;}
.ssbj .pd5 .jrj-tc{padding-top:20px;}
.ssbj .msmd{padding-bottom:15px;}
</style>
</head>
<body>
<div class="info">
	<div id="quote_con1"class="jrj-fl">
        <div class="mtit">
		<h2><a href="/share,900925.shtml" id="quote_nn"></a></h2><em class="jrj-f14">(上海：<a href="/share,900925.shtml" id="quote_code"></a>)</em>
		</div>
		<div class="num">
			<strong id="quote_np">--</strong>
			<p><b id="quote_hlp">--</b><b id="quote_pl">--</b></p>
			<p id="quote_time" class="time">--- ---</p>
		</div>
		<table class="data">
		<tr>
			<td>开盘：<em  id="quote_op" >--</em></td>
			<td>最高：<em class="jrj-red" id="quote_hp">--</em></td>
			<td>最低：<em class="jrj-gre" id="quote_lp">--</em></td>
			<td>市盈率：<em id="quote_ape">--</em></td>
		</tr>
		<tr>
			<td>成交：<em  id="quote_ta">--</em></td>
			<td>换手：<em  id="quote_tr">--</em></td>
			<td>主买：<em class="jrj-red" id="quote_outa">--</em></td>
			<td>市净率：<em  id="quote_apb">--</em></td>
		</tr>
		<tr>
			<td >前收：<em id="quote_lcp">--</em></td>
			<td >振幅：<em id="quote_sl">--</em></td>
			<td>主卖：<em class="jrj-gre" id="quote_inna" >--</em></td>
			<td>流通市值：<em id="quote_cwv">--</em></td>
		</tr>
		</table>
	</div>
	</div>
	<div class="jrj-w720">
    	<div class="graphbox">
        	<div class="jrj-fl">
<div class="flash" id="flash_div_sf">
 	  <div id="FlashHQSmall" class="fhi1"><span class="load2">数据加载中......</span></div>
</div>
        	</div>
        	<div class="jrj-fr ssbj" id="ssbjsf" style="display:block">
        						<ul id="real_trade"  class="mtab">
                	<li id="cj1" class="hover" for="five">实时报价</li>
                	<li id="cj2" class="last"  for="general">成交概况</li>
                </ul>
                <div id="real_trade_five"  class="cont">
                	<p class="tit1">机电B股[900925] <span id="real_trade_five_time">--:--:--</span></p>
                    <table id="real_trade_table" cellpadding="0" cellspacing="0" class="table">
                    <tbody>
                        <tr>
                            <td>卖⑤</td>
                            <td id="sell_five">--</td>
                            <td id="sell_five_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>卖④</td>
                            <td id="sell_four">--</td>
                            <td id="sell_four_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>卖③</td>
                             <td id="sell_three">--</td>
                            <td id="sell_three_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>卖②</td>
                             <td id="sell_two">--</td>
                            <td id="sell_two_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>卖①</td>
                              <td id="sell_one">--</td>
                            <td id="sell_one_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td><b>成交</b></td>
                            <td id="chengjiao" >--</td>
                            <td  class="jrj-tr"><b>现手 <span id="xianshou">--</span></b></td>
                        </tr>
                        <tr>
                            <td>买①</td>
                            <td id="buy_one">--</td>
                            <td id="buy_one_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>买②</td>
                           <td id="buy_two">--</td>
                            <td id="buy_two_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>买③</td>
                            <td id="buy_three">--</td>
                            <td id="buy_three_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>买④</td>
                            <td id="buy_four">--</td>
                            <td id="buy_four_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>买⑤</td>
                            <td id="buy_five">--</td>
                            <td id="buy_five_quantity" class="jrj-tr">--</td>
                        </tr>
                        <tr>
                            <td>委比</td>
                            <td id="weibi" >--</td>
                            <td id="liangbi" class="jrj-tr">量比 <span>--</span></td>
                        </tr>
                      </tbody>
                    </table>
                    <div id="detail_big" class="process tbh9" style="">
                        <table cellspacing="0" cellpadding="0" class="table">
                        <thead>
                          <tr>
                            <th>时间</th>
                            <th>成交价</th>
                            <th>成交量</th>
                            <th>状态</th>
                          </tr>
                          </thead>
                          <tbody>
                          </tbody>
                        </table>
                    </div>
                    <div id="detail_capital" class="process tbh9" style="display:none">
                        <table cellspacing="0" cellpadding="0" class="table">
                         <thead> 
                          <tr>
                            <th>时间</th>
                            <th>成交价</th>
                            <th>成交量</th>
                          </tr>
                         </thead>
                         <tbody>
                         </tbody>
                         </table>
                    </div>
                    <div id="detail_jlt" class="tbh9" style="display:none">
                    <p class="tit1">价量图（万手）</p>
                    <ul class="jltu" id="fenjia">
                    	<li id="fenjia_1">&nbsp;</li>
                    	<li id="fenjia_2">&nbsp;</li>
                    	<li id="fenjia_3">&nbsp;</li>
                     </ul>
                    </div>
                    <div id="detail_each" class="process tbh9" style="display:none">
                        <table cellspacing="0" cellpadding="0" class="table">  
                        <thead>            
                          <tr>
                            <th>时间</th>
                            <th>成交价</th>
                            <th>成交量</th>
                          </tr>
                          </thead>
                          <tbody>
                          </tbody>
                        </table>
                    </div>
                 	<ul id="detail" class="nemu">
                    	<li  class="hover" for="big" ><a href="http://stock.jrj.com.cn/share,900925,dadan.shtml">大单</a></li>
                    	<li  for="capital"><a href="http://stock.jrj.com.cn/share,900925,fenshi.shtml">分时</a></li>
                    	<li  for="jlt"><a href="http://stock.jrj.com.cn/share,900925,fenshi.shtml">价量</a></li>
                    	<li  class="last" for="each"><a href="http://stock.jrj.com.cn/share,900925,mingxi.shtml">明细</a></li>
                    </ul>
                </div>
                <div id="real_trade_general" class="cont pd5" style="display:none">
           			       <p class="jrj-tc"><img src="/stock/share/900925/nd/dd.png?_=2014-05-06 13:20:18" /></p>
          <div class="msmd">
	    	<p class="fc1">大单买入：0手 占0.0%</p>
		    <p class="fc2">大单卖出：0手  占0.0%</p>
			<p class="fc3">小单买入：1775手 占66.63%</p>
			<p class="fc4">小单卖出：851手 占31.93%</p>
		  </div>		  
		  <p class="tit1"><strong>散户跟风度</strong><span>2014-05-06 13:19:51</span></p>
					<div class="gfd">
						<ul>
							<li class="zlq"><span class=""></span>主力控制区</li>
							<li class="byq"><span class=""></span>博弈区</li>	
							<li class="shq"><span class=""></span>散户跟风区</li>		
						</ul>
						<p class="sm">
						 说明：目前的用户跟风数值为34.7%,						 
						 						 此数值说明此阶段的股价上涨可能是由主力与散户间的博弈引起						 						</p>
					</div>
                
            </div>
        </div>
        </div>
        
        <form  id="com_sbox_form">
		<span class="jrj-fr">
			<input id="com_combox_input" type="text" value="输入代码/简称/拼音" class="text" />
			<input id="com_combox_btn" type="button" value="查行情" class="btn" />
		</span>
	</form>
        
<script>
var stock_id="<%=request.getParameter("code")%>";
function isReady(){
    return true;
}
		var params = {   
		  AllowScriptAccess : "always",
		  wmode:"transparent",
		  allowFullScreen:"true"
		};
		var mySiteId="";
		var flashURL="/stock/stockFlash/normal_hq_main.swf";                 
		var _flashpara = stock_id+"|S|0";
		var ksio="1";
		swfobject.embedSWF(flashURL, "FlashHQSmall", "100%", "380", "9.0.0", "",{first:_flashpara,siteid:mySiteId,ksio:ksio},params)
		var sb={};
		var params2 = "&item=10&inc=utf8&otc=utf8&type=cn_sa"
		 //var params = "&item=10&type=hk"
		 var sb = new JRJ.ui.SearchBox("com_combox_input","com_combox_btn",com_combox_callback,params2,null,null,{
			 inputFocusClass: "sb-input-focus",
			 autoFocus: true,
			 clearInput: false,
			 hasHistory:false
			});
		sb.blurFunc = inputblur;
		
		var st=false;
		var codeUseAble=false;
		function inputblur (){  //一般blur事件不进行股票操作
			try{
				if(sb.slist!=undefined&&sb.slist.length>0&&st==false){
					st=true;
					codeUseAble=true;
					stock_callback(sb.slist[this.selectedIndex]);
				}
			}catch(e){}
			}
		function com_combox_callback(s1){
			codeUseAble=true;
			if(!st){
				stock_callback(sl[0]);
			}
		}
		function stock_callback(stock){
			stock_id=stock.code;
			location="/stock/stock.jsp?code="+stock.code;
		}
		</script>
</body>
</html>