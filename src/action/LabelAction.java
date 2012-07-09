package action;

import visual.GraphView;

import javax.swing.*;

import admin.MainFrame;

import java.awt.event.ActionEvent;

public class LabelAction extends AbstractAction {

    private GraphView gp;


    public LabelAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        JToggleButton t = (JToggleButton) e.getSource();
        if (gp.getRecStatus() == 2) {
            if (t.isSelected())
                gp.noLabelNode();

            else
                gp.labelNode();

    } else {
        if (t.isSelected())
            gp.labelNode();

        else
            gp.noLabelNode();
    }
  }
}