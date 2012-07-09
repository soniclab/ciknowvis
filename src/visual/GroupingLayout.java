package visual;

import prefuse.action.layout.CircleLayout;
import prefuse.visual.VisualItem;
import prefuse.data.tuple.TupleSet;
import prefuse.Display;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 3, 2008
 * Time: 11:49:54 PM
 * To change this template use File | Settings | File Templates.
 */


public class GroupingLayout extends CircleLayout {
    private double scaleX = 1.0, scaleY = 1.0;

    private String[] nodeIndex;
    private  Map<String, VisualItem>  nodeMap;
    private  TupleSet aggrTuple;
    private boolean grLayoutChanged, isWindowsResized;
    public GroupingLayout(String group, Display display, Set ais, String[] nodeIndex, Map<String, VisualItem>  nodeMap,  TupleSet aggrTuple, boolean grLayoutChanged, boolean isWindowsResized) {
        super(group);

        double x = display.getWidth();
        double y = display.getHeight();
        double min = x < y ? x : y;
        scaleX = x / min;
        scaleY = y / min;

            this.nodeIndex = nodeIndex;
        this.nodeMap = nodeMap;
        this.aggrTuple =  aggrTuple;
        this.grLayoutChanged = grLayoutChanged;
        this.isWindowsResized = isWindowsResized;


    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
    //    TupleSet ts = m_vis.getGroup(m_group);

     //   int nn = ts.getTupleCount();
       if(grLayoutChanged == true && !isWindowsResized){
            for (int m = 0; m < nodeIndex.length; ++m) {
                VisualItem n =  nodeMap.get(nodeIndex[m]);
                    setX( n, null, n.getX());
                    setY( n, null, n.getY());
            }
       }else{

        Rectangle2D r = getLayoutBounds();
        double height = r.getHeight();
        double width = r.getWidth();
        double cx = r.getCenterX();
        double cy = r.getCenterY();

        double radius = getRadius();
        if (radius <= 0) {
            radius = 0.45 * (height < width ? height : width);
        }

     //   Iterator items = ts.tuples();
        int nn = nodeMap.size();
        for (int m = 0; m < nodeIndex.length; ++m) {

                VisualItem n =  nodeMap.get(nodeIndex[m]);
                 double angle = (2 * Math.PI * m) / nn;
                    double x = Math.cos(angle) * radius * scaleX + cx;
                    double y = Math.sin(angle) * radius * scaleY + cy;
                    setX( n, null, x);
                    setY( n, null, y);

        }

        }
///////////////////////////////////////
     /*   Iterator iter1 = g.nodes();
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
         */
        ///////////////////////////////////////////
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
}