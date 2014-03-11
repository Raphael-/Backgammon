import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class BoardAnimation extends JPanel
{
	//board img
	private static final Image img = new ImageIcon(ClassLoader.getSystemResource("images/board.png")).getImage();
	//image that represents a possible move
	private static final Image pMoveImg = new ImageIcon(ClassLoader.getSystemResource("images/tick.png")).getImage();
	// same image but flipped
	private static final Image pMoveImgFl = new ImageIcon(ClassLoader.getSystemResource("images/flipped.png")).getImage();
	private static ArrayList<Move> pmoves; // possible moves to draw
	private static Board currBoard; // current board to animate
	private static Rectangle[] checkerGroup; //checker groups are represented by a rectangle
	private static ArrayList<Checker> whiteCheckers;
	private static ArrayList<Checker> blackCheckers;

	public BoardAnimation()
	{
		pmoves = new ArrayList<Move>();
		whiteCheckers = new ArrayList<Checker>();
		blackCheckers = new ArrayList<Checker>();
		Dimension size = new Dimension(800, 600);
		createPoints();
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
		this.setVisible(true);
	}

	/**
	 * Creates all the checker positions/points and the "graveyards".
	 * 
	 */
	private void createPoints()
	{
		// initial coordinates on the board
		short startX = 733;
		short startY = 330;
		short width = 55;
		short height = 250;
		checkerGroup = new Rectangle[26];
		int b = 0;
		for (; b < 24; b++) // foreach board position
		{
			// at positions 6, 12 and 18 there's a gap on the board
			// that has to be skipped
			if(b == 6)
			{
				startX -= 2 * width;
			}
			else if(b == 12)
			{
				startY -= 310;
				startX = 17;
			}
			else if(b == 18)
			{
				startX += 2 * width;
			}

			// for each position make a new checker group
			checkerGroup[b] = new Rectangle(startX, startY, width, height);

			// proceed to next position
			if(b < 11)
			{
				startX -= width;
			}
			else
			{
				startX += width;
			}

		}

		// create the graveyard areas
		for (; b < checkerGroup.length; b++)
		{
			if(b == 24)
			{
				// white graveyard
				startX = 378;
				startY = 20;
			}
			else
			{
				// black graveyard
				startX = 378;
				startY = 330;
			}
			checkerGroup[b] = new Rectangle(startX, startY, width, height);
		}
	}// end of createPoints

	/**
	 * Animates currBoard by adding Checkers(JLabels) to the JPanel
	 */
	public void animateBoard()
	{
		for (int i = 0; i < 15; i++)
		{
			whiteCheckers.add(new Checker(1));
			this.add(whiteCheckers.get(i));
			blackCheckers.add(new Checker(2));
			blackCheckers.get(i).setEnabled(false);
			this.add(blackCheckers.get(i));
		}
	}

	/**
	 * The Board "painter".
	 * 
	 * @param g
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(img, 0, 0, 800, 600, null);
		short x = 0, y = 0;
		for (Move m : pmoves)
		{
			x = (short) (checkerGroup[m.getDest()].getX() + (checkerGroup[m.getDest()].getWidth() / 2) - 12);
			if(m.getDest() < 12) // bottom half
			{
				y = (short) (checkerGroup[m.getDest()].getY() + checkerGroup[m.getDest()].getHeight());
				g.drawImage(pMoveImgFl, x, y, null, this);
			}
			else
			// top half
			{
				y = (short) (checkerGroup[m.getDest()].getY() - 22);
				g.drawImage(pMoveImg, x, y, null, this);
			}

		}
		if(Checker.getDraggedStatus() || currBoard == null)
			return;
		int index1 = 0, index2 = 0;
		ArrayList<Checker> arr = null;
		for (int b = 0; b < 26; b++)
		{
			int total = currBoard.checkersNumber(b);
			int index;
			if(total == 0)
				continue;
			else if(total > 0)
			{
				arr = whiteCheckers;
				index = index1;
			}
			else
			{
				arr = blackCheckers;
				index = index2;
			}
			total = Math.abs(total);
			int cNum = 0;
			x = (short) (checkerGroup[b].getX());
			while (total != cNum)
			{
				/*
				 * check if the checker is placed within the bottom , the graveyards or the top half of the board and calculate
				 * the right y value
				 */
				if(b < 12) // bottom half
				{
					y = (short) (checkerGroup[b].getY() + checkerGroup[b].getHeight() - arr.get(index).getHeight() - ((cNum >= 5) ? 4 : cNum) * arr
							.get(index).getHeight());
				}
				else if(b >= 12 && b < 24)// top half
				{
					y = (short) (checkerGroup[b].getY() + ((cNum >= 5) ? 4 : cNum) * arr.get(index).getHeight());
				}
				else if(b == 24)
				{
					y = (short) (checkerGroup[b].getY() + checkerGroup[b].getHeight() - arr.get(index).getHeight() - ((cNum >= 5) ? 4 : cNum) * arr
							.get(index).getHeight());
				}
				else
				{
					y = (short) (checkerGroup[b].getY() + ((cNum >= 5) ? 4 : cNum) * arr.get(index).getHeight());
				}
				arr.get(index).setText("");
				arr.get(index).setLocation(x, y);
				arr.get(index).setPoint(b);
				if(cNum >= 5) //if more than 5 checkers in this group
				{
					//show a counter that indicates how many checkers are in this group.The counter is shown only at the last (first added) checker
					arr.get(index - cNum).setText(String.valueOf(total));
				}
				if(arr == whiteCheckers)
				{
					index1++;
					index++;
				}
				else
				{
					index2++;
					index++;
				}
				cNum++;
			}
		}
	}

	/**
	 * Using interpolation formula p(t) = p1*(1-t) + p2*t to animate Computer's move t is normalized ( t[0,1] )
	 */
	public void animateMove(int from, int to)
	{
		Checker.setDraggedStatus(true); //repaint of the board is disabled during move operation
		final Checker src = this.getTopChecker(from);
		Checker dest = this.getTopChecker(to);
		final short x0 = (short) src.getX();
		final short y0 = (short) src.getY();
		final short x1;
		final short y1;
		if(dest != null) //there is at least one checker in checker group
		{
			x1 = (short) dest.getX();
			y1 = (short) dest.getY();
		}
		else
		//checker group is empty
		{
			x1 = (short) checkerGroup[to].getX();
			y1 = (short) checkerGroup[to].getY();
		}
		Timer t = new Timer(100, new ActionListener()
		{
			float t = 0.0f;

			public void actionPerformed(ActionEvent e)
			{
				short x = (short) (x0 * (1 - t) + x1 * t);
				short y = (short) (y0 * (1 - t) + y1 * t);
				t += 0.04f;
				src.setLocation(x, y);
				if(t >= 1.0f)
				{
					((Timer) e.getSource()).stop();
					Checker.setDraggedStatus(false);

				}
			}
		});
		t.start();
		while (t.isRunning())
		{

		}
		this.placeChecker(from, to, src.getOwner(), false);
		this.validate();
		this.repaint();
	}

	public Checker getTopChecker(int targetIndex)
	{
		int total = currBoard.checkersNumber(targetIndex);
		if(total >= 0)
			return null;
		int b = 0;
		for (; b < blackCheckers.size(); b++)
		{
			if(blackCheckers.get(b).getPoint() > targetIndex)
			{
				return blackCheckers.get(b - 1);
			}
		}
		return blackCheckers.get(b - 1);
	}

	/**
	 * Checks and controls the movement of a checker.
	 * 
	 * @param from
	 *            point on the board where the checker will be moved
	 * @param to
	 *            point on the board where the checker was taken
	 * @param owner
	 *            the owner of the checker (player 1 or 2)
	 * @param diceCheck
	 *            true if used to validate the move
	 * @return true if the checker was moved successfully(valid move),false if the move was invalid thus the checker wasn't moved
	 */
	public boolean placeChecker(int from, int to, int owner, boolean diceCheck)
	{
		if(diceCheck)
		{
			Dice dice = ((Main) SwingUtilities.getRoot(this)).getGame().getDice();
			int toCheck = Math.abs(to - from);
			if(dice.diceAvailable(toCheck))
			{
				if(currBoard.move(owner, from, to))
				{
					dice.consumeDiceWithNum(toCheck);
					return true;
				}
			}
		}
		else
		{
			if(currBoard.move(owner, from, to))
				return true;
		}
		return false;
	}

	/**
	 * Checks whether the point given is inside the board.
	 * 
	 * @param p
	 *            : the point
	 * @return the number of the checker group if the point is valid, -1 else
	 */
	public int isInside(Point p)
	{
		for (short i = 0; i < 24; i++)
		{
			if(checkerGroup[i].contains(p))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Sets the status(enabled/disabled) of player's checkers
	 * 
	 * @param status
	 *            true=enabled , false=disabled
	 */
	public void setCheckersStatus(boolean status)
	{
		ArrayList<Checker> checkers = whiteCheckers;
		for (int b = 0; b < checkers.size(); b++)
		{
			checkers.get(b).setEnabled(status);
		}
	}

	/**
	 * Checks if all the checkers of the player are in the "gathering" area.
	 * 
	 * @return true if they are all in the area
	 */
	public boolean hasAllHome(int playerID)
	{
		ArrayList<Checker> checkers = null;
		if(playerID == 1)
		{
			checkers = whiteCheckers;
		}
		else
		{
			checkers = blackCheckers;
		}
		if(playerID == 1)
		{
			for (int b = 0; b < checkers.size(); b++)
			{
				if(checkers.get(b).getPoint() < 18)
					return false;
			}
			return true;
		}
		else
		{
			for (int b = 0; b < checkers.size(); b++)
			{
				if(checkers.get(b).getPoint() > 5 || checkers.get(b).getPoint() == currBoard.gyard(playerID))
					return false;
			}
			return true;
		}

	}

	/**
	 * Removes the checker from its stack. This method is used for player's gathering state.
	 * 
	 * @param point
	 *            : the current point of the checker
	 */
	public void removeChecker(Checker chk)
	{
		Dice dice = ((Main) SwingUtilities.getRoot(this)).getGame().getDice();
		if(currBoard.canGather(chk.getPoint(), dice))
		{
			whiteCheckers.remove(chk);
			currBoard.removeChecker(chk.getPoint());
			this.remove(chk);
			this.revalidate();
			this.repaint();
		}
	}

	/**
	 * Removes the checker from its stack. This method is used for computer's gathering state.
	 * 
	 * @param point
	 *            : the current point of the checker
	 */
	public void removeChecker(int point)
	{
		currBoard.removeChecker(point);
		Checker toRmv = this.getTopChecker(point);
		blackCheckers.remove(toRmv);
		this.remove(toRmv);
		this.revalidate();
		this.repaint();
	}

	/**
	 * Adds all possible moves that can be performed by Checker c
	 * 
	 * @param c
	 */
	public void addPossibleMovesOf(Checker c)
	{
		pmoves.addAll(c.getPossibleMoves());
	}

	/**
	 * Clears all possible moves inside pmoves ArrayList.
	 * 
	 */
	public void clearPossibleMoves()
	{
		pmoves.clear();
	}

	/**
	 * Clears all the checker groups (empties board).
	 * 
	 */
	public void clearBoard()
	{
		for (int b = 0; b < whiteCheckers.size(); b++)
		{
			this.remove(whiteCheckers.get(b));
		}
		for (int b = 0; b < whiteCheckers.size(); b++)
		{
			this.remove(blackCheckers.get(b));
		}
		// repaint and validate the board
		this.validate();
		this.repaint();
	}// end of clear Board

	/**
	 * Sets the current board
	 * 
	 * @param b
	 */
	public void setCurrBoard(Board board)
	{
		currBoard = board;
	}

	public Board getCurrentBoard()
	{
		return currBoard;
	}
}
