package visual;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.Map;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;
import prefuse.visual.NodeItem;
import prefuse.data.tuple.TupleSet;
import prefuse.Constants;
import data.AppletDataHandler1;


public class NodeSizeShapeRenderer extends AbstractShapeRenderer {


    private Rectangle2D m_rect = new Rectangle2D.Double();
    private Ellipse2D m_ellipse = new Ellipse2D.Double();
    private GeneralPath m_path = new GeneralPath();
    private AppletDataHandler1 dh;
    private boolean forMinNode, zeroMin1, zeroMin2;
    private int normalizedType1, normalizedType2;
    private double displayMin, displayMax, displayMin2, displayMax2, idMax;
    private Map<String, String> attrShape;
    private boolean fromShapedNode;

    /**
     * Creates a new ShapeRenderer with default base size of 10 pixels.
     */
    public NodeSizeShapeRenderer(GraphView gv, Map<String, String> shapeMap, boolean fromShapedNode, AppletDataHandler1 dh, int normalizedType1, int normalizedType2, double displayMin, double displayMax, double displayMin2, double displayMax2, boolean zeroMin1, boolean zeroMin2) {

        this.dh = dh;
        this.attrShape = shapeMap;
        this.fromShapedNode = fromShapedNode;
        this.normalizedType1 = normalizedType1;
        this.normalizedType2 = normalizedType2;
        this.displayMax = displayMax;
        this.displayMin = displayMin;

        this.displayMax2 = displayMax2;
        this.displayMin2 = displayMin2;

        this.zeroMin1 = zeroMin1;
        this.zeroMin2 = zeroMin2;


        TupleSet ts = gv.getCurrentVis().getGroup("graph.nodes");
        Iterator items2 = ts.tuples();


        for (int i = 0; i < ts.getTupleCount(); i++) {
            NodeItem n = (NodeItem) items2.next();

            if (n.getString("nodeType").equals("Recommendation")) {
              try{

                    idMax =dh.getMaxRecSize();
              }catch(Exception e){
                 System.out.println("exception when getting max recId value: " + e);
              }
            }
        }

    }

