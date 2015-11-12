package com.cfo.stock.web.services.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.service.api.result.MessageListResult;
import com.cfo.common.service.api.utils.ResultUtil;
import com.cfo.common.vo.MessageVo;
import com.cfo.stock.web.exception.StockServiceException;
import com.cfo.stock.web.platform.MessageInterface;
import com.cfo.stock.web.platform.UserCenterInterface;
import com.jrj.common.page.PageInfo;
import com.jrj.common.service.AbstractBaseService;
import com.jrj.common.utils.PropertyManager;

@Service
public class AccountWebService extends AbstractBaseService {
	
	private static final String HEAD_IMAGE_DOMAIN=PropertyManager.getString("head.image.domain");
	
	@Autowired
	UserCenterInterface userCenterInterface;
	@Autowired
	MessageInterface messageInterface;
	
	
	public JSONObject checkMobile(String mobile)throws StockServiceException{
		String  rs = userCenterInterface.checkMobile(mobile);
		if(StringUtils.isBlank(rs)){
			return new JSONObject();
		}
		return JSONObject.parseObject(rs);
	}
	
	public String checkMobileAjax(String mobile){
		return userCenterInterface.checkMobile(mobile);		
	}
	public String checkIdNumber(String id){
		String rs = userCenterInterface.checkIDNumber(id);
		String content="";
		if(!"".equals(rs)){
			JSONObject json = JSONObject.parseObject(rs);
			if(json.getInteger("resultCode")==0){
				content = "ok";
			}else{
				content= json.getString("resultMsg");
			}
		}
		return content;
	}
	
	public String checkINAjax(String idNumber){
		return userCenterInterface.checkIDNumber(idNumber);	
	}
	
	public String validINRealName(String idNumber,String realName){
		return userCenterInterface.validIdNumberRealName(idNumber, realName);
	}
	public String verifyInvitationCode(String userId, String code){
		return userCenterInterface.verifyInvitationCode(userId, code);
	}
	/*public String updateUser(String userId,String idNumber,String realName){
		return userCenterInterface.updateUserInfo(userId, idNumber, realName);
	}
	public String updateUser(String userId,String idNumber,String realName,String mobile){
		return userCenterInterface.updateUserInfo(userId, idNumber, realName, mobile);
	}*/
	public String updateUser(String userId,String idNumber,String realName,String mobile,String email){
		return userCenterInterface.updateUserInfo(userId, idNumber, realName, mobile, email);
	}
	public String updateHkUser(String userId,String idNumber, String realName,String englishName,String hkIdCard, String hkPassport,String mobile, String email){
		return userCenterInterface.updateHkUserInfo(userId, idNumber, realName,englishName,hkIdCard,hkPassport, mobile, email);
	}
	public Map<String,String> regist(String mobile, String password, String ip,
			String cccode,String validcode,String clientinfo) throws StockServiceException{
		String rs = userCenterInterface.regist(mobile, password, ip,cccode,validcode,clientinfo);
		Map<String ,String> map  = new HashMap<String, String>();
		if(!"".equals(rs)){
			JSONObject json = JSONObject.parseObject(rs);
			if(json.getInteger("retcode")==0){
				map.put("userId",json.getString("userid"));
			}else{
				map.put("msg", json.getString("msg"));
			}
		}
		return map;
	}
	
	public String getMobileCode(String mobile){
		String rs = userCenterInterface.sendMobileCode(mobile);
		String content="";
		if(!"".equals(rs)){
			JSONObject json = JSONObject.parseObject(rs);
			if(json.getInteger("resultCode")==0){
				json.put("status", 1);
			}else{
				json.put("status", -1);
			}
			content = json.toJSONString();
		}
		return content;
	}
	
	public String validMobileCode(String mobile,String validCode) throws StockServiceException{
		String rs = userCenterInterface.validMobileCode(mobile,  validCode);
		String content="";
		if(!"".equals(rs)){
			JSONObject json = JSONObject.parseObject(rs);
			if("0".equals(json.getString("resultCode"))){
				json.put("status", 1);
			}else{
				json.put("status", -1);
				json.put("resultCode", -1);
				json.put("resultMsg", "验证码不正确！");
			}
			content=json.toJSONString();
		}else{
			JSONObject json = JSONObject.parseObject(rs);
			json.put("status", -1);
			json.put("resultCode", -1);
			json.put("resultMsg", "验证码不正确！");
			content=json.toJSONString();
		}
		return content;
	}
	
	public List<MessageVo> querySysNotice(String userId,PageInfo pageInfo, Integer publishtype,Integer showtype) {
		String messageList=messageInterface.messageList(userId, pageInfo!=null?pageInfo.getCurrentPageNo():null, pageInfo!=null?pageInfo.getPageSize():null, publishtype, showtype);
		if(log.isDebugEnabled()){
			log.debug("====message=="+messageList);
		}
		MessageListResult result=ResultUtil.parseResult(messageList, MessageListResult.class);
		if(pageInfo!=null){
			pageInfo.setTotalCount(result.getTotalSize());
		}
		return result.getMessagelist();
	}
	
	public String protectIdNumber(String userid){
		String rs = userCenterInterface.protectIdNumber(userid);
		String content="";
		if(!"".equals(rs)){
			JSONObject json = JSONObject.parseObject(rs);
			if(json.getInteger("retcode")==0){
				content = "ok";
			}else{
				content= json.getString("msg");
			}
		}
		return content;
	}
	
	public String inviteme(String userid,Integer invitetype,String invitecode){
		String rs = userCenterInterface.inviteme(userid,invitetype,invitecode);
		String content="";
		if(!"".equals(rs)){
			JSONObject json = JSONObject.parseObject(rs);
			if(json.getInteger("retcode")==0){
				content = "ok";
			}else{
				content= json.getString("msg");
			}
		}
		return content;
	}
	/**
	 * 
	 * @param userName 可以是用户名/手机/邮箱中的一种
	 * @param type 1：身份证,2：用户名，3：手机，4：邮箱
	 * @param password
	 * @return
	 */
	public String  login(String userName,int type,String password){
		String rs = userCenterInterface.userLogin(userName, type, password);
		return rs;
	}
	/**
	 * 根据通行证id获取用户信息
	 */
	public String userInfo(String userId){
		return userCenterInterface.userInfo(userId);
	}

	/**
	 * 根据用户身份证号获取用户ID
	 * @param idNumber
	 * @return
	 */
	public String queryUserIdByIDNumber(String idNumber) {
		String re=userCenterInterface.queryUserIdByIDNumber(idNumber);
		if(StringUtils.isNotBlank(re)){
			JSONObject json = JSONObject.parseObject(re);
			//if("0".equals(json.getString("retcode"))){
				String userId=json.getString("passportId");
				return userId;
			//}
		}
		return null;
	}
	
	/**
	 * 获取i投顾用户信息
	 * @param userId
	 * @return
	 */
	public JSONObject queryItouguUserInfo(String userId){
		String re=userCenterInterface.itouguUserInfo(userId);
		JSONObject result = JSONObject.parseObject(re);
		JSONObject json=new JSONObject();
		if(result.getIntValue("returnCode") == 0){
			json = result.getJSONObject("user");
		}else{
			return json;
		}
		String headImage=json.getString("headImage");
		if(headImage.indexOf("default")  != -1){
			headImage="/stock/images/default_s.jpg";
		}else{
			headImage=headImage.replace(HEAD_IMAGE_DOMAIN, "/stock/images/");
		}
		
		json.put("headImage", headImage);
		return json;
	}
}
