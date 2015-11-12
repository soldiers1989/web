package com.cfo.stock.web.vo;

import java.io.Serializable;
import java.util.Date;

import com.jrj.stocktrade.api.account.vo.FundAccount;
import com.jrj.stocktrade.api.stock.vo.StockAccount;

public class UserAuthVo  implements Serializable{
	private long id;//主键id
	private String userId;
	private String accUserId;
	private long accountId;
	private Integer status;
	private Integer type;
	private Integer sort;
	private boolean def;
	private Date ctime;
	private Date utime;
	private boolean valid;
	private String brokerId;
	private String relationUserId;//关系用户ID
	private String message;
	private String trueName;//真实姓名
	private FundAccount fundAccount;//资金帐号信息
	private String shstockAccount;//沪市
	private String szstockAccount;//深市
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccUserId() {
		return accUserId;
	}
	public void setAccUserId(String accUserId) {
		this.accUserId = accUserId;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public boolean isDef() {
		return def;
	}
	public void setDef(boolean def) {
		this.def = def;
	}
	public Date getCtime() {
		return ctime;
	}
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}
	public Date getUtime() {
		return utime;
	}
	public void setUtime(Date utime) {
		this.utime = utime;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getBrokerId() {
		return brokerId;
	}
	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}
	public String getRelationUserId() {
		return relationUserId;
	}
	public void setRelationUserId(String relationUserId) {
		this.relationUserId = relationUserId;
	}
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	public FundAccount getFundAccount() {
		return fundAccount;
	}
	public void setFundAccount(FundAccount fundAccount) {
		this.fundAccount = fundAccount;
	}
	public String getShstockAccount() {
		return shstockAccount;
	}
	public void setShstockAccount(String shstockAccount) {
		this.shstockAccount = shstockAccount;
	}
	public String getSzstockAccount() {
		return szstockAccount;
	}
	public void setSzstockAccount(String szstockAccount) {
		this.szstockAccount = szstockAccount;
	}
}
