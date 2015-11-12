package com.cfo.stock.web.action.trade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Produces;

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
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.common.BrokerId;
import com.jrj.stocktrade.api.common.RealStatus;
import com.jrj.stocktrade.api.common.SecurityQueryType;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.iframe.vo.TradeIframe;
import com.jrj.stocktrade.api.stock.SecurityQueryService;
import com.jrj.stocktrade.api.stock.StockHistoryQueryService;
import com.jrj.stocktrade.api.stock.vo.Business;
import com.jrj.stocktrade.api.stock.vo.CancelableEntrust;
import com.jrj.stocktrade.api.stock.vo.Entrust;
import com.jrj.stocktrade.api.stock.vo.HistoryBusiness;
import com.jrj.stocktrade.api.stock.vo.HistoryEntrust;
import com.jrj.stocktrade.api.stock.vo.HistoryFundStockEx;


@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class TradeQueryAjaxAction  extends AbstractStockBaseAction{
	
	static final int ps =15;
	@Autowired
	SecurityQueryService securityQueryService;
	@Autowired
	StockHistoryQueryService stockHistoryQueryService;
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
		List<Entrust> entrustList  = new ArrayList<Entrust>();
		try {
			entrustList = securityQueryService.unsafeSecurityEntrustQuery(userId, accountId, Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/entrust";
		model.addAttribute("queryList",entrustList);
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
		List<Business> queryList  = new ArrayList<Business>();
		try {
			queryList =securityQueryService.unsafeSecurityRealtimeQuery(userId, accountId, Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/business";
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
		List<HistoryBusiness> queryList  = new ArrayList<HistoryBusiness>();
		List<Business> busList  = new ArrayList<Business>();
		try {
			 queryList = stockHistoryQueryService.unsafeHistoryBusinessQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), ps);
			 if(queryList!=null){
				 for(HistoryBusiness hb:queryList){
					 Business b=new Business();
					 b.setReportNo(hb.getReportNo());
					 b.setEntrustNo(hb.getEntrustNo());
					 b.setBusinessTime(hb.getInitDate());
					 b.setStockCode(hb.getStockCode());
					 b.setStockName(hb.getStockName());
					 b.setEntrustBs(hb.getEntrustBs());
					 //b.setRealType(); //成交类别---
					 b.setBusinessAmount(hb.getOccurAmount()); //成交数量
					 b.setBusinessPrice(hb.getBusinessPrice());
					 b.setBusinessBalance(hb.getBusinessBalance());
					 if(hb.getBusinessStatus()!=null){
						 //成交状态
						 if("0".equals(hb.getBusinessStatus().getValue())){
							 b.setRealStatus(RealStatus.BUSINESS);
						 }else if("1".equals(hb.getBusinessStatus().getValue())){
							 b.setRealStatus(RealStatus.ABANDON);
						 }else if("4".equals(hb.getBusinessStatus().getValue())){
							 b.setRealStatus(RealStatus.CONFIRM);
						 }
					 }
					 busList.add(b);
				 }
			 }
		} catch (ServiceException e){
			log.error("errMsg",e); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}catch(Exception e){
			log.error("errMsg",e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/business";
		model.addAttribute("selecttype",selecttype);
		model.addAttribute("queryList",busList);
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
		
		String viewName="/piece/entrust";
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
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 30)){
			model.addAttribute("errMsg", "您选择的时间区间大于30天");
			return viewName;
		}
		List<HistoryEntrust> queryList  = new ArrayList<HistoryEntrust>();
		List<Entrust> entrustList  = new ArrayList<Entrust>();
		try {
			 queryList = stockHistoryQueryService.unsafeHistoryEntrustQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), ps);
			 if(queryList.size()>0){
				 for(HistoryEntrust h: queryList){//转换返回对象
					 Entrust e=new Entrust();
					 e.setReportNo(e.getReportNo());
					 e.setEntrustNo(h.getEntrustNo());
					 e.setEntrustTime(h.getEntrustDate());
					 e.setStockCode(h.getStockCode());
					 e.setStockName(h.getStockName());
					 e.setEntrustBs(h.getEntrustBs());
					 e.setEntrustAmount(h.getEntrustAmount());
					 e.setEntrustPrice(h.getEntrustPrice());
					 e.setBusinessAmount(h.getBusinessAmount());
					 e.setEntrustStatus(h.getEntrustStatus());
					 entrustList.add(e);
				 }
			 }
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}catch(Exception e){
			log.error(e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		
		
		model.addAttribute("selecttype",selecttype);
		model.addAttribute("queryList",entrustList);
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
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		List<CancelableEntrust> queryList  = new ArrayList<CancelableEntrust>();
		try {
			queryList =  securityQueryService.unsafeSecurityCancelableEntrustQuery(userId, accountId, Integer.parseInt(pageNum), ps);	
		} catch (ServiceException e){
			log.error(e.getErrorInfo()+" #getCancelPage:",e); 
			//对中金没有返回结果报”[331022][委托状态不允许撤单]“错误进行特殊处理
			if(BrokerId.ZJZQ_SECURITIES.equals(accUserInfo.getBrokerId())  &&  !e.getErrorNo().equals("-54")){
				model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			}
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/cancel";
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		model.addAttribute("accountId",accountId);
		return viewName;
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
		List<HistoryFundStockEx> queryList  = new ArrayList<HistoryFundStockEx>();
		try {
			queryList = stockHistoryQueryService.unsafeHistoryFundStockAllQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate),Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}catch(Exception e){
			log.error(e); 
			model.addAttribute("errMsg", "服务器忙，请稍后再试！");
			model.addAttribute("pageNum", Integer.parseInt(pageNum));
		}
		String viewName="/piece/fund";
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
			String viewName="/piece/fund";
			model.addAttribute("errMsg","您选择的时间区间大于90天");
			return viewName;
		}
		startDate = StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);
		String userId= getSelfUserId(session);
		List<HistoryFundStockEx> queryList  = new ArrayList<HistoryFundStockEx>();
		try {
			queryList = stockHistoryQueryService.unsafeHistoryFundStockAllQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate),1, ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
			model.addAttribute("pageNum", 1);
		}
		String viewName="/piece/fund";
		model.addAttribute("queryList",queryList);
		model.addAttribute("pageNum", 1);
		return viewName;
	}
	
	//对账单查询
	@RequestMapping(value="/searchIframeFund",method=RequestMethod.GET)
	@ResponseBody
	public String searchIframeFund(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		String userId = getSelfUserId(session);
		JSONObject json = new JSONObject();
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
        	json.put("startDate","");
        	json.put("endDate","");
        }else if("month".equals(selecttype)){//一个月
        	rightNow.add(Calendar.DAY_OF_YEAR,-30);
       	 	startDate =sdf.format(rightNow.getTime());
       	 	endDate =sdf.format(dt);
       	 	json.put("startDate","");
       	 	json.put("endDate","");
        }else{
        	json.put("startDate",startDate);
        	json.put("endDate",endDate);
        }
        json.put("selecttype", selecttype);
        
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			json.put("errMsg", "您选择的时间区间大于90天");
			return json.toJSONString();
		}
		startDate=StockUtil.formatDate(StockUtil.formatDate(startDate,"yyyyMMdd"), "yyyy-MM-dd");
		endDate=StockUtil.formatDate(StockUtil.formatDate(endDate,"yyyyMMdd"), "yyyy-MM-dd");
		try {
			TradeIframe tradeIframe = tradeIframeService.securityQuery(userId,accountId,SecurityQueryType.DELIVERYORDER,StockUtil.getDateStringToDate(startDate),StockUtil.getDateStringToDate(endDate));
			//拼接参数
			json.put("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			return json.toJSONString();
		} catch (ServiceException e) {
			log.error("中山，用户 :" +userId +"--"+"对账单ajax失败 ：" +e.getErrorInfo());
			json.put("errMsg", "对账单ajax失败");
			return json.toJSONString();
		}catch(Exception e){
			log.error("中山，用户 :" +userId +"--"+"对账单ajax失败" +e);
			json.put("errMsg", "服务器忙，请稍后再试！");
			return json.toJSONString();
		}
	}
	//中山历史成交
	@RequestMapping(value="/searchHIframeBusiness",method=RequestMethod.GET)
	@Produces("application/json;charset=UTF-8")
	@ResponseBody
	public String searchHIframeuBsiness(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		String userId = getSelfUserId(session);
		JSONObject json = new JSONObject();
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
        	json.put("startDate","");
        	json.put("endDate","");
        }else if("month".equals(selecttype)){//一个月
        	rightNow.add(Calendar.DAY_OF_YEAR,-30);
       	 	startDate =sdf.format(rightNow.getTime());
       	 	endDate =sdf.format(dt);
       	 	json.put("startDate","");
       	 	json.put("endDate","");
        }else{
        	json.put("startDate",startDate);
        	json.put("endDate",endDate);
        }
        json.put("selecttype", selecttype);
        
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			json.put("errMsg", "您选择的时间区间大于90天");
			return json.toJSONString();
		}
		startDate=StockUtil.formatDate(StockUtil.formatDate(startDate,"yyyyMMdd"), "yyyy-MM-dd");
		endDate=StockUtil.formatDate(StockUtil.formatDate(endDate,"yyyyMMdd"), "yyyy-MM-dd");
		try {
			TradeIframe tradeIframe = tradeIframeService.securityQuery(userId, accountId, SecurityQueryType.HISTORYDEAL,StockUtil.getDateStringToDate(startDate),StockUtil.getDateStringToDate(endDate));
			//拼接参数
			json.put("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			return json.toJSONString();
		} catch (ServiceException e) {
			log.error("中山，用户 :" +userId +"--"+"查询历史成交ajax成交失败 ：" +e.getErrorInfo());
			json.put("errMsg", "查询历史成交ajax成交失败");
			return json.toJSONString();
		}catch(Exception e){
			log.error("中山，用户 :" +userId +"--"+"查询历史ajax成交失败 ：" +e);
			json.put("errMsg", "服务器忙，请稍后再试！");
			return json.toJSONString();
		}
	}
	//中山当日成交
	@RequestMapping(value="/searchTIframeBusiness",method=RequestMethod.GET)
	@Produces("application/json;charset=UTF-8")
	@ResponseBody
	public String searchTIframeBusiness(
			@PathVariable Long accountId,
			@RequestParam(value="startDate" ,defaultValue="")String startDate,
			@RequestParam(value="endDate" ,defaultValue="")String endDate,
			@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
			@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			HttpSessionWrapper session,
			Model model){
		
		String userId = getSelfUserId(session);
		JSONObject json = new JSONObject();
		
        json.put("selecttype", selecttype);
        
		if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
			json.put("errMsg", "您选择的时间区间大于90天");
			return json.toJSONString();
		}
		try {
			TradeIframe tradeIframe = tradeIframeService.securityQuery(userId, accountId ,SecurityQueryType.DAYDEAL,null,null);
			//拼接参数
			json.put("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
			return json.toJSONString();
		} catch (ServiceException e) {
			log.error("中山，用户 :" +userId +"--"+"查询当日成交ajax成交失败：" +e.getErrorInfo());
			json.put("errMsg", "查询当日成交ajax成交失败");
			return json.toJSONString();
		}
	}
	
		//中山当日委托
		@RequestMapping(value="/searchIframeTodayEntrust",method=RequestMethod.GET)
		@ResponseBody
		public String searchIframeTodayEntrust(
				@PathVariable Long accountId,
				@RequestParam(value="startDate" ,defaultValue="")String startDate,
				@RequestParam(value="endDate" ,defaultValue="")String endDate,
				@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
				@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
				HttpSessionWrapper session,
				Model model){
			
			String userId = getSelfUserId(session);
			JSONObject json = new JSONObject();

			try {
				TradeIframe tradeIframe = tradeIframeService.securityQuery(userId,accountId,SecurityQueryType.ENTRUST,null,null);
				//拼接参数
				json.put("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
				return json.toJSONString();
			} catch (ServiceException e) {
				log.error("中山，用户 :" +userId +"--"+"查询历史ajax成交失败 ：" +e.getErrorInfo());
				json.put("errMsg", "查询当日委托ajax成交失败");
				return json.toJSONString();
			}
		}
		//中山历史委托
		@RequestMapping(value="/searchIframeEntrust",method=RequestMethod.GET)
		@ResponseBody
		public String searchIframeEntrust(
				@PathVariable Long accountId,
				@RequestParam(value="startDate" ,defaultValue="")String startDate,
				@RequestParam(value="endDate" ,defaultValue="")String endDate,
				@RequestParam(value="selecttype" ,defaultValue="")String selecttype,
				@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
				HttpSessionWrapper session,
				Model model){
			
			String userId = getSelfUserId(session);
			JSONObject json = new JSONObject();
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
	        	json.put("startDate","");
	        	json.put("endDate","");
	        }else if("month".equals(selecttype)){//一个月
	        	rightNow.add(Calendar.DAY_OF_YEAR,-30);
	       	 	startDate =sdf.format(rightNow.getTime());
	       	 	endDate =sdf.format(dt);
	       	 	json.put("startDate","");
	       	 	json.put("endDate","");
	        }else{
	        	json.put("startDate",startDate);
	        	json.put("endDate",endDate);
	        }
	        json.put("selecttype", selecttype);
			
			if(!StockUtil.compareDate(StockUtil.formatDate(startDate, "yyyyMMdd"), StockUtil.formatDate(endDate, "yyyyMMdd"), 90)){
				json.put("errMsg", "您选择的时间区间大于90天");
				return json.toJSONString();
			}
			startDate=StockUtil.formatDate(StockUtil.formatDate(startDate,"yyyyMMdd"), "yyyy-MM-dd");
			endDate=StockUtil.formatDate(StockUtil.formatDate(endDate,"yyyyMMdd"), "yyyy-MM-dd");
			try {
				TradeIframe tradeIframe = tradeIframeService.securityQuery(userId,accountId,SecurityQueryType.HISTORYENTRUST,StockUtil.getDateStringToDate(startDate),StockUtil.getDateStringToDate(endDate));
				//拼接参数
				json.put("url", ActionUtils.getURL(tradeIframe.getUrl(), tradeIframe.getParam()));
				return json.toJSONString();
			} catch (ServiceException e) {
				log.error("中山，用户 :" +userId +"--"+"查询历史委托ajax失败 ：" +e.getErrorInfo());
				json.put("errMsg", "查询历史委托ajax成交失败");
				return json.toJSONString();
			}catch(Exception e){
				log.error("中山，用户 :" +userId +"--"+"查询历史委托ajax成交失败 ：" +e);
				json.put("errMsg", "服务器忙，请稍后再试！");
				return json.toJSONString();
			}
			
		}
}
