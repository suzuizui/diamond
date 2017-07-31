<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=GBK" />
    <title>Diamond�ͻ�����Ϣ��ѯ</title>
    <script type="text/javascript">
        function queryConfigInfo(method){
            document.all.queryForm.method.value=method;
            document.all.queryForm.submit();
        }
    </script>
</head>
<c:url var="adminUrl" value="/admin.do" >
</c:url>
<c:if test="${method==null}">
    <c:set var="method" value="listAllClientSubConfig"/>
</c:if>
<body>
<c:import url="/jsp/common/message.jsp"/>
<center><h1><strong>�ͻ�����Ϣ��ѯ</strong></h1></center>
<p align='center'>
<form name="queryForm" action="${adminUrl}">
    <table align='center'>
        <tr>
            <td>dataId:</td>
            <td><input type="text" name="dataId"/></td>
            <td>����:</td>
            <td><input type="text" name="group"/></td>
            <td>
                <input type='hidden' name="method" value='${method}'/>
                <input type='button' value='��ѯ' onclick="queryConfigInfo('listAllClientSubConfig');"/>
        </tr>
    </table>
</form>
</p>
<p align='center'>
    <c:if test="${clientInfo!=null}">
<table border='1' width="800">
    <tr>
        <td>dataId</td>
        <td>����</td>
        <td>�ͻ���IP</td>
        <td>�ͻ��������ѵʱ��</td>
    </tr>
    <c:forEach items="${clientInfo}" var="clientInfo">
        <tr>
            <td name="tagDataID">
                <c:out value="${clientInfo.dataId}"/>
            </td>
            <td name="tagGroup">
                <c:out value="${clientInfo.group}" escapeXml="false"/>
            </td>
            <td name="tagIP">
                <c:out value="${clientInfo.ipAddr}" escapeXml="false"/>
            </td>
            <td name="tagTime">
                <c:out value="${clientInfo.connTime}" escapeXml="false"/>
            </td>
        </tr>
    </c:forEach>
</table>
</c:if>
</p>
</body>
</html>
