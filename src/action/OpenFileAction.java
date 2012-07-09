package action;

import visual.GraphView;

import javax.swing.*;
import java.awt.event.ActionEvent;

import admin.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 18, 2008
 * Time: 4:08:46 PM
 * To change this template use File | Settings | File Templates.
 */

public class OpenFileAction extends AbstractAction {

    private MainFrame _frame;


       public OpenFileAction(MainFrame frame) {
           
           _frame = frame;

       }

       public void actionPerformed(ActionEvent e) {
          
           _frame.LoadData();
       }

}
