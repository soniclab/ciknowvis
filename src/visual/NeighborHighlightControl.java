package visual;


import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 23, 2008
 * Time: 1:36:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class NeighborHighlightControl extends ControlAdapter {

    private String sourceGroupName, targetGroupName, inEdgeGroupName, outEdgeGroupName, mutalEGroupName, undirectedEGroupName;

    private TupleSet sourceTupleSet, targetTupleSet,  InEdgeTupleSet, OutEdgeTupleSet, mutalETupleSet, undirectedETupleSet;
    private GraphView gv;
    private int recStatus, layoutNumber;
    private boolean showWeight;

    public NeighborHighlightControl(GraphView gv, Visualization visu, String[] groupNames, int recStatus, int layoutNumber,  boolean showWeight) {

        
        sourceGroupName = groupNames[0];
        targetGroupName = groupNames[1];

        inEdgeGroupName = groupNames[2];
        outEdgeGroupName = groupNames[3];
        mutalEGroupName = groupNames[4];
        undirectedEGroupName = groupNames[5];

        try {
            visu.addFocusGroup(sourceGroupName);
            visu.addFocusGroup(targetGroupName);

            visu.addFocusGroup(inEdgeGroupName);
            visu.addFocusGroup(outEdgeGroupName);
            visu.addFocusGroup(mutalEGroupName);
            visu.addFocusGroup(undirectedEGroupName);

        } catch (Exception e) {
            System.out.println("Problems over problems while adding foucs groups to visualization " + e.getMessage());
        }

        sourceTupleSet = visu.getFocusGroup(sourceGroupName);
        targetTupleSet = visu.getFocusGroup(targetGroupName);

        InEdgeTupleSet = visu.getFocusGroup(inEdgeGroupName);
        OutEdgeTupleSet = visu.getFocusGroup(outEdgeGroupName);
        mutalETupleSet = visu.getFocusGroup(mutalEGroupName);
        undirectedETupleSet = visu.getFocusGroup(undirectedEGroupName);

        this.gv = gv;
        this.recStatus = recStatus;
        this.layoutNumber = layoutNumber;
        this.showWeight = showWeight;


    }

    public void itemClicked(VisualItem item, MouseEvent e) {
        Cursor cursor = gv.getDisplay().getCursor();
          sourceTupleSet.clear();
            targetTupleSet.clear();

            InEdgeTupleSet.clear();
            OutEdgeTupleSet.clear();
            mutalETupleSet.clear();
            undirectedETupleSet.clear();

        if (cursor.getType() == (Cursor.DEFAULT_CURSOR)) {
            if (item instanceof NodeItem)
                setNeighbourHighlight((NodeItem) item);
        }
    }

   /*
     public void itemEntered(VisualItem item, MouseEvent e) {
          Cursor cursor = gv.getDisplay().getCursor();
        if (cursor.getType() == (Cursor.DEFAULT_CURSOR)) {
            if (item instanceof NodeItem && gv.isGreyEdge)
                setNeighbourHighlight((NodeItem) item);

           
            
        }
     }
      */
     public void mouseClicked(MouseEvent e) {

        if (!(e.getSource().getClass().getName()).equalsIgnoreCase("VisualItem")) {

           sourceTupleSet.clear();
            targetTupleSet.clear();

            InEdgeTupleSet.clear();
            OutEdgeTupleSet.clear();
            mutalETupleSet.clear();
            undirectedETupleSet.clear();

        }
     }

   
    protected void setNeighbourHighlight(NodeItem centerNode) {
        
        if(recStatus == 2 && gv.isGreyEdge){
            gv.greyEdges();
            gv.highlightGreyEdges(centerNode);
        } else{
          HashSet target = new HashSet();

        HashSet source = new HashSet();
        Iterator allEdges = centerNode.edges();
        while (allEdges.hasNext()) {
            EdgeItem e = (EdgeItem) allEdges.next();
            if (e.getString("direction").equalsIgnoreCase("2")) {
                
                mutalETupleSet.addTuple(e);
            } else if (e.getString("direction").equalsIgnoreCase("0")) {
                undirectedETupleSet.addTuple(e);
            } else if (e.getString("direction").equalsIgnoreCase("1")) {
            
                NodeItem sourceNode = e.getSourceItem();
                NodeItem targetNode = e.getTargetItem();
                if (targetNode.equals(centerNode)) {
                    InEdgeTupleSet.addTuple(e);
                    source.add(sourceNode);
                } else {
                    OutEdgeTupleSet.addTuple(e);

                        target.add(targetNode);

                }
            }
        }


        Iterator iterSource = source.iterator();
        while (iterSource.hasNext())
            sourceTupleSet.addTuple((NodeItem) iterSource.next());

        Iterator iterTarget = target.iterator();
        while (iterTarget.hasNext())
            targetTupleSet.addTuple((NodeItem) iterTarget.next());
       /*
        Iterator iterBoth = both.iterator();
        while (iterBoth.hasNext())
            bothTupleSet.addTuple((NodeItem) iterBoth.next());

        */
    }
    }
}


