package dialog;

import admin.MainFrame;
import admin.FileReader;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class NodePropertyMap extends JFrame implements ActionListener, ItemListener {

    private MainFrame frame;
    private JButton okJButton;
    private FileReader filereader;
    private JComboBox labelBox, typeBox, colorBox, shapeBox, groupBox, size1Box, size2Box, typeBox1, weightBox;
    private Map<String, String> propertyMap;
    private List<String> sizeList = null;
      private Map<String, String> lastPropertymap;
    public NodePropertyMap(Map<String, String> lastPropertymap, List<String> nodeProperties, Map<String, String> nodePropertymap, List<String> edgeProperties, Map<String, String> edgePropertymap, FileReader filereader) {
        //   super(JOptionPane.getFrameForComponent(_frame), "", false);
        
        this.filereader = filereader;
        propertyMap = new HashMap<String, String>();
        this.lastPropertymap = lastPropertymap;

        initDialog(nodeProperties, nodePropertymap, edgeProperties, edgePropertymap);
    }


    private JPanel createButtons() {

        JPanel flowJPanel;
        okJButton = new JButton("Ok");
        okJButton.addActionListener(this);
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowJPanel.setBackground(Color.white);
        flowJPanel.add(okJButton);

        return flowJPanel;
    }


    private JScrollPane createTextPane(List<String> nodeProperties, Map<String, String> nodePropertymap, List<String> edgeProperties, Map<String, String> edgePropertymap) {


        JPanel pathPane = new JPanel(new GridLayout(12, 1));
        pathPane.setBackground(Color.white);
        //  pathPane.setBorder(BorderFactory.createTitledBorder("node propeties Setting:"));
        pathPane.add(new JLabel("<html><b><font color=blue>Node propeties Setting:</font></b></html>"));
        pathPane.add(new JLabel(""));

         List<String> labelList = new ArrayList<String>();
        List<String> typeList = new ArrayList<String>();
        sizeList = new ArrayList<String>();
         sizeList.add("none");
        for (String nodeProp : nodeProperties) {

            if (nodePropertymap.get(nodeProp).endsWith("String")) {
                   labelList.add(nodeProp);
                typeList.add(nodeProp);
            }

            if (!nodePropertymap.get(nodeProp).endsWith("String") && !nodePropertymap.get(nodeProp).endsWith("boolean")) {

                sizeList.add(nodeProp);
            }

        }

        typeList.add("unknown");
        String[] typeArray = typeList.toArray(new String[typeList.size()]);

        labelBox = new JComboBox(labelList.toArray(new String[labelList.size()]));
        labelBox.addItemListener(this);
        if(lastPropertymap != null)
          labelBox.setSelectedItem(lastPropertymap.get("label"));
         else{
        if (nodeProperties.contains("label"))
            labelBox.setSelectedItem("label");
        }

        typeBox = new JComboBox(typeArray);
        typeBox.addItemListener(this);
        if(lastPropertymap != null)
          typeBox.setSelectedItem(lastPropertymap.get("nodeType"));
         else{
        if (nodeProperties.contains("type"))
            typeBox.setSelectedItem("type");
        else
            typeBox.setSelectedItem(typeArray[typeList.size() - 1]);
        }
        colorBox = new JComboBox(typeArray);
        colorBox.addItemListener(this);
         if(lastPropertymap != null)
          colorBox.setSelectedItem(lastPropertymap.get("color"));
        else{
             if (nodeProperties.contains("type"))
            colorBox.setSelectedItem("type");
        else
            colorBox.setSelectedItem(typeArray[typeList.size() - 1]);
         }

        shapeBox = new JComboBox(typeArray);
        shapeBox.addItemListener(this);

          if(lastPropertymap != null)
          shapeBox.setSelectedItem(lastPropertymap.get("shape"));
        else{
              if (nodeProperties.contains("type"))
            shapeBox.setSelectedItem("type");
        else
            shapeBox.setSelectedItem(typeArray[typeList.size() - 1]);
          }

        groupBox = new JComboBox(typeArray);

        groupBox.addItemListener(this);

          if(lastPropertymap != null)
          groupBox.setSelectedItem(lastPropertymap.get("group"));
        else{
              if (nodeProperties.contains("type"))
        groupBox.setSelectedItem("type");
        else
            groupBox.setSelectedItem(typeArray[typeList.size() - 1]);
          }



        if (sizeList.size() > 1) {

            String[] sizeArray = sizeList.toArray(new String[sizeList.size()]);

            size1Box = new JComboBox(sizeArray);
            size1Box.addItemListener(this);
          //  size1Box.setSelectedItem(sizeArray[sizeList.size() - 1]);
            if(lastPropertymap != null) {
             if(lastPropertymap.get("size1") != null )
         size1Box.setSelectedItem(lastPropertymap.get("size1"));
            }

                size2Box = new JComboBox(sizeArray);
           if(lastPropertymap != null) {
             if(lastPropertymap.get("size1") != null )

                size2Box.setEnabled(true);
           }else
                size2Box.setEnabled(false);
              //   size2Box.setSelectedItem(sizeArray[sizeList.size() - 1]);
                // if size1Box is selected != none, size2 is enable
                size2Box.addItemListener(this);
              if(lastPropertymap != null)
              {
             if(lastPropertymap.get("size2") != null )
         size2Box.setSelectedItem(lastPropertymap.get("size2"));
              }
        }

        pathPane.add(new JLabel("Node label:"));
        pathPane.add(labelBox);
        pathPane.add(new JLabel("Node type:"));
        pathPane.add(typeBox);
        pathPane.add(new JLabel("Node colors by:"));
        pathPane.add(colorBox);
        pathPane.add(new JLabel("Node shapes by:"));
        pathPane.add(shapeBox);
        pathPane.add(new JLabel("Node groups by:"));
        pathPane.add(groupBox);


        JPanel pathPane1 = new JPanel(new GridLayout(12, 1));

        pathPane1.setBackground(Color.white);

        //       String[] propertyArray1 = edgeProperties.toArray(new String[edgeProperties.size()]);

        List<String> etypeList = new ArrayList<String>();
        List<String> weightList = new ArrayList<String>();
        etypeList.add("unknown");
        weightList.add("unknown");
        for (String edgeProp : edgeProperties) {

            if (edgePropertymap.get(edgeProp).endsWith("String")) {

                etypeList.add(edgeProp);
            }

            if (!edgePropertymap.get(edgeProp).endsWith("String") && !edgePropertymap.get(edgeProp).endsWith("boolean")) {

                weightList.add(edgeProp);
            }
        }
        weightBox = new JComboBox(weightList.toArray(new String[weightList.size()]));
        weightBox.addItemListener(this);

          if(lastPropertymap != null)
         weightBox.setSelectedItem(lastPropertymap.get("weight"));
    else{
        if (edgeProperties.contains("weight"))
            weightBox.setSelectedItem("weight");
          }

        typeBox1 = new JComboBox(etypeList.toArray(new String[etypeList.size()]));
        typeBox1.addItemListener(this);

          if(lastPropertymap != null)
         typeBox1.setSelectedItem(lastPropertymap.get("edgeType"));
    else{
        if (edgeProperties.contains("type"))
            typeBox1.setSelectedItem("type");
          }
        if (sizeList.size() > 1)  {

                pathPane1.add(new JLabel("Node size1 by:"));
                pathPane1.add(size1Box);
                pathPane1.add(new JLabel("Node size2 by:"));
                pathPane1.add(size2Box);

                pathPane1.add(new JLabel("------------------------------------------------------------"));

                pathPane1.add(new JLabel("<html><b><font color=blue>Edge propeties setting:</font></b></html>"));
                pathPane1.add(new JLabel(""));
                pathPane1.add(new JLabel("Edge type:"));
                pathPane1.add(typeBox1);
                pathPane1.add(new JLabel("Edge weight:"));
                pathPane1.add(weightBox);
                pathPane1.add(new JLabel(""));

        } else {
            pathPane1.add(new JLabel(""));
            pathPane1.add(new JLabel(""));
            pathPane1.add(new JLabel(""));
            pathPane1.add(new JLabel("------------------------------------------------------------"));

            pathPane1.add(new JLabel("<html><b><font color=blue>Edge propeties setting:</font></b></html>"));
            pathPane1.add(new JLabel(""));
            pathPane1.add(new JLabel("Edge type:"));
            pathPane1.add(typeBox1);
            pathPane1.add(new JLabel("Edge weight:"));
            pathPane1.add(weightBox);
            pathPane1.add(new JLabel(""));
            pathPane1.add(new JLabel(""));

        }

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));   //////
        panel.setBackground(Color.white);
        panel.add(pathPane);
        panel.add(pathPane1);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setOpaque(true);

        return scrollPane;
    }


    private void initDialog
            (List<String> nodeProperties, Map<String, String> nodePropertymap, List<String> edgeProperties, Map<String, String> edgePropertymap) {

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createTextPane(nodeProperties, nodePropertymap, edgeProperties, edgePropertymap), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        pack();
        //  setSize(500, 500);
    }


    public void itemStateChanged(ItemEvent event) {

        if (event.getSource() == labelBox
                && event.getStateChange() == ItemEvent.SELECTED)
            propertyMap.put("label", labelBox.getSelectedItem().toString());

        if (event.getSource() == typeBox
                && event.getStateChange() == ItemEvent.SELECTED)
            propertyMap.put("nodeType", typeBox.getSelectedItem().toString());

        if (event.getSource() == colorBox
                && event.getStateChange() == ItemEvent.SELECTED)
            propertyMap.put("color", colorBox.getSelectedItem().toString());

        if (event.getSource() == shapeBox
                && event.getStateChange() == ItemEvent.SELECTED)
            propertyMap.put("shape", shapeBox.getSelectedItem().toString());

        if (event.getSource() == groupBox
                && event.getStateChange() == ItemEvent.SELECTED)
            propertyMap.put("group", groupBox.getSelectedItem().toString());


      //  if (sizeList.size() > 1) {
            if (event.getSource() == size1Box
                    && event.getStateChange() == ItemEvent.SELECTED) {

                if (size1Box.getSelectedItem().equals("none"))
                    size2Box.setEnabled(false);
               else
                     if(lastPropertymap == null)
                    size2Box.setEnabled(true);

                    propertyMap.put("size1", size1Box.getSelectedItem().toString());
               // }
            }
                if (event.getSource() == size2Box
                        && event.getStateChange() == ItemEvent.SELECTED) {
                  //  if (!size2Box.getSelectedItem().equals("none"))
                        propertyMap.put("size2", size2Box.getSelectedItem().toString());
                }
          //  }

            if (event.getSource() == typeBox1
                    && event.getStateChange() == ItemEvent.SELECTED) {
                propertyMap.put("edgeType", typeBox1.getSelectedItem().toString());
            }

            if (event.getSource() == weightBox
                    && event.getStateChange() == ItemEvent.SELECTED) {
                propertyMap.put("weight", weightBox.getSelectedItem().toString());

            }


        }

        /**
         * Close the dialog on a button event
         */
        public void actionPerformed
        (ActionEvent
        e){
        JButton button = (JButton) e.getSource();
        if (button == okJButton) {
              propertyMap.put("label", labelBox.getSelectedItem().toString());
               propertyMap.put("nodeType", typeBox.getSelectedItem().toString());
             propertyMap.put("color", colorBox.getSelectedItem().toString());
             propertyMap.put("shape", shapeBox.getSelectedItem().toString());
              propertyMap.put("group", groupBox.getSelectedItem().toString());
               propertyMap.put("edgeType", typeBox1.getSelectedItem().toString());
             propertyMap.put("weight", weightBox.getSelectedItem().toString());

           filereader.xml2Ciknow(propertyMap);
            dispose();
        }
    }

    }