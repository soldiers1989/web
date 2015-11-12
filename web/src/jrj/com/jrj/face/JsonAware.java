package com.jrj.face;

import java.util.Map;

/**
 * 能感知Json对象
 * 以key-value键值对形式存在
 * @author weili.li
 */
public interface JsonAware {
	/**
	 * 设置json对象
	 * @param jsonMap
	 */
	void setJson(Map<String, Object> jsonMap);
	
	/**
	 * 返回json对象中的value
	 * @param key json的key
	 * @return value部分
	 */
	Object getJsonValue(String key);
}
