/**
 * 
 */
package com.jrj.face.sso.type;

import java.io.Serializable;
import java.util.Date;

/**
 * 个性化信息完全版
 * @author coldwater
 *
 */
public class SelfInfoFull implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6339048339516535185L;
	private String userId;
	private String userName;
	/**
	 * 个性域名
	 */
	private String spaceHost;
	private String trueName;
	/**
	 * 头像小
	 */
	/**
	 * 75,75 
	 */
	private String headPicS;
	/**
	 * 150,150 
	 */
	private String headPicM;
	/**
	 * 50,50 
	 */
	private String headPicXS;
	/**
	 * 25,25 
	 */
	private String headPicMS;
	/**
	 * 性别
	 */
	private String gender;
	private String email;
	private String mobile;
	private boolean mobileIsBind;
	private String education;
	private String occupation;
	private String province;
	private String city;
	private Date registerTime;
	private String finance  ;//finace;
	private String operationType;
	private String chooseType;
	private String investExperience;
	private Long [] investTypes;
	private String summary;
	private boolean headPicIsDefault;
	private boolean spaceHostIsDefault;
	
	//-----new add
	/**
	 * 修改用户信息时的油箱
	 */
	private String email2 ;
	private String birthDay ;
	private String updateTime;
	/**
	 * 老系统的uid
	 */
	private Long uid ;
	private String headPicUTime ;
	
	public boolean isMobileIsBind() {
		return mobileIsBind;
	}
	public void setMobileIsBind(boolean mobileIsBind) {
		this.mobileIsBind = mobileIsBind;
	}
	public boolean isHeadPicIsDefault() {
		return headPicIsDefault;
	}
	public void setHeadPicIsDefault(boolean headPicIsDefault) {
		this.headPicIsDefault = headPicIsDefault;
	}
	public boolean isSpaceHostIsDefault() {
		return spaceHostIsDefault;
	}
	public void setSpaceHostIsDefault(boolean spaceHostIsDefault) {
		this.spaceHostIsDefault = spaceHostIsDefault;
	}
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
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	/**
	 * 75,75
	 * @return
	 */
	public String getHeadPicS() {
		return headPicS;
	}
	public void setHeadPicS(String headPicS) {
		this.headPicS = headPicS;
	}
	/**
	 * 150,150 
	 * @return
	 */
	public String getHeadPicM() {
		return headPicM;
	}
	public void setHeadPicM(String headPicM) {
		this.headPicM = headPicM;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * 是注册时的油箱
	 * @return
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 是注册时的油箱
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Date getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}
	
	
	
	public String getFinance() {
		return finance;
	}
	public void setFinance(String finance) {
		this.finance = finance;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getChooseType() {
		return chooseType;
	}
	public void setChooseType(String chooseType) {
		this.chooseType = chooseType;
	}
	public String getInvestExperience() {
		return investExperience;
	}
	public void setInvestExperience(String investExperience) {
		this.investExperience = investExperience;
	}
	public Long[] getInvestTypes() {
		return investTypes;
	}
	public void setInvestTypes(Long[] investTypes) {
		this.investTypes = investTypes;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * 修改用户信息时的油箱
	 * @return
	 */
	public String getEmail2() {
		return email2;
	}
	/**
	 * 修改用户信息时的油箱
	 * @param email2
	 */
	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * l老系统的uid
	 * @return
	 */
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public String getHeadPicUTime() {
		return headPicUTime;
	}
	public void setHeadPicUTime(String headPicUTime) {
		this.headPicUTime = headPicUTime;
	}
	/**
	 * @param headPicXS the headPicXS to set
	 */
	public void setHeadPicXS(String headPicXS) {
		this.headPicXS = headPicXS;
	}
	/**
	 * 50,50
	 * @return the headPicXS
	 */
	public String getHeadPicXS() {
		return headPicXS;
	}
	/**
	 * @param headPicMS the headPicMS to set
	 */
	public void setHeadPicMS(String headPicMS) {
		this.headPicMS = headPicMS;
	}
	/**
	 * 25,25
	 * @return the headPicMS
	 */
	public String getHeadPicMS() {
		return headPicMS;
	}
	
	
	
	
}
