package visual;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;
import prefuse.Constants;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Sep 8, 2009
 * Time: 11:53:38 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Renderer for drawing simple shapes. This class provides a number of built-in
 * shapes, selected by an integer value retrieved from a VisualItem.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class NodeShapeRenderer extends AbstractShapeRenderer {

    private int m_baseSize = 10;

    private Ellipse2D m_ellipse = new Ellipse2D.Double();
    private Rectangle2D m_rect = new Rectangle2D.Double();
    private GeneralPath m_path = new GeneralPath();
     private Map<String, String> attrShape;
    /**
     * Creates a new ShapeRenderer with default base size of 10 pixels.
     */
    public NodeShapeRenderer(Map<String, String> attrShape) {
         this.attrShape =  attrShape;
    }

    /**
     * Creates a new ShapeRenderer with given base size.
     * @param size the base size in pixels
     */
    public NodeShapeRenderer(int size) {
       setBaseSize(size);
    }

    /**
     * Sets the base size, in pixels, for shapes drawn by this renderer. The
     * base size is the width and height value used when a VisualItem's size
     * value is 1. The base size is scaled by the item's size value to arrive
     * at the final scale used for rendering.
     * @param size the base size in pixels
     */
    public void setBaseSize(int size) {
        m_baseSize = size;
    }

    /**
     * Returns the base size, in pixels, for shapes drawn by this renderer.
     * @return the base size in pixels
     */
    public int getBaseSize() {
        return m_baseSize;
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {
         String  itemAttri = item.getString("shapeAttri");
           int stype= 0;
            for(String attri:  attrShape.keySet()) {
               if(itemAttri.equalsIgnoreCase(attri)) {
                    stype = Integer.parseInt(attrShape.get(attri));
               }
            }

        double x = item.getX();
        if ( Double.isNaN(x) || Double.isInfinite(x) )
            x = 0;
        double y = item.getY();
        if ( Double.isNaN(y) || Double.isInfinite(y) )
            y = 0;
        double width = m_baseSize*item.getSize();

        // Center the shape around the specified x and y
        if ( width > 1 ) {
            x = x-width/2;
            y = y-width/2;
        }

        switch ( stype ) {
        case Constants.SHAPE_NONE:
            return null;
        case Constants.SHAPE_RECTANGLE:
            return rectangle(x, y, width, width);
        case Constants.SHAPE_ELLIPSE:
            return ellipse(x, y, width, width);
        case Constants.SHAPE_TRIANGLE_UP:
            return triangle_up((float)x, (float)y, (float)width);
        case Constants.SHAPE_CROSS:
            return cross((float)x, (float)y, (float)width);
        case Constants.SHAPE_STAR:
            return star((float)x, (float)y, (float)width);
        case Constants.SHAPE_HEXAGON:
            return hexagon((float)x, (float)y, (float)width);
        case Constants.SHAPE_DIAMOND:
            return diamond((float)x, (float)y, (float)width);
             case 6:
            return ellipse(x, y, width+ 3, width-3);
         case 7:
            return rectangle(x, y, width+3, width-3);
          case 8:
            return trapezoid((float)x, (float)y, (float)width);
        default:
            throw new IllegalStateException("Unknown shape type: "+stype);
        }
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
        m_path.moveTo(x,y+height);
        m_path.lineTo(x+height/2, y);
        m_path.lineTo(x+height, (y+height));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a down-pointing triangle of the given dimenisions.
     */
    public Shape triangle_down(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x,y);
        m_path.lineTo(x+height, y);
        m_path.lineTo(x+height/2, (y+height));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a left-pointing triangle of the given dimenisions.
     */
    public Shape triangle_left(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x+height, y);
        m_path.lineTo(x+height, y+height);
        m_path.lineTo(x, y+height/2);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a right-pointing triangle of the given dimenisions.
     */
    public Shape triangle_right(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x,y+height);
        m_path.lineTo(x+height, y+height/2);
        m_path.lineTo(x, y);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a cross shape of the given dimenisions.
     */
    public Shape cross(float x, float y, float height) {
        float h14 = 3*height/8, h34 = 5*height/8;
        m_path.reset();
        m_path.moveTo(x+h14, y);
        m_path.lineTo(x+h34, y);
        m_path.lineTo(x+h34, y+h14);
        m_path.lineTo(x+height, y+h14);
        m_path.lineTo(x+height, y+h34);
        m_path.lineTo(x+h34, y+h34);
        m_path.lineTo(x+h34, y+height);
        m_path.lineTo(x+h14, y+height);
        m_path.lineTo(x+h14, y+h34);
        m_path.lineTo(x, y+h34);
        m_path.lineTo(x, y+h14);
        m_path.lineTo(x+h14, y+h14);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a star shape of the given dimenisions.
     */
    public Shape star(float x, float y, float height) {
        float s = (float)(height/(2*Math.sin(Math.toRadians(54))));
        float shortSide = (float)(height/(2*Math.tan(Math.toRadians(54))));
        float mediumSide = (float)(s*Math.sin(Math.toRadians(18)));
        float longSide = (float)(s*Math.cos(Math.toRadians(18)));
        float innerLongSide = (float)(s/(2*Math.cos(Math.toRadians(36))));
        float innerShortSide = innerLongSide*(float)Math.sin(Math.toRadians(36));
        float innerMediumSide = innerLongSide*(float)Math.cos(Math.toRadians(36));

        m_path.reset();
        m_path.moveTo(x, y+shortSide);
        m_path.lineTo((x+innerLongSide),(y+shortSide));
        m_path.lineTo((x+height/2),y);
        m_path.lineTo((x+height-innerLongSide),(y+shortSide));
        m_path.lineTo((x+height),(y+shortSide));
        m_path.lineTo((x+height-innerMediumSide),(y+shortSide+innerShortSide));
        m_path.lineTo((x+height-mediumSide),(y+height));
        m_path.lineTo((x+height/2),(y+shortSide+longSide-innerShortSide));
        m_path.lineTo((x+mediumSide),(y+height));
        m_path.lineTo((x+innerMediumSide),(y+shortSide+innerShortSide));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a hexagon shape of the given dimenisions.
     */
    public Shape hexagon(float x, float y, float height) {
        float width = height/2;

        m_path.reset();
        m_path.moveTo(x,y+0.5f*height);
        m_path.lineTo(x+0.5f*width, y);
        m_path.lineTo(x+1.5f*width, y);
        m_path.lineTo(x+2.0f*width, y+0.5f*height);
        m_path.lineTo(x+1.5f*width, y+height);
        m_path.lineTo(x+0.5f*width, y+height);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a diamond shape of the given dimenisions.
     */
    public Shape diamond(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x,(y+0.5f*height));
        m_path.lineTo((x+0.8f*height),y);
        m_path.lineTo((x+1.6f*height),(y+0.5f*height));
        m_path.lineTo((x+0.8f*height),(y+height));
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
          m_path.moveTo(x,y+0.5f*height);
        m_path.lineTo((x+1.2f*height),y +0.5f* height);
        m_path.lineTo((x+0.9f*height),(y-0.2f*height));
        m_path.lineTo((x+0.3f*height),(y-0.2f*height));
        m_path.closePath();
          return m_path;
    }

} // end of class ShapeRenderer
