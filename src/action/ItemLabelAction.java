package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Oct 23, 2009
 * Time: 10:14:15 AM
 * To change this template use File | Settings | File Templates.
 */

public class ItemLabelAction extends AbstractAction {

    private MainFrame _frame;


    public ItemLabelAction(MainFrame frame) {
        _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {

        _frame.toggleLabelNodes();

    }
}
