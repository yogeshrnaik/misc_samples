/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class Receipt extends HttpServlet
{
	private static Vector cart = null;
	public void init() throws ServletException
	{
		super.init();
	}
/******************************************************************/
	public void doGet(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		PrintWriter out =  resp.getWriter(); 
		resp.setContentType("text/html");
		out.println("<HTML><BODY>");
		//out.println("<META HTTP-EQUIV='REFRESH' CONTENT='3;Bookstore'>");
		String name = req.getParameter("name");
		Bookstore.showTitle(out);
		String str = "<H3>Thank you for purchasing your books from us " + name + ".</H3><BR>";
		str += "<H3>Please shop with us again soon!</H3><BR>";
		str += "<I>This page automatically resets.</I>";
		HttpSession hs = req.getSession(true);
		hs.invalidate();
		resp.setHeader("refresh", "3;URL="+"Store");

		out.println(str);
		out.println("</BODY></HTML>");
	}
/******************************************************************/
	public void doPost(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		doGet(req, resp);
	}
/******************************************************************/
}
/*************************************************************************/

