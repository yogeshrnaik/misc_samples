/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class Cashier extends HttpServlet
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
		Bookstore.showTitle(out);
		showPage(out, req);
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
	public void showPage(PrintWriter out, HttpServletRequest req) throws IOException
	{
		out.println("<FORM action='Receipt' method='post'>");
		HttpSession hs = req.getSession(true);
		BookCart bc = (BookCart)hs.getAttribute("bookcart");
		if (bc == null || bc.cart == null || bc.cart.size() == 0)
		{
			out.println("<H1>No books present in Shopping Cart.</H1>");
		}
		else
		{
			out.println("Your total purchase amount is : <B>" + bc.getGrandTotal() + "</B>");
			out.println("<BR><BR>");
			out.println("To purchase the items in your shopping cart, please provide us with the following information.");
			out.println("<BR><BR>");

			out.println("<TABLE border=0>");
			out.println("<TR><TD><B>Name : </B>");
			out.println("<TD><INPUT name='name' type='text'/>");
			out.println("<TR><TD><B>Credit Card Number : </B>");
			out.println("<TD><INPUT name='cardno' type='text'/>");
			out.println("<TR> <TD> <BR>");
			out.println("<TD> <INPUT name='submit' type='submit' value='Submit Information'/>");
			out.println("</TABLE>");
			out.println("</FORM>");
		}
	}
}
/*************************************************************************/

