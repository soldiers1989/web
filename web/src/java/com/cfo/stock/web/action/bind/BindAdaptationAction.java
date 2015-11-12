package com.cfo.stock.web.action.bind;

import java.util.List;
import java.util.Map;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.platform.UserCenterInterface;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.UserInfoService;
import com.jrj.stocktrade.api.account.vo.BindReq;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.account.vo.UserInfo;
import com.jrj.stocktrade.api.common.AccUserType;
import com.jrj.stocktrade.api.common.BrokerId;
import com.jrj.stocktrade.api.common.BrokerStatus;
import com.jrj.stocktrade.api.common.BrokerType;
import com.jrj.stocktrade.api.common.IdType;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.common.TradeWay;
import com.jrj.stocktrade.api.common.UserStatus;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.itn.ItnAuthService;
import com.jrj.stocktrade.api.itn.vo.ItnBindResp;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;

@Controller
@RequestMapping("/stock")
@NeedLogin
public class BindAdaptationAction extends AbstractStockBaseAction {
	@Autowired
	AccountService accountService;
	@Autowired
	UserCenterInterface userCenterInterface;
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	ItnAuthService itnAuthService;
	@Autowired
	UserInfoService userInfoService;

	@RequestMapping(value = "/initBroker", method = RequestMethod.GET)
	public String initBroker(
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId,
			HttpSessionWrapper session, Model model) {
		brokerId = URLUtils.ParamterFilter(brokerId, '\0');
		String userId = getSelfUserId(session);
		Map<String, String> params = null;
		String url = "";

		// 掉用户中心接口获取用户信息
		UserInfoVo user = getSelfUserInfo(session);
		if (user == null) {
			return "bindBack/fail";
		}
		String idNumber = user.getIdNumber();
		String realName = user.getRealName();
		String returnUrl = Global.BIND_CALLBACK_URL.replaceAll("\\$brokerId",
				brokerId);
		String mobile = user.getMobile();
		log.debug("用户--" + realName + "---绑定券商认证 ---身份证号：" + idNumber);
		try {
			BindReq br = accountService.bindSend(userId, IdType.ID, idNumber,
					realName, returnUrl, brokerId, mobile);
			if (br.getErrorNo() != 0) {
				model.addAttribute("msg", br.getErrorInfo());
				return "bindBack/fail";
			}
			params = br.getParam();
			url = br.getUrl();
		} catch (ServiceException e) {
			log.error(e.getErrorInfo());
			String viewName = "bindBack/fail";
		}
		model.addAttribute("params", params);
		model.addAttribute("url", url);
		String viewName = null;
		if (BrokerId.ZSZQ_SECURITIES.equals(brokerId)) {
			viewName = "bind/bind_zs";
		} else if (BrokerId.CITIC_SECURITIES.equals(brokerId)) {
			viewName = "bind/bind_zx";
		} else if (BrokerId.CNHT_SECURITIES.equals(brokerId)
				|| BrokerId.ZJZQ_SECURITIES.equals(brokerId)) {
			viewName = "bind/bind_ht";
		} else if (BrokerId.CJZQ_SECURITIES.equals(brokerId)) {
			viewName = "bind/bind_cj";
		} else {
			viewName = "bindBack/fail";
		}
		return viewName;
	}

	/**
	 * 查询开户类型 int 或 自有券商
	 * 
	 * @author: jianchao.zhao
	 * @date: 2015年3月24日 下午3:33:17
	 * @mail: jianchao.zhao@jrj.com.cn
	 * @param brokerId
	 * @param session
	 * @return 0:int 1:自有券商 2:已绑定 3:未知错误
	 */
	@RequestMapping(value = "/checkBindType", method = RequestMethod.GET)
	@ResponseBody
	public String checkBindType(
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId,
			HttpSessionWrapper session) {
		brokerId = URLUtils.ParamterFilter(brokerId, '\0');
		String userId = getSelfUserId(session);
		return checkBind(userId, brokerId);
	}

