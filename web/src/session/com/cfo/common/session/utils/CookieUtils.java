package com.cfo.common.session.utils;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CookieUtils {
	protected Log log=LogFactory.getLog(this.getClass());
	
	private static String cookieDomain = "";

	private static String cookiePath = "/";

	private static int TenAge = 864000;
	
	public static String getCookieByName(HttpServletRequest request,String name) {
		Cookie cookies[] = request.getCookies();
		Cookie mycookie = null;
		String cookievalue = "";
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				mycookie = cookies[i];
				if (name.equals(mycookie.getName())) {
					cookievalue = mycookie.getValue();
					 break;
				}
			}
		}
		return cookievalue;
	}
	
	public static void removeCookieByName(HttpServletRequest request,HttpServletResponse response,List<String> tempList){
		Cookie[] cookies=request.getCookies(); 
		try 
		{ 
		     for(int i=0;i<cookies.length;i++)   
		     { 
		       if(tempList.contains(cookies[i].getName())){
		        	cookies[i].setMaxAge(0);
		        	cookies[i].setPath("/");
		        	response.addCookie(cookies[i]); 
		      }
		     } 
		}catch(Exception ex) 
		{ 
			System.out.println("remove cookie error----"+ex);
		} 
	}
	
	public static void updateCookieByName(HttpServletResponse response,String key,String value){
		Cookie mycookies = new Cookie(key, value);
		mycookies.setMaxAge(TenAge);
		mycookies.setDomain(cookieDomain);
		mycookies.setPath(cookiePath);
		response.addCookie(mycookies);
	}
}
