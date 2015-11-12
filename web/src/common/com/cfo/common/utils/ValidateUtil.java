package com.cfo.common.utils;

import java.util.regex.Pattern;

/**
 * 验证用户类型工具类
 * @author hailong.qu
 *
 */
public class ValidateUtil {
	
	/**
	 * 是否是身份证
	 * @param idNumber
	 * @return
	 */
	public static boolean isIdNumber(String idNumber){
		if(idNumber==null || idNumber.length()!=18){
			return false;
		}
		Pattern pattern = Pattern
				.compile("^\\d{17}([0-9]|X|x)$");
		return pattern.matcher(idNumber).matches();
	}
	/**
	 * 是否是邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		if (email == null || email.length() < 1 || email.length() > 256) {
			return false;
		}
		Pattern pattern = Pattern
				.compile("^[0-9a-z_][_\\.0-9a-z-]{0,31}@([0-9a-z][0-9a-z-]{0,30}\\.){1,4}[a-z]{2,4}$");
		return pattern.matcher(email).matches();
	}
	/**
	 * 是否是手机
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		try {
			String regex = "^1[\\d]{10}$";
			Pattern pattern = Pattern.compile(regex);
			return pattern.matcher(mobile).matches();
		} catch (RuntimeException e) {
			return false;
		}
	}
	/**
	 * 验证是否用户名
	 * @param userName
	 * @return
	 */
	public static boolean isUserName(String userName){
		Pattern pattern = Pattern
				.compile("^[A-Za-z][A-Za-z0-9]{5,15}$");
		return pattern.matcher(userName).matches();
	}
}
