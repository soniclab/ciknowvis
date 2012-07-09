package action;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.*;
import admin.MainFrame;
import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 6:37:58 AM
 * To change this template use File | Settings | File Templates.
 */

public class ImageNodesAction extends AbstractAction {

    private GraphView gp;

    private AppletDataHandler1 dh;

    public ImageNodesAction(GraphView _gp, AppletDataHandler1 _dh) {
        gp = _gp;
        dh = _dh;
    }

    public void actionPerformed(ActionEvent e) {
        gp.resetCursor();
        JToggleButton t = (JToggleButton) e.getSource();
        if (!dh.getLabelHiden().equals("0")) {
            if(gp.isCustomlayout){
                if (t.isSelected()) {
                  gp.minNode();


            } else {
                gp.imageNode(gp.imageSize);

            }
            }else{
            if (t.isSelected()) {

                 gp.imageNode(gp.imageSize);

            } else {

                gp.minNode();
            }
            }
        } else {
             if(gp.isCustomlayout){
                if (t.isSelected()) {
                 gp.maxNode();


            } else {
                gp.imageNode(gp.imageSize);

            }
            }else{
            if (t.isSelected()) {

                 gp.imageNode(gp.imageSize);
               

            } else {
             
                gp.maxNode();
            }
             }
        }

    }
}

