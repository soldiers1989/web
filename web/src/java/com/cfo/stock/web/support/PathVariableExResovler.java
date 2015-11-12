package com.cfo.stock.web.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 获取pathvarable，不匹配不报错
 * @author yuan.cheng
 *
 */
public class PathVariableExResovler implements HandlerMethodArgumentResolver{

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(PathVariableEx.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		String pathVarName = parameter.getParameterName();
		Map<String, String> uriTemplateVariables =
				(Map<String, String>) servletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (uriTemplateVariables != null && uriTemplateVariables.containsKey(pathVarName)) {
			//根据参数类型不同需要进行类型转换
			return Long.parseLong(uriTemplateVariables.get(pathVarName));
		}
		return null;
	}

	
}
