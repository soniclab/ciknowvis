package dialog;

import admin.MainFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import prefuse.data.tuple.TupleSet;
import prefuse.Visualization;
import prefuse.visual.VisualItem;
import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class ShapeDialog extends JDialog {

    // private static final String message = "Select edge type:";

    private MainFrame frame;
    private JButton hideJButton;
    private JButton showJButton;
    private JButton cancelJButton;

    private JCheckBox[] checkButton;

    private Set<String> choices;

    private Set<String> attriSet;
    private String[] keyArray;


    public ShapeDialog(MainFrame _frame) {

        super(JOptionPane.getFrameForComponent(_frame), "", false);

        frame = _frame;

        choices = new HashSet<String>();

        initDialog();


    }


    private JPanel createButtons() {
        JPanel gridJPanel;
        JPanel flowJPanel;

        hideJButton = new JButton("Hide");
        showJButton = new JButton("Show");
        cancelJButton = new JButton("Cancel");
        gridJPanel = new JPanel(new GridLayout(1, 3));
        gridJPanel.add(hideJButton);
        gridJPanel.add(showJButton);
        gridJPanel.add(cancelJButton);
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowJPanel.add(gridJPanel);

        return flowJPanel;
    }


    private JScrollPane createCheckBoxPane() {
        ItemActionListener itemActionListener = new ItemActionListener();

        JPanel boxJPanel = new JPanel();
        boxJPanel.setLayout(new BoxLayout(boxJPanel, BoxLayout.Y_AXIS));

        boxJPanel.add(new JLabel(""));

        Visualization vis = frame.getGP1().getCurrentVis();
        TupleSet allTuple = vis.getVisualGroup("graph.nodes");
        attriSet = new HashSet<String>();

               Iterator allIems = allTuple.tuples();

               while (allIems.hasNext()) {

                   VisualItem item = (VisualItem) allIems.next();
                   String attri = item.getString("shapeAttri");
                   attriSet.add(attri);
               }

        keyArray = attriSet.toArray(new String[attriSet.size()]);


        Arrays.sort(keyArray);
        checkButton = new JCheckBox[keyArray.length];

        int attriLength = 0;
        for (int j = 0; j < keyArray.length; j++) {

            //check box for nodetype
            checkButton[j] = new JCheckBox(keyArray[j]);

            checkButton[j].addItemListener(itemActionListener);

            boxJPanel.add(checkButton[j]);

        }

        boxJPanel.add(new JLabel(""));
        return new JScrollPane(boxJPanel);

    }


    private void initDialog() {

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JLabel("Node Shaped by: " + frame.getHandler1().getShapeBy()), BorderLayout.NORTH);
        getContentPane().add(createCheckBoxPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        registerListeners();
        pack();

        if (keyArray.length > 20)
            setSize(350, 600);
    }

    // Listeners
    private class ItemActionListener implements ItemListener {

        /**
         * Listens to the check boxes.
         */
        public void itemStateChanged(ItemEvent e) {


            String type = "";
            Object selected = e.getItemSelectable();

            for (int i = 0; i < attriSet.size(); i++) {
                type = checkButton[i].getText();
                if (selected == checkButton[i]) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        choices.add(type);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        choices.remove(type);
                    }
                }
            }
        }
    }


    private void registerListeners() {
        ButtonsActionListener buttonsActionListener = new ButtonsActionListener();

        hideJButton.addActionListener(buttonsActionListener);
        showJButton.addActionListener(buttonsActionListener);
        cancelJButton.addActionListener(buttonsActionListener);
    }


    // Listeners
    private class ButtonsActionListener implements ActionListener {

        GraphView view = frame.getGP1();

        public void actionPerformed(ActionEvent e) {

            if (e.getSource().equals(hideJButton)) {
              //  System.out.println("hiden attriChoices: " + attriChoices);
                view.hideNodeShape(choices);
                setVisible(false);
                dispose();

            } else if (e.getSource().equals(showJButton)) {
                //  System.out.println("show attriChoices: " + attriChoices);
                view.showNodeShape(choices);
                setVisible(false);
                dispose();

            } else {

                setVisible(false);
                dispose();

            }

        }
    }


}