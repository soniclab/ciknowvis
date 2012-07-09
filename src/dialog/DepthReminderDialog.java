package dialog;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class DepthReminderDialog extends JDialog implements ActionListener {

    private JPanel panel1 = new JPanel();
    private JPanel panel2 = new JPanel();
    private JPanel insetsPanel1 = new JPanel();
    private JPanel insetsPanel3 = new JPanel();
    private JButton button = new JButton();

    private JLabel label3 = new JLabel();
    private JLabel label4 = new JLabel();

    private BorderLayout borderLayout1 = new BorderLayout();
    private BorderLayout borderLayout2 = new BorderLayout();
    private FlowLayout flowLayout1 = new FlowLayout();
    private GridLayout gridLayout1 = new GridLayout();

    private String copyright1 = "Please select two or more nodes";
    private String copyright2 = "as the root nodes !";
  //  private JCheckBox checkBox;


    public DepthReminderDialog(MainFrame frame) {

       super(JOptionPane.getFrameForComponent(frame), "", false);


        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        pack();

       // this.checkBox = checkBox;
    }

    /**
     * Component initialization
     */
    private void jbInit() throws Exception {
        //imageLabel.setIcon(new ImageIcon(MainFrame_AboutBox.class.getResource("[Your Image]")));
        this.setTitle("Reminder");
        setResizable(false);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);

        gridLayout1.setRows(2);
        gridLayout1.setColumns(1);

        label3.setText(copyright1);
        label3.setFont(new Font("SansSerif", Font.BOLD, 12));
        label4.setText(copyright2);
        label4.setFont(new Font("SansSerif", Font.BOLD, 12));

        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setText("Ok");
        button.addActionListener(this);

        this.getContentPane().add(panel1, null);

        insetsPanel3.add(label3, null);
        insetsPanel3.add(label4, null);
        panel1.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        // panel1.add(panel2, BorderLayout.NORTH);
    }

    /**
     * Overridden so we can exit when window is closed
     */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    /**
     * Close the dialog
     */
    void cancel() {
         setVisible(false);
        dispose();
    }

    /**
     * Close the dialog on a button event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            cancel();
        }
    }


}