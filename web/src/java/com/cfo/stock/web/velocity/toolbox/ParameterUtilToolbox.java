package com.cfo.stock.web.velocity.toolbox;

import java.text.DecimalFormat;

import javax.servlet.ServletRequest;

import org.springframework.web.util.HtmlUtils;

/**
 * jsp获取参数
 * @author 陈延民
 *
 */
public class ParameterUtilToolbox 
{
	/**
	 * 获取字符型参数，若输入字符串为null，则返回设定的默认值
	 * @param str 输入字符串
	 * @param defaults 默认值 
	 * @return 字符串参数
	 */
    public static String getStringParameter(String str, String defaults)
    {
    	 if(str == null){
             return HtmlUtils.htmlEscape(defaults);
         }else{
             return HtmlUtils.htmlEscape(str);
         }
    }
   
    /**
     * 获取int参数，若输入字符串为null，则返回设定的默认值
     * @param str 输入字符串
     * @param defaults 默认值
     * @return int参数
     */
    public static int getIntParameter(String str, int defaults)
    {
        if(str == null)
        {
            return defaults;
        }        
        try
        {
            return Integer.parseInt(str);
        }
        catch(Exception e)
        {
            return defaults;
        }
    }
    
    /**
     * 获取long型参数，若输入字符串为null，则返回设定的默认值
     * @param str 输入字符串
     * @param defaults 默认值
     * @return long参数
     */
    public static long getLongParameter(String str, long defaults)
    {
        if(str == null)
        {
            return defaults;
        }        
        try
        {
            return Long.parseLong(str);
        }
        catch(Exception e)
        {
            return defaults;
        }
    }
    
    /**
     * 获取double型参数，若输入字符串为null，则返回设定的默认值
     * @param str 输入字符串
     * @param defaults 默认值
     * @return double型参数
     */
    public static double getDoubleParameter(String str, double defaults)
    {
        if(str == null)
        {
            return defaults;
        }
        try
        {
            return Double.parseDouble(str);
        }
        catch(Exception e)
        {
            return defaults;
        }
    }
    
    /**
     * 获取short型参数，若输入字符串为null，则返回设定的默认值
     * @param str 输入字符串
     * @param defaults 默认值
     * @return short型参数
     */
    public static short getShortParameter(String str, short defaults)
    {
        if(str == null)
        {
            return defaults;
        }
        try
        {
            return Short.parseShort(str);
        }
        catch(Exception e)
        {
            return defaults;
        }
    }
    
    /**
     * 获取float型参数，若输入字符串为null，则返回设定的默认值
     * @param str 输入字符串
     * @param defaults 默认值
     * @return float型参数
     */
    public static float getFloatParameter(String str, float defaults)
	{
        if(str == null)
        {
            return defaults;
        }
        try
        {
            return Float.parseFloat(str);
        }
        catch(Exception e)
        {
            return defaults;
        }
	}
    
    /**
     * 获取页面输入的String类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return String型的输入参数
     */
    public static String getStringParameter(ServletRequest request, String name, String defaults)
    {
    	if(request.getAttribute(name)==null){
    		return getStringParameter(request.getParameter(name), defaults);
    	}else{
    		return getStringParameter((String)request.getAttribute(name), defaults);
    	}
    }
    /**
     * 获取页面输入的String类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @param escape 是否对特殊字符转换
     * @return String型的输入参数
     */
    public static String getStringParameter(ServletRequest request,String name, String defaults,boolean escape){
    	if(escape){
    		return getStringParameter(request,name,defaults);
    	}else{
    		if(request.getAttribute(name)==null){
    			return defaults;
    		}else{
    			return (String)request.getAttribute(name);
    		}
    	}
    	
    }   
    /**
     * 获取页面输入的int类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return int型的输入参数
     */
    public static int getIntParameter(ServletRequest request, String name, int defaults)
    {
    	if(request.getAttribute(name)==null){
    		return getIntParameter(request.getParameter(name), defaults);
    	}else{
    		return getIntParameter(""+request.getAttribute(name), defaults);
    	}
    }
    
