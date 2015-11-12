/**
 * 
 */
package com.cfo.common.service;


import org.springframework.stereotype.Service;

import com.cfo.common.enums.UserLoginRegResult;
import com.cfo.common.enums.UserNameType;
import com.cfo.common.service.api.utils.ResultUtil;
import com.cfo.common.utils.ValidateUtil;
import com.cfo.common.vo.UserBaseVo;



/**
 * 登录登出业务处理Servic类
 * @author coldwater
 *
 */
@Service
public class LoginOutService extends AbstractXJBService {

	/**
	 * 登录检验
	 * @param username
	 * @param password
	 * @return
	 */
	public UserBaseVo check(String username, String password) {
		//=================================测试用
//		UserBaseVo bo=isTest(username,password);
//		if(bo!=null)return bo;
		
		//以下为正式
		int type=0;
		if(ValidateUtil.isIdNumber(username)){
			type = UserNameType.IDCARD.type;
		}else if(ValidateUtil.isUserName(username)){
			type = UserNameType.USERNAME.type;
		}else if(ValidateUtil.isMobile(username)){
			type = UserNameType.MOBILE.type;
		}else if(ValidateUtil.isEmail(username)){
			type = UserNameType.EMAIL.type;
		}
		if(type == 0){
			return null;
		}
		UserLoginRegResult u = null;
		try {
			//String result = userXJBApi.userLogin(username, type, password);
			String result = this.userCenterInterface.userLogin(username,type,password);
			if(log.isDebugEnabled()){
			log.debug("====登录结果！=="+result);
		}
			u = ResultUtil.parseResultNoE(result, UserLoginRegResult.class);
		} catch (Exception e) {
			if(log.isDebugEnabled()){
				e.printStackTrace();
			log.debug("登录错误："+e);
		}
			return null;
		}
		return u==null?null:u.parse();
		
	}
	
	private UserBaseVo isTest(String username,String password){
		if("123".equals(username)&&"202cb962ac59075b964b07152d234b70".equals(password)){
			UserBaseVo ba=new UserBaseVo();
			ba.setFailtimes(0);
			ba.setUserId("75a73c22c80f11e2976f001f29e53dc8");
			return ba;
		}
		return null;
	}

	
}
