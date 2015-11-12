package com.cfo.stock.web.services.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cfo.stock.web.util.StockUtil;
import com.cfo.stock.web.vo.StockAccountStatusVo;
import com.cfo.stock.web.vo.UserAuthVo;
import com.jrj.common.service.AbstractBaseService;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.FundAccount;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.common.AccUserStatus;
import com.jrj.stocktrade.api.common.AccUserType;
import com.jrj.stocktrade.api.common.ExchangeType;
import com.jrj.stocktrade.api.common.StockAccountType;
import com.jrj.stocktrade.api.common.TradeWay;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.helper.BrokerHelper;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;
import com.jrj.stocktrade.api.stock.vo.StockAccount;

@Service
public class UserAuthWebService extends AbstractBaseService {
	
	@Autowired
	UserAccountService userAccountService;
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	AccountWebService accountWebService;
	@Autowired
	AccountQueryService accountQueryService;
	@Autowired
	StockAccountQueryService stockAccountQueryService;
	@Autowired
	BrokerHelper brokerHelper;
	
	/**
	 * 处理核心返回的数据
	 */
	public List<UserAuthVo> doUserAccAuthDate(List<UserAccAuth> accauthList) throws ServiceException{
		List<UserAuthVo>  result = new ArrayList<UserAuthVo>();
		List<UserAuthVo> temp=new ArrayList<UserAuthVo>();
		for(UserAccAuth userAccAuth : accauthList){
			if(userAccAuth != null){
				UserAuthVo userAuthVo = new  UserAuthVo();
				BeanUtils.copyProperties(userAccAuth, userAuthVo);
				//查询关系人
				Map<String, String> relaMap = getUserInfo(userAccAuth.getRelationUserId());
				//关系人姓名
				String trueName = "--";
				if(relaMap.get("trueName") != null){
					trueName = relaMap.get("trueName")+"";
				}
				//发起授权状态描述
				if(userAccAuth.getStatus() != null){
					//自控制中
					if(userAccAuth.getStatus() == AccUserStatus.OWNCONTROL.status){
						userAuthVo.setMessage("已绑定");
					}else if(userAccAuth.getStatus() == AccUserStatus.OTHERSREQUESTAUTH.status){//
						userAuthVo.setMessage(trueName+"申请绑定您的账户");
					}else if(userAccAuth.getStatus() == AccUserStatus.OTHERCONTROL.status){//
						userAuthVo.setMessage(trueName+"已绑定");
					}else if(userAccAuth.getStatus() == AccUserStatus.DOREQUEST.status){//
						userAuthVo.setMessage("正在申请绑定"+trueName+"的账户，等待"+trueName+"验证通过");
					}else if(userAccAuth.getStatus() == AccUserStatus.REFUSEREQUEST.status){//
						userAuthVo.setMessage(trueName+"账户拒绝了您的申请");
					}else if(userAccAuth.getStatus() == AccUserStatus.CONTROLING.status){//
						userAuthVo.setMessage("已绑定");
					}else if(userAccAuth.getStatus() == AccUserStatus.RELIEVE.status){//
						userAuthVo.setMessage(trueName+"账户解除绑定 ");
					}
				}
				if(userAccAuth.getStatus() == AccUserStatus.REFUSEREQUEST.status || 
						userAccAuth.getStatus() == AccUserStatus.RELIEVE.status ||
						userAccAuth.getStatus() == AccUserStatus.DOREQUEST.status){
					temp.add(userAuthVo);
				}else{
					result.add(userAuthVo);
				}
			}
		}
		if(temp.size()>0){
			result.addAll(temp);
		}
		return result;
	}
	/**
	 * 处理核心返回的数据
	 */
	public List<StockAccountStatusVo> doStockAccountStatus(List<StockAccountStatus> stockAccountList,List<UserAccAuth> accauthList){
		List<StockAccountStatusVo>  result = new ArrayList<StockAccountStatusVo>();
		for(StockAccountStatus stockAccountStatus : stockAccountList){
			if(stockAccountStatus != null){
				//TODO 2015-01-08 只要存在已绑定记录 不管什么状态 都消失
				//过滤已绑定的记录
//				if(stockAccountStatus.getAccountStatus() == AccountStatus.TRADEABLE){
					//判断状态为待绑定的记录是否已绑定成功
					if(isBindForOpenUser(stockAccountStatus,accauthList)){
						continue;
					}
//				}
				StockAccountStatusVo stockAccountStatusVo = new  StockAccountStatusVo();
				BeanUtils.copyProperties(stockAccountStatus, stockAccountStatusVo);
				stockAccountStatusVo.setAccountStatus(stockAccountStatus.getAccountStatus().getValue());
				stockAccountStatusVo.setType(stockAccountStatus.getType().type);
				stockAccountStatusVo.setBrokerId(stockAccountStatus.getBrokerId());
				stockAccountStatusVo.setState(stockAccountStatus.getAccountStatus().getValue());
				stockAccountStatusVo.setStateName(stockAccountStatus.getAccountStatus().getName());
				if(StockAccountType.ZHUANHU.type == stockAccountStatus.getType().type){
					if("开户中".equals(stockAccountStatusVo.getStateName())){
						stockAccountStatusVo.setStateName("转户中");
					}else if("开户失败".equals(stockAccountStatusVo.getStateName())){
						stockAccountStatusVo.setStateName("转户失败");
					}else if("开户成功".equals(stockAccountStatusVo.getStateName())){
						stockAccountStatusVo.setStateName("转户成功");
					}else if("开户完成".equals(stockAccountStatusVo.getStateName())){
						stockAccountStatusVo.setStateName("转户完成");
					}
				}
				stockAccountStatusVo.setStrBrokerId(stockAccountStatus.getBrokerId());
				stockAccountStatusVo.setStrBrokerName(brokerHelper.getBrokerName(stockAccountStatus.getBrokerId()));
				stockAccountStatusVo.setStrCreateTime(StockUtil.howLongFromToday(stockAccountStatus.getCreateTime()));
				if(stockAccountStatus.getTradeableTime() != null){
					stockAccountStatusVo.setStrTradeableTime(StockUtil.formatDate(stockAccountStatus.getTradeableTime(), StockUtil.format_1));
				}
				if(stockAccountStatus.getCompleteTime()!=null){
					stockAccountStatusVo.setStrComplateTime(StockUtil.formatDate(stockAccountStatus.getCompleteTime(), StockUtil.format_1));
				}
				stockAccountStatusVo.setStrAccountType(stockAccountStatus.getType().type);
				result.add(stockAccountStatusVo);
			}
		}
		return result;
	}
	/**
	 * 判断开户记录是否绑定
	 */
	public boolean isBindForOpenUser(StockAccountStatus stockAccountStatus,List<UserAccAuth> accauthList){
		Broker broker=brokerHelper.getBroker(stockAccountStatus.getBrokerId());
		String compareBroker=stockAccountStatus.getBrokerId();
		if(TradeWay.ITN==TradeWay.getTradeWayByValue(broker.getTradeWay())){
			compareBroker="ITN_"+compareBroker;
		}
		for(UserAccAuth UserAccAuth:accauthList){
			if(UserAccAuth.getUserId().equals(stockAccountStatus.getUserId()) && 
					UserAccAuth.getBrokerId().equals(compareBroker) &&
					UserAccAuth.getType()!=null	&& 
					UserAccAuth.getType().intValue() == AccUserType.OWNER.type
					){
				return true;
			}
		}
		return false;
	}
	/**
	 * 处理核心返回的数据
	 */
	public StockAccountStatusVo getStockAccountStatus(StockAccountStatus stockAccountStatus) {
		StockAccountStatusVo stockAccountStatusVo = new StockAccountStatusVo();
		if(stockAccountStatus != null){
			BeanUtils.copyProperties(stockAccountStatus, stockAccountStatusVo);
			stockAccountStatusVo.setAccountStatus(stockAccountStatus.getAccountStatus().getValue());
			stockAccountStatusVo.setStrCreateTime(StockUtil.formatDate(stockAccountStatus.getCreateTime(), StockUtil.format_1));
			stockAccountStatusVo.setStrBrokerId(stockAccountStatus.getBrokerId());
				stockAccountStatusVo.setStrBrokerName(brokerHelper.getBrokerName(stockAccountStatus.getBrokerId()));
		}
		return stockAccountStatusVo;

	}
	/**
	 * 通过用户id查询用户信息
	 */
	public Map<String, String> getUserInfo(String userId){
		Map<String,String> map = new HashMap<String,String>();
		try{
			//查询关系人用户信息
			String userResult = accountWebService.userInfo(userId);
			//关系人姓名
			String trueName = "";
			//关系人身份证号
			String idCard = "";
			if(StringUtils.isNotEmpty(userResult)){
				JSONArray jsonArr = JSONArray.parseArray(userResult);
				JSONObject jsons = JSONObject.parseObject(jsonArr.getString(0));
				trueName = jsons.getString("trueName");
				idCard = jsons.getString("idCard");
				map.put("trueName", trueName);
				map.put("idCard", idCard);
			}else{
				log.error("Method --> getUserInfo :"+userId+"查询通行证接口失败:"+userResult);
			}
		}catch(Exception e){
			log.error("Method --> getUserInfo userId:"+userId);
		}
		
		return map;
	}
	/**
	 * 查询资金账号
	 * @throws ServiceException 
	 */
	public FundAccount getFundAccount(UserAccAuth userAccAuth){
		try{
			List<FundAccount> fundaList = accountQueryService.fundAccountQuery(userAccAuth.getUserId(), userAccAuth.getAccountId());
			if(fundaList != null && fundaList.size()>0){
				FundAccount fundAccount = fundaList.get(0);
				//如果资金账号不是自己的，只保留前四位
				if(fundAccount != null && userAccAuth.getType() != null  &&
						userAccAuth.getType().intValue() == AccUserType.OTHER.type){
					//判断资金账号
					if(fundAccount.getFundAccount() != null && fundAccount.getFundAccount().length()>4){
						fundAccount.setFundAccount(fundAccount.getFundAccount().substring(0, 4)+"*******");
					}
				}
				return fundAccount;
			}
		}catch(Exception e){
			log.error("Method --> getFundAccount userId:"+userAccAuth.getUserId()+",accountId:"+userAccAuth.getAccountId(), e);
		}
		return null;
	}
	/**
	 * 证券股东信息查询
	 * @param userId 盈利宝帐户id
	 * @param accountId 账户ID
	 * @return 证券股东信息
	 */
	public Map<String, String> getStockAccount(UserAccAuth userAccAuth){
		Map<String, String> map = new HashMap<String, String>();
		try {
			List<StockAccount> acList = stockAccountQueryService.clientStockAccountQuery(userAccAuth.getUserId(), userAccAuth.getAccountId());
			if(acList != null){
				for(StockAccount stockAccount : acList){
					if(stockAccount.getExchangeType() == ExchangeType.SH){
						map.put("shstockAccount", stockAccount.getStockAccount());
					}else if(stockAccount.getExchangeType() == ExchangeType.SZ){
						map.put("szstockAccount", stockAccount.getStockAccount());
					}
				}
			}
		} catch (Exception e) {
			log.error("Method --> getStockAccount userId:"+userAccAuth.getUserId()+",accountId:"+userAccAuth.getAccountId(), e);
		}
		return map;
	}
}
