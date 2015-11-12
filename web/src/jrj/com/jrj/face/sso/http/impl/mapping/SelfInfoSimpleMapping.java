/**
 * 
 */
package com.jrj.face.sso.http.impl.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.jrj.common.page.PageInfo;
import com.jrj.face.JsonListMapping;
import com.jrj.face.exception.FaceException;
import com.jrj.face.sso.type.SelfInfo;
import com.jrj.face.sso.type.SelfInfoSimple;

/**
 * 个性化信息Json映射类
 * 
 * @author coldwater
 * 
 */
public class SelfInfoSimpleMapping extends AbstractJsonMapping implements JsonListMapping<SelfInfoSimple> {
	private static SelfInfoSimpleMapping selfInfoMapping=null;
	/**
	 * 获得一个selfInfoMapping实例，单例模式
	 * @return
	 */
	public static SelfInfoSimpleMapping getInstance(){
//		if (selfInfoMapping==null){
			selfInfoMapping=new SelfInfoSimpleMapping();
//		}
		return selfInfoMapping;
	}

	@Override
	public List<SelfInfoSimple> map(String json) {
		throw new NotImplementedException();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<SelfInfoSimple> map(String json, PageInfo page) {
		List<SelfInfoSimple> result = null;
		try {
			// 数据格式：{“recordCount”:X,“items”:[[userID,userName ,spaceHost,headPic]，[]，…]}
			Map<String,Object> jo = getJsonMap(json);
			
			page.setTotalCount(((Number)jo.get("recordCount")).intValue());
			result=new ArrayList<SelfInfoSimple>();
			if(page.getTotalCount()>0){
				List items = (List)jo.get("items");
				
				
				for (int i=0;i<items.size();i++){
					List datas=(List)items.get(i);
					SelfInfo si=new SelfInfo();
					int j=0;
					si.setUserId(datas.get(j++).toString());
					si.setUserName(datas.get(j++).toString());
					si.setSpaceHost(datas.get(j++).toString());
					si.setHeadPic(datas.get(j++).toString());
					result.add(si);
				}
			}
			
			
		} catch (Exception e) {
			throw new FaceException(e);
		}
		return result;
	}
}
