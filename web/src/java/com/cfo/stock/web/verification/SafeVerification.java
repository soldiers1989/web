package com.cfo.stock.web.verification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfo.common.session.constant.AttributeKeys;
import com.cfo.stock.web.util.LogHelper;
import com.jrj.common.cache.memcached.MemcachedCache;
import com.jrj.common.utils.DateUtil;
@Service
public class SafeVerification {
	
	//锁定记录的后缀
	private final String suffix = "_Lock";
	@Autowired
	private MemcachedCache memcachedCache;
	private Properties props;
	public SafeVerification(){
		props = loadBasicinformation("basicinformation.properties");
	}
	
	/**
	 * 完善个人信息
	 * 向手机发送验证码
	 */
	public boolean safeValidate(String params,String rulecode,String source){
		//参数不正确直接返回通过验证
		if(StringUtils.isBlank(params) || StringUtils.isBlank(rulecode)){
			return true;
		}
		//读取开关 0 为关   1为开
		String stockSafeSw = props.getProperty("stock_safe_rules_sw");
		if("0".equals(stockSafeSw)){
			return true;
		}
		//读取规则
		String rules = props.getProperty(rulecode);
		//调用验证
		return doSafeValidate(params,rules,source);
	}
	/**
	 * 接口防刷
	 * 通过用户IP及手机号限制
	 * param(ip:127.0.0.1@phone:13521036045)
	 * rules(ip:10;12;24@phone:5;1;12)
	 * source(来源:完善个人信息发送)
	 * 如程序中出现错误，默认验证通过
	 */
	public boolean doSafeValidate(String params,String rules,String source){
		boolean flag = true;
		try{
			//参数不正确直接返回通过验证
			if(StringUtils.isBlank(params) || StringUtils.isBlank(rules)){
				return true;
			}
			Map<String,String> param = analParam(params);
			if(validateIsLock(param,source)){//返回true表示没有被锁定的用户
				//判断参数是否有值
				if(param.size()>0){
					//解析规则
					Map<String,String> ruleMap = analParam(rules);
					//遍历需要验证的参数
					for (String key : param.keySet()) {
						//拼缓存中的key
						String paraValue = AttributeKeys.STOCK_FS_PREFIX + param.get(key);
						//读取该变量的规则
						String rule = ruleMap.get(key);
						//如果没有设置规则，则跳过
						if(rule == null){
							continue;
						}
						//将规则中的数据解析出来
						String[] ruleValue = rule.split(";");
						//阀值，达到阀值，将限制用户访问
						int maxNum = Integer.parseInt(ruleValue[0]);
						//没达阀值时缓存设置的时间
						int outTime = Integer.parseInt(ruleValue[1]);
						//达到阀值时缓存设置的时间
						int lockTime = Integer.parseInt(ruleValue[2]);
						//查询key在缓存中的值，达到阀值后更新缓存时间，并存入日志表中
						long paramNum = memcachedCache.getCounter(paraValue);
						if(memcachedCache.getCounter(paraValue) >= maxNum){
							flag = false;
							//拼缓存中的key，更新缓存时间
							String paraValueLock = paraValue+suffix;
							if(memcachedCache.get(paraValueLock) == null){
								memcachedCache.set(paraValueLock, paramNum, DateUtil.add(new Date(), Calendar.MINUTE, lockTime));
							}
							//打日志
							LogHelper.safevalidate.info("Method ---> safeValidate "+key+"限制调用 "+source+" params："+params+"，key:"+paraValue+",锁定时间："+DateUtil.format(DateUtil.add(new Date(), Calendar.MINUTE, lockTime),DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
							break;
						}else{
							//调用加1
							memcachedCache.addOrIncr(paraValue, 1, DateUtil.add(new Date(), Calendar.MINUTE, outTime), null);
						}
				    }
					return flag;
				}else{
					return flag;
				}
			}else{
				return false;
			}
		}catch(Exception e){
			LogHelper.safevalidate.error("Method --> safeValidate error ",e);
			return true;
		}
	}
	/**
	 * 判断所有参数是否已被锁定
	 * 有一个参数存在锁定，限制用户
	 */
	public boolean validateIsLock(Map<String,String> param,String source){
		boolean flag = true;
		//查询参数是否为空
		if(param!=null && param.size()>0){
			for (String key : param.keySet()) {
				//拼缓存中的key
				String paraValueLock = AttributeKeys.STOCK_FS_PREFIX + param.get(key)+suffix;
				if(memcachedCache.get(paraValueLock) != null){
					LogHelper.safevalidate.info("Method ---> safeValidate "+key+"限制调用 "+source+" params："+param+"，key:"+paraValueLock);
					flag = false;
					break;
				}
			}
			return flag;
		}else{
			return flag;
		}
	}
	/**
	 * 解析参数和规则
	 */
	public Map analParam(String rules){
		Map<String,String> map = new HashMap<String,String>();
		String[] rule = StringUtils.split(rules,"|");
		for(int i=0;i<rule.length;i++){
			String[] value = StringUtils.split(rule[i],":");
			map.put(value[0], value[1]);
		}
		return map;
	}
	/**
	 * 加载验证配置
	 */
	private Properties loadBasicinformation(String propertiesName) {
		Properties props = new Properties();
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesName);
			props.load(in);
			return props;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
	public static void main(String[] args) {
		String re="phone:weili.li@jrj.com.cn|ip:127.0.0.1";
		String[] res=StringUtils.split(re, "|");
		
		System.out.println(res[0]);
	}
}
