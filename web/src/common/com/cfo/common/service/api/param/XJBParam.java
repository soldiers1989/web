package com.cfo.common.service.api.param;

import java.math.BigDecimal;

public class XJBParam extends Param {
	private String userid;
	private String passwd;
	private BigDecimal applicationamount;
	private String depositcard;
	private String channelid;
	private String cardid;
	
	public XJBParam(String userid, String passwd, BigDecimal applicationamount,
			String depositcard) {
		super();
		this.userid = userid;
		this.passwd = passwd;
		this.applicationamount = applicationamount;
		this.depositcard = depositcard;
	}
	public XJBParam(String userid, String passwd, BigDecimal applicationamount,
			String depositcard, String channelid) {
		super();
		this.userid = userid;
		this.passwd = passwd;
		this.applicationamount = applicationamount;
		this.depositcard = depositcard;
		this.channelid = channelid;
	}
	public XJBParam(String userid, String passwd, BigDecimal applicationamount) {
		super();
		this.userid = userid;
		this.passwd = passwd;
		this.applicationamount = applicationamount;
	}
	public XJBParam(String userid, String passwd, String cardid, BigDecimal applicationamount) {
		super();
		this.userid = userid;
		this.passwd = passwd;
		this.applicationamount = applicationamount;
		this.cardid = cardid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public BigDecimal getApplicationamount() {
		return applicationamount;
	}
	public void setApplicationamount(BigDecimal applicationamount) {
		this.applicationamount = applicationamount;
	}
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getDepositcard() {
		return depositcard;
	}
	public void setDepositcard(String depositcard) {
		this.depositcard = depositcard;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	
}
