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


public class CircleLayoutCustom extends CircleLayout {
    private double scaleX = 1.0, scaleY = 1.0;

    public CircleLayoutCustom(String group, Display display) {
        super(group);

        double x = display.getWidth();
        double y = display.getHeight();
        double min = x < y ? x : y;
        scaleX = x / min;
        scaleY = y / min;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
     
        TupleSet ts = m_vis.getGroup(m_group);

        Iterator items = ts.tuples();
        List<VisualItem> itemSet = new ArrayList<VisualItem>();
        for (int i = 0; items.hasNext(); i++) {

            VisualItem n = (VisualItem) items.next();
            if(n.isVisible())
              itemSet.add(n);
        }

        int nn = itemSet.size();

        Rectangle2D r = getLayoutBounds();
        double height = r.getHeight();
        double width = r.getWidth();
        double cx = r.getCenterX();
        double cy = r.getCenterY();

        double radius = getRadius();
        if (radius <= 0) {
            radius = 0.45 * (height < width ? height : width);
        }

         int i = 0;
        for (VisualItem n: itemSet) {

            double angle = (2 * Math.PI * i) /nn;
            double x = Math.cos(angle) * radius * scaleX + cx;
            double y = Math.sin(angle) * radius * scaleY + cy;
            setX(n, null, x);
            setY(n, null, y);
          i++;
        }
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