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
public class ResultSetTag extends BodyTagSupport
{
	String id;
/***************************************************************/
	public void setId(String id)				{			this.id = id;			}
	public String getId()						{			return this.id;			}
/***************************************************************/
	public int doStartTag() throws JspTagException
	{
		try
		{
			ResultSet rset = (ResultSet)pageContext.getSession().getAttribute("rset");
			pageContext.getSession().removeAttribute("rset");
			rset.next();
			pageContext.getSession().setAttribute(id, rset);
			return EVAL_BODY_INCLUDE;
		}
		catch(Exception e)
		{
			System.out.println("exception in doStartTag of ResultSetTag");
			return SKIP_PAGE;
		}
	}
/***************************************************************/
	public int doAfterBody() throws JspTagException
	{
		try
		{
			ResultSet rs = (ResultSet)pageContext.getSession().getAttribute(id);
			if(rs.next())
			{
				pageContext.getSession().setAttribute(id, rs);
				return EVAL_BODY_AGAIN;
			}
			else
			{
				return SKIP_BODY;
			}
		}
		catch(Exception e)
		{
			System.out.println("exception in doAfterBody of ResultSetTag" + e);
			return SKIP_PAGE;
		}
	}
}
/***************************************************************/
	

