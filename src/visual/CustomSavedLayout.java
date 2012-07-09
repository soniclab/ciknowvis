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
import prefuse.Display;
import prefuse.data.tuple.TupleSet;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Map;

import data.AppletDataHandler1;


/**
 * Performs a random layout of items within the layout bounds.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CustomSavedLayout extends Layout {
    private double scaleX = 1.0, scaleY = 1.0;
    private AppletDataHandler1 dh;
    private Map<String, double[]>  nodeXYMap;
    /**
     * Create a new RandomLayout.
     *
     * @param group the data group to layout
     */
    public CustomSavedLayout(String group, Display display, Map<String, double[]> nodeXYMap) {

        super(group);
      /*   double x = display.getWidth();
        double y = display.getHeight();
        double min = x < y ? x : y;
        scaleX = x / min;
        scaleY =y / min;  */
        this.nodeXYMap = nodeXYMap;

    }


    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {

        Rectangle2D b = getLayoutBounds();

        TupleSet ts = m_vis.getGroup("graph.nodes");

                Iterator  iter = ts.tuples();

                for (int i = 0;  iter.hasNext(); i++) {
               
           NodeItem item = (NodeItem) iter.next();
            if(nodeXYMap != null){
             double[] xy =nodeXYMap.get(item.getString("login"));
              setX(item, null, xy[0]);
            setY(item, null, xy[1]);

            }else{
            setX(item, null, item.getDouble("x")*scaleX);
            setY(item, null, item.getDouble("y")*scaleY );
            }
      
        }

    }

   public void setRescale(double x, double y) {
        scaleX = x;
        scaleY = y;

    }

} // end of class RandomLayout



