<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE> Learn.jsp </TITLE>
</HEAD>

<FRAMESET ROWS = "15%, *">
	<FRAME SRC = "top.jsp" NAME = "top" SCROLLING = "no" NORESIZE></FRAME>
	<FRAMESET COLS = "55%, 50%">
		<FRAME SRC = "left.jsp" NAME = "left" SCROLLING = "auto" NORESIZE></FRAME>
		<FRAME SRC = "right.jsp" NAME = "right" SCROLLING = "auto"></FRAMESET>
	</FRAMESET>

<%	/*String user = request.getRemoteUser();
		if (user == null)
		{
			response.setStatus(response.SC_UNAUTHORIZED);
			response.setHeader("WWW-Authenticate", "BASIC realm=\"priviledged-few\"");
		}*/
%>

</HTML>
