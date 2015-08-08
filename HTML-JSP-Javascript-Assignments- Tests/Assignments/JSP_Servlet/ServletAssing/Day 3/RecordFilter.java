/******************************import statements******************************/
import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import javax.servlet.*;
import javax.servlet.http.*;
/*********************************public class*********************************/
public class RecordFilter implements Filter
{
	private FilterConfig filterConfig;
	public void init(FilterConfig filterConfig)
          throws ServletException
	{
		this.filterConfig = filterConfig;
	}
/******************************************************************/
	public void destroy()
	{
		filterConfig = null;
	}
/******************************************************************/
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
									throws ServletException, IOException
	{
		if (filterConfig == null)
			return;
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		writer.println("Request received at " + new Timestamp(System.currentTimeMillis()));
		HttpServletRequest hreq = (HttpServletRequest)req;
		writer.println("Name : " + hreq.getParameter("name"));
		writer.println("Credit Card No. : " + hreq.getParameter("cardno"));
		writer.flush();
		filterConfig.getServletContext().log(sw.getBuffer().toString());
		//chain.doFilter(req, resp);
	}
}
/*************************************************************************/

