package action;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 14, 2008
 * Time: 1:01:17 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.*;
import java.awt.event.*;

import admin.*;
import prefuse.util.io.IOLib;
import prefuse.data.Graph;

public class OpenGraphAction extends AbstractAction {


    private MainFrame frame;

    public OpenGraphAction(MainFrame _frame) {
        frame = _frame;
        this.putValue(AbstractAction.NAME, "Open File...");
        this.putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("ctrl O"));
    }

    public void actionPerformed(ActionEvent e) {
          Graph g = IOLib.getGraphFile(frame);
      /*  if ( g == null ) return;
        String label = frame.getModel().getLabel(frame, g);
        if ( label != null ) {
          frame.getModel().setGraph(g, label);
        } */
    }
}
