/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class BookDetails extends HttpServlet
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
		
		Bookstore.showTitle(out);
		showBookDetails(req.getParameter("bookid"), out);

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
	public void showBookDetails(String bookid, PrintWriter out) throws IOException
	{
		String str = "";
		Book b = Catalog.getBook(bookid);
		str += "<BR><H1>" + b.name + "</H1>";
		str += "by <I>" + b.author + "</I><BR><BR>";
		str += "<B>Here's what the critics say : </B><BR>";
		str += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		str += b.desc + "<BR><BR>";
		str += "<B>Our price : $" + b.price +"</B>";
		str += "<BR><BR><BR><BR><BR><BR>";
		str += "<CENTER>";
		str += "<A href='Catalog?Buy=" + b.bid +"'>Add this item to your shopping cart";
		str += "</CENTER>";
		out.println(str);
	}
}
/*************************************************************************/

