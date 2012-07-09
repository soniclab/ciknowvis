package visual;

import prefuse.Constants;
import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.StrokeLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.NodeItem;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * <p>Renderer that draws edges as lines connecting nodes. Both
 * straight and curved lines are supported. Curved lines are drawn using
 * cubic Bezier curves. Subclasses can override the
 * {@link #getCurveControlPoints(java.awt.geom.Point2D[],double,double,double,double)}
 * method to provide custom control point assignment for such curves.</p>
 * <p/>
 * <p>This class also supports arrows for directed edges. See the
 * {@link #setArrowType(int)} method for more.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @version 1.0
 *          modified by Jinling Li for supporting 1)multiple edges between two nodes  2) undirected edges
 */
public class MyEdgeRenderer extends AbstractShapeRenderer {

    public static final String EDGE_TYPE = "edgeType";

    protected static final double HALF_PI = Math.PI / 2;

    protected Line2D m_line = new Line2D.Float();
    protected CubicCurve2D m_cubic = new CubicCurve2D.Float();

    protected int m_edgeType = Constants.EDGE_TYPE_LINE;
    protected double m_xAlign1 = Constants.CENTER;
    protected double m_yAlign1 = Constants.CENTER;
    protected double m_xAlign2 = Constants.CENTER;
    protected double m_yAlign2 = Constants.CENTER;

    protected double m_width = 1;
    protected float m_curWidth = 1;
    protected Point2D m_tmpPoints[] = new Point2D[2];
    protected Point2D m_ctrlPoints[] = new Point2D[2];
    protected Point2D m_isctPoints[] = new Point2D[2];


    protected Point2D m_tmpPoints1[] = new Point2D[2];
    protected Point2D m_isctPoints1[] = new Point2D[2];


    // arrow head handling
    protected int m_edgeArrow = Constants.EDGE_ARROW_FORWARD;

    protected int m_arrowWidth = 8;
    protected int m_arrowHeight = 12;
    protected Polygon m_arrowHead = updateArrowHead(
            m_arrowWidth, m_arrowHeight);
    protected AffineTransform m_arrowTrans = new AffineTransform();

    protected Shape m_curArrow;
    protected Shape m_curArrow1;
    double offset2;
    double n1x1;
    double n1y1;
    double n2x1;
    double n2y1;


    /**
     * Create a new EdgeRenderer.
     */
    public MyEdgeRenderer() {
        m_tmpPoints[0] = new Point2D.Float();
        m_tmpPoints[1] = new Point2D.Float();

        m_tmpPoints1[0] = new Point2D.Float();
        m_tmpPoints1[1] = new Point2D.Float();


        m_ctrlPoints[0] = new Point2D.Float();
        m_ctrlPoints[1] = new Point2D.Float();
        m_isctPoints[0] = new Point2D.Float();
        m_isctPoints[1] = new Point2D.Float();

        m_isctPoints1[0] = new Point2D.Float();
        m_isctPoints1[1] = new Point2D.Float();

    }


    /**
     * Create a new EdgeRenderer with the given edge type.
     *
     * @param edgeType the edge type, one of
     *                 {@link prefuse.Constants#EDGE_TYPE_LINE} or
     *                 {@link prefuse.Constants#EDGE_TYPE_CURVE}.
     */
    public MyEdgeRenderer(int edgeType) {
        this(edgeType, Constants.EDGE_ARROW_FORWARD);
    }

