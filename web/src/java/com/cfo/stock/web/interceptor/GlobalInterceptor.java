package com.cfo.stock.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.SessionFactory;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.exception.InvalidSessionException;
import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.common.useragent.Browser;
import com.cfo.common.useragent.OperatingSystem;
import com.cfo.common.useragent.UserAgent;
import com.cfo.stock.web.util.ActionUtils;
import com.jrj.stocktrade.api.rpc.HsRpcContext;

/**
 * 全局拦截器用户植入cookie
 * @author yuanlong.wang
 *
 */
public class GlobalInterceptor extends SpringMvcSessionInterceptor {
	public final static String OPEN="_open";
	
	public final static String FROM="from";
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("=====global in=====");
		}
		SessionFactory factory=SessionFactory.getInstance(request, response);
		try {
			String sessinId=factory.getCurrentSessionIdCookie();
			if(log.isDebugEnabled()){
				log.debug("=====global sessinID:====="+sessinId);
			}
			if(sessinId==null||"".equals(sessinId)){
				if(log.isDebugEnabled()){
					log.debug("=====global add cookie=====");
				}
				factory.createSesionId();
			}
		} catch (InvalidSessionException e) {
			if(log.isDebugEnabled()){
				log.debug("=====global invalid=====");
			}
			factory.createSesionId();
		}
		HsRpcContext.setUserApps("stockweb");
		String ip=ActionUtils.getRemoteIpAdress(request);
		if(log.isDebugEnabled())
			log.debug("userIp:"+ip);
		//注入用户ip
		HsRpcContext.setUserIP(ip);
		
		// 将用户from记入session
		try {
			String from=request.getParameter(FROM);
			if(null!=from&&!"".equals(from)){
				getSession(request, response).setAttribute(AttributeKeys.AD_FROM, from);
				log.info("set from=" + from);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("记录来源失败！",e);
		}
		try {
		HttpSessionWrapper session = getSession(request, response);
		String network = session.getAttribute(HsRpcContext.MOBILEIMEI,
				String.class);
		String disk = session.getAttribute(HsRpcContext.MOBILEUUID,
				String.class);
		HsRpcContext.putValue(HsRpcContext.MOBILEIMEI,
				network != null ? network : "");
		HsRpcContext
				.putValue(HsRpcContext.MOBILEUUID, disk != null ? disk : "");
		HsRpcContext.setUserMac(network);
		UserAgent userAgent = UserAgent.parseUserAgentString(request
				.getHeader("User-Agent"));
		Browser browser = userAgent.getBrowser();
		OperatingSystem os = userAgent.getOperatingSystem();
		HsRpcContext.putValue(HsRpcContext.TERMINAL_DEVICE, os.getDeviceType()
				.getName());
		HsRpcContext.putValue(HsRpcContext.TERMINAL_OS, os.getName());
		HsRpcContext
				.putValue(HsRpcContext.TERMINAL_PLATFORM, browser.getName());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("注入系统信息失败！",e);
		}
		return true;
	}

}
