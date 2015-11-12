/**
 * 
 */
package com.cfo.common.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * HttpSession管理器
 * 
 * @author yuanlong.wang 2013-4-25
 * 
 */

public class HttpSessionWrapper  {
	private String sessionId;
	private HttpSession session;

	public HttpSessionWrapper(String sessionId, HttpSession session) {
		this.sessionId = sessionId;
		this.session = session;
	}

	/**
	 * 获取属性值 
	 * 仅适用于取简单类型的数据 如存储的是复杂类型 
	 * 			如 一个VO 则请用 getAttribute(String name,Class<T> clazz)
	 */
	public Object getAttribute(String name) {
		HttpSessionService service = HttpSessionService.getInstance();
		return service.getAttribute(sessionId, name);
	}
	/**
	 * 获取属性值
	 * @param name
	 * @param clazz
	 * @return
	 */
	public <T> T getAttribute(String name,Class<T> clazz){
		return JSONObject.parseObject(JSONObject.toJSONString(getAttribute(name)),clazz);
	}

	/**
	 * 获取所有属性
	 * 
	 * @return
	 */
	public Map<String, Object> getAttributs() {
		HttpSessionService service = HttpSessionService.getInstance();
		List<AttributeBean> list = service.getAttributes(sessionId);
		Map<String, Object> map = new HashMap<String, Object>();
		for (AttributeBean bean : list) {
			map.put(bean.getKey(), bean.getValue());
		}
		return map;
	}

	public long getCreationTime() {
		return session.getCreationTime();
	}

	public String getId() {
		return this.sessionId;
	}
/**
 * 获取最后访问时间
 */
	public long getLastAccessedTime() {
//		return session.getLastAccessedTime();
		Long lastAccessTime=HttpSessionService.getInstance().getLastAccessdTime(sessionId);
		if(lastAccessTime==null)return -1;
		return lastAccessTime.longValue();
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	public ServletContext getServletContext() {
		return session.getServletContext();
	}
/**
 * 单独存储的值
 */
	public Object getValue(String name) {
		System.out.println("sessionid=="+sessionId+"=name="+name);
		HttpSessionService service = HttpSessionService.getInstance();
		return service.getValue(sessionId, name);
	}
	/**
	 * 获取属性值
	 * @param name
	 * @param clazz
	 * @return
	 */
	public <T> T getValue(String name,Class<T> clazz){
		return (T)getValue(name);
	}
	
	public String[] getValueNames() {
		List<String> list = new ArrayList<String>();
		List<String> names = HttpSessionService.getInstance()
				.getAttributeNames(sessionId);
		if (names != null) {
			for (String name : names) {
				list.add(name);
			}
		}
		if (session != null) {
			String[] strArray = session.getValueNames();
			if (strArray != null) {
				for (int i = 0, n = strArray.length; i < n; i++) {
					list.add(strArray[i]);
				}
			}
		}
		return (String[]) list.toArray();
	}

	public void invalidate() {
		session.invalidate();
	}

	public boolean isNew() {
		return session.isNew();
	}
/**
 * 单独存储值
 *  与 setAttribute 不同
 */
	public void putValue(String name, Object value) {
		HttpSessionService service = HttpSessionService.getInstance();
		service.putValue(sessionId, name, value,null);
	}
/**
 * 单独存值 带过期时间
 * @param name
 * @param value
 * @param time
 */
	public void putValue(String name,Object value, Date time){
		HttpSessionService service = HttpSessionService.getInstance();
		System.out.println("sessionid=="+sessionId+"=name="+name+"=value="+value);
		service.putValue(sessionId, name, value,time);
	}
	
	public void removeAttribute(String name) {
		HttpSessionService service = HttpSessionService.getInstance();
		service.removeAttribute(sessionId, name);
	}
/**
 * 移除单独存储值
 */
	public void removeValue(String name) {
		HttpSessionService service = HttpSessionService.getInstance();
		service.removeValue(sessionId, name);
	}
/**
 * 存在公共的sessionKey上
 * 	与 putValue 不同
 */
	public void setAttribute(String name, Object value) {
		HttpSessionService service = HttpSessionService.getInstance();
		service.setAttribute(sessionId, name, value);
	}

	public void setMaxInactiveInterval(int arg0) {
		session.setMaxInactiveInterval(arg0);
	}
	/**
	 * 移除session
	 */
	public void deleteSession() {
		HttpSessionService.getInstance().deleteSession(this.sessionId);
	}
	
	public void refreshSession(){
		HttpSessionService.getInstance().refreshSession(this.sessionId);
	}

	//@Override
	public Enumeration<String> getAttributeNames() {
		Vector v = new Vector();
		List<String> names = HttpSessionService.getInstance()
				.getAttributeNames(sessionId);
		if (names != null) {
			for (String name : names) {
				v.add(name);
			}
		}
		if (session != null) {
			Enumeration e = session.getAttributeNames();
			while (e.hasMoreElements()) {
				v.add(e.nextElement());
			}
		}
		return v.elements();
	}

	//@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}
}
