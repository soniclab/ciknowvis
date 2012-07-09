package dialog;

import admin.MainFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Apr 28, 2008
 * Time: 4:52:47 PM
 * To change this template use File | Settings | File Templates.
 */


public class ProcessingMessage extends JDialog{


    private MainFrame frame;
    private int totalSize;

    public ProcessingMessage(MainFrame _frame, int totalSize) {

        super(JOptionPane.getFrameForComponent(_frame), "", false);
        frame = _frame;
        this.totalSize = totalSize;
        initDialog();

    }

    private JPanel centralPanel() {

       JPanel flowJPanel = new JPanel();
       flowJPanel.setLayout(new BoxLayout(flowJPanel, BoxLayout.Y_AXIS));
        flowJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
          String message ="";

          message = "<html>THe network is large, please wait...<br><br>If you do not want to wait, <br>please go back to the interface and select a new network</html>";

        flowJPanel.add(new JLabel(message));
        return flowJPanel;
    }


    private void initDialog() {

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(centralPanel(), BorderLayout.CENTER);

     setSize(350, 130);
    
    }





}