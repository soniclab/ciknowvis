package visual;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

import prefuse.Constants;
import prefuse.Display;
import prefuse.action.layout.graph.TreeLayout;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ArrayLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class RecLayout extends TreeLayout {

    private int m_orientation;  // the orientation of the tree
    private double m_bspace = 20;   // the spacing between sibling nodes
    private double m_tspace = 25;  // the spacing between subtrees
    private double m_dspace = 90;  // the spacing between depth levels
    private double m_offset = 50;  // pixel offset for root node position

    private double[] m_depths = new double[10];
    private int m_maxDepth = 0;

    private double m_ax, m_ay; // for holding anchor co-ordinates
    private int ur_depth, rt_depth;
    private String utHasDirLink;

    private int sizeOfDepth11, sizeOfDepth12; // how many nodes for depth 1, depth 2 between user and rec.
    private int sizeOfDepth21, sizeOfDepth22;   // how many nodes for depth 1, depth 2 between rec and target.

    /**
     * Create a new NodeLinkTreeLayout. A left-to-right orientation is assumed.
     *
     * @param group the data group to layout. Must resolve to a Graph instance.
     */
    public RecLayout(String group, int ur_depth, int rt_depth, String utHasDirLink) {
        super(group);
        m_orientation = Constants.ORIENT_LEFT_RIGHT;

        this.ur_depth = ur_depth;
        this.rt_depth = rt_depth;
        this.utHasDirLink = utHasDirLink;

    }

    /**
     * Create a new NodeLinkTreeLayout.
     *
     * @param group       the data group to layout. Must resolve to a Graph instance.
     * @param orientation the orientation of the tree layout. One of
     *                    {@link prefuse.Constants#ORIENT_LEFT_RIGHT},
     *                    {@link prefuse.Constants#ORIENT_RIGHT_LEFT},
     *                    {@link prefuse.Constants#ORIENT_TOP_BOTTOM}, or
     *                    {@link prefuse.Constants#ORIENT_BOTTOM_TOP}.
     * @param dspace      the spacing to maintain between depth levels of the tree
     * @param bspace      the spacing to maintain between sibling nodes
     * @param tspace      the spacing to maintain between neighboring subtrees
     */
    public RecLayout(String group, int orientation,
                     double dspace, double bspace, double tspace) {
        super(group);
        m_orientation = orientation;
        m_dspace = dspace;
        m_bspace = bspace;
        m_tspace = tspace;
    }

    // ------------------------------------------------------------------------

    /**
     * Set the orientation of the tree layout.
     *
     * @param orientation the orientation value. One of
     *                    {@link prefuse.Constants#ORIENT_LEFT_RIGHT},
     *                    {@link prefuse.Constants#ORIENT_RIGHT_LEFT},
     *                    {@link prefuse.Constants#ORIENT_TOP_BOTTOM}, or
     *                    {@link prefuse.Constants#ORIENT_BOTTOM_TOP}.
     */
    public void setOrientation(int orientation) {
        if (orientation < 0 ||
                orientation >= Constants.ORIENTATION_COUNT ||
                orientation == Constants.ORIENT_CENTER) {
            throw new IllegalArgumentException(
                    "Unsupported orientation value: " + orientation);
        }
        m_orientation = orientation;
    }

    /**
     * Get the orientation of the tree layout.
     *
     * @return the orientation value. One of
     *         {@link prefuse.Constants#ORIENT_LEFT_RIGHT},
     *         {@link prefuse.Constants#ORIENT_RIGHT_LEFT},
     *         {@link prefuse.Constants#ORIENT_TOP_BOTTOM}, or
     *         {@link prefuse.Constants#ORIENT_BOTTOM_TOP}.
     */
    public int getOrientation() {
        return m_orientation;
    }

    /**
     * Set the spacing between depth levels.
     *
     * @param d the depth spacing to use
     */
    public void setDepthSpacing(double d) {
        m_dspace = d;
    }

    /**
     * Get the spacing between depth levels.
     *
     * @return the depth spacing
     */
    public double getDepthSpacing() {
        return m_dspace;
    }

    /**
     * Set the spacing between neighbor nodes.
     *
     * @param b the breadth spacing to use
     */
    public void setBreadthSpacing(double b) {
        m_bspace = b;
    }

    /**
     * Get the spacing between neighbor nodes.
     *
     * @return the breadth spacing
     */
    public double getBreadthSpacing() {
        return m_bspace;
    }

    /**
     * Set the spacing between neighboring subtrees.
     *
     * @param s the subtree spacing to use
     */
    public void setSubtreeSpacing(double s) {
        m_tspace = s;
    }

    /**
     * Get the spacing between neighboring subtrees.
     *
     * @return the subtree spacing
     */
    public double getSubtreeSpacing() {
        return m_tspace;
    }

    /**
     * Set the offset value for placing the root node of the tree. The
     * dimension in which this offset is applied is dependent upon the
     * orientation of the tree. For example, in a left-to-right orientation,
     * the offset will a horizontal offset from the left edge of the layout
     * bounds.
     *
     * @param o the value by which to offset the root node of the tree
     */
    public void setRootNodeOffset(double o) {
        m_offset = o;
    }

    /**
     * Get the offset value for placing the root node of the tree.
     *
     * @return the value by which the root node of the tree is offset
     */
    public double getRootNodeOffset() {
        return m_offset;
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.layout.Layout#getLayoutAnchor()
     */
    public Point2D getLayoutAnchor() {
        if (m_anchor != null)
            return m_anchor;

        m_tmpa.setLocation(0, 0);
        if (m_vis != null) {
            Display d = m_vis.getDisplay(0);
            Rectangle2D b = this.getLayoutBounds();
            switch (m_orientation) {
                case Constants.ORIENT_LEFT_RIGHT:
                    m_tmpa.setLocation(m_offset, d.getHeight() / 2.0);
                    break;
                case Constants.ORIENT_RIGHT_LEFT:
                    m_tmpa.setLocation(b.getMaxX() - m_offset, d.getHeight() / 2.0);
                    break;
                case Constants.ORIENT_TOP_BOTTOM:
                    m_tmpa.setLocation(d.getWidth() / 2.0, m_offset);
                    break;
                case Constants.ORIENT_BOTTOM_TOP:
                    m_tmpa.setLocation(d.getWidth() / 2.0, b.getMaxY() - m_offset);
                    break;
            }
            d.getInverseTransform().transform(m_tmpa, m_tmpa);
        }
        return m_tmpa;
    }

    private double spacing(NodeItem l, NodeItem r, boolean siblings) {
        boolean w = (m_orientation == Constants.ORIENT_TOP_BOTTOM ||
                m_orientation == Constants.ORIENT_BOTTOM_TOP);
        return (siblings ? m_bspace : m_tspace) + 0.5 *
                (w ? l.getBounds().getWidth() + r.getBounds().getWidth()
                        : l.getBounds().getHeight() + r.getBounds().getHeight());
    }

    private void updateDepths(int depth, NodeItem item) {
        boolean v = (m_orientation == Constants.ORIENT_TOP_BOTTOM ||
                m_orientation == Constants.ORIENT_BOTTOM_TOP);
        double d = (v ? item.getBounds().getHeight()
                : item.getBounds().getWidth());
        if (m_depths.length <= depth)
            m_depths = ArrayLib.resize(m_depths, 3 * depth / 2);
        m_depths[depth] = Math.max(m_depths[depth], d);
        m_maxDepth = Math.max(m_maxDepth, depth);
    }

    private void determineDepths() {
        for (int i = 1; i < m_maxDepth; ++i)
            m_depths[i] += m_depths[i - 1] + m_dspace;
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
        Graph g = (Graph) m_vis.getGroup(m_group);
        initSchema(g.getNodes());

        Arrays.fill(m_depths, 0);
        m_maxDepth = 0;

        Point2D a = getLayoutAnchor();
        m_ax = a.getX();
        m_ay = a.getY();

        NodeItem root = getLayoutRoot();
        Params rp = getParams(root);

        // do first pass - compute breadth information, collect depth info
        firstWalk(root, 0, 1);

        // sum up the depth info
        determineDepths();

        // do second pass - assign layout positions
        secondWalk(root, null, -rp.prelim, 0);

        // relocate the nodes for better layout
        Iterator iter = m_vis.items(m_group);

        Rectangle2D r = getLayoutBounds();

        double cx = r.getWidth() / 2;

        ArrayList<Integer> recNodeKey = new ArrayList<Integer>();

        double userY = 0.0;
        boolean ur_movedUp = false, ur_movedDown = false, rt_movedUp = false, rt_movedDown = false;

        while (iter.hasNext()) {
            VisualItem nodeitem = (VisualItem) iter.next();
            try {
                if (nodeitem.getString("nodeType").equals("Recommendation")) {
                    recNodeKey.add(nodeitem.getInt("nodeKey"));
                }

                if (nodeitem.getString("nodeType").equals("Requester") && (nodeitem.getInt("nodeKey") == 0)) {

                    userY = nodeitem.getY();
                }
            } catch (Exception e) {
                //  System.out.println("Exception in RecLayout: " + e);
            }
        }

        Integer[] recKeys = recNodeKey.toArray(new Integer[recNodeKey.size()]);

        Iterator iter2 = m_vis.items(m_group);
        while (iter2.hasNext()) {
            VisualItem nodeitem = (VisualItem) iter2.next();
            try {
                double itemY = nodeitem.getY();
                if ((nodeitem.getInt("nodeKey") > 0) && (nodeitem.getInt("nodeKey") < recKeys[0])) {

                    if ((itemY <= userY) && ((userY - itemY) <= m_bspace))

                        ur_movedUp = true;

                    else if ((itemY > userY) && ((itemY - userY) < m_bspace))
                        ur_movedDown = true;
                }

                if ((nodeitem.getInt("nodeKey") >  recKeys[recNodeKey.size()-1]) && !(nodeitem.getString("nodeType").equals("Target"))) {

                    if ((itemY <= userY) && ((userY - itemY) <= m_bspace))

                        rt_movedUp = true;

                    else if ((itemY > userY) && ((itemY - userY) < m_bspace))
                        rt_movedDown = true;
                }

            } catch (Exception e) {
                //System.out.println("Exception in RecLayout: " + e);
            }

        }

        // y distance between two recs

         int  recDisCount = (recKeys.length-1);
         double recDis = recDisCount* g.getNodeCount();
        double firstRecLoc = userY - recDis/2;
        int i = 0; // first rec
        Iterator iter1 = m_vis.items(m_group);
    
        while (iter1.hasNext()) {
            VisualItem item = (VisualItem) iter1.next();
            double unitLength = 2 * cx / (ur_depth + rt_depth);
            try {
                if (item.getString("nodeType").equals("Requester") && (item.getInt("nodeKey") == 0)) {

                    setX(item, null, 50);

                } else if ((item.getInt("nodeKey") > 0) && (item.getInt("nodeKey") < recKeys[0])) {

                    int itemDepth = getDepth((NodeItem) item, 1, 0);

                   // System.out.println("depth for this item:" + itemDepth);

                    setX(item, null, itemDepth * unitLength);


                    setY(item, null, item.getY());

                    if (ur_movedUp && (item.getY() <= userY)) {

                        setY(item, null, item.getY() - 3 * m_bspace);
                    } else if (ur_movedDown && (item.getY() > userY)) {

                        setY(item, null, item.getY() + 3 * m_bspace);
                    }

                     
                } else if (item.getString("nodeType").equals("Recommendation")) {

                    setX(item, null, ur_depth * unitLength);
                   
                    if(utHasDirLink.equalsIgnoreCase("true") && recKeys.length == 1){

                        setY(item, null, userY - g.getNodeCount());
                    }else
                        setY(item, null, firstRecLoc + i *g.getNodeCount() );

                   i++;

                } else if ((item.getInt("nodeKey") >  recKeys[recNodeKey.size()-1]) && !(item.getString("nodeType").equals("Target"))) {
                    int itemDepth = getDepth((NodeItem) item, 2, 0);

                    setX(item, null, (itemDepth + ur_depth) * unitLength);

                     setY(item, null, item.getY());

                 if (rt_movedUp && (item.getY() <= userY)) {

                        setY(item, null, item.getY() - 3 * m_bspace);


                    } else if (rt_movedDown && (item.getY() > userY)) {

                        setY(item, null, item.getY() + 3 * m_bspace);
                    }

                } else if (item.getString("nodeType").equals("Target")) {

                    setX(item, null, 2 * cx - 50);
                    setY(item, null, userY);
                }

                //for(String nodeid: commenNodesForRec){
                 //   if(item.get("login").equals(nodeid))
                  //  setX(item, null, ur_depth * unitLength);
               // }

            } catch (Exception e) {
                // System.out.println("Exception in RecLayout: " + e);
            }

        }


        //////////////////////////////
    /*    Iterator iter1 = m_vis.items(m_group);

        while (iter1.hasNext()) {
            VisualItem item = (VisualItem) iter1.next();
            double unitLength = 2 * cx / (ur_depth + rt_depth);
            try {
                if (item.getString("nodeType").equals("Requester") && (item.getInt("nodeKey") == 0)) {

                    setX(item, null, 50);

                } else if ((item.getInt("nodeKey") > 0) && (item.getInt("nodeKey") < recKeys[0])) {

                    int itemDepth = getDepth((NodeItem) item, 1, 0);

                   // System.out.println("depth for this item:" + itemDepth);

                    setX(item, null, itemDepth * unitLength);

                    setY(item, null, item.getY());

                    if (ur_movedUp && (item.getY() <= userY)) {

                        setY(item, null, item.getY() - 3 * m_bspace);
                    } else if (ur_movedDown && (item.getY() > userY)) {

                        setY(item, null, item.getY() + 3 * m_bspace);
                    }


                } else if (item.getString("nodeType").equals("Recommendation")) {

                    setX(item, null, ur_depth * unitLength);

                    if(utHasDirLink.equalsIgnoreCase("true") && recKeys.length == 1){

                        setY(item, null, userY - g.getNodeCount());
                    }else
                        setY(item, null, firstRecLoc + i *g.getNodeCount() );

                   i++;

                } else if ((item.getInt("nodeKey") >  recKeys[recNodeKey.size()-1]) && !(item.getString("nodeType").equals("Target"))) {
                    int itemDepth = getDepth((NodeItem) item, 2, 0);

                    setX(item, null, (itemDepth + ur_depth) * unitLength);

                     setY(item, null, item.getY());

                 if (rt_movedUp && (item.getY() <= userY)) {

                        setY(item, null, item.getY() - 3 * m_bspace);


                    } else if (rt_movedDown && (item.getY() > userY)) {

                        setY(item, null, item.getY() + 3 * m_bspace);
                    }

                } else if (item.getString("nodeType").equals("Target")) {

                    setX(item, null, 2 * cx - 50);
                    setY(item, null, userY);
                }

                //for(String nodeid: commenNodesForRec){
                 //   if(item.get("login").equals(nodeid))
                  //  setX(item, null, ur_depth * unitLength);
               // }

            } catch (Exception e) {
                // System.out.println("Exception in RecLayout: " + e);
            }

        } */
        /////////////////////////////////////////////////////////
    }

    private int getDepth(NodeItem n, int recGroup, int depth) {
        int finalDepth = 0;
        if (recGroup == 1) {
            finalDepth = depthBusiness(1, n, depth, "Requester", "Recommendation");
            if(finalDepth == 1)
            sizeOfDepth11++;
            else  if(finalDepth == 2)
             sizeOfDepth12++;
        } else if (recGroup == 2) {
            finalDepth = depthBusiness(2, n, depth, "Recommendation", "Target");
            if(finalDepth == 1)
            sizeOfDepth21++;
            else  if(finalDepth == 2)
             sizeOfDepth22++;
        }

        return finalDepth;
    }

    private int depthBusiness(int recGroup, NodeItem n, int depth, String nodetype1, String nodetype2){
     boolean gotDepth = false;
     try {
                Iterator it = n.neighbors();

                while (it.hasNext()) {
                    NodeItem ni = (NodeItem) it.next();

                    if (ni.getString("nodeType").equals(nodetype1)) {

                        depth = 1 + depth;
                        gotDepth = true;
                        break;
                    } else if (ni.getString("nodeType").equals(nodetype2)) {

                        if(recGroup ==1)

                        depth = ur_depth - (1 + depth);
                        else
                         depth = rt_depth - (1 + depth);

                        gotDepth = true;
                        break;
                    }
                }
                if (!gotDepth) {

                    Iterator it1 = n.neighbors();
                    depth++;
                    while (it1.hasNext()) {
                        NodeItem ni1 = (NodeItem) it1.next();
                        depthBusiness(recGroup, ni1, depth, nodetype1, nodetype2);
                    }
                }
        }catch(Exception   e){
        System.out.println("exception when getting depth for rt_Group: " + e);
    }

    return depth;
 }
    private void firstWalk(NodeItem n, int num, int depth) {
        Params np = getParams(n);
        np.number = num;
        updateDepths(depth, n);

        boolean expanded = n.isExpanded();
        if (n.getChildCount() == 0 || !expanded) // is leaf
        {
            NodeItem l = (NodeItem) n.getPreviousSibling();
            if (l == null) {
                np.prelim = 0;
            } else {
                np.prelim = getParams(l).prelim + spacing(l, n, true);
            }
        } else if (expanded) {
            NodeItem leftMost = (NodeItem) n.getFirstChild();
            NodeItem rightMost = (NodeItem) n.getLastChild();
            NodeItem defaultAncestor = leftMost;
            NodeItem c = leftMost;
            for (int i = 0; c != null; ++i, c = (NodeItem) c.getNextSibling()) {
                firstWalk(c, i, depth + 1);
                defaultAncestor = apportion(c, defaultAncestor);
            }

            executeShifts(n);

            double midpoint = 0.5 *
                    (getParams(leftMost).prelim + getParams(rightMost).prelim);

            NodeItem left = (NodeItem) n.getPreviousSibling();
            if (left != null) {
                np.prelim = getParams(left).prelim + spacing(left, n, true);
                np.mod = np.prelim - midpoint;
            } else {
                np.prelim = midpoint;
            }
        }
    }

    private NodeItem apportion(NodeItem v, NodeItem a) {
        NodeItem w = (NodeItem) v.getPreviousSibling();
        if (w != null) {
            NodeItem vip, vim, vop, vom;
            double sip, sim, sop, som;

            vip = vop = v;
            vim = w;
            vom = (NodeItem) vip.getParent().getFirstChild();

            sip = getParams(vip).mod;
            sop = getParams(vop).mod;
            sim = getParams(vim).mod;
            som = getParams(vom).mod;

            NodeItem nr = nextRight(vim);
            NodeItem nl = nextLeft(vip);
            while (nr != null && nl != null) {
                vim = nr;
                vip = nl;
                vom = nextLeft(vom);
                vop = nextRight(vop);
                getParams(vop).ancestor = v;
                double shift = (getParams(vim).prelim + sim) -
                        (getParams(vip).prelim + sip) + spacing(vim, vip, false);
                if (shift > 0) {
                    moveSubtree(ancestor(vim, v, a), v, shift);
                    sip += shift;
                    sop += shift;
                }
                sim += getParams(vim).mod;
                sip += getParams(vip).mod;
                som += getParams(vom).mod;
                sop += getParams(vop).mod;

                nr = nextRight(vim);
                nl = nextLeft(vip);
            }
            if (nr != null && nextRight(vop) == null) {
                Params vopp = getParams(vop);
                vopp.thread = nr;
                vopp.mod += sim - sop;
            }
            if (nl != null && nextLeft(vom) == null) {
                Params vomp = getParams(vom);
                vomp.thread = nl;
                vomp.mod += sip - som;
                a = v;
            }
        }
        return a;
    }

    private NodeItem nextLeft(NodeItem n) {
        NodeItem c = null;
        if (n.isExpanded()) c = (NodeItem) n.getFirstChild();
        return (c != null ? c : getParams(n).thread);
    }

    private NodeItem nextRight(NodeItem n) {
        NodeItem c = null;
        if (n.isExpanded()) c = (NodeItem) n.getLastChild();
        return (c != null ? c : getParams(n).thread);
    }

    private void moveSubtree(NodeItem wm, NodeItem wp, double shift) {
        Params wmp = getParams(wm);
        Params wpp = getParams(wp);
        double subtrees = wpp.number - wmp.number;
        wpp.change -= shift / subtrees;
        wpp.shift += shift;
        wmp.change += shift / subtrees;
        wpp.prelim += shift;
        wpp.mod += shift;
    }

    private void executeShifts(NodeItem n) {
        double shift = 0, change = 0;
        for (NodeItem c = (NodeItem) n.getLastChild();
             c != null; c = (NodeItem) c.getPreviousSibling()) {
            Params cp = getParams(c);
            cp.prelim += shift;
            cp.mod += shift;
            change += cp.change;
            shift += cp.shift + change;
        }
    }

    private NodeItem ancestor(NodeItem vim, NodeItem v, NodeItem a) {
        NodeItem p = (NodeItem) v.getParent();
        Params vimp = getParams(vim);
        if (vimp.ancestor.getParent() == p) {
            return vimp.ancestor;
        } else {
            return a;
        }
    }

    private void secondWalk(NodeItem n, NodeItem p, double m, int depth) {
        Params np = getParams(n);
        setBreadth(n, p, np.prelim + m);
        setDepth(n, p, m_depths[depth]);

        if (n.isExpanded()) {
            depth += 1;
            for (NodeItem c = (NodeItem) n.getFirstChild();
                 c != null; c = (NodeItem) c.getNextSibling()) {
                secondWalk(c, n, m + np.mod, depth);
            }
        }

        np.clear();
    }

    private void setBreadth(NodeItem n, NodeItem p, double b) {
        switch (m_orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
            case Constants.ORIENT_RIGHT_LEFT:
                setY(n, p, m_ay + b);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
            case Constants.ORIENT_BOTTOM_TOP:
                setX(n, p, m_ax + b);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void setDepth(NodeItem n, NodeItem p, double d) {
        switch (m_orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
                setX(n, p, m_ax + d);
                break;
            case Constants.ORIENT_RIGHT_LEFT:
                setX(n, p, m_ax - d);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
                setY(n, p, m_ay + d);
                break;
            case Constants.ORIENT_BOTTOM_TOP:
                setY(n, p, m_ay - d);
                break;
            default:
                throw new IllegalStateException();
        }
    }

// ------------------------------------------------------------------------
// Params Schema

    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String PARAMS = "_reingoldTilfordParams";
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema PARAMS_SCHEMA = new Schema();

    static {
        PARAMS_SCHEMA.addColumn(PARAMS, Params.class);
    }

    protected void initSchema(TupleSet ts) {
        ts.addColumns(PARAMS_SCHEMA);
    }

    private Params getParams(NodeItem item) {
        Params rp = (Params) item.get(PARAMS);
        if (rp == null) {
            rp = new Params();
            item.set(PARAMS, rp);
        }
        if (rp.number == -2) {
            rp.init(item);
        }
        return rp;
    }

    /**
     * Wrapper class holding parameters used for each node in this layout.
     */
    public static class Params implements Cloneable {
        double prelim;
        double mod;
        double shift;
        double change;
        int number = -2;
        NodeItem ancestor = null;
        NodeItem thread = null;

        public void init(NodeItem item) {
            ancestor = item;
            number = -1;
        }

        public void clear() {
            number = -2;
            prelim = mod = shift = change = 0;
            ancestor = thread = null;
        }


    }

} // end of class NodeLinkTreeLayout
