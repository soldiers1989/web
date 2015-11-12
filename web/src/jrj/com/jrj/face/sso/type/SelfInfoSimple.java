/**
 * 
 */
package com.jrj.face.sso.type;

import java.io.Serializable;

/**
 * 用户个性化信息
 * @author coldwater
 *
 */
@SuppressWarnings("serial")
public class SelfInfoSimple implements Serializable {
	private String userId;
	private String userName;
	private String spaceHost;
	private String headPic;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSpaceHost() {
		return spaceHost;
	}
	public void setSpaceHost(String spaceHost) {
		this.spaceHost = spaceHost;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
}
