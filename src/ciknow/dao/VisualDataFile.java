package ciknow.dao;

import visual.GraphView;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.visual.NodeItem;
import java.util.*;


import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jan 7, 2010
 * Time: 2:01:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisualDataFile {

    private StringBuilder nodeIdStr = new StringBuilder();
	private StringBuilder nodeTypesStr = new StringBuilder();
	private StringBuilder nodeLabelStr = new StringBuilder();
	private StringBuilder hiddenNodesStr = new StringBuilder();
	private StringBuilder nodeColorStr = new StringBuilder();
	private StringBuilder nodeShapeStr = new StringBuilder();
	private StringBuilder nodeGroupStr = new StringBuilder();
	private StringBuilder userNameStr = new StringBuilder();
    private StringBuilder nodeImageStr = new StringBuilder();
    private StringBuilder nodeSizeStr = new StringBuilder();
    private StringBuilder nodeSizeStr2 = new StringBuilder();
     private StringBuilder islabelStr = new StringBuilder();
    private StringBuilder xCoordinateStr = new StringBuilder();
    private StringBuilder yCoordinateStr = new StringBuilder();

    private StringBuilder edgeStr = new StringBuilder();
	private StringBuilder edgeTypesStr = new StringBuilder();
	private StringBuilder edgeDirectionStr = new StringBuilder();
	private StringBuilder edgeWeightStr = new StringBuilder();
	private StringBuilder edgeLabelStr = new StringBuilder();
	private StringBuilder hiddenEdgesStr = new StringBuilder();
	private StringBuilder edgeColorStr = new StringBuilder();
    private StringBuilder nodeLegendStr = new StringBuilder();
    private StringBuilder linkLegendStr = new StringBuilder();

    private  double width, height;
    private List<Long> nodeIds;
    private List<String> edgeFTTypes; // from-to-type

    public VisualDataFile()  {

    }

   public String  generateHtmlFile(GraphView gv, AppletDataHandler1 dh, String XMLStr, boolean forEmail){


       Map<String, double[]> nodeXYMap = gv.getXyCoordinate();
       System.out.println("1111 gv.getXyCoordinate(): " + nodeXYMap.size());

       width = gv.getDisplay().getBounds().getWidth();
        height = gv.getDisplay().getBounds().getHeight();

       Graph vg = gv.getGraph();
           for (Iterator iter = vg.nodes(); iter.hasNext();) {

                           NodeItem node = (NodeItem) iter.next();

                 if(node.isVisible()){
                    double[] xy = {node.getX(), node.getY()};
                nodeXYMap.put(node.getString("login"), xy);
                }

            }

        Graph g = gv.getCurrentGraph();
         int nodeSizeType = dh.getNodeSizeType();
       String isLabelNode = "0";
         if(gv.isLabelNode())
           isLabelNode = "1";

        if (nodeSizeType == 1 ||  nodeSizeType == 2)
           nodeSizeStr.append(dh.getSizeLabel()).append("||");
           if (nodeSizeType == 2)
            nodeSizeStr2.append(dh.getSize2Label()).append("||");

       String colorBy = dh.getColorQuestion();

       Set<String> legendSet = new HashSet<String>();
        nodeIds = new ArrayList<Long>();

         for (Iterator iter = g.nodes(); iter.hasNext();) {

           Node node = (Node) iter.next();
           nodeIds.add(Long.parseLong(node.getString("login")));

           nodeIdStr.append(node.getString("login")).append("||");
           nodeTypesStr.append(node.getString("nodeType")).append("||");
           nodeLabelStr.append(node.getString("label")).append("||");
           nodeShapeStr.append(node.getString("shapeAttri")).append("||");
           nodeGroupStr.append(node.getString("groupAttri")).append("||");
            hiddenNodesStr.append(node.getString("hiddenItem")).append("||");
            nodeColorStr.append(node.getString("nodeColor")).append("||");
             String    nodecolorStr = null;
             if (!colorBy.equalsIgnoreCase("NodeType"))
                    nodecolorStr = node.getString("colorAttri");
             else
                    nodecolorStr = node.getString("nodeType");

            legendSet.add(nodecolorStr + "-" + node.getString("nodeColor"));

             userNameStr.append(node.getString("userName")).append("||");
             if(node.getString("image") == null)
            nodeImageStr.append("0||");
            else
             nodeImageStr.append("1||");
           if (nodeSizeType == 1 ||  nodeSizeType == 2)
           nodeSizeStr.append(node.getDouble("nodeSize") + "").append("||");
           if (nodeSizeType == 2)
            nodeSizeStr2.append(node.getDouble("nodeSize2")+ "").append("||");
           double[] xy =nodeXYMap.get(node.getString("login"));
            if(isLabelNode.equalsIgnoreCase("1")){
                if(node.getString("focusLabel") == null)
                 islabelStr.append("0||");
                else
                 islabelStr.append("1||");
            }

             xCoordinateStr.append(xy[0]).append("||");
                yCoordinateStr.append(xy[1]).append("||");

         }

       Set<String> linkSet = new HashSet<String>();
       edgeFTTypes = new ArrayList<String>();
       for (Iterator iter = g.edges(); iter.hasNext();) {

           Edge edge= (Edge) iter.next();

           String edgePair = edge.getString("nodePairType");
           int fromid = Integer.parseInt(edgePair.substring(0, edgePair.indexOf('-')));
           int toid = Integer.parseInt(edgePair.substring(1 + edgePair.indexOf('-'), edgePair.indexOf('&')));
           String[] logins = dh.getLogins();

           String edgeS = logins[fromid - 1] + "&" + logins[toid - 1] + "&" + edgePair.substring(edgePair.indexOf('&') + 1);
           //System.out.println("edgeFTType: " + edgeS);
           edgeFTTypes.add(edgeS);

           int from =  edge.getInt("sourceKey") + 1;
           int to =  edge.getInt("targetKey") + 1;
            edgeStr.append(from).append("-").append(to).append("||");
           edgeTypesStr.append(edge.getString("edgeType")).append("||");
           edgeDirectionStr.append(edge.getString("direction")).append("||");
          edgeWeightStr.append(edge.getString("weightString")).append("||");
           edgeLabelStr.append(edge.getString("edgeTypeDis")).append("||");
            hiddenEdgesStr.append(edge.getString("hiddenItem")).append("||");
             edgeColorStr.append(edge.getString("edgeColor")).append("||");
           linkSet.add(edge.getString("edgeTypeDis") + "-" + edge.getString("edgeColor"));
        }

       String[] legendArray = legendSet.toArray(new String[legendSet.size()]);
        Arrays.sort(legendArray);

        for (int j = 0; j < legendArray.length; j++) {
            nodeLegendStr.append(legendArray[j]).append("||");
        }


      String[] linkArray = linkSet.toArray(new String[linkSet.size()]);
        Arrays.sort(linkArray);

        for (int j = 0; j < linkArray.length; j++) {
            linkLegendStr.append(linkArray[j]).append("||");
        }


        String disNodeBy = gv.nodedisStatus() + "";
       String  isGroupNode="0";

       String isLabelEdge = "0";
       String isWeightEdge  ="0";

        if(gv.isGroupNode()== 5)
            isGroupNode = "1";
       if(gv.isLabelEdge())
        isLabelEdge = "1";
       if(gv.isWeightEdge())
          isWeightEdge  ="1";
        String  os = dh.getOS();
           if(os == null)
             os = "Win";

       return composeHtml(forEmail, XMLStr, nodeSizeType, dh.getGroupStr(), dh.getLinkCGIPrefix(), dh.getNodeCGIPrefix(),
               dh.getTitle(), dh.getColorQuestion(), dh.getGroupBy(), dh.getShapeBy(), os, dh.getLabelHiden(), disNodeBy,isGroupNode, isLabelNode,isLabelEdge, isWeightEdge, dh.getImageLocation(), gv.imageSize );


    }


       private String composeHtml(boolean foremail, String XMLStr, int nodeSizeType, String groupStr,  String linkURLPrefix, String nodeURLPrefix,
                                  String title, String colorBy, String groupBy, String shapeBy, String os, String hideNodeLabel,
                                  String disNodeBy, String isGroupNode, String isLabelNode, String isLabelEdge, String isWeightEdge, String imageLocation, int imageSize) {
               StringBuilder html = new StringBuilder();
              
               if (nodeIdStr.length() > 0) {

                   html.append("<html>").append("\n");

                   html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>").append("\n");
                  
                   if(XMLStr != null || foremail){
                     if (os.equalsIgnoreCase("mac")){
                       html.append("<applet archive=\"./graphApplet5.jar, ./jh.jar, ./jhall.jar, ./jhbasic.jar, ./jsearch.jar,./swinglayout.jar,  ./prefuse5.jar\" code=\"admin.MainFrame.class\" codebase=\"http://ciknow.northwestern.edu/downloads/vis\" width=\"100%\" height=\"100%\">\n\n");
                   } else{
                       html.append("<applet archive=\"./graphApplet.jar, ./jh.jar, ./jhall.jar, ./jhbasic.jar, ./jsearch.jar, ./swinglayout.jar,  ./prefuse.jar\" code=\"admin.MainFrame.class\" codebase=\"http://ciknow.northwestern.edu/downloads/vis\" width=\"100%\" height=\"100%\">\n\n");
                   }  
                   } else{
                   if (os.equalsIgnoreCase("mac")){
                       html.append("<applet archive=\"./graphApplet5.jar, ./jh.jar, ./jhall.jar, ./jhbasic.jar, ./jsearch.jar, ./swinglayout.jar,  ./prefuse5.jar\" code=\"admin.MainFrame.class\" width=\"100%\" height=\"100%\">\n\n");
                   } else{
                       html.append("<applet archive=\"./graphApplet.jar, ./jh.jar, ./jhall.jar, ./jhbasic.jar, ./jsearch.jar, ./swinglayout.jar, ./prefuse.jar\" code=\"admin.MainFrame.class\" width=\"100%\" height=\"100%\">\n\n");
                   }
                   }
                   // node related
                   html.append("<param name=login value=\"" + nodeIdStr.toString() + "\">\n");
                   if(!hideNodeLabel.equalsIgnoreCase("2"))
                   html.append("<param name=nodes value=\"" + nodeLabelStr.toString().replace("\"", "'") + "\">\n");
                   html.append("<param name=nodeTypes value=\"" + nodeTypesStr.toString().replace("\"", "'") + "\">\n");
                   html.append("<param name=colors value=\"" + nodeColorStr.toString() + "\">\n");
                   html.append("<param name=shapeAttri value=\"" + nodeShapeStr.toString().replace("\"", "'") + "\">\n");
                   html.append("<param name=groupAttri value=\"" + nodeGroupStr.toString().replace("\"", "'") + "\">\n");
                   if (nodeSizeType == 1 ||  nodeSizeType == 2) html.append("<param name=nodeSize value=\"" + nodeSizeStr.toString() + "\">\n");
                   if (nodeSizeType == 2) html.append("<param name=nodeSize2 value=\"" + nodeSizeStr2.toString() + "\">\n");
                   html.append("<param name=hiddenNodes value=\"" + hiddenNodesStr.toString() + "\">\n");
                   html.append("<param name=images value=\"" + nodeImageStr.toString() + "\">\n");
                   html.append("<param name=username value=\"" + userNameStr.toString() + "\">\n");
                   if(isLabelNode.equalsIgnoreCase("1"))
                   html.append("<param name=nodelabel value=\"" + islabelStr.toString() + "\">\n");
                   html.append("<param name=x value=\"" + xCoordinateStr.toString().replace("\"", "'") + "\">\n");
                   html.append("<param name=y value=\"" + yCoordinateStr.toString().replace("\"", "'") + "\">\n\n");
                   // edge related
                   html.append("<param name=edges value=\"" + edgeStr.toString() + "\">\n");
                   html.append("<param name=edgeTypes value=\"" + edgeTypesStr.toString().replace("\"", "'") + "\">\n");
                   html.append("<param name=edgeDirection value=\"" + edgeDirectionStr.toString() + "\">\n");
                   html.append("<param name=edgeTypeDis value=\"" + edgeLabelStr.toString().replace("\"", "'") + "\">\n");
                   html.append("<param name=strengths value=\"" + edgeWeightStr.toString() + "\">\n");
                   html.append("<param name=edgeColors value=\"" + edgeColorStr.toString() + "\">\n");
                   html.append("<param name=hiddenEdges value=\"" + hiddenEdgesStr.toString() + "\">\n\n");

                   // legends
                   html.append("<param name=group value=\"" + groupStr + "\">\n");
                   html.append("<param name=thelegend value=\"" + nodeLegendStr.toString() + "\">\n");
                   html.append("<param name=linkLegend value=\"" + linkLegendStr.toString() + "\">\n\n");

                   // general
                   html.append("<param name=title value=\"" + title + "\">\n");
                   html.append("<param name=link_prefix value=\"" + linkURLPrefix + "\">\n");
                   html.append("<param name=node_prefix value=\"" + nodeURLPrefix + "\">\n");
                   html.append("<param name=cgi_url value=\"\">\n");
                   html.append("<param name=colorBy value=\"" + colorBy + "\">\n");
                   html.append("<param name=groupBy value=\"" + groupBy + "\">\n");
                   html.append("<param name=shapeBy value=\"" + shapeBy + "\">\n");
                    if(disNodeBy.equalsIgnoreCase("4"))
                   html.append("<param name=imageSize value=\"" + imageSize + "\">\n");
                   html.append("<param name=os value=\"" + os + "\">\n");
                    html.append("<param name=hideNodeLabel value=\"" + hideNodeLabel + "\">\n");

                   html.append("<param name=default_width value=\"" + width + "\">\n");
                   html.append("<param name=default_height value=\"" + height + "\">\n");

                   //node and edge display format
                   // disNodeBy: 0--default, 1-- minNode, 2-- shaped, 3-- sized, 4--imaged
                   html.append("<param name=nodeBy value=\"" + disNodeBy + "\">\n");
                   html.append("<param name=groupNode value=\"" + isGroupNode + "\">\n");
                   html.append("<param name=labelNode value=\"" + isLabelNode + "\">\n");
                   html.append("<param name=labelEdge value=\"" + isLabelEdge + "\">\n");
                   html.append("<param name=weightEdge value=\"" + isWeightEdge + "\">\n");
                  if(disNodeBy.equalsIgnoreCase("4"))
                   html.append("<param name=imageLocation value=\"" +  imageLocation + "\">\n");
                   if(XMLStr != null)
                    html.append("<param name=graphML value=\"" +  XMLStr + "\">\n");
                   html.append("</applet>");
                   html.append("</html>");
               } else {
                   html.append("There is no valid node for visualization, please close this window and try again!");
               }

               return html.toString();      
           }

    public List<Long> getNodeIds(){
     return  nodeIds;
}
    public List<String> getEdgeFTTypes(){
       return edgeFTTypes;
}


}
