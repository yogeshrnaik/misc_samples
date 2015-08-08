/******************************import statements******************************/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class UploadServlet extends HttpServlet
{
	public void init() throws ServletException
	{
		super.init();
	}
/******************************************************************/
	public void doGet(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		resp.setContentType("text/html");
		
	}
/******************************************************************/
	public void doPost(	HttpServletRequest req,
								HttpServletResponse resp)
									throws ServletException, IOException
	{
		final int bufferSize = 8 *1024;
		final int ONE_MB = 1024 * 1024;
		int contentLen = req.getContentLength();
		if (contentLen > ONE_MB)
		{
			resp.setContentType("text/html");
			ServletOutputStream out = resp.getOutputStream();
			out.println("<HTML><BODY><H1>Error File size must be &lt; 1 MB.</BODY></HTML>");
		}
		else
		{
			ServletInputStream in = req.getInputStream();
			byte[] b = new byte[contentLen];
			int result;
			int totRead = 0;
			try
			{
				result = in.readLine(b, 0, b.length);
				while (result != -1)
				{
					totRead += result;
					result = in.readLine(b, totRead, bufferSize);
				}
			}
			catch (IOException e)	{}
			resp.setContentType("text/html");
			ServletOutputStream out = resp.getOutputStream();
			out.println("<HTML><BODY><PRE>");
			
			String str = new String(b);
			StringTokenizer st = new StringTokenizer(str, "\r\n");
			st.nextToken();
			String strFilePath = getFilepath(st.nextToken());
			st.nextToken();
			String strContent = "";
			int count = st.countTokens();
			for (int i=1; i<count; i++)
			{
				if (st.hasMoreTokens())
				{
					strContent += st.nextToken() + "\n";
					//out.println("Token is : " + st.nextToken() + "\n");
				}
			}
			String filename = strFilePath.substring(strFilePath.lastIndexOf('\\')+1);
			try
			{
				createFile(filename, strContent);	
			}
			catch (Exception e)
			{
				out.println("<H1><FONT color='red'>Could not create file on server side.</FONT></H1>");
				out.println("<H1><FONT color='red'>" + e + "</FONT></H1>");
			}
			out.println("<H3>File Name is : " + filename + "</H3>");
			out.println("<H3>File Contents are : </H3>" + strContent);
		}
	}
/******************************************************************/
	public String getFilepath(String str)
		{
			StringTokenizer st = new StringTokenizer(str, "=");
			int index = str.indexOf("filename=");
			return str.substring(index+10, str.length()-1);
		}
		
/******************************************************************/
	public void createFile(String filename, String strContent) throws Exception
	{
		File file = new File(filename);
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter br = new BufferedWriter(fw);
		br.write(strContent, 0, strContent.length());
		br.flush();	
	}
}
/*************************************************************************/

