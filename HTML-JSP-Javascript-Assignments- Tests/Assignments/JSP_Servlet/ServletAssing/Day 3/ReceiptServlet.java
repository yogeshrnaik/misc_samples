/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class ReceiptServlet extends HttpServlet
{
	public void init() throws ServletException
	{
		super.init();
	}
/******************************************************************/
	public void doGet(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		PrintWriter out = resp.getWriter();
	}
/******************************************************************/
	public void doPost(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		PrintWriter out = resp.getWriter();
		out.println("Name : " + req.getParameter("name") + "<BR>");
		out.println("Credit Card No. : " + req.getParameter("cardno") + "<BR>");
	}
}
/*************************************************************************/

