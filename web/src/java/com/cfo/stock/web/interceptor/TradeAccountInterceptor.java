package com.cfo.stock.web.interceptor;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.stock.web.util.ActionUtils;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.exception.ServiceException;

/**
 *   验证action 必须选中一个默认账户
 * TradeAccountInterceptor
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014-10-13    		yuan.cheng     create  
 * ---------------------------------------------------------  
 * </PRE> 
 *
 */

public class TradeAccountInterceptor extends SpringMvcSessionInterceptor {
	private static final String NO_ACCOUNT="/stock/noAccount.jspa";
	private static final String ERROR_REQUEST ="/stock/error.jspa";
	@Autowired
	AccountService accountService;
	@Autowired
	UserAuthService userAuthService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("===i'm in=="+request.getContextPath() +"是否有默认账户");
		}
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			TradeAccount m_annotation = handlerMethod.getMethod()
					.getAnnotation(TradeAccount.class);
			
			TradeAccount c_annotation=handlerMethod.getBeanType().getAnnotation(TradeAccount.class);
		
			if(m_annotation==null&&c_annotation==null){
				return true;
			}
			boolean flag = checkAccount(request,response);
			if(log.isDebugEnabled()){
				log.debug("===broker flag =="+ flag);
			}
			if(flag){
				return true;
			}else{
					if(request.getQueryString() != null){
						response.sendRedirect(NO_ACCOUNT+"?redirect=" + URLEncoder.encode(ActionUtils.getCurrentURL(request), "utf-8"));
					}else{
						response.sendRedirect(NO_ACCOUNT);
					}
				return false;
			}			
		}else{
			if(request.getRequestURI().startsWith("/stock")){
				if(request.getQueryString()!=null){
					response.sendRedirect(ERROR_REQUEST+"?redirect="+request.getRequestURI()+"?" + request.getQueryString());
				}else{
					response.sendRedirect(ERROR_REQUEST);
				}
			}
			return false;
		}
	}
	/**
	 * 检查默认账户状态
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	private boolean checkAccount(HttpServletRequest request, HttpServletResponse response){
		log.debug("检测是否有默认可操作账户");
		HttpSessionWrapper session=getSession(request,response);
		if(session == null){
			return false;
		}
		String userID = session.getAttribute(AttributeKeys.USER_ID, String.class);
		log.info("### CHECKING_IP_USERID: " + ActionUtils.getRemoteIpAdress(request) + " = [" + userID + "] ###");
		
		try {
			List<UserAccAuth> userList = userAuthService.queryAccessAble(userID);
			if(userList == null || userList.size() == 0){//没有默认账户，返回false，不通过。
				return false;
			}
			UserAccAuth user = userList.get(0);
			if(user == null){
				return false;
			}
		} catch (ServiceException e) {
			log.error("exception",e);
			return false;
		}
		return true;
	}

	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TradeAccount{
		boolean value() default true;
		String noBrokerUrl() default NO_ACCOUNT;
	}
	
}
