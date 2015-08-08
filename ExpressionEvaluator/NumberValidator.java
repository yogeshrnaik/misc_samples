/*********************************************************************************/
import java.io.*;
import java.util.*;
/********************************************************************************/
public class NumberValidator
{
/********************************************************************************/
	public static void main(String[] args) throws IOException
	{
		if (args.length == 0 || args == null)
		{
			System.out.println("Usage: java NumberValidator <number-to-be-validated");
			System.out.println("E.g.: java NumberValidator 25.54");
			System.exit(1);
		}
		if (NumberValidator.isNum(args[0]))
		{
			System.out.println("Number : " + args[0] + " is a VALID Number.");
		}
		else
		{
			System.out.println("Number : " + args[0] + " is NOT a VALID Number.");
		}
	}
/********************************************************************************/
	public static boolean isNum(String opd)
	{
		try
		{
			double temp = Double.parseDouble(opd);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
/********************************************************************************/
}
/********************************************************************************/
