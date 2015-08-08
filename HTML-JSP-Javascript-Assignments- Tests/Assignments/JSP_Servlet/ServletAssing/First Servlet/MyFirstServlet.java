/******************************import statements******************************/
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class MyFirstServlet extends HttpServlet
{
	public void doGet(	HttpServletRequest request,
								HttpServletResponse response)
									throws ServletException, IOException
	{
		PrintWriter out;
		String title = "<HTML><BODY><B>My First Servlet</BODY></HTML>";
		response.setContentType("text/html");
		out = response.getWriter();
		out.println(title);
		out.close();
	}
}
/*************************************************************************/
