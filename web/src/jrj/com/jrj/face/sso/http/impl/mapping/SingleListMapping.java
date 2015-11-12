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

/**
 * 简单listmapping,類似 { “items”: [[userName,userID],…]}
 * @author zhangzhiyong
 *
 */
public class SingleListMapping extends AbstractJsonMapping implements
		JsonListMapping<String[]> {
			
    private static final String JSON_NAME = "items";
    
    private static SingleListMapping instance = new SingleListMapping();
	
	private SingleListMapping(){
		
	}
	
	public static SingleListMapping getInstance(){
		
		return instance ;
	}

	@Override
	public List<String[]> map(String json) {
		try{
			List<String[]> retList = new ArrayList<String[]>();
			Map<String,Object> map = this.getJsonMap(json);
			List<List<String[]>> list = (List<List<String[]>>)map.get(JSON_NAME);
			if(list ==null) return retList ;
			for(List s:list){
				retList.add((String[])s.toArray(new String[]{}));
			}
			
			return retList;
		}catch(Exception e){
			throw new FaceException(e);
		}
		
	}

	@Override
	public List<String[]> map(String json, PageInfo page) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

}
