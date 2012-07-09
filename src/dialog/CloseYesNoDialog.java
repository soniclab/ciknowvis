package dialog;

import admin.MainFrame;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Oct 7, 2010
 * Time: 4:05:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloseYesNoDialog {


   public  CloseYesNoDialog(MainFrame _frame){
    int choice = JOptionPane.showOptionDialog(null,
          "Are you sure you want to close this graph and open a new one?",
          "Close?",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null, null, null);

      // interpret the user's choice
      if (choice == JOptionPane.YES_OPTION)
      {
         _frame.frontPage();
         _frame.openDataFile();

      }


   }

}


