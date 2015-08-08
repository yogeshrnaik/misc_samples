/******************************************************************************************/
import java.io.*;
import com.util.expression.ExpressionEvaluator;
import com.util.expression.InvalidExpression;
/******************************************************************************************/
public class ExpressionEvaluatorDemo
{
	public static void main(String[] args) throws IOException, InvalidExpression
	{
		BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			System.out.println("\t\tSelect Expression Type..........");
			System.out.println("\t\t 1. Infix Expression");
			System.out.println("\t\t 2. Postfix Expression");
			System.out.println("\t\t 3. Exit");
			String option = sin.readLine();
			option = option.trim();
			if (option.equals("1"))
			{
				System.out.print("Enter Infix Expression : ");
				String infixExp = sin.readLine();
				double value = ExpressionEvaluator.evalInfixExpression(infixExp.trim());
				System.out.println("Value of expression = " + value);
			}
			else if (option.equals("2"))
			{
				System.out.print("Enter Post Fix Expression : ");
				String infixExp = sin.readLine();
				double value = ExpressionEvaluator.evalPostfixExpression(infixExp.trim());
				System.out.println("Value of expression = " + value);
			}
			else 
			{
				System.exit(1);
			}
		}	
	}
}
/******************************************************************************************/
