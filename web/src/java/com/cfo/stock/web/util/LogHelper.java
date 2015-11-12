package com.cfo.stock.web.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * hui.mi
 *
 */
public class LogHelper {
	
	//安全验证日志输出
	public static final String SAFE_VALIDATE = "SafeValidate";
    
	//安全验证日志输出
	public static final Log safevalidate = LogFactory.getLog(SAFE_VALIDATE);

	/**
	 * 打印错误信息
	 */
	public static String getExceptionTrace(Throwable e) {
		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		else
			return null;
	}
	
}
