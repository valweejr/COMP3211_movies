import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;

public class RecommendMovie extends HttpServlet {
  
  public static final String propsFile = "jdbc.properties";
  private static final String CONTENT_TYPE = "text/html";
  String[] movies = null;

  /** Process the HTTP Get request to get list of popular movies*/
  public void doGet(HttpServletRequest request, HttpServletResponse
      response) throws ServletException, IOException {

    Connection database = null;
    PrintWriter out = response.getWriter();
    
    try{
      database = getConnection(); //connect to database

      Statement statement = database.createStatement();
      ResultSet results = statement.executeQuery(
      "SELECT title FROM movies"); //get list of movies

      List<String> popularMovies = new ArrayList<String>();

      while (results.next()) {
        popularMovies.add(results.getString("title"));
      }
      movies = popularMovies.toArray(new String[0]);

      statement.close();

    }catch(SQLException e){
      out.write("<h3>error: " + e + "</h3>");
      //catch error
    }finally {
      if (database != null) {
        try {
          database.close();
        }
        catch (Exception error) {}
      }
    }

    //display list of movies
    response.setContentType(CONTENT_TYPE);
    out.println("<h3>Movie Recommender</h3>");
    out.println("<form method=\"post\" action=" +
      "/movies/RecommendMovie>");
    out.println("Choose your favourite movie <select size=\"1\" name=\"movies_select\">");

    //display movie options in dropdown
    for (int i = 0; i < movies.length; i++) {
      out.println("<option value=\"" + i +"\">" +
        movies[i] + "</option>");
    }
    out.println("</select>");

    out.println("<p><input type=\"submit\" value=\"Submit\" >");
    out.println("</form>");
    out.close(); // Close stream
  }   


  public void doPost(HttpServletRequest request, HttpServletResponse
      response) throws ServletException, IOException {

    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding("GB18030");

    Connection database = null;
    PrintWriter out = response.getWriter();
    int movieIndex = Integer.parseInt(request.getParameter("movies_select"));

    //connect to database
    try{
      database = getConnection();
      Statement statement = database.createStatement();
      ResultSet results = statement.executeQuery( //get genre of movie
      "SELECT genre FROM movies WHERE movie_id = " +  movieIndex); 
      
      while (results.next()) {
        String genre = results.getString("genre");
        out.write("<h3>genre to look into tmdb: " + genre + "</h3>");
      }

      statement.close();

    }catch(SQLException e){
      out.write("<h3>error: " + e + "</h3>");
      //catch error
    }finally {
      if (database != null) {
        try {
          database.close();
        }
        catch (Exception error) {}
      }
    }
    out.close(); // Close stream
  }


  /**Establishes a connection to the database**/
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
}




