package com.cfo.stock.web.action.account;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.stock.web.action.AbstractStockBaseAction;
/**
 * 用户授权
 * @author hui.mi
 *
 */
@Controller
@RequestMapping("/stock")
public class ProtocolAction extends AbstractStockBaseAction{
	/**
	 * 进入协议页面
	 * @return
	 */
	@RequestMapping(value = "/dlgProtocol", method = RequestMethod.GET)
	public String dlgProtocol(HttpSessionWrapper session,HttpServletRequest request, Model model,
			@RequestParam(value = "brokerId", defaultValue = "") String brokerId
			) {
		model.addAttribute("brokerId", brokerId);
		return "/account/dlgProtocol";
	}
}
