package com.cfo.stock.web.hk.action.account;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TokenInterceptor.Token;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.hk.api.service.HkSecurityLoginService;
import com.jrj.stocktrade.hk.api.vo.LoginResp;
@Controller
@RequestMapping("/stock/hk/{accountId}/")
@NeedLogin
public class HkPasswordAction extends AbstractStockBaseAction{
	@Autowired
	HkSecurityLoginService hkSecurityLoginService;
	/**
	 * 弹出密码窗口
	 */
	@RequestMapping(value = "/hkPasswords", method = RequestMethod.GET)
	public String hkPasswords(
			@PathVariable Long accountId,
			@RequestParam(value="returnUrl", defaultValue="")String returnUrl,
			HttpSessionWrapper session,
			HttpServletRequest request, 
			Model model
			){
		model.addAttribute("accountId", accountId);
		model.addAttribute("returnUrl", returnUrl);
		return "/hkPassword";
	}
	@RequestMapping(value="/hkLogin",method=RequestMethod.POST)
	@Token(tokenUrl="/stock")
	public String HkLogin(
			@PathVariable Long accountId,
			@RequestParam(value = "password", defaultValue = "") String password,
			@RequestParam(value = "returnUrl", defaultValue = "/stock") String returnUrl,
			HttpSessionWrapper session, 
			RedirectAttributes attributes,
			Model model
			) {
		password=URLUtils.ParamterFilter(password, '\0');
		returnUrl=URLUtils.ParamterFilter(returnUrl, '\0');
		if(StringUtils.isBlank(returnUrl)){
			returnUrl="/stock";
		}
		model.addAttribute("returnUrl", returnUrl);
		String inputView = "/hkPassword";
		String successView = "redirect:" + returnUrl;
		try{
			//获取登录人的用户信息
			String userId = getSelfUserId(session);
			LoginResp loginResp = hkSecurityLoginService.login(userId, accountId, password);
			if(loginResp == null){
				model.addAttribute("retcode", "-1");
				model.addAttribute("msg", "密码错误!");
				return inputView;
			}else{
				if(loginResp.isResult()){
					session.setAttribute(AttributeKeys.HK_PASSWORD+accountId, "password_hk_ok");
				}else{
					model.addAttribute("retcode", loginResp.getErrorNo());
					model.addAttribute("msg", loginResp.getErrorInfo());
					return inputView;
				}
			}
		}catch(Exception e){
			log.error("Method --> ajaxHkLogin error", e);
			model.addAttribute("retcode", "-1");
			model.addAttribute("msg", "密码错误!");
			return inputView;
		}
		return successView;
	}
	/**
	 * 港股账号登录
	 */
	@RequestMapping(value="/ajaxHkLogin",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxHkLogin(
			@PathVariable Long accountId,
			@RequestParam(value = "password", defaultValue = "") String password,
			HttpSessionWrapper session, 
			Model model
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录人的用户信息
			String userId = getSelfUserId(session);
			LoginResp loginResp = hkSecurityLoginService.login(userId, accountId, password);
			if(loginResp == null){
				json.put("retcode", "-1");
				json.put("msg", "密码错误!");
			}else{
				if(loginResp.isResult()){
					session.setAttribute(AttributeKeys.HK_PASSWORD+accountId, "password_hk_ok");
					json.put("retcode", "0");
					json.put("msg", "");
				}else{
					json.put("retcode", loginResp.getErrorNo());
					json.put("msg", loginResp.getErrorInfo());
				}
			}
		}catch(Exception e){
			log.error("Method --> ajaxHkLogin error", e);
			json.put("retcode", "-1");
			json.put("msg", "密码错误!");
		}
		return json.toJSONString();
	}
}
