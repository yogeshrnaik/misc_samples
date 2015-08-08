/******************************************************************************************/
package wargame.model;
/******************************************************************************************/
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
/******************************************************************************************/
public class Cell 
{
	public Color color;
	public int x, y;
	public static final int CELL_HEIGHT = 40;
	public static final int CELL_WIDTH = 40;
	public Piece p;
	public Cell(int posi, int posj, int x, int y, Color c)
	{
		this.x = x;
		this.y = y;
		color = c;
		if (((posi==0 || posi==8) && posj%2!=0)  || ((posi==1 || posi==9) && posj%2==0))
		{
			p = new Piece(posi, posj, x, y);
		}
	}
/******************************************************************************************/
	public void drawCell(Graphics g, boolean setPiece)
	{
		if (color != null)
		{
			g.setColor(color);
			g.fill3DRect(x, y, CELL_HEIGHT, CELL_WIDTH, true);
		}
		else 
		{
			g.draw3DRect(x, y, CELL_HEIGHT, CELL_WIDTH, true);
		}
		/*if (p != null)
		{
			p.drawPiece(g, x, y, setPiece);
		}*/
	}
/******************************************************************************************/
	public void deSelect()
	{
		if (p != null)
		{
			p.deHighlight();
		}
	}
/******************************************************************************************/
	public void selectCell()
	{
		if (p != null)
		{
			p.highlight = true;
		}
	}
}
/******************************************************************************************/
