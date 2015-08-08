<%@page import="java.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE> right.jsp </TITLE>
</HEAD>

<BODY>
<I><U><B>Output is : <BR></B></U></I>
<jsp:useBean id = "jdbcBean" scope = "session" class = "beans.JDBCBean"/>
<jsp:setProperty name = "jdbcBean" property = "driver" value="oracle.jdbc.driver.OracleDriver"/>
<jsp:setProperty name = "jdbcBean" property = "url" value="jdbc:oracle:thin:@192.168.12.16:1521:oracle8i"/>
<jsp:setProperty name = "jdbcBean" property = "username"  value="scott"/>
<jsp:setProperty name = "jdbcBean" property = "password" value="tiger"/>

<% String query = request.getParameter("query"); %>
<% 
	if (query == null)
		query = "select * from employee";
%>
<jsp:setProperty name = "jdbcBean" property = "query" value="<%= query%>"/>
<BR>
<% jdbcBean.connectToDatabase(); %>
<% 
	int flag = jdbcBean.execute();
%>
<%
	Vector data = jdbcBean.getData();
	if (flag == 0 && data != null)
	{
%>
<TABLE BORDER="1" CELLSPACING="0" CELLPADDING="1">
<%
		for (int i=0; i<data.size(); i++)
		{
%>
		<TR>
<%
		String arr[] = (String[])data.get(i);
		for (int j=0; j<arr.length; j++)
		{
%>
			<TD><%=arr[j]%></TD>
<%
		}
%>
		</TR>
<%
		}
	}
	else if (flag >= 1)
		out.println("<B>Query executed successfully</B>");
	else 
		out.println("<B>Error ocurred while executing query.</B>");
%>
</TABLE>
</BODY>
</HTML>
