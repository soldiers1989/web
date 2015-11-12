package com.cfo.stock.web.action.qrcode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cfo.common.session.utils.Md5Utils;

public class QrConfig {

	public final static String OPNE_TYPE = "open";

	public final static List<QrCodeBean> qrCodeBeans = new ArrayList<QrConfig.QrCodeBean>();

	static {
		initQrCode(QrConfig.class.getResource("/config/QrCodeConfig.xml")
				.getPath());
	}
	private static void initQrCode(String path) {
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(path);
			Element apps = doc.getRootElement();
			Iterator<Element> i = apps.elementIterator("app");
			Element foo;
			while (i.hasNext()) {
				foo = (Element) i.next();
				String type = foo.elementText("type");// 类型
				String broker = foo.elementText("broker");
				String name = foo.elementText("name");
				String appUrl = foo.elementText("appUrl");
				String logo = foo.elementText("logo");
				int qrWidth = Integer.parseInt(foo.elementText("qrWidth"));
				int qrHeight = Integer.parseInt(foo.elementText("qrHeight"));
				QrCodeBean bean = new QrCodeBean(type, broker, name, appUrl,
						logo, qrWidth, qrHeight);
				qrCodeBeans.add(bean);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static QrCodeBean getQrConfig(String broker, String type) {
		for(QrCodeBean bean:qrCodeBeans){
			if(bean.getBroker().equals(broker)&&bean.getType().equals(type))
				return bean;
		}
		return null;
	}

	private static final String QR_SIGN="zm4d$afdRw";
	/**
	 * 简易签名
	 * @param key
	 * @return
	 */
	public static String sign(String key){
		return Md5Utils.getMD5String(QR_SIGN+key);
	}
	
	static class QrCodeBean {
		private String type;
		private String broker;
		private String name;
		private String appUrl;
		private String logo;
		private int qrWidth;
		private int qrHeight;

		public QrCodeBean(String type, String broker, String name,
				String appUrl, String logo, int qrWidth, int qrHeight) {
			super();
			this.type = type;
			this.broker = broker;
			this.name = name;
			this.appUrl = appUrl;
			this.logo = logo;
			this.qrWidth = qrWidth;
			this.qrHeight = qrHeight;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getBroker() {
			return broker;
		}

		public void setBroker(String broker) {
			this.broker = broker;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAppUrl() {
			return appUrl;
		}

		public void setAppUrl(String appUrl) {
			this.appUrl = appUrl;
		}

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public int getQrWidth() {
			return qrWidth;
		}

		public void setQrWidth(int qrWidth) {
			this.qrWidth = qrWidth;
		}

		public int getQrHeight() {
			return qrHeight;
		}

		public void setQrHeight(int qrHeight) {
			this.qrHeight = qrHeight;
		}

	}
}
