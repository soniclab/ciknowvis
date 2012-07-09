package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 9:56:39 AM
 * To change this template use File | Settings | File Templates.
 */

public class ZoomInAction extends AbstractAction {

    private MainFrame frame;
    private GraphView gp;

    public ZoomInAction(GraphView _gp, MainFrame _frame) {
        frame = _frame;
        gp = _gp;

    }


    public void actionPerformed(ActionEvent e) {

         if (gp.getDisplay().getCursor().getName().equals("zoom"))
            frame.toggleZoom();
         else if(gp.getDisplay().getCursor().getName().equals("zoomout"))
           frame.toggleZoomOut();
         else if(gp.getDisplay().getCursor().getName().equals("zoomout1"))
           frame.toggleZoomOut1();
          else if(gp.getDisplay().getCursor().getName().equals("zoomin1"))
           frame.toggleZoomIn1();
         else   if(gp.getDisplay().getCursor().getName().equals("pan"))
           frame.togglePan();
       
       
     JToggleButton t = (JToggleButton) e.getSource();
        
         if (t.isSelected()) {
           gp.getDisplay().setCursor(frame.getZoomIn());
        } else {
            gp.getDisplay().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }

    }


}