package dialog;

import admin.MainFrame;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import data.AppletDataHandler1;
import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class GroupLegendDialog extends JDialog {

    // private static final String message = "Select edge group:";


    private JButton cancelJButton;
    private JButton hideJButton;
    private JButton showJButton;
    private JCheckBox[] checkButton;
    private ArrayList<String> groupColor;
    private String[] keyArray;
    private Set<String> choices;
    private GraphView gv;
    private String groupBy;
    private  JPanel boxJPanel;

    public GroupLegendDialog(MainFrame _frame, GraphView gv,String groupBy) {
        super(JOptionPane.getFrameForComponent(_frame), "", false);

        this.gv = gv;
        this.groupBy = groupBy;
        initDialog();
        choices = new HashSet<String>();

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

    private JScrollPane createLegendPane() {

        ItemActionListener itemActionListener = new ItemActionListener();
        boxJPanel = new JPanel(new GridLayout(0, 1));

        boxJPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boxJPanel.setBackground(Color.white);

        groupColor = gv.getGroupColor();

        keyArray = groupColor.toArray(new String[groupColor.size()]);

        JLabel labels[] = new JLabel[keyArray.length];

        checkButton = new JCheckBox[keyArray.length];

        AppletDataHandler1.Group[] items = gv.getGroups();

        for (int j = 0; j < keyArray.length; j++) {

            //Create the check boxes.
            checkButton[j] = new JCheckBox();
            checkButton[j].setName(keyArray[j]);


            checkButton[j].addItemListener(itemActionListener);

           JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            labelPanel.setBackground(Color.white);

            labels[j] = new JLabel(keyArray[j]);
            labels[j].setFont(new Font("SansSerif", Font.PLAIN, 12));



            for (int m = 0; m < items.length; m++) {
                if (items[m].key.equalsIgnoreCase(keyArray[j])) {

                    Color cl = Color.decode(items[m].color);
              
                    checkButton[j].setBackground(cl);
                    labelPanel.add(checkButton[j]);
                }
            }

           labelPanel.setBackground(Color.white);

          //  labelPanel.add(colors[j]);
            labelPanel.add(labels[j]);

            boxJPanel.add(labelPanel);

        }

        return new JScrollPane(boxJPanel);

    }


    private void initDialog() {

        getContentPane().setLayout(new BorderLayout());
        String titleString = "<html>Node grouped by <font color=\"blue\" >" + groupBy + ":</font></html>";
        getContentPane().add(new JLabel(titleString), BorderLayout.NORTH);

        getContentPane().add(createLegendPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);
        registerListeners();
        pack();
        if (keyArray.length > 10)
            setSize(350, 600);
    }


    // Listeners
    private class ItemActionListener implements ItemListener {

        /**
         * Listens to the check boxes.
         */
        public void itemStateChanged(ItemEvent e) {


            String group = "";
            Object selected = e.getItemSelectable();


            for (int i = 0; i < groupColor.size(); i++) {

                group = checkButton[i].getName();

                if (selected == checkButton[i]) {

                    if (e.getStateChange() == ItemEvent.SELECTED) {

                        choices.add(group);

                    } else {

                        choices.remove(group);

                    }
                }
            }

        }


    }
    public JPanel getGroupLegend(){
        return boxJPanel;
    }
    private void registerListeners() {
        ButtonsActionListener buttonsActionListener = new ButtonsActionListener();

        hideJButton.addActionListener(buttonsActionListener);
        showJButton.addActionListener(buttonsActionListener);
        cancelJButton.addActionListener(buttonsActionListener);
    }

    // Listeners
    private class ButtonsActionListener implements ActionListener {


        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(hideJButton)) {
                 setVisible(false);
                dispose();
                gv.hideNodeGroup(choices);
               

            } else if (e.getSource().equals(showJButton)) {
                setVisible(false);
                dispose();
                gv.showNodeGroup(choices);

            } else {

                setVisible(false);
                dispose();

            }
        }
    }
}
