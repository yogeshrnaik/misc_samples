/*****************************************************************************************/
import java.sql.*;
import java.io.*;
/*****************************************************************************************/
public class MSAccessCon 
{
/*****************************************************************************************/
	static 
	{
		try 
		{ 
			// register the driver with DriverManager
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			//DriverManager.setLogStream(System.err);
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	 }
/*****************************************************************************************/
	public static void main(String argv[]) 
	{
		try 
		{
			String id;
			String name;
			// URL is jdbc:db2:dbname
			String url = "jdbc:odbc:MSAccessConTest";
			// connect with default id/password
			Connection con = DriverManager.getConnection(url);;
			System.out.println("Connection : " + con);

			// retrieve data from the database
			System.out.println("Retrieve some data from the database...");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * from Contacts");
			System.out.println("Received results:" + rs);
			// display the result set
			// rs.next() returns false when there are no more rows
			while (rs.next()) 
			{
				id = rs.getString("id");
				name = rs.getString("name");

				System.out.println(" Contact ID = " + id);
				System.out.println(" Contact Name = " + name);
				System.out.println();
			}
			rs.close();
			stmt.close();
			con.close();
		} 
		catch (SQLException ex) 
		{
			System.err.println ("\n*** SQLException caught ***\n" + ex); 
			while (ex != null) 
			{
				System.err.println ("SQLState: " + ex.getSQLState ());
				System.err.println ("Message:  " + ex.getMessage ());
				System.err.println ("Vendor:   " + ex.getErrorCode ());
				ex = ex.getNextException ();
				System.err.println ("");
			}
		}
		catch (java.lang.Exception ex) 
		{
			// Got some other type of exception.  Dump it.
			ex.printStackTrace ();
		}
	} //main
/*****************************************************************************************/
}
/*****************************************************************************************/
