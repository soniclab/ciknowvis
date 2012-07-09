package visual;

import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.ColorAction;
import prefuse.visual.VisualItem;
import prefuse.util.ColorLib;

import java.util.logging.Logger;

import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Sep 17, 2009
 * Time: 3:38:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class EdgeColorAction extends DataColorAction {
   private boolean greyEdge;
    public EdgeColorAction(String group, String dataField,
                             int dataType, String colorField, boolean greyEdge) {
        super(group, dataField, dataType, colorField);
       this.greyEdge = greyEdge;
    }

    /**
     * @see prefuse.action.assignment.ColorAction#getColor(prefuse.visual.VisualItem)
     */
    public int getColor(VisualItem item) {

        // check for any cascaded rules first
        Object o = lookup(item);
        if (o != null) {
            if (o instanceof ColorAction) {
                return ((ColorAction) o).getColor(item);
            } else if (o instanceof Integer) {
                return ((Integer) o).intValue();
            } else {
                Logger.getLogger(this.getClass().getName())
                        .warning("Unrecognized Object from predicate chain.");
            }
        }
        try {
            Color cl;
              
                cl = Color.decode(item.getString("edgeColor"));

            int red = cl.getRed();
            int green = cl.getGreen();
            int blue = cl.getBlue();
                if(greyEdge)
                return  ColorLib.gray(200);

                else
                return ColorLib.rgb(red, green, blue);
         
        } catch (Exception e) {
            System.out.println("Exception when getting colors for visual edges" + e);
            return 0;
        }


    } // end of class DataColorAction

}