package com.cfo.stock.web.hk.action.account;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.hk.hkvo.StockHoldingVo;
import com.cfo.stock.web.hk.services.account.HkAccountWebService;
import com.cfo.stock.web.interceptor.HkPasswordInterceptor.NeedHkPassword;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.account.UserInfoService;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.authorization.AuthorizationService;
import com.jrj.stocktrade.api.deposit.FundService;
import com.jrj.stocktrade.api.deposit.vo.FundInfo;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.pub.BranchQueryService;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;
import com.jrj.stocktrade.hk.api.service.HkSecurityFundService;
import com.jrj.stocktrade.hk.api.vo.BuyingPower;
@Controller
@RequestMapping("/stock/hk/{accountId}/")
@NeedLogin
@TradeAccount
@NeedHkPassword
public class HkAccountAction extends AbstractStockBaseAction{
	private static final int ps =15;
	@Autowired
	HkAccountWebService hkAccountWebService;
	@Autowired
	AccountService accountService;
	@Autowired
	FundService fundService;
	@Autowired
	AccountWebService accountWebService;	
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	StockAccountQueryService stockAccountQueryService;
	@Autowired	
	AccountQueryService accountQueryService;
	@Autowired
	BranchQueryService branchQueryService;
	@Autowired
	UserAuthService userAuthService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	AuthorizationService authorizationService;
	@Autowired
	protected UserAccountService userAccountService;
	
	@Autowired
	HkSecurityFundService hkSecurityFundService;
	
	@RequestMapping(value="/accountIndex",method=RequestMethod.GET)
	public String account(
			@PathVariable Long accountId,
			@RequestParam(value="transferType" ,defaultValue="1" )String transferType,
			@RequestParam(value = "from", defaultValue = "") String from,
			HttpSessionWrapper session,
			Model model) {
		
		// 记录from参数
		if(StringUtils.isNotBlank(from)){
			from = URLUtils.ParamterFilter(from,'\0');
			session.setAttribute(AttributeKeys.AD_FROM, from);
		}
		
		String viewName="/account/index";
		
		return viewName;
	}
	/**
	 * 当前持仓
	 * @param accountId
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/stockInfoIndex",method=RequestMethod.GET)
	public String stockInfo(
			@PathVariable Long accountId,
			HttpSessionWrapper session, Model model){
		
		String userId=getSelfUserId(session);
		//查询持仓
		List<StockHoldingVo> positions = new ArrayList<StockHoldingVo>();
		try{
			positions = hkAccountWebService.stockHoldingQueryPage(userId, accountId , 1, ps);
		}catch(ServiceException e){
			log.error(e.getErrorInfo(), e);
			model.addAttribute("errMsg",StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		model.addAttribute("isfirst", 1);
		model.addAttribute("positions", positions);
		model.addAttribute("pageNum", 1);

		return "/account/stockInfoIndex_hk";
	}
	/**
	 * 当前 持仓 分页ajax
	 * @param pageNum
	 * @param accountId
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/getPositionPage",method=RequestMethod.GET)
	public String getPositionNext(@RequestParam(value="pageNum" ,defaultValue="1")String pageNum,
			@PathVariable Long accountId,
			HttpSessionWrapper session, 
			Model model){
		
		String userId= getSelfUserId(session);
		List<StockHoldingVo> positions = new ArrayList<StockHoldingVo>();
		try {
			positions = hkAccountWebService.stockHoldingQueryPage(userId, accountId, Integer.parseInt(pageNum), ps);
		} catch (ServiceException e){
			log.error(e.getErrorInfo(), e);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getErrorInfo()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.addAttribute("errMsg", StockUtil.getErrorInfo(e.getMessage()));
		}
		model.addAttribute("positions",positions);
		model.addAttribute("pageNum", Integer.parseInt(pageNum));
		String viewName="/piece/position";
		return viewName;
	}
		
	/**
	 * 转户的连接
	 * @return
	 */
	@RequestMapping(value="/changeBroker",method=RequestMethod.GET)
	public String changeBroker(){
		return "changeBroker";
	}
	@RequestMapping(value="/ajaxgetQQStatus",method=RequestMethod.GET)
	@ResponseBody
	public String getQQStatus(){
		return "var online = [0,1] ";
	}
	
	@RequestMapping(value="/getComplaintsPhone",method=RequestMethod.GET)
	public String getComplaintsPhone() throws ServiceException{
		String viewName="/account/complaints_phone";
		return viewName;
	}
	
