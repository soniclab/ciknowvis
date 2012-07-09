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


public class NormalizedDialog extends JDialog implements ActionListener {

    private JRadioButton realValueButton, linearButton, logButton;
    private ButtonGroup bg;
    private GraphView gv;

    private JTextField tf1, tf2;
   private  double disMin = 1.0;
   private double disMax = 18;

    private boolean zeroMin;

    public NormalizedDialog(Component parent, GraphView gv, AppletDataHandler1 dh, String sizeLabel) {

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

        realValueButton.setBackground(Color.white);

        linearButton = new JRadioButton("Linear normalization");

        linearButton.setBackground(Color.white);

        logButton = new JRadioButton("Log normalization");
       
        logButton.setBackground(Color.white);

        boolean realsize;
        
            realsize = dh.isRealSize(2, disMin, disMax);
            zeroMin =  dh.isMinZero();
        if(gv.normalizedType1 ==-1){
            if (realsize)
            realValueButton.setSelected(true);
        else
            linearButton.setSelected(true);
        }else{
             if (gv.normalizedType1 ==0)
            realValueButton.setSelected(true);
        else  if (gv.normalizedType1 ==1)
            linearButton.setSelected(true);
        else
              logButton.setSelected(true); 
        }

        JLabel tf1Label = new JLabel("Min size:");
        JLabel tf2Label = new JLabel("Max size:");


            if (realsize)
                gv.setResizePara(0, -1, disMin, disMax, 0, 0, true, zeroMin, false);
            else
                gv.setResizePara(1, -1, disMin, disMax, 0, 0, true, zeroMin, false);
               gv.resizeNode();

        bg = new ButtonGroup();
        bg.add(realValueButton);
        bg.add(linearButton);
        bg.add(logButton);

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
       if (sizeLabel != null) {

            String tipText = "<html>Resize nodes by <font color=\"blue\" >" + sizeLabel + ":</font></html>";
            labelpanel.add(new JLabel(tipText));
        }


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


                    gv.setResizePara(0, -1, disMin, disMax, 0, 0, false, zeroMin, false);

            } else if (linearButton.isSelected()) {


                   gv.setResizePara(1, -1, disMin, disMax, 0, 0, false, zeroMin, false);

            } else if (logButton.isSelected()) {


                    gv.setResizePara(2, -1, disMin, disMax, 0, 0, false, zeroMin, false);

            }

             gv.fitGraph();

        } else {


            JRadioButton radioB = (JRadioButton) e.getSource();

            if (radioB == realValueButton) {


                    gv.setResizePara(0, -1, disMin, disMax, 0, 0, false, zeroMin, false);

            } else if (radioB == linearButton) {


                    gv.setResizePara(1, -1, disMin, disMax, 0, 0, false, zeroMin, false);

            } else if (radioB == logButton) {

               
                    gv.setResizePara(2, -1, disMin, disMax, 0, 0, false, zeroMin, false);

            }
        }
       gv.resizeNode();
    }


}