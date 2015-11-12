package com.cfo.stock.web.hk.action.trade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.HkPasswordInterceptor.NeedHkPassword;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.hk.api.service.HkSecurityHistoryQueryService;
import com.jrj.stocktrade.hk.api.service.HkSecurityQueryService;
import com.jrj.stocktrade.hk.api.vo.OrderCancelation;
import com.jrj.stocktrade.hk.api.vo.OrderExecution;
import com.jrj.stocktrade.hk.api.vo.OrderRecord;
import com.jrj.stocktrade.hk.api.vo.StockMovementRecord;

@Controller
@RequestMapping("/stock/hk/{accountId}")
@NeedLogin
@TradeAccount
@NeedHkPassword
public class HKTradeQueryAction extends AbstractStockBaseAction {
	
	static final int ps =15;
	
	@Autowired
	HkSecurityQueryService hksecurityQueryService;
	
	@Autowired
	HkSecurityHistoryQueryService hkSecurityHistoryQueryService;

	/**
	 * 单日委托查询
	 * 
	 * @param pn
	 * @return
	 */
	@RequestMapping(value = "/entrust", method = RequestMethod.GET)
	public String entrust(@PathVariable Long accountId,
			HttpSessionWrapper session, Model model) {
		String viewName = "";
		viewName = "/trade/entrust_hk";
		String userId = getSelfUserId(session);
		List<OrderRecord> recordList  = new ArrayList<OrderRecord>();
		try{
			//获取当日委托
			recordList = hksecurityQueryService.orderQuery(userId, accountId, 1, ps);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"查询当日委托失败 ：" +e.getErrorInfo(), e);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum",1);
		}
		
/*		OrderRecord orderRecord1 =new OrderRecord();
		orderRecord1.setOrderNo(222);
		orderRecord1.setInstrumentNo("00001");
		orderRecord1.setInstrumentName("中华煤气");
		orderRecord1.setBidAskType(BidAskType.BUY);
		orderRecord1.setExecutedQuantity(new BigDecimal("200"));
		orderRecord1.setPrice(new BigDecimal("200"));
		orderRecord1.setQuantity(new BigDecimal("200"));
		orderRecord1.setOrderStatus(OrderStatus.AJ);
		orderRecord1.setOrderSubmitDatetime(new Date());
		recordList.add(orderRecord1);*/
		model.addAttribute("selecttype", "today");
		model.addAttribute("queryList", recordList);
		model.addAttribute("pageNum", 1);
		model.addAttribute("accountId",accountId);
		
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
		String viewName=null;
            viewName="/trade/business_hk";
			List<OrderExecution> queryList = new ArrayList<OrderExecution>();
			try{
				queryList=hksecurityQueryService.orderExecutionQuery(userId, accountId, 1, ps); 
			}catch(ServiceException e){
				log.error("用户 :" +userId +"--"+"查询当日成交失败 ：" +e.getErrorInfo(), e);
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
				model.addAttribute("pageNum", 1);
			}
/*			OrderExecution orderExecution1=new OrderExecution();
			orderExecution1.setInstrumentNo("00001");
			orderExecution1.setInstrumentName("中华煤气");
			orderExecution1.setOrderNo(222);
			orderExecution1.setPrice(new BigDecimal("200"));
			orderExecution1.setExecutedQuantity(new BigDecimal("2"));
			orderExecution1.setExecutionDatetime(new Date());
			orderExecution1.setEntrustBs(BidAskType.BUY);
			queryList.add(orderExecution1);*/
			model.addAttribute("selecttype", "today");
			model.addAttribute("queryList", queryList);
			model.addAttribute("pageNum", 1);
		return viewName;
	}
	/**
	 * 挂单列表查询
	 * @param pn
	 * @return
	 */
	@RequestMapping(value="/cancelable",method=RequestMethod.GET)
	public String cancelableEntruts(
			@PathVariable Long accountId,
			HttpSessionWrapper session, Model model){
		
		//get userId
		String userId = getSelfUserId(session);
		List<OrderCancelation> queryList = new ArrayList<OrderCancelation>();
		try{
			queryList =  hksecurityQueryService.orderCancelationQuery(userId, accountId, 1,ps);	
		}catch(ServiceException e ){
			log.error("用户 :" +userId +"--"+"撤单列表查询失败 ：" +e.getErrorInfo(), e);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", 1);
		}
		model.addAttribute("queryList", queryList);
		model.addAttribute("pageNum", 1);
		model.addAttribute("accountId", accountId);
		
		return "/trade/cancelableEntrust_hk";
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
		//Date now = new Date();
		String viewName = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dt = new Date();
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(dt);

		rightNow.add(Calendar.DAY_OF_YEAR, -7);
		startDate = sdf.format(rightNow.getTime());
		endDate = sdf.format(dt);

		viewName = "/trade/fund_hk";
		List<StockMovementRecord> queryList = new ArrayList<StockMovementRecord>();
		try{
			queryList=hkSecurityHistoryQueryService.historyStockMovementQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), 1, ps);
		}catch(ServiceException e){
			log.error("用户 :" +userId +"--"+"交割单查询失败 ：" +e.getErrorInfo());
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", 1);
		}
		/*StockMovementRecord stockMovementRecord =new StockMovementRecord();
		stockMovementRecord.setRefNo("333");
		stockMovementRecord.setExchange("exchange");
		stockMovementRecord.setInstrumentNo("600152");
		stockMovementRecord.setInstrumentName("维科精华");
		stockMovementRecord.setMovementType(MovementType.Deposit);
		stockMovementRecord.setQty(new BigDecimal("200"));
		stockMovementRecord.setTransactionDate(new Date());
		stockMovementRecord.setValueDate(new Date());
		queryList.add(stockMovementRecord);*/
		model.addAttribute("selecttype", "today");
		model.addAttribute("queryList", queryList);
		model.addAttribute("pageNum", 1);
		
		return viewName;
	}
	
}
