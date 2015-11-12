package com.cfo.stock.web.hk.services.hkbind;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cfo.stock.web.hk.services.CloudAccountHttpService;
import com.jrj.common.service.AbstractBaseService;

@Service
public class HkBindWebService extends AbstractBaseService {
	@Autowired
	CloudAccountHttpService cloudAccountHttpService;
	//通过邮箱查询资金账号及姓名
	public Map<String,String> queryClientStatus(String email){
		Map<String,String> map = new HashMap<String,String>();
		try{
			map.put("client_no", "");
			map.put("account_name", "");
			//通过email查询账户号
			String rs = cloudAccountHttpService.queryClientStatus(email);
			//测试代码  ISTAR01
			//String rs = "{\"result\":0,\"step\": 7,\"cash_no\":\"100034\",\"account_name\":\"米辉\",\"time\":\"\"}";
			if(rs == null){
				log.error("Mehtod -> queryClientStatus rs is null");
			}else{
				JSONObject json = JSONObject.parseObject(rs);
				if("0".equals(json.getString("result"))){
					if("7".equals(json.getString("step"))){
						//查询支开户接口
						map.put("client_no", json.getString("cash_no"));
						map.put("account_name", json.getString("account_name"));
					}else{
						log.error("Mehtod -> queryClientStatus step is not 7 rs="+rs+" , email="+email);
					}
				}else{
					log.error("Mehtod -> queryClientStatus error rs="+rs+" , email="+email);
				}
			}
		}catch(Exception e){
			log.error("Method -> queryClientStatus error",e);
		}
		return map;
	}
}
