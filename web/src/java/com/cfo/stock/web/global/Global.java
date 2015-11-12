package com.cfo.stock.web.global;

import com.jrj.common.utils.PropertyManager;

public class Global {
	public static final String USER_CENTER = PropertyManager.getString("user_center_service");
	public static final String USER_INVITE = PropertyManager.getString("user_invite_service");
	public static final String MOBILE_CODE_TYPE_REGIST ="201";
	public static final String BIND_CALLBACK_URL= PropertyManager.getString("bind_callBack_url");
	public static final String DEBUG_SSO = PropertyManager.getString("debug_sso");
	public static final String USER_CENTER_AUTH_GZT = PropertyManager.getString("user_center.authgzt");
	public static final String PASSPORT_LOGIN_URL=PropertyManager.getString("passport.login.url");
	public static final String TICKET_KEY =PropertyManager.getString("passport.zqt.ticket");
	public static final String ZQT_SPID=PropertyManager.getString("passport.zqt.spid");
	public static final String ZQT_SERVER = PropertyManager.getString("passport.zqt.server");
	public static final String COOKIE_LOGIN_OK_KEY = PropertyManager.getString("passport.cookie.login_ok_key");
	public static final String COOKIE_USER_ID_KEY=PropertyManager.getString("passport.cookie.user_id_key");
	public static final String LOGIN_OK = PropertyManager.getString("passport.cookie.login_ok");
	public static final String WEB_CACHE_EXPIRE_TIME = PropertyManager.getString("cfo.web.cache.expire");
	public static final String SEND_EMAIL_URL = PropertyManager.getString("cfo.web.send.email.url");
	public static final String ITOUGU_SERVICE = PropertyManager.getString("itougu.serivce");
	public static final String CLOUD_ACCOUNT_URL = PropertyManager.getString("cfo.web.cloud.account.url");
	public static final String HK_INIT_PASSWORD = PropertyManager.getString("hk.init.password");
	public static final String CFO_WEB_ENVIRONMENT_TYPE = PropertyManager.getString("cfo.web.environment.type");
	public static void main(String[] args) {
		
	}
	//不需要密码
	public static final String NONEPWD="none";

}
