package com.cfo.stock.web.interceptor;

import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.SessionFactory;
import com.cfo.common.session.interceptor.SpringMvcSessionInterceptor;
import com.cfo.common.utils.TokenHelper;

public class TokenInterceptor extends SpringMvcSessionInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if ("GET".equals(request.getMethod())) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		Token annotation = method
				.getAnnotation(Token.class);
		if(annotation==null){
			return true;
		}
		HttpSessionWrapper session = SessionFactory.getInstance(request,
				response).getSession();
		synchronized (session) {
			if (!TokenHelper.validToken(request, response)) {
				String isajax = request.getHeader("x-requested-with");
				if(isajax != null){//ajax请求
					PrintWriter writer = response.getWriter();
					writer.println("{\"errMsg\":\"重复提交！\",\"mutiple_request_error\":-1}");
					writer.flush();
				}else{
					response.sendRedirect(annotation.tokenUrl());
				}
				return false;
			}
		}
		return true;
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Token {
		String tokenUrl() default "";
	}
}
