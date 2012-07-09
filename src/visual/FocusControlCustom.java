package visual;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jul 30, 2008
 * Time: 11:43:46 AM
 * To change this template use File | Settings | File Templates.
 */

import prefuse.controls.ControlAdapter;
import prefuse.controls.Control;
import prefuse.Visualization;
import prefuse.util.StringLib;
import prefuse.util.ui.UILib;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

import java.util.logging.Logger;
import java.awt.event.MouseEvent;
import java.awt.Cursor;


/**
 * <p>Updates the contents of a TupleSet of focus items in response to mouse
 * actions. For example, clicking a node or double-clicking a node could
 * update its focus status. This Control supports monitoring a specified
 * number of clicks to executing a focus change. By default a click pattern
 * will cause a VisualItem to become the sole member of the focus group.
 * Hold down the control key while clicking to add an item to a group
 * without removing the current members.</p>
 * <p/>
 * <p>Updating a focus group does not necessarily cause
 * the display to change. For this functionality, either register an action
 * with this control, or register a TupleSetListener with the focus group.
 * </p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class FocusControlCustom extends ControlAdapter {

    private String group = Visualization.FOCUS_ITEMS;
    protected String activity;
    protected VisualItem curFocus;
    protected int ccount;
    protected int button = Control.LEFT_MOUSE_BUTTON;
    protected int button1 = Control.RIGHT_MOUSE_BUTTON;
    protected Predicate filter = null;
   private   GraphView gv;

    /**
     * Creates a new FocusControl that changes the focus when an item is
     * clicked the specified number of times. A click value of zero indicates
     * that the focus should be changed in response to mouse-over events.
     *
     * @param clicks the number of clicks needed to switch the focus.
     */
    public FocusControlCustom(int clicks, GraphView gv) {
        ccount = clicks;
        this.gv = gv;
    }

    /**
     * Creates a new FocusControl that changes the focus when an item is
     * clicked the specified number of times. A click value of zero indicates
     * that the focus should be changed in response to mouse-over events.
     *
     * @param focusGroup the name of the focus group to use
     * @param clicks     the number of clicks needed to switch the focus.
     */
    public FocusControlCustom(String focusGroup, int clicks) {
        ccount = clicks;
        group = focusGroup;
    }

    /**
     * Creates a new FocusControl that changes the focus when an item is
     * clicked the specified number of times. A click value of zero indicates
     * that the focus should be changed in response to mouse-over events.
     *
     * @param clicks the number of clicks needed to switch the focus.
     * @param act    an action run to upon focus change
     */
    public FocusControlCustom(int clicks, String act) {
        ccount = clicks;
        activity = act;
    }

    /**
     * Creates a new FocusControl that changes the focus when an item is
     * clicked the specified number of times. A click value of zero indicates
     * that the focus should be changed in response to mouse-over events.
     *
     * @param focusGroup the name of the focus group to use
     * @param clicks     the number of clicks needed to switch the focus.
     * @param act        an action run to upon focus change
     */
    public FocusControlCustom(String focusGroup, int clicks, String act) {
        ccount = clicks;
        activity = act;
        this.group = focusGroup;
    }

    // ------------------------------------------------------------------------

    /**
     * Set a filter for processing items by this focus control. Only items for
     * which the predicate returns true (or doesn't throw an exception) will
     * be considered by this control. A null value indicates that no filtering
     * should be applied. That is, all items will be considered.
     *
     * @param p the filtering predicate to apply
     */
    public void setFilter(Predicate p) {
        this.filter = p;
    }

    /**
     * Get the filter for processing items by this focus control. Only items
     * for which the predicate returns true (or doesn't throw an exception)
     * are considered by this control. A null value indicates that no
     * filtering is applied.
     *
     * @return the filtering predicate
     */
    public Predicate getFilter() {
        return filter;
    }

    /**
     * Perform a filtering check on the input item.
     *
     * @param item the item to check against the filter
     * @return true if the item should be considered, false otherwise
     */
    protected boolean filterCheck(VisualItem item) {
        if (filter == null)
            return true;

        try {
            return filter.getBoolean(item);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).warning(
                    e.getMessage() + "\n" + StringLib.getStackTrace(e));
            return false;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
   /* public void itemEntered(VisualItem item, MouseEvent e) {
        if (!filterCheck(item)) return;
      //  Display d = (Display) e.getSource();
      //  d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (ccount == 0) {
            Visualization vis = item.getVisualization();
            TupleSet ts = vis.getFocusGroup(group);
            ts.setTuple(item);
            curFocus = item;
            runActivity(vis);
        }
    }   */

    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemExited(VisualItem item, MouseEvent e) {
        if (!filterCheck(item)) return;
      //  Display d = (Display) e.getSource();
      //  d.setCursor(Cursor.getDefaultCursor());
        if (ccount == 0) {
            curFocus = null;
            Visualization vis = item.getVisualization();
            TupleSet ts = vis.getFocusGroup(group);
            ts.removeTuple(item);
            runActivity(vis);
        }
    }

    /**
     * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemClicked(VisualItem item, MouseEvent e) {
        Cursor cursor = gv.getDisplay().getCursor();

      //   if (UILib.isButtonPressed(e, Control.LEFT_MOUSE_BUTTON)){
         if (cursor.getType() == (Cursor.DEFAULT_CURSOR)) {
       //   System.out.println("cursor.getName() in focus: " + cursor.getName());
        if (!filterCheck(item)) return;
        if (UILib.isButtonPressed(e, button) &&
                e.getClickCount() == ccount) {
            // if ( item != curFocus ) {
            Visualization vis = item.getVisualization();
            TupleSet ts = vis.getFocusGroup(group);

            boolean ctrl = e.isControlDown();
            if (!ctrl) {
                curFocus = item;
                ts.setTuple(item);
            } else if (ts.containsTuple(item)) {
                ts.removeTuple(item);
            } else {
                ts.addTuple(item);
            }
            runActivity(vis);

            /*  } else if ( e.isControlDown() ) {
                Visualization vis = item.getVisualization();
                TupleSet ts = vis.getFocusGroup(group);
                ts.removeTuple(item);
                curFocus = null;
                runActivity(vis);
            }*/
        }
        // focus can also work for right mouse button
        if (UILib.isButtonPressed(e, button1) &&
                e.getClickCount() == ccount  ) {
            // if ( item != curFocus ) {
            Visualization vis = item.getVisualization();
            TupleSet ts = vis.getFocusGroup(group);
            if(ts.getTupleCount() == 0) {
            boolean ctrl = e.isControlDown();
            if (!ctrl) {
                curFocus = item;
                ts.setTuple(item);
            } else if (ts.containsTuple(item)) {
                ts.removeTuple(item);
            } else {
                ts.addTuple(item);
            }
            runActivity(vis);

            /*  } else if ( e.isControlDown() ) {
                Visualization vis = item.getVisualization();
                TupleSet ts = vis.getFocusGroup(group);
                ts.removeTuple(item);
                curFocus = null;
                runActivity(vis);
            }*/
            }
        }
        // }

         } //else{
          //   if (UILib.isButtonPressed(e, Control.RIGHT_MOUSE_BUTTON))
          //   gv.getDisplay().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         //}
    }
    //  self added method for   focusing when itemPressed
    /*  public void itemPressed(VisualItem item, MouseEvent e) {
if ( !filterCheck(item) ) return;
if ( UILib.isButtonPressed(e, button) &&
     e.getClickCount() == ccount )
{
   // if ( item != curFocus ) {
        Visualization vis = item.getVisualization();
        TupleSet ts = vis.getFocusGroup(group);

        boolean ctrl = e.isControlDown();
        if ( !ctrl ) {
            curFocus = item;
            ts.setTuple(item);
        } else if ( ts.containsTuple(item) ) {
            ts.removeTuple(item);
        } else {
            ts.addTuple(item);
        }
        runActivity(vis);

  /*  } else if ( e.isControlDown() ) {
        Visualization vis = item.getVisualization();
        TupleSet ts = vis.getFocusGroup(group);
        ts.removeTuple(item);
        curFocus = null;
        runActivity(vis);
    }*/
    // }
    //  }   */


    private void runActivity(Visualization vis) {
        if (activity != null) {
            vis.run(activity);
        }
    }

} // end of class FocusControl
