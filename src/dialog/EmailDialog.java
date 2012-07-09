package dialog;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import admin.MainFrame;
import visual.GraphView;
import data.AppletDataHandler1;
import ciknow.dao.GraphData;
import ciknow.dao.VisualDataFile;
import ciknow.dao.DataSender;

public class EmailDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JTextArea jTextArea0;
	private JScrollPane jScrollPane0;
	private JTextArea jTextArea1;
	private JScrollPane jScrollPane1;
	private JButton jButton1;
	private JButton jButton0;
	private JTextField jTextField0, jTextField1;
    private JLabel jLabel3, jLabel4, jLabel5;
     private GraphView gv;
    private AppletDataHandler1 dh;
    private MainFrame frame;

    public EmailDialog(MainFrame _frame, GraphView gv, AppletDataHandler1 dh) {
         super(JOptionPane.getFrameForComponent(_frame), "", false);
        this.gv = gv;
        this.dh = dh;
        this.frame = _frame;
        initComponents();
				this.setTitle("Send visualization");
				this.setLocationRelativeTo(null);
                 this.getContentPane().setBackground(new Color(115, 170, 218));
                this.getContentPane().setPreferredSize(this.getSize());
				this.pack();
				this.setVisible(true);

    }

	private void initComponents() {
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(92, 135, 205));
		setForeground(Color.black);
		setLayout(new GroupLayout());
	/*	add(getJScrollPane1(), new Constraints(new Leading(85, 350, 10, 10), new Leading(103, 171, 10, 10)));
		//add(getJButton1(), new Constraints(new Leading(243, 10, 10), new Leading(290, 10, 10)));
		//add(getJButton0(), new Constraints(new Leading(141, 67, 10, 10), new Leading(290, 12, 12)));
        add(getJButton0(), new Constraints(new Leading(138, 67, 10, 10), new Leading(317, 12, 12)));
        add(getJButton1(), new Constraints(new Leading(221, 10, 10), new Leading(317, 12, 12)));

        add(getJTextField0(), new Constraints(new Leading(85, 350, 12, 12), new Leading(64, 27, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Leading(86, 348, 12, 12), new Leading(12, 42, 12, 12)));
		add(getJLabel2(), new Constraints(new Leading(21, 12, 12), new Leading(103, 12, 12)));
		add(getJLabel1(), new Constraints(new Leading(59, 12, 12), new Leading(12, 12, 12)));
		add(getJLabel0(), new Constraints(new Leading(30, 12, 12), new Leading(62, 10, 10)));
        add(getJLabel4(), new Constraints(new Leading(16, 418, 10, 10), new Leading(298, 10, 10)));
		add(getJLabel3(), new Constraints(new Leading(17, 418, 12, 12), new Leading(277, 15, 12, 12)));
		setSize(448, 351);   */
        ///////////////////////////////
        add(getJLabel1(), new Constraints(new Leading(59, 12, 12), new Leading(12, 12, 12)));
		add(getJButton0(), new Constraints(new Leading(132, 67, 10, 10), new Leading(355, 12, 12)));
		add(getJButton1(), new Constraints(new Leading(217, 12, 12), new Leading(355, 12, 12)));
		add(getJLabel4(), new Constraints(new Leading(16, 418, 12, 12), new Leading(327, 12, 12)));
		add(getJLabel3(), new Constraints(new Leading(16, 418, 12, 12), new Leading(309, 15, 10, 10)));
		add(getJLabel0(), new Constraints(new Leading(28, 10, 10), new Leading(101, 12, 12)));
		add(getJLabel5(), new Constraints(new Leading(28, 12, 12), new Leading(64, 12, 12)));
		add(getJLabel2(), new Constraints(new Leading(21, 12, 12), new Leading(133, 18, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Leading(83, 351, 12, 12), new Leading(12, 42, 12, 12)));
		add(getJTextField0(), new Constraints(new Leading(81, 353, 12, 12), new Leading(100, 27, 10, 10)));
		add(getJTextField1(), new Constraints(new Leading(82, 352, 12, 12), new Leading(64, 28, 10, 10)));
		add(getJScrollPane1(), new Constraints(new Leading(82, 352, 12, 12), new Leading(135, 171, 10, 10)));
		setSize(448, 390);
        
    }

    private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Reply to:");
		}
		return jLabel5;
	}

	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();			
		}
		return jTextField1;
	}
    private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			 jLabel4.setForeground(Color.red);
           jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel4.setFont(new Font("Arial", Font.PLAIN, 12));
		}
		return jLabel4;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
            jLabel3.setForeground(Color.red);
           jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel3.setFont(new Font("Arial", Font.ITALIC, 12));
         
		}
		return jLabel3;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
		jTextField0.setText("network VIS");
		}
		return jTextField0;
	}

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
           jButton0.setBackground(Color.white);
            jButton0.setText("Send");
        }

        jButton0.addActionListener(this);
        jButton0.setActionCommand("send");

        return jButton0;
	}

	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
             jButton1.setBackground(Color.white);
            jButton1.setText("Cancel");
		}

        jButton1.addActionListener(this);
        jButton1.setActionCommand("cancel");
        return jButton1;
	}

	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
            jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollPane1.setViewportView(getJTextArea1());
		}
		return jScrollPane1;
	}

	private JTextArea getJTextArea1() {
		if (jTextArea1 == null) {
			jTextArea1 = new JTextArea();
			jTextArea1.setText("The visualization is generated from C-IKNOW system");
		}
		return jTextArea1;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
             jScrollPane0.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollPane0.setViewportView(getJTextArea0());
		}
		return jScrollPane0;
	}

	private JTextArea getJTextArea0() {
		if (jTextArea0 == null) {
			jTextArea0 = new JTextArea();
			//jTextArea0.setText("email");
		}
		return jTextArea0;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Message:");
		}
		return jLabel2;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("To:");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Subject:");
		}
		return jLabel0;
	}

     public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("cancel")) {

            setVisible(false);
            dispose();
        } else if (action.equals("send")) {
            GraphData data = new GraphData();
            data.setEmailAd(jTextArea0.getText());
          //  data.setSub("network VIS");
          //  data.setMsg("The network is generated from C-IKNOW system");
            data.setSub(jTextField0.getText());
            data.setReply(jTextField1.getText());
            data.setMsg(jTextArea1.getText());
            data.setFileName("vis.html");
            String XMLStr = null;

            VisualDataFile vd = new VisualDataFile();
            String dataStr = vd.generateHtmlFile(gv, dh, XMLStr, true);
            List<Long> nodeIds = vd.getNodeIds();
            List<String> edgeFTTypes = vd.getEdgeFTTypes();

            data.setHTMLData(dataStr);
            data.setNodeIds(nodeIds);
            data.setEdgeFTTypes(edgeFTTypes);
            DataSender ds = new DataSender(data, frame);
            char feedBack = ds.feedbackFromServer();

           if (feedBack == 'n' || feedBack == 's') {
                setVisible(false);
                dispose();
                new SaveConfirmDialog(feedBack);
            } else  if(feedBack == 't') {
                    String str = "The email address in the \"To\" field was not recognized.";
                    String str1 =  "Please make sure that all addresses are properly formed.";
                    jLabel3.setText(str);
                    jLabel4.setText(str1);

             } else {

              String str = "Service temporarily unavailable. ";
              jLabel3.setText(str);
            }
        }
    }

}
