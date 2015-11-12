package com.cfo.stock.web.action.trade;

import java.util.Date;

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

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.utils.TokenHelper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.action.password.PasswordShape;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TokenInterceptor.Token;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.common.utils.DateUtil;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.common.BrokerType;
import com.jrj.stocktrade.api.exception.NeedTradePwdException;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.iframe.TradeIframeService;
import com.jrj.stocktrade.api.rpc.HsRpcContext;
import com.jrj.stocktrade.api.stock.SecurityQueryService;
import com.jrj.stocktrade.api.stock.SecurityStockService;
import com.jrj.stocktrade.api.stock.vo.Entrust;
import com.jrj.stocktrade.api.stock.vo.EntrustWithdrawResp;
import com.opensymphony.oscache.util.StringUtil;


@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class EntrustCancelAction  extends AbstractStockBaseAction{
	@Autowired
	SecurityStockService securityStockService;
	@Autowired
	SecurityQueryService securityQueryService;
	@Autowired
	AccountService accountService ;
	@Autowired
	TradeIframeService tradeIframeService;
	
	@RequestMapping(value="/cancelStep2",method=RequestMethod.GET)
	public String cancelStep2(
			@PathVariable Long accountId,
			@RequestParam(value="reportNo" ,defaultValue="")String reportNo,
			HttpSessionWrapper session,
			Model model){
		
		String userId = getSelfUserId(session);
		//按申请单号查询委托单数据
		try {
			Entrust rs = securityQueryService.securityEntrustQueryByEntrustNo(userId, accountId, reportNo);	
			model.addAttribute("rs", rs);
			session.setAttribute(AttributeKeys.STOCK_CANCEL_ENTRUSTNO, rs.getEntrustNo());
		} catch (ServiceException e) {
			log.error("用户 :" +userId +"--"+"查询撤单单号 ：" +e.getErrorInfo());
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		String viewName="/trade/cancelStep2";
		return viewName;
	}
	
	@Token
	@RequestMapping(value="/cancelStep3",method=RequestMethod.POST)
	public String cancelStep3(
			@PathVariable Long accountId,
			@RequestParam(value="stockName" ,defaultValue="")String stockName,
			@RequestParam(value="password" ,defaultValue="")String password,
			@RequestParam(value="userMac" ,defaultValue="")String userMac,
			HttpSessionWrapper session,
			Model model){
				
		String viewName="/trade/cancelStep3";
		if("".equals(userMac)){
			model.addAttribute("errMsg", "你还没有正常安装安全控件");
			return viewName;
		}
		HsRpcContext.setUserMac(userMac);
		String userId = getSelfUserId(session);
		String entrustno =session.getAttribute(AttributeKeys.STOCK_CANCEL_ENTRUSTNO).toString();
		try{
			this.assembleOpFrom(session);
			EntrustWithdrawResp resp =  securityStockService.securityEntrustWithdraw(userId, accountId, password, entrustno);
			model.addAttribute("rs", resp);
			model.addAttribute("stockName", stockName);
			model.addAttribute("nowDate", DateUtil.format(new Date(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"撤单失败失败 ：" +e.getErrorInfo());
			String errMsg=e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", "");
			model.addAttribute("errMsg", errMsg);
		}finally{
			session.removeAttribute(AttributeKeys.STOCK_CANCEL_ENTRUSTNO);
		}
		return viewName;
	}
	
	@RequestMapping(value="/cancelStep4",method=RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@Token
	@ResponseBody
	public String cancelStep(
			@PathVariable Long accountId,
			@RequestParam(value="stockName" ,defaultValue="")String stockName,
			@RequestParam(value="type",required=false) Integer type,
			@RequestParam(value="password" ,defaultValue="")String password,
			@RequestParam(value="userMac" ,defaultValue="")String userMac,
			@RequestParam(value="entrustNo" ,defaultValue="")String entrustNo,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			Model model){
		JSONObject json = new JSONObject();
		json.put("token",TokenHelper.setToken(request,response));
		if(type!=PasswordShape.NONE){
		if(StringUtil.isEmpty(password)){
			json.put("errCode","-3");
			json.put("errMsg", "请输入您的密码");
			return json.toJSONString();
		}
		Broker broker=getBroker(accountId);
		if(PasswordShape.isActive(broker)){
			String mcrypt_key=session.getAttribute("mcrypt_key",String.class);
			if(mcrypt_key==null){
				json.put("errCode","-3");
				json.put("errMsg", "您的交易已过期");
				return  json.toJSONString();
			}
			password=AESWithJCE.getResult(mcrypt_key,password);
			collectClientInfo(request, session);
		}
		}else{
			password="";
		}
		String userId = getSelfUserId(session);
		try{
			this.assembleOpFrom(session);
			EntrustWithdrawResp resp =  securityStockService.securityEntrustWithdraw(userId, accountId, password, entrustNo);
			
			json.put("rs", resp);
			json.put("stockName", stockName);
			json.put("nowDate", DateUtil.format(new Date(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
		}catch(NeedTradePwdException e1){
			json.put("errCode","-3");
			json.put("errMsg", "需要验证交易密码");
		}catch(ServiceException e){
			json.put("errCode",e.getErrorNo());
			log.error("用户 :" +userId +"--"+"撤单失败失败 ：" +e.getErrorInfo());
			json.put("errMsg", "" +e.getErrorInfo().replaceAll("[\\[\\]\\d-]*", ""));
		}finally{
			//session.removeAttribute(AttributeKeys.STOCK_CANCEL_ENTRUSTNO);
		}
		return json.toJSONString();
	}
	
}
