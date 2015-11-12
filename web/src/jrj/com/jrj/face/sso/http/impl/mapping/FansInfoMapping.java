package com.jrj.face.sso.http.impl.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.jrj.common.page.PageInfo;
import com.jrj.face.JsonListMapping;
import com.jrj.face.sso.type.FansInfo;

public class FansInfoMapping extends AbstractJsonMapping implements JsonListMapping<FansInfo> {

	private static FansInfoMapping instance = new FansInfoMapping();
	
	private FansInfoMapping(){};

	public static FansInfoMapping getInstance() {
		
		return instance;
	}
	
	@Override
	public List<FansInfo> map(String json) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public List<FansInfo> map(String json, PageInfo page) {
		List<FansInfo> result = new ArrayList<FansInfo>();
		
		Map<String, Object> jo = getJsonMap(json);
		page.setTotalCount(((Number) jo.get("recordCount")).intValue());
		if(page.getTotalCount()>0){
			List items = (List) jo.get("items");
			FansInfo info=null;
			for (int i = 0; i < items.size(); i++) {
				List datas = (List) items.get(i);
				info = new FansInfo();
				int j = 0;
				info.setUserID((String)datas.get(j++));
				info.setUserName((String)datas.get(j++));
				info.setSpaceHost((String)datas.get(j++));
				
				
				result.add(info);
			}
		}
		
		
		return result;
	}

}
