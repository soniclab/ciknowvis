package dialog;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import visual.GraphView;
import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class WeightNormalizedDialog extends JDialog implements ActionListener {

    private JRadioButton realValueButton, linearButton, logButton;
    private ButtonGroup bg;
    private GraphView gv;


    private JTextField tf1, tf2;
   private  double disMin = 1.0;
   private double disMax = 18;

    private boolean zeroMin;

    public WeightNormalizedDialog(Component parent, GraphView gv, AppletDataHandler1 dh) {
        super(JOptionPane.getFrameForComponent(parent), "", false);
        this.gv = gv;

        JButton okButton = new JButton("Cancel");
        okButton.setActionCommand("ok");
        okButton.addActionListener(this);
        okButton.setBackground(Color.white);

        JButton setButton = new JButton("Set");
        setButton.setActionCommand("set");
        setButton.addActionListener(this);
        setButton.setBackground(Color.white);


        realValueButton = new JRadioButton("Real value");
        realValueButton.setActionCommand("linear");
        realValueButton.setBackground(Color.white);

        linearButton = new JRadioButton("Linear normalization");
        linearButton.setActionCommand("linear");
        linearButton.setBackground(Color.white);

        logButton = new JRadioButton("Log normalization");
        logButton.setActionCommand("log");
        logButton.setBackground(Color.white);



        boolean realsize;

            disMax = 10;
            realsize = dh.isRealSize(1, disMin, disMax);

        if (realsize)
            realValueButton.setSelected(true);
        else
            linearButton.setSelected(true);

        JLabel tf1Label = new JLabel("Min size:");
        JLabel tf2Label = new JLabel("Max size:");



            if (realsize)
                gv.maxEdgeWidth(0, disMin, disMax);
            else
                gv.maxEdgeWidth(1, disMin, disMax);

            tf1Label = new JLabel("Min width:");
            tf2Label = new JLabel("Max width:");

        bg = new ButtonGroup();
        bg.add(realValueButton);
        bg.add(linearButton);
        bg.add(logButton);

        this.add(realValueButton);
        this.add(linearButton);
        this.add(logButton);

        realValueButton.addActionListener(this);
        linearButton.addActionListener(this);
        logButton.addActionListener(this);


        JPanel radiopanel = new JPanel(new GridLayout(4, 1));
        radiopanel.setBackground(Color.white);

        radiopanel.add(realValueButton);
        radiopanel.add(new JLabel("  -------------------------------------------  "));
        radiopanel.add(linearButton);
        radiopanel.add(logButton);


        tf1 = new JTextField(5);
        tf1.setText(disMin + "");

        tf1.addActionListener(this);

        tf2 = new JTextField(5);
        tf2.setText(disMax + "");
        tf2.addActionListener(this);


        JPanel textpanel = new JPanel(new GridLayout(2, 2));
        textpanel.setBorder(BorderFactory.createTitledBorder("Normalization Setting:"));
        textpanel.setBackground(Color.white);
        textpanel.add(tf1Label);
        textpanel.add(tf1);
        textpanel.add(tf2Label);
        textpanel.add(tf2);

        JPanel centralpanel = new JPanel(new BorderLayout());
        centralpanel.add(radiopanel, BorderLayout.CENTER);
         centralpanel.add(textpanel, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel okPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okPanel.setBackground(Color.white);
         okPanel.add(setButton);
        okPanel.add(okButton);


        JPanel labelpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelpanel.setBackground(Color.white);

            labelpanel.add(new JLabel("Edge width by :"));


        panel.add(labelpanel, BorderLayout.NORTH);

        panel.add(centralpanel, BorderLayout.CENTER);
        panel.add(okPanel, BorderLayout.SOUTH);

        setContentPane(panel);
         pack();

    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("ok")) {

            setVisible(false);
            dispose();
        } else if (action.equals("set")) {

           disMin = Double.parseDouble(tf1.getText());
           disMax = Double.parseDouble(tf2.getText());
           if (realValueButton.isSelected()) {

                gv.maxEdgeWidth(0, disMin, disMax);

            } else if (linearButton.isSelected()) {
                gv.maxEdgeWidth(1, disMin, disMax);

            } else if (logButton.isSelected()) {
                gv.maxEdgeWidth(2, disMin, disMax);
            }

        } else {

            JRadioButton radioB = (JRadioButton) e.getSource();

            if (radioB == realValueButton) {

                    gv.maxEdgeWidth(0, disMin, disMax);

            } else if (radioB == linearButton) {

                    gv.maxEdgeWidth(1, disMin, disMax);

            } else if (radioB == logButton) {


                    gv.maxEdgeWidth(2, disMin, disMax);
               
            }
        }

    }


}