package com.cfo.stock.web.action.brokerBank;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ocx.AESWithJCE;

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
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.action.password.PasswordShape;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.util.StockUtil;
import com.cfo.stock.web.util.ZQTUtils;
import com.cfo.stock.web.vo.ErrorInfo;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.banktrans.BankHistoryQueryService;
import com.jrj.stocktrade.api.banktrans.BankTransferQueryService;
import com.jrj.stocktrade.api.banktrans.BankTransferService;
import com.jrj.stocktrade.api.banktrans.vo.BankTransfer;
import com.jrj.stocktrade.api.banktrans.vo.HistoryBankTransfer;
import com.jrj.stocktrade.api.banktrans.vo.TransResp;
import com.jrj.stocktrade.api.common.BrokerType;
import com.jrj.stocktrade.api.common.BusinessType;
import com.jrj.stocktrade.api.common.MoneyType;
import com.jrj.stocktrade.api.common.SecurityQueryType;
import com.jrj.stocktrade.api.common.TransferDirection;
import com.jrj.stocktrade.api.common.ValueableEnumUtil;
import com.jrj.stocktrade.api.deposit.BankFundService;
import com.jrj.stocktrade.api.deposit.vo.FundAmtQueryResp;
import com.jrj.stocktrade.api.exception.NeedTradePwdException;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.iframe.vo.TradeIframe;
import com.jrj.stocktrade.api.pub.vo.PasswordFlag;

