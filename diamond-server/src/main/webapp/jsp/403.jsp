<%@ page session="false" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%
Object o = request.getAttribute("content");
if(o==null||o.equals("")){
	out.println("403 error");
}else{
	out.println(o);
}
%>