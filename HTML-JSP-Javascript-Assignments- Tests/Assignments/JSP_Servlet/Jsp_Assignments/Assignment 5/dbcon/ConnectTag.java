/***************************************************************/
package dbcon;
/***************************************************************/
import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.sql.*;
/***************************************************************/
public class ConnectTag extends TagSupport
{
	String id,url,driver,user,password;
/***************************************************************/
	public String getId()				{		return this.id;					}
	public String getUrl()				{		return this.url;				}
	public String getDriver()			{		return this.driver;			}
	public String getUser()			{		return this.driver;			}
	public String getPassword()		{		return this.password;		}
/***************************************************************/
	public void setId(String id)							{		this.id = id;								}
	public void setUrl(String url)						{		this.url = url;								}
	public void setDriver(String driver)				{		this.driver = driver;					}
	public void setUser(String user)					{		this.user = user;						}
	public void setPassword(String password)		{		 this.password = password;			}
/***************************************************************/	
	public int doStartTag() throws JspTagException 
	{
		try
		{
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url,user,password);
			pageContext.getSession().setAttribute(id, connection);
			return EVAL_BODY_INCLUDE;
		}
		catch(Exception e)
		{
			System.out.println("exception in ConnectTag doStart " + e);
			return SKIP_PAGE;
		}
	}
/***************************************************************/
}

