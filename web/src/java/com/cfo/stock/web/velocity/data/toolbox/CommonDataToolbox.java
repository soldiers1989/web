package com.cfo.stock.web.velocity.data.toolbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.velocity.data.DataServiceTool;
import com.jrj.common.velocity.data.toolbox.DataBaseToolbox;
import com.jrj.stocktrade.api.account.vo.FundAccount;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.common.ExchangeType;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.pub.vo.BranchQueryResp;
import com.jrj.stocktrade.api.stock.vo.Stock;
import com.jrj.stocktrade.api.stock.vo.StockAccount;

/**
 * 公共信息toolbox类
 * @author yuan.cheng
 *
 */
public class CommonDataToolbox extends DataBaseToolbox{
	
	/**
	 * 获取用户账户基本信息
	 * @param userId
	 * @param accountId
	 * @return
	 */
	public Map<String,Object> getAccUserInfo(String userId, Long accountId){
		if(userId==null || accountId==null)return null;
		Map<String,Object> result=new HashMap<String, Object>();
		UserAccAuth userAuth = null;
		try {
			userAuth = DataServiceTool.userAuthService.queryUserAuth(userId,accountId);
			if(userAuth != null){
				//券商名称
				result.put("qsName", userAuth.getBrokerName());
				//券商代码
				result.put("qsFlag", userAuth.getBrokerId());
				//券商类型，SA沪深,HK港股
				result.put("qsType", userAuth.getMarketType());
			}else{
				result.put("errMsg_broker", "获取券商信息失败");
			}
		} catch (ServiceException e) {
			log.error("exception:",e);
			result.put("errMsg_broker", "获取券商信息失败");
		}
		UserAccount userAccount= null;
		UserInfoVo userInfo = null;
		//获取股东姓名，先从cfo_account表中获取，没有的情况下在通行证中获取客户真实姓名
		String clientName="";
		if(userAuth != null){
			String accUserId = userAuth.getAccUserId();
			//根据accUserId查询股东姓名
			userInfo = DataServiceTool.personalService.getUserInfo(accUserId);
			try {
				userAccount = DataServiceTool.userAccountService.queryAccount(accUserId, accountId);
			} catch (ServiceException e) {
				log.error(e.getErrorInfo(),e);
				result.put("errMsg_brankName", "获取绑定账户信息失败");
			}
			if(userAccount!=null){
				if(userAccount.getClientName()!=null&&!userAccount.getClientName().equals("")){
					clientName=userAccount.getClientName();
				}
			}
			
			if(clientName.equals("")){
				//股东姓名
				if(userInfo != null){
					result.put("clientName", userInfo.getRealName());
				}else{
					result.put("errMsg_UserName", "获取用户真实姓名信息失败");
				}
			}else{
				result.put("clientName", clientName);
			}
		}
		
		try {
			//查询主账户
			FundAccount fundAccount = DataServiceTool.userAuthWebService.getFundAccount(userAuth);
			// 账户资产，资金账号
			result.put("fundAccount", fundAccount);
			// 查询主账户的营业部
			if (fundAccount != null && userInfo !=null) {
				String idN = userInfo.getIdNumber();
				BranchQueryResp bqr = DataServiceTool.branchQueryService.branchQuery(userId,
						accountId, fundAccount.getBranchNo() + "", idN);
				if (bqr == null) {
					this.log.error(userId
							+ "  get the portName exception : no more messger for user's portNo");
					result.put("brankName", "");
				} else {
					//营业部名称
					result.put("brankName", bqr.getBranchName());
				}
			}
		} catch (ServiceException e) {
			log.error(e.getErrorInfo(),e);
			result.put("errMsg_brankName", "获取分支机构名称错误");
		} catch (Exception e){
			log.error("accountId==>"+accountId+", exception:",e);
		}
		
		List<StockAccount> accountList= null;
		if(userAuth._getMarketType()==MarketType.STOCK){
			try {
				accountList =  DataServiceTool.stockAccountQueryService.clientStockAccountQuery(userId, accountId);
			} catch (Exception e) {
				log.error("userId==>"+userId+"accountId==>"+accountId+", exception:",e);
			}
		}
		if(accountList != null){
			for(StockAccount a:accountList){
				if(ExchangeType.SH == a.getExchangeType()){
					result.put("stockAccount_SH", true);
				}
				if(ExchangeType.SZ == a.getExchangeType()){
					result.put("stockAccount_SZ", true);
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取用户绑定账户列表
	 * @param userId
	 * @return
	 */
	public List<UserAccAuth> getAccUserList(String userId){
		try {
			List<UserAccAuth> list = DataServiceTool.userAuthService.queryAccessAble(userId);
			// 左侧绑定的账户列表，仅accountId
			return list;
		} catch (ServiceException e) {
			log.error(e.getErrorInfo(),e);
		} catch (Exception e){
			log.error("userId==>"+userId+", exception:",e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public JSONObject getItouGuUserInfo(String userId){
		JSONObject json = DataServiceTool.accountWebService.queryItouguUserInfo(userId);
		return json;
	}
	
	public JSONObject queryStocks(Integer bigType){
		List<Stock> list= DataServiceTool.stockDataService.queryStock(bigType, null, null);
		JSONObject result=new JSONObject();
		
		JSONArray arr=new JSONArray();
		if(list!=null){
			for(Stock stock:list){
				JSONObject obj=new JSONObject();
				obj.put("stid", stock.getCode());
				obj.put("code", stock.getCode());
				obj.put("name", stock.getSname());
				obj.put("shrt", stock.getSname());
				if(stock.getMarktType()==1){
				obj.put("mrkt", "cn.sz");
				}else if(stock.getMarktType()==2){
					obj.put("mrkt", "cn.sh");
				}else{
					obj.put("mrkt", "");
				}
				//暂时写死 r 逆回购
				obj.put("type", "r");
				obj.put("stat", "1");
				arr.add(obj);
			}
		}
		result.put("CodeData", arr);
		return result;
	}
}