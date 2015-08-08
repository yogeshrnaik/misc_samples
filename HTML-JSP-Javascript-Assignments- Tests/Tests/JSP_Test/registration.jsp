<%@page import="ConDatabase" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>

<BODY>
<%
if (request.getSession(true).getAttribute("runame") == null)
{
%>
<FORM METHOD="post" ACTION="ControllerServlet?action=registerme">
<H3>Registration Page</H3>
<TABLE BORDER="1">
<TR>
	<TD>User name : </TD>
	<TD><INPUT TYPE="text" NAME="runame"></TD>
</TR>
<TR>
	<TD>Password : </TD>
	<TD><INPUT TYPE="password" NAME="rpasswd"></TD>
</TR>
<TR>
	<TD>Email address : </TD>
	<TD><INPUT TYPE="text" NAME="remail"></TD>
</TR>
<TR>
	<TD>Address : </TD>
	<TD><TEXTAREA NAME="raddress" ROWS="5" COLS="15"></TEXTAREA></TD>
</TR>
</TABLE>
<BR>
<INPUT TYPE="submit" VALUE="Register Me">
</FORM>
<%
}
else
{
	//out.println("else");
	ConDatabase conDB = (ConDatabase)request.getSession(true).getAttribute("connection");
	String uname = (String)request.getSession(true).getAttribute("runame");
	String passwd = (String)request.getSession(true).getAttribute("rpasswd");
	String email = (String)request.getSession(true).getAttribute("remail");
	String address = (String)request.getSession(true).getAttribute("raddress");
	/*out.println("registration.jsp \t uname : " + uname +"\tpasswd " + passwd + 
									"\temail " + email +"\taddress " + address + "\tcon" + conDB + "<BR>");*/
	if (email == null)
	{
		//out.println("email is null<BR>");
		// send to login page
%>
		<jsp:forward page="ControllerServlet?action=login"/>
<%
	}
	else
	{
		// add user
		if (!conDB.isPresent(uname))
		{
			//out.println("not present<BR>");
			boolean flag = conDB.addUser(uname, passwd, email, address);
			//out.println("after adding user" + flag + "<BR>");
			if (flag)
			{
				request.getSession(true).removeAttribute("runame");
				request.getSession(true).removeAttribute("rpasswd");
				request.getSession(true).removeAttribute("remail");
				request.getSession(true).removeAttribute("raddress");
%>
				<jsp:forward page="login"/>
<%
			}
			

		}
		else
		{
			request.getSession(true).removeAttribute("runame");
			request.getSession(true).removeAttribute("rpasswd");
			request.getSession(true).removeAttribute("remail");
			request.getSession(true).removeAttribute("raddress");
			out.println("<H1>Please choose some other username.</H1>");
%>
			<jsp:include page="registration"/>
<%
		}
	}
}
%>


</BODY>
</HTML>
