package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jun 24, 2008
 * Time: 4:30:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemShowWeightAction extends AbstractAction {

    private MainFrame _frame;


    public ItemShowWeightAction(MainFrame frame) {
        _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {

        _frame.toggleWeight();


    }
}