package com.cfo.stock.web.action.activity;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.enums.ActivityBroker;
import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.util.ActionUtils;
import com.cfo.stock.web.verification.SafeVerification;
import com.jrj.common.cache.memcached.MemcachedCache;
import com.jrj.common.utils.DateUtil;
import com.jrj.stocktrade.api.activity.OpenaccountActivityService;
import com.jrj.stocktrade.api.activity.vo.OpenaccountActivity;


/**  
 * @Title: OpenaccountActivityAction.java 
 * @Package com.cfo.stock.web.action.activity 
 * @Copyright: Copyright(c)2004-2014
 * @Company:中国金融在线集团

 * @Description: 开户活动action
 * @author haijun.sun(孙海军)
 * @mail haijun.sun@jrj.com.cn
 * @date 2015年4月7日 下午4:44:22 
 * @version V1.0  
 */
@Controller
@RequestMapping("/openAccount/activity")
public class OpenaccountActivityAction {

	@Autowired
	private OpenaccountActivityService openaccountActivityService;
	@Autowired
	SafeVerification safeVerification;
	@Autowired
	AccountWebService accountWebService;
	
	@Autowired
	MemcachedCache memcachedCache;
	
	/**
	 * @Title: activityIndex 
	 * @Description: 长江活动入口页面
	 * @param  qdcode 渠道码
	 * @param  yqcode 邀请码
	 * @return String    返回类型 
	 * @throws 
	 * @author haijun.sun(孙海军)
	 * @mail haijun.sun@jrj.com.cn
	 * @date 2015年4月8日 下午7:25:11
	 */
	@RequestMapping(value="/index2")
	public String activityIndex(@RequestParam(value="qdcode" ,required=false)String qdcode,@RequestParam(value="yqcode" ,required=false)String yqcode,Model model){
		if(qdcode!=null)
			model.addAttribute("qdcode", qdcode);
		if(yqcode!=null)
			model.addAttribute("yqcode", yqcode);
		
		return "/activity/index";
	}
	
	/**
	 * @Title: activityDownload 
	 * @Description: 长江H5活动结束下载页面
	 * @param  mobile 手机号码
	 * @throws 
	 * @author haijun.sun(孙海军)
	 * @mail haijun.sun@jrj.com.cn
	 * @date 2015年4月8日 下午7:25:11
	 */
	@RequestMapping(value="/index")
	public String activityDownload(HttpServletRequest request,@RequestParam(value="mobile" ,required=false)String mobile,@RequestParam(value="qdcode" ,required=false)String qdcode,Model model,RedirectAttributes attributes){
		
		/*String ip = ActionUtils.getRemoteIpAdress(request);
		String key = "cjh5_"+ip;
		if(memcachedCache.get(key)==null){//如果ip不存在memcachedCache中则直接进行活动主页
			attributes.addAttribute("qdcode", qdcode);
			return "redirect:/openAccount/activity/index.jspa";
		}
		if(mobile!=null)
			model.addAttribute("mobile", mobile);*/
		
		return "/activity/download";
	}
	
	/**
	 * @Title: addinfo 
	 * @Description: 参与长江H5活动
	 * @param  mobile 手机号码
	 * @param  vcode 验证码
	 * @param  qdcode 渠道码
	 * @param  yqcode 邀请码
	 * @throws 
	 * @author haijun.sun(孙海军)
	 * @mail haijun.sun@jrj.com.cn
	 * @date 2015年4月8日 下午7:25:11
	 */
	@RequestMapping(value = "/addinfo", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String addinfo(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			@RequestParam(value = "vcode", defaultValue = "") String vcode,
			@RequestParam(value = "terminalType", defaultValue = "") String terminalType,
			@RequestParam(value = "qdcode", defaultValue = "") String qdcode,
			@RequestParam(value = "yqcode", defaultValue = "") String yqcode,HttpSessionWrapper session){
		
		JSONObject json = new JSONObject();
		// 验证手机号验证码 start 不验证可以注释掉
		String strVa = accountWebService.validMobileCode(mobile,vcode);
		if(StringUtils.isEmpty(strVa)){
			json.put("retcode", "-1");
			json.put("msg", "您输入的验证码不正确!");
			return json.toJSONString();
		}else{
			JSONObject upRealJson = JSONObject.parseObject(strVa);
			if(!"0".equals(upRealJson.getString("resultCode"))){
				json.put("retcode", "-1");
				json.put("msg", upRealJson.getString("resultMsg"));
				return json.toJSONString();
			}
		}
		// 验证手机号验证码 end
		
		OpenaccountActivity bean = openaccountActivityService.selectByMobile(mobile, ActivityBroker.CJZQ.key);
		if(bean==null){//如果有记录则不处理
			//插入活动数据
			OpenaccountActivity record = new OpenaccountActivity();
			record.setMobile(mobile);
			record.setYqcode(yqcode);
			record.setBrokerId(ActivityBroker.CJZQ.key);
			record.setTerminalType(terminalType);
			record.setQdcode(qdcode);
			record.setStatus(1);
			openaccountActivityService.insert(record);
		}
		
		//ip存入memcachedCache中
		String ip = ActionUtils.getRemoteIpAdress(request);
		String key = "cjh5_"+ip;
		memcachedCache.set(key, ip, DateUtil.add(new Date(), Calendar.MINUTE, 10));
		
		json.put("retcode", "0");
		json.put("msg", "提交信息成功!");
		return json.toString();
	}
	
	/**
	 * @Title: modfiytInfo 
	 * @Description: 激活参与长江H5活动状态为有效
	 * @param  mobile 手机号码
	 * @throws 
	 * @author haijun.sun(孙海军)
	 * @mail haijun.sun@jrj.com.cn
	 * @date 2015年4月8日 下午7:25:11
	 */
	@RequestMapping(value = "/modfiytInfo", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String modfiytInfo(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "mobile", defaultValue = "") String mobile){
		
		JSONObject json = new JSONObject();
		
		OpenaccountActivity bean = openaccountActivityService.selectByMobile(mobile, ActivityBroker.CJZQ.key);
		if(bean!=null&&bean.getStatus()==1){//记录不为空，同时为未激活的状态
			//插入活动数据
			OpenaccountActivity record = new OpenaccountActivity();
			record.setMobile(mobile);
			record.setStatus(2);
			record.setBrokerId(ActivityBroker.CJZQ.key);
			openaccountActivityService.updateByMobile(record);
		}
		json.put("retcode", "0");
		json.put("msg", "更新活动状态成功!");
		
		return json.toString();
	}
	
	
	
	/** 
	 * 获取验证码
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/getCode", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String getMobileCode(
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			HttpServletRequest request) {
        //mobile 参数过滤
		mobile = URLUtils.ParamterFilter(mobile, '\0');
		//获取客户端ip
		String ip = ActionUtils.getRemoteIpAdress(request);
		//验证接口防刷stock.phone.rules
		boolean flag = safeVerification.safeValidate("phone:"+mobile+"|ip:"+ip,"stock_phone_rules","长江开户H5发送手机验证码");
		//验证通过为true
		if(flag){
			String rs = accountWebService.getMobileCode(mobile);
			return rs;
		}else{
			return "{\"status\":1 ,\"retcode\":10217,\"resultMsg\":\"不能频繁获取验证码\"}";
		}
	}
}
