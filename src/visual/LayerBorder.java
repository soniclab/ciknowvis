package visual;

import prefuse.Display;
import prefuse.util.display.PaintListener;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;

import data.AppletDataHandler1;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: May 23, 2008
 * Time: 1:36:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayerBorder implements PaintListener {

    private GraphView gv;
    private double cx = 0.0, cy = 0.0;
    private Map<String, NodeItem> recSeMap; //recx = 0.0,recy = 0.0;
    Map<NodeItem, Line2D> lines;

    public LayerBorder(GraphView gv,  HashMap<NodeItem, Line2D> lines) {
        this.gv = gv;

        this.lines = lines;

    }

    public void prePaint(Display d, Graphics2D g) {

        if (gv.layoutNumber == 6 && gv.getRecStatus() == 2) {
            double seMax = 0.00;

            recSeMap = new HashMap<String, NodeItem>();
            TupleSet ts = gv.getCurrentVis().getGroup("graph.nodes");

            Iterator iter = ts.tuples();

            for (int i = 0; iter.hasNext(); i++) {

                NodeItem item = (NodeItem) iter.next();

                if (item.getString("nodeType").equals("Requester")) {
                    cx = item.getX();
                    cy = item.getY();

                } else if (item.getString("nodeType").equals("Recommendation")) {
                    // get all edges for this rec
                    Iterator edges = item.edges();
                  /*  boolean hasNoRecDirect = false;
                    for (int j = 0; edges.hasNext(); j++) {
                        EdgeItem edge = (EdgeItem) edges.next();
                        if (edge.getString("invisibleItem").equalsIgnoreCase("2")) {
                            hasNoRecDirect = true;
                            break;
                        }
                    } */
                    seMax = Math.max(seMax, 1 / item.getDouble("recSe"));
                    double se = Double.parseDouble(item.getString("recSe"));
                    double finalSe = 1 / se;
                    recSeMap.put(finalSe + "", item);

                    if (item.getInDegree()!= 0 || item.getOutDegree() != 0) {                       
                        drawLine(g, item);

                   }

                } //else
            } //for

            double radius = 0.0;
            if (recSeMap.size() > 0) {

                double recx = 0.0, recy = 0.0;

                for (String recSe : recSeMap.keySet()) {

                    if (recSe.equalsIgnoreCase("1.0")) {
                        recx = recSeMap.get(recSe).getX();
                        recy = recSeMap.get(recSe).getY();
                        radius = Math.sqrt((cy - recy) * (cy - recy) + (cx - recx) * (cx - recx));
                        break;
                    } else if (recSe.equalsIgnoreCase("2.0")) {

                        recx = recSeMap.get(recSe).getX();
                        recy = recSeMap.get(recSe).getY();
                        radius = (Math.sqrt((cy - recy) * (cy - recy) + (cx - recx) * (cx - recx))) / 2;
                        break;

                    } else if (Double.parseDouble(recSe) > 2.0 && Double.parseDouble(recSe) < 3.0) {


                        recx = recSeMap.get(recSe).getX();
                        recy = recSeMap.get(recSe).getY();

                        radius = (Math.sqrt((cy - recy) * (cy - recy) + (cx - recx) * (cx - recx))) / 3;
                        break;
                    }

                }  //for

            }//if

            for (int i = 1; i < seMax + 1; i++) {
                double length = i * radius;
             
                drawCircle(g, length);


            }
        }
    }

    private void drawCircle(Graphics2D g, double radius) {


        g.setColor(new Color(210, 210, 210));
        final float dash1[] = {10.0f};
        final BasicStroke dashed = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);
        g.setStroke(dashed);

        g.draw(new Ellipse2D.Double(cx - radius, cy - radius, 2 * radius, 2 * radius));


    }

    private void drawLine(Graphics2D g, NodeItem item) {

        g.setColor(new Color(210, 210, 210));
        final float dash1[] = {10.0f};
        final BasicStroke dashed = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);
        g.setStroke(dashed);
        Line2D line = new Line2D.Double((int) (cx + 0.5), (int) (cy + 0.5), (int) (item.getX() + 0.5), (int) (item.getY() + 0.5));

        g.draw(line);


        lines.put(item, line);
        //  g.drawLine((int)(cx+0.5), (int)( cy+0.5), (int)(recx+0.5), (int)(recy+0.5));
    }

    /**
     * Prints a debugging statistics string in the Display.
     *
     * @see prefuse.util.display.PaintListener#postPaint(prefuse.Display, java.awt.Graphics2D)
     */
    public void postPaint(Display d, Graphics2D g) {
        

    }

}


