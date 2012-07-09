package dialog;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import visual.GraphView;
import prefuse.data.Graph;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */

public class ClusteringDialog extends JDialog implements ActionListener {



    private JButton okJButton;

    private JButton cancelJButton;

    private JComboBox viewBox;

     private JComboBox disBox;

    private GraphView view;
 
     String disValue;
    String viewValue;

    // graph view
    final String[] viewList = {"Component view",  "Structural view"};
    // node distance constant
    final String[] disList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    public ClusteringDialog(MainFrame _frame) {
        super(JOptionPane.getFrameForComponent(_frame), "", false);

         view = _frame.getGP1();

        initDialog();

    }


    private JPanel createButtons() {
        JPanel gridJPanel;
        JPanel flowJPanel;

        okJButton = new JButton("OK");
        cancelJButton = new JButton("Cancel");
        okJButton.addActionListener(this);
        cancelJButton.addActionListener(this);
        gridJPanel = new JPanel(new GridLayout(1, 2));
        gridJPanel.add(okJButton);
        gridJPanel.add(cancelJButton);
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowJPanel.add(gridJPanel);

        return flowJPanel;
    }

    private JPanel createComboBoxPane() {

        disValue = getDefaultDistance();
        viewValue = viewList[0];

        disBox = new JComboBox(disList);

        disBox.setSelectedIndex(Integer.parseInt(disValue) - 1);

        disBox.addItemListener(new ItemListener2());

        viewBox = new JComboBox(viewList);

        viewBox.setSelectedIndex(0);

        viewBox.addItemListener(new ItemListener2());

        JPanel boxJPanel = new JPanel(new GridLayout(2, 2));

        boxJPanel.add(new JLabel("Graph style: "));
         boxJPanel.add(viewBox);
         boxJPanel.add(new JLabel("Distance between nodes: "));
         boxJPanel.add(disBox);

        return boxJPanel;
    }

    // only one component-> 2; multiple components and each size of component < 10 -> 2;
    //  multiple components and one of the size of component > 10, (1)count of components with size less than 10  < 10, ->2
    //(2) count of components with size less than 10  > 9, ->5
    private String getDefaultDistance(){

        Graph g = (Graph) view.getGraph();


         if(g.getNodeCount() < 200){
            disValue = "2";
        }else if(g.getNodeCount() > 199 && g.getNodeCount() < 300){
             disValue = "3";
            }else if(g.getNodeCount() > 299 && g.getNodeCount() < 500){
             disValue = "4";
          }else if(g.getNodeCount() > 499 && g.getNodeCount() < 600){
             disValue = "5";
         }else if(g.getNodeCount() > 599 && g.getNodeCount() < 700){
             disValue = "6";
         }else if(g.getNodeCount() > 699 && g.getNodeCount() < 800){
             disValue = "7";
        }else if(g.getNodeCount() > 799 && g.getNodeCount() < 900){
             disValue = "8";
        }else if(g.getNodeCount() > 899){
            disValue = "9";
       }

        return disValue;
    }

    private void initDialog() {

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JLabel("Options: "), BorderLayout.NORTH);
        getContentPane().add(createComboBoxPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        pack();

    }


    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(okJButton)) {

           view.generateAutoLayout(disValue, viewValue);
            setVisible(false);
            dispose();
        } else if (e.getSource().equals(cancelJButton)) {
            setVisible(false);
            dispose();
        }
    }




    class ItemListener2 implements ItemListener {
        // This method is called only if a new item has been selected.
        public void itemStateChanged(ItemEvent ie) {

            if (ie.getSource().equals(disBox)) {
                disValue = (String) disBox.getSelectedItem();

            } else if (ie.getSource().equals(viewBox)) {
               viewValue = (String) viewBox.getSelectedItem();

            }
        }
    }
}

