package com.cfo.stock.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfo.stock.web.exception.NeedLoginException;
import com.cfo.stock.web.global.Global;
import com.jrj.common.utils.HttpUitl;
import com.jrj.stocktrade.api.common.BrokerId;

public class ZQTUtils {
	private static Log log = LogFactory.getLog(ZQTUtils.class);

	/**
	 * 获得金融界当前登录用户ID(可能需要nginx module支持).
	 * 先检查当前Classpath下有无adduser.properties,如有从中提取用户ID(for development).
	 * 从cookie中检查myjrj_userid的cookie并进行验证,从中读取当前用户ID(for production).
	 * 
	 * @return
	 * @throws NeedLoginException
	 */
	public static String getSsoUserId(HttpServletRequest request) throws NeedLoginException {
		String userid;
		//Cookie useridCookieDev = HttpUitl.getCookie(request,
				//Global.COOKIE_USER_ID_KEY);
		//if (useridCookieDev == null || useridCookieDev.getValue().length() <= 0) {
			//调试使用
			if("1".equals(Global.DEBUG_SSO) ) {
				try {
					InputStream in = ZQTUtils.class.getClassLoader().getResourceAsStream("debugsso.properties");
					Properties p = new Properties();
					if (in != null) {
						p.load(in);
						in.close();
					}
					userid = p.getProperty("ssoid");
					log.info("get userid:" + userid );
					if (userid == null) {
						throw new NeedLoginException();
					}
					return userid;
				} catch (IOException e) {
					log.error("IO错误", e);
				}
			}//调试使用end
		//}
		String loginOk = request.getHeader(Global.COOKIE_LOGIN_OK_KEY);
		log.info("loginCookie:"+loginOk);
		
		if (loginOk == null) {
			throw new NeedLoginException();
		} else {
			Cookie useridCookie = HttpUitl.getCookie(request,
					Global.COOKIE_USER_ID_KEY);
			if (useridCookie == null || useridCookie.getValue().length() <= 0) {
				throw new NeedLoginException();
			} else {
				userid = useridCookie.getValue();
				log.info("get userid:" + userid);
				return userid;
			}
		}
	}
	
	/** 
	 * 方法名称:transMapToString 
	 * 传入参数:map 
	 * 返回值:String 形如 username=chenziwen&password=1234 
	*/  
	public static String transMapToString(Map map){  
		if(map!=null && map.size()>0){
			NameValuePair[] data = new NameValuePair[map.size()]; 
			Set set = map.entrySet();
	        Iterator iterator = set.iterator(); 
	        int i=0;  
	        while (iterator.hasNext()) {  
	            Map.Entry entry = (Map.Entry) iterator.next();
	            data[i]=new NameValuePair((String)entry.getKey(),(String)entry.getValue());  
	            i++;  
	        }  
			return EncodingUtil.formUrlEncode(data, "UTF-8");
		}
		return "";
	} 
	
	/**
	 * 获取券商页面展现方式
	 * @param broker
	 * @return
	 */
	public static WebTradeType webTradeType(String broker){
		if(BrokerId.ZSZQ_SECURITIES.equals(broker))
			return WebTradeType.IFRAME;
		else
			return WebTradeType.ORIGINAL;
	}
	
	public static enum WebTradeType{
		IFRAME,//iframe 方式 
		ORIGINAL//原生页面方式
	}
}
