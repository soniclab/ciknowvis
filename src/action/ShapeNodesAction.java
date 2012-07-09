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

public class ShapeNodesAction extends AbstractAction {

    private GraphView gp;
    private MainFrame _frame;
    private AppletDataHandler1 dh;

    public ShapeNodesAction(GraphView _gp, MainFrame frame, AppletDataHandler1 _dh) {
        dh = _dh;
        _frame = frame;
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        JToggleButton t = (JToggleButton) e.getSource();

        if (!dh.getLabelHiden().equals("0")) {
            if (gp.isCustomlayout) {
                if (t.isSelected()) {
                    gp.minNode();
                } else {
                    gp.shapeNode();

                }

            } else {
                if (t.isSelected())

                    gp.shapeNode();
                else
                    gp.minNode();
            }
        } else {
            if (gp.isCustomlayout) {
                if (t.isSelected()) {
                     gp.maxNode();
                } else {
                    gp.shapeNode();
                }

            } else {
                if (t.isSelected())

                    gp.shapeNode();
                else
                    gp.maxNode();

            }
        }
    }
}




