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


public class NodeDialog extends JDialog {

    // private static final String message = "Select edge type:";

    private MainFrame frame;
    private JButton hideJButton;
    private JButton showJButton;
    private JButton cancelJButton;

    private JCheckBox[] checkButton;
    private JCheckBox[] checkButton1;

    private Set<String> choices;

    private boolean all = false;
    private Set<String> types;
    private String[] keyArray;
    private Map<String, Set<String>> typeAttri;
    private Map<String, Set<String>> attriChoices;

    public NodeDialog(MainFrame _frame) {

        super(JOptionPane.getFrameForComponent(_frame), "", false);

        frame = _frame;

        choices = new HashSet<String>();
        attriChoices = new HashMap<String, Set<String>>();

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


     private String getNodeLabel(String s, String realLabel) {
        String jaString = "\u25ac" + "\u25ba";
		Color c = Color.decode(s);

          String hexStr = "#" + Integer.toHexString(c.getRGB()).substring(2);

        String linkshape = "<html><FONT COLOR=\"" + hexStr + "\">" + jaString + "</FONT>" +" " +realLabel + "</html>";

        return linkshape;

    }

    private JScrollPane createCheckBoxPane() {
        ItemActionListener itemActionListener = new ItemActionListener();

        JPanel boxJPanel = new JPanel();
        boxJPanel.setLayout(new BoxLayout(boxJPanel, BoxLayout.Y_AXIS));

        boxJPanel.add(new JLabel(""));

        Visualization vis = frame.getGP1().getCurrentVis();
        TupleSet allTuple = vis.getVisualGroup("graph.nodes");
        types = new HashSet();
        typeAttri = new HashMap<String, Set<String>>();
        Iterator allIems = allTuple.tuples();

        while (allIems.hasNext()) {

            VisualItem item = (VisualItem) allIems.next();
         //   if (item.isVisible() == true) {
                String type = item.getString("nodeType");

                types.add(type.trim());
           // }
        }

        int totalAttri = 0;
        for (String type : types) {

            Iterator allIems1 = allTuple.tuples();
            Set<String> attriSet = new HashSet<String>();
            String type1 = "";

            while (allIems1.hasNext()) {

                VisualItem item = (VisualItem) allIems1.next();

                    type1 = item.getString("nodeType");
                    if (type.equals(type1)) {


                        try {
                            String colorAttr = item.getString("colorAttri");

                            attriSet.add(colorAttr);

                        } catch (Exception e) {

                        }

                    }

            }
            if(attriSet.size() > 0){

            typeAttri.put(type, attriSet);
            totalAttri += attriSet.size();
            }
        }


        keyArray = types.toArray(new String[types.size()]);


        Arrays.sort(keyArray);
        checkButton = new JCheckBox[keyArray.length];
        checkButton1 = new JCheckBox[totalAttri];

        int attriLength = 0;
        for (int j = 0; j < keyArray.length; j++) {

            //check box for nodetype
            checkButton[j] = new JCheckBox(keyArray[j]);

            checkButton[j].addItemListener(itemActionListener);

            boxJPanel.add(checkButton[j]);

            //check box for node attribute

            if( typeAttri.size() > 0){
            Set<String> attris = typeAttri.get(keyArray[j]);

            String[] attrisArray = attris.toArray(new String[attris.size()]);
            Arrays.sort(attrisArray);

            JPanel boxJPanel1 = new JPanel();

            boxJPanel1.setLayout(new BoxLayout(boxJPanel1, BoxLayout.Y_AXIS));

            boxJPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 1,
                    1, 1, Color.blue), "Attribute(s):", TitledBorder.LEFT, TitledBorder.TOP));

             Set<String> seleAttri = new HashSet<String>();
             ItemActionListener1 itemActionListener1 = new ItemActionListener1(j, seleAttri);


            for (int m = 0; m < attrisArray.length; m++) {
                int n = m + attriLength;

                String attriString = attrisArray[m];
                if (attriString.length() < 15)
                    attriString += "              ";
                checkButton1[n] = new JCheckBox(attriString);
                checkButton1[n].addItemListener(itemActionListener1);

                boxJPanel1.add(checkButton1[m + attriLength]);

            }

            attriLength += attrisArray.length;
            boxJPanel.add(boxJPanel1);

            }
        }

        boxJPanel.add(new JLabel(""));
        return new JScrollPane(boxJPanel);

    }


    private void initDialog() {

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JLabel("Node types: "), BorderLayout.NORTH);
        getContentPane().add(createCheckBoxPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        registerListeners();
        pack();
        int listSize = keyArray.length + typeAttri.size();
        if (listSize > 20)
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

            for (int i = 0; i < types.size(); i++) {
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


    // Listeners
      private class ItemActionListener1 implements ItemListener {

          /**
           * Listens to the check boxes.
           *
           *
           */
          private int  forwhichType;
          Set<String> seleAttribute;
          private   ItemActionListener1(int forwhich, Set<String> seleAttribute){
              forwhichType = forwhich;
              this.seleAttribute = seleAttribute;
          }
          public void itemStateChanged(ItemEvent e) {


              String type = "";
              Object selected = e.getItemSelectable();

              int attriLength = 0;

              for (int i = 0; i < keyArray.length; i++) {

                  Set<String> attris = typeAttri.get(keyArray[i]);

                  String type1 = "";
                  int j = 0;
                  for (String attri : attris) {

                      int n = j + attriLength;
                      type1 = checkButton1[n].getText();

                      if (selected == checkButton1[n]) {

                          if (e.getStateChange() == ItemEvent.SELECTED) {

                              seleAttribute.add(type1.trim());
                          } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                              seleAttribute.remove(type1.trim());
                          }

                      }//if
                      j++;
                  }//for


                  if (seleAttribute.size() > 0 && i == forwhichType ) {

                      attriChoices.put(keyArray[forwhichType], seleAttribute);

                  }

                  attriLength += attris.size();
              }//for


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
                view.hideNodeType(choices, attriChoices);
                setVisible(false);
                dispose();

            } else if (e.getSource().equals(showJButton)) {
                //  System.out.println("show attriChoices: " + attriChoices);
                view.showNodeType(choices, attriChoices);
                setVisible(false);
                dispose();

            } else {

                setVisible(false);
                dispose();

            }

        }
    }


}