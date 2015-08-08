<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE> left.jsp </TITLE>
</HEAD>
<%@page import="java.io.*" %>
<BODY>
<FORM NAME = "frm" ACTION = "process.jsp" METHOD = "post" TARGET = "right">
	<H3><FONT COLOR = "red"> Welcome to self learning! </FONT></H3>
	<TEXTAREA NAME = "jspcode" ROWS = "15" COLS = "45" SCROLLING = "auto">
<%
// read content.jsp file and display it here
String filepath = pageContext.getServletContext().getRealPath("/");
//String filename = filepath  + "JSP\\P041013\\Assignment 1\\content.jsp";
String filename = filepath  + "content.jsp";
FileReader fr = new FileReader(filename);
BufferedReader br = new BufferedReader(fr);
String line = "";
while ((line = br.readLine() ) != null)
{
	out.println(line);
}
%>
	</TEXTAREA>
	<BR>
	<INPUT TYPE = "submit" NAME = "submit_btn" VALUE = "Show Output">
	<INPUT TYPE = "reset" NAME = "reset_btn" VALUE = "Reset" ONCLICK="reset()">
</FORM>
</BODY>
</HTML>
