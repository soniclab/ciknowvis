package dialog;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Jan 13, 2008
 * Time: 4:46:50 PM
 * To change this template use File | Settings | File Templates.
 */

public class LimitDialog extends JDialog implements ActionListener, PropertyChangeListener {
    private static final String set = "Set";
    private static final String close = "Close";
    private static final String message = "Limit visible edge weights:";
    private double min;
    private double max;

    private JSpinner minSpin, maxSpin;
    private JOptionPane optionPane;
    private MainFrame frame;

    public LimitDialog(MainFrame _frame) {
        super(JOptionPane.getFrameForComponent(_frame), "", false);
        frame = _frame;
        JPanel pane = new JPanel(new FlowLayout());
        pane.add(new JLabel("between "));
        min = frame.getGP1().getMinWeight();
        max = frame.getGP1().getMaxWeight();
        double minEdge = frame.getHandler1().getMinEdgeStrength();
        double maxEdge = frame.getHandler1().getMaxEdgeStrength();
        if (min > minEdge) min = minEdge;
        if (max < maxEdge) max = maxEdge;

        minSpin = new JSpinner(new SpinnerNumberModel(new Double(minEdge),
                new Double(min - 0.1), new Double(max + 0.1), new Double(0.1)));
        minSpin.setEditor(new JSpinner.NumberEditor(minSpin, "0.00"));
        maxSpin = new JSpinner(new SpinnerNumberModel(new Double(maxEdge),
                new Double(min - 0.1), new Double(max + 0.1), new Double(0.1)));
        maxSpin.setEditor(new JSpinner.NumberEditor(maxSpin, "0.00"));
        pane.add(minSpin);
        pane.add(new JLabel(" and "));
        pane.add(maxSpin);
        Object[] messages = {message, pane};
        Object[] buttons = {set, close};
        optionPane = new JOptionPane(messages,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null, buttons, buttons[0]);
        setContentPane(optionPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });
        optionPane.addPropertyChangeListener(this);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(set);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible() && (e.getSource() == optionPane) &&
                (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                        JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                return;
            }

            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (set.equals(value)) {
                min = ((Double) (minSpin.getValue())).doubleValue();
                max = ((Double) (maxSpin.getValue())).doubleValue();
                frame.getGP1().setLimit(min, max);
            } else {
                setVisible(false);
            }
        }
    }
}