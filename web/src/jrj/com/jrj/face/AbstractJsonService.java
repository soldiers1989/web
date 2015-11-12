/**
 * 
 */
package com.jrj.face;

import java.util.List;

import com.jrj.common.page.PageInfo;
import com.jrj.common.service.AbstractBaseService;

/**
 * 接口基类
 * @author coldwater
 *
 */
public abstract class AbstractJsonService extends AbstractBaseService {

	/**
	 * 从Json串实体化为类
	 * @param <T>
	 * @param json
	 * @param jsonMapping
	 * @return
	 */
	protected <T> List<T> getEntityForList(String json, JsonListMapping<T> jsonMapping) {
		return jsonMapping.map(json);
	}

	/**
	 * 从Json串实体化为类
	 * @param <T>
	 * @param json
	 * @param jsonMapping
	 * @return
	 */
	protected <T> T getEntity(String json,JsonObjectMapping<T> jsonMapping){
		return jsonMapping.map(json);
	}
	
	/**
	 * 从Json串实体化为数组
	 * @param <T>
	 * @param json
	 * @param jsonMapping
	 * @return
	 */
	protected <T> T[] getEntityForArray(String json,JsonArrayMapping<T> jsonMapping){
		return jsonMapping.map(json);
	}
	
	/**
	 * 从Json串实体化为分页列表对象
	 * @param <T>
	 * @param json
	 * @param page
	 * @param jsonMapping
	 * @return
	 */
	protected <T> List<T> getEntityForPageList(String json, PageInfo page,JsonListMapping<T> jsonMapping){
		return jsonMapping.map(json, page);
	}
}
