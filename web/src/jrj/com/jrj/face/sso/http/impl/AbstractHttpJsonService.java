/**
 * 
 */
package com.jrj.face.sso.http.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.jrj.common.net.HttpConnectionClient;
import com.jrj.common.net.KeyValue;
import com.jrj.face.AbstractJsonService;

/**
 * http+json接口
 * @author coldwater
 *
 */
public abstract class AbstractHttpJsonService extends AbstractJsonService {
	//10.1服务接口，当静态页面取不到时调用
	protected static final String GETJSON_SERVICE_URL="http://iservices.jrjc.local/services/getJSONData.jsp";
	protected static final String GETJSON_SERVICE_PARAM="pageURL";

	/**
	 * http链接管理器
	 */
	protected HttpConnectionClient httpConnectionClient;

	/**
	 * @param httpConnectionClient the httpConnectionClient to set
	 */
	public void setHttpConnectionClient(HttpConnectionClient httpConnectionClient) {
		this.httpConnectionClient = httpConnectionClient;
	}

	/**
	 * @return the httpConnectionClient
	 */
	public HttpConnectionClient getHttpConnectionClient() {
		return httpConnectionClient;
	}
	
	protected  String getAgain(String url){
		try {
			log.debug("befor ="+url);
			url = java.net.URLEncoder.encode(url, "UTF-8");
			log.debug("after="+url);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<KeyValue> params=new ArrayList<KeyValue>();
		params.add(new KeyValue(GETJSON_SERVICE_PARAM,url));
		return httpConnectionClient.getContextByPostMethod(GETJSON_SERVICE_URL, params);
	}

}