@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class BankAction  extends BrokerBaseAction{
	private static final String BANK_TRANSFER_CALL_BACK_URL=Global.ZQT_SERVER + "/stock/$accountId/transferCallBack.jspa";
	private static final int ps =15;
	@Autowired
	BankTransferService bankTransferService;
	@Autowired
	BankHistoryQueryService bankHistoryQueryService;
	@Autowired
	BankTransferQueryService bankTransferQueryService;
	@Autowired
	BankFundService bankFundService;
	/**
	 * 资金转入
	 * @return
	 */
	@RequestMapping(value="/bankIndex",method=RequestMethod.GET)
	public String bankRollIn(@RequestParam(value="transferType" ,defaultValue="1" )String transferType,
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		
		transferType = URLUtils.ParamterFilter(transferType, '\0');
		model.addAttribute("type", transferType);
		
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);

		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
		case ORIGINAL://原生
			try {
				initBroker(Integer.parseInt(transferType), accountId, session, model);
			} catch (ServiceException e) {
				log.error(e.getErrorInfo());
				model.addAttribute("", "获取证券账户信息错误");
			}		
			viewName="/bank/brokerBankTransfer";
			break;
		case IFRAME://中山
			viewName="/bank/brokerBankTransfer_iframe";
			String userId  = getSelfUserId(session);
			TransferDirection direct =TransferDirection.getTransferDirection(Integer.parseInt(transferType));
			TradeIframe re=null;
			try {
				re = tradeIframeService.bankTransfer(userId, accountId, direct, BANK_TRANSFER_CALL_BACK_URL.replaceAll("\\$accountId", accountId.toString()));
			} catch (ServiceException e) {
				log.error(e.getErrorInfo(),e);
			}
			if(re != null){
				model.addAttribute("url", re.getUrl());
				model.addAttribute("params",re.getParam());
			}
			break;
		}
		
		//获取该证券绑定的所有银行卡
		return viewName;
	}
	/**
	 * 资金转出
	 * @return
	 */
	@RequestMapping(value="/bankRollOut",method=RequestMethod.GET)
	public String bankRollOut(@RequestParam(value="transferType" ,defaultValue="2" )String transferType,
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		
		transferType = URLUtils.ParamterFilter(transferType, '\0');
		model.addAttribute("type", transferType);
		
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
		case ORIGINAL://中信
			try {
				initBroker(Integer.parseInt(transferType), accountId, session, model);
			} catch (ServiceException e) {
				log.error(e.getErrorInfo());
				model.addAttribute("", "获取证券账户信息错误");
			}		
			viewName="/bank/brokerBankTransferOut";
			break;
		case IFRAME://中山
			viewName="/bank/brokerBankTransfer_iframe";
			String userId  = getSelfUserId(session);
			TransferDirection direct =TransferDirection.getTransferDirection(Integer.parseInt(transferType));
			TradeIframe re=null;
			try {
				re = tradeIframeService.bankTransfer(userId, accountId, direct, BANK_TRANSFER_CALL_BACK_URL.replaceAll("\\$accountId", accountId.toString()));
			} catch (ServiceException e) {
				log.error(e.getErrorInfo(),e);
			}
			if(re != null){
				model.addAttribute("url", re.getUrl());
				model.addAttribute("params",re.getParam());
			}
			break;
		}
		
		//获取该证券绑定的所有银行卡
		return viewName;
	}
	
	/**
	 * 银转回调
	 * @return
	 */
	@RequestMapping(value="transferCallBack")
	public String transferCallBack(
			@PathVariable Long accountId,
			HttpServletRequest request,
			HttpSessionWrapper session,
			Model model){
		
		
		Map<String, Object> data=new HashMap<String, Object>();
//		Map<String, String[]> paramMap = request.getParameterMap();
		Enumeration<String> it = request.getParameterNames();
		String name=null;
		while(it.hasMoreElements()){
			name = it.nextElement();
			data.put(name, request.getParameter(name));
		}
		log.info("param:"+JSONObject.toJSONString(data));
		String userId = getSelfUserId(session);
		
		TransResp re=null;
		try {
			re = tradeIframeService.bankTransferReceive(userId, accountId, data);
		} catch (ServiceException e) {
			log.error(e.getErrorInfo(),e);
			model.addAttribute("errMsg", e.getErrorInfo());
		}
		if(re != null){
			model.addAttribute("result",re);
		}
		String viewName="/bank/callBack";
		return viewName;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/bankTransfer",method=RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String bankInOut(
			@PathVariable Long accountId,
			@RequestParam(value="transferType" ,defaultValue="" )String transferType,
			@RequestParam(value="bankNo" ,defaultValue="" )String bankNo,
			@RequestParam(value="balance" ,defaultValue="" )String balance,
			@RequestParam(value="bankPassword" ,defaultValue="" )String bankPassword,
			@RequestParam(value="fundPassword" ,defaultValue="" )String fundPassword,
			@RequestParam(value="tradePassword" ,defaultValue="" )String tradePassword,
			@RequestParam(value="userMac" ,defaultValue="" )String userMac,
			@RequestParam(value="bankAcc",required=false)String bankAcc,
			HttpServletRequest request,
			HttpSessionWrapper session,
			Model model){
		JSONObject json = new JSONObject();
		
		transferType = URLUtils.ParamterFilter(transferType);
		bankNo = URLUtils.ParamterFilter(bankNo);
		balance = URLUtils.ParamterFilter(balance);
		transferType = URLUtils.ParamterFilter(transferType);
		Broker broker=getBroker(accountId);
		if(PasswordShape.isActive(broker)){
			String mcrypt_key=session.getAttribute("mcrypt_key",String.class);
			if(mcrypt_key==null){
				json.put("errCode","-1");
				json.put("errMsg", "您的交易已过期");
				return  json.toJSONString();
			}
			if(!StringUtils.isEmpty(bankPassword)){
			bankPassword=AESWithJCE.getResult(mcrypt_key,bankPassword);
			}
			if(!StringUtils.isEmpty(fundPassword)){
			fundPassword=AESWithJCE.getResult(mcrypt_key,fundPassword);
			}
			if(!StringUtils.isEmpty(tradePassword)){
			tradePassword=AESWithJCE.getResult(mcrypt_key,tradePassword);
			}
			collectClientInfo(request, session);
		}
		String userId = getSelfUserId(session);
		BigDecimal occurBalance = new BigDecimal(balance);
		TransferDirection direct =(TransferDirection)ValueableEnumUtil.getEnum(transferType, TransferDirection.class);
		String rs="";		
		try {
			this.assembleOpFrom(session);
			TransResp transResp = bankTransferService.bankTransfer(userId, accountId, tradePassword, MoneyType.CNY, bankNo, occurBalance, direct, bankPassword, fundPassword,bankAcc);
			rs = JSONObject.toJSONString(transResp);
			model.addAttribute("rs", transResp);
		}catch(NeedTradePwdException e1){
			json.put("errCode","-3");
			json.put("errMsg", "需要验证交易密码");
			rs = json.toJSONString();
		} catch (ServiceException e) {
			log.error(e.getErrorInfo()); 
			json.put("errCode", e.getErrorNo());
			json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			rs = json.toJSONString();
		}
		return rs;
	}
	//当日流水查询
	@RequestMapping(value="/fundQuery",method=RequestMethod.GET)
	public String fundQuery(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="" )String startDate,
			@RequestParam(value="endDate" ,defaultValue="" )String endDate,
			HttpSessionWrapper session,
			Model model){
		
		String userId = getSelfUserId(session);
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
		case ORIGINAL://中信
			startDate = URLUtils.ParamterFilter(startDate, '\0');
			endDate = URLUtils.ParamterFilter(endDate, '\0');
			viewName="/bank/fundQuery";
	//		Date now = new Date();
	//		startDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-604800000l));
	//		endDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-86400000));
	//		List<HistoryBankTransfer> list = new ArrayList<HistoryBankTransfer>();
			List<BankTransfer> list = new ArrayList<BankTransfer>();
			try {
	//			 list = bankHistoryQueryService.unsafeBankTransferHistoryQuery(userId,"",sb, Integer.parseInt(startDate), Integer.parseInt(endDate), 1, ps);		
				list=bankTransferQueryService.unsafeBankTransferQuery(userId, accountId, "",  1, ps);
			} catch (ServiceException e){
				log.error(e.getErrorInfo(),e); 
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			}
			try {
				initBroker(-1, accountId, session,model);
			} catch (ServiceException e) {
				log.error(e.getErrorInfo(),e); 
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			}
			model.addAttribute("queryList", list);
			model.addAttribute("pageNum", 1);
			break;
		case IFRAME://中山
			try {
				TradeIframe tradeIframe = tradeIframeService.securityQuery(userId,accountId, SecurityQueryType.EXCHANGE,null,null);
				model.addAttribute("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			} catch (ServiceException e) {
				log.error("中山，用户 :" +userId +"--"+"转账查询失败 ：" +e.getErrorInfo(),e);
			}
			viewName="/bank/fundQuery_iframe";
			break;
		}
		return viewName;
	}
	//历史流水查询ajax
	@RequestMapping(value="/searchBankFund",method=RequestMethod.GET)
	public String excute(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="pageNum" ,defaultValue="1" )String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		
		pageNum = URLUtils.ParamterFilter(pageNum, '\0');
		startDate = URLUtils.ParamterFilter(startDate, '\0');
		endDate = URLUtils.ParamterFilter(endDate, '\0');
		
		//指定时间输出格式
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Date dt = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);        
        //判断查询起止时间
        if("week".equals(selecttype)){//一周
        	rightNow.add(Calendar.DAY_OF_YEAR,-7);
        	startDate =sdf.format(rightNow.getTime());
        	endDate =sdf.format(dt);
        	model.addAttribute("startDate","");
    		model.addAttribute("endDate","");
        }else if("month".equals(selecttype)){//一个月
        	rightNow.add(Calendar.DAY_OF_YEAR,-30);
       	 	startDate =sdf.format(rightNow.getTime());
       	 	endDate =sdf.format(dt);
       	 	model.addAttribute("startDate","");
       	 	model.addAttribute("endDate","");
        }else{
    		model.addAttribute("startDate",startDate);
    		model.addAttribute("endDate",endDate);
        }
        model.addAttribute("selecttype",selecttype);
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			String viewName="/error";
			ErrorInfo info = new ErrorInfo();
			info.setBusinessName("银证转账流水查询");
			info.setErrorInfo("查询起止时间间隔大于90天");
			info.setReturnName("我的证券账户");
			info.setReturnUrl("/stock/fundQuery.jspa");
			model.addAttribute("errorInfo", info);
			return viewName;
		}
		String viewName="/piece/bankfund";
		startDate = StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);

		
		String userId = getSelfUserId(session);
		List<HistoryBankTransfer> list = new ArrayList<HistoryBankTransfer>();
		List<BankTransfer> listBankTransfer = new ArrayList<BankTransfer>();
		try {
			list = bankHistoryQueryService.unsafeBankTransferHistoryQuery(userId, accountId, "", Integer.parseInt(startDate), Integer.parseInt(endDate),Integer.parseInt(pageNum), 15);
			for(HistoryBankTransfer h :list){
				BankTransfer b=new BankTransfer();
				b.setInitDate(h.getInitDate());
				b.setEntrustTime(h.getEntrustTime());
				b.setBankName(h.getBankName());
				b.setBusinessType(h.getBusinessType());
				b.setOccurBalance(h.getOccurBalance());
				b.setEntrustStatus(h.getEntrustStatus());
				listBankTransfer.add(b);
			}
			
		} catch (ServiceException e){
			log.error(e.getErrorInfo(),e); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		model.addAttribute("queryList", listBankTransfer);
		model.addAttribute("pageNum", pageNum);
		return viewName;
	}
	//当日流水查询ajax
	@RequestMapping(value="/bankFundPage",method=RequestMethod.GET)
	public String excuteNext(
			@PathVariable Long accountId,
			@RequestParam(value="pageNum" ,defaultValue="1" )String pageNum,
			HttpSessionWrapper session,
			Model model){
		pageNum = URLUtils.ParamterFilter(pageNum, '\0');
		String viewName="piece/bankFund";
		String userId = getSelfUserId(session);
		List<BankTransfer> list = new ArrayList<BankTransfer>();
		try {
			//list = bankHistoryQueryService.unsafeBankTransferHistoryQuery(userId,"", sb, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), ps);
			list=bankTransferQueryService.unsafeBankTransferQuery(userId, accountId,  "", Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo(),e); 
		}
		model.addAttribute("queryList", list);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		return viewName;
	}
	
	@RequestMapping(value="/initBalance",method=RequestMethod.GET)
	public String initBalanceQuery(
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		
		try {
			initBroker(3, accountId, session,model);
		} catch (ServiceException e) {
			log.error(e.getErrorInfo(),e); 
			model.addAttribute("errMsg", "获取证券账户信息错误");
		}
		String viewName="bank/balanceQuery";
		return viewName;
		
	}
	
	@RequestMapping(value="/bank_balance_Query",method=RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String balanceQuery(
			@PathVariable Long accountId,
			@RequestParam(value="bankNo" ,defaultValue="" )String bankNo ,
			@RequestParam(value="bankPassword" ,defaultValue="" )String bankPassword,
			@RequestParam(value="fundPassword" ,defaultValue="" )String fundPassword,
			@RequestParam(value="tradePassword" ,defaultValue="" )String tradePassword,
			@RequestParam(value="userMac" ,defaultValue="" )String userMac,
			@RequestParam(value="bankAcc",required=false)String bankAcc,
			HttpServletRequest request,
			HttpSessionWrapper session,
			Model model){
		
		bankNo = URLUtils.ParamterFilter(bankNo);
		Broker broker=getBroker(accountId);
		if(PasswordShape.isActive(broker)){
			String mcrypt_key = session
					.getAttribute("mcrypt_key", String.class);
			if (mcrypt_key == null) {
				model.addAttribute("errMsg", "您的交易已过期");
				return JSON.toJSONString(model);
			}
			if (!StringUtils.isEmpty(bankPassword)) {
				bankPassword = AESWithJCE.getResult(mcrypt_key, bankPassword);
			}
			if (!StringUtils.isEmpty(fundPassword)) {
				fundPassword = AESWithJCE.getResult(mcrypt_key, fundPassword);
			}
			if (!StringUtils.isEmpty(tradePassword)) {
				tradePassword = AESWithJCE.getResult(mcrypt_key, tradePassword);
			}
			collectClientInfo(request, session);
		}
		String userId = getSelfUserId(session);
		String rs="";
		JSONObject json = new JSONObject();
		try{
			json.put("serialNo", 0);
			json.put("occurBalance", "— —");
			//查询流水
			FundAmtQueryResp resp =  bankFundService.fundAmountQuery(userId, accountId, tradePassword, fundPassword, bankPassword, bankNo, MoneyType.CNY,bankAcc);
			//判断流水号是否为空
			if(resp != null){
				json.put("serialNo", resp.getSerialNo());
				if(resp.getOccurBalance()!=null){
					json.put("occurBalance", resp.getOccurBalance());
				}else{
					//根据流水号查询流水记录
					BankTransfer bankTransfer  = bankTransferQueryService.bankTransferQuery(userId, accountId,tradePassword,resp.getSerialNo());
					//判断是否是银行余额类型
					if(bankTransfer != null && bankTransfer.getBusinessType() ==  BusinessType.BANKAMTQRY){
						json.put("occurBalance", bankTransfer.getOccurBalance());
					}
				}
			}
			//rs = JSONObject.toJSONString(resp);
			rs = json.toJSONString();
		}catch(ServiceException e){
			log.error(e.getErrorInfo(),e); 
			json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			rs =json.toJSONString();
		}
		return rs;
	}
	
	@RequestMapping(value="/getPasswordAjax",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getPasswordAjax(
			@PathVariable Long accountId,
			@RequestParam(value="bankNo" ,defaultValue="" )String bankNo,
			@RequestParam(value="type" ,defaultValue="-1" )String type,
			@RequestParam(value="bank_password_require" ,defaultValue="" )String bank_password_require,
			HttpSessionWrapper session){
		bankNo = URLUtils.ParamterFilter(bankNo, '\0');
		type = URLUtils.ParamterFilter(type, '\0');
		String userId = getSelfUserId(session);
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		PasswordFlag pf = getPasswordFlag(accUserInfo.getBrokerId(),userId, accountId, bankNo, Integer.parseInt(type));
		String rs = null;
		if(pf !=null){
		 JSONObject json = new JSONObject();
		 if(pf.getFlag()!=-1){
		 if(pf.isBankPasswordRequired()){
			json.put("bankPassword", 1); 
		 }
		 if(pf.isFundPasswordRequired()){
			json.put("fundPassword", 1); 
		 }
		 if(pf.isPasswordRequired()){
			 json.put("tradePassword", 1);
		 }
		 }else{
			 json.put("pass", -1);
		 }
		 rs = json.toJSONString();
		}	 
		return rs;
	}
	@RequestMapping(value="/queryBankMoney",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String queryBankMoney(
			@PathVariable Long accountId,
			@RequestParam(value="serialNo" ,defaultValue="" )long serialNo,
			@RequestParam(value="tradePassword" ,defaultValue="-1" )String tradePassword,
			HttpSessionWrapper session){
		JSONObject json = new JSONObject();
		String rs = "";
		try{
			tradePassword = URLUtils.ParamterFilter(tradePassword, '\0');
			String userId = getSelfUserId(session);
			
			//根据流水号查询流水记录
			BankTransfer bankTransfer  = bankTransferQueryService.bankTransferQuery(userId, accountId,tradePassword,serialNo);
			//判断是否是银行余额类型
			if(bankTransfer != null && bankTransfer.getBusinessType() ==  BusinessType.BANKAMTQRY){
				json.put("occurBalance", bankTransfer.getOccurBalance());
			}
			rs = json.toJSONString();
		}catch(ServiceException e){
			log.error("Method --> queryBankMoney",e); 
			json.put("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			rs =json.toJSONString();
		}	
		return rs;
	}
	
}
