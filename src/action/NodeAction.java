package action;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

import visual.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 18, 2008
 * Time: 12:09:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeAction extends AbstractAction {

    private MainFrame frame;



    public NodeAction(MainFrame _frame) {
        frame = _frame;


    }

    public void actionPerformed(ActionEvent e) {
        frame.doDisplayNode();

    }
}

