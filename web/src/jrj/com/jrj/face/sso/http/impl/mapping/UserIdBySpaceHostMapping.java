/**
 * 
 */
package com.jrj.face.sso.http.impl.mapping;

import java.util.Map;

import com.jrj.face.JsonObjectMapping;
import com.jrj.face.exception.FaceException;

/**
 * 通过个性域名取用户id
 * 
 * @author zhangzhiyong
 * 
 */
public class UserIdBySpaceHostMapping extends AbstractJsonMapping implements
		JsonObjectMapping<String> {

	private static final String USER_ID = "userID";

	private static UserIdBySpaceHostMapping instance = new UserIdBySpaceHostMapping();

	private UserIdBySpaceHostMapping() {

	}

	public static UserIdBySpaceHostMapping getInstance() {
		return instance;
	}

	@Override
	public String map(String json) {
		try {
			Map<String, Object> jo = getJsonMap(json);
			return (String)jo.get(USER_ID);
		} catch (Exception e) {
			throw new FaceException("json 通过个性域名取用户id 出错误", e);
		}
	}

}// ~;
