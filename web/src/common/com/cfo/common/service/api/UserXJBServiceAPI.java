/**
 * 
 */
package com.cfo.common.service.api;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cfo.common.constant.XJBGlobals;
import com.cfo.common.service.api.param.UserUniqueParam;
import com.cfo.common.service.api.param.ValidIdNumberParam;

/**
 * 用户和验证码等相关接口
 * 
 * @author coldwater
 * 
 */
@Service("userXJBApi")
public class UserXJBServiceAPI extends XJBServiceAPI {
	// 用户登录
	private final static String USER_LOGIN_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/login";
	
	// 用户注册
	private final static String USER_RSGIST_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/reg";
	
	// 用户信息
	private final static String USER_INFO_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/info/$userid";
	
	// 发送修改密码验证码
	private final static String GET_MODIFY_PASSWORD_CODE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/modifypwd/sendvalidcode";
	
	// 验证修改密码验证码
	private final static String CHECK_MODIFY_PASSWORD_CODE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/modifypwd/validcode";
	
	// 修改密码
	private final static String MODIFY_PASSWORD_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/modifypwd/modify";
	
	// 找回密码
	private final static String FIND_PASSWORD_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpwd";
	
	// 手机获取修改密码验证码
	private final static String GETVALIDCODE_BYMOBILE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpasswd/getvalidcode_bymobile";
	// 验证手机修改密码验证码
	private final static String VALIDCODE_BYMOBILE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpasswd/validcode_bymobile";
	// 通过手机修改密码
	private final static String MODIFY_BYMOBILE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpasswd/modify_bymobile";

	// 发送找回密码激活邮件
	private final static String SEND_ACTIVEMAIL_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpasswd/sendactivemail";

	// 验证邮箱修改密码验证码
	private final static String ACTIVEMAIL_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpasswd/activemail";

	// 通过邮箱修改密码
	private final static String MODIFY_BYEMAIL_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/findpasswd/modify_byemail";

	// 发送绑定手机验证码
	private final static String GET_MOBILE_BIND_CODE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/bandmobile/sendvalidcode";
	
	// 验证绑定手机验证码
	private final static String CHECK_MOBILE_PASSWORD_CODE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/bandmobile/validcode";
	
	// 绑定手机
	private final static String BIND_MOBILE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/bandmobile/bind";

	// 发送绑定激活邮件
	private final static String BIND_EAMIL_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/bandemail/send";
	
	// 激活绑定新邮箱
	private final static String ACTIVE_EMAIL_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/bandemail/active";
	
	// 身份证唯一性
	private final static String IDNUMBER_UNIQUE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/valid_idnumber";
	//绑卡时验证身份证有效性
	private final static String BINDVALID_IDNUMBER_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/bindvalid_idnumber";

	// 手机唯一性
	private final static String MOBILE_UNIQUE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/valid_mobile";

	//手机/用户id获取用户注册校验信息
	private final static String REGIST_QUERY=XJBGlobals.XJB_REST_PROVIDER_SERVER
			+"/user/reg_query";
	
	// 用户名唯一性
	private final static String USERNAME_UNIQUE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/valid_username";

	// 邮箱唯一性
	private final static String EMAIL_UNIQUE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/valid_email";
	
	// 修改用户信息
	private final static String MODIFY_USER_INFO_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/update";
	//更新用户名和省份证
	private final static String UPDATE_NAME_ID =XJBGlobals.XJB_REST_PROVIDER_SERVER
			+"/user/updatename";
	// 获取验证码
	private final static String GET_IDENTIFYING_CODE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/sms/getcode";

	// 验证验证码
	private final static String CHECK_IDENTIFYING_CODE_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/sms/valid";
	
	// 身份证有效性
	private final static String IDNUMBER_VALID_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/idnumber/valid";

	// 无密码获取用户手机、邮箱
	private final static String QUERY_USER_INFO_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/queryinfo";
	
	// 用户密码校验
	private final static String  CHECK_PASSWORD = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+ "/user/valid_passwd";
	
	// 用户提醒列表
	private final static String NOTIFY_LIST_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
				+ "/user/notifylist";
	
	// 设置用户提醒
	private final static String UPDATE_NOTIFY_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
					+ "/user/updatenotify";
		
	//获取用户提醒设置
	private final static String NOTIFY_SETTING_URI = XJBGlobals.XJB_REST_PROVIDER_SERVER
					+ "/user/notifysetting/%s";

