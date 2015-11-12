var optv = {
	callback : funCallback
};
optv["items_per_page"] = 10;
optv["prev_text"] = '上一页';
optv["next_text"] = '下一页';
optv["num_display_entries"] = 6;
optv["num_edge_entries"] = 2;

function funCallback(page_index, jq) {
	var page = page_index + 1;
	var beginTime = $("#beginTime").val().replace(/\-/g, '');
	var endTime = $("#endTime").val().replace(/\-/g, '');
	var data = "{startDate:'" + beginTime + "',endDate:'" + endTime + "',pn:"
			+ page + "}";
	choosefun(eval('(' + data + ')'));
	return false;
}

function choosefun(datap) {
	// 交易记录
	$.ajax({
		type : "POST",
		url : "/stock/ajaxhistoryBusiness.jspa",
		data : datap,
		dataType : "json",
		success : function(data) {
			var list_hb = data.HistoryBusiness;
			var str = "";
			for (var i = 0; i < list_hb.length; i++) {
				var funattr = "<tr>" + "<td>"
						+ getFormatDateByLong(list_hb[i]["businessTime"])
						+ "</td>" + "<td>" + list_hb[i]["stockCode"] + "</td>"
						+ "<td>" + list_hb[i]["stockName"] + "</td>" + "<td>"
						+ list_hb[i]["entrustBs"] + "</td>" + "<td>"
						+ list_hb[i]["occurAmount"] + "</td>" + "<td>"
						+ list_hb[i]["occurBalance"] + "</td>" + "<td>"
						+ list_hb[i]["businessBalance"] + "</td>" + "<td>"
						+ list_hb[i]["businessStatus"] + "</td>" + "</tr>";
				str += funattr;
			}
			$("#tradeTable tbody").html("").html(str);
			if (datap == undefined || datap.pn == undefined) {
				$("#Pagination").pagination(parseInt(data.totalsize), optv);
			}
		}
	});
}

/**
 * 转换long值为日期字符串
 * 
 * @param l
 *            long值
 * @param pattern
 *            格式字符串,例如：yyyy-MM-dd hh:mm:ss
 * @return 符合要求的日期字符串
 */

function getFormatDateByLong(l) {
	var pattern = "yyyy-MM-dd";
	if (l == undefined) {
		return "--";
	} else {
		return getFormatDate(new Date(l), pattern);
	}

}
/**
 * 转换日期对象为日期字符串
 * 
 * @param l
 *            long值
 * @param pattern
 *            格式字符串,例如：yyyy-MM-dd hh:mm:ss
 * @return 符合要求的日期字符串
 */
function getFormatDate(date, pattern) {
	if (date == undefined) {
		date = new Date();
	}
	if (pattern == undefined) {
		pattern = "yyyy-MM-dd hh:mm:ss";
	}
	return date.format(pattern);
}
$(function() {
	// page load
	choosefun();

	$(".search-buy-btn").click(function() {
		$("#tradeTable tbody").html("");
		$("#Pagination").html("");
		var beginTime = $("#beginTime").val().replace(/\-/g, '');
		var endTime = $("#endTime").val().replace(/\-/g, '');
		var data = "{beginTime:'" + beginTime + "',endTime:'" + endTime + "'}";
		choosefun(eval('(' + data + ')'));

	});

	var s = new JRJ.ui.Calendar("beginTime", {
		yearRange : [ 2013, 2099 ],
		validType : 'blur',// 'blur','input'
		triggerType : "focus",
		onPicked : function(s) {
		}
	});

	var e = new JRJ.ui.Calendar("endTime", {
		yearRange : [ 2013, 2099 ],
		validType : 'blur',// 'blur','input'
		triggerType : "focus",
		onPicked : function(d) {
		}
	});
})