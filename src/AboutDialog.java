
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class AboutDialog implements ActionListener
{
    //GUI components. JDialog is used for the pop-up

    private JButton b;
    private JDialog jdial;
    private JTextArea jta;
    private final String textToPrint = "Backgammon using MiniMax Algorithm.\nCreators:" + "\nMagkos Rafail" + "\nMpogdanos Michail" + "\nNtatsev Ntaniel";
    private Timer t;

    /**
     * The Dialog GUI constructor.
     *
     * @param parent: the main frame
     */
    public AboutDialog(JFrame parent)
    {
        //GUI components are initialized and set
        jdial = new JDialog(parent);
        jdial.setSize(300, 200);
        jdial.setTitle("About");
        jdial.getContentPane().setBackground(Color.BLACK);
        jdial.setResizable(false);
        jta = new JTextArea(10, 50);
        jta.setBackground(Color.BLACK);
        jta.setForeground(Color.GREEN);
        jta.setEditable(false);
        b = new JButton("OK");
        b.setPreferredSize(new Dimension(55, 25));
        b.addActionListener(this);
        b.setBackground(Color.BLACK);
        b.setForeground(Color.GREEN);
        jdial.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        jdial.setLocationRelativeTo(parent);
        jdial.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        jdial.add(jta, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 15, 15, 15);
        jdial.add(b, gbc);
        jdial.setVisible(true);

        //a timer is used for the desired effect of the text display
        //an anonymous inner class is used to make the listener respond to the desired event
        t = new Timer(80, new ActionListener()
        {

            int i = 0; //letter position variable

            /**
             * Prints the desired text, character by character.
             *
             * When done, it loops between the two if-statements so as to display the underscore
             * with the desired effect
             */
            public void actionPerformed(ActionEvent e)
            {

                if (i < textToPrint.length())
                {
                    jta.append(String.valueOf(textToPrint.charAt(i++)));
                }
                else if (i == textToPrint.length())
                {
                    jta.append("_");
                    i++;
                }
                else if (i > textToPrint.length())
                {
                    jta.setText(jta.getText().substring(0, jta.getText().length() - 1));
                    i--;
                }
            }
        });
        t.start();
    }

    /**
     * The listener for the dialog button.
     *
     */
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        if (arg0.getSource() == b)
        {
            if (t.isRunning())
            {
                t.stop();
            }
            jdial.dispose();
        }
    }
}
