/******************************import statements******************************/
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class Catalog extends HttpServlet
{
	private static Hashtable allBooks;
	private static BookCart bookCart;
	public void init() throws ServletException
	{
		super.init();
		ResultSet books = getCatalog();
		allBooks = new Hashtable();
		try
		{
			while (books.next())
			{
				Book b = new Book(books.getString(1),books.getString(2),books.getString(3),books.getString(4), books.getDouble(5));
				allBooks.put(books.getString(1), b);
			}
		}
		catch (Exception e)	 {	}
		bookCart = new BookCart();
	}
/******************************************************************/
	public void doGet(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		PrintWriter out =  resp.getWriter(); 
		resp.setContentType("text/html");
		HttpSession hs = req.getSession(true);
		hs.setAttribute("bookcart", bookCart);

		out.println("<HTML><BODY>");
		Bookstore.showTitle(out);
		String bid = req.getParameter("Buy");
		if (bid != null)
		{
			bookCart.addToCart(bid);
			hs.setAttribute("bookcart", bookCart);
			showBookAdded(bid, out);		
		}
		showCatalog(out);
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
	public void showCatalog(PrintWriter out) throws IOException
	{
		String str = "<B>Please choose from our selection </B><BR>";
		str += "<TABLE width=100%>";
		Iterator iterator = allBooks.values().iterator();
		while (iterator.hasNext())
		{
			Book b = (Book)iterator.next();
			str += "<TR><TD><A href='BookDetails?bookid=" + b.bid + "'>" + b.name + "</A>" +
						"<BR>by <I>" + b.author + "</I>";
			str += "<TD> $" + b.price;
			str += "<TD><A href='Catalog?Buy=" + b.bid + "'> Add to Card </A>" ;
		}
		str += "<TABLE>";
		out.println(str);
	}
/*************************************************************************/
	public static Book getBook(String id)
	{
		Book b = (Book)allBooks.get(id);
		return b;
	}
/*************************************************************************/
	public void showBookAdded(String bid, PrintWriter out) throws IOException
	{
		Book b = getBook(bid);
		String str = "<H2>You just added <I>" + b.name +"</I> to your shopping cart. </H2>";
		str += "<A href='Showcart'><B>Check Shopping Cart</B></A>&nbsp;&nbsp;&nbsp;&nbsp;";
		str += "<A href='Cashier'><B>Buy your Books</B></A><BR><BR>";
		out.println(str);
	}
/*************************************************************************/
	public ResultSet getCatalog()
	{
		ResultSet result;
		try
		{
			Connection con = (Connection)getServletContext().getAttribute("con");
			Statement st = con.createStatement();
			result = st.executeQuery("select * from BookStore");
			return result;
		}
		catch (Exception e)
		{
			//out.println(e);
			return null;
		}
	}
}
/*************************************************************************/

