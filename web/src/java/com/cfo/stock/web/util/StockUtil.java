package com.cfo.stock.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.time.DateUtils;

import com.alibaba.fastjson.JSONObject;
import com.jrj.common.utils.DateUtil;
import com.jrj.common.velocity.utils.toolbox.DateUtilsToolbox;


public class StockUtil {
	public static final String format_1 = "yyyy年MM月dd日";
	public static final String format_2 = "yyyy-MM-dd HH:mm:ss";
	public static final String format_3 = "yyyy-MM-dd";
	public static String getNowDate(String dateString){
		if("".equals(dateString)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return  sdf.format(new Date());
		}
		return dateString;
	}
	
	public static String formatDate(long time){
		try{
			Date date = new Date(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return sdf.format(date);
		}catch(Exception e){
			return "00000000";
		}
	}
	
	public static String formatDate(Date date,String strFormat){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
			return sdf.format(date);
		}catch(Exception e){
			return "00000000";
		}
	}
	
	public static Date formatDate(String time,String format){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(time);
		}catch(Exception e){
			return new Date();
		}
	}
	/**
	 * 判断两个时间是否在某区间内
	 * @param startDate
	 * @param endDate
	 * @param dateCount
	 * @return
	 */
	public static boolean compareDate(Date startDate,Date endDate,int dateCount){
		long count = dateCount*24*60*60*1000l;
		long _count = endDate.getTime() - startDate.getTime();
		if(_count > count || _count <0){
			return false;
		}
		return true;
	}
	/**
	 * 用于flash 显示，将 例如“20140506”时间 ，转化得到“05/06”
	 * @param date
	 * @return
	 */
	public static String getDate(String date){
		String rs ="";
		try{
			rs = date.substring(4, 6) +"/"+ date.subSequence(6, 8);
		}catch(Exception e){
			rs ="--/--";
		}		
		return rs;
	}
	
	/**
	 * 对core返回的错误如 [12456][ssssss]的错误处理 (目前没有统一错误格式，全放开)
	 * @param errorInfo
	 * @return
	 */
	public static String getErrorInfo(String errorInfo){
		return errorInfo;
		/*try{
			if(errorInfo.startsWith("[")){
				return  "[" + errorInfo.split("]")[0].trim().substring(1) +"] ["+errorInfo.split("]")[1].trim().substring(1)+"]";
			}else{
				return "[" + errorInfo.split("\\[")[0].trim() +"]";
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return errorInfo;
		}*/
	}
	//随机生成8位密码
	public static String generatePassword(int length)
	{     
	    String val = "";     
	             
	    Random random = new Random();     
	    for(int i = 0; i < length; i++)     
	    {     
	        String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字     
	                 
	        if("char".equalsIgnoreCase(charOrNum)) // 字符串     
	        {     
	            int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母     
	            val += (char) (choice + random.nextInt(26));     
	        }     
	        else if("num".equalsIgnoreCase(charOrNum)) // 数字     
	        {     
	            val += String.valueOf(random.nextInt(10));     
	        }     
	    }     
	             
	    return val;     
	}
	//查询时间是否在否一时间段内
	public static boolean judgeTimeBetween(Date someTime,String startTime,String endTime){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format_2);
			return judgeTimeBetween(someTime,sdf.parse(startTime),sdf.parse(endTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	//查询时间是否在否一时间段内
	public static boolean judgeTimeBetween(Date someTime,Date startTime,Date endTime){
		if ((startTime.before(someTime)) && (someTime.before(endTime))){
			return true;
		}else{
			return false;
		}
	}
	//将字符转成Date类型
	public static Date getDateStringToDate(String dateTime){
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(dateTime);
		}
		catch (ParseException e){
			return null;
		}
	}
	
	/**
	 * 返回指定日期距离今天有几天的字符串
	 * @param date
	 * @return
	 */
	public static String howLongFromToday(Date date){
		Date today = new Date();
		long dif = today.getTime() - date.getTime();
		int re = (int)Math.ceil(dif/(24*3600*1000));
		if(re == 0){
			return "今天";
		}else{
			return re + "天前";
		}
	}
	
	public static void main(String[] args){
		Date dates = getDateStringToDate("2015-1-7");
		System.out.println(formatDate(dates,format_1));
		System.out.println(howLongFromToday(dates));
	}
}
