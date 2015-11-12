package com.cfo.stock.web.action.trade;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.util.StockUtil;
import com.cfo.stock.web.util.ZQTUtils;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.common.BrokerId;
import com.jrj.stocktrade.api.common.SecurityQueryType;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.iframe.vo.TradeIframe;
import com.jrj.stocktrade.api.stock.SecurityQueryService;
import com.jrj.stocktrade.api.stock.StockHistoryQueryService;
import com.jrj.stocktrade.api.stock.vo.Business;
import com.jrj.stocktrade.api.stock.vo.CancelableEntrust;
import com.jrj.stocktrade.api.stock.vo.Entrust;
import com.jrj.stocktrade.api.stock.vo.HistoryBusiness;
import com.jrj.stocktrade.api.stock.vo.HistoryFundStockEx;
@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class TradeQueryAction  extends AbstractStockBaseAction{
	
	static final int ps =15;
	@Autowired
	AccountService accountService;
	@Autowired
	SecurityQueryService securityQueryService;
	@Autowired
	StockHistoryQueryService stockHistoryQueryService;
	/**
	 * 单日委托查询
	 * @param pn
	 * @return
	 */
	@RequestMapping(value="/entrust",method=RequestMethod.GET)
	public String entrust(
			@PathVariable Long accountId,
			HttpSessionWrapper session,Model model){
		
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		//get userId
		String userId = getSelfUserId(session);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
			viewName="/trade/entrust";

			List<Entrust> entrustList  = new ArrayList<Entrust>();
			//应该是在session 中获取用户进入证券账户时选择的证券公司
			try{
				//获取当日委托
				entrustList = securityQueryService.unsafeSecurityEntrustQuery(userId, accountId, 1, ps);
			}catch(ServiceException e){
				log.error("中信，用户 :" +userId +"--"+"查询当日委托失败 ：" +e.getErrorInfo());
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
				model.addAttribute("pageNum",1);
			}
			model.addAttribute("selecttype", "today");
			model.addAttribute("queryList", entrustList);
			model.addAttribute("pageNum", 1);
			model.addAttribute("accountId",accountId);
			break;
		case IFRAME://中山
			try {
				TradeIframe tradeIframe = tradeIframeService.securityQuery(userId,accountId,SecurityQueryType.ENTRUST,null,null);
				model.addAttribute("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			} catch (ServiceException e) {
				log.error("中山，用户 :" +userId +"--"+"查询当日委托失败 ：" +e.getErrorInfo());
			}
			viewName="/trade/entrust_iframe";
			break;
	    }
		//viewName="/trade/entrust";
		return viewName;
	}
	
	/**
	 * 当日成交
	 * @param pn
	 * @return
	 */
	@RequestMapping(value="/business",method=RequestMethod.GET)
	public String bussinessQuery(
			@PathVariable Long accountId,
			HttpSessionWrapper session,Model model){
		
		String userId = getSelfUserId(session);
//		//获取用户的证券公司
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
			viewName="/trade/business";
			List<Business> queryList = new ArrayList<Business>();
			try{
			    queryList =securityQueryService.unsafeSecurityRealtimeQuery(userId, accountId, 1, ps);
			}catch(ServiceException e){
				log.error("用户 :" +userId +"--"+"查询当日成交失败 ：" +e.getErrorInfo());
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
				model.addAttribute("pageNum", 1);
			}
			model.addAttribute("selecttype", "today");
			model.addAttribute("queryList", queryList);
			model.addAttribute("pageNum", 1);
			break;
		case IFRAME://中山
			try {
				TradeIframe tradeIframe = tradeIframeService.securityQuery(userId, accountId ,SecurityQueryType.DAYDEAL,null,null);
				model.addAttribute("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			} catch (ServiceException e) {
				log.error("中山，用户 :" +userId +"--"+"查询当日成交失败 ：" +e.getErrorInfo());
			}
			viewName="/trade/business_iframe";
			break;
		}
		return viewName;
	}
	/**
	 * 历史成交
	 * @param pn
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="/historyBusiness",method=RequestMethod.GET)
	public String HistorybussinessQuery(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			HttpSessionWrapper session,
			Model model){
		
		Date now = new Date();
		String userId = getSelfUserId(session);
		//获取用户的证券公司  后面应该是从session 中获取用户进入首页时候选择的券商
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
		case ORIGINAL://中信
			startDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-604800000));
			endDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-86400000));
			viewName="/trade/historyBusiness";
			
			List<HistoryBusiness> queryList = new ArrayList<HistoryBusiness>();
			try{
			queryList = stockHistoryQueryService.unsafeHistoryBusinessQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), 1, ps);		
			}catch(ServiceException e){
				log.error("用户 :" +userId +"--"+"查询历史成交失败 ：" +e.getErrorInfo());
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
				model.addAttribute("pageNum", 1);
			}
			model.addAttribute("queryList", queryList);
			model.addAttribute("pageNum", 1);
			break;
		case IFRAME://中山
			try {
				startDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-7776000000l));
				endDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()));
				TradeIframe tradeIframe = tradeIframeService.securityQuery(userId, accountId ,SecurityQueryType.HISTORYDEAL,StockUtil.getDateStringToDate(startDate),StockUtil.getDateStringToDate(endDate));
				//拼接参数
				model.addAttribute("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			} catch (ServiceException e) {
				log.error("中山，用户 :" +userId +"--"+"查询历史成交失败 ：" +e.getErrorInfo());
			}
			viewName="/trade/historyBusiness_iframe";
			break;
		}
		return viewName;
	}
	/**
	 * 可撤单查询
	 * @param pn
	 * @return
	 */
	@RequestMapping(value="/cancelable",method=RequestMethod.GET)
	public String cancelableEntruts(
			@PathVariable Long accountId,
			HttpSessionWrapper session, Model model){
		
		//get userId
		String userId = getSelfUserId(session);
		//获取用户的证券公司  后面应该是从session 中获取用户进入首页时候选择的券商
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
				viewName="/trade/cancelableEntrust";
				List<CancelableEntrust> queryList = new ArrayList<CancelableEntrust>();
				try{
					queryList =  securityQueryService.unsafeSecurityCancelableEntrustQuery(userId, accountId, 1, ps);	
				}catch(ServiceException e ){
					log.error("用户 :" +userId +"--"+"撤单列表查询失败 ：" +e.getErrorInfo(), e);
					//对中金没有返回结果报”[331022][委托状态不允许撤单]“错误进行特殊处理
					if(BrokerId.ZJZQ_SECURITIES.equals(accUserInfo.getBrokerId())   &&  !e.getErrorNo().equals("-54")){
						model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
					}
				}
				model.addAttribute("queryList", queryList);
				model.addAttribute("pageNum", 1);
				break;
			case IFRAME://中山
				viewName="/trade/cancelableEntrust_iframe";
				TradeIframe re = null;
				try {
					re = tradeIframeService.securityEntrustWithdraw(userId, accountId);
				} catch (ServiceException e1) {
					log.error("exception", e1);
					log.error("用户 :" +userId +"--"+"查询撤单单号 ：" +e1.getErrorInfo());
					model.addAttribute("errMsg", StockUtil.getErrorInfo(e1.getErrorInfo()));
					viewName="/trade/cancelStep2";
				}
				if(re != null){
					model.addAttribute("url", re.getUrl());
					model.addAttribute("params",re.getParam());
				}
				break;
		default:
			break;
		}
		
		return viewName;
	}
	/**
	 * 资金流水 (交割单）
	 * @return
	 */
	@RequestMapping(value="/fund",method=RequestMethod.GET)
	public String fund(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			HttpSessionWrapper session,
			Model model){
		
		String userId = getSelfUserId(session);
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		Date now = new Date();
		String viewName=null;
		switch(ZQTUtils.webTradeType(accUserInfo.getBrokerId())){
			case ORIGINAL://中信
				//startDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-604800000));
				///endDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-86400000));
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		        Date dt = new Date();
		        Calendar rightNow = Calendar.getInstance();
		        rightNow.setTime(dt);        
		     
	        	rightNow.add(Calendar.DAY_OF_YEAR,-7);
	        	startDate =sdf.format(rightNow.getTime());
	        	endDate =sdf.format(dt);
				
				viewName="/trade/fund";
				List<HistoryFundStockEx> queryList = new ArrayList<HistoryFundStockEx>();
				try{
					queryList = stockHistoryQueryService.unsafeHistoryFundStockAllQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate),1, ps);
				}catch(ServiceException e){
					log.error("中信 用户 :" +userId +"--"+"交割单查询失败 ：" +e.getErrorInfo());
					model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
					model.addAttribute("pageNum", 1);
				}
				model.addAttribute("selecttype", "today");
				model.addAttribute("queryList", queryList);
				model.addAttribute("pageNum", 1);
				break;
			case IFRAME://中山
				try {
					startDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()-2592000000l));
					endDate = StockUtil.getNowDate(StockUtil.formatDate(now.getTime()));
					TradeIframe tradeIframe = tradeIframeService.securityQuery(userId, accountId ,SecurityQueryType.DELIVERYORDER,null,null);
					Map<String,String> map = tradeIframe.getParam();
					model.addAttribute("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
					map.put("strdate", startDate);
					map.put("enddate", endDate);
					model.addAttribute("urlAddDate", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
				} catch (ServiceException e) {
					log.error("中山，用户 :" +userId +"--"+"交割单查询失败 ：" +e.getErrorInfo());
				}
				viewName="/trade/fund_iframe";
				break;
		}
		return viewName;
	}
}
