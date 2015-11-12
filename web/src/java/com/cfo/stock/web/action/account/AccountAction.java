package com.cfo.stock.web.action.account;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ocx.AESWithJCE;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.action.password.PasswordShape;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.interceptor.TradeAccountInterceptor.TradeAccount;
import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.util.StockUtil;
import com.jrj.stocktrade.api.account.AccountQueryService;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.account.UserInfoService;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.authorization.AuthorizationService;
import com.jrj.stocktrade.api.common.AccountStatus;
import com.jrj.stocktrade.api.common.BrokerId;
import com.jrj.stocktrade.api.common.BrokerType;
import com.jrj.stocktrade.api.common.StockAccountType;
import com.jrj.stocktrade.api.deposit.FundService;
import com.jrj.stocktrade.api.deposit.vo.FundInfo;
import com.jrj.stocktrade.api.deposit.vo.FundInfoEx;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;
import com.jrj.stocktrade.api.pub.BranchQueryService;
import com.jrj.stocktrade.api.stock.StockAccountQueryService;
import com.jrj.stocktrade.api.stock.StockQueryService;
import com.jrj.stocktrade.api.stock.vo.StockInfo;
import com.opensymphony.oscache.util.StringUtil;
@Controller
@RequestMapping("/stock/{accountId}")
@NeedLogin
@TradeAccount
public class AccountAction extends AbstractStockBaseAction{
	private static final int ps =15;
	@Autowired
	StockQueryService stockQueryService;
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
		
