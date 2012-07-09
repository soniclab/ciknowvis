package admin;

import prefuse.data.io.AbstractGraphWriter;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.DataIOException;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.util.io.XMLWriter;

import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.PrintWriter;

import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 4, 2010
 * Time: 11:47:00 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * GraphWriter instance that writes a graph file formatted using the
 * GraphML file format. GraphML is an XML format supporting graph
 * structure and typed data schemas for both nodes and edges.
 *
 */
public class MyGraphMLWriter {

    /**
     * String tokens used in the GraphML format.
     */
   private  AppletDataHandler1 dh;
   private String xmlStr;
    public MyGraphMLWriter(AppletDataHandler1 dh)
       {
        this.dh = dh;
       }
    public interface Tokens extends GraphMLReader.Tokens  {
        public static final String GRAPHML = "graphml";

        public static final String GRAPHML_HEADER =
            "<graphml>\n\n";
    }

    /**
     * Map containing legal data types and their names in the GraphML spec
     */
    private static final HashMap TYPES = new HashMap();
    static {
        TYPES.put(int.class, Tokens.INT);
        TYPES.put(long.class, Tokens.LONG);
        TYPES.put(float.class, Tokens.FLOAT);
        TYPES.put(double.class, Tokens.DOUBLE);
        TYPES.put(boolean.class, Tokens.BOOLEAN);
        TYPES.put(String.class, Tokens.STRING);
    }

    /**
     * @see prefuse.data.io.GraphWriter#writeGraph(prefuse.data.Graph, java.io.OutputStream)
     */
    public void writeGraph(Graph graph, OutputStream os) throws DataIOException
    {
        // first, check the schemas to ensure GraphML compatibility
        Schema ns = graph.getNodeTable().getSchema();
        Schema es = graph.getEdgeTable().getSchema();
        checkGraphMLSchema(ns);
        checkGraphMLSchema(es);
    /*   if(os== null){
           ////////////////////////////////////////
           XMLStrGenerator xml = new  XMLStrGenerator(new StringBuffer());

        xml.begin(Tokens.GRAPHML_HEADER, 2);
        xml.println();

        // print graph contents
        xml.start(Tokens.GRAPH, Tokens.EDGEDEF,
            graph.isDirected() ? Tokens.DIRECTED : Tokens.UNDIRECTED);

        // print the nodes
        xml.comment("nodes");
        Iterator nodes = graph.nodes();
        while ( nodes.hasNext() ) {
            Node n = (Node)nodes.next();

            if ( ns.getColumnCount() > 0 ) {
                xml.start(Tokens.NODE, Tokens.ID, String.valueOf(n.getRow()));
                for ( int i=0; i<ns.getColumnCount(); ++i ) {
                    String field = ns.getColumnName(i);
                    String mapfield = field;
                    //// change property label for "groupAttri", "shapeAttri", "colorAttri", "nodeSize" , "nodeSize2"
                    // not show: "noLabel","disLabel", "focusLabel",  "hiddenItem", "nodeColor" , "x", "y"
              
                     if(!field.equals("x") && !field.equals("y") )
                    xml.contentTag(Tokens.DATA, Tokens.KEY, mapfield,
                                   n.getString(field));
                }
                xml.end();
            } else {

                xml.tag(Tokens.NODE, Tokens.ID, String.valueOf(n.getRow()));
            }
        }

        // add a blank line
        xml.println();

        // print the edges
        String[] attr = new String[]{Tokens.ID, Tokens.SOURCE, Tokens.TARGET};
        String[] vals = new String[3];

        xml.comment("edges");
        Iterator edges = graph.edges();
        while ( edges.hasNext() ) {
            Edge e = (Edge)edges.next();
            vals[0] = String.valueOf(e.getRow());
            vals[1] = String.valueOf(e.getSourceNode().getRow());
            vals[2] = String.valueOf(e.getTargetNode().getRow());

            if ( es.getColumnCount() > 2 ) {
                xml.start(Tokens.EDGE, attr, vals, 3);
                for ( int i=0; i<es.getColumnCount(); ++i ) {
                    String field = es.getColumnName(i);
                    if ( field.equals(graph.getEdgeSourceField()) ||
                         field.equals(graph.getEdgeTargetField()) )
                        continue;

                    xml.contentTag(Tokens.DATA, Tokens.KEY, field,
                                   e.getString(field));
                }
                xml.end();
            } else {
                xml.tag(Tokens.EDGE, attr, vals, 3);
            }
        }
        xml.end();

        // finish writing file
        xml.finish("</"+Tokens.GRAPHML+">\n");
        xmlStr =xml.getXMLStr();  */
           ///////////////////////////////////////////////////////////////
     //  }else{
        XMLWriter xml = new XMLWriter(new PrintWriter(os));
        xml.begin(Tokens.GRAPHML_HEADER, 2);

       // xml.comment("prefuse GraphML Writer | "
        //        + new Date(System.currentTimeMillis()));

        // print the graph schema

         printSchema(xml, Tokens.NODE, ns, null);
        printSchema(xml, Tokens.EDGE, es, new String[] {
            graph.getEdgeSourceField(), graph.getEdgeTargetField()
        });
        xml.println();

        // print graph contents
        xml.start(Tokens.GRAPH, Tokens.EDGEDEF,
            graph.isDirected() ? Tokens.DIRECTED : Tokens.UNDIRECTED);

        // print the nodes
        xml.comment("nodes");
        Iterator nodes = graph.nodes();
        while ( nodes.hasNext() ) {
            Node n = (Node)nodes.next();

            if ( ns.getColumnCount() > 0 ) {
                xml.start(Tokens.NODE, Tokens.ID, String.valueOf(n.getRow()));
                for ( int i=0; i<ns.getColumnCount(); ++i ) {
                    String field = ns.getColumnName(i);
                    String mapfield = field;
                    //// change property label for "groupAttri", "shapeAttri", "colorAttri", "nodeSize" , "nodeSize2"
                    // not show: "noLabel","disLabel", "focusLabel",  "hiddenItem", "nodeColor" , "x", "y"
               /*     if(field.equals("groupAttri"))
                       mapfield = dh.getGroupBy();
                    else if(field.equals("shapeAttri"))
                       mapfield = dh.getShapeBy();
                    else if(field.equals("colorAttri"))
                       mapfield = dh.getColorQuestion();
                   else if(field.equals("nodeSize"))
                       mapfield = dh.getSizeLabel();
                   else if(field.equals("nodeSize2"))
                       mapfield = dh.getSize2Label();   */
                     if(!field.equals("x") && !field.equals("y") )
                    xml.contentTag(Tokens.DATA, Tokens.KEY, mapfield,
                                   n.getString(field));
                }
                xml.end();
            } else {

                xml.tag(Tokens.NODE, Tokens.ID, String.valueOf(n.getRow()));
            }
        }

        // add a blank line
        xml.println();

        // print the edges
        String[] attr = new String[]{Tokens.ID, Tokens.SOURCE, Tokens.TARGET};
        String[] vals = new String[3];

        xml.comment("edges");
        Iterator edges = graph.edges();
        while ( edges.hasNext() ) {
            Edge e = (Edge)edges.next();
            vals[0] = String.valueOf(e.getRow());
            vals[1] = String.valueOf(e.getSourceNode().getRow());
            vals[2] = String.valueOf(e.getTargetNode().getRow());

            if ( es.getColumnCount() > 2 ) {
                xml.start(Tokens.EDGE, attr, vals, 3);
                for ( int i=0; i<es.getColumnCount(); ++i ) {
                    String field = es.getColumnName(i);
                    if ( field.equals(graph.getEdgeSourceField()) ||
                         field.equals(graph.getEdgeTargetField()) )
                        continue;

                    xml.contentTag(Tokens.DATA, Tokens.KEY, field,
                                   e.getString(field));
                }
                xml.end();
            } else {
                xml.tag(Tokens.EDGE, attr, vals, 3);
            }
        }
        xml.end();

        // finish writing file
        xml.finish("</"+Tokens.GRAPHML+">\n");
      // }
    }

