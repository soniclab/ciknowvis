package admin;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.*;
import java.util.Map;
import java.util.List;
import java.io.*;
import javax.swing.*;
import javax.swing.text.html.parser.ParserDelegator;
import javax.help.*;

import dialog.*;
import action.*;
import visual.*;
import data.*;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Edge;
import ciknow.dao.DataSender;
import ciknow.dao.GraphData;

public class MainFrame extends JApplet {


    private JToggleButton labelButton, strengthButton, minimizeNodesButton, imageNodesButton, shapeNodesButton, resizeNodesButton, minimizeEdgesButton,
            initialViewButton, zoomInButton, zoomButton, zoomOutButton, zoomInButton1, zoomOutButton1, panButton;
    private JButton recAttriButton, selClampButton, circleButton, randomButton, annealButton, treeButton, groupButton, customButton, showallButton, edgeButton, nodeButton, shapeButton, limitButton,
            isolateButton, hideselButton, fitButton, recButton, whyButton, depthButton;
    private JToolBar tb;
    private JMenuItem showLabel, saveData, sendMail, saveImage, setting, circle, random, tree, group, customItem, showAll, nodetype, selectShape, hidesel, selClamp,
            hideiso, edge, limit, fit, miniNode, imageNode, shapeNode, depth, st, miniEdge, zoomItem, zoomInItem, zoomOutItem, panItem, zoomIn1Item, zoomOut1Item, search,
            nodeTypeLayout, attriLayout, resizeRecSize, desc, show, menuItem11, menuItem22, resizeImage;

    private JMenu resizeNode;
    private JMenuItem setNode, sizeNode;
    public boolean showInitialViewButton;
    private ExitAction exitAction;
    private AboutAction aboutAction;
    private DocAction docAction;

    private SelClampAction selClampAction;

    private MinimizeNodesAction minimizeNodesAction;
    private ImageNodesAction imageNodesAction;
    private ResizeNodesAction resizeNodesAction;
    private ShapeNodesAction shapeNodesAction;
    private MinimizeEdgesAction minimizeEdgesAction;
    private LabelAction labelAction;
    private StrengthAction strengthAction;
    private InitialViewAction initialViewAction;
    private RecAction recAction;
    private RecEdgeAction recEdgeAction;
    private WhyAction whyAction;
    private CircleAction circleAction;
    private CustomAction customAction;
    private TreeAction treeAction;
    private AggregateAction aggregateAction;
    private RandomAction randomAction;

    private FitAction fitAction;
    private HideselAction hideselAction;

    private EdgeAction EdgeAction;
    private NodeAction NodeAction;
    private SelectShapeAction seShapeAction;
    private HideisoAction hideisoAction;
    private ShowAllAction showAllAction;
    private LimitAction limitAction;
    private DepthAction depthAction;
    private FindAction findAction;
    private SaveGraphAction saveGraphAction;
    private SettingAction settingAction;
    private SaveDataAction saveDataAction;
    private SendMailAction sendMailAction;
    private OpenFileAction openAction;


    private PanAction panAction;
    private ZoomInAction zoomInAction;
    private ZoomAction zoomAction;
    private ZoomOutAction zoomOutAction;

    private ZoomInAction1 zoomInAction1;
    private ZoomOutAction1 zoomOutAction1;

    private ShowPathAction showPAction;

    private ItemShowStrengthAction iSSAction;
    private ItemShowWeightAction iSWAction;

    private ItemImageNodeAction iImageNodeAction;
    private ItemMiniNodeAction iMiniNodeAction;
    private ItemLabelAction iLabelAction;
    private ItemRecAttriAction iRecAttriAction;
    private ItemResizeNodeAction iResizeNodeAction;
    private ItemResizeSetAction iResizeSetAction;
    private ItemShapeNodeAction iShapeNodeAction;
    private ItemZoomInAction iZoomInAction;
    private ItemZoomAction iZoomAction;
    private ItemZoomOutAction iZoomOutAction;
    private ItemRecSizeAction iRecSizeAction;
    private ItemImageSizeAction iImageSizeAction;
    private ItemZoomInAction1 iZoomInAction1;
    private ItemZoomOutAction1 iZoomOutAction1;

    private ItemPanAction iPanAction;

    private LimitDialog limitD = null;
    private SaveDataDialog saveD = null;
    private EmailDialog mailD = null;
    private WhyDialog whyD = null;
    private AboutDialog aboutD = null;
    private EdgeDialog edgeD = null;
    private NodeDialog nodeD = null;
    private ShapeDialog shapeD = null;
    private ProcessingMessage messageD = null;

    private ClusteringDialog clusteringD = null;
    private WeightNormalizedDialog wNormalizedD = null;
    private NormalizedDialog normalizedD = null;
    private NormalizedDialog2 normalizedD2 = null;
    private ImageDialog imageD = null;
    private NodePropertyMap propertyD = null;
    private GraphView gv;

    private AppletDataHandler1 dh1;

    private Cursor zoomIn;
    private Cursor zoomOut;
    private Cursor zoom;

    private Cursor zoomIn1;
    private Cursor zoomOut1;

    private Cursor pan;
    private JMenu recMenu;

    private boolean treeActivated = false;
    private boolean clusteringActivated = false;
    private boolean isCustomlayout = false;
    private int recStatus;
    private final String DefaultIKNOWTitle = "I-KNOW Knowledge Network";
    private ResourceBundle rb;
    // public  JLabel loadStatus;
    /**
     * the window object
     */
    private MainFrame frame;
    private JFrame jframe;
    /**
     * a flag indicating whether the visualizer is running
     * as an applet
     */
    public static boolean isApplet = true;

    /**
     * object which handles all data loading.
     */
    private Map<String, List<String>> nodeAttri;
    private List<String> nodeProperties;
    private AppletDataHandler1 dataHandler1;
    private Display display;
    private Graph g;
    private String sizeLabel, size2Label, shapeLabel;
    private Map<String, String> htmlParas;
    public boolean isGraphML;
    public int framewidth, frameheight;
    private Dimension frameExtents;
    final static String IMAGEPANEL = "Card with Logo";
    final static String GRAPHPANEL = "Card with graph";
    private JPanel parentDisPane;
    private FileReader reader;
    private String language = "";

    /**
     * Construct the frame
     */

    public void init() {

        //setIconImage(Toolkit.getDefaultToolkit().createImage("[Icon]"));
        // get screen and frame dimensions, then compare them to make sure
        // the frame will fit on the screen

        Dimension screenExtents = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        frameExtents = new Dimension(getWidth(), getHeight());

        if (frameExtents.width > screenExtents.width)
            frameExtents.width = screenExtents.width;
        if (frameExtents.height > screenExtents.height)
            frameExtents.height = screenExtents.height;
        framewidth = frameExtents.width;
        frameheight = frameExtents.height;

        ParserDelegator workaround = new ParserDelegator();

        // select the Java look and feel
        try {
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        }
        if (rb == null) {
            try {

                try {

                    URL servlet = new URL(this.getCodeBase().toString());
                    URL dataStream = new URL(servlet + "la.txt");
                    BufferedReader in = new BufferedReader(new InputStreamReader(dataStream.openStream()));

                    language = in.readLine();

                    if (language.length() == 0) {

                        // language = this.getParameter("language");
                        language = "US";
                    }

                    //  inputStream.close();

                } catch (IOException e) {

                    System.out.println("IOException when read la file");
                    //  e.printStackTrace();
                    //  language = this.getParameter("language");
                    language = "US";
                }

                if (language.equals("CN"))
                    rb = ResourceBundle.getBundle("vis", Locale.CHINA);
                else if (language.equals("DE"))
                    rb = ResourceBundle.getBundle("vis", Locale.GERMANY);
                else if (language.equals("US"))
                    rb = ResourceBundle.getBundle("vis", Locale.US);

            } catch (MissingResourceException mre) {
                mre.printStackTrace();
            }
        }
        // create all the widgets
        System.out.println("Building user interface");


        parentDisPane = new JPanel(new CardLayout());
        JPanel gvPanel = new JPanel(new GridLayout(3, 1));
        if (isApplet) {
            visualGraph(null);

            parentDisPane.add(gv, GRAPHPANEL);
        } else {
            zoomCursor();

            openAction = new OpenFileAction(this);     // to loadData
            exitAction = new ExitAction();
            aboutAction = new AboutAction(this);
            docAction = new DocAction(this);
            //      loadStatus = new JLabel("");
            //     JPanel message = new JPanel();

            //     message.add(loadStatus);

            gvPanel.add(new JPanel());
            gvPanel.add(new JLabel(getImage("SONICWhite.jpg")));
            gvPanel.add(new JPanel());
            //     gvPanel.add( message);
            parentDisPane.add(gvPanel, IMAGEPANEL);
        }

        JPanel pane = new JPanel();
        BorderLayout bord = new BorderLayout();
        pane.setLayout(bord);

        pane.setPreferredSize(frameExtents);
        setJMenuBar(createMenuBar());
        pane.add(createToolBar(), BorderLayout.NORTH);
        pane.add(parentDisPane, BorderLayout.CENTER);

        setContentPane(pane);

        this.setFocusable(true);


        setVisible(true);

        if (isApplet)
            ((CardLayout) parentDisPane.getLayout()).show(parentDisPane, GRAPHPANEL);
        else
            ((CardLayout) parentDisPane.getLayout()).show(parentDisPane, IMAGEPANEL);
        System.out.println("UI ok, going to window");
    }


    public void frontPage() {
        ((CardLayout) parentDisPane.getLayout()).show(parentDisPane, IMAGEPANEL);

    }

