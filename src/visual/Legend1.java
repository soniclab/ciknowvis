package visual;


import data.AppletDataHandler1;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.*;
import java.util.*;

import prefuse.util.FontLib;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import dialog.ProcessingMessage;

public class Legend1 extends JPanel implements ActionListener, ChangeListener {

    /**
     * a single item (key/color) pair in the legend
     */

    private AppletDataHandler1.LinkItem[] linkItems;
    private AppletDataHandler1.Item[] items;


    private static final int SwatchWidth = 20;
    private static final int HPadding = 2;
    private static final int SwatchHeight = 12;
    private static final int VPadding = 4;
    private static final Color SwatchBorderColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    private static final Color TextColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    private int app_width;

    Font plainFont = new Font("SansSerif", Font.PLAIN, 12);
    private Map<Color, String> e_ColorType;
    private Map<Color, String> n_ColorType;
    private int legendSize;
    private boolean hScrollable;
    private boolean isRec;
    private Map<String, String> shapeMap;
    private boolean shapeActivated, groupActivated;
    private GeneralPath m_path = new GeneralPath();
    private String shapeBy, colorBy, groupBy;
    private ProcessingMessage message;
    private JColorChooser colorChooser;
    private JDialog dialog;
    Color currentColor;
    private String nodeButtonName, linkButtonName;
    private String jaString;
    private  JButton label;
    private Set<String> edgeTypes, nodeTypes, shapeAttris;
    private boolean defaultlegend = true;
    private ArrayList<String> groupColor;
    private GraphView view;

    /**
     * constructor, get legend items, font, fontmetrics
     */
    public Legend1(AppletDataHandler1 dh, GraphView view, ProcessingMessage message) {

        setOpaque(true);
        items = dh.getLegendItems();
        linkItems = dh.getLinkLegendItems();
        Font font = plainFont;
         this.view = view;
        app_width = getWidth();

        int app_height = getHeight();
        e_ColorType = new HashMap<Color, String>();
        n_ColorType = new HashMap<Color, String>();

        isRec = dh.isRec;

        //   calcWidth();
        shapeMap = view.getShapeMap();
        legendSize = items.length + linkItems.length + shapeMap.size();
        if(view.getRecStatus() == 2)
        legendSize = legendSize + 5;


        for (int i = 0; i < items.length; ++i) {

            if (items[i].key.length() > 20 && !hScrollable) {
                //  System.out.println("items[i].key: " + items[i].key);
                hScrollable = true;
            }
        }

        for (int i = 0; i < linkItems.length; ++i) {

            if (linkItems[i].key.length() > 20 && !hScrollable) {
               //  System.out.println("linkItems[i].key: " + linkItems[i].key);
                hScrollable = true;
            }
        }

        shapeBy = dh.getShapeBy();
        colorBy = dh.getColorQuestion();
        groupBy = dh.getGroupBy();

        this.message = message;
        setLayout(null);


        colorChooser = new JColorChooser();
         colorChooser.setPreviewPanel(new JPanel());
         label = new JButton( );
        this.view = view;
        jaString = "\u25ac" + "\u25ba";

    }

    public void shapeLegendStatus(boolean _shapeActivated) {
        shapeActivated = _shapeActivated;
    }

    public void groupLegendStatus(boolean _groupActivated) {
        groupActivated = _groupActivated;
    }

    public void currentLegend(){
   // edges
         defaultlegend = false;
        edgeTypes = new HashSet<String>();
        nodeTypes = new HashSet<String>();
        shapeAttris = new HashSet<String>();

        TupleSet tupleSet = view.getCurrentVis().getVisualGroup("graph.edges");
         Iterator edgeItems = tupleSet.tuples();
            while (edgeItems.hasNext()) {

                EdgeItem item = (EdgeItem) edgeItems.next();

                if(item.isVisible()){
                    String edgeType = item.getString("edgeTypeDis");

                edgeTypes.add(edgeType);
                }
            }

     //nodes
        TupleSet tupleSet1 = view.getCurrentVis().getVisualGroup("graph.nodes");
         Iterator nodeItems = tupleSet1.tuples();
            while (nodeItems.hasNext()) {

                NodeItem item = (NodeItem) nodeItems.next();


               //  if (shapeActivated) {
                      if(item.isVisible()){
                    String shapeAttri = item.getString("shapeAttri");
                     shapeAttris.add(shapeAttri);
                     }
               //  }

                  if(item.isVisible()) {
                      String nodeType = item.getString("nodeType");
                      if (!colorBy.equalsIgnoreCase("NodeType")) {
                          nodeType = item.getString("colorAttri");

                    }
                nodeTypes.add(nodeType);
                  }
            }

    }
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        int v = VPadding;