    /**
     * Create a new EdgeRenderer with the given edge and arrow types.
     *
     * @param edgeType  the edge type, one of
     *                  {@link prefuse.Constants#EDGE_TYPE_LINE} or
     *                  {@link prefuse.Constants#EDGE_TYPE_CURVE}.
     * @param arrowType the arrow type, one of
     *                  {@link prefuse.Constants#EDGE_ARROW_FORWARD},
     *                  {@link prefuse.Constants#EDGE_ARROW_REVERSE}, or
     *                  {@link prefuse.Constants#EDGE_ARROW_NONE}.
     * @see #setArrowType(int)
     */
    public MyEdgeRenderer(int edgeType, int arrowType) {
        this();
        setEdgeType(edgeType);
        setArrowType(arrowType);
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRenderType(prefuse.visual.VisualItem)
     */
    public int getRenderType(VisualItem item) {
        return RENDER_TYPE_DRAW;
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {

        // create the edge shape
        Shape shape = null;
        EdgeItem edge = (EdgeItem) item;
         VisualItem item1 = null;
        VisualItem item2  = null;
          int numberOfEdges  = 0;
         int seqOfEdge = 0;
        try{


        item1 = edge.getSourceItem();
        item2 = edge.getTargetItem();
        
        Set<EdgeItem> multiEdges = getMultiEgdes(edge);
        Map<EdgeItem, String> seqmap = multiMap(multiEdges);



       numberOfEdges = multiEdges.size();


        if (seqmap != null) {

            try {
                seqOfEdge = Integer.parseInt(seqmap.get(edge));

            } catch (Exception e) {

            }
        }

        }catch(Exception e){

        }


        int type = m_edgeType;

        try{
        getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
                m_xAlign1, m_yAlign1);
        getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
                m_xAlign2, m_yAlign2);

        getAlignedPoint(m_tmpPoints1[0], item1.getBounds(),
                m_xAlign1, m_yAlign1);
        getAlignedPoint(m_tmpPoints1[1], item2.getBounds(),
                m_xAlign2, m_yAlign2);

        m_curWidth = (float) (m_width * getLineWidth(item));

        if (numberOfEdges == 1) {
            // create the arrow head, if needed
            EdgeItem e = (EdgeItem) item;
            // one direction
            if (e.getString("direction").equalsIgnoreCase("1") && m_edgeArrow != Constants.EDGE_ARROW_NONE) {
                 m_curArrow1  = null;
                // get starting and ending edge endpoints
                boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
                Point2D start, end;
                start = m_tmpPoints[forward ? 0 : 1];
                end = m_tmpPoints[forward ? 1 : 0];

                // compute the intersection with the target bounding box
                VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
                int i = GraphicsLib.intersectLineRectangle(start, end,
                        dest.getBounds(), m_isctPoints);
                if (i > 0) end = m_isctPoints[0];

                // create the arrow head shape
                AffineTransform at = getArrowTrans(start, end, m_curWidth);
                m_curArrow = at.createTransformedShape(m_arrowHead);

                // update the endpoints for the edge shape
                // need to bias this by arrow head size
                Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
                lineEnd.setLocation(0, -m_arrowHeight);
                at.transform(lineEnd, lineEnd);

                //mutual edge
            } else if (e.getString("direction").equalsIgnoreCase("2") && m_edgeArrow != Constants.EDGE_ARROW_NONE) {

                // get starting and ending edge endpoints
                boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
                Point2D start, end;
                start = m_tmpPoints[forward ? 0 : 1];
                end = m_tmpPoints[forward ? 1 : 0];

                // compute the intersection with the target bounding box
                VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
                int i = GraphicsLib.intersectLineRectangle(start, end,
                        dest.getBounds(), m_isctPoints);
                if (i > 0) end = m_isctPoints[0];

                // create the arrow head shape
                AffineTransform at = getArrowTrans(start, end, m_curWidth);
                m_curArrow = at.createTransformedShape(m_arrowHead);

                // update the endpoints for the edge shape
                // need to bias this by arrow head size
                Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
                lineEnd.setLocation(0, -m_arrowHeight);
                at.transform(lineEnd, lineEnd);
                ////// locate another arrow for mutual link
                // get starting and ending edge endpoints
                boolean forward1 = (m_edgeArrow == Constants.EDGE_ARROW_REVERSE);
                Point2D start1, end1;
                start1 = m_tmpPoints1[forward1 ? 0 : 1];
                end1 = m_tmpPoints1[forward1 ? 1 : 0];

                // compute the intersection with the target bounding box
                VisualItem dest1 = forward1 ? e.getTargetItem() : e.getSourceItem();
                int i1 = GraphicsLib.intersectLineRectangle(start1, end1,
                        dest1.getBounds(), m_isctPoints1);
                if (i1 > 0) end1 = m_isctPoints1[0];

                // create the arrow head shape
                AffineTransform at1 = getArrowTrans(start1, end1, m_curWidth);
                m_curArrow1 = at1.createTransformedShape(m_arrowHead);

                // update the endpoints for the edge shape
                // need to bias this by arrow head size
                Point2D lineEnd1 = m_tmpPoints1[forward1 ? 1 : 0];
                lineEnd1.setLocation(0, -m_arrowHeight);
                at1.transform(lineEnd1, lineEnd1);


            } else {
                m_curArrow = null;
                m_curArrow1 = null;
            }

            // create the edge shape

            double n1x = m_tmpPoints[0].getX();
            double n1y = m_tmpPoints[0].getY();
            double n2x = m_tmpPoints[1].getX();
            double n2y = m_tmpPoints[1].getY();


            switch (type) {
                case Constants.EDGE_TYPE_LINE:
                    m_line.setLine(n1x, n1y, n2x, n2y);
                    shape = m_line;
                    break;
                case Constants.EDGE_TYPE_CURVE:
                    getCurveControlPoints(m_ctrlPoints, n1x, n1y, n2x, n2y);
                    m_cubic.setCurve(n1x, n1y,
                            m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(),
                            m_ctrlPoints[1].getX(), m_ctrlPoints[1].getY(),
                            n2x, n2y);
                    shape = m_cubic;
                    break;
                default:
                    throw new IllegalStateException("Unknown edge type");
            }

        } else {

            //	    create the edge shape

            double n1x = m_tmpPoints[0].getX();
            double n1y = m_tmpPoints[0].getY();
            double n2x = m_tmpPoints[1].getX();
            double n2y = m_tmpPoints[1].getY();

            double c, radAngle, degAngle;

//	    a = length of x-axis, b = length of y-axis
            double a, b;

            a = ((n1x - n2x));
            b = ((n1y - n2y));
            c = Math.sqrt((a * a) + (b * b));

            radAngle = Math.acos(((Math.abs(a)) / c));
            degAngle = radAngle * (180 / Math.PI);

            boolean xDirection;
            int flip;

//	    a <= 0 -> target is left to source, or vertical if a == 0
//	    b < 0  -> source is above target, or horizontal if b == 0

            flip = (a > b) ? -1 : 1;

            xDirection = ((degAngle > 45 & degAngle < 180) || (degAngle > 225 & degAngle < 360));
            //   xDirection = (degAngle > 45) ? true : false;
            double offset = 0.0;

            if (numberOfEdges > 1) {

                double constant = 0.5;

                if (numberOfEdges == 3 || numberOfEdges == 2)
                    constant = 2;
                else if (numberOfEdges == 4)
                    constant = 1.5;

                else if (numberOfEdges == 5)
                                    constant = 1.3;
                 else if (numberOfEdges == 6)
                              constant = 1.2;
                 else if (numberOfEdges == 7)
                              constant = 1.1;

                if (numberOfEdges > 9) {
                    constant = 4.5 / (numberOfEdges - 1);
                }
                boolean numberEven = false;

                String s = numberOfEdges * 0.5 + "";

                if (!s.contains(".5")) {

                    numberEven = true;
                }
                boolean seqEven = false;

                String s1 = seqOfEdge * 0.5 + "";

                if (!s1.contains(".5")) {

                    seqEven = true;
                }
                if (numberEven) {

                    if (seqOfEdge == 1) {
                        if (flip > 0) {
                            offset = flip * m_width * constant;

                        } else {
                            offset = -flip * m_width * constant;
                        }

                    } else if (seqOfEdge == 2) {
                        if (flip > 0) {
                            offset = -flip * m_width * constant;

                        } else {
                            offset = flip * m_width * constant;
                        }

                    } else if (seqOfEdge > 2) {

                        if (seqEven) {
                            if (flip > 0) {
                                offset = -flip * m_width * constant * (seqOfEdge) + constant;

                            } else {
                                offset = flip * m_width * constant * (seqOfEdge) + constant;
                            }

                        } else {

                            if (flip > 0) {
                                offset = flip * m_width * constant * (seqOfEdge);

                            } else {
                                offset = -flip * m_width * constant * (seqOfEdge);
                            }
                        }
                    }
                } else {  // number of edges is odd

                    if (seqEven) {
                        if (flip > 0) {
                            offset = -flip * m_width * constant * seqOfEdge;


                        } else {
                            offset = flip * m_width * constant * seqOfEdge;

                        }

                    } else if (seqOfEdge > 1) {
                        if (flip > 0) {
                            offset = flip * m_width * constant * seqOfEdge - constant;

                        } else {
                            offset = -flip * m_width * constant * seqOfEdge - constant;
                        }

                    }
                }
            }

            if (xDirection) {
                n1x += offset;
                n2x += offset;


            } else {
                n1y += offset;
                n2y += offset;
            }


            m_tmpPoints[0].setLocation(n1x, n1y);
            m_tmpPoints[1].setLocation(n2x, n2y);


            if (edge.getString("direction").equalsIgnoreCase("1") && m_edgeArrow != Constants.EDGE_ARROW_NONE) {

                 m_curArrow1  = null;
                // get starting and ending edge endpoints
                boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
                Point2D start, end;
                start = m_tmpPoints[forward ? 0 : 1];
                end = m_tmpPoints[forward ? 1 : 0];

                // compute the intersection with the target bounding box
                VisualItem dest = forward ? edge.getTargetItem() : edge.getSourceItem();
                int i = GraphicsLib.intersectLineRectangle(start, end,
                        dest.getBounds(), m_isctPoints);
                if (i > 0) end = m_isctPoints[0];

                // create the arrow head shape
                AffineTransform at = getArrowTrans(start, end, m_curWidth);
                m_curArrow = at.createTransformedShape(m_arrowHead);

                // update the endpoints for the edge shape
                // need to bias this by arrow head size
                Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
                lineEnd.setLocation(0, -m_arrowHeight);
                at.transform(lineEnd, lineEnd);

            } else if (edge.getString("direction").equalsIgnoreCase("2") && m_edgeArrow != Constants.EDGE_ARROW_NONE) {

                  m_tmpPoints1[0].setLocation(n1x, n1y);
                 m_tmpPoints1[1].setLocation(n2x, n2y);


                // get starting and ending edge endpoints
                boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
                Point2D start, end;
                start = m_tmpPoints[forward ? 0 : 1];
                end = m_tmpPoints[forward ? 1 : 0];

                // compute the intersection with the target bounding box
                VisualItem dest = forward ? edge.getTargetItem() : edge.getSourceItem();
                int i = GraphicsLib.intersectLineRectangle(start, end,
                        dest.getBounds(), m_isctPoints);
                if (i > 0) end = m_isctPoints[0];

                // create the arrow head shape
                AffineTransform at = getArrowTrans(start, end, m_curWidth);
                m_curArrow = at.createTransformedShape(m_arrowHead);

                // update the endpoints for the edge shape
                // need to bias this by arrow head size
                Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
                lineEnd.setLocation(0, -m_arrowHeight);
                at.transform(lineEnd, lineEnd);


                ///////locate another arrow
                // get starting and ending edge endpoints
                boolean forward1 = (m_edgeArrow == Constants.EDGE_ARROW_REVERSE);
                Point2D start1, end1;
                start1 = m_tmpPoints1[forward1 ? 0 : 1];
                end1 = m_tmpPoints1[forward1 ? 1 : 0];

                // compute the intersection with the target bounding box
                VisualItem dest1 = forward1 ? edge.getTargetItem() : edge.getSourceItem();
                int i1 = GraphicsLib.intersectLineRectangle(start1, end1,
                        dest1.getBounds(), m_isctPoints1);
                if (i1 > 0) end1 = m_isctPoints1[0];

                // create the arrow head shape
                AffineTransform at1 = getArrowTrans(start1, end1, m_curWidth);
                m_curArrow1 = at1.createTransformedShape(m_arrowHead);
                // update the endpoints for the edge shape
                // need to bias this by arrow head size
                Point2D lineEnd1 = m_tmpPoints1[forward1 ? 1 : 0];
                lineEnd1.setLocation(0, -m_arrowHeight);
                at1.transform(lineEnd1, lineEnd1);


            } else {
                m_curArrow = null;
                 m_curArrow1  = null;
            }


            m_line.setLine(n1x, n1y, n2x, n2y);
            shape = m_line;
        }
        }catch(Exception e){

        }
//	    return the edge shape
        return shape;


    }

