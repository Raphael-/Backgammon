
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DiceAnimation extends JPanel
{
    // the GUI components for the dice
    private JLabel die1, die2, extradie1, extradie2; // extra dice for doubles
    private JButton thr;
    private GridBagConstraints c;
    private RollDice rd;                // animation for the dice
    private Dice d;						// dice object to animate
    private boolean done;
    
    /**
     * Dice constructor.
     *
     */
    public DiceAnimation()
    {
        //set all the GUI variables for the dice JPanel
        setBackground(Color.BLACK);
        c = new GridBagConstraints();
        die1 = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/1.gif")));
        die2 = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/1.gif")));
        extradie1 = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/1.gif")));
        extradie2 = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/1.gif")));
        thr = new JButton("Roll");
        setLayout(new GridBagLayout());
        thr.setPreferredSize(new Dimension(70, 30));
        die1.setPreferredSize(new Dimension(48, 48));
        die2.setPreferredSize(new Dimension(48, 48));
        showExtra(false);
        c.gridx = 0;
        c.gridy = 0;
        add(extradie1, c);
        c.gridx = 0;
        c.gridy = 1;
        add(die1, c);
        c.gridx = 0;
        c.gridy = 2;
        add(die2, c);
        c.gridx = 0;
        c.gridy = 3;
        add(extradie2, c);
        d = new Dice();
    }// end of constructor

    /**
     * Executes the swing worker for the animation.
     */
    public void animatedRoll(byte howManyDice) throws InterruptedException, ExecutionException, CancellationException
    {
        rd = new RollDice(this, howManyDice);
        this.done = false;
        rd.execute();
    }

    /**
     * The current thread waits for the dice to be rolled.
     */
    public void waitForRoll() throws InterruptedException, CancellationException, ExecutionException
    {
        rd.get();
        this.done = true;
    }

    /**
     * Cancels the roll if necessary.
     */
    public void cancelAnimatedRoll()
    {
        rd.cancel(true);
    }

    /**
     * Show the extra dice for doubles.
     */
    public void showExtra(boolean show)
    {
        extradie1.setVisible(show);
        extradie2.setVisible(show);
    }

    public Dice getDice()
    {
    	return this.d;
    }
    
    public boolean doneRolling()
    {
    	return this.done;
    }
    
    public void setRolling(boolean b)
    {
    	this.done = b;
    }
    
    /**
     * Set the dice icon depending on the rolled number.
     */
    public void setDieIcon(byte dieIcon, byte dieResult)
    {
        switch (dieIcon)
        {
            case 0:
                die1.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/" + dieResult + ".gif")));
                break;
            case 1:
                die2.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/" + dieResult + ".gif")));
                break;
            case 2:
                extradie1.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/" + dieResult + ".gif")));
                break;
            case 3:
                extradie2.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/" + dieResult + ".gif")));
                break;
        }
    }
}
