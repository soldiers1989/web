package com.cfo.stock.web.action.regist;

/* 调整注册流程，将证券通注册和盈利宝注册分开. 2014-7-20 */
// @Deprecated

//@Controller
//@RequestMapping("/stock")
//public class RegistAction extends AbstractStockBaseAction {
//	private static final String forwardUrl = "redirect:/stock/registStep1.jspa";
//	private static String IC = "_ic";
//	@Autowired
//	LoginOutService loginOutService;
//	@Autowired
//	AccountWebService accountWebService;
//	@Autowired
//	AccountService accountService;
//
//	@RequestMapping(value = "/registStep1", method = RequestMethod.GET)
//	public String stepOne(
//			@RequestParam(value = "redirect", defaultValue = "") String redirect,
//			@RequestParam(value = "from", defaultValue = "") String from,
//			@RequestParam(value = "ic", defaultValue = "") String ic) {
//		ModelAndView model = new ModelAndView();		
//		//判断当前用户是否处于登陆状态  如果处于登陆状态 直接进入账户首页
//		boolean isLogin = isLogin();
//		if(isLogin){
//			String viewName="redirect:/stock/accountIndex.jspa";
//		}else{
//			//判断回调地址是否为空，回调地址是外部链接过来购买
//			if (StringUtils.isNotBlank(redirect)) {
//				model.addAttribute("redirect", URLUtils.urlEncode(redirect));
//			}
//			ic = URLUtils.ParamterFilter(ic, '\0');
//			if (StringUtils.isNotBlank(from)) {
//				model.addAttribute("from", URLUtils.ParamterFilter(from, '\0'));
//			}
//			if(StringUtils.isNotBlank(ic)){
//				 session.setAttribute(AttributeKeys.INVITE_CODE,ic);
//			}
//			String viewName="/regist/step1";
//		}	
//		return viewName;
//	}
//	/* 注册第二步 用户填写手机号码 密码注册 成功进行登录操作(写入session) 
//	 * 并跳转到 填写用户真实姓名 身份证号码填写页面 
//	*/
//	@RequestMapping(value = "/registStep2")
//	public String stepTwo(
//			HttpServletRequest request,
//			@RequestParam(value = "mobile", defaultValue = "") String mobile,
//			@RequestParam(value = "captcha", defaultValue = "") String captcha,
//			@RequestParam(value = "validateCode", defaultValue = "") String validateCode,
//			@RequestParam(value = "password", defaultValue = "") String password,
//			@RequestParam(value = "repassword", defaultValue = "") String repassword,
//			@RequestParam(value = "from", defaultValue = "") String from,
//			@RequestParam(value = "redirect", defaultValue = "") String redirect,
//			@RequestParam(value = "ic", defaultValue = "") String ic) {
//		ModelAndView model = new ModelAndView();
//		// 判断回调地址是否为空，回调地址是外部链接过来购买
//		if (StringUtils.isNotBlank(redirect)) {
//			model.addAttribute("redirect", URLUtils.urlEncode(redirect));
//		}
//
//		if("GET".equals(request.getMethod())&&!isLogin()){
//			model.clear();
//			String viewName="redirect:/user/login.jspa";
//			return viewName;
//		}
//		ic = URLUtils.ParamterFilter(ic,'\0');
//		if(StringUtils.isNotBlank(ic)){
//			CookieUtils.updateCookieByName(response,IC,ic);
//			session.setAttribute(AttributeKeys.INVITE_CODE, ic);
//		}else{
//			String inner_ic = CookieUtils.getCookieByName(request,IC);
//			if(StringUtils.isNotBlank(inner_ic)){
//				inner_ic = URLUtils.ParamterFilter(inner_ic,'\0');
//				ic = inner_ic;
//				session.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
//			}else{
//				Object session_ic = session.getAttribute(AttributeKeys.INVITE_CODE);
//				if(session_ic!=null){
//					ic =  URLUtils.ParamterFilter((String)session_ic,'\0');
//					CookieUtils.updateCookieByName(response,IC,ic);
//				}
//			}
//		}
//		model.addAttribute("inviteCode", ic);
//		if("POST".equals(request.getMethod())){
//			
//		
//		mobile = URLUtils.ParamterFilter(mobile, '\0');
//		captcha = URLUtils.ParamterFilter(captcha, '\0');
//		validateCode = URLUtils.ParamterFilter(validateCode, '\0');
//		password = URLUtils.ParamterFilter(password, '\0');
//		repassword = URLUtils.ParamterFilter(repassword, '\0');
//
//		if (StringUtils.isEmpty(mobile) 
//				|| StringUtils.isEmpty(captcha)
//				|| StringUtils.isEmpty(validateCode)
//				|| StringUtils.isEmpty(password)
//				|| StringUtils.isEmpty(repassword)
//				|| !password.equals(repassword)) {
//			model.setViewName(forwardUrl);
//			return viewName;
//		}
//		// 验证验证码
//		boolean flag = ImageCaptchaValidator.validateResponse(request, captcha,
//				false);
//		if (!flag) {
//			model.setViewName(forwardUrl);
//			return viewName;
//		}
//		
//		// 验证手机号唯一性
//		JSONObject result  = accountWebService.checkMobile(mobile);
//		if(result != null){
//			if(result.getInteger("retcode")!=0){
//				model.setViewName(forwardUrl);
//				return viewName;
//			}
//		}
//		
//		String ip = ActionUtils.getRemoteIpAdress(request);
//		String clientinfo = ActionUtils.getClientInfo(request);
//		from = URLUtils.ParamterFilter(from, '\0');
//		if (StringUtils.isBlank(from)) {
//			from = (String) session.getAttribute(AttributeKeys.FROM);
//		}
//		// 提交表单
//		// Map<String ,String> rs_regist = accountWebService.regist(mobile,password, validateCode);
//		try {
//		Map<String, String> registResult = accountWebService.regist(mobile,
//				password, ip, from, validateCode, clientinfo);
//		if (StringUtils.isEmpty(registResult.get("userId"))) {
//			model.setViewName(forwardUrl);
//			return viewName;
//		}
//		String userId = registResult.get("userId");
//		
//		// 植入session（让用户处于登录状态）
//
//		Object flags=session.getAttribute(AttributeKeys.FLAGS);
//		String inner_ic = URLUtils.ParamterFilter(CookieUtils.getCookieByName(request,IC),'\0');
//		Object invite_code = session.getAttribute(AttributeKeys.INVITE_CODE);
//		session.deleteSession();
//		if(from !=null){
//			session.setAttribute(AttributeKeys.AD_FROM, from);
//		}
//		if (flags != null) {
//			session.setAttribute(AttributeKeys.FLAGS, flags);
//		}
//		if(StringUtils.isNotBlank(inner_ic)){
//			ic=inner_ic;
//			session.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
//		}else{
//			if(invite_code!=null&&StringUtils.isNotBlank((String)invite_code)){
//				CookieUtils.updateCookieByName(response,IC,(String)invite_code);
//				ic=(String)invite_code;
//			}else{
//				ic="";
//			}
//		}
//		session.setAttribute(AttributeKeys.INVITE_CODE, ic);
//		session.setAttribute(AttributeKeys.LOGIN,
//				XJBGlobals.XJB_LOGIN_OK);
//		session.setAttribute(AttributeKeys.USER_ID, userId);
//		session.removeAttribute(AttributeKeys.LOGIN_COUNT);
//
//		UserInfoVo user = this.getSelfUserInfo(session);
//		// 登录时添加用户信息完整度标记
//		if (StringUtils.isBlank(user.getIdNumber())) {
//			session.setAttribute(AttributeKeys.USER_INFO_STATUS,
//					UserInfoStatus.NEED_ID_INFO.status);
//		}
//			String viewName="/regist/step12";
//			return viewName;
//		} catch (StockServiceException e) {
//			e.printStackTrace();
//			log.equals(e.getMessage());
//			// model = new ModelAndView("redirect : /stock/bindStep1.jspa");
//			String viewName="redirect : /stock/bindStep1.jspa";
//			return viewName;
//		}
//	  }
//		UserInfoVo currentUser = getSelfUserInfo(session);
//		if(currentUser.getIdNumber()!=null){
//			String viewName="redirect:/stock/accountIndex.jspa";
//			return viewName;
//		}
//		String viewName="/regist/step12";
//		return viewName;
//	}
//	/*
//	 * 填写用户真实姓名 身份证号码 先判断当前用户信息是否完整
//	*/
//	@RequestMapping(value = "/registStep22", method = RequestMethod.POST)
//	public String stepTwoTwo(
//			HttpServletRequest request,
//			@RequestParam(value = "realname", defaultValue = "") String realname,
//			@RequestParam(value = "idnumber", defaultValue = "") String idnumber,
//			@RequestParam(value = "from", defaultValue = "") String from,
//			@RequestParam(value = "invitetype", defaultValue = "") String invitetype,
//			@RequestParam(value = "invite_mobile", defaultValue = "") String invite_mobile,
//			@RequestParam(value = "redirect", defaultValue = "") String redirect) {
////		过滤参数
//		realname = URLUtils.ParamterFilter(realname, '\0');
//		idnumber = URLUtils.ParamterFilter(idnumber, '\0');
//		from = URLUtils.ParamterFilter(from, '\0');
//		redirect = URLUtils.ParamterFilter(redirect, '\0');
//		invitetype = URLUtils.ParamterFilter(invitetype, '\0');
//		invite_mobile = URLUtils.ParamterFilter(invite_mobile, '\0');
//		
//		ModelAndView model = new ModelAndView();
////		String status= (String)session.getAttribute(AttributeKeys.USER_INFO_STATUS);
///*		如果信息注册完整 不能继续向下注册
//		if(UserInfoStatus.FULL.status.equals(status)){
//			跳转到登陆页
//			String viewName="/login";
//		}*/
//		// 验证身份证号唯一性
//		String rs_idnumber = accountWebService.checkIdNumber(idnumber);
//		if (!"ok".equals(rs_idnumber)) {
//			model.setViewName(forwardUrl);
//			return viewName;
//		}
//		Integer inner_invite_type = 0;
//		String inner_ic = CookieUtils.getCookieByName(request,IC);
//		Object this_invite_code=null;
//		String invite_code = "";
//		if(StringUtils.isBlank(inner_ic)){
//			this_invite_code = session.getAttribute(AttributeKeys.INVITE_CODE);
//			if(this_invite_code!=null){
//				invite_code = (String)this_invite_code;	
//			}
//		}else{
//			invite_code = inner_ic;	
//		}
//		
//		if (StringUtils.isBlank(from)) {
//			from = (String) session.getAttribute(AttributeKeys.FROM);
//		}
//		String userId = getSelfUserId(session);
//		
//		if(StringUtils.isBlank(invitetype)&&StringUtils.isNotBlank(invite_code)){
//			inner_invite_type = 2;
//		}else if(StringUtils.isBlank(invitetype)&&StringUtils.isBlank(invite_code)){
//			inner_invite_type = 0;
//		}else{
//			inner_invite_type = Integer.parseInt(invitetype);
//			if(StringUtils.isBlank(invite_mobile)){
//				inner_invite_type = 0;
//				invite_code = "";
//			}else{
//				invite_code = invite_mobile;
//			}
//			session.setAttribute(AttributeKeys.INVITE_MOBILE, invite_code);
//		}
//		
//		// 更新用户真实姓名和身份证号码
//		accountWebService.updateUser(userId, realname, idnumber);		
//		accountWebService.inviteme(userId,inner_invite_type,invite_code);
//		
//		session.setAttribute(AttributeKeys.USER_INFO_STATUS, UserInfoStatus.UNBIND.status);
//		// 植入session（让用户处于登录状态）
//		Object flags = session.getAttribute(AttributeKeys.FLAGS);
//
//		if (flags != null) {
//			session.setAttribute(AttributeKeys.FLAGS, flags);
//		}
//		session.setAttribute(AttributeKeys.LOGIN, XJBGlobals.XJB_LOGIN_OK);
//		session.setAttribute(AttributeKeys.USER_ID, userId);
//		String viewName="redirect:/stock/getBrokerList.jspa";
//		return viewName;
//	}
//	@RequestMapping(value="/getBrokerList",method=RequestMethod.GET)
//	public String getBrokerList(){
//		// 获取证券公司列表
//		this.
//		String userId = getSelfUserId(session);
//		try {
//			List<Broker> brokerList = accountService.queryBrokers(userId);
//			String viewName="/regist/step2";
//			model.addAttribute("brokerList", brokerList);
//			return viewName;
//		} catch (ServiceException e) {
//			e.printStackTrace();
//			String viewName="redirect : /stock/bindStep1.jspa";
//			return viewName;
//		}
//	}
//	@RequestMapping(value="/rbindRS",method=RequestMethod.GET)
//	public String getBindRS(@RequestParam(value="brokerId" ,defaultValue="")String brokerId){
//		
//		//get loginuser form session
//		String userId=this.getSelfUserId(session);
//		if("".equals(brokerId)){
//			String viewName="regist/bindRS_F";
//			return viewName;
//		}
//		SecuritiesBroker sb = SecuritiesBroker.getBroker(brokerId);
//		try {
//			BindInfo  info = accountService.getBindInfo(userId, sb);
//			if(info !=null){
//				String viewName="regist/bindRS_S";
//			}else{
//				String viewName="regist/bindRS_F";
//			}
//		} catch (ServiceException e) {
//			String viewName="regist/bindRS_S";
//			model.addAttribute("errMsg", e.getErrorInfo());
//		}	
//		return viewName;
//	}
//	
//	/** 获取验证码
//	 * @param mobile
//	 * @return
//	 */
//	@RequestMapping(value = "/getCode", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
//	@ResponseBody
//	public String getMobileCode(
//			@RequestParam(value = "mobile", defaultValue = "") String mobile) {
////		mobile 参数过滤
//		mobile = URLUtils.ParamterFilter(mobile, '\0');
//		String rs = accountWebService.getMobileCode(mobile);
////		String test = "{\"status\":1 ,\"retcode\":0}";
//		return rs;
//	}
//	
//	/**
//	 * @param validateCode
//	 * @param mobile
//	 * @return
//	 */
//	@RequestMapping(value = "/validCode", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
//	@ResponseBody
//	public String validMobileCode(
//			@RequestParam(value = "validateCode", defaultValue = "") String validateCode,
//			@RequestParam(value = "mobile", defaultValue = "") String mobile) {	
//		validateCode = URLUtils.ParamterFilter(validateCode, '\0');
//		mobile = URLUtils.ParamterFilter(mobile, '\0');
//		try{		
//			String rs = accountWebService.validMobileCode(mobile, validateCode);
//			return rs;
//		}catch(StockServiceException e){
//			log.error(e.getMessage());
//			return "";
//		}
//		
//	}
//
//	/**
//	 * 
//	 * @param mobile
//	 * @return
//	 */
//	@RequestMapping(value = "/validMobile", method = RequestMethod.POST,
//			produces = {"application/json;charset=UTF-8"})
//	@ResponseBody
//	public String validMobileUniqueAjax(
//			@RequestParam(value = "mobile", defaultValue = "") String mobile) {
//		mobile = URLUtils.ParamterFilter(mobile, '\0');
//		try{
//			String rs = accountWebService.checkMobileAjax(mobile);
//			return rs;
//		}catch(StockServiceException e){
//			log.error(e.getMessage());
//			return "";
//		}
//		
//	}
//
//	/**
//	 * 
//	 * @param idNumber
//	 * @return
//	 */
//	@RequestMapping(value = "/validIN", method = RequestMethod.POST,
//			produces = {"application/json;charset=UTF-8"})
//	@ResponseBody	
//	public String validINUniqueAjax(
//			@RequestParam(value = "idNumber", defaultValue = "") String idNumber,
//			@RequestParam(value = "realName", defaultValue = "") String realName) {
//		idNumber = URLUtils.ParamterFilter(idNumber,'\0');
//		realName = URLUtils.ParamterFilter(realName,'\0');
//		JSONObject json  = new JSONObject();
//		String rs = "";
//		String result = "";
//		if(StringUtils.isEmpty(realName)||StringUtils.isEmpty(idNumber)){
//			return rs;
//		}
//		if(isLogin()){
//			try{				
//				rs = accountWebService.checkINAjax(idNumber);
//				if(StringUtils.isNotEmpty(rs)){
//					json = JSONObject.parseObject(rs);							
//					int retcode = json.getIntValue("retcode");
//					if (retcode != 0) {
//						json.put("status", -3);
//						return json.toJSONString();
//					}
//				}			
//				result = accountWebService.validINRealName(idNumber, realName);
//				if(StringUtils.isNotEmpty(result)){
//					json = JSONObject.parseObject(result);
//					json.put("status", 1);
//				}
//			}catch(Exception e){
//				json.put("status", -1);
//				log.error("校验身份证有效性失败：", e);
//			}
//			
//		}else{
//			//没有登录，或者登录超时
//			json.put("status", -2);
//			if(log.isDebugEnabled()){			
//				log.info("ajaxVaildIdNumber--->idNumber:--->"+idNumber+"----realName:---->"+realName);
//			}
//		}		
//		return json.toJSONString();
//	}
//
//	@RequestMapping(value = "/validCaptcha", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
//	@ResponseBody
//	public String excute(
//			@RequestParam(value = "captchaCode", defaultValue = "") String captchaCode) {
//		captchaCode = URLUtils.ParamterFilter(captchaCode, '\0');
//		boolean flag = false;
//		JSONObject json = new JSONObject();
//		flag = ImageCaptchaValidator.validateResponse(request, captchaCode,
//				true);
//		if (flag) {
//			json.put("status", 1);
//		} else {
//			json.put("status", 0);
//		}
//		return json.toJSONString();
//	}
//}
