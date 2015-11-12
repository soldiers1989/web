package com.cfo.common.service.api.param;


/**
 * 身份证有效性
 * @author hailong.qu
 *
 */
public class ValidIdNumberParam extends Param {
	private String idnumber;
	private String realname;
	private String userid;
	
	public ValidIdNumberParam() {
		super();
	}


	public ValidIdNumberParam(String userid,String idnumber, String realname) {
		super();
		this.userid = userid;
		this.idnumber = idnumber;
		this.realname = realname;
	}


	public String getIdnumber() {
		return idnumber;
	}


	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}


	public String getRealname() {
		return realname;
	}


	public void setRealname(String realname) {
		this.realname = realname;
	}


	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	
}

