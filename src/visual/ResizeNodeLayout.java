package visual;

import prefuse.action.layout.Layout;
import prefuse.visual.VisualItem;
import prefuse.data.tuple.TupleSet;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 3, 2008
 * Time: 11:49:54 PM
 * To change this template use File | Settings | File Templates.
 */


public class ResizeNodeLayout extends Layout {
    private double comX = 0, comY = 0;
    protected Point2D m_origin;
   // private boolean changed = false;
    private int layoutNumber;

    public ResizeNodeLayout(String group) {
        super(group);
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    @SuppressWarnings({"PointlessBooleanExpression"})
    public void run(double frac) {

        TupleSet ts = m_vis.getGroup(m_group);

        Iterator items = ts.tuples();
        for (int i = 0; items.hasNext(); i++) {
            VisualItem n = (VisualItem) items.next();
           // if (changed == true) {
                setX(n, null, n.getX());
                setY(n, null, n.getY());
           // }
        }
    }

   // public void nodeSizeChanged(boolean changed) {
     //   this.changed = changed;

   // }
}