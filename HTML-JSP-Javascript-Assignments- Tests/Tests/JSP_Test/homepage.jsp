<%@page import="java.sql.*" %>
<%@page import="ConDatabase" %>
<H1>Welcome to Home page of 
<%=request.getSession(true).getAttribute("luname")
%>!</H1>
<%
	ConDatabase conDB = (ConDatabase)request.getSession(true).getAttribute("connection");
	String uname = (String)request.getSession(true).getAttribute("luname");
	ResultSet rs = conDB.getUserInfo(uname);
	rs.next();
%>
<H3>Details of user : </H3>
<H3>Email address : 
<%= rs.getString(3)%>
</H3>
<H3>Address : 
<%= rs.getString(4)%>
</H3>
