package com.cfo.stock.web.action.account;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.services.account.UserAuthWebService;
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.verification.SafeVerification;
import com.jrj.common.cache.memcached.MemcachedCache;
import com.jrj.stocktrade.api.account.UserAccountService;
import com.jrj.stocktrade.api.account.UserAuthService;
import com.jrj.stocktrade.api.account.UserInfoService;
import com.jrj.stocktrade.api.account.vo.FundAccount;
import com.jrj.stocktrade.api.account.vo.UserAccAuth;
import com.jrj.stocktrade.api.account.vo.UserAccount;
import com.jrj.stocktrade.api.account.vo.UserInfo;
import com.jrj.stocktrade.api.common.AccUserType;
import com.jrj.stocktrade.api.common.AccountAuthStatus;
import com.jrj.stocktrade.api.common.AuthorizeType;
import com.jrj.stocktrade.api.common.MarketType;
import com.jrj.stocktrade.api.common.ProcAuthorizeType;
import com.jrj.stocktrade.api.common.UserStatus;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;
/**
 * 用户授权
 * @author hui.mi
 *
 */
@Controller
@RequestMapping("/stock")
@NeedLogin
public class UserAuthAction extends AbstractStockBaseAction{
	
	@Autowired
	UserAuthService userAuthService;
	@Autowired
	UserAccountService userAccountService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	StockAccountStatusService stockAccountStatusService;
	@Autowired
	UserAuthWebService userAuthWebService;
	@Autowired
	AccountWebService accountWebService;
	@Autowired
	SafeVerification safeVerification;
	@Autowired
	MemcachedCache memcachedCache;
	/**
	 * 添加授权
	 */
	@RequestMapping(value="/ajaxAuthorize",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String authorize(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "realname", defaultValue = "") String realname,
			@RequestParam(value = "idnumber", defaultValue = "") String idnumber,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录人的用户信息
			String userId = getSelfUserId(session);
			//通过姓名和身体证获取到通行证id  查询被绑人的通行证id
			String accUserId = accountWebService.queryUserIdByIDNumber(idnumber);
			long  accountId = 0;
			// 判断要绑定的资金帐户是否开通证券通，即是否完善信息
			// 查询用户是否完善信息
			UserInfo userInfo = userInfoService.queryUserInfo(accUserId);
			if (userInfo == null) {
				json.put("retcode", "-1");
				json.put("msg", "您搜的身份证信息未开通证券通!");
				return json.toJSONString();
			}
			// 判断要绑定的帐户是否绑定券商
			UserAccount userAccount = userAccountService.queryAccount(
					accUserId, brokerId);
			// 判断要绑定的账户是否绑定券商
			if (userAccount == null) {
				json.put("retcode", "-1");
				json.put("msg", "您搜的身份证信息未绑定此券商账户!");
				return json.toJSONString();
			}
			//判断该账户是否授权其它账户
			if(userAccount.getAuthStatus() == AccountAuthStatus.AUTH.getValue()){
				json.put("retcode", "-1");
				json.put("msg", "您搜索的账户已经授权其它账户!");
				return json.toJSONString();
			}
			//判断要绑定的账户是否与登录账户有关系--不能重复添加关系
			/*UserAccAuth userAccAuth = userAuthService.queryUserAuth(
					userId, userAccount.getAccountId());
			// 判断状态是否是自己控制中
			if (userAccAuth != null) {
				if(userAccAuth.getStatus() == AccUserStatus.DOREQUEST.status){
					json.put("retcode", "-1");
					json.put("msg", "不能重复对该帐户发起请求!");
				}else if(userAccAuth.getStatus() == AccUserStatus.CONTROLING.status){
					json.put("retcode", "-1");
					json.put("msg", "您正在控制该账户，无需重新申请!");
				}
			}*/
			accountId = userAccount.getAccountId();
			// 向证券通用户表添加记录 cfo_userinfo
			userAuthService.authorize(userId, accUserId, accountId,
					AuthorizeType.REQUEST);
			json.put("retcode", "0");
			json.put("msg", "已提交绑定申请!");
		}catch(ServiceException e){
			log.error("Method --> ajaxAuthorize error", e);
			json.put("retcode", "-1");
			json.put("msg", e.getErrorInfo());
		}catch(Exception e){
			log.error("Method --> ajaxAuthorize error", e);
			json.put("retcode", "-1");
			json.put("msg", "绑定失败!");
		}
		return json.toJSONString();
	}
	//完善信息
	@RequestMapping(value="/ajaxCreateUserInfo",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String createUserInfo(
			HttpSessionWrapper session, 
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "realname", defaultValue = "") String realname,
			@RequestParam(value = "idnumber", defaultValue = "") String idnumber,
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			@RequestParam(value = "validcode", defaultValue = "") String validcode,
			@RequestParam(value = "retType", defaultValue = "1") String retType,
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "emailcode", defaultValue = "") String emailcode,
			@RequestParam(value = "isValid", defaultValue = "") String isValid,
			@RequestParam(value = "invitecode", defaultValue = "") String invitecode,
			@RequestParam(value = "btype", defaultValue = "") String btype
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			realname = URLUtils.ParamterFilter(realname, '\0');
			idnumber = URLUtils.ParamterFilter(idnumber, '\0');
			mobile = URLUtils.ParamterFilter(mobile, '\0');
			validcode = URLUtils.ParamterFilter(validcode, '\0');
			email = URLUtils.ParamterFilter(email, '\0');
			emailcode = URLUtils.ParamterFilter(emailcode, '\0');
			invitecode = URLUtils.ParamterFilter(invitecode, '\0');
			
			String yqcode = "0";
			
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			//获取登录信息
			UserInfoVo userInfoVo = getSelfUserInfo(session);
			
			// 查询用户是否完善信息
			UserInfo userInfo = userInfoService.queryUserInfo(userId,MarketType.getMarketType(Integer.parseInt(retType)));
			boolean isperfect=false;
			if(btype.equals("0")){//itn
				if(userInfo == null||(userInfo !=null&&userInfo.getStatus()==UserStatus.INCOMPLETE.status)){
					isperfect=true;
				}else {
					isperfect=false;
				}
			}else if(btype.equals("1")){//自有
				if(userInfo == null||(userInfo != null && userInfo.getStatus() != UserStatus.COMPETE.status)){
					isperfect=true;
				}else{
					isperfect=false;
				}
			}
			// A. 未完善
			if (isperfect) {
				// 提交通信证用的字段
				String idnumberVerify, realnameVerify, mobileVerify, emailVerify, invitecodeVerify;
				idnumberVerify = realnameVerify = mobileVerify = emailVerify = invitecodeVerify= null;
				
				/// A.0 防刷
				//获取客户端ip
				String ip = ActionUtils.getRemoteIpAdress(request);
				//防刷限制
				boolean flag = safeVerification.safeValidate("phone:"+mobile+"|ip:"+ip,"stock_phone_rules","完善个人信息验证身份证");
				if(!flag){
					json.put("retcode", "-4");
					json.put("msg", "不能频繁提交，请稍后再试!");
					return json.toJSONString();
				}
				
				if(btype.equals("1")){//非itn的情况
					/// A.1 验证姓名和身份证
					if(userInfoVo == null || 
							StringUtils.isBlank(userInfoVo.getRealName()) || StringUtils.isBlank(userInfoVo.getIdNumber()) ) {
						//验证身份证是否注册
						String isEidnumber = accountWebService.checkINAjax(idnumber);
						if(isEidnumber != null && StringUtils.isNotEmpty(isEidnumber)){
							JSONObject jsons = JSONObject.parseObject(isEidnumber);
							if("1".equals(jsons.getString("resultCode"))){
								json.put("retcode", "-2");
								json.put("msg", "该身份证已注册!");
								return json.toJSONString();
							}
						}
						
						// 验证国证通
						String strInReal = accountWebService.validINRealName(idnumber,realname);
						//判断如果环境是否是测试环境
						if(isTestEnvironment()){
							//测试用可删除
							strInReal = "{\"resultMsg\":\"测试验证通过\",\"resultCode\":\"0\"}";
						}
						//判断返回值是否为空
						if(StringUtils.isEmpty(strInReal)){
							json.put("retcode", "-2");
							json.put("msg", "验证姓名身份证失败!");
							return json.toJSONString();
						}else{
							JSONObject realJson = JSONObject.parseObject(strInReal);
							if(!"0".equals(realJson.getString("resultCode"))){
								json.put("retcode", "-2");
								json.put("msg", realJson.getString("resultMsg"));
								return json.toJSONString();
							}
						}
						
						idnumberVerify = idnumber;
						realnameVerify = realname;
					}
				}
		
				/// A.2 验证手机和邮箱
				if(userInfoVo == null || 
						StringUtils.isBlank(userInfoVo.getMobile()) || !mobile.equals(userInfoVo.getMobile())) {
					//验证手机号是否注册
					String strMobile = accountWebService.checkMobileAjax(mobile);
					if(strMobile != null && StringUtils.isNotEmpty(strMobile)){
						JSONObject jsons = JSONObject.parseObject(strMobile);
						if("1".equals(jsons.getString("resultCode"))){
							json.put("retcode", "-3");
							json.put("msg", "该手机号已注册!");
							return json.toJSONString();
						}
					}
					// 验证手机号验证码
					String strVa = accountWebService.validMobileCode(mobile,validcode);
					//判断如果环境是否是测试环境
					if(isTestEnvironment()){
						//测试用可删除
						strVa = "{\"resultMsg\":\"手机号验证码测试验证通过\",\"resultCode\":\"0\"}";
					}
					if(StringUtils.isEmpty(strVa)){
						json.put("retcode", "-1");
						json.put("msg", "您输入的验证码不正确!");
						return json.toJSONString();
					}else{
						JSONObject upRealJson = JSONObject.parseObject(strVa);
						if(!"0".equals(upRealJson.getString("resultCode"))){
							json.put("retcode", "-1");
							json.put("msg", upRealJson.getString("resultMsg"));
							return json.toJSONString();
						}
					}
					
					mobileVerify = mobile;
				}
				/// A.3 验证邮箱
				if(userInfoVo == null || 
						StringUtils.isBlank(userInfoVo.getEmail()) || !email.equals(userInfoVo.getEmail())) {
					//只有港股验证，A股不验证
					if(retType.equals(String.valueOf(MarketType.HK.type))){
						//港股EMAIL判断和验证
						String serCode = (String)memcachedCache.get(AttributeKeys.STOCK_EMAIL_PREFIX+userId);
						if(serCode == null || !serCode.equals(emailcode)){
							json.put("retcode", "-4");
							json.put("msg", "邮箱验证码不正确");
							log.error("Method -> ajaxCreateUserInfo 邮箱验证码不正确  userId="+userId+",emailcode="+emailcode+",serCode="+serCode);
							return json.toJSONString();
						}
						emailVerify = email;
					}
				}
				
				/// A.4验证是否是A股，并看用户是否添写邀请码
				//  -1:参数为空 -2:无对应邀请码 -3:邀请码不正确 -4:邀请人已经邀请过被邀请人 0:校验失败 1:校验成功)
				if(retType.equals(String.valueOf(MarketType.STOCK.type))){
					//判断邀请码是不为空，调用接口
					if(StringUtils.isNotBlank(invitecode)){
						//检验验证码
						String inviteResult = accountWebService.verifyInvitationCode(userId,invitecode);
						if(StringUtils.isEmpty(inviteResult)){
							json.put("retcode", "-5");
							json.put("msg", "邀请码添加失败!");
							return json.toJSONString();
						}else{
							JSONObject upRealJson = JSONObject.parseObject(inviteResult);
							if(!"1".equals(upRealJson.getString("result")) 
									&& !"-4".equals(upRealJson.getString("result"))){
								json.put("retcode", "-5");
								json.put("msg", upRealJson.getString("resultMsg"));
								return json.toJSONString();
							}else{
								//调用邀请码接口成功，本地cfo_userinfo 表的状态改为已添写邀请
								yqcode = "2";
							}
						}
					}
				}
				
				/// A.5 调用通行证更新接口
				//调用通行证接口更新通行证
				if(idnumberVerify != null || realnameVerify != null || mobileVerify != null || emailVerify != null) {
					String strUpReal = accountWebService.updateUser(userId,idnumberVerify,realnameVerify,mobileVerify,emailVerify);
					if(StringUtils.isEmpty(strUpReal)){
						json.put("retcode", "-1");
						json.put("msg", "更新用户信息失败!");
						return json.toJSONString();
					}else{
						JSONObject upRealJson = JSONObject.parseObject(strUpReal);
						if(!"0".equals(upRealJson.getString("resultCode"))){
							json.put("retcode", "-4");
							json.put("msg", upRealJson.getString("resultMsg"));
							return json.toJSONString();
						}
					}
				}
				
				/// A.6 持久化
				if(userInfo == null){
					if(btype.equals("0")){//itn
						userInfoService.createUserInfo(userId, MarketType.getMarketType(Integer.parseInt(retType)), UserStatus.ITNCOMPETE, yqcode);
					}else{
						userInfoService.createUserInfo(userId, MarketType.getMarketType(Integer.parseInt(retType)), UserStatus.COMPETE, yqcode);
					}
					
				}else{
					if(btype.equals("0")){
						userInfoService.updateUserInfo(userId, MarketType.getMarketType(Integer.parseInt(retType)), UserStatus.ITNCOMPETE, yqcode);
					}else{
						userInfoService.updateUserInfo(userId, MarketType.getMarketType(Integer.parseInt(retType)), UserStatus.COMPETE, yqcode);	
					}
				}
				//完善信息后删除session中用户信息
				session.removeAttribute(AttributeKeys.USER_INFO);
				json.put("retcode", "0");
				json.put("msg", "完善信息成功!");
			// B. 已完善
			} else {
				//A股与港股区别最后错误提示显示位置
				json.put("retcode", retType.equals(String.valueOf(MarketType.STOCK.type))?"-5":"-4");
				json.put("msg", "不能重复完善信息!");
				return json.toJSONString();
			}
			
		}catch(Exception e){
			log.error("Method --> ajaxCreateUserInfo error", e);
			//A股与港股区别最后错误提示显示位置
			json.put("retcode", retType.equals(String.valueOf(MarketType.STOCK.type))?"-5":"-4");
			json.put("msg", "完善信息失败!");
		}
		return json.toJSONString();
	}
	//添加邀请码
	@RequestMapping(value="/ajaxInviteCode",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ajaxInviteCode(
			HttpSessionWrapper session, 
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "retType", defaultValue = "1") String retType,
			@RequestParam(value = "invitecode", defaultValue = "") String invitecode
			) throws ServiceException{
	    JSONObject json = new  JSONObject();
	    //获取登录的用户信息
		String userId = getSelfUserId(session);
	    try{
	    	retType = URLUtils.ParamterFilter(retType, '\0');
            invitecode = URLUtils.ParamterFilter(invitecode, '\0');
			
	    	/// A.4验证是否是A股，并看用户是否添写邀请码
			//  -1:参数为空 -2:无对应邀请码 -3:邀请码不正确 -4:邀请人已经邀请过被邀请人 0:校验失败 1:校验成功)
			if(retType.equals(String.valueOf(MarketType.STOCK.type))){
				//判断邀请码是不为空，调用接口
				if(StringUtils.isNotBlank(invitecode)){
					//检验验证码
					String inviteResult = accountWebService.verifyInvitationCode(userId,invitecode);
					if(StringUtils.isEmpty(inviteResult)){
						json.put("retcode", "-1");
						json.put("msg", "邀请码添加失败!");
						return json.toJSONString();
					}else{
						JSONObject upRealJson = JSONObject.parseObject(inviteResult);
						if(!"1".equals(upRealJson.getString("result")) 
								&& !"-4".equals(upRealJson.getString("result"))){
							log.error("Mehtod -> ajaxInviteCode 调用通行证验证码接口失败,userId="+userId+",inviteResult="+inviteResult);
							json.put("retcode", "-1");
							json.put("msg", upRealJson.getString("resultMsg"));
							return json.toJSONString();
						}else{
							//将cfo_userinfo 状态改为已添邀请码
							userInfoService.updateUserInfoYqcode(userId, MarketType.getMarketType(Integer.parseInt(retType)), "2");
							json.put("retcode", "0");
							json.put("msg", "添加邀请码成功!");
						}
					}
				}
			}else{
				json.put("retcode", "-1");
				json.put("msg", "只有A股添写邀请码");
			}
	    }catch(Exception e){
	    	json.put("retcode", "-1");
			json.put("msg", "邀请码添写失败！");
			log.error("Method -> ajaxInviteCode error userId="+userId,e);
	    }
	    return json.toJSONString();
	}
	
	//完善港澳台信息
	@RequestMapping(value="/ajaxCreateHMTUserInfo",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String createHMTUserInfo(
			HttpSessionWrapper session, 
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "realname", defaultValue = "") String realname,
			@RequestParam(value = "englishname", defaultValue = "") String englishname,
			@RequestParam(value = "idnumber", defaultValue = "") String idnumber,
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			@RequestParam(value = "retType", defaultValue = "1") String retType,
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "emailcode", defaultValue = "") String emailcode,
			@RequestParam(value = "HMTTYPE", defaultValue = "") String HMTTYPE,
			@RequestParam(value = "idtype", defaultValue = "") String idtype
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			realname = URLUtils.ParamterFilter(realname, '\0');
			idnumber = URLUtils.ParamterFilter(idnumber, '\0');
			mobile = URLUtils.ParamterFilter(mobile, '\0');
			englishname = URLUtils.ParamterFilter(englishname, '\0');
			email = URLUtils.ParamterFilter(email, '\0');
			emailcode = URLUtils.ParamterFilter(emailcode, '\0');
			
			String hkIdCard="";//港身份证
			String hkPassport="";//护照
			if(HMTTYPE.equals("HK")){
				if(idtype.equals("IdCard")){
					hkIdCard=idnumber;
				}else{
					hkPassport=idnumber;
				}
			}else{
				hkPassport=idnumber;
			}
			//判断手机号是否需要验证
			String isMobileValid = "false";
			//判断邮箱是否需要验证
			String isEmailValid = "false";
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			//获取登录信息
			UserInfoVo userInfoVo = getSelfUserInfo(session);
			//登录用户真实姓名
			String realName = "";
			//登录用户身体证号
			String idNo = "";
			if(userInfoVo != null){
				if(userInfoVo.getMobile() == null || StringUtils.isEmpty(userInfoVo.getMobile())){
					isMobileValid = "true";
				}else{
					mobile = userInfoVo.getMobile();
				}
				if(email != null && email.equals(userInfoVo.getEmail())){
					email = null; // 不更新
				}else {
					//判断只有港股才进行邮箱验证
					if(retType.equals(String.valueOf(MarketType.HK.type))){
						isEmailValid = "true";
					}
				}
			}else{
				isMobileValid = "true";
			}
			//查询用户是否完善信息
			UserInfo userInfo = userInfoService.queryUserInfo(userId,MarketType.getMarketType(Integer.parseInt(retType)));
			if(userInfo == null || ( userInfo != null && userInfo.getStatus() == UserStatus.INCOMPLETE.status)){
				//获取客户端ip
				String ip = ActionUtils.getRemoteIpAdress(request);
				//防刷限制
				boolean flag = safeVerification.safeValidate("phone:"+mobile+"|ip:"+ip,"stock_phone_rules","完善个人信息验证身份证");
				if(!flag){
					json.put("retcode", "-1");
					json.put("msg", "不能频繁提交，请稍后再试!");
					return json.toJSONString();
				}
				//调用邮箱验证接口
				if("true".equals(isEmailValid)){
					//获取缓存中的验证码
					String serCode = (String)memcachedCache.get(AttributeKeys.STOCK_EMAIL_PREFIX+userId);
					if(serCode == null || !serCode.equals(emailcode)){
						json.put("retcode", "-4");
						json.put("msg", "邮箱验证码不正确");
						return json.toJSONString();
					}
				}
				//调用通行证接口更新通行证
				String strUpReal = "";
				
                strUpReal = accountWebService.updateHkUser(userId,idnumber,realname,englishname,hkIdCard,hkPassport,mobile,email);
				
				if(StringUtils.isEmpty(strUpReal)){
					json.put("retcode", "-1");
					json.put("msg", "更新用户信息失败!");
					return json.toJSONString();
				}else{
					JSONObject upRealJson = JSONObject.parseObject(strUpReal);
					if(!"0".equals(upRealJson.getString("resultCode"))){
						json.put("retcode", "-1");
						json.put("msg", upRealJson.getString("resultMsg"));
						return json.toJSONString();
					}
				}
				//向证券通用户表添加记录  cfo_userinfo
				if(userInfo == null){
					userInfoService.createUserInfo(userId, MarketType.getMarketType(Integer.parseInt(retType)), UserStatus.COMPETE);
				}else{
					userInfoService.updateUserInfo(userId, MarketType.getMarketType(Integer.parseInt(retType)), UserStatus.COMPETE);
				}
				//完善信息后删除session中用户信息
				session.removeAttribute(AttributeKeys.USER_INFO);
				json.put("retcode", "0");
				json.put("msg", "完善信息成功!");
			}else{
				json.put("retcode", "-1");
				json.put("msg", "不能重复完善信息!");
				return json.toJSONString();
			}
		}catch(Exception e){
			log.error("Method --> ajaxCreateHMTUserInfo error", e);
			json.put("retcode", "-1");
			json.put("msg", "完善信息失败!");
		}
		return json.toJSONString();
	}
	
	
	@RequestMapping(value="/ajaxUserInfoIsPerfect",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String userInfoIsPerfect(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "retType", defaultValue = "1") String retType,
			@RequestParam(value="btype",defaultValue="1")String btype
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			boolean isperfect=false;
			//查询用户是否完善信息
			UserInfo userInfo = userInfoService.queryUserInfo(userId,MarketType.getMarketType(Integer.parseInt(retType)));
			
			if(btype.equals("0")){//itn
				if(userInfo == null||(userInfo !=null&&userInfo.getStatus()==UserStatus.INCOMPLETE.status)){
					isperfect=true;
				}else {
					isperfect=false;
				}
			}else if(btype.equals("1")){//自有
				if(userInfo == null||(userInfo != null && userInfo.getStatus() != UserStatus.COMPETE.status)){
					isperfect=true;
				}else{
					isperfect=false;
				}
			}
			if(isperfect){
				json.put("retcode", "0");
				json.put("flag", "1");
				//通过姓名和身体证获取到通行证id  查询被绑人的通行证id
				String userResult = accountWebService.userInfo(userId);
				if(StringUtils.isNotEmpty(userResult)){
					JSONArray jsonArr = JSONArray.parseArray(userResult);
					if(!jsonArr.isEmpty()){
						JSONObject jsons = JSONObject.parseObject(jsonArr.getString(0));
						json.put("trueName", jsons.getString("trueName"));
						json.put("idCard", jsons.getString("idCard"));
						json.put("mobile", jsons.getString("mobile"));
						json.put("email", jsons.getString("email"));
					}
				}
			}else{
				String yqcode = userInfo.getYqcode();
				if("0".equals(yqcode) || "1".equals(yqcode)){
					List<UserAccAuth> accauthList = userAuthService.queryUserAuth(userId,AccUserType.OWNER);
					if(accauthList.size()>0){
						yqcode = "2";
					}
				}
				json.put("yqcode", yqcode);
				json.put("retcode", "0");
				json.put("flag", "2");
			}
		}catch(Exception e){
			log.error("Method --> ajaxUserInfoIsPerfect error", e);
			json.put("retcode", "-1");
			json.put("msg", "查询失败!");
		}
		return json.toJSONString();
	}
	/**
	 * 进入绑定页面
	 * @return
	 */
	@RequestMapping(value = "/bindQs", method = RequestMethod.GET)
	public String bindQs(HttpSessionWrapper session,HttpServletRequest request, Model model,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId
			) {
		try {
			String userId = getSelfUserId(session);
			//获取登录信息
			UserInfoVo userInfoVo = getSelfUserInfo(session);
			//登录用户真实姓名
			String realName = userInfoVo.getRealName();
			//登录用户身体证号
			String idNo = userInfoVo.getIdNumber();
			//读取暖登录用户的开户信息
			StockAccountStatus stockAccountStatus = stockAccountStatusService.queryStockOpenStatus(userId, brokerId);
			model.addAttribute("stockAccountStatusVo", userAuthWebService.getStockAccountStatus(stockAccountStatus));
			model.addAttribute("brokerId", brokerId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Method --> bindQs error", e);
		}
		return "/account/bindQs";

	}
	/**
	 * 通过验证
	 */
	@RequestMapping(value="/ajaxAcceptRequest",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String acceptRequest(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "accountId", defaultValue = "") long accountId
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			//通过验证
			userAuthService.procAuthorize(userId, accountId, ProcAuthorizeType.ACCEPT);
			json.put("retcode", "0");
			json.put("msg", "已通过验证！");
		}catch(Exception e){
			log.error("Method --> ajaxAcceptRequest error", e);
			json.put("retcode", "-1");
			json.put("msg", "验证失败!");
		}
		return json.toJSONString();
	}
	/**
	 * 拒绝
	 */
	@RequestMapping(value="/ajaxRefuseRequest",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String refuseRequest(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "accountId", defaultValue = "") long accountId
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			//通过验证
			userAuthService.procAuthorize(userId, accountId, ProcAuthorizeType.REFUSE);
			json.put("retcode", "0");
			json.put("msg", "已拒绝！");
		}catch(Exception e){
			log.error("Method --> ajaxRefuseRequest error", e);
			json.put("retcode", "-1");
			json.put("msg", "拒绝失败!");
		}
		return json.toJSONString();
	}
	/**
	 * 解除绑定
	 */
	@RequestMapping(value="/ajaxCancelRequest",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String cancelRequest(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "accountId", defaultValue = "") long accountId
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			//通过验证
			userAuthService.procAuthorize(userId, accountId, ProcAuthorizeType.CANCEL);
			json.put("retcode", "0");
			json.put("msg", "成功解除绑定！");
		}catch(Exception e){
			log.error("Method --> ajaxCancelRequest error", e);
			json.put("retcode", "-1");
			json.put("msg", "解除绑定失败!");
		}
		return json.toJSONString();
	}
	/**
	 * 账户列表排序
	 */
	@RequestMapping(value="/ajaxChangeSort",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeSort(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "accountIds", defaultValue = "") List<Long> accountIds
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			//通过验证
			userAuthService.changeSort(userId, accountIds);
			json.put("retcode", "0");
			json.put("msg", "排序成功！");
		}catch(Exception e){
			log.error("Method --> ajaxChangeSort error", e);
			json.put("retcode", "-1");
			json.put("msg", "排序异常!");
		}
		return json.toJSONString();
	}
	//自动加载账户信息
	@RequestMapping(value="/ajaxInitAccountInfo",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String initAccountInfo(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "accountId", defaultValue = "") String accountId
			) throws ServiceException{
		JSONObject json = new  JSONObject();
		try{
			//获取登录的用户信息
			String userId = getSelfUserId(session);
			UserAccAuth userAccAuth = userAuthService.queryUserAuth(userId,Long.parseLong(accountId));
			//查询股东姓名  
			Map<String, String> maps = userAuthWebService.getUserInfo(userAccAuth.getAccUserId());
			if(maps.get("trueName") == null){
				json.put("trueName", "--");
			}else{
				json.put("trueName", maps.get("trueName")+"");
			}
			//查询资金账号  AccountQueryService
			FundAccount fundAccount = userAuthWebService.getFundAccount(userAccAuth);
			if(fundAccount == null){
				json.put("fundAccount", "********");
			}else{
				json.put("fundAccount", fundAccount.getFundAccount());
			}
			//查询股东卡号com.jrj.stocktrade.api.stock.StockAccountQueryService.clientStockAccountQuery(String, long)
			Map<String, String> map = userAuthWebService.getStockAccount(userAccAuth);
			if(map != null){
				if(map.get("shstockAccount") ==null){
					json.put("shstockAccount", "********");
				}else{
					json.put("shstockAccount", map.get("shstockAccount")+"");
				}
				if(map.get("szstockAccount") ==null){
					json.put("szstockAccount", "********");
				}else{
					json.put("szstockAccount", map.get("szstockAccount")+"");
				}
			}
			json.put("retcode", "0");
		}catch(Exception e){
			log.error("Method --> ajaxInitAccountInfo error", e);
			json.put("retcode", "-1");
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value="/ajaxDeleteAuthRecord",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteAuthRecord(
			@RequestParam(value = "accountId", defaultValue = "") long accountId,
			HttpSessionWrapper session
			){
		JSONObject json=new JSONObject();
		String userId = getSelfUserId(session);
		try {
			userAuthService.procAuthorize(userId, accountId, ProcAuthorizeType.DELETE);
			json.put("retcode", "0");
			json.put("msg", "删除成功！");
		} catch (ServiceException e) {
			log.error("删除授权记录失败"+e.getMessage(),e);
			json.put("retcode", "-1");
			json.put("msg", e.getMessage());
		}
		return json.toJSONString();
	}
	public static void main(String[] a){
		String as = "[{\"idCard\":\"130181198211217330\",\"trueName\":\"米辉\",\"passportId\":\"141017010039806379\",\"nickName\":\"zsw\",\"email\":\"110897614@qq.com\",\"passportName\":\"zswtest\",\"mobile\":\"18633914631\"}]";
		JSONArray jsonArr = JSONArray.parseArray(as);
		JSONObject json = JSONObject.parseObject(jsonArr.getString(0));
		System.out.println(json.getString("trueName"));
		System.out.println(json.getString("idCard"));
		System.out.println(json.getString("mobile"));
	}
}
