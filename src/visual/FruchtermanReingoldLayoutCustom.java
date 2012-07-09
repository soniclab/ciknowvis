package visual;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 30, 2008
 * Time: 12:15:15 PM
 * To change this template use File | Settings | File Templates.
 */


import prefuse.action.layout.Layout;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

import java.awt.geom.Rectangle2D;
import java.util.*;

import data.AppletDataHandler1;
import dialog.FRProcessingDialog;

import javax.swing.*;


public class FruchtermanReingoldLayoutCustom extends Layout {

    private double forceConstant;
    private double temp;
    private int maxIter = 0;

    protected String m_nodeGroup;
    protected String m_edgeGroup;
    protected int m_fidx;

    private static final double EPSILON = 0.000001D;
    private static final double ALPHA = 0.1;

    String nodes, edges;
    double distance;

    //  private Random rand = new Random(12345678L);

    //  private static final double ALPHA = 0.1;
    private boolean visualDataChanged;
    private AppletDataHandler1 dh;
    private int dis;
    private int viewFormat;
    private int iter;
    private FRProcessingDialog processingD;
    private GraphView gv;
    private int clusterMethod;
    /**
     * Create a new FruchtermanReingoldLayout.
     *
     * @param graph the data field to layout. Must resolve to a Graph instance.
     */

    public FruchtermanReingoldLayoutCustom(String graph, int maxIter, boolean visualDataChanged, String clusteringView, String clusteringDis, int iter, FRProcessingDialog processingD, GraphView gv, int clusterMethod) {
        super(graph);
        setMargin(0, 40, 0, 40);

        this.visualDataChanged = visualDataChanged;

        this.maxIter = maxIter;
        dis = Integer.parseInt(clusteringDis);
        if (clusteringView.equalsIgnoreCase("Component view"))
            viewFormat = 1;
        else
            viewFormat = 2;
        this.iter = iter;
        this.processingD = processingD;
        this.gv = gv;
        this.clusterMethod = clusterMethod;
    }


    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {

        Graph g = (Graph) m_vis.getGroup(m_group);

        Rectangle2D bounds = super.getLayoutBounds();
        double cForConstant = 0.00;


        if (dis == 1) {
            cForConstant = 0.65;
        } else if (dis == 2) {
            cForConstant = 0.75;
        } else if (dis == 3) {
            cForConstant = 0.85;
        } else if (dis == 4) {
            cForConstant = 1.35;
        } else if (dis == 5) {
            cForConstant = 1.45;
        } else if (dis == 6) {
            cForConstant = 1.55;
        } else if (dis == 7) {
            cForConstant = 1.65;
        } else if (dis == 8) {
            cForConstant = 1.75;
        } else if (dis == 9) {
            cForConstant = 1.85;
        } else
            cForConstant = 1.95;

        init(g, bounds, cForConstant);

        for (int curIter = 0; curIter < maxIter; curIter++) {
            boolean stopIter = true;

            // Calculate repulsion

            for (Iterator iter = g.nodes(); iter.hasNext();) {

                NodeItem n = (NodeItem) iter.next();

                if (n.getDegree() > 0) {
                    if (n.isFixed()) continue;
                    calcRepulsion(g, n);
                }

            }//repulsion

            for (Iterator iter = g.edges(); iter.hasNext();) {

                EdgeItem e = (EdgeItem) iter.next();
                if (e.isVisible())
                    calcAttraction(e);

            }

            for (Iterator iter = g.nodes(); iter.hasNext();) {
                NodeItem n = (NodeItem) iter.next();
                if (n.getDegree() > 0) {
                    if (n.isFixed()) continue;
                    calcPositions(n, bounds);
                }
            }

            cool(curIter);

            /*  if(stopIter == true){
              gv.isbreak();
               if( maxIter == 17)
               break;

           } */

        }

        finish(g, bounds);
    }


    private void init(Graph g, Rectangle2D b, double cForConstant) {

        initSchema(g.getNodes());
        int nn = g.getNodeCount();

        double height = b.getHeight();
        double width = b.getWidth();

        double cx = b.getCenterX();
        double cy = b.getCenterY();

        double radius = 0.45 * (height < width ? height : width);

        temp = width / 10;
        forceConstant = cForConstant *
                Math.sqrt(height * width / nn);

        // initialize node positions
        Iterator nodeIter = g.nodes();

        int i = 0;

        while (nodeIter.hasNext()) {
            NodeItem n = (NodeItem) nodeIter.next();
            if (n.getDegree() > 0) {
                Params np = getParams(n);
                if (visualDataChanged) {
                    /*
                      Random rand = new Random(12345678L); // get a deterministic layout result
                      double scaleW = ALPHA * b.getWidth() / 2;
                      double scaleH = ALPHA * b.getHeight() / 2;

                      np.loc[0] = b.getCenterX() + rand.nextDouble() * scaleW;
                      np.loc[1] = b.getCenterY() + rand.nextDouble() * scaleH;
                    */
                    // circle layout
                    double angle = (2 * Math.PI * i) / nn;
                    double x = Math.cos(angle) * radius + cx;
                    double y = Math.sin(angle) * radius + cy;
                    np.loc[0] = x;
                    np.loc[1] = y;
                    i++;

                } else {

                    np.loc[0] = n.getX();
                    np.loc[1] = n.getY();
                }
            }
        }
    }

