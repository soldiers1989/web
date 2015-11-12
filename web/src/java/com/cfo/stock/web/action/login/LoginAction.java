package com.cfo.stock.web.action.login;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.passport.utils.Constant;
import com.cfo.stock.web.passport.utils.Utility;

/**
 * 退出
 * LoginAction
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2014年11月3日    		iriyadays     create  
 * ---------------------------------------------------------  
 * </PRE> 
 *
 */
@Controller
@RequestMapping("/stock")
public class LoginAction extends AbstractStockBaseAction{
	
	/**
	 * 退出证券通和passport
	 * @param session
	 * @param attributes
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletResponse response, HttpSessionWrapper session,RedirectAttributes attributes){
		// 删除session
		session.deleteSession();
		
		// 删除passport的cookie
		Utility.delCookie(Constant.ZQT_UID, Constant.ZQT_DOMAIN, response);
		Utility.delCookie(Constant.ZQT_MD5, Constant.ZQT_DOMAIN, response);
		// FIXME: 应该passport去删除cookie
		Utility.delCookie("JRJ.SSOPASSPORT", "jrj.com.cn", response);
		
		String url = Global.PASSPORT_LOGIN_URL + "?ReturnURL="+Constant.ZQT_SERVER+"/stock";
		return "redirect:"+url;
	}
	
	@RequestMapping(value="/userInfo", produces="application/x-javascript;charset=UTF-8")
	@ResponseBody
	public String userInfo(HttpSessionWrapper session){
		UserInfoVo userInfo = getSelfUserInfo(session);
		JSONObject json=new JSONObject();
		if(userInfo == null){
			return "var basicUserInfo=" + json.toJSONString();
		}
		json.put("headImage", "/stock/images/sample-1.jpg");
		json.put("isAdviser", "1");
		json.put("type", "2");
		json.put("userId", userInfo.getUserId());
		json.put("userName", userInfo.getUserName());
		return "var basicUserInfo=" + json.toJSONString();
	}
	
	@RequestMapping(value="/messageNum", produces="application/x-javascript;charset=UTF-8")
	@ResponseBody
	public String messageNum(
			@RequestParam(value="callback",required=false) String callBack){
		JSONObject json=new JSONObject();
		json.put("t_1", 0);
		json.put("t_11", 0);
		json.put("t_2", 0);
		json.put("t_3", 0);
		json.put("t_4", 14);
		json.put("t_8", 0);
		json.put("t_9", 3);
		if(StringUtils.isBlank(callBack)){
			return json.toJSONString();
		}else{
			return callBack+"("+json.toJSONString()+")";
		}
		
	}
}
