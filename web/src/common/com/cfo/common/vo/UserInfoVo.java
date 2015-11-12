package com.cfo.common.vo;

/**
 * 用户信息VO
 * @author hailong.qu
 *
 */
public class UserInfoVo extends UserBaseVo{
	
	private String realName;
	private String userName;
	private String idNumber;
	private String validDate;
	private String mobile;
	private String email;
	private String postCode;
	private String address;
	private String regionCode;
	private String uniquecode;
	private Integer uncommonword;//是否包含生僻字，0：不包含，1：包含
	private Integer companyuser=0;//是否公司员工,0:不是，1：是
	private Integer idchecked;//身份证是否经过验证 1:已验证，0:未验证
	public UserInfoVo() {
		// TODO Auto-generated constructor stub
	}
	
	public UserInfoVo(String userId, String realName, String userName,
			String idNumber, String validDate, 
			String mobile, String email, String postCode, String address,
			String regionCode,String uniquecode,Integer uncommonword,Integer companyuser,Integer idchecked) {
		super();
		this.realName = realName;
		this.userName = userName;
		this.idNumber = idNumber;
		this.validDate = validDate;
		this.mobile = mobile;
		this.email = email;
		this.postCode = postCode;
		this.address = address;
		this.regionCode = regionCode;
		this.uniquecode = uniquecode;
		this.uncommonword=uncommonword;
		this.companyuser=companyuser;
		this.idchecked = idchecked;
	}

	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getValidDate() {
		return validDate;
	}
	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getUniquecode() {
		return uniquecode;
	}


	public void setUniquecode(String uniquecode) {
		this.uniquecode = uniquecode;
	}


	public Integer getUncommonword() {
		return uncommonword;
	}


	public void setUncommonword(Integer uncommonword) {
		this.uncommonword = uncommonword;
	}


	public Integer getCompanyuser() {
		return companyuser;
	}


	public void setCompanyuser(Integer companyuser) {
		this.companyuser = companyuser;
	}

	public Integer getIdchecked() {
		return idchecked;
	}

	public void setIdchecked(Integer idchecked) {
		this.idchecked = idchecked;
	}

	
}

