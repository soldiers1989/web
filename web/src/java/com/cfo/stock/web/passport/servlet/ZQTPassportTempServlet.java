package com.cfo.stock.web.passport.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.SessionFactory;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.passport.utils.Constant;
import com.cfo.stock.web.passport.utils.Utility;
import com.cfo.stock.web.util.ActionUtils;
import com.jrj.sso.ws.SSOProxy;
import com.jrj.sso.ws.SSOProxyServiceClient;
import com.jrj.sso.ws.SsoToken;

/**
 * 
 * ZQTPassportServlet 临时解决方案
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014年10月29日    	yuanlong.wang     create  
 * ---------------------------------------------------------  
 * </PRE> 
 *
 */
public class ZQTPassportTempServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4044511624612782173L;
	protected Log log=LogFactory.getLog(getClass());
	protected static final String PARAM_BACKURL = "backUrl";
	protected static final String cdomin = Constant.ZQT_DOMAIN;
//	protected String FIRSTPAGE_URL = "/404.html";
	private static final String LOGIN_RESULT = Global.PASSPORT_LOGIN_URL;
	
	private static final String PARAM_TICKET = "ticket";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String remoteaddr = Utility.getRemoteIpAdress(req);
		String referer = req.getHeader("Referer");
		String cookieString = req.getHeader("Cookie");
		String sessionId= SessionFactory.getInstance(req, resp).getCurrentSessionId();
		/**
		 * 第一步：验证是否有ssocookie 开始
		 */
//		String ssocookie = null;
//		for (Cookie cookie : req.getCookies()) {
//			if (Constant.SSO_COOKIE_KEY.equals(cookie.getName())) {
//				ssocookie = cookie.getValue();
//			}
//		}
//		log.info(ssocookie);
//		if (ssocookie == null) {
//			toErrorPage("ssocookie not find!  ip:" + remoteaddr + " referer:"
//					+ referer, resp);
//			return;
//		}
		// 验证结束

		/**
		 * 第二步：获取ticket 开始
		 */
		String ticket;
		String backUrl = getBackUrl(req);
		ticket = req.getParameter(PARAM_TICKET);

		// 过滤ticket
		// http://x.jrj.com.cn/main/index.jspa?ticket=dcL70skiZhq10cRy
		int querystart = backUrl.indexOf("?");
		String queryString = "";
		if (querystart >= 0) {
			String tmpstr = backUrl.substring(querystart + 1);
			String[] pars = tmpstr.split("&");
			if (pars != null) {
				for (String par : pars) {
					if (par.indexOf("ticket=") < 0) {
						queryString += par + "&";
					}
				}
				// 去掉最后的&
				if (queryString.length() > 0) {
					queryString = queryString.substring(0,
							queryString.length() - 1);
				}
			}
			if (queryString.length() > 0) {
				backUrl = backUrl.substring(0, querystart) + "?" + queryString;
			} else {
				backUrl = backUrl.substring(0, querystart);
			}
		}
		log.info(ticket);
		log.info(backUrl);
		if (ticket == null) {
			log.info("ticket is null! ip:" + remoteaddr + " referer:"
					+ referer);
			sendRedirect(resp, LOGIN_RESULT+"?SPID=t&ReturnURL="+ActionUtils.getCurrentURL(req));
			return;
//			toErrorPage("ticket is null! ip:" + remoteaddr + " referer:"
//					+ referer, resp);
//			return;
		}
		// 获取ticket 结束

		/**
		 * 第三步：根据ticket 获取SSO用户信息 开始
		 */

		String t = DigestUtils.md5Hex(ticket + Constant.SSOID_KEY);
		SSOProxy sso = new SSOProxyServiceClient().getSSOProxyPort();
		log.info("Hex value:" + t);
		SsoToken ssoToken = sso.findUserByTicket(t, Constant.SPID);
		log.info("SsoTokenCode:" + ssoToken.getReturnCode());
		// 根据ticket 获取SSO用户信息 结束
		if (!"error".equals(ssoToken.getReturnCode())) {
			String ssoid = ssoToken.getUserID();
			String userName = ssoToken.getName();
			log.info("Username:" + userName);
			/**
			 * 第四步：防止没种上COOKIE 用本地缓存做3次验证
			 */
			if (!checkRequestCount(resp, backUrl, ssoid)) {
				log.info("retry " + CHECK_COUNT + " per ! ip:" + remoteaddr
						+ " referer:" + referer + " cookies:" + cookieString);
//				toErrorPage("retry " + CHECK_COUNT + " per ! ip:" + remoteaddr
//						+ " referer:" + referer + " cookies:" + cookieString,
//						resp);
				sendRedirect(resp, LOGIN_RESULT+"?SPID=t&ReturnURL="+ActionUtils.getCurrentURL(req));
				return;
			}

			/**
			 * 第五步 种cookie
			 */
			makeCookie(resp, ssoid, sessionId);
			
			/**
			 * 第六步：跳转
			 */
			sendRedirect(resp, backUrl);
		} else {
			log.info("SsoTokenCode:ERROR! Redirect TO Home!");
			sendRedirect(resp, LOGIN_RESULT+"?SPID=t&ReturnURL="+ActionUtils.getCurrentURL(req));
		}
	}
	
	/**
	 * 种cookie
	 * @param resp
	 * @param identiy
	 * @param cdomin
	 * @param win_uid
	 */
	protected void makeCookie(HttpServletResponse resp,String ssoid, String ssocookie) {
		String ssiodMd5 = DigestUtils.md5Hex(ssoid + ssocookie+Constant.SSOID_KEY);
        Utility.addCookie(Constant.ZQT_UID, ssoid, Constant.ZQT_DOMAIN,null, resp);        
        Utility.addCookie(Constant.ZQT_MD5, ssiodMd5,  Constant.ZQT_DOMAIN,null, resp);

	}
	
	protected String getBackUrl(HttpServletRequest request){
		return request.getParameter(PARAM_BACKURL);
	}
}
