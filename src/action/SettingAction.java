package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Sep 24, 2010
 * Time: 3:54:35 PM
 * To change this template use File | Settings | File Templates.
 */


public class SettingAction extends AbstractAction {

    private MainFrame _frame;


       public SettingAction(MainFrame frame) {

           _frame = frame;

       }

       public void actionPerformed(ActionEvent e) {

           _frame.reSettingGraph();
       }

}
