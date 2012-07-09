package dialog;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 13, 2008
 * Time: 4:48:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class OtherDialog extends JDialog implements ActionListener {

    private JSpinner depth;
    private int value = -1;

    public OtherDialog() {

    }

    public OtherDialog(Component parent) {
        super(JOptionPane.getFrameForComponent(parent), "Other Depth", true);
        depth = new JSpinner(new SpinnerNumberModel(new Integer(5),
                new Integer(1), new Integer(100), new Integer(1)));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        JButton okButton = new JButton("OK");
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
            setVisible(false);
            dispose();
        } else if (action.equals("cancel")) {
            value = -1;
            setVisible(false);
            dispose();
        }
    }
}
