import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JLabel;

public class Player
{
	private ArrayList<Checker> checkers;
	private int playerNum;
	private String name;
	private JLabel jl;
	private Game game;
	private boolean hasGraveyard;
	private int score; //number of checkers gathered

	/**
	 * Player constructor.
	 * 
	 * Initializes the player checkers.
	 */
	public Player(int pn, String str, Game game)
	{
		this.playerNum = pn;
		this.name = str;
		this.game = game;
		this.hasGraveyard = false;
		this.score = 0;
		jl = new JLabel(this.name);
		jl.setFont(new Font("Arial", Font.BOLD, 14));
		jl.setOpaque(true);
		jl.setBackground(Color.RED);
		jl.setForeground(Color.BLUE);
	}

	/**
	 * Initializes the player checkers and disables them until a game starts.
	 */
	public void createCheckers()
	{
		/*
		 * checkers = new ArrayList<Checker>(15); int i = 0; while (i != 15) { //checkers[i] = new Checker(playerNum);
		 * checkers.add(i, new Checker(playerNum)); i++; } enableCheckers(false);
		 */
	}

	public Checker getChecker(int index)
	{
		return checkers.get(index);
	}

	public JLabel getLabel()
	{
		return jl;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Switch the background color of the names for each turn.
	 */
	public void startTurn()
	{
		jl.setBackground(Color.GREEN);
	}

	public void endTurn()
	{
		jl.setBackground(Color.RED);
	}

	public Game getGame()
	{
		return this.game;
	}

	public int getNum()
	{
		return playerNum;
	}

	public void setGraveyard(boolean status)
	{
		this.hasGraveyard = status;
	}

	public boolean hasGraveyard()
	{
		return this.hasGraveyard;
	}
}
