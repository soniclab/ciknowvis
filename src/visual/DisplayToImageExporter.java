package visual;


import prefuse.util.io.SimpleFileFilter;
import prefuse.util.io.IOLib;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.HashSet;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Nov 18, 2008
 * Time: 3:41:03 PM
 * To change this template use File | Settings | File Templates.
 */

public class DisplayToImageExporter {

    /** The FileChooser to select the file to save to */
    protected JFileChooser chooser = null;

    //~--- Constructors -------------------------------------------------------

    public DisplayToImageExporter() {
    }

    //~--- Methods ------------------------------------------------------------

    /**
     * This method initiates the chooser components, detecting available image formats
     *
     */
    protected void init() {
       try{
        // Initialize the chooser
        chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Export graph as image");
        chooser.setAcceptAllFileFilterUsed(false);

        HashSet<String> availableFormats = new HashSet<String>();
        String[]        fmts             = ImageIO.getWriterFormatNames();

        for (int i = 0; i < fmts.length; i++) {
            String s = fmts[i].toLowerCase();
            // quality of jpeg image is bad, so ignore this format
            if(!s.equalsIgnoreCase("jpg")) {
            if ((s.length() == 3) &&!availableFormats.contains(s)) {
                availableFormats.add(s);
                chooser.setFileFilter(new SimpleFileFilter(s, s.toUpperCase() + " Image (*." + s + ")"));
            }}
        }

        availableFormats.clear();
        availableFormats = null;
       }catch(Exception e){
           System.out.println("Exception when init the File chooser: " + e);
       }
    }


    public void export(JPanel pane) {

            // Initialize if needed
            if (chooser == null) {
                init();
            }

            // open image save dialog
            File f         = null;

              int  returnVal = chooser.showSaveDialog(pane);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                f = chooser.getSelectedFile();
            } else {

                return;
            }

            String format = ((SimpleFileFilter) chooser.getFileFilter()).getExtension();
            String ext    = IOLib.getExtension(f);

            if (!format.equals(ext)) {
                f = new File(f.toString() + "." + format);
            }

            //  save the image
            boolean success = false;

            try {

                OutputStream out = new BufferedOutputStream(new FileOutputStream(f));

                System.out.print("INFO Saving image " + f.getName() + ", " + format + " format...");
                 success = exportImage(pane, out, format);
                out.flush();
                out.close();


            } catch (Exception e) {
                success = false;
            }

            // show result dialog on failure
            if (!success) {

                  JOptionPane.showMessageDialog(pane, "Error Saving Image!", "Image Save Error",
                                              JOptionPane.ERROR_MESSAGE);
            }

         
        }

         private boolean exportImage(JPanel pane, OutputStream output, String format) {


                   Dimension size = pane.getSize();
                   BufferedImage myImage =
                     new BufferedImage(size.width, size.height,
                     BufferedImage.TYPE_INT_RGB);
                   Graphics2D g2 = myImage.createGraphics();
                   pane.paint(g2);

                   try {
                     ImageIO.write(myImage, format, output);
                       return true;
                   } catch (Exception e) {
                     System.out.println(e);
                       return false;
                   }
        }


    /**
     * This method lets the user select the target file and exports the <code>Display</code>
     *
     * @paran display the <code>Display</code> to export
     *
     */
       public void export(JSplitPane pane) {

        // Initialize if needed
        if (chooser == null) {
            init();
        }

        // open image save dialog
        File f         = null;
         pane.setDividerSize(0);
          int  returnVal = chooser.showSaveDialog(pane);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        } else {
             pane.setDividerSize(1);
            return;
        }

        String format = ((SimpleFileFilter) chooser.getFileFilter()).getExtension();
        String ext    = IOLib.getExtension(f);

        if (!format.equals(ext)) {
            f = new File(f.toString() + "." + format);
        }

        //  save the image
        boolean success = false;

        try {

            OutputStream out = new BufferedOutputStream(new FileOutputStream(f));

            System.out.print("INFO Saving image " + f.getName() + ", " + format + " format...");
             success = exportImage(pane, out, format);
            out.flush();
            out.close();

            
        } catch (Exception e) {
            success = false;
        }

        // show result dialog on failure
        if (!success) {

              JOptionPane.showMessageDialog(pane, "Error Saving Image!", "Image Save Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

      pane.setDividerSize(1);
    }

     private boolean exportImage(JSplitPane pane, OutputStream output, String format) {


               Dimension size = pane.getSize();
               BufferedImage myImage =
                 new BufferedImage(size.width, size.height,
                 BufferedImage.TYPE_INT_RGB);
               Graphics2D g2 = myImage.createGraphics();
               pane.paint(g2);
           
               try {
                 ImageIO.write(myImage, format, output);
                   return true;
               } catch (Exception e) {
                 System.out.println(e);
                   return false;
               }
    }

}

