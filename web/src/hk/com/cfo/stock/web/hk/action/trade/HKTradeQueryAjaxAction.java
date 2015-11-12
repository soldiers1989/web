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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.stock.SecurityQueryService;
import com.jrj.stocktrade.api.stock.StockHistoryQueryService;
import com.jrj.stocktrade.api.stock.vo.HistoryBusiness;
import com.jrj.stocktrade.hk.api.service.HkSecurityCodeService;
import com.jrj.stocktrade.hk.api.service.HkSecurityHistoryQueryService;
import com.jrj.stocktrade.hk.api.service.HkSecurityQueryService;
import com.jrj.stocktrade.hk.api.vo.HkSecurityConfirm;
import com.jrj.stocktrade.hk.api.vo.OrderCancelation;
import com.jrj.stocktrade.hk.api.vo.OrderExecution;
import com.jrj.stocktrade.hk.api.vo.OrderRecord;
import com.jrj.stocktrade.hk.api.vo.StockMovementRecord;


@Controller
@RequestMapping("/stock/hk/{accountId}")
@NeedLogin
@TradeAccount
public class HKTradeQueryAjaxAction  extends AbstractStockBaseAction{
	
	static final int ps =15;
	@Autowired
	SecurityQueryService securityQueryService;
	@Autowired
	StockHistoryQueryService stockHistoryQueryService;
	@Autowired
	HkSecurityQueryService hkSecurityQueryService;
	@Autowired
	HkSecurityHistoryQueryService hkSecurityHistoryQueryService;
	@Autowired
	HkSecurityCodeService hkSecurityCodeService;
	/**
	 * 当日委托下页查询
	 * @return
	 */
	@RequestMapping(value="/getEntrustPage",method=RequestMethod.GET)
	public String getEntrustNext(
			@PathVariable Long accountId,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		String userId= getSelfUserId(session);
		List<OrderRecord> recordList= new ArrayList<OrderRecord>();
		try {
			recordList=hkSecurityQueryService.orderQuery(userId, accountId, Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/entrust_hk";
		model.addAttribute("queryList",recordList);
		model.addAttribute("selecttype",selecttype);
		model.addAttribute("startDate","");
		model.addAttribute("endDate","");
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		model.addAttribute("accountId",accountId);
		return viewName;
	}
	//当日成交记录ajax
	@RequestMapping(value="/getBusinessPage",method=RequestMethod.GET)
	public String getBusinessNext(
			@PathVariable Long accountId,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		String userId= getSelfUserId(session);
		List<OrderExecution> queryList  = new ArrayList<OrderExecution>();
		try {
			queryList=hkSecurityQueryService.orderExecutionQuery(userId, accountId, Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/business_hk";
		model.addAttribute("queryList",queryList);
		model.addAttribute("selecttype", selecttype);
		model.addAttribute("startDate", "");
		model.addAttribute("endDate", "");
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		return viewName;
	}
	//历史成交记录ajax
	@RequestMapping(value="/getHistoryBusinessPage",method=RequestMethod.GET)
	public String getHistoryBusinessNext(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			HttpSessionWrapper session,
			Model model){
        
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
        
		startDate =StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);
		
		String userId= getSelfUserId(session);

		
		List<OrderExecution> queryList  = new ArrayList<OrderExecution>();
		try {
			 queryList = hkSecurityHistoryQueryService.historyOrderExecutionQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error("errMsg",e); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}catch(Exception e){
			log.error("errMsg",e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/business_hk";
		model.addAttribute("selecttype",selecttype);
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		return viewName;
	}
	//历史委托
	@RequestMapping(value="/getHistoryEntrustPage",method=RequestMethod.GET)
	public String getHistoryEntrustNext(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			HttpSessionWrapper session,
			Model model){
		
		String viewName="/piece/entrust_hk";
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
        
		startDate =StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);
		String userId= getSelfUserId(session);
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			model.addAttribute("errMsg", "您选择的时间区间大于90天");
			return viewName;
		}
		List<OrderRecord> recordList =new ArrayList<OrderRecord>();
		try {
			 recordList=hkSecurityHistoryQueryService.historyOrderQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}catch(Exception e){
			log.error(e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		
/*		OrderRecord orderRecord1 =new OrderRecord();
		orderRecord1.setOrderNo(222);
		orderRecord1.setInstrumentNo("00001");
		orderRecord1.setInstrumentName("中华煤气");
		orderRecord1.setEntrustBs(BidAskType.BUY);
		orderRecord1.setExecutedQuantity(new BigDecimal("200"));
		orderRecord1.setPrice(new BigDecimal("200"));
		orderRecord1.setQuantity(new BigDecimal("200"));
		orderRecord1.setOrderStatus(OrderStatus.AJ);
		orderRecord1.setOrderSubmitDatetime(new Date());
		recordList.add(orderRecord1);*/
		
		model.addAttribute("selecttype",selecttype);
		model.addAttribute("queryList",recordList);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		return viewName;
	}
	@RequestMapping(value="/searchHBusiness",method=RequestMethod.GET)
	public String searchHBusiness(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			HttpSessionWrapper session,
			Model model){
		
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			String viewName="/piece/hbusiness";
			model.addAttribute("errMsg","您选择的时间区间大于90天");
			return viewName;
		}
		startDate = StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);
		String userId= getSelfUserId(session);
		List<HistoryBusiness> queryList  = new ArrayList<HistoryBusiness>();
		try {
			 queryList = stockHistoryQueryService.unsafeHistoryBusinessQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), 1, ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum",1);
		}catch(Exception e){
			log.error(e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", 1);
		}
		String viewName="/piece/hbusiness";
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", 1);
		return viewName;
	}
	//可撤单查询
	@RequestMapping(value="/getCancelPage",method=RequestMethod.GET)
	public String getCancelNext(
			@PathVariable Long accountId,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		String userId= getSelfUserId(session);
		List<OrderCancelation> queryList  = new ArrayList<OrderCancelation>();
		try {
			queryList =  hkSecurityQueryService.orderCancelationQuery(userId, accountId, Integer.parseInt(pageNum), ps);	
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		model.addAttribute("accountId",accountId);
		return "/piece/cancel_hk";
	}
	
	@RequestMapping(value="/getFundPage",method=RequestMethod.GET)
	public String getFundNext(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
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
		model.addAttribute("selecttype", selecttype);
		
		String userId= getSelfUserId(session);
		List<StockMovementRecord> queryList  = new ArrayList<StockMovementRecord>();
		try {
			queryList=hkSecurityHistoryQueryService.historyStockMovementQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}catch(Exception e){
			log.error(e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/fund_hk";
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		return viewName;
	}
	
	@RequestMapping(value="/searchFund",method=RequestMethod.GET)
	public String searchFund(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			HttpSessionWrapper session,
			Model model){
		
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			String viewName="/piece/fund_hk";
			model.addAttribute("errMsg","您选择的时间区间大于90天");
			return viewName;
		}
		startDate = StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);
		String userId= getSelfUserId(session);
		List<StockMovementRecord> queryList  = new ArrayList<StockMovementRecord>();
		try {
			queryList=hkSecurityHistoryQueryService.historyStockMovementQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), 1, ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", 1);
		}
		String viewName="/piece/fund_hk";
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", 1);
		return viewName;
	}
	
	@RequestMapping(value="/stockInfo",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String stockInfo(
			@PathVariable Long accountId,
			@RequestParam(value="stockCode")String stockCode,
			HttpSessionWrapper session
			){
		String userId= getSelfUserId(session);
		HkSecurityConfirm stock = null;
		try {
			stock = hkSecurityCodeService.securityCodeEnter(userId, accountId, stockCode);
		} catch (ServiceException e) {
			log.error(e.getErrorInfo(), e);
			JSONObject json = new JSONObject();
			json.put("errMsg", e.getErrorInfo());
			return json.toJSONString();
		}
		String re=JSONObject.toJSONString(stock);
		return re;
	}
}
