package visual;

import prefuse.action.layout.CircleLayout;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.data.tuple.TupleSet;
import prefuse.data.Schema;
import java.awt.geom.Rectangle2D;
import java.util.*;

import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 3, 2008
 * Time: 11:49:54 PM
 * To change this template use File | Settings | File Templates.
 */


public class RecLayerLayout extends CircleLayout {
    private double scaleX = 1.0, scaleY = 1.0;
    private boolean zeroMin2,hasOneRec;
    private int normalizedType1, normalizedType2;
    private double displayMin;
    private AppletDataHandler1 dh;
    double cx, cy;

    public RecLayerLayout(AppletDataHandler1 dh, String group,  int normalizedType1, int normalizedType2, double displayMin, double displayMax, boolean zeroMin2, boolean hasOneRec) {

        super(group);
        scaleX = 1;
        scaleY = 1;
        this.dh = dh;

        this.normalizedType1 = normalizedType1;
        this.normalizedType2 = normalizedType2;

        this.displayMin = displayMin;
        this.hasOneRec = hasOneRec;
        this.zeroMin2 = zeroMin2;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {

        TupleSet ts = m_vis.getGroup("graph.nodes");
        initSchema(ts);

        int nn = ts.getTupleCount();
        NodeItem oneRecNode = null;
        Rectangle2D r = getLayoutBounds();
        double height = r.getHeight();
        double width = r.getWidth();
        cx = r.getCenterX();
        cy = r.getCenterY();

        Iterator items2 = ts.tuples();
        double seMax = 0.00;

        for (int i = 0; i < nn; i++) {
            NodeItem n = (NodeItem) items2.next();

            Params np = getParams(n);

            np.loc[0] = cx;
            np.loc[1] = cy;

           if(n.getString("nodeType").equals("Recommendation")) {

                 oneRecNode = n;
                seMax = Math.max(seMax, 1/n.getDouble("recSe"));

            }
        }

        double seMin = seMax;

        Iterator items3 = ts.tuples();

        for (int i = 0; i < nn; i++) {


            VisualItem n = (VisualItem) items3.next();

           if(n.getString("nodeType").equals("Recommendation")) {

                seMin = Math.min(seMin, 1/n.getDouble("recSe"));

            }
        }

        if(seMin > 1.0)
            seMin = 1.0;
        double radius = 0.48 * (height < width ? width : height);
         double minRadius =  0.48*width* seMin;
        double      radius1 = 0.0;
           if(minRadius > 0.48*height)
                radius1 = 0.48*height*seMax/seMin;
        radius = Math.min(radius, radius1) -20;

        List<NodeItem> finalNodes = getOrderedNodes(ts, seMax);

        Iterator items = finalNodes.iterator();

        double leftMaxX = cx-0.5*width;

        double rightMinX = 2*cx;

        double leftMaxAngle = 0.0;
        double leftMinAngle = (3/2)*Math.PI;
        double rightMaxAngle = 0.0;
        double rightMinAngle = 2*Math.PI;

        boolean leftOutBoundExist = false;
       boolean  rightOutBoundExist = false;
        Map<NodeItem, Double> nodeAngle = new HashMap<NodeItem, Double>();
        Map<NodeItem, Double> nodeLength = new HashMap<NodeItem, Double>();


        for (int i = 0; items.hasNext(); i++) {

            NodeItem n = (NodeItem) items.next();
            double angle = (2 * Math.PI * i) / (finalNodes.size());

            // if the rec algorithm is shortest path
            double recSe = 1/n.getDouble("recSe");
            // if the rec algorithm is not shortest path
           // double recSe = seMax / n.getDouble("recSe");

           // if (zeroMin2)
            //    recSe = recSe + 0.001;

            double length = recSe * radius / (seMax / seMin);
          /*  no need to normalize length
            if (normalizedType2 == 1) {

                length = (dh.getLinearSize(length, displayMin, radius, zeroMin2, 5));

            } else if (normalizedType2 == 2) {

                length = (dh.getLogSize(length, displayMin, radius, zeroMin2, 5));

            }
           */
            nodeLength.put(n, length);


            double x = Math.cos(angle) * length*scaleX + cx;
            double y = Math.sin(angle) * length*scaleX + cy;

            double topY = cy - 0.48 * height;
            double bottomY = cy + 0.48 * height;

            if (y < topY || y > bottomY) {

                if (x < cx || x == cx) {
                    leftMaxX = Math.max(leftMaxX, x);
                    leftOutBoundExist = true;
                    if (y < topY)
                        leftMaxAngle = Math.PI + Math.asin((0.5 * height - 15.00) / length);

                     else  if (y > bottomY)
                        leftMinAngle = Math.PI - Math.asin((0.5 * height - 15.00) / length);

                } else if (x > cx ) {

                    rightMinX = Math.min(rightMinX, x);

                     rightOutBoundExist = true;
                    if (y < topY)
                        rightMaxAngle = 2 * Math.PI - Math.asin((0.5 * height - 15.00) / length);
                    else  if (y > bottomY)
                        rightMinAngle = Math.asin((0.5 * height - 15.00) / length);
                }
            }
            nodeAngle.put(n, angle);

            Params np = getParams(n);

            np.loc[0] = x;
            np.loc[1] = y;


        }


       List<NodeItem> leftNodes = new ArrayList<NodeItem>();
        List<NodeItem> rightNodes = new ArrayList<NodeItem>();

        Iterator items1 = finalNodes.iterator();

        double leftMaxAngle1 = 0.0;
        double leftMinAngle1 = (3/2)*Math.PI;
        double rightMaxAngle1 = 2*Math.PI;
        double rightMinAngle1 = 0.0;

        while (items1.hasNext()) {

            NodeItem n = (NodeItem) items1.next();

            Params np = getParams(n);

                if ((np.loc[0] < leftMaxX) || (np.loc[0] == leftMaxX)) {

                    leftMinAngle1 = Math.min(leftMinAngle1, nodeAngle.get(n));
                    leftMaxAngle1 = Math.max(leftMaxAngle1, nodeAngle.get(n));
                    if(leftOutBoundExist){

                     leftNodes.add(n);
                    }
                }

               else if (np.loc[0] > rightMinX || np.loc[0] == rightMinX) {

                    if(rightOutBoundExist){

                         rightNodes.add(n);
                    }
                }

        }

        if (leftNodes.size() > 0) {

            if (leftMaxAngle == 0.0)
                leftMaxAngle = leftMaxAngle1;
            if (leftMinAngle == (3/2)*Math.PI)
                leftMinAngle = leftMinAngle1;
            double leftDisAngle = 0.0;
             if(leftNodes.size()==1)
              leftDisAngle = (leftMaxAngle - leftMinAngle)/2;
            else
           leftDisAngle = (leftMaxAngle - leftMinAngle) / (leftNodes.size() - 1);
            int j = 0;
            for (NodeItem n : leftNodes) {
                Params np = getParams(n);

                double realAngle = leftMinAngle + leftDisAngle * j;
                np.loc[0] = Math.cos(realAngle) * nodeLength.get(n) + cx;

                np.loc[1] = Math.sin(realAngle) * nodeLength.get(n) + cy;
                j++;
            }
        }

        if (rightNodes.size() > 0) {

            List<NodeItem> newRightNodes = new ArrayList<NodeItem>();
            List<NodeItem> lowerRightNodes = new ArrayList<NodeItem>();  // nodes located lower than cy
            List<NodeItem> upRightNodes = new ArrayList<NodeItem>();      // nodes located upper than cy

            for (NodeItem n : rightNodes) {

                Params np = getParams(n);

                if (np.loc[1] > cy || np.loc[1] == cy) {

                    lowerRightNodes.add(n);
                    rightMinAngle1 = Math.max(rightMinAngle1, nodeAngle.get(n));


                } else  if (np.loc[1] < cy) {

                    upRightNodes.add(n);
                    rightMaxAngle1 = Math.min(rightMaxAngle1, nodeAngle.get(n));

                }
            }


            Collections.reverse(lowerRightNodes);
            Collections.reverse(upRightNodes);
            newRightNodes.addAll(lowerRightNodes);
            newRightNodes.addAll(upRightNodes);

            if (rightMaxAngle == 0.0){

                rightMaxAngle = rightMaxAngle1;

            }
            if (rightMinAngle ==2*Math.PI) {

                rightMinAngle = rightMinAngle1;

            }

            double rightDisAngle = (2 * Math.PI - (rightMaxAngle - rightMinAngle)) / (rightNodes.size() - 1);
            int m = 0;

            for (NodeItem n : newRightNodes) {


                Params np = getParams(n);
                double realAngle = 0.00;


                realAngle = rightMinAngle - rightDisAngle * m;

                if (realAngle < 0)
                    realAngle = 2 * Math.PI + realAngle;

                np.loc[0] = Math.cos(realAngle) * nodeLength.get(n) + cx;

                np.loc[1] = Math.sin(realAngle) * nodeLength.get(n) + cy;
                m++;

            }
        }



       // locate nodes
        Iterator items4 = ts.tuples();


        while (items4.hasNext()) {

            NodeItem n = (NodeItem) items4.next();
            Params np = getParams(n);
            setX(n, null, np.loc[0]);
            setY(n, null, np.loc[1]);

        }

      //  if(hasOneRec){
       //              indirectNodes(oneRecNode);
        //        }

    }

    /**
     * specifies the recale factors
     *
     * @param x
     * @param y
     */
    public void setRescale(double x, double y) {
        scaleX = x;
        scaleY = y;
    }

    private List<NodeItem> getOrderedNodes(TupleSet ts, double seMax) {

        List<NodeItem> finalList = new ArrayList<NodeItem>();

        Map<NodeItem, Double> node_index = new HashMap<NodeItem, Double>();
        Set<Double> recsevalue = new HashSet<Double>();

        Iterator items1 = ts.tuples();

        for (int i = 0; items1.hasNext(); i++) {
            NodeItem n = null;
            if (i == 0) {
                n = (NodeItem) items1.next();

            } else {
                n = (NodeItem) items1.next();

                if( n.getString("nodeType").equals("Recommendation")){

                double recSe = seMax * n.getDouble("recSe");

                recsevalue.add(recSe);
                node_index.put(n, recSe);
                }
            }
        }

        double[] recSeArray = new double[recsevalue.size()];
        int n = 0;
        for (double recSe : recsevalue) {
            recSeArray[n] = recSe;
            n++;
        }

        Arrays.sort(recSeArray);

        List<NodeItem> nodeList = new ArrayList<NodeItem>();

        for (int i = recSeArray.length - 1; i > -1; i--) {

            for (NodeItem node : node_index.keySet()) {
                double se = node_index.get(node);
                if (recSeArray[i] == se) {
                    nodeList.add(node);

                }
            }
        }

        NodeItem[] nodeListArray = new NodeItem[nodeList.size()];
        nodeList.toArray(nodeListArray);

        List<NodeItem> evenList = new ArrayList<NodeItem>();
        List<NodeItem> oddList = new ArrayList<NodeItem>();

        for (int i = 0; i < nodeListArray.length / 2+1 ; i++) {
                try {

                 oddList.add(nodeListArray[nodeListArray.length -1-2*i]);

                    evenList.add(nodeListArray[nodeListArray.length -2-2*i]);

            } catch (Exception e) {

            }
        }

        NodeItem[] evenArray = new NodeItem[evenList.size()];
        evenList.toArray(evenArray);

        NodeItem[] oddArray = new NodeItem[oddList.size()];
        oddList.toArray(oddArray);


        List<NodeItem> evenList1 = new ArrayList<NodeItem>();
        List<NodeItem> evenList2 = new ArrayList<NodeItem>();

        for (int i = 0; i < evenArray.length / 2 + 1; i++) {

            try {
                evenList1.add(evenArray[2 * i]);
                evenList2.add(evenArray[2 * i + 1]);
            } catch (Exception e) {
            }
        }

        NodeItem[] evenArray1 = new NodeItem[evenList1.size()];
        evenList1.toArray(evenArray1);

        NodeItem[] evenArray2 = new NodeItem[evenList2.size()];
        evenList2.toArray(evenArray2);


        List<NodeItem> oddList1 = new ArrayList<NodeItem>();
        List<NodeItem> oddList2 = new ArrayList<NodeItem>();

        for (int i = 0; i < oddArray.length / 2 + 1; i++) {

            try {

                oddList1.add(oddArray[2 * i]);

                oddList2.add(oddArray[2 * i + 1]);
            } catch (Exception e) {

            }
        }

        NodeItem[] oddArray1 = new NodeItem[oddList1.size()];
        oddList1.toArray(oddArray1);

        NodeItem[] oddArray2 = new NodeItem[oddList2.size()];
        oddList2.toArray(oddArray2);

        for (int i = 0; i < evenArray1.length; i++) {
            try {
                if (!finalList.contains(evenArray1[i]))
                    finalList.add(evenArray1[i]);

            } catch (Exception e) {

            }
        }

        for (int i = 0; i < evenArray2.length; i++) {
            try {
                if (!finalList.contains(evenArray2[evenArray2.length - 1 - i]))
                    finalList.add(evenArray2[evenArray2.length - 1 - i]);

            } catch (Exception e) {

            }
        }

        for (int i = 0; i < oddArray1.length; i++) {
            try {
                if (!finalList.contains(oddArray1[i]))
                    finalList.add(oddArray1[i]);

            } catch (Exception e) {

            }
        }

        for (int i = 0; i < oddArray2.length; i++) {
            try {
                if (!finalList.contains(oddArray2[oddArray2.length - 1 - i]))
                    finalList.add(oddArray2[oddArray2.length - 1 - i]);

            } catch (Exception e) {

            }
        }

        return finalList;
    }


    // ------------------------------------------------------------------------
    // Params Schema

    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String PARAMS = "_RecLayerLayout";
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

    private Params getParams(NodeItem item) {
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

    }

}