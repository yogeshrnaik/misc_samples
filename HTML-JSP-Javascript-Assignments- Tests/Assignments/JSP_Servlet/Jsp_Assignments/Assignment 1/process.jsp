<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE> process.jsp </TITLE>
</HEAD>

<BODY>
<I><U><B>Output is : <BR></B></U></I>

<jsp:useBean id = "saveData" scope = "session" class = "beans.SaveDataBean"/>
<jsp:setProperty name = "saveData" property = "jspcode"/>
<%  
	String filepath = pageContext.getServletContext().getRealPath("/");
	saveData.saveToFile(filepath +"content.jsp");
	pageContext.forward("right.jsp");
%>

</BODY>
</HTML>
