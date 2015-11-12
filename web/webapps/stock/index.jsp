<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" %>
<%
	String query = request.getQueryString();
	String url = "/stock/accountIndex.jspa";
	if(query != null) {
		url = url + "?" + query;
	}
	response.sendRedirect(url);
%>