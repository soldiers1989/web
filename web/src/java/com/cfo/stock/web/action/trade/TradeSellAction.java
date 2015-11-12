package com.cfo.stock.web.action.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ocx.AESWithJCE;

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
import com.cfo.stock.web.action.password.PasswordShape;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.SystemStatusInterceptor.GetSysStatus;
import com.cfo.stock.web.interceptor.TokenInterceptor.Token;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.StockUtil;
import com.cfo.stock.web.util.ZQTUtils;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.common.EntrustBs;
import com.jrj.stocktrade.api.common.EntrustProp;
import com.jrj.stocktrade.api.common.ExchangeType;
import com.jrj.stocktrade.api.common.ValueableEnumUtil;
import com.jrj.stocktrade.api.exception.NeedTradePwdException;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.iframe.vo.TradeIframe;
import com.jrj.stocktrade.api.rpc.HsRpcContext;
import com.jrj.stocktrade.api.stock.SecurityCodeService;
import com.jrj.stocktrade.api.stock.SecurityStockService;
import com.jrj.stocktrade.api.stock.StockDataServiceApi;
import com.jrj.stocktrade.api.stock.StockQueryService;
import com.jrj.stocktrade.api.stock.vo.EntrustResp;
import com.jrj.stocktrade.api.stock.vo.SellLimit;
import com.jrj.stocktrade.api.stock.vo.Stock;
import com.jrj.stocktrade.api.stock.vo.StockInfo;
import com.opensymphony.oscache.util.StringUtil;


