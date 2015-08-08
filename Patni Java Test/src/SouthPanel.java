/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	SouthPanel
 * Description		:	Panel containing a three buttons.
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.awt.*;
import java.awt.event.*;
import java.util.*;
/*********************************public class*********************************/
public class SouthPanel extends Panel implements ActionListener
{
	Button prev, next, save;
	CenterPanel cp;
	SouthPanel(CenterPanel cp)
	{
		this.cp = cp;
		prev = new Button("Previous");
		next = new Button("Next");
		save = new Button("Save");
		//setLayout(new BorderLayout());
		add(prev);
		add(next);
		add(save);
		prev.addActionListener(this);
		next.addActionListener(this);
		save.addActionListener(this);
		setSize(300, 50);
		setVisible(true);
	}
	public void actionPerformed (ActionEvent ae)
	{
		if (ae.getSource() == prev)
		{
			//System.out.println("prev clicked");
			cp.showPrevQuestion();
			//cp.cardLO.show(cp, "Radio");
		}
		else if (ae.getSource() == next)
		{
			//System.out.println("next clicked");
			cp.showNextQuestion();
			//cp.cardLO.show(cp, "Checkbox");
		}
		else if (ae.getSource() == save)
		{
			//System.out.println("save clicked");
			cp.saveAnswers();
			//cp.cardLO.show(cp, "Textarea");
		}
	}
}
/*************************************************************************/