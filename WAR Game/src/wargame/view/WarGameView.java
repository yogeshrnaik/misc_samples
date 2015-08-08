/**************************************************************************************************/
package wargame.view;
/**************************************************************************************************/
import wargame.model.Cell;
import wargame.model.Piece;
/******************************************************************************************/
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
/******************************************************************************************/
public class WarGameView extends JFrame implements MouseListener, MouseMotionListener
{
	public static int frame_height;
	public static int frame_width;
	public static int STARTX, STARTY; 
	public static boolean isMouseReleased = true;
	public Cell selectedCell;
	public Cell mat[][];
	// for double buffering
	private Image dbImage;
	private Graphics dbg;

	public WarGameView(String title)
	{
		super(title);
		initGameArea();
		addWindowListener(new WindowAdapter()
										{
											public void windowClosing(WindowEvent we) 
											{
												System.exit(0);
											}
										});
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(getToolkit().getScreenSize());
		setResizable(false);
		setVisible(true);
	}

/******************************************************************************************/
	public void initGameArea()
	{
		frame_height = (int)getToolkit().getScreenSize().getHeight();
		frame_width = (int)getToolkit().getScreenSize().getWidth();
		int h = Cell.CELL_HEIGHT, w = Cell.CELL_WIDTH;
		STARTX = 50;//frame_width / 2 - 4 * w;
		STARTY = 100;//frame_height / 2 - 4 * h;
		mat = new Cell[10][10];
		for (int i=0; i<10; i++)
		{
			for (int j=0; j<10; j++)
			{
				int x = STARTX+j*h;
				int y = STARTY+i*w;
				Color c = Color.BLACK;
				if ((isOdd(i) && isOdd(j)) || (isEven(i) && isEven(j)))
					c = Color.BLACK;
				if ((isOdd(i) && isEven(j)) || (isEven(i) && isOdd(j)))
					c = Color.WHITE;
				mat[i][j] = new Cell(i, j, x, y, c);
			}
		}
	}
/******************************************************************************************/
	public void paint(Graphics g)
	{
		for (int i=0; i<10; i++)
		{
			for (int j=0; j<10; j++)
			{
				mat[i][j].drawCell(g, isMouseReleased);
			}
		}
		for (int i=0; i<10; i++)
		{
			for (int j=0; j<10; j++)
			{
				if (mat[i][j].p != null)
				{
					mat[i][j].p.drawPiece(g, mat[i][j].x, mat[i][j].y, isMouseReleased);
				}
			}
		}
		if (selectedCell != null && selectedCell.p != null)
		{
			selectedCell.p.drawPiece(g, selectedCell.x, selectedCell.y, isMouseReleased);
		}
	}
/******************************************************************************************/
	public boolean isEven(int x)
	{
		return (x % 2 == 0);
	}
	public boolean isOdd(int x)
	{
		return (x % 2 != 0);
	}
/******************************************************************************************/
	public void mouseClicked(MouseEvent e) { }
/******************************************************************************************/
	public void mouseEntered(MouseEvent e) { }
/******************************************************************************************/
	public void mouseExited(MouseEvent e) { }
/******************************************************************************************/
	public void mouseMoved(MouseEvent e) {}
/******************************************************************************************/
	public void mousePressed(MouseEvent e) 
	{ 
		isMouseReleased = false;
		int mx = e.getX();
		int my = e.getY();
		if (selectedCell != null)
		{
			selectedCell.deSelect();
			selectedCell = null;
		}
		selectedCell = getCell(mx, my);
		if (selectedCell != null)
			selectedCell.selectCell();
		repaint();
	}
/******************************************************************************************/
	/*Invoked when a mouse button is pressed on a component and then dragged.*/
	public void mouseDragged(MouseEvent e) 
	{
		int x = e.getX() - Piece.PIECE_WIDTH/2;
		int y = e.getY() - Piece.PIECE_HEIGHT/2;
		if (x < STARTX)
		{
			x = STARTX;
		}
		if (y < STARTY)
		{
			y = STARTY;
		}
		if (x > STARTX + 10 * Cell.CELL_WIDTH - Piece.PIECE_WIDTH)
		{
			x = STARTX + 10 * Cell.CELL_WIDTH - Piece.PIECE_WIDTH;
		}
		if (y > STARTY + 10 * Cell.CELL_HEIGHT - Piece.PIECE_HEIGHT)
		{
			y = STARTY + 10 * Cell.CELL_HEIGHT - Piece.PIECE_HEIGHT;
		}
		if (selectedCell.p != null)
		{
			selectedCell.p.x = x;
			selectedCell.p.y = y;
		}
		repaint();
	}
/******************************************************************************************/
	public void mouseReleased(MouseEvent e) 
	{
		isMouseReleased = true;
		int mx = e.getX();
		int my = e.getY();
		Cell currCell = getCell(mx, my);
		if (selectedCell != null && currCell != null)
		{
			if (currCell.p == null)
			{
				currCell.p = selectedCell.p;
				if (selectedCell.p != null)
					selectedCell.p.deHighlight();
				selectedCell.p = null;
				//repaint();
			}
			else
			{
				if (selectedCell.p != null)
					selectedCell.p.reset(selectedCell.x, selectedCell.y);
			}
		}
		repaint();
	}
/******************************************************************************************/
	public Cell getCell(int mx, int my)
	{
		if (mx >= STARTX && mx <= STARTX + 10 * Cell.CELL_WIDTH)
		{
			if (my >= STARTY && my <= STARTY + 10 * Cell.CELL_HEIGHT)
			{
				int posi = (int)((my - STARTY) / Cell.CELL_HEIGHT);
				int posj = (int)((mx - STARTX) / Cell.CELL_WIDTH);
				/*
				System.out.println("posi = " + posi + " posj = " + posj);
				System.out.println("mx = " + mx + " my = " + my);
				System.out.println("STARTX = " + STARTX + " STARTY = " + STARTY);
				System.out.println("*****************************************************************");
				*/
				return mat[posi][posj];
			}
		}
		return null;
	}
/******************************************************************************************/
	/** Update - Method, implements double buffering */
	public void update (Graphics g)
	{ 
		// initialize buffer
		if (dbImage == null)
		{ 
			dbImage = createImage (this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics ();
		}
		// clear screen in background
		dbg.setColor (getBackground ());
		dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);

		// draw elements in background
		dbg.setColor (getForeground());
		paint(dbg);

		// draw image on the screen
		g.drawImage (dbImage, 0, 0, this);
	} // update
/******************************************************************************************/
}
/******************************************************************************************/
