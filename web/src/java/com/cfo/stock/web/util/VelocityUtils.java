/**
 * 
 */
package com.cfo.stock.web.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**  
 *   
 *   
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * 1.0           2012-5-25    yuanlong.wang     create  
 * ---------------------------------------------------------  
 * </PRE>  
 *  
 */

public class VelocityUtils implements Observer {
	private static final VelocityUtils instance = new VelocityUtils();
	private VelocityEngine velocityEngine;
Logger logger=Logger.getLogger(getClass());
	public static VelocityUtils getInstance() {
		return instance;
	}
	
	private VelocityUtils(){};

	
	/**
	 * 根据模板生成字符串，返回
	 * 
	 * @param vm
	 * @param context
	 * @return
	 * @throws ShareAppException
	 */
	public String mergeToString(String vm, Map context)  {

		try {
			context = fillContext(context);
			return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, vm, context);
		} catch (VelocityException e) {
			String msg = getClass().getName() + "#mergeToString VelocityException";
			return "";
		}
	}
	/**
	 * 用velocity输出排名
	 * 
	 * @param vm
	 *            模版文件
	 * @param page
	 *            页面文件
	 * @param context
	 * 			  Map数据信息
	 */
	public void exportByVelocity(String vm, String page,
			Map<String, Object> context) {
		Writer writer = null;
		try {
			
			writer = getWriter(page);
			VelocityEngineUtils.mergeTemplate(velocityEngine, vm,
					context, writer);
			writer.flush();
		} catch (Exception e) {
			
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
				}
		}
	}
	
	private Writer getWriter(String name) throws IOException {
		File dest = new File(name);
		if (!dest.getParentFile().exists())
			dest.getParentFile().mkdirs();
		return new PrintWriter(name, "UTF-8");
	}
	@SuppressWarnings("unchecked")
	private Map fillContext(Map context) {
		context =  (context == null) || (context.isEmpty()) ? new HashMap() : context;
		return context;
	}

	public void update(Observable o, Object obj) {
		velocityEngine = (VelocityEngine) obj;
	}
	
	public String mergeStringToString(String str,Map context){
		StringWriter writer = new StringWriter(); 
		context = fillContext(context);
		VelocityContext velocityContext=new VelocityContext(context);
		try {
			velocityEngine.evaluate(velocityContext, writer, "", str);
			return writer.toString();
		}catch (Exception ex) {
			  logger.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
			  return null;
		}
	}
}
