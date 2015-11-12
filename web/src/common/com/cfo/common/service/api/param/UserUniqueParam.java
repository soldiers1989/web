package com.cfo.common.service.api.param;

public class UserUniqueParam extends Param {
	private String email;
	private String username;
	private String mobileno;
	private String idnumber;
	
	
	public UserUniqueParam() {
		super();
	}
	
	public UserUniqueParam(String email, String userName, String mobile,
			String idNumber) {
		super();
		this.email = email;
		this.username = userName;
		this.mobileno = mobile;
		this.idnumber = idNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	
}
