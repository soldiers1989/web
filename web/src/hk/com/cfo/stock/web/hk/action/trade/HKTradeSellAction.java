package com.cfo.stock.web.hk.action.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import com.jrj.stocktrade.api.stock.SecurityStockService;
import com.jrj.stocktrade.api.stock.StockQueryService;
import com.jrj.stocktrade.hk.api.common.BidAskType;
import com.jrj.stocktrade.hk.api.common.Market;
import com.jrj.stocktrade.hk.api.common.MatchingType;
import com.jrj.stocktrade.hk.api.exception.NeedLoginException;
import com.jrj.stocktrade.hk.api.service.HkSecurityCodeService;
import com.jrj.stocktrade.hk.api.service.HkSecurityHoldingService;
import com.jrj.stocktrade.hk.api.service.HkSecurityOrderService;
import com.jrj.stocktrade.hk.api.vo.HkSecurityConfirm;
import com.jrj.stocktrade.hk.api.vo.OrderPlacementResp;
import com.jrj.stocktrade.hk.api.vo.StockHolding;


@Controller
@RequestMapping("/stock/hk/{accountId}")
@NeedLogin
@TradeAccount
@NeedHkPassword
public class HKTradeSellAction  extends AbstractStockBaseAction{
	@Autowired
	StockQueryService stockQueryService;
	@Autowired
	SecurityStockService securityStockService;
	@Autowired
	HkSecurityOrderService hkSecurityOrderService;
	@Autowired
	HkSecurityCodeService hkSecurityCodeService; 
	@Autowired
	HkSecurityHoldingService hkSecurityHoldingService;
	@GetSysStatus
	@RequestMapping(value={"/sellStep1","/sellStep2","/sellStep3"},method=RequestMethod.GET)
	public String buyStep1(@RequestParam(value="stockCode" ,defaultValue="")String stockCode,Model model){
		
		String viewName="/trade/sellStep1_hk";		
		model.addAttribute("stockCode", stockCode);
		return viewName;
	}
	
