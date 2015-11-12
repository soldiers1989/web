/**
 * 
 */
package com.jrj.face.sso.type;

/**
 * @author zhangzhiyong
 *
 */
public class FansInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2348125620936815484L;
	
	private String userID;
	private String userName;
	private String spaceHost;
	private String headPic;
	private int friendCount;
	private int concernCount ;
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
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
	public int getFriendCount() {
		return friendCount;
	}
	public void setFriendCount(int friendCount) {
		this.friendCount = friendCount;
	}
	public int getConcernCount() {
		return concernCount;
	}
	public void setConcernCount(int concernCount) {
		this.concernCount = concernCount;
	}
	
	
 
	

}
