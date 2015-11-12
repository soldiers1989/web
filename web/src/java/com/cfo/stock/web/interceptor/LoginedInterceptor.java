/**
 * 
 */
package com.cfo.stock.web.interceptor;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

import com.cfo.common.enums.UserInfoStatus;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.constant.SessionConfig;
import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.common.session.utils.CookieUtils;
import com.cfo.common.session.utils.URLUtils;

/**  
 * 
 * @author yuanlong.wang 2013-7-11 
 *  
 */

public class LoginedInterceptor extends SpringMvcSessionInterceptor{
	private final static String LOGINED="_logined";
	private static String IC = "_ic";
	/*@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		
	}*/
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("===i'm in=="+request.getRequestURI());
		}
		HttpSessionWrapper session=getSession(request,response);
		if(session!=null){
			HttpSessionWrapper mysession=session;
			Map<String, Object> map=mysession.getAttributs();
			String from=request.getParameter("from");
			from = URLUtils.ParamterFilter(from,'\0');
			if(log.isDebugEnabled()){
				log.debug("get from in the logined interceptor ==>"+from);
			}
			String inviteCode=request.getParameter("ic");
			if(log.isDebugEnabled()){
				log.debug("get inviteCode in the logined interceptor ==>"+inviteCode);
			}
			inviteCode = URLUtils.ParamterFilter(inviteCode,'\0');
			if(StringUtils.isNotBlank(inviteCode)){
				mysession.setAttribute(AttributeKeys.INVITE_CODE, inviteCode);
			}else{
				String inner_ic = CookieUtils.getCookieByName(request,IC);
				if(StringUtils.isNotBlank(inner_ic)){
					mysession.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
				}else{
					Object session_ic = mysession.getAttribute(AttributeKeys.INVITE_CODE);
					if(session_ic!=null){
						CookieUtils.updateCookieByName(response,IC,(String)session_ic);
					}
				}
			}
			mysession.setAttribute(AttributeKeys.AD_FROM, from);
			//检验是否有登录标示
			//检查用户信息是否完全，如果不完全则允许访问
			String login=(String)map.get(AttributeKeys.LOGIN);
			String status=(String)map.get(AttributeKeys.USER_INFO_STATUS);
			if((login==null||!SessionConfig.LOGIN_OK.equals(login)) && !UserInfoStatus.FULL.status.equals(status)){
				return true;
			}
			
			//检查当前是否过期
			long lastaccesstime=mysession.getLastAccessedTime();
			long now=new Date().getTime();
			if(now-lastaccesstime>SessionConfig.expire_time){
				return true;
			}else{
				
				return false;
			}
		}
			return true;
	}
}
