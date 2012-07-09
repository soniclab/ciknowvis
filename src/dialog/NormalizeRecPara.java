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


public class NormalizeRecPara extends JDialog {

    private JRadioButton realValueButton, realValueButton1, linearButton, logButton, linearButton1, logButton1;
    private ButtonGroup bg;
    private ButtonGroup bg1;
    private GraphView gv;
    private JTextField tf1, tf2, tf11, tf21;
    private double disMin = 1.0;
    private double disMax = 18;
     private double disMin2 = 1.0;
    private double disMax2 = 0.0;
     private boolean zeroMin1, zeroMin2;

    private int selectedButton, selectedButton2; // 1 for realbutton, 2 for linearbuton, 3 for logbutton;

    public NormalizeRecPara(Component parent, GraphView gv, AppletDataHandler1 dh, String sizeLabel, String size2Label) {
        super(JOptionPane.getFrameForComponent(parent), "", false);
        this.gv = gv;

        Group1Action group1Action = new Group1Action();

        JButton okButton = new JButton("Cancel");
        okButton.setActionCommand("ok");
        okButton.addActionListener(group1Action);
        okButton.setBackground(Color.white);

        JButton setButton = new JButton("Set");
        setButton.setActionCommand("set");
        setButton.addActionListener(group1Action);
        setButton.setBackground(Color.white);

        realValueButton = new JRadioButton("Real value");

        realValueButton.setBackground(Color.white);


        linearButton = new JRadioButton("Linear normalization");

        linearButton.setBackground(Color.white);
        logButton = new JRadioButton("Log normalization");

        logButton.setBackground(Color.white);

        boolean realsize, realsize2;

        realsize = dh.isRealSize(4, disMin, disMax);

        zeroMin1 =  dh.isMinZero();
      // Rectangle2D r =gv.getDisplay().getBounds();

        //double height = gv.frameheight;
      //  double width = gv.framewidth - 175;

       // double radius = 0.49 * (height < width ? width : height);
          double radius = 358.4;
         disMax2 =  radius;
        disMin2   = 20.0;

        realsize2 = dh.isRealSize(5, 20.0,  radius);
         zeroMin2 =  dh.isMinZero2();

        if (realsize) {
            realValueButton.setSelected(true);
            selectedButton = 0;
        } else {
            linearButton.setSelected(true);
            selectedButton = 1;
        }
        if (size2Label != null) {

            realValueButton1 = new JRadioButton("Real value");

            realValueButton1.setBackground(Color.white);


            linearButton1 = new JRadioButton("Linear normalization");

            linearButton1.setBackground(Color.white);
            logButton1 = new JRadioButton("Log normalization");

            logButton1.setBackground(Color.white);


        }
         int normalizeStatus1 = 0;
         int normalizeStatus2 = 0;
        if (!realsize)
        normalizeStatus1 = 1;
         if (!realsize2)
        normalizeStatus2 = 1;

            gv.setResizePara(normalizeStatus1, normalizeStatus2, disMin, disMax, disMin2, disMax2,true, zeroMin1, zeroMin2);

           gv.resizeNode();

        JLabel tf1Label = new JLabel("Min size: ");
        JLabel tf2Label = new JLabel("Max size: ");

        JLabel tf11Label = new JLabel("Min size: ");
        JLabel tf21Label = new JLabel("Max size: ");

        bg = new ButtonGroup();

        bg.add(realValueButton);
        bg.add(linearButton);
        bg.add(logButton);

        realValueButton.addActionListener(group1Action);
        linearButton.addActionListener(group1Action);
        logButton.addActionListener(group1Action);


        JPanel radiopanel = new JPanel(new GridLayout(4, 1));

        radiopanel.setBackground(Color.white);
        radiopanel.add(realValueButton);
         radiopanel.add(new JLabel("  -------------------------------------------"));
        radiopanel.add(linearButton);
        radiopanel.add(logButton);
        JPanel radiopanel1 = null;

      /*  if (size2Label != null) {

            Group2Action group2Action = new Group2Action();
            bg1 = new ButtonGroup();
            bg1.add(realValueButton1);
            bg1.add(linearButton1);
            bg1.add(logButton1);

            if (realsize2) {
                realValueButton1.setSelected(true);
                selectedButton2 = 0;
            } else {
                linearButton1.setSelected(true);
                selectedButton2 = 1;
            }
            realValueButton1.addActionListener(group2Action);
            linearButton1.addActionListener(group2Action);
            logButton1.addActionListener(group2Action);


            radiopanel1 = new JPanel(new GridLayout(4, 1));
            radiopanel1.setBackground(Color.white);

            radiopanel1.add(realValueButton1);
               radiopanel1.add(new JLabel("  -------------------------------------------"));
            radiopanel1.add(linearButton1);
            radiopanel1.add(logButton1);

        }
       */

        tf1 = new JTextField(5);
        tf1.setText(disMin + "");

        tf1.addActionListener(group1Action);

        tf2 = new JTextField(5);
        tf2.setText(disMax + "");
        tf2.addActionListener(group1Action);


        JPanel textpanel = new JPanel(new GridLayout(2, 2));
        textpanel.setBorder(BorderFactory.createTitledBorder("Normalization Setting:"));
        textpanel.setBackground(Color.white);
        textpanel.add(tf1Label);
        textpanel.add(tf1);
        textpanel.add(tf2Label);
        textpanel.add(tf2);


         tf11 = new JTextField(5);
        tf11.setText(disMin2 + "");

        tf11.addActionListener(group1Action);

        tf21 = new JTextField(5);
        tf21.setText(disMax2 + "");
        tf21.setEditable(false);
        tf21.setEnabled(false);
       // tf21.addActionListener(group1Action);


        JPanel textpanel1 = new JPanel(new GridLayout(2, 2));
        textpanel1.setBorder(BorderFactory.createTitledBorder("Normalization Setting:"));
        textpanel1.setBackground(Color.white);
        textpanel1.add(tf11Label);
        textpanel1.add(tf11);
        textpanel1.add(tf21Label);
        textpanel1.add(tf21);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel okPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okPanel.setBackground(Color.white);
         okPanel.add(setButton);
        okPanel.add(okButton);
         /*
        JPanel labelpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelpanel.setBackground(Color.white);
        String tipText = "Node size by ";
        labelpanel.add(new JLabel(tipText));


        panel.add(labelpanel, BorderLayout.NORTH);
       */

      //  JPanel bigpanel = new JPanel(new GridLayout(2, 1));
      //  bigpanel.setBackground(Color.white);

         JPanel bigpanel1 = new JPanel(new BorderLayout());

        bigpanel1.setBackground(Color.white);

       //  JPanel bigpanel2 = new JPanel(new BorderLayout());
     //   bigpanel2.setBackground(Color.white);
         String tipText1 = "";
        if(gv.getSWLabel()!=null)
         tipText1  = "<html> Node size by <font color=\"blue\" >" + gv.getSWLabel()[1] + ":          </font></html>";
       else
         tipText1  = "<html> Node size by connection to <font color=\"blue\" >" + sizeLabel + ":          </font></html>";
      //  String tipText2 = "<html> Edge length by <font color=\"blue\" >" + size2Label + ":         </font></html>";
        JLabel title1 = new JLabel(tipText1);
       // JLabel title2 = new JLabel(tipText2);

         JPanel labelpanel1 = new JPanel(new GridLayout(2, 1));
      //   JPanel labelpanel2 = new JPanel(new GridLayout(2, 1));

        labelpanel1.add(new JLabel());
        labelpanel1.setBackground(Color.white);
        labelpanel1.add(title1);


       //  labelpanel2.add(new JLabel());
       //   labelpanel2.setBackground(Color.white);
       // labelpanel2.add(title2);

        bigpanel1.add(labelpanel1, BorderLayout.NORTH);
        bigpanel1.add(radiopanel, BorderLayout.CENTER);
         bigpanel1.add(textpanel, BorderLayout.SOUTH);
        /*
         bigpanel2.add(labelpanel2,BorderLayout.NORTH);
        bigpanel2.add(radiopanel1, BorderLayout.CENTER);
        bigpanel2.add(textpanel1, BorderLayout.SOUTH);
       */

//      //  bigpanel.add(bigpanel2);

        panel.add(bigpanel1, BorderLayout.CENTER);
        panel.add(okPanel, BorderLayout.SOUTH);

        setContentPane(panel);
          pack();

    }


