function getFormatDateByLong(longDate) {
	var pattern = "yyyy-MM-dd";
	if (longDate == undefined) {
		return getFormatDate(new Date(), pattern)
	} else {
		return getFormatDate(new Date(longDate), pattern);
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