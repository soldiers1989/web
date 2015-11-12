package com.cfo.common.session.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class URLUtils {
	/**
	 * 解析出url请求的路径，包括页面
	 * 
	 * @param strURL
	 *            url地址
	 * @return url路径
	 */
	public static String UrlPage(String strURL) {
		String strPage = null;
		String[] arrSplit = null;

		strURL = strURL.trim();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 0) {
			if (arrSplit.length > 1) {
				if (arrSplit[0] != null) {
					strPage = arrSplit[0];
				}
			}
		}

		return strPage;
	}

	/**
	 * 去掉url中的路径，留下请求参数部分
	 * 
	 * @param strURL
	 *            url地址
	 * @return url请求参数部分
	 */
	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}

		return strAllParam;
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 * 
	 * @param URL
	 *            url地址
	 * @return url请求参数部分
	 */
	public static Map<String, String> URLRequest(String URL) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		// 每个键值为一组
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}
	public static String ParamterFilter(String aText){
		return ParamterFilter(aText, '\0');
	}
	/**
	 * 过滤入参，防止url注入攻击
	 */
	public static String ParamterFilter(String aText,char excludeCharcter){
		if(StringUtils.isBlank(aText)) return aText;
		
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE ){
		if (character == '<'&& excludeCharcter!='<') {
			result.append("");
		}
		else if (character == '>'&& excludeCharcter!='>') {
			result.append("");
		}
		else if (character == '&'&& excludeCharcter!='&') {
			result.append("");
		}
		else if (character == '|'&& excludeCharcter!='|') {
			result.append("");
		}
		else if (character == ';'&& excludeCharcter!=';') {
			result.append("");
		}
		else if (character == '$'&& excludeCharcter!='$') {
			result.append("");
		}
		else if (character == '%'&& excludeCharcter!='%') {
			result.append("");
		}
		else if (character == '\''&& excludeCharcter!='\'') {
			result.append("");
		}
		else if (character == '\"'&& excludeCharcter!='\"') {
			result.append("");
		}
		else if (character == '\\'&& excludeCharcter!='\\') {
			result.append("");
		}
		else if (character == ','&& excludeCharcter!=',') {
			result.append("");
		}
		else if (character == '+'&& excludeCharcter!='+') {
			result.append("");
		}
		else if (character == '('&& excludeCharcter!='(') {
			result.append("");
		}
		else if (character == ')'&& excludeCharcter!=')') {
			result.append("");
		}
		else if (character == '\r'&& excludeCharcter!='\r') {
			result.append("");
		}
		else if (character == '\n'&& excludeCharcter!='\n') {
			result.append("");
		}
		else{
			result.append(character);
		}
		character = iterator.next();
		}
		return result.toString();
		}
	
	public static String urlEncode(String url){
		
		String reultUrl = "";
		if(StringUtils.isBlank(url)){
			return "";
		}
		try {
			reultUrl = URLEncoder.encode(URLUtils.ParamterFilter(URLDecoder.decode(url,"utf-8"),'&'),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
		return reultUrl;
	}
	public static void main(String[] args) {
		String s = "http://localhost:8081/stock/spread.jspa?stockCode=002069&direction=2&from=xxx";
		System.out.println(ParamterFilter(s,'\0'));
	}
}