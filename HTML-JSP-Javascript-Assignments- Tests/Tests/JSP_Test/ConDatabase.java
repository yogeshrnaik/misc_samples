import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/************************************************************/
public class ConDatabase 
{
		public Connection con;
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
	public ResultSet getUserInfo()
	{
		ResultSet result;
		try
		{
			Statement st = con.createStatement();
			result = st.executeQuery("select uname, passwd from userinfo");
			return result;
		}
		catch (Exception e)
		{
			return null;
		}
	}
/********************************************************************/
	public boolean isValid(String uname, String passwd)
	{
		ResultSet result = getUserInfo();
		try
		{
			while (result.next())
			{
				String realUser = result.getString(1);
				String realPasswd = result.getString(2);
				System.out.println("realUser = " + realUser + "\t realPasswd = " + realPasswd);
				if (realUser.equals(uname) && realPasswd.equals(passwd))
					return true;
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
/********************************************************************/
	public boolean addUser(String uname, String passwd, String email, String address)
	{
		String query = "insert into userinfo values('" + uname + "','" + passwd + 
						"','" + email + "','" + address +  "')";
		System.out.println(query);
		try
		{
			Statement stat = con.createStatement();
			int c = stat.executeUpdate(query);
			if (c > 0)
				return true;
		}
		catch (SQLException e)
		{
			return false;
		}
		return false;
	}
/********************************************************************/
	public boolean isPresent(String uname)
	{
		ResultSet result;
		try
		{
			Statement st = con.createStatement();
			result = st.executeQuery("select * from userinfo");
			while (result.next())
			{
				String realUser = result.getString(1);
				if (realUser.equals(uname))
					return true;
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
/************************************************************/
	public ResultSet getUserInfo(String uname)
	{
		ResultSet result;
		try
		{
			Statement st = con.createStatement();
			result = st.executeQuery("select * from userinfo where uname='" + uname + "'");
			return result;
		}
		catch (Exception e)
		{
			return null;
		}
	}
/********************************************************************/

}