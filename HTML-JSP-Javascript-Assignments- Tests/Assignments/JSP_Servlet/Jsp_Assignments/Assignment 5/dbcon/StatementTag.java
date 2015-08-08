/***************************************************************/
package dbcon;
/***************************************************************/
import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.sql.*;
/***************************************************************/
public class StatementTag extends BodyTagSupport
{
	String id, conn;
/***************************************************************/
	public void setId(String id)					{			this.id = id;				}
	public void setConn(String conn)			{			this.conn = conn;		}
	public String getId()							{			return this.id;				}
	public String getConn()						{			return this.conn;			}
/***************************************************************/
	public int doStartTag() throws JspTagException
	{
		try
		{
			Statement st = ((Connection)pageContext.getSession().getAttribute(conn)).createStatement();
			pageContext.getSession().setAttribute(id, st);
			return EVAL_BODY_INCLUDE;
		}
		catch(Exception e)
		{
			System.out.println("exception in doStartTag of StatementTag" + e);
			return SKIP_PAGE;
		}
	}
}
/***************************************************************/