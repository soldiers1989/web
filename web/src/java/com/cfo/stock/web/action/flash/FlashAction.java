package com.cfo.stock.web.action.flash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.util.ComparatorPosition;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.common.EntrustBs;
import com.jrj.stocktrade.api.deposit.FundService;
import com.jrj.stocktrade.api.deposit.vo.FundInfo;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.stock.StockHistoryQueryService;
import com.jrj.stocktrade.api.stock.StockQueryService;
import com.jrj.stocktrade.api.stock.vo.HistoryBusiness;
import com.jrj.stocktrade.api.stock.vo.StockInfo;

/**
 *首页
 * @author yuanlong.wang
 *
 */
@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin

public class FlashAction  extends AbstractStockBaseAction{
	@Autowired
	StockQueryService stockQueryService;
	
	@Autowired
	StockHistoryQueryService stockHistoryQueryService;
	
	@Autowired
	FundService fundService;
	
	@RequestMapping(value="/flashPositionInfo",method=RequestMethod.GET,produces = {"application/xml;charset=UTF-8"})
	public String execute(
			@PathVariable Long accountId,
			HttpSessionWrapper session,Model model){
		
		String userId = getSelfUserId(session);
		List<StockInfo> positions = new ArrayList<StockInfo>();
		List<StockInfo> positionsNew = new ArrayList<StockInfo>();
		FundInfo fund  =null;
		try{
			positions = stockQueryService.securityStockFastQuery(userId, accountId);
			 fund = fundService.clientFundFastQuery(userId, accountId);
		}catch(ServiceException e){
			log.error(e.getErrorInfo()); 
		}
		if(positions.size()>6){
			ComparatorPosition comparatorPosition = new ComparatorPosition();
			Collections.sort(positions, comparatorPosition);
			int index =1;
			BigDecimal sum = BigDecimal.ZERO;
			StockInfo othiersInfo = new StockInfo();
			othiersInfo.setStockName("其他");
			for(int i=positions.size()-1;i>0;i--){
				if(index<6){
					StockInfo info = positions.get(i);
					positionsNew.add(info);
					index++;
				}
				StockInfo info = positions.get(i);			
				BigDecimal marketValue = info.getMarketValue();
				sum = sum.add(marketValue);				
			}
			othiersInfo.setMarketValue(sum);
			if(positions.size()>6){
				positionsNew.add(othiersInfo);
			}
		}else{
			positionsNew =positions;
		}
		String viewName="/flash/positionInfo";
		model.addAttribute("fund", fund);
		model.addAttribute("queryList", positionsNew);		
		return viewName;
	}	
	@RequestMapping(value="/flashFundInfo",method=RequestMethod.GET,produces = {"application/xml;charset=UTF-8"})
	public String excute(
			@PathVariable Long accountId,
			HttpSessionWrapper session, Model model){
		
		String userId = getSelfUserId(session);
		String endDate =StockUtil.getNowDate("");
		Date now = new Date();
		String startDate = StockUtil.formatDate(now.getTime() - 2592000000l);
		Map<String, String> map = new TreeMap<String, String>();
		try {
			List<HistoryBusiness> list = stockHistoryQueryService.historyBusinessQuery(userId, accountId, Integer.parseInt(startDate), Integer.parseInt(endDate));
			if(list.size()>0){
				for(int i=0;i<list.size();i++){
					HistoryBusiness info = list.get(i);
					if(info.getEntrustBs() != EntrustBs.BUY){
						continue;
					}				
					if(map.size()>20){
						break;
					}					
					String thisDate =new Long(info.getInitDate()).toString();
					if(map.containsKey(StockUtil.getDate(thisDate))){					
						map.put(StockUtil.getDate(thisDate),( info.getBusinessBalance().add(
								new BigDecimal(map.get(StockUtil.getDate(thisDate))!=null?map.get(StockUtil.getDate(thisDate)) :"0"))).toString());
					}else{
						map.put(StockUtil.getDate(thisDate), info.getBusinessBalance().toString());
					}
				}
			}else{
				model.addAttribute("listStatus", 0);
			}
		}catch (ServiceException e) {
			log.error(e.getErrorInfo()); 
		}	
		String viewName="/flash/fundInfo";
		model.addAttribute("dataMap", map);
		return viewName;
	}
}
