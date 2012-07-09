package data;

import prefuse.data.*;

import java.util.StringTokenizer;
import java.util.*;
import admin.*;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.text.DecimalFormat;


/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Feb 2, 2008
 * Time: 6:17:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppletDataHandler1 {

    public static class Item {
        public String key;
        public String color;
    }

    public static class LinkItem {
        public String key;
        public String color;
    }

    public static class Group {
        public String key;
        public String color;
    }

    private MainFrame mainFrame;

    private String[] nodeList = null;
    private String[] loginList = null;
    private String[] initialHiddenNodes = null;
    private String[] edgeList = null;
    private String[] recList = null;
    private boolean hasLegend = false;
    private boolean hasLinkLegend = false;
    private Item legendItems[] = null;
    private LinkItem linkLegendItems[] = null;
    private Group groups[] = null;
    private double minEdge = Double.POSITIVE_INFINITY;
    private double maxEdge = 0.0;
    private String target;
    private String[] colors;
    private String[] nodetypes;
    private String[] edgeColors = null;
    private String[] xs = null;
    private String[] ys = null;

    private String legendStr, linkStr, groupStr;

    private List<String> hideNodes;
    private List<String> hideEdges;
    private int nodeSizeType = 0;
    private String sizeLabel;
    private String size2Label;
    private double valueMax1, valueMin1, valueMax2, valueMin2, valueMax3, valueMin3, valueMax4, valueMin4, valueMax5, valueMin5;

    private String[] strengths;
    private String[] nodeSizes;
    private String[] nodeSizes2;
    private String[] recPara;
    private String invisibleEdge;
    public boolean isRec, addInvisibleColumn, hasNoRecEdge;
    private boolean zeroMin, zeroMin2, recForAttri, fromFile;
    private Map<String, String> htmlparas;

    private Map<String, List<String>> nodeAttri;       // node info from graphML
    private FileReader reader;
    private String title;
    private prefuse.data.Graph g;
    private String imageLocation, hiddenNodesStr, hiddenEdgesStr, recRequestor;
    private double seMax = 0.00;
    private double seMin = 0.00;
    private Map<String, Double> login_recSeStr;       // store original recSe by similarity

    /**
     * constructs a new IKNOWDataHandler, parsing all parameter tags
     */
    public AppletDataHandler1(MainFrame _mainFrame, Map<String, String> htmlparas, FileReader reader) {

        mainFrame = _mainFrame;

        if (reader != null) {
            try {
                imageLocation = reader.getImageLocation();
            } catch (Exception e) {
                System.out.println("Exception when locating images: " + e);

            }
        }

        this.reader = reader;

        login_recSeStr = new HashMap<String, Double>();
        hideNodes = new ArrayList<String>();
        hideEdges = new ArrayList<String>();
        this.htmlparas = htmlparas;

        if (htmlparas != null)
            fromFile = true;


        nodeList = getActors();
        loginList = getLogins();
        colors = getColors();
        edgeColors = getEdgeColors();

        if (fromFile) {

            if (htmlparas.get("x") != null) {

                xs = getXs();
                ys = getYs();
            }
        } else {

            if (mainFrame.getParameter("x") != null) {

                xs = getXs();
                ys = getYs();
            }
        }
        edgeList = getEdgePair();
        nodetypes = getNodeType();

        String s = null;
        String nodeSize = null;
        String nodeSize2 = null;
        if (fromFile) {
            s = trimList(htmlparas.get("strengths"));
            nodeSize = htmlparas.get("nodeSize");
            nodeSize2 = htmlparas.get("nodeSize2");
            title = htmlparas.get("title");
            if (title.startsWith("RecScores")) {
                if (htmlparas.get("hiddenNodes").contains("1"))
                    addInvisibleColumn = true;
                if (htmlparas.get("hiddenEdges").contains("3"))
                    hasNoRecEdge = true;
                String finalhiddenNode = htmlparas.get("hiddenNodes").replaceAll("1", "0");
                finalhiddenNode = finalhiddenNode.replaceAll("2", "1");
                hiddenNodesStr = trimList(finalhiddenNode);
                String finalhiddenEdge = htmlparas.get("hiddenEdges").replaceAll("1", "0");
                finalhiddenEdge = finalhiddenEdge.replaceAll("2", "0");
                finalhiddenEdge = finalhiddenEdge.replaceAll("3", "1");
                hiddenEdgesStr = trimList(finalhiddenEdge);

            } else {
                hiddenNodesStr = trimList(htmlparas.get("hiddenNodes"));
                hiddenEdgesStr = trimList(htmlparas.get("hiddenEdges"));
            }

        } else {
            s = trimList(mainFrame.getParameter("strengths"));
            nodeSize = mainFrame.getParameter("nodeSize");
            nodeSize2 = mainFrame.getParameter("nodeSize2");
            title = mainFrame.getParameter("title");
            if (title.startsWith("RecScores")) {
                if (mainFrame.getParameter("hiddenNodes").contains("1"))
                    addInvisibleColumn = true;
                if (mainFrame.getParameter("hiddenEdges").contains("3"))
                    hasNoRecEdge = true;

                String finalhiddenNode = mainFrame.getParameter("hiddenNodes").replaceAll("1", "0");
                finalhiddenNode = finalhiddenNode.replaceAll("2", "1");
                hiddenNodesStr = trimList(finalhiddenNode);

                String finalhiddenEdge = mainFrame.getParameter("hiddenEdges").replaceAll("1", "0");
                finalhiddenEdge = finalhiddenEdge.replaceAll("2", "0");
                finalhiddenEdge = finalhiddenEdge.replaceAll("3", "1");
                hiddenEdgesStr = trimList(finalhiddenEdge);

            } else {
                hiddenNodesStr = trimList(mainFrame.getParameter("hiddenNodes"));
                hiddenEdgesStr = trimList(mainFrame.getParameter("hiddenEdges"));
            }
        }

      
        strengths = makeArrayFromStr(s);

        if (nodeSize != null) {
            try {

                if (nodeSize.length() > 0)
                    nodeSizeType = 1;
                if (nodeSize2 != null) {
                    if (nodeSize2.length() > 0)
                        nodeSizeType = 2;
                }
            } catch (Exception e) {
                System.out.println("Exception when getting node resize data : " + e);
            }
        }

        if (title != null) {
            try {

                if (title.startsWith("RecScores"))
                    nodeSizeType = 3;

            } catch (Exception e) {
                System.out.println("Exception when getting nodeSizeType : " + e);
            }
        }


        if (nodeSizeType > 0 && nodeSizeType < 3) {
            String sizeStr = trimList(nodeSize);
            int index = sizeStr.indexOf("||");
            sizeLabel = sizeStr.substring(0, index);
            nodeSizes = makeArrayFromStr(sizeStr.substring(index + 1));

        }


        if (nodeSizeType == 2) {
            String sizeStr2 = trimList(nodeSize2);
            int index = sizeStr2.indexOf("||");
            size2Label = sizeStr2.substring(0, index);
            nodeSizes2 = makeArrayFromStr(sizeStr2.substring(index + 1));

        }


        if (title.startsWith("Recommendation")) {
            isRec = true;
        }
        loadLegend();

        g = getGraph();
        makeGraphEdges();
        loadLinkLegend();
     loadGroup();

    }
    public String getRecTarget() {
        return target;
    }

    public prefuse.data.Graph getInitGraph() {
        return g;
    }

    /**
     * gets an ordered list of the actors in the network
     *
     * @return array of <code>String<code>s which actor names,
     *         or <code>null</code> if none given
     */
    public String[] getActors() {
        if (nodeList == null) {
            String actors = "";
            try {
                if (fromFile) {
                    if (htmlparas.get("nodes") != null)
                        actors = trimList(htmlparas.get("nodes"));
                } else {
                    if (mainFrame.getParameter("nodes") != null)
                        actors = trimList(mainFrame.getParameter("nodes"));
                }
                nodeList = makeArrayFromStr(actors);

            }
            catch (NullPointerException e) {

                System.out.println("Exception when reading node label data: " + e);

            }
        }

        return nodeList;
    }

    public int getNodesCount() {
        return loginList.length;
    }

    /*  public String[] getImages() {
        if (nodeImages == null) {
            String images = "";
            try {

                images = trimList(mainFrame.getParameter("images"));
                System.out.println("images: " + images.length());

            }
            catch (NullPointerException e) {
                System.out.println("can not get node images" + e);
            }
            nodeImages = makeArrayFromStr(images);
        }

        return nodeImages;
    }*/

    public String[] getLogins() {
        if (loginList == null) {
            String actors = "";
            try {
                if (fromFile)
                    actors = trimList(htmlparas.get("login"));
                else
                    actors = trimList(mainFrame.getParameter("login"));
                loginList = makeArrayFromStr(actors);
            }
            catch (NullPointerException e) {
                System.out.println("Exception when reading login data: " + e);

            }

        }

        return loginList;
    }

    /*
    public String[] getBold() {
        if (boldList == null) {
            String bolds = "";
            try {


                bolds = trimList(mainFrame.getParameter("bold"));
            }
            catch (NullPointerException e) {
                System.out.println(e);
            }
            boldList = makeArrayFromStr(bolds);
        }

        return boldList;
    }

    public String[] getUnderline() {
        if (underlineList == null) {
            String underlines = "";
            try {
                underlines = trimList(mainFrame.getParameter("underline"));
            }
            catch (NullPointerException e) {
                System.out.println(e);
            }
            underlineList = makeArrayFromStr(underlines);
        }

        return underlineList;
    }

    public String[] getItalic() {
        if (italicList == null) {
            String italics = "";
            try {
                italics = trimList(mainFrame.getParameter("italic"));
            }
            catch (NullPointerException e) {
                System.out.println(e);
            }
            italicList = makeArrayFromStr(italics);
        }

        return italicList;
    }
     */
    public String[] getRecPara() {
        if (recList == null) {

            recList = makeArrayFromStr(title);
        }

        return recList;
    }

    public String[] getRecPath() {
        String[] recPath;

        String recs = "";
        try {

            if (fromFile)
                recs = trimList(htmlparas.get("path"));
            else
                recs = trimList(mainFrame.getParameter("path"));
        }
        catch (NullPointerException e) {
            System.out.println(e);
        }
        recPath = makeArrayFromStr(recs);

        return recPath;
    }

    public String getTitle() {
        return title;
    }


    public String[] getEdgePair() {
        if (edgeList == null) {
            String edgePair = "";
            try {

                if (fromFile)
                    edgePair = trimList(htmlparas.get("edges"));
                else
                    edgePair = trimList(mainFrame.getParameter("edges"));
            }
            catch (NullPointerException e) {

                //     if(mainFrame.getParameter("debug").equals("1"))
                System.out.println("Exception when getting edges: " + e);
                // else
                // System.out.println("Exception when getting edges");
            }
            edgeList = makeArrayFromStr(edgePair);
        }

        return edgeList;
    }


    public String[] getInitiallyHiddenNodes() {
        if (initialHiddenNodes == null) {

            initialHiddenNodes = makeArrayFromStr(hiddenNodesStr);
        }

        return initialHiddenNodes;
    }

    public boolean hiddenItemsExist() {

        if (title.startsWith("RecScores")) {
            return hasNoRecEdge;
        } else {
            String[] initialHiddenNodes = makeArrayFromStr(hiddenNodesStr);

            String[] initialHiddenEdges = makeArrayFromStr(hiddenEdgesStr);

            for (String s : initialHiddenNodes) {
                if (s.equals("1")) {
                    return true;
                }
            }

            for (String s : initialHiddenEdges) {
                if (s.equals("1")) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean noRecEdgeExist() {
        String[] nodetypes = getNodeType();
        int j = 0;
        for (int i = 0; i < nodetypes.length; i++) {
            if (nodetypes[i].equals("Recommendation"))
                j++;
        }

        if (j == 1 && hasNoRecEdge)
            return true;
        else
            return false;
    }

    public double getMinEdgeStrength() {
        return minEdge;
    }

    public double getMaxEdgeStrength() {
        return maxEdge;
    }

    /**
     * return whether we have a legend
     */
    public boolean hasLegend() {
        return hasLegend;
    }

    public boolean hasLinkLegend() {
        return hasLinkLegend;
    }

    /**
     * gets a list with the items for the legend pane; the constructor
     * calls loadLegend(), so we can just return the object
     */
    public Item[] getLegendItems() {
        return legendItems;
    }

    public LinkItem[] getLinkLegendItems() {
        return linkLegendItems;
    }

    public Group[] getGroups() {
        return groups;
    }

    /**
     * get the CGI URL for getting more info about a node
     *
     * @return the prefix
     */
    public String getNodeCGIPrefix() {
        String prefix;
        try {
            if (fromFile)
                prefix = htmlparas.get("node_prefix");
            else

                prefix = mainFrame.getParameter("node_prefix");
        }
        catch (NullPointerException e) {
            return null;
        }
        return prefix;
    }

    /**
     * get the CGI URL for getting more info about an edge
     *
     * @return the prefix
     */
    public String getLinkCGIPrefix() {
        String prefix;
        try {
            if (fromFile)
                prefix = htmlparas.get("link_prefix");
            else
                prefix = mainFrame.getParameter("link_prefix");
        }
        catch (NullPointerException e) {
            return null;
        }
        return prefix;
    }


    public String[] getColors() {
        String c;

        try {
            if (fromFile)
                c = trimList(htmlparas.get("colors"));
            else
                c = trimList(mainFrame.getParameter("colors"));
            colors = makeArrayFromStr(c);

        }
        catch (NullPointerException e) {
            //    if(mainFrame.getParameter("debug").equals("1"))
            System.out.println("Exception when reading node colors: " + e);
            //       else
            //       System.out.println("Exception when reading node colors");
        }
        return colors;
    }

    public String[] getNodeType() {

        String nodetype;
        try {
            if (fromFile)
                nodetype = trimList(htmlparas.get("nodeTypes"));
            else
                nodetype = trimList(mainFrame.getParameter("nodeTypes"));
            nodetypes = makeArrayFromStr(nodetype);

        }
        catch (NullPointerException e) {
            System.out.println("Exception when reading node types: " + e);
        }
        return nodetypes;
    }

    public String getRecRequestor() {
        return recRequestor;

    }
    public String[] getOriNodeIndex() {
        String[] oriNodes;
        String oriNodeIndex;
        try {

            if (fromFile)
                oriNodeIndex = trimList(htmlparas.get("oriNodeIndex"));
            else
                oriNodeIndex = trimList(mainFrame.getParameter("oriNodeIndex"));
            oriNodes = makeArrayFromStr(oriNodeIndex);
        }
        catch (NullPointerException e) {
            return null;
        }
        return oriNodes;
    }

    public String[] getEdgeColors() {
        String c;

        try {
            if (fromFile)
                c = trimList(htmlparas.get("edgeColors"));
            else
                c = trimList(mainFrame.getParameter("edgeColors"));
            edgeColors = makeArrayFromStr(c);

        }
        catch (NullPointerException e) {
            //  if(mainFrame.getParameter("debug").equals("1"))
            System.out.println("Exception when reading edge colors: " + e);
            //     else
            //     System.out.println("Exception when reading edge colors");

        }
        return edgeColors;
    }


    public String[] getXs() {
        String x;

        try {
            if (fromFile)
                x = trimList(htmlparas.get("x"));
            else
                x = trimList(mainFrame.getParameter("x"));
            xs = makeArrayFromStr(x);

        }
        catch (NullPointerException e) {
            //  if(mainFrame.getParameter("debug").equals("1"))
            System.out.println("Exception when getting x coordinate: " + e);
            //   else
            //         System.out.println("Exception when getting x coordinate");

        }
        return xs;
    }

    public String[] getYs() {
        String y;

        try {
            if (fromFile)
                y = trimList(htmlparas.get("y"));
            else
                y = trimList(mainFrame.getParameter("y"));
            ys = makeArrayFromStr(y);

        }
        catch (NullPointerException e) {
            //  if(mainFrame.getParameter("debug").equals("1"))
            System.out.println("Exception when getting y coordinate: " + e);
            //  else
            //    System.out.println("Exception when getting y coordinate");
        }
        return ys;
    }

    /**
     * Returns the CGI URL
     */
/* public String getCGIURL() {

   String prefix;
   try {

       prefix = mainFrame.getParameter("cgi_url");
   } catch (NullPointerException e) {
       return null;
   }
   return prefix;
}
*/
//	-- PRIVATE --
    private void loadLegend() {
        String param = null;
        String[] items;
        hasLegend = false;
        try {
            if (fromFile)
                param = trimList(htmlparas.get("thelegend"));
            else
                param = trimList(mainFrame.getParameter("thelegend"));
        }
        catch (NullPointerException e) {
            System.out.println("exception when reading legend: " + e);
        }
        items = makeArrayFromStr(param);
        if (items.length == 0)
            return;
        legendItems = new Item[items.length];

        ArrayList<String> legentList = new ArrayList<String>();
        String missingStr = null;
        String notApplicableStr = null;

        for (int i = 0; i < items.length; ++i) {
            String keyStr = items[i].substring(0, items[i].lastIndexOf('-'));
            if (!keyStr.equals("MISSING VALUE") && !keyStr.equals("NOT APPLICABLE"))
                legentList.add(items[i]);
            else if (keyStr.equals("MISSING VALUE"))
                missingStr = items[i];
            else if (keyStr.equals("NOT APPLICABLE"))
                notApplicableStr = items[i];
        }
        if (missingStr != null)
            legentList.add(missingStr);
        if (notApplicableStr != null)
            legentList.add(notApplicableStr);

        int m = 0;
        for (String legendStr : legentList) {
            legendItems[m] = new Item();
            legendItems[m].color = legendStr.substring(1 + legendStr.lastIndexOf('-'), legendStr.length());
            legendItems[m].key = legendStr.substring(0, legendStr.lastIndexOf('-'));
            m++;
        }

        hasLegend = true;
        legendStr = param;
    }


    public String getColorQuestion() {

        String colorQuestion;
        try {
            if (fromFile)
                colorQuestion = htmlparas.get("colorBy");
            else
                colorQuestion = mainFrame.getParameter("colorBy");
        } catch (NullPointerException e) {
            System.out.println("exception when reading colorBy: " + e);
            return null;
        }
        return colorQuestion;
    }


    public String getShapeBy() {

        String shapeBy;
        try {
            if (fromFile)
                shapeBy = htmlparas.get("shapeBy");
            else
                shapeBy = mainFrame.getParameter("shapeBy");
        } catch (NullPointerException e) {
            return null;
        }
        return shapeBy;
    }

    public String getGroupBy() {

        String groupBy;
        try {
            if (fromFile)
                groupBy = htmlparas.get("groupBy");
            else
                groupBy = mainFrame.getParameter("groupBy");
        } catch (NullPointerException e) {
            return null;
        }
        return groupBy;
    }


    public String getOS() {

        String os;
        try {
            if (fromFile)
                os = htmlparas.get("os");
            else
                os = mainFrame.getParameter("os");
        } catch (NullPointerException e) {
            return null;
        }
        return os;
    }

    public String[] loadColorAttributes() {
        String attris[] = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < legendItems.length; j++) {
                if (colors[i].equalsIgnoreCase(legendItems[j].color))
                    attris[i] = legendItems[j].key;
            }
        }

        return attris;
    }

    private void loadLinkLegend() {
        String param;
        String[] items;
        hasLinkLegend = false;
        try {
            if (fromFile)
                param = trimList(htmlparas.get("linkLegend"));
            else
                param = trimList(mainFrame.getParameter("linkLegend"));
        }
        catch (NullPointerException e) {
            System.out.println("Exception when reading edge legend: " + e);
            return;
        }
        items = makeArrayFromStr(param);

        if (items.length == 0)
            return;
        linkLegendItems = new LinkItem[items.length];
        for (int i = 0; i < items.length; ++i) {
            try {
                linkLegendItems[i] = new LinkItem();
                linkLegendItems[i].color = items[i].substring(1 + items[i].lastIndexOf('-'), items[i].length());
                linkLegendItems[i].key = items[i].substring(0, items[i].lastIndexOf('-'));
            } catch (Exception e) {
                System.out.println("Exception when loading linkLegendItem: " + e);

            }

        }
        hasLinkLegend = true;
        linkStr = param;

    }


    private void loadGroup() {
        String param;
        String[] items;

        try {
            if (fromFile)
                param = trimList(htmlparas.get("group"));
            else
                param = trimList(mainFrame.getParameter("group"));
        }
        catch (NullPointerException e) {
            System.out.println("Exception when reading group legend: " + e);
            return;
        }
        items = makeArrayFromStr(param);

        if (items.length == 0)
            return;
        groups = new Group[items.length];
        for (int i = 0; i < items.length; ++i) {
            try {
                groups[i] = new Group();
                groups[i].color = items[i].substring(1 + items[i].lastIndexOf('-'), items[i].length());
                groups[i].key = items[i].substring(0, items[i].lastIndexOf('-'));
            } catch (Exception e) {
                System.out.println("Exception when loading grouping legend: " + e);
            }

        }

        groupStr = param;
    }

    /**
     * IKNOW likes to put extra commas at the end of comma-delim
     * lists; strip those
     *
     * @param str string to strip
     * @return stripped string
     */
    public String trimList(String str) {
        return str.endsWith("||,") ? str.substring(0, str.length() - 2) : str;
    }


    public String getLengendStr() {
        return legendStr;
    }

    public String getLinkStr() {
        return linkStr;
    }

    public String getGroupStr() {
        return groupStr;
    }

    /**
     * scans through a comma-delimitd list and produces an
     * array containing each element
     *
     * @param str string to scan
     * @return <code>String[]</code> with elements
     *         corresponding to str
     */
    public String[] makeArrayFromStr(String str) {
        ArrayList<String> tmp = new ArrayList<String>();
        for (StringTokenizer t = new StringTokenizer(str, "||");
             t.hasMoreTokens();) {
            tmp.add(t.nextToken());
        }
        return tmp.toArray(new String[tmp.size()]);
    }


    public int getNodeSizeType() {
        return nodeSizeType;
    }

    public String getSizeLabel() {
        return sizeLabel;
    }

    public String getSize2Label() {
        return size2Label;
    }

    public prefuse.data.Graph getGraph() {


        Table nodes = new Table();
        nodes.addColumn("nodeKey", int.class);
        nodes.addColumn("label", String.class);
        nodes.addColumn("noLabel", String.class);
        nodes.addColumn("nodeType", String.class);
        nodes.addColumn("groupAttri", String.class);
        nodes.addColumn("shapeAttri", String.class);
        nodes.addColumn("disLabel", String.class);
        nodes.addColumn("focusLabel", String.class);
        nodes.addColumn("hiddenItem", String.class);
        nodes.addColumn("login", String.class);
        nodes.addColumn("nodeColor", String.class);
        nodes.addColumn("image", String.class);
        nodes.addColumn("userName", String.class);
        if (getLabelNodeStatus() != null) {
            if (getLabelNodeStatus().equalsIgnoreCase("1")) {
                nodes.addColumn("isNodeLabel", String.class);
            }
        }
        if (nodeSizeType > 0 && nodeSizeType < 3) {
            if (nodeSizeType == 2) {
                nodes.addColumn("nodeSize2", double.class);
            }
            nodes.addColumn("nodeSize", double.class);
        }

        if (getColorQuestion() != null) {
            if (!getColorQuestion().equalsIgnoreCase("NodeType")) {
                nodes.addColumn("colorAttri", String.class);
            }
        }

        if (xs != null) {

            nodes.addColumn("x", double.class);
            nodes.addColumn("y", double.class);
        }

        if (title.startsWith("RecScores") || title.startsWith("Recommendation")) {
            nodes.addColumn("recId", double.class);
            nodes.addColumn("recSe", double.class);
            nodes.addColumn("recScore", double.class);
        }

        if (title.startsWith("RecScores"))
            nodes.addColumn("invisibleItem", String.class);


        Table edges = new Table();
        edges.addColumn("sourceKey", int.class);
        edges.addColumn("targetKey", int.class);


        edges.addColumn("edgeKey", String.class);
        edges.addColumn("edgeType", String.class);
        edges.addColumn("edgeTypeDis", String.class);
        edges.addColumn("nodePairType", String.class);
        edges.addColumn("hiddenItem", String.class);
        edges.addColumn("direction", String.class);
        edges.addColumn("edgeColor", String.class);
        edges.addColumn("noWeightLine", double.class);
        edges.addColumn("edgeWeight", double.class);
        edges.addColumn("weightString", String.class);
        edges.addColumn("multiEdges", String.class);

        if (title.startsWith("RecScores"))
            edges.addColumn("invisibleItem", String.class);


        Graph g = new prefuse.data.Graph(nodes, edges, true,
                "nodeKey", "sourceKey", "targetKey");

        String nodetype, groupAttri, shapeAttri, loginStr, image, username;
        String nodelabel = null;
        String invisibleNode = null;
// try {
        if (fromFile) {
            if (getLabelNodeStatus() != null) {
                if (getLabelNodeStatus().equalsIgnoreCase("1"))
                    nodelabel = trimList(htmlparas.get("nodelabel"));
            }
            nodetype = trimList(htmlparas.get("nodeTypes"));
            groupAttri = trimList(htmlparas.get("groupAttri"));
            shapeAttri = trimList(htmlparas.get("shapeAttri"));
            loginStr = trimList(htmlparas.get("login"));

            image = trimList(htmlparas.get("images"));
            username = trimList(htmlparas.get("username"));
            if (title.startsWith("RecScores"))
                invisibleNode = trimList(htmlparas.get("hiddenNodes"));

        } else {

            if (getLabelNodeStatus() != null) {
                if (getLabelNodeStatus().equalsIgnoreCase("1"))
                    nodelabel = trimList(mainFrame.getParameter("nodelabel"));
            }
            nodetype = trimList(mainFrame.getParameter("nodeTypes"));
            groupAttri = trimList(mainFrame.getParameter("groupAttri"));
            shapeAttri = trimList(mainFrame.getParameter("shapeAttri"));
            loginStr = trimList(mainFrame.getParameter("login"));

            image = trimList(mainFrame.getParameter("images"));
            username = trimList(mainFrame.getParameter("username"));
            if (title.startsWith("RecScores"))
                invisibleNode = trimList(mainFrame.getParameter("hiddenNodes"));

        }

        String[] nodelabels = null;
        String[] invisibleNodes = null;
        if (nodelabel != null)
            nodelabels = makeArrayFromStr(nodelabel);


        String[] nodetypes = makeArrayFromStr(nodetype);
        String[] groupAttris = makeArrayFromStr(groupAttri);
        String[] shapeAttris = makeArrayFromStr(shapeAttri);
        String[] hiddenNodes = makeArrayFromStr(hiddenNodesStr);
        String[] logins = makeArrayFromStr(loginStr);
        String[] images = makeArrayFromStr(image);
        String[] userNames = makeArrayFromStr(username);

        if (title.startsWith("RecScores"))
            invisibleNodes = makeArrayFromStr(invisibleNode);
        recPara = getRecPara();

        int j = 2;
        int m = 0;
        if (title.startsWith("Recommendation"))
            target = nodeList[loginList.length - 1];
       
        for (int i = 0; i < loginList.length; i++) {

            Node n = g.addNode();
            try {
                n.setInt("nodeKey", i);
                String nodeLabel = "";
                if (getLabelHiden().equalsIgnoreCase("2"))
                    n.setString("label", "  ");
                else {

                    nodeLabel = nodeList[i].trim();

                    n.setString("label", nodeLabel);
                }
                n.setString("noLabel", "  ");
                n.setString("nodeType", nodetypes[i]);
                n.setString("shapeAttri", shapeAttris[i]);
                n.setString("groupAttri", groupAttris[i]);
                n.setString("nodeColor", colors[i]);

                if (title.startsWith("RecScores"))
                    n.setString("invisibleItem", invisibleNodes[i]);

                if (getColorQuestion() != null) {
                    if (!getColorQuestion().equalsIgnoreCase("NodeType")) {
                        n.setString("colorAttri", loadColorAttributes()[i]);
                    }
                }
                n.setString("login", logins[i]);
                if (nodeSizeType > 0 && nodeSizeType < 3) {

                    n.setDouble("nodeSize", Double.parseDouble(nodeSizes[i]));


                    if (nodeSizeType == 2) {

                        n.setDouble("nodeSize2", Double.parseDouble(nodeSizes2[i]));

                    }
                }

                if (xs != null) {
                    n.setDouble("x", Double.parseDouble(xs[i]));
                    n.setDouble("y", Double.parseDouble(ys[i]));
                }


                if (getLabelNodeStatus() != null) {
                    if (getLabelNodeStatus().equalsIgnoreCase("1"))
                        n.setString("isNodeLabel", nodelabels[i]);
                }
                String dl = nodeLabel;

                if (nodetypes[i].equals("Requester"))

                    recRequestor = nodeLabel;
                if (nodetypes[i].equals("Requester") || nodetypes[i].equals("Recommendation") || nodetypes[i].equals("Target")) {
                    if (dl.length() > 10) {
                        dl = dl.substring(0, 10).trim() + "..";
                    }

                } else {
                    if (dl.length() > 13) {
                        dl = dl.substring(0, 13).trim() + "..";
                    }
                }
                n.setString("disLabel", dl);


                n.setString("hiddenItem", hiddenNodes[i]);
                n.setString("userName", userNames[i]);

                if (images[i].equalsIgnoreCase("1")) {
                    //saved image location
                    String imageName;
                    String separator = System.getProperty("file.separator");

                    if (fromFile) {
                        if (getNodeDisBy() != null) {
                            if (getNodeDisBy().equalsIgnoreCase("4"))
                                imageLocation = htmlparas.get("imageLocation");
                        }
                        imageName = imageLocation + separator + "images" + separator + userNames[i] + ".jpg";
                    } else {
                        imageName = mainFrame.getCodeBase().toString() + "/images/photos/" + userNames[i] + ".jpg";
                    }
                    n.setString("image", imageName);
                } else {
                    n.setString("image", null);
                }


                if (hiddenNodes[i].equalsIgnoreCase("1")) {
                    hideNodes.add(String.valueOf(n.getRow()));
                }
            } catch (Exception e) {
                System.out.println("Exception when setting nodes for the graph: " + e);
            }

            if (title.startsWith("RecScores")) {
                String seMetric = getAttriRecSeMetric();
                getMAxMinSe();

                try {
                    if (((addInvisibleColumn) && invisibleNodes[i].equalsIgnoreCase("0")) || (!addInvisibleColumn)) {
                        
                        if (i == 0) {

                            n.setDouble("recId", 2.00);
                            n.setDouble("recSe", 0.0);
                            n.setDouble("recScore", 0.0);

                        } else if (i == loginList.length - 1) {

                            if (recPara[m].contains("-")) {
                                String[] recstr = recPara[m].split("-");
                                n.setDouble("recId", Double.parseDouble(recstr[1]));

                               double se = Double.parseDouble(recstr[0]);

                                    login_recSeStr.put(logins[i], se);

                                    if (seMax == seMin) {

                                        se = 1.0;

                                    } else {

                                        se = 0.3333 + (((se - seMin) / (seMax - seMin)) * (1.00 - 0.3333));

                                    }

                                n.setDouble("recSe", se);

                                n.setDouble("recScore", Double.parseDouble(recstr[2]));
                                recForAttri = true;


                            } else {

                                n.setDouble("recId", 0.0);
                                n.setDouble("recSe", 0.0);
                                n.setDouble("recScore", 0.0);

                            }


                        } else {

                            String[] recstr = recPara[m].split("-");
                            n.setDouble("recId", Double.parseDouble(recstr[1]));

                            double se = Double.parseDouble(recstr[0]);

                                login_recSeStr.put(logins[i], se);

                                if (seMax == seMin) {
                                    se = 1.0;

                                } else {
                                    se = 0.3333 + (((se - seMin) / (seMax - seMin)) * (1.00 - 0.3333));                                   
                                }

                            n.setDouble("recSe", se);

                            n.setDouble("recScore", Double.parseDouble(recstr[2]));

                        }
                        m++;
                    } else {

                        n.setDouble("recId", 1.0);
////////  llll
                        n.setDouble("recSe", 0.0);
                        n.setDouble("recScore", 0.0);

                    }


                } catch (Exception e) {
                    System.out.println("Exception when processing recscore:" + e);
                }


            } else if (title.startsWith("Recommendation")) {

                if (nodetypes[i].equals("Recommendation")) {
                    String[] recstr = recPara[j].split("-");
                    n.setDouble("recId", Double.parseDouble(recstr[1]));
                    n.setDouble("recSe", Double.parseDouble(recstr[0]));
                    n.setDouble("recScore", Double.parseDouble(recstr[2]));
                    j++;
                } else {

                    n.setDouble("recId", 0.0);
                    n.setDouble("recSe", 0.0);
                    n.setDouble("recScore", 0.0);
                }

            }

        }

        String e, edgetype, edgetypeDis, direction;

        if (fromFile) {
            e = trimList(htmlparas.get("edges"));
            edgetype = trimList(htmlparas.get("edgeTypes"));
            edgetypeDis = trimList(htmlparas.get("edgeTypeDis"));
            direction = trimList(htmlparas.get("edgeDirection"));
            if (title.startsWith("RecScores")) {
                invisibleEdge = trimList(htmlparas.get("hiddenEdges"));
            }
        } else {

            e = trimList(mainFrame.getParameter("edges"));

            edgetype = trimList(mainFrame.getParameter("edgeTypes"));

            edgetypeDis = trimList(mainFrame.getParameter("edgeTypeDis"));

            direction = trimList(mainFrame.getParameter("edgeDirection"));
            if (title.startsWith("RecScores")) {
                invisibleEdge = trimList(mainFrame.getParameter("hiddenEdges"));

            }
        }

        String[] arrayEdges = makeArrayFromStr(e);
        String[] edgetypes = makeArrayFromStr(edgetype);
        String[] edgetypeDiss = makeArrayFromStr(edgetypeDis);
        String[] hiddenEdges = makeArrayFromStr(hiddenEdgesStr);
        String[] directions = makeArrayFromStr(direction);

        String[] invisibleEdges = null;

        if (title.startsWith("RecScores"))
            invisibleEdges = makeArrayFromStr(invisibleEdge);

        int from, to;

        for (int i = 0; i < arrayEdges.length; ++i) {

            from = Integer.valueOf(arrayEdges[i].substring(0, arrayEdges[i].indexOf('-'))) - 1;
            to = Integer.valueOf(arrayEdges[i].substring(1 + arrayEdges[i].indexOf('-'), arrayEdges[i].length())) - 1;

            Node fromNode = g.getNode(from);
            Node toNode = g.getNode(to);
            Edge edge = g.addEdge(fromNode, toNode);

            try {
                edge.setInt("sourceKey", from);
                edge.setInt("targetKey", to);

                edge.setString("edgeKey", String.valueOf(i));
                edge.setString("edgeType", edgetypes[i]);

                edge.setString("edgeTypeDis", edgetypeDiss[i]);
                edge.setString("edgeColor", edgeColors[i]);

                edge.setString("nodePairType", arrayEdges[i] + "&" + edgetypes[i]);
                edge.setString("hiddenItem", hiddenEdges[i]);

                edge.setString("direction", directions[i]);
                if (title.startsWith("RecScores"))
                    edge.setString("invisibleItem", invisibleEdges[i]);

                if (hiddenEdges[i].equalsIgnoreCase("1")) {
                    hideEdges.add(String.valueOf(edge.getRow()));
                }

                edge.setString("weightString", strengths[i]);

// for limit edge
                edge.setDouble("edgeWeight", Double.parseDouble(strengths[i]));
// for edge thickness
                edge.setDouble("noWeightLine", 1.00);
            } catch (Exception ex) {
                System.out.println("Exception when setting edges for graph: " + ex);
            }
        }

        // } catch (Exception e) {
        //    System.out.println("Exception when mapping data to graph" + e);
        //  }
        return g;

    }

    public prefuse.data.Graph setGraphPara(boolean toInit, boolean showOrHide, boolean fromInit, Set<String> showOrHideNodes, Set<String> showOrHideEdges, Map<String, String> nodeColors, Map<String, String> edgeColors, String datatype) {

        Graph g = getGraph();
        for (int i = 0; i < g.getNodeCount(); ++i) {

            Node n = g.getNode(i);
            if (nodeColors != null) {
                for (String nodeType : nodeColors.keySet()) {

                    if (datatype.equals("nodeType")) {
                        if (n.getString("nodeType").equals(nodeType)) {

                            n.setString("nodeColor", nodeColors.get(nodeType));
                        }
                    } else {

                        if (n.getString("colorAttri").equals(nodeType)) {

                            n.setString("nodeColor", nodeColors.get(nodeType));
                        }
                    }
                }

            }
            if (!showOrHide) {
                if (toInit) {
                    for (String nodeRow : hideNodes) {
                        if (n.getRow() == Integer.parseInt(nodeRow)) {
                            n.setString("hiddenItem", "1");
                        }
                    }

                } else {
                    for (String nodeRow : hideNodes) {

                        if (n.getRow() == Integer.parseInt(nodeRow)) {

                            n.setString("hiddenItem", "0");
                        }
                    }

                }

            } else {

                if (fromInit) {
                    for (String nodeKey : showOrHideNodes) {
                        if (n.getString("nodeKey").equalsIgnoreCase(nodeKey)) {
                            n.setString("hiddenItem", "1");

                        }
                    }
                } else {
                    for (String nodeRow : hideNodes) {

                        if (n.getRow() == Integer.parseInt(nodeRow)) {

                            n.setString("hiddenItem", "0");
                        }
                    }

                    for (String nodeKey : showOrHideNodes) {
                        if (n.getString("nodeKey").equalsIgnoreCase(nodeKey)) {
                            n.setString("hiddenItem", "1");

                        }
                    }
                }
            }

        }//for

        // edge business

        for (int i = 0; i < g.getEdgeCount(); ++i) {

            Edge e = g.getEdge(i);
            if (edgeColors != null) {
                for (String edgeTypeDis : edgeColors.keySet()) {
                    if (e.getString("edgeTypeDis").equals(edgeTypeDis)) {
                        e.setString("edgeColor", edgeColors.get(edgeTypeDis));
                    }
                }

            }

            if (!showOrHide) {
                if (toInit) {
                    for (String edgeRow : hideEdges) {
                        if (e.getRow() == Integer.parseInt(edgeRow)) {
                            e.setString("hiddenItem", "1");
                        }
                    }

                } else {
                    for (String edgeRow : hideEdges) {
                        if (e.getRow() == Integer.parseInt(edgeRow)) {
                            e.setString("hiddenItem", "0");
                        }
                    }

                }

            } else {
                if (fromInit) {
                    for (String edgeKey : showOrHideEdges) {

                        if (e.getString("edgeKey").equalsIgnoreCase(edgeKey) && e.isValid()) {

                            e.setString("hiddenItem", "1");

                        }
                    }
                } else {
                    for (String edgeRow : hideEdges) {
                        if (e.getRow() == Integer.parseInt(edgeRow)) {
                            e.setString("hiddenItem", "0");
                        }
                    }
                    for (String edgeKey : showOrHideEdges) {

                        if (e.getString("edgeKey").equalsIgnoreCase(edgeKey) && e.isValid()) {

                            e.setString("hiddenItem", "1");

                        }
                    }
                }

            }

        }//for


        return g;
    }

    public Map<String, Double> getLogin_recSeStr() {

        return login_recSeStr;
    }
    private void getMAxMinSe() {

        for (int i = 0; i < recPara.length; i++) {

            if (recPara[i].contains("-")) {
                String[] recstr = recPara[i].split("-");

                double se = Double.parseDouble(recstr[0]);
                seMax = Math.max(seMax, se);

            }

        }
        seMin = seMax;
        for (int i = 0; i < recPara.length; i++) {
            if (recPara[i].contains("-")) {
                String[] recstr = recPara[i].split("-");

                double se = Double.parseDouble(recstr[0]);
                seMin = Math.min(seMin, se);

            }

        }
    }

// for node label and color

    public Vector getVertex
            () {
        Vector<Vertex1> v = new Vector<Vertex1>();


        {

            for (int i = 0; i < nodeList.length; ++i) {


                Vertex1 vt = new Vertex1();
                vt.setName(nodeList[i]);
                vt.setLogin(loginList[i]);
                vt.setColor(colors[i]);
                vt.setType(nodetypes[i]);
                v.add(vt);
            }


            return v;
        }
    }

    // for edge and color
    public Vector getEdge
            () {
        Vector<Edge1> v = new Vector<Edge1>();


        {

            for (int i = 0; i < edgeColors.length; ++i) {


                Edge1 vt = new Edge1();
                vt.setName(String.valueOf(i));

                vt.setColor(edgeColors[i]);
                v.add(vt);
            }


            return v;
        }

    }

    private double getLinearSize
            (
                    double currentSize,
                    double valueMin,
                    double valueMax,
                    double displayMin,
                    double displayMax,
                    boolean zeroMin) {

        if (zeroMin)
            valueMin = 1;
        double size = displayMin + (((currentSize - valueMin) / (valueMax - valueMin)) * (displayMax - displayMin));

        if ((size < 0) || Double.isNaN(size) || Double.isInfinite(size))
            size = displayMin;

        return size;
    }

    private double getLogSize
            (
                    double currentSize,
                    double valueMin,
                    double valueMax,
                    double displayMin,
                    double displayMax,
                    boolean zeroMin) {
        if (zeroMin)
            valueMin = 1;
        double size = displayMin + ((Math.log(currentSize / valueMin) / Math.log(valueMax / valueMin)) * (displayMax - displayMin));

        if ((size < 0) || Double.isNaN(size) || Double.isInfinite(size))
            size = displayMin;

        return size;
    }


    public double getLinearSize
            (
                    double currentSize,
                    double displayMin,
                    double displayMax,
                    boolean zeroMin,
                    int sizeType) {

        double valueMin = 0;
        double valueMax = 0;
        if (sizeType == 4) {
            valueMin = valueMin4;
            valueMax = valueMax4;
        } else if (sizeType == 5) {
            valueMin = valueMin5;
            valueMax = displayMax;
        } else if (sizeType == 3) {
            valueMin = valueMin3;
            valueMax = valueMax3;
        } else if (sizeType == 2) {
            valueMin = valueMin2;
            valueMax = valueMax2;
        } else if (sizeType == 1) {
            valueMin = valueMin1;
            valueMax = valueMax1;
        }
        return getLinearSize(currentSize, valueMin, valueMax, displayMin, displayMax, zeroMin);
    }

    public String getRecInviEdge
            () {
        return invisibleEdge;
    }

    public Map<String, List<String>> getNodeAttri
            () {
        return reader.getNodeAttriMap();
    }
    public double getLogSize(double currentSize, double displayMin, double displayMax, boolean zeroMin, int sizeType) {

        double valueMin = 0;
        double valueMax = 0;
        if (sizeType == 4) {
            valueMin = valueMin4;
            valueMax = valueMax4;
        } else if (sizeType == 5) {
            valueMin = valueMin5;
            valueMax = displayMax;
        } else if (sizeType == 3) {
            valueMin = valueMin3;
            valueMax = valueMax3;
        } else if (sizeType == 2) {
            valueMin = valueMin2;
            valueMax = valueMax2;
        } else if (sizeType == 1) {
            valueMin = valueMin1;
            valueMax = valueMax1;
        }
        return getLogSize(currentSize, valueMin, valueMax, displayMin, displayMax, zeroMin);
    }

    private boolean processMinMax
            (
                    int sizeType, String[]
                    sizeArray, double displayMin,
                               double displayMax) {

        boolean realsize = false;
        double[] wArray = null;

// identification value for recommendation
        if (sizeType == 4) {
            wArray = new double[sizeArray.length - 4];

            for (int i = 0; i < sizeArray.length - 2; i++) {
                if (i > 0 && i < sizeArray.length - 3) {
                    String[] recstr = sizeArray[i].split("-");
                    wArray[i - 1] = Math.abs(Double.parseDouble(recstr[0]));
                }
            }
            //
        } else if (sizeType == 5) { //se value
            wArray = new double[sizeArray.length - 4];

            for (int i = 0; i < sizeArray.length - 2; i++) {
                if (i > 0 && i < sizeArray.length - 3) {
                    String[] recstr = sizeArray[i].split("-");
                    wArray[i - 1] = Math.abs(Double.parseDouble(recstr[1]));
                }
            }
            //

        } else {
            wArray = new double[sizeArray.length];

            for (int i = 0; i < sizeArray.length; i++) {
                double w = Double.valueOf(sizeArray[i]);
                if (sizeType == 1) {
                    String weightForDis = strengths[i];

                    if (weightForDis.startsWith("-")) {
                        weightForDis = weightForDis.substring(1);
                        w = Double.valueOf(weightForDis);
                    }
                }

                wArray[i] = Math.abs(w);
            }
        }
        Arrays.sort(wArray);

        if (sizeType == 5) {

            double max = wArray[wArray.length - 1];

            double min = wArray[0];

            valueMin5 = min * displayMax / max;

            if (valueMin5 == 0 && sizeType != 1) {
                zeroMin2 = true;

            }

            if ((displayMax / valueMin5) < 20) {
                realsize = true;

            }

        } else if (sizeType == 4) {


            valueMax4 =getMaxRecSize();
           valueMin4 = getMinRecSize();

            if (valueMin4 == 0 && sizeType != 1) {
                zeroMin = true;

            }

            if (valueMax4 < displayMax && (valueMin4 > displayMin || valueMin4 == displayMin)) {
                realsize = true;
            }

        } else if (sizeType == 3) {

            valueMax3 = wArray[sizeArray.length - 1];

            valueMin3 = wArray[0];

            if (valueMin3 == 0 && sizeType != 1) {
                zeroMin2 = true;

            }

            if (valueMax3 < displayMax && (valueMin3 > displayMin || valueMin3 == displayMin)) {
                realsize = true;
            }


        } else if (sizeType == 2) {

            valueMax2 = wArray[sizeArray.length - 1];

            valueMin2 = wArray[0];

            if (valueMin2 == 0 && sizeType != 1) {
                zeroMin = true;

            }

            if (valueMax2 < displayMax && (valueMin2 > displayMin || valueMin2 == displayMin)) {
                realsize = true;
            }
        } else if (sizeType == 1) {

            valueMax1 = wArray[sizeArray.length - 1];

            valueMin1 = wArray[0];

            if (valueMax1 < displayMax && (valueMin1 > displayMin || valueMin1 == displayMin)) {
                realsize = true;
            }
        }
        return realsize;
    }

    public double getMaxRecSize(){
       String max = mainFrame.getParameter("recMaxSize");
         double doubleMax = Double.parseDouble(max);
       if(doubleMax == 0.00)
          doubleMax = 0.000000001;


        return doubleMax;
    }

    public double getMinRecSize(){
       String min = mainFrame.getParameter("recMinSize");
       double doubleMin = Double.parseDouble(min);
       if(doubleMin == 0.00)
          doubleMin = 0.000000001;


        return doubleMin;
    }

    public String getLabelHiden
            () {

        try {
            if (fromFile)
                return htmlparas.get("hideNodeLabel");

            else
                return mainFrame.getParameter("hideNodeLabel");

        } catch (Exception e) {
            return null;
        }

    }

    public boolean isRealSize
            (
                    int sizeType,
                    double displayMin,
                    double displayMax) {

        boolean realsize = false;
        if (sizeType == 1) {

            realsize = processMinMax(sizeType, strengths, displayMin, displayMax);


        } else if (sizeType == 2) {
            realsize = processMinMax(sizeType, nodeSizes, displayMin, displayMax);

        } else if (sizeType == 3) {
            realsize = processMinMax(sizeType, nodeSizes2, displayMin, displayMax);

        } else if (sizeType == 4) {
            realsize = processMinMax(sizeType, recPara, displayMin, displayMax);

        } else if (sizeType == 5) {
            realsize = processMinMax(sizeType, recPara, displayMin, displayMax);
        }
        return realsize;
    }

    public String getAttriRecIdMetric
            () {
        return recPara[recPara.length - 3];
    }

    public String getAttriRecSeMetric
            () {
        return recPara[recPara.length - 2];
    }

    public boolean isMinZero
            () {

        return zeroMin;
    }

    public boolean isMinZero2
            () {

        return zeroMin2;
    }

    public String getImageLocation
            () {
        return imageLocation;
    }

    public String getNodeDisBy
            () {
        try {
            if (fromFile)
                return htmlparas.get("nodeBy");
            else
                return mainFrame.getParameter("nodeBy");
        } catch (Exception e) {
            return null;
        }
    }

    public String getImageSize
            () {
        try {
            if (fromFile)
                return htmlparas.get("imageSize");
            else
                return mainFrame.getParameter("imageSize");
        } catch (Exception e) {
            return null;
        }
    }

    public String getImagelocation
            () {
        return htmlparas.get("imageLocation");
    }

    public String getLabelNodeStatus
            () {
        try {
            if (fromFile)
                return htmlparas.get("labelNode");
            else
                return mainFrame.getParameter("labelNode");
        } catch (Exception e) {
            return null;
        }
    }

    public String getGroupNodeStatus
            () {
        try {
            if (fromFile)

                return htmlparas.get("groupNode");
            else
                return mainFrame.getParameter("groupNode");
        } catch (Exception e) {
            return null;
        }
    }

    public String getLabelEdgeStatus
            () {
        try {
            if (fromFile)

                return htmlparas.get("labelEdge");
            else
                return mainFrame.getParameter("labelEdge");
        } catch (Exception e) {
            return null;
        }


    }

    public String getWeightEdgeStatus
            () {
        try {
            if (fromFile)

                return htmlparas.get("weightEdge");
            else
                return mainFrame.getParameter("weightEdge");
        } catch (Exception e) {
            return null;
        }

    }

    public boolean partNodesLabel
            () {
        try {
            if (fromFile) {
                if (htmlparas.get("nodelabel").contains("1"))
                    return true;
                else
                    return false;
            } else {
                if (mainFrame.getParameter("nodelabel").contains("1"))
                    return true;
                else
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Munge IKNOW's edges and strengths parameters
     */
    private void makeGraphEdges
            () {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);
        String e, s;
        try {

            if (fromFile) {
                e = trimList(htmlparas.get("edges"));
                s = trimList(htmlparas.get("strengths"));
            } else {
                e = trimList(mainFrame.getParameter("edges"));
                s = trimList(mainFrame.getParameter("strengths"));
            }
            String[] edges = makeArrayFromStr(e);
            String[] strengths = makeArrayFromStr(s);

            int i;
            double w;

// XXX assumes no duplicate edges in edgelist

            for (i = 0; i < edges.length; ++i) {
                w = Double.valueOf(strengths[i]);
                minEdge = minEdge < w ? minEdge : w;
                maxEdge = maxEdge > w ? maxEdge : w;

            }
        } catch (NullPointerException ex) {
            System.out.println("Exception when calculating max, min edge weight: " + ex);
        }
    }

}