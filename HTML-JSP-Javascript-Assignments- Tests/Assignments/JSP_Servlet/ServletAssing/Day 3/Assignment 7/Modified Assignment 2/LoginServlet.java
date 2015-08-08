/******************************import statements******************************/
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class LoginServlet extends HttpServlet
{
	static int count = 0;
	public void init() throws ServletException
	{
		super.init();
	}
/******************************************************************/
	public void doGet(	HttpServletRequest request,
								HttpServletResponse response)
									throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		String queryString = request.getQueryString();
		if (queryString == null)
		{
			sendFirstResponse(response, "Enter valid Username and Password to Login.");
		}
		else if (getIDfromQuery(queryString).equals("new"))
		{
			//out.write("Sending new User page from get" + (++count));
			sendNewUserPage(response, "Please enter Username and Password.");
		}
		else if (getIDfromQuery(queryString).equals("change"))
		{
			//out.write("Sending change passwd page from get" );
			sendChangePasswordPage(response, "<B>Change password</B>");
		}
		//out.write("</BODY>");
	}
/******************************************************************/
	public void doPost(	HttpServletRequest request,
								HttpServletResponse response)
									throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		String queryString = request.getQueryString();
		String ID = getIDfromQuery(queryString);
		//out.write("<BODY> query string is " + queryString);
		if (ID.equals("login"))
		{
			//out.write(validate(request)+"");
			if (validate(request))
			{
				String messages[] = {"Welcome " + request.getParameter("uname").trim() + "!"};
				sendWelcomePage(response, messages);
			}
			else
			{
				sendFirstResponse(response, "Please enter username and password correcly.");
			}

		}
		else if (ID.equals("new"))
		{
			//out.write("Sending new User page from post"  +(++count));
			sendNewUserPage(response, "Please enter the username and password currectly.");
		}
		else if (ID.equals("add"))
		{
			//out.write("adding User to database");
			String uname = request.getParameter("uname").trim();
			String passwd = request.getParameter("passwd").trim();
			if (!isPresent(uname) && addUser(uname, passwd))
			{
				String messages[] =	{	"Welcome " + request.getParameter("uname").trim() + "!", 
													"A new account is created for you."
												};
				sendWelcomePage(response, messages);
			}
			else
			{
				sendNewUserPage(response, "Please select another username.");
			}
		}
		else if (ID.equals("change"))
		{
			//out.write("changing password of the User from post\n");
			String uname = request.getParameter("uname").trim();
			String oldPasswd = request.getParameter("oldPasswd").trim();
			String newPasswd = request.getParameter("newPasswd").trim();
			String retypePasswd = request.getParameter("retypePasswd").trim();
			String correctOldPasswd = getPasswd(uname);
			if (correctOldPasswd == null)
			{
				sendChangePasswordPage(response, "No account exists for Username : " + uname);
			}
			else if (!correctOldPasswd.equals(oldPasswd))
			{
				sendChangePasswordPage(response, "Please enter Correct Old Password for Username : " + uname);
			}
			else if (!newPasswd.equals(retypePasswd))
			{
				sendChangePasswordPage(response, "Please retype new Password correctly.");
			}
			else
			{
				// change password
				if (changePasswd(uname, newPasswd))
				{
					// send a message saying that password is changed
					out.write("<H1>Password of user : " + uname + " is changed successfully.</H1>");
				}
				else
				{
					out.write("<H1>Password of user : " + uname + " is not changed successfully.</H1>");
				}
			}
		}
		//out.write("</BODY>");
	}
/******************************************************************/
	public boolean validate(HttpServletRequest request)
	{
		String uname = request.getParameter("uname");
		String passwd = request.getParameter("passwd");
		return isValid(uname, passwd);
	}
/******************************************************************/
	public String getIDfromQuery(String queryString)
	{
		StringTokenizer st = new StringTokenizer(queryString, "=");
		st.nextToken();
		return st.nextToken();
	}
