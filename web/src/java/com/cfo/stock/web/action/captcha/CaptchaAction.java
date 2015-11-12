package com.cfo.stock.web.action.captcha;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfo.common.captcha.processor.ImageCaptchaCreator;
import com.cfo.common.captcha.sigleton.CaptchaServiceSingleton;
import com.cfo.common.captcha.utils.Constant;
import com.cfo.common.captcha.utils.InitData;
import com.cfo.common.captcha.vo.VerifyCodeBean;
import com.cfo.stock.web.action.AbstractStockBaseAction;
/**
 * 生成验证码
 * @author yuanlong.wang
 *
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping("/stock")
public class CaptchaAction extends AbstractStockBaseAction{
	@RequestMapping(value="/captchaCode",method=RequestMethod.GET)
	@ResponseBody
	public void captcha(HttpServletRequest request, HttpServletResponse response) {
		try {
			String url_header = request.getHeader(Constant.HEADER_REFERER);
			if(url_header!=null && url_header.contains("?")) {
				int pos = url_header.indexOf("?");
				url_header = url_header.substring(0,pos);
			}
			if(log.isDebugEnabled()){
				log.debug("referer url:" + url_header);
			}
			VerifyCodeBean verifyCodeBean = InitData.getVerifyCodeBean(url_header);
			if(log.isDebugEnabled()){
				log.debug("used id:" + verifyCodeBean.getId() + " , userd typeName:" + verifyCodeBean.getName() + ", verifycode type:" + verifyCodeBean.getVerifyCodeEnum());
			}
			ImageCaptchaCreator verify = new ImageCaptchaCreator(CaptchaServiceSingleton.getInstance(verifyCodeBean),verifyCodeBean);
			verify.returnbyImage(request, response);
		} catch (Exception e) {
			log.error("captcha ERROR!",e);
		}
	}
	
}
