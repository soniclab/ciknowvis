package action;

import admin.MainFrame;

import javax.swing.*;

import visual.GraphView;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jan 31, 2011
 * Time: 10:25:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class SendMailAction extends AbstractAction {


    private MainFrame _frame;
     private GraphView gp;
    private boolean isApplet;

        public SendMailAction(MainFrame frame, GraphView _gp, boolean isApplet) {
            _frame = frame;
             gp = _gp;
          this.isApplet = isApplet;
        }

        public void actionPerformed(ActionEvent e) {
           // System.out.println("in action fffff");
            if(isApplet)
            _frame.SendMailWin();

        }
}