package com.cfo.stock.web.velocity.data;

import org.springframework.stereotype.Component;

import com.cfo.common.service.PersonalService;
import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.services.account.UserAuthWebService;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.helper.BrokerHelper;
import com.jrj.stocktrade.api.pub.BranchQueryService;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;
import com.jrj.stocktrade.api.stock.StockDataServiceApi;

@Component
public class DataServiceTool {
	public static PersonalService personalService;
	public static UserAccountService userAccountService;
	public static AccountQueryService accountQueryService;
	public static BranchQueryService branchQueryService;
	public static UserAuthService userAuthService;
	public static StockAccountQueryService stockAccountQueryService;
	public static UserAuthWebService userAuthWebService;	
	public static AccountWebService accountWebService;
	public static BrokerHelper brokerHelper;
	public static StockDataServiceApi stockDataService;
	public static PersonalService getPersonalService() {
		return personalService;
	}
	public void setPersonalService(PersonalService personalService) {
		DataServiceTool.personalService = personalService;
	}
	public static UserAccountService getUserAccountService() {
		return userAccountService;
	}
	public void setUserAccountService(UserAccountService userAccountService) {
		DataServiceTool.userAccountService = userAccountService;
	}
	public static AccountQueryService getAccountQueryService() {
		return accountQueryService;
	}
	public void setAccountQueryService(
			AccountQueryService accountQueryService) {
		DataServiceTool.accountQueryService = accountQueryService;
	}
	public static BranchQueryService getBranchQueryService() {
		return branchQueryService;
	}
	public void setBranchQueryService(BranchQueryService branchQueryService) {
		DataServiceTool.branchQueryService = branchQueryService;
	}
	public static UserAuthService getUserAuthService() {
		return userAuthService;
	}
	public void setUserAuthService(UserAuthService userAuthService) {
		DataServiceTool.userAuthService = userAuthService;
	}
	public static StockAccountQueryService getStockAccountQueryService() {
		return stockAccountQueryService;
	}
	public void setStockAccountQueryService(
			StockAccountQueryService stockAccountQueryService) {
		DataServiceTool.stockAccountQueryService = stockAccountQueryService;
	}
	public static UserAuthWebService getUserAuthWebService() {
		return userAuthWebService;
	}
	public void setUserAuthWebService(UserAuthWebService userAuthWebService) {
		DataServiceTool.userAuthWebService = userAuthWebService;
	}
	public static AccountWebService getAccountWebService() {
		return accountWebService;
	}
	public void setAccountWebService(AccountWebService accountWebService) {
		DataServiceTool.accountWebService = accountWebService;
	}
	public static BrokerHelper getBrokerHelper() {
		return brokerHelper;
	}
	public  void setBrokerHelper(BrokerHelper brokerHelper) {
		DataServiceTool.brokerHelper = brokerHelper;
	}
	public static StockDataServiceApi getStockDataService() {
		return stockDataService;
	}
	public static void setStockDataService(StockDataServiceApi stockDataService) {
		DataServiceTool.stockDataService = stockDataService;
	}
}