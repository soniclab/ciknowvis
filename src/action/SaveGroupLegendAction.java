package action;

import visual.GraphView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 18, 2008
 * Time: 4:08:46 PM
 * To change this template use File | Settings | File Templates.
 */

public class SaveGroupLegendAction extends AbstractAction {

    private GraphView gp;


    public SaveGroupLegendAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
      gp.resetCursor();
        gp.SaveGraph(false);
    }
}