    public NodeSizeShapeRenderer(boolean forMinNode) {
        this.forMinNode = forMinNode;
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {

        Shape shape = null;
        int shapeType = 0;
        double x = item.getX();
        if (Double.isNaN(x) || Double.isInfinite(x))
            x = 0;
        double y = item.getY();
        if (Double.isNaN(y) || Double.isInfinite(y))
            y = 0;
        double w_baseSize = 0.5;

        if (!forMinNode) {
            shapeType = dh.getNodeSizeType();
            try {

                double nodesize;
                if (shapeType == 3) {
                    String idMetric = dh.getAttriRecIdMetric();
                    if (idMetric.equals("sp") || idMetric.startsWith("sm.")) {
                        if(item.getDouble("recId")!= 0.0)
                        nodesize = idMax / item.getDouble("recId");
                        else
                        nodesize = 0.0; 
                    } else

                        nodesize = item.getDouble("recId");

                    displayMin2 = 0;
                    displayMax2 = 0;
                    zeroMin2 = false;

                } else{

                    nodesize = item.getDouble("nodeSize");

                }

                
                if (zeroMin1)
                    nodesize = nodesize + 1.00;
                if (normalizedType1 == 0) {
                    w_baseSize = 0.5 * nodesize;

                } else if (normalizedType1 == 1) {
                    if (shapeType == 3) {// for RecScores
                        w_baseSize = 0.5 * (dh.getLinearSize(nodesize, displayMin, displayMax, zeroMin1, 4));

                        if (item.getString("nodeType").equals("Requester"))
                            w_baseSize = 1.00;
                        else if (!item.getString("nodeType").equals("Recommendation"))
                            w_baseSize = 0.5;
                      
                    } else
                        w_baseSize = 0.5 * (dh.getLinearSize(nodesize, displayMin, displayMax, zeroMin1, 2));
                } else if (normalizedType1 == 2) {
                    if (shapeType == 3) {
                        w_baseSize = 0.5 * (dh.getLogSize(nodesize, displayMin, displayMax, zeroMin1, 4));
                        if (item.getString("nodeType").equals("Requester"))
                            w_baseSize = 1.00;

                        else if (!item.getString("nodeType").equals("Recommendation"))
                            w_baseSize = 0.5;
                    } else
                        w_baseSize = 0.5 * (dh.getLogSize(nodesize, displayMin, displayMax, zeroMin1, 2));
                }

            } catch (Exception e) {

            }
        }


        double h_baseSize = w_baseSize;

        if (!forMinNode) {
            if (shapeType == 2) {

                try {
                    double nodesize2 = item.getDouble("nodeSize2");
                    if (zeroMin2)
                        nodesize2 = nodesize2 + 1.00;

                    if (normalizedType2 == 0)
                        h_baseSize = 0.5 * nodesize2;
                    else if (normalizedType2 == 1)
                        h_baseSize = 0.5 * (dh.getLinearSize(nodesize2, displayMin2, displayMax2, zeroMin2, 3));
                    else if (normalizedType2 == 2)
                        h_baseSize = 0.5 * (dh.getLogSize(nodesize2, displayMin2, displayMax2, zeroMin2, 3));
                } catch (Exception e) {

                }
            }
        }
        double width = w_baseSize * item.getSize() * 12;

        double height = h_baseSize * item.getSize() * 12;

        if (width > 12.0)
            width = 12 * (1 + width / 12 * 0.4);
        if (height > 12.0)
            height = 12 * (1 + height / 12 * 0.4);
        if (width > 1)
            x = x - width / 2;
        if (height > 1)
            y = y - height / 2;

        shape = ellipse(x, y, width, width);
        if (!forMinNode) {
            if (shapeType == 2)
                shape = rectangle(x, y, width, height);
            if (shapeType == 1) {

                if (attrShape == null)
                    shape = ellipse(x, y, width, width);
                else if (fromShapedNode) { // from node shape

                    String itemAttri = item.getString("shapeAttri");
                    int stype = 0;
                    for (String attri : attrShape.keySet()) {
                        if (itemAttri.equalsIgnoreCase(attri)) {
                            stype = Integer.parseInt(attrShape.get(attri));
                        }
                    }

                    switch (stype) {
                        case Constants.SHAPE_NONE:
                            return null;
                        case Constants.SHAPE_RECTANGLE:
                            return rectangle(x, y, width, width);
                        case Constants.SHAPE_ELLIPSE:
                            return ellipse(x, y, width, width);
                        case Constants.SHAPE_TRIANGLE_UP:
                            return triangle_up((float) x, (float) y, (float) width);
                        case Constants.SHAPE_CROSS:
                            return cross((float) x, (float) y, (float) width);
                        case Constants.SHAPE_STAR:
                            return star((float) x, (float) y, (float) width);
                        case Constants.SHAPE_HEXAGON:
                            return hexagon((float) x, (float) y, (float) width);
                        case Constants.SHAPE_DIAMOND:
                            return diamond((float) x, (float) y, (float) width);
                        case 6:
                            return ellipse(x, y, width + 3, width - 3);
                        case 7:
                            return rectangle(x, y, width + 3, width - 3);
                        case 8:
                            return trapezoid((float) x, (float) y, (float) width);
                        default:
                            throw new IllegalStateException("Unknown shape type: " + stype);
                    }//switch

                }
            }
        }
        return shape;

    }

    /**
     * Returns a rectangle of the given dimenisions.
     */
    public Shape rectangle(double x, double y, double width, double height) {
        m_rect.setFrame(x, y, width, height);
        return m_rect;
    }

    /**
     * Returns an ellipse of the given dimenisions.
     */
    public Shape ellipse(double x, double y, double width, double height) {
        m_ellipse.setFrame(x, y, width, height);
        return m_ellipse;
    }


    /**
     * Returns a up-pointing triangle of the given dimenisions.
     */
    public Shape triangle_up(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, y + height);
        m_path.lineTo(x + height / 2, y);
        m_path.lineTo(x + height, (y + height));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a down-pointing triangle of the given dimenisions.
     */
    public Shape triangle_down(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, y);
        m_path.lineTo(x + height, y);
        m_path.lineTo(x + height / 2, (y + height));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a left-pointing triangle of the given dimenisions.
     */
    public Shape triangle_left(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x + height, y);
        m_path.lineTo(x + height, y + height);
        m_path.lineTo(x, y + height / 2);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a right-pointing triangle of the given dimenisions.
     */
    public Shape triangle_right(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, y + height);
        m_path.lineTo(x + height, y + height / 2);
        m_path.lineTo(x, y);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a cross shape of the given dimenisions.
     */
    public Shape cross(float x, float y, float height) {
        float h14 = 3 * height / 8, h34 = 5 * height / 8;
        m_path.reset();
        m_path.moveTo(x + h14, y);
        m_path.lineTo(x + h34, y);
        m_path.lineTo(x + h34, y + h14);
        m_path.lineTo(x + height, y + h14);
        m_path.lineTo(x + height, y + h34);
        m_path.lineTo(x + h34, y + h34);
        m_path.lineTo(x + h34, y + height);
        m_path.lineTo(x + h14, y + height);
        m_path.lineTo(x + h14, y + h34);
        m_path.lineTo(x, y + h34);
        m_path.lineTo(x, y + h14);
        m_path.lineTo(x + h14, y + h14);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a star shape of the given dimenisions.
     */
    public Shape star(float x, float y, float height) {
        float s = (float) (height / (2 * Math.sin(Math.toRadians(54))));
        float shortSide = (float) (height / (2 * Math.tan(Math.toRadians(54))));
        float mediumSide = (float) (s * Math.sin(Math.toRadians(18)));
        float longSide = (float) (s * Math.cos(Math.toRadians(18)));
        float innerLongSide = (float) (s / (2 * Math.cos(Math.toRadians(36))));
        float innerShortSide = innerLongSide * (float) Math.sin(Math.toRadians(36));
        float innerMediumSide = innerLongSide * (float) Math.cos(Math.toRadians(36));

        m_path.reset();
        m_path.moveTo(x, y + shortSide);
        m_path.lineTo((x + innerLongSide), (y + shortSide));
        m_path.lineTo((x + height / 2), y);
        m_path.lineTo((x + height - innerLongSide), (y + shortSide));
        m_path.lineTo((x + height), (y + shortSide));
        m_path.lineTo((x + height - innerMediumSide), (y + shortSide + innerShortSide));
        m_path.lineTo((x + height - mediumSide), (y + height));
        m_path.lineTo((x + height / 2), (y + shortSide + longSide - innerShortSide));
        m_path.lineTo((x + mediumSide), (y + height));
        m_path.lineTo((x + innerMediumSide), (y + shortSide + innerShortSide));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a hexagon shape of the given dimenisions.
     */
    public Shape hexagon(float x, float y, float height) {
        float width = height / 2;

        m_path.reset();
        m_path.moveTo(x, y + 0.5f * height);
        m_path.lineTo(x + 0.5f * width, y);
        m_path.lineTo(x + 1.5f * width, y);
        m_path.lineTo(x + 2.0f * width, y + 0.5f * height);
        m_path.lineTo(x + 1.5f * width, y + height);
        m_path.lineTo(x + 0.5f * width, y + height);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a diamond shape of the given dimenisions.
     */
    public Shape diamond(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, (y + 0.5f * height));
        m_path.lineTo((x + 0.8f * height), y);
        m_path.lineTo((x + 1.6f * height), (y + 0.5f * height));
        m_path.lineTo((x + 0.8f * height), (y + height));
        m_path.closePath();
        return m_path;
    }


    public Shape trapezoid(float x, float y, float height) {
        /* m_path.reset();
     m_path.moveTo(x,y);
     m_path.lineTo((x+1.2f*height),y);
     m_path.lineTo((x+0.9f*height),(y-0.7f*height));
     m_path.lineTo((x+0.3f*height),(y-0.7f*height));
     m_path.closePath();
     return m_path;  */
        m_path.reset();
        m_path.moveTo(x, y + 0.5f * height);
        m_path.lineTo((x + 1.2f * height), y + 0.5f * height);
        m_path.lineTo((x + 0.9f * height), (y - 0.2f * height));
        m_path.lineTo((x + 0.3f * height), (y - 0.2f * height));
        m_path.closePath();
        return m_path;
    }

} // end of class ShapeRenderer
