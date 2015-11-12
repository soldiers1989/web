var HGT_CONFIG={
	"CCZQ":"help-hgt-cczq.html",
	"ITN_CCZQ":"help-hgt-cczq.html", //长城证券
	"ITN_CFZQ":"help-hgt-cfzq.html", //财富证券
	"CFZQ":"help-hgt-cfzq.html",     //财富证券
	"ITN_DFZQ":"help-hgt-dfzq.html", //东方证券
	"DFZQ":"help-hgt-dfzq.html",
	"ITN_DHZQ":"help-hgt-dhzq.html",//东海证券
	"ITN_HAITZQ":"help-hgt-haitzq.html",//海通证券
	"HAITZQ":"help-hgt-haitzq.html",
	"ITN_HTZQ":"help-hgt-htzq.html",//恒泰证券
	"HTZQ":"help-hgt-htzq.html",
	"ITN_PAZQ":"help-hgt-pazq.html",//平安证券
	"PAZQ":"help-hgt-pazq.html",
	"ITN_XNZQ":"help-hgt-xnzq.html",//西南证券
	"XNZQ":"help-hgt-xnzq.html",
	"ITN_XSDZQ":"help-hgt-xsdzq.html",//新时代证券
	"ZJZQ":"help-hgt-zjzq.html",//中金证券
	"ITN_ZJZQ":"help-hgt-zjzq.html",//中金证券
	"ZSZQ":"help-hgt-zszq.html",//中山证券
	"ITN_ZSZQ":"help-hgt-zszq.html",//中山证券
	"ZXZQ":"help-hgt-zxzq.html",//中信证券
	"ITN_ZXZQ":"help-hgt-zxzq.html",//中信证券
};
$(function(){
	$(".openHgt").each(function(k,v){
		var brokerId = $(this).data("brokerid");
		//alert('brokerId-->'+brokerId);
		if(HGT_CONFIG[brokerId]!=undefined){
		var href = "http://t.jrj.com.cn/khlc/"+HGT_CONFIG[brokerId];
		//alert('href-->'+href);
		$(this).attr("href",href).show();
		}
	})
})
