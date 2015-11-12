package com.cfo.stock.web.action.bind;




import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.cfo.stock.web.interceptor.NeedLoginInterceptor.NeedLogin;
import com.cfo.stock.web.services.account.AccountWebService;
import com.jrj.stocktrade.api.account.AccountService;
import com.jrj.stocktrade.api.common.BrokerId;
@Controller
@RequestMapping("/stock/bind")
@NeedLogin

public class BindCallBackAction  extends AbstractStockBaseAction{
	@Autowired
	AccountService accountService;
	@Autowired
	AccountWebService accountWebService;
	@RequestMapping(value="/{brokerId}",method=RequestMethod.GET)
	/**
	 * 
	 * @param token  中信回传的用户id
	 * @param fundid 资金帐号
	 * @param clientId 客户号
	 * @param branchNo 营业部号
	 * @param sysnode 
	 * @param sign 签名
	 * @return
	 */
	public String bindZX(
			@PathVariable("brokerId") String brokerId,
			@RequestParam(value="p" ,defaultValue="")String p,
			@RequestParam(value="s" ,defaultValue="")String sign,
			HttpServletRequest request,
			Model model,
			RedirectAttributes attributes){
		Map<String, String[]> pmap = request.getParameterMap();
		// 不能过滤URL
		//sign = URLUtils.ParamterFilter(sign, '\0');
		//p = URLUtils.ParamterFilter(p, '\0');
		String header=request.getHeader("user-agent");
		if(header.indexOf("Mozilla")==-1){
			String viewName="redirect:/stock/loadMobileBind.jspa";
			attributes.addAttribute("p", p);
			attributes.addAttribute("s", sign);
			return viewName;
		}else{
			String viewName="redirect:/stock/loadPCBind.jspa";
			attributes.addAttribute("brokerId", brokerId);
			if(BrokerId.CITIC_SECURITIES.equals(brokerId)||
				BrokerId.CNHT_SECURITIES.equals(brokerId)||
				BrokerId.CJZQ_SECURITIES.equals(brokerId)||
				BrokerId.ZJZQ_SECURITIES.equals(brokerId)){
					attributes.addAttribute("p", p);
					attributes.addAttribute("s", sign);
			}else if(BrokerId.ZSZQ_SECURITIES.equals(brokerId)){
					p=pmap.get("data")[0];
					sign=pmap.get("sign")[0];
					attributes.addAttribute("p", p);
					attributes.addAttribute("s", sign);
			}
			
			return viewName;
		}
		
	}

}
