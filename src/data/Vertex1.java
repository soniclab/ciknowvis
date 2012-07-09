package data;

/**
 * Created by IntelliJ IDEA.
 * User: Li
 * Date: Feb 4, 2008
 * Time: 8:07:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Vertex1 {

    //    private LinkedList[] edges;
    private String name;
    private String bold;
    private String color;
    private String login;
    private String type;

    public Vertex1() {


    }

    public String getBold() {

        return bold;
    }

    public void setBold(String _bold) {
        bold = _bold;
    }

    public String getType() {

           return type;
       }

       public void setType(String _type) {
           type = _type;
       }



    public String getColor() {

        return color;
    }

    public void setColor(String _color) {
        color = _color;
    }

     public String getLogin() {

        return login;
    }

    public void setLogin(String _login) {
        login = _login;
    }

    public String getName() {

        return name;
    }

    public void setName(String _name) {
        name = _name;
    }
}
