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

import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.Visualization;
import data.AppletDataHandler1;
import visual.GraphView;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class WhyDialog extends JDialog implements ActionListener, TreeSelectionListener {

    private MainFrame frame;
    private JButton okJButton;

    private String user = "";
    private String target = "";

    private NodeItem userNode;
    private NodeItem targetNode;

    private int recStatus;
    private Visualization vis;

    private JPanel pathPane;
    private JTree tree;
    private Map<Integer, List<String>> ur_pathsMap, rt_pathsMap;
    private List<NodeItem> recList;
    // private boolean rt_folder, ur_folder;
    private Map<NodeItem, Set<String>> ur_itemsMap, rt_itemsMap;
    private Set<Integer> indirectlinks;
    private GraphView gv;

    // constructor for setting path for no-hidden edges
    public WhyDialog(GraphView gv, AppletDataHandler1 dh1, Visualization vis) {
        this.recStatus = 1;
        this.gv = gv;
        ur_pathsMap = new HashMap<Integer, List<String>>();
        rt_pathsMap = new HashMap<Integer, List<String>>();

        ur_itemsMap = new HashMap<NodeItem, Set<String>>();
        rt_itemsMap = new HashMap<NodeItem, Set<String>>();

        this.vis = vis;
        setRecKeyLabelMap(gv);


        for (NodeItem recItem : recList) {
            int recKey = recItem.getInt("nodeKey");
            List ur_paths = pathsList(1, recItem, userNode, false);
            List rt_paths = pathsList(2, recItem, targetNode, false);
            ur_pathsMap.put(recKey, ur_paths);
            rt_pathsMap.put(recKey, rt_paths);
        }

    }

    public Map<Integer, List<String>> getUr_pathsMap() {
        return ur_pathsMap;
    }

    public Map<Integer, List<String>> getRt_pathsMap() {
        return rt_pathsMap;
    }


    public WhyDialog(GraphView gv, MainFrame _frame, AppletDataHandler1 dh1, int recStatus) {
        super(JOptionPane.getFrameForComponent(_frame), "", false);
        this.gv = gv;
        frame = _frame;
        this.recStatus = recStatus;
        indirectlinks = new HashSet<Integer>();
        String[] recPara = dh1.getRecPara();
        if (recStatus == 2)
            target = recPara[recPara.length - 1];

        vis = frame.getGP1().getCurrentVis();

        if (recStatus == 1) {
            Iterator iter = vis.items("graph.nodes");

            while (iter.hasNext()) {
                NodeItem node = (NodeItem) iter.next();
                if (node.getString("nodeType").equals("Requester"))
                    userNode = node;
                if (node.getString("nodeType").equals("Target"))
                    targetNode = node;
            }
        }
        initDialog(dh1);
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

    private void setRecKeyLabelMap(GraphView gv) {
        // relocate the nodes for better layout
        Iterator iter = vis.items("graph.nodes");
        Set<Integer> recKeys = new HashSet<Integer>();
        recList = new ArrayList<NodeItem>();

        while (iter.hasNext()) {

            NodeItem nodeitem = (NodeItem) iter.next();
            int nodeKey = nodeitem.getInt("nodeKey");

            try {

                //  if (nodeitem.getString("nodeType").equals("Recommendation") || ((recStatus == 2) && (!nodeitem.getString("nodeType").equals("Requester")))) {
                if (nodeitem.getString("nodeType").equals("Recommendation")) {
                    if (recStatus == 1 || (gv.getHoveredNodes().size() == 0 && recStatus == 2))
                        recList.add(nodeitem);

                    recKeys.add(nodeKey);

                } else if (nodeitem.getString("nodeType").equals("Requester")) {
                    user = nodeitem.getString("label");

                    userNode = nodeitem;
                } else if (nodeitem.getString("nodeType").equals("Target")) {
                    if (recStatus == 1) {
                        target = nodeitem.getString("label");
                        targetNode = nodeitem;
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception when getting nodekey: " + e);
            }

        }

        if (gv.getHoveredNodes() != null && recStatus == 2) {
            recList.clear();
            recList.addAll(gv.getHoveredNodes());
        }


    }

    private JScrollPane createTextPane(AppletDataHandler1 dh1) {
        pathPane = new JPanel(new GridLayout(1, 0));

        // relocate the nodes for better layout

        ur_pathsMap = new HashMap<Integer, List<String>>();
        ur_itemsMap = new HashMap<NodeItem, Set<String>>();
        if (recStatus == 1) {
            rt_pathsMap = new HashMap<Integer, List<String>>();
            rt_itemsMap = new HashMap<NodeItem, Set<String>>();
        }
        setRecKeyLabelMap(gv);
        if (recStatus == 1) {
            for (NodeItem recItem : recList) {
                int recKey = recItem.getInt("nodeKey");
                List ur_paths = pathsList(1, recItem, userNode, false);
                List rt_paths = pathsList(2, recItem, targetNode, false);
                ur_pathsMap.put(recKey, ur_paths);
                rt_pathsMap.put(recKey, rt_paths);
            }
        }
        //Create the nodes.
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode("");
        createNodes(top, ur_itemsMap, rt_itemsMap, dh1);

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

        JPanel textJPanel = new JPanel();
        textJPanel.setLayout(new BoxLayout(textJPanel, BoxLayout.Y_AXIS));
        textJPanel.setBackground(Color.white);

        String rec = "recommendation";

        if (recList.size() > 1)
            rec = recList.size() + " recommendations";
        if (recStatus == 2 && recList.size() == 0) {
            if (ur_itemsMap.size() != 1)

                rec = ur_itemsMap.size() + " recommendations";
        }
        String was = " was";
        if (recList.size() > 1 || (recStatus == 2 && recList.size() == 0))
            was = " were";
        String content = null;
        if (recStatus == 1) {
            if (dh1.hiddenItemsExist()) {
                content = "<html>&nbsp;&nbsp;<b>Information about why the following <i><font color=blue>" + rec + "</font></i> " + was + " made to </b>"
                        + "<b> <i><font color=blue>" + user + "</font></i> <br>&nbsp&nbsp;in response to </b>"
                        + " <b>the query: <i><font color=blue>" + target + "</font></i></b><br><br>"
                        + "&nbsp;&nbsp;The Visualization shows the shortest path(s) between <b> <i><font color=blue>" + user + "</font></i></b>, the <b> <i><font color=blue>" + rec + "</font></i></b>" +
                        " and <b> <i><font color=blue>" + target + "</font></i></b>.<br>"
                        + "&nbsp;&nbsp;You can reveal additinal paths by clicking the 'Show/ Hide Paths' button at the far right of the control menu.<br><br>"
                        + "</html>";


            } else {
                content = "<html>&nbsp;&nbsp;<b>Information about why the following <i><font color=blue>" + rec + "</font></i> " + was + " made to </b>"
                        + "<b> <i><font color=blue>" + user + "</font></i> <br> &nbsp;&nbsp;in response to </b>"
                        + " <b>the query: <i><font color=blue>" + target + "</font></i></b><br><br>"
                        + "&nbsp;&nbsp;The Visualization shows the shortest path(s) between <b> <i><font color=blue>" + user + "</font></i></b>, the <b> <i><font color=blue>" + rec + "</font></i></b>" +
                        " and <b> <i><font color=blue>" + target + "</font></i></b>.<br>"
                        + "</html>";
            }
        } else {
            content = "<html>&nbsp;&nbsp;<b>Information about why the following <i><font color=blue>" + rec + "</font></i> " + was + " made to </b>"
                    + "<b> <i><font color=blue>" + user + "</font></i> <br>&nbsp;&nbsp;in response to </b>"
                    + " <b>the query: <i><font color=blue>" + target + "</font></i></b><br><br>"
                    + "&nbsp;&nbsp;The Visualization shows how the recommendations are connected to the query: <i><font color=blue>" + target + "</font></i><br>" +
                    "&nbsp;&nbsp;and to you <i><font color=blue>" + user + "</font></i>.<br><br>"
                    + "</html>";
        }
        Font plainFont = new Font("SansSerif", Font.PLAIN, 12);

        //  JPanel text = new JPanel(new FlowLayout(FlowLayout.LEFT) );

        //   text.setBorder(BorderFactory.createEmptyBorder());

        //   text.setBackground(Color.white);

        JLabel theText = new JLabel(content);
        //  text.add(theText);
        theText.setFont(plainFont);
        textJPanel.add(theText);
        textJPanel.add(pathPane);
        JScrollPane scrollPane = new JScrollPane(textJPanel);
        scrollPane.setOpaque(true);

        return scrollPane;
    }

    private String[] connecList(NodeItem nodeitem) {

        /* Set<String> path = new HashSet<String>();

    Iterator edges = nodeitem.edges();

    while (edges.hasNext()) {
        EdgeItem edge = (EdgeItem) edges.next();
        String edgestatus = edge.getString("invisibleItem");
        if(nodeitem.getString("invisibleItem").equalsIgnoreCase("0") && edgestatus.equalsIgnoreCase("0")){

        String edgeFormat = edge.getString("direction");
        Node fromNode = edge.getSourceNode();
        Node toNode = edge.getTargetNode();
        String from = "<i><font color=blue>" + fromNode.getString("label") + "</font></i>";
        String to = "<i><font color=blue>" + toNode.getString("label") + "</font></i>";
        String relation = " " + edge.getString("edgeTypeDis") + " ";
        String des = "";
        if (edgeFormat.equalsIgnoreCase("2"))
            des = from + relation + to + " ( Mutual relation )";
        else if (edgeFormat.equalsIgnoreCase("0"))
            des = from + relation + to + " ( undirected relation )";
        else
            des = from + relation + to;
        path.add("\u2726 " + des);
    }else{
           int recKey = nodeitem.getInt("nodeKey");
        List ur_paths = pathsList(1, nodeitem, userNode);
        jjj
        ur_pathsMap.put(recKey, ur_paths);
        }
    }
        */
        int recKey = nodeitem.getInt("nodeKey");


        List<String> ur_paths = pathsList(1, nodeitem, userNode, true);
        ur_pathsMap.put(recKey, ur_paths);
        String[] stArray = new String[ur_paths.size()];
        int i = 0;
        for (String st : ur_paths) {
            stArray[i] = st;
            i++;
        }
        Arrays.sort(stArray);

        return stArray;
    }

    ///////////////
    /*
      private void attriRecEdges(NodeItem invisibleNode, NodeItem recNode) {

          Set<String> urItems;
                 urItems = new HashSet<String>();

          Iterator edges = invisibleNode.edges();

            List<String> path = new ArrayList<String>();

            while (edges.hasNext()) {

                EdgeItem item = (EdgeItem) edges.next();

                if (item.getString("hiddenItem").equalsIgnoreCase("0")) {

                    String edgeFormat = item.getString("direction");
                    NodeItem fromNode = item.getSourceItem();
                    NodeItem toNode = item.getTargetItem();

                    String from = "<i><font color=blue>" + fromNode.getString("label") + "</font></i>";
                    String to = "<i><font color=blue>" + toNode.getString("label") + "</font></i>";
                    String relation = " " + item.getString("edgeTypeDis") + " ";
                    String des = "";


                    if (edgeFormat.equalsIgnoreCase("2"))
                        des = from + relation + to + " ( Mutual relation )";
                    else if (edgeFormat.equalsIgnoreCase("0"))
                        des = from + relation + to + " ( undirected relation )";
                    else
                        des = from + relation + to;


                    if (fromNode.equals(userNode) || toNode.equals(userNode)) {
                        path.add("\u2726 " + des);

                    } else {

                        Iterator edges1 = null;
                        boolean useFromNode = false;   // flag to get next step node

                        if (userNode.equals(fromNode)) {
                            edges1 = toNode.edges();
                            String toLabel = toNode.getString("label");

                                urItems.add(toLabel);


                        } else {
                            edges1 = fromNode.edges();
                            String fromLabel = fromNode.getString("label");

                                urItems.add(fromLabel);

                            useFromNode = true;
                        }

                        urItems.remove(user);

                        while (edges1.hasNext()) {

                            EdgeItem item1 = (EdgeItem) edges1.next();

                            if (item1.getString("hiddenItem").equalsIgnoreCase("0")) {

                                String edgeFormat1 = item1.getString("direction");
                                NodeItem fromNode1 = item1.getSourceItem();
                                NodeItem toNode1 = item1.getTargetItem();


                                String from1 = "<i><font color=blue>" + fromNode1.getString("label") + "</font></i>";
                                String to1 = "<i><font color=blue>" + toNode1.getString("label") + "</font></i>";
                                String relation1 = " " + item1.getString("edgeTypeDis") + " ";
                                String des1;


                                if (edgeFormat1.equalsIgnoreCase("2")) {

                                        des1 = des + "<br>and " + from1 + relation1 + to1 + " ( Mutual relation )";

                                } else if (edgeFormat1.equalsIgnoreCase("0")) {

                                        des1 = des + "<br>and " + from1 + relation1 + to1 + " ( undirected relation )";

                                } else {

                                        des1 = des + "<br>and " + from1 + relation1 + to1;

                                }

                                if (fromNode1.equals(recNode) || toNode1.equals(recNode)) {
                                    if((des1.indexOf(user) > -1)){

                                    }else
                                    path.add("\u2726 " + des1);
                                    // paths by two nodes
                                } else {

                                    // 3 steps path
                                } //else not reach "rec"
                            }// not hidden
                        }// while

                    } //else not reach "rec"
                }// not hidden
            }// while

                if (urItems.size() > 0)
                    ur_itemsMap.put(recNode, urItems);


            return path;

      }
     private List<String> attriRecPaths(NodeItem recNode) {
            Set<String> rtItems = new HashSet<String>();

         Set<NodeItem> indirectSet = new HashSet<NodeItem>();
                            Iterator invisibleNodes = recNode.neighbors();
                                        while (invisibleNodes.hasNext()) {
                                            NodeItem invisibleNode = (NodeItem) invisibleNodes.next();

                                                 indirectSet.add(invisibleNode);

                                            }
        }
    */
    private List<String> pathsList(int group, NodeItem recNode, NodeItem useOrTarNode, boolean forAttriRec) {
        Set<String> urItems1, rtItems1;
        urItems1 = new HashSet<String>();
        rtItems1 = new HashSet<String>();

        Set<String> urItems2 = new HashSet<String>();
        Set<String> rtItems2 = new HashSet<String>();

        Iterator edges = useOrTarNode.edges();

        List<String> path = new ArrayList<String>();
        boolean pathBy1Node = false;
        while (edges.hasNext()) {

            EdgeItem item = (EdgeItem) edges.next();

            if (item.getString("hiddenItem").equalsIgnoreCase("0")) {
                if ((!forAttriRec) || (forAttriRec && !(item.getString("invisibleItem").equalsIgnoreCase("2")))) {

                    String edgeFormat = item.getString("direction");
                    NodeItem fromNode = item.getSourceItem();
                    NodeItem toNode = item.getTargetItem();


                    String from = "<i><font color=blue>" + fromNode.getString("label") + "</font></i>";
                    String to = "<i><font color=blue>" + toNode.getString("label") + "</font></i>";
                    String relation = " " + item.getString("edgeTypeDis") + " ";
                    String des = "";


                    if (edgeFormat.equalsIgnoreCase("2"))
                        des = from + relation + to + " ( Mutual relation )";
                    else if (edgeFormat.equalsIgnoreCase("0"))
                        des = from + relation + to + " ( undirected relation )";
                    else
                        des = from + relation + to;

                    if (fromNode.equals(recNode) || toNode.equals(recNode)) {
                        path.add("\u2726 " + des);

                    } else {

                        Iterator edges1 = null;
                        boolean useFromNode = false;   // flag to get next step node

                        if (useOrTarNode.equals(fromNode)) {
                            edges1 = toNode.edges();
                            String toLabel = toNode.getString("label");
                            if (group == 1) {

                                urItems1.add(toLabel);
                            } else {
                                rtItems1.add(toLabel);
                            }

                        } else {
                            edges1 = fromNode.edges();
                            String fromLabel = fromNode.getString("label");
                            if (group == 1) {

                                urItems1.add(fromLabel);
                            } else {
                                rtItems1.add(fromLabel);
                            }
                            useFromNode = true;
                        }

                        urItems1.remove(user);
                        urItems1.remove(target);
                        rtItems1.remove(user);
                        rtItems1.remove(target);

                        while (edges1.hasNext()) {

                            EdgeItem item1 = (EdgeItem) edges1.next();

                            if (item1.getString("hiddenItem").equalsIgnoreCase("0")) {

                                String edgeFormat1 = item1.getString("direction");
                                NodeItem fromNode1 = item1.getSourceItem();
                                NodeItem toNode1 = item1.getTargetItem();

                                if ((group == 1 && (!fromNode1.getString("label").equals(user) && !toNode1.getString("label").equals(user))) ||
                                        (group == 2 && (!fromNode1.getString("label").equals(target) && !toNode1.getString("label").equals(target)))) {

                                    String from1 = "<i><font color=blue>" + fromNode1.getString("label") + "</font></i>";
                                    String to1 = "<i><font color=blue>" + toNode1.getString("label") + "</font></i>";
                                    String relation1 = " " + item1.getString("edgeTypeDis") + " ";
                                    String des1;


                                    if (edgeFormat1.equalsIgnoreCase("2")) {
                                        if (group == 1)
                                            des1 = des + "<br>and " + from1 + relation1 + to1 + " ( Mutual relation )";
                                        else
                                            des1 = from1 + relation1 + to1 + " ( Mutual relation )<br>and " + des;

                                    } else if (edgeFormat1.equalsIgnoreCase("0")) {
                                        if (group == 1)
                                            des1 = des + "<br>and " + from1 + relation1 + to1 + " ( undirected relation )";
                                        else
                                            des1 = from1 + relation1 + to1 + " ( undirected relation )<br>and " + des;


                                    } else {
                                        if (group == 1)
                                            des1 = des + "<br>and " + from1 + relation1 + to1;
                                        else
                                            des1 = from1 + relation1 + to1 + "<br>and " + des;

                                    }


                                    if (fromNode1.equals(recNode) || toNode1.equals(recNode)) {

                                        if (!forAttriRec) {
                                            if ((des1.indexOf(user) > -1) && (des1.indexOf(target) > -1)) {

                                            } else {

                                                path.add("\u2726 " + des1);
                                            }

                                        } else
                                        if ((des1.indexOf(user) > -1) && (des1.indexOf(recNode.getString("label")) > -1)) {

                                            indirectlinks.add(recNode.getInt("nodeKey"));
                                            path.add("\u2726 " + des1);

                                        }

                                        pathBy1Node = true;
                                        // paths by two nodes
                                    } else {

                                        //  System.out.println("path by 2 nodes");
                                        Iterator edges2 = null;

                                        if (useFromNode) {

                                            if (fromNode.equals(fromNode1) && !fromNode.equals(user)) {
                                                edges2 = toNode1.edges();
                                                String toLabel = toNode1.getString("label");
                                                if (group == 1) {

                                                    urItems2.add(fromNode.getString("label") + " `  " + toLabel);

                                                } else {
                                                    rtItems2.add(fromNode.getString("label") + " `  " + toLabel);
                                                }
                                            } else if (fromNode.equals(toNode1) && !fromNode.equals(user)) {


                                                edges2 = fromNode1.edges();
                                                String fromLabel = fromNode1.getString("label");
                                                if (group == 1) {
                                                    urItems2.add(fromNode.getString("label") + " `  " + fromLabel);
                                                } else {
                                                    rtItems2.add(fromNode.getString("label") + " `  " + fromLabel);
                                                }
                                            }
                                        } else {
                                            if (toNode.equals(fromNode1) && !toNode.equals(user)) {
                                                edges2 = toNode1.edges();
                                                String toLabel = toNode1.getString("label");
                                                if (group == 1) {

                                                    urItems2.add(toNode.getString("label") + " `  " + toLabel);

                                                } else {
                                                    rtItems2.add(toNode.getString("label") + " `  " + toLabel);
                                                }
                                            } else if (toNode.equals(toNode1) && !toNode.equals(user)) {

                                                edges2 = fromNode1.edges();
                                                String fromLabel = fromNode1.getString("label");
                                                if (group == 1) {

                                                    urItems2.add(toNode.getString("label") + " `  " + fromLabel);

                                                } else {
                                                    rtItems2.add(toNode.getString("label") + " `  " + fromLabel);
                                                }
                                            }


                                        }


                                        urItems2.remove(user);
                                        urItems2.remove(target);
                                        rtItems2.remove(user);
                                        rtItems2.remove(target);

                                        while (edges2.hasNext()) {

                                            EdgeItem item2 = (EdgeItem) edges2.next();

                                            if (item2.getString("hiddenItem").equalsIgnoreCase("0")) {

                                                String edgeFormat2 = item2.getString("direction");
                                                NodeItem fromNode2 = item2.getSourceItem();
                                                NodeItem toNode2 = item2.getTargetItem();


                                                String from2 = "<i><font color=blue>" + fromNode2.getString("label") + "</font></i>";
                                                String to2 = "<i><font color=blue>" + toNode2.getString("label") + "</font></i>";
                                                String relation2 = " " + item2.getString("edgeTypeDis") + " ";
                                                String des2;


                                                if (edgeFormat2.equalsIgnoreCase("2")) {
                                                    if (group == 1)
                                                        des2 = des1 + "<br>and " + from2 + relation2 + to2 + " ( Mutual relation )";
                                                    else
                                                        des2 = from2 + relation2 + to2 + " ( Mutual relation )<br>and " + des1;

                                                } else if (edgeFormat2.equalsIgnoreCase("0")) {
                                                    if (group == 1)
                                                        des2 = des1 + "<br>and " + from2 + relation2 + to2 + " ( undirected relation )";
                                                    else
                                                        des2 = from2 + relation2 + to2 + " ( undirected relation )<br>and " + des1;


                                                } else {
                                                    if (group == 1)
                                                        des2 = des1 + "<br>and " + from2 + relation2 + to2;
                                                    else
                                                        des2 = from2 + relation2 + to2 + "<br>and " + des1;

                                                }


                                                if (fromNode2.equals(recNode) || toNode2.equals(recNode)) {
                                                    if (!forAttriRec) {
                                                        if ((des2.indexOf(user) > -2) && (des2.indexOf(target) > -1)) {

                                                        } else
                                                            path.add("\u2726 " + des2);
                                                    } else
                                                    if ((des2.indexOf(user) > -1) && (des2.indexOf(recNode.getString("label")) > -1)) {

                                                        indirectlinks.add(recNode.getInt("nodeKey"));
                                                        path.add("\u2726 " + des2);

                                                    }
                                                    // paths by two nodes
                                                }

                                                // 3 steps path
                                            } //else not reach "rec" */
                                        }
                                        // 3 steps path
                                    } //else not reach "rec" */

                                }
                            }// not hidden

                        }// while

                    } //else not reach "rec"
                }// invisible
            }// not hidden
        }// while

        if (group == 1) {

            if (ur_itemsMap != null) {
                if (urItems2 != null && !pathBy1Node) {
                    ur_itemsMap.put(recNode, urItems2);

                } else if (urItems1.size() > 0) {
                    ur_itemsMap.put(recNode, urItems1);
                }
            }
        } else {
            if (rt_itemsMap != null) {
                if (rtItems2 != null && !pathBy1Node) {
                    rt_itemsMap.put(recNode, rtItems2);
                } else if (rtItems1.size() > 0) {
                    rt_itemsMap.put(recNode, rtItems1);
                }
            }

        }

        return path;
    }
     private void  dealBridgePath( List list, String[] itemsArray, List<String> paths, DefaultMutableTreeNode uptreeNode, DefaultMutableTreeNode path){


                        for (int i = 0; i < itemsArray.length; i++) {

                            String[] itemsStr = null;
                             if (itemsArray[i].contains("`"))
                            itemsStr = itemsArray[i].split(" ` ");

                            Set<String> bridgePaths = new HashSet<String>();
                            for (String innerPath : paths) {
                                if (!list.contains(innerPath)) {
                                     if (itemsStr != null) {
                                        if ((innerPath.indexOf(itemsStr[0].trim()) > -1) && (innerPath.indexOf(itemsStr[1].trim()) > -1))

                                            bridgePaths.add(innerPath);
                                    } else {
                                        if (innerPath.indexOf(itemsArray[i]) > -1)

                                            bridgePaths.add(innerPath);
                                    }
                                }
                            }
                           DefaultMutableTreeNode bridgePath = null;
                            if (itemsArray[i].contains("`"))
                                bridgePath = new DefaultMutableTreeNode("<html><b>Connection(s) via <i><font color=blue>" + itemsStr[0] + "</font></i>" + " and " + "<i><font color=blue>" + itemsStr[1] + "</font></i>(" + bridgePaths.size() + ")</b></html>");
                            else
                                bridgePath = new DefaultMutableTreeNode("<html><b>Connection(s) via <i><font color=blue>" + itemsArray[i] + "</font></i> (" + bridgePaths.size() + ")</b></html>");

                            if (bridgePaths.size() > 0)
                               uptreeNode.add(bridgePath);

                            for (String innerPath : bridgePaths) {
                                path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");
                                bridgePath.add(path);
                            }
                        }
     }

    private void createNodes
            (DefaultMutableTreeNode
                    top, Map<NodeItem, Set<String>> ur_items, Map<NodeItem, Set<String>> rt_items, AppletDataHandler1
                    dh1) {

        DefaultMutableTreeNode recommendation = null;

        DefaultMutableTreeNode path = null;
        if (recStatus == 1) {
            Map<Integer, List<String>> ur_pathsMap1 = null;
            Map<Integer, List<String>> rt_pathsMap1 = null;

            if (frame.getGP1().showAllRec) {
                // get no-hidden edges between user and rec
                ur_pathsMap1 = frame.getGP1().getUr_pathsMap();

                // get no-hidden edges between rec and target
                rt_pathsMap1 = frame.getGP1().getRt_pathsMap();
            }

            for (NodeItem node : recList) {

                String recScore = (node.getDouble("recScore") + "");
                if (recScore.length() > 5)
                    recScore = recScore.substring(0, 5);

                String recId = (node.getDouble("recId") + "");
                if (recId.length() > 5)
                    recId = recId.substring(0, 5);

                String recSe = (node.getDouble("recSe") + "");
                if (recSe.length() > 5)
                    recSe = recSe.substring(0, 5);


                boolean ur_folder = false;
                boolean rt_folder = false;

                int ur_depth = 0;
                int rt_depth = 0;
                String ur_hidden = "";
                String rt_hidden = "";

                String[] recPaths = dh1.getRecPath();
                for (int i = 0; i < recPaths.length; i++) {
                    String[] recPath = recPaths[i].split("-");
                    if (node.getString("login").equalsIgnoreCase(recPath[0])) {
                        ur_depth = Integer.parseInt(recPath[1]);
                        ur_hidden = recPath[2];
                        rt_depth = Integer.parseInt(recPath[3]);
                        rt_hidden = recPath[4];

                    }
                }


                if ( rt_depth == 3 || (rt_hidden.equalsIgnoreCase("false") && rt_depth == 2))
                    rt_folder = true;
                if (ur_depth == 3 || (ur_hidden.equalsIgnoreCase("false") && ur_depth == 2))
                    ur_folder = true;


                if (ur_folder)
                    ur_pathsMap1 = frame.getGP1().getUr_pathsMap();

                if (rt_folder)

                    rt_pathsMap1 = frame.getGP1().getRt_pathsMap();


                int recKey = node.getInt("nodeKey");
                String recLabel = node.getString("label");

                String moreInfo = "<html><font color=\"red\">" + "Connection to query: " + recId + ";</font>"
                        + "<font color=\"green\">" + " Connection to you: " + recSe + ";</font>"

                        + "<font color=\"blue\">" + " Score: " + recScore + "</font>";


                recommendation = new DefaultMutableTreeNode("<html><b><font color=blue>" + recLabel + "</font></b> (" + moreInfo + ")</html>");
                top.add(recommendation);
                List<String> ur_paths = pathsList(1, node, userNode, false);
                List<String> rt_paths = pathsList(2, node, targetNode, false);
                DefaultMutableTreeNode group1 = new DefaultMutableTreeNode("<html><b>Path(s) between <i><font color=blue>" + user + "</font></i> and <i><font color=blue>" + recLabel + "</font></i> (" + ur_paths.size() + ")</b></html>");
                recommendation.add(group1);

                DefaultMutableTreeNode group2 = new DefaultMutableTreeNode("<html><b>Path(s) between <i><font color=blue>" + recLabel + "</font></i> and <i><font color=blue>" + target + "</font></i> (" + rt_paths.size() + ")</b></html>");
                recommendation.add(group2);

                if (frame.getGP1().showAllRec) {

                    int shortestSize1 = ur_pathsMap1.get(recKey).size();
                    int hiddenSize1 = ur_paths.size() - shortestSize1;
                    DefaultMutableTreeNode shortest1 = new DefaultMutableTreeNode("<html><b>Shortest path (" + shortestSize1 + ")</b></html>");

                    DefaultMutableTreeNode hidden1 = new DefaultMutableTreeNode("<html><b>Hidden path (" + hiddenSize1 + ")</b></html>");
                    group1.add(shortest1);

                    group1.add(hidden1);

                    int shortestSize2 = rt_pathsMap1.get(recKey).size();
                    int hiddenSize2 = rt_paths.size() - shortestSize2;

                    DefaultMutableTreeNode shortest2 = new DefaultMutableTreeNode("<html><b>Shortest path (" + shortestSize2 + ")</b></html>");

                    DefaultMutableTreeNode hidden2 = new DefaultMutableTreeNode("<html><b>Hidden path (" + hiddenSize2 + ")</b></html>");


                    group2.add(shortest2);

                    group2.add(hidden2);



                    if (ur_folder) {

                        List list = Arrays.asList(ur_pathsMap1.get(recKey));

                        String[] urItemsArray = ur_items.get(node).toArray(new String[ur_items.get(node).size()]);
                        Arrays.sort(urItemsArray);

                        dealBridgePath(list, urItemsArray, ur_paths, shortest1, path);

                    } else {
                        // no-hidden edges between user and rec

                        for (String innerPath : ur_pathsMap1.get(recKey)) {
                            path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");
                            shortest1.add(path);
                        }

                        List list = Arrays.asList(ur_pathsMap1.get(recKey));

                        String[] urItemsArray = ur_items.get(node).toArray(new String[ur_items.get(node).size()]);
                        Arrays.sort(urItemsArray);

                        dealBridgePath(list, urItemsArray, ur_paths, hidden1, path);

                    }

                    if (rt_folder) {

                        List list1 = Arrays.asList(rt_pathsMap1.get(recKey));
                        String[] rtItemsArray = rt_items.get(node).toArray(new String[rt_items.get(node).size()]);
                        Arrays.sort(rtItemsArray);
                        dealBridgePath(list1, rtItemsArray, rt_paths, shortest2, path);
                    } else {
                        // no-hidden edges between user and rec

                        for (String innerPath : rt_pathsMap1.get(recKey)) {
                            path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");
                            shortest2.add(path);
                        }

                        List list1 = Arrays.asList(rt_pathsMap1.get(recKey));
                        String[] rtItemsArray = rt_items.get(node).toArray(new String[rt_items.get(node).size()]);
                        Arrays.sort(rtItemsArray);

                        dealBridgePath(list1, rtItemsArray, rt_paths, hidden2, path);

                    }

                    // for original paths
                } else {

                    if (ur_folder) {

                        List list = Arrays.asList(ur_pathsMap.get(recKey));

                        String[] urItemsArray = ur_items.get(node).toArray(new String[ur_items.get(node).size()]);
                        Arrays.sort(urItemsArray);

                        dealBridgePath(list, urItemsArray, ur_paths, group1, path);
                    } else {

                        for (String innerPath : ur_paths) {

                            path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");
                            group1.add(path);

                        }
                    }

                    if (rt_folder) {

                        List list = Arrays.asList(rt_pathsMap.get(recKey));

                                               String[] rtItemsArray = rt_items.get(node).toArray(new String[rt_items.get(node).size()]);
                                               Arrays.sort(rtItemsArray);
                                              
                          dealBridgePath(list, rtItemsArray, rt_paths, group2, path);
                 
                    } else {
                       
                        for (String innerPath : rt_paths) {


                            path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");

                            group2.add(path);


                        }//for

                    }
                }
            }
        } else {//  if (recStatus == 2)
            TupleSet allTuple = vis.getVisualGroup("graph.nodes");
            Iterator allIems = allTuple.tuples();
            Map<String, NodeItem> directRecs = new HashMap<String, NodeItem>();
            Set<String> directLabels = new HashSet<String>();
            Map<String, NodeItem> indirectRecs = new HashMap<String, NodeItem>();
            Set<String> indirectLabels = new HashSet<String>();

            while (allIems.hasNext()) {
                NodeItem nodeitem = (NodeItem) allIems.next();

                if (nodeitem.getString("nodeType").equals("Recommendation")) {

                    connecList(nodeitem);

                    int recKey = nodeitem.getInt("nodeKey");
                    String recLabel = nodeitem.getString("label");

                    if (indirectlinks.contains(recKey)) {
                        indirectRecs.put(recLabel, nodeitem);
                        indirectLabels.add(recLabel);
                    } else {

                        directRecs.put(recLabel, nodeitem);
                        directLabels.add(recLabel);
                    }
                }

            }


            String[] indirectArray = null;
            indirectArray = indirectLabels.toArray(new String[indirectLabels.size()]);
            Arrays.sort(indirectArray);

            String[] directArray = null;
            directArray = directLabels.toArray(new String[directLabels.size()]);
            Arrays.sort(directArray);

            if (recList.size() == 0) {
                for (int j = 0; j < directArray.length; j++) {
                    NodeItem node = directRecs.get(directArray[j]);

                    String recScore = (node.getDouble("recScore") + "");
                    if (recScore.length() > 5)
                        recScore = recScore.substring(0, 5);

                    String recId = (node.getDouble("recId") + "");
                    if (recId.length() > 5)
                        recId = recId.substring(0, 5);

                    String recSe = (node.getDouble("recSe") + "");
                    if (recSe.length() > 5)
                        recSe = recSe.substring(0, 5);

                    String[] ur_paths = connecList(node);

                    String recLabel = node.getString("label");

                    String moreInfo = "Number of relations with you: " + ur_paths.length + ";";

                    moreInfo += "<font color=\"red\">" + " Connection to query: " + recId + ";</font>"
                            + "<font color=\"green\">" + " Connection to you: " + recSe + ";</font>"

                            + "<font color=\"blue\">" + " Score: " + recScore + "</font>";
                    recommendation = new DefaultMutableTreeNode("<html><b><font color=blue>" + recLabel + "</font></b> (" + moreInfo + ")</html>");
                    top.add(recommendation);

                    for (String innerPath : ur_paths) {
                        path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");

                        recommendation.add(path);
                    }

                }

                for (int j = 0; j < indirectArray.length; j++) {

                    NodeItem node = indirectRecs.get(indirectArray[j]);
                    treeNodeForSelectedRec(node, ur_items, recommendation, top, path);

                }//for
            }// for all
            else { // for selected rec
                for (NodeItem node : recList) {

                    treeNodeForSelectedRec(node, ur_items, recommendation, top, path);

                }//for

            }
        } //else
    }


    private void treeNodeForSelectedRec
            (NodeItem
                    node, Map<NodeItem, Set<String>> ur_items, DefaultMutableTreeNode
                    recommendation, DefaultMutableTreeNode
                    top, DefaultMutableTreeNode
                    path) {

        String[] ur_paths = connecList(node);

        String recScore = (node.getDouble("recScore") + "");
        if (recScore.length() > 5)
            recScore = recScore.substring(0, 5);

        String recId = (node.getDouble("recId") + "");
        if (recId.length() > 5)
            recId = recId.substring(0, 5);

        String recSe = (node.getDouble("recSe") + "");
        if (recSe.length() > 5)
            recSe = recSe.substring(0, 5);

        ur_items = ur_itemsMap;

        String recLabel = node.getString("label");

        String moreInfo = "";
        int recKey = node.getInt("nodeKey");
        List list = null;
        String[] urItemsArray = null;

        list = Arrays.asList(ur_pathsMap.get(recKey));

        urItemsArray = ur_items.get(node).toArray(new String[ur_items.get(node).size()]);
        Arrays.sort(urItemsArray);

        int intermediateNo = 0;
        for (int i = 0; i < urItemsArray.length; i++) {
            String[] urItemsStr = null;
            if (urItemsArray[i].contains("`"))
                urItemsStr = urItemsArray[i].split(" ` ");
            Set<String> bridgePaths = new HashSet<String>();
            for (String innerPath : ur_paths) {
                if (!list.contains(innerPath)) {
                    if (urItemsStr != null) {
                        if ((innerPath.indexOf(urItemsStr[0].trim()) > -1) && (innerPath.indexOf(urItemsStr[1].trim()) > -1))

                            bridgePaths.add(innerPath);
                    } else {
                        if (innerPath.indexOf(urItemsArray[i]) > -1)

                            bridgePaths.add(innerPath);
                    }

                }
            }
            if (bridgePaths.size() > 0)
                intermediateNo++;
        }
        moreInfo = "Direct contact: 0; Number of Intermediate contact: " + intermediateNo + ";";


        moreInfo += "<font color=\"red\">" + " Connection to query: " + recId + ";</font>"
                + "<font color=\"green\">" + " Connection to you: " + recSe + ";</font>"

                + "<font color=\"blue\">" + " Score: " + recScore + "</font>";
        recommendation = new DefaultMutableTreeNode("<html><b><font color=blue>" + recLabel + "</font></b> (" + moreInfo + ")</html>");
        top.add(recommendation);


        for (int i = 0; i < urItemsArray.length; i++) {

            String[] urItemsStr = null;
            if (urItemsArray[i].contains("`"))
                urItemsStr = urItemsArray[i].split(" ` ");
            Set<String> bridgePaths = new HashSet<String>();
            for (String innerPath : ur_paths) {

                if (!list.contains(innerPath)) {
                    if (urItemsStr != null) {
                        if ((innerPath.indexOf(urItemsStr[0].trim()) > -1) && (innerPath.indexOf(urItemsStr[1].trim()) > -1))

                            bridgePaths.add(innerPath);
                    } else {
                        if (innerPath.indexOf(urItemsArray[i]) > -1)

                            bridgePaths.add(innerPath);
                    }
                }
            }
            DefaultMutableTreeNode bridgePath = null;
            if (urItemsArray[i].contains("`"))
                bridgePath = new DefaultMutableTreeNode("<html><b>Connection(s) via <i><font color=blue>" + urItemsStr[0] + "</font></i>" + " and " + "<i><font color=blue>" + urItemsStr[1] + "</font></i>(" + bridgePaths.size() + ")</b></html>");
            else
                bridgePath = new DefaultMutableTreeNode("<html><b>Connection(s) via <i><font color=blue>" + urItemsArray[i] + "</font></i> (" + bridgePaths.size() + ")</b></html>");

            if (bridgePaths.size() > 0)
                recommendation.add(bridgePath);

            for (String innerPath : bridgePaths) {
                path = new DefaultMutableTreeNode("<html>" + innerPath + "</html>");
                bridgePath.add(path);
            }
        } //for
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
            (AppletDataHandler1
                    dh1) {

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createTextPane(dh1), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        pack();
        setSize(790, 350);
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