package com.cfo.common.utils;

/**
 * http请求头媒体类型枚举 
 *
**/
public enum MediaType {
	APPLICATION_JSON_TYPE,APPLICATION_XML_TYPE,APPLICATION_X_WWW_FORM_URLENCODE;
	public String toString() {
		switch (this){
			case APPLICATION_JSON_TYPE:
				return "application/json";
			case APPLICATION_XML_TYPE:
				return "application/xml";
			case APPLICATION_X_WWW_FORM_URLENCODE:
				return "application/x-www-form-urlencoded";
			default:
				return "*/*";
		}
	};
}