@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class TradeSellAction  extends AbstractStockBaseAction{
	private static final String SELL_CALLBACK_URL = Global.ZQT_SERVER+"/stock/$accountId/sellCallBack.jspa";
	@Autowired
	StockQueryService stockQueryService;
	@Autowired
	SecurityStockService securityStockService;
	@Autowired
	SecurityCodeService securityCodeService; 
	@Autowired
	StockDataServiceApi stockDataService;
	@GetSysStatus
	@RequestMapping(value={"/sellStep1","/sellStep2","/sellStep3"},method=RequestMethod.GET)
	public String buyStep1(@RequestParam(value="stockCode" ,defaultValue="")String stockCode,Model model){
		
		String viewName="/trade/sellStep1";		
		model.addAttribute("stockCode", stockCode);
		return viewName;
	}
	
	@RequestMapping(value="/sellStep2",method=RequestMethod.POST)
	public String buyStep2(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			@RequestParam(value="entrustPrice" ,defaultValue="")String entrustPrice,
			@RequestParam(value="entrustAmount" ,defaultValue="")String entrustAmount,
			HttpSessionWrapper session,
			Model model){
		
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		entrustPrice = URLUtils.ParamterFilter(entrustPrice, '\0');
		entrustAmount = URLUtils.ParamterFilter(entrustAmount, '\0');
		String viewName=null;
		if(stockCode==null || entrustPrice == null || entrustAmount ==null){
			viewName="/trade/sellStep1";
			model.addAttribute("stockCode",stockCode);
			model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			return viewName;
		}
		viewName="/trade/buyStep2";
		String stockCodes = (String) session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
		if(!stockCode.equals(stockCodes)){
			viewName="/trade/sellStep1";
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			model.addAttribute("errMsg", "股票代码输入错误");
			return viewName;
		}
		BigDecimal count = new BigDecimal(entrustAmount);
		BigDecimal canBuyCount =session.getAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId,BigDecimal.class) ;
		if(count.compareTo(canBuyCount)==1){
			viewName="/trade/sellStep1";
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			model.addAttribute("errMsg", "下单数量大于最大可买");
			return viewName;
		}
		session.setAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId, entrustPrice);
		session.setAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId, entrustAmount);
		
		model.addAttribute("stockCode", stockCode);
		model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
		model.addAttribute("entrustPrice", entrustPrice);
		model.addAttribute("entrustAmount", entrustAmount);
		
		String brokerId = getBrokerId(accountId);
		switch(ZQTUtils.webTradeType(brokerId)){
			case ORIGINAL://中信
				viewName="/trade/sellStep2";
				break;
			case IFRAME://中山
				viewName="/trade/sellStep2_iframe";
				String userId  = getSelfUserId(session);
				String tradeType = (String)session.getAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
				ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
				BigDecimal amont =new BigDecimal(entrustAmount);
				BigDecimal price = new BigDecimal(entrustPrice);
				TradeIframe tradeIframe = null;
				try {
					tradeIframe = tradeIframeService.securityEntrust(userId, accountId, EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.SELL, exType, SELL_CALLBACK_URL.replaceAll("\\$accountId", accountId.toString()));
				} catch (ServiceException e) {
					log.error("exception",e);
				}
				if(tradeIframe != null){
					model.addAttribute("url", tradeIframe.getUrl());
					model.addAttribute("params",tradeIframe.getParam());
				}
				break;
		}
		
		return viewName;

	}
	
	@RequestMapping(value="/ajaxSellStep2",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxSellStep2(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			@RequestParam(value="entrustPrice" ,defaultValue="")String entrustPrice,
			@RequestParam(value="entrustAmount" ,defaultValue="")String entrustAmount,
			HttpSessionWrapper session,
			Model model){
		
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		entrustPrice = URLUtils.ParamterFilter(entrustPrice, '\0');
		entrustAmount = URLUtils.ParamterFilter(entrustAmount, '\0');
		
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
		
		json.put("stockCode", stockCode);
		json.put("stockName", session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
		json.put("entrustPrice", entrustPrice);
		json.put("entrustAmount", entrustAmount);
		
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String result=json.toJSONString();
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
				result = json.toJSONString();
				break;
			case IFRAME://中山
				
				String userId  = getSelfUserId(session);
				String tradeType = (String)session.getAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
				ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
				BigDecimal amont =new BigDecimal(entrustAmount);
				BigDecimal price = new BigDecimal(entrustPrice);
				TradeIframe tradeIframe = null;
				try {
					tradeIframe = tradeIframeService.securityEntrust(userId, accountId, EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.SELL, exType, SELL_CALLBACK_URL.replaceAll("\\$accountId", accountId.toString()));
				} catch (ServiceException e) {
					log.error("exception",e);
				}
				if(tradeIframe != null){
					json.put("url", tradeIframe.getUrl());
					json.put("params",tradeIframe.getParam());
				}
				result = json.toJSONString();
				break;
		default:
			break;
		}
		
		return result;

	}
	
	@Token
	@RequestMapping(value="/sellStep3",method=RequestMethod.POST)
	public String buyStep3(
			@PathVariable Long accountId,
			@RequestParam(value="password" ,defaultValue="")String password,
			@RequestParam(value="userMac" ,defaultValue="")String userMac,
			HttpSessionWrapper session,
			Model model){
		
		password = URLUtils.ParamterFilter(password, '\0');
		userMac = URLUtils.ParamterFilter(userMac, '\0');
		if("".equals(userMac)){
			String viewName="/trade/sellStep3";
			model.addAttribute("errMsg", "你还没有正常安装安全控件");
			return viewName;
		}
		if(session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId)==null){
			String viewName="/trade/sellStep3";
			model.addAttribute("errMsg", "您的交易已过期");	
			return viewName;
		}		
		String viewName="/trade/sellStep3";
		HsRpcContext.setUserMac(userMac);
		String userId  = getSelfUserId(session);
		String stockCode = (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
		BigDecimal amont =new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId));
		BigDecimal price = new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId));
		String tradeType = (String)session.getAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
		ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
		try{
				this.assembleOpFrom(session);
			EntrustResp rs = securityStockService.securityEntrust(userId, accountId, password, EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.SELL,exType);
			model.addAttribute("result", rs);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId).toString());
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"卖出股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("stockCode", stockCode);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}finally{
			deleteSellSession(session, accountId);
		}	
		return viewName;
	}
	@RequestMapping(value="/ajaxSellStep3",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@Token
	@ResponseBody
	public String ajaxSellStep3(
			@PathVariable Long accountId,
			@RequestParam(value="type",required=false) Integer type,
			@RequestParam(value="password" ,defaultValue="")String password,
			@RequestParam(value="userMac" ,defaultValue="")String userMac,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model){
		
		model.addAttribute("token",TokenHelper.setToken(request,response));
		if(session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId)==null){
			model.addAttribute("errCode","-1");
			model.addAttribute("errMsg", "您的交易已过期");	
			return JSON.toJSONString(model);
		}		
		if(type!=PasswordShape.NONE){
			if(StringUtil.isEmpty(password)){
				model.addAttribute("errCode","-2");
				model.addAttribute("errMsg", "请输入您的密码");
				return JSON.toJSONString(model);
			}
			Broker broker=getBroker(accountId);
			if(PasswordShape.isActive(broker)){
				String mcrypt_key=session.getAttribute("mcrypt_key",String.class);
				if(mcrypt_key==null){
					model.addAttribute("errCode","-1");
					model.addAttribute("errMsg", "您的交易已过期");
					return  JSON.toJSONString(model);
				}
				password=AESWithJCE.getResult(mcrypt_key,password);
				collectClientInfo(request, session);
			}
		}else{
			password="";
		}
		String userId  = getSelfUserId(session);
		String stockCode = (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
		BigDecimal amont =new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId));
		BigDecimal price = new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId));
		String tradeType = (String)session.getAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
		ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
		try{
			this.assembleOpFrom(session);
			EntrustResp rs = securityStockService.securityEntrust(userId, accountId, password, EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.SELL,exType);
			model.addAttribute("result", rs);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId).toString());
			deleteSellSession(session, accountId);
		}catch(NeedTradePwdException e1){
			model.addAttribute("errCode","-3");
			model.addAttribute("errMsg", "需要验证交易密码");
			return  JSON.toJSONString(model);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"卖出股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("stockCode", stockCode);
			String errMsg=e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", "");
			model.addAttribute("errCode",e.getErrorNo());
			//model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("errMsg", errMsg);
		}	
		return JSON.toJSONString(model);
	}
	
	@RequestMapping(value="sellCallBack",method=RequestMethod.GET )
	public String sellCallBack(HttpServletRequest request,
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
//		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String,Object> param=new HashMap<String, Object>();
		Enumeration<String> it = request.getParameterNames();
		String name=null;
		while(it.hasMoreElements()){
			name = it.nextElement();
			param.put(name, request.getParameter(name));
		}
		session.setAttribute(AttributeKeys.STOCK_TRADE_CALLBACK_PARAMS + "_" + accountId, param);
		return "trade/callBack";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="sellCallBack",method=RequestMethod.POST )
	@Token
	public String sellCallBack(
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		
		String viewName="trade/sellStep3";
		Map<String, Object> paramMap=(Map<String, Object>) session.getAttribute(AttributeKeys.STOCK_TRADE_CALLBACK_PARAMS + "_" + accountId);
		if(paramMap == null){
			model.addAttribute("errMsg", "您的交易已过期");
			return viewName;
		}
		
		String userId  = getSelfUserId(session);
		try{
			this.assembleOpFrom(session);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_SELL_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_SELL_COUNT + "_" + accountId).toString());
			EntrustResp rs = tradeIframeService.securityEntrustReceive(userId, accountId, paramMap);
			model.addAttribute("result", rs);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"买入股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}finally{
			deleteSellSession(session, accountId);
		}
		
		return viewName;
	}

	@RequestMapping(value="/ajaxQueryPosition",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxQueryPosition(
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model
			){
		String userId=getSelfUserId(session);
		List<StockInfo> positions = new ArrayList<StockInfo>();
		try {
			positions = stockQueryService.unsafeSecurityStockFastQuery(userId, accountId,1, 15);
		} catch (ServiceException e){
			log.error(e.getErrorInfo());
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
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
			Stock stock=stockDataService.queryStock(stockCode);
			if(stock==null){
				JSONObject json = new JSONObject();
				json.put("errMsg", "暂无法识别该股票代码");
			}else{
				EntrustProp prop=EntrustProp.BUYSELL;
				if(stock.getType()==7){
					prop=EntrustProp.BACK_ORDER;
				}
				ExchangeType exType=null;
				if(stock.getMarktType()==1){
					exType= ExchangeType.SZ;
				}else if(stock.getMarktType()==2){
					exType= ExchangeType.SH;
				}
		SellLimit limit=	securityCodeService.securityEntrustSellLimit(userId, accountId, prop, exType, stockCode);
				JSONObject result=new JSONObject();
				result.put("stockName", stock.getSname());
				result.put("stockCode", stock.getCode());
				result.put("enableAmount", limit.getEnableAmount());
				rs =result.toJSONString();
				session.removeAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_STOCKCODE + "_" + accountId, stockCode);
				session.removeAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_STOCKNAME + "_" + accountId, stock.getSname());
				session.removeAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_ENABLE+ "_" + accountId, limit.getEnableAmount());
				session.removeAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId);
				session.setAttribute(AttributeKeys.STOCK_SELL_TRADE_TYPE + "_" + accountId,exType==null?"": exType.getValue());
			}
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"股票卖出限制信息获取失败 ：" +e.getErrorInfo());
			JSONObject json = new JSONObject();
			json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
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
	}
}
