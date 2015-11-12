package com.cfo.stock.web.action.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.constant.XJBGlobals;
import com.cfo.common.enums.UserInfoStatus;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.CookieUtils;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.exception.StockServiceException;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.services.SendEmailService;
import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.verification.SafeVerification;
import com.jrj.stocktrade.api.account.AccountService;
//import com.jrj.stocktrade.api.account.vo.UserAuth;
//import com.jrj.stocktrade.api.common.AuthStatus;
//import com.jrj.stocktrade.api.common.AuthType;

/**
 * 用户中心、实名认证相关
 * UserCenterAction
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014年7月20日    		iriyadays     create  
 * ---------------------------------------------------------  
 * </PRE> 
 *
 */
@Controller
@RequestMapping("/stock")

public class UserCenterAction extends AbstractStockBaseAction {
	private static final String forwardUrl = "redirect:/stock/user.jspa";
	private static String IC = "_ic";
	private static String auth_ylb_lock_key="stock_auth_ylb_rules";
	private static String auth_ylb_message="Auth the account of ylb to zqt";
	
	@Autowired
	AccountWebService accountWebService;
	@Autowired
	AccountService accountService;
	@Autowired
	SafeVerification safeVerification;
	@Autowired
	SendEmailService sendEmailService;
	/**
	 * 用户信息首页
	 * @param redirect
	 * @param from
	 * @param ic
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String user(
			@RequestParam(value = "redirect", defaultValue = "") String redirect,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "ic", defaultValue = "") String ic,
			HttpSessionWrapper session,
			HttpServletResponse response,
			Model model,
			RedirectAttributes attributes
			) throws IOException {
		//判断是否是由开户链接过来的-目前只有活动用到
		if(redirect != null && redirect.indexOf("getKhpage")>-1){
		    session.setAttribute(AttributeKeys.DH_CHANNEL,"Zskh");
		}
		// 参数
		if (StringUtils.isNotBlank(redirect)) {
			model.addAttribute("redirect", URLUtils.urlEncode(redirect));
		}
		ic = URLUtils.ParamterFilter(ic, '\0');
		if (StringUtils.isNotBlank(from)) {
			model.addAttribute("from", URLUtils.ParamterFilter(from, '\0'));
		}
		if(StringUtils.isNotBlank(ic)){
			 session.setAttribute(AttributeKeys.INVITE_CODE,ic);
		}
		
		// 获取用户状态
		boolean isLogin = isLogin(session);
		if(isLogin) {
			// 有盈利宝账户
			UserInfoVo vo = getSelfUserInfo(session);
			
			if(vo !=null){
				if(vo.getRealName() == null || vo.getIdNumber() == null) {
					// 去补充真实姓名和身份证
					return "/user/update";
				}
				//response.sendRedirect("/stock/accountIndex.jspa");
				return "redirect:/stock/accountIndex.jspa";
			}else {
				// 所有信息都已完善去账户页
//				String viewName="redirect:/stock/accountIndex.jspa";
//				response.sendRedirect("/stock/accountIndex.jspa");
				return "redirect:/stock/accountIndex.jspa";
			}
		}else {
			// 没有盈利宝账户去注册
			return "/user/regist";
		}
	}
	
	@RequestMapping(value="/userMsgCnt",method=RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String userMsgCnt(HttpSessionWrapper session){
		String ssoId = session.getAttribute(AttributeKeys.JRJ_SSOID, String.class);
		JSONObject json=new JSONObject();
		if(ssoId!=null){
			int count=jrjUserInfoService.getNewMessageCount(ssoId);
			json.put("msgCnt", count);
		}else{
			json.put("msgCnt", 0);
		}
		return json.toJSONString();
	}
	
	/*@RequestMapping(value="/Authylb",method=RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String  Authylb(
			@RequestParam(value = "userInfo", defaultValue = "") String userInfo,
			@RequestParam(value = "validateCode", defaultValue = "") String validateCode,
			@RequestParam(value = "realname", defaultValue = "") String realname,
			@RequestParam(value = "sign", defaultValue = "") String sign,
			HttpSessionWrapper session){
		
		JSONObject json = new JSONObject();
		userInfo = userInfo.trim();
		//pwd = pwd.trim();
		sign = sign.trim();
		userInfo = URLUtils.ParamterFilter(userInfo, '\0');
		//pwd = URLUtils.ParamterFilter(pwd, '\0');
		sign = URLUtils.ParamterFilter(sign, '\0');
		
		String ssoId = session.getAttribute(AttributeKeys.JRJ_SSOID,String.class);		
		if(ssoId ==null || StringUtils.isBlank(ssoId)){
			json.put("error", "_login");
			return json.toJSONString();
		}
		//参数判断是否为空
		if(StringUtils.isBlank(userInfo) || StringUtils.isBlank(validateCode)||StringUtils.isBlank(sign)){
			json.put("error", "信息填写错误");
			return json.toJSONString();
		}
		int type = 1;
		if("s".equals(sign)){
			type =1;
		}
		if("m".equals(sign)){
			type =3;
		}
		validateCode = URLUtils.ParamterFilter(validateCode, '\0');
		//通过身份证号，获取用户手机
		String userId = accountWebService.checkINAjax(userInfo);
		String mobileno="";
		if(StringUtils.isNotEmpty(userId)){
			json = JSONObject.parseObject(userId);							
			int retcode = json.getIntValue("retcode");
			if (retcode != 0) {
				userId=accountWebService.queryUserIdByIDNumber(userInfo);
				if(userId != null){
					String userInfo2=accountWebService.userInfo(userId);
					if(StringUtils.isNotEmpty(userInfo)){
						JSONObject userInfoJson = JSONObject.parseObject(userInfo2);							
						String userinfo = userInfoJson.getString("user");
						JSONObject userInfoJson2 = JSONObject.parseObject(userinfo);
						mobileno = userInfoJson2.getString("mobileno");
						
					}
				}
			}
		}
			//手机验证码验证
			String rs_mc = accountWebService.validMobileCode(mobileno, validateCode);
			if(StringUtils.isNotBlank(rs_mc)){
				json = JSONObject.parseObject(rs_mc);
				if(!"1".equals(json.getString("status"))){
					json.put("error", "验证码错误!");
					return json.toJSONString();
				}
			}
			
		//String rs = accountWebService.login(userInfo, type, pwd);
////		if(rs ==null){
////			json.put("error", "系统错误");
////			return json.toJSONString();
//		}
		//JSONObject js = JSONObject.parseObject(rs);
//		if( !"0".equals(js.getString("retcode"))){
//			json.put("error", js.getString("msg"));
//			return json.toJSONString();
//		}
		//判断该盈利宝帐号是否有授权
		//String userId = js.getString("userid");
		List<UserAuth> list = accountAuthService.getAuthInfo(userId, AuthStatus.OK);
		if(list !=null &&list.size()>0){
			json.put("error", "该账号已授权其他证券通账号");
			return json.toJSONString();
		}
		//没有授权记录就授权s
		
		try {
			//实名认证
			String result = accountWebService.validINRealName(userInfo, realname);
			//如果验证通过
			if(StringUtils.isNotEmpty(result)){
				json = JSONObject.parseObject(result);
				json.put("status", 1);
				//通过盈利宝user_id查询用户信息
				String userinfo = accountWebService.userInfo(userId);
				//判断姓名与已注册的盈利宝姓名是否一致
				if(StringUtils.isNotEmpty(userinfo)){
					//解析盈利宝的用户信息
					json = JSONObject.parseObject(userinfo);
					String ylbUserInfo = json.get("user").toString();
					json = JSONObject.parseObject(ylbUserInfo);
					//获取到盈利宝的用户名
					String ylbUserName = json.get("realname").toString();
					//判断用户名是否相同
					if(!realname.equals(ylbUserName)){
						//修改盈利宝的用户名
						accountWebService.updateUser(userId, realname, userInfo);
						log.info("Method --> Authylb 用户在完善信息的时候发现在盈利宝帐户与认证过的姓名不相同，修改盈利宝userId:"+userId+"盈利宝的姓名："+realname);
					}
				}
			}else{
				json.put("error", "用户名与身份证谁失败");
				return json.toJSONString();
			}
			accountAuthService.authorize(userId, ssoId, AuthType.JRJSSO);
			json.put("success", 1);
			return json.toJSONString();
		} catch (ServiceException e) {
			log.error(e.getErrorInfo()); 
			json.put("error", e.getErrorInfo());
			return json.toJSONString();
		}
				
	}*/
	/**
	 * 用户完善个人信息（盈利宝注册）
	 * @param request
	 * @param mobile
	 * @param captcha
	 * @param validateCode
	 * @param from
	 * @param redirect
	 * @param ic
	 * @param realname
	 * @param idnumber
	 * @param invitetype
	 * @param invite_mobile
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	/*@RequestMapping(value = "/regist", method=RequestMethod.POST)
	public String regist(
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			//@RequestParam(value = "captcha", defaultValue = "") String captcha,
			@RequestParam(value = "validateCode", defaultValue = "") String validateCode,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "redirect", defaultValue = "") String redirect,
			@RequestParam(value = "ic", defaultValue = "") String ic,
			@RequestParam(value = "realname", defaultValue = "") String realname,
			@RequestParam(value = "idnumber", defaultValue = "") String idnumber,
			//@RequestParam(value = "invitetype", defaultValue = "") String invitetype,
			//@RequestParam(value = "invite_mobile", defaultValue = "") String invite_mobile
			HttpSessionWrapper session,
			HttpServletResponse response,
			HttpServletRequest request,
			Model model,
			RedirectAttributes attributes
			) throws UnsupportedEncodingException {
		//判断是否是由开户链接过来的-目前只有活动用到
		if(redirect != null && redirect.indexOf("getKhpage")>-1){
			session.setAttribute(AttributeKeys.DH_CHANNEL,"Zskh");
		}
		// 参数
		ic = URLUtils.ParamterFilter(ic,'\0');
		if(StringUtils.isNotBlank(ic)){
			CookieUtils.updateCookieByName(response,IC,ic);
			session.setAttribute(AttributeKeys.INVITE_CODE, ic);
		}else{
			String inner_ic = CookieUtils.getCookieByName(request,IC);
			if(StringUtils.isNotBlank(inner_ic)){
				inner_ic = URLUtils.ParamterFilter(inner_ic,'\0');
				ic = inner_ic;
				session.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
			}else{
				Object session_ic = session.getAttribute(AttributeKeys.INVITE_CODE);
				if(session_ic!=null){
					ic =  URLUtils.ParamterFilter((String)session_ic,'\0');
					CookieUtils.updateCookieByName(response,IC,ic);
				}
			}
		}
		model.addAttribute("inviteCode", ic);
		
		String ip = ActionUtils.getRemoteIpAdress(request);
		String clientinfo = ActionUtils.getClientInfo(request);
		from = URLUtils.ParamterFilter(from, '\0');
		if (StringUtils.isBlank(from)) {
			from = (String) session.getAttribute(AttributeKeys.AD_FROM);
		}
		
		// 检查参数
		mobile = URLUtils.ParamterFilter(mobile, '\0');
		//captcha = URLUtils.ParamterFilter(captcha, '\0');
		validateCode = URLUtils.ParamterFilter(validateCode, '\0');
		realname = URLUtils.ParamterFilter(realname, '\0');
		idnumber = URLUtils.ParamterFilter(idnumber, '\0');
		//from = URLUtils.ParamterFilter(from, '\0');
		//redirect = URLUtils.ParamterFilter(redirect, '\0');
		//invitetype = URLUtils.ParamterFilter(invitetype, '\0');
		//invite_mobile = URLUtils.ParamterFilter(invite_mobile, '\0');

		if (StringUtils.isEmpty(mobile) 
				//|| StringUtils.isEmpty(captcha)
				|| StringUtils.isEmpty(validateCode)
				|| StringUtils.isEmpty(realname)
				|| StringUtils.isEmpty(idnumber)) {
			return forwardUrl;
		}
		
		// 验证验证码
		boolean flag = ImageCaptchaValidator.validateResponse(request, captcha,
				false);
		if (!flag) {
			model.setViewName(forwardUrl);
			return viewName;
		}
		
		//手机验证码验证
		JSONObject json = new JSONObject();
		String rs_mc = accountWebService.validMobileCode(mobile, validateCode);
		if(StringUtils.isNotBlank(rs_mc)){
			json = JSONObject.parseObject(rs_mc);
		}
		if(!"1".equals(json.getString("status"))){
			String viewName="user/regist";
			model.addAttribute("idNumber", idnumber);
			model.addAttribute("realName", realname);
			model.addAttribute("errorCode", 4);
			return viewName;
		}
		// 验证手机号唯一性
		JSONObject result  = accountWebService.checkMobile(mobile);
		if(result !=null && result.getInteger("retcode")!=0){
			String viewName="user/regist";
			model.addAttribute("idNumber", idnumber);
			model.addAttribute("realName", realname);
			model.addAttribute("errorCode", 1);
			return viewName;
		}
		String rs_idnumber = accountWebService.checkIdNumber(idnumber);
		if (!"ok".equals(rs_idnumber)) {
			String viewName="user/regist";
			model.addAttribute("idNumber", idnumber);
			model.addAttribute("realName", realname);
			model.addAttribute("errorCode", 2);
			return viewName;
		}
		//实名验证
		JSONObject jsonVIR = new JSONObject();
		String rs ="";
		rs = accountWebService.validINRealName(idnumber, realname);	
		if(StringUtils.isNotBlank(rs)){
			jsonVIR = JSONObject.parseObject(rs);
		}
		if(jsonVIR.getIntValue("retcode")!=0){
			String viewName="user/regist";
			model.addAttribute("idNumber", idnumber);
			model.addAttribute("realName", realname);
			model.addAttribute("errorCode", 3);
			return viewName;
		}
		// 注册盈利宝
		String userId = null;
		try {
			//随机生成8位随机数做为盈利宝的密码
			String password = StockUtil.generatePassword(8);
		    Map<String, String> registResult = accountWebService.regist(mobile,
				password, ip, from, validateCode, clientinfo);
			if (StringUtils.isEmpty(registResult.get("userId"))) {
				log.info("### 没有返回: userId: mobile=" + mobile);
				return forwardUrl;
			}
			userId = registResult.get("userId");
			log.info("### 盈利宝注册成功: mobile=" + mobile + ",userId=" + userId);
			
			// 更新用户真实姓名和身份证号码
			String updateUserResult = accountWebService.updateUser(userId, realname, idnumber);	
			log.info("### 更新身份证真实姓名成功: userId:" + userId + ",result" + updateUserResult);
			
			// 盈利宝账号授权JRJ
			String ssoId = session.getAttribute(AttributeKeys.JRJ_SSOID, String.class);
			accountAuthService.authorize(userId, ssoId, AuthType.JRJSSO);
			log.info("### 盈利宝授权JRJ成功: userId=" + userId + ",ssoId=" + ssoId);
			
			// 自动登录
			createSession(from, ic, userId,session,request,response);
			log.info("### 自动登录成功: userId=" + userId);
			
		} catch (StockServiceException e) {
			e.printStackTrace();
			log.equals(e.getMessage());
			return forwardUrl;
		} catch (ServiceException e) {
			log.error(e.getErrorInfo()); 
			log.info("授权JRJ账号失败", e);
		}
		
		// 邀请
		Integer inner_invite_type = 0;
		String inner_ic = CookieUtils.getCookieByName(request,IC);
		Object this_invite_code=null;
		String invite_code = "";
		if(StringUtils.isBlank(inner_ic)){
			this_invite_code = session.getAttribute(AttributeKeys.INVITE_CODE);
			if(this_invite_code!=null){
				invite_code = (String)this_invite_code;	
			}
		}else{
			invite_code = inner_ic;	
		}
		
		if (StringUtils.isBlank(from)) {
			from = (String) session.getAttribute(AttributeKeys.FROM);
		}
		
		
		if(StringUtils.isBlank(invitetype)&&StringUtils.isNotBlank(invite_code)){
			inner_invite_type = 2;
		}else if(StringUtils.isBlank(invitetype)&&StringUtils.isBlank(invite_code)){
			inner_invite_type = 0;
		}else{
			inner_invite_type = Integer.parseInt(invitetype);
			if(StringUtils.isBlank(invite_mobile)){
				inner_invite_type = 0;
				invite_code = "";
			}else{
				invite_code = invite_mobile;
			}
			session.setAttribute(AttributeKeys.INVITE_MOBILE, invite_code);
		}
		accountWebService.inviteme(userId,inner_invite_type,invite_code);
		
		
		session.setAttribute(AttributeKeys.USER_INFO_STATUS, UserInfoStatus.UNBIND.status);
		
		// 完成去账户首页
		redirect  = getLocalReUrl(redirect);
		if(StringUtils.isBlank(redirect)){
			return "redirect:/stock/accountIndex.jspa";
		}else{
			return "redirect:"+redirect;
		}
	}*/

