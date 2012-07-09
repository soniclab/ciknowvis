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
 * Time: 9:56:59 AM
 * To change this template use File | Settings | File Templates.
 */

public class ZoomOutAction extends AbstractAction {

    private MainFrame frame;
    private GraphView gp;


    public ZoomOutAction(GraphView _gp, MainFrame _frame) {
        frame = _frame;
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
      if (gp.getDisplay().getCursor().getName().equals("zoom"))
            frame.toggleZoom();
        else if(gp.getDisplay().getCursor().getName().equals("zoomin"))
           frame.toggleZoomIn();
         else if(gp.getDisplay().getCursor().getName().equals("zoomout1"))
           frame.toggleZoomOut1();
          else if(gp.getDisplay().getCursor().getName().equals("zoomin1"))
           frame.toggleZoomIn1();
         else   if(gp.getDisplay().getCursor().getName().equals("pan"))
           frame.togglePan();
     JToggleButton t = (JToggleButton) e.getSource();
  
        if (t.isSelected()) {
           gp.getDisplay().setCursor(frame.getZoomOut());
        } else {
            gp.getDisplay().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }


    }
}