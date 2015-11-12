package com.cfo.stock.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cfo.common.enums.UserInfoStatus;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.common.session.utils.CookieUtils;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.action.AbstractStockBaseAction;
/**
 * 用户信息完整度拦截器
 * @author cheng
 *
 */
@SuppressWarnings("serial")
public class UserInfoStatusInterceptor extends SpringMvcSessionInterceptor {
	private static final String REGISTS2="_registerS2";
	private String regists2Url;
	private static String IC = "_ic";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("===i'm in=="+request.getRequestURI());
		}
		HttpSessionWrapper session=getSession(request,response);
		if(session!=null){
			HttpSessionWrapper mysession=((HttpSessionWrapper)session);
			Map<String, Object> map=mysession.getAttributs();
			log.info(request.getRequestURI());
			String from=(String)map.get(AttributeKeys.AD_FROM);
			from = URLUtils.ParamterFilter(from,'\0');
			if(log.isDebugEnabled()){
				log.debug("get from in userinfostatus interceptor==>"+from);
			}
			String inviteCode=request.getParameter("ic");
			inviteCode = URLUtils.ParamterFilter(inviteCode,'\0');
			if(log.isDebugEnabled()){
				log.debug("get inviteCode in the UserInfoStatusInterceptor ==>"+inviteCode);
			}
			if(StringUtils.isNotBlank(inviteCode)){
				mysession.setAttribute(AttributeKeys.INVITE_CODE, inviteCode);
			}else{
				String inner_ic = CookieUtils.getCookieByName(request,IC);
				if(StringUtils.isNotBlank(inner_ic)){
					inviteCode = inner_ic;
					mysession.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
				}else{
					Object session_ic = mysession.getAttribute(AttributeKeys.INVITE_CODE);
					if(session_ic!=null){
						inviteCode = (String)session_ic;
						CookieUtils.updateCookieByName(response,IC,inviteCode);
					}
				}
			}
			/*if(arg2 instanceof AbstractStockBaseAction){
				AbstractStockBaseAction action=(AbstractStockBaseAction) arg2;
				action.set__from(from);
				action.set__inviteCode(inviteCode);
			}*/
			//检验是否有登录标示
			String status=(String)map.get(AttributeKeys.USER_INFO_STATUS);
			if(status==null){
				response.sendRedirect(errorUrl);
				return false;
			}
			if(UserInfoStatus.NEED_ID_INFO.status.equals(status)){
				if(log.isDebugEnabled()){
					log.debug(request.getRequestURI());
				}
				if(request.getRequestURI().indexOf("user/registerS2.jspa")>=0){
					return true;
				}
				response.sendRedirect(regists2Url);
				return false;
			}
			if(UserInfoStatus.UNBIND.status.equals(status)){
				return true;
			}
			if(UserInfoStatus.UNOPEN.status.equals(status)){
				if(request.getRequestURI().indexOf("user/registerS2.jspa")>=0){
					return true;
				}
				response.sendRedirect(regists2Url);
				return false;
			}
		}
		return true;
	}
	public String getRegists2Url() {
		return regists2Url;
	}
	public void setRegists2Url(String regists2Url) {
		this.regists2Url = regists2Url;
	}
	
	
}