	// 自动登录
	private void createSession(String from, String ic, String userId, HttpSessionWrapper session, 
			HttpServletRequest request,
			HttpServletResponse response) {
		// 植入session（让用户处于登录状态）

		Object flags=session.getAttribute(AttributeKeys.FLAGS);
		String inner_ic = URLUtils.ParamterFilter(CookieUtils.getCookieByName(request,IC),'\0');
		Object invite_code = session.getAttribute(AttributeKeys.INVITE_CODE);
		//session.deleteSession();
		if(from !=null){
			session.setAttribute(AttributeKeys.AD_FROM, from);
		}
		if (flags != null) {
			session.setAttribute(AttributeKeys.FLAGS, flags);
		}
		if(StringUtils.isNotBlank(inner_ic)){
			ic=inner_ic;
			session.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
		}else{
			if(invite_code!=null&&StringUtils.isNotBlank((String)invite_code)){
				CookieUtils.updateCookieByName(response,IC,(String)invite_code);
				ic=(String)invite_code;
			}else{
				ic="";
			}
		}
		session.setAttribute(AttributeKeys.INVITE_CODE, ic);
		session.setAttribute(AttributeKeys.LOGIN,
				XJBGlobals.XJB_LOGIN_OK);
		session.setAttribute(AttributeKeys.USER_ID, userId);
		session.removeAttribute(AttributeKeys.LOGIN_COUNT);

		UserInfoVo user = this.getSelfUserInfo(session);
		// 登录时添加用户信息完整度标记
		if (StringUtils.isBlank(user.getIdNumber())) {
			session.setAttribute(AttributeKeys.USER_INFO_STATUS,
					UserInfoStatus.NEED_ID_INFO.status);
		}
		
	}
	