	/**
	 * 异步加载账户列表
	 * @param accountId
	 * @param session
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value="/ajaxAccountList",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxAccountList(
			@PathVariable Long accountId,
			@RequestParam("accountId") Long curAccountId, 
			HttpSessionWrapper session) throws ServiceException{
		JSONObject json = null;
		
		//缓存标记，默认从缓存中取数据
		boolean flag = true;
		if(accountId.equals(curAccountId)){//如果为当前用户，则从接口取数据
			if(log.isDebugEnabled()){
				log.debug("是当前用户。取接口数据 accountId==>"+accountId+", curAccountId==>"+curAccountId);
			}
			flag = false;
		}
		String cacheKey = createAssetCacheKey(curAccountId);
		json =(JSONObject) cache.get(cacheKey);
		if(flag && json != null){
			return json.toJSONString();
		}
		json = new JSONObject();
		json.put("accountId", curAccountId);
		
//		session.putValue(name, value, time);
		// 根据accountId查询accUserId 查询券商信息
		UserAccount accUserInfo = userAccountService
				.queryAccount(curAccountId);
		/*String brokerId = accUserInfo.getBrokerId();
		SecuritiesBroker sb = SecuritiesBroker.getBroker(brokerId);
		//券商名称
		json.put("qsName", sb.name);
		//券商代码
		json.put("qsFlag", sb.flag);*/
		String accUserId = accUserInfo.getUserId();
		try {
			//根据accUserId查询股东姓名
			UserInfoVo userInfo = personalService.getUserInfo(accUserId);
			//股东姓名
			json.put("clientName", userInfo.getRealName());
		} catch (Exception e) {
			log.error("get userinfo error",e);
		}
		
		try {
			BuyingPower fundAll = hkSecurityFundService.buyingPower(accUserId, accountId);
			//获取持仓
			List<StockHoldingVo> positions = hkAccountWebService.stockHoldingQuery(accUserId, accountId);
			
			BigDecimal totalMarketValue=BigDecimal.ZERO;
			//计算总市值
			for(StockHoldingVo s:positions){
				totalMarketValue = totalMarketValue.add(s.getMarketValue());
			}
			//总资产加入总市值
			fundAll.setBuyingPower(fundAll.getBuyingPower().add(totalMarketValue));
			
			json.put("fundAll", fundAll);
			
		} catch (Exception e) {
			log.error("accountId==>"+curAccountId+", exception:",e);
			json.put("errMsg_fund", e.getMessage()==null ? "error" : e.getMessage());
		}
		/*try{
			//今日涨跌
			BigDecimal todayGenLose = fundService.caculateTodayGenLose(userId, curAccountId, true);
			json.put("todayGenLose", todayGenLose);
		}catch(Exception e){
			log.error("accountId==>"+curAccountId+", exception:",e);
			json.put("errMsg_todayGenLose", e.getMessage()==null ? "error" : e.getMessage());
		}*/
		
		Calendar cal=Calendar.getInstance();
		json.put("updateTime", cal.getTimeInMillis());
		
		cal.add(Calendar.MINUTE, Integer.parseInt(Global.WEB_CACHE_EXPIRE_TIME));
		cache.set(cacheKey, json, cal.getTime());
		if(log.isDebugEnabled()){
			log.debug("user asset data: accountId==>"+accountId+", curAccountId==>"+curAccountId+"data==>"+json.toJSONString());
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value="/ajaxFundInfo",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxFundInfo(
			@PathVariable Long accountId,
			@RequestParam("accountId")Long curAccountId,
			HttpSessionWrapper session
			){
		//缓存标记，默认从缓存中取数据
		boolean flag = true;
		if(accountId.equals(curAccountId)){//如果为当前用户，则从接口取数据
			flag = false;
		}
		
		String userId = getSelfUserId(session);
		JSONObject json=new JSONObject();
		FundInfo fund=null;
		try {
			fund = getFundInfo(userId, accountId, flag);
		} catch (ServiceException e) {
			log.error("exception:#ajaxFundInfo,userId==>"+userId+", accountId==>"+accountId, e);
			json.put("errMsg_fundInfo", e.getMessage());
		}
		
		json.put("fund", fund);
		return json.toJSONString();
	}
	
	/**
	 * 初始化券商交易密码页面
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value="/initQsPwd",method=RequestMethod.GET)
	public String initQsPwd() throws ServiceException{
		String viewName="/account/initpwd";
		return viewName;
	}
	
	/**
	 * 初始化券商交易密码（处理）
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value="/initQsPwdResult",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String initQsPwdResult(
			@RequestParam("password") String password,
			@PathVariable Long accountId,
			HttpSessionWrapper session) throws ServiceException{
		
		String userId=getSelfUserId(session);
		
		JSONObject json =new JSONObject();
		try {
			authorizationService.authorization(userId, accountId, password);
			json.put("errorno", 0);
			
		} catch (ServiceException e) {
			log.error("#authroization error", e);
			json.put("errorno", -1);
			json.put("errMsg", e.getMessage());
		}
		return json.toJSONString();
	}
}
