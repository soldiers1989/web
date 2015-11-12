package com.cfo.stock.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;

public class CompanyWorkerInterceptor extends SpringMvcSessionInterceptor {
	/**
	 * 公司员工拦截器
	 * @author li.jiang 2014-02-21
	 */
	private static final long serialVersionUID = 1L;
	private static final String WORKER_RESULT="notworker";
/*	public String intercept(ActionInvocation invocation) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("===i'm in=="+invocation.getInvocationContext().getName());
		}
		HttpSession session=getSession();
		if(session!=null){
			if(session instanceof HttpSessionWrapper){
				HttpSessionWrapper mysession=((HttpSessionWrapper)session);
				UserInfoVo user = mysession.getAttribute(AttributeKeys.USER_INFO,UserInfoVo.class);
				if(user==null || StringUtils.isBlank(user.getRealName())){
					AbstractXJBBaseAction action = (AbstractXJBBaseAction) invocation.getAction();
					UserInfoVo userInfoVo = action.getSelfUserInfo(session);
					if(userInfoVo!=null){
						mysession.setAttribute(AttributeKeys.USER_INFO, userInfoVo);
						if(UserIdentify.NOTWORKER.status==userInfoVo.getCompanyuser()){
							return WORKER_RESULT;	
						}
					}
				}else{
					if(UserIdentify.NOTWORKER.status==user.getCompanyuser()){
						return WORKER_RESULT;	
					}
				}
			}
		}
		return invocation.invoke();
	}*/
	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2) throws Exception {
		return true;
	}
}
