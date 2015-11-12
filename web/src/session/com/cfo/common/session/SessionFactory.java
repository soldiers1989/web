package com.cfo.common.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.cfo.common.session.exception.InvalidSessionException;
import com.cfo.common.session.utils.Md5Utils;
import com.jrj.common.utils.PropertyManager;

/**
 * Session工厂
 * 
 * @author yuanlong.wang
 * 
 */
public class SessionFactory {
	public static final ThreadLocal<String> session = new ThreadLocal<String>();
	protected static Logger log = Logger.getLogger(SessionFactory.class);
	private static String sessionKey = "XJBSESIONID";

	private static String cookieDomain = "";

	private static String cookiePath = "/";

	private static int maxAge = -1;

	private static String authorizedKey = "xldxjbauth";

	private static int authorizedLength = 16;
	static {
		try {
			String key = PropertyManager.getString("xjb.web.sessionKey");
			if (key != null && key.trim().length() > 0) {
				sessionKey = key;
			}
			cookieDomain = PropertyManager.getString("xjb.web.cookieDomain");
			if (cookieDomain == null) {
				cookieDomain = "";
			}
			cookiePath = PropertyManager.getString("xjb.web.cookiePath");
			if (cookiePath == null || cookiePath.trim().length() == 0) {
				cookiePath = "/";
			}

			String authorized = PropertyManager
					.getString("xjb.web.authorizedKey");
			if (authorized != null && authorized.trim().length() > 0) {
				authorizedKey = authorized;
			}
			if (authorizedKey != null && authorizedKey.trim().length() > 0) {
				authorizedKey = Md5Utils.get16MD5String(authorizedKey);
			}
		} catch (Exception e) {
			log.info("Can not load session Config. Use Default.");
		}
	}

	private HttpServletResponse response;
	private HttpServletRequest request;
	private SessionFactory(HttpServletRequest request,
			HttpServletResponse response) {
		super();
		this.response = response;
		this.request = request;
	}

	public static SessionFactory getInstance(HttpServletRequest request,HttpServletResponse response){
		return new SessionFactory(request,response);
	}
	
	
	/**
	 * 获取当前sessionID
	 * 	如果cookie中没有 将会在 threadLocal中获取
	 * @return
	 */
	public String getCurrentSessionId() {
		String sessionId = getCurrentSessionIdCookie();
		if(StringUtils.isBlank(sessionId)){
			sessionId=session.get();
		}
		return sessionId;
	}
	
	public  void createSesionId(){
			String sessionId = buildSessionId();
			Cookie mycookies = new Cookie(sessionKey, sessionId);
			mycookies.setMaxAge(maxAge);
			mycookies.setDomain(cookieDomain);
			mycookies.setPath(cookiePath);
			response.addCookie(mycookies);
			session.remove();
			session.set(sessionId);
	}
	/**
	 * 获取当前sessionID Cookie
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public  String getCurrentSessionIdCookie() {
		Cookie cookies[] = request.getCookies();
		Cookie mycookie = null;
		String sessionId = "";
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				mycookie = cookies[i];
				if (sessionKey.equals(mycookie.getName())) {
					sessionId = mycookie.getValue();
					// break;
				}
			}
		}
		if (sessionId != null && sessionId.trim().length() != 0) {
			if (!checkSessionId(sessionId)) {
				Cookie mycookies = new Cookie(sessionKey, sessionId);
				mycookies.setMaxAge(0);
				mycookies.setDomain(cookieDomain);
				mycookies.setPath(cookiePath);
				response.addCookie(mycookies);
				throw new InvalidSessionException();
			}
		}
		return sessionId;
	}

	/**
	 * 创建新的sessionID
	 * 
	 * @return
	 */
	private static String buildSessionId() {
		String sessionId = java.util.UUID.randomUUID().toString().toUpperCase();
		if (authorizedKey != null && authorizedKey.length() > 0) {
			StringBuilder sb = new StringBuilder(60);
			sb.append(authorizedKey);
			sb.append("-");
			sb.append(sessionId);
			sessionId = sb.toString();
		}
		return sessionId;
	}

	/**
	 * 检验sessionID是否合法
	 * 
	 * @param sessionId
	 * @return
	 */
	private static boolean checkSessionId(String sessionId) {
		if (authorizedKey != null && authorizedKey.length() > 0) {
			if (sessionId.trim().length() < authorizedLength) {
				return false;
			}
			String key = sessionId.substring(0, authorizedLength);
			if (authorizedKey.equals(key)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取当前session
	 * @return
	 */
	public  HttpSessionWrapper getSession(){
		if(request.getSession() == null) throw new InvalidSessionException();
		
		String sessionId=getCurrentSessionId();
		HttpSessionWrapper session= new HttpSessionWrapper(sessionId, request.getSession());
		return session;
	}
}
