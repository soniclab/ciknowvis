package visual;

import prefuse.controls.ControlAdapter;
import prefuse.controls.Control;
import prefuse.Visualization;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ui.UILib;

import java.awt.event.MouseEvent;
import java.awt.*;
import admin.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jul 18, 2008
 * Time: 12:50:28 PM
 * To change this template use File | Settings | File Templates.
 */


public class FocusReleaseControl extends ControlAdapter {


    private Visualization vis;
    private GraphView gv;
    private MainFrame admin;
      protected int button1 = Control.RIGHT_MOUSE_BUTTON;
    public FocusReleaseControl(Visualization vis, GraphView gv, MainFrame admin) {
        super();
        this.vis = vis;
        this.gv = gv;
        this.admin = admin;

    }

    
    public void mouseClicked(MouseEvent e) {
      
        if (!(e.getSource().getClass().getName()).equalsIgnoreCase("VisualItem") && !UILib.isButtonPressed(e, button1)) {
             if (gv.getDisplay().getCursor().getType() == (Cursor.DEFAULT_CURSOR)) {
           // release all focus and neighbour highlight
            TupleSet ts = vis.getGroup(Visualization.FOCUS_ITEMS);
              if(gv.getRecStatus() == 2 && gv.fromIndirect && (ts.getTupleCount() ==0) && !gv.fromAll)
            gv.showDirectGraph();

            ts.clear();
            vis.removeGroup("depthEdge");
             gv.releaseSearchFocus();
             }
        }

         if (UILib.isButtonPressed(e, button1) &&   e.getClickCount() == 1) {
         if(gv.getDisplay().getCursor().getName().equals("zoom"))
           admin.toggleZoom();
         else if(gv.getDisplay().getCursor().getName().equals("zoomout"))
           admin.toggleZoomOut();
           else if(gv.getDisplay().getCursor().getName().equals("zoomin"))
           admin.toggleZoomIn();
         else if(gv.getDisplay().getCursor().getName().equals("zoomout1"))
           admin.toggleZoomOut1();
          else if(gv.getDisplay().getCursor().getName().equals("zoomin1"))
           admin.toggleZoomIn1();
         else   if(gv.getDisplay().getCursor().getName().equals("pan"))
           admin.togglePan();

         }


       
    }
}
