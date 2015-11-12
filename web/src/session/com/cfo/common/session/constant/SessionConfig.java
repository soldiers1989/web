package com.cfo.common.session.constant;

import com.jrj.common.utils.PropertyManager;

public class SessionConfig {
	public static final String SESSION_EXPIRED=PropertyManager.getString("xjb.web.sessiontime");
	public static final long expire_time=Long.parseLong(SESSION_EXPIRED)*60*1000;
	public static final String CFO_WEB_DOMAIN=PropertyManager.getString("xjb.web.domain");
	public static final String LOGIN_OK="ok";
}
