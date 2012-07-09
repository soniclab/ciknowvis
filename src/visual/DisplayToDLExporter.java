package visual;

import prefuse.util.io.SimpleFileFilter;
import prefuse.util.io.IOLib;
import prefuse.data.io.GraphMLWriter;
import prefuse.data.Graph;

import javax.swing.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

import ciknow.dao.VisualDataFile;
import data.AppletDataHandler1;
import admin.MyGraphMLWriter;
import admin.DLWriter;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 18, 2008
 * Time: 3:41:03 PM
 * To change this template use File | Settings | File Templates.
 */

public class DisplayToDLExporter {

    /**
     * The FileChooser to select the file to save to
     */
    protected JFileChooser chooser = null;

    //~--- Constructors -------------------------------------------------------

    public DisplayToDLExporter() {
    }

    //~--- Methods ------------------------------------------------------------

    /**
     * This method initiates the chooser components, detecting available image formats
     */
    protected void init() {

        // Initialize the chooser
        chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Export Graph Data file");
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.setFileFilter(new SimpleFileFilter("embedded.txt", "DL embedded format(*.embedded.txt)"));
        chooser.setFileFilter(new SimpleFileFilter("txt", "DL format(*.txt)"));
        chooser.setFileFilter(new SimpleFileFilter("xml", "graphML format(*.xml)"));
        chooser.setFileFilter(new SimpleFileFilter("html", "html format(*.html)"));
        chooser.setFileFilter(new SimpleFileFilter("ciknow", "ciknow format(*.ciknow)"));

    }

    /**
     * This method lets the user select the target file and exports the <code>Display</code>
     *
     * @paran display the <code>Display</code> to export
     */
    public void export(GraphView gv, AppletDataHandler1 dh, File openedFile) {

        //export(Display dis, Visualization vis, String nodes, String edges )

        // Initialize if needed
        if (chooser == null) {
            init();
        }

        // open image save dialog
        File f = null;
        int returnVal = chooser.showSaveDialog(gv);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        } else {
            return;
        }

        String format = ((SimpleFileFilter) chooser.getFileFilter()).getExtension();

        boolean success = false;


        if (format.equalsIgnoreCase("ciknow")) {
            try {
                VisualDataFile vd = new VisualDataFile();

                String dataStr = vd.generateHtmlFile(gv, dh, null, false);

                f = new File(f.toString() + "." + format);
                success = exportHTML(dataStr, f);

            } catch (Exception e) {
                System.out.println("Exception when generate html file: " + e);
                success = false;
            }
        } else if (format.equalsIgnoreCase("html")) {
            String XMLStr = null;

            try {
                InputStream in = new FileInputStream(openedFile);

                if (in != null) {
                    Writer writer = new StringWriter();

                    char[] buffer = new char[1024];
                    try {
                        Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        int n;
                        while ((n = reader.read(buffer)) != -1) {
                            writer.write(buffer, 0, n);
                        }
                    } catch (Exception e) {

                    }

                    in.close();

                    XMLStr = writer.toString();
                } else {
                    XMLStr = "";
                }

            } catch (Exception e) {

            }       

            try {
                VisualDataFile vd = new VisualDataFile();

                String a = XMLStr.replace("\"", "~");
                String dataStr = vd.generateHtmlFile(gv, dh, a, false);

                f = new File(f.toString() + "." + format);
                success = exportHTML(dataStr, f);

            } catch (Exception e) {
                System.out.println("Exception when generate html file: " + e);
                success = false;
            }


        } else if (format.equalsIgnoreCase("xml")) {
            if (openedFile.getName().endsWith("xml")) {

                BufferedWriter bw = null;

                try {

                    InputStream in = new FileInputStream(openedFile);
                    bw = new BufferedWriter(new FileWriter(f = new File(f.toString() + "." + format)));

                    StringBuilder builder = new StringBuilder();
                    int letter;

                    while ((letter = in.read()) != -1) {

                        bw.write((char) letter);
                        bw.flush();

                        builder.append((char) letter);


                    }

                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }


            } else {
               
                try {

                    MyGraphMLWriter gw = new MyGraphMLWriter(dh);
                    Graph g = gv.getCurrentGraph();

                    f = new File(f.toString() + "." + format);
                    gw.writeGraph(g, new FileOutputStream(f));
                    success = true;
                } catch (Exception e) {
                    System.out.println("Exception when generate GraphML file: " + e);
                    success = false;
                }
            }
        } else if (format.equalsIgnoreCase("embedded.txt") || format.equalsIgnoreCase("txt")) {
            try {
                DLWriter dw = new DLWriter(gv);

                if (format.equalsIgnoreCase("txt")) {
                    f = new File(f.toString() + ".dl.zip");
                    dw.write(f, false);
                } else {
                    f = new File(f.toString() + ".embedded.dl.zip");
                    dw.write(f, true);
                }
                success = true;
            } catch (Exception e) {
                System.out.println("Exception when generate DL file: " + e);
                success = false;
            }
        }

        // show result dialog on failure
        if (!success) {
            JOptionPane.showMessageDialog(gv, "Error Saving Data!", "Data Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

   /* private String getXMLStr(File openedFile){
        String XMLStr = null;
            try {

                InputStream in = new FileInputStream(openedFile);

                if (in != null) {
                    Writer writer = new StringWriter();

                    char[] buffer = new char[1024];
                    try {
                        Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        int n;
                        while ((n = reader.read(buffer)) != -1) {
                            writer.write(buffer, 0, n);
                        }
                    } catch (Exception e) {

                    }

                    in.close();

                    XMLStr = writer.toString();
                } else {
                    XMLStr = "";
                }
            } catch (Exception e) {

            }
        return XMLStr;
    } */

    private boolean exportHTML(String dataStr, File f) {

        try {
            StringBuffer sb = new StringBuffer();
            sb.append(dataStr);
            StringReader read = new StringReader(sb.toString());
            Writer osw = new OutputStreamWriter(new FileOutputStream(f), "UTF8");

            int ch;
            while ((ch = read.read()) != -1) {
                osw.write((char) ch);
            }
            osw.flush();
            osw.close();


            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /*  private boolean exportDLData(Visualization vis, String nodes, String edges, File f) {


       try {


                    StringBuffer sb = new StringBuffer();
                  TupleSet nodeTuple = vis.getVisualGroup(nodes);
                  int n = nodeTuple.getTupleCount();
                  sb.append("dl n =" + n + "\n format=edgelist1 \n labels embedded \n data: \n");
                TupleSet allTuple = vis.getVisualGroup(edges);

                            Iterator allIems = allTuple.tuples();

                            while (allIems.hasNext()) {

                                VisualItem item = (VisualItem) allIems.next();

                                Edge e = (Edge) item;
                                Node source = e.getSourceNode();
                                Node target = e.getTargetNode();

                                String sourceLabel = source.getString("disLabel");
                                String targetLabel = target.getString("disLabel");

                                String edge = '"' + sourceLabel +  '"' + "  " +    '"' + targetLabel +  '"' + "\n";
                                 sb.append(edge);
                            }//while




               StringReader read = new StringReader(sb.toString());
               Writer osw = new OutputStreamWriter(new FileOutputStream(f), "UTF8");

               int ch;
               while((ch = read.read() ) != -1){
                 osw.write((char) ch);
               }
                 osw.flush();
               osw.close();

              // System.out.println("NN" + nnId + ": raw data received and saved.");





           return true;
       } catch (Exception e) {
           e.printStackTrace();

           return false;
       }
   } */


}