          if (view.recLegend) {
              Graphics2D gbi3 = (Graphics2D) g;

      gbi3.setFont(FontLib.getFont("Times New Roman", Font.PLAIN, 14));
                 //  v += SwatchHeight +2 * VPadding;
                 gbi3.drawString("Distance from requestor to", HPadding, v + SwatchHeight);
                v += SwatchHeight +2 * VPadding;
                gbi3.drawString("recommendation is based on", HPadding, v + SwatchHeight);
                 v += SwatchHeight +2 * VPadding;
               JLabel jb = new JLabel();
              jb.setText(view.recDistanceinfo);
              jb.setForeground(Color.red);
               jb.setFont(FontLib.getFont("Times New Roman", Font.PLAIN, 14));
              jb.setBounds(HPadding, v, 400, SwatchHeight);
              add(jb);

               v += SwatchHeight + 4 * VPadding;
                gbi3.drawString("Node size for recommendation", HPadding, v + SwatchHeight);
               v += SwatchHeight +2 * VPadding;
                gbi3.drawString("is based on", HPadding, v + SwatchHeight);
               v += SwatchHeight +2 * VPadding;
                JLabel jb1 = new JLabel();
              jb1.setText(view.recNodeInfo);
              jb1.setFont(FontLib.getFont("Times New Roman", Font.PLAIN, 14));
              Color c = new Color(Integer.parseInt("009933", 16));
              jb1.setForeground(c);
              jb1.setBounds(HPadding, v, 400, SwatchHeight);
              add(jb1);
               v += SwatchHeight +2 * VPadding;
   gbi3.drawString("-----------------------------------------------------------", HPadding, v + SwatchHeight);
    gbi3.drawString("-----------------------------------------------------------", HPadding, v + SwatchHeight);
               v += SwatchHeight + 4 * VPadding;
          }


        // gbi represents legend key string.
        Graphics2D gbi = (Graphics2D) g;

          if (groupActivated) {
              gbi.setFont(FontLib.getFont("SansSerif", Font.BOLD, 12));
                          gbi.drawString("Grouped by " + groupBy + ":", HPadding, v + SwatchHeight);
                          v += SwatchHeight + 4 * VPadding;

           gbi.setFont(FontLib.getFont("SansSerif", Font.PLAIN, 12));
          groupColor =  view.getGroupColor();
        String[] keyArray = groupColor.toArray(new String[groupColor.size()]);

        JCheckBox[] checkButton = new JCheckBox[keyArray.length];

        AppletDataHandler1.Group[] items = view.getGroups();

        for (int j = 0; j < keyArray.length; j++) {

            //Create the check boxes.
            checkButton[j] = new JCheckBox();

             checkButton[j].setBounds(HPadding, v, SwatchWidth, SwatchHeight+6);
           checkButton[j].setEnabled(false);

            for (int m = 0; m < items.length; m++) {

                if (items[m].key.equalsIgnoreCase(keyArray[j])) {

                    Color cl = Color.decode(items[m].color);

                    checkButton[j].setBackground(cl);
                    add(checkButton[j]);


            gbi.setColor(TextColor);

            gbi.drawString(keyArray[j], SwatchWidth + 4 * HPadding, v + SwatchHeight);

                }
            }

           v += SwatchHeight + 4 * VPadding;
        }

        }


        // put "MISSING VALUE" , "NOT APPLICABLE" at the end of the legend list.

        ArrayList<String> sList = new ArrayList<String>();
        ArrayList<String> allspeList = new ArrayList<String>();
        boolean hasMissing = false;
        boolean notApplicable = false;
        for (String attri : shapeMap.keySet()) {
            if (!attri.equals("MISSING VALUE") && !attri.equals("NOT APPLICABLE"))
                sList.add(attri);
            else if (attri.equals("MISSING VALUE"))
                hasMissing = true;
            else if (attri.equals("NOT APPLICABLE"))
                notApplicable = true;
        }

        String[] sarray = sList.toArray(new String[sList.size()]);
        Arrays.sort(sarray);

