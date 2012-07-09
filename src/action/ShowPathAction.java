package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import admin.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jun 24, 2008
 * Time: 3:46:19 PM
 * To change this template use File | Settings | File Templates.
 */


public class ShowPathAction extends AbstractAction {

    private MainFrame _frame;


    public ShowPathAction(MainFrame frame) {
        _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {

        _frame.toggleShowInitialView();

    }
}
