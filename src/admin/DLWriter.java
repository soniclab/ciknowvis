package admin;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 4, 2010
 * Time: 3:53:54 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * the code is modified based on DLWriter.java from Ciknow. the original author is York Yao.
 */

import visual.GraphView;

import java.io.*;

import java.util.*;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import prefuse.data.Graph;
import prefuse.data.Edge;
import prefuse.data.Node;


public class DLWriter {

    private Graph g;

    public DLWriter(GraphView gv) {
        g = gv.getCurrentGraph();
    }

    public void write(File f, boolean labelEmbedded) throws IOException {

        OutputStream os = new FileOutputStream(f);
        ZipOutputStream zos = new ZipOutputStream(os);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(zos, "UTF-8"));

        Map<String, Map<NodeTypePair, List<Edge>>> edgesByType = classify();
        Map<String, Set<Node>> typeToNodesMap = null;
        //if (showIsolate)
        typeToNodesMap = classifyNodes();

        for (String edgeType : edgesByType.keySet()) {
            Map<NodeTypePair, List<Edge>> edgesByNTP = edgesByType.get(edgeType);

            for (NodeTypePair ntp : edgesByNTP.keySet()) {
                List<Edge> list = edgesByNTP.get(ntp);

                String entryName = "dl__" + ntp.ftype + "__" + edgeType + "__" + ntp.ttype;
                if (labelEmbedded)
                entryName += ".embedded.txt";
                else entryName += ".txt";
                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);

                String content = "";

                if (ntp.ftype.equals(ntp.ttype)) {
                    Collection<Node> nodeSet = new TreeSet<Node>(new NodeLabelComparator());
                  //  if (showIsolate)
                     nodeSet.addAll(typeToNodesMap.get(ntp.ftype));
                    content = getAsEdgeList1(nodeSet, list, true, labelEmbedded);
                } else {
                    Collection<Node> fnodeSet = new TreeSet<Node>(new NodeLabelComparator());
                    Collection<Node> tnodeSet = new TreeSet<Node>(new NodeLabelComparator());
                   // if (showIsolate) {
                        fnodeSet.addAll(typeToNodesMap.get(ntp.ftype));
                        tnodeSet.addAll(typeToNodesMap.get(ntp.ttype));
                  //  }
                    content = getAsEdgeList2(fnodeSet, tnodeSet, list, true, labelEmbedded);
                }

                writer.print(content);
                writer.flush();
            }
        }

        writer.close(); // this is necessary, otherwise the downloaded file is corrupted

    }

    private Map<String, Set<Node>> classifyNodes() {
        Map<String, Set<Node>> typeToNodesMap = new HashMap<String, Set<Node>>();

        for (Iterator iter = g.nodes(); iter.hasNext();) {
            Node node = (Node) iter.next();
            String nodeType = node.getString("nodeType");
            Set<Node> nodeSet = typeToNodesMap.get(nodeType);
            if (nodeSet == null) {
                nodeSet = new TreeSet<Node>(new NodeLabelComparator());
                typeToNodesMap.put(nodeType, nodeSet);
            }
            nodeSet.add(node);
        }

        return typeToNodesMap;
    }

    private Map<String, Map<NodeTypePair, List<Edge>>> classify() {
        Map<String, Map<NodeTypePair, List<Edge>>> edgesByType = new HashMap<String, Map<NodeTypePair, List<Edge>>>();

        for (Iterator iter = g.edges(); iter.hasNext();) {
            Edge edge = (Edge) iter.next();
            Map<NodeTypePair, List<Edge>> edgesByNTP = edgesByType.get(edge.getString("edgeType"));
            if (edgesByNTP == null) {
                edgesByNTP = new HashMap<NodeTypePair, List<Edge>>();
                edgesByType.put(edge.getString("edgeType"), edgesByNTP);
            }

            NodeTypePair ntp = new NodeTypePair(edge);
            List<Edge> list = edgesByNTP.get(ntp);
            if (list == null) {
                list = new LinkedList<Edge>();
                edgesByNTP.put(ntp, list);
            }

            list.add(edge);
        }

        return edgesByType;
    }

    /**
     * Write to dl format=edgelist1 (only single node type)
     *
     * @param edges
     * @param labelEmbedded
     * @return
     */
    private String getAsEdgeList1(Collection<Node> nodes, List<Edge> edges, boolean showIsolate, boolean labelEmbedded) {
        StringBuilder sb = new StringBuilder();

        if (!showIsolate) {
            nodes = new TreeSet<Node>(new NodeLabelComparator());
            for (Edge edge : edges) {
                nodes.add(edge.getSourceNode());
                nodes.add(edge.getTargetNode());
            }
        }
        sb.append("dl n=" + nodes.size() + " format=edgelist1\n");

        Set<String> labelSet = new HashSet<String>();
        if (showIsolate) {
            for (Node node : nodes) {
                labelSet.add(node.getString("label"));
            }
        }

        if (labelEmbedded) {
            sb.append("labels embedded\n");

            sb.append("data:\n");
            for (Edge edge : edges) {
                String flabel = edge.getSourceNode().getString("label");
                String tlabel = edge.getTargetNode().getString("label");
                sb.append(clean(flabel)).append(" ");
                sb.append(clean(tlabel)).append(" ");
                sb.append(edge.getString("weightString")).append("\n");

                if (showIsolate) {
                    labelSet.remove(flabel);
                    labelSet.remove(tlabel);
                }
            }

            if (showIsolate) {
                for (String label : labelSet) {
                    sb.append(clean(label)).append(" ").append(clean(label)).append(" ");
                    sb.append("0").append("\n");
                }
            }
        } else {
            Map<String, Integer> map = new HashMap<String, Integer>();
            int i = 1;
            sb.append("labels:\n");
            for (Node node : nodes) {
                String label = node.getString("label");
                map.put(label, i);

                label = clean(label);
                sb.append(label).append("\n");
                i++;
            }

            sb.append("data:\n");
            for (Edge edge : edges) {
                String flabel = edge.getSourceNode().getString("label");
                String tlabel = edge.getTargetNode().getString("label");
                sb.append(map.get(flabel)).append(" ");
                sb.append(map.get(tlabel)).append(" ");
                sb.append(edge.getString("weightString")).append("\n");

                if (showIsolate) {
                    labelSet.remove(flabel);
                    labelSet.remove(tlabel);
                }
            }

            if (showIsolate) {
                for (String label : labelSet) {
                    int index = map.get(label);
                    sb.append(index).append(" ").append(index).append(" ");
                    sb.append("0").append("\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Write to dl format=edgelist2 (2-mode, e.g. two different node types)
     *
     * @param edges
     * @param labelEmbedded
     * @return
     */
    private String getAsEdgeList2(Collection<Node> rowNodes, Collection<Node> colNodes, List<Edge> edges, boolean showIsolate, boolean labelEmbedded) {
        StringBuilder sb = new StringBuilder();
        if (!showIsolate) {

                    new TreeSet<Node>(new NodeLabelComparator());
            colNodes = new TreeSet<Node>(new NodeLabelComparator());
            for (Edge edge : edges) {
                rowNodes.add(edge.getSourceNode());
                colNodes.add(edge.getTargetNode());
            }
        }
        sb.append("dl nr=" + rowNodes.size() + " nc=" + colNodes.size() + " format=edgelist2\n");

        Set<String> rowLabelSet = new HashSet<String>();
        Set<String> colLabelSet = new HashSet<String>();
        if (showIsolate) {
            for (Node node : rowNodes) {
                rowLabelSet.add(node.getString("label"));
            }

            for (Node node : colNodes) {
                colLabelSet.add(node.getString("label"));
            }
        }

        if (labelEmbedded) {
            sb.append("labels embedded\n");

            sb.append("data:\n");
            for (Edge edge : edges) {
                String flabel = edge.getSourceNode().getString("label");
                String tlabel = edge.getTargetNode().getString("label");
                sb.append(clean(flabel)).append(" ");
                sb.append(clean(tlabel)).append(" ");
                sb.append(edge.getString("weightString")).append("\n");

                if (showIsolate) {
                    rowLabelSet.remove(flabel);
                    colLabelSet.remove(tlabel);
                }
            }

            if (showIsolate) {
                String rowDummy = rowNodes.iterator().next().getString("label");
                String colDummy = colNodes.iterator().next().getString("label");
                for (String label : rowLabelSet) {
                    sb.append(clean(label)).append(" ").append(clean(colDummy)).append(" ");
                    sb.append("0").append("\n");
                }
                for (String label : colLabelSet) {
                    sb.append(clean(rowDummy)).append(" ").append(clean(label)).append(" ");
                    sb.append("0").append("\n");
                }
            }
        }else {
            Map<String, Integer> rowMap = new HashMap<String, Integer>();
            int i = 1;
            sb.append("row labels:\n");
            for (Node node : rowNodes) {
                String label = node.getString("label");
                rowMap.put(label, i++);
                label = clean(label);
                sb.append(label).append("\n");
            }

            Map<String, Integer> colMap = new HashMap<String, Integer>();
            i = 1;
            sb.append("col labels:\n");
            for (Node node : colNodes) {
                String label = node.getString("label");
                colMap.put(label, i++);
                label = clean(label);
                sb.append(label).append("\n");
            }

            sb.append("data:\n");
            for (Edge edge : edges) {

                String flabel = edge.getSourceNode().getString("label");
                String tlabel = edge.getTargetNode().getString("label");
                sb.append(rowMap.get(flabel)).append(" ");
                sb.append(colMap.get(tlabel)).append(" ");
                sb.append(edge.getString("weightString")).append("\n");
            }
        } 

        return sb.toString();
    }

    private String clean(String label) {
        label = label.replaceAll("\\s+", "_");
        label = label.replaceAll(",", ".");
        label = label.replaceAll("\r", "(carriage return)");
        label = label.replaceAll("\n", "(new line)");
        label = label.replaceAll("\'", "_");
        label = label.replaceAll("\"", "_");
        label = "\"" + label + "\"";

        //label = label.replaceAll("-", "_");
        //label = label.replaceAll("\\(", "[");
        //label = label.replaceAll("\\)", "]");
        //if (label.length() > LABEL_LENGTH_MAX) label = label.substring(0, LABEL_LENGTH_MAX);
        return label;
    }


    private class NodeTypePair {
        private String ftype, ttype;

        public NodeTypePair(String ftype, String ttype) {
            this.ftype = ftype;
            this.ttype = ttype;
        }

        public NodeTypePair(Edge edge) {
            this(edge.getSourceNode().getString("nodeType"), edge.getTargetNode().getString("nodeType"));
        }


        public String getFtype() {
            return ftype;
        }

        public void setFtype(String ftype) {
            this.ftype = ftype;
        }

        public String getTtype() {
            return ttype;
        }

        public void setTtype(String ttype) {
            this.ttype = ttype;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((ftype == null) ? 0 : ftype.hashCode());
            result = prime * result + ((ttype == null) ? 0 : ttype.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final NodeTypePair other = (NodeTypePair) obj;
            if (ftype == null) {
                if (other.ftype != null)
                    return false;
            } else if (!ftype.equals(other.ftype))
                return false;
            if (ttype == null) {
                if (other.ttype != null)
                    return false;
            } else if (!ttype.equals(other.ttype))
                return false;
            return true;
        }

        public String toString() {
            return ftype + " --> " + ttype;
		}
	}
}


class NodeLabelComparator implements Comparator<Node>{

	public int compare(Node o1, Node o2) {
		Node node1 = (Node) o1;
		Node node2 = (Node) o2;
		return node1.getString("label").compareTo(node2.getString("label"));
	}

}
