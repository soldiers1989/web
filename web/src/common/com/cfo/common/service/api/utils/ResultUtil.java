package com.cfo.common.service.api.utils;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cfo.common.exception.XJBServiceException;
import com.cfo.common.service.api.result.Result;
/**
 * Result工具类
 * @author yuanlong.wang
 *
 */
public class ResultUtil {
	/**
	 * 指定转换成Result的子类
	 * @param result
	 * @param clazz
	 * @return
	 */
	public static <T extends Result> T parseResult(String result,Class<T> clazz)throws XJBServiceException{
		T  t=null;
		if(!StringUtils.isBlank(result)){
			t=JSONObject.parseObject(result, clazz);
		}
		if(t==null){
			throw new XJBServiceException(-1, "The Data Is Empty");
		}
		if(t.getRetcode()!=0){
			throw new XJBServiceException(t.getRetcode(), t.getMsg());
		}
		return t;
	}
	/**
	 * 指定转换成Result的子类 不检验异常
	 * @param result
	 * @param clazz
	 * @return
	 */
	public static <T extends Result> T parseResultNoE(String result,Class<T> clazz){
		T  t=null;
		if(!StringUtils.isBlank(result)){
			t=JSONObject.parseObject(result, clazz);
		}
		return t;
	}
	
	public static Result parseResult(String result){
		return JSONObject.parseObject(result, Result.class);
	}
	
	public static Result parseResultE(String result){
		Result t=null;
		if(!StringUtils.isBlank(result)){
			t=JSONObject.parseObject(result,Result.class);
		}
		if(t==null){
			throw new XJBServiceException(-1, "The Data Is Empty");
		}
		if(t.getRetcode()!=0){
			throw new XJBServiceException(t.getRetcode(), t.getMsg());
		}
		return t;
	}
}
