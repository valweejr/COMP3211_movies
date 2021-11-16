import java.io.*;
import java.sql.*;
import java.util.*;

public class CreateDB {

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

    // Obtain access parameters and use them to create connection
    String url = props.getProperty("jdbc.url");
    String user = props.getProperty("jdbc.user");
    String password = props.getProperty("jdbc.password");

    return DriverManager.getConnection(url, user, password);
  }

  public static void createTable(Connection database) throws SQLException
  {
    Statement statement = database.createStatement();
    try {
      statement.executeUpdate("DROP TABLE movies");
    }
    catch (SQLException error) {
      // Catch and ignore SQLException, as this merely indicates
      // that the table didn't exist in the first place!
    }

    // Create a fresh table

    statement.executeUpdate("CREATE TABLE movies ("
                          + "movie_id CHAR(8) NOT NULL PRIMARY KEY,"
                          + "title VARCHAR(100) NOT NULL,"
                          + "genre VARCHAR(20) NOT NULL)");

    statement.close();
  }

  public static void addData(BufferedReader in, Connection database)
   throws IOException, SQLException
  {
    // Prepare statement used to insert data

    PreparedStatement statement =
     database.prepareStatement("INSERT INTO movies VALUES(?,?,?)");

    // Loop over input data, inserting it into table...
 
    while (true) {

      // Obtain movie ID, title and genre from input file

      String line = in.readLine();
      if (line == null)
        break;
      StringTokenizer parser = new StringTokenizer(line,",");
      String movie_id = parser.nextToken();
      String title = parser.nextToken();
      String genre = parser.nextToken();

      // Insert data into table

      statement.setString(1, movie_id);
      statement.setString(2, title);
      statement.setString(3, genre);
      statement.executeUpdate();

    }

    statement.close();
    in.close();
  }


  /**
   * Main program.
   */

  public static void main(String[] argv)
  {
    if (argv.length == 0) {
      System.err.println("usage: java CreateDB <inputFile>");
      System.exit(1);
    }

    Connection database = null;
 
    try {
      BufferedReader input = new BufferedReader(new FileReader(argv[0]));
      database = getConnection();
      createTable(database);
      addData(input, database);
    }
    catch (Exception error) {
      error.printStackTrace();
    }
    finally {

      if (database != null) {
        try {
          database.close();
        }
        catch (Exception error) {}
      }
    }
  }


}
