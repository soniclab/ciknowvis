package visual;

import prefuse.action.assignment.DataSizeAction;
import prefuse.Constants;
import prefuse.visual.VisualItem;

import data.AppletDataHandler1;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jul 9, 2009
 * Time: 10:56:18 AM
 * To change this template use File | Settings | File Templates.
 */



public class MyDataSizeAction extends DataSizeAction {

    protected static final double NO_SIZE = Double.NaN;

    protected String m_dataField;

    protected double m_minSize = 1;
    protected double m_sizeRange;

    protected int m_scale = Constants.LINEAR_SCALE;
    protected int m_bins = Constants.CONTINUOUS;

    protected boolean m_inferBounds = true;
    protected boolean m_inferRange = true;
    protected boolean m_is2DArea = true;
    protected double[] m_dist;

    protected int m_tempScale;
      private AppletDataHandler1 dh;

    private int  normalizedType;
    private double displayMin,  displayMax;

     public MyDataSizeAction(String group, AppletDataHandler1 dh, int normalizedType, double displayMin, double displayMax) {
       super(group, "edgeWeight");
          this.dh= dh;

        this.normalizedType = normalizedType;
     
        this.displayMax = displayMax;
        this.displayMin = displayMin;

    }

    /**
     * @see prefuse.action.assignment.SizeAction#getSize(prefuse.visual.VisualItem)
     */
    public double getSize(VisualItem item) {

         double dissize = 0.0;
         double edgesize = item.getDouble("edgeWeight");
                 if(normalizedType ==0)
              dissize = 0.5*edgesize;
             if(normalizedType ==1){
             dissize=(dh.getLinearSize(edgesize, displayMin,  displayMax, false, 1));

             } else  if(normalizedType ==2)
           dissize =(dh.getLogSize(edgesize, displayMin,  displayMax, false, 1));
        return dissize;
    }

} // end of class DataSizeAction
