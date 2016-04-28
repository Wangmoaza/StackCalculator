import java.io.*;
import java.util.Stack;
import java.lang.Character;
import java.lang.Long;
import java.lang.Math;
import java.util.EmptyStackException;

/**
 * Converts infix notation to postfix and evalutates the expression.
 * @author Ha-Eun Hwangbo
 * @version 2.0
 */

public class CalculatorTest
{
	private long calResult;
	private String postfix;
	private String input;
	private boolean validity;
	
	public CalculatorTest(String in)
	{
		input = in;
		postfix = "";
		calResult = 0;
		validity = validityCheck(in);

		if (validity)	//only do the block when infix is valid
		{
			InToPost post = new InToPost(input);
			post.doTrans();
			postfix = post.toString();
			calResult = calPostfix();
		}
	}	
	
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("q") == 0)
					break;

				command(input);
			}
			catch (Exception e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	private static void command(String input)
	{
		CalculatorTest calculator = new CalculatorTest(input);
		
		if (!calculator.isValid())
			System.out.println("ERROR");
		
		else
		{
			System.out.println(calculator.getPostfix());
			System.out.println(calculator.getResult());
		}
	}
	
	/**
	 * Get converted postfix expression
	 * @return postfix string
	 */
	public String getPostfix()
	{
		return postfix;
	}
	
	/**
	 * Get calculated result
	 * @return calculated result
	 */
	public long getResult()
	{
		return calResult;
	}
	
	/**
	 * Returns validity of the expression (whether it has any errors)
	 * @return validity of the expression
	 */
	public boolean isValid()
	{
		return validity;
	}
	
	/**
	 * Checks infix validity
	 * @return validity of input infix expression
	 */
	private boolean validityCheck(String s)
	{
		s = s.trim();
		Stack<Character> parenStack = new Stack<>();
		int infixFlag = 0; // operand: +1. operator: +100
		boolean newNumberFlag = true;
		boolean binaryMinusFlag = false;

		int i = 0;

		while(i < s.length())
		{
			char ch = s.charAt(i);
			 
			if (ch == '-')
			{
				if (binaryMinusFlag)
					infixFlag += 100;

				binaryMinusFlag = false;
				newNumberFlag = true;
				i++;
			}
			 
			else if (ch == '*' || ch == '/' || ch == '%' || ch == '+' || ch == '^')
			{
				binaryMinusFlag = false;
				newNumberFlag = true;
				infixFlag += 100;
				i++;
			}
			 
			else if ('0' <= ch && ch <= '9')
			{
				if (newNumberFlag)
					infixFlag += 1;
			    
				binaryMinusFlag = true;
				newNumberFlag = false;
				i++;
			}
			 
			else if (ch == '(')
			{
				// check parentheses closure
				parenStack.push(ch);
				int j;

				try
				{
					for (j = 1; !parenStack.isEmpty() && i+j < s.length(); j++)
					{
					    char ch2 = s.charAt(i+j);
					    if (ch2 == '(')
					        parenStack.push(ch2);
					    else if (ch2 == ')')
					        parenStack.pop();
					} // i+j-1 at index of matching ')'
				}
				
				catch(EmptyStackException e)
				{
					return false;
				}

				if (!parenStack.isEmpty())
				{
					return false;
				}

				// should be full infix expression between closed parentheses
				if (!validityCheck(s.substring(i+1, i+j-1)))
				{
					return false;
				}

				infixFlag += 1;
				binaryMinusFlag = true;
				newNumberFlag = true;
				i += j;
			}
			 
			else if (ch == ')')
			{
				return false;
			}

			else if (ch == 9 || ch == 32) // whitespace
			{
				newNumberFlag = true;
				i++;
			}

			else // invalid character
			{
				return false;
			}

			// validity check
			// each stage should be in <operand> || <operand><operator> || <operand><operator><operand>
			if (infixFlag == 1 || infixFlag == 101 || infixFlag == 0)
			{}

			else if (infixFlag == 102) // full infix expression
				infixFlag = 1;

			else
				return false;
		}

		if (infixFlag != 0)
			return true;

		else // empty string is invalid
			return false;
	}

	/**
	 * Evaluates postfix expression
	 * @return result of calculation
	 */
	private long calPostfix()
	{
		String[] postArr = postfix.split("\\s+");
		Stack<Long> postStack = new Stack<>();
		long result = 0;
		
		for (int i = 0; i < postArr.length; i++)
		{
			String item = postArr[i];
			long op1, op2;
			
			if (isBinaryOp(item))
			{
				op2 = postStack.pop();
				op1 = postStack.pop();
				result = calUnit(op1, op2, item);
				postStack.push(result);
			}
			
			else if (item.equals("~"))	// unary operator
			{
				op1 = postStack.pop();
				result = (-1) * op1;
				postStack.push(result);
				
			}
			
			else // operand
			{
				postStack.push(Long.valueOf(item));
			}
		}
		
		result = postStack.pop();
		return result;
	}
	
	/**
	 * Calculates binary operation (+, -, %, *, /, ^) unit
	 * @param op1 first operand
	 * @param op2 second operand
	 * @param operator binary operator
	 * @return result of unit calculation
	 */
	private long calUnit(long op1, long op2, String operator)
	{
		long result = 0;
		
		try
		{
			switch (operator)
			{
				case "+":
					result = op1 + op2;
					break;
				case "-":
					result = op1 - op2;
					break;
				case "*":
					result = op1 * op2;
					break;
				case "/":
					result = op1 / op2;
					break;
				case "%":
					result = op1 % op2;
					break;
				case "^":
					if (op1 == 0 && op2 < 0)
						validity = false;
					else
						result = (long) Math.pow(op1, op2);
					break;
			}
		}
		
		catch (ArithmeticException e) // catch a/0, a%0
		{
			validity = false;
		}
		return result;
	}
	
	/**
	 * Checks whether input string is a binary operator
	 * @param str operator
	 * @return true if binary operator, otherwise false
	 */
	private boolean isBinaryOp(String str)
	{
		return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("%") || str.equals("^");
	}
	
	/**
	 * This inner class parses infix to postfix.
	 */
	class InToPost 
	{
		// modification from http://www.tutorialspoint.com/javaexamples/data_intopost.htm
		private Stack<Character> theStack;
		private String infix;
		private String output = "";
		   
		public InToPost(String in) 
		{
			infix = in.trim();
			theStack = new Stack<Character>();
		}
		
		/**
		 * Converts infix to postfix
		 * @return converted postfix string
		 */
		public String doTrans() 
		{
			boolean binaryMinusFlag = false;
			boolean newNumberFlag = false;

			for (int j = 0; j < infix.length(); j++) 
			{
				char ch = infix.charAt(j);

				if (ch == '-')
				{
					// discriminate unary and binary
					// - (binary) can only appear right after ')' or digit
		            if (binaryMinusFlag)
		               gotOper(ch);

		            else
		               gotOper('~');
		            
		            binaryMinusFlag = false;
		            newNumberFlag = true;
		         }

				else if (ch == '*' || ch == '/' || ch == '%' || ch == '+' || ch == '^' )
				{
					gotOper(ch);
		            binaryMinusFlag = false;
		            newNumberFlag = true;
				}

				else if (ch == '(')
				{
		            theStack.push(ch);
		            binaryMinusFlag = false;
		            newNumberFlag = true;
				}

				else if (ch == ')')
				{
		            gotParen(ch);
		            binaryMinusFlag = true;
		            newNumberFlag = true;
				}

				else if ('0' <= ch && ch <='9')
				{
		            if (newNumberFlag && output != "")
		               output = output + " " + ch;
		            else
		               output = output + ch;
		            
		            binaryMinusFlag = true;
		            newNumberFlag = false;
		         }

				else if (ch == 9 || ch == 32)
				{
		            newNumberFlag = true;
				}
			}
		
			while (!theStack.isEmpty()) 
			{
		         output = output + " " + theStack.pop();
			}
		      
			return output;
		}

		private int precedence(char ch)
		{
			// precedence: lowest (1) - highest (4)
			// 1: +, - (binary)
			// 2: *, /, %
			// 3: ~ (unary)
			// 4: ^
			switch (ch)
			{
		         case '+':
		         case '-':
		            return 1;
		         case '*':
		         case '/':
		         case '%':
		            return 2;
		         case '~':
		            return 3;
		         case '^':
		            return 4;
		         default:
		            System.out.println("not an operator");
		            return 0;
			}
		}
		   
		private void gotOper(char opThis) 
		{
			int thisPrec = precedence(opThis);

			while (!theStack.isEmpty()) 
			{
				char opTop = theStack.pop();
		         
		         if (opTop == '(') 
		         {
		            theStack.push(opTop);
		            break;
		         }
		         
		         else 
		         {
		            int topPrec = precedence(opTop);
		            
		            if (topPrec < thisPrec) 
		            { 
		               theStack.push(opTop);
		               break;
		            }

		            else if (topPrec == thisPrec && (opThis == '^' || opThis == '~')) // for right-associative operators
		            {
		               theStack.push(opTop);
		               break;
		            }

		            else
		               output = output + " " + opTop;
		         }
			}
			theStack.push(opThis);
		}
		   
		private void gotParen(char ch)
		{ 
			while (!theStack.isEmpty()) 
			{
				char chx = theStack.pop();
		         
		         if (chx == '(') 
		            break; 
		         else
		            output = output + " " + chx; 
			}
		}
		
		@Override
		public String toString()
		{
			return output;
		}
	}
}

