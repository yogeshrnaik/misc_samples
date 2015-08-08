/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	TextAreaPanel
 * Description		:	Panel in which a text area is provided for answer
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.awt.*;
import java.util.*;
/*********************************public class*********************************/
public class TextAreaPanel extends Panel
{
	Label question;
	TextArea ans;
	TextAreaPanel(QuestionAns qans)
	{
		setLayout(null);
		question = new Label(qans.qno + ". " + qans.ques);
		ans = new TextArea(50, 100);
		ans.setText("");
		int x=75, y=75, w=75, h=20, vs=25;
		question.setBounds(x, y, 600, 50);
		ans.setBounds(x, y+2*vs, 600, 100);
		add(question);
		add(ans);
		
		setSize(600, 300);
		setVisible(true);
	}
	public String getAnswer()
	{
		String strAns = "";
		strAns += ans.getText();
		return strAns;
	}
}
/*************************************************************************/