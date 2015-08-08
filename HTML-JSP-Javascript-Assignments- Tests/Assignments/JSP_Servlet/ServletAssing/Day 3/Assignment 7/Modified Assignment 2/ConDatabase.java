import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/************************************************************/
public class ConDatabase implements ServletContextListener
{
	private ServletContext context = null;
/************************************************************/
	public void contextInitialized(ServletContextEvent event)
	{
		this.context = event.getServletContext();
		try
		{
			// create a url string to connect to the oracle server
			String url = "jdbc:oracle:thin:@192.168.12.16:1521:oracle8i";
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection con = DriverManager.getConnection(url , "scott", "tiger");	
			context.setAttribute("con", con);
		}
		catch (SQLException s)
		{
			System.out.println("SQLException occured.");
		}

	}
/************************************************************/
	public void contextDestroyed(ServletContextEvent event)
	{
		this.context = null;
	}
}
/********************************************************************/