    private boolean visualGraph(FileReader reader) {
        boolean isDataValid = true;
        String xStr = null;
        String recStatusStr = null;
        if (isApplet) {

                if (this.getParameter("graphML") != null) {
                    // applet for clicking html file
                    readXML(this.getParameter("graphML"));
                }
                 try {
                dh1 = new AppletDataHandler1(this, null, null);

                g = dh1.getInitGraph();
                } catch (Exception e) {
                isDataValid = false;
                System.out.println("The Data Is Invalid! " + e);

              //  JOptionPane.showMessageDialog(this, "The Data Is Invalid!", "Data Open Error",
               //      JOptionPane.ERROR_MESSAGE);

            }
                recStatusStr = this.getParameter("title");
                  if (this.getParameter("x") != null) {
                try {
                    xStr = this.getParameter("x");
                } catch (Exception e) {
                     System.out.println("Exception when reading node location: " + e);
                }
                  }
        } else {
            if (reader.isHtml) {
                JOptionPane.showMessageDialog(jframe, "Not Supported Data Format!", "Data Open Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try {

                    dh1 = new AppletDataHandler1(this, htmlParas, reader);

                    g = dh1.getInitGraph();

                    recStatusStr = htmlParas.get("title");
                    try {
                        xStr = htmlParas.get("x");
                    } catch (Exception e) {

                    }

                    saveData.setEnabled(true);

                    saveImage.setEnabled(true);
                    if (isGraphML)
                        setting.setEnabled(true);

                    frameExtents.width = 1200;
                    frameExtents.height = 800;

                    jframe.setTitle("C-IKNOW Visualizer -- " + reader.selectedFile());
                } catch (Exception e) {
                    isDataValid = false;
                    if (!reader.getDataStatus()) {
                        JOptionPane.showMessageDialog(jframe, "No Valid Data!", "Data Open Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    System.out.println("Error when creating graph: " + e);
                }
            }
        }

        if (isDataValid) {
            recStatus = 0;

            if (recStatusStr.startsWith("Recommendation"))
                recStatus = 1;

            else if (recStatusStr.startsWith("RecScores"))
                recStatus = 2;

            if (xStr != null)
                isCustomlayout = true;


            System.out.println(" graph file loaded");


            gv = new GraphView(rb, g, "label", "disLabel", dh1, this, frameExtents.width, frameExtents.height, recStatus, isCustomlayout, null, reader);

            sizeLabel = dh1.getSizeLabel();
            size2Label = dh1.getSize2Label();

            zoomCursor();

            openAction = new OpenFileAction(this);

            exitAction = new ExitAction();
            aboutAction = new AboutAction(this);
            docAction = new DocAction(this);


            initActions();

            gv.setFocusable(true);
            gv.fitGraph();
            gv.requestFocusInWindow();


            display = gv.getDisplay();

        }
        return isDataValid;
    }

    private void initActions() {
        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2"))
                minimizeNodesAction = new MinimizeNodesAction(gv, dh1);
        } else {
            minimizeNodesAction = new MinimizeNodesAction(gv, dh1);
        }
        imageNodesAction = new ImageNodesAction(gv, dh1);
        if (size2Label != null)
            resizeNodesAction = new ResizeNodesAction(gv, this, dh1);
        else
            resizeNodesAction = new ResizeNodesAction(gv, this, dh1);

        shapeNodesAction = new ShapeNodesAction(gv, this, dh1);
        minimizeEdgesAction = new MinimizeEdgesAction(gv, this);
        labelAction = new LabelAction(gv);
        selClampAction = new SelClampAction(gv);
        EdgeAction = new EdgeAction(this);
        NodeAction = new NodeAction(this);
        seShapeAction = new SelectShapeAction(this);
        hideisoAction = new HideisoAction(gv);
        strengthAction = new StrengthAction(gv);
        initialViewAction = new InitialViewAction(gv);

        recAction = new RecAction(gv);
        recEdgeAction = new RecEdgeAction(gv);
        whyAction = new WhyAction(this);
        circleAction = new CircleAction(gv);
        customAction = new CustomAction(gv, dh1.hiddenItemsExist());
        aggregateAction = new AggregateAction(gv, this);
        treeAction = new TreeAction(gv);
        randomAction = new RandomAction(gv);
        fitAction = new FitAction(gv);
        hideselAction = new HideselAction(gv);
        showAllAction = new ShowAllAction(gv);

        saveGraphAction = new SaveGraphAction(gv);

        settingAction = new SettingAction(this);

        saveDataAction = new SaveDataAction(this, gv, isApplet);
        sendMailAction = new SendMailAction(this, gv, isApplet);
        showPAction = new ShowPathAction(this);

        iSSAction = new ItemShowStrengthAction(this);
        iSWAction = new ItemShowWeightAction(this);
        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2"))
                iMiniNodeAction = new ItemMiniNodeAction(this);
        } else
            iMiniNodeAction = new ItemMiniNodeAction(this);
        iImageNodeAction = new ItemImageNodeAction(this);
        iLabelAction = new ItemLabelAction(this);
        iRecAttriAction = new ItemRecAttriAction(this);
        iRecSizeAction = new ItemRecSizeAction(gv);
        iImageSizeAction = new ItemImageSizeAction(gv);
        iResizeSetAction = new ItemResizeSetAction(this);
        iResizeNodeAction = new ItemResizeNodeAction(this);
        iShapeNodeAction = new ItemShapeNodeAction(this);
        iZoomInAction = new ItemZoomInAction(this);
        iZoomAction = new ItemZoomAction(this);
        iZoomOutAction = new ItemZoomOutAction(this);

        iZoomInAction1 = new ItemZoomInAction1(this);
        iZoomOutAction1 = new ItemZoomOutAction1(this);

        iPanAction = new ItemPanAction(this);

        panAction = new PanAction(gv, this);
        zoomInAction = new ZoomInAction(gv, this);
        zoomAction = new ZoomAction(gv, this);
        zoomOutAction = new ZoomOutAction(gv, this);

        zoomInAction1 = new ZoomInAction1(gv, this);

        zoomOutAction1 = new ZoomOutAction1(gv, this);


        limitAction = new LimitAction(this);
        depthAction = new DepthAction(gv);
        findAction = new FindAction(gv);
    }


    private JMenu createFileJMenu() {

        //JMenu file = new JMenu("File");
        JMenu file = new JMenu(rb.getString("File"));

        if (!isApplet) {
            JMenuItem openfile = file.add(openAction);
            openfile.setText(rb.getString("OpenGraphFile") + "...");
            file.add(openfile);

        }

        saveData = new JMenuItem();


        if (isApplet) {
            sendMail = new JMenuItem();
            saveData.addActionListener(saveDataAction);
            sendMail.addActionListener(sendMailAction);
            sendMail.setText("Send the visualization to others...");
        }
        saveData.setText(rb.getString("SaveGraphData") + "...");

        if (!isApplet) {
            saveData.setEnabled(false);
        } else if (this.getCodeBase().toString().equals("http://ciknow.northwestern.edu/downloads/vis/")) {
            saveData.setEnabled(false);
            sendMail.setEnabled(false);
        }
        {

        }
        file.add(saveData);
        if (isApplet)
            file.add(sendMail);
        if (!isApplet) {

            saveImage = new JMenuItem();

            saveImage.setText(rb.getString("SaveGraphImage") + "...");
            saveImage.setEnabled(false);
            file.add(saveImage);

            setting = new JMenuItem();

            setting.setText(rb.getString("SettingGraph") + "...");
            setting.setEnabled(false);
            file.add(setting);


            JMenuItem exit = file.add(exitAction);
            exit.setText(rb.getString("Exit"));
            exit.setMnemonic('X');
            exit.setAccelerator(
                    KeyStroke.getKeyStroke('X', java.awt.Event.SHIFT_MASK, false));

            file.add(exit);
        }


        return file;
    }

    // Create the edit menu
    private JMenu createEditJMenu() {
        JMenu edit = new JMenu(rb.getString("EditNetwork"));

        // node menuitems
        JMenu node = new JMenu(rb.getString("EditNode"));

        showAll = new JMenuItem();
        showAll.addActionListener(showAllAction);
        showAll.setText(rb.getString("ShowAllNodes/edges"));
        showAll.setAccelerator(
                KeyStroke.getKeyStroke('A', java.awt.Event.CTRL_MASK, false));

        hidesel = new JMenuItem();
        hidesel.addActionListener(hideselAction);
        hidesel.setText(rb.getString("HideSelection"));
        hidesel.setAccelerator(
                KeyStroke.getKeyStroke('H', java.awt.Event.CTRL_MASK, false));

        selClamp = new JMenuItem();
        selClamp.addActionListener(selClampAction);
        selClamp.setText(rb.getString("ShowSelection"));
        selClamp.setAccelerator(
                KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK, false));


        nodetype = new JMenuItem();
        nodetype.addActionListener(NodeAction);
        nodetype.setText(rb.getString("Hide/ShowSelectedNodeType/attribute") + "...");
        nodetype.setAccelerator(
                KeyStroke.getKeyStroke('N', java.awt.Event.CTRL_MASK, false));

        selectShape = new JMenuItem();
        selectShape.addActionListener(seShapeAction);
        selectShape.setText(rb.getString("Hide/ShowSelectedNodeShape") + "...");
        selectShape.setAccelerator(
                KeyStroke.getKeyStroke('H', java.awt.Event.CTRL_MASK, false));
        selectShape.setEnabled(false);

        hideiso = new JMenuItem();
        hideiso.addActionListener(hideisoAction);
        hideiso.setText(rb.getString("HideIsolateNode"));
        hideiso.setAccelerator(
                KeyStroke.getKeyStroke('Q', java.awt.Event.CTRL_MASK, false));

        edge = new JMenuItem();
        edge.addActionListener(EdgeAction);
        edge.setText(rb.getString("Hide/ShowSelectedEdgeType") + "...");
        edge.setAccelerator(
                KeyStroke.getKeyStroke('E', java.awt.Event.CTRL_MASK, false));

        limit = new JMenuItem();
        limit.addActionListener(limitAction);
        limit.setText(rb.getString("LimitEdgeStrengths") + "...");
        limit.setAccelerator(
                KeyStroke.getKeyStroke('L', java.awt.Event.CTRL_MASK, false));

        node.add(hidesel);
        node.add(selClamp);
        node.add(hideiso);

