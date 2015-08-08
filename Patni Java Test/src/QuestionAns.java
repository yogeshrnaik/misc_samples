/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	QuestionAns
 * Description		:	A class that holds the question-answer unit
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
/*********************************public class*********************************/
public class QuestionAns
{
	int qno;				// Question number
	String qtype;		// Question type
	String ques;		// Question 
	String options[];	// Possible answers
	String ans;			// Answer selected by user

	public QuestionAns(int qno, String qtype, String ques, String ans)
	{
		this.qno	= qno;				
		this.qtype = qtype;
		this.ques = ques;
		this.options = null;			// since the question type is Textarea
		this.ans = ans;
	}

	public QuestionAns(int qno, String qtype, String ques, String[] options, String ans)
	{
		this.qno	= qno;				
		this.qtype = qtype;
		this.ques = ques;
		this.options = options;
		this.ans = ans;
	}
}
/*************************************************************************/