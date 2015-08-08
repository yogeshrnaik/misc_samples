import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class DeleteFile extends HttpServlet 
{
	public PrintWriter pw=null;
	public PrintWriter out=null;
	public ServletContext context=null;

	public void init() throws ServletException
	{
		super.init();
		this.context = getServletContext();
	}
/************************************************************************/
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException 
	{
		out = res.getWriter();
		out.println("<HTML><BODY><TABLE>");
		String queryString = req.getQueryString();
		if(queryString == null)
		{
			displayFiles("/");
		} 
		else if (req.getParameter("dirname") != null)
		{
			displayFiles(req.getParameter("dirname"));
		}
		else
		{
			deleteFile(req);
		}
		out.println("</TABLE></BODY></HTML>");
	}
/************************************************************************/
	public void deleteFile(HttpServletRequest req)
	{
		try
		{
			String fname = req.getParameter("filename");
			File fdel = new File(fname);
			fdel.delete();
		}
		catch(Exception e)
		{	
			out.println("<H1><Font Color='red'>Could not Delete File.<BR><BR>"+e+"</H1></Font>");	
		}
	}
	public void displayFiles(String dirname)
	{
		Set root = context.getResourcePaths(dirname);
		Iterator it = root.iterator();
		String str = "";
		String filePath = "";
		while(it.hasNext())
		{
			str = (String)it.next();
			//out.println("original = " + str + " <BR>");
			if (!dirname.equals("/"))
			{
				str = str.substring(0, str.lastIndexOf("//")) + str.substring(str.lastIndexOf("//")+1);
				//out.println("modified = " + str + "<BR>");
			}
			if (!str.endsWith("/"))	// file
			{	  
				filePath  =  str;
				filePath = "/DeleteFileCtx"+filePath;
				out.println("<TR> <TD> <B> File Name : </B>");
				out.println("<TD> " + filePath);
				out.println("<TD> <A href = /DeleteFileCtx/Delete?filename="+filePath+" >"+"Delete File"+"</A>");
			}
			else if (str.endsWith("/"))	// directory 
			{	
				out.println("<TR><TD><B> Directory Name : </B>");
				out.println("<TD><A href = /DeleteFileCtx/Delete?dirname="+str+">"+str+"</A>");
				out.println("<TD><BR>");
			}
		}
	}
/************************************************************************/
}
