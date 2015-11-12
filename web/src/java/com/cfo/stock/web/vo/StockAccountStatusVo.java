package com.cfo.stock.web.vo;

import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;

/**
* @author hui.mi
*/

@SuppressWarnings("serial")
public class StockAccountStatusVo extends StockAccountStatus{
	private String state;//开户状态值
	private String stateName;//开户状态名称
    private String strCreateTime;//添加日期
    private String strTradeableTime="";//可交易日期
    private String strCompleteTime="";//开通日期
    private String strBrokerId;//券商id
    private String strBrokerName;//券商名称
    private int strAccountType;//开户类型
    private String strComplateTime="";//开户完成日期
	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStrBrokerId() {
		return strBrokerId;
	}

	public void setStrBrokerId(String strBrokerId) {
		this.strBrokerId = strBrokerId;
	}

	public String getStrBrokerName() {
		return strBrokerName;
	}

	public void setStrBrokerName(String strBrokerName) {
		this.strBrokerName = strBrokerName;
	}

	public int getStrAccountType() {
		return strAccountType;
	}

	public void setStrAccountType(int strAccountType) {
		this.strAccountType = strAccountType;
	}

	public String getStrTradeableTime() {
		return strTradeableTime;
	}

	public void setStrTradeableTime(String strTradeableTime) {
		this.strTradeableTime = strTradeableTime;
	}

	public String getStrComplateTime() {
		return strComplateTime;
	}

	public void setStrComplateTime(String strComplateTime) {
		this.strComplateTime = strComplateTime;
	}
	
}
