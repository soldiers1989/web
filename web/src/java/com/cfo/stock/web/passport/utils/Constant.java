package com.cfo.stock.web.passport.utils;

import java.util.Calendar;
import java.util.Date;

import com.cfo.stock.web.global.Global;
import com.jrj.common.utils.PropertyManager;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class Constant {
	public static final int MAXPERSON_ADDNUM = 500;
	
	
	/** HTTP字头 **/
	public static final String HTTP = "http://";
	/**
	 * HTTPS字头
	 */
	public static final String HTTPS = "htpps://";
	
	/** 刷新周期 **/
	public  static final int REFRESH_PERIOD = 5;//刷新周期5秒
	
	/** SPID **/
	public static final String SPID = PropertyManager.getString("passport.zqt.spid");
	
	/** 加密的密钥 **/
	public static final String SSOID_KEY = PropertyManager.getString("passport.zqt.ticket");

	/** sso的cookiekey **/
	public static final String SSO_COOKIE_KEY = "JRJ.SSOUser";

	/**COOKIE中的用户ID的key值**/
	public static final String ZQT_UID=PropertyManager.getString("passport.cookie.user_id_key");
	
	/**COOKIE中的MD5用户ID的key值 **/
	public static final String ZQT_MD5=PropertyManager.getString("passport.cookie.user_md5_key");
	/**COOKIE超时时间**/
	public static final Integer ZQT_COOKIE_MAX_AGE =Integer.valueOf(PropertyManager.getString("passport.cookie.maxage"));
	/**
	 * 防刷时间
	 */
	public static final int ZQT_TIMEOUT=10;
	/**
	 * 证券通域名
	 */
	public static final String ZQT_DOMAIN=PropertyManager.getString("passport.zqt.domain");
	
	public static final String ZQT_SERVER=PropertyManager.getString("passport.zqt.server");
	/** 本地缓存 **/
	public static  GeneralCacheAdministrator admin = new GeneralCacheAdministrator();
	
	/** 本地缓存静态获得方法 **/
	public static GeneralCacheAdministrator getAdmin(){
		if(admin!=null)
		return admin;
		else 
	     admin=new GeneralCacheAdministrator();
		return admin;
		
	}
	
	/**
	 * 超时时间
	 * @return
	 */
	public static long getTimestamp(){
		 Calendar c = Calendar.getInstance();
		    c.setTime(new Date());
		    c.add(12, ZQT_TIMEOUT);
		    return c.getTimeInMillis()/1000;
	}
	
	public static final String ZQT_SESSIONID=PropertyManager.getString("xjb.web.sessionKey");
	
	public static final String ZQT_PASSPORT=PropertyManager.getString("passport.login.passport");
}
