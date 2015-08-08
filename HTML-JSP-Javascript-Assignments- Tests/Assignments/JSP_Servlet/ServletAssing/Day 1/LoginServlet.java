/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class LoginServlet extends HttpServlet
{
	Hashtable database;
	static int count = 0;
	public void init() throws ServletException
	{
		super.init();
		database = new Hashtable();
		database.put("admin", "admin");
		database.put("a", "a");
		database.put("b", "b");
		database.put("c", "c");
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
			if (database.get(uname) == null)
			{
				database.put(uname, passwd);
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
			String correctOldPasswd = (String)database.get(uname);
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
				database.remove(uname);
				database.put(uname, newPasswd);
				// send a message saying that password is changed
				out.write("<H1>Password of user : " + uname + " is changed successfully.</H1>");
			}
		}
		//out.write("</BODY>");
	}
/******************************************************************/
	public boolean validate(HttpServletRequest request)
	{
		String uname = request.getParameter("uname");
		String passwd = request.getParameter("passwd");
		String correctPasswd = (String)database.get(uname);
		if (correctPasswd == null || !correctPasswd.trim().equals(passwd.trim()))
		{
			return false;
		}
		return true;
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
}
/*************************************************************************/

