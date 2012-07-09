package visual;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Feb 2, 2008
 * Time: 6:23:01 PM
 * To change this template use File | Settings | File Templates.
 */

import data.AppletDataHandler1;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.*;
import prefuse.action.assignment.*;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.Layout;
import prefuse.activity.Activity;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.*;
import prefuse.data.*;
import prefuse.data.util.BreadthFirstIterator;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.search.RegexSearchTupleSet;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.*;
import prefuse.render.Renderer;
import prefuse.util.*;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.*;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import prefuse.visual.sort.ItemSorter;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

import dialog.*;
import admin.MainFrame;
import admin.FileReader;

public class GraphView extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    private static final String linear = "linear";
    public static final String EDGE_DECORATORS = "weightString";

    private static final String AGGR = "aggregates";
    private static final String[] neighborGroups = {"sourceNode", "targetNode", "inEdge", "outEdge", "mutualE", "undirectedE"};
    private static final String depthGroup = "depthEdge";
    private double minWeight = 0.0;
    private double maxWeight = 1.0;

    private Visualization m_vis;
    private MainFrame admin;

    private Display display;
    private AppletDataHandler1 dh;
    private String label;
    private ActionList draw;
    // set circlelayout = 1, random = 2, auto = 4, tree = 3, group = 5, recommedation = 6
    public int layoutNumber;
    private Graph g;
    private RadialTreeLayoutCustom treeLayout;
    private CircleLayoutCustom cl;
    private CustomSavedLayout csl;
    private RecLayout rel;
    private RecLayerLayout recl;
    private GroupingLayout gl;
    private RandomLayoutCustom rl;
    private FruchtermanReingoldLayoutCustom fr;
    private boolean showWeightstarted;
    private boolean aggrstarted;
    private boolean showWeight;
    private boolean initView = true;
    String disLabel;
    private VisualGraph vg;
    private boolean fromInit = true;
    private boolean toInit, showOrHide;

    private boolean multiNodeDepth;
    private Set<Node> multiNode;
    public boolean showAllRec;
    // list to hold  hide or show nodes and edges
    // list to hold  hide or show nodes and edges
    private Set<String> nodeKeys;
    private Set<String> edgeKeys;
    private Map<String, String> nodeColorMap;
    private Map<String, String> edgeColorMap;
    private Map<String, double[]> nodeXYMap;

    private Set<String> aggLabel;

    private int nodeActionType;  //hidden node = 1, all nodes= 2,  show node= 3
    //  private int groupActionType;
    private JSearchPanel search;

    private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
    private Set<AggregateItem> groupSet;
    private int nodeCount = 0;
    private int edgeCount = 0;
    private boolean visualDataChanged;
    private AggregateTable at;
    private Set<String> attriSet;
    private boolean dataInit;
    private int ur_depth = 0, rt_depth = 0;
    private String utHasDirLink;
    private int height = 0;
    private boolean groupChanged;
    private GroupLegendDialog groupD = null;

    private String clusteringDis;
    private String clusteringView;
    final JFastLabel title = new JFastLabel("                 ");
    private int fontSize = 8;

    private boolean forZoom, forZoom1, forPan;
    JCheckBox[] checkButton;

    private boolean changedfromLayout;
    public int nodeDisStatus = 0;     // 0--default, 1-- minNode, 2-- shaped, 3-- sized, 4--imaged
    private boolean labelNode, maxEdgeWidth;
    private String edgeInfo = "";
    private String nodeInfo = "";
    private String refTarget = "";
    private Legend1 legend1;
    private ProcessingMessage message;
    private ColorAction nodeColorAction, edgeFillColor, edgeStrokeColor, textfill;
    private ColorAction border;
    private String datatype;
    private TupleSet edgeTupleSet;
    private Set<EdgeItem> invisibleEdges;
    public int normalizedType1 = -1, normalizedType2 = -1;
    private double displayMin, displayMax, displayMin2, displayMax2;
    private boolean defaultOption, zeroMin1, zeroMin2, fromzoomSelected, forShowAll, showIndiect;
    private int recStatus;
    private JSplitPane split;
    protected boolean isGreyEdge;
    private NormalizeRecPara normalizedD;
    private ImageDialog imageD;

    public int framewidth, frameheight;
    public int imageSize = 50;
    public int oriImageSize = 0;
    public boolean isCustomlayout, fromIndirect, hasOneRec, partialLabel, fromAll, fromShapedNode;
    private int originalDisStatus = 0;
    public int clusterMethod = 1;
    public String recNodeInfo = "";
     public String recDistanceinfo = "";
       public boolean recLegend;
    // private PrefixSearchTupleSet preSearch;
    private RegexSearchTupleSet preSearch;
    private Map<Integer, List<String>> ur_pathsMap, rt_pathsMap;
    private Set<NodeItem> hoveredNodes;
    private HashMap<NodeItem, Line2D> lines;
    private Set<NodeItem> clickedRecNodes;
    private Map<String, String> shapeMap;
    private ResourceBundle rb;
    private FileReader reader;

    static {
        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(100));
        DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 8));

    }

    public GraphView(ResourceBundle rb, Graph g, String label, String disLabel, AppletDataHandler1 _dh, MainFrame _admin, int width, int height, int recStatus, boolean isCustomlayout, ProcessingMessage message, FileReader reader) {
        super(new BorderLayout());
        framewidth = _admin.framewidth;
        frameheight = _admin.frameheight;
        // create a new, empty visualization for our data
        m_vis = new Visualization();
        this.message = message;
        dh = _dh;
        this.rb = rb;
        this.label = label;
        this.disLabel = disLabel;
        this.isCustomlayout = isCustomlayout;
        this.g = g;
        this.admin = _admin;
        this.height = height;
        this.recStatus = recStatus;
        this.reader = reader;
        if (!dh.getLabelHiden().equalsIgnoreCase("0"))
            originalDisStatus = 1;
        setBackground(Color.lightGray);

        // set up the renderers for nodes display
        vg = setGraph(this.g);

        if (width < 1160)
            fontSize = 9;
        else if (width < 1500 && width > 1160)
            fontSize = 10;
        else if (width > 1500)
            fontSize = 10;

        Font font = FontLib.getFont("SansSerif", Font.PLAIN, fontSize);

        FontAction fonts = new FontAction(nodes, font);

        font = FontLib.getFont("SansSerif", Font.BOLD, fontSize);
        fonts.add("nodeType == 'focal'", font);


        setRenderers();
        if (recStatus == 2) {
            lines = new HashMap<NodeItem, Line2D>();
        }
        // adds graph to visualization and sets renderer label field


        SelectionManager selectionManager = new SelectionManager(m_vis);

        display = setupDisplayAndControls(selectionManager);
        display.setBorder(BorderFactory.createEmptyBorder());

        // assign node border colors
        border = new ColorAction(nodes, VisualItem.STROKECOLOR, ColorLib.gray(100));

        border.add("ingroup('_search_')", ColorLib.rgb(255, 0, 0)); //MA

        border.add("ingroup('_focus_')", ColorLib.rgb(255, 0, 0));


        if (recStatus == 1) {
            border.add("nodeType == 'Requester'", ColorLib.rgb(0, 102, 204));
            border.add("nodeType == 'Recommendation'", ColorLib.rgb(0, 102, 204));
            border.add("nodeType == 'Target'", ColorLib.rgb(0, 102, 204));

            font = FontLib.getFont("SansSerif", Font.BOLD, 11);
            fonts.add("nodeType == 'Requester'", font);
            fonts.add("nodeType == 'Recommendation'", font);
            fonts.add("nodeType == 'Target'", font);

        }

        if (recStatus == 2) {
            border.add("nodeType == 'Requester'", ColorLib.rgb(0, 102, 204));
            font = FontLib.getFont("SansSerif", Font.BOLD, 11);
            fonts.add("nodeType == 'Requester'", font);
            // for recommendation node
            // border.add("nodeType == 'Recommendation'", ColorLib.rgb(0, 102, 204));
            border.add("nodeType == 'Recommendation'", ColorLib.rgb(211, 211, 211));
            font = FontLib.getFont("SansSerif", Font.BOLD, 11);
            fonts.add("nodeType == 'Recommendation'", font);


        }
        border.add(new InGroupPredicate(neighborGroups[1]), ColorLib.rgb(0, 255, 0));

        // hilighted nodes with darker border
        StrokeAction stroke = new StrokeAction(nodes);
        stroke.add("ingroup('_focus_')", StrokeLib.getStroke(2));
        stroke.add("ingroup('_focus_')", StrokeLib.getStroke(2));


        if (recStatus == 1) {
            stroke.add("nodeType == 'Requester'", StrokeLib.getStroke(2));
            stroke.add("nodeType == 'Recommendation'", StrokeLib.getStroke(2));
            stroke.add("nodeType == 'Target'", StrokeLib.getStroke(2));
        }

        // if (recStatus == 2) {
        //    stroke.add("nodeType == 'Requester'", StrokeLib.getStroke(2));
        //  stroke.add("nodeType == 'Recommendation'", StrokeLib.getStroke(2));
        // }

        // node color
        String colorBy = dh.getColorQuestion();
        datatype = "nodeType";
        if (!colorBy.equalsIgnoreCase("NodeType"))
            datatype = "colorAttri";

        // colorAttri  , nodeType
        nodeColorAction = new NodeColorAction(nodes, datatype, Constants.NOMINAL, VisualItem.FILLCOLOR);

        // edge color
        /*  if (recStatus == 2) {
         edgeFillColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.FILLCOLOR, true);
         edgeStrokeColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.STROKECOLOR, true);

         isGreyEdge = true;

     } else { */
        edgeFillColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.FILLCOLOR, false);
        edgeStrokeColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.STROKECOLOR, false);

      //   }

        setNeighbourColor();
        ActionList animatePaint = new ActionList(1);
        animatePaint.add(new ColorAnimator(nodes));

        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);

        draw = new ActionList();

        // node border color
        draw.add(border);

        draw.add(stroke);

        // node text color
        textfill = new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));

        // textfill.add("nodeType == 'focal'", ColorLib.rgb(255, 180, 180));
        textfill.add("nodeType == 'focal'", ColorLib.rgb(0, 0, 156));
        // if node color is black, set label font color to white
        textfill.add("nodeColor == '0x000000'", ColorLib.rgb(255, 255, 255));


        draw.add(fonts);
        draw.add(textfill);

        draw.add(nodeColorAction);
        draw.add(edgeFillColor);
        draw.add(edgeStrokeColor);
        selFocusNode();

        // animate paint change for the border color of searched node

        animatePaint();

        m_vis.putAction("draw", draw);

        m_vis.run("draw");

        // now create the main layout routine
        ActionList layout = new ActionList();

        // recPara[1]----how many edges from user to rec,
        // recPara[2]----true, has hidden nodes from user to rec,
        // recPara[3]----how many edges from rec to target,
        // recPara[4]----true, has hidden nodes from rec to target
        // recPara[5]----true, user and target has direct link
        // recPara[6- end]----NodeIds belong to both user-rec and rec-target
        if (isCustomlayout && recStatus == 0) {

            csl = new CustomSavedLayout(nodes, display, null);
            layout.add(csl);

        } else {
            if (recStatus == 1) {
                String[] recPaths = dh.getRecPath();
                for (int i = 0; i < recPaths.length; i++) {
                    int ur_depth_i = Integer.parseInt(recPaths[i].split("-")[1]);
                    int rt_depth_i = Integer.parseInt(recPaths[i].split("-")[3]);

                    ur_depth = Math.max(ur_depth, ur_depth_i);
                    rt_depth = Math.max(rt_depth, rt_depth_i);
                }

                utHasDirLink = dh.getRecPara()[1];
                /*
             if (recPara[2].equalsIgnoreCase("true")) {

                 //ur_depth = ur_depth + 1;
             }
             if (recPara[4].equalsIgnoreCase("true")) {

                // rt_depth = rt_depth + 1;
             }
                */

                rel = new RecLayout(graph, ur_depth, rt_depth, utHasDirLink);
                //   rel = new RecLayout(graph, ur_depth, rt_depth);

                rel.setBreadthSpacing(height * 20 / 1000);

                layout.add(rel);

            } else if (recStatus == 2) {
                hoveredNodes = new HashSet<NodeItem>();
                String[] recPara = dh.getRecPara();
                refTarget = recPara[recPara.length - 1];
                String requestor = dh.getRecRequestor();
                recl = new RecLayerLayout(dh, nodes, normalizedType1, normalizedType2, displayMin2, displayMax2, zeroMin2, hasOneRec);
                layout.add(recl);

                normalizedD = null;
                if (normalizedD == null) {
                    normalizedD = new NormalizeRecPara(admin, this, dh, refTarget, requestor);
                    // normalizedD.setVisible(true);
                }

                 Set<NodeItem> recSet = new HashSet<NodeItem>();
                    TupleSet allTuple = m_vis.getVisualGroup(nodes);
                    Iterator allIems = allTuple.tuples();

                     while (allIems.hasNext()) {
                        NodeItem vitem = (NodeItem) allIems.next();
                        if (vitem.getString("nodeType").equals("Recommendation")) {


                                showIndiect = true;

                               recSet.add(vitem);

                        }
                    }
                 indirectlayout(null, nodeXYMap, true, recSet);



            } else {
                cl = new CircleLayoutCustom(nodes, display);
                layout.add(cl);


            }

        }
        layout.add(new RepaintAction());

        layout.add(new ZoomToFit(graph));

        if (isCustomlayout && recStatus == 0) {
            m_vis.putAction("cslayout", layout);
            m_vis.run("cslayout");

            layoutNumber = 7;

        } else {
            if (recStatus == 2) {
                m_vis.putAction("recLayerlayout", layout);
                m_vis.run("recLayerlayout");
                labelNode();
                layoutNumber = 6;


            } else if (recStatus == 1) {
                m_vis.putAction("reclayout", layout);
                m_vis.run("reclayout");

                layoutNumber = 6;
            } else {
                m_vis.putAction("circlelayout", layout);
                m_vis.run("circlelayout");

                layoutNumber = 1;

            }
        }
        //panel for mainframe
        JPanel panel = new JPanel(new BorderLayout());

        Box box = getBox(m_vis, label);

        // create a new JSplitPane to present legend
        split = new JSplitPane();

        //applet width is set to 1200 from servlet. here to set the legend part to 175.
        split.setDividerLocation(width - 175);
        split.setLeftComponent(display);

        legend1 = new Legend1(dh, this, message);
        legend1.setBackground(Color.white);

        legend1.setVisible(true);

        int hOfDim, vOfDim = 18;
        boolean needVScroll = false, needHScroll = false;

        if (legend1.hScrollable()) {
            hOfDim = 175 * 2;
            needHScroll = true;
        } else {
            hOfDim = 175;
        }
        if (legend1.getLegendSize() > 20 && legend1.getLegendSize() < 31) {
            vOfDim = display.getHeight() * 2;
            needVScroll = true;
        } else if (legend1.getLegendSize() > 30 && legend1.getLegendSize() < 41) {
            vOfDim = display.getHeight() * 3;
            needVScroll = true;
        } else if (legend1.getLegendSize() > 40 && legend1.getLegendSize() < 51) {
            vOfDim = display.getHeight() * 4;
            needVScroll = true;
        } else if (legend1.getLegendSize() > 50) {
            vOfDim = display.getHeight() * 5;
            needVScroll = true;
        } else if (legend1.getLegendSize() < 21) {
            vOfDim = display.getHeight();

        }

        legend1.setPreferredSize(new Dimension(hOfDim, vOfDim));

        JScrollPane scp = new JScrollPane(legend1);
        scp.setBorder(BorderFactory.createEmptyBorder());
        if (needVScroll)
            scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        else
            scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        if (needHScroll)
            scp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        else
            scp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        split.setBorder(BorderFactory.createEmptyBorder());
        split.setRightComponent(scp);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(true);
        split.setDividerSize(1);

        split.setResizeWeight(1.00);
        panel.add(split, BorderLayout.CENTER);
        panel.add(box, BorderLayout.SOUTH);


        add(panel);

        nodeKeys = new HashSet<String>();
        edgeKeys = new HashSet<String>();
        aggLabel = new HashSet<String>();
        nodeColorMap = new HashMap<String, String>();
        edgeColorMap = new HashMap<String, String>();
        nodeXYMap = new HashMap<String, double[]>();
        clickedRecNodes = new HashSet<NodeItem>();
        if (isCustomlayout) {
            saveFullGraphXY();

        }

        legend1.currentLegend();
        legend1.removeAll();
        legend1.repaint();


        rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
        if (isCustomlayout) {
            restoreDisOption();
            fitGraph();
        }
    }

    private void restoreDisOption() {

        try {
            //node display format
            if (dh.getNodeDisBy().equalsIgnoreCase("1")) {
                if (dh.getLabelNodeStatus().equalsIgnoreCase("1")) {
                    // assign label to node
                    TupleSet allTuple = m_vis.getVisualGroup(nodes);
                    Iterator items = allTuple.tuples();
                    while (items.hasNext()) {
                        NodeItem n = (NodeItem) items.next();
                        if (n.getString("isNodeLabel").equalsIgnoreCase("1")) {
                            n.set("focusLabel", n.getString("disLabel"));

                            partialLabel = true;
                        } else {
                            n.set("focusLabel", null);
                        }
                    }
                    labelNode = true;
                }
                minNode();
            } else if (dh.getNodeDisBy().equalsIgnoreCase("2")) {
                if (dh.getLabelNodeStatus().equalsIgnoreCase("1")) {
                    // assign label to node

                    TupleSet allTuple = m_vis.getVisualGroup(nodes);
                    Iterator items = allTuple.tuples();
                    while (items.hasNext()) {
                        NodeItem n = (NodeItem) items.next();

                        if (n.getString("isNodeLabel").equalsIgnoreCase("1")) {

                            n.set("focusLabel", n.getString("disLabel"));
                            partialLabel = true;
                        } else {
                            n.set("focusLabel", null);
                        }
                    }
                    labelNode = true;
                }
                shapeNode();
            } else if (dh.getNodeDisBy().equalsIgnoreCase("3")) {

                if (dh.getLabelNodeStatus().equalsIgnoreCase("1")) {
                    // assign label to node
                    TupleSet allTuple = m_vis.getVisualGroup(nodes);
                    Iterator items = allTuple.tuples();
                    while (items.hasNext()) {
                        NodeItem n = (NodeItem) items.next();
                        if (n.getString("isNodeLabel").equalsIgnoreCase("1")) {
                            n.set("focusLabel", n.getString("disLabel"));
                            partialLabel = true;
                        } else {
                            n.set("focusLabel", null);
                        }
                    }
                    labelNode = true;
                }
                String sizeLabel = dh.getSizeLabel();
                if (dh.getNodeSizeType() == 2) {

                    String size2Label = dh.getSize2Label();
                    NormalizedDialog2 normalizedD2 = new NormalizedDialog2(admin, this, dh, sizeLabel, size2Label, admin);

                } else {

                    NormalizedDialog normalizedD = new NormalizedDialog(admin, this, dh, sizeLabel);

                }

                admin.enableSetNode(true);
                admin.visibleSetNode(true);

            } else if (dh.getNodeDisBy().equalsIgnoreCase("4")) {
                oriImageSize = Integer.parseInt(dh.getImageSize());
                imageSize = oriImageSize;
                imageNode(imageSize);

            }

            if (dh.getGroupNodeStatus().equalsIgnoreCase("1"))
                layoutGroup(false);

            if (dh.getLabelEdgeStatus().equalsIgnoreCase("1"))
                showEdgeStrength();
            if (dh.getWeightEdgeStatus().equalsIgnoreCase("1"))
                admin.doNormalizedAction(true, false);
        } catch (Exception e) {

        }

    }

    private void setNeighbourColor() {
        edgeFillColor.add(new InGroupPredicate(neighborGroups[4]), ColorLib.rgb(0, 0,
                255));


        edgeFillColor.add(new InGroupPredicate(neighborGroups[2]), ColorLib.rgb(0, 255,
                0));
        edgeFillColor.add(new InGroupPredicate(neighborGroups[3]), ColorLib.rgb(255, 0,
                0));


        edgeStrokeColor.add(new InGroupPredicate(neighborGroups[4]), ColorLib.rgb(0, 0,
                255));
        edgeStrokeColor.add(new InGroupPredicate(neighborGroups[5]), ColorLib.rgb(0, 0,
                0));


        edgeStrokeColor.add(new InGroupPredicate(neighborGroups[2]), ColorLib.rgb(0, 255,
                0));
        edgeStrokeColor.add(new InGroupPredicate(neighborGroups[3]), ColorLib.rgb(255, 0,
                0));


        edgeFillColor.add(new InGroupPredicate(depthGroup), ColorLib.rgb(255, 0, 0));
        edgeStrokeColor.add(new InGroupPredicate(depthGroup), ColorLib.rgb(255, 0, 0));

    }

    public void updateColor(Color currentColor, String nodelegend, String edgeLegend) {

        if (nodelegend != null) {
            TupleSet allTuple = m_vis.getVisualGroup(nodes);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                Node n = (Node) item;
                String nodeDis = "";

                if (datatype.equals("nodeType")) {
                    nodeDis = n.getString("nodeType");
                    if (nodeDis.equals(nodelegend)) {
                        n.setString("nodeColor", "0x" + COLOR2HEX(currentColor));
                        nodeColorMap.put(nodeDis, "0x" + COLOR2HEX(currentColor));
                    }
                } else {
                    nodeDis = n.getString("colorAttri");
                    if (nodeDis.equals(nodelegend)) {
                        n.setString("nodeColor", "0x" + COLOR2HEX(currentColor));
                        nodeColorMap.put(nodeDis, "0x" + COLOR2HEX(currentColor));
                    }
                }

            }//while

            draw.add(nodeColorAction);


        } else if (edgeLegend != null) {

            TupleSet allTuple = m_vis.getVisualGroup(edges);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                Edge e = (Edge) item;
                String edgeTypeDis = e.getString("edgeTypeDis");

                if (edgeTypeDis.equals(edgeLegend)) {
                    e.setString("edgeColor", "0x" + COLOR2HEX(currentColor));
                    edgeColorMap.put(edgeTypeDis, "0x" + COLOR2HEX(currentColor));

                }
            }//while

            draw.add(edgeFillColor);
            draw.add(edgeStrokeColor);

        }
        draw.add(new RepaintAction());
        m_vis.putAction("editColor", draw);

        m_vis.run("editColor");

        g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);

        visualDataChanged = true;
        nodeActionType = 1;

        legend1.currentLegend();
        legend1.repaint();
    }

    public static String COLOR2HEX(Color c) {
        String hexNumber = Integer.toHexString(c.getRGB() & 0x00ffffff);
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < 6 - hexNumber.length(); i++) {
            ret.append("0");
        }
        ret.append(hexNumber);
        return ret.toString();
    }

    private void animatePaint() {

        //  preSearch = new PrefixSearchTupleSet();
        preSearch = new RegexSearchTupleSet();
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, preSearch);


        preSearch.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {

                // if search results > 0, clear focus tuple and add search results to the tuple
                TupleSet searchItems = m_vis.getGroup(Visualization.SEARCH_ITEMS);
                TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);

                if (searchItems.getTupleCount() > 0) {
                    focus.clear();

                    Iterator it = searchItems.tuples();
                    while (it.hasNext()) {

                        focus.addTuple((NodeItem) it.next());
                    }
                } else {
                    focus.clear();
                }
                m_vis.cancel("animatePaint");
                m_vis.run("draw");
                m_vis.run("animatePaint");
            }
        });


    }

    private static String timeOfTheDay() {
        Calendar calendar = new GregorianCalendar();

        return "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + ":" + calendar.get(Calendar.MILLISECOND);
    }

    private Display setupDisplayAndControls(SelectionManager selectionManager) {

        // set display

        display = new Display(m_vis);

        // display.setSize(700, 700);
        display.setCustomToolTip(new javax.swing.JToolTip());


        display.setHighQuality(true);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        display.addControlListener(new MultipleDragControl(this));
        display.addControlListener(selectionManager.getSelectionListener(this, admin, true));
        display.addPaintListener(selectionManager.getSelectionListener(this, admin, true));
        display.addControlListener(new FocusReleaseControl(m_vis, this, admin));

        display.addControlListener(new FocusControlCustom(1, this));
        display.addControlListener(new HoverToolTip());
        display.setItemSorter(new TreeDepthItemSorter());
        display.addControlListener(new ZoomControlCustom(this));
        display.addControlListener(new ZoomAllControl(this));
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new PanControlCustom(this));

        display.addControlListener(new PopupControl(this));
        display.addControlListener(new NeighborHighlightControl(this, m_vis, neighborGroups, recStatus, layoutNumber, showWeight));
        display.addComponentListener(new ResizeWindowLayout());
        display.addControlListener(new UrlControl());
        display.addPaintListener(new LayerBorder(this, lines));
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
        display.setDamageRedraw(false);
        display.setItemSorter(new MyItemSorter(this));

        return display;


    }

    protected Map<String, String> getShapeMap() {
        Map<String, String> attrShape = new HashMap<String, String>();
        // 6-real elipse, 7-real rectangle, 8-trapezoid
        int[] ShapePalette = new int[]{Constants.SHAPE_RECTANGLE, Constants.SHAPE_ELLIPSE, Constants.SHAPE_TRIANGLE_UP, Constants.SHAPE_HEXAGON, Constants.SHAPE_STAR, Constants.SHAPE_DIAMOND, 6, 7, 8};

        Set<String> attriSet = new HashSet<String>();

        TupleSet allTuple = m_vis.getVisualGroup(nodes);

        Iterator allIems = allTuple.tuples();

        while (allIems.hasNext()) {

            VisualItem item = (VisualItem) allIems.next();
            String attri = item.getString("shapeAttri");
            attriSet.add(attri);

        }

        int i = 0;
        for (String attri : attriSet) {

            if (i < ShapePalette.length) {
                attrShape.put(attri, ShapePalette[i] + "");

            } else {
                attrShape.put(attri, Constants.SHAPE_CROSS + "");
            }

            i++;
        }

        return attrShape;
    }


    public Display getDisplay() {
        return display;
    }

    private String getrecSe(VisualItem item) {

        if (dh.getLogin_recSeStr().size() == 0)
            return null;
        else
            return dh.getLogin_recSeStr().get(item.getString("login")) + "";

    }

    public String[] getSWLabel() {
        String swLabel = "";
        String[] labelist = null;
        try {

            swLabel = trimList(admin.getParameter("nodeHoverDataLabels"));
            labelist = makeArrayFromStr(swLabel);
        }
        catch (NullPointerException e) {
            // System.out.println(e);
        }


        return labelist;
    }

    private String trimList(String str) {
        return str.endsWith("||,") ? str.substring(0, str.length() - 2) : str;
    }

    private String[] makeArrayFromStr(String str) {
        ArrayList<String> tmp = new ArrayList<String>();
        for (StringTokenizer t = new StringTokenizer(str, "||");
             t.hasMoreTokens();) {
            tmp.add(t.nextToken());
        }
        return tmp.toArray(new String[tmp.size()]);
    }

    class HoverToolTip extends ControlAdapter {
        public void itemEntered(VisualItem item, MouseEvent e) {
            String queryStr = "";
            String recScore = "";
            String recId = "";
            String recSe = "";

            String requestor = dh.getRecRequestor();
            try {
                if (item.getGroup().equalsIgnoreCase(nodes)) {
                    if (recStatus == 1)

                        queryStr = dh.getRecTarget();

                    else if (recStatus == 2)
                        queryStr = refTarget;
                    if (recStatus > 0) {
                        recScore = (item.getDouble("recScore") + "");
                        if (recScore.length() > 5)
                            recScore = recScore.substring(0, 5);
                        recId = (item.getDouble("recId") + "");
                        if (recId.length() > 5)
                            recId = recId.substring(0, 5);

                        if (getrecSe(item) == null) {

                            recSe = (item.getDouble("recSe") + "");

                        } else {

                            recSe = getrecSe(item);

                        }
                        if (recSe.length() > 5)
                            recSe = recSe.substring(0, 5);
                    }
                    String tip = "";
                    if (!dh.getLabelHiden().equalsIgnoreCase("2"))
                        tip = "<html><body>" + (m_vis.getSourceTuple(item)).getString("label") + "<br>"
                                + rb.getString("NodeType") + ": " + item.getString("nodeType");
                    else
                        tip = "<html><body>" + rb.getString("NodeType") + ": " + item.getString("nodeType");

                    if (item.getString("nodeType").equals("Recommendation")) {
                        if (admin.getParameter("nodeHoverDataLabels") == null) {
                            tip += "<br>"

                                   + "<font color=\"red\">" +  rb.getString("Score") + ": " + recSe  + "<font><br>"
                                    + "<font color=\"green\">" + rb.getString("ConnectionTo") + " <b><i>" + queryStr + "</i></b>: " + recId + "<font><br>"

                                   + "<font color=\"blue\">" + rb.getString("ConnectionTo") + " <b><i>" + requestor + "</i></b>: " + recScore  + "<font>";
                        } else {
                            String[] swlabel = getSWLabel();
                            tip += "<br>"
                                    + "<font color=\"red\">" + " <b><i>" + swlabel[0] + "</i></b>: " + recSe + "<font><br>"
                                    + "<font color=\"green\">" + " <b><i>" + swlabel[1] + "</i></b>: " + recId + "<font><br>"

                                    + "<font color=\"blue\">" + " <b><i>" + swlabel[2] + "</i></b>: " + recScore + "<font>";
                        }
                    }
                    if (nodeDisStatus == 3) {  // resized
                        if (recStatus == 2) {
                            tip += "</body></html>";
                        } else {
                            if (dh.getSize2Label() != null) {
                                if (dh.getColorQuestion().equalsIgnoreCase("NodeType"))
                                    tip += tip + "<br>"
                                            + "<font color=\"blue\">" + dh.getSizeLabel() + ": " + item.getDouble("nodeSize") + "<br>"
                                            + dh.getSize2Label() + ": " + item.getDouble("nodeSize2") + "<font></body></html>";
                                else
                                    tip += "<br>"
                                            + rb.getString("Attribute") + ": " + item.getString("colorAttri") + "<br>"
                                            + "<font color=\"blue\">" + dh.getSizeLabel() + ": " + item.getDouble("nodeSize") + "<br>"
                                            + dh.getSize2Label() + ": " + item.getDouble("nodeSize2") + "<font></body></html>";
                            } else {
                                if (dh.getColorQuestion().equalsIgnoreCase("NodeType"))
                                    tip += "<br>"
                                            + "<font color=\"blue\">" + dh.getSizeLabel() + ": " + item.getDouble("nodeSize") + "<font></body></html>";
                                else
                                    tip += "<br>"
                                            + rb.getString("Attribute") + ": " + item.getString("colorAttri") + "<br>"
                                            + "<font color=\"blue\">" + dh.getSizeLabel() + ": " + item.getDouble("nodeSize") + "<font></body></html>";
                            }
                        }// noderesized
                    } else {
                        if (dh.getColorQuestion().equalsIgnoreCase("NodeType"))
                            tip += "</body></html>";
                        else
                            tip += "<br>" + rb.getString("Attribute") + ": " + item.getString("colorAttri") + "</body></html>";
                    }

                    display.setToolTipText(tip);
                }


            } catch (ClassCastException ex) {
                //System.out.println("Exception form HoverToolTip: " + ex);
            } catch (NullPointerException ne) {
                ///System.out.println("Exception form HoverToolTip: " + ne);
            } catch (Exception ne) {
                //  System.out.println("Exception form HoverToolTip: " + ne);
            }
        }


        public void itemExited(VisualItem item, MouseEvent e) {
            Visualization v = item.getVisualization();
            Display d = v.getDisplay(0);
            d.setToolTipText(null);
            v.run("draw");
        }

    }


    private void groupColors(ColorAction fill, AppletDataHandler1.Group[] items) {

        try {
            for (int i = 0; i < items.length; ++i) {
                String nameLabel = items[i].key;
                String color = items[i].color;
                Color cl;
                cl = Color.decode(color);
                int red = cl.getRed();
                int green = cl.getGreen();
                int blue = cl.getBlue();

                //  Predicate pre = ExpressionParser.predicate("grouplabel == '" + nameLabel + "'");


                fill.add("grouplabel == '" + nameLabel + "'", ColorLib.rgba(red, green, blue, 215));

            }
        } catch (Exception e) {
            System.out.println("Exeption when assign group color: " + e);
        }

    }

    public void focusSearchField() {
        search.requestFocus();
        search.setBackground(Color.decode("0xA4D3EE"));

    }


    private Box getBox(Visualization vis, String label) {

        // create a search panel

        SearchQueryBinding sq = new SearchQueryBinding(
                vis.getGroup(nodes), label,
                (SearchTupleSet) vis.getGroup(Visualization.SEARCH_ITEMS));
        search = sq.createSearchPanel();


        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));


        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

        title.setPreferredSize(new Dimension(700, 30));
        title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 12));

        nodeCount = m_vis.getVisualGroup(nodes).getTupleCount();


        edgeCount = m_vis.getVisualGroup(edges).getTupleCount();

        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);

        /**
         getBox(Component[] c, boolean horiz,
         int margin1, int margin2, int spacing)
         * @param margin1 the margin, in pixels, for the left or top side
         * @param margin2 the margin, in pixels, for the right or bottom side
         * @param spacing the minimum spacing, in pixels, to use between
         * components
         * @return a new Box instance with the given properties.
         * @see javax.swing.Box
         */
        Box box = null;
        if (recStatus > 0) {
            JLabel targetLabel1 = new JLabel(rb.getString("RecommendationQuery") + ":   ");
            String disLabel = refTarget;
            if (recStatus == 1) {
                targetLabel1 = new JLabel(rb.getString("RecommendationTarget") + ":   ");
                disLabel = dh.getRecTarget();

            }
            if (refTarget.length() > 25)
                disLabel = refTarget.substring(0, 24) + "...";
            JLabel targetLabel = new JLabel(disLabel);
            targetLabel.setToolTipText(refTarget);

            targetLabel.setForeground(Color.blue);
            box = UILib.getBox(new Component[]{title, targetLabel1, targetLabel, search}, true, 10, 10, 0);
        } else {
            box = UILib.getBox(new Component[]{title, search}, true, 10, 50, 0);
        }

        box.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        return box;


    }


    private void resetCount() {


        TupleSet nt1 = m_vis.getVisualGroup(nodes);
        Iterator nts1 = nt1.tuples();
        nodeCount = nt1.getTupleCount();

        while (nts1.hasNext()) {

            VisualItem item = (VisualItem) nts1.next();
            if (!item.isVisible())
                nodeCount--;
        }

        TupleSet et1 = m_vis.getVisualGroup(edges);
        Iterator ets1 = et1.tuples();
        edgeCount = et1.getTupleCount();
        while (ets1.hasNext()) {

            VisualItem item = (VisualItem) ets1.next();
            if (!item.isVisible())
                edgeCount--;
        }
        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);

    }


    private class ResizeWindowLayout extends UpdateListener {

        public void update(Object src) {
        }

        public void componentResized(java.awt.event.ComponentEvent e) {
            double x = e.getComponent().getWidth();
            double y = e.getComponent().getHeight();
            double min = x < y ? x : y;

            if (layoutNumber == 5) {

                gl.setRescale(x / min, y / min);
                layoutGroup(true);
                m_vis.run("grouplayout");
                fitGraph();

            } else if (layoutNumber == 6 && recStatus == 2 && !isCustomlayout) {
                if (fromAll) {

                    showInitialView();
                } else
                    layoutRec();

            } else if (layoutNumber == 7) {
                if (recStatus != 2) {
                    rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
                } else {
                    //   csl.setRescale(x / min, y / min);

                    m_vis.run("cslayout");
                    fitGraph();
                }
            } else {

                rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
                fitGraph();
            }
        }

    }

    public void rescaleNodes(double zoom, boolean forZoom, double mouseX, double mouseY, boolean fromzoomSelected) {


        ActionList layout1 = new ActionList();

        RescaleLayout rl = new RescaleLayout(nodes, zoom, zoom, forZoom, mouseX, mouseY, display, fromzoomSelected, recStatus, isCustomlayout);
        layout1.add(rl);
        if (layoutNumber == 5)
            layout1.add(new AggregateLayout(AGGR, m_vis));

        layout1.add(new RepaintAction());

        m_vis.putAction("rescalelayout", layout1);


        m_vis.run("rescalelayout");
        this.forZoom = forZoom;
    }


    public void zoomRect(boolean forZoom, Rectangle rect) {


        ActionList layout1 = new ActionList();

        RescaleLayout rl = new RescaleLayout(nodes, forZoom, rect, display);
        layout1.add(rl);
        if (layoutNumber == 5)
            layout1.add(new AggregateLayout(AGGR, m_vis));
        layout1.add(new RepaintAction());

        m_vis.putAction("zoomRectlayout", layout1);


        m_vis.run("zoomRectlayout");
        this.forZoom = forZoom;
        fromzoomSelected = true;

    }


    public void rescaleNodesForLabel() {


        ActionList layout1 = new ActionList();
        RescaleLayout rl = new RescaleLayout(nodes, display, true);
        layout1.add(rl);
        if (layoutNumber == 5)
            layout1.add(new AggregateLayout(AGGR, m_vis));
        layout1.add(new RepaintAction());

        m_vis.putAction("rescalelayout", layout1);


        m_vis.run("rescalelayout");

    }


    public void ZoomGraph(boolean forZoom1) {

        this.forZoom1 = forZoom1;
    }

    public boolean isForZoom1() {

        return forZoom1;
    }

    private class UrlControl extends ControlAdapter {


        public void itemClicked(VisualItem item, MouseEvent e) {
            Cursor cursor = display.getCursor();
            if (cursor.getType() == (Cursor.DEFAULT_CURSOR)) {


                if (e.getClickCount() == 2 && item.getGroup().equalsIgnoreCase(nodes)) {
                    String n_prefix = dh.getNodeCGIPrefix();
                    String login = item.getString("login");
                    boolean localInfo = false;
                    try {
                        if (admin.isApplet && admin.getParameter("graphML") != null) {

                            localInfo = true;
                        } else if (admin.isGraphML || reader.getParaMap().get("graphML") != null) {
                            localInfo = true;
                        }
                    } catch (Exception ex) {

                    }

                    if (localInfo) {
                        NodeInfoDialog nodeInfoD = new NodeInfoDialog((NodeItem) item, admin, dh);
                        nodeInfoD.setVisible(true);
                    } else if (n_prefix != null) {

                        try {
                            admin.getAppletContext().showDocument(new URL(n_prefix + "node=" + URLEncoder.encode(login, "utf-8")), "browser");
                        }
                        catch (MalformedURLException ex) {
                            System.out.println(ex);
                        }
                        catch (UnsupportedEncodingException uex) {
                            System.out.println(uex);
                        }
                    }

                }

            }
        }

    }


    private void saveFullGraphXY() {

        for (Iterator iter = g.nodes(); iter.hasNext();) {
            //                NodeItem node = (NodeItem) iter.next();

            Node node = (Node) iter.next();
            double[] xy = {node.getDouble("x"), node.getDouble("y")};

            nodeXYMap.put(node.getString("login"), xy);
        }

    }

    private void setRenderers() {


        Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();

        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(128));
        DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 8));

        DefaultRendererFactory drf = new DefaultRendererFactory();
        if (!dh.getLabelHiden().equals("0")) {

            LabelSizeRenderer nodeR = new LabelSizeRenderer(null, labelNode);
            drf.setDefaultRenderer(nodeR);
            nodeDisStatus = 1;

        } else {
            LabelRenderer tr = new LabelRenderer();

            //  if (fontSize == 8)
            tr.setRoundedCorner(5, 5);
            // else if (fontSize == 9)
            //  tr.setRoundedCorner(5, 5);
            // else
            //   tr.setRoundedCorner(5, 5);

            tr.setVerticalAlignment(Constants.BOTTOM);
            tr.setHorizontalAlignment(Constants.CENTER);
            tr.setHorizontalPadding(1);
            tr.setVerticalPadding(0);
            drf.setDefaultRenderer(tr);
            ((LabelRenderer) drf.getDefaultRenderer()).setTextField(disLabel);
        }


        MyEdgeRenderer er = new MyEdgeRenderer();

        if (fontSize == 8)
            er.setArrowHeadSize(5, 8);
        else if (fontSize == 9)
            er.setArrowHeadSize(6, 9);
        else

            er.setArrowHeadSize(7, 10);

        er.setDefaultLineWidth(1.0);


        drf.setDefaultEdgeRenderer(er);

        // Set up a renderer factory w/ defaults

        drf.add(new InGroupPredicate(EDGE_DECORATORS), new LabelRenderer("weightString"));

        prefuse.render.Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
        ((PolygonRenderer) polyR).setCurveSlack(0.00f);
        drf.add("ingroup('aggregates')", polyR);

        m_vis.setRendererFactory(drf);
    }

    private void selFocusNode() {

        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);

        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
                for (int i = 0; i < rem.length; ++i)
                    ((VisualItem) rem[i]).setFixed(false);
                for (int i = 0; i < add.length; ++i) {
                    ((VisualItem) add[i]).setFixed(false);
                    ((VisualItem) add[i]).setFixed(true);
                }


                m_vis.cancel("animatePaint");
                m_vis.run("draw");
                m_vis.run("animatePaint");
            }
        });

    }

    public VisualGraph setGraph(Graph g) {

        String predicateString = "hiddenItem == '" + 0 + "'";

        Predicate filter = ExpressionParser.predicate(predicateString);


        m_vis.reset();
        VisualGraph vg = m_vis.addGraph(graph, g, filter);

        VisualItem f = (VisualItem) vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);

        TupleSet focus1 = m_vis.getGroup(Visualization.FOCUS_ITEMS);
        focus1.clear();

        return vg;
    }

    public double getMinWeight() {
        return minWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }


    public void setLimit(double min, double max) {

        if (nodeActionType == 2) {
            nodeKeys.clear();
            edgeKeys.clear();
        }

        minWeight = min;
        maxWeight = max;


        if (showWeight) {
            TupleSet decorators = m_vis.getVisualGroup(EDGE_DECORATORS);

            Iterator<DecoratorItem> it = decorators.tuples();


            while (it.hasNext()) {
                DecoratorItem decorator = it.next();
                VisualItem decoratorvis = decorator.getDecoratedItem();
                Edge e = (Edge) decoratorvis;
                String edgePair = e.getString("edgeKey");

                double weight = decoratorvis.getDouble("edgeWeight");
                if (weight > max || weight < min) {
                    decorator.setVisible(false);
                    PrefuseLib.updateVisible(decorator, false);
                    decoratorvis.setVisible(false);
                    PrefuseLib.updateVisible(decoratorvis, false);

                    edgeKeys.add(edgePair);


                } else if (!edgeKeys.contains(edgePair)) {

                    decorator.setVisible(true);
                    PrefuseLib.updateVisible(decorator, true);
                    decoratorvis.setVisible(true);
                    PrefuseLib.updateVisible(decoratorvis, true);
                }
            }//while
        } else {

            TupleSet allTuple = m_vis.getVisualGroup(edges);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();
                double weight = item.getDouble("edgeWeight");
                Edge e = (Edge) item;
                String edgePair = e.getString("edgeKey");

                if (weight > max || weight < min) {

                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);
                    edgeKeys.add(edgePair);

                } else if (!edgeKeys.contains(edgePair)) {

                    item.setVisible(true);
                    PrefuseLib.updateVisible(item, true);
                }
            }//while

        }//if


        ActionList selection = new ActionList();

        selection.add(new RepaintAction());
        m_vis.putAction("selection", selection);

        m_vis.run("selection");

        toInit = false;
        showOrHide = true;
        g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
        nodeActionType = 1;
        visualDataChanged = true;
        resetCount();
        legend1.currentLegend();
        legend1.removeAll();
        legend1.repaint();

    }


    public void minEdgeWidth() {

        DataSizeAction noEdgeWidth = new DataSizeAction(edges, "noWeightLine");
        draw.add(noEdgeWidth);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        m_vis.run("draw");

        edgeInfo = "";
        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);
        maxEdgeWidth = false;
    }

    public void maxEdgeWidth(int normalizedType, double disMin, double disMax) {

        MyDataSizeAction edgeWidth = null;
        edgeWidth = new MyDataSizeAction(edges, dh, normalizedType, disMin, disMax);

        draw.add(edgeWidth);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        m_vis.run("draw");

        if (normalizedType == 0)
            edgeInfo = rb.getString("WidthByRealWeight");
        else if (normalizedType == 1)
            edgeInfo = rb.getString("WidthByLinearNormalizedWeight");
        else if (normalizedType == 2)
            edgeInfo = rb.getString("WidthByLogNormalizedWeight");

        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);
        maxEdgeWidth = true;

    }

    public void setResizePara(int normalizedType1, int normalizedType2, double displayMin, double displayMax, double displayMin2, double displayMax2, boolean defaultOption, boolean zeroMin1, boolean zeroMin2) {
        this.normalizedType1 = normalizedType1;
        this.normalizedType2 = normalizedType2;
        this.displayMin = displayMin;
        this.displayMax = displayMax;
        this.displayMin2 = displayMin2;
        this.displayMax2 = displayMax2;
        this.defaultOption = defaultOption;
        this.zeroMin1 = zeroMin1;

    }

    /*  public void resizeLink() {
          ActionList layout = new ActionList();

          recl = new RecLayerLayout(dh, nodes, normalizedType1, normalizedType2, displayMin2, displayMax2, zeroMin2);
          layout.add(recl);

          layout.add(new RepaintAction());
          layout.add(new ZoomToFit(graph));

          m_vis.putAction("reclayout", layout);


          m_vis.run("reclayout");
          fitGraph();
      }
      */
    public void resizeNode() {  // if resize node based on shapednode, the node should keep its shape and only change the size.
        if (nodeDisStatus == 2)
            fromShapedNode = true;
        else if (nodeDisStatus != 3 && fromShapedNode)
            fromShapedNode = false;

        if (defaultOption) {

            if (nodeDisStatus != originalDisStatus && !labelNode && nodeDisStatus != 3) {

                if (nodeDisStatus == 2) {
                    admin.toggleShapeNodes();
                } else if (nodeDisStatus == 4) {
                    admin.toggleImageNodes();
                    if (admin.getImageDialog() != null)
                        admin.getImageDialog().dispose();

                } else {
                    admin.toggleMinNodes();
                }

            }

            if (nodeDisStatus != originalDisStatus && labelNode && nodeDisStatus != 3) {
                if (nodeDisStatus == 2) {
                    admin.toggleShapeNodes();
                } else if (nodeDisStatus == 4) {
                    admin.toggleImageNodes();
                    if (admin.getImageDialog() != null)
                        admin.getImageDialog().dispose();

                } else {
                    admin.toggleMinNodes();
                }
                labelNode = true;
            }
        }

        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();
        String fieldName = disLabel;
        if (!isCustomlayout) {
            if (setFocusLabel() > 0)
                fieldName = "focusLabel";
        } else {

            if (dh.partNodesLabel())
                fieldName = "focusLabel";
        }


        Renderer nodeR = new LabelSizeRenderer(this, shapeMap, fromShapedNode, dh, normalizedType1, normalizedType2, displayMin, displayMax, displayMin2, displayMax2, zeroMin1, zeroMin2, fieldName, labelNode);
        drf.setDefaultRenderer(nodeR);


        MyEdgeRenderer er = new MyEdgeRenderer();


        er.setArrowHeadSize(5, 8);

        er.setDefaultLineWidth(1.0);


        drf.setDefaultEdgeRenderer(er);

        m_vis.setRendererFactory(drf);

        changeNodesize();
        nodeDisStatus = 3;

        if (recStatus == 2) {
            /*   if (normalizedType2 == 0)
                edgeInfo = rb.getString("EdgeLengthByRealValues");
            if (normalizedType2 == 1)
                edgeInfo = rb.getString("EdgeLengthByLinearNormalized");
            if (normalizedType2 == 2)
                edgeInfo = rb.getString("EdgeLengthByLogNormalized");
            */
            String recNodeLabel = "";
            if (getSWLabel() == null)
                recNodeLabel = "connection to " + refTarget;
            else
                recNodeLabel = getSWLabel()[1];
           /* if (normalizedType1 == 0)
                recNodeInfo = "real value of " + recNodeLabel;
            else if (normalizedType1 == 1)
                recNodeInfo = "linear normalization of " + recNodeLabel;
            else if (normalizedType1 == 2)
                recNodeInfo = "log normalization of " + recNodeLabel;
            */
            recNodeInfo= recNodeLabel;
            if(layoutNumber == 6){
              String recDisLabel = "";
                if (getSWLabel() == null){
                     recDisLabel = "Total score";
                } else{

                    recDisLabel = getSWLabel()[0];
                }
                recDistanceinfo = recDisLabel;
                 recLegend = true;
            }
        } else {

            if (normalizedType2 == -1) {
                if (normalizedType1 == 0)
                    nodeInfo = rb.getString("SizeByRealValue");
                else if (normalizedType1 == 1)
                    nodeInfo = rb.getString("SizeByLinearNormalized");
                else if (normalizedType1 == 2)
                    nodeInfo = rb.getString("SizeByLogNormalized");
            } else if (normalizedType2 == 0) {
                if (normalizedType1 == 0)
                    nodeInfo = rb.getString("BothWidthAndHeightByRealValues");
                else if (normalizedType1 == 1)
                    nodeInfo = rb.getString("WidthByLinearNormalizedHeightByVealValue");
                else if (normalizedType1 == 2)
                    nodeInfo = rb.getString("WidthByLogNormalizedHeightByRealValue");
            }

            if (normalizedType2 == 1) {
                if (normalizedType1 == 0)
                    nodeInfo = rb.getString("WidthByRealValueHeightByLinearNormalized");
                else if (normalizedType1 == 1)
                    nodeInfo = rb.getString("BothWidthAndHeightByLinearNormalized");
                else if (normalizedType1 == 2)
                    nodeInfo = rb.getString("WidthByLogNormalizedHeightByLinearNormalized");
            }
            if (normalizedType2 == 2) {
                if (normalizedType1 == 0)
                    nodeInfo = rb.getString("WidthByRealValueHeightByLogNormalized");
                else if (normalizedType1 == 1)
                    nodeInfo = rb.getString("WidthByLinearNormalizedHeightByLogNormalized");
                else if (normalizedType1 == 2)
                    nodeInfo = rb.getString("BothWidthAndHeightByLogNormalized");
            }
        }
        if (fromShapedNode) {
            legend1.shapeLegendStatus(true);
            legend1.removeAll();
            legend1.repaint();
        }
        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);


        try {
            if (recStatus == 2)
                admin.enableRecResize(true);
            admin.enableImage(false);
            admin.enableButton(true);
            admin.enableMenuItem(true);
            admin.enableShapeButton(false);
            admin.enableShapeMenuItem(false);
            admin.enableSetNode(true);

        } catch (Exception e) {

        }

        if (recStatus != 2)
            fitGraph();
        if (!fromShapedNode)
            shapeMap = null;
    }


    private void changeNodesize() {

        draw.add(new RepaintAction());


        m_vis.putAction("draw", draw);

        m_vis.run("draw");


    }

    public void labelNode() {
        // only show the label of focus node


        if (nodeDisStatus == 2) {
            labelNode = true;
            shapeNode();
        } else if (nodeDisStatus == 1) {
            labelNode = true;

            minNode();
        } else if (nodeDisStatus == 3) {
            labelNode = true;
            resizeNode();

        }

        if (!forZoom && !forZoom1 && !forPan && recStatus != 2)
            fitGraph();
    }

    public void noLabelNode() {
        if (nodeDisStatus == 2) {
            labelNode = false;
            shapeNode();
        } else if (nodeDisStatus == 1) {
            labelNode = false;
            minNode();

        } else if (nodeDisStatus == 3) {
            labelNode = false;
            resizeNode();

        }
        if (!forZoom && !forZoom1 && !forPan && recStatus != 2)
            fitGraph();
    }

    public void imageNode(int width) {

        if (nodeDisStatus != originalDisStatus && !labelNode && nodeDisStatus != 4) {

            if (nodeDisStatus == 2)
                admin.toggleShapeNodes();
            else if (nodeDisStatus == 1)
                admin.toggleMinNodes();
            else {
                admin.toggleResizeNodes();
                if (admin.getNormalizedDialog() != null)
                    admin.getNormalizedDialog().dispose();
                else if (admin.getNormalizedDialog2() != null)
                    admin.getNormalizedDialog2().dispose();

            }
        } else if (nodeDisStatus != originalDisStatus && labelNode && nodeDisStatus != 4) {
            if (nodeDisStatus == 2)
                admin.toggleShapeNodes();
            else if (nodeDisStatus == 1)
                admin.toggleMinNodes();
            else {
                admin.toggleResizeNodes();
                if (admin.getNormalizedDialog() != null)
                    admin.getNormalizedDialog().dispose();
                else if (admin.getNormalizedDialog2() != null)
                    admin.getNormalizedDialog2().dispose();
            }
            labelNode = true;
        }

        admin.toggleLabelNodes();
        admin.enableRecResize(false);
        admin.enableButton(false);
        admin.enableMenuItem(false);
        admin.enableShapeButton(false);
        admin.enableShapeMenuItem(false);
        admin.enableSetNode(false);
        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();
        LabelRenderer nodeRenderer = null;

        if (!dh.getLabelHiden().equals("0")) {

            nodeRenderer = new LabelRenderer("noLabel", "image");
        } else {
            nodeRenderer = new LabelRenderer(disLabel, "image");
        }
        nodeRenderer.setVerticalAlignment(Constants.CENTER);
        nodeRenderer.setHorizontalPadding(1);
        nodeRenderer.setVerticalPadding(0);
        nodeRenderer.setImagePosition(Constants.TOP);

        nodeRenderer.setRoundedCorner(5, 5);

        nodeRenderer.setMaxImageDimensions(width, height);

        drf.setDefaultRenderer(nodeRenderer);


        MyEdgeRenderer er = new MyEdgeRenderer();


        er.setArrowHeadSize(5, 8);


        er.setDefaultLineWidth(1.0);


        drf.setDefaultEdgeRenderer(er);

        m_vis.setRendererFactory(drf);

        changeNodesize();

        legend1.shapeLegendStatus(false);
        legend1.groupLegendStatus(false);

        legend1.removeAll();
        legend1.repaint();

        nodeDisStatus = 4;

        labelNode = false;
        nodeInfo = "";
        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);
        if (recStatus == 2) {
            rescaleNodes(1.0, false, 0, 0, fromzoomSelected);


        }
        admin.enableImage(true);

        if (imageD == null) {
            imageD = new ImageDialog(admin, this);
            imageD.setVisible(false);
        }
        imageSize = width;
        if (isCustomlayout)
            imageD.widthF.setText(imageSize + "");

        admin.toggleFitGraph();

        admin.toggleFitGraph();

        admin.toggleFitGraph();

    }


    public void minNode() {

        if (dh.getLabelHiden().equals("0")) {
            if (nodeDisStatus != originalDisStatus && !labelNode && nodeDisStatus != 1) {
                if (nodeDisStatus == 3) {
                    admin.toggleResizeNodes();
                    if (admin.getNormalizedDialog() != null)
                        admin.getNormalizedDialog().dispose();
                    else if (admin.getNormalizedDialog2() != null)
                        admin.getNormalizedDialog2().dispose();
                } else if (nodeDisStatus == 4) {

                    admin.toggleImageNodes();
                    if (admin.getImageDialog() != null)
                        admin.getImageDialog().dispose();
                } else
                    admin.toggleShapeNodes();
            }
            if (nodeDisStatus != originalDisStatus && labelNode && nodeDisStatus != 1) {

                if (nodeDisStatus == 3) {
                    admin.toggleResizeNodes();
                    if (admin.getNormalizedDialog() != null)
                        admin.getNormalizedDialog().dispose();
                    else if (admin.getNormalizedDialog2() != null)
                        admin.getNormalizedDialog2().dispose();
                } else if (nodeDisStatus == 4) {
                    admin.toggleImageNodes();

                    if (admin.getImageDialog() != null)
                        admin.getImageDialog().dispose();
                } else
                    admin.toggleShapeNodes();
                labelNode = true;
            }

        }
        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();

        String fieldName = disLabel;
        if (!isCustomlayout) {
            if (setFocusLabel() > 0)
                fieldName = "focusLabel";
        } else {

            if (dh.partNodesLabel())
                fieldName = "focusLabel";
        }
        LabelSizeRenderer nodeR = new LabelSizeRenderer(fieldName, labelNode);
        drf.setDefaultRenderer(nodeR);

        MyEdgeRenderer er = new MyEdgeRenderer();


        er.setArrowHeadSize(4, 7);

        // er.setDefaultLineWidth(0.5);


        drf.setDefaultEdgeRenderer(er);

        m_vis.setRendererFactory(drf);

        changeNodesize();

        nodeDisStatus = 1;
        admin.enableRecResize(false);
        admin.enableButton(true);
        admin.enableMenuItem(true);
        admin.enableShapeButton(false);
        admin.enableImage(false);
        admin.enableShapeMenuItem(false);
        admin.enableSetNode(false);
        nodeInfo = "";
        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);

    }


    public void shapeNode() {

        if (nodeDisStatus != originalDisStatus && !labelNode && nodeDisStatus != 2) {

            if (nodeDisStatus == 3) {
                admin.toggleResizeNodes();
                if (admin.getNormalizedDialog() != null)
                    admin.getNormalizedDialog().dispose();
                else if (admin.getNormalizedDialog2() != null)
                    admin.getNormalizedDialog2().dispose();

            } else if (nodeDisStatus == 4) {
                admin.toggleImageNodes();
                if (admin.getImageDialog() != null)
                    admin.getImageDialog().dispose();
            } else {

                admin.toggleMinNodes();
            }
        }
        if (nodeDisStatus != originalDisStatus && labelNode && nodeDisStatus != 2) {

            if (nodeDisStatus == 3) {
                admin.toggleResizeNodes();
                if (admin.getNormalizedDialog() != null)
                    admin.getNormalizedDialog().dispose();
                else if (admin.getNormalizedDialog2() != null)
                    admin.getNormalizedDialog2().dispose();
            } else if (nodeDisStatus == 4) {
                admin.toggleImageNodes();
                if (admin.getImageDialog() != null)
                    admin.getImageDialog().dispose();

            } else
                admin.toggleMinNodes();

            labelNode = true;
        }

        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();

        String fieldName = disLabel;
        if (!isCustomlayout) {
            if (setFocusLabel() > 0)
                fieldName = "focusLabel";
        } else {

            if (dh.partNodesLabel())
                fieldName = "focusLabel";
        }

        shapeMap = getShapeMap();
        LabelShapeRenderer shapeR = new LabelShapeRenderer(shapeMap, fieldName, labelNode);
        drf.setDefaultRenderer(shapeR);

        MyEdgeRenderer er = new MyEdgeRenderer();


        er.setArrowHeadSize(5, 8);

        er.setDefaultLineWidth(1.0);


        drf.setDefaultEdgeRenderer(er);

        m_vis.setRendererFactory(drf);
        changeNodesize();
        legend1.shapeLegendStatus(true);
        legend1.removeAll();
        legend1.repaint();

        nodeDisStatus = 2;
        admin.enableRecResize(false);
        admin.enableButton(true);
        admin.enableMenuItem(true);
        admin.enableShapeButton(true);
        admin.enableShapeMenuItem(true);
        admin.enableImage(false);
        admin.enableSetNode(false);

    }


    public void maxNode() {

        if (!dh.getLabelHiden().equals("0")) {
            if (nodeDisStatus != originalDisStatus && !labelNode && nodeDisStatus != 1) {
                if (nodeDisStatus == 3) {
                    admin.toggleResizeNodes();

                } else
                    admin.toggleShapeNodes();

            } else if (nodeDisStatus == 4) {
                admin.toggleImageNodes();

            }

            if (nodeDisStatus != originalDisStatus && labelNode && nodeDisStatus != 1) {
                if (nodeDisStatus == 3) {
                    admin.toggleResizeNodes();

                } else
                    admin.toggleShapeNodes();
                labelNode = true;
            } else if (nodeDisStatus == 4) {
                admin.toggleImageNodes();

            }


        }

        if (admin.getNormalizedDialog() != null)
            admin.getNormalizedDialog().dispose();
        else if (admin.getNormalizedDialog2() != null)
            admin.getNormalizedDialog2().dispose();
        else if (imageD != null)
            imageD.dispose();

        if (labelNode) {
            admin.toggleLabelNodes();
        }

        admin.enableRecResize(false);
        admin.enableButton(false);
        admin.enableMenuItem(false);
        admin.enableShapeButton(false);
        admin.enableShapeMenuItem(false);
        admin.enableImage(false);
        admin.enableSetNode(false);

        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();


        LabelRenderer tr = new LabelRenderer(disLabel);
        tr.setRoundedCorner(5, 5);

        tr.setVerticalAlignment(Constants.BOTTOM);
        tr.setHorizontalAlignment(Constants.CENTER);
        tr.setHorizontalPadding(1);
        tr.setVerticalPadding(0);

        drf.setDefaultRenderer(tr);

        MyEdgeRenderer er = new MyEdgeRenderer();


        er.setArrowHeadSize(5, 8);


        er.setDefaultLineWidth(1.0);


        drf.setDefaultEdgeRenderer(er);

        m_vis.setRendererFactory(drf);

        changeNodesize();

        legend1.shapeLegendStatus(false);
        legend1.removeAll();
        legend1.repaint();

        nodeDisStatus = 0;
        labelNode = false;

        nodeInfo = "";


        title.setText(rb.getString("TotalNodes") + ": " + nodeCount + "   " + nodeInfo + "   " + rb.getString("TotalEdges") + ": " + edgeCount + edgeInfo);
        if (recStatus == 2) {
            rescaleNodes(1.0, false, 0, 0, fromzoomSelected);

        } else

            fitGraph();


    }

    private int setFocusLabel() {
        TupleSet allTuple = m_vis.getVisualGroup(nodes);
        Iterator items = allTuple.tuples();
        while (items.hasNext()) {
            NodeItem n = (NodeItem) items.next();
            n.set("focusLabel", null);

        }

        TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
        int focusCount = focus.getTupleCount();
        Iterator items1 = focus.tuples();
        while (items1.hasNext()) {
            NodeItem n = (NodeItem) items1.next();
            n.set("focusLabel", n.getString("disLabel"));

        }
        return focusCount;
    }

    public void makeLayout() {

        if (visualDataChanged) {
            vg = setGraph(g);

            if (aggrstarted) {

                attriSet.removeAll(aggLabel);
                setAggregate(attriSet);

            }
            aggrstarted = false;
            changedfromLayout = true;
        }

    }


    public void layoutCircle() {

        resetSearchPanel();
        if (layoutNumber == 3) {

            m_vis.removeAction("filter");

        } else if (layoutNumber == 5 && !visualDataChanged && groupChanged == false) {

            hiddenAggr();

        }

        // now create the main layout routine
        ActionList layout = new ActionList();
        cl = new CircleLayoutCustom(nodes, display);
        layout.add(cl);

        layout.add(new RepaintAction());

        m_vis.putAction("circlelayout", layout);


        m_vis.run("circlelayout");

        if (layoutNumber == 5) {
            legend1.groupLegendStatus(false);
            legend1.removeAll();
            legend1.repaint();
        }


        if (recStatus == 2 && recLegend) {
            recLegend = false;
            legend1.removeAll();
            legend1.repaint();
        }

        layoutNumber = 1;
        if (showWeight) {
            firstShowWeight();
        } else {
            showWeightstarted = false;
        }

        visualDataChanged = false;
        fitGraph();
        disposeGroupLegend();
        if (admin.getInitialViewButton() != null)
            admin.getInitialViewButton().setEnabled(false);
    }

    // method not used. possibke used for restore node location
    /* public void layoutRestore() {
      ActionList layout = new ActionList();

      layout.add(new RepaintAction());

      m_vis.putAction("restorlayout", layout);


      m_vis.run("restorlayout");

  }  */

    public void layoutRandom() {

        resetSearchPanel();

        if (layoutNumber == 3) {

            m_vis.removeAction("filter");

        } else if (layoutNumber == 5 && !groupChanged) {

            hiddenAggr();
        }

        // now create the main layout routine
        ActionList layout = new ActionList();
        rl = new RandomLayoutCustom(graph, display);

        rl.setRandomSeed(getRandomSeed());

        layout.add(rl);

        layout.add(new RepaintAction());
        layout.add(new ZoomToFit(graph));

        m_vis.putAction("randomlayout", layout);

        m_vis.run("randomlayout");

        fitGraph();

        if (layoutNumber == 5) {
            legend1.groupLegendStatus(false);
            legend1.removeAll();
            legend1.repaint();
        }

        if (recStatus == 2 && recLegend) {
            recLegend = false;
            legend1.removeAll();
            legend1.repaint();
        }

        layoutNumber = 2;
        if (showWeight) {
            firstShowWeight();
        } else {
            showWeightstarted = false;
        }

        visualDataChanged = false;
        disposeGroupLegend();
        if (admin.getInitialViewButton() != null)
            admin.getInitialViewButton().setEnabled(false);
    }


    public void layoutCustom(Map<String, double[]> nodeXYMap) {

        resetSearchPanel();

        if (layoutNumber == 3) {

            m_vis.removeAction("filter");

        } else if (layoutNumber == 5 && !groupChanged) {

            hiddenAggr();
        }

        // now create the main layout routine
        ActionList layout = new ActionList();
        csl = new CustomSavedLayout(graph, display, nodeXYMap);
        layout.add(csl);

        if (layoutNumber == 5) {

            try {
                groupChanged = false;
                if (!aggrstarted || dataInit) {
                    firstGroup();
                }
                if (visualDataChanged && !dataInit) {

                    setAggregate(attriSet);

                }
            } catch (Exception e) {

            }

            layout.add(new AggregateLayout(AGGR, m_vis));

            DisplayGroupLegend();

        } else
            disposeGroupLegend();

        layout.add(new RepaintAction());
        layout.add(new ZoomToFit(graph));


        m_vis.putAction("cslayout", layout);


        m_vis.run("cslayout");

        layoutNumber = 7;
        if (showWeight) {
            firstShowWeight();
        } else {
            showWeightstarted = false;
        }

        visualDataChanged = true;

        display.repaint();
        legend1.currentLegend();
        legend1.removeAll();
        legend1.repaint();
        if (admin.getInitialViewButton() != null)
            admin.getInitialViewButton().setEnabled(false);

        if (isCustomlayout) {
            restoreDisOption();
            fitGraph();
        }
    }


    public void layoutRec() {

        resetSearchPanel();

        if (layoutNumber == 3) {

            m_vis.removeAction("filter");

        } else if (layoutNumber == 5 && groupChanged == false) {

            hiddenAggr();
        }

        ActionList layout = new ActionList();

        if (recStatus == 1) {
            rel = new RecLayout(graph, ur_depth, rt_depth, utHasDirLink);
            //   rel = new RecLayout(graph, ur_depth, rt_depth);

            rel.setBreadthSpacing(height * 20 / 1000);


            layout.add(rel);
        } else {

            hasOneRec = hadOneRec();
            if (!forShowAll)
                showDirectGraph();


            String[] recPara = dh.getRecPara();
            refTarget = recPara[recPara.length - 1];

            String requestor = dh.getRecRequestor();
            recl = new RecLayerLayout(dh, nodes, normalizedType1, normalizedType2, displayMin2, displayMax2, zeroMin2, hasOneRec);
            layout.add(recl);

            normalizedD = null;
            normalizedD = new NormalizeRecPara(admin, this, dh, refTarget, requestor);

            showIndiect = false;
            labelNode();
        }
        layout.add(new RepaintAction());

        m_vis.putAction("reclayout", layout);


        m_vis.run("reclayout");

        if (layoutNumber == 5) {
            legend1.groupLegendStatus(false);
            legend1.removeAll();
            legend1.repaint();
        }

        layoutNumber = 6;

        if (recStatus == 2 && !recLegend) {
            recLegend = true;
            legend1.removeAll();
            legend1.repaint();
        }

        if (showWeight) {
            firstShowWeight();
        } else {
            showWeightstarted = false;
        }

        visualDataChanged = false;
        if (recStatus == 1) {
            rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
            fitGraph();
        }
        disposeGroupLegend();
        if (admin.getInitialViewButton() != null)

            admin.getInitialViewButton().setEnabled(true);
    }


    public void openNormalizedDialog() {
        if (normalizedD != null) {
            if (!normalizedD.isVisible())
                normalizedD.setVisible(true);
        }
    }

    public void openImageDialog() {

        if (imageD != null) {
            if (!imageD.isVisible())
                imageD.setVisible(true);
        }
    }

    // edges are only invisible, not really hidden
    public void greyEdges() {

        edgeFillColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.FILLCOLOR, true);
        edgeStrokeColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.STROKECOLOR, true);
        draw.add(edgeFillColor);
        draw.add(edgeStrokeColor);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        m_vis.run("draw");
        isGreyEdge = true;
    }

    public void highlightGreyEdges(NodeItem centerNode) {

        Iterator allEdges1 = centerNode.edges();
        while (allEdges1.hasNext()) {
            EdgeItem e = (EdgeItem) allEdges1.next();
            String edgeId = e.getString("edgeKey");
            Color cl;
            cl = Color.decode(e.getString("edgeColor"));

            int red = cl.getRed();
            int green = cl.getGreen();
            int blue = cl.getBlue();

            edgeStrokeColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));
            edgeFillColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));

        }

        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        m_vis.run("draw");

    }


    // set edges visible
    public void brightEdges() {
        edgeFillColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.FILLCOLOR, false);
        edgeStrokeColor = new EdgeColorAction(edges, "edgeType", Constants.NOMINAL, VisualItem.STROKECOLOR, false);
        setNeighbourColor();
        draw.add(edgeFillColor);
        draw.add(edgeStrokeColor);

        //  draw.add(new RepaintAction());
        //  m_vis.putAction("draw", draw);

        //  m_vis.run("draw");

        //  isGreyEdge = false;
    }

    public void brightNodes() {
        textfill = new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        nodeColorAction = new NodeColorAction(nodes, datatype, Constants.NOMINAL, VisualItem.FILLCOLOR);

        draw.add(nodeColorAction);
        draw.add(textfill);

        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        m_vis.run("draw");
    }

    private long getRandomSeed() {

        return (long) (Math.random() * (display.getBounds().getWidth()));
    }


    public void fitGraph() {


        ActionList layout = new ActionList();

        layout.add(new ZoomToFit(graph));
        layout.add(new RepaintAction());
        m_vis.putAction("fitlayout", layout);
        m_vis.run("fitlayout");
        //  nodeDisStatus == 4 (imaged node); nodeDisStatus == 3 (resized node)
        if ((forZoom && forZoom1) || nodeDisStatus == 3 || nodeDisStatus == 4 || (dh.getLabelHiden().equals("1") && nodeDisStatus == 0)) {    // back to fit screen after zoom by link and/or zoom by graph

            rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
            forZoom = false;
            forZoom1 = false;

            ActionList layout1 = new ActionList();

            layout1.add(new ZoomToFit(graph));
            layout1.add(new RepaintAction());
            m_vis.putAction("fitlayout1", layout1);
            m_vis.run("fitlayout1");

        }

        if (forPan)
            forPan = false;

        if (labelNode) {
            rescaleNodesForLabel();

            ActionList layout2 = new ActionList();

            layout2.add(new ZoomToFit(graph));
            layout2.add(new RepaintAction());
            m_vis.putAction("fitlayout2", layout2);
            m_vis.run("fitlayout2");
        }

        fromzoomSelected = false;
        if (nodeDisStatus == 4) {    // back to fit screen after zoom by link and/or zoom by graph

            rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
            forZoom = false;
            forZoom1 = false;

            ActionList layout1 = new ActionList();

            layout1.add(new ZoomToFit(graph));
            layout1.add(new RepaintAction());
            m_vis.putAction("fitlayout1", layout1);
            m_vis.run("fitlayout1");

        }
    }

    private class ZoomToFit extends GroupAction {
        public ZoomToFit(String graphGroup) {
            super(graphGroup);
            if (forZoom && !forZoom1) {

                rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
                forZoom = false;
            }
            if (forPan)
                forPan = false;
        }

        public void run(double frac) {

            // animate reset zoom to fit the data (must run only AFTER layout)
            Rectangle2D bounds = m_vis.getBounds(Visualization.ALL_ITEMS);

            GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
            fitViewToBounds(display, bounds, null, 0);
        }
    }

    private static void fitViewToBounds(Display display, Rectangle2D bounds,
                                        Point2D center, long duration) {

        // init variables

        double cx = (center == null ? (bounds.getCenterX()) : center.getX());
        double cy = (center == null ? bounds.getCenterY() : center.getY());

        // compute half-widths of final bounding box around
        // the desired center point

        // compute scale factor
        //  - figure out if z or y dimension takes priority
        //  - then balance against the current scale factor

        double scale = 1.4 * 1 / display.getScale();

        // animate to new display settings
        if (center == null)
            center = new Point2D.Double(cx, cy);
        if (duration > 0) {
            display.animatePanAndZoomToAbs(center, scale, duration);
        } else {
            display.panToAbs(center);
            display.zoomAbs(center, scale);

        }

    }

    // popupMenu is not really used.

    public List<EdgeItem> getMultiEgdes(EdgeItem edge) {
        NodeItem node1 = edge.getSourceItem();

        NodeItem node2 = edge.getTargetItem();
        Iterator it1 = node1.edges();
        Iterator it2 = node2.edges();
        Set<EdgeItem> edges1 = new HashSet<EdgeItem>();
        Set<EdgeItem> edges2 = new HashSet<EdgeItem>();
        List<EdgeItem> edgesList = new ArrayList<EdgeItem>();

        while (it1.hasNext()) {
            EdgeItem e1 = (EdgeItem) it1.next();
            if (e1.isVisible())
                edges1.add(e1);
        }
        while (it2.hasNext()) {
            EdgeItem e2 = (EdgeItem) it2.next();
            if (e2.isVisible())
                edges2.add(e2);
        }

        edges1.retainAll(edges2);
        // ordering edges by alpha letter

        Iterator iter = edges1.iterator();
        String[] edgedis = new String[edges1.size()];
        int i = 0;
        while (iter.hasNext()) {
            EdgeItem e1 = (EdgeItem) iter.next();
            String edgeType = e1.getString("edgeTypeDis");
            edgedis[i] = edgeType;
            i++;
        }

        Arrays.sort(edgedis);


        for (int j = 0; j < edges1.size(); j++) {
            String disStr = edgedis[j];

            Iterator iter1 = edges1.iterator();

            while (iter1.hasNext()) {

                EdgeItem e1 = (EdgeItem) iter1.next();
                String edgeType = e1.getString("edgeTypeDis");

                if ((disStr.equalsIgnoreCase(edgeType)) && (!edgesList.contains(e1)))
                    edgesList.add(e1);
            }
        }
        return edgesList;
    }

    // create the popup menu for neighborhood thingies
    /*private JPopupMenu showImage(NodeItem node) {
       JPopupMenu popup = new JPopupMenu();

       JLabel fancyLabel = new JLabel();
    Image image = admin.getImage(admin.getCodeBase(), "images/vis/" + node.getString("login") + ".jpg");
    ImageIcon  ic = new ImageIcon(image);

          //  if(ic.getIconHeight() > 0){
             fancyLabel.setIcon(ic);

              popup.add(fancyLabel);
              popup.add(new JLabel(node.getString("disLabel")));
          //  }
       return popup;
   }
    */

    private JPopupMenu getNeighborPopup(NodeItem node) {

        JMenuItem menuItem;
        JPopupMenu popup = new JPopupMenu();
        NeighborPopupListener rtClickListener = new NeighborPopupListener(node, depthGroup, this);
        menuItem = new JMenuItem(rb.getString("DirectLinks"));
        menuItem.setName("DirectLinks");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);
        menuItem = new JMenuItem(rb.getString("Depth1"));
        menuItem.setName("Depth1");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);
        menuItem = new JMenuItem(rb.getString("Depth2"));
        menuItem.setName("Depth2");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);
        menuItem = new JMenuItem(rb.getString("Depth3"));
        menuItem.setName("Depth3");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);
        menuItem = new JMenuItem(rb.getString("Depth4"));
        menuItem.setName("Depth4");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);

        menuItem = new JMenuItem(rb.getString("Other") + "...");
        menuItem.setName("Other");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);
        popup.addSeparator();
        menuItem = new JMenuItem(rb.getString("ShowSelectedLink") + "...");
        menuItem.setName("ShowSelectedLink");
        menuItem.addActionListener(rtClickListener);
        popup.add(menuItem);

        return popup;
    }


    private class NeighborPopupListener implements ActionListener {

        NodeItem item;
        String depthEdge;
        GraphView view;

        NeighborPopupListener(NodeItem item, String groupName, GraphView view) {
            this.item = item;
            this.view = view;
            depthEdge = groupName;

        }

        public void actionPerformed(ActionEvent e) {

            m_vis.removeGroup(depthEdge);

            JMenuItem source = (JMenuItem) e.getSource();
            String s = source.getName();
            int depth = 0;
            int directLinks = 0;
            if (s.equals("Depth1")) {
                depth = 1;
            } else if (s.equals("Depth2")) {
                depth = 2;
            } else if (s.equals("Depth3")) {
                depth = 3;
            } else if (s.equals("Depth4")) {
                depth = 4;
            } else if (s.equals("Other")) {
                OtherDialog od = new OtherDialog(display);
                od.setVisible(true);
                if ((depth = od.getValue()) == -1)
                    return;


            } else if (s.equals("ShowSelectedLink")) {
                LinkSelection od = new LinkSelection(display, view, item, null);

                od.setVisible(true);
            } else if (s.equals("DirectLinks")) {
                depth = 1;
                directLinks = 1;
            }

            if (depth != 0) {

                try {

                    m_vis.addFocusGroup(depthEdge);
                } catch (Exception ee) {
                    System.out.println("Problems while adding foucs groups to visualization " + ee.getMessage());
                }


                edgeTupleSet = m_vis.getFocusGroup(depthEdge);

                BreadthFirstIterator m_bfs = new BreadthFirstIterator();

                m_bfs.init(item, depth, Constants.NODE_AND_EDGE_TRAVERSAL);

                TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
                focus.clear();
                try {
                    // traverse the graph
                    while (m_bfs.hasNext()) {
                        VisualItem vitem = (VisualItem) m_bfs.next();
                        if (vitem.getGroup().equalsIgnoreCase(nodes)) {

                            focus.addTuple(vitem);

                            if (directLinks != 1) {
                                Node nite = (Node) vitem;
                                Iterator it = (nite.edges());
                                while (it.hasNext()) {
                                    EdgeItem eitem = (EdgeItem) it.next();
                                    if (eitem.isVisible())
                                        edgeTupleSet.addTuple(eitem);
                                }
                            }
                        } else {
                            if (vitem.isVisible()) {
                                if (directLinks != 1) {
                                    edgeTupleSet.addTuple(vitem);

                                } else {

                                    Iterator it = item.edges();
                                    while (it.hasNext()) {
                                        EdgeItem eim = (EdgeItem) it.next();

                                        if (((EdgeItem) vitem).equals(eim))
                                            edgeTupleSet.addTuple(vitem);
                                        // else
                                        // System.out.println("not direct link edge:" + ((EdgeItem)vitem));

                                    }

                                }
                            }
                        }

                    }

                    // find all nodes taht links to a focus edge
                    Iterator edges = edgeTupleSet.tuples();
                    Set<NodeItem> nodes = new HashSet<NodeItem>();
                    while (edges.hasNext()) {
                        EdgeItem eim = (EdgeItem) edges.next();
                        nodes.add(eim.getTargetItem());
                        nodes.add(eim.getSourceItem());
                    }
                    Set<NodeItem> removeNode = new HashSet<NodeItem>();
                    Iterator focusNodes = focus.tuples();

                    while (focusNodes.hasNext()) {
                        NodeItem nitem = (NodeItem) focusNodes.next();
                        if (!(nodes.contains(nitem))) {
                            removeNode.add(nitem);

                        }
                    }

                    for (NodeItem ni : removeNode) {
                        focus.removeTuple(ni);
                    }

                } catch (Exception ex) {

                }
            }
        }
    }

    public void resetFocus() {
        TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
        focus.clear();

        for (Node item : multiNode) {
            focus.addTuple(item);
        }

        m_vis.removeGroup(depthGroup);

    }

    public void multiNodeLink() {
        TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);

        LinkSelection od = new LinkSelection(display, this, null, focus);

        od.setVisible(true);
    }

    public void multiNodeDepth(int depth) {
        multiNode = new HashSet();
        TupleSet edgeTupleSet;
        m_vis.removeGroup(depthGroup);

        try {

            m_vis.addFocusGroup(depthGroup);
        } catch (Exception ee) {
            System.out.println("Problems while adding foucs groups to visualization " + ee.getMessage());
        }


        edgeTupleSet = m_vis.getFocusGroup(depthGroup);

        BreadthFirstIterator m_bfs = new BreadthFirstIterator();

        TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);

        if (focus.getTupleCount() > 1) {

            Iterator itfoc = focus.tuples();
            while (itfoc.hasNext()) {
                multiNode.add((Node) itfoc.next());
            }
            focus.clear();
            for (Node node : multiNode) {

                m_bfs.init(node, depth, Constants.NODE_AND_EDGE_TRAVERSAL);

                // traverse the graph
                while (m_bfs.hasNext()) {
                    VisualItem item = (VisualItem) m_bfs.next();
                    if (item.getGroup().equalsIgnoreCase(nodes)) {
                        focus.addTuple((NodeItem) item);

                    } else {
                        if (item.isVisible())
                            edgeTupleSet.addTuple(item);

                    }

                }//while

            }//for
            // find all nodes that links to a focus edge
            Iterator edges = edgeTupleSet.tuples();
            Set<NodeItem> nodes = new HashSet<NodeItem>();
            while (edges.hasNext()) {
                EdgeItem eim = (EdgeItem) edges.next();
                nodes.add(eim.getTargetItem());
                nodes.add(eim.getSourceItem());
            }
            Set<NodeItem> removeNode = new HashSet<NodeItem>();
            Iterator focusNodes = focus.tuples();

            while (focusNodes.hasNext()) {
                NodeItem nitem = (NodeItem) focusNodes.next();
                if (!(nodes.contains(nitem))) {
                    removeNode.add(nitem);

                }
            }

            for (NodeItem ni : removeNode) {
                focus.removeTuple(ni);
            }


        }
        multiNodeDepth = true;

    }

    private JPopupMenu getRtPopup(EdgeItem edge) {

        JPopupMenu rtPopup = new JPopupMenu();

        List<EdgeItem> es = getMultiEgdes(edge);


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
        JLabel blank = new JLabel(Integer.toString(size) + " " + rb.getString("edgesBetween") + "  ", JLabel.CENTER);

        //  blank.setForeground(Color.red);
        blank.setHorizontalTextPosition(JLabel.CENTER);
        JLabel tolabel = new JLabel(toLabel, JLabel.CENTER);

        tolabel.setHorizontalTextPosition(JLabel.CENTER);
        tolabel.setOpaque(true);
        tolabel.setBackground(ColorLib.getColor(toColor));
        JLabel and = new JLabel("  " + rb.getString("and") + "  ");
        JPanel titlePanel = new JPanel();

        titlePanel.setBackground(Color.white);
        titlePanel.add(blank);
        titlePanel.add(fromlabel);

        titlePanel.add(and);
        titlePanel.add(tolabel);
        rtPopup.add(titlePanel);


        Iterator it = es.iterator();

        while (it.hasNext()) {

            String toolString = "<html><body>";
            EdgeItem e = (EdgeItem) it.next();
            RtClickListener rtClickListener = new RtClickListener(e);
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
            JMenuItem menuItem;
            menuItem = new JMenuItem(toolString, JMenuItem.CENTER);
            menuItem.setBackground(Color.white);
            menuItem.setHorizontalTextPosition(JMenuItem.CENTER);

            menuItem.setName(e.getString("nodePairType"));

            menuItem.addActionListener(rtClickListener);


            rtPopup.add(menuItem);

        }

        //  JScrollPane scp = new JScrollPane(rtPopup);

        // scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return rtPopup;
    }

    private class RtClickListener implements ActionListener {

        EdgeItem clickededge;

        RtClickListener(EdgeItem e) {
            clickededge = e;
        }

        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            String s = source.getName();

            String l_prefix = dh.getLinkCGIPrefix();

            String[] logins = dh.getLogins();

            int from = Integer.parseInt(s.substring(0, s.indexOf('-')));
            int to = Integer.parseInt(s.substring(1 + s.indexOf('-'), s.indexOf('&')));
            String type = s.substring(s.indexOf('&') + 1);
            boolean localInfo = false;
            try {

                if (admin.isApplet && admin.getParameter("graphML") != null) {

                    localInfo = true;
                } else if (admin.isGraphML || reader.getParaMap().get("graphML") != null) {
                    localInfo = true;
                }
            } catch (Exception ex) {

            }

            if (localInfo) {
                EdgeInfoDialog edgeInfoD = new EdgeInfoDialog(clickededge, admin, reader, dh);
                edgeInfoD.setVisible(true);
            } else if (l_prefix != null) {

                try {
                    admin.getAppletContext().showDocument(new URL(l_prefix + "from=" + URLEncoder.encode(logins[from - 1], "utf-8") + "&to=" + URLEncoder.encode(logins[to - 1], "utf-8") + "&type=" + URLEncoder.encode(type, "utf-8")), "browser");
                }

                catch (MalformedURLException ex) {
                    System.out.println(ex);
                }
                catch (UnsupportedEncodingException uex) {
                    System.out.println(uex);
                } catch (Exception uex) {
                    System.out.println(uex);
                }
            }//if

        }

    }

    public void hideSelectedEdges(Set<EdgeItem> choices) {


        for (EdgeItem item : choices) {
            String key = item.getString("edgeKey");
            item.setVisible(false);
            PrefuseLib.updateVisible(item, false);
            edgeKeys.add(key);


        }
        ActionList hidden = new ActionList();

        hidden.add(new RepaintAction());
        m_vis.putAction("hidden", hidden);

        m_vis.run("hidden");
        toInit = false;
        showOrHide = true;
        g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
        visualDataChanged = true;
        nodeActionType = 1;
        resetCount();
        legend1.currentLegend();
        legend1.removeAll();
        legend1.repaint();

    }

    private class PopupControl extends ControlAdapter {
        int button = Control.RIGHT_MOUSE_BUTTON;
        GraphView gv;
        LayerBorder lb;

        public PopupControl(GraphView gv) {
            this.gv = gv;

        }

        private void showAllIndirect(){
             Set<NodeItem> recSet = new HashSet<NodeItem>();
                    TupleSet allTuple = m_vis.getVisualGroup(nodes);
                    Iterator allIems = allTuple.tuples();

                     while (allIems.hasNext()) {
                        NodeItem vitem = (NodeItem) allIems.next();
                        if (vitem.getString("nodeType").equals("Recommendation")) {
                            if (!fromAll) {

                                showIndiect = true;

                               recSet.add(vitem);
                             showDirectGraph();

                            }
                        }
                    }

                    showIndirectGraph(null, true, recSet);
        }
        public void itemEntered(VisualItem item, MouseEvent e) {
            Cursor cursor = display.getCursor();
            if (cursor.getType() == Cursor.DEFAULT_CURSOR) {

                if (item instanceof NodeItem && item.getString("nodeType").equals("Requester") && layoutNumber == 6) {

                   showAllIndirect();

                    }



                    if (item instanceof NodeItem && item.getString("nodeType").equals("Recommendation") && layoutNumber == 6 && recStatus == 2) {
                        boolean hasIndirect = true;

                        Iterator edges = ((NodeItem) item).edges();

                        for (int j = 0; edges.hasNext(); j++) {
                            EdgeItem edge = (EdgeItem) edges.next();
                            try {
                                if (edge.getString("invisibleItem").equalsIgnoreCase("0")) {
                                    hasIndirect = false;
                                    break;
                                }
                            } catch (Exception ex) {

                            }
                        }

                        if (hasIndirect) {

                            //  TupleSet ts =m_vis.getGroup(Visualization.FOCUS_ITEMS);
                            // if(ts.getTupleCount() == 0)
                            if (!fromAll) {
                                showDirectGraph();
                                showIndirectGraph((NodeItem) item, null);
                                showIndiect = true;

                            }
                            double se = Double.parseDouble(item.getString("recSe"));

                            if (se < 0.5) {
                                clickedRecNodes.clear();
                                clickedRecNodes.add((NodeItem) item);
                            }
                            if (hoveredNodes != null) {
                                hoveredNodes.clear();
                                hoveredNodes.add((NodeItem) item);
                            }
                        }
                    }

                }
            }


            public void itemClicked
            (VisualItem
            item, MouseEvent
            e){

            Cursor cursor = display.getCursor();
            if (cursor.getType() == Cursor.DEFAULT_CURSOR) {
                if (e.getClickCount() == 1 && item.getGroup().equalsIgnoreCase(edges)) {

                    EdgeItem edge = (EdgeItem) item;

                    Display display = (Display) e.getComponent();
                    if (!display.isTranformInProgress()) {
                        if (UILib.isButtonPressed(e, button))
                            new HiddenEdgePopup(getMultiEgdes(edge), showWeight, edge, gv).show(display, e.getX(), e.getY());
                        else
                            getRtPopup(edge).show(display, e.getX(), e.getY());
                    }


                }
            }

            if (item instanceof NodeItem && item.getString("nodeType").equals("Recommendation") && layoutNumber == 6) {
                boolean hasIndirect = true;

                Iterator edges = ((NodeItem) item).edges();

                for (int j = 0; edges.hasNext(); j++) {
                    EdgeItem edge = (EdgeItem) edges.next();
                    try {
                        if (edge.getString("invisibleItem").equalsIgnoreCase("0")) {
                            hasIndirect = false;
                            break;
                        }
                    } catch (Exception ex) {

                    }
                }
                if (hasIndirect && (e.getClickCount() == 1 && !UILib.isButtonPressed(e, button))) {
                    if (!fromAll) {
                        showDirectGraph();
                        showIndirectGraph(null, false, null);

                        showIndiect = true;
                    }
                }
            }


            if (e.getClickCount() == 1 && UILib.isButtonPressed(e, button)) {
                TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
                if (focus.getTupleCount() == 1) {
                    if (item.getGroup().equalsIgnoreCase(nodes)) {
                        NodeItem node = (NodeItem) item;
                        Display display = (Display) e.getComponent();
                        if (!display.isTranformInProgress()) {
                            getNeighborPopup(node).show(display, e.getX(), e.getY());
                        }
                    }
                } else if (focus.getTupleCount() > 1 && focus.containsTuple((Tuple) item)) {

                    multiNodeLink();
                }
            }
        }

            public void mouseMoved
            (MouseEvent
            e){
            if (recStatus == 2) {
                for (NodeItem item : lines.keySet()) {

                    Point2D p = e.getPoint();
                    display.getAbsoluteCoordinate(p, p);
                    double xp = p.getX();
                    double yp = p.getY();
                    //  Line2D line = lines.get(item);

                    double x1 = lines.get(item).getX1();
                    double x2 = lines.get(item).getX2();
                    double y1 = lines.get(item).getY1();
                    double y2 = lines.get(item).getY2();

                    double maxX = Math.max(x1, x2);
                    double minX = Math.min(x1, x2);
                    double maxY = Math.max(y1, y2);
                    double minY = Math.min(y1, y2);


                    double k1 = (y1 - y2) / (x1 - x2);
                    double k2 = (x1 - x2) / (y1 - y2);
                    double dis;

                    double disTo1 = Math.sqrt((y1 - yp) * (y1 - yp) + (x1 - xp) * (x1 - xp));
                    double disTo2 = Math.sqrt((y2 - yp) * (y2 - yp) + (x2 - xp) * (x2 - xp));

                    if (k1 != 0 && k2 != 0) {
                        double intersectX = (xp * k2 + x1 * k1 + yp - y1) / (k1 + k2);
                        double intersectY = k1 * (intersectX - x1) + y1;
                        dis = Math.sqrt((intersectY - yp) * (intersectY - yp) + (intersectX - xp) * (intersectX - xp));
                        if (xp < maxX && xp > minX && yp < maxY && yp > minY) {    // mouse point noy out of line fregment
                            if (dis < 1.00 && disTo1 > 5 && disTo2 > 5) {

                                showFakedirectLink(item);
                            }
                        }
                    } else if (k1 == 0) {
                        dis = Math.abs(yp - y1);
                        if (xp < maxX && xp > minX) {    // mouse point noy out of line fregment
                            if (dis < 1.00 && disTo1 > 5 && disTo2 > 5) {

                                showFakedirectLink(item);
                            }

                        }
                    } else if (k2 == 0) {
                        dis = Math.abs(xp - x1);
                        if (yp < maxY && yp > minY) {    // mouse point noy out of line fregment
                            if (dis < 1.00 && disTo1 > 5 && disTo2 > 5) {

                                showFakedirectLink(item);
                            }

                        }
                    }
                }

            }
        }
        }

        private void hiddenItemBusiness(Iterator items, TupleSet hideEdges) {
            Set<Node> hiddenNodes = new HashSet<Node>();

            while (items.hasNext()) {
                VisualItem item = (VisualItem) items.next();
                if (!(item.getGroup().equalsIgnoreCase(AGGR))) {

                    double[] xy = {item.getX(), item.getY()};

                    nodeXYMap.put(item.getString("login"), xy);

                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);
                    // node item

                    Node node = (Node) item;

                    String nodeRow = node.getString("nodeKey");
                    nodeKeys.add(nodeRow);

                    hiddenNodes.add(node);

                }
            }


            if (hideEdges != null) {


                Iterator edges = hideEdges.tuples();
                while (edges.hasNext()) {

                    VisualItem item = (VisualItem) edges.next();


                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);
                    edgeKeys.add(item.getString("edgeKey"));

                }
            }
            hiddenNode(hiddenNodes);
        }


        public void hideIsolate() {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }
            TupleSet allTuple = m_vis.getVisualGroup(nodes);
            Iterator allIems = allTuple.tuples();
            while (allIems.hasNext()) {
                VisualItem item = (VisualItem) allIems.next();

                NodeItem node = (NodeItem) item;
                String nodeRow = node.getString("nodeKey");
                boolean hidden = true;

                Iterator it = node.edges();

                while (it.hasNext()) {
                    EdgeItem edge = (EdgeItem) it.next();

                    if (edge.isVisible()) {

                        hidden = false;

                        break;

                    }
                }
                if (hidden) {

                    nodeKeys.add(nodeRow);
                    double[] xy = {item.getX(), item.getY()};

                    nodeXYMap.put(item.getString("login"), xy);
                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);

                }

            }

            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");

            toInit = false;
            showOrHide = true;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);

            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }

        public void showLink(Set<String> choices, NodeItem item, TupleSet focus) {
            if (focus == null) {
                singleNodelinkType(choices, item);
            } else {
                multiNodelinkType(choices, focus);
            }
            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");

        }

        private void multiNodelinkType(Set<String> choices, TupleSet focus) {

            Iterator items = focus.tuples();
            Set<NodeItem> nodeItems = new HashSet<NodeItem>();
            while (items.hasNext()) {
                nodeItems.add((NodeItem) items.next());
            }

            invisibleEdges = new HashSet<EdgeItem>();

            Iterator items1 = focus.tuples();
            while (items1.hasNext()) {

                NodeItem item = (NodeItem) items1.next();
                Iterator edgeIt = item.edges();
                if (choices.contains("All links")) {
                    while (edgeIt.hasNext()) {
                        EdgeItem edge = (EdgeItem) edgeIt.next();
                        if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                            edge.setVisible(true);
                            PrefuseLib.updateVisible(edge, true);
                        }
                    }
                } else {

                    while (edgeIt.hasNext()) {

                        EdgeItem edge = (EdgeItem) edgeIt.next();
                        if (nodeItems.contains(edge.getSourceItem()) && nodeItems.contains(edge.getTargetItem())) {
                            edge.setVisible(false);
                            PrefuseLib.updateVisible(edge, false);

                            if (edge.getString("direction").equalsIgnoreCase("2")) {  // mutual link
                                if (choices.contains("Mutual link")) {

                                    if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                                        edge.setVisible(true);
                                        PrefuseLib.updateVisible(edge, true);
                                    }
                                }

                            } else if (edge.getString("direction").equalsIgnoreCase("0")) {   // undirect link
                                if (choices.contains("Undirect link")) {
                                    if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                                        edge.setVisible(true);

                                        PrefuseLib.updateVisible(edge, true);
                                    }
                                }
                            }
                        }
                        if (!edge.isVisible()) {

                            invisibleEdges.add(edge);
                        }
                    }// while
                }
            }
        }

        private void singleNodelinkType(Set<String> choices, NodeItem item) {
            Iterator edgeIt = item.edges();
            if (choices.contains("All links")) {
                while (edgeIt.hasNext()) {
                    EdgeItem edge = (EdgeItem) edgeIt.next();
                    if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                        edge.setVisible(true);
                        PrefuseLib.updateVisible(edge, true);
                    }
                }
            } else {

                invisibleEdges = new HashSet<EdgeItem>();
                while (edgeIt.hasNext()) {

                    EdgeItem edge = (EdgeItem) edgeIt.next();
                    edge.setVisible(false);
                    PrefuseLib.updateVisible(edge, false);

                    if (edge.getString("direction").equalsIgnoreCase("2")) {  // mutual link
                        if (choices.contains("Mutual link")) {
                            if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                                edge.setVisible(true);
                                PrefuseLib.updateVisible(edge, true);
                            }
                        }

                    } else if (edge.getString("direction").equalsIgnoreCase("0")) {   // undirect link
                        if (choices.contains("Undirect link")) {
                            if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                                edge.setVisible(true);

                                PrefuseLib.updateVisible(edge, true);
                            }
                        }

                    } else if (edge.getString("direction").equalsIgnoreCase("1")) {
                        if (edge.getSourceItem() == item) {   // outgoing
                            if (choices.contains("Outgoing link")) {
                                if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                                    edge.setVisible(true);
                                    PrefuseLib.updateVisible(edge, true);
                                }
                            }
                        } else {                    // incoming
                            if (choices.contains("Incoming link")) {
                                if (edge.getSourceItem().isVisible() && edge.getTargetItem().isVisible()) {
                                    edge.setVisible(true);
                                    PrefuseLib.updateVisible(edge, true);
                                }
                            }
                        }
                    }

                    if (!edge.isVisible()) {

                        invisibleEdges.add(edge);
                    }
                }// while

            }
        }

        public void hideSelection
                () {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }
            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);

            Iterator items = focus.tuples();


            hiddenItemBusiness(items, edgeTupleSet);


            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;

            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);

            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();


        }


        public void showAllNodes
                () {

            Map<String, double[]> nodeXYMap = getXyCoordinate();


            for (Iterator iter = vg.nodes(); iter.hasNext();) {

                NodeItem node = (NodeItem) iter.next();

                if (node.isVisible()) {
                    double[] xy = {node.getX(), node.getY()};
                    nodeXYMap.put(node.getString("login"), xy);
                }

            }

            TupleSet allTuple = m_vis.getVisualGroup(graph);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                item.setVisible(true);
                PrefuseLib.updateVisible(item, true);

            }

            toInit = true;
            showOrHide = false;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, null, null, nodeColorMap, edgeColorMap, datatype);

            if (changedfromLayout || layoutNumber == 5) {

                visualDataChanged = true;
                vg = setGraph(g);


                resetSearchPanel();
                dataInit = true;
                layoutCustom(nodeXYMap);
                if (showWeight) {
                    showEdgeStrength();
                }

                nodeActionType = 2;
                changedfromLayout = false;

                legend1.currentLegend();
                legend1.removeAll();
                legend1.repaint();

                //  layoutRestore();
            } else {
                //// node only invisible
                visualDataChanged = false;
            }

            resetCount();
            fitGraph();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
        }


        private void setNo_hiddenRecEdges() {

            WhyDialog wd = new WhyDialog(this, dh, m_vis);
            ur_pathsMap = wd.getUr_pathsMap();
            rt_pathsMap = wd.getRt_pathsMap();

        }

        // for recommendation: keep no_hidden edges
        public Map<Integer, List<String>> getUr_pathsMap() {
            return ur_pathsMap;
        }

        public Map<Integer, List<String>> getRt_pathsMap() {
            return rt_pathsMap;
        }


        public void showAllGraph
                () {

            if (recStatus != 2 || (recStatus == 2 && clickedRecNodes.size() > 0)) {
                if (recStatus == 1)
                    setNo_hiddenRecEdges();

                nodeKeys.clear();
                edgeKeys.clear();
                toInit = false;
                showOrHide = false;
                Map<String, double[]> nodeXYMap = null;
                if (recStatus == 2 && clickedRecNodes.size() > 0) {
                    nodeXYMap = getXyCoordinate();


                    for (Iterator iter = vg.nodes(); iter.hasNext();) {

                        NodeItem node = (NodeItem) iter.next();

                        if (node.isVisible()) {
                            double[] xy = {node.getX(), node.getY()};
                            nodeXYMap.put(node.getString("login"), xy);
                        }

                    }

                }
                g = dh.setGraphPara(toInit, showOrHide, fromInit, null, null, nodeColorMap, edgeColorMap, datatype);
                visualDataChanged = true;

                vg = setGraph(g);

                resetSearchPanel();


                dataInit = true;

                if (layoutNumber == 1) {
                    layoutCircle();
                } else if (layoutNumber == 2) {
                    layoutRandom();
                } else if (layoutNumber == 3) {
                    VisualItem f = (VisualItem) vg.getNode(0);
                    m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
                    f.setFixed(false);
                    layoutTree();
                } else if (layoutNumber == 4) {
                    layoutAuto();
                } else if (layoutNumber == 5) {
                    layoutGroup(false);
                } else if (layoutNumber == 6) {
                    if (recStatus == 2) {
                        forShowAll = true;
                        layoutRec();
                        if (clickedRecNodes.size() > 1) {

                            showIndirectGraph(nodeXYMap, false, null);

                        } else if (clickedRecNodes.size() == 1) {
                            for (NodeItem nodeitem : clickedRecNodes) {

                                showIndirectGraph(nodeitem, nodeXYMap);
                            }

                        }
                        //   forShowAll = false;
                        forShowAll = false;
                        fromAll = true;


                    } else
                        layoutRec();

                } else if (layoutNumber == 7) {

                    layoutCustom(null);
                    rescaleNodes(1.0, false, 0, 0, fromzoomSelected);
                }
                toInit = false;

                fromInit = false;
                showAllRec = true;
                legend1.currentLegend();
                legend1.removeAll();
                legend1.repaint();
                resetCount();
            }
        }

        public void indirectlayout(NodeItem node, Map<String, double[]> nodeXYMap) {
            ActionList layout = new ActionList();

            IndirectRecLayerLayout irl = new IndirectRecLayerLayout(nodes, node, forShowAll, nodeXYMap);
            layout.add(irl);

            layout.add(new RepaintAction());
            //   layout.add(new ZoomToFit(graph));

            m_vis.putAction("reclayout", layout);


            m_vis.run("reclayout");
            // fitGraph();
        }


        public void indirectlayout(TupleSet focus, Map<String, double[]> nodeXYMap, boolean fromRequestor, Set<NodeItem> recSet) {

            ActionList layout = new ActionList();

            IndirectRecLayerLayout irl = new IndirectRecLayerLayout(nodes, focus, forShowAll, nodeXYMap, fromRequestor, recSet);
            layout.add(irl);

            layout.add(new RepaintAction());
            //  layout.add(new ZoomToFit(graph));

            m_vis.putAction("reclayout", layout);


            m_vis.run("reclayout");
            // fitGraph();
        }

        public void showIndirectGraph(NodeItem nodeItem, Map<String, double[]> nodeXYMap) {

            indirectlayout(nodeItem, nodeXYMap);
            String itemLogin = nodeItem.getString("login");

            nodeColorAction.add("login =='" + itemLogin + "'", ColorLib.rgb(0, 255, 0));
            textfill.add("login =='" + itemLogin + "'", ColorLib.rgb(0, 0, 0));

            nodeItem.setVisible(true);
            PrefuseLib.updateVisible(nodeItem, true);

            Iterator invisibleNodes0 = nodeItem.neighbors();
            boolean hasnext = true;
            while (invisibleNodes0.hasNext()) {

                NodeItem invisibleNode = (NodeItem) invisibleNodes0.next();
                Iterator nexeStepNodes = invisibleNode.neighbors();
                while (nexeStepNodes.hasNext()) {

                    NodeItem n = (NodeItem) nexeStepNodes.next();
                    if (n.getString("nodeType").equals("Requester")) {
                        hasnext = false;
                        break;
                    }
                }
                if (!hasnext)
                    break;
            }
            Iterator invisibleNodes1 = nodeItem.neighbors();
            while (invisibleNodes1.hasNext()) {
                try {
                    NodeItem invisibleNode = (NodeItem) invisibleNodes1.next();

                    if (!invisibleNode.getString("nodeType").equals("Requester")) {

                           boolean showEdge= false;
                        Iterator it1 = invisibleNode.edges();
                        while (it1.hasNext()) {
                            EdgeItem edgeItem = (EdgeItem) it1.next();

                             if ((edgeItem.getSourceItem().getString("nodeType").equals("Requester") && edgeItem.getTargetItem().getString("nodeType").equals("Recommendation")) ||
                                    (edgeItem.getTargetItem().getString("nodeType").equals("Requester") && edgeItem.getSourceItem().getString("nodeType").equals("Recommendation"))) {

                                String edgeId = edgeItem.getString("edgeKey");
                                Color cl;
                                cl = Color.decode(edgeItem.getString("edgeColor"));

                                int red = cl.getRed();
                                int green = cl.getGreen();
                                int blue = cl.getBlue();

                                edgeStrokeColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));
                                edgeFillColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));


                                String nodeLogin = invisibleNode.getString("login");
              
                                nodeColorAction.add("login =='" + nodeLogin + "'", ColorLib.rgb(0, 255, 0));
                                textfill.add("login =='" + nodeLogin + "'", ColorLib.rgb(0, 0, 0));
                               showEdge = true;
                            }

                            if ((edgeItem.getSourceItem().equals(invisibleNode) && (edgeItem.getTargetItem().getString("nodeType").equals("Requester") || edgeItem.getTargetItem().equals(nodeItem))) ||
                                    (edgeItem.getTargetItem().equals(invisibleNode) && (edgeItem.getSourceItem().getString("nodeType").equals("Requester") || edgeItem.getSourceItem().equals(nodeItem)))) {

                                invisibleNode.setVisible(true);
                                PrefuseLib.updateVisible(invisibleNode, true);

                                    edgeItem.setVisible(true);
                                PrefuseLib.updateVisible(edgeItem, true);


                            } else {
                                edgeItem.setVisible(false);
                                PrefuseLib.updateVisible(edgeItem, false);
                            }



                        }

                         if(!showEdge &&  invisibleNode.getString("nodeType").equals("Recommendation") && !hasnext){

                                       Iterator it2 = invisibleNode.edges();
                                                      while (it2.hasNext()) {
                                                          EdgeItem edgeItem2 = (EdgeItem) it2.next();

                                                          if( (edgeItem2.getSourceItem().equals(invisibleNode) && edgeItem2.getTargetItem().equals(nodeItem))||
                                                             (edgeItem2.getSourceItem().equals(nodeItem) && edgeItem2.getTargetItem().equals(invisibleNode))){

                                                              edgeItem2.setVisible(false);
                                                            PrefuseLib.updateVisible(edgeItem2, false);
                                                          }

                                                }
                        }


                        if (hasnext)
                            processnextIndirect(invisibleNode);
                    }
                } catch (Exception e) {

                }

            }


            TupleSet allTuple = m_vis.getVisualGroup(nodes);


            Iterator allitems = allTuple.tuples();

            while (allitems.hasNext()) {
                try {
                    NodeItem item = (NodeItem) allitems.next();
                    if (item.getString("invisibleItem").equalsIgnoreCase("0") && !item.getString("nodeType").equals("Requester")) {
                        String itemLogin1 = item.getString("login");

                        nodeColorAction.add("login =='" + itemLogin1 + "'", ColorLib.gray(230));
                       textfill.add("login =='" + itemLogin1 + "'", ColorLib.gray(230));
                    }
                } catch (Exception e) {

                }
            }


            TupleSet allTuple1 = m_vis.getVisualGroup(edges);


            Iterator allitems1 = allTuple1.tuples();

            while (allitems1.hasNext()) {
                try {
                    EdgeItem item = (EdgeItem) allitems1.next();

                    if (item.getString("invisibleItem").equalsIgnoreCase("0")) {

                        String itemKey = item.getString("edgeKey");

                        edgeFillColor.add("edgeKey =='" + itemKey + "'", ColorLib.gray(230));
                        edgeStrokeColor.add("edgeKey =='" + itemKey + "'", ColorLib.gray(230));

                    } else if (item.getString("invisibleItem").equals("2")) {

                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);
                    }
                } catch (Exception e) {

                }
            }

            draw.add(new RepaintAction());
            m_vis.putAction("hidden", draw);

            m_vis.run("hidden");
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
            fromIndirect = true;

        }

        private void showFakedirectLink(NodeItem nodeItem) {
            Iterator it = nodeItem.edges();

            while (it.hasNext()) {

                EdgeItem item = (EdgeItem) it.next();
                if ((item.getTargetItem().getString("nodeType").equals("Requester")) || (item.getSourceItem().getString("nodeType").equals("Requester"))) {

                    item.setVisible(true);
                    PrefuseLib.updateVisible(item, true);

                    String edgeId = item.getString("edgeKey");
                    Color cl;
                    cl = Color.decode(item.getString("edgeColor"));

                    int red = cl.getRed();
                    int green = cl.getGreen();
                    int blue = cl.getBlue();

                    edgeStrokeColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));
                    edgeFillColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));

                }

                draw.add(new RepaintAction());
                m_vis.putAction("hidden", draw);

                m_vis.run("hidden");
                resetCount();
                legend1.currentLegend();
                legend1.removeAll();
                legend1.repaint();

            }
            fromIndirect = true;
        }

        private void processnextIndirect(NodeItem nodeItem) {

            Iterator invisibleNodes1 = nodeItem.neighbors();
            while (invisibleNodes1.hasNext()) {
                try {
                    NodeItem invisibleNode = (NodeItem) invisibleNodes1.next();
                    Iterator it0 = invisibleNode.edges();
                    boolean pathToRequestor = false;
                    while (it0.hasNext()) {
                        EdgeItem edgeItem = (EdgeItem) it0.next();
                        if ((edgeItem.getTargetItem().getString("nodeType").equals("Requester")) || (edgeItem.getSourceItem().getString("nodeType").equals("Requester")))
                            pathToRequestor = true;

                    }
                    if (pathToRequestor) {
                        Iterator it1 = invisibleNode.edges();
                        while (it1.hasNext()) {
                            EdgeItem edgeItem = (EdgeItem) it1.next();

                            if ((edgeItem.getSourceItem().equals(invisibleNode) && (edgeItem.getTargetItem().getString("nodeType").equals("Requester") || edgeItem.getTargetItem().equals(nodeItem))) ||
                                    (edgeItem.getTargetItem().equals(invisibleNode) && (edgeItem.getSourceItem().getString("nodeType").equals("Requester") || edgeItem.getSourceItem().equals(nodeItem)))) {

                                if(nodeItem.getString("nodeType").equals("Recommendation")){
                                String nodeId = nodeItem.getString("login");

                                nodeColorAction.add("login =='" + nodeId + "'", ColorLib.rgb(0, 255, 0));
                                textfill.add("login =='" + nodeId + "'", ColorLib.rgb(0, 0, 0));
                            }

                                invisibleNode.setVisible(true);
                                PrefuseLib.updateVisible(invisibleNode, true);
                                edgeItem.setVisible(true);
                                PrefuseLib.updateVisible(edgeItem, true);
                            }

                            if ((edgeItem.getSourceItem().getString("nodeType").equals("Requester") && edgeItem.getTargetItem().getString("nodeType").equals("Recommendation")) ||
                                    (edgeItem.getTargetItem().getString("nodeType").equals("Requester") && edgeItem.getSourceItem().getString("nodeType").equals("Recommendation"))) {

                                String edgeId = edgeItem.getString("edgeKey");
                                Color cl;
                                cl = Color.decode(edgeItem.getString("edgeColor"));

                                int red = cl.getRed();
                                int green = cl.getGreen();
                                int blue = cl.getBlue();

                                edgeStrokeColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));
                                edgeFillColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));


                                String nodeLogin = invisibleNode.getString("login");

                                nodeColorAction.add("login =='" + nodeLogin + "'", ColorLib.rgb(0, 255, 0));
                                textfill.add("login =='" + nodeLogin + "'", ColorLib.rgb(0, 0, 0));

                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Exception when processing 3 steps recommendation: " + e);
                }

            }
        }

        public Set<NodeItem> getHoveredNodes() {
            return hoveredNodes;
        }


    private void dealShowIndirect(NodeItem item){


                    double se = Double.parseDouble(item.getString("recSe"));
                    if (se < 0.5)
                        clickedRecNodes.add(item);
                    String itemLogin = item.getString("login");

                    nodeColorAction.add("login =='" + itemLogin + "'", ColorLib.rgb(0, 255, 0));
                    textfill.add("login =='" + itemLogin + "'", ColorLib.rgb(0, 0, 0));

                    item.setVisible(true);
                    PrefuseLib.updateVisible(item, true);

                    Iterator invisibleNodes = item.neighbors();
                    while (invisibleNodes.hasNext()) {
                        try {
                            NodeItem invisibleNode = (NodeItem) invisibleNodes.next();
                            if (!invisibleNode.getString("nodeType").equals("Requester")) {
                                //  if (invisibleNode.getString("invisibleItem").equalsIgnoreCase("1")) {
                                if (item.getDouble("recSe") < 0.4)
                                    processnextIndirect(invisibleNode);

                                invisibleNode.setVisible(true);
                                PrefuseLib.updateVisible(invisibleNode, true);

                                Iterator it1 = invisibleNode.edges();
                                while (it1.hasNext()) {
                                    EdgeItem edgeItem = (EdgeItem) it1.next();


                                    if ((edgeItem.getSourceItem().equals(invisibleNode) && (edgeItem.getTargetItem().getString("nodeType").equals("Requester") || edgeItem.getTargetItem().equals(item))) ||
                                            (edgeItem.getTargetItem().equals(invisibleNode) && (edgeItem.getSourceItem().getString("nodeType").equals("Requester") || edgeItem.getSourceItem().equals(item)))) {

                                        edgeItem.setVisible(true);
                                        PrefuseLib.updateVisible(edgeItem, true);
                                    }

                                    if ((edgeItem.getSourceItem().getString("nodeType").equals("Requester") && edgeItem.getTargetItem().getString("nodeType").equals("Recommendation")) ||
                                            (edgeItem.getTargetItem().getString("nodeType").equals("Requester") && edgeItem.getSourceItem().getString("nodeType").equals("Recommendation"))) {

                                        String edgeId = edgeItem.getString("edgeKey");
                                        Color cl;
                                        cl = Color.decode(edgeItem.getString("edgeColor"));

                                        int red = cl.getRed();
                                        int green = cl.getGreen();
                                        int blue = cl.getBlue();

                                        edgeStrokeColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));
                                        edgeFillColor.add("edgeKey =='" + edgeId + "'", ColorLib.rgb(red, green, blue));


                                        String nodeLogin = invisibleNode.getString("login");

                                        nodeColorAction.add("login =='" + nodeLogin + "'", ColorLib.rgb(0, 255, 0));
                                        textfill.add("login =='" + nodeLogin + "'", ColorLib.rgb(0, 0, 0));

                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                        // }
                    }


                    hoveredNodes.add(item);
    }
        public void showIndirectGraph(Map<String, double[]> nodeXYMap, boolean fromRequestor, Set<NodeItem> recSet) {

            clickedRecNodes = new HashSet<NodeItem>();
            clickedRecNodes.clear();
            boolean hasIndirect = false;

          if(recSet != null){

              for(NodeItem item: recSet){

                  Iterator invisibleNodes = item.neighbors();
                while (invisibleNodes.hasNext()) {
                    NodeItem invisibleNode = (NodeItem) invisibleNodes.next();

                //    if (invisibleNode.getDouble("recScore") == 0.0 && invisibleNode.getDouble("recId") != 2.0)
                     if (! invisibleNode.getString("nodeType").equals("Requester"))
                        hasIndirect = true;
                }
              }
          } else {
               TupleSet focus1 = m_vis.getGroup(Visualization.FOCUS_ITEMS);

            Iterator items1 = focus1.tuples();

            while (items1.hasNext()) {

                NodeItem item = (NodeItem) items1.next();


                Iterator invisibleNodes = item.neighbors();
                while (invisibleNodes.hasNext()) {
                    NodeItem invisibleNode = (NodeItem) invisibleNodes.next();

                //    if (invisibleNode.getDouble("recScore") == 0.0 && invisibleNode.getDouble("recId") != 2.0)
                     if (! invisibleNode.getString("nodeType").equals("Requester"))
                        hasIndirect = true;
                }
            }

          }//if else


            // if has indirect links, do next steps
            if (hasIndirect) {

                if(recSet != null){


                    indirectlayout(null, nodeXYMap, true, recSet);
                    for(NodeItem item: recSet){
                        dealShowIndirect(item);
                    }
                }else{

                    TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
              
                    indirectlayout(focus, nodeXYMap, false, null);

                    Iterator items = focus.tuples();
                //  hoveredNodes.clear();
                    while (items.hasNext()) {
                        NodeItem item = (NodeItem) items.next();
                        dealShowIndirect(item);
                    }
                }

                TupleSet allTuple = m_vis.getVisualGroup(nodes);


                Iterator allitems = allTuple.tuples();

                while (allitems.hasNext()) {
                    try {
                        NodeItem item = (NodeItem) allitems.next();
                        if (item.getString("invisibleItem").equalsIgnoreCase("0") && !item.getString("nodeType").equals("Requester")) {
                            String itemLogin = item.getString("login");

                            nodeColorAction.add("login =='" + itemLogin + "'", ColorLib.gray(230));
                            textfill.add("login =='" + itemLogin + "'", ColorLib.gray(230));

                        }
                    } catch (Exception e) {

                    }
                }


                TupleSet allTuple1 = m_vis.getVisualGroup(edges);


                Iterator allitems1 = allTuple1.tuples();

                while (allitems1.hasNext()) {
                    try {
                        EdgeItem item = (EdgeItem) allitems1.next();
                        if (item.getString("invisibleItem").equalsIgnoreCase("0")) {

                            String itemKey = item.getString("edgeKey");

                            edgeFillColor.add("edgeKey =='" + itemKey + "'", ColorLib.gray(230));
                            edgeStrokeColor.add("edgeKey =='" + itemKey + "'", ColorLib.gray(230));
                        } else if (item.getString("invisibleItem").equals("2")) {

                            item.setVisible(false);
                            PrefuseLib.updateVisible(item, false);
                        }
                    } catch (Exception e) {

                    }
                }

                draw.add(new RepaintAction());
                m_vis.putAction("hidden", draw);

                m_vis.run("hidden");
                resetCount();
                legend1.currentLegend();
                legend1.removeAll();
                legend1.repaint();
            }
            fromIndirect = true;
        }


        private boolean hadOneRec() {

            if (dh.getRecPara().length == 3)

                return true;
            else
                return false;
        }

        public void showDirectGraph() {

            TupleSet allTuple = m_vis.getVisualGroup(nodes);


            Iterator items = allTuple.tuples();

            while (items.hasNext()) {

                NodeItem item = (NodeItem) items.next();
                try {
                    if (item.getString("invisibleItem").equalsIgnoreCase("1") && !hasOneRec) {

                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);

                        Iterator it = item.edges();
                        while (it.hasNext()) {
                            EdgeItem edgeItem = (EdgeItem) it.next();
                            edgeItem.setVisible(false);
                            PrefuseLib.updateVisible(edgeItem, false);
                        }

                    } else {


                        if (!item.isVisible()) {


                            item.setVisible(true);
                            PrefuseLib.updateVisible(item, true);

                            Iterator it = item.edges();
                            while (it.hasNext()) {
                                EdgeItem edgeItem = (EdgeItem) it.next();
                                edgeItem.setVisible(true);
                                PrefuseLib.updateVisible(edgeItem, true);
                            }
                        }


                    }
                } catch (Exception e) {

                }
            }


            TupleSet edgeTuple = m_vis.getVisualGroup(edges);

            Iterator edgeItems = edgeTuple.tuples();

            while (edgeItems.hasNext()) {
                try {
                    EdgeItem item = (EdgeItem) edgeItems.next();
                    if ((item.getString("invisibleItem").equalsIgnoreCase("1") || item.getString("invisibleItem").equalsIgnoreCase("2")) && !hasOneRec) {
                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);

                    }/* else {
                  item.setVisible(true);
                 PrefuseLib.updateVisible(item, true);
              } */
                } catch (Exception e) {

                }
            }

            if (fromIndirect) {
                brightNodes();
                brightEdges();

            }

            draw.add(new RepaintAction());
            m_vis.putAction("hidden", draw);

            m_vis.run("hidden");
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
            fromIndirect = false;
            hoveredNodes.clear();

        }


        public void showInitialView() {

            toInit = true;
            showOrHide = false;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, null, null, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            vg = setGraph(g);


            resetSearchPanel();


            dataInit = true;
            m_vis.putAction("draw", draw);


            m_vis.run("draw");

            if (isCustomlayout) {
                layoutCustom(null);
                rescaleNodes(1.0, false, 0, 0, fromzoomSelected);

            } else {
                if (layoutNumber == 1) {
                    layoutCircle();
                } else if (layoutNumber == 2) {
                    layoutRandom();
                } else if (layoutNumber == 3) {
                    VisualItem f = (VisualItem) vg.getNode(0);
                    m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
                    f.setFixed(false);
                    layoutTree();
                } else if (layoutNumber == 4) {
                    layoutAuto();
                } else if (layoutNumber == 5) {
                    layoutGroup(false);
                } else if (layoutNumber == 6) {
                    forShowAll = false;
                    fromAll = false;
                    layoutRec();
                }
            }
            toInit = true;

            fromInit = true;
            showAllRec = false;
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
            resetCount();
        }

        private void showItemBusiness
                (Iterator
                        allIems, TupleSet
                        ts, TupleSet
                        hideEdges) {

            Set<Node> hiddenNodes = new HashSet<Node>();
            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                if (!ts.containsTuple(item)) {
                    double[] xy = {item.getX(), item.getY()};

                    nodeXYMap.put(item.getString("login"), xy);
                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);


                    Node node = (Node) item;
                    String nodeRow = node.getString("nodeKey");
                    nodeKeys.add(nodeRow);

                    hiddenNodes.add(node);
                }
            }


            if (hideEdges != null) {

                TupleSet allTuple = m_vis.getVisualGroup(edges);

                Iterator edges = allTuple.tuples();
                while (edges.hasNext()) {

                    VisualItem item = (VisualItem) edges.next();

                    if (!hideEdges.containsTuple(item)) {
                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);
                        edgeKeys.add(item.getString("edgeKey"));
                    }
                }

            }

            hiddenNode(hiddenNodes);

        }


        public void showSelection
                () {
            if (invisibleEdges != null) {


                for (EdgeItem item : invisibleEdges) {
                    item.setVisible(true);
                    PrefuseLib.updateVisible(item, true);
                }

            }

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }
            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);


            TupleSet allTuple = m_vis.getVisualGroup(nodes);

            Iterator allIems = allTuple.tuples();

            showItemBusiness(allIems, focus, edgeTupleSet);

            ActionList selection = new ActionList();

            selection.add(new RepaintAction());
            m_vis.putAction("selection", selection);

            m_vis.run("selection");
            toInit = false;
            showOrHide = true;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            resetCount();
            nodeActionType = 3;
            if (multiNodeDepth == true) {
                resetFocus();

            }
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }


        public void hideNodeGroup
                (Set<String> choices) {
            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
                aggLabel.clear();
            }

            TupleSet allTuple = m_vis.getVisualGroup(AGGR);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                String type = item.getString("grouplabel");

                for (String c : choices) {

                    if (c.equals(type)) {

                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);

                        aggLabel.add(type);
                        Iterator itemInAgg = ((AggregateItem) item).items();
                        hiddenItemBusiness(itemInAgg, null);

                    }//if
                }//for
            }//while

            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;

            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);

            visualDataChanged = true;
            nodeActionType = 1;
            groupChanged = true;
            resetCount();
            DisplayGroupLegend();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
        }

        public void showNodeGroup
                (Set<String> choices) {
            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
                aggLabel.clear();
            }


            TupleSet allTuple = m_vis.getVisualGroup(AGGR);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                String type = item.getString("grouplabel");

                if (!choices.contains(type)) {
                    {
                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);
                        aggLabel.add(type);
                        Iterator itemInAgg = ((AggregateItem) item).items();
                        hiddenItemBusiness(itemInAgg, null);

                    }//if
                }//for
            }//while

            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;

            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);

            visualDataChanged = true;
            nodeActionType = 3;
            groupChanged = true;
            resetCount();
            DisplayGroupLegend();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
        }

        public void hideNodeShape
                (Set<String> choices) {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }

            Set<Node> hiddenNodes = new HashSet<Node>();
            TupleSet allTuple = m_vis.getVisualGroup(nodes);
            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                String attri = item.getString("shapeAttri");
                try {

                    for (String c : choices) {

                        if (c.equals(attri)) {
                            double[] xy = {item.getX(), item.getY()};

                            nodeXYMap.put(item.getString("login"), xy);
                            item.setVisible(false);
                            PrefuseLib.updateVisible(item, false);

                            // hide related edges
                            Node node = (Node) item;

                            String nodeRow = node.getString("nodeKey");
                            nodeKeys.add(nodeRow);

                            hiddenNodes.add(node);
                        }
                    }

                } catch (Exception e) {

                }
            }

            hiddenNode(hiddenNodes);

            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;

            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }

        public void showNodeShape
                (Set<String> choices) {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }
            Set<Node> hiddenNodes = new HashSet<Node>();
            TupleSet allTuple = m_vis.getVisualGroup(nodes);

            Iterator allIems = allTuple.tuples();

            Set<Node> showNodes = new HashSet<Node>();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                String attri = item.getString("shapeAttri");

                try {

                    for (String c : choices) {

                        if (c.equals(attri)) {
                            // hide related edges
                            Node node = (Node) item;
                            showNodes.add(node);
                            item.setVisible(true);
                            PrefuseLib.updateVisible(item, true);

                        }
                    }

                } catch (Exception e) {

                }


            }

            // also show edges of these nodes
            showNode(showNodes);

            Iterator allIems1 = allTuple.tuples();

            while (allIems1.hasNext()) {

                VisualItem item = (VisualItem) allIems1.next();
                Node node = (Node) item;
                if (!showNodes.contains(node)) {
                    double[] xy = {item.getX(), item.getY()};

                    nodeXYMap.put(item.getString("login"), xy);
                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);
                    String nodeRow = node.getString("nodeKey");
                    nodeKeys.add(nodeRow);
                    hiddenNodes.add(node);
                }
            }

            hiddenNode(hiddenNodes);
            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }

        public void hideNodeType
                (Set<String> choices, Map<String, Set<String>> attriChoices) {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }

            Set<Node> hiddenNodes = new HashSet<Node>();
            TupleSet allTuple = m_vis.getVisualGroup(nodes);
            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                String type = item.getString("nodeType");
                try {
                    if (!attriChoices.keySet().contains(type)) {
                        for (String c : choices) {

                            if (c.equals(type)) {
                                double[] xy = {item.getX(), item.getY()};

                                nodeXYMap.put(item.getString("login"), xy);
                                item.setVisible(false);
                                PrefuseLib.updateVisible(item, false);

                                // hide related edges
                                Node node = (Node) item;

                                String nodeRow = node.getString("nodeKey");
                                nodeKeys.add(nodeRow);

                                hiddenNodes.add(node);
                            }
                        }
                    }
                } catch (Exception e) {

                }

                if (attriChoices.size() > 0) {
                    String colorAttr = item.getString("colorAttri");

                    for (String c : attriChoices.keySet()) {


                        if (c.equals(type)) {
                            Set<String> attris = attriChoices.get(c);

                            try {

                                for (String attri : attris) {

                                    if (attri.equals(colorAttr)) {

                                        double[] xy = {item.getX(), item.getY()};

                                        nodeXYMap.put(item.getString("login"), xy);
                                        item.setVisible(false);
                                        PrefuseLib.updateVisible(item, false);

                                        // hide related edges
                                        Node node = (Node) item;

                                        String nodeRow = node.getString("nodeKey");
                                        nodeKeys.add(nodeRow);

                                        hiddenNodes.add(node);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }// if color by attri
                }
            }

            hiddenNode(hiddenNodes);

            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;

            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }

        public void showNodeType
                (Set<String> choices, Map<String, Set<String>> attriChoices) {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }
            Set<Node> hiddenNodes = new HashSet<Node>();
            TupleSet allTuple = m_vis.getVisualGroup(nodes);

            Iterator allIems = allTuple.tuples();

            Set<Node> showNodes = new HashSet<Node>();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();

                String type = item.getString("nodeType");


                try {
                    if (!attriChoices.keySet().contains(type)) {
                        for (String c : choices) {

                            if (c.equals(type)) {
                                // hide related edges
                                Node node = (Node) item;
                                showNodes.add(node);
                                item.setVisible(true);
                                PrefuseLib.updateVisible(item, true);

                            }
                        }
                    }
                } catch (Exception e) {

                }

                if (attriChoices.size() > 0) {
                    String colorAttr = item.getString("colorAttri");
                    for (String c : attriChoices.keySet()) {
                        if (c.equals(type)) {
                            Set<String> attris = attriChoices.get(c);
                            try {

                                for (String attri : attris) {

                                    if (attri.equals(colorAttr)) {
                                        // hide related edges
                                        Node node = (Node) item;

                                        showNodes.add(node);
                                        item.setVisible(true);
                                        PrefuseLib.updateVisible(item, true);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }

            // also show edges of these nodes
            showNode(showNodes);

            Iterator allIems1 = allTuple.tuples();

            while (allIems1.hasNext()) {

                VisualItem item = (VisualItem) allIems1.next();
                Node node = (Node) item;
                if (!showNodes.contains(node)) {
                    double[] xy = {item.getX(), item.getY()};

                    nodeXYMap.put(item.getString("login"), xy);
                    item.setVisible(false);
                    PrefuseLib.updateVisible(item, false);
                    String nodeRow = node.getString("nodeKey");
                    nodeKeys.add(nodeRow);
                    hiddenNodes.add(node);
                }
            }

            hiddenNode(hiddenNodes);
            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }

        private void showNode(Set<Node> showNodes) {

            for (Node n : showNodes) {

                Iterator edgeIt = n.edges();
                while (edgeIt.hasNext()) {

                    Edge e = (Edge) edgeIt.next();
                    String edgePair = e.getString("edgeKey");

                    EdgeItem e2 = (EdgeItem) e;

                    if ((e2.getTargetItem().isVisible()) || (e2.getSourceItem().isVisible())) {

                        if (showWeight) {


                        } else {
                            e2.setVisible(true);

                            PrefuseLib.updateVisible(e2, true);
                            edgeKeys.remove(edgePair);
                        }
                    }

                }// while

            }//for

            if (showWeight) {
                Iterator<DecoratorItem> it;
                TupleSet decorators = m_vis.getVisualGroup(EDGE_DECORATORS);

                it = decorators.tuples();


                DecoratorItem decorator = it.next();
                EdgeItem decoratorvis = (EdgeItem) decorator.getDecoratedItem();

                if (decoratorvis.isVisible()) {
                    decorator.setVisible(true);
                    PrefuseLib.updateVisible(decorator, true);


                }
            }
        }


        private void hiddenNode
                (Set<Node> hiddenNodes) {
            if (showWeight) {
                hiddenNode_withEdgeWeight(hiddenNodes);
            } else {

                for (Node n : hiddenNodes) {

                    Iterator edgeIt = n.edges();
                    while (edgeIt.hasNext()) {

                        Edge e = (Edge) edgeIt.next();
                        String edgePair = e.getString("edgeKey");

                        EdgeItem e2 = (EdgeItem) e;

                        if (!(e2.getTargetItem().isVisible()) || !(e2.getSourceItem().isVisible())) {
                            e2.setVisible(false);
                            PrefuseLib.updateVisible(e2, false);
                            edgeKeys.add(edgePair);
                        }

                    }// while

                }//for

            }
        }

        private void hiddenNode_withEdgeWeight
                (Set<Node> hiddenNodes) {
            TupleSet decorators = m_vis.getVisualGroup(EDGE_DECORATORS);

            Iterator it = decorators.tuples();


            while (it.hasNext())

            {
                DecoratorItem decorator = (DecoratorItem) it.next();
                EdgeItem decoratorvis = (EdgeItem) decorator.getDecoratedItem();

                String edgePair = decoratorvis.getString("edgeKey");
                try {

                    if (hiddenNodes.contains(decoratorvis.getTargetNode()) || hiddenNodes.contains(decoratorvis.getSourceNode())) {


                        decorator.setVisible(false);

                        PrefuseLib.updateVisible(decorator, false);

                        decoratorvis.setVisible(false);

                        PrefuseLib.updateVisible(decoratorvis, false);

                        edgeKeys.add(edgePair);

                    }  //if

                } catch (Exception ex) {

                }
            } //while


        }

        public void hideEdgeType
                (Set<String> choices) {

            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }
            TupleSet allTuple = m_vis.getVisualGroup(edges);

            Iterator allIems = allTuple.tuples();
            Iterator<DecoratorItem> it;

            if (showWeight) {
                TupleSet decorators = m_vis.getVisualGroup(EDGE_DECORATORS);

                it = decorators.tuples();


                while (it.hasNext())

                {
                    DecoratorItem decorator = it.next();
                    VisualItem decoratorvis = decorator.getDecoratedItem();

                    String type = decoratorvis.getString("edgeType");
                    for (String c : choices) {

                        if (c.equalsIgnoreCase(type)) {

                            decorator.setVisible(false);
                            PrefuseLib.updateVisible(decorator, false);

                            decoratorvis.setVisible(false);
                            PrefuseLib.updateVisible(decoratorvis, false);
                            Edge e = (Edge) decoratorvis;

                            String edgePair = e.getString("edgeKey");


                            edgeKeys.add(edgePair);


                        }
                    }

                }


            } else {


                while (allIems.hasNext()) {

                    VisualItem item = (VisualItem) allIems.next();

                    String type = item.getString("edgeType");
                    for (String c : choices) {

                        if (c.equalsIgnoreCase(type)) {

                            item.setVisible(false);
                            PrefuseLib.updateVisible(item, false);

                            // hide related edges
                            Edge e = (Edge) item;

                            String edgePair = e.getString("edgeKey");


                            edgeKeys.add(edgePair);


                        }
                    }

                }
            }

            ActionList hidden = new ActionList();

            hidden.add(new RepaintAction());
            m_vis.putAction("hidden", hidden);

            m_vis.run("hidden");
            toInit = false;
            showOrHide = true;
            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            nodeActionType = 1;
            resetCount();
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();

        }

        public void showEdgeType
                (Set<String> choices) {
            if (nodeActionType == 2) {
                nodeKeys.clear();
                edgeKeys.clear();
            }

            TupleSet allTuple = m_vis.getVisualGroup(edges);

            Iterator allIems = allTuple.tuples();

            Iterator<DecoratorItem> it;

            //collect edge types for the use of using multi edges
            Set<String> hiddenTypes = new HashSet<String>();

            if (showWeight) {
                TupleSet decorators = m_vis.getVisualGroup(EDGE_DECORATORS);

                it = decorators.tuples();

                if (showWeight) {
                    while (it.hasNext())

                    {
                        DecoratorItem decorator = it.next();
                        EdgeItem decoratorvis = (EdgeItem) decorator.getDecoratedItem();
                        if (choices.size() > 0) {
                            decorator.setVisible(false);
                            PrefuseLib.updateVisible(decorator, false);
                            decoratorvis.setVisible(false);
                            PrefuseLib.updateVisible(decoratorvis, false);
                            Edge e = (Edge) decoratorvis;

                            String edgePair = e.getString("edgeKey");


                            edgeKeys.add(edgePair);


                        }

                        String type = decoratorvis.getString("edgeType");

                        for (String c : choices) {

                            if (type.equalsIgnoreCase(c)) {
                                if (decoratorvis.getSourceItem().isVisible() && decoratorvis.getTargetItem().isVisible()) {
                                    decorator.setVisible(true);
                                    PrefuseLib.updateVisible(decorator, true);
                                    decoratorvis.setVisible(true);
                                    PrefuseLib.updateVisible(decoratorvis, true);
                                    Edge e = (Edge) decoratorvis;

                                    String edgePair = e.getString("edgeKey");


                                    edgeKeys.remove(edgePair);


                                } else
                                    hiddenTypes.add(type);
                            }
                        }
                    }

                }
            } else {

                while (allIems.hasNext()) {

                    EdgeItem item = (EdgeItem) allIems.next();

                    if (choices.size() > 0) {
                        item.setVisible(false);
                        PrefuseLib.updateVisible(item, false);
                        Edge e = (Edge) item;
                        String edgePair = e.getString("edgeKey");
                        edgeKeys.add(edgePair);


                    }

                    String type = item.getString("edgeType");
                    for (String c : choices) {

                        if (type.equalsIgnoreCase(c)) {
                            if (item.getSourceItem().isVisible() && item.getTargetItem().isVisible()) {
                                item.setVisible(true);
                                PrefuseLib.updateVisible(item, true);

                                Edge e = (Edge) item;

                                String edgePair = e.getString("edgeKey");
                                edgeKeys.remove(edgePair);

                            } else
                                hiddenTypes.add(type);
                        }
                    }
                }
            }
            ActionList selection = new ActionList();

            selection.add(new RepaintAction());
            m_vis.putAction("selection", selection);

            m_vis.run("selection");
            toInit = false;
            showOrHide = true;

            g = dh.setGraphPara(toInit, showOrHide, fromInit, nodeKeys, edgeKeys, nodeColorMap, edgeColorMap, datatype);
            visualDataChanged = true;
            nodeActionType = 3;
            resetCount();
            // collect the hiddenEdge types
            legend1.currentLegend();
            legend1.removeAll();
            legend1.repaint();
        }

        /**
         * Switch the root of the tree by requesting a new spanning tree
         * at the desired root
         */
        private static class TreeRootAction extends GroupAction {
            public TreeRootAction(String graphGroup) {
                super(graphGroup);
            }

            public void run(double frac) {
                TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
                if (focus == null || focus.getTupleCount() == 0) return;

                Graph g = (Graph) m_vis.getGroup(m_group);
                Node f = null;
                Iterator tuples = focus.tuples();
                while (tuples.hasNext() && !g.containsTuple(f = (Node) tuples.next())) {
                    f = null;
                }
                if (f == null) return;
                g.getSpanningTree(f);
            }
        }

        public void doDepth
                () {
            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);


            if (focus.getTupleCount() < 2) {
                DepthReminderDialog rootD = null;
                if (rootD == null || !rootD.isVisible())
                    rootD = new DepthReminderDialog(admin);

                rootD.setVisible(true);


            } else {
                DepthDialog depthD = null;

                if (depthD == null || !depthD.isVisible()) {
                    depthD = new DepthDialog(admin, this);
                    depthD.setVisible(true);
                }
            }
        }

        public void layoutTree
                () {


            resetSearchPanel();

            visualDataChanged = false;
            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);


            if (focus.getTupleCount() != 1) {
                FocusNodeReminderDialog rootD = null;
                if (rootD == null || !rootD.isVisible())
                    rootD = new FocusNodeReminderDialog(admin);

                rootD.setVisible(true);


            } else {

                if (layoutNumber == 5 && groupChanged == false) {
                    //   m_vis.removeAction("grouplayout");
                    hiddenAggr();
                }

                m_vis.setInteractive(edges, null, true);
                m_vis.removeAction("filter");


                NodeItem firstRootItem;


                Iterator allIems = focus.tuples();

                if (allIems.hasNext() && focus.getTupleCount() == 1) {
                    firstRootItem = (NodeItem) allIems.next();

                } else {
                    firstRootItem = (NodeItem) m_vis.getVisualItem(nodes, g.getNode(0));
                }

                // create the tree layout action


                treeLayout = new RadialTreeLayoutCustom(graph, firstRootItem, display);

                treeLayout.setDuration(Activity.DEFAULT_STEP_TIME);

                m_vis.putAction("treeLayout", treeLayout);

                CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(graph);

                m_vis.putAction("subLayout", subLayout);

                // create the filtering and layout
                ActionList filter = new ActionList();
                filter.add(new TreeRootAction(graph));

                filter.add(treeLayout);
                filter.add(subLayout);

                filter.add(new RepaintAction());
                filter.add(new ZoomToFit(graph));
                m_vis.putAction("filter", filter);

                // animated transition

                ActionList animate = new ActionList(1000);
                animate.setPacingFunction(new SlowInSlowOutPacer());
                animate.add(new QualityControlAnimator());
                animate.add(new VisibilityAnimator(graph));
                animate.add(new PolarLocationAnimator(nodes, linear));
                animate.add(new ColorAnimator(nodes));

                animate.add(new TreeRootAction(graph));

                animate.add(new RepaintAction());

                m_vis.putAction("animate", animate);
                m_vis.alwaysRunAfter("filter", "animate");

                m_vis.run("filter");

                // maintain a set of items that should be interpolated linearly
                // this isn't absolutely necessary, but makes the animations nicer
                // the PolarLocationAnimator should read this set and act accordingly
                try {
                    m_vis.addFocusGroup(linear, new DefaultTupleSet());
                    m_vis.getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
                            new TupleSetListener() {
                                public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                                    TupleSet linearInterp = m_vis.getGroup(linear);
                                    if (add.length < 1) return;
                                    linearInterp.clear();
                                    for (Node n = (Node) add[0]; n != null; n = n.getParent())
                                        linearInterp.addTuple(n);
                                }
                            }
                    );
                } catch (IllegalArgumentException ie) {

                }

                if (layoutNumber == 5) {
                    legend1.groupLegendStatus(false);
                    legend1.removeAll();
                    legend1.repaint();
                }

                if (recStatus == 2 && recLegend) {
            recLegend = false;
            legend1.removeAll();
            legend1.repaint();
        }
                layoutNumber = 3;

                if (showWeight) {
                    firstShowWeight();
                } else {
                    showWeightstarted = false;
                }
            }

            fitGraph();
            disposeGroupLegend();
            if (admin.getInitialViewButton() != null)
                admin.getInitialViewButton().setEnabled(false);
        }

        public void defaultAutoLayout
                (int method) {
            String disValue = "";
            clusterMethod = method;
            if (vg.getNodeCount() < 200) {
                disValue = "2";
            } else if (g.getNodeCount() > 199 && g.getNodeCount() < 300) {
                disValue = "3";
            } else if (g.getNodeCount() > 299 && g.getNodeCount() < 500) {
                disValue = "4";
            } else if (g.getNodeCount() > 499 && g.getNodeCount() < 600) {
                disValue = "5";
            } else if (g.getNodeCount() > 599 && g.getNodeCount() < 700) {
                disValue = "6";
            } else if (g.getNodeCount() > 699 && g.getNodeCount() < 800) {
                disValue = "7";
            } else if (g.getNodeCount() > 799 && g.getNodeCount() < 900) {
                disValue = "8";
            } else if (g.getNodeCount() > 899) {
                disValue = "9";
            }
            if (vg.getNodeCount() < 150) {
                Set<Set> comps = calcComp(vg);
                int compCount = comps.size();
                if (compCount == 1)
                    generateAutoLayout(disValue, "Structural view");
                else if (compCount < 6) {
                    int bigCount = 0;
                    int smallCount = 0;
                    for (Set<NodeItem> comp : comps) {
                        if (comp.size() > 50) {
                            bigCount++;
                        } else if (comp.size() < 5) {
                            smallCount++;
                        }
                    }

                    if ((bigCount + smallCount) == compCount) {

                        generateAutoLayout("8", "Structural view");


                    } else
                        generateAutoLayout(disValue, "Component view");
                } else {
                    generateAutoLayout(disValue, "Component view");
                }
            } else
                generateAutoLayout(disValue, "Structural view");
        }

        // get each component

        private Set<Set> calcComp
                (Graph
                        g) {

            Set<Set> comSets = new HashSet<Set>();

            for (Iterator iter = g.nodes(); iter.hasNext();) {
                NodeItem n = (NodeItem) iter.next();
                Set<NodeItem> comElements = new HashSet<NodeItem>();

                comSets.add(getComSet(n, comElements));

            }

            return comSets;
        }


        private Set<NodeItem> getComSet
                (NodeItem
                        n, Set<NodeItem> comElements) {

            comElements.add(n);
            Iterator iter = n.neighbors();

            while (iter.hasNext()) {
                NodeItem ele = (NodeItem) iter.next();
                while (!comElements.contains(ele)) {
                    getComSet(ele, comElements);
                }
            }
            return comElements;
        }


        public void generateAutoLayout
                (String
                        clusteringDis, String
                        viewFormat) {
            this.clusteringDis = clusteringDis;
            this.clusteringView = viewFormat;
            makeLayout();
            layoutAuto();
        }


        public void layoutAuto
                () {

            resetSearchPanel();
            if (layoutNumber == 3) {

                m_vis.removeAction("filter");

            } else if (layoutNumber == 5 && !visualDataChanged && groupChanged == false) {

                hiddenAggr();
            }

            int nodeCount = vg.getNodeCount();


            FRProcessingDialog processingD = null;
            runFRLayout(2, processingD);


            runFRLayout(3, processingD);

            runFRLayout(5, processingD);
            runFRLayout(8, processingD);
            runFRLayout(11, processingD);
            runFRLayout(13, processingD);

            if (nodeCount > 300) {
                processingD = new FRProcessingDialog(admin);
                processingD.setVisible(true);


                runFRLayout(15, processingD);

                runFRLayout(23, processingD);
            } else {

                runFRLayout(37, processingD);
            }

            if (layoutNumber == 5) {
                legend1.groupLegendStatus(false);
                legend1.removeAll();
                legend1.repaint();
            }

            if (recStatus == 2 && recLegend) {
            recLegend = false;
            legend1.removeAll();
            legend1.repaint();
        }

            layoutNumber = 4;
            if (showWeight) {
                firstShowWeight();
            } else {
                showWeightstarted = false;
            }


            visualDataChanged = false;
            fitGraph();
            disposeGroupLegend();
            if (admin.getInitialViewButton() != null)
                admin.getInitialViewButton().setEnabled(false);
        }

        public boolean isbreak
                () {
            return true;
        }

        public void runFRLayout
                (
                        int iter, FRProcessingDialog
                        processingD) {

            // now create the main layout routine
            ActionList clayout = new ActionList();

            fr = new FruchtermanReingoldLayoutCustom(graph, iter, visualDataChanged, clusteringView, clusteringDis, iter, processingD, this, clusterMethod);

            fr.setPacingFunction(new SlowInSlowOutPacer());
            clayout.add(fr);


            clayout.add(new RepaintAction());
            clayout.add(new ZoomToFit(graph));

            m_vis.putAction("clusterlayout", clayout);
            m_vis.run("clusterlayout");

            if ((nodeCount > 300 && iter < 20) || ((nodeCount < 300) && iter > 20)) {
                ActionList animate = new ActionList(2000);
                animate.setPacingFunction(new SlowInSlowOutPacer());
                // animate.add(new QualityControlAnimator());
                // animate.add(new PolarLocationAnimator(nodes, linear));
                animate.add(new LocationAnimator(nodes));
                animate.add(new RepaintAction());

                m_vis.putAction("animate", animate);
                m_vis.alwaysRunAfter("clusterlayout", "animate");
            }
            m_vis.run("fitlayout");


        }

        public void showEdgeStrength
                () {
            if (!showWeightstarted) {
                firstShowWeight();
                showWeightstarted = true;

            } else {
                TupleSet allTuple = m_vis.getVisualGroup(EDGE_DECORATORS);

                Iterator allIems = allTuple.tuples();

                while (allIems.hasNext()) {


                    DecoratorItem decorator = (DecoratorItem) allIems.next();
                    VisualItem decoratorvis = decorator.getDecoratedItem();
                    if (decoratorvis.isVisible()) {
                        decorator.setVisible(true);
                        PrefuseLib.updateVisible(decorator, true);
                    }
                }


                ActionList show = new ActionList();

                show.add(new RepaintAction());
                m_vis.putAction("show", show);

                m_vis.run("show");

            }
            showWeight = true;
        }


        public void releaseSearchFocus() {
            if (search != null)

                search.setQuery(null);
        }

        public void resetSearchPanel
                () {
            TupleSet allTuple = m_vis.getVisualGroup(nodes);
            Iterator allIems = allTuple.tuples();
            preSearch.index(allIems, label);


            if (!search.getQuery().equals("")) {
                // this will only be done if a query has been specified
                String vQuery = search.getQuery();
                search.setQuery("");
                search.setQuery(vQuery);
            }
        }

        private void firstShowWeight
                () {
            try {
                m_vis.addDecorators(EDGE_DECORATORS, edges, DECORATOR_SCHEMA);


                ActionList animate = new ActionList(Activity.INFINITY);

                animate.add(new EdgeDecorator(EDGE_DECORATORS));

                TupleSet allTuple = m_vis.getVisualGroup(EDGE_DECORATORS);

                Iterator allIems = allTuple.tuples();

                while (allIems.hasNext()) {


                    DecoratorItem decorator = (DecoratorItem) allIems.next();
                    VisualItem decoratorvis = decorator.getDecoratedItem();
                    if (decoratorvis.isVisible()) {
                        decorator.setVisible(true);
                        PrefuseLib.updateVisible(decorator, true);
                    } else {
                        decorator.setVisible(false);
                        PrefuseLib.updateVisible(decorator, false);
                    }
                }


                animate.add(new RepaintAction());

                m_vis.putAction("layout", animate);


                m_vis.run("layout");
            } catch (Exception e) {

            }
        }

        public Visualization getCurrentVis
                () {
            return m_vis;
        }

        public int getRecStatus
                () {
            return recStatus;
        }

        private void hiddenAggr
                () {

            TupleSet allTuple = m_vis.getVisualGroup(AGGR);

            Iterator allIems = allTuple.tuples();

            while (allIems.hasNext()) {

                VisualItem item = (VisualItem) allIems.next();
                if (item.isVisible())
                    item.setVisible(false);
            }

        }

        private void firstGroup() {

            ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
            aStroke.setDefaultColor(ColorLib.gray(100));
            aStroke.add("_hover", ColorLib.rgb(255, 0, 0));

            int[] pal = new int[]{
                    ColorLib.rgba(255, 200, 200, 150),
                    ColorLib.rgba(200, 255, 200, 150),
                    ColorLib.rgba(200, 200, 255, 150)
            };
            ColorAction aFill = new DataColorAction(AGGR, "grouplabel",
                    Constants.NOMINAL, VisualItem.FILLCOLOR, pal);

            AppletDataHandler1.Group[] items = getGroups();

            groupColors(aFill, items);
            attriSet = new HashSet<String>();

            for (int i = 0; i < items.length; ++i) {
                attriSet.add(items[i].key);
            }

            //stop the AggregateLayout before setting some nodes visible false (m_vis.cancel("aggLayout...");
            at = m_vis.addAggregates(AGGR);
            at.addColumn(VisualItem.POLYGON, float[].class);
            at.addColumn("grouplabel", String.class);

            setAggregate(attriSet);

            draw.add(aStroke);
            draw.add(aFill);

        }

        public int nodedisStatus() {
            return nodeDisStatus;
        }

        public int isGroupNode() {
            return layoutNumber;
        }

        public boolean isLabelNode() {
            return labelNode;
        }

        public boolean isLabelEdge() {
            return showWeight;
        }

        public boolean isWeightEdge() {
            return maxEdgeWidth;
        }

        public Graph getGraph
                () {
            return vg;
        }

        public Graph getCurrentGraph
                () {
            return g;
        }

        public AppletDataHandler1 getHandler
                () {
            return dh;
        }

        public AppletDataHandler1.Group[] getGroups
                () {
            return dh.getGroups();
        }

        public Map getXyCoordinate() {
            return nodeXYMap;
        }

        private void setAggregate
                (Set<String> attriSet) {

            at.clear();

            // add nodes to aggregates
            // create an aggregate for each type of nodes
            TupleSet allTuple = m_vis.getVisualGroup(nodes);

            groupSet = new HashSet<AggregateItem>();

            for (String att : attriSet) {

                AggregateItem aitem = (AggregateItem) at.addItem();
                aitem.setString("grouplabel", att);
                Iterator allIems = allTuple.tuples();
                while (allIems.hasNext()) {

                    VisualItem item = (VisualItem) allIems.next();
                    Node node = (Node) item;
                    if (node.getString("groupAttri").equals(att)) {
                        aitem.addItem((VisualItem) node);
                    }//if
                }// while

                if (aitem.getAggregateSize() > 0) {
                    groupSet.add(aitem);
                }


            }  //for

        }

        public void layoutGroup
                (
                        boolean isWindowsResized) {

            try {
                groupChanged = false;
                if (!aggrstarted || dataInit) {
                    firstGroup();
                }
                if ((layoutNumber == 5 || visualDataChanged) && !dataInit) {

                    setAggregate(attriSet);

                }

                // now create the main layout routine

                TupleSet aggrTuple = m_vis.getVisualGroup(AGGR);

                Iterator allIems = aggrTuple.tuples();

                while (allIems.hasNext()) {

                    VisualItem item = (VisualItem) allIems.next();
                    item.setVisible(true);
                }
                TupleSet ts = m_vis.getGroup(nodes);

                Iterator items = ts.tuples();
                Set<NodeItem> itemSet = new HashSet<NodeItem>();
                for (int i = 0; items.hasNext(); i++) {

                    NodeItem n = (NodeItem) items.next();
                    if (n.isVisible())
                        itemSet.add(n);
                }


                Map<String, VisualItem> nodeMap = new HashMap<String, VisualItem>();

                String[] attri_node = new String[itemSet.size()];

                int i = 0;
                for (NodeItem n : itemSet) {

                    String login = n.getString("login");
                    String attri = n.getString("groupAttri");

                    // colors for node and legend
                    nodeMap.put(login, (VisualItem) n);

                    if (n.getDegree() > 0) {
                        attri_node[i] = attri + "-" + login;
                    } else {  // // put isolate node at the end of each group
                        attri_node[i] = attri + "z-" + login;
                    }
                    i++;
                }

                Arrays.sort(attri_node);
                String[] nodeIndex = new String[attri_node.length];
                for (int j = 0; j < nodeIndex.length; j++) {
                    int index = attri_node[j].lastIndexOf("-");
                    nodeIndex[j] = attri_node[j].substring(index + 1);
                }

                ActionList layout = new ActionList();
                boolean grLayoutChanged = false;

                if (layoutNumber == 5 && !visualDataChanged)
                    grLayoutChanged = true;

                gl = new GroupingLayout(nodes, display, groupSet, nodeIndex, nodeMap, aggrTuple, grLayoutChanged, isWindowsResized);

                layout.add(draw);
                //  if(layoutNumber != 5 || visualDataChanged )
                layout.add(gl);

                layout.add(new AggregateLayout(AGGR, m_vis));
                layout.add(new RepaintAction());
                layout.add(new ZoomToFit(graph));
                m_vis.putAction("grouplayout", layout);

                // set things running
                m_vis.run("grouplayout");

                aggrstarted = true;
                layoutNumber = 5;

                legend1.groupLegendStatus(true);
                legend1.removeAll();
                legend1.repaint();

                if (recStatus == 2 && recLegend) {
            recLegend = false;
            legend1.removeAll();
            legend1.repaint();
        }

                dataInit = false;

                if (showWeight) {
                    firstShowWeight();
                } else {
                    showWeightstarted = false;
                }

                visualDataChanged = false;
            } catch (Exception e) {

            }
            DisplayGroupLegend();
            fitGraph();
            if (admin.getInitialViewButton() != null)
                admin.getInitialViewButton().setEnabled(false);
        }

        protected void setPan(boolean panactive) {
            forPan = panactive;
        }


        public ArrayList<String> getGroupColor() {


            ArrayList<String> groupColor = new ArrayList<String>();
            Set<String> partSet = new HashSet<String>();
            String missingStr = null;
            String notApplicableStr = null;
            Iterator allIems = m_vis.visibleItems("aggregates");
            while (allIems.hasNext()) {

                AggregateItem item = (AggregateItem) allIems.next();

                if (item.getAggregateSize() == 0)

                    continue;

                try {
                    String group = item.getString("grouplabel");
                    if (!group.equals("MISSING VALUE") && !group.equals("NOT APPLICABLE"))
                        partSet.add(group.trim());
                    else if (group.equals("MISSING VALUE"))
                        missingStr = group;
                    else if (group.equals("NOT APPLICABLE"))
                        notApplicableStr = group;

                } catch (Exception e) {
                }
            }
            // part group items except "missing value" and "not applicable" in a array and sort it
            String[] partitems = partSet.toArray(new String[partSet.size()]);
            Arrays.sort(partitems);

            for (int i = 0; i < partitems.length; i++) {
                groupColor.add(partitems[i]);
            }
            if (missingStr != null)
                groupColor.add(missingStr);
            if (notApplicableStr != null)
                groupColor.add(notApplicableStr);

            return groupColor;
        }

        public void resetCursor
                () {
            if (display.getCursor().getName().equals("zoom"))
                admin.toggleZoom();
            else if (display.getCursor().getName().equals("zoomin"))
                admin.toggleZoomIn();
            else if (display.getCursor().getName().equals("zoomout"))
                admin.toggleZoomOut();
            else if (display.getCursor().getName().equals("zoomin1"))
                admin.toggleZoomIn1();
            else if (display.getCursor().getName().equals("zoomout1"))
                admin.toggleZoomOut1();
            else if (display.getCursor().getName().equals("pan"))
                admin.togglePan();
        }

        public void DisplayGroupLegend
                () {

            if (groupD == null || !groupD.isVisible()) {
                String groupBy = dh.getGroupBy();
                groupD = new GroupLegendDialog(admin, this, groupBy);
                groupD.setVisible(true);

            }

        }

        private void disposeGroupLegend() {

            if (groupD != null && groupD.isVisible()) {
                groupD.setVisible(false);
                groupD.dispose();

            }

        }


        public void SaveGraph
                (boolean forGraph) {
            System.out.println("start to save graph ");
            DisplayToImageExporter exporter = new DisplayToImageExporter();

            if (forGraph)
                exporter.export(split);
            else
                exporter.export(groupD.getGroupLegend());
        }

        public void saveData() {
            System.out.println("start to save data ");
            DisplayToDLExporter exporter = new DisplayToDLExporter();

            exporter.export(this, dh, reader.getOpenedXMLFile());

        }


        public void hideEdgeStrength
                () {
            if (showWeight) {
                TupleSet allTuple = m_vis.getVisualGroup(EDGE_DECORATORS);

                Iterator allIems = allTuple.tuples();

                while (allIems.hasNext()) {

                    DecoratorItem decorator = (DecoratorItem) allIems.next();
                    decorator.setVisible(false);
                    PrefuseLib.updateVisible(decorator, false);

                }


                ActionList hidden = new ActionList();

                hidden.add(new RepaintAction());
                m_vis.putAction("hidden", hidden);

                m_vis.run("hidden");
            }
            showWeight = false;
        }

        class EdgeDecorator extends Layout {
            public EdgeDecorator(String group) {
                super(group);

            }

            public void run(double frac) {
                Iterator iter = m_vis.items(m_group);
                while (iter.hasNext()) {
                    DecoratorItem decorator = (DecoratorItem) iter.next();
                    VisualItem decoratedItem = decorator.getDecoratedItem();
                    Rectangle2D bounds = decoratedItem.getBounds();

                    double x = bounds.getCenterX();
                    double y = bounds.getCenterY();
                    setX(decorator, null, x);
                    setY(decorator, null, y);
                }
            }

        }

        // MyItemSorter
        class MyItemSorter extends ItemSorter {
            GraphView gv;
            MyItemSorter(GraphView gv){
               this.gv = gv;
            }
            public int score(VisualItem item) {
                int score = super.score(item);
              if(gv.recStatus == 2 && gv.layoutNumber == 6)  {
                try{

                  if (item.getString("invisibleItem").equalsIgnoreCase("0") && !item.getString("nodeType").equals("Requester")){
                         if(item.getFillColor()== ColorLib.gray(230))
                         score = score *10000;
                  }

            }catch(Exception e){
                
            }
            }
                return score;
            }
        }
    }

