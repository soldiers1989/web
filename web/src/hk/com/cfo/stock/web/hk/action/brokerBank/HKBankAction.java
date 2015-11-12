package com.cfo.stock.web.hk.action.brokerBank;

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

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.interceptor.HkPasswordInterceptor.NeedHkPassword;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.util.StockUtil;
import com.cfo.stock.web.vo.ErrorInfo;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.hk.api.service.HkSecurityFundService;
import com.jrj.stocktrade.hk.api.service.HkSecurityHistoryQueryService;
import com.jrj.stocktrade.hk.api.vo.BuyingPower;
import com.jrj.stocktrade.hk.api.vo.CashMovementRecord;


@Controller
@RequestMapping("/stock/hk/{accountId}")
@NeedLogin
@TradeAccount
@NeedHkPassword
public class HKBankAction  extends AbstractStockBaseAction{
	static final int ps =15;
	
	@Autowired
	HkSecurityHistoryQueryService hkSecurityHistoryQueryService;
	
	@Autowired
	HkSecurityFundService hkSecurityFundService;
	
	/**
	 * 资金转入
	 * @return
	 */
	@RequestMapping(value="/bankIndex",method=RequestMethod.GET)
	public String bankRollIn(@RequestParam(value="transferType" ,defaultValue="1" )String transferType,
			@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		    String viewName=null;
			viewName="/bank/brokerBankTransfer_hk";
		
		//获取该证券绑定的所有银行卡
		return viewName;
	}
	
	/**
	 * 资金转出
	 * @return
	 */
	@RequestMapping(value="/bankRollOut",method=RequestMethod.GET)
	public String bankRollOut(@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
		    String viewName=null;	
			viewName="/bank/brokerBankTransferOut_hk";
		
			
			JSONObject json = null;
			
			//缓存标记，默认从缓存中取数据
			boolean flag = true;
			if(accountId.equals(accountId)){//如果为当前用户，则从接口取数据
				if(log.isDebugEnabled()){
					log.debug("是当前用户。取接口数据 accountId==>"+accountId+", curAccountId==>"+accountId);
				}
				flag = false;
			}
			String cacheKey = createAssetCacheKey(accountId);
			json =(JSONObject) cache.get(cacheKey);
			if(flag && json != null){
				model.addAttribute("fundAll", json.get("fundAll"));
			}else{
				json = new JSONObject();
				json.put("accountId", accountId);
				
				UserAccount accUserInfo = userAccountService
						.queryAccount(accountId);
				String accUserId = accUserInfo.getUserId();
				
				try {
					BuyingPower fundAll = hkSecurityFundService.buyingPower(accUserId, accountId);
					model.addAttribute("fundAll", fundAll);
					json.put("fundAll", fundAll);
				} catch (Exception e) {
					log.error("accountId==>"+accountId+", exception:",e);
					json.put("errMsg_fund", e.getMessage()==null ? "error" : e.getMessage());
				}
				Calendar cal=Calendar.getInstance();
				json.put("updateTime", cal.getTimeInMillis());
				
				cal.add(Calendar.MINUTE, Integer.parseInt(Global.WEB_CACHE_EXPIRE_TIME));
				cache.set(cacheKey, json, cal.getTime());
				if(log.isDebugEnabled()){
					log.debug("user asset data: accountId==>"+accountId+", curAccountId==>"+accountId+"data==>"+json.toJSONString());
				}
			}	
			
		    return viewName;
	}
	
	@RequestMapping(value="/modifyBankInfo",method=RequestMethod.GET)
    public String modifyBankInfo(@PathVariable Long accountId,
			HttpSessionWrapper session,
			Model model){
    	String viewName=null;	
		viewName="/bank/modifyBankInfo";
	
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
			info.setReturnUrl("/stock/hk/fundQuery.jspa");
			model.addAttribute("errorInfo", info);
			return viewName;
		}
		String viewName="/piece/bankFund_hk";
		startDate = StockUtil.getNowDate(startDate);
		endDate = StockUtil.getNowDate(endDate);

		
		String userId = getSelfUserId(session);
		List<CashMovementRecord> listCashMovementRecord = new ArrayList<CashMovementRecord>();
		try {
			listCashMovementRecord = hkSecurityHistoryQueryService.historyCashMovementQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), Integer.parseInt(pageNum), 15);
			
		} catch (ServiceException e){
			log.error(e.getErrorInfo()); 
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		model.addAttribute("queryList", listCashMovementRecord);
		model.addAttribute("pageNum", pageNum);
		return viewName;
	}
	
	//周流水查询
		@RequestMapping(value="/fundQuery",method=RequestMethod.GET)
		public String fundQuery(
				@PathVariable Long accountId,
				@RequestParam(value="startDate" ,defaultValue="" )String startDate,
				@RequestParam(value="endDate" ,defaultValue="" )String endDate,
				HttpSessionWrapper session,
				Model model){
			
			String userId = getSelfUserId(session);
		    String viewName=null;
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date dt = new Date();
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);

			rightNow.add(Calendar.DAY_OF_YEAR, -7);
			startDate = sdf.format(rightNow.getTime());
			endDate = sdf.format(dt);
				viewName="/bank/fundQuery_hk";
				List<CashMovementRecord> list = new ArrayList<CashMovementRecord>();
				try {
	
					list=hkSecurityHistoryQueryService.historyCashMovementQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate), 1, ps);
				} catch (ServiceException e){
					log.error(e.getErrorInfo(),e); 
					model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
				}

				model.addAttribute("queryList", list);
				model.addAttribute("pageNum", 1);

			return viewName;
		}
	
}
