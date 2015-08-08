/*********************************************************************************/
import java.io.*;
import java.util.*;
/********************************************************************************/
public class ExpressionEvaluator
{
	String expression;
	Node root;
	public ExpressionEvaluator(String exp)
	{
		expression = exp;
		root = new Node();
		// Form the expression tree
		formExpTree(expression, root);
		printInorder(root);
		evalExpTree(root);
		System.out.println("\nAnswer = " + root.operand);
	}
/********************************************************************************/
	public static void main(String[] args) throws IOException
	{
		if (args.length == 0 || args == null)
		{
			System.out.println("Usage: java ExpressionEvaluator <expression>");
			System.out.println("E.g.: java ExpressionEvaluator 3+5*5-45");
			System.exit(1);
		}
		ExpressionEvaluator expEval = new ExpressionEvaluator(args[0]);
	}
/********************************************************************************/
	public void printInorder(Node curr)
	{
		if (curr != null)
		{
			printInorder(curr.left);
			if (curr.operator != null)
			{
				System.out.print(curr.operator + " ");
			}
			else
			{
				System.out.print(curr.operand + " ");
			}
			printInorder(curr.right);
		}
	}
/********************************************************************************/
	public void formExpTree(String exp, Node currNode)
	{
		// Get the position of first operator from left to right having 2nd operator precedence (i.e. get position of first occurrence of + or -)
		int pos = getFirstOpr2Pos(exp);
		if (pos == -1)
		{
			// Get the position of first operator from left to right having 1st operator precedence (i.e. get position of first occurrence of * or /)
			pos =  getFirstOpr1Pos(exp);
		}
		// Separate operand 1, operator and operand 2 based on this position
		String operand1 = exp.substring(0, pos);
		String operator = exp.substring(pos, pos+1);
		String operand2 = exp.substring(pos+1);
		// Set the operator to current node
		currNode.operator = operator;
		if (isNum(operand1))
		{
			double d_operand1 = Double.parseDouble(operand1);
			currNode.left = new Node(d_operand1);
		}
		else
		{
			currNode.left = new Node();
			formExpTree(operand1,currNode.left);
		}
		if (isNum(operand2))
		{
			double d_operand2 = Double.parseDouble(operand2);
			currNode.right = new Node(d_operand2);
		}
		else
		{
			currNode.right = new Node();
			formExpTree(operand2,currNode.right);
		}
	}
/********************************************************************************/
	public void evalExpTree(Node curr)
	{
		if (curr.left != null && curr.right != null)
		{
			if (curr.left.operator != null)
			{
				evalExpTree(curr.left);
			}
			if (curr.right.operator != null)
			{
				evalExpTree(curr.right);
			}
			curr.operand = evaluate(curr.left.operand, curr.operator, curr.right.operand);
			curr.left = null;
			curr.right = null;
			curr.operator = null;
		}
	}
/********************************************************************************/
	public double evaluate(double operand1, String operator, double operand2)
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
/********************************************************************************/
	// Get the position of first operator from left to right having 2nd operator precedence (i.e. get position of first occurrence of + or -)
	public int getFirstOpr2Pos(String exp)
	{
		for (int i=0; i<exp.length(); i++)
		{
			switch (exp.charAt(i))
			{
				case '+'	:	return i;
				case '-'	:	return i;
			}
		}
		return -1;
	}
/********************************************************************************/
	// Get the position of first operator from left to right having 1st operator precedence (i.e. get position of first occurrence of * or /)
	public int getFirstOpr1Pos(String exp)
	{
		for (int i=0; i<exp.length(); i++)
		{
			switch (exp.charAt(i))
			{
				case '*'	:	return i;
				case '/'		:	return i;
			}
		}
		return -1;
	}
/********************************************************************************/
	public boolean isNum(String opd)
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
}
/*########################################################*/
class Node
{
	String operator;
	double operand;
	Node left;
	Node right;
/********************************************************************************/
	Node()
	{
		operator = null;
		left = null;
		right = null;
	}
/********************************************************************************/
	Node (String opr)
	{
		operator = opr;
		left = null;
		right = null;
	}
/********************************************************************************/
	Node (double opd)
	{
		operand = opd;
		operator = null;
		left = null;
		right = null;
	}
/********************************************************************************/
}
/*########################################################*/
