package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;
import admin.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 9:54:55 AM
 * To change this template use File | Settings | File Templates.
 */

public class AggregateAction extends AbstractAction {

    private GraphView gp;
   private MainFrame frame;

    public AggregateAction(GraphView _gp, MainFrame _frame) {
        gp = _gp;
        frame = _frame;
    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        gp.makeLayout();
        gp.layoutGroup(false);

    }
}