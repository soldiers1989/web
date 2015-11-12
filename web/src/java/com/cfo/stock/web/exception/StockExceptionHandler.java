package com.cfo.stock.web.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.SessionFactory;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.utils.TokenHelper;
import com.cfo.stock.web.util.ActionUtils;

/**
 * 股票异常处理器
 * StockExceptionHandler
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014年7月20日    		iriyadays     create  
 * ---------------------------------------------------------  
 * </PRE> 
 *
 */
public class StockExceptionHandler extends SimpleMappingExceptionResolver {
	Log log = LogFactory.getLog(getClass());
	private static final String PASS_REQUEST_HK ="/stock/hk/accountId/hkPasswords.jspa";
	private static final String PASS_REQUEST ="/stock/accountId/initQsPwd.jspa";
	private static final String SYSTEM_UNAVAILABLE ="unavailable";
	private final static String ACCOUNTID="accountId";
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		log.error(ex.getMessage(),ex);
		ex.printStackTrace();
		ModelAndView mv = new ModelAndView();
		String returnUrl=request.getRequestURI();
		mv.addObject("returnUrl", returnUrl);
		long accountId = checkAccountId(request,response,handler);
		Exception old = ex; 
		if(ex.getCause() != null && ex.getCause() instanceof Exception) ex = (Exception) ex.getCause();
		log.info("old====== " + old.getClass());
		log.info("ex====== " + ex.getClass());
		//log.info("ex====== " + ex.getCause() != null ? ex.getCause().getClass() : "");
		
		//判断如果是密码超时错误就跳转到密码输入页面
		if(ex instanceof com.jrj.stocktrade.hk.api.exception.NeedLoginException){//港股密码失效
			//弹出密码窗口
			String view = "redirect:"+PASS_REQUEST_HK.replaceAll("accountId", accountId+"");
			mv.setViewName(view);
			log.info("ex => HK.NeedLoginException");
        }else if(ex instanceof com.jrj.stocktrade.api.exception.NeedLoginException){//A股密码失效
        	String view = "redirect:"+PASS_REQUEST.replaceAll("accountId", accountId+"");
			mv.setViewName(view);
			log.info("ex => A.NeedLoginException");
        }else if(ex instanceof com.jrj.stocktrade.api.exception.UnAvailableException){//系统不可用
        	HttpSessionWrapper session=SessionFactory.getInstance(request, response).getSession();
        	mv.addObject("selfUserId", session.getAttribute(AttributeKeys.USER_ID,String.class));
        	mv.addObject("exception",(com.jrj.stocktrade.api.exception.UnAvailableException)ex);
        	mv.setViewName(SYSTEM_UNAVAILABLE);
        	log.info("ex => UnAvailableException");
        }else if(ex instanceof com.jrj.stocktrade.api.exception.NeedTradePwdException){
        	if(ActionUtils.isAjax(request)){
        		mv=new ModelAndView("jsonView");
        		mv.addObject("token", TokenHelper.setToken(request,response));
        		mv.addObject("errCode", "-406");//需要验证交易密码
        		mv.addObject("errMsg", "需要输入交易密码");
        		return mv;
        	}else{
        		String view = "redirect:"+PASS_REQUEST.replaceAll("accountId", accountId+"");
    			mv.setViewName(view);
    			log.info("ex => A.NeedTradePwdException");
        	}
        }else if(ex instanceof com.alibaba.dubbo.remoting.TimeoutException){
        	if(ActionUtils.isAjax(request)){
        		mv=new ModelAndView("jsonView");
        		mv.addObject("errorNo", "-500");//需要验证交易密码
        		mv.addObject("errMsg", "服务器忙，请稍后再试。");
        		return mv;
        	}else{
        		return super.resolveException(request, response, handler, ex);
        	}
        }else{
        	return super.resolveException(request, response, handler, ex);
        }
		return mv;
	}
	/**
	 * 获取accountId
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	@SuppressWarnings("unchecked")
	private long checkAccountId(HttpServletRequest request, HttpServletResponse response,Object handler){
		try{
			//HandlerMethod handlerMethod = (HandlerMethod) handler;
			ServletWebRequest webRequest = new ServletWebRequest(request, response);
			Map<String, String> uriTemplateVars = 
					(Map<String, String>) webRequest.getAttribute(
							HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
			if(uriTemplateVars.containsKey(ACCOUNTID)){
				long accountId= (uriTemplateVars != null) ? Long.valueOf(uriTemplateVars.get(ACCOUNTID)) : 0;
				return accountId;
			}
		}catch(Exception e){
			log.error("Method -> StockExceptionHandler checkAccountId error", e);
		}
		return 0;
	}
}
