package visual;

import prefuse.action.GroupAction;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.NodeItem;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jun 23, 2008
 * Time: 3:38:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class LimitSizeAction extends GroupAction {

    private double maximumScale;
    private String nodes;

    public LimitSizeAction(String nodesStr) {

        this(1.0, nodesStr);

    }

    public LimitSizeAction(double maximumScale, String nodesStr) {
        this.maximumScale = maximumScale;
        nodes = nodesStr;

    }

    public void run(double frac) {

        TupleSet allTuple = m_vis.getVisualGroup(nodes);

        Iterator allIems = allTuple.tuples();
        double scale = getVisualization().getDisplay(0).getScale();
        while (allIems.hasNext()) {

            NodeItem item = (NodeItem) allIems.next();


            if (scale != maximumScale) {
                item.setSize(maximumScale / scale);
            } else {
                item.setSize(1.0);
            }

        }
    }
}
