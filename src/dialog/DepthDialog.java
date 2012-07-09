package dialog;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import visual.GraphView;
import admin.MainFrame;


/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 13, 2008
 * Time: 4:48:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DepthDialog extends JDialog implements ActionListener {

    private JSpinner depth;
    private int value = -1;

    private GraphView gv;
    public DepthDialog() {

    }

    public DepthDialog(MainFrame frame, GraphView _gv) {

      //  super();
         super(JOptionPane.getFrameForComponent(frame), "", false);
        JButton cancelButton, okButton;
        gv = _gv;
        depth = new JSpinner(new SpinnerNumberModel(1,1, 100, 1));
         cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        okButton = new JButton("OK");
        okButton.setActionCommand("ok");
        okButton.addActionListener(this);
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(cancelButton);
        buttons.add(okButton);
        JPanel notGiantPane = new JPanel();
        notGiantPane.setLayout(new FlowLayout());
        notGiantPane.add(new JLabel("Choose selection depth:"));
        notGiantPane.add(depth);
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        pane.add(notGiantPane);
        pane.add(buttons);
        setContentPane(pane);
        pack();

    }

    public int getValue() {
        return value;
    }

    public void actionPerformed(ActionEvent e) {

        String action = e.getActionCommand();
        if (action.equals("ok")) {
            value = ((Integer) (depth.getValue())).intValue();
           gv.multiNodeDepth(value);
              setVisible(false);
               dispose();

        } else if (action.equals("cancel")) {
           // value = -1;
            setVisible(false);
              dispose();
        }
    }


}