		return "/account/index";
	}
	/**
	 * 当前持仓
	 * @param accountId
	 * @param session
	 * @param model
	 * @return
	 */
	//@GetSysStatus
	@RequestMapping(value="/stockInfoIndex",method=RequestMethod.GET)
	public String stockInfo(
			@PathVariable Long accountId,
			HttpSessionWrapper session, Model model){
		
		String userId=getSelfUserId(session);
		//查询持仓
		List<StockInfo> positions = new ArrayList<StockInfo>();
		try{
			positions = stockQueryService.unsafeSecurityStockFastQuery(userId, accountId , 1, ps);
		}catch(ServiceException e){
			log.error(e.getErrorInfo(), e);
			model.addAttribute("errMsg",StockUtil.getErrorInfo(e.getErrorInfo()));
		}
		model.addAttribute("isfirst", 1);
		model.addAttribute("positions", positions);
		model.addAttribute("pageNum", 1);
		//String viewName="/account/stockInfoIndex";
		//return viewName;
		
		return "/account/stockInfoIndex";
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
		List<StockInfo> positions = new ArrayList<StockInfo>();
		try {
			positions = stockQueryService.unsafeSecurityStockFastQuery(userId, accountId, Integer.parseInt(pageNum), ps);
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
		
	@RequestMapping(value="/noBindIndex",method=RequestMethod.GET)
	public String noBroker(@RequestParam(value = "redirect", defaultValue = "") String redirect,
			HttpSessionWrapper session, 
			HttpServletRequest request,
			Model model,
			RedirectAttributes attributes) throws ServiceException{
		
		//渠道类型，活动过来的，开户页面需要只显示中山,如果是正常渠道过来的显示所有券商
		String khChannel = "";
		String viewName = "/account/noAccount";
		if(session.getAttribute(AttributeKeys.DH_CHANNEL) != null){
			khChannel=(String)session.getAttribute(AttributeKeys.DH_CHANNEL);
		}
		String userId=this.getSelfUserId(session);
		if(userId!=null){
			List<Broker> brokerList = accountService.queryBrokers(userId);
			UserInfoVo user = getSelfUserInfo(session);
			if(user !=null){
				List<StockAccountStatus> statusList = stockAccountStatusService.queryAllStockOpenStatusByUserId(userId);
				StockAccountStatus status = null;
				if(statusList != null && statusList.size()>0){
					status = statusList.get(0);
				}
				//初始化数据-页面上需要的数据
				initNoBrokerDate(status,userId, request, model);
				//根据开户流程状态跳转到不同的页面
				viewName=stepNoBrokerPage(status,userId,model,khChannel);
			}
			model.addAttribute("brokerList", brokerList);
		}
		if(StringUtils.isNotBlank(redirect)){
			model.addAttribute("redirect", redirect);
		}
		//model.addAttribute("Column", "INDEX");
		return viewName;
		
	}
	
	/**
	 * 访问开户流程页面前，初始化数据
	 */
	public void initNoBrokerDate(StockAccountStatus status,String userId,
			HttpServletRequest request,
			Model model){
		model.addAttribute("bindStatus", status);
		if(status!=null){
			log.info("StockAccountStatus: userId=" + userId + ",status=" +  status.getAccountStatus());
			//开户时间
			if(status.getOpenTime() != null){
				model.addAttribute("strOpenTime", StockUtil.formatDate(status.getOpenTime(),StockUtil.format_1));
			}
			//可交易时间
			if(status.getTradeableTime() != null){
				model.addAttribute("tradeableTime", StockUtil.formatDate(status.getTradeableTime(),StockUtil.format_1));
			}
			//券商信息
			if(status.getBrokerId() != null){
				Broker broker=getBroker(status.getBrokerId());
				model.addAttribute("qsNo", broker.getBrokerId());
				model.addAttribute("qsName", broker.getBrokerName());
			}
			if(request.getParameter("refreshFlag")!=null){
				model.addAttribute("refreshFlag", request.getParameter("refreshFlag"));
			}
		}
	}
	
	/**
	 * 根据券商标识对应到具体的券商流程
	 */
	public String stepNoBrokerPage(StockAccountStatus status,String userId,Model model,String khChannel){
		//中信，中山   第一步开户(中信，中山用的一个页面)
		String viewName=null;
		if(status == null) {
			// 未开户
			viewName="/account/step1"+khChannel;
		}else{
			//判断是中信开户
			if(BrokerId.CITIC_SECURITIES.equals(status.getBrokerId())){
				viewName=stepZXZQPage(status,userId,model);
			}else if(BrokerId.ZSZQ_SECURITIES.equals(status.getBrokerId())){
				viewName=stepZSZQPage(status,userId,model,khChannel);
			}else{
				//未查到券商
				viewName="/account/step1"+khChannel;
				log.error("Method --> stepNoBrokerPage 券商无法识 ，券商名称："+status.getBrokerId());
			}
		}
		return viewName;
	}
	
	/**
	 * 中信流程
	 */
	public String stepZXZQPage(StockAccountStatus status,String userId,Model model){
		//中信，中山   第一步开户(中信，中山用的一个页面)
		String viewName=null;
		if(status.getAccountStatus() == AccountStatus.NONE) {
			// 未开户
			viewName="/account/step1";
		}
		else if(status.getAccountStatus() == AccountStatus.ENTER_OPEN) {//中信，中山   第二步，开户中(中信，中山用的一个页面)
			// 开户中
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step2-kaihu";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step2-zhuanhu";
		}else if(status.getAccountStatus() == AccountStatus.SUCCESS) {//中信第三步，中山第四步  审核页面
			// 开户成功（未加白名单）
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step3-khcomplete";
			else if(status.getType() == StockAccountType.ZHUANHU)  viewName="/account/step3-zhcomplete";
		}else if(status.getAccountStatus() == AccountStatus.TRADEABLE) {
			model.addAttribute("SHStockAccount","********");
			model.addAttribute("SZStockAccount","********");
			model.addAttribute("fundId",status.getFundId());
			// 开户已加白名单
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step4-khavailable";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step4-zhavailable";
		}else {//异常状态
			viewName=stepErrorStatus(status,userId,model);
		}
		return viewName;
	}
	
	/**
	 * 中山流程
	 */
    public String stepZSZQPage(StockAccountStatus status,String userId,Model model,String khChannel){
		//中信，中山   第一步开户(中信，中山用的一个页面)
    	String viewName=null;
		if(status.getAccountStatus() == AccountStatus.NONE) {
			// 未开户
			viewName="/account/step1"+khChannel;
		}
		else if(status.getAccountStatus() == AccountStatus.ENTER_OPEN) {//中信，中山   第二步，开户中(中信，中山用的一个页面)
			// 开户中
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step2-kaihu";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step2-zhuanhu";
		}else if(status.getAccountStatus() == AccountStatus.OPEN_COMPLETE){//中山第三步开户完成(中山的一个页面)
			// 开户完成，只有中山有这一步
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step2-kaihu-compile";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step2-zhuanhu-compile";
		}else if(status.getAccountStatus() == AccountStatus.FAIL){//中山第三步开户失败(中山的一个页面)
			// 开户失败，只有中山有这一步
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step3-kaihu-fail";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step3-zhuanhu-fail";
		}else if(status.getAccountStatus() == AccountStatus.TRADEABLE) {
			model.addAttribute("SHStockAccount","********");
			model.addAttribute("SZStockAccount","********");
			model.addAttribute("fundId","********");
			//如果是中山证券需要解析一些参数
			if(BrokerId.ZSZQ_SECURITIES.equals(status.getBrokerId())){//中山证券
				if(status.getShStackAccountStatus() == null){
					model.addAttribute("SHStockAccount","未开通");
				}else{
					model.addAttribute("SHStockAccount","已开通");
				}
				if(status.getSzStackAccountStatus() == null){
					model.addAttribute("SZStockAccount","未开通");
				}else{
					model.addAttribute("SZStockAccount","已开通");
				}
			}
			// 开户已加白名单
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step4-khavailable";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step4-zhavailable";
		}else {//异常状态
			viewName=stepErrorStatus(status,userId,model);
		}
		return viewName;
	}
    
    /**
     * 异常状态
     */
    public String stepErrorStatus(StockAccountStatus status,String userId,Model model){
    	String viewName=null;
    	if(status.getAccountStatus().getValue().startsWith("-")){//状态小于0
			viewName="/account/step1";
		}else{
			// 开户中
			if(status.getType() == StockAccountType.KAIHU) viewName="/account/step2-kaihu";
			else if(status.getType() == StockAccountType.ZHUANHU) viewName="/account/step2-zhuanhu";
		}
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
		String clientName = "****";
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
		String userId = getSelfUserId(session);
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
			if(userInfo != null){
				clientName = userInfo.getRealName();
			}
			
			//股东姓名
			json.put("clientName", clientName);
		} catch (Exception e) {
			log.error("get userinfo error",e);
		}
		FundInfoEx fundAll=null;
		try {
			fundAll = fundService.clientFundAllQuery(userId,curAccountId);
			json.put("fundAll", fundAll);
		} catch (Exception e) {
			log.error("accountId==>"+curAccountId+", exception:",e);
			json.put("errMsg_fund", e.getMessage()==null ? "error" : e.getMessage());
		}
		try{
			//今日涨跌
			BigDecimal todayGenLose = fundService.caculateTodayGenLoseRate(accUserId, curAccountId, fundAll);
			json.put("todayGenLose", todayGenLose);
		}catch(Exception e){
			log.error("accountId==>"+curAccountId+", exception:",e);
			json.put("errMsg_todayGenLose", e.getMessage()==null ? "error" : e.getMessage());
		}
		
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
	public String initQsPwd(
			@RequestParam(value="returnUrl",required=false) String returnUrl,
			Model model
			) throws ServiceException{
		model.addAttribute("returnUrl", returnUrl);
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
			@RequestParam(value="txPassword",required=false)String txPassword,
			@PathVariable Long accountId,
			HttpServletRequest request,
			HttpSessionWrapper session) throws ServiceException{
		JSONObject json =new JSONObject();
		if(StringUtil.isEmpty(password)){
			json.put("errorno", -1);
			json.put("errMsg", "请输入您的交易密码");
			return json.toJSONString();
		}
		String userId=getSelfUserId(session);
		Broker broker=getBroker(accountId);
		if(PasswordShape.isActive(broker)){
			String mcrypt_key=session.getAttribute("mcrypt_key",String.class);
			if(mcrypt_key==null){
				json.put("errorno", -1);
				json.put("errMsg", "会话失效请刷新重试");
				return json.toJSONString();
			}
			password=AESWithJCE.getResult(mcrypt_key,password);
			if(!StringUtils.isEmpty(txPassword)){
				txPassword=AESWithJCE.getResult(mcrypt_key,txPassword);
			}
			collectClientInfo(request, session);
		}
		try {
			authorizationService.authorization(userId, accountId, password,txPassword);
			json.put("errorno", 0);
			
		} catch (ServiceException e) {
			log.error("#authroization error", e);
			json.put("errorno", -1);
			if(e.getErrorNo().equals("-1")){
				json.put("errMsg", "您输入的密码有误，请检查");
			}else{
				json.put("errMsg", e.getErrorInfo());
			}
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value="/pdflist",method=RequestMethod.GET)
	public String cjzqPdfList(){
		return "/trade/pdflist";
	}
}
