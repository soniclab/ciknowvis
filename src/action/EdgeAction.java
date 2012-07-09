package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 12:09:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class EdgeAction extends AbstractAction {

    private MainFrame frame;



    public EdgeAction(MainFrame _frame) {
        frame = _frame;


    }

    public void actionPerformed(ActionEvent e) {
        frame.doDisplayEdge();

    }
}
