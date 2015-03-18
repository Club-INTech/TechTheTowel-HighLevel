import org.junit.Test;

import tests.JUnit_Test;

public class JUnit_ReadingTest extends JUnit_Test
{
	@Test
	public void test() throws Exception
	{
		String inputLines="-95.751775";

		
		if(!isAsciiExtended(inputLines))
		{
			log.critical("='( , renvoi de "+inputLines,this);
		}
		if(!isNumber(inputLines))
		{
			log.critical("='( , renvoi de "+inputLines,this);
		}
		else if(! (inputLines.matches("\\A\\p{Digits}*\\z") && (inputLines.equals(".") ) ) )
		{
			log.critical("='( , renvoi de "+inputLines,this);
		}	
		else
		{
			log.critical("=D",this);
		}
	}
	
	public boolean isAsciiExtended(String inputLines) throws Exception
	{
		Boolean isAsciiExtended=true;
		for (int i = 0; i < inputLines.length(); i++) 
		{
	        int characterSet = inputLines.charAt(i);
	        if (characterSet > 259) 
	        {
				isAsciiExtended=false;
                log.critical(inputLines+"n'est pas ASCII", this);
				return isAsciiExtended;
	        }
	    }
		return isAsciiExtended;
	}
		
	public boolean isNumber(String inputLines) throws Exception
	{
		Boolean isNumber=true;
		for (int i = 0; i < inputLines.length(); i++) 
		{
	        if (!   ( inputLines.toCharArray()[i]=='-'  || inputLines.toCharArray()[i]=='.' || inputLines.toCharArray()[i]=='0' 
	        		|| inputLines.toCharArray()[i]=='1' || inputLines.toCharArray()[i]=='2' || inputLines.toCharArray()[i]=='3' 
	        		|| inputLines.toCharArray()[i]=='4' || inputLines.toCharArray()[i]=='5' || inputLines.toCharArray()[i]=='6' 
	        		|| inputLines.toCharArray()[i]=='7' || inputLines.toCharArray()[i]=='8' || inputLines.toCharArray()[i]=='9' ) )
	        {
	        	isNumber=false;
                log.critical(inputLines.toCharArray()[i]+"n'est pas un nombre", this);
				return isNumber;
	        }
	    }
		return isNumber;
	}
}
	