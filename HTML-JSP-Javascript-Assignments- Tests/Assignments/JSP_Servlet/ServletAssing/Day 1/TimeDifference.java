/******************************import statements******************************/
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class TimeDifference extends HttpServlet
{
	public void doGet(	HttpServletRequest request,
								HttpServletResponse response)
									throws ServletException, IOException
	{
		PrintWriter out;
		long serTime = System.currentTimeMillis();
		String strResponse = "<HTML><SCRIPT>";
		strResponse += "var serTime = " + serTime +"\n";
		strResponse += "var clientTime = new Date()" + "\n";
		strResponse += "document.write(\"Time Difference = \")" + "\n";
		strResponse += "document.write(clientTime-serTime)" + "\n";
		strResponse += "document.write(\" milli-seconds.\")" + "\n";
		strResponse += "</SCRIPT></HTML>";

		response.setContentType("text/html");
		out = response.getWriter();
		out.write(strResponse);
		out.close();
	}
}
/*************************************************************************/