	/**
	 * 绑定ITN
	 * 
	 * @author: jianchao.zhao
	 * @date: 2015年3月27日 下午2:52:26
	 * @mail: jianchao.zhao@jrj.com.cn
	 * @param session
	 * @param request
	 * @param capitalAccount
	 * @param password
	 * @param brokerId
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/ajaxBindITN", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxBindITN(
			HttpSessionWrapper session,
			HttpServletRequest request,
			@RequestParam(value = "capitalAccount", defaultValue = "") String capitalAccount,
			@RequestParam(value = "password", defaultValue = "") String password,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId,
			@RequestParam(value = "txPassword", required = false) String txPassword)
			throws ServiceException {
		JSONObject json = new JSONObject();
		try {
			capitalAccount = URLUtils.ParamterFilter(capitalAccount, '\0');
			brokerId = URLUtils.ParamterFilter(brokerId, '\0');
			if (StringUtils.isEmpty(capitalAccount)) {
				json.put("retcode", "-1");
				json.put("msg", "资金账户为空!");
				return json.toJSONString();
			}
			if (StringUtils.isEmpty(password)) {
				json.put("retcode", "-2");
				json.put("msg", "密码为空！");
				return json.toJSONString();
			}
			String mcrypt_key = session
					.getAttribute("mcrypt_key", String.class);
			if (mcrypt_key == null) {
				json.put("retcode", "-2");
				json.put("msg", "请刷新页面重试！");
				return json.toJSONString();
			}
			password = AESWithJCE.getResult(mcrypt_key, password);
			if (!StringUtils.isEmpty(txPassword)) {
				txPassword = AESWithJCE.getResult(mcrypt_key, txPassword);
			}
			collectClientInfo(request, session);

			if (StringUtils.isEmpty(brokerId)) {
				json.put("retcode", "-3");
				json.put("msg", "券商为空!");
				return json.toJSONString();
			}
			// 获取登录的用户信息
			String userId = getSelfUserId(session);
			// 获取登录信息
			// UserInfoVo userInfoVo = getSelfUserInfo(session);
			// 查询用户是否完善信息
			UserInfo userInfo = userInfoService.queryUserInfo(userId,
					MarketType.getMarketType(1));
			if (userInfo == null
					|| (userInfo != null && userInfo.getStatus() == UserStatus.INCOMPLETE.status)) {
				json.put("retcode", "-4");
				json.put("msg", "个人信息未完善!");
			} else {
				// 查询开户绑户记录
				String bindtype = checkBind(userId, brokerId);
				if ("0".equals(bindtype)) {
					UserInfoVo userInfoVo = getSelfUserInfo(session);
					ItnBindResp itnBindResp = itnAuthService.bindItn(userId,
							userInfoVo != null ? userInfoVo.getRealName() : "",
							userInfoVo != null ? userInfoVo.getMobile() : "",
							capitalAccount, password, brokerId, txPassword);
					if (itnBindResp != null) {
						json.put("retcode", itnBindResp.getErrorNo());
						json.put("msg", itnBindResp.getErrorInfo());
					} else {
						json.put("retcode", "-4");
						json.put("msg", "绑定失败!");
					}
				} else {
					json.put("retcode", "-4");
					json.put("msg", "已绑定或已开户!");
				}
			}
			// 获取客户端ip
			// String ip = ActionUtils.getRemoteIpAdress(request);
		} catch (Exception e) {
			log.error("Method --> ajaxCreateUserInfo error", e);
			// A股与港股区别最后错误提示显示位置
			json.put("retcode", "-4");
			json.put("msg", "绑定失败!");
		}
		return json.toJSONString();
	}

	/**
	 * 刷新绑定状态
	 * 
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/ajaxRefreshBindStatus", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxRefreshStatus(
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId,
			HttpSessionWrapper session) throws ServiceException {

		String userId = getSelfUserId(session);
		JSONObject json = new JSONObject();
		try {
			StockAccountStatus stockAccountStatus = stockAccountStatusService
					.refreshAccountStatus(userId, brokerId);
			if (stockAccountStatus != null) {
				json.put("errorno", "0");
				json.put("errMsg", "成功！");
				json.put("stockAccountStatus", stockAccountStatus);
			}
		} catch (ServiceException e) {
			log.error("ajaxRefreshBindStatus  error", e);
			json.put("errorno", e.getErrorNo());
			json.put("errMsg", e.getErrorInfo());
		}
		return json.toJSONString();
	}

	/**
	 * 返回 0 走ITN绑定 1 走自接绑定
	 * 
	 * @param userId
	 * @param brokerId
	 * @return
	 */
	public String checkBind(String userId, String brokerId) {
		List<StockAccountStatus> stockAccountList;
		List<UserAccAuth> accauthList;
		try {
			// 读取登录用户的开户信息
			stockAccountList = stockAccountStatusService
					.queryAllStockOpenStatusByUserId(userId);
			// 查询登录用户已绑定账户
			accauthList = userAuthService.queryUserAuth(userId,
					AccUserType.OWNER);
		} catch (ServiceException e) {
			// e.printStackTrace();
			log.error("查询用户开户记录失败", e);
			return "3";
		}
		// 判断开户记录
		Broker broker = brokerHelper.getBroker(brokerId);
		// 区分int券商 自接券商
		// String brokerId2;
		// if(brokerId!=null){
		// brokerId2=brokerId.substring(0,4);
		// if("ITN_".equals(brokerId2)){
		// brokerId=brokerId.substring(4);
		// }
		// }
		BrokerType bType = BrokerType.getType(broker.getBrokerType());
		Broker itnBroker = null;
		boolean checkItn=false;
		if (bType == BrokerType.PRIVATE) {
			itnBroker = brokerHelper.getBroker("ITN_" + brokerId);
			if(itnBroker != null
					&& BrokerStatus.USABLE.status == itnBroker.getStatus()){
				checkItn=true;
			}
		}
		// 判断是否绑定过券商账户
		for (UserAccAuth ua : accauthList) {
			if (ua.getBrokerId().equals(brokerId)) {
				return "2";
			}
			if (checkItn&& ua.getBrokerId().equals(itnBroker.getBrokerId())) {
				return "2";
			}
		}

		// 类似于海通类型的券商 开户自接 绑定及交易使用ITN类型 在开户列表点击绑定时走ITN绑定通道
		if (broker != null) {
			if (StringUtils.equals(broker.getTradeWay(),
					TradeWay.ITN.getValue())) {
				return "0";
			}
		}
		for (StockAccountStatus sa : stockAccountList) {
			if (sa.getBrokerId().equals(brokerId)) {
				return "1";
			}
		}
		return "0";
	}

	@RequestMapping(value = "/initBroker1", method = RequestMethod.GET)
	public String initBroker1() {
		return "bindBack/success_zs";
	}
}
