package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 12:09:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class DepthAction extends AbstractAction {

    private GraphView  gp;


    public DepthAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        gp.doDepth();
    }
}