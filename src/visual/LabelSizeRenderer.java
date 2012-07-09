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
import prefuse.visual.VisualItem;
import prefuse.Constants;
import data.AppletDataHandler1;

public class LabelSizeRenderer extends AbstractShapeRenderer {

private NodeSizeShapeRenderer m_shapeRenderer;
private LabelRendererCustom m_labelRenderer;
private boolean labelNode;
public LabelSizeRenderer(String textField, boolean labelNode) {
m_shapeRenderer = new NodeSizeShapeRenderer(true);
m_labelRenderer = new LabelRendererCustom(textField);
// m_labelRenderer.setHorizontalAlignment(Constants.LEFT);
  m_labelRenderer.setVerticalAlignment(Constants.BOTTOM);
 
m_labelRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_NONE);
    this.labelNode = labelNode;
}




 public LabelSizeRenderer(GraphView gv, Map<String, String> shapeMap, boolean fromShapedNode, AppletDataHandler1 dh, int normalizedType1, int normalizedType2, double displayMin, double displayMax, double displayMin2, double displayMax2, boolean zeroMin1, boolean zeroMin2, String textField, boolean labelNode) {

m_shapeRenderer = new NodeSizeShapeRenderer(gv, shapeMap, fromShapedNode, dh, normalizedType1, normalizedType2, displayMin, displayMax, displayMin2, displayMax2, zeroMin1, zeroMin2);

m_labelRenderer = new LabelRendererCustom(textField);
// m_labelRenderer.setHorizontalAlignment(Constants.LEFT);
  m_labelRenderer.setVerticalAlignment(Constants.BOTTOM);


m_labelRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_NONE);
    this.labelNode = labelNode;
}


@Override
public void render(Graphics2D g, VisualItem item) {

m_shapeRenderer.render(g, item);
 if(labelNode)
m_labelRenderer.render(g, item);
  // m_labelRenderer.setVerticalPadding((int)item.getSize());

}


@Override
///XXX getRawShape should return the "raw", not transformed shape!
//Actually, it just returns the (already transformed) ShapeRenderers shape.
protected Shape getRawShape(VisualItem item) {
   
return m_shapeRenderer.getShape(item);


}

}