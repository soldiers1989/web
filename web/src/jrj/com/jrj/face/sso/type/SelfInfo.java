/**
 * 
 */
package com.jrj.face.sso.type;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户个性化信息
 * @author coldwater
 *
 */
@SuppressWarnings("serial")
public class SelfInfo extends SelfInfoSimple implements Serializable {
	private boolean online;
	private Date loginTime;
	
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
}