        for (int j = 0; j < sarray.length; j++) {
            allspeList.add(sarray[j]);
        }
        if (hasMissing)
            allspeList.add("MISSING VALUE");
        if (notApplicable)
            allspeList.add("NOT APPLICABLE");
        if (shapeActivated) {
            gbi.setFont(FontLib.getFont("SansSerif", Font.BOLD, 12));
            gbi.drawString("Shaped by " + shapeBy + ":", HPadding, v + SwatchHeight);
            v += SwatchHeight + 4 * VPadding;

            for (String attri : allspeList) {
                 if(defaultlegend || (!defaultlegend && shapeAttris.contains(attri))){

                int stype = Integer.parseInt(shapeMap.get(attri));


                /*         public static final int SHAPE_RECTANGLE      = 0;
                        /** Ellipse/Circle shape */
                //  public static final int SHAPE_ELLIPSE        = 1;
                /** Diamond shape */
                //  public static final int SHAPE_DIAMOND        = 2;
                /** Cross shape */
                //  public static final int SHAPE_CROSS          = 3;
                /** Star shape */
                //  public static final int SHAPE_STAR           = 4;
                /** Up-pointing triangle shape */
                //  public static final int SHAPE_TRIANGLE_UP    = 5;
                //        int = 6 for rectangle
                //  int = 7 for  elipse
                /** Hexagon shape */
                //  public static final int SHAPE_HEXAGON        = 9;
                /** The number of recognized shape types */
                gbi.setFont(FontLib.getFont("SansSerif", Font.PLAIN, 12));
                if (stype == 0) {
                    gbi.draw(new Rectangle2D.Double(HPadding + 3, v, SwatchWidth - 8, SwatchWidth - 8));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);

                } else if (stype == 1) {
                    gbi.draw(new Ellipse2D.Double(HPadding + 3, v, SwatchWidth - 7, SwatchWidth - 7));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 2) {
                    gbi.draw(new GeneralPath(diamond((float) (HPadding + 1), (float) v, (float) (SwatchWidth - 8))));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 3) {
                    gbi.draw(new GeneralPath(cross((float) HPadding + 3, (float) v, (float) (SwatchWidth - 8))));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 4) {
                    gbi.draw(new GeneralPath(star((float) HPadding + 3, (float) v, (float) (SwatchWidth - 8))));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);

                } else if (stype == 5) {
                    gbi.draw(new GeneralPath(triangle_up((float) HPadding + 3, (float) v, (float) (SwatchWidth - 8))));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 6) {
                    gbi.draw(new Ellipse2D.Double(HPadding + 1, v, SwatchWidth - 4, SwatchWidth - 9));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 7) {
                    gbi.draw(new Rectangle2D.Double(HPadding + 1, v, SwatchWidth - 5, SwatchWidth - 10));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 8) {
                    gbi.draw(new GeneralPath(trapezoid((float) HPadding + 3, (float) v, (float) (SwatchWidth - 8))));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                } else if (stype == 9) {
                    gbi.draw(new GeneralPath(hexagon((float) HPadding + 3, (float) v, (float) (SwatchWidth - 8))));
                    gbi.drawString(attri, SwatchWidth + 4 * HPadding, v + SwatchHeight);
                }
                v += SwatchHeight + 4 * VPadding;
                }
            }
        }
        gbi.setFont(FontLib.getFont("SansSerif", Font.BOLD, 12));
        gbi.drawString("Colored by " + colorBy + ":", HPadding, v + SwatchHeight);
        gbi.setFont(FontLib.getFont("SansSerif", Font.PLAIN, 12));
        v += SwatchHeight + 4 * VPadding;

        for (int i = 0; i < items.length; ++i) {

            if(defaultlegend || (!defaultlegend && nodeTypes.contains((items[i].key)))){
                if (items[i].color.contains("UNDERLINE") || items[i].color.contains("ITALIC") || items[i].color.contains("BOLD"))
                    continue;

            Color c;
            c = Color.decode(items[i].color);
            if (nodeButtonName != null) {
                if (items[i].key.equals(nodeButtonName))

                    c = currentColor;
            }

            // button

            JButton button = new JButton();

            button.setBounds(HPadding, v, SwatchWidth, SwatchHeight);
            if(items.length == 1 && items[i].key.equals("unknown"))
            button.setToolTipText("Click to change the color");

            button.setActionCommand(items[i].key);
            button.setActionCommand("nodeLegend");
            button.setName(items[i].key);
           button.setBackground(c);
            button.addActionListener(this);
            add(button);


            gbi.setColor(TextColor);

            gbi.drawString(items[i].key, SwatchWidth + 4 * HPadding, v + SwatchHeight);
            v += SwatchHeight + 4 * VPadding;

            n_ColorType.put(c, items[i].key);
            }
        }

        // linkLegend

        Graphics2D gbi2 = (Graphics2D) g;


        for (int i = 0; i < linkItems.length; ++i) {

             if(defaultlegend || (!defaultlegend && edgeTypes.contains((linkItems[i].key)))){
            if (linkItems[i].color.contains("UNDERLINE") || linkItems[i].color.contains("ITALIC") || linkItems[i].color.contains("BOLD"))
                continue;

            Color c;
            c = Color.decode(linkItems[i].color);

            if (linkButtonName != null) {
                if (linkItems[i].key.equals(linkButtonName))

                    c = currentColor;
            }

            JButton button = new JButton(getLinkLabel(c));

            button.setBorderPainted(false);
            button.setFocusable(false);
            button.setBounds(HPadding - 18, v, SwatchWidth + 19, SwatchHeight);
            button.setToolTipText("Click to change the color");
            button.setActionCommand(linkItems[i].key);
            button.setActionCommand("linkLegend");
            button.setName(linkItems[i].key);
            button.setBackground(Color.WHITE);
            button.setForeground(c);
            button.addActionListener(this);
            add(button);

            gbi2.setColor(TextColor);
            gbi2.drawString(linkItems[i].key, SwatchWidth + 4 * HPadding, v + SwatchHeight);
            v += SwatchHeight + 4 * VPadding;

            e_ColorType.put(c, linkItems[i].key);
             }

        }

        if (message != null)

        {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        message.wait(500000);
                    } catch (Exception e) {

                    }
                    message.dispose();
                }
            });
        }
    }

    private String getLinkLabel(Color c) {

        String hexStr = "#" + Integer.toHexString(c.getRGB()).substring(2);

        String linkshape = "<html><FONT COLOR=\"" + hexStr + "\">" + jaString + "</FONT></html>";

        return linkshape;

    }

    public void actionPerformed(ActionEvent e) {
        currentColor = null;
         JPanel bannerPanel = new JPanel(new BorderLayout());

                bannerPanel.add(new JLabel("                 "), BorderLayout.NORTH);
                    bannerPanel.add(label, BorderLayout.CENTER);
             bannerPanel.add(new JLabel("                 "), BorderLayout.SOUTH);
                    bannerPanel.setBorder(BorderFactory.createTitledBorder("Preview"));

       colorChooser.getSelectionModel().addChangeListener(this);

        if ("nodeLegend".equals(e.getActionCommand())) {
            JButton button = (JButton) e.getSource();
            this.setNodeButtonName(button.getName());
            this.setLinkButtonName(null);
            dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler

            dialog.setSize(500, 350);
            dialog.add(bannerPanel, BorderLayout.EAST);

            //The user has clicked the cell, so
            //bring up the dialog.
            if (currentColor != null)
            colorChooser.setColor(currentColor);
            else
            colorChooser.setColor(button.getBackground());
            dialog.setVisible(true);
            if (currentColor != null)
                button.setBackground(currentColor);

        } else if ("linkLegend".equals(e.getActionCommand())) {
            JButton button = (JButton) e.getSource();
            this.setLinkButtonName(button.getName());
            this.setNodeButtonName(null);
            dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler
            dialog.setSize(500, 350);
                       dialog.add(bannerPanel, BorderLayout.EAST);

            //The user has clicked the cell, so
            //bring up the dialog.

            if (currentColor != null)
            colorChooser.setColor(currentColor);
            else
              colorChooser.setColor(button.getForeground());

            dialog.setVisible(true);
            if (currentColor != null) {
                button.setText(getLinkLabel(currentColor));
            }


        } else if ("OK".equals(e.getActionCommand())) { //User pressed dialog's "OK" button.

            nodeButtonName = this.getNodeBuutonName();
            linkButtonName = this.getLinkBuutonName();
            currentColor = colorChooser.getColor();

            view.updateColor(currentColor, nodeButtonName, linkButtonName);

        } else if ("Cancel".equals(e.getActionCommand())) { //User pressed dialog's "OK" button.

            dialog.dispose();
        }
    }


     public void stateChanged(ChangeEvent e) {
        Color newColor =  colorChooser.getColor();
        label.setBackground(newColor);
    }

    public Map getE_ColorType() {

        return e_ColorType;
    }


    public Map getN_ColorType() {

        return n_ColorType;
    }

    public int getLegendSize() {
        return legendSize;
    }

    public boolean hScrollable() {

        return hScrollable;
    }

    /**
     * figure out the size of the legend
     */
    private void calcWidth() {
        int width = app_width;
        int height = SwatchHeight + 2 * VPadding;
    }

    /**
     * Returns a up-pointing triangle of the given dimenisions.
     */
    public Shape triangle_up(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, y + height);
        m_path.lineTo(x + height / 2, y);
        m_path.lineTo(x + height, (y + height));
        m_path.closePath();
        return m_path;
    }


    /**
     * Returns a cross shape of the given dimenisions.
     */
    public Shape cross(float x, float y, float height) {
        float h14 = 3 * height / 8, h34 = 5 * height / 8;
        m_path.reset();
        m_path.moveTo(x + h14, y);
        m_path.lineTo(x + h34, y);
        m_path.lineTo(x + h34, y + h14);
        m_path.lineTo(x + height, y + h14);
        m_path.lineTo(x + height, y + h34);
        m_path.lineTo(x + h34, y + h34);
        m_path.lineTo(x + h34, y + height);
        m_path.lineTo(x + h14, y + height);
        m_path.lineTo(x + h14, y + h34);
        m_path.lineTo(x, y + h34);
        m_path.lineTo(x, y + h14);
        m_path.lineTo(x + h14, y + h14);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a star shape of the given dimenisions.
     */
    public Shape star(float x, float y, float height) {
        float s = (float) (height / (2 * Math.sin(Math.toRadians(54))));
        float shortSide = (float) (height / (2 * Math.tan(Math.toRadians(54))));
        float mediumSide = (float) (s * Math.sin(Math.toRadians(18)));
        float longSide = (float) (s * Math.cos(Math.toRadians(18)));
        float innerLongSide = (float) (s / (2 * Math.cos(Math.toRadians(36))));
        float innerShortSide = innerLongSide * (float) Math.sin(Math.toRadians(36));
        float innerMediumSide = innerLongSide * (float) Math.cos(Math.toRadians(36));

        m_path.reset();
        m_path.moveTo(x, y + shortSide);
        m_path.lineTo((x + innerLongSide), (y + shortSide));
        m_path.lineTo((x + height / 2), y);
        m_path.lineTo((x + height - innerLongSide), (y + shortSide));
        m_path.lineTo((x + height), (y + shortSide));
        m_path.lineTo((x + height - innerMediumSide), (y + shortSide + innerShortSide));
        m_path.lineTo((x + height - mediumSide), (y + height));
        m_path.lineTo((x + height / 2), (y + shortSide + longSide - innerShortSide));
        m_path.lineTo((x + mediumSide), (y + height));
        m_path.lineTo((x + innerMediumSide), (y + shortSide + innerShortSide));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a hexagon shape of the given dimenisions.
     */
    public Shape hexagon(float x, float y, float height) {
        float width = height / 2;

        m_path.reset();
        m_path.moveTo(x, y + 0.5f * height);
        m_path.lineTo(x + 0.5f * width, y);
        m_path.lineTo(x + 1.5f * width, y);
        m_path.lineTo(x + 2.0f * width, y + 0.5f * height);
        m_path.lineTo(x + 1.5f * width, y + height);
        m_path.lineTo(x + 0.5f * width, y + height);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a diamond shape of the given dimenisions.
     */
    public Shape diamond(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, (y + 0.5f * height));
        m_path.lineTo((x + 0.8f * height), y);
        m_path.lineTo((x + 1.6f * height), (y + 0.5f * height));
        m_path.lineTo((x + 0.8f * height), (y + height));
        m_path.closePath();
        return m_path;
    }

    public Shape trapezoid(float x, float y, float height) {
        m_path.reset();
        m_path.moveTo(x, y + height);
        m_path.lineTo((x + 1.2f * height), y + height);
        m_path.lineTo((x + 0.9f * height), (y + 0.2f * height));
        m_path.lineTo((x + 0.3f * height), (y + 0.2f * height));
        m_path.closePath();
        return m_path;
    }

    private ImageIcon getImage(String filename) {
        Class thisClass = getClass();

        java.net.URL url = thisClass.getResource("images/" + filename);
        if (url == null) {
            System.out.println("couldn't load image " + filename);
            return null;
        }
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage(url);

        return new ImageIcon(img);
    }


    public String getNodeBuutonName() {

        return nodeButtonName;
    }

    public void setNodeButtonName(String _name) {
        nodeButtonName = _name;
    }

    public String getLinkBuutonName() {

        return linkButtonName;
    }

    public void setLinkButtonName(String _name) {
        linkButtonName = _name;
    }

}
