/***************************************************************/
package dbcon;
/***************************************************************/
import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;
import java.sql.*;
/***************************************************************/
public class CloseTag extends TagSupport
{
	String conn;
/***************************************************************/
	public void setConn(String conn)			{		this.conn = conn;		}
	public String getConn()						{		return this.conn;			}
/***************************************************************/
	public int doEndTag() throws JspTagException
	{
		try
		{
			Connection c = (Connection)pageContext.getSession().getAttribute(conn);
			c.close();
			return EVAL_PAGE;
		}
		catch(Exception e)
		{
			System.out.println("exception in CloseTag doEndTag" + e);
			return SKIP_PAGE;
		}
	}
}
/***************************************************************/