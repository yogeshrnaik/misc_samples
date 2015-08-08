import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/************************************************************/
public class ConDatabase 
{
		public Connection con;
		public PrintStream out;
		private Statement stmt;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ResultSet rs;
		ResultSetMetaData rsmd;
/************************************************************/
	public ConDatabase ()//ToDatabase( )
	{
		try
		{
			// create a url string to connect to the oracle server
			String url = "jdbc:oracle:thin:@192.168.12.16:1521:oracle8i";
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			con = DriverManager.getConnection(url , "scott", "tiger");	
		}
		catch (SQLException s)
		{
			System.out.println("SQLException occured.");
		}
	}
/************************************************************/
	public ResultSet getCatalog()
	{
		ResultSet result;
		try
		{
			Statement st = con.createStatement();
			result = st.executeQuery("select * from BookStore");
			return result;
		}
		catch (Exception e)
		{
			out.println(e);
			return null;
		}
	}
}
/********************************************************************/
