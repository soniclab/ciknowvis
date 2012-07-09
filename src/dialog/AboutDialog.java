package dialog;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Calendar;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class AboutDialog extends JDialog implements ActionListener {

    private JPanel panel1 = new JPanel();
    private JPanel panel2 = new JPanel();
    private JPanel insetsPanel1 = new JPanel();
    private JPanel insetsPanel2 = new JPanel();
    private JPanel insetsPanel3 = new JPanel();
    private JButton button1 = new JButton();
    private JLabel imageLabel = new JLabel();
   private JLabel label1 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label3 = new JLabel();
    private JLabel label4 = new JLabel();
    private JLabel label5 = new JLabel();
    private JLabel label6 = new JLabel();
    private JLabel label7 = new JLabel();
    private JLabel label8 = new JLabel();
    private JLabel label9 = new JLabel();
   private boolean isApplet;

    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    FlowLayout flowLayout1 = new FlowLayout();
    GridLayout gridLayout1 = new GridLayout();
    String product = "C-IKNOW Visualizer 1.0";
    String version = "  ";
    String copyright1 = " Noshir Contractor, Science of Networks in Communities ";
    String copyright2 = "(SONIC), Northwestern University, Evanston, IL";

    String comments1 = "C-IKNOW Visualizer is developed by Jinling Li in the Science of Networks ";
    String comments2 = "in Communities (SONIC) research group at Northwestern University. ";
    String comments3 = "C-IKNOW development is supported primarily by grants from the ";
    String comments4="Army research Laboratory, the Army Research Institute, the Air Force Research,";
    String comments5 = "the National Science Foundation (NSF) and the National Institutes of Health (NIH).";

    public AboutDialog(MainFrame frame) {

         super(JOptionPane.getFrameForComponent(frame), "", false);


        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        isApplet = frame.getAppletStaus();
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        pack();

    }

    /**
     * Component initialization
     */
    private void jbInit() throws Exception {
        //imageLabel.setIcon(new ImageIcon(MainFrame_AboutBox.class.getResource("[Your Image]")));
        this.setTitle("About C-IKNOW Visualizer");
      //  if(!isApplet){
      //  java.net.URL url = getClass().getResource("images/" + "title.jpg");

      //  Toolkit tk = Toolkit.getDefaultToolkit();
      //  Image img = tk.getImage(url);
      //  this.setIconImage(img);
      //  }

        setResizable(false);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);

        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(flowLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(9);
        gridLayout1.setColumns(1);
        label1.setText(product);
        label2.setText(version);

        Calendar calendar = java.util.Calendar.getInstance();

        int currentYear= calendar.get(Calendar.YEAR);
         copyright1 = "©" + currentYear + copyright1;
        label3.setText(copyright1);
        label3.setFont(new Font("SansSerif", Font.BOLD, 12));
        label4.setText(copyright2);
        label4.setFont(new Font("SansSerif", Font.BOLD, 12));
        label5.setText(comments1);
        label6.setText(comments2);
        label7.setText(comments3);
        label8.setText(comments4);
         label9.setText(comments5);
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button1.setText("Ok");
        button1.addActionListener(this);
        insetsPanel2.add(imageLabel, null);
        panel2.add(insetsPanel2, BorderLayout.WEST);
        this.getContentPane().add(panel1, null);
        insetsPanel3.add(label1, null);
        insetsPanel3.add(label2, null);

        insetsPanel3.add(label5, null);
        insetsPanel3.add(label6, null);
        insetsPanel3.add(label7, null);
        insetsPanel3.add(label8, null);
        insetsPanel3.add(label9, null);
        insetsPanel3.add(label3, null);
        insetsPanel3.add(label4, null);
        panel2.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
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
        dispose();
    }

    /**
     * Close the dialog on a button event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            cancel();
        }
    }
}