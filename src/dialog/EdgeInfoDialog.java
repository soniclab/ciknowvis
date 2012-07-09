package dialog;

import admin.MainFrame;
import admin.FileReader;

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

import prefuse.data.Edge;
import prefuse.data.Node;
import data.AppletDataHandler1;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class EdgeInfoDialog extends JFrame implements ActionListener, TreeSelectionListener {

    private MainFrame frame;
    private JButton okJButton;
    private JPanel pathPane;
    private JTree tree;
    private Node fnode, tnode;
    private FileReader reader;
    private AppletDataHandler1 dh1;
   private String fLabel, tLabel;
    public EdgeInfoDialog(Edge edge, MainFrame _frame, FileReader reader, AppletDataHandler1 dh1) {
        //   super(JOptionPane.getFrameForComponent(_frame), "", false);
        frame = _frame;
        fnode = edge.getSourceNode();
        tnode = edge.getTargetNode();
        fLabel = fnode.getString("label");
       tLabel = tnode.getString("label");
        this.reader = reader;
        this.dh1 = dh1;

        initDialog();
    }


    private Map<String, Set<String>> getFinalQuMap() {
        Set<String> questions = new HashSet<String>();
        Set<String> realquSet = new HashSet<String>();
        if(frame.isApplet) {
         // if applet
        for (String prop : frame.getNodeProp()) {
            if (prop.contains("::")) {
                questions.add(prop);
                realquSet.add(prop.split("::")[0]);
            }
        }
        }else{
        // if application
         for (String prop : reader.getNodeProp()) {
            if (prop.contains("::")) {
                questions.add(prop);
                realquSet.add(prop.split("::")[0]);
            }
        }
        }

        Set<String> fnodeQuestions = nodeQuestionValue(fnode, dh1);
        Set<String> tnodeQuestions = nodeQuestionValue(tnode, dh1);

        Map<String, Set<String>> finalQuMap = new HashMap<String, Set<String>>();

        for (String realqu : realquSet) {
           Set<String> fquStr = new HashSet<String>();
           Set<String> tquStr = new HashSet<String>();
            Set<String> finalQu = new HashSet<String>();
           int commonNo = 0;
                            for (String fqu : fnodeQuestions) {

                                if (fqu.startsWith(realqu))
                                    fquStr.add(fqu);
                            }
                            for (String tqu : tnodeQuestions) {
                              if (tqu.startsWith(realqu))
                                    tquStr.add(tqu);
                            }
             for (String fquvalue : fquStr) {
                if(fquvalue.endsWith("1")) {
                    int index0  =fquvalue.indexOf("::") +2;
                     int index1  =fquvalue.indexOf("||");
                    String disvalue1 = fquvalue.substring(index0, index1);
                    if(tquStr.contains(fquvalue)) {
                        commonNo ++;

                            String quArray = disvalue1 + "||" +disvalue1;
                            finalQu.add(quArray);
                    }else{
                        String quArray = disvalue1 + "||" + "";
                            finalQu.add(quArray);
                    }
                }
            }

             for (String tquvalue : tquStr) {
                if(tquvalue.endsWith("1")&& (!fquStr.contains(tquvalue))) {

                     int index0  =tquvalue.indexOf("::") +2;
                     int index1  =tquvalue.indexOf("||");
                    String disvalue1 = tquvalue.substring(index0, index1);

                        String quArray = "" + "||" + disvalue1;
                            finalQu.add(quArray);

                }
            }
           if(finalQu.size()>0)
            finalQuMap.put(realqu + "||" + commonNo, finalQu);
       }


        return finalQuMap;
    }

    private Set<String> nodeQuestionValue(Node node, AppletDataHandler1 dh1) {
        Set<String> questionValues = new HashSet<String>();
        Map<String, List<String>> nodeAttri = null;
        if (frame.getNodeAttri() != null)
            nodeAttri = frame.getNodeAttri();
        else {
            dh1.getNodeAttri();
            nodeAttri = dh1.getNodeAttri();
        }


        List<String> nodeinfos = nodeAttri.get(node.getString("login"));
        for (String info : nodeinfos) {
            if (info.contains("::"))
                questionValues.add(info);
        }
        return questionValues;

    }

    private JPanel createButtons
            () {

        JPanel flowJPanel;
        okJButton = new JButton("Ok");
        okJButton.addActionListener(this);
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowJPanel.setBackground(Color.white);
        flowJPanel.add(okJButton);

        return flowJPanel;
    }


    private JScrollPane createTextPane
            () {
        pathPane = new JPanel(new GridLayout(1, 0));

        //Create the nodes.
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode("");
        createNodes(top);

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
            (DefaultMutableTreeNode
                    top) {

        DefaultMutableTreeNode directRe = null;
        DefaultMutableTreeNode questionNode = null;
        DefaultMutableTreeNode indirectRe = null;

        //Direct Relations
        directRe = new DefaultMutableTreeNode("<html><b><font color=blue>Direct Relations</font></b></html>");

        top.add(directRe);
        DefaultMutableTreeNode directtree = null;
        List<Edge> edges = getMultiEgdes();

        for (Edge edge : edges) {
            String fLabel = edge.getSourceNode().getString("label");
            String tLabel = edge.getTargetNode().getString("label");
            String type = edge.getString("edgeTypeDis");

            directtree = new DefaultMutableTreeNode("<html>" +"\u2726 " +  fLabel + " <b><i>" + type + "</i></b> " + tLabel + "</html>");

            directRe.add(directtree);
        }

        // Survey Question Answers
        questionNode = new DefaultMutableTreeNode("<html><b><font color=blue>Survey Question Answers:</font></b></html>");
        top.add(questionNode);

        Map<String, Set<String>> quMap = getFinalQuMap();
        for (String quandNo : quMap.keySet()) {

            int index = quandNo.indexOf("||");
            String quTitle = quandNo.substring(0, index);
             String coNo = quandNo.substring(index+2);

           DefaultMutableTreeNode   questiontree = new DefaultMutableTreeNode("<html><i>" + quTitle + " (" + coNo + " in common)</i> </html>");
          questionNode.add(questiontree);
          
            String title = "<html><table border=\"0\" CELLPADDING=\"5\" CELLSPACING=\"1\" WIDTH=\"500\">\n" +
                                  "  <tr>\n" ;

                          title = title+
                                  "    <td WIDTH=\"250\" align=center ><b>"  + fLabel + "</b></td>\n" +
                                  "    <td WIDTH=\"250\" align=center> <b>" + tLabel + "</b></td>\n" +
                                  "  </tr>\n" +
                                  "</table></html>";

                           DefaultMutableTreeNode titleValue = new DefaultMutableTreeNode(title);
                           questiontree.add(titleValue);


             for (String quAn : quMap.get(quandNo)) {

                 int indexNr = quAn.indexOf(("||"));
                   String str1 = "";
                 if(!quAn.startsWith("||"))
                    str1 = quAn.substring(0,indexNr );
                  String str2 = quAn.substring(indexNr+2 );


                 String label = "<html><table border=\"0\"  CELLPADDING=\"5\" CELLSPACING=\"1\" WIDTH=\"500\">\n" +
                       "  <tr>\n" ;
              if(str1.equals(str2)){

                label = label +
                       "    <td WIDTH=\"250\" bgcolor=#6CC417>"  + str1 + "</td>\n" +
                       "    <td WIDTH=\"250\" bgcolor=#6CC417>" + str2 + "</td>\n" +
                       "  </tr>\n" +
                       "</table></html>";
              }else{
                  label = label +
                       "    <td WIDTH=\"250\" bgcolor=#EDE275>"  + str1 + "</td>\n" +
                       "    <td WIDTH=\"250\" bgcolor=EDE275>" +str2 + "</td>\n" +
                       "  </tr>\n" +
                       "</table></html>";
              }
                DefaultMutableTreeNode quValue = new DefaultMutableTreeNode(label);
                questiontree.add(quValue);

             }
        }

        // indirect relation
        Map<String, Map<String, Set<String>>> labelRelations = getIndirectRelations();
        if (labelRelations != null) {
            indirectRe = new DefaultMutableTreeNode("<html><b><font color=blue>Indirect relation: They have the following nodes in common in the Knowledge Network</font></b></html>");
            top.add(indirectRe);
            DefaultMutableTreeNode nodeTypetree = null;
            for (String ntype : labelRelations.keySet()) {
                 Map<String, Set<String>> labelrelations = labelRelations.get(ntype);
                int size = labelrelations.size();
                nodeTypetree = new DefaultMutableTreeNode("<html><i>" + ntype + "(" + size + ")</i> </html>");
                indirectRe.add(nodeTypetree);

                DefaultMutableTreeNode nodeLabeltree = null;

                String[] labelArray = labelrelations.keySet().toArray(new String[labelrelations.keySet().size()]);
                Arrays.sort(labelArray);

                for (int j = 0; j < labelArray.length; j++) {

                    nodeLabeltree = new DefaultMutableTreeNode("<html><i>" + labelArray[j] + "</i> </html>");
                    nodeTypetree.add(nodeLabeltree);
                    DefaultMutableTreeNode relationtree = null;
                    for (String relation : labelrelations.get(labelArray[j])) {
                        relationtree = new DefaultMutableTreeNode("<html>" +"\u2726 " + relation + "</html>");
                        nodeLabeltree.add(relationtree);
                    }

                }
            }
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


    private Map<String, Map<String, Set<String>>> getIndirectRelations
            () {
        Map<String, Map<String, Set<String>>> nTypeRelations = new HashMap<String, Map<String, Set<String>>>();
        Map<String, Set<String>> nlabelRelations = new HashMap<String, Set<String>>();

        Set<Node> commonNodes = new HashSet<Node>();
        Set<Node> tNodes = new HashSet<Node>();

        for (Iterator fit = fnode.neighbors(); fit.hasNext();) {

            Node node = (Node) fit.next();
            commonNodes.add(node); // includeDerivedEdges=true
        }

        for (Iterator fit = tnode.neighbors(); fit.hasNext();) {

            Node node = (Node) fit.next();
            tNodes.add(node); // includeDerivedEdges=true
        }

        commonNodes.retainAll(tNodes);

        if (commonNodes.size() > 0) {
            Set<String> ntypes = new HashSet<String>();
            for (Node node : commonNodes) {
                ntypes.add(node.getString("nodeType"));
            }

            for (String ntype : ntypes) {

                for (Node node : commonNodes) {
                    String ntype1 = node.getString("nodeType");
                    String nodeLabel = node.getString("label");
                    Set<String> relStrSet = new HashSet<String>();

                    // get all edges between fromNode and node,  between toNode and node
                    for (Iterator nodeEdges = node.edges(); nodeEdges.hasNext();) {
                        Edge edge = (Edge) nodeEdges.next();
                        Set<Node> ftNodes = new HashSet<Node>();
                        ftNodes.add(edge.getSourceNode());
                        ftNodes.add(edge.getTargetNode());
                        if (ftNodes.contains(fnode) || ftNodes.contains(tnode)) {
                            String fLabel = edge.getSourceNode().getString("label");
                            String tLabel = edge.getTargetNode().getString("label");
                            String type = edge.getString("edgeTypeDis");
                            String relStr = fLabel + " <b><i>" + type + "</i></b> " + tLabel;
                            relStrSet.add(relStr);
                        }
                    }

                    nlabelRelations.put(nodeLabel, relStrSet);
                    if (ntype.equals(ntype1)) {
                        nTypeRelations.put(ntype, nlabelRelations);
                    }
                }
            }

            return nTypeRelations;
        } else
            return null;
    }

    private List<Edge> getMultiEgdes
            () {

        Iterator it1 = fnode.edges();
        Iterator it2 = tnode.edges();
        Set<Edge> edges1 = new HashSet<Edge>();
        Set<Edge> edges2 = new HashSet<Edge>();
        List<Edge> edgesList = new ArrayList<Edge>();

        while (it1.hasNext()) {
            Edge e1 = (Edge) it1.next();
            edges1.add(e1);
        }
        while (it2.hasNext()) {
            Edge e2 = (Edge) it2.next();
            edges2.add(e2);
        }

        edges1.retainAll(edges2);
        // ordering edges by alpha letter

        Iterator iter = edges1.iterator();
        String[] edgedis = new String[edges1.size()];
        int i = 0;
        while (iter.hasNext()) {
            Edge e1 = (Edge) iter.next();
            String edgeType = e1.getString("edgeType");
            edgedis[i] = edgeType;
            i++;
        }

        Arrays.sort(edgedis);


        for (int j = 0; j < edges1.size(); j++) {
            String disStr = edgedis[j];

            Iterator iter1 = edges1.iterator();

            while (iter1.hasNext()) {

                Edge e1 = (Edge) iter1.next();
                String edgeType = e1.getString("edgeType");

                if ((disStr.equalsIgnoreCase(edgeType)) && (!edgesList.contains(e1)))
                    edgesList.add(e1);
            }
        }
        return edgesList;
    }

    private void initDialog
            () {

        this.setTitle("Information for relations between " + fLabel + " and " + tLabel);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createTextPane(), BorderLayout.CENTER);
        getContentPane().add(createButtons(), BorderLayout.SOUTH);

        pack();
        setSize(700, 350);
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