	@RequestMapping(value="/ajaxSellStep2",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxSellStep2(
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
			json.put("errMsg","");
			json.put("stockCode",stockCode);
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			return json.toJSONString();
		}
		String stockCodes = (String) session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
		if(!stockCode.equals(stockCodes)){
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			json.put("errMsg", "股票代码输入错误");
			return json.toJSONString();
		}
		if(StringUtils.isBlank(matchingType)){
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			json.put("errMsg", "下单类型不能为空");
			return json.toJSONString();
		}
		BigDecimal count = new BigDecimal(entrustAmount);
		BigDecimal sellEnable =session.getAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId,BigDecimal.class) ;
		if(count.compareTo(sellEnable)==1){
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			json.put("errMsg", "下单数量大于最大可卖");
			return json.toJSONString();
		}
		session.setAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId, entrustPrice);
		session.setAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId, entrustAmount);
		session.setAttribute(AttributeKeys.STOCK_SELL_MATCHING_TYPE + "_" + accountId, matchingType);
		
		json.put("stockCode", stockCode);
		json.put("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
		json.put("entrustPrice", entrustPrice);
		json.put("entrustAmount", entrustAmount);
		String result=json.toJSONString();
		return result;

	}
	
	@RequestMapping(value="/ajaxSellStep3",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@Token
	@ResponseBody
	public String ajaxSellStep3(
			@PathVariable Long accountId,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model){
		
		model.addAttribute("token",TokenHelper.setToken(request,response));
		if(session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId)==null){
			model.addAttribute("errMsg", "您的交易已过期");	
			return JSON.toJSONString(model);
		}		
		String userId  = getSelfUserId(session);
		String stockCode = (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
		BigDecimal amont =new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId));
		String matchingType = (String)session.getAttribute(AttributeKeys.STOCK_SELL_MATCHING_TYPE + "_" + accountId);
		MatchingType type = MatchingType.getMatchingType(matchingType);
		BigDecimal price=BigDecimal.ZERO;
		if(type != MatchingType.AO){
			 price = new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId));
		}
		try{
			this.assembleOpFrom(session);
			OrderPlacementResp rs = hkSecurityOrderService.orderPlacement(userId, accountId, Market.HK, type, BidAskType.SELL, stockCode, price, amont);
			model.addAttribute("result", rs);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId).toString());
			deleteSellSession(session, accountId);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"账户 :"+accountId+"--"+"卖出股票失败 ：" +e.getErrorInfo(), e); 
			model.addAttribute("stockCode", stockCode);
			String errMsg=e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", "");
			//model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("errMsg", errMsg);
		}catch(Exception e){
			log.error("用户 :" +userId +"账户 :"+accountId+"--"+"卖出股票失败 ：" +e.getMessage(), e);
			model.addAttribute("stockCode", stockCode);
			if(e.getCause() instanceof NeedLoginException){
				model.addAttribute(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY, -1);
			}
		}
		return JSON.toJSONString(model);
	}
	
	@RequestMapping(value="/ajaxQueryPosition",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxQueryPosition(
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model
			){
		String userId=getSelfUserId(session);
		List<StockHolding> positions = new ArrayList<StockHolding>();
		try {
			positions = hkSecurityHoldingService.stockHoldingQueryPage(userId, accountId,1, 15);
		} catch (ServiceException e){
			log.error("用户 :" +userId +", 账户 :"+accountId+"--"+"查询持仓失败 ：" +e.getErrorInfo(), e);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		} catch (Exception e){
			log.error("用户 :" +userId +", 账户 :"+accountId+"--"+"查询持仓失败 ：" +e.getMessage(), e);
			model.addAttribute("errMsg", e.getMessage());
			if(e.getCause() instanceof NeedLoginException){
				model.addAttribute(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY, -1);
			}
		}
		model.addAttribute("positions",positions);
		return JSON.toJSONString(model);
	}
	
	@RequestMapping(value="/sellLimit",method=RequestMethod.GET ,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String getSellLimit(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			HttpSessionWrapper session,
			Model model	){
		
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		String userId = getSelfUserId(session);
		String rs ="";
		
		try{
			StockHolding hold = hkSecurityHoldingService.stockHoldingQuery(userId, accountId, stockCode);
			if(hold == null){
				JSONObject json = new JSONObject();
				json.put("errMsg", "您还没有该股票的持仓");
				rs = json.toJSONString();
			}else{
				HkSecurityConfirm stock = hkSecurityCodeService.securityCodeEnter(userId, accountId, stockCode);
				session.removeAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId, stockCode);
				session.removeAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId, hold.getInstrumentName());
				session.removeAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId, hold.getAvailableQuantity());
				//session.removeAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
				//session.setAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId, stock.getMarket().getValue());
				
				JSONObject json=new JSONObject();
				json.put("availableQuantity", hold.getAvailableQuantity());
				json.put("instrumentName", hold.getInstrumentName());
				json.put("lastPrice", hold.getLastPrice());
				if(stock != null){
					json.put("lotsz", stock.getLotsz());
					json.put("sasr", stock.getSasr());
				}
				rs=json.toJSONString();
			}
		}catch(ServiceException e){
			log.error("用户 :" +userId +", 账户 :"+accountId+"--"+"股票卖出限制信息获取失败 ：" +e.getErrorInfo());
			JSONObject json = new JSONObject();
			json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			rs = json.toJSONString();
		}catch(Exception e){
			log.error("用户 :" +userId +", 账户 :"+accountId+"--"+"股票卖出限制信息获取失败 ：" +e.getMessage());
			JSONObject json = new JSONObject();
			json.put("errMsg", e.getMessage());
			if(e.getCause() instanceof NeedLoginException){
				json.put(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY, -1);
			}
			rs = json.toJSONString();
		}
		return rs;
	}
	
	private void deleteSellSession(HttpSessionWrapper session, Long accountId){
		session.removeAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId);		
		session.removeAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_TRADE_CALLBACK_PARAMS + "_" + accountId);
		session.removeAttribute(AttributeKeys.STOCK_SELL_MATCHING_TYPE + "_" + accountId);
	}
}
