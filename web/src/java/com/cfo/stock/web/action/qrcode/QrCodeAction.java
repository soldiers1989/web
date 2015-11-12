package com.cfo.stock.web.action.qrcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.common.session.constant.SessionConfig;
import com.cfo.stock.web.action.qrcode.QrConfig.QrCodeBean;
import com.cfo.stock.web.global.Global;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 券商APP二维码 QrCodeAction
 * 
 * @history <PRE>
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * v1.0         2015年1月26日    	yuanlong.wang     create  
 * ---------------------------------------------------------
 * </PRE>
 * 
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping("/stock/qrCode")
public class QrCodeAction  {
		
	@RequestMapping(value = "/{brokerId}/open", method = RequestMethod.GET)
	@ResponseBody
	public void qrCodeOpen(@PathVariable("brokerId") String brokerId,
			@RequestParam(value = "type", defaultValue = "1") int type,
			HttpServletRequest request, HttpServletResponse response,HttpSessionWrapper session) {
		try {
			if(brokerId==null)return;//不存在的券商
			String loginStatus = (String) session.getAttribute(AttributeKeys.LOGIN);
			String userId=null;
			if ( SessionConfig.LOGIN_OK.equals(loginStatus)) {
				userId=session.getAttribute(AttributeKeys.USER_ID, String.class);
			}
			BufferedImage image =null;
			if(userId==null){
				QrCodeBean bean=QrConfig.getQrConfig(brokerId, QrConfig.OPNE_TYPE);
				image=createQrImage(bean.getAppUrl(),bean, brokerId);
			}else{
				String key=brokerId+"_"+type+"_"+userId;
				image= MatrixToImageWriter.getQrImageCache(key);
				if (image == null) {
					String url = Global.ZQT_SERVER+"/stock/qrScan/"+brokerId+"/"+type+"/"+userId+"/open.jspa?sign="+QrConfig.sign(key);
					QrCodeBean bean=QrConfig.getQrConfig(brokerId, QrConfig.OPNE_TYPE);
					//创建二维码
					image=createQrImage(url,bean, brokerId);
					//设置ehcacahe缓存
					MatrixToImageWriter.setQrImageCache(image, "png",key);
				}
			}
			if (!ImageIO.write(image, "png", response.getOutputStream())) {
				throw new IOException("Could not write an image of format png");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage createQrImage(String url,QrCodeBean bean,String broker) throws WriterException{
		Map<EncodeHintType, Object> hints =MatrixToImageWriter.getDecodeHintType();
		BitMatrix bitMatrix = new MultiFormatWriter().encode(url,
				BarcodeFormat.QR_CODE, bean.getQrWidth(), bean.getQrHeight(), hints);
		if(!StringUtils.isEmpty(bean.getLogo())){
			return MatrixToImageWriter.toBufferedImage(bitMatrix,new File(getClass().getClassLoader()
					.getResource(bean.getLogo()).getFile()));
		}else{
			return MatrixToImageWriter.toBufferedImage(bitMatrix,null);
		}
	}
}
