package com.cfo.stock.web.passport.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Utility {
	private static Log log = LogFactory.getLog(Utility.class);

	/**
	 * 截取字符串从字符末尾
	 * @param str
	 * @param begin
	 * @return
	 */
	public static String splitStringByStringToEnd(String str, String begin) {
		return str.substring(str.indexOf(begin));
	}

	/**
	 * 添加cookie;
	 **/
	public static void addCookie(String key, String content, String cdomin,
			HttpServletResponse response) {
		addCookie(key, content, cdomin, null, response);
	}

	/**
	 * 添加cookie;可设置超时
	 **/
	public static void addCookie(String key, String content, String cdomin,
			Integer age, HttpServletResponse response) {
		Cookie cuserId = new Cookie(key, content);
		if (age != null) {
			cuserId.setMaxAge(age);
		}
		if (log.isDebugEnabled()) {
			log.debug(cdomin);
		}
		cuserId.setPath("/");
		cuserId.setDomain(cdomin);
		response.addCookie(cuserId);
	}

	/**
	 * 删除cookie
	 * @param key
	 * @param cdomin
	 * @param response
	 */
	public static void delCookie(String key, String cdomin,
			HttpServletResponse response) {
		Cookie cuserId = new Cookie(key, null);
		cuserId.setMaxAge(0);
		cuserId.setPath("/");
		cuserId.setDomain(cdomin);
		response.addCookie(cuserId);
	}

	/**
	 * 获得客户端IP
	 * 
	 * @return String
	 */
	public static String getRemoteIpAdress(HttpServletRequest request){

			// 如果通过反向代理访问的服务器,则先取x-forwarded-for的header
			String ip =request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getHeader("WL-Proxy-Client-IP");
				}
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getRemoteAddr();
				}
//				// 否则返回J2EE的地址
//				return request.getRemoteAddr();
			}			
			else {
				// 如果经过了多级代理,x-forwarded-for中有多个IP地址,则取第一个不为unknown的
				if (ip.indexOf(",") != -1) {
					String[] address = ip.split(",");
					ip = address[0];
					for (int i = 0; i < address.length; i++) {
						if (!"unknown".equalsIgnoreCase(address[i].trim())) {
							ip = address[i].trim();
							break;
						}
					}
				}
			}
			return ip;
	}
}
