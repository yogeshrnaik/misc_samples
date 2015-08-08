<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE> right.jsp </TITLE>
</HEAD>

<%@page import="java.io.*"%>

<BODY>
<I><U><B>Output is : <BR></B></U></I>
<%
	String filename = "/" + request.getRemoteHost() + "Content.jsp";
	/*try 
	{
		FileReader file = new FileReader(filename);
	}
	catch (IOException e)
	{
		filename = "content.jsp";
	}*/
%>
<jsp:include page="<%=filename%>" flush="true"/>
</BODY>
</HTML>
