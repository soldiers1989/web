package com.cfo.common.service.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.service.api.param.Param;
import com.cfo.common.utils.ArrayUtils;
import com.cfo.common.utils.MediaType;
import com.cfo.common.utils.SimpleRestClientUtil;
import com.jrj.common.service.AbstractBaseService;


public abstract class XJBServiceAPI extends AbstractBaseService{
	/**
	 * get方式获取数据
	 * @param url
	 * @return
	 */
	protected String _get(String url){
		return SimpleRestClientUtil.doGet(url);
	}
	/**
	 * post方式提交并返回结果
	 * @param url
	 * @param param
	 * @return
	 */
	protected String _post(String url,Param param){
		if(log.isDebugEnabled()){
			log.debug("==post==:"+param.toString());
		}
		return SimpleRestClientUtil.doPost(url, param.toString(),MediaType.APPLICATION_JSON_TYPE,"UTF-8",true);
	}
	/**
	 * post方式提交并返回结果
	 * @param url
	 * @param param
	 * @return
	 */
	protected String _post(String url,Map<String,Object> param){
		if(log.isDebugEnabled()){
			log.debug("==post==:"+JSONObject.toJSONString(param));
		}
		return SimpleRestClientUtil.doPost(url, JSONObject.toJSONString(param),MediaType.APPLICATION_JSON_TYPE,"UTF-8",true);
	}
	
	
	/**
	 * post方式提交并返回状态码
	 * @param url
	 * @param param
	 * @return
	 */
	protected String _postStatus(String url,Param param){
		return SimpleRestClientUtil.doPost(url, param.toString(),MediaType.APPLICATION_JSON_TYPE,"UTF-8",false);
	}
	/**
	 * post方式提交并返回状态码
	 * @param url
	 * @param param
	 * @return
	 */
	protected String _postStatus(String url,Map<String,Object>  param){
		return SimpleRestClientUtil.doPost(url,JSONObject.toJSONString(param),MediaType.APPLICATION_JSON_TYPE,"UTF-8",false);
	}
	/**
	 * 生成地址
	 * @param url
	 * @param param
	 * @return
	 */
	protected String _makeUrl(String url,Map<String,String> param){
		List<String> params=new ArrayList<String>();
		for(String key:param.keySet()){
			String value=param.get(key);
			if(value!=null){
				params.add(key+"="+value);
			}
		}
		if(params.size()>0){
			return url+"?"+ArrayUtils.join(params.toArray(), "&");
		}else{
			return url;
		}
	}
	
	/**
	 * 生成rest风格地址
	 * @param url
	 * @param param
	 * @return
	 */
	protected String _makeRestUrl(String url, Map<String,String> param) {
		for(String key:param.keySet()){
			url=url.replace("{"+key+"}", param.get(key));
		}
		return url;
	}
}
