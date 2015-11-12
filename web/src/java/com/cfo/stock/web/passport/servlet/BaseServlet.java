/**
 * 
 */
package com.cfo.stock.web.passport.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfo.stock.web.global.Global;
import com.cfo.stock.web.passport.utils.Constant;
import com.cfo.stock.web.util.ActionUtils;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**  
 * 
 * @author yuanlong.wang 2013-1-4 
 *  
 */

public class BaseServlet extends HttpServlet  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9098487433960070063L;
	protected Log log=LogFactory.getLog(getClass());
	protected static final int CHECK_COUNT = 3;
	protected String ERROR_URL = Global.PASSPORT_LOGIN_URL;
	/**
	 * 检查重试次数
	 * @param resp
	 * @param backUrl
	 * @param identity
	 * @return
	 */
	protected boolean checkRequestCount(HttpServletResponse resp, String backUrl, String identity) {
		
		GeneralCacheAdministrator admin = Constant.getAdmin();
		String oskey = identity + "." + backUrl;
		try {
			Integer count = (Integer) admin.getFromCache(oskey, Constant.REFRESH_PERIOD);
			if (count.intValue() < CHECK_COUNT) {
				admin.putInCache(oskey, new Integer(count.intValue() + 1));
			} else {
				admin.removeEntry(oskey);
	
				return false;
			}
		} catch (NeedsRefreshException nre) {
			
			admin.putInCache(oskey, new Integer(1));
		} catch (Exception e) {
			admin.cancelUpdate(oskey);
		}
		return true;
	}
	
	
	/**
	 * 跳转到错误页面
	 * @param errMsg
	 * @param resp
	 */
	protected void toErrorPage(String errMsg,HttpServletResponse resp){
		try {
			if (log.isDebugEnabled()){
				log.debug("To Error page! msg:"+errMsg);
			}
			resp.sendRedirect(ERROR_URL);
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 最终跳转
	 * @param resp
	 * @param backUrl
	 */
	protected void sendRedirect(HttpServletResponse resp, String backUrl) {
		//P3P解决跨域
		resp.setHeader("P3P", "CP=\"CAO PSA OUR\"");
		try {
			resp.sendRedirect(backUrl);
//				resp.flushBuffer();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
