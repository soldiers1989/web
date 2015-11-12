package com.cfo.stock.web.hk.action.trade;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.utils.TokenHelper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.HkPasswordInterceptor.NeedHkPassword;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.SystemStatusInterceptor.GetSysStatus;
import com.cfo.stock.web.interceptor.TokenInterceptor.Token;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.stock.SecurityCodeService;
import com.jrj.stocktrade.api.stock.SecurityStockService;
import com.jrj.stocktrade.hk.api.common.BidAskType;
import com.jrj.stocktrade.hk.api.common.Market;
import com.jrj.stocktrade.hk.api.common.MatchingType;
import com.jrj.stocktrade.hk.api.exception.NeedLoginException;
import com.jrj.stocktrade.hk.api.service.HkSecurityCodeService;
import com.jrj.stocktrade.hk.api.service.HkSecurityOrderService;
import com.jrj.stocktrade.hk.api.vo.HkSecurityConfirm;
import com.jrj.stocktrade.hk.api.vo.OrderBuyLimit;
import com.jrj.stocktrade.hk.api.vo.OrderPlacementResp;


@Controller
@RequestMapping("/stock/hk/{accountId}")
@NeedLogin
@TradeAccount
@NeedHkPassword
public class HKTradeBuyAction  extends AbstractStockBaseAction{
	
	
	@Autowired
	SecurityCodeService securityCodeService; 
	@Autowired
	SecurityStockService securityStockService; 
	@Autowired
	HkSecurityCodeService hkSecurityCodeService; 
	@Autowired
	HkSecurityOrderService hkSecurityOrderService;
	@GetSysStatus
	@RequestMapping(value={"/buyStep1","/buyStep2","/buyStep3"},method=RequestMethod.GET)
	public String buyStep1(
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		if("".equals(stockCode)){
			String _stockCode = session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId,String.class);
			if(StringUtils.isNotBlank(_stockCode)){
				stockCode =_stockCode;
			}
		}
		String viewName="/trade/buyStep1_hk";
		model.addAttribute("stockCode", stockCode);
		return viewName;
	}
	
	@RequestMapping(value="/ajaxBuyStep2",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxBuyStep2(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			@RequestParam(value="entrustPrice" ,defaultValue="")String entrustPrice,
			@RequestParam(value="entrustAmount" ,defaultValue="")String entrustAmount,
			@RequestParam(value="matchingType",defaultValue="")String matchingType,
			HttpSessionWrapper session,
			Model model){
		
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		entrustPrice = URLUtils.ParamterFilter(entrustPrice, '\0');
		entrustAmount = URLUtils.ParamterFilter(entrustAmount, '\0');
		matchingType = URLUtils.ParamterFilter(matchingType, '\0');
		
		JSONObject json = new JSONObject();
		
		if(stockCode==null || entrustPrice == null || entrustAmount ==null){
			json.put("errMsg", "");
			json.put("stockCode",stockCode);
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			return json.toJSONString();
		}
		
		String stockCodes = (String) session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
		if(!stockCode.equals(stockCodes)){
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			json.put("errMsg", "股票代码输入错误");
			return json.toJSONString();
		}
		BigDecimal count = new BigDecimal(entrustAmount);
		BigDecimal canBuyCount =session.getAttribute(AttributeKeys.STOCK_BUY_ENABLE + "_" + accountId , BigDecimal.class);
		if(count.compareTo(canBuyCount) > 0){
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			json.put("errMsg", "下单数量大于最大可买");
			return json.toJSONString();
		}
		if(StringUtils.isBlank(matchingType)){
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			json.put("errMsg", "下单类型不能为空");
			return json.toJSONString();
		}
		session.setAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId, entrustPrice);
		session.setAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId, entrustAmount);
		session.setAttribute(AttributeKeys.STOCK_BUY_MATCHING_TYPE + "_" + accountId, matchingType);
		
		json.put("stockCode", stockCode);
		json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
		json.put("entrustPrice", entrustPrice);
		json.put("entrustAmount", entrustAmount);
		String result=json.toJSONString();
		return result;
	}
	
	@RequestMapping(value="/ajaxBuyStep3",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@Token
	@ResponseBody
	public String ajaxBuyStep3(
			@PathVariable Long accountId,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model){
		
		model.addAttribute("token",TokenHelper.setToken(request,response));
		if(session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId)==null){
			model.addAttribute("errMsg", "您的交易已过期");
			return  JSON.toJSONString(model);
		}		
		String userId  = getSelfUserId(session);
		
		String stockCode = (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
		BigDecimal amont =new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId));
		String marketType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
		String stockType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
		String matchingType = (String)session.getAttribute(AttributeKeys.STOCK_BUY_MATCHING_TYPE + "_" + accountId);
		MatchingType type = MatchingType.getMatchingType(matchingType);
		BigDecimal price=BigDecimal.ZERO;
		if(type != MatchingType.AO){
			 price = new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId));
		}
		try{
			this.assembleOpFrom(session);
			OrderPlacementResp rs = hkSecurityOrderService.orderPlacement(userId, accountId, Market.HK, type, BidAskType.BUY, stockCode, price, amont);
			model.addAttribute("result", rs);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId).toString());
			deleteBuySession(session, accountId);
		}catch(ServiceException e){
			log.error("用户 :" +userId +", 账户："+accountId+"--"+"买入股票失败 ：" +e.getErrorInfo(), e); 
			model.addAttribute("stockCode", stockCode);
			model.addAttribute("marketType", marketType);
			model.addAttribute("stockType", stockType);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}catch(Exception e){
			log.error(e.getMessage(), e);
			model.addAttribute("errMsg", e.getMessage());
			if(e.getCause() instanceof NeedLoginException){
				model.addAttribute(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY, -1);
			}
		}
		return JSON.toJSONString(model);
	}
	
	@RequestMapping(value="/stockCheck",method=RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String stockCheck(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			HttpSessionWrapper session,
			Model model){
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		String userId = getSelfUserId(session);
		String rs="";
		try{
			HkSecurityConfirm codeConfirm = hkSecurityCodeService.securityCodeEnter(userId, accountId, stockCode);
			session.removeValue(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId, codeConfirm.getStockName());
			rs = JSONObject.toJSONString(codeConfirm);

		}catch(ServiceException e){
			log.error("用户 :" +userId +", 账户："+accountId+"--"+"查询股票失败 ：" +e.getErrorInfo(), e);  
			JSONObject json = new JSONObject();
			json.put("errMsg",StockUtil.getErrorInfo(e.getErrorInfo()));
			rs = json.toJSONString();
		}catch (Exception e) {
			log.error("用户 :" +userId +", 账户："+accountId+"--"+"查询股票失败 ：" +e.getMessage(), e); 
			JSONObject json = new JSONObject();
			if(e.getCause() instanceof NeedLoginException){
				json.put(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY, -1);
			}
			json.put("errMsg","请输入正确代码或简称");
			
			rs = json.toJSONString();
		}	
		return rs;
	}
	
	@RequestMapping(value="/stockLimit",method=RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String stockLimit(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			@RequestParam(value="marketType" ,defaultValue="")String marketType,
			@RequestParam(value="stockType" ,defaultValue="")String stockType,
			@RequestParam(value="entrustPrice" ,defaultValue="")String entrustPrice,
			HttpSessionWrapper session,
			Model model){
		
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		marketType = URLUtils.ParamterFilter(marketType, '\0');
		stockType = URLUtils.ParamterFilter(stockType, '\0');
		entrustPrice = URLUtils.ParamterFilter(entrustPrice, '\0');
		String userId = getSelfUserId(session);
		String rs ="";
		try{
			OrderBuyLimit limit = hkSecurityCodeService.buyLimit(userId, accountId, Market.HK, stockCode,"".equals(entrustPrice)?null:new BigDecimal(entrustPrice));
			rs = JSONObject.toJSONString(limit);
			//每次调整价格 将可以购买的数量存入session
			session.removeAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId, stockCode);
			session.removeAttribute(AttributeKeys.STOCK_BUY_ENABLE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_ENABLE + "_" + accountId, limit.getAvailableQuantity());
			session.removeAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId, marketType);
			session.removeAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId, stockType);
			
		}catch(ServiceException e){
			log.error("用户 :" +userId +", 账户："+accountId+"--"+"查询买入限制失败 ：" +e.getErrorInfo(), e); 
			JSONObject json = new JSONObject();
			String errMsg=e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", "");
			//json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			json.put("errMsg", errMsg);
			rs = json.toJSONString();
		}catch (Exception e){
			log.error("用户 :" +userId +", 账户："+accountId+"--"+"查询买入限制失败 ：" +e.getMessage(), e);
			JSONObject json = new JSONObject();
			if(e.getCause() instanceof NeedLoginException){
				json.put(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY, -1);
			}
			
			json.put("errMsg", e.getMessage());
			
			rs = json.toJSONString();
		}
		return rs;
	}
	
	private void deleteBuySession(HttpSessionWrapper session, Long accountId){
		session.removeAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_BUY_ENABLE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId);	
		session.removeAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_TRADE_CALLBACK_PARAMS + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_BUY_MATCHING_TYPE + "_" + accountId);
	}

}
