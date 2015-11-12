package com.cfo.stock.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfo.common.session.utils.URLUtils;
import com.jrj.stocktrade.api.pub.SystemStatusQueryService;

/**
 *公共action
 *
 */
@Controller
@RequestMapping("/stock")

public class PubAction  extends AbstractStockBaseAction{

	@Autowired
	SystemStatusQueryService systemStatusQueryService;
	
	@RequestMapping(value="/checkBrokerSys",method=RequestMethod.GET)
	@ResponseBody
	public String execute(
			@RequestParam(value="brokerId" ,defaultValue="")String brokerId){
		brokerId = URLUtils.ParamterFilter(brokerId, '\0');
		Boolean flag  = systemStatusQueryService.isSystemAviable(brokerId);
		if(flag){
			return "1";
		}else{
			return "0";
		}
	}
}
