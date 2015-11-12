package com.cfo.common.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cfo.common.session.exception.InvalidSessionException;




public class JrjSessionArgumentResolver implements
		HandlerMethodArgumentResolver {

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer container, NativeWebRequest request,
			WebDataBinderFactory factory) throws Exception {
		HttpServletRequest srequest = request.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse sresponse = request.getNativeResponse(HttpServletResponse.class);
		HttpSessionWrapper session=SessionFactory.getInstance(srequest, sresponse).getSession();
		if(session==null){
			throw new InvalidSessionException();
		}
		return session;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return HttpSessionWrapper.class.isAssignableFrom(parameter.getParameterType());
	}

}


