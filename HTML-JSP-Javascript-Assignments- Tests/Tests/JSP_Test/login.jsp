<%@page import="ConDatabase" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>

<BODY>
<%
if (request.getSession(true).getAttribute("luname") == null)
{
%>
<FORM METHOD="post" ACTION="ControllerServlet?action=logme">
<H3>Login Page</H3>
<TABLE BORDER="1">
<TR>
	<TD>User name : </TD>
	<TD><INPUT TYPE="text" NAME="luname"></TD>
</TR>
<TR>
	<TD>Password : </TD>
	<TD><INPUT TYPE="password" NAME="lpasswd"></TD>
</TABLE>
<BR>
<INPUT TYPE="submit" VALUE="Log Me">
</FORM>
<% 
}
else 
{
	ConDatabase conDB = (ConDatabase)request.getSession(true).getAttribute("connection");
	String uname = (String)request.getSession(true).getAttribute("luname");
	String passwd = (String)request.getSession(true).getAttribute("lpasswd");
	if (conDB.isValid(uname, passwd))
	{
%>
	<jsp:forward page="homepage"/>
<%
	}
	else
	{
		request.getSession(true).removeAttribute("luname");
		request.getSession(true).removeAttribute("lpasswd");
		out.println("<H1>Not a valid user.</H1>");
%>
		<jsp:include page="login"/>
<%
	}
}
%>
</BODY>
</HTML>
