/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	CenterPanel
 * Description		:	Panel in which questions are displayed one after the other
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.awt.*;
import java.util.*;
import java.io.*;
/*********************************public class*********************************/
public class CenterPanel extends Panel
{
	final static String RESULTS = "results.txt";
	int currQno;
	CardLayout cardLO;
	RadioPanel rp;
	CheckBoxPanel cbp;
	TextAreaPanel tap;
	Vector allPanels;				// a vector which holds all the panels required for displaying questions
	ExamSystem parent;
	CenterPanel(ExamSystem parent)
	{
		this.parent = parent;
		currQno = 1;
		cardLO = new CardLayout();
		setLayout(cardLO);
		
		allPanels = new Vector();
		createQuestionPanels();

		/*rp = new RadioPanel(5);
		cbp = new CheckBoxPanel(6);
		tap = new TextAreaPanel();*/

		/*add(rp, "Radio");
		add(cbp, "Checkbox");
		add(tap, "Textarea");*/
		
		setSize(300, 50);
		setVisible(true);
	}
	public void createQuestionPanels()
	{
		// for each question in database create an appropriate panel and add it to vector 
		for (int i=0; i<parent.qah.database.size(); i++)
		{
			QuestionAns qans = (QuestionAns)parent.qah.database.get(i);
			if (qans.qtype.equalsIgnoreCase("radio"))
			{
				RadioPanel radioPanel = new RadioPanel(qans);
				allPanels.add(radioPanel);
				add(radioPanel, "Question "+qans.qno);
									/*rp = new RadioPanel(5);
							cbp = new CheckBoxPanel(6);
							tap = new TextAreaPanel();*/

							/*add(rp, "Radio");
							add(cbp, "Checkbox");
							add(tap, "Textarea");*/
			}
			else if (qans.qtype.equalsIgnoreCase("textarea"))
			{
				TextAreaPanel textAreaPanel = new TextAreaPanel(qans);
				allPanels.add(textAreaPanel);
				add(textAreaPanel, "Question "+qans.qno);
			}
			else if (qans.qtype.equalsIgnoreCase("checkbox"))
			{
				CheckBoxPanel checkBoxPanel = new CheckBoxPanel(qans);
				allPanels.add(checkBoxPanel);
				add(checkBoxPanel, "Question "+qans.qno);
			}
		}
	}
	public void showNextQuestion()
	{
		if (currQno < parent.qah.database.size())
		{
			currQno++;
		}
		cardLO.show(this, "Question "+currQno);
	}
	public void showPrevQuestion()
	{
		if (currQno > 0)
		{
			currQno--;
		}
		cardLO.show(this, "Question "+currQno);
	}
	public void saveAnswers()
	{
		try
		{
			FileWriter fw = new FileWriter(RESULTS);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i=0; i<parent.qah.database.size(); i++)
			{
				Object p = allPanels.get(i);
				if (p instanceof RadioPanel)
				{
					RadioPanel rp = (RadioPanel)p;
					String strAnswer = "";
					if (rp.getAnswer().length() == 0)
						strAnswer += (i+1) + "\t" + "Not attempted";
					else 
						strAnswer += (i+1) + "\t" + rp.getAnswer();
					bw.write(strAnswer, 0, strAnswer.length());
					bw.newLine();
				}
				else if (p instanceof CheckBoxPanel)
				{
					CheckBoxPanel cbp = (CheckBoxPanel)p;
					String strAnswer= "";
					if (cbp.getAnswer().length() == 0)
						strAnswer += (i+1) + "\t" + "Not attempted";
					else 
						strAnswer += (i+1) + "\t" + cbp.getAnswer();
					bw.write(strAnswer, 0, strAnswer.length());
					bw.newLine();
				}
				else if (p instanceof TextAreaPanel)
				{
					TextAreaPanel tap = (TextAreaPanel)p;
					String strAnswer= "";
					if (tap.getAnswer().length() == 0)
						strAnswer += (i+1) + "\t" + "Not attempted";
					else 
						strAnswer += (i+1) + "\t" + tap.getAnswer();
					bw.write(strAnswer, 0, strAnswer.length());
					bw.newLine();
				}
			}
			fw.flush();
			bw.flush();
			fw.close();
			bw.close();
		}
		catch (Exception e)
		{
			System.out.println("Could not write results.txt file.");
		}
	}
}
/*************************************************************************/