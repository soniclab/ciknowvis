package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Oct 23, 2009
 * Time: 10:14:15 AM
 * To change this template use File | Settings | File Templates.
 */

public class ItemImageSizeAction extends AbstractAction {

     private GraphView gp;


    public ItemImageSizeAction(GraphView _gp) {
        gp = _gp;

    }

    public void actionPerformed(ActionEvent e) {

        gp.openImageDialog();

    }
}
