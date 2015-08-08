/******************************import statements******************************/
import java.util.*;
/*********************************public class*********************************/
public class BookCart
{
	Vector cart = null;
	public BookCart()
	{
		cart = new Vector();
	}
/******************************************************************/
	public void addToCart(String bid)
	{
		if (cart == null)
		{
			cart = new Vector();
			cart.add(new CartItem(bid, 1));
		}
		else 
		{
			for (int i=0; i < cart.size(); i++)
			{
				CartItem c = (CartItem)cart.get(i);
				if (c.bid.equals(bid))
				{
					c.qty++;
					return;
				}
			}
			cart.add(new CartItem(bid, 1));
		}
	}
/******************************************************************/
	public void removeFromCart(String bid)
	{
		if (cart != null && cart.size() > 0)
		{
			for (int i=0; i<cart.size(); i++)
			{
				CartItem c = (CartItem)cart.get(i);
				if (c.bid.equals(bid))
				{
					if (c.qty > 1)
						c.qty--;
					else
					{
						cart.remove(i);
						if (cart.size() == 0)
						{
							cart = null;
						}
					}
					return;
				}
			}
		}
	}
/******************************************************************/
	public void removeAll()
	{
		cart = null;
	}
/******************************************************************/
	public double getSubTotal()
	{
		double subtotal = 0;
		if (cart != null)
		{
			for (int i=0; i<cart.size(); i++)
			{
				CartItem c = (CartItem)cart.get(i);
				Book b = Catalog.getBook(c.bid);
				subtotal += b.price * c.qty;
			}
		}
		return subtotal;
	}
/******************************************************************/
	public double getGrandTotal()
	{
		double subtotal = getSubTotal();
		double grandtotal = subtotal + 2 * subtotal / 100;
		return grandtotal;
	}
}
/*************************************************************************/