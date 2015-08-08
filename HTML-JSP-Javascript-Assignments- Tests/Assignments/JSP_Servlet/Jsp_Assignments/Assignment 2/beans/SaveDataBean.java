package beans;
import java.io.*;
/**********************************************************/
public class SaveDataBean
{
	private String jspcode;
/**********************************************************/
	public String getJspcode()
	{
		return this.jspcode;
	}
/**********************************************************/
	public void setJspcode(String jspcode)
	{
		this.jspcode = jspcode;
	}
/**********************************************************/
	public void saveToFile(String path)
	{
		try
		{
			 FileOutputStream fw = new FileOutputStream(path);
			 PrintStream ps = new PrintStream(fw);
			 ps.println(jspcode);
			 ps.flush();
			 ps.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
/**********************************************************/