package com.cfo.stock.web.action.account;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.services.account.AccountWebService;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.deposit.FundService;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.pub.BranchQueryService;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;
import com.jrj.stocktrade.api.stock.StockQueryService;
@Controller
@RequestMapping("/stock")
@NeedLogin
/**
 * 专用链接类
 * @author mihui
 *
 */
public class AutoGotoPageAction extends AbstractStockBaseAction{
	private static final int ps =15;
	private static final String GO_BIND="/stock/noBindIndex.jspa";
	private static final String DO_GO="/stock/noDoGo.jspa";
	@Autowired
	StockQueryService stockQueryService;
	@Autowired
	AccountService accountService;
	@Autowired
	FundService fundService;
	@Autowired
	AccountWebService accountWebService;	
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	StockAccountQueryService stockAccountQueryService;
	@Autowired	
	AccountQueryService accountQueryService;
	@Autowired
	BranchQueryService branchQueryService;
	
	
    /**
     * 活动专用-给中山活动提供的链接，开户的时候只显示中山
     * 向session添加数据，用来判断是否是此链接进入的证券通
     * @param redirect
     * @return
     * @throws ServiceException
     */
	@RequestMapping(value="/getKhpage",method=RequestMethod.GET)
	public String getKhpage(
			@RequestParam(value = "bid", defaultValue = "") String bid,
			@RequestParam(value = "khtype", defaultValue = "") String khtype,
			@RequestParam(value = "from", defaultValue = "") String channel,
			HttpSessionWrapper session
			) throws ServiceException{
		
		//向session赋值
		session.setAttribute(AttributeKeys.DH_CHANNEL,"Zskh");
		//request.getRequestDispatcher(DO_GO).forward(request, response);
		return "forward:"+DO_GO;
	}
	 /**
     * 添加NeedBroker拦截器，跳转到开户流程页面
     * @param redirect
     * @return
     * @throws ServiceException
     */
	
	@RequestMapping(value="/noDoGo",method=RequestMethod.GET)
	public String noDoGo(
			@RequestParam(value = "bid", defaultValue = "") String bid,
			@RequestParam(value = "khtype", defaultValue = "") String khtype,
			@RequestParam(value = "from", defaultValue = "") String channel
			) throws ServiceException{
		
		
			//跳转到开户流程页面
			//request.getRequestDispatcher(GO_BIND).forward(request, response);
		return "forward:"+GO_BIND;
		
	}
}
