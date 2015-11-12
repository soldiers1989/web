/**
 * 
 */
package com.cfo.stock.web.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.stock.web.passport.utils.Constant;
import com.cfo.stock.web.passport.utils.Utility;
import com.cfo.stock.web.util.ActionUtils;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.helper.BrokerHelper;

/**
 * 验证港股是否需要输入密码
 * HkPasswordInterceptor
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014-11-26    		hui.mi     create  
 * ---------------------------------------------------------  
 * </PRE> 
 *
 */

public class HkPasswordInterceptor extends SpringMvcSessionInterceptor {
	
	private final static String ACCOUNTID="accountId";
	private final static String BRODERTYPE = "HK";
	private static final String PASS_REQUEST ="/stock/hk/accountId/hkPasswords.jspa";
	private static final String ERROR_REQUEST ="/stock/error.jspa";
	@Autowired
	protected UserAccountService userAccountService;
	@Autowired
	private BrokerHelper brokerHelper;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		try {
			if(handler instanceof HandlerMethod){
				//检查方法是否有该拦截器的注解
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				NeedHkPassword m_annotation = handlerMethod.getMethod()
						.getAnnotation(NeedHkPassword.class);
				NeedHkPassword c_annotation=handlerMethod.getBeanType().getAnnotation(NeedHkPassword.class);
				if(m_annotation==null&&c_annotation==null){
					return true;
				}
				//获取accountId
				long accountId = checkAccountId(request,response,handler);
				//判断券商，只有港股进行拦截
				if(!checkHKBroder(accountId)){
					return true;
				}
				//判断港股是否需要输入密码
				if(checkHKPassword(request,response,handler,accountId)){
					return true;
				}else{
					//弹出密码窗口
					String isajax = request.getHeader("x-requested-with");
					if(isajax != null){//ajax请求
						PrintWriter writer = response.getWriter();
						writer.println("{\"errMsg\":\"密码过期！\",\"hk_password_error\":-1}");
						writer.flush();
					}else{
						response.sendRedirect(PASS_REQUEST.replaceAll("accountId", accountId+"")+"?returnUrl="+URLEncoder.encode(ActionUtils.getCurrentURL(request), "UTF-8"));
					}
					return false;
				}
			}else{
				if(request.getRequestURI().startsWith("/stock")){
					response.sendRedirect(ERROR_REQUEST+"?redirect="+ActionUtils.getCurrentURL(request));
				}
				return false;
			}
		} catch (Exception e) {
			log.error("Methoid -- > HkPasswordInterceptor ==>",e);
		}
		return true;
	}
	/**
	 * 获取accountId
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	@SuppressWarnings("unchecked")
	private long checkAccountId(HttpServletRequest request, HttpServletResponse response,Object handler){
		//HandlerMethod handlerMethod = (HandlerMethod) handler;
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		Map<String, String> uriTemplateVars = 
				(Map<String, String>) webRequest.getAttribute(
						HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if(uriTemplateVars.containsKey(ACCOUNTID)){
			long accountId= (uriTemplateVars != null) ? Long.valueOf(uriTemplateVars.get(ACCOUNTID)) : 0;
			return accountId;
		}
		return 0;
	}
	/**
	 * 判断是否是港股 是港股  true
	 */
	private boolean checkHKBroder(long accountId){
		if(accountId == 0){
			return false;
		}
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		if(accUserInfo == null){
			return false;
		}else{
			MarketType type = brokerHelper.getBrokerType(accUserInfo
					.getBrokerId());
			if (type == null) {
				return false;
			} else {
				if (BRODERTYPE.equals(type.name())) {
					return true;
				}
			}
		}
		return false;
	}
    /**
     * 判断缓存中是否有密码
     */
	private boolean checkHKPassword(HttpServletRequest request, HttpServletResponse response,Object handler,long accountId){
		HttpSessionWrapper mysession=getSession(request,response);
		//如果session不存在，弹出层
		if(mysession == null){
			return false;
		}
		//从session中取中密码
		Map<String, Object> map=mysession.getAttributs();
		String hkPassword=(String)map.get(AttributeKeys.HK_PASSWORD+accountId);
		if(StringUtils.isBlank(hkPassword) || StringUtils.isEmpty(hkPassword)){
			return false;
		}
		return true;
	}
	
	/**
	 * 发送ajax响应给客户端
	 * 
	 * @param str
	 */
	public void sendAjax(String ContentType, String str,HttpServletResponse response) {
		try {
			ContentType = ContentType == null ? "text/html" : ContentType;
			response.setContentType(ContentType + "; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write(str + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	/**
	 * 登出方法
	 * @param session
	 * @param response
	 */
	@SuppressWarnings("unused")
	private void logOut(HttpSessionWrapper session, HttpServletResponse response){
		session.deleteSession();
		// 删除passport的cookie
		Utility.delCookie(Constant.ZQT_UID, Constant.ZQT_DOMAIN, response);
		Utility.delCookie(Constant.ZQT_MD5, Constant.ZQT_DOMAIN, response);
		// FIXME: 应该passport去删除cookie
		Utility.delCookie("JRJ.SSOPASSPORT", "jrj.com.cn", response);
	}
	
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface NeedHkPassword{
		boolean value() default true;
	}
	
}
