package dialog;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.util.ColorLib;

import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Feb 8, 2010
 * Time: 2:35:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class HiddenEdgePopup extends JPopupMenu {

    private EdgeItem edge;
    private List<EdgeItem> es;
    private boolean showWeight;
    private JCheckBox[] checkButton;
    private JButton hideJButton;
   // private JButton showJButton;
    private JButton cancelJButton;
    private GraphView gv;
    private Set<EdgeItem> choices;
    public   HiddenEdgePopup(List<EdgeItem> es, boolean showWeight, EdgeItem edge, GraphView gv ){
        this.setBackground(Color.white);
        this.setLayout(new GridLayout(0, 1));
        this.edge = edge;
        this.showWeight = showWeight;
       this.es = es;
        this.gv = gv;
        choices = new HashSet<EdgeItem>();

         NodeItem from = edge.getSourceItem();
           NodeItem to = edge.getTargetItem();
           int size = es.size();

           int fromColor = from.getEndFillColor();
           int toColor = to.getEndFillColor();

           String fromLabel = from.getString("disLabel");
           String toLabel = to.getString("disLabel");

           JLabel fromlabel = new JLabel(fromLabel, JLabel.CENTER);

           fromlabel.setHorizontalTextPosition(JLabel.CENTER);
           fromlabel.setOpaque(true);
           fromlabel.setBackground(ColorLib.getColor(fromColor));
           JLabel blank = new JLabel(Integer.toString(size) + " edges between  ", JLabel.CENTER);

           //  blank.setForeground(Color.red);
           blank.setHorizontalTextPosition(JLabel.CENTER);
           JLabel tolabel = new JLabel(toLabel, JLabel.CENTER);

           tolabel.setHorizontalTextPosition(JLabel.CENTER);
           tolabel.setOpaque(true);
           tolabel.setBackground(ColorLib.getColor(toColor));
           JLabel and = new JLabel("  and  ");
           JPanel titlePanel = new JPanel();

           titlePanel.setBackground(Color.white);
           titlePanel.add(blank);
           titlePanel.add(fromlabel);

           titlePanel.add(and);
           titlePanel.add(tolabel);
           this.add(titlePanel);

           ItemActionListener itemActionListener = new ItemActionListener(es);
             checkButton = new JCheckBox[es.size()];
           int j = 0;
           for(EdgeItem e: es){

               String toolString = "<html><body>";

               String colorStr = e.getString("edgeColor");

               String edgeType = e.getString("edgeTypeDis");

               Color col = Color.decode(e.getString("edgeColor"));
               String jaString = "";
               String weight = e.getString("weightString");

               if (e.getString("direction").equalsIgnoreCase("2")) {
                   if (showWeight)
                       jaString = "\u25c4" + "\u25ac" + "\u25ba" + "  " + weight;
                   else
                       jaString = "\u25c4" + "\u25ac" + "\u25ba";
               } else if (e.getString("direction").equalsIgnoreCase("0")) {
                   if (showWeight)
                       jaString = "\u25ac" + "\u25ac" + "  " + weight;
                   else
                       jaString = "\u25ac" + "\u25ac";

               } else if (e.getString("direction").equalsIgnoreCase("1")) {


                   if (e.getSourceItem() != from) {
                       if (showWeight)
                           jaString = "\u25c4" + "\u25ac" + "  " + weight;
                       else
                           jaString = "\u25c4" + "\u25ac";
                   } else {
                       if (showWeight)
                           jaString = "\u25ac" + "\u25ba" + "  " + weight;
                       else
                           jaString = "\u25ac" + "\u25ba";
                   }
               }
               String hexStr = "#" + Integer.toHexString(col.getRGB()).substring(2);

               toolString = toolString + "<FONT COLOR=\"" + hexStr + "\">" + jaString + "</FONT>" + "    " + edgeType + "<br>";

                //Create the check boxes.
               checkButton[j] = new JCheckBox(toolString);
               checkButton[j].setBackground(Color.white);
               checkButton[j].addItemListener(itemActionListener);
                this.add(checkButton[j]);
              j++;
           }
         this.add(createButtons());
        

    }

     private JPanel createButtons() {
        JPanel gridJPanel;
        JPanel flowJPanel;
        ButtonsActionListener  baction = new ButtonsActionListener();

        hideJButton = new JButton("Hide");
        hideJButton.addActionListener(baction);
        hideJButton.setBackground(Color.white);

      //  showJButton = new JButton("Show");
      //  showJButton.addActionListener(baction);
      //  showJButton.setBackground(Color.white);

         cancelJButton = new JButton("Cancel");
         cancelJButton.addActionListener(baction);
         cancelJButton.setBackground(Color.white);
         
        gridJPanel = new JPanel(new GridLayout(1, 2));
         gridJPanel.setBackground(Color.white); 
        gridJPanel.add(hideJButton);
      //  gridJPanel.add(showJButton);
        gridJPanel.add(cancelJButton);
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowJPanel.setBackground(Color.white);
        flowJPanel.add(gridJPanel);

        return flowJPanel;
    }

       // Listeners
          private class ItemActionListener implements ItemListener {


              public ItemActionListener(List<EdgeItem> _es){
                 es = _es;
              }
              public void itemStateChanged(ItemEvent e) {


                  String label;
                  Object selected = e.getItemSelectable();

                   int i = 0;
                   for(EdgeItem ei: es){

                      label = checkButton[i].getText();

                      if (selected == checkButton[i]) {

                          if (e.getStateChange() == ItemEvent.SELECTED) {

                              choices.add(ei);

                          } else {

                              choices.remove(ei);

                          }
                      }
                       i++;
                  }

              }


          }

     private class ButtonsActionListener implements ActionListener {


        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(hideJButton)) {
               gv.hideSelectedEdges(choices);
                setVisible(false);


           /* } else if (e.getSource().equals(showJButton)) {
               gv.showEdgeType(choices, false);
                setVisible(false);

             */
            } else {

                setVisible(false);
           
            }

        } 
    }
     
}
