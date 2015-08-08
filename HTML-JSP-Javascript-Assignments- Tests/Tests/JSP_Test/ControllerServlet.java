/******************************import statements******************************/
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class ControllerServlet extends HttpServlet
{
	ConDatabase conDB;
	public void init() throws ServletException
	{
		super.init();
		conDB = new ConDatabase();
	}
/******************************************************************/
	public void doGet(	HttpServletRequest request,
								HttpServletResponse response)
									throws ServletException, IOException
	{
		doPost(request, response);
	}
/******************************************************************/
	public void doPost(	HttpServletRequest request,
								HttpServletResponse response)
									throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		String str = request.getQueryString();
		if (request.getSession(true).getAttribute("connection") == null)
		{
			HttpSession hs = request.getSession(true);
			hs.setAttribute("connection", conDB);
		}
		
		if (str == null)
		{
			// registration page
			//out.println("query string is null");
			response.sendRedirect("registration");
		}
		else
		{
			String action = request.getParameter("action");
			if (action.equals("registration"))
			{
				//out.println("query string is : " + action);
				response.sendRedirect("registration");
			}
			else if (action.equals("registerme"))
			{
				System.out.println("query string is : " + action);
				//out.println("query string is : " + action);
				// add all parameters in the context and send registration page
				String uname = request.getParameter("runame");
				String passwd = request.getParameter("rpasswd");
				String email = request.getParameter("remail");
				String address = request.getParameter("raddress");
				System.out.println("uname : " + uname +"\tpasswd " + passwd + 
									"\temail " + email +"\taddress " + address);
				HttpSession hs = request.getSession(true);
				hs.setAttribute("runame", uname);
				hs.setAttribute("rpasswd", passwd);
				hs.setAttribute("remail", email);
				hs.setAttribute("raddress", address);
				response.sendRedirect("registration");
			}
			else if (action.equals("login"))
			{
				// show login page
				response.sendRedirect("login");
			}
			else if (action.equals("logme"))
			{
				out.println("query string is : " + action);
				// add all parameters in the context and send login page
				String uname = request.getParameter("luname");
				String passwd = request.getParameter("lpasswd");
				System.out.println("luname : " + uname +"\tlpasswd " + passwd);
				HttpSession hs = request.getSession(true);
				hs.setAttribute("luname", uname);
				hs.setAttribute("lpasswd", passwd);
				response.sendRedirect("login");
			}
		}
	}
}
/*************************************************************************/

