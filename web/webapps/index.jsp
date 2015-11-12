<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" %>
<%
	response.sendRedirect("/stock/accountIndex.jspa");
%>
<form action="https://test.gzb.com/stock/buyStep2.jspa" method="post">
	<input type="text" value="000001"  name="stockCode"/><br/>
	<input type="text" value="ffffff" name="entrustPrice"/><br/>
	<input type="text" value="https://test.gzb.com/stock/images/homev2014/header/logo_sub.png"  name="entrustAmount"/><br/>
	<input type="submit"  value="提交" />
</form>