package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;
import admin.MainFrame;
import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 6:37:58 AM
 * To change this template use File | Settings | File Templates.
 */

public class ResizeNodesAction extends AbstractAction {

    private GraphView gp;
    private MainFrame _frame;
    private AppletDataHandler1 dh;

    public ResizeNodesAction(GraphView _gp, MainFrame frame, AppletDataHandler1 _dh) {
        gp = _gp;
        _frame = frame;
        dh = _dh;
    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        JToggleButton t = (JToggleButton) e.getSource();
        int recStatus = gp.getRecStatus();
        if (recStatus == 2) {
            if (t.isSelected()) {

                gp.maxNode();
            } else
                _frame.doNormalizedAction(false, true);

        } else {
            if (!dh.getLabelHiden().equals("0")) {
                if (gp.isCustomlayout) {
                    if (t.isSelected()) {
                        gp.minNode();
                    } else {
                        _frame.doNormalizedAction(false, false);
                    }
                } else {
                    if (t.isSelected())
                        _frame.doNormalizedAction(false, false);

                    else
                        gp.minNode();
                }
            } else {
                if (gp.isCustomlayout) {
                    if (t.isSelected()) {
                        gp.maxNode();
                    } else {
                        _frame.doNormalizedAction(false, false);
                    }
                } else {
                    if (t.isSelected())
                        _frame.doNormalizedAction(false, false);
                    else
                        gp.maxNode();
                }
            }
        }
    }
}




