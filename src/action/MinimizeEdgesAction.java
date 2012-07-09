package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;
import admin.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 6:37:58 AM
 * To change this template use File | Settings | File Templates.
 */

public class MinimizeEdgesAction extends AbstractAction {

    private GraphView gp;
     private MainFrame _frame;

    public MinimizeEdgesAction(GraphView _gp, MainFrame frame) {
        gp = _gp;
         _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {
       gp.resetCursor();
        JToggleButton t = (JToggleButton) e.getSource();
        if (t.isSelected()) {
            _frame.doNormalizedAction(true, false);

        } else
            gp.minEdgeWidth();
    }
}
