<%@ page session="false" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%
Object o = request.getAttribute("content");
if (o==null||o.equals("")) {
	out.println("config data not exist");
} else {
	out.println(o);
}
%>