	/**
	 * 完善个人信息-补充真实姓名和身份证
	 * @param request
	 * @param from
	 * @param redirect
	 * @param ic
	 * @param realname
	 * @param idnumber
	 * @return
	 */
	@NeedLogin
	@RequestMapping(value = "/update", method=RequestMethod.POST)
	public String update(
			HttpServletRequest request,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "redirect", defaultValue = "") String redirect,
			@RequestParam(value = "ic", defaultValue = "") String ic,
			@RequestParam(value = "realname", defaultValue = "") String realname,
			@RequestParam(value = "idnumber", defaultValue = "") String idnumber,
			HttpSessionWrapper session,
			Model model,
			RedirectAttributes attributes) {
		
		// 验证身份证号码唯一性
		String rs_idnumber = accountWebService.checkIdNumber(idnumber);
		if (!"ok".equals(rs_idnumber)) {
			return forwardUrl;
		}
		
		// 更新用户真实姓名和身份证号码
		String userId = getSelfUserId(session);
		accountWebService.updateUser(userId, realname, idnumber, null, null);
		return forwardUrl;
	}
	/**
	 * 
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/validMobile", method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String validMobileUniqueAjax(
			@RequestParam(value = "mobile", defaultValue = "") String mobile) {
		mobile = URLUtils.ParamterFilter(mobile, '\0');
		try{
			String rs = accountWebService.checkMobileAjax(mobile);
			return rs;
		}catch(StockServiceException e){
			log.error(e.getMessage());
			return "";
		}
		
	}
	/** 获取验证码
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/getCode", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String getMobileCode(
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			HttpServletRequest request) {
        //mobile 参数过滤
		mobile = URLUtils.ParamterFilter(mobile, '\0');
		//获取客户端ip
		String ip = ActionUtils.getRemoteIpAdress(request);
		//验证接口防刷stock.phone.rules
		boolean flag = safeVerification.safeValidate("phone:"+mobile+"|ip:"+ip,"stock_phone_rules","完善个人信息发送手机验证码");
		//验证通过为true
		if(flag){
			String rs = accountWebService.getMobileCode(mobile);
			return rs;
		}else{
			return "{\"status\":1 ,\"retcode\":10217,\"resultMsg\":\"不能频繁获取验证码\"}";
		}
	}
	/** 获取邮箱验证码
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/getEmailCode", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String getEmailCode(
			HttpSessionWrapper session, 
			Model model,
			@RequestParam(value = "email", defaultValue = "") String email,
			HttpServletRequest request) {
		JSONObject json = new  JSONObject();
		//获取登录人的用户信息
		String userId = getSelfUserId(session);
        //mobile 参数过滤
		email = URLUtils.ParamterFilter(email, '\0');
		//获取客户端ip
		String ip = ActionUtils.getRemoteIpAdress(request);
		//验证接口防刷stock.phone.rules
		boolean flag = safeVerification.safeValidate("phone:"+email+"|ip:"+ip,"stock_phone_rules","完善个人信息发送邮箱验证码");
		//验证通过为true
		if(flag){
			boolean sendFlag = sendEmailService.sendEmail(userId, email);
			if(sendFlag){
				json.put("status", 1);
				json.put("resultCode", 0);
				json.put("resultMsg", "验证邮件发送成功！");
			}else{
				json.put("status", 1);
				json.put("resultCode", -1);
				json.put("resultMsg", "验证邮件发送失败！");
			}
			return json.toJSONString();
		}else{
			return "{\"status\":1 ,\"retcode\":10217,\"resultMsg\":\"不能频繁获取验证码\"}";
		}
	}
	/**
	 * @param validateCode
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/validCode", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String validMobileCode(
			@RequestParam(value = "validateCode", defaultValue = "") String validateCode,
			@RequestParam(value = "mobile", defaultValue = "") String mobile) {	
		validateCode = URLUtils.ParamterFilter(validateCode, '\0');
		mobile = URLUtils.ParamterFilter(mobile, '\0');
		try{		
			String rs = accountWebService.validMobileCode(mobile, validateCode);
			return rs;
		}catch(StockServiceException e){
			log.error(e.getMessage());
			return "";
		}
		
	}
	/**
	 * 
	 * @param idNumber
	 * @return
	 */
	/*@RequestMapping(value = "/validIN", method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody	
	public String validINUniqueAjax(
			@RequestParam(value = "idNumber", defaultValue = "") String idNumber) {
		idNumber = URLUtils.ParamterFilter(idNumber,'\0');
		JSONObject json  = new JSONObject();
		String rs = "";
		String result = "";
		if(StringUtils.isEmpty(idNumber)){
			json.put("status", -3);
			json.put("msg", "身份证不能为空");
			return json.toJSONString();
		}
//		if(isLogin()){
			try{				
				rs = accountWebService.checkINAjax(idNumber);
				if(StringUtils.isNotEmpty(rs)){
					json = JSONObject.parseObject(rs);							
					int retcode = json.getIntValue("retcode");
					if (retcode != 0) {
						rs=accountWebService.queryUserIdByIDNumber(idNumber);
						if(rs != null){
							String userInfo=accountWebService.userInfo(rs);
							if(StringUtils.isNotEmpty(userInfo)){
								JSONObject userInfoJson = JSONObject.parseObject(userInfo);							
								String userinfo = userInfoJson.getString("user");
								JSONObject userInfoJson2 = JSONObject.parseObject(userinfo);
								String mobileno = userInfoJson2.getString("mobileno");
								json.put("mobileno",mobileno);
							}
							List<UserAuth> list = accountAuthService.getAuthInfo(rs, AuthStatus.OK);
							if(list !=null &&list.size()>0){//已经开通证券通
								json.put("status", -4);
							}else{
								json.put("status", -3);
							}
						}else{
							json.put("status", -3);
						}
						return json.toJSONString();
					}else{
						json.put("status", 1);
					}
				}			
			}catch(Exception e){
				json.put("status", -1);
				log.error("校验身份证有效性失败：", e);
			}
			
//		}else{
//			//没有登录，或者登录超时
//			json.put("status", -2);
//			if(log.isDebugEnabled()){			
//				log.info("ajaxVaildIdNumber--->idNumber:--->"+idNumber+"----realName:---->"+realName);
//			}
//		}		
		return json.toJSONString();
	}*/
	public static String  getLocalReUrl(String url){
		try{
			url =URLDecoder.decode(url);
			url =url.substring(url.indexOf("/stock/")+7);
			return url;
		}catch(Exception e){
			return null;
		}	
	}
}
