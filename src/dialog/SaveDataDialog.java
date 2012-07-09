package dialog;

import admin.MainFrame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import visual.GraphView;
import ciknow.dao.*;
import ciknow.dao.DataSender;
import data.AppletDataHandler1;
import prefuse.util.FontLib;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jan 6, 2010
 * Time: 1:34:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveDataDialog extends JDialog implements ActionListener {



    private	JButton							cancelButton;
        private	JButton							saveButton;

        private	JLabel							name;
        private	JLabel							label;

        private	JPanel							dataJPanel;

        private	JTextField					namefield;
        private	JTextField					labelField;
        private GraphView gv;
        private AppletDataHandler1 dh;
        private MainFrame frame;
        private JLabel messageLabel;

      public SaveDataDialog(MainFrame _frame, GraphView gv, AppletDataHandler1 dh) {
        super(JOptionPane.getFrameForComponent(_frame), "", false);
         //  getContentPane().setLayout(new GridLayout(3,1));
        getContentPane().add(setdataPane(),BorderLayout.CENTER);
        getContentPane().add(setButtons(), BorderLayout.SOUTH);
          
         pack();

          this.gv = gv;
          this.dh = dh;
          this.frame = _frame;
      }



      private JPanel setButtons()
      {
        JPanel gridJPanel;
        JPanel flowJPanel;

        saveButton = new JButton("Save");
         saveButton.setBackground(Color.white);
           saveButton.addActionListener(this);
          // saveButton.;
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.white);
        cancelButton.addActionListener(this);
           saveButton.setActionCommand("save");
           cancelButton.setActionCommand("cancel");

        gridJPanel = new JPanel();
        gridJPanel.setBackground(Color.white);
           gridJPanel.add(saveButton);
        gridJPanel.add(cancelButton);

        flowJPanel = new JPanel(new BorderLayout());
         flowJPanel.setBackground(Color.white);

           JPanel labelpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelpanel.setBackground(Color.white);
          messageLabel = new JLabel("<html><br> </html>");
         
        messageLabel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 12));
         labelpanel.add(messageLabel);
        flowJPanel.add(labelpanel, BorderLayout.CENTER);
        flowJPanel.add(gridJPanel, BorderLayout.SOUTH);

        return flowJPanel;
      }


      private JPanel setdataPane()
      {
        JPanel flowJPanel;
        JPanel gridJPanel;

            dataJPanel = new JPanel();
            dataJPanel.setLayout(new BoxLayout(dataJPanel, BoxLayout.X_AXIS));
           dataJPanel.setBackground(Color.white);
        gridJPanel = new JPanel(new GridLayout(2,1));
         gridJPanel.setBackground(Color.white);

            name = new JLabel("Name:");

        flowJPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         flowJPanel.setBackground(Color.white);
        flowJPanel.add(name);
        gridJPanel.add(flowJPanel);

            label = new JLabel("Description:");
            flowJPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
           flowJPanel.setBackground(Color.white);  
            flowJPanel.add(label);
            gridJPanel.add(flowJPanel);

            dataJPanel.add(gridJPanel);

            gridJPanel = new JPanel(new GridLayout(2,1));

            namefield = new JTextField(25);

            flowJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
               flowJPanel.setBackground(Color.white);
            flowJPanel.add(namefield);
            gridJPanel.add(flowJPanel);

            labelField = new JTextField(25);
            flowJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowJPanel.setBackground(Color.white);
            flowJPanel.add(labelField);
          
            gridJPanel.add(flowJPanel);
            dataJPanel.add(gridJPanel);

            return dataJPanel;
      }


      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("cancel")) {

            setVisible(false);
            dispose();
        } else if (action.equals("save")) {
            GraphData data = new GraphData();
            data.setLabel(labelField.getText());
            data.setName(namefield.getText());
            VisualDataFile vd = new VisualDataFile();
            String dataStr = vd.generateHtmlFile(gv, dh, null, false);
        //System.out.println(dataStr);
            data.setData(dataStr);
           // should not for recommendation network
            data.setType(dh.getRecPara()[0]);
            DataSender ds = new DataSender(data, frame);
            char hasSameName = ds.feedbackFromServer();
            // char hasSameName = 'n';
            if(hasSameName == 'y'){

              String str = "The name already exists, please enter a different one";
              String message =   "<html><font color=\"red\" >" + str+ "</font></html>";
                messageLabel.setText(message);
            }else  if(hasSameName == 'n'){
                  setVisible(false);
            dispose();
                new SaveConfirmDialog('n');
             
           
            } else{
                  String str = "Service temporarily unavailable ";
              String message =   "<html><font color=\"red\" >" + str+ "</font></html>";
                messageLabel.setText(message);
            }
        }
      }
}


