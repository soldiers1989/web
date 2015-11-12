package com.cfo.stock.web.hk.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.springframework.stereotype.Service;

import com.cfo.common.utils.MediaType;
import com.cfo.common.utils.SimpleRestClientUtil;
import com.cfo.stock.web.global.Global;
import com.jrj.common.service.AbstractBaseService;

@Service
public class CloudAccountHttpService extends AbstractBaseService {
	
	//通过身份证号获取通行证ID
	public static final String GET_CLOUD_ACCOUNT_STATUS=Global.CLOUD_ACCOUNT_URL+"/queryClientStatus.jspa";
	//云开户查询状态
	//http://cloud.jrj.com/cloud/queryClientStatus.jspa?email=a@b.com
	public String queryClientStatus(String email){
		//调用接口
		String url = GET_CLOUD_ACCOUNT_STATUS;
		Map<String,String>params=new HashMap<String, String>();
		params.put("email", email);
		params.put("channel", "ZQT");
		String rs=SimpleRestClientUtil.doPost(url, transMapToString(params),MediaType.APPLICATION_X_WWW_FORM_URLENCODE,"utf-8",true);
		return rs;
	}
	/** 
	 * 方法名称:transMapToString 
	 * 传入参数:map 
	 * 返回值:String 形如 username=chenziwen&password=1234 
	*/  
	private String transMapToString(Map<String,String> map){  
		NameValuePair[] data = new NameValuePair[map.size()]; 
		Set<Entry<String, String>> set = map.entrySet();
        Iterator<Entry<String, String>> iterator = set.iterator(); 
        int i=0;  
        while (iterator.hasNext()) {  
        	Entry<String, String> entry = (Entry<String, String>) iterator.next();
            data[i]=new NameValuePair(entry.getKey(),entry.getValue());  
            i++;  
        }  
		return EncodingUtil.formUrlEncode(data, "UTF-8");
	} 
}
