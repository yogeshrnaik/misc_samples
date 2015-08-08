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
public class QueryTag extends BodyTagSupport
{
	public int doEndTag() throws JspTagException
	{
		try
		{
			String query = getBodyContent().getString();
			Statement stm = (Statement)pageContext.getSession().getAttribute(((StatementTag)getParent()).getId());
			ResultSet rs = stm.executeQuery(query);
			pageContext.getSession().setAttribute("rset", rs);
			return EVAL_BODY_BUFFERED;
		}
		catch(Exception e)
		{
			System.out.println("exception in doEndTag of QueryTag" + e);
			return SKIP_PAGE;
		}
	}
}
/***************************************************************/