package com.cfo.stock.web.action.brokerBank;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.banktrans.BankAccountQueryService;
import com.jrj.stocktrade.api.banktrans.vo.BankAccountInfo;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.pub.BranchQueryService;
import com.jrj.stocktrade.api.pub.PasswordFlagService;
import com.jrj.stocktrade.api.pub.vo.PasswordFlag;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;


/**
 *首页
 *
 */
public class BrokerBaseAction  extends AbstractStockBaseAction{

	@Autowired
	BankAccountQueryService bankAccountQueryService;
	@Autowired
	StockAccountQueryService stockAccountQueryService;
	@Autowired	
	AccountQueryService accountQueryService;
	@Autowired
	BranchQueryService branchQueryService;
	@Autowired
	PasswordFlagService passwordFlagService;
	

	protected List<BankAccountInfo> bankList = new ArrayList<BankAccountInfo>();
	
	public void initBroker(int type, Long accountId, HttpSessionWrapper session, Model model) throws ServiceException{

		String userId = getSelfUserId(session);
		UserAccount account=getUserAccount(accountId);
		String brokerId=account.getBrokerId();
		//获取证券账户绑定的银行卡
		 bankList =  bankAccountQueryService.bankAccountQuery(userId, accountId);
		 
		 //查询默认业务是否需要密码
		 if(bankList.size()>0){
			 if(type != -1){
				 String _bankNo = bankList.get(0).getBankNo();
				 PasswordFlag  pf  = getPasswordFlag(brokerId,userId, accountId, _bankNo, type);
				 model.addAttribute("passwordFlag", pf);
				 model.addAttribute("_bankNo", _bankNo);
				 model.addAttribute("_bankAcc",bankList.get(0).getBankAccount());
			 } 
		 }else{
			 model.addAttribute("errMsg", "未知的银行信息");
		 }
		 		 
		 model.addAttribute("bankList", bankList);
		 model.addAttribute("Column", "ZQZH");
		 model.addAttribute("broker_id", brokerId);
	}
	
	public   PasswordFlag getPasswordFlag(String brokerid,String userId,Long accountId ,String bankNo ,int type){
		PasswordFlag pf = null;
		try {
			pf =passwordFlagService.passwordFlagQuery(userId, accountId, bankNo, type);
		} catch (ServiceException e) {
			log.error(e.getErrorInfo()); 
		}
		return pf;
	}
	
}
