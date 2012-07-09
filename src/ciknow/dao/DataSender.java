package ciknow.dao;

/**
 * Created by IntelliJ IDEA.
 * User: jinling
 * Date: Jan 6, 2010
 * Time: 11:41:31 AM
 * To change this template use File | Settings | File Templates.
 */
//  File:  Student.java
//  Listing 1
//

import admin.MainFrame;

import java.net.URLConnection;
import java.net.URL;
import java.io.ObjectOutputStream;
import java.io.InputStream;



public class DataSender implements java.io.Serializable
{

    private   char ch = 'x';
    // constructors
    public DataSender(GraphData data, MainFrame _frame)
    {
         System.out.println(data.getName());

        
         try{
          URL servlet = new URL(_frame.getCodeBase().toString()+ "ac");
        URLConnection servletConnection =servlet.openConnection();

// inform the connection that we will send output and accept input
        servletConnection.setDoInput(true);
        servletConnection.setDoOutput(true);

// Don't use a cached version of URL connection.
        servletConnection.setUseCaches (false);
        servletConnection.setDefaultUseCaches (false);

// Specify the content type that we will send binary data
        servletConnection.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");


// send the student object to the servlet using serialization
       ObjectOutputStream  outputToServlet = new ObjectOutputStream(servletConnection.getOutputStream());

// serialize the object

        outputToServlet.writeObject(data);
        

        outputToServlet.flush();
        outputToServlet.close();

         InputStream inputStream = servletConnection.getInputStream();

         ch = (char)inputStream.read();
         //  System.out.println("************ message from servlet: " + ch);
        inputStream.close();



      } catch (Exception ee) {
          System.out.println(" exception when connected to Servlet");
         }

    }

    public char feedbackFromServer(){
        return ch;
    }

}