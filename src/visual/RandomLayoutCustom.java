package visual;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 3, 2008
 * Time: 11:49:54 PM
 * To change this template use File | Settings | File Templates.
 */


import prefuse.action.layout.Layout;
import prefuse.visual.VisualItem;
import prefuse.Display;
import prefuse.data.tuple.TupleSet;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;


/**
 * Performs a random layout of items within the layout bounds.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class RandomLayoutCustom extends Layout {
    private double scaleX = 1.0, scaleY = 1.0;
    private Random r = new Random(12345678L);
    protected Point2D m_origin;


    /**
     * Create a new RandomLayout.
     *
     * @param group the data group to layout
     */
    public RandomLayoutCustom(String group, Display display) {

        super(group);
        double x = display.getWidth();
        double y = display.getHeight();
        double min = x < y ? x : y;
        scaleX = x / min;
        scaleY = y / min;

    }

    /**
     * Set the seed value for the random number generator.
     *
     * @param seed the random seed value
     */
    public void setRandomSeed(long seed) {
        r.setSeed(seed);
    }


    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {

        Rectangle2D b = getLayoutBounds();
        double x, y;
        double w = b.getWidth() - 75;
        double h = b.getHeight() - 25;

        TupleSet ts = m_vis.getGroup(m_group);

        Iterator items = ts.tuples();
        Set<VisualItem> itemSet = new HashSet<VisualItem>();
        for (int i = 0; items.hasNext(); i++) {

            VisualItem n = (VisualItem) items.next();
            if(n.isVisible())
              itemSet.add(n);
        }

       for(VisualItem item: itemSet) {

            x = (int) (b.getX() + r.nextDouble() * w);
            y = (int) (b.getY() + r.nextDouble() * h);
            setX(item, null, x);
            setY(item, null, y);
        }

        /*  Rectangle2D b = getLayoutBounds();
      double x, y;
      double w = b.getWidth();
      double h = b.getHeight();
      Iterator iter = getVisualization().visibleItems(m_group);
      while ( iter.hasNext() ) {
          VisualItem item = (VisualItem)iter.next();
          x = (int)(b.getX() + scaleX*r.nextDouble()*w);
          y = (int)(b.getY() + scaleY*r.nextDouble()*h);
          setX(item,null,x);
          setY(item,null,y);
      }  */
    }


             
} // end of class RandomLayout



