package com.cfo.stock.web.hk.action.account;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.interceptor.HkPasswordInterceptor.NeedHkPassword;
@Controller
@RequestMapping("/stock/hk/{accountId}")
public class TestHkAction{
	/**
	 * 进入协议页面
	 * @return
	 */
	@RequestMapping(value = "/test3", method = RequestMethod.GET)
	@NeedHkPassword
	public String test3(HttpSessionWrapper session,HttpServletRequest request, Model model
			) {
		
		return "/account/accountManager";
	}
}
