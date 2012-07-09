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
public class AboutAction extends AbstractAction {

    private MainFrame _frame;


    public AboutAction(MainFrame frame) {
        _frame = frame;
 }

    public void actionPerformed(ActionEvent e) {
        _frame.doAboutAction();
    }
}