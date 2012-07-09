package dialog;

import admin.MainFrame;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import data.AppletDataHandler1;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class NodeInfoDialog extends JFrame implements ActionListener, TreeSelectionListener {

    private MainFrame frame;
    private JButton okJButton;
    private JPanel pathPane;
    private JTree tree;


    public NodeInfoDialog(NodeItem nodeItem, MainFrame _frame, AppletDataHandler1 dh1) {
        frame = _frame;
        Map<String, List<String>> nodeAttri = null;
        if(frame.getNodeAttri() !=null)
          nodeAttri =  frame.getNodeAttri();
        else{
       dh1.getNodeAttri();
        nodeAttri =  dh1.getNodeAttri();
        }

       initDialog(nodeItem, nodeAttri);
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


    private JScrollPane createTextPane(NodeItem nodeitem, Map<String, List<String>> nodeAttri) {
        pathPane = new JPanel(new GridLayout(1, 0));

        //Create the nodes.
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode("");
        createNodes(top, nodeitem, nodeAttri);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.putClientProperty("JTree.lineStyle", "None");

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        tree.setCellRenderer(renderer);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        pathPane.add(tree);

        JScrollPane scrollPane = new JScrollPane(pathPane);
        scrollPane.setOpaque(true);

        return scrollPane;
    }


    private void createNodes
            (DefaultMutableTreeNode top, NodeItem nodeitem, Map<String, List<String>> nodeAttri) {

        DefaultMutableTreeNode infoCat = null;
        DefaultMutableTreeNode relType_out = null;
        DefaultMutableTreeNode outType = null;
        DefaultMutableTreeNode inType = null;

        DefaultMutableTreeNode relType_in = null;
        DefaultMutableTreeNode infotree = null;

        infoCat = new DefaultMutableTreeNode("<html><b><font color=blue>Node info:</font></b></html>");

        top.add(infoCat);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // System.out.println("**** nodeAttri: " + nodeAttri);


        List<String> nodeinfos = nodeAttri.get(nodeitem.getString("login"));
        for (String info : nodeinfos) {

            int index = info.indexOf("||");
            infotree = new DefaultMutableTreeNode("<html><b>" + info.substring(0, index) + ":</b> " + info.substring(index + 2) + "</html>");

            infoCat.add(infotree);
        }

        if(nodeitem.outEdges().hasNext()) {
        relType_out = new DefaultMutableTreeNode("<html><b><font color=blue>Relations(outgoing):</font></b></html>");
                top.add(relType_out);
        }
         if(nodeitem.inEdges().hasNext()) {
                relType_in = new DefaultMutableTreeNode("<html><b><font color=blue>Relations(incoming):</font></b></html>");
                top.add(relType_in);
         }

        Set<String> outTypes = new HashSet<String>();
        for (Iterator iter = nodeitem.outEdges(); iter.hasNext();) {

            EdgeItem edge = (EdgeItem) iter.next();
            String type = edge.getString("edgeType");
            outTypes.add(type);

        }
        for (String type : outTypes) {
             outType =  new DefaultMutableTreeNode("<html><b><font color=blue>" + type + "</font></b></html>");

             relType_out.add(outType);

            String nodeinfoStr = "<HTML><table width=100% border=0 cellpadding=2><tr><td width=70%><b>Node</b></td><td width=20%><b>Weight</b></td></tr>";

            int i = 1;
            for (Iterator iter = nodeitem.outEdges(); iter.hasNext();) {

                EdgeItem edge = (EdgeItem) iter.next();
                String t = edge.getString("edgeType");

                if(t.equals(type)) {
                    String label = edge.getTargetItem().getString("label");
                    String weight = edge.getString("weightString");
                    String rowStr = "";
                    if (i % 2 == 0)  // even number
                    rowStr = "<tr style=\"background-color:yellow\"><td width=300>" + label + "</td><td width=50>" + weight + "</td></tr>";
                    else
                     rowStr = "<tr><td width=300>" + label + "</td><td width=50>" + weight + "</td></tr>";
                  nodeinfoStr += rowStr;
                    i++;
                }
            }
            nodeinfoStr += "</table></HTML>";

            DefaultMutableTreeNode nodeinfo = new DefaultMutableTreeNode(nodeinfoStr);
          outType.add(nodeinfo);

        }

        Set<String> inTypes = new HashSet<String>();
        for (Iterator iter = nodeitem.inEdges(); iter.hasNext();) {

            EdgeItem edge = (EdgeItem) iter.next();
            String type = edge.getString("edgeType");
            inTypes.add(type);

        }

        for (String type : inTypes) {
             inType =  new DefaultMutableTreeNode("<html><b><font color=blue>" + type + "</font></b></html>");

             relType_in.add(inType);

             String nodeinfoStr1 = "<HTML><table width=100% border=0 cellpadding=2><tr><td width=70%><b>Node</b></td><td width=20%><b>Weight</b></td></tr>";

            int j = 1;

            for (Iterator iter = nodeitem.inEdges(); iter.hasNext();) {

                EdgeItem edge = (EdgeItem) iter.next();
                String t = edge.getString("edgeType");
                if(t.equals(type)) {
                   String label = edge.getSourceItem().getString("label");
                    String weight = edge.getString("weightString");
                    String rowStr = "";
                    if (j % 2 == 0)  // even number
                    rowStr = "<tr style=\"background-color:yellow\"><td width=300>" + label + "</td><td width=50>" + weight + "</td></tr>";
                    else
                     rowStr = "<tr><td width=300>" + label + "</td><td width=50>" + weight + "</td></tr>";
                  nodeinfoStr1 += rowStr;
                    j++;
                }
            }
            nodeinfoStr1 += "</table></HTML>";

            DefaultMutableTreeNode nodeinfo = new DefaultMutableTreeNode(nodeinfoStr1);
          inType.add(nodeinfo);
        }
    }

    public void valueChanged
            (TreeSelectionEvent
                    e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (node == null) return;

    }

    protected static ImageIcon createImageIcon
            (String
                    path) {
        java.net.URL imgURL = WhyDialog.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    private void initDialog
            (NodeItem nodeitem, Map<String, List<String>> nodeAttri) {

         this.setTitle("Information for: " + nodeitem.getString("label"));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createTextPane(nodeitem, nodeAttri), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        pack();
        setSize(500, 350);
    }

    /**
     * Close the dialog on a button event
     */
    public void actionPerformed
            (ActionEvent
                    e) {
        JButton button = (JButton) e.getSource();
        if (button == okJButton) {
            dispose();
        }
    }

}