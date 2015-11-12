package com.cfo.stock.web.action.bind;



import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.common.vo.UserInfoVo;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.services.account.AccountWebService;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.account.vo.BindInfo;
import com.jrj.stocktrade.api.account.vo.Broker;
import com.jrj.stocktrade.api.exception.ServiceException;
import com.jrj.stocktrade.api.opstatus.StockAccountStatusService;
import com.jrj.stocktrade.api.opstatus.vo.StockAccountStatus;
@Controller
@RequestMapping("/stock")

public class BindAction  extends AbstractStockBaseAction{
	@Autowired
	AccountService accountService;
	@Autowired
	AccountWebService accountWebService;
	
	@Autowired
	StockAccountStatusService accountStatusService;
	
	@RequestMapping(value="/bindStep1",method=RequestMethod.GET)
	@NeedLogin
	public String stepOne(@RequestParam(value="callbackurl" ,defaultValue="")String callbackurl,
			HttpSessionWrapper session,
			Model model){
		
		//get loginuser form session
		String userId=this.getSelfUserId(session);
		
		//判断回调地址是否为空，回调地址是外部链接过来购买
		if(StringUtils.isNotBlank(callbackurl)){
			model.addAttribute("callbackurl", URLUtils.urlEncode(callbackurl));
		}
		String viewName=null;
		//获取证券公司列表
		try {
			List<Broker> brokerList = accountService.queryBrokers(userId);
			viewName="/bind/step1";
			model.addAttribute("brokerList", brokerList);
		} catch (ServiceException e) {
			log.error(e.getErrorInfo());
			model.addAttribute("errMsg", "网络不给力哦");
		}	
		return viewName;
	}
	


	@RequestMapping(value="/bindRS",method=RequestMethod.GET)
	@NeedLogin
	public String getBindRS(
			@RequestParam(value="brokerId" ,defaultValue="")String brokerId,
			@RequestParam(value = "redirect", defaultValue = "") String redirect,
			HttpSessionWrapper session,
			Model model){
		
		//get loginuser form session
		String userId=this.getSelfUserId(session);
		if(StringUtils.isNotBlank(redirect)){
			model.addAttribute("redirect", redirect);
		}
		if("".equals(brokerId)){
			return "bind/bindRS_F";
		}
		
		UserInfoVo userInfo = getSelfUserInfo(session);
		StockAccountStatus status=null;
		try {
			if(userInfo != null){
				status = accountStatusService.queryStockOpenStatus(userId, userInfo.getRealName(), userInfo.getIdNumber(), brokerId);
			}
		} catch (ServiceException e1) {
			log.error("exception",e1);
		}
		
		if(status != null){
			model.addAttribute("type", status.getType().type);
			if(status.getAccountStatus()!=null){
				model.addAttribute("status", status.getAccountStatus().getValue());
			}
		}
		
		model.addAttribute("qsName",brokerHelper.getBrokerName(brokerId));
		model.addAttribute("qsFlag",brokerId);
		String viewName=null;
		try {
			BindInfo  info = accountService.getBindInfo(userId, brokerId);
			if(info !=null){
				viewName= "bind/bindRS_S";
			}else{
				viewName= "bind/bindRS_F";
			}
		} catch (ServiceException e) {
			log.error(e.getErrorInfo());
			viewName="bind/bindRS_F";
			model.addAttribute("errMsg", e.getErrorInfo());
		}
		return viewName;
	}
	
	@RequestMapping(value="/binding",method=RequestMethod.GET)
	@NeedLogin
	public String binding(){
		
		String viewName="bind/binding";
		return viewName;
	}
	@RequestMapping(value="/loadPCBind",method=RequestMethod.GET)
	@NeedLogin
	public String loadPCBind(
			@RequestParam(value="brokerId" ,defaultValue="")String brokerId,
			@RequestParam(value="p" ,defaultValue="")String p,
			@RequestParam(value="s" ,defaultValue="")String s,
			HttpSessionWrapper session,
			Model model){
			
		this.assembleOpFrom(session);
		String userId = getSelfUserId(session);
		
		this.log.debug("用户：" + userId + "授权回调 ， 回调时间：" + new Date() );
		String failView="bindBack/fail";
		//回传信息缺少，跳转到错误也，错误提示(中信认证信息不全，无法进行绑定操作)
		if("".equals(p) || "".equals(brokerId) ||"".equals(s)){
			model.addAttribute("msg","认证信息不全，无法通过绑定");
			return failView;
		}
		//查询当前登录用户绑定券商的结果：（主要是看是否绑定过）
		UserInfoVo userInfo = getSelfUserInfo(session);
		StockAccountStatus status=null;
		try {
			status = accountStatusService.queryStockOpenStatus(userId, userInfo.getRealName(), userInfo.getIdNumber(), brokerId);
		} catch (ServiceException e1) {
			log.error("exception",e1);
		}
		
		if(status != null){
			model.addAttribute("type", status.getType().type);
		}
		
		try{
			List<Broker> bindBrokers = accountService.queryBindedBrokers(userId);
			if(bindBrokers !=null && bindBrokers.size() >0){
				// 如果该券商已经被绑定，提示已绑定过
				for(Broker borker : bindBrokers) {
					if(borker.getBrokerId() .equals(brokerId)) {
						model.addAttribute("msg","您已经绑定了该券商");
						return failView;
					}
				}
			}
		}catch(ServiceException e){
			log.error(e.getErrorInfo());
			model.addAttribute("msg",e.getErrorInfo());
			return failView;
		}
		//将中信回传的参数写入map ，作为调用hs的参数
		Map<String,Object	> data = new HashMap<String, Object>();
		data.put("p", p);
		data.put("s", s);
		BindInfo info;
		String viewName=null;
		try {
			info = accountService.bindReceive(userId, brokerId, data);
			if(info !=null){
				viewName="bindBack/success";
			}else{
				viewName=failView;
			}
		} catch (ServiceException e) {
			log.error(e.getErrorInfo(), e);
			viewName=failView;
			model.addAttribute("msg",e.getErrorInfo());
			return viewName;
		}
		
		return viewName;
		
	}
	@RequestMapping(value="/loadMobileBind",method=RequestMethod.GET)
	@ResponseBody	
	public String loadMobileBind(){
		return "";
	}
}
