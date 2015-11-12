package com.cfo.stock.web.action.trade;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ocx.AESWithJCE;

import org.apache.commons.collections.functors.NonePredicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.security.util.Password;

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
import com.jrj.stocktrade.api.common.BrokerType;
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
import com.jrj.stocktrade.api.stock.vo.BuyLimit;
import com.jrj.stocktrade.api.stock.vo.EntrustResp;
import com.jrj.stocktrade.api.stock.vo.SecurityCodeConfirm;
import com.opensymphony.oscache.util.StringUtil;


@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class TradeBuyAction  extends AbstractStockBaseAction{
	
	private final static String 	BUY_CALLBACK_URL=Global.ZQT_SERVER+"/stock/$accountId/buyCallBack.jspa";
	
	@Autowired
	SecurityCodeService securityCodeService; 
	@Autowired
	SecurityStockService securityStockService; 
	
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
		String viewName="/trade/buyStep1";
		model.addAttribute("stockCode", stockCode);
		return viewName;
	}
	@RequestMapping("buystep1test")
	public String step1test(){
		return "trade/buyStep1test";
	}
	@RequestMapping(value="/buyStep2",method=RequestMethod.POST)
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

		if(stockCode==null || entrustPrice == null || entrustAmount ==null){
			String viewName="/trade/buyStep1";
			model.addAttribute("stockCode",stockCode);
			model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			return viewName;
		}
		
		String stockCodes = (String) session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
		if(!stockCode.equals(stockCodes)){
			String viewName="/trade/buyStep1";
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			model.addAttribute("errMsg", "股票代码输入错误");
			return viewName;
		}
		BigDecimal count = new BigDecimal(entrustAmount);
		BigDecimal canBuyCount =session.getAttribute(AttributeKeys.STOCK_BUY_ENABLE , BigDecimal.class);
		if(count.compareTo(canBuyCount) > 0){
			String viewName="/trade/buyStep1";
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			model.addAttribute("errMsg", "下单数量大于最大可买");
			return viewName;
		}
		session.setAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId, entrustPrice);
		session.setAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId, entrustAmount);
		
		model.addAttribute("stockCode", stockCode);
		model.addAttribute("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
		model.addAttribute("entrustPrice", entrustPrice);
		model.addAttribute("entrustAmount", entrustAmount);
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
				viewName="/trade/buyStep2";
				break;
			case IFRAME://中山
				viewName="trade/buyStep2_iframe";
				String userId  = getSelfUserId(session);
				String marketType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
				String stockType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
				String tradeType = getStockType(marketType, stockType);
				ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
				BigDecimal amont =new BigDecimal(entrustAmount);
				BigDecimal price = new BigDecimal(entrustPrice);
				TradeIframe tradeIframe = null;
				try {
					tradeIframe = tradeIframeService.securityEntrust(userId, accountId, EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.BUY, exType, BUY_CALLBACK_URL.replaceAll("\\$accountId", accountId.toString()));
				} catch (ServiceException e) {
					log.error("exception",e);
				}
				if(tradeIframe != null){
					model.addAttribute("url", tradeIframe.getUrl());
					model.addAttribute("params",tradeIframe.getParam());
				}
				break;
		default:
			break;
		}
		return viewName;
	}
	@RequestMapping(value="/ajaxBuyStep2",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxBuyStep2(
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
			//String viewName="/trade/buyStep1";
			json.put("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			json.put("errMsg", "下单数量大于最大可买");
			return json.toJSONString();
		}
		session.setAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId, entrustPrice);
		session.setAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId, entrustAmount);
		
		json.put("stockCode", stockCode);
		json.put("stockName", session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
		json.put("entrustPrice", entrustPrice);
		json.put("entrustAmount", entrustAmount);
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String result=json.toJSONString();
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
				result=json.toJSONString();
				break;
			case IFRAME://中山
				
				String userId  = getSelfUserId(session);
				String marketType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
				String stockType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
				String tradeType = getStockType(marketType, stockType);
				ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
				BigDecimal amont =new BigDecimal(entrustAmount);
				BigDecimal price = new BigDecimal(entrustPrice);
				TradeIframe tradeIframe = null;
				try {
					tradeIframe = tradeIframeService.securityEntrust(userId, accountId, EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.BUY, exType, BUY_CALLBACK_URL.replaceAll("\\$accountId", accountId.toString()));
				} catch (ServiceException e) {
					json.put("errMsg", e.getMessage());
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
	@RequestMapping(value="/buyStep3",method=RequestMethod.POST)
	public String buyStep3(
			@PathVariable Long accountId,
			@RequestParam(value="password" ,defaultValue="")String password,
			@RequestParam(value="userMac" ,defaultValue="")String userMac,
			HttpSessionWrapper session,
			Model model){
		
		password = URLUtils.ParamterFilter(password, '\0');
		userMac = URLUtils.ParamterFilter(userMac, '\0');
		if("".equals(userMac)){
			String viewName="/trade/buyStep3";
			model.addAttribute("errMsg", "你还没有正常安装安全控件");
			return viewName;
		}
		if(session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId)==null){
			String viewName="/trade/buyStep3";
			model.addAttribute("errMsg", "您的交易已过期");
			return viewName;
		}		
		String viewName="/trade/buyStep3";
		HsRpcContext.setUserMac(userMac);
		String userId  = getSelfUserId(session);
		//UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		//SecuritiesBroker sb = SecuritiesBroker.getBroker(accUserInfo.getBrokerId());
		
		String stockCode = (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
		BigDecimal amont =new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId));
		BigDecimal price = new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId));
		String marketType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
		String stockType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
		String tradeType = getStockType(marketType, stockType);
		ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
		try{
			this.assembleOpFrom(session);
			EntrustResp rs = securityStockService.securityEntrust(userId, accountId, password , EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.BUY,exType);
			model.addAttribute("result", rs);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId).toString());
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"买入股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("stockCode", stockCode);
			model.addAttribute("marketType", marketType);
			model.addAttribute("stockType", stockType);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}finally{
			deleteBuySession(session, accountId);
		}	
		return viewName;
	}
	
	@RequestMapping(value="/ajaxBuyStep3",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@Token
	@ResponseBody
	public String ajaxBuyStep3(
			@PathVariable Long accountId,
			@RequestParam(value="type",required=false) Integer type,
			@RequestParam(value="password" ,defaultValue="")String password,
			@RequestParam(value="userMac" ,defaultValue="")String userMac,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model){
		
		model.addAttribute("token",TokenHelper.setToken(request,response));
		if(session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId)==null 
				|| session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId)==null){
			model.addAttribute("errCode","-1");
			model.addAttribute("errMsg", "您的交易已过期");
			return  JSON.toJSONString(model);
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
		//UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		//SecuritiesBroker sb = SecuritiesBroker.getBroker(accUserInfo.getBrokerId());
		
		String stockCode = (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
		BigDecimal amont =new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId));
		BigDecimal price = new BigDecimal((String)session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId));
		String marketType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
		String stockType=(String)session.getAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
		String tradeType = getStockType(marketType, stockType);
		ExchangeType  exType = (ExchangeType)ValueableEnumUtil.getEnum(tradeType, ExchangeType.class);
		try{
			this.assembleOpFrom(session);
			EntrustResp rs = securityStockService.securityEntrust(userId, accountId, password , EntrustProp.BUYSELL, stockCode, amont, price, EntrustBs.BUY,exType);
			model.addAttribute("result", rs);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId).toString());
			deleteBuySession(session, accountId);
		}catch(NeedTradePwdException e1){
			model.addAttribute("errCode","-3");
			model.addAttribute("errMsg", "需要验证交易密码");
			return  JSON.toJSONString(model);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"买入股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("stockCode", stockCode);
			model.addAttribute("marketType", marketType);
			model.addAttribute("stockType", stockType);
			model.addAttribute("errCode",e.getErrorNo());
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		return JSON.toJSONString(model);
	}
	@RequestMapping(value="buyCallBack",method=RequestMethod.GET )
	public String buyCallBack(HttpServletRequest request, 
			HttpSessionWrapper session,
			@PathVariable Long accountId,
			Model model){
//		Map<String, String[]> paramMap = request.getParameterMap();
		Map<String,Object> param=new HashMap<String, Object>();
		Enumeration<String> it = request.getParameterNames();
		String name=null;
		while(it.hasMoreElements()){
			name = it.nextElement();
			param.put(name, request.getParameter(name));
		}
		log.info("param:"+JSONObject.toJSONString(param));
		String userId  = getSelfUserId(session);
		model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
		model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
		model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId).toString());
		model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId).toString());
		try {
			EntrustResp rs = tradeIframeService.securityEntrustReceive(userId, accountId, param);
			model.addAttribute("result", rs);
		} catch (ServiceException e) {
			log.error("用户 :" +userId +"--"+"买入股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		
		return "trade/callBack";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="buyCallBack",method=RequestMethod.POST )
	@Token
	public String buyCallBack(
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		
		String viewName="trade/buyStep3";
		Map<String, Object> paramMap=(Map<String, Object>) session.getAttribute(AttributeKeys.STOCK_TRADE_CALLBACK_PARAMS + "_" + accountId);
		if(paramMap == null){
			model.addAttribute("errMsg", "您的交易已过期");
			return viewName;
		}
		
		String userId  = getSelfUserId(session);
		
		try{
			this.assembleOpFrom(session);
			model.addAttribute("stockCode", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId));
			model.addAttribute("stockName", (String)session.getAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId));
			model.addAttribute("entrustPrice",session.getAttribute(AttributeKeys.STOCK_BUY_PRICE + "_" + accountId).toString());
			model.addAttribute("entrustAmont", session.getAttribute(AttributeKeys.STOCK_BUY_COUNT + "_" + accountId).toString());
			EntrustResp rs = tradeIframeService.securityEntrustReceive(userId, accountId, paramMap);
			model.addAttribute("result", rs);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"买入股票失败 ：" +e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}finally{
			deleteBuySession(session, accountId);
		}
		
		return viewName;
	}
	
	@RequestMapping(value="/stockCheck",method=RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String stockCheck(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode" ,defaultValue="")String stockCode,
			@RequestParam(value="marketType" ,defaultValue="")String marketType,
			@RequestParam(value="stockType" ,defaultValue="")String stockType,
			HttpSessionWrapper session,
			Model model){
		stockCode = URLUtils.ParamterFilter(stockCode, '\0');
		marketType = URLUtils.ParamterFilter(marketType, '\0');
		stockType = URLUtils.ParamterFilter(stockType, '\0');
		String userId = getSelfUserId(session);
		String type = getStockType(marketType,stockType);
		if(type == null){
			JSONObject json = new JSONObject();
			json.put("errMsg", "对不起，暂不支持该股票代码的交易类型");
			return json.toJSONString();
		}
		ExchangeType exType =(ExchangeType)ValueableEnumUtil.getEnum(type, ExchangeType.class);
		EntrustProp prop=EntrustProp.BUYSELL;
		if("r".equals(stockType)){
			prop=EntrustProp.BACK_ORDER;
		}
		String rs="";
		try{
			SecurityCodeConfirm codeConfirm = securityCodeService.securityCodeEnter(userId, accountId, prop, exType, stockCode);
			session.removeValue(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_STOCKNAME + "_" + accountId, codeConfirm.getStockName());
			rs = JSONObject.toJSONString(codeConfirm);

		}catch(ServiceException e){
			log.error(e.getErrorInfo()); 
			JSONObject json = new JSONObject();
			json.put("errMsg",StockUtil.getErrorInfo(e.getErrorInfo()));
			rs = json.toJSONString();
		}catch (Exception e) {
			e.printStackTrace();
			JSONObject json = new JSONObject();
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
		String type = getStockType(marketType,stockType);
		ExchangeType exType =(ExchangeType)ValueableEnumUtil.getEnum(type, ExchangeType.class);
		BigDecimal price = new BigDecimal(entrustPrice);
		String rs ="";
		if(price.compareTo(BigDecimal.ZERO)==0){
			JSONObject json = new JSONObject();
			json.put("errMsg", "");
			rs = json.toJSONString();
			return rs;
		}
		EntrustProp prop=EntrustProp.BUYSELL;
		if("r".equals(stockType)){
			prop=EntrustProp.BACK_ORDER;
		}
		try{
			BuyLimit limit = securityCodeService.securityEntrustBuyLimit(userId, accountId, prop, exType, stockCode, price);
			rs = JSONObject.toJSONString(limit);
			//每次调整价格 将可以购买的数量存入session
			session.removeAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_STOCKCODE + "_" + accountId, stockCode);
			session.removeAttribute(AttributeKeys.STOCK_BUY_ENABLE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_ENABLE + "_" + accountId, limit.getEnableBuyAmount());
			session.removeAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_TRADE_MARKET_TYPE + "_" + accountId, marketType);
			session.removeAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId);
			session.setAttribute(AttributeKeys.STOCK_BUY_TRADE_STOCK_TYPE + "_" + accountId, stockType);
			
		}catch(ServiceException e){
			log.error(e.getErrorInfo()); 
			JSONObject json = new JSONObject();
			String errMsg=e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", "");
			//json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			json.put("errMsg", errMsg);
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
	}
	private static String getStockType(String marketType,String stockType ){
		try{			
			stockType = stockType.split("\\.")[1];
		}catch(Exception e ){
			stockType = "";
		}
		if("cn.sh".equals(marketType)){
			if("sb".equals(stockType)){
				return "D";
			}else{
				return "1";
			}
		}
		if("cn.sz".equals(marketType)){
			if("sb".equals(stockType)){
				return "H";
			}else{
				return "2";
			}
		}
		return "2";			
	}

}
