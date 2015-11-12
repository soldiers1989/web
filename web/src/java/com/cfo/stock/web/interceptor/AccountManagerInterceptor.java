/**
 * 
 */
package com.cfo.stock.web.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;

/**
 * 账户登录验证
 *
 * @Title:AccountManagerInterceptor.java
 * @Package:com.cfo.stock.web.interceptor
 * @Copyright: Copyright(c)2004-2014
 * @Company:中国金融在线集团
 *
 * @author: wangzhao(王昭)
 * @date: Jan 27, 2015 3:58:17 PM
 * @mail: zhao.wang01@jrj.com.cn
 * @vision: V1.0
 */

public class AccountManagerInterceptor extends SpringMvcSessionInterceptor {
	private static final String COOKIE_NAME="channelCode";
	private static final String COOKIE_DOMAIN="jrj.com.cn";
	private static final String URL_PARA_TGQD_CODE="tgqdcode";
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		String tgqdcode = request.getParameter(URL_PARA_TGQD_CODE);
		log.info("tgqdcode="+tgqdcode);
		if(StringUtils.isNotBlank(tgqdcode)){
			Cookie cookie = new Cookie(COOKIE_NAME,tgqdcode);
			cookie.setMaxAge(24 * 60 * 60 * 3);
			cookie.setPath("/");
			cookie.setDomain(COOKIE_DOMAIN);
			response.addCookie(cookie);
		}
		return true;
	}

	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface AccountManager{
		boolean value() default true;
	}
	
}
