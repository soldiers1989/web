/**
 * 
 */
package com.cfo.common.service;



import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cfo.common.service.api.result.UserInfoResult;
import com.cfo.common.service.api.utils.ResultUtil;
import com.cfo.common.vo.UserInfoVo;



/**
 * 个人中心业务处理Servic类
 * @author hailong.qu
 *
 */
@Service
public class PersonalService extends AbstractXJBService {
	
	
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public UserInfoVo getUserInfo(String userId){
		UserInfoVo userInfoVo = null;
		//获取用户信息
		try {
			String result = userCenterInterface.userInfo(userId);
			if(StringUtils.isBlank(result)){
				return userInfoVo;
			}
			JSONArray json = JSONObject.parseArray(result);
			if(json != null && json.size()>0){
				String info = json.get(0).toString();
				UserInfoResult userInfo =  ResultUtil.parseResult(info, UserInfoResult.class);
				userInfoVo = userInfo.parse();
			}
			
		} catch (Exception e) {
			log.error("getUserInfo exception--"+e.getMessage(),e);
		}
		return userInfoVo;
	}


	
	/**
	 * 检验邮箱唯一性
	 * @param email
	 * @return
	 */
	public String emailUnique(String email) {
		return userXJBApi.emailUnique(email);
	}

	
	/**
	 * 用户密码校验
	 * @param userId
	 * @param password
	 * @return
	 */
	public JSONObject checkPassword(String userId, String password){
		String rs = userXJBApi.checkPassword(userId, password);
		if(!StringUtils.isBlank(rs)){
			return JSONObject.parseObject(rs);
		}
		return null;
	}
}
