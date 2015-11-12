package com.cfo.stock.web.interceptor;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.stock.web.exception.ForbiddenException;
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.util.ZQTUtils;
import com.jrj.stocktrade.api.account.UserAuthService;

public class AccountAuthInterceptor extends SpringMvcSessionInterceptor{
	private final static String ZQTJSON_COOKIE = "zqt_u_";
	private final static String ZQTJSON_ENCRYPT_KEY = "zqt_orz";
	private final static String ACCOUNTID="accountId";
	@Autowired
	private UserAuthService userAuthService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		boolean checkAccount=false;
		Long accountId=null;
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		Map<String, String> uriTemplateVars = 
				(Map<String, String>) webRequest.getAttribute(
						HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if(uriTemplateVars.containsKey(ACCOUNTID)){
			checkAccount=true;
			accountId= (uriTemplateVars != null) ? Long.valueOf(uriTemplateVars.get(ACCOUNTID)) : null;
		}
		if(checkAccount){
			//检验是否有权限操作
			if(null==accountId){
				throw new ForbiddenException();
			}else{
				String userId=ZQTUtils.getSsoUserId(request);
				if(!checkAccountAuthCookie(request, userId, accountId)){
					log.info("用户" + userId + "账户" + accountId + "cookie里没有是否有权限标记");
					boolean right=userAuthService.isOperRight(userId, accountId);
					log.info("用户" + userId + "账户" + accountId +" 是否可控制:"+right);
					if(right){
						addAccountAuthCookie(response, userId, accountId);
					}else{
						removeAccountAuthCookie(request, response, userId, accountId);
						throw new ForbiddenException();
					}
				}
			}
		}
		return super.preHandle(request, response, handlerMethod);
	}
	/**
	 * 在用户cookie中增加表示有权限控制某账户
	 * @param response
	 * @param userId
	 * @param accountId
	 */
	private void addAccountAuthCookie(HttpServletResponse response, String userId,
			long accountId) {
		String zqtjsonMd5 = encrypt(userId + "_" + accountId);
		String checkUuidCookieKey = ZQTJSON_COOKIE + userId + "_a_" + accountId;
		response.addCookie(new Cookie(checkUuidCookieKey, zqtjsonMd5));
	}
	/**
	 *  在用户cookie中删除 表示没有权限控制某账户
	 * @param request
	 * @param response
	 * @param userId
	 * @param accountId
	 */
	private void removeAccountAuthCookie(HttpServletRequest request,HttpServletResponse response, String userId,
			long accountId) {
		String checkUuidCookieKey = ZQTJSON_COOKIE + userId + "_a_" + accountId;
		ActionUtils.delCookie(checkUuidCookieKey, request.getHeader("Host"), response);
	}
	/**
	 * 检查用户cookie中是否有权限控制某账户标识
	 * @param req
	 * @param userId
	 * @param accountId
	 * @return
	 */
	private boolean checkAccountAuthCookie(HttpServletRequest req, String userId,
			long accountId) {
		String zqtuuidMd5 = null;
		String checkUuidCookieKey = ZQTJSON_COOKIE + userId + "_a_" + accountId;
		for (Cookie cookie : req.getCookies()) {
			if (checkUuidCookieKey.equals(cookie.getName())) {
				zqtuuidMd5 = cookie.getValue();
			}
		}
		if (zqtuuidMd5 != null) {
			String md5 = encrypt(userId + "_" + accountId);
			if (zqtuuidMd5.equals(md5)) {
				return true;
			}
		}
		return false;
	}
	private String encrypt(String val) {
		return DigestUtils.md5Hex(val + ZQTJSON_ENCRYPT_KEY);
	}
}
