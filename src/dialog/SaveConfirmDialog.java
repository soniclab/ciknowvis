package dialog;


import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jan 6, 2010
 * Time: 1:34:18 PM
 * To change this template use File | Settings | File Templates.
 */

public class SaveConfirmDialog  {


  public SaveConfirmDialog (char message){
   if(message == 'n')
   JOptionPane.showMessageDialog(null, "The network has been successfully saved!");
   else
   JOptionPane.showMessageDialog(null, "The visualization has been successfully sent!");
  }

}