	//用户注册V2
	private static final String USER_RSGIST_URI_V2 = XJBGlobals.XJB_REST_PROVIDER_SERVER
			+"/user/reg_v2";

	//填写用户姓名、身份证
	private static final String UPDATE_NAME= XJBGlobals.XJB_REST_PROVIDER_SERVER
			+"/user/updatename";
	
	//我的邀请列表
		private static final String MYINVITE_LIST= XJBGlobals.XJB_REST_PROVIDER_SERVER
				+"/myfriend/myinvitelist/$userid";
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public String userInfo(String userId){
		String uri = USER_INFO_URI.replaceAll("\\$userid", userId);
		return _get(uri);
	}
	
	/**
	 * 无密码获取用户手机、邮箱
	 * @param loginname 登录名
	 * @return
	 */
	public String queryUserInfo(String loginname, int nametype){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("loginname", loginname);
		map.put("nametype", nametype);
		return _post(QUERY_USER_INFO_URI, map);
	}
	
	
	/**
	 * 发送修改密码验证码
	 * @param userId
	 * @return
	 */
	public String getModifyIdentifyingCode(String userId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userid", userId);
		return _post(GET_MODIFY_PASSWORD_CODE_URI, map);
	}
	
	/**
	 * 验证修改密码验证码
	 * @param userId
	 * @param validCode
	 * @return
	 */
	public String checkModifyIdentifyingCode(String userId, String validCode){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userid", userId);
		map.put("validcode", validCode);
		return _post(CHECK_MODIFY_PASSWORD_CODE_URI, map);
	}
	
	
	/**
	 * 身份证唯一性
	 * 
	 * @param idNumber
	 * @return
	 */
	public String idNumberUnique(String idNumber) {
		return _post(IDNUMBER_UNIQUE_URI, 
				new UserUniqueParam(null, null, null, idNumber));
	}

	/**
	 * 手机号唯一性
	 * 
	 * @param mobile
	 * @return
	 */
	public String mobileUnique(String mobile) {
		return _post(MOBILE_UNIQUE_URI, 
				new UserUniqueParam(null, null, mobile, null));
	}

	/**
	 * 用户名唯一性
	 * 
	 * @param userName
	 * @return
	 */
	public String userNameUnique(String userName) {
		return _post(USERNAME_UNIQUE_URI, 
				new UserUniqueParam(null, userName, null, null));
	}

	/**
	 * 邮箱唯一性
	 * 
	 * @param email
	 * @return
	 */
	public String emailUnique(String email) {
		return _post(EMAIL_UNIQUE_URI, 
				new UserUniqueParam(email, null, null, null));
	}
	
	
	/**
	 * 身份证有效性
	 * 
	 * @param realName
	 * @param idNumber
	 * @return
	 */
	public String idNumberValid(String userid, String idNumber, String realName) {
		return _post(BINDVALID_IDNUMBER_URI, 
				new ValidIdNumberParam(userid,idNumber, realName));
	}
	
	/**
	 * 用户密码校验
	 * @param userId
	 * @param password
	 * @return
	 */
	public String checkPassword(String userId, String password){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userId);
		map.put("passwd", password);
		return _post(CHECK_PASSWORD, map);
	}
	
	
	/**
	 * 查询手机/用户id获取用户注册校验信息
	 * @param querykey
	 * @param querytype
	 * @return
	 */
	public String registQuery(String querykey, String querytype){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("querykey", querykey);
		param.put("querytype", querytype);
		return _post(REGIST_QUERY, param);
	}

	public String createMobileCode(String mobile,String codeType){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("mobileno", mobile);
		param.put("codetype", codeType);
		return _post(GET_IDENTIFYING_CODE_URI, param);
	}
	
	public String validMobileCode(String mobile,String codeType,String valideCode){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("mobileno", mobile);
		param.put("codetype", codeType);
		param.put("validcode", valideCode);
		return _post(CHECK_IDENTIFYING_CODE_URI, param);
	}
	
	public String regist(String mobile,String pwd ,String validcode){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("mobileno", mobile);
		param.put("passwd", pwd);
		param.put("validcode", validcode);
		return _post(USER_RSGIST_URI, param);
	}
	
	public String updateNameAndId(String userId,String realName,String idnumber){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("userid", userId);
		param.put("idnumber", realName);
		param.put("realname", idnumber);
		return _post(UPDATE_NAME_ID, param);
	}
}
