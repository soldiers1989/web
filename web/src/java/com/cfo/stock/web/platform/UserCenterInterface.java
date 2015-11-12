package com.cfo.stock.web.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.utils.MediaType;
import com.cfo.common.utils.SimpleRestClientUtil;
import com.cfo.stock.web.global.Global;

@Service
public class UserCenterInterface {
	public static final String USERINFO = Global.USER_CENTER +"/user/info/$1";
	//获取用户信息
	public static final String CONTROLLED_USERINFO = Global.USER_CENTER +"/userPassportById.jsp";
	//验证手机号是否已经注册
	public static final String UNIQUE_MOBILE = Global.USER_CENTER + "/mobileExist.jsp";
	//验证身份证号是否已经注册
	public static final String UNIQUE_IDNUMBER = Global.USER_CENTER + "/certificateExist.jsp";
	//通过身份证号获取通行证ID
	public static final String GET_USER_INFO_BY_IDNUMBER=Global.USER_CENTER+"/getPassportIdByIdCard.jsp";
	public static final String QUERY_INFO = Global.USER_CENTER + "/user/queryinfo";
	//校验身份证号姓名是否一致
	public static final String VALID_IDNUMBER_REALNAME = Global.USER_CENTER + "/certificateValid.jsp";
	//更新通行证用户信息
	public static final String UPDATE_USER_INFO = Global.USER_CENTER + "/updatePassport.jsp";
	//更新通行证用户信息
	public static final String UPDATE_HK_USER_INFO = Global.USER_CENTER + "/updateHKPassport.jsp";
	public static final String REGIST = Global.USER_CENTER + "/user/reg";
	public static final String UPDATE_NAME_ID = Global.USER_CENTER + "/user/updatename";
	//发送手机验证码
	public static final String CREATE_MOBILE_CODE = Global.USER_CENTER+"/sendVerifyCode.jsp";
	//验证手机验证码
	public static final String VALID_MOBILE_CODE =Global.USER_CENTER + "/verifyCode.jsp";
	public static final String USER_LOGIN = Global.USER_CENTER + "/user/login";
	public static final String PROTECT_IDNUMBER = Global.USER_CENTER + "/user/idnumber/protect";
	public static final String INVITE_ME = Global.USER_CENTER + "/myfriend/inviteme";
	//邀请
	public static final String VALID_INVITE_CODE =Global.USER_INVITE + "/verifyInvitationCode.jsp";
	//i投顾获取用户信息
	public static final String ITOUGU_USER_INFO=Global.ITOUGU_SERVICE+"account/service/getuserinfo.jspa?userId=$userId";
	
	SimpleRestClientUtil simpleRestClient;
	
	/**
	 * 根据用户ID查询用户信息
	 * @param userId
	 * @return "[{\"passportId\":1231231,\"passportName\":\"zhangsan\",\"email\":\"123@234.com\",\"mobile\":\"1413131313131\",\"idCard\":\"451313123131231231\",\"trueName\":\"张三\",\"nickName\":\"张小三\"}]";
	 */
	public String userInfo(String userId){
		String url =CONTROLLED_USERINFO;
		String rs = SimpleRestClientUtil.doPost(url, "passportIds="+userId,MediaType.APPLICATION_X_WWW_FORM_URLENCODE,"utf-8",true);
		//System.out.println("================"+userId+"==="+rs);
		return rs;
	}
	
