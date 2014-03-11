
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/*
 * Artificial Intelligence Course 2012-2013
 * 
 * Magkos Rafail-Georgios 3100098
 * Mpogdanos Michail 3100123
 * Ntatsev Ntaniel 3100131
 */
public class Main extends JFrame implements ActionListener
{
    //GUI components
    private JToolBar jtb;
    private JButton newGame;
    private JButton exit;
    private JButton about;
    private JButton roll;
    //Board and Game objects
    private Board b;
    private Game game;
	private BoardAnimation ba;
    private DiceAnimation diceAn;   // the DiceAnimation object

    /**
     * Main is treated as an object because it's a JFrame.
     *
     * The GUI components are initiated and set in this constructor
     */
    public Main()
    {
        //basic layout settings
        setTitle("Backgammon");
        setSize(1024, 768);
        this.getContentPane().setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //set the ToolBar, Buttons' Background and Foreground variables
        jtb = new JToolBar();
        newGame = new JButton("New Game");
        newGame.setBackground(Color.BLACK);
        newGame.setForeground(Color.GREEN);
        newGame.addActionListener(this);
        about = new JButton("About");
        about.setBackground(Color.BLACK);
        about.setForeground(Color.GREEN);
        about.addActionListener(this);
        roll = new JButton("Roll");
        roll.setBackground(Color.BLACK);
        roll.setForeground(Color.GREEN);
        roll.setEnabled(false);
        roll.addActionListener(this);
        exit = new JButton("Exit");
        exit.setBackground(Color.BLACK);
        exit.setForeground(Color.GREEN);
        exit.addActionListener(this);
        jtb.add(newGame);
        jtb.addSeparator(new Dimension(10, 0));
        jtb.add(about);
        jtb.addSeparator(new Dimension(10, 0));
        jtb.add(exit);
        jtb.setFloatable(false);
        jtb.setBackground(Color.BLACK);

        //initialize the Board with the corresponding image and the Game
        b = new Board();
        diceAn = new DiceAnimation();
        game = new Game(this);
        game.setDice(diceAn.getDice());
        ba = new BoardAnimation();
        //b.setGame(game);

        //set layout constraints and add the components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 15, 15);
        add(jtb, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(game.getPlayer((byte) 0).getLabel(), gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(game.getPlayer((byte) 1).getLabel(), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(ba, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(this.diceAn, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(roll, gbc);
    }//end of Main

    /**
     * Action listener for the game buttons.
     *
     * @param e: the action event
     */
    public void actionPerformed(ActionEvent e)
    {
        //if the roll button is pushed, roll both dice
        if (e.getSource() == roll)
        {
        	game.setRolled();
        }
        //if the new game button is pushed, check if there is already a game running and reset the board
        else if (e.getSource() == newGame)
        {
            if (game.isRunning())
            {
                if (JOptionPane.showConfirmDialog(this, "Current game is still running.Are you sure you want to start a new one?", "Warning", JOptionPane.YES_NO_OPTION) == 0)
                {
                	 ba.clearBoard();
                     b = new Board();
                     game.exitCurrent();
                }
                else
                {
                    return;
                }
            }
            b.initCheckers();
            ba.setCurrBoard(b);
            ba.repaint();
            ba.animateBoard();
            game.startGame();
        }
        //about button pushed
        else if (e.getSource() == about)
        {
            new AboutDialog(this);
        }
        else if(e.getSource() == exit)
        {
        	if(game.isRunning())
        	{
        		if(JOptionPane.showConfirmDialog(this, "Current game is still running.Are you sure you want to exit?", "Warning", JOptionPane.YES_NO_OPTION) == 0)
        			System.exit(0);
        	}
        	else
        		System.exit(0);

        }

    }

    /**
     * get methods for Main variables
     *
     */
    public Game getGame()
    {
        return game;
    }

    public BoardAnimation getBoardAnimation()
    {
        return ba;
    }

    public Player getPlayer1()
    {
        return game.getPlayer((byte) 0);
    }

    public Player getPlayer2()
    {
        return game.getPlayer((byte) 1);
    }

    public JButton getDiceButton()
    {
        return roll;
    }
    
    public DiceAnimation getDiceAnimation()
    {
    	return this.diceAn;
    }

    /**
     * Instantiates the Main object and the GUI.
     *
     */
    public static void main(String args[])
    {
        Main m = new Main();
        m.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        m.setVisible(true);
        m.addWindowListener(new WindowAdapter(){
        	  public void windowClosing(WindowEvent we){
        		  if(((Main)we.getComponent()).getGame().isRunning())
        		  {
        			  if(JOptionPane.showConfirmDialog(we.getComponent(), "Current game is still running.Are you sure you want to exit?", "Warning", JOptionPane.YES_NO_OPTION) == 0)
        				  System.exit(0);
        		  }
        		  else
        			  System.exit(0);
        	  }
        	  });
    }
}
