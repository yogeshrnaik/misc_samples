/***************************************************************/
package dbcon;
/***************************************************************/
import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.sql.*;
/***************************************************************/
public class ColumnTag extends TagSupport
{
	String position;
/***************************************************************/
	public void setPosition(String position)		{		this.position = position;	}
	public String getPosition()						{		return this.position;		}
/***************************************************************/
	public int doStartTag() throws JspTagException 
	{
		try
		{
			int position1 = Integer.parseInt(getPosition());
			ResultSet rs = (ResultSet)pageContext.getSession().getAttribute(((ResultSetTag)getParent()).getId());
			pageContext.getOut().write(rs.getString(position1));

			return EVAL_BODY_INCLUDE;
		}
		catch(Exception e)
		{
			System.out.println("exception in doStartTag ColumnTag" + e);
			return SKIP_PAGE;
		}
	}
}
/***************************************************************/