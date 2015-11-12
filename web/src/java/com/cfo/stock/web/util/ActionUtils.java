package com.cfo.stock.web.util;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cfo.stock.web.global.Global;
import com.jrj.common.utils.HttpUitl;

/**
 * Struts Action工具类
 * @author Administrator
 * @author weili.li
 */
public class ActionUtils
{
	private final static Logger log=Logger.getLogger(ActionUtils.class);
    private ActionUtils()
    {
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
				// 如果经过了多级代理,x-forwarded-for中有多个IP地址,则取第一个不为unknown的
				if (ip != null&&ip.indexOf(",") != -1) {
					String[] address = ip.split(",");
					ip = address[0];
					for (int i = 0; i < address.length; i++) {
						if (!"unknown".equalsIgnoreCase(address[i].trim())) {
							ip = address[i].trim();
							break;
						}
					}
				}
			return ip;
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
	 * @throws UnsupportedEncodingException 
	 **/
	public static void addCookie(String key, String content, String cdomin,
			Integer age, HttpServletResponse response)  {
		Cookie cuserId = new Cookie(key,content);
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
	 * 获取Cookie
	 * @param key
	 * @param request
	 * @return
	 */
	public static String getCookie(String key,HttpServletRequest request){
		Cookie cookie = HttpUitl.getCookie(request,
				key);
		if (cookie == null || cookie.getValue().length() <= 0) {
			return null;
		}
		return cookie.getValue();
	}
	
	public static  String getClientInfo(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		return userAgent;
	}
	
	/**
	 * 获得当前请求地址
	 * @param request http请求
	 * @return
	 */
	public static String getCurrentURL(HttpServletRequest request) {
		if(request.getQueryString()!=null){
			return Global.ZQT_SERVER + request.getRequestURI()+ "?" + request.getQueryString();
		}else{
			return Global.ZQT_SERVER + request.getRequestURI();
		}
	}
	/**
	 * 拼接地址
	 */
	public static String getURL(String url,Map<String,String> param){
		StringBuffer resultUrl = new StringBuffer();
		try {
			int num = 0;
			if(url != null){
				resultUrl.append(url);
				if(param != null && param.size()>0){
					Set<String> key = param.keySet();
			        for (Iterator it = key.iterator(); it.hasNext();) {
			        	if(num == 0){
			        		resultUrl.append("?");
			        	}else{
			        		resultUrl.append("&");
			        	}
			            String s = (String) it.next();
						resultUrl.append(s+"="+URLEncoder.encode(param.get(s), "utf-8"));
			            num++;
			        }
				}else{
					log.error("Method --> kaiHuUrl error oar.getParam() is null or size is 0");
				}
			}else{
				log.error("Method --> kaiHuUrl error oar.getOpenUrl() is null");
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Method --> getURL error");
		}
		return resultUrl.toString();
	}
	/**
	 * 获得当前请求地址,加refer参数。给统计专用
	 * @param request
	 * @param refer
	 * @return
	 */
	public static String getCurrentURL(HttpServletRequest request, String refer) {
		if(request.getQueryString()!=null){
			return Global.ZQT_SERVER + request.getRequestURI()+ "?" + request.getQueryString()+"&refer="+refer;
		}else{
			return Global.ZQT_SERVER + request.getRequestURI()+"?refer="+refer;
		}
	}
	/**
	 * 判断是否是AJAX
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request){
		String isajax   = request.getHeader("x-requested-with");
		if(isajax != null&&"XMLHttpRequest".equals(isajax)){
			return true;
		}
		return false;
	}
	
	/**
	 * 发送ajax响应给客户端
	 * 
	 * @param str
	 */
	public static void sendAjax(String ContentType, String str,HttpServletResponse response) {
		try {
			ContentType = ContentType == null ? "text/html" : ContentType;
			response.setContentType(ContentType + "; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write(str + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void sendXmlAjax(String str,HttpServletResponse response) {
		sendAjax("text/xml", str,response);
	}

	public static  void sendJavaScript(String str,HttpServletResponse response) {
		sendAjax("text/javascript", str,response);
	}

	public static void sendJsonAjax(String str,HttpServletResponse response) {
		sendAjax("application/json", str,response);
	}
	
	/**
	 * 返回HTTP请求的查询串
	 * @param request http request
	 * @return
	 */
	public static String getQueryString(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		Iterator<Entry<String, String[]>> it = parameterMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String[]> para = it.next();
			for(String value : para.getValue()) {
				if(!first) {
					sb.append("&"); // 非第一个串前加'&'号
				}
				sb.append(para.getKey()).append("=").append(value);
				first = false;
			}
		}
		
		return sb.toString();
	}
}
