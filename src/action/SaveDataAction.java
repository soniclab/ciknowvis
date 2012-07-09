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

public class SaveDataAction extends AbstractAction {

    private MainFrame _frame;
     private GraphView gp;
    private boolean isApplet;

        public SaveDataAction(MainFrame frame, GraphView _gp, boolean isApplet) {
            _frame = frame;
             gp = _gp;
          this.isApplet = isApplet;
        }

        public void actionPerformed(ActionEvent e) {
           // System.out.println("in action fffff");
            if(isApplet)
            _frame.openSaveWin();
            else{
              gp.resetCursor();
            gp.saveData();

            }

        }
}