/**
 * 
 */
package com.cfo.stock.web.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import ocx.AESWithJCE;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cfo.common.service.PersonalService;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.constant.SessionConfig;
import com.cfo.common.useragent.Browser;
import com.cfo.common.useragent.DeviceType;
import com.cfo.common.useragent.OperatingSystem;
import com.cfo.common.useragent.UserAgent;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.services.JrjUserInfoService;
import com.cfo.stock.web.support.PathVariableEx;
import com.jrj.common.cache.memcached.MemcachedCache;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.FundAccount;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.deposit.FundService;
import com.jrj.stocktrade.api.deposit.vo.FundInfo;
import com.jrj.stocktrade.api.deposit.vo.FundInfoEx;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.helper.BrokerHelper;
import com.jrj.stocktrade.api.iframe.TradeIframeService;
import com.jrj.stocktrade.api.pub.BranchQueryService;
import com.jrj.stocktrade.api.rpc.HsRpcContext;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;
import com.jrj.stocktrade.api.stock.StockQueryService;
import com.jrj.stocktrade.api.stock.vo.StockAccount;
import com.jrj.stocktrade.api.stock.vo.StockInfoEx;

/**
 * 
 * AbstractStockBaseAction web项目Action抽象基类，放业务相关底层公共方法
 * 
 * @history <PRE>
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014-4-11    		yuanlong.wang     create  
 * v1.1         2014-10-13    		yuan.cheng          modify  
 * ---------------------------------------------------------
 * </PRE>
 * 
 */

public abstract class AbstractStockBaseAction {
	protected Logger log = Logger.getLogger(getClass());
	//asset cache key
	private static final String ASSET_CACHE_KEY="asset_$accountId";
	private static final String FUNDINFO_CACHE_KEY="fundinfo_$accountId";
	protected static final String CONTINUE = "continue";// “继续”页面，表示后台操作进行中，请返回查询结果
	// private static final String IC = "_ic";
	private static final String FUND_ALL_CACHE_KEY = "fundall_$accountId";
	//Cache的过期时间
	private static final long EXPIRED =Integer.parseInt(Global.WEB_CACHE_EXPIRE_TIME)* 60 *1000; 
	//用于ajax请求时接口登录过期错误提示键，一般此值设置为-1 例如 json.put(HK_AJAX_LOGIN_EXPIRE_ERROR_KEY,-1)
	public static final String HK_AJAX_LOGIN_EXPIRE_ERROR_KEY="hk_password_error";
	private ThreadLocal<Map<String, Object>> localSession=new ThreadLocal<Map<String,Object>>();// 用于存储本地session

	@Autowired
	protected AccountService accountService;

	@Autowired
	protected JrjUserInfoService jrjUserInfoService;

	@Autowired
	protected TradeIframeService tradeIframeService;

	@Autowired
	protected PersonalService personalService;

	@Autowired
	protected FundService fundService;

	@Autowired
	protected UserAuthService userAuthService;

	@Autowired
	protected AccountQueryService accountQueryService;

	@Autowired
	protected BranchQueryService branchQueryService;

	@Autowired
	protected UserAccountService userAccountService;

	@Autowired
	protected StockQueryService stockQueryService;

	@Autowired
	protected StockAccountQueryService stockAccountQueryService;
	
	@Autowired
	protected MemcachedCache cache;
	
	@Autowired
	protected BrokerHelper brokerHelper;
	
	/**
	 * 获得当前登录用户UserId
	 * 
	 * @return 返回当前登录用户UserId,如果未登录则返回null
	 */
	public String getSelfUserId(HttpSessionWrapper session) {
		String loginStatus = (String) session.getAttribute(AttributeKeys.LOGIN);
		if (loginStatus == null || !SessionConfig.LOGIN_OK.equals(loginStatus)) {
			return null;
		}
		// 检查当前是否过期
		/*
		 * long lastaccesstime=session.getLastAccessedTime(); long now=new
		 * Date().getTime(); if(now-lastaccesstime>SessionConfig.expire_time){
		 * session.deleteSession(); return null; }else{ return
		 * session.getAttribute(AttributeKeys.USER_ID, String.class); }
		 */
		return session.getAttribute(AttributeKeys.USER_ID, String.class);
	}

	/**
	 * 获取当前用户信息
	 * 
	 * @return
	 */
	public UserInfoVo getSelfUserInfo(HttpSessionWrapper session) {
		String userid = getSelfUserId(session);
		if (userid != null) {
			UserInfoVo user = session.getAttribute(AttributeKeys.USER_INFO,
					UserInfoVo.class);
			log.debug("session 中没有用户信息");
			if (user == null) {
				log.debug("session 中没有用户信息 开始查找用户中心 userId ：" + userid);
				user = personalService.getUserInfo(userid);
				if (user != null) {
					user.setUserId(userid);
					session.setAttribute(AttributeKeys.USER_INFO, user);
				}
			}
			return user;
		}
		return null;
	}

	public String getSelfUserName(HttpSessionWrapper session) {
		UserInfoVo user = getSelfUserInfo(session);
		if (user == null) {
			return null;
		}
		return user.getRealName();
	}

