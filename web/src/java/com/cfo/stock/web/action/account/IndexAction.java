package com.cfo.stock.web.action.account;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.CookieUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.AccountManagerInterceptor.AccountManager;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.services.account.UserAuthWebService;
import com.cfo.stock.web.util.ActionUtils;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.account.UserInfoService;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.account.vo.UserInfo;
import com.jrj.stocktrade.api.common.BrokerStatus;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.common.UserStatus;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;

@Controller
@RequestMapping(value = "/stock")
@NeedLogin
public class IndexAction extends AbstractStockBaseAction {
    
	private final static String ISFIRSTLOGIN_COOKIE = "LOGIN_IN_";
	@Autowired
	UserAuthService userAuthService;
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	UserAuthWebService userAuthWebService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	AccountService accountService;
	
	@RequestMapping(value = "/accountIndex", method = RequestMethod.GET)
	public String accountIndex(RedirectAttributes attributes,HttpSessionWrapper session) throws ServiceException {
		String userId=getSelfUserId(session);
		List<UserAccAuth> userAccAuthlist;
		Long accountId=null;
		try {
			//获取默认账户id
			userAccAuthlist = userAuthService.queryAccessAble(userId);
			if(userAccAuthlist == null || userAccAuthlist.size()==0){
				return "redirect:/stock/accountManager.jspa";
			} 
			else{				
				for (UserAccAuth acc : userAccAuthlist) {
					if (BrokerStatus.USABLE.status == acc.getBstatus()&&acc.getBind().equals("0")){
					accountId = acc.getAccountId();
					MarketType type = acc._getMarketType();
					if (type == MarketType.HK) {
						return "redirect:/stock/hk/" + accountId+ "/stockInfoIndex.jspa";
					} else {
						return "redirect:/stock/" + accountId+ "/stockInfoIndex.jspa";
					}
				}
					
				}
			}return "redirect:/stock/accountManager.jspa";
		} catch (ServiceException e) {
			log.error("exception",e);
			throw e;
		}
	}

	/**
	 * 无账户页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/noAccountIndex", method = RequestMethod.GET)
	public String noAccount(HttpSessionWrapper session,RedirectAttributes attributes) {
		String userId=getSelfUserId(session);
		List<UserAccAuth> userAccAuthlist;
		try {
			//获取默认账户id
			userAccAuthlist = userAuthService.queryUserAuth(userId,true);
			if(userAccAuthlist!=null && userAccAuthlist.size()>0){
				Long accountId=userAccAuthlist.get(0).getAccountId();
				return "redirect:/stock/" + accountId + "/accountIndex.jspa";
			}
		}catch (ServiceException e) {
			log.error("exception",e);
		}
		return "/account/noAccount";
	}

	/**
	 * 进入账户管理页面
	 * @return
	 */
	@AccountManager
	@RequestMapping(value = "/accountManager", method = RequestMethod.GET)
	public String accountManager(HttpSessionWrapper session,HttpServletRequest request,HttpServletResponse response, Model model) {
		try {
			String userId = getSelfUserId(session);
			// 查询用户是否完善信息
			UserInfo userInfo = userInfoService.queryUserInfo(userId);
			//判断是否完善信息
			if (userInfo == null || userInfo.getStatus()==UserStatus.INCOMPLETE.status){
				//判断是否存在cookie
				boolean isCookie = checkAccountAuthCookie(request,userId);
				//如果不存在跳转到无账户页,并存入cookie
				if(!isCookie){
					//存cookie
					String checkUuidCookieKey = ISFIRSTLOGIN_COOKIE + userId + "_first";
					CookieUtils.updateCookieByName(response,checkUuidCookieKey,"ok");
					//跳转到无账户页
					// return "redirect:/stock/noAccountIndex.jspa";
					// 修正往noAccountIndex跳时参数丢失问题
					String queryString = ActionUtils.getQueryString(request);
					return "redirect:/stock/noAccountIndex.jspa" + ( StringUtils.isBlank(queryString) ? "" : ("?" + queryString) );
				}
			}
			//获取登录信息
			UserInfoVo userInfoVo = getSelfUserInfo(session);
			//登录用户真实姓名
			String realName = "";
			//登录用户身体证号
			String idNo = "";
			if(userInfoVo != null){
				//登录用户真实姓名
				if(userInfoVo.getRealName() != null)
					realName = userInfoVo.getRealName();
				//登录用户身体证号
				if(userInfoVo.getIdNumber() != null)
					idNo = userInfoVo.getIdNumber();
			}
			List<Broker> brokerlist= accountService.getAllBrokers();
			//读取登录用户的开户信息
			List<StockAccountStatus> stockAccountList = stockAccountStatusService.queryAllStockOpenStatusByUserId(userId);
			//查询登录用户的所有授权信息
			List<UserAccAuth> accauthList = userAuthService.queryUserAuth(userId);
			model.addAttribute("stockAccountList", userAuthWebService.doStockAccountStatus(stockAccountList,accauthList));
			model.addAttribute("accauthList", userAuthWebService.doUserAccAuthDate(accauthList));
			model.addAttribute("brokerlist",brokerlist);
			//券商列表json
			 JSONObject json = new JSONObject();
			 json.put("brokerlist", brokerlist);
			 model.addAttribute("jsonbrokerlist", json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Method --> accountManager error",e);
		}
		return "/account/accountManager_new";

	}
	/**
	 * 检查用户cookie中是否有权限控制某账户标识
	 * @param req
	 * @param userId
	 * @param accountId
	 * @return
	 */
	private boolean checkAccountAuthCookie(HttpServletRequest req, String userId) {
		String zqtuuidMd5 = null;
		String checkUuidCookieKey = ISFIRSTLOGIN_COOKIE + userId + "_first";
		if(req.getCookies() != null){
			for (Cookie cookie : req.getCookies()) {
				if (checkUuidCookieKey.equals(cookie.getName())) {
					zqtuuidMd5 = cookie.getValue();
				}
			}
			if (zqtuuidMd5 != null) {
				return true;
			}
		}
		return false;
	}
}
