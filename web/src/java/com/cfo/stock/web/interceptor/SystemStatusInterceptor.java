/**
 * 
 */
package com.cfo.stock.web.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.pub.SystemStatusQueryService;

/**
 *
 *获取券商列表
 *
 */

public class SystemStatusInterceptor extends SpringMvcSessionInterceptor {
	private static final String GO_ERROR ="/stock/sysError.jspa";

	private static final String ACCOUNTID = "accountId";

	@Autowired
	SystemStatusQueryService systemStatusQueryService;

	@Autowired
	private UserAccountService userAccountService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("===i'm in=="+request.getContextPath() +"系统不可用");
		}
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			GetSysStatus m_annotation = handlerMethod.getMethod()
					.getAnnotation(GetSysStatus.class);
			
			GetSysStatus c_annotation=handlerMethod.getBeanType().getAnnotation(GetSysStatus.class);
		
			if(m_annotation==null&&c_annotation==null){
				return true;
			}
			
			Long accountId=null;
			ServletWebRequest webRequest = new ServletWebRequest(request, response);
			@SuppressWarnings("unchecked")
			Map<String, String> uriTemplateVars = 
					(Map<String, String>) webRequest.getAttribute(
							HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
			if(uriTemplateVars.containsKey(ACCOUNTID)){
				accountId= (uriTemplateVars != null) ? Long.valueOf(uriTemplateVars.get(ACCOUNTID)) : null;
			}
			
			boolean flag = checkSystemStatus(accountId);
			if(flag){
				return true;
			}else{	
				response.sendRedirect(GO_ERROR);
				return false;
			}			
		}else{
			if(request.getRequestURI().startsWith("/stock")){
				response.sendRedirect(GO_ERROR);
			}
			return false;
		}
	}
	/**
	 * 添加默认券商状态
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	private boolean checkSystemStatus(Long accountId){
		if(log.isDebugEnabled()){
			log.debug("检测是否有选中的券商");
		}
		if(accountId!=null){
			UserAccount accUserInfo = userAccountService.queryAccount(accountId);
			return systemStatusQueryService.isSystemAviable(accUserInfo.getBrokerId());
		}else{
			return false;
		}
	}

	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface GetSysStatus{
		boolean value() default true;
		String noBrokerUrl() default GO_ERROR;
	}
	
}
