package action;

import java.awt.event.*;
import javax.swing.AbstractAction;

import admin.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 14, 2008
 * Time: 12:57:58 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Swing menu action that loads a graph into the graph viewer.
 */
public class GraphMenuAction extends AbstractAction {

    private MainFrame _frame;


    public GraphMenuAction(MainFrame frame) {
        _frame = frame;

    }

    public void actionPerformed(ActionEvent e) {

    }

    /*      private GraphView m_view;



public GraphMenuAction(String name, String accel, GraphView view) {
   m_view = view;
   this.putValue(AbstractAction.NAME, name);
   this.putValue(AbstractAction.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke(accel));
}
public void actionPerformed(ActionEvent e) {
   m_view.setGraph(getGraph(), "label");

    /*  openAction.setEnabled(false);
   exitAction.setEnabled(false);
   closeAction.setEnabled(true);
   newAction.setEnabled(true);
updateAction.setEnabled(true);
   deleteAction.setEnabled(true);
   //selectAll.setEnabled(true);
   nameAction.setEnabled(true);
numberAction.setEnabled(true);
   printAction.setEnabled(true);
}
protected abstract Graph getGraph();  */
}