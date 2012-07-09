package admin;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.data.Table;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.util.io.SimpleFileFilter;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.awt.*;

import dialog.NodePropertyMap;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Feb 19, 2010
 * Time: 9:30:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileReader {

    /**
     * The FileChooser to select the file to open to
     */
    protected JFileChooser chooser = null;
    private MainFrame frame;
    private Map<String, String> paraMap;
    private Map<String, List<String>> nodeAttri;
    private String imageLocation;
    private String fileName;
    private boolean isXml, dataInvalid;
    private Graph g;
    private List nodeProperties, edgeProperties;
    private Map<String, String> propertyTypeMap, epropertyTypeMap;
    private Map<String, String> lastpropertyMap, nodeSeqMap;
    private HashMap<String, Integer> id_rowMap;
    private  File f = null;
    public boolean isHtml;
    public FileReader(MainFrame frame) {
        this.frame = frame;
    }

    protected void init() {
        // Initialize the chooser
        chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Open file");
    }

    private Color HEX2COLOR(String hexString) {
        return new Color(Integer.parseInt(hexString, 16));
    }

    public File  getOpenedXMLFile(){       
        return f;
    }

    public void openData() {

        // Initialize if needed
        if (chooser == null) {
            init();
        }

        // open image save dialog
       
        int returnVal = chooser.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        } else {
            return;
        }
        try {
            int index = f.getName().lastIndexOf(".");
            String extention = f.getName().substring(index + 1);

            if (extention.equalsIgnoreCase("xml")) {

                try {
                   MyGraphMLReader  mlReader = new MyGraphMLReader();

                    g = mlReader.readGraph(f);

                   id_rowMap = mlReader.getNodeMap();

                    nodeProperties = new ArrayList<String>();
                    propertyTypeMap = new HashMap<String, String>();

                    for (Iterator iter = g.nodes(); iter.hasNext();) {

                        Node node = (Node) iter.next();

                        // save all nodeinfo
                        int nodeattriCount = node.getColumnCount();
                        for (int k = 0; k < nodeattriCount; k++) {
                            String prop = node.getColumnName(k);
                            if (!nodeProperties.contains(prop) && node.getString(prop) != null) {

                                nodeProperties.add(prop);
                                propertyTypeMap.put(prop, node.getColumnType(k).getName());
                            }
                        }

                         // break;
                    }

                    nodeProperties.add("id");
                   propertyTypeMap.put("id", "java.lang.String");


                   edgeProperties = new ArrayList<String>();
                    epropertyTypeMap = new HashMap<String, String>();

                    for (Iterator iter = g.edges(); iter.hasNext();) {

                        Edge edge = (Edge) iter.next();
                        // save all nodeinfo
                        int edgeattriCount = edge.getColumnCount();

                        for (int k = 0; k < edgeattriCount; k++) {
                            String prop = edge.getColumnName(k);

                            if (!edgeProperties.contains(prop) && edge.getString(prop) != null) {
                                edgeProperties.add(prop);
                                epropertyTypeMap.put(prop, edge.getColumnType(k).getName());
                            }
                        }

                        break;
                    }

                    NodePropertyMap propertyD = new NodePropertyMap(null, nodeProperties, propertyTypeMap, edgeProperties, epropertyTypeMap, this);
                    propertyD.setVisible(true);
                } catch (Exception e) {
                   System.out.println("Exception when read graphML file: " + e);
                        dataInvalid = true;
                        JOptionPane.showMessageDialog(frame, "No Valid Data!", "Data Open Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 if (extention.equalsIgnoreCase("html")) {
                     isHtml= true;
                 }
                importData(f);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void dL2Ciknow(Map<String, String> loginMap, Map<String, String> nodeTypeMap, Set<String> edgeSet) {

        StringBuilder nodeIdStr = new StringBuilder();
        StringBuilder nodeTypesStr = new StringBuilder();
        StringBuilder nodeLabelStr = new StringBuilder();
        StringBuilder hiddenNodesStr = new StringBuilder();
        StringBuilder nodeColorStr = new StringBuilder();
        StringBuilder nodeShapeStr = new StringBuilder();
        StringBuilder nodeGroupStr = new StringBuilder();

        StringBuilder userNameStr = new StringBuilder();
        StringBuilder nodeImageStr = new StringBuilder();

        StringBuilder islabelStr = new StringBuilder();

        StringBuilder edgeStr = new StringBuilder();
        StringBuilder edgeTypesStr = new StringBuilder();
        StringBuilder edgeDirectionStr = new StringBuilder();
        StringBuilder edgeWeightStr = new StringBuilder();
        StringBuilder edgeLabelStr = new StringBuilder();
        StringBuilder hiddenEdgesStr = new StringBuilder();
        StringBuilder edgeColorStr = new StringBuilder();
        StringBuilder nodeLegendStr = new StringBuilder();
        StringBuilder groupLegendStr = new StringBuilder();
        StringBuilder size1Str = new StringBuilder();
        StringBuilder size2Str = new StringBuilder();

        StringBuilder linkLegendStr = new StringBuilder();
        Set<String> legendSet = new HashSet<String>();
        Set<String> groupSet = new HashSet<String>();
        Set<String> nodeTypeSet = new HashSet<String>();

        Map<String, String> colorMap = new HashMap<String, String>();

        String[] colors = {"0xff8c69", "0xa4d3ee", "0xffff66", "0xDAC4E5", "0xB272A6", "0xE0E0E0", "0x8FBC8F", "0xEE9A49",
                "0x2F4F4F", "0x6B8E23", "0xDAA520", "0xFF69B4", "0x4876FF", "0x8DB6CD", "0xB452CD", "0xCD8500"};

        String unknowStr = "unknown-0xFFFFFF";
        String unknowLink = "unknown-0x000000";

        for (String label : nodeTypeMap.keySet()) {

            nodeTypeSet.add(nodeTypeMap.get(label));
        }


        if (nodeTypeSet.size() == 1 && nodeTypeSet.contains("unknown")) {
            legendSet.add(unknowStr);
            groupSet.add(unknowStr);
        } else {
            int m = 0;
            for (String type : nodeTypeSet) {

                legendSet.add(type + "-" + colors[m]);
                colorMap.put(type, colors[m]);
                groupSet.add(type + "-" + colors[15 - m]);
                // groupSet.add(type + "-" + colors[15-m]);
                m++;
            }
        }


        int i = 1;
        String username = "unknown";

        nodeSeqMap = new HashMap<String, String>();
        for (String label : loginMap.keySet()) {
            nodeSeqMap.put(loginMap.get(label), i + "");
            nodeIdStr.append(loginMap.get(label)).append("||");
            nodeLabelStr.append(label).append("||");
            hiddenNodesStr.append("" + 0).append("||");
            userNameStr.append(username).append("||");

            nodeImageStr.append("0||");

            islabelStr.append("0||");
            String nodeType = nodeTypeMap.get(label);

            if (nodeTypeSet.size() == 1 && nodeTypeSet.contains("unknown")) {
                nodeTypesStr.append("unknown").append("||");
                nodeColorStr.append("0xFFFFFF").append("||");
                nodeGroupStr.append("unknown").append("||");
                nodeShapeStr.append("unknown").append("||");

            } else {
                nodeTypesStr.append(nodeType).append("||");
                nodeColorStr.append(colorMap.get(nodeType)).append("||");
                nodeGroupStr.append(nodeType).append("||");
                nodeShapeStr.append(nodeType).append("||");
            }

            i++;
        }

        Set<String> linkSet = new HashSet<String>();
        Set<String> etypeSet = new HashSet<String>();
        Map<String, String> ecolorMap = new HashMap<String, String>();


        for (String edge : edgeSet) {
            String[] edgeArray =  edge.split("`");
            etypeSet.add(edgeArray[3]);

        }

        if (etypeSet.size() == 1 && etypeSet.contains("unknown")) {
            linkSet.add(unknowLink);
        } else {
            int n = 0;
            for (String type : etypeSet) {


                ecolorMap.put(type, colors[15 - n]);
                linkSet.add(type + "-" + colors[15 - n]);
                n++;
            }
        }


        for (String edge : edgeSet) {

            String[] edgeArray = edge.split("`");

            String from = nodeSeqMap.get(loginMap.get(edgeArray[0]));
            String to = nodeSeqMap.get(loginMap.get(edgeArray[1]));


            edgeStr.append(from).append("-").append(to).append("||");

            edgeDirectionStr.append("" + 1).append("||");

            edgeWeightStr.append(edgeArray[2]).append("||");

            hiddenEdgesStr.append("" + 0).append("||");
            String edgeType = edgeArray[3];
            if (etypeSet.size() == 1 && etypeSet.contains("unknown")) {
                edgeColorStr.append("0x000000").append("||");
                edgeTypesStr.append("unknown").append("||");
                edgeLabelStr.append("unknown").append("||");
            } else {

                edgeColorStr.append(ecolorMap.get(edgeType)).append("||");
                edgeLabelStr.append(edgeType).append("||");
                edgeTypesStr.append(edgeType).append("||");
            }

        }


        String[] legendArray = legendSet.toArray(new String[legendSet.size()]);
        Arrays.sort(legendArray);


        for (int j = 0; j < legendArray.length; j++) {
            nodeLegendStr.append(legendArray[j]).append("||");
        }

        String[] groupArray = groupSet.toArray(new String[groupSet.size()]);
        Arrays.sort(groupArray);

        for (int j = 0; j < groupArray.length; j++) {
            groupLegendStr.append(groupArray[j]).append("||");
        }


        String[] linkArray = linkSet.toArray(new String[linkSet.size()]);
        Arrays.sort(linkArray);

        for (int j = 0; j < linkArray.length; j++) {
            linkLegendStr.append(linkArray[j]).append("||");
        }

        paraMap = new HashMap<String, String>();

        paraMap.put("login", nodeIdStr.toString());
        paraMap.put("nodes", nodeLabelStr.toString());
        paraMap.put("nodeTypes", nodeTypesStr.toString());
        paraMap.put("colors", nodeColorStr.toString());
        paraMap.put("shapeAttri", nodeShapeStr.toString());
        paraMap.put("groupAttri", nodeGroupStr.toString());
        paraMap.put("hiddenNodes", hiddenNodesStr.toString());
        paraMap.put("images", nodeImageStr.toString());
        paraMap.put("username", userNameStr.toString());

        paraMap.put("edges", edgeStr.toString());
        paraMap.put("edgeTypes", edgeTypesStr.toString());

        paraMap.put("edgeDirection", edgeDirectionStr.toString());

        paraMap.put("edgeTypeDis", edgeLabelStr.toString());

        paraMap.put("strengths", edgeWeightStr.toString().replaceAll("f", ""));

        paraMap.put("edgeColors", edgeColorStr.toString());
        paraMap.put("hiddenEdges", hiddenEdgesStr.toString());

        paraMap.put("group", groupLegendStr.toString());        ///


        paraMap.put("thelegend", nodeLegendStr.toString());
        paraMap.put("linkLegend", linkLegendStr.toString());

        paraMap.put("title", "Custom Network");
        paraMap.put("link_prefix", "http://localhost:8400/_ciknow/vis_get_link_info.jsp?");
        paraMap.put("node_prefix", "http://localhost:8400/_ciknow/vis_get_node_info.jsp?");

        if (nodeTypeSet == null) {
            paraMap.put("colorBy", "unknown");
            paraMap.put("groupBy", "unknown");
            paraMap.put("shapeBy", "unknown");
        } else {
            paraMap.put("colorBy", "NodeType");
            paraMap.put("groupBy", "NodeType");
            paraMap.put("shapeBy", "NodeType");
        }

        paraMap.put("hideNodeLabel", "0");

        isXml = false;
        /////////////////////////////////////////
      //  for (String n : paraMap.keySet()) {
       //     System.out.println(n + ":" + paraMap.get(n));
      //  }
        ////////////////////////////////////////

        frame.processData(paraMap, isXml);
    }


    public void xml2Ciknow(Map<String, String> propertyMap) {

        lastpropertyMap = propertyMap;
        StringBuilder nodeIdStr = new StringBuilder();
        StringBuilder nodeTypesStr = new StringBuilder();
        StringBuilder nodeLabelStr = new StringBuilder();
        StringBuilder hiddenNodesStr = new StringBuilder();
        StringBuilder nodeColorStr = new StringBuilder();
        StringBuilder nodeShapeStr = new StringBuilder();
        StringBuilder nodeGroupStr = new StringBuilder();

        StringBuilder userNameStr = new StringBuilder();
        StringBuilder nodeImageStr = new StringBuilder();

        StringBuilder islabelStr = new StringBuilder();

        StringBuilder edgeStr = new StringBuilder();
        StringBuilder edgeTypesStr = new StringBuilder();
        StringBuilder edgeDirectionStr = new StringBuilder();
        StringBuilder edgeWeightStr = new StringBuilder();
        StringBuilder edgeLabelStr = new StringBuilder();
        StringBuilder hiddenEdgesStr = new StringBuilder();
        StringBuilder edgeColorStr = new StringBuilder();
        StringBuilder nodeLegendStr = new StringBuilder();
        StringBuilder groupLegendStr = new StringBuilder();
        StringBuilder size1Str = new StringBuilder();
        StringBuilder size2Str = new StringBuilder();

        StringBuilder linkLegendStr = new StringBuilder();
        Set<String> legendSet = new HashSet<String>();
        Set<String> groupSet = new HashSet<String>();
        Set<String> typeSet = new HashSet<String>();
        Set<String> colorBySet = new HashSet<String>();
        Set<String> groupBySet = new HashSet<String>();
        Map<String, String> colorMap = new HashMap<String, String>();

        String[] colors = {"0xff8c69", "0xa4d3ee", "0xffff66", "0xDAC4E5", "0xB272A6", "0xE0E0E0", "0x8FBC8F", "0xEE9A49",
                "0x2F4F4F", "0x6B8E23", "0xDAA520", "0xFF69B4", "0x4876FF", "0x8DB6CD", "0xB452CD", "0xCD8500"};


        String unknowStr = "unknown-0xFFFFFF";
        String unknowLink = "unknown-0x000000";
        for (Iterator iter = g.nodes(); iter.hasNext();) {
            Node node = (Node) iter.next();

            //node color
            try {

                colorBySet.add(node.getString(propertyMap.get("color")));
            } catch (Exception e) {
                colorBySet.add("unknown");
            }

            //node group
            try {

                groupBySet.add(node.getString(propertyMap.get("group")));
            } catch (Exception e) {
                groupBySet.add("unknown");
            }

        }


        if (colorBySet.size() == 1 && colorBySet.contains("unknown")) {
            legendSet.add(unknowStr);
        } else {
            int m = 0;
            for (String type : colorBySet) {

                legendSet.add(type + "-" + colors[m]);
                colorMap.put(type, colors[m]);
                // groupSet.add(type + "-" + colors[15-m]);
                m++;
            }
        }


        if (groupBySet.size() == 1 && groupBySet.contains("unknown")) {
            groupSet.add(unknowStr);
        } else {
            int m = 0;
            for (String type : groupBySet) {

                groupSet.add(type + "-" + colors[15 - m]);
                m++;
            }
        }


        int i = 1;
        nodeAttri = new HashMap<String, List<String>>();

        if (propertyMap.get("size1") != null) {
            if (!propertyMap.get("size1").equalsIgnoreCase("none"))
                size1Str.append(propertyMap.get("size1")).append("||");
        }
        if (propertyMap.get("size2") != null) {
            if (!propertyMap.get("size2").equalsIgnoreCase("none"))
                size2Str.append(propertyMap.get("size2")).append("||");
        }
        for (Iterator iter = g.nodes(); iter.hasNext();) {

            Node node = (Node) iter.next();
            // save all nodeinfo
            int nodeattriCount = node.getColumnCount();
            List<String> atriList = new ArrayList<String>();
            for (int k = 0; k < nodeattriCount; k++) {
               // System.out.println("node.getColumnName(k):" + node.getColumnName(k));
                if (node.getString(k) != null)
                    atriList.add(node.getColumnName(k) + "||" + node.getString(k));

            }

            nodeAttri.put("" + i, atriList);
            nodeIdStr.append("" + i).append("||");
            if(propertyMap.get("label").equals("id")) {

                    for(String id: id_rowMap.keySet()){
                            if(node.getRow()== id_rowMap.get(id))

                        nodeLabelStr.append(id).append("||");
                    }
            }
            else{
                 if(node.getString(propertyMap.get("label")) == null)
                nodeLabelStr.append("unknown").append("||");
                else
                  nodeLabelStr.append(node.getString(propertyMap.get("label"))).append("||");
            }
            hiddenNodesStr.append("" + 0).append("||");
            String username = "";
            try {

                username = node.getString("username");
            } catch (Exception e) {
                username = "unknown";
            }

            userNameStr.append(username).append("||");

            nodeImageStr.append("0||");

            islabelStr.append("0||");

            try {


                nodeTypesStr.append(node.getString(propertyMap.get("nodeType"))).append("||");
            } catch (Exception e) {
                nodeTypesStr.append("unknown").append("||");
            }

            //node color
            try {

                nodeColorStr.append(colorMap.get(node.getString(propertyMap.get("color")))).append("||");
            } catch (Exception e) {
                nodeColorStr.append("0xFFFFFF").append("||");
            }

            //node group
            try {

                nodeGroupStr.append(node.getString(propertyMap.get("group"))).append("||");
            } catch (Exception e) {
                nodeGroupStr.append("unknown").append("||");
            }

            //node shape
            try {

                nodeShapeStr.append(node.getString(propertyMap.get("shape"))).append("||");
            } catch (Exception e) {
                nodeShapeStr.append("unknown").append("||");
            }

            //node size
            try {
                if (!propertyMap.get("size1").equalsIgnoreCase("none"))
                    size1Str.append(node.getString(propertyMap.get("size1"))).append("||");
            } catch (Exception e) {
                size1Str.append("1.0").append("||");
            }

            try {
                if (!propertyMap.get("size2").equalsIgnoreCase("none"))
                    size2Str.append(node.getString(propertyMap.get("size2"))).append("||");
            } catch (Exception e) {
                size2Str.append("1.0").append("||");
            }


            i++;
        }

        Set<String> linkSet = new HashSet<String>();
        Set<String> etypeSet = new HashSet<String>();
        Map<String, String> ecolorMap = new HashMap<String, String>();


        for (Iterator iter = g.edges(); iter.hasNext();) {

            Edge edge = (Edge) iter.next();

            try {

                etypeSet.add(edge.getString(propertyMap.get("edgeType")));
            } catch (Exception e) {
                etypeSet.add("unknown");
            }

        }

        if (etypeSet.size() == 1 && etypeSet.contains("unknown")) {
            linkSet.add(unknowLink);
        } else {
            int n = 0;
            for (String type : etypeSet) {


                ecolorMap.put(type, colors[15 - n]);
                linkSet.add(type + "-" + colors[15 - n]);
                n++;
            }
        }


        for (Iterator iter = g.edges(); iter.hasNext();) {

            Edge edge = (Edge) iter.next();

            int from = edge.getInt("source") + 1;
            int to = edge.getInt("target") + 1;

            edgeStr.append(from).append("-").append(to).append("||");

            edgeDirectionStr.append("" + 1).append("||");
            try {
                if (propertyMap.get("weight").equals("unknown"))
                    edgeWeightStr.append("1.0").append("||");
                else
                    edgeWeightStr.append(edge.getString(propertyMap.get("weight"))).append("||");
            } catch (Exception e) {
                edgeWeightStr.append("1.0").append("||");
            }

            hiddenEdgesStr.append("" + 0).append("||");

            //edge color
            try {

                edgeColorStr.append(ecolorMap.get(edge.getString(propertyMap.get("edgeType")))).append("||");
            } catch (Exception e) {
                edgeColorStr.append("0x000000").append("||");
            }

            try {

                edgeLabelStr.append(edge.getString(propertyMap.get("edgeType"))).append("||");
                edgeTypesStr.append(edge.getString(propertyMap.get("edgeType"))).append("||");
            } catch (Exception e) {
                edgeTypesStr.append("unknown").append("||");
                edgeLabelStr.append("unknown").append("||");
            }
        }


        String[] legendArray = legendSet.toArray(new String[legendSet.size()]);
        Arrays.sort(legendArray);


        for (int j = 0; j < legendArray.length; j++) {
            nodeLegendStr.append(legendArray[j]).append("||");
        }

        String[] groupArray = groupSet.toArray(new String[groupSet.size()]);
        Arrays.sort(groupArray);

        for (int j = 0; j < groupArray.length; j++) {
            groupLegendStr.append(groupArray[j]).append("||");
        }


        String[] linkArray = linkSet.toArray(new String[linkSet.size()]);
        Arrays.sort(linkArray);

        for (int j = 0; j < linkArray.length; j++) {
            linkLegendStr.append(linkArray[j]).append("||");
        }

        paraMap = new HashMap<String, String>();

        paraMap.put("login", nodeIdStr.toString());
        paraMap.put("nodes", nodeLabelStr.toString());
        paraMap.put("nodeTypes", nodeTypesStr.toString());
        paraMap.put("colors", nodeColorStr.toString());
        paraMap.put("shapeAttri", nodeShapeStr.toString());
        paraMap.put("groupAttri", nodeGroupStr.toString());
        paraMap.put("hiddenNodes", hiddenNodesStr.toString());
        paraMap.put("images", nodeImageStr.toString());
        paraMap.put("username", userNameStr.toString());

        paraMap.put("edges", edgeStr.toString());
        paraMap.put("edgeTypes", edgeTypesStr.toString());

        paraMap.put("edgeDirection", edgeDirectionStr.toString());

        paraMap.put("edgeTypeDis", edgeLabelStr.toString());

        paraMap.put("strengths", edgeWeightStr.toString().replaceAll("f", ""));

        paraMap.put("edgeColors", edgeColorStr.toString());
        paraMap.put("hiddenEdges", hiddenEdgesStr.toString());

        paraMap.put("group", groupLegendStr.toString());        ///

        if (propertyMap.get("size1") != null) {
            paraMap.put("nodeSize", size1Str.toString());

        }
        if (propertyMap.get("size2") != null)
            paraMap.put("nodeSize2", size2Str.toString());

        paraMap.put("thelegend", nodeLegendStr.toString());
        paraMap.put("linkLegend", linkLegendStr.toString());

        paraMap.put("title", "Custom Network");
        paraMap.put("link_prefix", "http://localhost:8400/_ciknow/vis_get_link_info.jsp?");
        paraMap.put("node_prefix", "http://localhost:8400/_ciknow/vis_get_node_info.jsp?");

        if (propertyMap.get("color") == null)
            paraMap.put("colorBy", "unknown");
        else
            paraMap.put("colorBy", propertyMap.get("color"));
        if (propertyMap.get("group") == null)
            paraMap.put("groupBy", "unknown");
        else
            paraMap.put("groupBy", propertyMap.get("group"));
        if (propertyMap.get("shape") == null)
            paraMap.put("shapeBy", "unknown");
        else
            paraMap.put("shapeBy", propertyMap.get("shape"));

        paraMap.put("hideNodeLabel", "0");

        isXml = true;

        frame.processData(paraMap, isXml);
    }

    private boolean isZipFile(File file) {
        boolean zipfile = false;
        try {

            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            int test = in.readInt();
            in.close();
            if (test == 0x504b0304)
                zipfile = true;

        } catch (Exception e) {

        }

        return zipfile;
    }

    private void importData(File myhtml) {
        String separator = System.getProperty("file.separator");
        int index = myhtml.toString().lastIndexOf(separator);
        imageLocation = myhtml.toString().substring(0, index);
        fileName = myhtml.toString().substring(index + 1);

        try {

            FileInputStream fileinput = null;
            BufferedInputStream mybuffer = null;
            DataInputStream datainput = null;

            fileinput = new FileInputStream(myhtml);
            mybuffer = new BufferedInputStream(fileinput);

            datainput = new DataInputStream(mybuffer);
            boolean ciknowFile = false;
            paraMap = new HashMap<String, String>();

            while (datainput.available() != 0) {

                String lineStr = datainput.readLine();
                if (lineStr.contains("<param name=")) {
                    int nameStart = lineStr.indexOf("=");
                    int nameEnd = lineStr.indexOf("value");

                    String name = lineStr.substring(nameStart + 1, nameEnd - 1).trim();
                    String finalStr = lineStr.substring(nameEnd + 7, lineStr.length() - 2);
                    paraMap.put(name, finalStr);
                    ciknowFile = true;
                }
            }

            fileinput.close();
            mybuffer.close();
            datainput.close();

            if (!ciknowFile) {

                Map<String, String> loginMap = new HashMap<String, String>();
                Map<String, String> nodeTypeMap = new HashMap<String, String>();
                Set<String> edgeMap = new HashSet<String>();

                if (isZipFile(myhtml)) {  // read di file in a zip file
                    ZipFile zf = new ZipFile(myhtml);

                    try {
                        for (Enumeration<? extends ZipEntry> e = zf.entries();
                             e.hasMoreElements();) {
                            ZipEntry ze = e.nextElement();

                            String name = ze.getName();
                            if (name.endsWith(".txt")) {
                                // read from 'in'
                                InputStream in = zf.getInputStream(ze);
                                BufferedInputStream mybuffer1 = new BufferedInputStream(in);

                                DataInputStream datainput1 = new DataInputStream(mybuffer1);

                                readDL(name, datainput1, loginMap, nodeTypeMap, edgeMap);
                                in.close();
                                mybuffer1.close();
                                datainput1.close();
                            }
                        }
                    } finally {
                        zf.close();
                    } // //zip file end
                } else { // no zip dl file
                      if (myhtml.toString().endsWith(".txt")) {

                    FileInputStream fileinput2 = new FileInputStream(myhtml);
                    BufferedInputStream mybuffer2 = new BufferedInputStream(fileinput2);

                    DataInputStream datainput2 = new DataInputStream(mybuffer2);
                    readDL(myhtml.toString(), datainput2, loginMap, nodeTypeMap, edgeMap);
                    fileinput2.close();
                    mybuffer2.close();
                    datainput2.close();
                      }
                }


                dL2Ciknow(loginMap, nodeTypeMap, edgeMap);
            }

        } catch (Exception e) {
                   System.out.println("error reading data: " + e);
                 //  dataInvalid = true;
                 //   JOptionPane.showMessageDialog(frame, "No Valid Data!", "Data Open Error",
                  //            JOptionPane.ERROR_MESSAGE);

               }


            if (paraMap.size() > 0) {
                frame.processData(paraMap, isXml);


            }


    }

    public boolean getDataStatus(){
        return dataInvalid;
    }
    private void readDL(String name, DataInputStream datainput1, Map<String, String> loginMap, Map<String, String> nodeTypeMap, Set<String> edgeMap) {

        //dl__Author__Authorship__Publication.embedded
        String nodeType1 = "unknown";
        String edgeType = "unknown";
        String nodeType2 = "unknown";
        String[] title = name.split("__");   //
        if (title.length == 4) {
            nodeType1 = title[1];
            edgeType = title[2];
            nodeType2 = title[3].replaceAll(".embedded.txt", "");
        }

        try {

            int labelStatus=0;

            while (datainput1.available() != 0) {
                String linre = datainput1.readLine();
                if (linre.contains("labels") && linre.contains("embedded")) {
                    labelStatus = 1;    // label embedded
                    break;
                }else  if (linre.contains("labels:") && !linre.contains("embedded")) {
                     labelStatus = 2;  // label not embedded
                    break;
                } else  if (linre.contains("data:")) {
                     labelStatus = 3;   // not sure
                    break;
                }
            }

            if ( labelStatus ==1 || labelStatus ==3 ) {
                readEmbeddedDL(datainput1, nodeType1, nodeType2, edgeType, loginMap, nodeTypeMap, edgeMap);

            } // no embedded
            else if(labelStatus ==2){
                readNoEmbDL(datainput1, nodeType1, nodeType2, edgeType, loginMap, nodeTypeMap, edgeMap);

            }

        } catch (Exception e) {

        }
    }

    private void readEmbeddedDL(DataInputStream datainput1, String nodeType1, String nodeType2, String edgeType, Map<String, String> loginMap, Map<String, String> nodeTypeMap, Set<String> edgeMap) {


        int i = 1;
        int j = 0;
         int m = 0;
        try {
            while (datainput1.available() != 0) {
                j = i + 1;
                String linre = datainput1.readLine();
                if (!linre.contains("data:")) {
                    String[] data = linre.split(" ");
                    loginMap.put(data[0], i + "");
                    loginMap.put(data[1], j + "");
                    nodeTypeMap.put(data[0], nodeType1);
                    nodeTypeMap.put(data[1], nodeType2);
                    // if dl file contains weight info and weight != 0
                    if (data.length == 3 ){
                        if(!data[2].equalsIgnoreCase("0"))
                            edgeMap.add(data[0] + "`" + data[1] + "`" + data[2] + "`" + edgeType);
                    }else
                        edgeMap.add(data[0] + "`" + data[1]+ "`" + "1.0" + "`" + edgeType);

                }
                i = i + 2;
               m++;
            } //while

        } catch (Exception e) {
             System.out.println(" exception from read embeddedDL: " + e);
        }

    }

    private void readNoEmbDL(DataInputStream datainput1, String nodeType1, String nodeType2, String edgeType, Map<String, String> loginMap, Map<String, String> nodeTypeMap, Set<String> edgeMap) {
           Map<String, String> labelMap = new HashMap<String, String>();
        List<String> labelList = new ArrayList<String>();
        List<String> labelList2 = new ArrayList<String>();

         boolean hasNodeType2 = false;
           try {

            while (datainput1.available() != 0) {

                String linre = datainput1.readLine();
                 if (!linre.contains("data:") && !linre.equalsIgnoreCase("col labels:")){
                    String[] label =  linre.split(",");
                     for(int d = 0; d < label.length; d++){
                          labelList.add(label[d]);

                     }
                 }
                 if(linre.equalsIgnoreCase("col labels:")){
                          hasNodeType2 = true;
                              break;
                 }
                if(!hasNodeType2){
                 if (linre.contains("data:"))
                  break;
                }


            } //while
        } catch (Exception e) {

        }

        if(hasNodeType2){
           try {

            while (datainput1.available() != 0) {

                String linre = datainput1.readLine();

                  if (!linre.contains("data:")){
                    String[] label =  linre.split(",");
                     for(int d = 0; d < label.length; d++){

                          labelList2.add(label[d]);

                     }
                 }
               if (linre.contains("data:"))
                  break;


            } //while
        } catch (Exception e) {

        }

        }
        int k = 1;
        for(String label: labelList){

            loginMap.put(label, k + "");
            labelMap.put(k+ "", label);
            nodeTypeMap.put(label, nodeType1);
            k++;
        }

         if(hasNodeType2){
            for(String label: labelList2){

            loginMap.put(label, k + "");
            labelMap.put(k+ "", label);
            nodeTypeMap.put(label, nodeType2);

            k++;
        }
         }

           int i = 1;
           int j = 0;

           try {
               while (datainput1.available() != 0) {
                   j = i + 1;
                   String linre = datainput1.readLine();

                       String[] data = linre.split(" ");
                       String label1 = labelMap.get(data[0]);
                       String label2 = labelMap.get(data[1]);
                       if (data.length == 3)
                           edgeMap.add(label1 + "`" + label2+ "`" + data[2] + "`" + edgeType);
                       else
                           edgeMap.add(label1 + "`" + label2 + "`" + "1.0" + "`" + edgeType);



                   i = i + 2;
               } //while
           } catch (Exception e) {

            System.out.println("Exception when reading no embedded Dl file: " + e);
           }

       }


    public Map<String, List<String>> getNodeAttriMap() {
        return nodeAttri;
    }



    public String getImageLocation() {
        return imageLocation;
    }

    public String selectedFile() {

        return fileName;
    }

    public Map<String, String> getNodePropMap() {
        return propertyTypeMap;
    }

    public Map<String, String> getEdgePropMap() {
        return epropertyTypeMap;
    }

    public List<String> getEdgeProp() {
        return edgeProperties;
    }

    public List<String> getNodeProp() {
        return nodeProperties;
    }

    public Map<String, String> getLastPropMap() {
        return lastpropertyMap;
    }
   public Map<String, String> getParaMap() {
        return paraMap;
    }
   /* public void messagedis(){
        System.out.println("message");
          frame.loadStatus.setText("<html><font color=\"red\"><b>Processing data, please wait...</b><font></html>");
    }  */
}
