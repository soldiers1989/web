package com.cfo.stock.web.action.password;

import java.util.HashMap;
import java.util.Map;

import ocx.GetRandom;

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
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.exception.ServiceException;

@Controller
@NeedLogin
public class BrokerPasswordAction  extends AbstractStockBaseAction{

	@RequestMapping(value="/stock/password/{brokerId}/view",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String view(@PathVariable("brokerId")String brokerId,
			@RequestParam("action")int action
			,HttpSessionWrapper session) throws ServiceException{
		JSONObject result=new JSONObject();
		result.put("status", 0);
		result.put("type", 2);//activex
		Map<String,Object> map=new HashMap<String,Object>();
		Broker broker=brokerHelper.getBroker(brokerId);
		if(broker!=null){
			if(broker.getTxPwd()==1){
				result.put("txpwd", 1);
				map.put("txpwd", 1);
			}
		}
		String rdm=""+System.currentTimeMillis();
		result.put("random", rdm);
		
		map.put("activex", 1);
		map.put("random", rdm);
		result.put("data", VelocityUtils.getInstance().mergeToString("template/vm/activex_password.vm", map));
		return result.toJSONString();
	}
	
	@RequestMapping(value="/stock/password/prandnum",method=RequestMethod.POST)
	@ResponseBody
	public String  prandNum(HttpSessionWrapper session){
		String mcrypt_key=GetRandom.generateString(32);
		session.setAttribute("mcrypt_key", mcrypt_key);
		return 	mcrypt_key;	
	}
}
