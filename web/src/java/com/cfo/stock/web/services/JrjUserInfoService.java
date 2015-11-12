package com.cfo.stock.web.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.utils.StringUtil;
import com.cfo.common.utils.SimpleRestClientUtil;
import com.cfo.stock.web.global.Global;
import com.jrj.face.sso.SelfService;
import com.jrj.face.sso.type.SelfInfo;
import com.jrj.face.sso.type.SelfInfoFull;
import com.jrj.sso.ws.SSOProxy;
import com.jrj.sso.ws.SSOProxyServiceClient;
import com.jrj.sso.ws.SsoToken;
import com.jrj.sso.ws.UserSession;

@Service
public class JrjUserInfoService {
	Logger log=Logger.getLogger(getClass());
	private SelfService selfService;
	public SsoToken getSsoToken(String ticket){
		String t = DigestUtils.md5Hex(ticket +Global.TICKET_KEY);
		SSOProxy sso = new SSOProxyServiceClient().getSSOProxyPort();
		SsoToken ssoToken = sso.findUserByTicket(t, Global.ZQT_SPID);
		return ssoToken;
	}
	public int getNewMessageCount(String uid) {
		if (uid==null) {
			return 0;
		}
		String json = "";
		try {
			String url = "http://iservices.jrjc.local/message/newMessagesCount.jsp?userID=" + uid;
			json = SimpleRestClientUtil.doGet(url);
			JSONObject obj = JSONObject.parseObject(json);
			int i1 = obj.getIntValue("newSystemCount");
			int i2 = obj.getIntValue("newCommonCount");
			int i3 = obj.getIntValue("newAnnouncementCount");
			return i1+i2+i3;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public SelfInfoFull getSelfInfoFull(String userid) {
		try{
		return getSelfService().getUserInfoFull(userid);
		}catch (Exception e) {
			log.error("请求用户信息失败", e);
		}
		return null;
	}
	
	public SelfService getSelfService() {
		return selfService;
	}
	public void setSelfService(SelfService selfService) {
		this.selfService = selfService;
	}
	
	
}
