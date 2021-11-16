import java.io.*;
import java.sql.*;
import java.util.*;


public class test {


  public static final String propsFile = "jdbc.properties";



  public static Connection getConnection() throws IOException, SQLException
  {
    // Load properties

    FileInputStream in = new FileInputStream(propsFile);
    Properties props = new Properties();
    props.load(in);

    // Define JDBC driver

    String drivers = props.getProperty("jdbc.drivers");
    if (drivers != null)
      System.setProperty("jdbc.drivers", drivers);
      // Setting standard system property jdbc.drivers
      // is an alternative to loading the driver manually
      // by calling Class.forName()

    // Obtain access parameters and use them to create connection

    String url = props.getProperty("jdbc.url");
    String user = props.getProperty("jdbc.user");
    String password = props.getProperty("jdbc.password");

    return DriverManager.getConnection(url, user, password);
  }


  public static void getMovies(Connection database)
   throws SQLException
  {
    Statement statement = database.createStatement();
    ResultSet results = statement.executeQuery(
     "SELECT title FROM movies");
    while (results.next()) {
      System.out.println(results.getString("title"));
    }
    statement.close();
  }


  /**
   * Main program.
   */

  public static void main(String[] argv)
  {

    Connection connection = null;
 
    try {
      connection = getConnection();
      getMovies(connection);
    }
    catch (Exception error) {
      error.printStackTrace();
    }
    finally {

      // This will always execute, even if an exception has
      // been thrown elsewhere in the code - so this is
      // the ideal place to close the connection to the DB...

      if (connection != null) {
        try {
          connection.close();
        }
        catch (Exception error) {}
      }
    }
  }


}
