package action;

import visual.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

import admin.MainFrame;


/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 6:37:17 AM
 * To change this template use File | Settings | File Templates.
 */

public class AnnealAction extends AbstractAction {

    private GraphView gp;
    private MainFrame _frame;
    private boolean  hasDialogBox;
    private int method;
    public AnnealAction(GraphView _gp, int method) {

        gp = _gp;
        this.method = method;
    }

     public AnnealAction(MainFrame frame, int method) {
        _frame = frame;
      hasDialogBox  = true;
         this.method = method;
    }

    public void actionPerformed(ActionEvent e) {

        if(hasDialogBox){
             _frame.doCusteringAction(method);
        }else{           
           gp.resetCursor();
           gp.defaultAutoLayout(method);
        }
    }
}