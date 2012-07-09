package visual;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Oct 12, 2009
 * Time: 3:25:27 PM
 * To change this template use File | Settings | File Templates.
 */


import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Map;

import prefuse.render.AbstractShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;
import prefuse.Constants;

public class LabelShapeRenderer extends AbstractShapeRenderer {

private NodeShapeRenderer m_shapeRenderer;
private LabelRenderer m_labelRenderer;
private boolean labelNode;
public LabelShapeRenderer(Map<String, String> attrShape, String textField, boolean labelNode) {
m_shapeRenderer = new NodeShapeRenderer(attrShape);
m_labelRenderer = new LabelRenderer(textField);
 //m_labelRenderer.setHorizontalAlignment(Constants.LEFT);
 m_labelRenderer.setVerticalAlignment(Constants.BOTTOM);
m_labelRenderer.setVerticalPadding(4);


m_labelRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_NONE);
    this.labelNode = labelNode;
}


@Override
public void render(Graphics2D g, VisualItem item) {
m_shapeRenderer.render(g, item);
 if(labelNode)
m_labelRenderer.render(g, item);
}


@Override
///XXX getRawShape should return the "raw", not transformed shape!
//Actually, it just returns the (already transformed) ShapeRenderers shape.
protected Shape getRawShape(VisualItem item) {
return m_shapeRenderer.getShape(item);
}

}