    private class Group1Action implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();
            if (action.equals("ok")) {

                setVisible(false);
                dispose();
            }else{

                if (action.equals("set")) {


                disMin = Double.parseDouble(tf1.getText());
                disMax = Double.parseDouble(tf2.getText());


                if (realValueButton.isSelected()) {


                        gv.setResizePara(0, 0, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);


                } else if (linearButton.isSelected()) {


                        gv.setResizePara(1, 0, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);


                } else if (logButton.isSelected()) {


                        gv.setResizePara(2, 0, disMin, disMax,  disMin2, disMax2,false, zeroMin1, zeroMin2);


                }


            } else {
                JRadioButton radioB = (JRadioButton) e.getSource();
                if (radioB == realValueButton) {
                    gv.setResizePara(0, 0, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);
                } else if (radioB == linearButton) {

                    gv.setResizePara(1, 0, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);


                } else if (radioB == logButton) {

                   gv.setResizePara(2, 0, disMin, disMax,  disMin2, disMax2,false, zeroMin1, zeroMin2);
                }

            }
            gv.resizeNode();
            }
        }
    }
   /*
    private class Group2Action implements ActionListener {
           public void actionPerformed(ActionEvent e) {

               JRadioButton radioB = (JRadioButton) e.getSource();
               if (radioB == realValueButton1) {
                   selectedButton2 = 0;
                   if (selectedButton == 0) {

                       gv.setResizePara(0, 0, disMin, disMax,  disMin2, disMax2,false, zeroMin1, zeroMin2);

                   } else if (selectedButton == 1) {

                      gv.setResizePara(1, 0, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   } else if (selectedButton == 2) {

                       gv.setResizePara(2, 0, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   }
               } else if (radioB == linearButton1) {

                   selectedButton2 = 1;
                   if (selectedButton == 0) {

                       gv.setResizePara(0, 1, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   } else if (selectedButton == 1) {

                       gv.setResizePara(1, 1, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   } else if (selectedButton == 2) {

                       gv.setResizePara(2, 1, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   }

               } else if (radioB == logButton1) {

                   selectedButton2 = 2;
                   if (selectedButton == 0) {

                       gv.setResizePara(0, 2, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   } else if (selectedButton == 1) {

                       gv.setResizePara(1, 2, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   } else if (selectedButton == 2) {

                      gv.setResizePara(2, 2, disMin, disMax, disMin2, disMax2, false, zeroMin1, zeroMin2);

                   }
               }
               gv.resizeLink();
           }

       } */
   }
