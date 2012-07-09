package dialog;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.NodeItem;
import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class LinkSelection extends JDialog {

    // private static final String message = "Select edge type:";

    private GraphView view;

    private JButton showJButton;
    private JButton cancelJButton;
    private Set<String> choices;
    private JCheckBox[] checkButton;
    private TupleSet focus;
    private NodeItem item;
    private static final String[] types = {"All links", "Outgoing link", "Incoming link", "Mutual link", "Undirected link"};
    private static final String[] types1 = {"All links", "Mutual link", "Undirected link"};

    public LinkSelection(Component parent, GraphView view, NodeItem item, TupleSet focus) {
        super(JOptionPane.getFrameForComponent(parent), "Select link type", true);

        this.view = view;
        this.focus = focus;
        this.item = item;
        choices = new HashSet<String>();
        initDialog();

    }


    private JPanel createButtons() {
        JPanel gridJPanel;
        JPanel flowJPanel;


        showJButton = new JButton("Show");
        cancelJButton = new JButton("Cancel");
        gridJPanel = new JPanel(new GridLayout(1, 2));

        gridJPanel.add(showJButton);
        gridJPanel.add(cancelJButton);
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowJPanel.add(gridJPanel);

        return flowJPanel;
    }

    private JPanel createCheckBoxPane() {
        ItemActionListener itemActionListener = new ItemActionListener();

        JPanel boxJPanel = new JPanel();
        boxJPanel.setLayout(new BoxLayout(boxJPanel, BoxLayout.Y_AXIS));

        boxJPanel.add(new JLabel(""));
        if (focus == null)
            checkButton = new JCheckBox[5];
        else
            checkButton = new JCheckBox[3];

        for (int j = 0; j < checkButton.length; j++) {

            //check box for nodetype
            if (focus == null) {
                checkButton[j] = new JCheckBox(types[j]);
                boxJPanel.add(checkButton[j]);
                if (j == 0)
                    boxJPanel.add(new JLabel("   ---------------------------------"));
            } else {
                checkButton[j] = new JCheckBox(types1[j]);
                boxJPanel.add(checkButton[j]);

            }
            checkButton[j].addItemListener(itemActionListener);

        }


        boxJPanel.add(new JLabel(""));
        return boxJPanel;

    }


    private void initDialog() {

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JLabel(), BorderLayout.NORTH);
        getContentPane().add(createCheckBoxPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        registerListeners();
        pack();

    }

    // Listeners
    private class ItemActionListener implements ItemListener {

        /**
         * Listens to the check boxes.
         */
        public void itemStateChanged(ItemEvent e) {


            String type = "";
            Object selected = e.getItemSelectable();
            int checkBoxlength;
            if (focus == null)
                checkBoxlength = types.length;
            else
                checkBoxlength = types1.length;
            for (int i = 0; i < checkBoxlength; i++) {
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

        showJButton.addActionListener(buttonsActionListener);
        cancelJButton.addActionListener(buttonsActionListener);
    }


    // Listeners
    private class ButtonsActionListener implements ActionListener {


        public void actionPerformed(ActionEvent e) {

            if (e.getSource().equals(showJButton)) {
                view.showLink(choices, item, focus);
                setVisible(false);
                dispose();

            } else {

                setVisible(false);
                dispose();

            }

        }
    }


}