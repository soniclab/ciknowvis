package visual;

import java.awt.*;
import java.awt.event.MouseEvent;

import prefuse.Display;
import prefuse.controls.AbstractZoomControl;
import prefuse.controls.Control;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;


/**
 * Zooms the display, changing the scale of the viewable region. By default,
 * zooming is achieved by pressing the right mouse button on the background
 * of the visualization and dragging the mouse up or down. Moving the mouse up
 * zooms out the display around the spot the mouse was originally pressed.
 * Moving the mouse down similarly zooms in the display, making items
 * larger.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ZoomAllControl extends AbstractZoomControl {



    private GraphView gv;
     private Point m_point = new Point();
    /**
     * Create a new zoom control.
     */
    public ZoomAllControl(GraphView gv) {
        this.gv = gv;
        // do nothing
    }

   

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {

        if(UILib.isButtonPressed(e, Control.LEFT_MOUSE_BUTTON)){
             Cursor cursor = gv.getDisplay().getCursor();
            Display display = (Display)e.getComponent();
            m_point.x = e.getX();
            m_point.y = e.getY();



            //////////////
         //   xToCenterDis = cx - ((mouseX - xMin) / (xMax - xMin)) *width;
         //   yToCenterDis = cy - ((mouseY - yMin) / (yMax - yMin)) *height;


            ////////////////////////////

            if (cursor.getName().equals("zoomin1")) {

            zoom(display, m_point, 1.1
                , false);
            gv.ZoomGraph(true);

            } else if (cursor.getName().equals("zoomout1")) {

        zoom(display, m_point,
                1/1.1, false);

          gv.ZoomGraph(true);
        }
        }

       
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemPressed(VisualItem item, MouseEvent e) {
        if ( m_zoomOverItem )
            mousePressed(e);
    }

    /**
     * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemDragged(VisualItem item, MouseEvent e) {
        if ( m_zoomOverItem )
            mouseDragged(e);
    }

    /**
     * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemReleased(VisualItem item, MouseEvent e) {
        if ( m_zoomOverItem )
            mouseReleased(e);
    }

} // end of class ZoomControl