        edit.add(node);
        edit.add(nodetype);
        edit.add(edge);
        edit.add(selectShape);
        edit.add(limit);
        edit.add(showAll);
        if ((isApplet && isCustomlayout) || !isApplet) {
            customItem = new JMenuItem();
            if (!isApplet)
                customItem.setEnabled(false);
            customItem.addActionListener(customAction);
            customItem.setText(rb.getString("ResetToTheSavedNetwork"));
            customItem.setAccelerator(
                    KeyStroke.getKeyStroke('B', java.awt.Event.CTRL_MASK, false));
            edit.add(customItem);
        }
        return edit;
    }


    //Create layout menu
    private JMenu createLayoutJMenu() {
        JMenu view = new JMenu(rb.getString("Layout"));


        circle = new JMenuItem();
        circle.addActionListener(circleAction);
        circle.setText(rb.getString("PutNodesInCircle"));
        circle.setAccelerator(
                KeyStroke.getKeyStroke('C', java.awt.Event.CTRL_MASK, false));


        random = new JMenuItem();
        random.addActionListener(randomAction);
        random.setText(rb.getString("RandomlyScatterNodes"));

        random.setAccelerator(
                KeyStroke.getKeyStroke('R', java.awt.Event.CTRL_MASK, false));

        JMenu anneal = new JMenu();

        menuItem11 = new JMenuItem();
        menuItem11.setText(rb.getString("ClusteringWithoutEdgeWeight") + "...");
        if (isApplet)
            menuItem11.addActionListener(new AnnealAction(this, 1));

        anneal.add(menuItem11);

        menuItem22 = new JMenuItem();
        menuItem22.setText(rb.getString("ClusteringWithEdgeWeight") + "...");
        if (isApplet)
            menuItem22.addActionListener(new AnnealAction(this, 2));

        anneal.add(menuItem22);

        anneal.setText(rb.getString("AutomaticallyClusterNetwork"));
        //  anneal.setAccelerator(
        //         KeyStroke.getKeyStroke('U', java.awt.Event.CTRL_MASK, false));

        tree = new JMenuItem();
        tree.addActionListener(treeAction);
        tree.setText(rb.getString("PutNodesInRadialTree"));
        tree.setAccelerator(
                KeyStroke.getKeyStroke('T', java.awt.Event.CTRL_MASK, false));

        group = new JMenuItem();
        group.addActionListener(aggregateAction);
        group.setText(rb.getString("GroupNodesByAttribute"));
        group.setAccelerator(
                KeyStroke.getKeyStroke('G', java.awt.Event.CTRL_MASK, false));


        view.add(circle);
        view.add(random);
        view.add(anneal);
        view.add(tree);
        view.add(group);

        return view;

    }

    //Create display option  menu
    private JMenu createDisplayJMenu() {
        JMenu display = new JMenu(rb.getString("DisplayOptions"));

        fit = new JMenuItem();
        fit.addActionListener(fitAction);
        fit.setText(rb.getString("FitGraphToScreen"));

        fit.setAccelerator(
                KeyStroke.getKeyStroke('D', java.awt.Event.CTRL_MASK, false));

        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2")) {
                miniNode = new JMenuItem();
                miniNode.addActionListener(iMiniNodeAction);
                miniNode.setText(rb.getString("MinimizeNodes"));

                miniNode.setAccelerator(
                        KeyStroke.getKeyStroke('K', java.awt.Event.CTRL_MASK, false));
            }
        } else {
            miniNode = new JMenuItem();
            miniNode.addActionListener(iMiniNodeAction);
            miniNode.setText(rb.getString("MinimizeNodes"));

            miniNode.setAccelerator(
                    KeyStroke.getKeyStroke('K', java.awt.Event.CTRL_MASK, false));
        }
        imageNode = new JMenuItem();
        if (isApplet)
            imageNode.addActionListener(iImageNodeAction);

        imageNode.setText(rb.getString("ShowNodeImage"));
        imageNode.setAccelerator(
                KeyStroke.getKeyStroke('B', java.awt.Event.CTRL_MASK, false));

        resizeImage = new JMenuItem();
        resizeImage.setText("Resize image...");
        resizeImage.setAccelerator(
                KeyStroke.getKeyStroke('P', java.awt.Event.CTRL_MASK, false));
        if (isApplet) {
            if (getParameter("images").contains("1")) {
                imageNode.setEnabled(true);

                resizeImage.addActionListener(iImageSizeAction);
                if (gv.nodedisStatus() == 4)
                    resizeImage.setEnabled(true);
                else
                    resizeImage.setEnabled(false);
            } else {
                imageNode.setEnabled(false);
                imageNode.setVisible(false);
            }

        } else {
            imageNode.setEnabled(false);
            imageNode.setVisible(false);
            resizeImage.setEnabled(false);
            resizeImage.setVisible(false);
        }

        shapeNode = new JMenuItem();
        if (isApplet)
            shapeNode.addActionListener(iShapeNodeAction);
        shapeNode.setText(rb.getString("NodeShapeBy"));

        shapeNode.setAccelerator(
                KeyStroke.getKeyStroke('S', java.awt.Event.CTRL_MASK, false));

        if (recStatus != 2) {
            shapeNode.setEnabled(true);
        } else {
            shapeNode.setEnabled(false);
            shapeNode.setVisible(false);
        }
        shapeNode.setEnabled(true);

        resizeNode = new JMenu();
        sizeNode = new JMenuItem();
        setNode = new JMenuItem();
        if (isApplet) {
            sizeNode.addActionListener(iResizeNodeAction);
            setNode.addActionListener(iResizeSetAction);
        }
        resizeNode.setText("Resize nodes");

        sizeNode.setText("Toggle node size by default");
        sizeNode.setAccelerator(
                KeyStroke.getKeyStroke('F', java.awt.Event.CTRL_MASK, false));
        setNode.setText("Set node size...");
        setNode.setAccelerator(
                KeyStroke.getKeyStroke('Z', java.awt.Event.CTRL_MASK, false));

        if (isApplet) {
            if (dh1.getNodeSizeType() > 0 && recStatus != 2) {
                resizeNode.setEnabled(true);
                sizeNode.setEnabled(true);
                if (gv.nodedisStatus() == 3)
                    setNode.setEnabled(true);
                else
                    setNode.setEnabled(false);
            } else {
                resizeNode.setEnabled(false);
                resizeNode.setVisible(false);
                sizeNode.setEnabled(false);
                setNode.setEnabled(false);

            }
        } else {
            resizeNode.setEnabled(false);
            resizeNode.setVisible(false);
            sizeNode.setEnabled(false);
            sizeNode.setVisible(false);
            setNode.setEnabled(false);
            setNode.setVisible(false);

        }

        resizeNode.add(sizeNode);
        resizeNode.add(setNode);

        showLabel = new JMenuItem();
        showLabel.addActionListener(iLabelAction);
        showLabel.setText(rb.getString("Show/HiddenLabel"));
        showLabel.setAccelerator(
                KeyStroke.getKeyStroke('P', java.awt.Event.CTRL_MASK, false));
        if (recStatus == 2)
            showLabel.setEnabled(true);
        else
            showLabel.setEnabled(false);


        depth = new JMenuItem();
        depth.addActionListener(depthAction);
        depth.setText(rb.getString("NodeDepth") + "...");

        depth.setAccelerator(
                KeyStroke.getKeyStroke('Y', java.awt.Event.CTRL_MASK, false));


        st = new JMenuItem();
        st.addActionListener(iSSAction);
        st.setText(rb.getString("ShowEdgeStrengths"));

        st.setAccelerator(
                KeyStroke.getKeyStroke('J', java.awt.Event.CTRL_MASK, false));

        miniEdge = new JMenuItem();
        miniEdge.addActionListener(iSWAction);
        miniEdge.setText(rb.getString("WeightEdges"));

        miniEdge.setAccelerator(
                KeyStroke.getKeyStroke('W', java.awt.Event.CTRL_MASK, false));

        display.add(fit);
        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2"))
                display.add(miniNode);
            if (getParameter("images").contains("1"))
                display.add(resizeImage);
        } else
            display.add(miniNode);
        display.add(resizeImage);
        display.add(imageNode);
        display.add(shapeNode);
        display.add(resizeNode);
        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2"))
                display.add(showLabel);
        } else
            display.add(showLabel);
        display.add(depth);
        display.add(st);
        display.add(miniEdge);
        return display;

    }


    //Create view  menu
    private JMenu createViewJMenu() {
        JMenu view = new JMenu(rb.getString("View"));

        zoomItem = new JMenuItem();
        zoomItem.addActionListener(iZoomAction);
        zoomItem.setText(rb.getString("ZoomToSelection"));
        zoomItem.setAccelerator(
                KeyStroke.getKeyStroke('Z', java.awt.Event.CTRL_MASK, false));

        zoomInItem = new JMenuItem();
        zoomInItem.addActionListener(iZoomInAction);
        zoomInItem.setText(rb.getString("ZoomInOnEdges"));
        zoomInItem.setMnemonic('I');
        zoomInItem.setAccelerator(
                KeyStroke.getKeyStroke('I', java.awt.Event.CTRL_MASK, false));

        zoomOutItem = new JMenuItem();
        zoomOutItem.addActionListener(iZoomOutAction);
        zoomOutItem.setText(rb.getString("ZoomOutOnEdges"));
        zoomOutItem.setMnemonic('O');
        zoomOutItem.setAccelerator(
                KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));


        zoomIn1Item = new JMenuItem();
        zoomIn1Item.addActionListener(iZoomInAction1);
        zoomIn1Item.setText(rb.getString("ZoomInOnGraph"));
        zoomIn1Item.setMnemonic('I');
        zoomIn1Item.setAccelerator(
                KeyStroke.getKeyStroke('I', java.awt.Event.SHIFT_MASK, false));

        zoomOut1Item = new JMenuItem();
        zoomOut1Item.addActionListener(iZoomOutAction1);
        zoomOut1Item.setText(rb.getString("ZoomOutOnGraph"));
        zoomOut1Item.setMnemonic('O');
        zoomOut1Item.setAccelerator(
                KeyStroke.getKeyStroke('O', java.awt.Event.SHIFT_MASK, false));


        panItem = new JMenuItem();
        panItem.addActionListener(iPanAction);
        panItem.setText(rb.getString("MoveGraph"));

        panItem.setAccelerator(
                KeyStroke.getKeyStroke('M', java.awt.Event.CTRL_MASK, false));

        search = new JMenuItem();
        search.addActionListener(findAction);
        search.setText(rb.getString("Find") + "...");
        search.setMnemonic('F');
        search.setAccelerator(
                KeyStroke.getKeyStroke('F', java.awt.Event.CTRL_MASK, false));

        view.add(zoomItem);
        view.add(zoomInItem);
        view.add(zoomOutItem);
        view.add(zoomIn1Item);
        view.add(zoomOut1Item);
        view.add(panItem);
        view.add(search);
        return view;

    }


    //Create display option  menu
    private JMenu createRecommJMenu(int recFormat) { // recFormat = 1 for path  recommentation, recFormat =2 for circle recommentation
        recMenu = new JMenu(rb.getString("Recommendation"));
        if (recFormat == 1 || recFormat == 2) {
            if (recFormat == 1) {
                nodeTypeLayout = new JMenuItem();

                nodeTypeLayout.addActionListener(recAction);
                nodeTypeLayout.setText(rb.getString("SHowRecommendationPath"));

                nodeTypeLayout.setAccelerator(
                        KeyStroke.getKeyStroke('S', java.awt.Event.SHIFT_MASK, false));
                recMenu.add(nodeTypeLayout);
            } else if (recFormat == 2) {
                attriLayout = new JMenuItem();

                attriLayout.addActionListener(iRecAttriAction);

                attriLayout.setText(rb.getString("SHowRecommendationLayer"));

                attriLayout.setAccelerator(
                        KeyStroke.getKeyStroke('S', java.awt.Event.SHIFT_MASK, false));
                recMenu.add(attriLayout);
                resizeRecSize = new JMenuItem();

                resizeRecSize.addActionListener(iRecSizeAction);

                resizeRecSize.setText("Resize recommendation" + "...");

                resizeRecSize.setAccelerator(
                        KeyStroke.getKeyStroke('R', java.awt.Event.SHIFT_MASK, false));
                recMenu.add(resizeRecSize);

            }

            desc = new JMenuItem();

            desc.addActionListener(whyAction);
            desc.setText(rb.getString("RecommendationDescription"));
            desc.setAccelerator(
                    KeyStroke.getKeyStroke('W', java.awt.Event.SHIFT_MASK, false));
            recMenu.add(desc);

            if ((dh1 != null && dh1.hiddenItemsExist())) {

                show = new JMenuItem();
                show.addActionListener(showPAction);

                if (recFormat == 1) {

                    show.setText(rb.getString("Show/HideNextLevelPath"));

                } else {

                    show.setText("Show/Hide other relations");

                }

                show.setAccelerator(
                        KeyStroke.getKeyStroke('P', java.awt.Event.SHIFT_MASK, false));

                recMenu.add(show);
            }
        } else {
            recMenu.setEnabled(false);
            recMenu.setVisible(false);
        }
        return recMenu;

    }

    //Create help menu
    private JMenu createHelpJMenu() {
        JMenu help = new JMenu(rb.getString("Help"));


        JMenuItem menHelpUser = help.add(docAction);
        menHelpUser.setText(rb.getString("UserGuide"));

        JMenuItem menHelpAbout = help.add(aboutAction);
        menHelpAbout.setText(rb.getString("About"));
        return help;
    }

    private JMenu createLanguageJMenu() {
        JMenu option = new JMenu(rb.getString("LanguageOptions"));
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        Locale[] locales = {Locale.US, Locale.GERMANY, Locale.CHINA};
        for (int x = 0; x < locales.length; ++x) {
            String displayLanguage = locales[x].getDisplayLanguage(locales[x]);
            JRadioButton la = new JRadioButton(displayLanguage);
            la.setActionCommand(locales[x].getCountry());

            if (language.equals(locales[x].getCountry()))
                la.setSelected(true);

            group.add(la);
            option.add(la);
            //Register a listener for the radio buttons.
            la.addActionListener(new LocaleAction(this));

        }


        return option;
    }


    // Add menus to the menu bar
    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        bar.add(createFileJMenu());

        bar.add(createLayoutJMenu());
        bar.add(createEditJMenu());
        bar.add(createDisplayJMenu());
        bar.add(createViewJMenu());
        if (isApplet) {
            if (dh1 != null && this.getParameter("title").startsWith("RecScores")) {
                bar.add(createRecommJMenu(2));
            }
            if (dh1 != null && this.getParameter("title").startsWith("Recommendation")) {
                bar.add(createRecommJMenu(1));
            }
        } else {
            bar.add(createRecommJMenu(0));
        }

        bar.add(createHelpJMenu());
        //  bar.add(createLanguageJMenu());

        return bar;
    }

    // create tool bar.
    private JToolBar createToolBar() {
        tb = new JToolBar();
        tb.setFloatable(true);
        //   int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
        ToolTipManager.sharedInstance().setDismissDelay(60000); // set tooltip to 1 minutes
        // Show tool tips immediately
        //  ToolTipManager.sharedInstance().setInitialDelay(0);

        circleButton = new JButton();
        if (isApplet)
            circleButton.addActionListener(circleAction);
        // else
        // circleButton.setEnabled(false);
        circleButton.setIcon(getImage("circle.gif"));
        circleButton.setToolTipText(rb.getString("PutNodesInCircle"));
        tb.add(circleButton);

        randomButton = new JButton();
        if (isApplet)
            randomButton.addActionListener(randomAction);
        //  else
        //  randomButton.setEnabled(false);
        randomButton.setIcon(getImage("random.gif"));
        randomButton.setToolTipText(rb.getString("RandomlyScatterNodes"));
        tb.add(randomButton);

        annealButton = new JButton();

        /* if (isApplet) {
         annealButton.addMouseListener(new MouseAdapter() {
              public void mousePressed(MouseEvent e) {
                  getPopup().show(e.getComponent(), e.getX(), e.getY());
              }
          });
      }  */
        if (isApplet)
            annealButton.addActionListener(new AnnealAction(gv, 2));
        annealButton.setIcon(getImage("anneal.gif"));
        annealButton.setToolTipText("Automatically cluster network");

        annealButton.setIcon(getImage("anneal.gif"));
        annealButton.setToolTipText(rb.getString("AutomaticallyClusterNetwork"));
        if (clusteringActivated == false)
            annealButton.setEnabled(true);
        else
            annealButton.setEnabled(false);
        tb.add(annealButton);

        treeButton = new JButton();
        if (isApplet)
            treeButton.addActionListener(treeAction);
        treeButton.setIcon(getImage("tree.gif"));
        treeButton.setToolTipText(rb.getString("PutNodesInRadialTree"));
        if (treeActivated == false)
            treeButton.setEnabled(true);
        else
            treeButton.setEnabled(false);
        tb.add(treeButton);

        groupButton = new JButton();
        if (isApplet)
            groupButton.addActionListener(aggregateAction);

        groupButton.setIcon(getImage("group.gif"));
        groupButton.setToolTipText(rb.getString("GroupNodesByAttribute"));

        tb.add(groupButton);


        tb.addSeparator();

        if ((isApplet && isCustomlayout) || !isApplet) {
            customButton = new JButton();
            if (!isApplet)
                customButton.setEnabled(false);
            customButton.addActionListener(customAction);

            customButton.setIcon(getImage("first.gif"));
            customButton.setToolTipText(rb.getString("ResetToTheSavedNetwork"));

            if (isCustomlayout) {

                customButton.setEnabled(true);
            } else {
                customButton.setEnabled(false);
                customButton.setVisible(false);
            }

            tb.add(customButton);
        }
        // New hiding button
        showallButton = new JButton();
        if (isApplet)
            showallButton.addActionListener(showAllAction);
        showallButton.setIcon(getImage("showall.GIF"));
        showallButton.setToolTipText(rb.getString("ShowAllNodes/edges"));
        tb.add(showallButton);

        hideselButton = new JButton();
        if (isApplet)
            hideselButton.addActionListener(hideselAction);
        hideselButton.setIcon(getImage("hidesel.GIF"));
        hideselButton.setToolTipText(rb.getString("HideSelection"));
        tb.add(hideselButton);

        selClampButton = new JButton();
        if (isApplet)
            selClampButton.addActionListener(selClampAction);
        selClampButton.setIcon(getImage("showsel.GIF"));
        selClampButton.setToolTipText(rb.getString("ShowSelection"));
        tb.add(selClampButton);


        edgeButton = new JButton();
        if (isApplet)
            edgeButton.addActionListener(EdgeAction);
        edgeButton.setIcon(getImage("edgetype.gif"));
        edgeButton.setToolTipText(rb.getString("Hide/showSelectedEdgeType"));
        tb.add(edgeButton);

        nodeButton = new JButton();
        if (isApplet)
            nodeButton.addActionListener(NodeAction);
        nodeButton.setIcon(getImage("nodetype.gif"));
        nodeButton.setToolTipText(rb.getString("Hide/ShowSelectedNodeShape"));
        tb.add(nodeButton);

        shapeButton = new JButton();
        if (isApplet)
            shapeButton.addActionListener(seShapeAction);
        shapeButton.setIcon(getImage("selectShape.gif"));
        shapeButton.setToolTipText(rb.getString("Hide/ShowSelectedNodeShape"));
        shapeButton.setEnabled(false);
        tb.add(shapeButton);

        isolateButton = new JButton();
        if (isApplet)
            isolateButton.addActionListener(hideisoAction);
        isolateButton.setIcon(getImage("removeIsolate.gif"));
        isolateButton.setToolTipText(rb.getString("HideIsolateNode"));
        tb.add(isolateButton);


        limitButton = new JButton();
        if (isApplet)
            limitButton.addActionListener(limitAction);
        limitButton.setIcon(getImage("limit.gif"));
        limitButton.setToolTipText(rb.getString("LimitEdgeStrengths"));
        tb.add(limitButton);

        tb.addSeparator();

        fitButton = new JButton();
        if (isApplet)
            fitButton.addActionListener(fitAction);
        fitButton.setIcon(getImage("fit.gif"));
        fitButton.setToolTipText(rb.getString("FitGraphToScreen"));
        tb.add(fitButton);

        // toggle set false to true
        minimizeNodesButton = new JToggleButton();
        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2")) {

                minimizeNodesButton.addActionListener(minimizeNodesAction);
                minimizeNodesButton.setIcon(getImage("minimizenodes.GIF"));
                minimizeNodesButton.setToolTipText(rb.getString("MinimizeNodes"));
                tb.add(minimizeNodesButton);
            }
        } else {
            minimizeNodesButton.setIcon(getImage("minimizenodes.GIF"));
            minimizeNodesButton.setToolTipText(rb.getString("MinimizeNodes"));
            tb.add(minimizeNodesButton);
        }
        imageNodesButton = new JToggleButton();
        if (isApplet)
            imageNodesButton.addActionListener(imageNodesAction);
        imageNodesButton.setIcon(getImage("face.gif"));
        imageNodesButton.setToolTipText(rb.getString("ShowNodeImage"));

        if (isApplet) {
            if (getParameter("images").contains("1")) {

                imageNodesButton.setEnabled(true);
            } else {
                imageNodesButton.setEnabled(false);
                imageNodesButton.setVisible(false);
            }

        } else {
            imageNodesButton.setEnabled(false);
            imageNodesButton.setVisible(false);
        }
        tb.add(imageNodesButton);


        shapeNodesButton = new JToggleButton();
        String shapeTip = rb.getString("NodeShapeBy2");
        if (isApplet) {
            shapeNodesButton.addActionListener(shapeNodesAction);

            shapeTip = rb.getString("NodeShapeBy") + " " + dh1.getShapeBy();
        }
        shapeNodesButton.setIcon(getImage("shape.gif"));

        shapeNodesButton.setToolTipText(shapeTip);

        if (recStatus != 2) {
            shapeNodesButton.setEnabled(true);
        } else {
            shapeNodesButton.setEnabled(false);
            shapeNodesButton.setVisible(false);
        }
        shapeNodesButton.setEnabled(true);

        tb.add(shapeNodesButton);

        // toggle set false to true
        resizeNodesButton = new JToggleButton();
        if (isApplet)
            resizeNodesButton.addActionListener(resizeNodesAction);
        resizeNodesButton.setIcon(getImage("resizenodes.gif"));
        String tipText = "Resize Nodes by nodeType/attribute";
        if (isApplet) {
            if (size2Label != null)
                tipText = "<html>Resize Nodes by:<br><b>\u2194 " + sizeLabel + "</b><br><b>  \u2195        " + size2Label + "</b></html>";

            else
                tipText = "<html>Resize Nodes by: <b>" + sizeLabel + "</b></html>";
        }
        resizeNodesButton.setToolTipText(tipText);

        if (isApplet) {
            if (dh1.getNodeSizeType() > 0 && recStatus != 2) {
                resizeNodesButton.setEnabled(true);
            } else {
                resizeNodesButton.setEnabled(false);
                resizeNodesButton.setVisible(false);
            }

        } else {
            resizeNodesButton.setEnabled(false);
            resizeNodesButton.setVisible(false);
        }
        tb.add(resizeNodesButton);

        // toggle "label small node" button
        labelButton = new JToggleButton();
        if (isApplet)
            labelButton.addActionListener(labelAction);


        labelButton.setIcon(getImage("label.gif"));
        labelButton.setToolTipText(rb.getString("Show/HiddenLabel"));
        labelButton.setEnabled(false);

        if (recStatus == 2)
            labelButton.setEnabled(true);
        else if (isApplet) {
            if (dh1.getLabelHiden().equals("1")) {
                labelButton.setEnabled(true);

            }
        }
        if (isApplet) {
            if (!dh1.getLabelHiden().equals("2"))
                tb.add(labelButton);
        } else
            tb.add(labelButton);
        depthButton = new JButton();
        if (isApplet)
            depthButton.addActionListener(depthAction);
        depthButton.setIcon(getImage("depth.gif"));
        depthButton.setToolTipText(rb.getString("NodeDepth"));
        tb.add(depthButton);

        strengthButton = new JToggleButton();
        if (isApplet)
            strengthButton.addActionListener(strengthAction);
        strengthButton.setIcon(getImage("strength.gif"));
        strengthButton.setToolTipText(rb.getString("ShowEdgeStrengths"));
        tb.add(strengthButton);

        // toggle set false to true
        minimizeEdgesButton = new JToggleButton();
        if (isApplet)
            minimizeEdgesButton.addActionListener(minimizeEdgesAction);
        minimizeEdgesButton.setIcon(getImage("weightedge.gif"));
        minimizeEdgesButton.setToolTipText(rb.getString("WeightEdges"));
        tb.add(minimizeEdgesButton);


        tb.addSeparator();
        zoomButton = new JToggleButton();
        if (isApplet)
            zoomButton.addActionListener(zoomAction);
        zoomButton.setIcon(getImage("zoom.gif"));
        zoomButton.setToolTipText(rb.getString("ZoomToSelection"));
        tb.add(zoomButton);

        zoomInButton = new JToggleButton();
        if (isApplet)
            zoomInButton.addActionListener(zoomInAction);
        zoomInButton.setIcon(getImage("zoomin.gif"));
        zoomInButton.setToolTipText(rb.getString("ZoomInOnEdges"));
        tb.add(zoomInButton);

        zoomOutButton = new JToggleButton();
        if (isApplet)
            zoomOutButton.addActionListener(zoomOutAction);
        zoomOutButton.setIcon(getImage("zoomout.gif"));
        zoomOutButton.setToolTipText(rb.getString("ZoomOutOnEdges"));
        tb.add(zoomOutButton);

        zoomInButton1 = new JToggleButton();
        if (isApplet)
            zoomInButton1.addActionListener(zoomInAction1);
        zoomInButton1.setIcon(getImage("zoomin1.gif"));
        zoomInButton1.setToolTipText(rb.getString("ZoomInOnGraph"));
        tb.add(zoomInButton1);

        zoomOutButton1 = new JToggleButton();
        if (isApplet)
            zoomOutButton1.addActionListener(zoomOutAction1);
        zoomOutButton1.setIcon(getImage("zoomout1.gif"));
        zoomOutButton1.setToolTipText(rb.getString("ZoomOutOnGraph"));
        tb.add(zoomOutButton1);


        panButton = new JToggleButton();
        if (isApplet)
            panButton.addActionListener(panAction);
        panButton.setIcon(getImage("hand.gif"));
        panButton.setToolTipText(rb.getString("MoveGraph"));
        tb.add(panButton);

        if (isApplet) {
            if (dh1 != null && dh1.getTitle().startsWith("Recommendation")) {

                tb.addSeparator();
                recButton = new JButton();

                recButton.addActionListener(recAction);
                recButton.setIcon(getImage("rec.gif"));
                recButton.setToolTipText(rb.getString("SHowRecommendationPath"));

                recButton.setEnabled(true);
                tb.add(recButton);

                whyButton = new JButton();

                whyButton.addActionListener(whyAction);
                whyButton.setIcon(getImage("qu.gif"));
                whyButton.setToolTipText(rb.getString("RecommendationDescription"));

                whyButton.setEnabled(true);

                tb.add(whyButton);

                initialViewButton = new JToggleButton();

                initialViewButton.addActionListener(initialViewAction);
                initialViewButton.setIcon(getImage("Icon_ShortestPath.gif"));
                initialViewButton.setToolTipText(rb.getString("Show/HideNextLevelPath"));
                if (dh1 != null && dh1.hiddenItemsExist()) {
                    initialViewButton.setEnabled(true);
                } else {
                    initialViewButton.setEnabled(false);
                    initialViewButton.setVisible(false);
                }
                initialViewButton.setEnabled(true);
                tb.add(initialViewButton);
            }

            if (dh1 != null && dh1.getTitle().startsWith("RecScores")) {

                tb.addSeparator();
                recAttriButton = new JButton();

                recAttriButton.addActionListener(recEdgeAction);

                recAttriButton.setIcon(getImage("recCircle.gif"));

                recAttriButton.setToolTipText(rb.getString("SHowRecommendationLayer"));
                recAttriButton.setEnabled(true);
                tb.add(recAttriButton);

                whyButton = new JButton(whyAction);

                whyButton.addActionListener(whyAction);
                whyButton.setIcon(getImage("qu.gif"));
                whyButton.setToolTipText(rb.getString("RecommendationDescription"));
                whyButton.setEnabled(true);
                tb.add(whyButton);

                initialViewButton = new JToggleButton();

                initialViewButton.addActionListener(initialViewAction);
                initialViewButton.setIcon(getImage("Icon_ShortestPath.gif"));
                initialViewButton.setToolTipText(rb.getString("Show/HideNoRecommendedPaths"));
                if (dh1 != null && dh1.noRecEdgeExist()) {
                    initialViewButton.setEnabled(true);
                } else {
                    initialViewButton.setEnabled(false);
                    initialViewButton.setVisible(false);
                }
                initialViewButton.setEnabled(true);
                tb.add(initialViewButton);

            }
        }

        return tb;

    }


    private JPopupMenu getPopup() {

        JPopupMenu popup = new JPopupMenu();

        JMenuItem menuItem1 = new JMenuItem();
        JMenuItem menuItem2 = new JMenuItem();
        menuItem1.setText(rb.getString("ClusteringWithoutEdgeWeight"));

        menuItem1.addActionListener(new AnnealAction(gv, 1));

        popup.add(menuItem1);


        menuItem2.setText(rb.getString("ClusteringWithEdgeWeight"));

        menuItem2.addActionListener(new AnnealAction(gv, 2));

        popup.add(menuItem2);

        return popup;

    }


    public JToggleButton getInitialViewButton() {
        return initialViewButton;
    }
    // make cursors for zooming

    private void zoomCursor() {
        zoom = Toolkit.getDefaultToolkit().createCustomCursor(getImage("zoom-ori.gif").getImage(), new Point(10, 10), "zoom");
        zoomIn = Toolkit.getDefaultToolkit().createCustomCursor(getImage("zoomin.gif").getImage(), new Point(10, 10), "zoomin");
        zoomOut = Toolkit.getDefaultToolkit().createCustomCursor(getImage("zoomout.gif").getImage(), new Point(10, 10), "zoomout");

        zoomIn1 = Toolkit.getDefaultToolkit().createCustomCursor(getImage("zoomin1-1.gif").getImage(), new Point(10, 10), "zoomin1");
        zoomOut1 = Toolkit.getDefaultToolkit().createCustomCursor(getImage("zoomout1-1.gif").getImage(), new Point(10, 10), "zoomout1");


        pan = Toolkit.getDefaultToolkit().createCustomCursor(getImage("hand-1.gif").getImage(), new Point(10, 10), "pan");

    }


    public void enableButton(boolean enableButton) {
        if (labelButton != null)
            labelButton.setEnabled(enableButton);
    }

    public void enableShapeButton(boolean enableButton) {
        if (shapeButton != null)
            shapeButton.setEnabled(enableButton);
    }

    public void enableMenuItem(boolean enableButton) {
        if (showLabel != null)
            showLabel.setEnabled(enableButton);
    }

    public void enableRecResize(boolean enabled) {
        if (resizeRecSize != null)
            resizeRecSize.setEnabled(enabled);
    }

    public void enableImage(boolean enabled) {
        if (resizeImage != null)
            resizeImage.setEnabled(enabled);
    }
    public void enableSetNode(boolean enableButton) {
        if (setNode != null)
            setNode.setEnabled(enableButton);
    }

    public void visibleSetNode(boolean enableButton) {
        if (setNode != null)
            setNode.setVisible(enableButton);
    }

    public void enableShapeMenuItem(boolean enableButton) {
        if (selectShape != null)
            selectShape.setEnabled(enableButton);
    }

    public Cursor getPan() {
        return pan;
    }

    public Cursor getZoomIn() {
        return zoomIn;
    }

    public Cursor getZoom() {
        return zoom;
    }

    public Cursor getZoomOut() {
        return zoomOut;
    }

    public Cursor getZoomIn1() {
        return zoomIn1;
    }

    public Cursor getZoomOut1() {
        return zoomOut1;
    }

    public GraphView getGP1() {

        return gv;
    }

    private ImageIcon getImage(String filename) {
        Class thisClass = getClass();
        // System.out.println("loading icon image "+filename);
        java.net.URL url = thisClass.getResource("images/" + filename);
        if (url == null) {
            System.out.println("couldn't load image " + filename);
            return null;
        }
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage(url);

        return new ImageIcon(img);
    }

    public AppletDataHandler1 getHandler1() {

        return dh1;
    }


    public void disableInitialViewButton() {
        initialViewButton.setEnabled(false);
    }

    public void toggleShowInitialView() {
        initialViewButton.doClick();
    }

    public void toggleStrength() {
        strengthButton.doClick();
    }

    public void toggleWeight() {
        minimizeEdgesButton.doClick();
    }
    public void toggleFitGraph() {
        fitButton.doClick();
    }
    public void toggleMinNodes() {

        minimizeNodesButton.doClick();

    }

    public void toggleImageNodes() {
        imageNodesButton.doClick();
    }

    public void toggleZoom() {
        zoomButton.doClick();
    }

    public void toggleZoomIn() {
        zoomInButton.doClick();
    }

    public void toggleZoomOut() {
        zoomOutButton.doClick();
    }


    public void toggleZoomIn1() {
        zoomInButton1.doClick();
    }

    public void toggleZoomOut1() {
        zoomOutButton1.doClick();
    }

    public void togglePan() {
        panButton.doClick();
    }


    public void toggleResizeNodes() {
        resizeNodesButton.doClick();
    }

    public void toggleShapeNodes() {
        shapeNodesButton.doClick();

    }

    public void toggleLabelNodes() {
        if (labelButton != null)
            labelButton.doClick();
    }

    public void toggleRecAttriLayout() {
        recAttriButton.doClick();
    }

    public void toggleSelClamp() {
        selClampButton.doClick();
    }

    public void doLimitStrength() {
        if (limitD == null || !limitD.isVisible())
            limitD = new LimitDialog(this);
        limitD.setVisible(true);
        gv.resetCursor();
    }

    public void openSaveWin() {
        if (saveD == null || !saveD.isVisible())
            saveD = new SaveDataDialog(this, gv, dh1);
        saveD.setVisible(true);
        gv.resetCursor();
    }

    public void SendMailWin() {
        if (mailD == null || !mailD.isVisible())
            mailD = new EmailDialog(this, gv, dh1);
        mailD.setVisible(true);
        gv.resetCursor();
    }

    public void doCusteringAction(int method) {
        if (clusteringD == null || !clusteringD.isVisible())
            clusteringD = new ClusteringDialog(this);
        clusteringD.setVisible(true);
        gv.resetCursor();
        gv.clusterMethod = method;
    }

    public NormalizedDialog getNormalizedDialog() {
        return normalizedD;
    }

    public NormalizedDialog2 getNormalizedDialog2() {
        return normalizedD2;
    }

    public ImageDialog getImageDialog() {
        return imageD;
    }

    public void doNormalizedAction(boolean forEdgeWidth, boolean forRec) {
        if (forEdgeWidth) {
            if (wNormalizedD == null || !wNormalizedD.isVisible()) {
                wNormalizedD = new WeightNormalizedDialog(this, gv, dh1);
                // wNormalizedD.setVisible(true);

            }
        } else if (forRec) {
            /* NormalizeRecPara normalizedD = null;
          if (normalizedD == null || !normalizedD.isVisible()) {
              String[] recPara = dh1.getRecPara();
              String refTarget = recPara[recPara.length - 1];

              String requestor = dh1.getRecRequestor();
              normalizedD = new NormalizeRecPara(this, gv, dh1, refTarget, requestor);
            //  normalizedD.setVisible(true);
          }  */
        } else {
            if (dh1.getNodeSizeType() == 2) {
                if (normalizedD2 == null || !normalizedD2.isVisible()) {
                    normalizedD2 = new NormalizedDialog2(this, gv, dh1, sizeLabel, size2Label, this);
                    // normalizedD2.setVisible(true);
                }
            } else {
                if (normalizedD == null || !normalizedD.isVisible()) {
                    normalizedD = new NormalizedDialog(this, gv, dh1, sizeLabel);
                    // normalizedD.setVisible(true);
                }
            }
        }
        gv.resetCursor();
    }

    public void openNormalizedDialog() {

        if (dh1.getNodeSizeType() == 2) {

            if (normalizedD2 != null) {
                if (!normalizedD2.isVisible())
                    normalizedD2.setVisible(true);
            } else {
                normalizedD2 = new NormalizedDialog2(this, gv, dh1, sizeLabel, size2Label, this);
                 normalizedD2.setVisible(true);
            }
        } else {
             
            if (normalizedD != null) {
                if (!normalizedD.isVisible())
                    normalizedD.setVisible(true);
            } else {
                normalizedD = new NormalizedDialog(this, gv, dh1, sizeLabel);
                  normalizedD.setVisible(true);
            }

        }
    }

    public void doImageAction() {

        if (imageD != null && imageD.isVisible()) {
            imageD.dispose();
        }
    }


    public void doAboutAction
            () {
        if (aboutD == null || !aboutD.isVisible())
            aboutD = new AboutDialog(this);
        aboutD.setVisible(true);
    }

    public HelpSet getHelpSet(ClassLoader loader, URL helpseturl) {
        HelpSet hs = null;

        try {

            hs = new HelpSet(loader, helpseturl);
        } catch (Exception ee) {
            System.out.println("HelpSet: " + ee.getMessage());

        }
        return hs;
    }

    public void doDocAction
            () {

        java.net.URL url = getClass().getResource("visualizerHelp/visualizer.hs");
        ClassLoader loader = getClass().getClassLoader();
        HelpSet hs = getHelpSet(loader, url);

        HelpBroker hb = hs.createHelpBroker();

        hb.setDisplayed(true);

        /*  File file;
        java.net.URL url = getClass().getResource("vis.chm") ;
        try {
          file = new File(url.toURI());
        } catch(URISyntaxException e) {
          file = new File(url.getPath());
        }


                       try
                       {
                         Runtime.getRuntime().exec("HH.EXE ms-its:" + file.getAbsolutePath() + "::/Welcome.html");
                       } catch (IOException e1)
                       {
                         e1.printStackTrace();
                       }
        */

    }


    public void doWhyAction
            () {
        if (whyD == null || !whyD.isVisible())
            whyD = new WhyDialog(gv, this, dh1, recStatus);

        whyD.setVisible(true);
        gv.resetCursor();
    }

    public void doDisplayEdge
            () {

        if (edgeD == null || !edgeD.isVisible()) {

            edgeD = new EdgeDialog(this);
            edgeD.setVisible(true);
        }
        gv.resetCursor();
    }

    public void doDisplayNode
            () {

        if (nodeD == null || !nodeD.isVisible()) {

            nodeD = new NodeDialog(this);
            nodeD.setVisible(true);

        }
        gv.resetCursor();
    }

    public void doShapeSelect() {

        if (shapeD == null || !shapeD.isVisible()) {

            shapeD = new ShapeDialog(this);
            shapeD.setVisible(true);

        }
        gv.resetCursor();
    }


    /**
     * gets the width of the display device
     *
     * @return <code>int</code> containing width in pels
     */
    private int getMaxWidth
            () {
        Toolkit kit = this.getToolkit();
        Dimension screen_size = kit.getScreenSize();
        return screen_size.width - 20;
    }

    /**
     * gets the height of the display device
     *
     * @return <code>int</code> containing height in pels
     */
    private int getMaxHeight
            () {
        Toolkit kit = this.getToolkit();
        Dimension screen_size = kit.getScreenSize();
        return screen_size.height - 20;
    }

    /**
     * gets the applet data handler object
     *
     * @return <code>DataHandler</code> if it exists or <code>null</code>
     */


    public AppletDataHandler1 getDataHandler1
            () {
        return dataHandler1;
    }

    public MainFrame getMainFrame
            () {
        return frame;
    }


    public JFrame getJFrame
            () {
        return jframe;
    }


    /**
     * called when the applet is destroyed
     */
    public void destroy
            () {

        //  display.clearDamage();
        try {
            display.removeAll();

            g.clear();
            g.dispose();


            display.setVisualization(null);
            display = null;

        } catch (Exception e) {

        }
        System.out.println("Applet destroyed");
    }


    private void addApplicationAction() {
        saveData.addActionListener(saveDataAction);
        saveImage.addActionListener(saveGraphAction);

        if (isGraphML)
            setting.addActionListener(settingAction);

        //    resizeNode, shapeNode, depth, st, miniEdge, zoomItem, zoomInItem, zoomOutItem, panItem, zoomIn1Item, zoomOut1Item,search,
        //     nodeTypeLayout, attriLayout, desc, show;
        selClampButton.addActionListener(selClampAction);
        selClamp.addActionListener(selClampAction);
        circleButton.addActionListener(circleAction);
        circle.addActionListener(circleAction);
        randomButton.addActionListener(randomAction);
        random.addActionListener(randomAction);
        annealButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                getPopup().show(e.getComponent(), e.getX(), e.getY());
            }
        });
        menuItem11.addActionListener(new AnnealAction(this, 1));
        menuItem22.addActionListener(new AnnealAction(this, 2));

        treeButton.addActionListener(treeAction);
        tree.addActionListener(treeAction);
        groupButton.addActionListener(aggregateAction);
        group.addActionListener(aggregateAction);
        customItem.addActionListener(customAction);
        customButton.addActionListener(customAction);
        showallButton.addActionListener(showAllAction);
        showAll.addActionListener(showAllAction);
        edgeButton.addActionListener(EdgeAction);
        edge.addActionListener(EdgeAction);
        nodeButton.addActionListener(NodeAction);
        shapeButton.addActionListener(seShapeAction);
        nodetype.addActionListener(NodeAction);
        limitButton.addActionListener(limitAction);
        limit.addActionListener(limitAction);
        isolateButton.addActionListener(hideisoAction);
        hideiso.addActionListener(hideisoAction);
        hideselButton.addActionListener(hideselAction);
        hidesel.addActionListener(hideselAction);
        fitButton.addActionListener(fitAction);
        fit.addActionListener(fitAction);
        labelButton.addActionListener(labelAction);
        showLabel.addActionListener(iLabelAction);
        strengthButton.addActionListener(strengthAction);
        st.addActionListener(iSSAction);
        if (isApplet) {
            if (!dh1.getLabelHiden().equalsIgnoreCase("2")) {
                minimizeNodesButton.addActionListener(minimizeNodesAction);
                miniNode.addActionListener(iMiniNodeAction);
            }
        } else {
            minimizeNodesButton.addActionListener(minimizeNodesAction);
            miniNode.addActionListener(iMiniNodeAction);
        }
        imageNodesButton.addActionListener(imageNodesAction);
        imageNode.addActionListener(iImageNodeAction);
        resizeImage.addActionListener(iImageSizeAction);
        shapeNodesButton.addActionListener(shapeNodesAction);
        shapeNode.addActionListener(iShapeNodeAction);
        resizeNodesButton.addActionListener(resizeNodesAction);
        sizeNode.addActionListener(iResizeNodeAction);
        setNode.addActionListener(iResizeSetAction);
        minimizeEdgesButton.addActionListener(minimizeEdgesAction);
        miniEdge.addActionListener(iSWAction);
        zoomInButton.addActionListener(zoomInAction);
        zoomInItem.addActionListener(iZoomInAction);
        zoomButton.addActionListener(zoomAction);
        zoomItem.addActionListener(iZoomAction);
        zoomOutButton.addActionListener(zoomOutAction);
        zoomOutItem.addActionListener(iZoomOutAction);
        zoomInButton1.addActionListener(zoomInAction1);
        zoomIn1Item.addActionListener(iZoomInAction1);
        zoomOutButton1.addActionListener(zoomOutAction1);
        zoomOut1Item.addActionListener(iZoomOutAction1);
        panButton.addActionListener(panAction);
        panItem.addActionListener(iPanAction);
        depthButton.addActionListener(depthAction);
        depth.addActionListener(depthAction);
    }

    public void processData(Map<String, String> htmlParas, boolean isGraphML) {

        this.htmlParas = htmlParas;
        this.isGraphML = isGraphML;
        boolean hasValidData = visualGraph(reader);
        if (hasValidData) {
            addApplicationAction();

            if (htmlParas.get("images").contains("1")) {
                imageNodesButton.setEnabled(true);
                imageNodesButton.setVisible(true);
                imageNode.setEnabled(true);
                imageNode.setVisible(true);
                resizeImage.setVisible(true);
                if (gv.nodedisStatus() == 4)
                    resizeImage.setEnabled(true);
            } else {
                imageNodesButton.setEnabled(false);
                imageNodesButton.setVisible(false);
                imageNode.setEnabled(false);
                imageNode.setVisible(false);
            }

            if (isCustomlayout) {
                customItem.setEnabled(true);
                customItem.setVisible(true);
                customButton.setEnabled(true);
                customButton.setVisible(true);
            } else {
                customItem.setEnabled(false);
                customItem.setVisible(false);
                customButton.setEnabled(false);
                customButton.setVisible(false);
            }

            if (dh1.getNodeSizeType() > 0 && recStatus != 2) {
                resizeNodesButton.setEnabled(true);
                resizeNodesButton.setVisible(true);
                resizeNode.setEnabled(true);
                resizeNode.setVisible(true);
                sizeNode.setEnabled(true);
                sizeNode.setVisible(true);
                sizeNode.setEnabled(true);
                sizeNode.setVisible(true);

            } else {
                resizeNodesButton.setEnabled(false);
                resizeNodesButton.setVisible(false);
                resizeNode.setEnabled(false);
                resizeNode.setVisible(false);
                if (normalizedD2 != null) {
                    normalizedD2.setVisible(false);
                    normalizedD2.dispose();
                }
                if (normalizedD != null) {
                    normalizedD.setVisible(false);
                    normalizedD.dispose();
                }
            }
            if (dh1.getLabelHiden().equals("1"))
                labelButton.setEnabled(true);

            if ((dh1 != null && dh1.getTitle().startsWith("RecScores")) || (dh1 != null && dh1.getTitle().startsWith("Recommendation"))) {

                if (dh1 != null && dh1.getTitle().startsWith("Recommendation")) {
                    tb.addSeparator();
                    recButton = new JButton();

                    recButton.addActionListener(recAction);
                    recButton.setIcon(getImage("rec.gif"));
                    recButton.setToolTipText(rb.getString("SHowRecommendationPath"));

                    recButton.setEnabled(true);
                    tb.add(recButton);

                    whyButton = new JButton();

                    whyButton.addActionListener(whyAction);
                    whyButton.setIcon(getImage("qu.gif"));
                    whyButton.setToolTipText(rb.getString("RecommendationDescription"));

                    whyButton.setEnabled(true);

                    tb.add(whyButton);

                    initialViewButton = new JToggleButton();

                    initialViewButton.addActionListener(initialViewAction);
                    initialViewButton.setIcon(getImage("Icon_ShortestPath.gif"));
                    initialViewButton.setToolTipText(rb.getString("Show/HideNextLevelPath"));
                    if (dh1 != null && dh1.hiddenItemsExist()) {
                        initialViewButton.setEnabled(true);
                    } else {
                        initialViewButton.setEnabled(false);
                        initialViewButton.setVisible(false);
                    }
                    initialViewButton.setEnabled(true);
                    tb.add(initialViewButton);
                } else {

                    if (recButton != null)
                        recButton.setVisible(false);
                    if (initialViewButton != null)
                        initialViewButton.setVisible(false);
                }

                if (dh1 != null && dh1.getTitle().startsWith("RecScores")) {

                    tb.addSeparator();
                    recAttriButton = new JButton();

                    recAttriButton.addActionListener(recEdgeAction);

                    recAttriButton.setIcon(getImage("recCircle.gif"));

                    recAttriButton.setToolTipText(rb.getString("SHowRecommendationLayer"));
                    recAttriButton.setEnabled(true);
                    tb.add(recAttriButton);

                    whyButton = new JButton(whyAction);

                    whyButton.addActionListener(whyAction);
                    whyButton.setIcon(getImage("qu.gif"));
                    whyButton.setToolTipText(rb.getString("RecommendationDescription"));
                    whyButton.setEnabled(true);
                    tb.add(whyButton);

                } else {
                    if (recAttriButton != null)
                        recAttriButton.setVisible(false);
                    if (normalizedD != null) {
                        normalizedD.setVisible(false);
                        normalizedD.dispose();
                    }
                }
            } else {
                try {
                    if (recAttriButton != null)
                        recAttriButton.setVisible(false);
                    if (recButton != null)
                        recButton.setVisible(false);
                    if (whyButton != null)
                        whyButton.setVisible(false);
                    if (initialViewButton != null)
                        initialViewButton.setVisible(false);
                    if (normalizedD != null) {
                        normalizedD.setVisible(false);
                        normalizedD.dispose();
                    }
                } catch (Exception e) {

                }

            }


            if ((dh1 != null && htmlParas.get("title").startsWith("RecScores")) || (dh1 != null && htmlParas.get("title").startsWith("Recommendation"))) {

                if (dh1 != null && htmlParas.get("title").startsWith("RecScores")) {
                    recMenu.setEnabled(true);
                    recMenu.setVisible(true);
                    attriLayout = new JMenuItem();

                    attriLayout.addActionListener(iRecAttriAction);

                    attriLayout.setText(rb.getString("SHowRecommendationLayer"));

                    attriLayout.setAccelerator(
                            KeyStroke.getKeyStroke('S', java.awt.Event.SHIFT_MASK, false));
                    recMenu.add(attriLayout);
                    resizeRecSize = new JMenuItem();

                    resizeRecSize.addActionListener(iRecSizeAction);

                    resizeRecSize.setText(rb.getString("Resize recommendation") + "...");

                    resizeRecSize.setAccelerator(
                            KeyStroke.getKeyStroke('R', java.awt.Event.SHIFT_MASK, false));
                    recMenu.add(resizeRecSize);


                } else {
                    if (attriLayout != null)
                        attriLayout.setVisible(false);
                    if (resizeRecSize != null)
                        resizeRecSize.setVisible(false);
                    if (normalizedD != null) {
                        normalizedD.setVisible(false);
                        normalizedD.dispose();
                    }
                }
                if (dh1 != null && htmlParas.get("title").startsWith("Recommendation")) {
                    recMenu.setEnabled(true);
                    recMenu.setVisible(true);
                    nodeTypeLayout = new JMenuItem();

                    nodeTypeLayout.addActionListener(recAction);
                    nodeTypeLayout.setText(rb.getString("SHowRecommendationPath"));

                    nodeTypeLayout.setAccelerator(
                            KeyStroke.getKeyStroke('S', java.awt.Event.SHIFT_MASK, false));
                    recMenu.add(nodeTypeLayout);
                } else {
                    if (nodeTypeLayout != null)
                        nodeTypeLayout.setVisible(false);
                }

                if (desc == null) {
                    desc = new JMenuItem();

                    desc.addActionListener(whyAction);
                    desc.setText(rb.getString("RecommendationDescription"));
                    desc.setAccelerator(
                            KeyStroke.getKeyStroke('W', java.awt.Event.SHIFT_MASK, false));
                    recMenu.add(desc);
                }
                if (dh1 != null && dh1.hiddenItemsExist()) {


                    show = new JMenuItem();


                    show.addActionListener(showPAction);

                    show.setText(rb.getString("Show/HideNextLevelPath"));

                    show.setAccelerator(
                            KeyStroke.getKeyStroke('P', java.awt.Event.SHIFT_MASK, false));

                    recMenu.add(show);
                } else {
                    if (show != null)
                        show.setVisible(false);
                }
            } else {
                if (recMenu != null)
                    recMenu.setVisible(false);
                if (normalizedD != null) {
                    normalizedD.setVisible(false);
                    normalizedD.dispose();
                }
            }

            parentDisPane.add(gv, GRAPHPANEL);

            ((CardLayout) parentDisPane.getLayout()).show(parentDisPane, GRAPHPANEL);
        }

        //loadStatus.setText("");
    }

    public void LoadData
            () {
        if (gv == null) {
            openDataFile();
        } else {
            new CloseYesNoDialog(this);
        }
    }


    public void openDataFile() {
        reader = new FileReader(this);
        reader.openData();
    }

    public void reSettingGraph() {
        if (propertyD == null || !propertyD.isVisible()) {
            propertyD = new NodePropertyMap(reader.getLastPropMap(), reader.getNodeProp(), reader.getNodePropMap(), reader.getEdgeProp(), reader.getEdgePropMap(), reader);
            propertyD.setVisible(true);
        }
    }


    private String getLaLocation() {
        String className = this.getClass().getName();
        String resourceName = className.replace('.', '/') + ".class";
        URL url = this.getClass().getClassLoader().getResource(resourceName);
        String szUrl = url.toString();
        //  System.out.println("00000szUrl: " + szUrl);
        if (szUrl.startsWith("jar:file:")) {
            try {
                szUrl = szUrl.substring("jar:".length(), szUrl.lastIndexOf("!"));
                //   System.out.println("11111szUrl: " + szUrl);
                szUrl = szUrl.replaceAll("graphApplet.jar", "la.txt");

            } catch (Exception e) {

            }
        } else if (szUrl.startsWith("file:")) {

            try {
                szUrl = szUrl.substring(0, szUrl.length() - resourceName.length());
                //   System.out.println("2222szUrl: " + szUrl);
                szUrl = szUrl + "la.txt";

            } catch (Exception e) {

            }
        }

        szUrl = szUrl.substring("file:/".length());
        //  System.out.println("la file location: " + szUrl);
        return szUrl;
    }

    private void initFrame
            () {

        try {

            try {
                String filename = getLaLocation();
                filename = filename.replaceAll("%20", " ");
                java.io.FileReader inputFileReader = new java.io.FileReader(new File(filename));

                // Create Buffered/PrintWriter Objects
                BufferedReader inputStream = new BufferedReader(inputFileReader);


                language = inputStream.readLine();
                if (language.length() == 0) {
                    language = System.getProperty("user.country");
                }
                inputStream.close();

                inputFileReader.close();

            } catch (IOException e) {

                System.out.println("IOException:");
                e.printStackTrace();

            }
            if (language.equals("CN"))
                rb = ResourceBundle.getBundle("vis", Locale.CHINA);
            else if (language.equals("DE"))
                rb = ResourceBundle.getBundle("vis", Locale.GERMANY);
            else if (language.equals("US"))
                rb = ResourceBundle.getBundle("vis", Locale.US);

        } catch (MissingResourceException mre) {
            mre.printStackTrace();
        }


        jframe = new JFrame();
        jframe.setSize(1200, 800);
        jframe.setTitle("C-IKNOW Visualizer");
        java.net.URL url = getClass().getResource("images/" + "title.jpg");

        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage(url);
        jframe.setIconImage(img);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        isApplet = false;

        //	Initialize the applet
        init();

        //	Add the applet to the frame
        jframe.add(this);

        jframe.setVisible(true);
    }

    public static void main
            (String[] args) {

        MainFrame app = new MainFrame();
        app.initFrame();
    }

    public boolean getAppletStaus
            () {
        return isApplet;
    }


    class LocaleAction implements ActionListener {

        MainFrame frame;

        public LocaleAction(MainFrame frame) {
            this.frame = frame;
        }

        public void actionPerformed(ActionEvent e) {

            JRadioButton radioB = (JRadioButton) e.getSource();
            String val = "";
            if (radioB.getActionCommand().equals("US")) {

                val = "US";
                updatelanguage(val);
            } else if (radioB.getActionCommand().equals("DE")) {

                val = "DE";
                updatelanguage(val);
            } else if (radioB.getActionCommand().equals("CN")) {

                val = "CN";
                updatelanguage(val);
            }

        }

        private void updatelanguage(String val) {

            try {


                if (isApplet) {

                    GraphData data = new GraphData();
                    data.setLanguage(val);
                    DataSender ds = new DataSender(data, frame);


                    JOptionPane.showMessageDialog(jframe, "Please refresh your browser! Note: the network will be start over by this action!");
                } else {

                    String filename = getLaLocation();

                    //FileWriter outputFileReader = new FileWriter(new File(url.getFile()));
                    FileWriter outputFileReader = new FileWriter(new File(filename));

                    // Create Buffered/PrintWriter Objects

                    PrintWriter outputStream = new PrintWriter(outputFileReader);

                    outputStream.println(val);

                    outputStream.close();

                    if (gv == null) {
                        jframe.dispose();

                        MainFrame app = new MainFrame();
                        app.initFrame();
                    } else {
                        if (JOptionPane.showConfirmDialog(new JFrame(),
                                "You need to reload your data after changing the laguage. Do you really want to change the laguage?", "Language Option",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {


                            jframe.dispose();

                            MainFrame app = new MainFrame();
                            app.initFrame();
                        } else {

                        }

                    }
                }
            } catch (IOException e) {

                System.out.println("IOException:");
                e.printStackTrace();

            }

            // System.out.println("La: " + val);
        }
    }

    public Map<String, List<String>> getNodeAttri
            () {
        return nodeAttri;
    }

    public List<String> getNodeProp
            () {
        return nodeProperties;
    }

    private void readXML
            (String
                    fileContent) {


        String fileStr = fileContent.replaceAll("~", "\"");
        fileStr = fileStr.replaceAll("&", "&amp;");
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(fileStr.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Exception when convert XML String to InputStream: " + e);
        }

        try {
            MyGraphMLReader mlReader = new MyGraphMLReader();

            g = mlReader.readGraph(is);

            nodeAttri = new HashMap<String, List<String>>();

            int i = 1;
            for (Iterator iter = g.nodes(); iter.hasNext();) {

                Node node = (Node) iter.next();
                // save all nodeinfo
                int nodeattriCount = node.getColumnCount();
                List<String> atriList = new ArrayList<String>();

                String realNodeId = "";
                for (int k = 0; k < nodeattriCount; k++) {
                    if (node.getString(k) != null)
                        atriList.add(node.getColumnName(k) + "||" + node.getString(k));
                    if (node.getColumnName(k).equals("username"))
                        realNodeId = node.getString(k).split("~")[0];

                }


                if (isApplet) {

                    nodeAttri.put(realNodeId, atriList);
                } else
                    nodeAttri.put("" + i, atriList);

                i++;
            }

            nodeProperties = new ArrayList<String>();

            for (Iterator iter = g.nodes(); iter.hasNext();) {

                Node node = (Node) iter.next();

                // save all nodeinfo
                int nodeattriCount = node.getColumnCount();
                for (int k = 0; k < nodeattriCount; k++) {
                    String prop = node.getColumnName(k);
                    if (!nodeProperties.contains(prop) && node.getString(prop) != null) {

                        nodeProperties.add(prop);

                    }
                }

                // break;
            }

            nodeProperties.add("id");
        } catch (Exception e) {
            System.out.println("Exception when read graphML file: " + e);
            //  dataInvalid = true;
            // JOptionPane.showMessageDialog(frame, "No Valid Data!", "Data Open Error",
            //         JOptionPane.ERROR_MESSAGE);
        }
    }
}


