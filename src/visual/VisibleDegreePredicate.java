package visual;

import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractPredicate;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 19, 2008
 * Time: 1:53:05 PM
 * To change this template use File | Settings | File Templates.
 */
class VisibleDegreePredicate extends AbstractPredicate {

    private Visualization vis;
    private String nodeGroupName;

    private int minDegree;


    public VisibleDegreePredicate(Visualization vis, String nodeGroupName) {
        this(vis, nodeGroupName, 1);
    }

    public VisibleDegreePredicate(Visualization vis, String nodeGroupName, int minDegree) {
        this.vis = vis;
        this.nodeGroupName = nodeGroupName;
        this.minDegree = minDegree;
    }

    public int getMinDegree() {
        return minDegree;
    }

    public void setMinDegree(int n) {
        minDegree = n;
    }

    public boolean getBoolean(Tuple t) {
        if (t instanceof Edge) {
            return false;
        }

        NodeItem vi = (NodeItem) vis.getVisualItem(nodeGroupName, t);

        Iterator iter = vi.edges();

        boolean result = false;
        int n = 0;
        while (!result && iter.hasNext()) {
            EdgeItem aEdgeItem = (EdgeItem) iter.next();
            if (aEdgeItem.isVisible()) {
                n++;
            }
            if (n >= minDegree) {
                result = true;
            }

        }

        return result;
    }


}
