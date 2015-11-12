package com.cfo.stock.web.action.user;


import java.io.IOException;
import java.util.Map;








import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.utils.CookieUtils;
import com.cfo.common.session.utils.URLUtils;
import com.cfo.stock.web.action.AbstractStockBaseAction;
import com.jrj.common.utils.PropertyManager;

@Controller
@RequestMapping("/stock")

public class NavInviteAction extends AbstractStockBaseAction {

	private String from;

	private static String IC = "_ic";
	
	@RequestMapping(value="/invite",method=RequestMethod.GET)
	public String invite(@RequestParam(value="ic" ,defaultValue="")String ic,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSessionWrapper session,
			RedirectAttributes attributes
			) throws IOException {
		if (log.isDebugEnabled()) {
			log.info("get invitecode in inviteaction:" + ic);
		}
		if (StringUtils.isNotBlank(ic)) {
			ic = URLUtils.ParamterFilter(ic,'\0');
			CookieUtils.updateCookieByName(response,IC,ic);
			session.setAttribute(AttributeKeys.INVITE_CODE, ic);
		}else{
			String inner_ic = CookieUtils.getCookieByName(request,IC);
			if(StringUtils.isNotBlank(inner_ic)){
				session.setAttribute(AttributeKeys.INVITE_CODE, inner_ic);
			}else{
				Object session_ic = session.getAttribute(AttributeKeys.INVITE_CODE);
				if(session_ic!=null){
					CookieUtils.updateCookieByName(response,IC,(String)session_ic);
				}
			}
		}
		 String address = PropertyManager.getString("invite_redirect_address");
		if (log.isDebugEnabled()) {
			log.debug("address--------->" + address);
		}
		if (StringUtils.isNotBlank(from)) {
			address += address.indexOf("?") > 0 ? "&" : "?" + "from=" + from;
		}
		//response.sendRedirect(address);
		return "redirect:"+address;
	}


	public void setFrom(String from) {
		this.from = from;
	}
}