    /**
     * @see prefuse.render.Renderer#render(java.awt.Graphics2D, prefuse.visual.VisualItem)
     */
    public void render
            (Graphics2D
                    g, VisualItem
                    item) {
        // render the edge line
        super.render(g, item);
        // render the edge arrow head, if appropriate
        if (m_curArrow != null) {
            g.setPaint(ColorLib.getColor(item.getFillColor()));
            g.fill(m_curArrow);
        }


        if (m_curArrow1 != null) {
            g.setPaint(ColorLib.getColor(item.getFillColor()));
            g.fill(m_curArrow1);
        }

    }

    /**
     * Returns an affine transformation that maps the arrowhead shape
     * to the position and orientation specified by the provided
     * line segment end points.
     */
    protected AffineTransform getArrowTrans
            (Point2D
                    p1, Point2D
                    p2,
                        double width) {
        m_arrowTrans.setToTranslation(p2.getX(), p2.getY());
        m_arrowTrans.rotate(-HALF_PI +
                Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX()));
        if (width > 1) {
            double scalar = (width - 1) / 4 + 1;

            m_arrowTrans.scale(scalar, scalar);
        }
        return m_arrowTrans;
    }

    /**
     * Update the dimensions of the arrow head, creating a new
     * arrow head if necessary. The return value is also set
     * as the member variable <code>m_arrowHead</code>
     *
     * @param w the width of the untransformed arrow head base, in pixels
     * @param h the height of the untransformed arrow head, in pixels
     * @return the untransformed arrow head shape
     */
    protected Polygon updateArrowHead
            (
                    int w,
                    int h) {
        if (m_arrowHead == null) {
            m_arrowHead = new Polygon();
        } else {
            m_arrowHead.reset();
        }
        m_arrowHead.addPoint(0, 0);
        m_arrowHead.addPoint(-w / 2, -h);
        m_arrowHead.addPoint(w / 2, -h);
        m_arrowHead.addPoint(0, 0);

        return m_arrowHead;
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getTransform(prefuse.visual.VisualItem)
     */
    protected AffineTransform getTransform
            (VisualItem
                    item) {
        return null;
    }

    /**
     * @see prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D, prefuse.visual.VisualItem)
     */
    public boolean locatePoint
            (Point2D
                    p, VisualItem
                    item) {

        Shape s = getShape(item);
        if (s == null) {
            return false;
        } else {
            double width = Math.max(2, getLineWidth(item));
            double halfWidth = width / 10.0;
            return s.intersects(p.getX() - halfWidth,
                    p.getY() - halfWidth,
                    width, width);
        }
    }

    /**
     * @see prefuse.render.Renderer#setBounds(prefuse.visual.VisualItem)
     */
    public void setBounds
            (VisualItem
                    item) {
        if (!m_manageBounds) return;
        Shape shape = getShape(item);
        if (shape == null) {
            item.setBounds(item.getX(), item.getY(), 0, 0);
            return;
        }
        GraphicsLib.setBounds(item, shape, getStroke(item));
        if (m_curArrow != null) {
            Rectangle2D bbox = (Rectangle2D) item.get(VisualItem.BOUNDS);
            Rectangle2D.union(bbox, m_curArrow.getBounds2D(), bbox);
        }

        if (m_curArrow1 != null) {
            Rectangle2D bbox = (Rectangle2D) item.get(VisualItem.BOUNDS);
            Rectangle2D.union(bbox, m_curArrow1.getBounds2D(), bbox);
        }

    }

    /**
     * Returns the line width to be used for this VisualItem. By default,
     * returns the base width value set using the {@link #setDefaultLineWidth(double)}
     * method, scaled by the item size returned by
     * {@link VisualItem#getSize()}. Subclasses can override this method to
     * perform custom line width determination, however, the preferred
     * method is to change the item size value itself.
     *
     * @param item the VisualItem for which to determine the line width
     * @return the desired line width, in pixels
     */
    protected double getLineWidth
            (VisualItem
                    item) {
        return item.getSize();
    }

    /**
     * Returns the stroke value returned by {@link VisualItem#getStroke()},
     * scaled by the current line width
     * determined by the {@link #getLineWidth(VisualItem)} method. Subclasses
     * may override this method to perform custom stroke assignment, but should
     * respect the line width paremeter stored in the {@link #m_curWidth}
     * member variable, which caches the result of <code>getLineWidth</code>.
     *
     * @see prefuse.render.AbstractShapeRenderer#getStroke(prefuse.visual.VisualItem)
     */
    protected BasicStroke getStroke
            (VisualItem
                    item) {
        return StrokeLib.getDerivedStroke(item.getStroke(), m_curWidth);
    }

    /**
     * Determines the control points to use for cubic (Bezier) curve edges.
     * Override this method to provide custom curve specifications.
     * To reduce object initialization, the entries of the Point2D array are
     * already initialized, so use the <tt>Point2D.setLocation()</tt> method rather than
     * <tt>new Point2D.Double()</tt> to more efficiently set custom control points.
     *
     * @param cp array of Point2D's (length >= 2) in which to return the control points
     * @param x1 the x co-ordinate of the first node this edge connects to
     * @param y1 the y co-ordinate of the first node this edge connects to
     * @param x2 the x co-ordinate of the second node this edge connects to
     * @param y2 the y co-ordinate of the second node this edge connects to
     */
    protected void getCurveControlPoints
            (Point2D[] cp,
             double x1,
             double y1,
             double x2,
             double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        cp[0].setLocation(x1 + 2 * dx / 3, y1);
        cp[1].setLocation(x2 - dx / 8, y2 - dy / 8);
    }

    /**
     * Helper method, which calculates the top-left co-ordinate of a rectangle
     * given the rectangle's alignment.
     */
    protected static void getAlignedPoint
            (Point2D
                    p, Rectangle2D
                    r, double xAlign,
                       double yAlign) {
        double x = r.getX(), y = r.getY(), w = r.getWidth(), h = r.getHeight();
        if (xAlign == Constants.CENTER) {
            x = x + (w / 2);
        } else if (xAlign == Constants.RIGHT) {
            x = x + w;
        }
        if (yAlign == Constants.CENTER) {
            y = y + (h / 2);
        } else if (yAlign == Constants.BOTTOM) {
            y = y + h;
        }
        p.setLocation(x, y);
    }

    /**
     * Returns the type of the drawn edge. This is one of
     * {@link prefuse.Constants#EDGE_TYPE_LINE} or
     * {@link prefuse.Constants#EDGE_TYPE_CURVE}.
     *
     * @return the edge type
     */
    public int getEdgeType
            () {
        return m_edgeType;
    }

    /**
     * Sets the type of the drawn edge. This must be one of
     * {@link prefuse.Constants#EDGE_TYPE_LINE} or
     * {@link prefuse.Constants#EDGE_TYPE_CURVE}.
     *
     * @param type the new edge type
     */
    public void setEdgeType
            (
                    int type) {
        if (type < 0 || type >= Constants.EDGE_TYPE_COUNT)
            throw new IllegalArgumentException(
                    "Unrecognized edge curve type: " + type);
        m_edgeType = type;
    }

    /**
     * Returns the type of the drawn edge. This is one of
     * {@link prefuse.Constants#EDGE_ARROW_FORWARD},
     * {@link prefuse.Constants#EDGE_ARROW_REVERSE}, or
     * {@link prefuse.Constants#EDGE_ARROW_NONE}.
     *
     * @return the edge type
     */
    public int getArrowType
            () {
        return m_edgeArrow;
    }

    /**
     * Sets the type of the drawn edge. This is either
     * {@link prefuse.Constants#EDGE_ARROW_NONE} for no edge arrows,
     * {@link prefuse.Constants#EDGE_ARROW_FORWARD} for arrows from source to
     * target on directed edges, or
     * {@link prefuse.Constants#EDGE_ARROW_REVERSE} for arrows from target to
     * source on directed edges.
     *
     * @param type the new arrow type
     */
    public void setArrowType
            (
                    int type) {
        if (type < 0 || type >= Constants.EDGE_ARROW_COUNT)
            throw new IllegalArgumentException(
                    "Unrecognized edge arrow type: " + type);
        m_edgeArrow = type;
    }

    /**
     * Sets the dimensions of an arrow head for a directed edge. This specifies
     * the pixel dimensions when both the zoom level and the size factor
     * (a combination of item size value and default stroke width) are 1.0.
     *
     * @param width  the untransformed arrow head width, in pixels. This
     *               specifies the span of the base of the arrow head.
     * @param height the untransformed arrow head height, in pixels. This
     *               specifies the distance from the point of the arrow to its base.
     */
    public void setArrowHeadSize
            (
                    int width,
                    int height) {
        m_arrowWidth = width;
        m_arrowHeight = height;
        m_arrowHead = updateArrowHead(width, height);
    }

    /**
     * Get the height of the untransformed arrow head. This is the distance,
     * in pixels, from the tip of the arrow to its base.
     *
     * @return the default arrow head height
     */
    public int getArrowHeadHeight
            () {
        return m_arrowHeight;
    }

    /**
     * Get the width of the untransformed arrow head. This is the length,
     * in pixels, of the base of the arrow head.
     *
     * @return the default arrow head width
     */
    public int getArrowHeadWidth
            () {
        return m_arrowWidth;
    }

    /**
     * Get the horizontal aligment of the edge mount point with the first node.
     *
     * @return the horizontal alignment, one of {@link prefuse.Constants#LEFT},
     *         {@link prefuse.Constants#RIGHT}, or {@link prefuse.Constants#CENTER}.
     */
    public double getHorizontalAlignment1
            () {
        return m_xAlign1;
    }

    /**
     * Get the vertical aligment of the edge mount point with the first node.
     *
     * @return the vertical alignment, one of {@link prefuse.Constants#TOP},
     *         {@link prefuse.Constants#BOTTOM}, or {@link prefuse.Constants#CENTER}.
     */
    public double getVerticalAlignment1
            () {
        return m_yAlign1;
    }

    /**
     * Get the horizontal aligment of the edge mount point with the second
     * node.
     *
     * @return the horizontal alignment, one of {@link prefuse.Constants#LEFT},
     *         {@link prefuse.Constants#RIGHT}, or {@link prefuse.Constants#CENTER}.
     */
    public double getHorizontalAlignment2
            () {
        return m_xAlign2;
    }

    /**
     * Get the vertical aligment of the edge mount point with the second node.
     *
     * @return the vertical alignment, one of {@link prefuse.Constants#TOP},
     *         {@link prefuse.Constants#BOTTOM}, or {@link prefuse.Constants#CENTER}.
     */
    public double getVerticalAlignment2
            () {
        return m_yAlign2;
    }

    /**
     * Set the horizontal aligment of the edge mount point with the first node.
     *
     * @param align the horizontal alignment, one of
     *              {@link prefuse.Constants#LEFT}, {@link prefuse.Constants#RIGHT}, or
     *              {@link prefuse.Constants#CENTER}.
     */
    public void setHorizontalAlignment1
            (
                    int align) {
        m_xAlign1 = align;
    }

    /**
     * Set the vertical aligment of the edge mount point with the first node.
     *
     * @param align the vertical alignment, one of
     *              {@link prefuse.Constants#TOP}, {@link prefuse.Constants#BOTTOM}, or
     *              {@link prefuse.Constants#CENTER}.
     */
    public void setVerticalAlignment1
            (
                    int align) {
        m_yAlign1 = align;
    }

    /**
     * Set the horizontal aligment of the edge mount point with the second
     * node.
     *
     * @param align the horizontal alignment, one of
     *              {@link prefuse.Constants#LEFT}, {@link prefuse.Constants#RIGHT}, or
     *              {@link prefuse.Constants#CENTER}.
     */
    public void setHorizontalAlignment2
            (
                    int align) {
        m_xAlign2 = align;
    }

    /**
     * Set the vertical aligment of the edge mount point with the second node.
     *
     * @param align the vertical alignment, one of
     *              {@link prefuse.Constants#TOP}, {@link prefuse.Constants#BOTTOM}, or
     *              {@link prefuse.Constants#CENTER}.
     */
    public void setVerticalAlignment2
            (
                    int align) {
        m_yAlign2 = align;
    }

    /**
     * Sets the default width of lines. This width value will
     * be scaled by the value of an item's size data field. The default
     * base width is 1.
     *
     * @param w the desired default line width, in pixels
     */
    public void setDefaultLineWidth
            (
                    double w) {
        m_width = w;
    }

    /**
     * Gets the default width of lines. This width value that will
     * be scaled by the value of an item's size data field. The default
     * base width is 1.
     *
     * @return the default line width, in pixels
     */
    public double getDefaultLineWidth
            () {
        return m_width;
    }


    private Map<EdgeItem, String> multiMap(Set<EdgeItem> edges) {

        Map<EdgeItem, String> m = new HashMap();

        int j = 0;

        int[] edgeKeys = new int[edges.size()];
        for (EdgeItem e : edges) {

            edgeKeys[j] = (Integer.parseInt(e.getString("edgeKey")));
            j++;
        }

        Arrays.sort(edgeKeys);

        for (int i = 1; i < edgeKeys.length + 1; i++) {

            for (EdgeItem e1 : edges) {
                if (edgeKeys[i - 1] == Integer.parseInt(e1.getString("edgeKey")))
                    m.put(e1, String.valueOf(i));
            }
        }

        return m;
    }

    private Set<EdgeItem> getMultiEgdes(EdgeItem edge) {
         Set<EdgeItem> edges1 = new HashSet();
        Set<EdgeItem> edges2 = new HashSet();

        try{

        NodeItem node1 = edge.getSourceItem();

        NodeItem node2 = edge.getTargetItem();
        Iterator it1 = node1.edges();
        Iterator it2 = node2.edges();


        while (it1.hasNext()) {
            EdgeItem e1 = (EdgeItem) it1.next();
            if (e1.isVisible() == true)
                edges1.add(e1);
        }
        while (it2.hasNext()) {
            EdgeItem e2 = (EdgeItem) it2.next();
            if (e2.isVisible() == true)
                edges2.add(e2);
        }

        edges1.retainAll(edges2);
        }catch(Exception e) {

        }
        return edges1;
    }
} // end of class EdgeRenderer
