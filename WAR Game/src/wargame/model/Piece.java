/******************************************************************************************/
package wargame.model;
/******************************************************************************************/
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
/******************************************************************************************/
public class Piece 
{
	public Color color;
	public int x, y, score;
	public boolean highlight;
	public static final int PIECE_HEIGHT = 25;
	public static final int PIECE_WIDTH = 25;
	public Piece(int posi, int posj, int cellx, int celly)
	{
		highlight = false;
		if (posi == 0 || posi == 9)
		{
			score = 20;
		}
		else if (posi == 1 || posi == 8)
		{
			score = 10;
		}
		if (posi == 0 || posi == 1)
		{
			color = Color.red;
		}
		if (posi == 8 || posi == 9)
		{
			color = Color.green;
		}
		x = cellx + Cell.CELL_WIDTH/2 - PIECE_WIDTH/2;
		y = celly + Cell.CELL_HEIGHT/2 - PIECE_HEIGHT/2;
	}
/******************************************************************************************/
	public void drawPiece(Graphics g, int cellx, int celly, boolean setPiece)
	{
		if (setPiece)
		{
			x = cellx + Cell.CELL_WIDTH/2 - PIECE_WIDTH/2;
			y = celly + Cell.CELL_HEIGHT/2 - PIECE_HEIGHT/2;
		}
		if (color != null)
			g.setColor(color);
		g.fill3DRect(x, y, PIECE_HEIGHT, PIECE_WIDTH, highlight);
		g.setColor(Color.BLACK);
		g.setFont(new Font("arial", Font.BOLD, 12));
		g.drawString(score + "", x+5, y+15);
	}
/******************************************************************************************/
	public void deHighlight()
	{
		highlight = false;
	}
/******************************************************************************************/
	public void reset(int cellx, int celly)
	{
		x = cellx + Cell.CELL_WIDTH/2 - PIECE_WIDTH/2;
		y = celly + Cell.CELL_HEIGHT/2 - PIECE_HEIGHT/2;
		deHighlight();
	}
/******************************************************************************************/
}
/******************************************************************************************/
