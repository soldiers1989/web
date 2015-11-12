package com.cfo.common.service.api.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.cfo.common.vo.UserInfoVo;

/**
 * 用户信息VO
 * @author hailong.qu
 *
 */
public class UserInfoResult extends Result<UserInfoVo>{
	
	@JSONField(name="passportId")
	private String userid;
	@JSONField(name="trueName")
	private String realname;
	@JSONField(name="passportName")
	private String username;
	@JSONField(name="idCard")
	private String idnumber;
	private String validdate;
	@JSONField(name="mobile")
	private String mobileno;
	private String email;
	private String postcode;
	private String address;
	private String regioncode;
	private String uniquecode;
	private Integer uncommonword;//是否包含生僻字，0：不包含，1：包含
	private Integer companyuser;//是否公司员工,0:不是，1：是
	private String reservedinfo;//个人预留验证信息
	private String cookieid;//用户cookieid
	private Integer idchecked;
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getValiddate() {
		return validdate;
	}

	public void setValiddate(String validdate) {
		this.validdate = validdate;
	}


	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRegioncode() {
		return regioncode;
	}

	public void setRegioncode(String regioncode) {
		this.regioncode = regioncode;
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

	public String getReservedinfo() {
		return reservedinfo;
	}

	public void setReservedinfo(String reservedinfo) {
		this.reservedinfo = reservedinfo;
	}

	public String getCookieid() {
		return cookieid;
	}

	public void setCookieid(String cookieid) {
		this.cookieid = cookieid;
	}

	public Integer getIdchecked() {
		return idchecked;
	}

	public void setIdchecked(Integer idchecked) {
		this.idchecked = idchecked;
	}

	@Override
	public UserInfoVo parse() {
		return new UserInfoVo(userid, realname, username, idnumber, validdate, mobileno, email, postcode, address, regioncode,uniquecode,uncommonword,companyuser,idchecked);
	}
}