    /**
     * 获取页面输入的int类型参数，若无该输入参数，则返回0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return int型的输入参数
     */
    public static int getIntParameter(ServletRequest request, String name){
    	return getIntParameter(request, name, 0);
    }
    
    /**
     * 获取页面输入的long类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return long型的输入参数
     */
    public static long getLongParameter(ServletRequest request, String name, long defaults){
    	if(request.getAttribute(name)==null){
    		return getLongParameter(request.getParameter(name), defaults);
    	}else{
    		return getLongParameter(""+request.getAttribute(name), defaults);
    	}
    }
    
    /**
     * 获取页面输入的long类型参数，若无该输入参数，则返回0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return long型的输入参数
     */
    public static long getLongParameter(ServletRequest request, String name){
    	return getIntParameter(request, name, 0);
    }
    
    /**
     * 获取页面输入的double类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return double型的输入参数
     */
    public static double getDoubleParameter(ServletRequest request, String name, double defaults){
    	if(request.getAttribute(name)==null){
    		return getDoubleParameter(request.getParameter(name), defaults);
    	}else{
    		return getDoubleParameter(""+request.getAttribute(name), defaults);
    	}
    }
    
    /**
     * 获取页面输入的double类型参数，若无该参数，则返回0.0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return long型的输入参数
     */
    public static double getDoubleParameter(ServletRequest request, String name){
    	return getDoubleParameter(request, name, 0.0);
    }
    
    /**
     * 获取页面输入的short类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return short型的输入参数
     */
    public static short getShortParameter(ServletRequest request, String name, short defaults)
    {
        if(request.getAttribute(name)==null){
    		return getShortParameter(request.getParameter(name), defaults);
    	}else{
    		return getShortParameter(""+request.getAttribute(name), defaults);
    	}
    }
    
    /**
     * 获取页面输入的short类型参数，若无该参数，则返回0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return short型的输入参数
     */
    public static short getShortParameter(ServletRequest request, String name)
    {
        return getShortParameter(request, name, (short)0);
    }
    
    /**
     * 获取页面输入的float类型参数
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @param defaults 设定的默认值
     * @return float型的输入参数
     */
    public static float getFloatParameter(ServletRequest request, String name, float defaults)
    {
    	if(request.getAttribute(name)==null){
      		return getFloatParameter(request.getParameter(name), defaults);
      	}else{
      		return getFloatParameter(""+request.getAttribute(name), defaults);
      	}
    }
    
    /**
     * 获取页面输入的float类型参数，若无该参数，则返回0.0
     * @param request ServletRequest的实例
     * @param name 参数名字
     * @return long型的输入参数
     */
    public static float getFloatParameter(ServletRequest request, String name)
    {
    	return getFloatParameter(request, name, (float)0.0);
    }
    
    
    /**
     * 超过千万数据屏蔽科学计数法
     * @param defaultsNumber 大数据
     * @return
     */
    public static String getLargeNumerFormat(double defaultsNumber){
    	return getLargeNumerFormat(defaultsNumber, "0.00");
    }
    
    /**
     * 一位
     * @param defaultsNumber
     * @return
     */
    public static String getLargeNumerOneFormat(double defaultsNumber){
    	return getLargeNumerFormat(defaultsNumber, "0.0");
    }
    
    /**
     * 格式化大数字
     * @param defaultsNumber
     * @param format "0"或者"0.0"，或者"0.00"
     * @return
     */
    public static String getLargeNumerFormat(double defaultsNumber, String format){
    	String strReturnValue = "";
    	if(defaultsNumber==0){
    		strReturnValue = Double.toString(defaultsNumber);
    	}else{
    		DecimalFormat decimalFormat = new DecimalFormat(format);
    		strReturnValue = decimalFormat.format(defaultsNumber);
    	}
    	return strReturnValue;
    }
    
    public static void main(String[] args) {
//		System.out.println(getLargeNumerFormat(0, "0"));
		System.out.println(String.format("%3s %4s %4s", "18710015843"));
	}
}
