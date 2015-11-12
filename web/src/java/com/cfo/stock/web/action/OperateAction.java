package com.cfo.stock.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.common.BrokerStatus;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;
import com.jrj.stocktrade.api.pub.SystemStatusQueryService;

/**
 * 首页
 * 
 * @author yuanlong.wang
 * 
 */
@Controller
@RequestMapping("/stock/operate")
public class OperateAction extends AbstractStockBaseAction {

	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	SystemStatusQueryService systemStatusQueryService;
	
	@RequestMapping(value = "hjdioegfjREDKDFGUQWESMDEQWsss", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getUserInfo(
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "brokerId", required = false) String brokerId) {
		try {
			if (userId == null) {
				return "{'result':'请传用户ID'}";
			}
			List<StockAccountStatus> list = stockAccountStatusService
					.queryAllStockOpenStatusByUserId(userId);
			if (brokerId != null) {
				for (StockAccountStatus status : list) {
					if (status.getBrokerId().equals(brokerId)) {
						return JSON.toJSONString(status);
					}
				}
				return "{'result':'不存在 " + brokerId + " 的开户记录'}";
			}
			if (list != null) {
				return JSON.toJSONString(list);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return "{'result':'无开户记录'}";
	}
	@RequestMapping(value = "fjat3oegfjREDKDwejdkui1MDWDRnfa2skkf", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getQsServerStatus(@RequestParam(value="brokerId",required=false) String brokerId){
		JSONObject json=new JSONObject();
		if(brokerId==null){
			try {
				List<Broker> list=accountService.queryAllBrokers(BrokerStatus.USABLE);
				json.put("result", 0);
				for(Broker broker:list){
					try{
					json.put(broker.getBrokerId(), systemStatusQueryService.getSystemStatus(broker.getBrokerId()));
					}catch(Exception e){
						Map<String,Object> map=new HashMap<String,Object>();
						map.put("status", -1);
						map.put("desc", "检验券商状态失败！");
						json.put(broker.getBrokerId(),map);
					}
				}
			} catch (ServiceException e) {
				json.put("result", -1);
				json.put("errorInfo", "获取券商列表失败");
			}
		}else{
			try{
				if(brokerHelper.getBroker(brokerId)!=null){
					json.put("result", 0);
					json.put(brokerId,  systemStatusQueryService.getSystemStatus(brokerId));
				}else{
					json.put("result", -1);
					json.put("errorInfo", "券商不存在");
				}
			}catch(Exception e){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("status", -1);
				map.put("desc", "检验券商状态失败！");
				json.put(brokerId,map);
			}
		}
		return json.toJSONString();
	}
}
