/**
 * 
 */
package com.jrj.face.sso.http.impl.mapping;

import java.util.Map;

import com.jrj.face.JsonObjectMapping;
import com.jrj.face.exception.FaceException;

/**
 * 解读简单的，类似 { “xxx”:xxx}
 * @author zhangzhiyong
 *
 */
public class SingleMapping extends AbstractJsonMapping implements JsonObjectMapping<Map> {

	
	private static SingleMapping instance = new SingleMapping();
	
	private SingleMapping(){
		
	}
	
	public static SingleMapping getInstance(){
		
		return instance ;
	}
	
	@Override
	public Map map(String json) {
		try{
		Map<String,Object> map =this.getJsonMap(json);
		
		return map;
		}catch(Exception e){
			throw new FaceException(e);
		}
	}

}///~;
