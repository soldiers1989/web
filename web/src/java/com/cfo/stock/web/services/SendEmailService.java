package com.cfo.stock.web.services;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.stock.web.global.Global;
import com.jrj.common.cache.memcached.MemcachedCache;
import com.jrj.common.utils.DateUtil;
import com.jrj.userinfo.front.model.user.RetrieveInfo;
import com.jrj.userinfo.restclient.user.UserRestClient;
import com.jrj.userinfo.restclient.user.UserRestClientFactory;

@Service
public class SendEmailService {
	Logger log=Logger.getLogger(getClass());
	@Autowired
	MemcachedCache memcachedCache;
	
	public  boolean sendEmail(String userId,String email){
    	UserRestClientFactory clientFactory = new UserRestClientFactory(Global.SEND_EMAIL_URL);
		UserRestClient client = clientFactory.getUserRestClientClient();
		
		String random = "";
		while(random.length()<4){
			random+=(int)(Math.random()*10);
		}
		//email = "153563985@qq.com";
		//userId = "141108010071134303";
		try {
			RetrieveInfo ri = client.retrievePWD(userId+","+email+","+random, "5");
			if(ri != null && userId != null && userId.equals(ri.getUserId())){//发送成功
                String key = AttributeKeys.STOCK_EMAIL_PREFIX+userId;
				memcachedCache.set(key, random, DateUtil.add(new Date(), Calendar.HOUR_OF_DAY, 1));
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Method -> sendEmail error", e);
		}
		return false;
    }
}
