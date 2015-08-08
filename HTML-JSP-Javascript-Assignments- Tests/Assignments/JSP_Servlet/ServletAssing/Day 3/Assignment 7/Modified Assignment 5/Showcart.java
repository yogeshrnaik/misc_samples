/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class Showcart extends HttpServlet
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
		HttpSession hs = req.getSession(true);
		BookCart bc = (BookCart)hs.getAttribute("bookcart");
		Bookstore.showTitle(out);
		String bid = req.getParameter("Remove");
		if (bc != null)
		{
			if (bid != null)
			{
				if (bid.equals("all"))
					bc.removeAll();
				else
					bc.removeFromCart(bid);
			}
			hs.setAttribute("bookcart", bc);
		}
		showCart(out, req);
		out.println("</BODY></HTML>");
	}
/******************************************************************/
	public void showCart(PrintWriter out, HttpServletRequest req) throws IOException
	{
		String str = "";
		HttpSession hs = req.getSession(true);
		BookCart bc = (BookCart)hs.getAttribute("bookcart");
		if (bc != null && bc.cart != null)
		{
			str += "<H2>You have "+ bc.cart.size() +" items in your shopping cart.</H2>";
			str += "<TABLE border=0 width=100%>" ;
			str += "<TR><TD><B>Quantity</B><TD><B>Title</B><TD><B>Price</B><TD><BR>";
			for (int i=0; i<bc.cart.size(); i++)
			{
				CartItem c = (CartItem)bc.cart.get(i);
				Book b = Catalog.getBook(c.bid);
				str += "<TR><TD align=right'>" + c.qty;
				str += "<TD align='left'><A href='BookDetails?bookid="+ b.bid + "'>"+b.name +"</A>";
				str += "<TD align='left'>$" + b.price;
				str += "<TD><A href='Showcart?Remove=" + c.bid +"'>Remove<BR>Item</A>";
			}
			double subtotal = bc.getSubTotal();
			double tax = 2 * subtotal / 100;
			str += "<TR><TD><BR>";
			str += "<TD align='right'><B>Subtotal : </B>";
			str += "<TD align='left'>$" + subtotal + "<BR>";
			str += "<TD><BR>";
			
			str += "<TR><TD><BR>";
			str += "<TD align='right'><B>CA Sales Tax : </B>";
			str += "<TD align='left'>$" + tax + "<BR>";
			str += "<TD><BR>";

			str += "<TR><TD><BR>";
			str += "<TD align='right'><B>Grand Total : </B>";
			str += "<TD align='left'>$" + (subtotal + tax) + "<BR>";
			str += "<TD><BR>";
			str += "</TABLE>";
		}
		else
			str += "<H2>You have <I>ZERO</I> items in your shopping cart.</H2>";
		str += "<BR><BR><BR><BR>";
		str += "<A href='Catalog'>See the Catalog</A>";
		str += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		str += "<A href='Cashier'>Check Out</A>";
		str += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		str += "<A href='Showcart?Remove=all'>Clear Cart</A>";
		out.println(str);
	}
}
/*************************************************************************/

