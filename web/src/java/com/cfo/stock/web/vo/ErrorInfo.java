package com.cfo.stock.web.vo;

import java.util.Date;

public class ErrorInfo {
	//业务名称
	public String  businessName;
	//错误信息
	public String errorInfo;
	//返回页面名称
	public String returnName;
	//返回目标页面地址
	public String returnUrl;
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public String getReturnName() {
		return returnName;
	}
	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
	
}