	@ModelAttribute
	public void initMode(
			HttpServletRequest request, 
			HttpSessionWrapper session,
			@PathVariableEx Long accountId,
			Model model) {
		String isajax = request.getHeader("x-requested-with");
		if(isajax != null){
			return;
		}
		model.addAttribute("selfUserId", getSelfUserId(session));
		model.addAttribute("selfUserName", getSelfUserName(session));
		model.addAttribute("accountId",accountId);
		//根据accountId查询accUserId 查询券商信息
		UserAccount accUserInfo = userAccountService.queryAccount(accountId);
		if(accUserInfo != null){
			Broker sb = getBroker(accUserInfo.getBrokerId());
			if(sb!=null){
			//券商名称
			model.addAttribute("qsName", sb.getBrokerName());
			//券商代码
			model.addAttribute("qsFlag", sb.getBrokerId());
			//券商类型，SA沪深,HK港股
			model.addAttribute("qsType", sb.getType());
			}
		}
	}

	public Broker getBroker(String brokerId){
			return brokerHelper.getBroker(brokerId);
	}
	public Broker getBroker(Long accountId){
		String broker=getBrokerId(accountId);
		if(broker!=null){
			return getBroker(broker);
		}
		return null;
	}
	public String getBrokerId(Long accountId){
		if(accountId==null)return null;
		UserAccount acc= getUserAccount(accountId);
		return acc!=null?acc.getBrokerId():null;
	}
	public UserAccount getUserAccount(Long accountId){
		Map<String,Object> map=localSession.get();
		String key="_localAcc_"+accountId;
		if (( map!= null)) {
			if(map.containsKey(key)){
				return (UserAccount)map.get(key);
			}
		} else {
			map=new HashMap<String,Object>();
		}
		UserAccount account= userAccountService.queryAccount(accountId);
		if(account!=null){
			map.put(key, account);
			localSession.set(map);
		}
		return account;
	}
	/**
	 * 获取当前账户资产数据
	 * 
	 * @param request
	 * @param model
	 */
	public void initAccount(HttpServletRequest request,
			HttpSessionWrapper session, Model model) {
		String isajax = request.getHeader("x-requested-with");
		if(isajax != null){
			return;
		}
		Pattern pattern = Pattern.compile("^/stock/(\\d+)/.*$");
		Matcher match = pattern.matcher(request.getRequestURI());
		Long accountId=null;
		//String userId = getSelfUserId(session);
		if (!match.find()) {
		} else {
			String matchStr = match.group(1);
			if (!StringUtils.isNumeric(matchStr)) {
				return;
			}
			accountId = Long.parseLong(matchStr);
		}
		log.info("获取到accountId==>" + accountId);
		if(accountId==null)return;
		model.addAttribute("accountId", accountId);
	}
	
	/**
	 * 查询证券账户
	 * @param userId
	 * @param accountId
	 * @return
	 * @throws ServiceException
	 */
	public FundAccount getFundAccount(String userId,Long accountId) throws ServiceException{
		List<FundAccount> fundAccountList = accountQueryService
				.fundAccountQuery(userId, accountId);
		FundAccount fundAccount = null;
		for (FundAccount fa : fundAccountList) {
			fundAccount = fa;
			if (fa.getMainFlag() != null && "1".equals(fa.getMainFlag().getValue())) {
				fundAccount = fa;
			}
		}
		return fundAccount;
	}


	/**
	 * 查询股东列表 
	 * @param userId
	 * @param accountId
	 * @return
	 */
	public List<StockAccount> getStockAccountList(String userId, Long accountId){
		List<StockAccount>stockAccountList = new ArrayList<StockAccount>(); 
		try{
				stockAccountList = stockAccountQueryService.clientStockAccountQuery(userId, accountId);
			} catch (ServiceException e) {
				log.error("#getStockAccountList: userId==>"+userId+", accountId==>"+accountId+" e==>",e);
			}
		return stockAccountList;
	}

	/**
	 * 查询账户资产
	 * @param session
	 * @param userId
	 * @param accountId
	 * @param flag 是否刷新缓存
	 * @return
	 * @throws ServiceException
	 */
	public FundInfo getFundInfo(String userId, Long accountId, boolean flag)
			throws ServiceException {
		FundInfo fund=null;
		String cacheKey = createFondInfoCacheKey(accountId);
		fund = (FundInfo)cache.get(cacheKey);
		if(flag && fund != null){
			return fund;
		}
		
		fund = fundService.clientFundFastQuery(userId, accountId);
		cache.set(cacheKey, fund,new Date(System.currentTimeMillis()+EXPIRED));
		return fund;
	}
	

	/**
	 * 查询账户资产（精确）
	 * @param session
	 * @param userId
	 * @param accountId
	 * @return
	 * @throws ServiceException
	 */
	public FundInfoEx getFundAll(String userId, Long accountId, boolean flag)
			throws ServiceException {
		FundInfoEx fundAll = null;
		String cacheKey = createFundAllCacheKey(accountId);
		fundAll = (FundInfoEx)cache.get(cacheKey);
		if(flag && fundAll != null){
			return fundAll;
		}		
		fundAll = fundService.clientFundAllQuery(userId, accountId);
		cache.set(cacheKey, fundAll, new Date(System.currentTimeMillis() + EXPIRED));
		return fundAll;
	}
	
	