/******************************************************************/
	public void sendFirstResponse(HttpServletResponse response, String message) 
												throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		String strResponse = "<HTML>\n";
		strResponse += "<BODY>";
		strResponse += message;
		strResponse += "<FORM method='post' action='http://localhost:8000/LoginServletCtx/Login?id=login'>";
		strResponse += "<TABLE>";
		strResponse += "<TR> <TD> Username : ";
		strResponse += "<TD><INPUT type='text' name='uname'>";
		strResponse += "<TR> <TD> Password : ";
		strResponse += "<TD><INPUT type='password' name='passwd'>";
		strResponse += "<TR> <TD> <A href='http://localhost:8000/LoginServletCtx/Login?id=new'>New user</A>";
		strResponse += "<TD> <A href='http://localhost:8000/LoginServletCtx/Login?id=change'>Change Password</A>";
		strResponse += "</TABLE>";
		strResponse += "<BR>";
		strResponse += "<INPUT type='submit' value='Login'>";
		strResponse += "</FORM>";
		strResponse += "</BODY>";
		strResponse += "</HTML>";
		out.write(strResponse);
		out.close();
	}
/******************************************************************/
	public void sendWelcomePage(HttpServletResponse response, String messages[]) 
												throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		String strResponse = "<HTML>\n";
		strResponse += "<BODY>";
		for (int i=0; i<messages.length; i++)
			strResponse += "<H1>" + messages[i] + "</H1>";
		strResponse += "</BODY>";
		strResponse += "</HTML>";
		out.write(strResponse);
		out.close();
	}
/******************************************************************/
	public void sendNewUserPage(HttpServletResponse response, String message) 
												throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		String strResponse = "<HTML>\n";
		strResponse += "<SCRIPT>\n";
		strResponse += "function validatePasswd()\n";
		strResponse += "{\n";
		strResponse += "var uname = frm.uname.value\n";
		strResponse += "var passwd = frm.passwd.value\n";
		strResponse += "var retypePasswd = frm.retypePasswd.value\n";
		strResponse += "if (uname == \"\")\n";
		strResponse += "{\n";
		strResponse += "alert(\"Please enter Username.\")\n";
		strResponse += "}\n";
		strResponse += "else if (passwd == \"\")\n";
		strResponse += "{\n";
		strResponse += "alert(\"Please enter Password.\")\n";
		strResponse += "}\n";
		strResponse += "else if (passwd != retypePasswd)\n";
		strResponse += "{\n";
		strResponse += "alert(\"Password do not match\")\n";
		strResponse += "}\n";
		strResponse += "else\n";
		strResponse += "{\n";
		strResponse += "frm.action = \"http://localhost:8000/LoginServletCtx/Login?id=add\"\n";
		strResponse += "}\n";
		strResponse += "}\n";
		strResponse += "</SCRIPT>\n";

		strResponse += "<BODY>";
		strResponse += message;
		strResponse += "<FORM id='frm' method='post'>";
		strResponse += "<TABLE>";
		strResponse += "<TR> <TD> Username : ";
		strResponse += "<TD><INPUT type='text' name='uname'>";
		strResponse += "<TR> <TD> Password : ";
		strResponse += "<TD><INPUT type='password' name='passwd'>";
		strResponse += "<TR> <TD> Retype Password : ";
		strResponse += "<TD><INPUT type='password' name='retypePasswd'>";
		strResponse += "</TABLE><BR>";
		strResponse += "<INPUT type='submit' value='Add' onclick='validatePasswd()'>";
		strResponse += "</FORM>";
		strResponse += "</BODY>";
		strResponse += "</HTML>";
		out.write(strResponse);
		out.close();
	}
