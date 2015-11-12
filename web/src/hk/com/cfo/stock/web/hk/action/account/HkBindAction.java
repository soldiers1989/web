package com.cfo.stock.web.hk.action.account;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.hk.services.hkbind.HkBindWebService;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.vo.BindInfo;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.hk.api.service.HkSecurityLoginService;
import com.jrj.stocktrade.hk.api.vo.LoginResp;
@Controller
@RequestMapping("/stock/hk/")
@NeedLogin
public class HkBindAction extends AbstractStockBaseAction{
	@Autowired
	HkSecurityLoginService hkSecurityLoginService;
	@Autowired
	HkBindWebService hkBindWebService;
	@Autowired
	AccountService accountService;
	/**
	 * 弹出港股修改密码窗口
	 */
	@RequestMapping(value = "/hkChangePass", method = RequestMethod.GET)
	public String hkChangePass(
			HttpSessionWrapper session,
			HttpServletRequest request, 
			Model model,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId
			){
		UserInfoVo userInfoVo = getSelfUserInfo(session);
		if(userInfoVo != null){
			Map<String,String> map = hkBindWebService.queryClientStatus(userInfoVo.getEmail());
			//查询支开户接口
			model.addAttribute("client_no", map.get("client_no"));
			model.addAttribute("account_name", map.get("account_name"));
		}else{
			model.addAttribute("client_no", "");
			model.addAttribute("account_name", "");
		}
		model.addAttribute("brokerId", brokerId);
		//读取账号及姓名
		return "/account/change_hk_pass";
	}
	/**
	 * 修改初始密码
	 */
	@RequestMapping(value="/dohkChangePass",method=RequestMethod.POST)
	public String dohkChangePass(
			@RequestParam(value = "accountNo", defaultValue = "") String accountNo,
			@RequestParam(value = "accountName", defaultValue = "") String accountName,
			@RequestParam(value = "oldPass", defaultValue = "") String oldPass,
			@RequestParam(value = "newPass", defaultValue = "") String newPass,
			@RequestParam(value = "reNewPass", defaultValue = "") String reNewPass,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId,
			@RequestParam(value = "client_no", defaultValue = "") String client_no,
			@RequestParam(value = "account_name", defaultValue = "") String account_name,
			HttpSessionWrapper session, 
			Model model
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			accountNo = URLUtils.ParamterFilter(accountNo, '\0');
			accountName = URLUtils.ParamterFilter(accountName, '\0');
			oldPass = URLUtils.ParamterFilter(oldPass, '\0');
			newPass = URLUtils.ParamterFilter(newPass, '\0');
			reNewPass = URLUtils.ParamterFilter(reNewPass, '\0');
			brokerId = URLUtils.ParamterFilter(brokerId, '\0');
			
			String userId=this.getSelfUserId(session);
			UserInfoVo userInfoVo = getSelfUserInfo(session);
			//初始化数据
			if(userInfoVo != null){
				Map<String,String> map = hkBindWebService.queryClientStatus(userInfoVo.getEmail());
				client_no = map.get("client_no");
				account_name = map.get("account_name");
			}else{
				client_no = "";
				account_name = "";
			}
			//初始化提交的数据
			model.addAttribute("client_no", client_no);
			model.addAttribute("account_name", account_name);
			model.addAttribute("brokerId", brokerId);
			
			model.addAttribute("accountNo", accountNo);
			model.addAttribute("accountName", accountName);
			model.addAttribute("oldPass", oldPass);
			model.addAttribute("newPass", newPass);
			model.addAttribute("reNewPass", reNewPass);
			
			//调用登录接口判断密码是否成功
			LoginResp loginResp = hkSecurityLoginService.changePassword(userId,brokerId,accountNo,oldPass,newPass);
			if(loginResp == null){
				model.addAttribute("msg", "密码错误!");
			}else{
				if(loginResp.isResult()){
					json.put("retcode", "0");
					json.put("msg", "密码修改成功！");
					return "/account/change_hk_pass_success";
					
					/*Map<String,Object> data = new HashMap<String,Object>();
					data.put("accountNo", accountNo);
					//调用绑定接口
					BindInfo bindInfo = accountService.bindReceive(userId,SecuritiesBroker.getBroker(brokerId),data);
					if(bindInfo == null){
						model.addAttribute("msg", "修改密码成功，绑定失败!");
					}else{
						if(bindInfo.getErrorNo() == 0){
							return "/account/change_hk_pass_success";
						}else{
							model.addAttribute("msg", bindInfo.getErrorInfo());
						}
					}*/
					
				}else{
					model.addAttribute("msg", loginResp.getErrorInfo());
				}
			}
		}catch(Exception e){
			log.error("Method --> dohkChangePass error", e);
			model.addAttribute("msg", "修改密码失败!");
		}
		return "/account/change_hk_pass";

	}
	/**
	 * 弹出港股绑定窗口
	 */
	@RequestMapping(value = "/hkBind", method = RequestMethod.GET)
	public String hkBind(
			HttpSessionWrapper session,
			HttpServletRequest request, 
			Model model,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId
			){
		//获取登录信息
		//UserInfoVo userInfoVo = getSelfUserInfo(session);
		
		//初始化数据
		//Map<String,String> map = hkBindWebService.queryClientStatus(userInfoVo.getEmail());
		
		/*
		model.addAttribute("client_no", map.get("client_no"));*/
		model.addAttribute("client_no", "");
		model.addAttribute("brokerId", brokerId);
		//读取账号及姓名
		return "/account/bind_hk";
	}
	/**
	 * 执行绑定接口
	 */
	@RequestMapping(value = "/doHkBind", method = RequestMethod.POST)
	public String doHkBind(
			@RequestParam(value = "accountNo", defaultValue = "") String accountNo,
			@RequestParam(value = "password", defaultValue = "") String password,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId,
			@RequestParam(value = "client_no", defaultValue = "") String client_no,
			HttpSessionWrapper session, 
			Model model
			) throws ServiceException{
		try{
			//获取登录信息
			//UserInfoVo userInfoVo = getSelfUserInfo(session);
			
			accountNo = URLUtils.ParamterFilter(accountNo, '\0');
			password = URLUtils.ParamterFilter(password, '\0');
			client_no = URLUtils.ParamterFilter(client_no, '\0');
			brokerId = URLUtils.ParamterFilter(brokerId, '\0');
			
			//初始化数据
			/*Map<String,String> map = hkBindWebService.queryClientStatus(userInfoVo.getEmail());
			client_no = map.get("client_no");*/
			
			model.addAttribute("client_no", client_no);
			model.addAttribute("brokerId", brokerId);
			model.addAttribute("accountNo", accountNo);
			model.addAttribute("password", password);
			
			//判断密码是否是初始密码
			/*if(Global.HK_INIT_PASSWORD.equals(password)){
				model.addAttribute("account_name", map.get("account_name"));
				return "/account/change_hk_pass";
			}*/
			
			String userId=this.getSelfUserId(session);
			//调用登录接口判断密码是否成功
			LoginResp loginResp = hkSecurityLoginService.login(userId,brokerId,accountNo,password);
			if(loginResp == null){
				log.error("Method -> bind error , 密码错误 userId="+userId+",brokerId="+brokerId);
				model.addAttribute("msg", "密码错误!");
			}else{
				if(loginResp.isResult()){
					Map<String,Object	> data = new HashMap<String, Object>();
					data.put("accountNo", accountNo);
					//调用绑定接口
					BindInfo bindInfo = accountService.bindReceive(userId,brokerId,data);
					if(bindInfo == null){
						log.error("Method -> bindInfo is null , 绑定失败 userId="+userId+",brokerId="+brokerId);
						model.addAttribute("msg", "绑定失败!");
					}else{
						if(bindInfo.getErrorNo() == 0){
							return "/account/bind_hk_success";
						}else{
							log.error("Method -> 绑定失败  userId="+userId+",brokerId="+brokerId+", error:"+bindInfo.getErrorInfo());
							model.addAttribute("msg", bindInfo.getErrorInfo());
						}
					}
				}else{
					model.addAttribute("msg", loginResp.getErrorInfo());
				}
			}
		}catch(Exception e){
			log.error("Method --> doHkBind error", e);
			model.addAttribute("msg", "绑定失败!");
		}
		return "/account/bind_hk";
	}
}
