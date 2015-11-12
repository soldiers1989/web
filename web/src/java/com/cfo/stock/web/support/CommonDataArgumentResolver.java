package com.cfo.stock.web.support;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CommonDataArgumentResolver implements
		HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		CommonData data=null;
		 //让方法和参数或者类 三种target通过
        if(parameter.hasParameterAnnotation(CommonData.class)){
        	data=parameter.getParameterAnnotation(CommonData.class);
        }
        else if (parameter.getMethodAnnotation(CommonData.class) != null){
        	data=parameter.getMethodAnnotation(CommonData.class);
        }
        else if(parameter.getMethod().getDeclaringClass().isAnnotationPresent(CommonData.class)){
        	data=parameter.getMethod().getDeclaringClass().getAnnotation(CommonData.class);
        }
        return data==null?false:data.value();
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		
		return null;
	}

}
