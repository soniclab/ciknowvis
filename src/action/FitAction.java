package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 9:55:23 AM
 * To change this template use File | Settings | File Templates.
 */

public class FitAction extends AbstractAction {

    private GraphView gp;


    public FitAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        gp.fitGraph();

        if(gp.isForZoom1())
         gp.fitGraph();
            
    }
}