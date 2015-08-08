/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	RadioPanel
 * Description		:	Panel in which options of questions are displayed as radio buttons
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.awt.*;
import java.util.*;
/*********************************public class*********************************/
public class RadioPanel extends Panel
{
	Label question;
	Checkbox cb[];

	RadioPanel(QuestionAns qans)
	{
		setLayout(null);
		question = new Label(qans.qno + ". " + qans.ques);
		int x=75, y=75, w=125, h=25, vs=30;
		question.setBounds(x, y, 600, 50);
		add(question);
		CheckboxGroup cbg = new CheckboxGroup();
		cb = new Checkbox[qans.options.length];
		for (int i=0; i<cb.length; i++)
		{
			cb[i] = new Checkbox(qans.options[i], cbg, false);
			cb[i].setBounds(x, y+((i+2)*vs), w, h);
			add(cb[i]);
		}
		
		setSize(600, 300);
		setVisible(true);
	}
	public String getAnswer()
	{
		String ans ="";
		for (int i=0; i<cb.length; i++)
		{		
			if (cb[i].getState())
			{
				ans += cb[i].getLabel();
				break;
			}
		}
		return ans;
	}
}
/*************************************************************************/