	/**
	 * 验证手机号是否已经注册
	 * @param mobile
	 * @return {“resultCode”:0,”resultMsg”:”xxxxxx”}
	 */
	public String checkMobile( String mobile){
		Map<String,String>params=new HashMap<String, String>();
		params.put("mobile", mobile);
		String url = UNIQUE_MOBILE;
		
		String rs = SimpleRestClientUtil.doPost(url, transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
//		String rs=SimpleRestClientUtil.doGet(url);
		return rs;
	}
	
	/**
	 * 验证身份证号是否已经注册
	 * @param IDNumber
	 * @return {“resultCode”:0,”resultMsg”:”xxxxxx”}
	 */
	public String checkIDNumber(String IDNumber){
		String url = UNIQUE_IDNUMBER;
		Map<String,String> params=new HashMap<String, String>();
		params.put("certificateId", IDNumber);
		
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		//String rs = SimpleRestClientUtil.doGet(url);
		return rs;
	}
	
	/**
	 * 根据用户身份证号获取用户ID
	 * @param IDNumber
	 * @return {"passportId":"000822010000046691"}
	 */
	public String queryUserIdByIDNumber(String IDNumber){
		String url = GET_USER_INFO_BY_IDNUMBER;
		Map<String,String>params=new HashMap<String, String>();
		params.put("idCard", IDNumber);
		System.out.println(transMapToString(params));
		String rs=SimpleRestClientUtil.doPost(url, transMapToString(params),MediaType.APPLICATION_X_WWW_FORM_URLENCODE,"utf-8",true);
		//String rs = SimpleRestClientUtil.doGet(url);
		return rs;
	}
	
	/**
	 * 校验身份证号姓名是否一致
	 * @param idNumber
	 * @param realName
	 * @param appParam
	 * @return {“resultCode”:0,”resultMsg”:”xxxxxx”}
	 * 状态码（code）	状态码描述信息(descr)
1	用户的真实姓名包含非中文字符
0	姓名和身份证编号一致
-1	身份证编号不能为空!
-2	15位身份证编号错误!
-3	18位身份证编号错误!
-4	身份证编号必须是15位或18位有效编码!
-5	接口校验异常
-7	姓名和身份证编号不一致

	 */
	public String validIdNumberRealName(String idNumber,String realName){
		String url = VALID_IDNUMBER_REALNAME;
		Map<String,String>params=new HashMap<String, String>();
		params.put("certificateId", idNumber);
		params.put("certificateName", realName);
		params.put("appParam", "ZQT");
		System.out.println(transMapToString(params));
		String rs = SimpleRestClientUtil.doPost(url, transMapToString(params),MediaType.APPLICATION_X_WWW_FORM_URLENCODE,"utf-8",true);
		return rs;
		
	}
	
	/**
	 * 更新用户身份信息
	 * @param userId
	 * @param idNumber
	 * @param realName
	 * @return {“resultCode”:0,”resultMsg”:”成功”}
	 */
	/*public String updateUserInfo(String userId,String idNumber, String realName){
		//JSONObject json=new JSONObject();
		Map<String,String>params=new HashMap<String, String>();
		params.put("passportId", userId);
		params.put("idCard", idNumber);
		params.put("trueName", realName);
		String url = UPDATE_USER_INFO;
		System.out.println(transMapToString(params));
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		return rs;
	}*/
	
	/**
	 * 更新用户身份信息
	 * @param userId
	 * @param idNumber
	 * @param realName
	 * @return {“resultCode”:0,”resultMsg”:”成功”}
	 */
	/*public String updateUserInfo(String userId,String idNumber, String realName, String mobile){
		//JSONObject json=new JSONObject();
		Map<String,String>params=new HashMap<String, String>();
		params.put("passportId", userId);
		params.put("idCard", idNumber);
		params.put("trueName", realName);
		params.put("mobile", mobile);
		String url = UPDATE_USER_INFO;
		System.out.println(transMapToString(params));
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		return rs;
	}*/
	/**
	 * 更新用户身份信息
	 * @param userId
	 * @param idNumber
	 * @param realName
	 * @return {“resultCode”:0,”resultMsg”:”成功”}
	 */
	public String updateUserInfo(String userId,String idNumber, String realName, String mobile, String email){
		//JSONObject json=new JSONObject();
		Map<String,String>params=new HashMap<String, String>();
		params.put("passportId", userId);
		if(StringUtils.isNotBlank(idNumber)) params.put("idCard", idNumber);
		if(StringUtils.isNotBlank(realName)) params.put("trueName", realName);
		if(StringUtils.isNotBlank(mobile)) params.put("mobile", mobile);
		if(StringUtils.isNotBlank(email)) params.put("email", email);
		String url = UPDATE_USER_INFO;
		System.out.println(transMapToString(params));
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		return rs;
	}
	
	/**
	 * 更新用户身份信息
	 * @param userId
	 * @param idNumber
	 * @param realName
	 * @return {“resultCode”:0,”resultMsg”:”成功”}
	 */
	public String updateHkUserInfo(String userId,String idNumber, String realName,String englishName,String hkIdCard, String hkPassport,String mobile, String email){
		//JSONObject json=new JSONObject();
		Map<String,String>params=new HashMap<String, String>();
		params.put("passportId", userId);
		//if(StringUtils.isNotBlank(idNumber)) params.put("idCard", idNumber);
		if(StringUtils.isNotBlank(realName)) params.put("trueName", realName);
		if(StringUtils.isNotBlank(mobile)) params.put("mobile", mobile);
		if(StringUtils.isNotBlank(email)) params.put("email", email);
		if(StringUtils.isNotBlank(englishName)) params.put("EnglishName", englishName);
		if(StringUtils.isNotBlank(hkIdCard)) params.put("HKIdCard", hkIdCard);
		if(StringUtils.isNotBlank(hkPassport)) params.put("HKPassport", hkPassport);
		String url = UPDATE_HK_USER_INFO;
		System.out.println(transMapToString(params));
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		return rs;
	}

	@Deprecated
	public String updateNameAndId(String userId,String realName,String idnumber){
		JSONObject json =new JSONObject();
		json.put("userid", userId);
		json.put("idnumber", idnumber);
		json.put("realname", realName);
		String url = UPDATE_NAME_ID;
		String rs = SimpleRestClientUtil.doPost(url, json.toJSONString(), MediaType.APPLICATION_XML_TYPE, "utf-8", true);
		return rs;
	}
	
	public String regist(String mobile, String password, String ip,
			String cccode,String validcode,String clientinfo){
		JSONObject json =new JSONObject();
		json.put("mobileno", mobile);
		json.put("passwd", password);
		json.put("validcode", validcode);
		json.put("cccode", cccode);
		json.put("clientinfo", clientinfo);
		String url = REGIST;
		String rs = SimpleRestClientUtil.doPost(url, json.toJSONString(), MediaType.APPLICATION_XML_TYPE, "utf-8", true);
		return rs;
	}
	
	@Deprecated
	public String createMobileCode(String mobile,String codeType){
		JSONObject json =new JSONObject();
		json.put("mobileno", mobile);
		json.put("codetype", codeType);
		json.put("smstemplate", "尊敬的用户您好，您正在进行开户注册操作。您的验证码是$!{vercode}，将在$!{expiretime}秒后过期，请您妥善保存，并尽快使用。 证券通");
		String url = CREATE_MOBILE_CODE;
		// 已修正，改回来
		// by iriyadays 2014-9-1
		String rs = SimpleRestClientUtil.doPost(url, json.toJSONString(), MediaType.APPLICATION_XML_TYPE, "utf-8", true);
		return rs;
	}
	
	/**
	 * 发送手机验证码
	 * @param mobile
	 * @param appParam
	 * @return {“verifyCode”:0,” mobile”:””}
	 */
	public String sendMobileCode(String mobile){
		String url=CREATE_MOBILE_CODE;
		Map<String,String>params=new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("appParam", "ZQT");
		String rs = SimpleRestClientUtil.doPost(url, transMapToString(params),MediaType.APPLICATION_X_WWW_FORM_URLENCODE,"utf-8",true);	
		return rs;
	}
	
	/**
	 * 验证手机验证码
	 * @param mobile
	 * @param codeType
	 * @param valideCode
	 * @return
	 */
	public String validMobileCode(String mobile, String valideCode){
		Map<String,String>params=new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("code", valideCode);
		params.put("appParam", "ZQT");
		String url = VALID_MOBILE_CODE;
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		return rs;
	}
	
	/**
	 * 邀请码验证
	 * @param userId
	 * @param code
	 * https://sso.jrjc.local/sso/invitation/verifyInvitationCode.jsp
	 * @return
	 */
	public String verifyInvitationCode(String userId, String code){
		Map<String,String>params=new HashMap<String, String>();
		params.put("passportToId", userId);
		params.put("code", code);
		params.put("bizSource", "ZQT");
		params.put("remark", "2");
		String url = VALID_INVITE_CODE;
		String rs = SimpleRestClientUtil.doPost(url,transMapToString(params), MediaType.APPLICATION_X_WWW_FORM_URLENCODE, "utf-8", true);
		return rs;
	}
	
	public String userLogin(String userName,int type,String password){
		JSONObject json =new JSONObject();
		json.put("loginname", userName);
		json.put("nametype", type);
		json.put("passwd", password);
		String url = USER_LOGIN;
		String rs = SimpleRestClientUtil.doPost(url, json.toJSONString(), MediaType.APPLICATION_XML_TYPE, "utf-8", true);
		return rs;
	}
	/**
	 * 身份证保护_此后不可以修改
	 * @param userid
	 * @return
	 */
	public String protectIdNumber(String userid){
		JSONObject json =new JSONObject();
		json.put("userid", userid);
		String url = PROTECT_IDNUMBER;
		String rs = SimpleRestClientUtil.doPost(url, json.toJSONString(), MediaType.APPLICATION_XML_TYPE, "utf-8", true);
		return rs;
	}
	
	/**
	 * 填写邀请码
	 * @param userid
	 * @param invitetype
	 * @pe
	 * @return
	 */
	public String inviteme(String userid,Integer invitetype,String invitecode){
		JSONObject json =new JSONObject();
		json.put("userid", userid);
		json.put("invitecode", invitecode);
		json.put("invitetype", invitetype);
		String url = INVITE_ME;
		String rs = SimpleRestClientUtil.doPost(url, json.toJSONString(), MediaType.APPLICATION_XML_TYPE, "utf-8", true);
		return rs;
	}
	
	/**
	 * 获取itougu用户信息
	 * @param userId
	 * @return
	 */
	public String itouguUserInfo(String userId){
		String url=ITOUGU_USER_INFO.replaceAll("\\$userId", userId);
		System.out.println(url);
		String rs=simpleRestClient.doGet(url);
		return rs;
	}
	
	public SimpleRestClientUtil getSimpleRestClient() {
		return simpleRestClient;
	}
	public void setSimpleRestClient(SimpleRestClientUtil simpleRestClient) {
		this.simpleRestClient = simpleRestClient;
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
	
	public static void main(String[] args) {
		UserCenterInterface inter=new UserCenterInterface();
		//String rs=inter.userInfo("141216010000351844");
		//String rs1=inter.updateUserInfo("141216010000351844",null,"云飞扬",null,null);
		//System.out.println(rs1);
		//
		//String rs=inter.queryUserIdByIDNumber("130181198211217330");孙美洪,330719196804253671
		//String rs=inter.updateUserInfo("140806010018682129","110224198509010521","刘晶");
		//String rs=inter.validIdNumberRealName("130181198211217330", "米辉");
		String rs=USERINFO.replaceAll("\\$1", "12312");
		System.out.println(rs);
	}
	
}