	/**
	 * 查询所有持仓封装浮动盈亏
	 * @return
	 */
	public BigDecimal getProfitLoss(String userId, Long accountId){
		List<StockInfoEx> positionsAll = new ArrayList<StockInfoEx>();
		BigDecimal profitLoss =BigDecimal.ZERO;
		try{
			positionsAll=stockQueryService.securityStockQuery(userId, accountId);
			for(StockInfoEx info : positionsAll){
				profitLoss = profitLoss.add(info.getIncomeBalance());
			}
		}catch(ServiceException e){
			log.error(e.getErrorInfo());
		}
		return profitLoss;
	}

	/**
	 * 是否已经登录
	 * 
	 * @return
	 */
	public boolean isLogin(HttpSessionWrapper session) {
		// 检验是否有登录标示
		String login = (String) session.getAttribute(AttributeKeys.LOGIN);
		if (login == null || !SessionConfig.LOGIN_OK.equals(login)) {
			return false;
		}
		// 检查当前是否过期
		long lastaccesstime = session.getLastAccessedTime();
		long now = new Date().getTime();
		if (now - lastaccesstime > SessionConfig.expire_time) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * 本地session 如需使用memecache进行存储Session 请调用 session
	 * 
	 * @return
	 */
	public ThreadLocal<Map<String, Object>> getLocalSession() {
		return localSession;
	}

	/**
	 * 生成cache key
	 * @param accountId
	 * @return
	 */
	protected String createAssetCacheKey(Long accountId){
		return ASSET_CACHE_KEY.replaceAll("\\$accountId", accountId.toString());
	}
	/**
	 * 生成资金cache key
	 * @param accountId
	 * @return
	 */
	protected String createFondInfoCacheKey(Long accountId) {
		return FUNDINFO_CACHE_KEY.replaceAll("\\$accountId", accountId.toString());
	}
	
	protected String createFundAllCacheKey(Long accountId) {
		return FUND_ALL_CACHE_KEY.replaceAll("\\$accountId",accountId.toString());
	}
	/**
	 * 添加操作广告来源
	 */
	public void assembleOpFrom(HttpSessionWrapper session) {
		String from = (String) session.getAttribute(AttributeKeys.AD_FROM);
		if (from != null) {
			HsRpcContext.setFrom(from);
		}
	}
	/**
	 * 获取当前环境
	 */
	public boolean isTestEnvironment(){
		if(Global.CFO_WEB_ENVIRONMENT_TYPE.equals("test")){
			return true;
		}
		return false;
	}
	/**
	 * 采集客户端信息
	 * @param request
	 * @param session
	 */
	public void collectClientInfo(HttpServletRequest request,
			HttpSessionWrapper session){
		String mcrypt_key=session.getAttribute("mcrypt_key",String.class);
		String local_network = request.getParameter("local_network");//加密后的客户端网卡和MAC信息;    
		String local_disk = request.getParameter("local_disk");//获取加密后的客户端硬盘序列号;
		String local_cpu = request.getParameter("local_cpu");
		String network="",disk="",cpu="";
		if (mcrypt_key != null) {
			if (local_network != null){
				network = AESWithJCE.getResult(mcrypt_key, local_network);// 调用解密接口.获取网卡信息明文;
				if(network!=null){
					String[] maces=network.split(";");
					if(maces!=null&&maces.length>0){
						network=maces[0].split(":")[0];
					}
				}
			}
			if (local_disk != null)
				disk = AESWithJCE.getResult(mcrypt_key, local_disk);// 调用解密接口.获取硬盘序列号信息明文;
			if (local_cpu != null)
				cpu = AESWithJCE.getResult(mcrypt_key, local_cpu);// 调用解密接口.获取cpuid号信息明文;
		}
		HsRpcContext.setUserMac(network);
	     HsRpcContext.putValue(HsRpcContext.MOBILEIMEI, network);
	     HsRpcContext.putValue(HsRpcContext.MOBILEUUID, disk+";"+cpu);//硬盘序列号+cpu序列号
	     
	     UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));  
	       Browser browser = userAgent.getBrowser();  
	       OperatingSystem os = userAgent.getOperatingSystem();
	     HsRpcContext.putValue(HsRpcContext.TERMINAL_DEVICE, os.getDeviceType().getName());
	     HsRpcContext.putValue(HsRpcContext.TERMINAL_OS, os.getName());
	     HsRpcContext.putValue(HsRpcContext.TERMINAL_PLATFORM, browser.getName());
	     session.setAttribute(HsRpcContext.MOBILEIMEI, network);
	     session.setAttribute(HsRpcContext.MOBILEUUID, disk+";"+cpu);
	     session.setAttribute(HsRpcContext.USERMAC, network);
	     session.removeAttribute("mcrypt_key");
	}
}