/******************************************************************/
	public void sendChangePasswordPage(HttpServletResponse response, String message) 
												throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		String strResponse = "<HTML>\n";


		strResponse += "<SCRIPT>\n";
		strResponse += "function validatePasswd()\n";
		strResponse += "{\n";
		strResponse += "var uname = frm.uname.value\n";
		strResponse += "var oldPasswd = frm.oldPasswd.value\n";
		strResponse += "var newPasswd = frm.newPasswd.value\n";
		strResponse += "var retypePasswd = frm.retypePasswd.value\n";
		strResponse += "if (uname == \"\")\n";
		strResponse += "alert(\"Please enter Username.\")\n";
		strResponse += "else if (oldPasswd == \"\")\n";
		strResponse += "alert(\"Please enter Password.\")\n";
		strResponse += "else if (newPasswd == \"\")\n";
		strResponse += "alert(\"Please enter New Password.\")\n";
		strResponse += "else if (newPasswd != retypePasswd)\n";
		strResponse += "alert(\"Retype Password Correctly.\")\n";
		strResponse += "else\n";
		strResponse += "{\n";
		strResponse += "frm.action = \"http://localhost:8000/LoginServletCtx/Login?id=change\"\n";
		strResponse += "}\n";
		strResponse += "}\n";
		strResponse += "</SCRIPT>\n";

		strResponse += "<BODY>";
		strResponse += message;
		strResponse += "<FORM id='frm' method='post'>";
		strResponse += "<TABLE>";
		strResponse += "<TR> <TD> Username : ";
		strResponse += "<TD><INPUT type='text' name='uname'>";
		strResponse += "<TR> <TD> Old Password : ";
		strResponse += "<TD><INPUT type='password' name='oldPasswd'>";
		strResponse += "<TR> <TD> New Password : ";
		strResponse += "<TD><INPUT type='password' name='newPasswd'>";
		strResponse += "<TR> <TD> Retype New Password : ";
		strResponse += "<TD><INPUT type='password' name='retypePasswd'>";
		strResponse += "</TABLE><BR>";
		strResponse += "<INPUT type='submit' value='Change Password' onclick='validatePasswd()'>";
		strResponse += "</FORM>";
		strResponse += "</BODY>";
		strResponse += "</HTML>";
		out.write(strResponse);
		out.close();
	}
/***********************************************************************/
	public boolean isValid(String uname, String passwd)
	{
		ResultSet result;
		try
		{
			Connection con = (Connection)getServletContext().getAttribute("con");
			Statement st = con.createStatement();
			result = st.executeQuery("select * from login");
			while (result.next())
			{
				String realUser = result.getString(1);
				String realPasswd = result.getString(2);
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
/************************************************************/
	public boolean addUser(String uname, String passwd)
	{
		String query = "insert into login values('" + uname + "','" + passwd + "')";
		try
		{
			Connection con = (Connection)getServletContext().getAttribute("con");
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
/************************************************************/
	public boolean changePasswd(String uname, String passwd)
	{
		// update login set password='aa'  where username='a';
		String query = "update login set password='" + passwd + "' where username='"+uname+"'";
		try
		{
			Connection con = (Connection)getServletContext().getAttribute("con");
			Statement stat = con.createStatement();
			int c = stat.executeUpdate(query);
			if (c > 0)
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			return false;
		}
		return false;
	}
/************************************************************/
	public boolean isPresent(String uname)
	{
		try
		{
			Connection con = (Connection)getServletContext().getAttribute("con");
			Statement stat = con.createStatement();
			ResultSet result = stat.executeQuery("select count(*) from login where username='" + uname + "'");
			if (result.next())
			{
				int count = result.getInt(1);
				if (count >= 1)
					return true;
			}
		}
		catch (SQLException e)
		{
			return false;
		}
		return false;
	}
/************************************************************/
	public String getPasswd(String uname)
	{
		String query = "select password from login where username='"+uname+"'";
		try
		{
			Connection con = (Connection)getServletContext().getAttribute("con");
			Statement stat = con.createStatement();
			ResultSet result = stat.executeQuery(query);
			if (result.next())
			{
				String passwd = result.getString(1);
				return passwd;
			}
		}
		catch (SQLException e)
		{
			return null;
		}
		return null;
	}
}
/*************************************************************************/