    /**
     * Print a table schema to a GraphML file
     * @param xml the XMLWriter to write to
     * @param group the data group (node or edge) for the schema
     * @param s the schema
     */
    private void printSchema(XMLWriter xml, String group, Schema s,
                             String[] ignore)
    {

        String[] attr = new String[] {Tokens.ID, Tokens.FOR,
                Tokens.ATTRNAME, Tokens.ATTRTYPE };
        String[] vals = new String[4];

OUTER:
        for ( int i=0; i<s.getColumnCount(); ++i ) {
            vals[0] = s.getColumnName(i);
           
            for ( int j=0; ignore!=null && j<ignore.length; ++j ) {
                if ( vals[0].equals(ignore[j]) )
                    continue OUTER;
            }
           /* String mapAttri = vals[0];
             if(vals[0].equals("groupAttri"))
                       mapAttri = dh.getGroupBy();
                    else if(vals[0].equals("shapeAttri"))
                       mapAttri= dh.getShapeBy();
                    else if(vals[0].equals("colorAttri"))
                       mapAttri = dh.getColorQuestion();
                   else if(vals[0].equals("nodeSize"))
                      mapAttri = dh.getSizeLabel();
                   else if(vals[0].equals("nodeSize2"))
                       mapAttri = dh.getSize2Label();
            vals[0] = mapAttri;   */
            vals[1] = group;
            vals[2] = vals[0];
            vals[3] = (String)TYPES.get(s.getColumnType(i));
            Object dflt = s.getDefault(i);
            if(!vals[0].equals("x") && !vals[0].equals("y")) {
            if ( dflt == null ) {
                xml.tag(Tokens.KEY, attr, vals, 4);
            } else {
                xml.start(Tokens.KEY, attr, vals, 4);
                xml.contentTag(Tokens.DEFAULT, dflt.toString());
                xml.end();
            }
            }
        }
    }

    /**
     * Checks if all Schema types are compatible with the GraphML specification.
     * The GraphML spec only allows the types <code>int</code>,
     * <code>long</code>, <code>float</code>, <code>double</code>,
     * <code>boolean</code>, and <code>string</code>.
     * @param s the Schema to check
     */
    private void checkGraphMLSchema(Schema s) throws DataIOException {
        for ( int i=0; i<s.getColumnCount(); ++i ) {
            Class type = s.getColumnType(i);
            if ( TYPES.get(type) == null ) {
                throw new DataIOException("Data type unsupported by the "
                    + "GraphML format: " + type.getName());
            }
        }
    }


    public String getXML(){
        return xmlStr;
    }

} // end of class GraphMLWriter
