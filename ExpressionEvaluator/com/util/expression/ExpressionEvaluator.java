/******************************************************************************************/
package com.util.expression;
/******************************************************************************************/
import java.io.*;
import java.util.*;
/******************************************************************************************/
public class ExpressionEvaluator
{
/******************************************************************************************/
	private static ArrayList postFixExpression = new ArrayList();
/******************************************************************************************/
	public static double evalInfixExpression (String infixExp) throws InvalidExpression
	{
		if (infixExp == null || infixExp.length() == 0)
		{
			throw new InvalidExpression("Expression Cannot be NULL.");
		}
		convertToPostFix(infixExp);
		//printPostFixExpression();
		return evaluatePostFixArrayList();
	}
/******************************************************************************************/
	private static void convertToPostFix(String infixExp) throws InvalidExpression
	{
		String temp = "";
		String topOfStack = "";
		Stack stack = new Stack();
		try
		{
			for (int i=0; i<infixExp.length(); i++)
			{
				char ch = infixExp.charAt(i);
				switch (ch)
				{
					case '+'	:	
					case '-'	:	
					case '*'	:	
					case '/'	 	:	if (temp.length() > 0)
										{
											postFixExpression.add(temp);
											temp = "";
										}
										if (!stack.empty())
										{
											topOfStack = (String)stack.peek();
										}
										while (!stack.empty() && !(topOfStack = (String)stack.peek()).equals("(") && getPrecedence("" + ch) <= getPrecedence(topOfStack))
										{
											postFixExpression.add(stack.pop());
										}
										stack.push(new String("" + ch));
										break;
					case '('	:	stack.push(new String (""+ch));
										break;
					case ')'	:	if (temp.length() > 0)
										{
											postFixExpression.add(temp);
											temp = "";
										}
										topOfStack = "";
										while (!(topOfStack = (String)stack.peek()).equals("("))
										{
											postFixExpression.add(stack.pop());
										}
										stack.pop(); // remove the open parenthesis '(' from stack
										break;
					default		:	temp = temp + ch;
				}
				//System.out.println(ch + "\t" + stack.toString() + "\t" + postFixExpression.toString());
			}
			if (temp.length() > 0)
			{
					postFixExpression.add(temp);
					temp = "";
			}
			while (!stack.empty())
			{
				postFixExpression.add(stack.pop());
			}			
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			throw new InvalidExpression("Expression is INVALID.");
		}
	}
/******************************************************************************************/
	private static void printPostFixExpression()
	{
		System.out.print("Post Order Expression = ");
		for (int i=0; i<postFixExpression.size(); i++)
		{
			System.out.print((String)postFixExpression.get(i) + " ");
		}
		System.out.println();
	}
/******************************************************************************************/
	private static double evaluatePostFixArrayList() throws InvalidExpression
	{
		Stack stack = new Stack();
		try 
		{
			for (int i=0; i<postFixExpression.size(); i++)
			{
				String temp = (String)postFixExpression.get(i);
				if (temp.equals("+") || temp.equals("-") || temp.equals("*") || temp.equals("/"))
				{
					double operand1 = Double.parseDouble((String)stack.pop());
					double operand2 = Double.parseDouble((String)stack.pop());
					String operator = temp;
					double ans = evaluate(operand2, operator, operand1);
					stack.push(new String(ans+""));
				}
				else
				{
					stack.push(temp);
				}
			}
			return Double.parseDouble((String)stack.pop());
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			throw new InvalidExpression("Expression is INVALID.");
		}
	}
/******************************************************************************************/
	private static double evaluate(double operand1, String operator, double operand2)
	{
		if (operator.equals("+"))
		{
			return (operand1 + operand2);
		}
		else if (operator.equals("-"))
		{
			return (operand1 - operand2);
		}
		else if (operator.equals("*"))
		{
			return (operand1 * operand2);
		}
		else if (operator.equals("/"))
		{
			return (operand1 / operand2);
		}
		return 0.0/0.0;
	}
/******************************************************************************************/
	private static int getPrecedence(String operator)
	{
		int precedence = 0;
		if (operator.equals("(") || operator.equals(")"))
		{
			precedence = 3;
		}
		else if (operator.equals("*") || operator.equals("/"))
		{
			precedence = 2;
		}
		else if (operator.equals("+") || operator.equals("-"))
		{
			precedence = 1;
		}
		return precedence;
	}
/******************************************************************************************/
	public static double evalPostfixExpression(String postfixExp) throws InvalidExpression
	{
		if (postfixExp == null || postfixExp.length() == 0)
		{
			throw new InvalidExpression("Expression cannot be NULL.");
		}
		return 0.0/0.0;
	}
}
/******************************************************************************************/
