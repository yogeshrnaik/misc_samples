package beans;
/******************************import statements******************************/
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class JDBCBean
{
	private String driver;
	private String url;
	private String username;
	private String password;
	private String query;
	private Vector data;
/*************************************************************/
	private Connection con;
	private ResultSet rs;
	private ResultSetMetaData rsmd;
/*************************************************************/
	public String getDriver()			{	return driver;				}
	public String getUrl()				{	return url;					}
	public String getUsername()	{	return username;		}
	public String getPassword()		{	return password;		}
	public String getQuery()			{	return query;				}
	public Vector getData()			{	return data;				}
/*************************************************************/
	public void setDriver(String value)			{	driver = value;			}
	public void setUrl(String value)				{	url = value;				}
	public void setUsername(String value)		{	username = value;		}
	public void setPassword(String value)		{	password = value;		}
	public void setQuery(String value)			{	query = value;			}
	public void setData(Vector value)				{	data = value;				}
/*************************************************************/
	public int getColumnCount()
	{
		try
		{
			if (rsmd != null)
				return rsmd.getColumnCount();
		}
		catch (Exception e)
		{
			return 0;
		}
		return 0;
	}
/*************************************************************/
	public String getColumnLabel(int col)
	{
		try
		{
			if (rsmd != null)
				return rsmd.getColumnLabel(col);
		}
		catch (Exception e)
		{
			return "None";
		}
		return "None";
	}
/*************************************************************/
	public int getRowCount()
	{
		try
		{
			if (query.trim().charAt(0) != 'S' || query.trim().charAt(0) != 's')
			{
				if (execute() == 1)
				{
					int count = 0;
					while (rs.next())
					{
						count++;
					}
					return count;
				}
			}
		}
		catch (Exception e)
		{
			return 0;
		}
		return 0;
	}
/*************************************************************/
	public String getCell(int row, int col)
	{
		try
		{
			if (query.trim().charAt(0) != 'S' || query.trim().charAt(0) != 's')
			{
				if (execute() == 1)
				{
					int count = 0;
					rsmd = rs.getMetaData();
					for (int i=1; i<row; i++)
					{
						rs.next();
					}
					return rs.getString(col);
				}
			}
		}
		catch (Exception e)
		{
			return "None";
		}
		return "None";
	}
/*************************************************************/
	public boolean isResultSetGenerated()
	{
		try
		{
			if (query.trim().charAt(0) != 'S' || query.trim().charAt(0) != 's')
				return (execute() == 1);
		}
		catch (Exception e)
		{
			return false;
		}
		return false;
	}
/*************************************************************/
	public int execute()
	{
		String q = query.trim();
		try
		{
			if (q.charAt(0) == 'S'  || q.charAt(0) == 's')
			{
				Statement stat = con.createStatement();
				rs = stat.executeQuery(q);
				data = new Vector(2, 3);
				rsmd = rs.getMetaData();
				String allCols[] = new String[rsmd.getColumnCount()];
				for (int i=0; i<allCols.length; i++)
				{
					allCols[i] = rsmd.getColumnLabel(i+1);
				}
				data.addElement(allCols);
				while (rs.next())
				{
					allCols = new String[rsmd.getColumnCount()];
					for (int i=0; i<allCols.length; i++)
					{
						allCols[i] = rs.getString(i+1);
					}
					data.addElement(allCols);
				}
				if (data.size() > 0)
				{
					return 0;
				}
			}
			else
			{
				data = null;
				Statement stat = con.createStatement();
				int rows = stat.executeUpdate(q);
				if (rows > 0)
				{
					return rows;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			return -2;
		}
		return -3;
	}
/*************************************************************/
	public boolean connectToDatabase() throws Exception
	{
		try
		{
			// create a url string to connect to the oracle server
			Class.forName(driver);
			//System.out.println("Drivers Registered");
			con = DriverManager.getConnection(url , username, password);
			//System.out.println("Got connection");
		}
		catch (SQLException s)
		{
			System.out.println("SQLException occured.");
			return false;
		}
		return (con == null) ? false : true;
	}
}
/*************************************************************************/
