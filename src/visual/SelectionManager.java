package visual;

import prefuse.visual.NodeItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.data.expression.AbstractPredicate;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.controls.ControlAdapter;
import prefuse.util.display.PaintListener;
import prefuse.util.ui.UILib;
import prefuse.Display;
import prefuse.Visualization;

import java.util.HashSet;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import admin.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jun 29, 2008
 * Time: 9:37:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionManager {


    private HashSet<NodeItem> selectedNodes;
    private HashSet<EdgeItem> selectedEdges;
    private SelectionListener sl;
    private SelectionPredicate sp;
    private Visualization m_vis;
    private GraphView gv;
    public SelectionManager(Visualization m_vis) {
        this.selectedEdges = new HashSet<EdgeItem>();
        this.selectedNodes = new HashSet<NodeItem>();
        this.m_vis = m_vis;

    }

    //	methods to add an item
    public void addSelectedItem(VisualItem item) {
        if (item instanceof EdgeItem)
            this.addSelectedEdge((EdgeItem) item);
        else if (item instanceof NodeItem)
            this.addSelectedNode((NodeItem) item);
    }

    public void addSelectedItem(VisualItem item, boolean removeIfInSelection) {
        if (removeIfInSelection) {
            if (this.isSelected(item))
                this.removeSelectedItem(item);
            else
                this.addSelectedItem(item);
        } else
            this.addSelectedItem(item);
    }

    public void addSelectedNode(NodeItem item) {
        this.selectedNodes.add(item);
        m_vis.getGroup("_focus_").addTuple(item);
    }

    public void addSelectedEdge(EdgeItem item) {
        this.selectedEdges.add(item);
    }

    //	methods to remove an item
    public void removeSelectedItem(VisualItem item) {
        if (item instanceof EdgeItem)
            this.removeSelectedEdge((EdgeItem) item);
        else if (item instanceof NodeItem)
            this.removeSelectedNode((NodeItem) item);
    }

    public void removeSelectedNode(NodeItem node) {
        this.selectedNodes.remove(node);
        m_vis.getGroup("_focus_").removeTuple(node);
    }

    public void removeSelectedEdge(EdgeItem edge) {
        this.selectedEdges.remove(edge);
    }


    public void clearAll() {
        this.selectedEdges.clear();
        this.selectedNodes.clear();
    }

    //	methods to ask if an item is selected
    public boolean isSelected(VisualItem item) {
        if (item instanceof NodeItem)
            return isSelected((NodeItem) item);
        else if (item instanceof EdgeItem)
            return isSelected((EdgeItem) item);
        else
            return false;
    }

    public boolean isSelected(NodeItem node) {
        return this.selectedNodes.contains(node);
    }

    public boolean isSelected(EdgeItem edge) {
        return this.selectedEdges.contains(edge);
    }

    public Iterator<EdgeItem> edges() {

        return this.selectedEdges.iterator();
    }

    public Iterator<NodeItem> nodes() {

        return this.selectedNodes.iterator();
    }


    public SelectionPredicate getSelectionPredictate() {
        if (sp == null)
            sp = new SelectionPredicate();
        return sp;
    }

    private class SelectionPredicate extends AbstractPredicate {
        public boolean getBoolean(Tuple obj) {
            if (obj instanceof NodeItem) {
                return selectedNodes.contains(obj);
            } else if (obj instanceof EdgeItem) {
                return selectedEdges.contains(obj);
            }
            return false;
        }
    }

    public SelectionListener getSelectionListener(GraphView gv, MainFrame admin, boolean panOverItem) {
        if (sl == null)
            sl = new SelectionListener(gv, admin, panOverItem);
        return sl;
    }

    private class SelectionListener extends ControlAdapter implements PaintListener {
        private Point start = null;
        private Point end = null;
        private boolean buttonPressed = false;
        private int m_button = LEFT_MOUSE_BUTTON;
        private GraphView gv;
        private MainFrame admin;
         private boolean m_panOverItem;
       private   TupleSet allTuple;
        public SelectionListener(GraphView gv, MainFrame admin, boolean panOverItem){
            this.gv = gv;
            this.admin = admin;
            m_panOverItem = panOverItem;
           allTuple = m_vis.getVisualGroup("graph.edges");
        }

        public SelectionListener(int button) {
            m_button = button;
        }

        private Rectangle createRect(Point2D start, Point2D end) {
            int x = (int) Math.min(start.getX(), end.getX());
            int y = (int) Math.min(start.getY(), end.getY());
            int x2 = (int) Math.max(start.getX(), end.getX());
            int y2 = (int) Math.max(start.getY(), end.getY());
            int width = Math.abs(x2 - x);
            int height = Math.abs(y2 - y);
            return new Rectangle(x, y, width, height);
        }


        /**
            * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
            */
           public void itemPressed(VisualItem item, MouseEvent e) {

               if ( m_panOverItem  && allTuple.containsTuple((Tuple)item))
                   mousePressed(e);
           }

           /**
            * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
            */
           public void itemDragged(VisualItem item, MouseEvent e) {
               if ( m_panOverItem  && allTuple.containsTuple((Tuple)item))
                   mouseDragged(e);
           }

           /**
            * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
            */
           public void itemReleased(VisualItem item, MouseEvent e) {
               if ( m_panOverItem  && allTuple.containsTuple((Tuple)item))
                   mouseReleased(e);
           }


        public void mousePressed(MouseEvent e) {

            if (UILib.isButtonPressed(e, m_button)) {
                this.start = e.getPoint();
                this.buttonPressed = true;
            }


        }

        public void mouseReleased(MouseEvent e) {
            Cursor cursor = gv.getDisplay().getCursor();
            if (cursor.getType() == Cursor.DEFAULT_CURSOR || (cursor.getName().equals("zoom"))) {
                if (UILib.isButtonPressed(e, m_button)) {
                    this.end = e.getPoint();
                    Display display = (Display) e.getComponent();

                    Point2D absStart = display.getAbsoluteCoordinate(this.start, null);
                    Point2D absEnd = display.getAbsoluteCoordinate(this.end, null);
                    Rectangle rect = createRect(absStart, absEnd);
                    if (cursor.getName().equals("zoom")) {
                        if(rect.isEmpty()){
                         admin.toggleZoom();
                        } else {
                            gv.resetCursor();
                            gv.zoomRect(true, rect);
                        }
                    } else {
                        //when releasing left mouse button mark all nodes and edges in rectangle as selected
                        Iterator it = display.getVisualization().visibleItems();
                        VisualItem item;

                        while (it.hasNext()) {
                            item = (VisualItem) it.next();
                            if (rect.contains(item.getBounds())) {

                                if (e.isShiftDown())
                                    //  if(e.isControlDown())
                                    removeSelectedItem(item);
                                else {
                                    addSelectedItem(item);
                                }
                            }
                        }
                    }
//		    reset selection rectangle
                    display.getVisualization().run("color");
                    display.repaint();
                    this.end = null;
                    this.buttonPressed = false;
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            Cursor cursor = gv.getDisplay().getCursor();
            if ((cursor.getType() == Cursor.DEFAULT_CURSOR)|| (cursor.getName().equals("zoom"))){

                if (this.buttonPressed) {
                    this.end = e.getPoint();
                    Display display = (Display) e.getComponent();
                    display.repaint();
                }
            }
        }


        public void postPaint(Display d, Graphics2D g) {
            Cursor cursor = gv.getDisplay().getCursor();
            if((cursor.getType() == Cursor.DEFAULT_CURSOR)||(cursor.getName().equals("zoom"))) {
                if (this.end != null) {
                    Rectangle rect = createRect(this.start, this.end);
                    g.setColor(Color.red);
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                }
            }

        }

        public void prePaint(Display d, Graphics2D g) {
        }
    }
}
        

