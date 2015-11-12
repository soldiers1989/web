package com.cfo.stock.web.velocity.toolbox;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cfo.stock.web.velocity.data.DataServiceTool;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.stock.vo.StockInfo;

public class StockUtilsToolbox {
	
	public String getBrokerImage(String brokerId){
		String imagePath="/stock/brokerImages/"+brokerId+".png";
		return imagePath;
	}
	public String formatDate(Long longTime,String format){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = new Date(longTime);
			return sdf.format(date);
		}catch(Exception e){
			return "-- --";
		}		
	}
	public String urlDecode(String url){
		String path = "";
		if(StringUtils.isNotBlank(url)){
			
			try {
				path = URLDecoder.decode(url,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		
		}
		return path;
	}
	//用户见查询类的时间 转换成  “时：分：秒” 格式
	public String longTime2String(String time){
		try{
			String m = time.substring(time.length()-2);
			String s = time.substring(time.length()-4, time.length()-2);
			String h = time.substring(0,time.length()-4);
			return h+":"+s+":"+m;
		}catch(Exception e){
			return "-- --";
		}
		
	}
	public String longDate2String(String date){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date2 = sdf.parse(date);
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			return sdf2.format(date2);
		}catch(Exception e){
			return "";
		}
		
	}
	public String longDate2StringMD(String date){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date2 = sdf.parse(date);
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
			return sdf2.format(date2);
		}catch(Exception e){
			return "";
		}
		
	}
	/**
	 * 获取券商类型
	 * @param brokerId
	 * @return
	 */
	public MarketType brokerType(String brokerId){
		return DataServiceTool.getBrokerHelper().getBrokerType(brokerId);
	}
	
	public String brokerStr(String brokerId){
		return DataServiceTool.getBrokerHelper().getBrokerName(brokerId);
	}
	
	public BigDecimal calIncomeRate(StockInfo stock){
		//盈亏比例= 浮动盈亏  / (成本价 * 持有数量)
		 if(stock.getCurrentAmount().equals(BigDecimal.ZERO)||BigDecimal.ZERO.equals(stock.getCostPrice())){
			 return null;
		 }

		BigDecimal rs = stock
				.getIncomeBalance().multiply(new BigDecimal(100))
				.divide(stock.getCurrentAmount().multiply(stock.getCostPrice()),
						2, RoundingMode.HALF_UP);
		return rs;
	}
}
