/**
 * 
 */
package com.jrj.face;


/**
 * json数组映射接口
 * @author coldwater
 *
 */
public interface JsonArrayMapping<T> {
	
	public abstract T[] map(String json);
}
