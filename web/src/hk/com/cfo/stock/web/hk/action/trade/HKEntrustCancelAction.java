package com.cfo.stock.web.hk.action.trade;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.utils.TokenHelper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TokenInterceptor.Token;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.jrj.common.utils.DateUtil;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.hk.api.service.HkSecurityOrderService;
import com.jrj.stocktrade.hk.api.vo.OrderAmendmentResp;
import com.jrj.stocktrade.hk.api.vo.OrderCancelationResp;


@Controller
@RequestMapping("/stock/hk/{accountId}")
@NeedLogin
@TradeAccount
public class HKEntrustCancelAction  extends AbstractStockBaseAction{

	@Autowired
	AccountService accountService ;
	@Autowired
	HkSecurityOrderService hkSecurityOrderService;

	
	@RequestMapping(value="/cancelStep4",method=RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@Token
	@ResponseBody
	public String cancelStep(
			@PathVariable Long accountId,
			@RequestParam(value="stockName" ,defaultValue="")String stockName,
			@RequestParam(value="entrustNo" ,defaultValue="")String entrustNo,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model){
		stockName = URLUtils.ParamterFilter(stockName, '\0');
		JSONObject json = new JSONObject();
		json.put("token",TokenHelper.setToken(request,response));
		String userId = getSelfUserId(session);
		long entrustno =Long.parseLong(entrustNo);
		try{
			this.assembleOpFrom(session);
			OrderCancelationResp resp =  hkSecurityOrderService.orderCancelation(userId, accountId, entrustno);
			
			json.put("rs", resp);
			json.put("stockName", stockName);
			json.put("nowDate", DateUtil.format(new Date(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"撤单失败失败 ：" +e.getErrorInfo());
			json.put("errMsg", "撤单失败失败 ：" +e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", ""));
		}finally{
			//session.removeAttribute(AttributeKeys.STOCK_CANCEL_ENTRUSTNO);
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value="/modifyOrder",method=RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@Token
	@ResponseBody
	public String modifyOrder(
			@PathVariable Long accountId,
			@RequestParam(value="stockName" ,defaultValue="")String stockName,
			@RequestParam(value="orderNo" ,defaultValue="")Long orderNo,
			@RequestParam(value="price" ,defaultValue="")BigDecimal price,
			@RequestParam(value="quantity" ,defaultValue="")BigDecimal quantity,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model
			){
		stockName = URLUtils.ParamterFilter(stockName, '\0');
		String userId = getSelfUserId(session);
		OrderAmendmentResp result = null;
		JSONObject json = new JSONObject();
		json.put("token",TokenHelper.setToken(request,response));
		try {
			result = hkSecurityOrderService.orderAmendment(userId, accountId, orderNo, price, quantity);
		} catch (ServiceException e) {
			log.error("accountid-->"+accountId+", userId-->"+userId+", errMsg-->"+e.getErrorInfo(), e);
			json.put("stockName", stockName);
			json.put("errMsg", e.getErrorInfo());
			return json.toJSONString();
		}
		
		json.put("result", result);
		return json.toJSONString();
	}
}
