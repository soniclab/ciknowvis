package visual;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 3, 2008
 * Time: 11:49:54 PM
 * To change this template use File | Settings | File Templates.
 */


import prefuse.action.layout.Layout;
import prefuse.visual.NodeItem;
import prefuse.data.tuple.TupleSet;

import java.awt.geom.Rectangle2D;
import java.util.*;


/**
 * Performs a random layout of items within the layout bounds.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class IndirectRecLayerLayout extends Layout {

    private TupleSet focus;
    private double cx, cy;

    private NodeItem node;
    private NodeItem node1;
    private boolean forShowAll, fromRequestor;
    private Map<String, double[]> nodeXYMap;
    private Set<NodeItem> recSet;

    /**
     * Create a new RandomLayout.
     *
     * @param group the data group to layout
     */
    public IndirectRecLayerLayout(String group, TupleSet focus, boolean forShowAll, Map<String, double[]> nodeXYMap, boolean fromRequestor, Set<NodeItem> recSet) {

        super(group);
        this.focus = focus;
        this.forShowAll = forShowAll;
        this.nodeXYMap = nodeXYMap;
        this.fromRequestor = fromRequestor;
        this.recSet = recSet;

    }

    public IndirectRecLayerLayout(String group, NodeItem node, boolean forShowAll, Map<String, double[]> nodeXYMap) {

        super(group);
        this.node = node;
        this.forShowAll = forShowAll;
        this.nodeXYMap = nodeXYMap;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {

        Rectangle2D r = getLayoutBounds();

        // cx = r.getCenterX();
        // cy = r.getCenterY();

        TupleSet ts = m_vis.getGroup("graph.nodes");
        Iterator iter = ts.tuples();

        for (int i = 0; iter.hasNext(); i++) {

            NodeItem item = (NodeItem) iter.next();
            if (item.isVisible()) {
                setX(item, null, item.getX());
                setY(item, null, item.getY());
                if (item.getString("nodeType").equals("Requester")) {
                    cx = item.getX();
                    cy = item.getY();
                    node1 = item;
                }
            }

        }
        indirectNodes(node,focus, recSet);
        if (nodeXYMap != null)
            resetNodes();
    }

   private void manageIndirectNodes(NodeItem node) {
         try {

            Set<NodeItem> indirectSet1 = new HashSet<NodeItem>();
            Set<NodeItem> indirectSet2 = new HashSet<NodeItem>();
            //   Iterator invisibleNodes = n.neighbors();
            Iterator invisibleNodes = null;
            if (forShowAll)

                invisibleNodes = node1.neighbors();
            else
                invisibleNodes = node.neighbors();

            while (invisibleNodes.hasNext()) {

                NodeItem invisibleNode = (NodeItem) invisibleNodes.next();


                if (!invisibleNode.getString("nodeType").equals("Requester") && !invisibleNode.getString("nodeType").equals("Recommendation")) {
                    if (forShowAll)
                        indirectSet2.add(invisibleNode);
                    else
                        indirectSet1.add(invisibleNode);


                    Iterator invisibleNodes1 = invisibleNode.neighbors();
                    while (invisibleNodes1.hasNext()) {

                        NodeItem invisibleNode1 = (NodeItem) invisibleNodes1.next();
                        if (!invisibleNode1.getString("nodeType").equals("Requester") && !invisibleNode1.getString("nodeType").equals("Recommendation")){
                               // && node.getDouble("recSe") < 0.4) {
                            if (forShowAll)
                                indirectSet1.add(invisibleNode1);
                            else
                                indirectSet2.add(invisibleNode1);

                        }
                    }
                }

            }

            if (indirectSet1.size() > 0)
                locateIndirectNodes(indirectSet1, indirectSet2, node);


        } catch (Exception e) {

        }
   }
    private void indirectNodes(NodeItem node, TupleSet focus, Set<NodeItem> recSet) {

        if(focus == null && recSet == null) {

             manageIndirectNodes(node);
        } else{
           if(recSet == null){
           Iterator items = focus.tuples();

            try{
               while (items.hasNext()) {

                    NodeItem n = (NodeItem) items.next();

                      manageIndirectNodes(n);

             }
            }catch(Exception e){

            }
           }else{
               for(NodeItem item: recSet){
                    manageIndirectNodes(item);
               }

           }
        }
    }


    private void locateIndirectNodes(Set<NodeItem> indirectSet1, Set<NodeItem> indirectSet2, NodeItem n) {

        int size1 = indirectSet1.size();
        int size2 = indirectSet2.size();
        // slope of perpendicular bisector of the line from "Recommendation" node to "Requester" node
        /* double slope = (n.getX() -cx)/(cy-n.getY());
        // middle point of segment from  "Recommendation" node to "Requester" node
        double xm = (cx+n.getX())/2;
        double ym = (cy+n.getY())/2;
       // equation of the point on theline:
        double x = 0.0;
        double y = 0.0;
    // distance between two points on the perpendicular bisector shpuld be 20
       int i = 1;

        for(NodeItem n1: indirectSet){


       double  xi = xm + (Math.pow((-1), i))*15*(i-1)/Math.sqrt(1+ slope*slope);

        double  yi =slope*(xi-xm) + ym;

             setX(n1, null, xi);
            setY(n1, null, yi);

            i++;
        }
        */

        double recLineAngle = Math.atan((cy - n.getY()) / (cx - n.getX())) + Math.PI / 24;
        if (n.getX() < cx)
            recLineAngle = Math.PI + Math.atan((cy - n.getY()) / (cx - n.getX())) + Math.PI / 24;
        //  double recLineAngle  = Math.PI/16;
        if (size2 == 0) {
            double radius = 0.5 * (Math.sqrt((cy - n.getY()) * (cy - n.getY()) + (cx - n.getX()) * (cx - n.getX())));

            // double radius = 0.5;
            int i = 1;
            for (NodeItem n1 : indirectSet1) {

                double angle = recLineAngle + (Math.PI * (i - (size1 + 1) / 2)) / (size1 + 1);
                //  double angle = (Math.PI * i) /size;

                double x = Math.cos(angle) * radius + cx;
                double y = Math.sin(angle) * radius + cy;

                //    double x = Math.cos(angle) * radius * + cx;
                //    double y = Math.sin(angle) * radius * + cy;

                setX(n1, null, x);
                setY(n1, null, y);


                i++;
            }
        } else {

            double radius1 = 0.666 * (Math.sqrt((cy - n.getY()) * (cy - n.getY()) + (cx - n.getX()) * (cx - n.getX())));

            double baseAngle = Math.PI;
             ////////////////////////
             Rectangle2D r= getLayoutBounds();
        double height = r.getHeight();

            double topY = cy - 0.48 * height;
            double bottomY = cy + 0.48 * height;

            double angle1 = recLineAngle + ((Math.PI) / (size1 + 1))* (size1 - (size1 + 1) / 2);
           double angle2 = recLineAngle +((Math.PI) / (size1 + 1))* (1 - (size1 + 1) / 2);

             double y1 = 0.00;
            y1 = -Math.sin(angle1) * radius1 + cy;

            double y2 =0.00;
            y2 = -Math.sin(angle2) * radius1 + cy;

            if(y1 > y2) {// y1 compare with bottomY, y2 compare with topY
                if(y1 > bottomY) {

                      if(angle1 > (3/2)*Math.PI || angle1 == (3/2)*Math.PI)
                       angle1 = (3/2)*Math.PI  + Math.acos(0.48 * height/radius1);
                      else if((angle1 < 0 && angle1>-(1/2)*Math.PI )|| angle1 == 0 || angle1 == (-(1/2)*Math.PI ))
                        angle1 =  -Math.asin(0.48 * height/radius1);
                     else if(angle1 < (3/2)*Math.PI && angle1>Math.PI)
                       angle1 = (3/2)*Math.PI  - Math.acos(0.48 * height/radius1);
                       else if(angle1 < -(1/2)*Math.PI && angle1>-Math.PI)
                        angle1 = -Math.PI + Math.asin(0.48 * height/radius1);
                }
                  if(y2 < topY) {

                      if(angle2 < (1/2)*Math.PI )
                       angle2 = Math.asin(0.48 * height/radius1);
                       else if(angle2 > (1/2)*Math.PI)
                        angle2 =Math.PI -Math.asin(0.48 * height/radius1);

                }

            } else{     // y2 compare with bottomY, y1 compare with topY
                if(y2 > bottomY) {

                      if(angle2 > (3/2)*Math.PI || angle1 == (3/2)*Math.PI)
                       angle2 = (3/2)*Math.PI  + Math.acos(0.48 * height/radius1);
                      else  if(angle2 < 0 && angle2> -Math.PI*0.5 || angle1 == 0 || angle1 == (-(1/2)*Math.PI )) {
                        angle2 =  -Math.asin(0.48 * height/radius1);

                       }
                     else if(angle2 < (3/2)*Math.PI && angle2>Math.PI)
                       angle2 = (3/2)*Math.PI  - Math.acos(0.48 * height/radius1);
                       else if(angle2 < -(1/2)*Math.PI && angle2>-Math.PI)
                        angle2 = -Math.PI + Math.asin(0.48 * height/radius1);
                }
                  if(y1 < topY) {

                       double standAngle = Math.PI*0.5;

                      if(angle1 < standAngle){
                       angle1 = Math.asin(0.48 * height/radius1);

                      } else if(angle1 >standAngle ){
                        angle1 =Math.PI -Math.asin(0.48 * height/radius1);

                      }
                }


            }

            baseAngle = Math.abs(angle2 - angle1);

            int i = 1;
        //    double single =  baseAngle/(size1 - 1);
            for (NodeItem n1 : indirectSet1) {

                double angle = recLineAngle + (baseAngle * (i - (size1 + 1) / 2)) / (size1 + 1);
               // double angle = recLineAngle + single * (i - (size1 + 1) / 2);
                double x = Math.cos(angle) * radius1 + cx;
                double y = Math.sin(angle) * radius1 + cy;

                setX(n1, null, x);
                setY(n1, null, y);

                i++;

            }

            double radius2 = 0.333 * (Math.sqrt((cy - n.getY()) * (cy - n.getY()) + (cx - n.getX()) * (cx - n.getX())));

            // double radius = 0.5;
            int j = 1;
            for (NodeItem n1 : indirectSet2) {

                double angle = recLineAngle + (Math.PI * (j - (size2 + 1) / 2)) / (size2 + 1);
                //  double angle = (Math.PI * i) /size;

                double x = Math.cos(angle) * radius2 + cx;
                double y = Math.sin(angle) * radius2 + cy;

                setX(n1, null, x);
                setY(n1, null, y);

                j++;
            }
        }

    }


    private void resetNodes() {

        TupleSet ts = m_vis.getGroup("graph.nodes");

        Iterator iter = ts.tuples();

        for (int i = 0; iter.hasNext(); i++) {

            NodeItem item = (NodeItem) iter.next();

            if (nodeXYMap.get(item.getString("login")) != null){

                    double[] xy = nodeXYMap.get(item.getString("login"));
                    setX(item, null, xy[0]);
                    setY(item, null, xy[1]);

            }

        }


    }
} // end of class RandomLayout



