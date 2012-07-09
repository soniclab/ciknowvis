package dialog;

import admin.MainFrame;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;
import prefuse.Visualization;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class EdgeDialog extends JDialog {

    private MainFrame frame;
    private JButton hideJButton;
    private JButton showJButton;
    private JButton cancelJButton;

    private JCheckBox[] checkButton;
    private Set<String> choices;
    private Map<String, String> type_Label;
    private String[] keyArray;

    public EdgeDialog(MainFrame _frame) {
        super(JOptionPane.getFrameForComponent( _frame), "", false);
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

     private String getLinkLabel(String s, String realLabel) {
        String jaString = "\u25ac" + "\u25ba";
		Color c = Color.decode(s);
		
          String hexStr = "#" + Integer.toHexString(c.getRGB()).substring(2);

        String linkshape = "<html><FONT COLOR=\"" + hexStr + "\">" + jaString + "</FONT>" +" " +realLabel + "</html>";

        return linkshape;

    }
    private JScrollPane createCheckBoxPane() {



        ItemActionListener itemActionListener = new ItemActionListener();

        JPanel boxJPanel = new JPanel(new GridLayout(0, 1));

        boxJPanel.add(new JLabel(""));

        Visualization vis = frame.getGP1().getCurrentVis();
        TupleSet allTuple = vis.getVisualGroup("graph.edges");
        type_Label = new HashMap<String, String>();
        Iterator allIems = allTuple.tuples();

        while (allIems.hasNext()) {

            VisualItem item = (VisualItem) allIems.next();

                String type = item.getString("edgeType");
                String label = item.getString("edgeTypeDis");
                String colorLabel = getLinkLabel(item.getString("edgeColor"), label.trim());

               type_Label.put(colorLabel, type);
          
        }


         keyArray = type_Label.keySet().toArray(new String[type_Label.size()]);

        Arrays.sort(keyArray);
        checkButton = new JCheckBox[keyArray.length];
        for (int j = 0; j < keyArray.length; j++) {

            //Create the check boxes.
            checkButton[j] = new JCheckBox(keyArray[j]);

            checkButton[j].addItemListener(itemActionListener);

            boxJPanel.add(checkButton[j]);

           
        }


        boxJPanel.add(new JLabel(""));

        return new JScrollPane(boxJPanel);
    }


    private void initDialog
            () {

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JLabel("Edge types: "), BorderLayout.NORTH);
        getContentPane().add(createCheckBoxPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        registerListeners();
       
        pack();
      if(keyArray.length > 20)
      setSize(350, 600);
    }

    // Listeners
    private class ItemActionListener implements ItemListener {

        /**
         * Listens to the check boxes.
         */
        public void itemStateChanged(ItemEvent e) {


            String label;
            Object selected = e.getItemSelectable();


            for (int i = 0; i < type_Label.size(); i++) {

                label = checkButton[i].getText();

                if (selected == checkButton[i]) {

                    if (e.getStateChange() == ItemEvent.SELECTED) {

                        choices.add(type_Label.get(label));

                    } else {

                        choices.remove(type_Label.get(label));

                    }
                }
            }

        }


    }


    private void registerListeners
            () {
        ButtonsActionListener buttonsActionListener = new ButtonsActionListener();

        hideJButton.addActionListener(buttonsActionListener);
        showJButton.addActionListener(buttonsActionListener);
        cancelJButton.addActionListener(buttonsActionListener);
    }

    // Listeners
    private class ButtonsActionListener implements ActionListener {


        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(hideJButton)) {
                frame.getGP1().hideEdgeType(choices);
                setVisible(false);
                dispose();

            } else if (e.getSource().equals(showJButton)) {
                frame.getGP1().showEdgeType(choices);
                setVisible(false);
                dispose();

            } else {

                setVisible(false);
                dispose();

            }

        }
    }


}