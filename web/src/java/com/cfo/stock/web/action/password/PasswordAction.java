package com.cfo.stock.web.action.password;

import java.util.HashMap;
import java.util.Map;

import ocx.GetRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.util.VelocityUtils;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.common.PasswordType;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.password.PasswordService;
import com.jrj.stocktrade.api.password.vo.PasswordReq;
import com.jrj.stocktrade.api.pub.PasswordFlagService;
import com.jrj.stocktrade.api.pub.vo.PasswordFlag;
@Controller
@RequestMapping("/stock/{accountId}/password")
@NeedLogin
public class PasswordAction extends AbstractStockBaseAction{
	@Autowired
	private PasswordService passwordService;
	@Autowired
	PasswordFlagService passwordFlagService;
	
	@RequestMapping(value="/view",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String view(@PathVariable("accountId")long accountId,
			@RequestParam("action")int action,
			@RequestParam(value="bankNo",required=false)String bankNo
			,HttpSessionWrapper session) throws ServiceException{
		JSONObject result=new JSONObject();
		try{
		String userId=getSelfUserId(session);
		PasswordType type=PasswordType.getPasswordType(action);
		
		if(type==null){
			result.put("status", -1);
			return result.toJSONString();
		}
		PasswordReq req=passwordService.passwordSend(userId, accountId, type,bankNo, "");
		if(req!=null){
			if(0==req.getErrorNo()){
				result.put("status", 0);
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("url", req.getUrl());
				map.put("params", req.getParam());
				map.put("random", System.currentTimeMillis());
				result.put("data", VelocityUtils.getInstance().mergeToString("template/vm/iframe_password.vm", map));
				result.put("type", PasswordShape.IFRAME);//iframe
				result.put("iframe_src", req.getUrl());
				return result.toJSONString();
			}else if(-1==req.getErrorNo()){
				result.put("status", -2);//中山不走
				return result.toJSONString();
			}else if(-3==req.getErrorNo()){
				result.put("status", 1);//不要密码
				result.put("type", PasswordShape.NONE);//不要密码
				return result.toJSONString();
			}else{
				Map<String,Object> map=new HashMap<String,Object>();
				switch (type) {
				case INIT:
					if(-4==req.getErrorNo()){
						result.put("txpwd", 1);
						map.put("txpwd", 1);
					}
				case TRADE:
				case CANCEL:
				case BIND:
				{
					result.put("status", 0);
					result.put("type", PasswordShape.ACTIVE);//activex
					String rdm=""+System.currentTimeMillis();
					result.put("random", rdm);
					map.put("activex", 1);
					map.put("random", rdm);
					result.put("data", VelocityUtils.getInstance().mergeToString("template/vm/activex_password.vm", map));
					return result.toJSONString();
				}
				case BANK2TRADE:
				case TRADE2BANK:
				case BANKQUERY:
					{
						result.put("status", 0);
						result.put("type", PasswordShape.ACTIVE);//activex
						String rdm=""+System.currentTimeMillis();
						result.put("random", rdm);
						map.put("activex", 2);
						map.put("random", rdm);
						PasswordFlag flag=queryPasswordFlag(userId, accountId, bankNo, type);
						map.put("passwordFlag", flag);
						result.put("data", VelocityUtils.getInstance().mergeToString("template/vm/activex_password.vm", map));
						return result.toJSONString();
					}
				default:
					result.put("status", -3);
					return result.toJSONString();
				}
			}
		}
		}catch(Exception e){
			log.error("#password.view", e);
			result.put("status", -2);
			return result.toJSONString();
		}
		
		return null;
	}
	
	private PasswordFlag queryPasswordFlag(String userId, long accountId, String bankNo,PasswordType type) throws ServiceException{
		switch (type) {
		case BANK2TRADE:
			return passwordFlagService.passwordFlagQuery(userId, accountId, bankNo, PasswordFlag.TYPE_TRANSFER_BANK_TO_FUND);
		case BANKQUERY:
			return passwordFlagService.passwordFlagQuery(userId, accountId, bankNo, PasswordFlag.TYPE_BANK_AMT_QUERY);
		case TRADE2BANK:
			return passwordFlagService.passwordFlagQuery(userId, accountId, bankNo, PasswordFlag.TYPE_TRANSFER_FUND_TO_BANK);
		default:
			return null;
		}
		
	}
}
