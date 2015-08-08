/**
 * The descriptive text that explaining the purpose and use of the class.
 * 
 * Class				:	NorthPanel
 * Description		:	Panel containing a simple Label
 * @author			:	Yogesh Naik
 * Creation Date	:	23-Jan-2004
*/
/******************************import statements******************************/
import java.awt.*;
import java.util.*;
/*********************************public class*********************************/
public class NorthPanel extends Panel
{
	Label label;
	NorthPanel()
	{
		//setLayout(null);
		//setFont(new Font("ARIAL", Font.BOLD, 72));
		label = new Label("History Examination");
		//label.setBounds(50, 50, 100, 25);
		add(label, BorderLayout.CENTER);
		setSize(300, 50);
		setVisible(true);
	}
}
/*************************************************************************/