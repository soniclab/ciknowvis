package visual;

import prefuse.action.layout.Layout;
import prefuse.visual.NodeItem;
import prefuse.data.tuple.TupleSet;
import prefuse.Display;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 3, 2008
 * Time: 11:49:54 PM
 * To change this template use File | Settings | File Templates.
 */


public class RescaleLayout extends Layout {
    private double comX = 0, comY = 0;
    protected Point2D m_origin;
    // private boolean changed = false;
    private int layoutNumber;
    private double zoomx, zoomy;
    private boolean forZoom;
    private double mouseX, mouseY;
    private Display dis;
    private boolean forLabelNode, fromZoomSelect, isCustomLayout, forNolabel;
    private Rectangle rect = null;
    private    int recstatus;



    public RescaleLayout(String group, Display dis, boolean forLabelNode) {
        super(group);
        this.forLabelNode = forLabelNode;
        this.dis = dis;
    }


    public RescaleLayout(String group, double zoomx, double zoomy, boolean forZoom, double mouseX, double mouseY, Display dis, boolean fromZoomSelect, int recstatus, boolean isCustomLayout) {
        super(group);

        this.zoomx = zoomx;
         this.zoomy = zoomy;
        this.forZoom = forZoom;
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        this.dis = dis;
        this.fromZoomSelect = fromZoomSelect;
        this.recstatus = recstatus;
        this.isCustomLayout = isCustomLayout;
    }

    public RescaleLayout(String group, boolean forZoom, Rectangle rect, Display dis) {
        super(group);
       

        this.forZoom = forZoom;

        this.dis = dis;
        this.rect = rect;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    @SuppressWarnings({"PointlessBooleanExpression"})
    public void run(double frac) {

        TupleSet ts = m_vis.getGroup(m_group);

        double width = 0.00;
        double height = 0.00;
        double xToCenterDis = 0.00;
        double yToCenterDis = 0.00;

        Rectangle2D bounds = super.getLayoutBounds();

        Iterator iter = ts.tuples();

        //System.out.println("rescaling...");
        double xMax = 0.00;
        double yMax = 0.00;
        double xMin = 0.00;
        double yMin = 0.00;

        // get xMax, yMax, xMin, yMin
        while (iter.hasNext()) {
            NodeItem n = (NodeItem) iter.next();
            xMax = n.getX();
            yMax = n.getY();
            xMin = n.getX();
            yMin = n.getY();

            break;
        }


        Iterator iter1 = ts.tuples();
        while (iter1.hasNext()) {
            NodeItem n = (NodeItem) iter1.next();

            double nwidth = n.getBounds().getWidth() / 2;
            double nheight = n.getBounds().getHeight() / 2;

            xMax = Math.max(xMax, n.getX() + nwidth);
            yMax = Math.max(yMax, n.getY() + nheight);
            xMin = Math.min(xMin, n.getX() - nwidth);
            yMin = Math.min(yMin, n.getY() - nheight);
        }
        if (rect == null) {
            if (!forZoom) {

                  width = bounds.getWidth();
                height = bounds.getHeight();

               if(recstatus ==2 ){
               
                   width =  (height < width ? width : height);
                   height = (height < width ?  width : height);

                 
               }

            } else {

              //  System.out.println("zoomx: " + zoomx);
              //  System.out.println("zoomy: " + zoomy);
               
                width = (xMax - xMin) * zoomx;
                height = (yMax - yMin) * zoomy;

               ///////////////////////////////////////////
             //   if(forZoom && recstatus ==2){
             //       width =  (height < width ? width : height);
             //       height = (height < width ?  width : height);


             //   }
              //////////////////////////////////////////
                double cx = dis.getBounds().getWidth() / 2;
                double cy = dis.getBounds().getHeight() / 2;

                //   System.out.println("mouseX:" +mouseX);
                //   System.out.println(" mouseY:" +  mouseY);


                xToCenterDis = cx - ((mouseX - xMin) / (xMax - xMin)) * width;
                yToCenterDis = cy - ((mouseY - yMin) / (yMax - yMin)) * height;

            }

            // rescale nodes to the real frame

            Iterator iter2 = ts.tuples();
            while (iter2.hasNext()) {
                NodeItem n = (NodeItem) iter2.next();

                if (!forZoom) {

                    if (forLabelNode) {
                        setX(n, null, ((n.getX() - xMin) / (xMax - xMin)) * (width - 60));

                        setY(n, null, ((n.getY() - yMin) / (yMax - yMin)) * (height - 20));
                    } else if(fromZoomSelect){
                   
                        setX(n, null, ((n.getX() - xMin) / (xMax - xMin)) * (width - 40));

                        setY(n, null, ((n.getY() - yMin) / (yMax - yMin)) * (height - 20));
                    }else if (recstatus == 2){

                             setX(n, null, n.getX());

                        setY(n, null, n.getY());
                           

                    } else{
                         setX(n, null, ((n.getX() - xMin) / (xMax - xMin)) * (width - 10));

                        setY(n, null, ((n.getY() - yMin) / (yMax - yMin)) * (height - 10));
                    }
                } else {

                    setX(n, null, ((n.getX() - xMin) / (xMax - xMin)) * width + xToCenterDis);
                    setY(n, null, ((n.getY() - yMin) / (yMax - yMin)) * height + yToCenterDis);
                   
                }

            }

        } else {   // zoom to selection
                    double cx = rect.getCenterX();
             double cy = rect.getCenterY();

                      width = (xMax - xMin) *  bounds.getWidth()/rect.getWidth();
                      height = (yMax - yMin) *  bounds.getHeight()/rect.getHeight();
                       Iterator iter2 = ts.tuples();
                       while (iter2.hasNext()) {
                           NodeItem n = (NodeItem) iter2.next();
                           setX(n, null, ((n.getX() - xMin) / (xMax - xMin)) * width );
                           setY(n, null, ((n.getY() - yMin) / (yMax - yMin)) * height);

                       }

             double cx1 = ((cx - xMin) / (xMax - xMin)) * width;
             double cy1 = ((cy - yMin) / (yMax - yMin)) * height;

            // pan the selected area to the center of the screen


             Point2D center =  new Point2D.Double(cx1, cy1);

            dis.panToAbs(center);
       

        }

    }
}