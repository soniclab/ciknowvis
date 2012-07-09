package visual;

import prefuse.controls.AbstractZoomControl;
import prefuse.controls.Control;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

import java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.awt.Cursor;


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
/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 17, 2009
 * Time: 2:19:34 PM
 * To change this template use File | Settings | File Templates.
 */


public class ZoomControlCustom extends AbstractZoomControl {

    private int yLast;
    private Point2D down = new Point2D.Float();
    private int button = RIGHT_MOUSE_BUTTON;
    private GraphView gv;
    /**
     * Create a new zoom control.
     */
    public ZoomControlCustom(GraphView gv) {
        this.gv = gv;
        // do nothing
    }

    /**
     * Create a new zoom control.
     * @param mouseButton the mouse button that should initiate a zoom. One of
     * {@link prefuse.controls.Control#LEFT_MOUSE_BUTTON}, {@link prefuse.controls.Control#MIDDLE_MOUSE_BUTTON},
     * or {@link prefuse.controls.Control#RIGHT_MOUSE_BUTTON}.
     */
    public ZoomControlCustom(int mouseButton) {
        button = mouseButton;
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {

        Cursor cursor = gv.getDisplay().getCursor();
        if(UILib.isButtonPressed(e, Control.LEFT_MOUSE_BUTTON)){
		if (cursor.getName().equals("zoomin")) {

        gv.rescaleNodes(1.2, true, e.getX(), e.getY(), false);


        } else if (cursor.getName().equals("zoomout")) {

        gv.rescaleNodes(1/1.2, true, e.getX(), e.getY(), false);


        }}

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
      //     Cursor cursor = gv.getDisplay().getCursor();
     //    if (UILib.isButtonPressed(e, Control.RIGHT_MOUSE_BUTTON) && (cursor.getName().equals("zoomin") ||
		//		cursor.getName().equals("zoomout")))
          //   gv.getDisplay().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
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
