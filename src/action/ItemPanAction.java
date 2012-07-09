package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jun 24, 2008
 * Time: 4:30:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemPanAction extends AbstractAction {

    private MainFrame _frame;


    public ItemPanAction(MainFrame frame) {
        _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {

        _frame.togglePan();

    }
}


