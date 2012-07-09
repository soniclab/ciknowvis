package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 9:54:55 AM
 * To change this template use File | Settings | File Templates.
 */

public class CustomAction extends AbstractAction {

    private GraphView gp;
    private boolean hiddenExists;

    public CustomAction(GraphView _gp, boolean _hiddenExists) {
        gp = _gp;
        hiddenExists = _hiddenExists;
    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();

        if(hiddenExists){

            gp.showInitialView();
        }else{

        gp.layoutCustom(null);
            gp.rescaleNodes(1.0, false, 0, 0, false);
        }
    }
}