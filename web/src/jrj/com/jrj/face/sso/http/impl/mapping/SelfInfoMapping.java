/**
 * 
 */
package com.jrj.face.sso.http.impl.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.jrj.common.page.PageInfo;
import com.jrj.common.utils.DateUtil;
import com.jrj.face.JsonListMapping;
import com.jrj.face.exception.FaceException;
import com.jrj.face.sso.type.SelfInfo;

/**
 * 个性化信息Json映射类
 * 
 * @author coldwater
 * 
 */
public class SelfInfoMapping extends AbstractJsonMapping implements
		JsonListMapping<SelfInfo> {
	private static SelfInfoMapping selfInfoMapping = null;

	/**
	 * 获得一个selfInfoMapping实例，单例模式
	 * 
	 * @return
	 */
	public static SelfInfoMapping getInstance() {
//		if (selfInfoMapping == null) {
			selfInfoMapping = new SelfInfoMapping();
//		}
		return selfInfoMapping;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SelfInfo> map(String json) {
		List<SelfInfo> result = null;
		try {
			// 数据格式：{items:[[ online,userID,username,spaceHost，headPic,
			// loginTime],[],…]}
			Map<String, Object> jo = getJsonMap(json);
			List items = (List) jo.get("items");
			if (items != null) {
				result=new ArrayList<SelfInfo>();
				for (int i = 0; i < items.size(); i++) {
					List datas = (List) items.get(i);
					SelfInfo si = new SelfInfo();
					int j = 0;
					si.setOnline((Boolean) datas.get(j++));
					si.setUserId(datas.get(j++).toString());
					si.setUserName(datas.get(j++).toString());
					si.setSpaceHost(datas.get(j++).toString());
					si.setHeadPic(datas.get(j++).toString());
					si.setLoginTime(DateUtil
							.parseAll(datas.get(j++).toString()));
					result.add(si);
				}
			}
		} catch (Exception e) {
			throw new FaceException(e);
		}
		return result;
	}

	@Override
	public List<SelfInfo> map(String json, PageInfo page) {
		throw new NotImplementedException();
	}
}
