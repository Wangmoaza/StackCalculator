import java.io.*;
import java.util.Stack;
import java.lang.Character;
import java.lang.Long;
import java.lang.Math;
import java.util.EmptyStackException;

/**
 * 
 * @author 하은
 * 1. prefix, postfix 형태로 들어올 때 잡아줘야함
 * 2. () 짝 안맞을 때
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
		validity = simpleValidityCheck();

		if (validity)
		{
			InToPost post = new InToPost(input);
			post.doTrans();
			postfix = post.toString();
			//System.out.println(postfix);
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
	
	public String getPostfix()
	{
		return postfix;
	}
	
	public long getResult()
	{
		return calResult;
	}
	
	public boolean isValid()
	{
		return validity;
	}
	
	private boolean simpleValidityCheck()
	{
		String infix = input.trim();
		String[] infixArr = infix.split("[+]|[-]|[*]|[/]|[%]|[\\^]");
		Stack<Character> parenStack = new Stack<>();
		
		// check if two operands appear consecutively
		for (int i = 0; i < infixArr.length; i++)
		{
			int consecCnt = 0;
			String[] innerArr = infixArr[i].split("\\s+");
			
			for (int j = 0; j < innerArr.length; j++)
			{
				if (!innerArr[j].equals(""))
					consecCnt ++;
			}
			
			if (consecCnt > 1)
				return false;
		}
		
		// check parenthesis closure
		for (int i = 0; i < infix.length(); i++)
		{
			char ch = infix.charAt(i);
		
			try
			{
				if (ch == '(')
					parenStack.push(ch);
				else if (ch == ')')
					parenStack.pop();
			}
			
			catch(EmptyStackException e)
			{
				return false;
			}
		}

		if (!parenStack.isEmpty())
			return false;
		
		return true;
	}
	
	private long calPostfix()
	{
		String[] postArr = postfix.split("\\s+");
		Stack<Long> postStack = new Stack<>();
		long result = 0;
		
		try
		{
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
				
				else if (item.equals("~"))
				{
					op1 = postStack.pop();
					result = (-1) * op1;
					postStack.push(result);
					
				}
				else // item is an operand
					postStack.push(Long.valueOf(item));
			}
			
			result = postStack.pop();
			
			if (!postStack.isEmpty()) // result should have been the only thing left in stack
				validity = false;
		}
		
		catch (EmptyStackException e)
		{
			validity = false;
		}
		
		return result;
	}
	
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
				default:
					System.out.println("wrong operand in calUnit");
			}
		}
		
		catch (ArithmeticException e) // catch a/0, a%0
		{
			validity = false;
		}
		return result;
	}
	
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

