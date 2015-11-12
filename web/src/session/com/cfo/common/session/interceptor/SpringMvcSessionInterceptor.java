/**
 * 
 */
package com.cfo.common.session.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.SessionFactory;

/**
 * 
 * @author yuanlong.wang 2013-4-25
 * 
 */

public abstract class SpringMvcSessionInterceptor extends HandlerInterceptorAdapter {
	protected Logger log = Logger.getLogger(getClass());

	protected String errorUrl;
	/**
	 * 获取当前sessionID
	 * 
	 * @return
	 */
	protected String getCurrentSessionId(HttpServletRequest request,
			HttpServletResponse response) {
		return SessionFactory.getInstance(request, response)
				.getCurrentSessionId();
	}

	/**
	 * 获取当前session
	 * 
	 * @return
	 */
	protected HttpSessionWrapper getSession(HttpServletRequest request,
			HttpServletResponse response) {
		return SessionFactory.getInstance(request, response).getSession();
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}
	
}