    private void finish(Graph g, Rectangle2D bounds) {
        int compCount = 0;

        if ((maxIter > 20) || viewFormat == 2) {
            Set<Set> comps = calcComp(g);
            compCount = comps.size();
        }


        Iterator nodeIter = g.nodes();

        while (nodeIter.hasNext()) {
            NodeItem n = (NodeItem) nodeIter.next();
            if (n.getDegree() > 0) {
                Params np = getParams(n);
                if (compCount != 1 && viewFormat != 2) {
                    setX(n, null, np.loc[0]);
                    setY(n, null, np.loc[1]);
                }
            }
        }
        // ajust node location to avoid node overlapping
        if ((compCount != 1 && viewFormat != 2) || (viewFormat == 2 && dis == 8 && g.getNodeCount() < 150)) {
            ajustNode(g);
        }

        if (compCount != 1 && viewFormat != 2) {

            if (iter > 20 && processingD != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        /* processingD.label3.setText("          Done!");

                       processingD.label4.setText("");
                       processingD.repaint(); */
                        processingD.dispose();
                    }
                });
            }


        }

        //   if (compCount == 1 || viewFormat == 2)
        rescalePositions(g, bounds);


    }

    private void ajustNode(Graph g) {

        for (Iterator iter = g.edges(); iter.hasNext();) {

            EdgeItem e = (EdgeItem) iter.next();

            if (e.isVisible()) {
                NodeItem n1 = e.getSourceItem();
                Params n1p = getParams(n1);
                NodeItem n2 = e.getTargetItem();
                Params n2p = getParams(n2);


                double ydis = Math.abs(n2p.loc[1] - n1p.loc[1]);
                double xdis = Math.abs(n2p.loc[0] - n1p.loc[0]);

                boolean intersect = n1.getBounds().intersects(n2.getBounds());
                //System.out.println(" edgeLength: " + edgeLength);
                if (intersect || (ydis < 18 && xdis < 60)) {
                    //  System.out.println("link from " + n1.getString("disLabel") + " to " + n2.getString("disLabel") );
                    double xcenter = (n2p.loc[0] + n1p.loc[0]) / 2;
                    double ycenter = (n2p.loc[1] + n1p.loc[1]) / 2;

                    if (n1.getDegree() < 10) {
                        if (n1p.loc[0] < xcenter)
                            n1p.loc[0] = n1p.loc[0] - 6;
                        else
                            n1p.loc[0] = n1p.loc[0] + 6;

                        if (n1p.loc[1] < ycenter)
                            n1p.loc[1] = n1p.loc[1] - 6;
                        else
                            n1p.loc[1] = n1p.loc[1] + 6;

                        setX(n1, null, n1p.loc[0]);
                        setY(n1, null, n1p.loc[1]);
                    }
                    if (n2.getDegree() < 10) {
                        if (n2p.loc[0] < xcenter)
                            n2p.loc[0] = n2p.loc[0] - 6;
                        else
                            n2p.loc[0] = n2p.loc[0] + 6;

                        if (n2p.loc[1] < ycenter)
                            n2p.loc[1] = n2p.loc[1] - 6;
                        else
                            n2p.loc[1] = n2p.loc[1] + 6;

                        setX(n2, null, n2p.loc[0]);
                        setY(n2, null, n2p.loc[1]);
                    }
                }//if intersect

            }
        }
    }

    public boolean calcPositions(NodeItem n, Rectangle2D b) {

        boolean stopIter = true;
        Params np = getParams(n);
        double deltaLength = Math.max(EPSILON,
                Math.sqrt(np.disp[0] * np.disp[0] + np.disp[1] * np.disp[1]));

        double xDisp = np.disp[0] / deltaLength * Math.min(deltaLength, temp);

        if (Double.isNaN(xDisp)) {
            System.err.println("Mathematical error... (calcPositions:xDisp)");
        }

        double yDisp = np.disp[1] / deltaLength * Math.min(deltaLength, temp);

        //  double disNew_old = Math.sqrt(xDisp*xDisp +yDisp*yDisp);

        //  if(disNew_old > 0.00000001)
        //   stopIter = false;

        np.loc[0] += xDisp;
        np.loc[1] += yDisp;


        if (viewFormat == 1) {
            double borderWidth = b.getWidth() / 100.0;

            if (np.loc[0] < borderWidth) {
                np.loc[0] = borderWidth + Math.random() * borderWidth * 2.0;
            } else if (np.loc[0] > (b.getWidth() - borderWidth)) {
                np.loc[0] = b.getWidth() - borderWidth - Math.random()
                        * borderWidth * 2.0;
            }


            if (np.loc[1] < borderWidth) {
                np.loc[1] = borderWidth + Math.random() * borderWidth * 2.0;
            } else if (np.loc[1] > (b.getHeight() - borderWidth)) {
                np.loc[1] = b.getHeight() - borderWidth
                        - Math.random() * borderWidth * 2.0;
            }
        }
        return stopIter;
    }

    public void calcAttraction(EdgeItem e) {
        NodeItem n1 = e.getSourceItem();
        Params n1p = getParams(n1);
        NodeItem n2 = e.getTargetItem();
        Params n2p = getParams(n2);

        double xDelta = (n1p.loc[0] - n2p.loc[0]);
        double yDelta = (n1p.loc[1] - n2p.loc[1]);

        double deltaLength = Math.max(EPSILON,
                Math.sqrt(xDelta * xDelta + yDelta * yDelta));

      if(clusterMethod == 2){   // by average of weight values
      int edgeSize = getMultiEgdes(e).size(); // how many edges between this source and target nodes
       deltaLength = deltaLength * e.getDouble("edgeWeight")/edgeSize;
        }
       double force = (deltaLength * deltaLength) / forceConstant;

        if (Double.isNaN(force)) {
            System.err.println("Mathematical error...");
        }

        double xDisp = (xDelta / deltaLength) * force;
        double yDisp = (yDelta / deltaLength) * force;


        n1p.disp[0] -= xDisp;
        n1p.disp[1] -= yDisp;
        n2p.disp[0] += xDisp;
        n2p.disp[1] += yDisp;


    }

    public void calcRepulsion(Graph g, NodeItem n1) {


        Params np = getParams(n1);
        np.disp[0] = 0.0;
        np.disp[1] = 0.0;


        for (Iterator iter2 = g.nodes(); iter2.hasNext();) {
            NodeItem n2 = (NodeItem) iter2.next();
            if (n2.getDegree() > 0) {
                Params n2p = getParams(n2);

                if (n2.isFixed()) continue;

                if (n1 != n2) {
                    double xDelta = np.loc[0] - n2p.loc[0];
                    double yDelta = np.loc[1] - n2p.loc[1];

                    double deltaLength = Math.max(EPSILON,
                            Math.sqrt(xDelta * xDelta + yDelta * yDelta));

                    double disOfNodes = Math.sqrt(xDelta * xDelta + yDelta * yDelta);

                    if ((viewFormat == 2) || ((viewFormat == 1) && (disOfNodes < 2 * forceConstant))) {
                        double force = forceConstant * forceConstant / deltaLength;

                        if (Double.isNaN(force)) {
                            System.err.println("Mathematical error...");
                        }
                        np.disp[0] += (xDelta / deltaLength) * force;
                        np.disp[1] += (yDelta / deltaLength) * force;
                    }
                }
            }
        }
    }

    // get each component

    private Set<Set> calcComp(Graph g) {

        Set<Set> comSets = new HashSet<Set>();

        for (Iterator iter = g.nodes(); iter.hasNext();) {
            NodeItem n = (NodeItem) iter.next();
            if (n.getDegree() > 0) {
                Set<NodeItem> comElements = new HashSet<NodeItem>();

                comSets.add(getComSet(n, comElements));
            }
        }

        return comSets;
    }


    private Set<NodeItem> getComSet(NodeItem n, Set<NodeItem> comElements) {

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



     private Set<EdgeItem> getMultiEgdes(EdgeItem edge) {
         Set<EdgeItem> edges1 = new HashSet();
        Set<EdgeItem> edges2 = new HashSet();

        try{

        NodeItem node1 = edge.getSourceItem();

        NodeItem node2 = edge.getTargetItem();
        Iterator it1 = node1.edges();
        Iterator it2 = node2.edges();


        while (it1.hasNext()) {
            EdgeItem e1 = (EdgeItem) it1.next();
            if (e1.isVisible() == true)
                edges1.add(e1);
        }
        while (it2.hasNext()) {
            EdgeItem e2 = (EdgeItem) it2.next();
            if (e2.isVisible() == true)
                edges2.add(e2);
        }

        edges1.retainAll(edges2);
        }catch(Exception e) {

        }
        return edges1;
    }

    private void rescalePositions(Graph g, Rectangle2D b) {

        double xMax = 0.00;
        double yMax = 0.00;
        double xMin = 0.00;
        double yMin = 0.00;

        Iterator iter0 = g.nodes();
        double itemheight = 0.00;
        // get xMax, yMax, xMin, yMin
        while (iter0.hasNext()) {
            NodeItem item = (NodeItem) iter0.next();
            itemheight = item.getBounds().getHeight();
            if (item.getDegree() > 0) {
                Params np = getParams(item);

                xMax = np.loc[0];
                yMax = np.loc[1];
                xMin = np.loc[0];
                yMin = np.loc[1];

                break;
            }
        }


        Iterator iter1 = g.nodes();
        Set<NodeItem> isoItems = new HashSet<NodeItem>();
        Set<String> isoLabels = new HashSet<String>();

        while (iter1.hasNext()) {

            NodeItem item = (NodeItem) iter1.next();
            if (item.getDegree() > 0) {
                Params np = getParams(item);

                xMax = Math.max(xMax, np.loc[0]);
                yMax = Math.max(yMax, np.loc[1]);
                xMin = Math.min(xMin, np.loc[0]);
                yMin = Math.min(yMin, np.loc[1]);
            } else {
                isoItems.add(item);
                isoLabels.add(item.getString("disLabel"));
            }
        }

        int isolateSize = isoItems.size();
        String[] labelArray = isoLabels.toArray(new String[isoLabels.size()]);
        Arrays.sort(labelArray);

        // rescale nodes to the real frame
        Iterator iter2 = g.nodes();
        double isoY = 0.00;

        while (iter2.hasNext()) {
            NodeItem item = (NodeItem) iter2.next();
            if (isolateSize > 0) {
                if (item.getDegree() > 0) {
                    Params np = getParams(item);

                    np.loc[0] = 70.0 + ((np.loc[0] - xMin) / (xMax - xMin)) * (b.getWidth() - 90);
                    np.loc[1] = ((np.loc[1] - yMin) / (yMax - yMin)) * (b.getHeight() - 10);
                    setX(item, null, np.loc[0]);
                    setY(item, null, np.loc[1]);

                }
            } else {
                Params np = getParams(item);

                np.loc[0] = ((np.loc[0] - xMin) / (xMax - xMin)) * (b.getWidth() - 10);
                np.loc[1] = ((np.loc[1] - yMin) / (yMax - yMin)) * (b.getHeight() - 10);
                setX(item, null, np.loc[0]);
                setY(item, null, np.loc[1]);
            }

        }

        if (isolateSize > 0) {

            for (int i = 0; i < labelArray.length; i++) {
                for (NodeItem item : isoItems) {
                    if (item.getString("disLabel").equals(labelArray[i])) {
                        double ydis = (b.getHeight() - 10) / isolateSize;

                        setX(item, null, 0.00);
                        setY(item, null, isoY);
                       // if (ydis < itemheight) {
                            isoY += ydis;
                       // } else {
                           // isoY += itemheight;
                        //}
                    }
                }
            }
        }


        if (iter > 20 && processingD != null) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // processingD.label3.setText("          Done!");
                    //  processingD.label4.setText("");
                    // processingD.repaint();

                    processingD.dispose();
                }
            });


        }


    }

    // Get the current time as a formatted string

    private static String timeOfTheDay() {
        Calendar calendar = new GregorianCalendar();

        return "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + ":" + calendar.get(Calendar.MILLISECOND);
    }


    private void cool(int curIter) {
        temp *= (1.0 - curIter / (double) maxIter);
    }

    // ------------------------------------------------------------------------
    // Params Schema

    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String PARAMS = "_fruchtermanReingoldParams";
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema PARAMS_SCHEMA = new Schema();

    static {
        PARAMS_SCHEMA.addColumn(PARAMS, Params.class);
    }

    protected void initSchema(TupleSet ts) {
        try {
            ts.addColumns(PARAMS_SCHEMA);
        } catch (IllegalArgumentException iae) {
        }
    }

    private Params getParams(VisualItem item) {
        Params rp = (Params) item.get(PARAMS);
        if (rp == null) {
            rp = new Params();
            item.set(PARAMS, rp);
        }
        return rp;
    }

    /**
     * Wrapper class holding parameters used for each node in this layout.
     */
    public static class Params implements Cloneable {
        double[] loc = new double[2];
        double[] disp = new double[2];
    }

} // end of class FruchtermanReingoldLayout
