import java.io.*;
import java.util.Stack;
import java.lang.Character;
import java.lang.Long;
import java.lang.Math;

/**
 * @author 하은
 * need to do:
 * 1. illegal expression 처리
 */

public class CalculatorTest
{
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
		// TODO : 아래 문장을 삭제하고 구현해라.
		System.out.println("<< command 함수에서 " + input + " 명령을 처리할 예정입니다 >>");
	}
	
	private static void calPostfix(String in)
	{
		String[] postArr = in.split("\\s+");
		Stack<Long> postStack = new Stack<>();
		
		for (int i = 0; i < in.length(); i++)
		{
			String item = postArr[i];
			long op1, op2, result;

			if (isBinaryOp(item)) // 바꿔야함 binary unary 나눠야한다
			{
				op2 = postStack.pop();
				op1 = postStack.pop();
				result = calUnit(op1, op2, item);
			}
			
			else if (isUnaryOp(item))
			{
				op1 = postStack.pop();
				result = calUnit(op1, item);
				
			}
			else // item is an operand
				postStack.push(Long.valueOf(item));
		}
		
	}
	
	private static long calUnit(long op1, long op2, String operator)
	{
		long result = 0;
		
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
				// TODO
				// exception handling: a/0
				result = op1 / op2;
				break;
			case "%":
				// TODO
				// exception handling: a%0
				result = op1 % op2;
				break;
			case "^":
				// TODO
				// exception handling: a^(negative)
				result = (long) Math.pow(op1, op2);
				break;
			default:
				System.out.println("wrong operand in calUnit");
				result = 0;
		}
		
		return result;
	}
	
	private static long calUnit(long op, String operator)
	{
		long result;
		
		
	}
	
	private static boolean isBinaryOp(String str)
	{
		return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("%") || str.equals("^");
	}
	
	private static boolean isUnaryOp(String str)
	{
		return str.equals("~");
	}
	
	class InToPost 
	{
		// modification from http://www.tutorialspoint.com/javaexamples/data_intopost.htm
		private Stack<Character> theStack;
		private String input;
		private String output = "";
		   
		public InToPost(String in) 
		{
			input = in.trim();
			theStack = new Stack<Character>();
		}
		   
		public String doTrans() 
		{
			boolean binaryMinusFlag = false;
			boolean newNumberFlag = false;

			for (int j = 0; j < input.length(); j++) 
			{
				char ch = input.charAt(j);

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

				else if (ch == '\t' || ch == ' ')
				{
		            newNumberFlag = true;
				}

				else
				{
					System.out.println("ERROR");
					break;
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

		            else if (topPrec == thisPrec && (opThis == '^' || opThis == '~'))
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

