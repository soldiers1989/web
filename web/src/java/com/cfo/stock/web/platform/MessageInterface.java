package com.cfo.stock.web.platform;


import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.utils.MediaType;
import com.cfo.common.utils.SimpleRestClientUtil;
import com.cfo.stock.web.global.Global;

/**
 * 站内消息接口
 * 
 * @author hailong.qu
 * 
 */
@Service("messageXJBApi")
public class MessageInterface {
	
	
	// 用户站内消息列表
	private final static String GET_MESSAGE_URI = Global.USER_CENTER
			+ "/mymessage/list";
	
	SimpleRestClientUtil simpleRestClient;
	/**
	 * 获取用户站内公告列表
	 * @param page
	 * @param size
	 * @param msgtype
	 * @param publishtype
	 * @param showtype
	 * @return
	 */
	public String messageList(String userId,Integer page,Integer size,Integer publishtype,Integer showtype){
		JSONObject json =new JSONObject();
		json.put("userid", userId);
		json.put("page", page);
		json.put("size", size);
		json.put("publishtype", publishtype);
		json.put("showtype", showtype);
		json.put("msgapptype", 2);
		return simpleRestClient.doPost(GET_MESSAGE_URI,json.toJSONString(),MediaType.APPLICATION_XML_TYPE, "utf-8", true);
	}
	

}
