package com.cfo.stock.web.action;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *首页
 * @author yuanlong.wang
 *
 */
@Controller
@RequestMapping("/stock")
public class ErrorAction  extends AbstractStockBaseAction{

	@RequestMapping(value="/error",method=RequestMethod.GET)
	public String execute(){
		return "/error";
	}
	
	@RequestMapping(value="/sysError",method=RequestMethod.GET)
	public String sysError(){
		return "/unavailable";
	}
	
	@RequestMapping(value="/noMacError",method=RequestMethod.GET)
	public String macError(){
		return "/noMacError";
	}
}
