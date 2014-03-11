
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

class RollDice extends SwingWorker<Void, Void>
{
    private Random rg;
    private byte count;
    private Clip c;
    private byte num1, num2; // die numbers
    private DiceAnimation da;
    private byte diceToRoll;

    public RollDice(DiceAnimation da, byte diceToRoll)
    {
        rg = new Random();
        this.diceToRoll = diceToRoll;
        this.da = da;
        this.count = 20;
        try
        {
            c = AudioSystem.getClip();
        } catch (LineUnavailableException e)
        {
            System.err.println("Cannot open audio line");
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground() throws Exception
    {
    	Game game = ((Main) SwingUtilities.getRoot(da)).getGame();
    	if(!game.isFirstRoll())
    		game.setEnabledCheckers(false);
        da.showExtra(false);
        da.getDice().reset(); // reset to not consumed
        while (this.count > 0)
        {
            decideRoll(this.diceToRoll);
            playSound("shake.wav");
            this.count--;
            Thread.sleep(100);
        }

        if (c.isRunning())
        {
            c.stop();
        }
        playSound("roll.wav");

        int[] b = da.getDice().getDiceResults();
        if (this.diceToRoll == 1)
        {
            b[0] = num1;
        }
        else if (this.diceToRoll == 2)
        {
            b[1] = num2;
        }
        else
        {
            b[0] = num1;
            b[1] = num2;
        }

        // code for doubles
        if (num1 == num2 && this.diceToRoll > 2)
        {
            da.showExtra(true);
            da.setDieIcon((byte) 2, num1);
            da.setDieIcon((byte) 3, num1);
            b[2] = num1;
            b[3] = num2;
        }
        else
        {
            b[2] = 0;
            b[3] = 0;
            da.getDice().consume((byte) 2);
            da.getDice().consume((byte) 3);
        }
    	if(!game.isFirstRoll() && game.getCurrentPlayer().getNum()!=2)
    		game.setEnabledCheckers(true);
        return null;
    }

    public void playSound(String soundName)
    {
        if (c.isRunning())
        {
            return;
        }
        c.close();
        AudioInputStream audioInputStream = null;
        try
        {
            audioInputStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("audio/" + soundName));
            c.open(audioInputStream);
        } catch (Exception ex)
        {
            System.err.println("Sound error.");
            ex.printStackTrace();
        }
        c.start();
    }

    /**
     * Decide which dice to roll.
     *
     * @param diceToRoll which dice to roll 1 = upper die, 2 = lower die,  any other value = both
     */
    private void decideRoll(byte diceToRoll)
    {
        switch (diceToRoll)
        {
            case 1:
                num1 = (byte) (1 + rg.nextInt(6));
                da.setDieIcon((byte) 0, num1);
                break;
            case 2:
                num2 = (byte) (1 + rg.nextInt(6));
                da.setDieIcon((byte) 1, num2);
                break;
            default:
                num1 = (byte) (1 + rg.nextInt(6));
                da.setDieIcon((byte) 0, num1);
                num2 = (byte) (1 + rg.nextInt(6));
                da.setDieIcon((byte) 1, num2);
                break;
        }
    }
}
