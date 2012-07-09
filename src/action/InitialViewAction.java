package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 6:55:58 AM
 * To change this template use File | Settings | File Templates.
 */

public class InitialViewAction extends AbstractAction {

    private GraphView gp;


    public InitialViewAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        JToggleButton t = (JToggleButton) e.getSource();
        if (t.isSelected()) {
            gp.showAllGraph();
        } else
            gp.showInitialView();
    }
}