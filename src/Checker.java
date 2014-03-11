import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class Checker extends JComponent
{
	// variables used for the checker moving
	private short screenX = 0;
	private short screenY = 0;
	private short currX = 0;
	private short currY = 0;
	private Color color;
	private String text; // text for first checker of each group (number of checkers in stack)
	private static boolean isDragged; //true if any checker object is currently being dragged
	private static boolean disabledPress; //true if mouse was pressed while component was disabled
	private boolean top; // marks whether the checker is the first one at its board position
	private int point; // point where the checker is
	public static final int r = 25; // radius

	/**
	 * Checker constructor.
	 * 
	 * @param owner
	 */
	public Checker(int owner)
	{
		this.color = owner == 1 ? Color.white : Color.black;
		setBounds(0, 0, 1 + 2 * r, 1 + 2 * r); // set checker graphical bounds
		text = "";
		addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				//check if the right click is used while the checker is enabled and remove it
				if(e.getButton() == MouseEvent.BUTTON3 && e.getComponent().isEnabled())
				{
					BoardAnimation ba = (BoardAnimation) e.getComponent().getParent();
					ba.removeChecker((Checker)e.getComponent());
				}
				e.consume();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if(e.getModifiers() != InputEvent.BUTTON1_MASK)
				{
					e.consume();
					return;
				}
				else if(!e.getComponent().isEnabled())
				{
					Checker.disabledPress = true;
					e.consume();
					return;
				}
				Checker.disabledPress = false;
				// if the left click is pressed while the checker is enabled 
				// get the coordinates
				screenX = (short) e.getXOnScreen();
				screenY = (short) e.getYOnScreen();
				currX = (short) getX();
				currY = (short) getY();

				BoardAnimation ba = (BoardAnimation) e.getComponent().getParent();
				Checker c = (Checker) e.getComponent();
				ba.addPossibleMovesOf(c);
				ba.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(e.getModifiers() != InputEvent.BUTTON1_MASK || !e.getComponent().isEnabled())
				{
					e.consume();
					return;
				}
				Checker.disabledPress = false;
				Checker.isDragged = false;
				//when the left click is released, get the new coordinates
				short x = (short) e.getComponent().getX();
				short y = (short) e.getComponent().getY();
				BoardAnimation ba = (BoardAnimation) e.getComponent().getParent();
				ba.clearPossibleMoves();
				ba.repaint();
				// check if the point where the checker was moved is valid
				int chk = ba.isInside(new Point(x + (e.getComponent().getWidth() / 2), (y + (e.getComponent().getHeight() / 2))));
				//move is invalid (chk == -1) or trying to move to current point
				if(chk == -1 || chk == ((Checker) e.getComponent()).point)
				{
					resetPos();
				}
				else
				{
					if(!ba.placeChecker(point, chk, ((Checker) e.getComponent()).getOwner(),true))
						resetPos();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		}); //end of MouseListener

		addMouseMotionListener(new MouseMotionListener()
		{

			@Override
			public void mouseDragged(MouseEvent e)
			{
				if(e.getModifiers() != InputEvent.BUTTON1_MASK || !e.getComponent().isEnabled() || Checker.disabledPress)
				{
					e.consume();
					return;
				}
				//if the left click is used to drag the checker, define and set the new point for the checker
				short deltaX = (short) (e.getXOnScreen() - screenX);
				short deltaY = (short) (e.getYOnScreen() - screenY);
				short newX = (short) (currX + deltaX);
				short newY = (short) (currY + deltaY);
				Checker.isDragged = true;
				((Checker) e.getComponent()).setText("");
				setLocation(newX, newY);
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
			}
		});
	}// end of Checker constructor

	@Override
	protected void paintComponent(Graphics g)
	{
		//GUI tweaks and coloring
		super.paintComponent(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(0, 0, 0));
		g.drawOval(0, 0, 2 * r, 2 * r);
		g.setColor(this.color);
		g.fillOval(1, 1, (2 * r) - 1, (2 * r) - 1);

		if(this.color.equals(Color.white))
		{
			g.setColor(new Color(242, 242, 242));
		}
		else
		{
			g.setColor(new Color(82, 82, 82));
		}
		g.fillOval(r - (r / 2), r - (r / 2), r, r);

		if(this.color.equals(Color.white))
		{
			g.setColor(Color.BLACK);
		}
		else
		{
			g.setColor(Color.WHITE);
		}
		//text alignment based on text's length 
		if(text.length() == 1)
			g.drawString(text, r - r / 7, r + r / 5);
		else
			g.drawString(text, r - r / 4, r + r / 5);
	}// end of paintComponent

	public ArrayList<Move> getPossibleMoves()
	{
		ArrayList<Move> moves = new ArrayList<Move>();
		int op =   (this.getOwner() == 1 ? -1 : 1);
		Dice d = ((Main)SwingUtilities.getRoot(this)).getGame().getDice();
		Board board = ((BoardAnimation)this.getParent()).getCurrentBoard();
		int pos,b=0;
		if(d.isRollDouble())
		{
			int counter=1;
			b=d.totalConsumed();
			while(b!=4)
			{
				pos =   (this.getPoint() + (((counter++))*op*d.getDie(b)));
				if(board.inBounds(pos))
				{
					if(board.isValid(this.getOwner(), board.checkersNumber(pos)))
					{
						moves.add(new Move(pos,d.getDie(b)));
						b++;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			for (; b < 2; b++)
	        {
				if(d.isConsumed(b))
				{
	                continue;
				}
				pos =   (this.getPoint() + (op*d.getDie(b)));
				if(board.inBounds(pos))
				{
					if(board.isValid(this.getOwner(), board.checkersNumber(pos)))
					{
						moves.add(new Move(pos,d.getDie(b)));
					}
				}
				
	        }
			if(moves.size() >= 1 && d.totalConsumed() == 2)
			{
				pos =   (this.getPoint() + (op*(d.getDie( 0)+d.getDie( 1))));
				if(board.inBounds(pos))
				{
					if(board.isValid(this.getOwner(), board.checkersNumber(pos)))
					{
						moves.add(new Move(pos,d.getDie(b)));
					}
				}
			}
		}
		return moves;
	}

	private void resetPos()
	{
		setLocation(currX, currY);
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public int getOwner()
	{
		return   (this.color.equals(Color.white) ? 1 : 2);
	}

	public static boolean getDraggedStatus()
	{
		return Checker.isDragged;
	}

	public static void setDraggedStatus(boolean val)
	{
		Checker.isDragged = val;
	}

	public int getPoint()
	{
		return this.point;
	}

	public void setPoint(int p)
	{
		this.point = p;
	}

	public void setTop(boolean val)
	{
		this.top = val;
	}

	public boolean getTop()
	{
		return this.top;
	}
}
