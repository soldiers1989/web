package com.cfo.stock.web.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.common.BrokerStatus;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.exception.ServiceException;

/**
 * 渠道过来购买股票action
 * 
 * @author yuan.cheng 2014 03 12
 * 
 */

@Controller
@RequestMapping("/stock")
@NeedLogin
public class SpreadAction extends AbstractStockBaseAction {
	
	
	private static final String ACCOUNT_INDEX="/stock/accountIndex.jspa";
	private static final String BUY_RESULT="/stock/$accountId/buyStep1.jspa";
	private static final String 	SELL_RESULT="/stock/$accountId/sellStep1.jspa";
	
	@RequestMapping(value = "/spread", method = RequestMethod.GET)
	public String spread(
			@RequestParam(value = "stockCode", defaultValue = "") String stockCode,
			@RequestParam(value = "direction", defaultValue = "1") String direction,
			@RequestParam(value = "from", defaultValue = "") String from,
			RedirectAttributes attributes,
			HttpSessionWrapper session,
			HttpServletResponse response)
			throws IOException {
		stockCode = URLUtils.ParamterFilter(stockCode,'\0');
		if(StringUtils.isNotBlank(from)){
			from = URLUtils.ParamterFilter(from,'\0');
			session.setAttribute(AttributeKeys.AD_FROM, from);
		}
		String userId = getSelfUserId(session);
		Long accountId=null;
		try {
			//获取默认账户id
			 List<UserAccAuth> userAccAuthlist = userAuthService.queryAccessAble(userId);
			if(userAccAuthlist != null && userAccAuthlist.size() > 0){
				for(UserAccAuth auth:userAccAuthlist){
					//Broker broker=getBroker(auth.getBrokerId());
					if (auth != null
							&& auth._getMarketType() == MarketType.STOCK
							&& BrokerStatus.USABLE.status == auth.getBstatus()&&auth.getBind().equals("0")) {
						accountId = auth.getAccountId();
						break;
					}
				}
			}else{
				log.info("无可操作账户");
				return "redirect:"+ACCOUNT_INDEX;
			}
		} catch (ServiceException e) {
			log.error("exception",e);
		}
		if (StringUtils.isNotBlank(stockCode) && accountId != null) {//判断股票代码
			if("2".equals(direction)){
				return "redirect:"+(SELL_RESULT.replaceAll("\\$accountId", accountId.toString())+"?stockCode="+stockCode);
			}else{
				return "redirect:"+(BUY_RESULT.replaceAll("\\$accountId", accountId.toString())+"?stockCode="+stockCode);
			}			
		}else{
			return "redirect:"+ACCOUNT_INDEX;
		}
	}
}
