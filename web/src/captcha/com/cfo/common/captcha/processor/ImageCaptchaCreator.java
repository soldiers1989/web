package com.cfo.common.captcha.processor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cfo.common.captcha.enums.CaptchaType;
import com.cfo.common.captcha.service.CustomImageCaptchaService;
import com.cfo.common.captcha.sigleton.CaptchaMemcacheSigleton;
import com.cfo.common.captcha.utils.Constant;
import com.cfo.common.captcha.vo.VerifyCodeBean;
import com.jrj.common.utils.PropertyManager;
import com.octo.captcha.Captcha;

/** 
 * 创建验证码
 * @author Fu Yi
 *
 */
public class ImageCaptchaCreator {
	private static Logger log = Logger.getLogger("VerifycodeLog");
	private CustomImageCaptchaService service;
	private VerifyCodeBean verifyCodeBean;
	public ImageCaptchaCreator(CustomImageCaptchaService service,VerifyCodeBean verifyCodeBean) {
		this.service = service;
		this.verifyCodeBean = verifyCodeBean;
	}

	/** 
	 * 返回验证码图片
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void returnbyImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			IOException {
		ServletOutputStream out = response.getOutputStream();
		//ByteArrayOutputStream out = new ByteArrayOutputStream();
		if(service != null) {
			setResponsePara(response);
			// create uuid
			String uid = UUID.randomUUID().toString();
			CaptchaType type = verifyCodeBean.getVerifyCodeEnum();
			uid = uid + "-" + type.ordinal();
			log.info("created uid:" + uid);
			// return cookie
			// Added by Cao.Qiang
		    // Add date：2012-05-22
			// 修改验证码功能，使验证码cookie的域名支持多个
			String sysid = request.getParameter("sysid");
			response.addCookie(addCookies(uid, sysid));
			Captcha captcha = service.getCaptchaForID(uid);
			// create the image with the text
			BufferedImage bi = service.getImageChallengeForID(captcha);
			// save image to tt
			saveVerifyCodeToTT(uid,captcha);
			log.info("验证码生成成功！");
			// write the data out
			ImageIO.write(bi, "jpg", out);
		} else {
			log.info("验证码生成失败！");
		}
		try {
			out.flush();
		} finally {
			out.close();
		}
	}
	
	/** 
	 * 将验证码和其ID一起存入TT
	 * @param uuid
	 * @param captcha
	 */
	private void saveVerifyCodeToTT(String uuid,Captcha captcha) {
		CaptchaMemcacheSigleton.getInstance().add(Constant.SESSION_VERIFYCODE + uuid, captcha,new Date(System.currentTimeMillis() + 10*60*1000));
	}
	
	/** 
	 * 将验证码ID加入到cookie中
	 * @param uid
	 * @return
	 */
	private Cookie addCookies(String uid) {
		Cookie cookie = new Cookie(Constant.VERIFYCODE_ID,uid);
		cookie.setPath("/");
		cookie.setDomain(PropertyManager.getString(Constant.COOKIE_DOMAIN));
		return cookie;
	}
	
	private Cookie addCookies(String uid, String sysid) {
		String key = "";
		if(StringUtils.isBlank(sysid)){
			key = Constant.COOKIE_DOMAIN;
		}else{
			key = Constant.COOKIE_DOMAIN + "." + sysid;
		}
		
		Cookie cookie = new Cookie(Constant.VERIFYCODE_ID,uid);
		cookie.setPath("/");
		cookie.setDomain(PropertyManager.getString(key));
		return cookie;
	}
	
	private void setResponsePara(HttpServletResponse response) {
		// Set to expire far in the past.
		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control",
				"post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// return a jpeg
		response.setContentType("image/jpeg");
	}

}
