package com.cfo.stock.web.action.account;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.util.InfoMasker;
import com.cfo.stock.web.util.ZQTUtils;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.common.AccountStatus;
import com.jrj.stocktrade.api.common.IdType;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.common.StockAccountType;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.OpenAccountReq;

@Controller
@RequestMapping("/stock")
@NeedLogin

public class BrokerInfoAction extends AbstractStockBaseAction {
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	
	@RequestMapping(value="/kaihu",method=RequestMethod.GET)
	@ResponseBody
	public String invite(@RequestParam(value="brokerId" ,defaultValue="")String brokerId,@RequestParam(value="type" ,defaultValue="1")int type,
			HttpSessionWrapper session){
		JSONObject json = new JSONObject();
		try {
			//添加操作广告来源
			assembleOpFrom(session);
			if(brokerId ==null || "".equals(brokerId)){
				json.put("error", "-1");
				return json.toJSONString();
			}
			//获取userId
			String userId=this.getSelfUserId(session);
			//获取用户
			UserInfoVo user = getSelfUserInfo(session);
			if(user ==null){
				//数据不存在
				json.put("error", "-2");
				log.error("Method --> kaihu user is null");
				return json.toJSONString();
			}
			String realName =user.getRealName();//真实姓名
			String idN = user.getIdNumber();//用户身份证号
			String mobile = user.getMobile();//手机号
			String email = user.getEmail();//邮箱
			Broker broker = getBroker(brokerId);
			//判断券商类型
			switch (MarketType.getMarketType(broker.getType())){
			    case STOCK:{//A股判断
			    	if(StringUtils.isBlank(realName) || StringUtils.isBlank(idN) || StringUtils.isBlank(mobile)
						||"null".equals(realName) ||"null".equals(idN) ||"null".equals(mobile)){
			    		//数据不完整
						json.put("error", "-2");
						log.error("Method --> kaihu 取用户信息不完善！");
						return json.toJSONString();
			    	}
			    	break;
			    }
			    case HK:{//港股判断
			    	if(StringUtils.isBlank(email) || "null".equals(email)){
			    		//数据不完整
						json.put("error", "-2");
						log.error("Method --> kaihu 取用户信息不完善！");
						return json.toJSONString();
			    	}
			    	break;
			    }
			    default:break;
			}
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("realName", realName);
			params.put("mobile", mobile);
			params.put("email", email);
			params.put("idType", IdType.ID);
			params.put("idNo", idN);
			OpenAccountReq  oar = stockAccountStatusService.
					openAccount(userId,broker.getBrokerId()
							,StockAccountType.getStockAccountType(type),params);
			json.put("kaihuUrl", kaiHuUrl(oar));
			return json.toJSONString();
		} catch (ServiceException e) {
			log.error(e.getErrorInfo());
			json.put("error", e.getErrorNo());
			return json.toJSONString();
		}
	}
	/**
	 * 修改开户的状态
	 * @param brokerId
	 * @param state
	 * @return
	 */
	@RequestMapping(value="/updatekaihuState",method=RequestMethod.GET)
	@ResponseBody
	public String updatekaihuState(@RequestParam(value="brokerId" ,defaultValue="")String brokerId,@RequestParam(value="state" ,defaultValue="")String state,
			HttpSessionWrapper session){
		
		assembleOpFrom(session);
		JSONObject json = new JSONObject();
		if(brokerId ==null || "".equals(brokerId)){
			json.put("error", "-1");
			return json.toJSONString();
		}
		if(state ==null || "".equals(state)){
			json.put("error", "-1");
			return json.toJSONString();
		}
		String userId=this.getSelfUserId(session);
		if(userId ==null){
			json.put("error", "-1");
			return json.toJSONString();
		}
		UserInfoVo user = getSelfUserInfo(session);
		String realName ="";
		String idN = "";
		String mobile = null;
		String email = null;
		if(user !=null){
			realName =user.getRealName();
			idN = user.getIdNumber();
			mobile = user.getMobile();
			email = user.getEmail();
		}
		try {
			//修改状态
			OpenAccountReq  oar = stockAccountStatusService.updateStatus(userId,brokerId,AccountStatus.getAccountStatusType(state));
			json.put("kaihuUrl", kaiHuUrl(oar));
		} catch (ServiceException e) {
			log.error(e.getErrorInfo());
			json.put("error", e.getErrorInfo());
		}
		System.out.println("brokerId="+brokerId+",userId"+userId);
		return json.toJSONString();
	}	
	/**
	 * 拼接中山开户链接
	 * @param oar
	 * @return
	 */
	public String kaiHuUrl(OpenAccountReq  oar){
			if(oar != null){
				if(oar.getOpenUrl() != null){
					String queryString=ZQTUtils.transMapToString(oar.getParam());
					GetMethod gm = new GetMethod(oar.getOpenUrl());
					if(gm.getQueryString()!=null){
						gm.setQueryString(gm.getQueryString()+"&"+queryString);
					}else{
						gm.setQueryString(queryString);
					}
					try {
						return gm.getURI().toString();
					} catch (URIException e) {
						log.error("Method --> kaiHuUrl error Cann't create OpenUrl",e);
					}
				}else{
					log.error("Method --> kaiHuUrl error oar.getOpenUrl() is null");
				}
			}else{
				log.error("Method --> kaiHuUrl error oar is null");
			}
			return null;
	}
	
	/**
	 * 删除开户
	 * @param brokerId
	 * @param state
	 * @return
	 */
	@RequestMapping(value="/delKaihu",method=RequestMethod.GET)
	@ResponseBody
	public String delKaihu(@RequestParam(value="brokerId" ,defaultValue="")String brokerId,
			HttpSessionWrapper session){
		JSONObject json = new JSONObject();
		String userId=this.getSelfUserId(session);
		if(userId ==null){
			json.put("error", "-1");
			return json.toJSONString();
		}
		int result=0;
		try {
			result=stockAccountStatusService.delAccountOpen(userId, brokerId);
		}catch (ServiceException e) {
			log.error(e.getErrorInfo());
			json.put("error", e.getErrorInfo());
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value="/mobileTip",method=RequestMethod.GET)
	public String mobileTip(@RequestParam(value = "type", defaultValue = "") String type,
			@RequestParam(value="brokerId" ,defaultValue="")String brokerId,
			HttpSessionWrapper session,
			Model model){
		String userName = "***";
		String mobile = "***";
		String idNumber = "***";
		UserInfoVo vo=getSelfUserInfo(session);
		if(vo != null){
			if(vo.getMobile()!=null){
				mobile = InfoMasker.masker(vo.getMobile(), 3, 4, "*");
			}
			if(vo.getIdNumber()!=null ){
				idNumber = InfoMasker.masker(vo.getIdNumber(), 6, 8, "*");
			}
		}
		model.addAttribute("userName", userName);
		model.addAttribute("mobile",mobile);
		model.addAttribute("idNumber",idNumber);
		model.addAttribute("type", type);
		model.addAttribute("brokerId", brokerId);
		return "/account/mobileTip";
	}
	
}
