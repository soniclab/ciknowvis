package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 9:54:55 AM
 * To change this template use File | Settings | File Templates.
 */

public class CircleAction extends AbstractAction {

    private GraphView gp;


    public CircleAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        gp.makeLayout();
        gp.layoutCircle();

    }
}