package visual;

import prefuse.controls.ControlAdapter;
import prefuse.util.ui.UILib;
import prefuse.Display;
import prefuse.visual.VisualItem;

import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 29, 2009
 * Time: 3:41:38 PM
 * To change this template use File | Settings | File Templates.
 */


import java.awt.Cursor;


/**
 * Pans the display, changing the viewable region of the visualization.
 * By default, panning is accomplished by clicking on the background of a
 * visualization with the left mouse button and then dragging.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class PanControlCustom  extends ControlAdapter {

    private boolean m_panOverItem;
    private int m_xDown, m_yDown;
    private int m_button;
   private   GraphView gv;
    /**
     * Create a new PanControl.
     */
    public PanControlCustom(GraphView gv) {
        this(LEFT_MOUSE_BUTTON, true);
        this.gv = gv;
    }

    /**
     * Create a new PanControl.
     * @param panOverItem if true, the panning control will work even while
     * the mouse is over a visual item.
     */
    public PanControlCustom(boolean panOverItem) {
        this(LEFT_MOUSE_BUTTON, panOverItem);
    }

    /**
     * Create a new PanControl.
     * @param mouseButton the mouse button that should initiate a pan. One of
     * {@link prefuse.controls.Control#LEFT_MOUSE_BUTTON}, {@link prefuse.controls.Control#MIDDLE_MOUSE_BUTTON},
     * or {@link prefuse.controls.Control#RIGHT_MOUSE_BUTTON}.
     */
    public PanControlCustom(int mouseButton) {
        this(mouseButton, false);
    }

    /**
     * Create a new PanControl
     * @param mouseButton the mouse button that should initiate a pan. One of
     * {@link prefuse.controls.Control#LEFT_MOUSE_BUTTON}, {@link prefuse.controls.Control#MIDDLE_MOUSE_BUTTON},
     * or {@link prefuse.controls.Control#RIGHT_MOUSE_BUTTON}.
     * @param panOverItem if true, the panning control will work even while
     * the mouse is over a visual item.
     */
    public PanControlCustom(int mouseButton, boolean panOverItem) {
        m_button = mouseButton;
        m_panOverItem = panOverItem;
    }

    // ------------------------------------------------------------------------

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
             Cursor cursor = gv.getDisplay().getCursor();
        
        if ( UILib.isButtonPressed(e, m_button)  && cursor.getName().equals("pan")) {
          
            m_xDown = e.getX();
            m_yDown = e.getY();
        }
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
          Cursor cursor = gv.getDisplay().getCursor();
        if ( UILib.isButtonPressed(e, m_button) && cursor.getName().equals("pan") ) {
            Display display = (Display)e.getComponent();
            int x = e.getX(),   y = e.getY();
            int dx = x-m_xDown, dy = y-m_yDown;
            display.pan(dx,dy);
            m_xDown = x;
            m_yDown = y;
            display.repaint();
            gv.setPan(true);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
          Cursor cursor = gv.getDisplay().getCursor();
        // if (UILib.isButtonPressed(e, Control.RIGHT_MOUSE_BUTTON) && (cursor.getName().equals("pan")) )
          //   gv.getDisplay().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            m_xDown = -1;
            m_yDown = -1;
        
    }

    /**
     * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemPressed(VisualItem item, MouseEvent e) {
        if ( m_panOverItem )
            mousePressed(e);
    }

    /**
     * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemDragged(VisualItem item, MouseEvent e) {
        if ( m_panOverItem )
            mouseDragged(e);
    }

    /**
     * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemReleased(VisualItem item, MouseEvent e) {
        if ( m_panOverItem )
            mouseReleased(e);
    }

} // end of class PanControl
