package com.cfo.stock.web.action.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cfo.stock.web.services.account.AccountWebService;
import com.cfo.stock.web.verification.SafeVerification;
import com.jrj.common.cache.memcached.MemcachedCache;
import com.jrj.stocktrade.api.activity.OpenaccountActivityService;

@Controller
@RequestMapping("/openAccount/PAZQ")
public class OpenAccountPNZQAction {
	@Autowired
	private OpenaccountActivityService openaccountActivityService;
	@Autowired
	SafeVerification safeVerification;
	@Autowired
	AccountWebService accountWebService;
	
	@Autowired
	MemcachedCache memcachedCache;
	@RequestMapping(value="/index")
	public String activityIndex(@RequestParam(value="mobile" ,required=false)String mobile,Model model){
		//mobile="13261250343";
//		if(StringUtils.isBlank(mobile))
			model.addAttribute("mobile", mobile);
		
		return "stockopen/pazq";
	}
}
