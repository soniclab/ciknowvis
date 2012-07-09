package dialog;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import visual.GraphView;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class ImageDialog extends JDialog implements ActionListener {

    private GraphView gv;

   private javax.swing.JTextField  heightF;
   private  int size = 50;
   public javax.swing.JTextField widthF;
    public ImageDialog(Component parent, GraphView gv) {

     super(JOptionPane.getFrameForComponent(parent), "", false);
        this.gv = gv;
        this.setTitle("Resize Image");
        JButton okButton = new JButton("Cancel");
        okButton.setActionCommand("cancel");
        okButton.addActionListener(this);
        okButton.setBackground(Color.white);

        JButton setButton = new JButton("Set");
        setButton.setActionCommand("set");
        setButton.addActionListener(this);
        setButton.setBackground(Color.white);

        JLabel widthLabel = new JLabel("    Size:");

        widthF = new JTextField(3);

        size = gv.imageSize;
        widthF.setText(size + "");

        widthF.addActionListener(this);

        JLabel percent1 = new JLabel(" %");
        JLabel blank = new JLabel(" ");
        JLabel blank1 = new JLabel(" ");
        JLabel blank2 = new JLabel(" ");

        JPanel textpanel = new JPanel(new GridLayout(2, 3));


        textpanel.setBackground(Color.white);
        textpanel.add(blank);
        textpanel.add(blank1);
        textpanel.add(blank2);

        textpanel.add(widthLabel);
        textpanel.add(widthF);
        textpanel.add(percent1);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel okPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okPanel.setBackground(Color.white);
         okPanel.add(setButton);
        okPanel.add(okButton);

        panel.add(textpanel, BorderLayout.CENTER);
        panel.add(okPanel, BorderLayout.SOUTH);

        setContentPane(panel);
         pack();

    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("cancel")) {

            setVisible(false);
            dispose();
        } else if (action.equals("set")) {

           size = Integer.parseInt(widthF.getText());

           gv.imageNode(size);

        }

    }
}