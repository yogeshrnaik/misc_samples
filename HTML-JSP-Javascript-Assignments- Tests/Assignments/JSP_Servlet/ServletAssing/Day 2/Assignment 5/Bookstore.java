/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class Bookstore extends HttpServlet
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
		PrintWriter out =  resp.getWriter(); 
		resp.setContentType("text/html");
		out.println("<HTML><BODY>");
		showTitle(out);
		showFirstPage(out);
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
	public static void showTitle(PrintWriter out) throws IOException
	{
		out.println("<H1><CENTER>Duke's Bookstore</CENTER></H1>");
		out.println("<HR size=1 width=100%>");
	}
/******************************************************************/
	public void showFirstPage(PrintWriter out) throws IOException
	{
		String strResponse = "";
		strResponse += "<A href='Catalog'>Catalog</A><BR>";
		strResponse += "<H4>Choose from our excellent selection of books</H4>";
		strResponse += "<BR>";
		strResponse += "<A href='Showcart'>Shopping Cart</A><BR>";
		strResponse += "<H4>Look at your shopping cart to see the books you have chosen</H4>";
		strResponse += "<BR>";
		strResponse += "<A href='Cashier'>Buy your Books</A><BR>";
		strResponse += "<H4>Pay for the books you have put in your cart!</H4>";
		out.println(strResponse);
	}
}
/*************************************************************************/

