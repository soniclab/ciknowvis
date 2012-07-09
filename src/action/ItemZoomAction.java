package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 12, 2009
 * Time: 2:46:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemZoomAction extends AbstractAction {

    private MainFrame _frame;


    public ItemZoomAction(MainFrame frame) {
        _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {

        _frame.toggleZoom();

    }
}
