package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 9:57:13 AM
 * To change this template use File | Settings | File Templates.
 */

public class ShowAllAction extends AbstractAction {

    private GraphView gp;


    public ShowAllAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();

        if(gp.isCustomlayout)
        gp.showAllGraph();
        else
            gp.showAllNodes();
    }
}