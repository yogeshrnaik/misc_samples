/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	ExamSystem
 * Description		:	Main class of an Online Examination System
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.awt.*;
import java.util.*;
import java.awt.event.*;
/*********************************public class*********************************/
public class ExamSystem extends Frame
{
	QuesAnsHolder qah;
	NorthPanel north;
	CenterPanel center;
	SouthPanel south;
	public ExamSystem ()
	{
		super("Online Examination System");
		qah = new QuesAnsHolder();
		setLayout(new BorderLayout());
		setSize(getToolkit().getScreenSize());
		setResizable(false);
		north = new NorthPanel();
		center = new CenterPanel(this);
		south = new SouthPanel(center);

		add(north, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter()
									{
										public void windowClosing(WindowEvent we) 
										{
											System.exit(0);
										}
									});
		setVisible(true);
	}
	public static void main(String[] args) 
	{
		ExamSystem examSys = new ExamSystem();
	}
}
/*************************************************************************/