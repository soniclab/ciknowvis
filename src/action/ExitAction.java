package action;

import java.awt.event.*;
import javax.swing.AbstractAction;


public class ExitAction extends AbstractAction {


    public ExitAction() {

    }

    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}




