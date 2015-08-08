/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	QuesAnsHolder
 * Description		:	Reads the "database.txt" file and 
							creates a vector containing objects of QuestionAns class
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.io.*;
import java.util.*;
/*********************************public class*********************************/
public class QuesAnsHolder
{
	Vector database;
	final static String FILENAME = "database.txt";
	public QuesAnsHolder()
	{
		database = new Vector();
		loadFile();
	}
	public void loadFile()
	{
		try
		{
			FileReader fr = new FileReader(FILENAME);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null)
			{
				QuestionAns qa = getQuesAns(line);
				database.add(qa);
				//System.out.println();
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception occured while reading database.txt file.");
			System.exit(0);
		}
	}
	public QuestionAns getQuesAns(String line)
	{
		String qno = "";							// Question no.
		String qtype = "";					// Question type
		String ques = "";					// Question 
		String options[] = null;			// Possible answers
		String ans = "";						// Answer selected by user
		String strAllOptions = "";

		StringTokenizer lineTokenizer = new StringTokenizer(line, "\t");
		qno = lineTokenizer.nextToken();
		qtype = lineTokenizer.nextToken();
		ques = lineTokenizer.nextToken();
		if (!qtype.equalsIgnoreCase("textarea"))
		{
			strAllOptions = lineTokenizer.nextToken();
			//System.out.println(qno + "\n" + qtype + "\n" + ques + "\n" + strAllOptions);
			StringTokenizer optionTokenizer = new StringTokenizer(strAllOptions, ",");
			options = new String[optionTokenizer.countTokens()];
			for (int i=0; i<options.length; i++)
			{
				options[i] = optionTokenizer.nextToken();
				//System.out.println("option " + (i+1) + " = " + options[i]);
			}
			return new QuestionAns(Integer.parseInt(qno), qtype, ques, options, ans);
		}
		//System.out.println(qno + "\n" + qtype + "\n" + ques);
		return new QuestionAns(Integer.parseInt(qno), qtype, ques, ans);
		
	}

}
/*************************************************************************/