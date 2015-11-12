/**
 * 
 */
package com.jrj.face.sso.http.impl.mapping;

import java.util.Map;

import com.jrj.common.json.JSONValidatingReader;

/**
 * @author coldwater
 *
 */
public abstract class AbstractJsonMapping {
	protected JSONValidatingReader jr;
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getJsonMap(String json){
		jr=new JSONValidatingReader();
		return (Map<String,Object>)jr.read(json);
	}
	
	
}
