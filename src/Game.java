import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

public class Game implements Runnable
{

	private byte currPlayer; // player indicator ('0' for player 1 (/human), '1'
								// for player 2 (/computer)
	private Player[] players;
	private Main frame; // the Main object
	private boolean winner; // winner check variable
	private boolean rolled; // dice roll check variable
	
	private Dice realDice; // the real dice of the game
	private boolean firstroll; // used for the first two dice rolls (initial
								// player check)
	private boolean gameStarted; // indicates whether a game has been started
	private Thread gameThread; // thread used to balance the flow of the program
	private byte chk; // used to indicate whether a player has won

	/**
	 * 1 parameter constructor for the Game object.
	 * 
	 * @param frame
	 *            : the Main class JFrame
	 */
	public Game(Main frame)
	{
		this(frame, 1);
	}

	/**
	 * 2 parameter constructor for the Game object.
	 * 
	 * @param frame
	 *            : the Main class JFrame
	 * @param numOfPlayers
	 *            : number of human players
	 */
	public Game(Main frame, int numOfPlayers)
	{
		// initialize all the Game variables
		this.frame = frame;
		this.winner = false;
		this.rolled = false;
		this.players = new Player[2];
		this.firstroll = true;
		this.gameStarted = false;
		players[0] = new Player(1, "Tavliaris", this);
		players[1] = new Computer(2, this);
	}// end of Game

	/**
	 * Exits the current game.
	 * 
	 */
	public void exitCurrent()
	{
		// reset the dice, interrupt any rolls and set the firstroll variable to
		// true
		frame.getDiceAnimation().showExtra(false);
		frame.getDiceAnimation().cancelAnimatedRoll();
		firstroll = true;

		// interrupt the game thread and end any player turns
		gameThread.interrupt();
		players[0].endTurn();
		players[1].endTurn();
	}// end of exitCurrent

	/**
	 * Initializes and starts the thread.
	 * 
	 * It is called when the new game button is pressed
	 */
	public void startGame()
	{
		gameThread = new Thread(this, "GameThread");
		gameThread.start();
	}

	/**
	 * Runs the thread.
	 * 
	 * Catches any thread exceptions, runs the infinite game loop
	 */
	@Override
	public void run()
	{
		this.gameStarted = true;
		this.setEnabledCheckers(false);
		try
		{
			setInitial();
		}
		catch (InterruptedException e1)
		{
			return;
		}
		catch (ExecutionException e)
		{
			return;
		}
		catch (CancellationException e)
		{
			return;
		}
		rolled = currPlayer==1;
		frame.getDiceButton().setEnabled(currPlayer==0);
		while (!winner)
		{
			while (rolled)
			{
				if (firstroll)
				{
					firstroll = false;
					if(this.getCurrentPlayer().getNum() == 1)
					{
						frame.getDiceButton().setEnabled(true);
						continue;
					}
					continue;
				}
				rolled=false;
				if(this.getCurrentPlayer().getNum() == 1)
				{
					this.rollDice((byte) 3);
					this.getCurrentPlayer().startTurn();
					while (!this.getDice().consumedAll())
					{
						if(rolled)
							break;
						if(hasWon())
						{
							winner=true;
							break;
						}
						//else if()
						try
						{
							Thread.sleep(1500);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					this.getCurrentPlayer().endTurn();
					frame.getDiceButton().setEnabled(false);
					rolled=true;
				}
				else
				{
					this.rollDice((byte) 3);
					long time = System.currentTimeMillis();
					System.out.println("started");
					((Computer) this.getCurrentPlayer()).play();
					System.out.println("done in " + (System.currentTimeMillis() - time) + " ms (including graphical move)");
					rolled = false;
					frame.getDiceButton().setEnabled(true);// enable dice again
				}
				chk = changeTurn(); // next turn
			}
			// perform the check every 900 ms
			try
			{
				Thread.sleep(900);
			}
			catch (InterruptedException e)
			{
				System.err.println("Thread interrupted");
				return;
			}
		}
		JOptionPane.showMessageDialog(frame,
			    "Player "+this.players[chk].getName()+" WON!!!1!!1111!!");

	}// end of run

	/**
	 * Rolls the dice and sets the initial player.
	 * 
	 */
	public void setInitial() throws InterruptedException, ExecutionException, CancellationException
	{
		// this infinite loop only breaks when the two dice sums are not equal
		while (true)
		{
			// roll the dice properly (1 for each player)
			rollDice((byte) 1);
			frame.getDiceAnimation().waitForRoll();
			rollDice((byte) 2);
			frame.getDiceAnimation().waitForRoll();

			// get and check the dice sums
			int res[] = getDice().getDiceResults();
			if(res[0] > res[1])
			{
				// player 1 starts first
				this.players[0].startTurn();
				currPlayer = 0;
				break;
			}
			else if(res[0] < res[1])
			{
				// player 2 starts first
				this.players[1].startTurn();
				currPlayer = 1;
				break;
			}
		}
	}// end of setInitial

	/**
	 * Turn changing
	 * 
	 * @return the number of the player who won, 0 if none
	 */
	public byte changeTurn()
	{
		getCurrentPlayer().endTurn();
		currPlayer = (byte) ((currPlayer + 1) % 2); // get next player
		
		if(hasWon())
			return currPlayer;
		getCurrentPlayer().startTurn();
		if(currPlayer == 0)
			setEnabledCheckers(true); 
		return 0;
	}// end of changeTurn

	/**
	 * Rolls one or both of the dice.
	 * 
	 * @param howManyDice
	 *            : number of die to be rolled (1 for d1, 2 for d2, 3 for both)
	 */
	public void rollDice(byte howManyDice)
	{
		try
		{
			frame.getDiceAnimation().animatedRoll(howManyDice);
			frame.getDiceAnimation().waitForRoll();
		}
		catch (CancellationException e)
		{
		}
		catch (InterruptedException e)
		{
		}
		catch (ExecutionException e)
		{
		}
	}// end of rollDice

	public void setRolled()
	{
		this.rolled = true;
	}
	
	/**
	 * Checks if current player has won by checking if he gathered all of his checkers
	 */
	public boolean hasWon()
	{
		return frame.getBoardAnimation().getCurrentBoard().totalCount(getCurrentPlayer().getNum()) == 0;
	}
	/**
	 * Enables only the checkers of the current player.
	 * @param status
	 *            : true to enable, false to disable
	 */
	public void setEnabledCheckers(boolean status)
	{
		frame.getBoardAnimation().setCheckersStatus(status);
	}

	/*
	 * get methods
	 */

	public Player getCurrentPlayer()
	{
		return this.players[currPlayer];
	}

	public Player getPlayer(byte b)
	{
		return this.players[b];
	}

	public Dice getDice()
	{
		return this.realDice;
	}

	public void setDice(Dice d)
	{
		this.realDice = d;
	}

	public boolean isFirstRoll()
	{
		return this.firstroll;
	}

	public boolean isRunning()
	{
		return gameStarted;
	}
	
	public Board getCurrentBoard()
	{
		return this.frame.getBoardAnimation().getCurrentBoard();
	}
	
	public BoardAnimation getBoardAnimation()
	{
		return this.frame.getBoardAnimation();
	}
}
