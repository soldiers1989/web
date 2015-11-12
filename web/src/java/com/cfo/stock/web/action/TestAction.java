package com.cfo.stock.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *首页
 * @author yuanlong.wang
 *
 */
@Controller
@RequestMapping("/stock")
public class TestAction  extends AbstractStockBaseAction{

	@RequestMapping(value="/test",method=RequestMethod.GET)
	public String execute(
			@RequestParam(value="token" ,defaultValue="")String token,
			Model model){
		String userId="";
		String brokerId="ZXZQ";
		String viewName="/testIndex";
		model.addAttribute("token", token);
		return viewName;
	}
	
	@RequestMapping(value="/newTest",method=RequestMethod.GET)
	public String getList(){
		ModelAndView model = new ModelAndView();
		String viewName="/trade/buyStep1";
		return viewName;
	}

}
