package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// this class is the place to go to interact with our database.
public class Admin {

    public static final String databaseDir
                = System.getProperty("user.home") + "/graph-dbs";

    public static final String dbName = "graphmainbase";
    public static final String dbGraphs = "designs";

    public static void createNewDatabase() {

        new File(databaseDir).mkdirs();

        final String tableLvl = "create table if not exists " + dbGraphs + "("
                + "date integer primary key,"
                + "title text not null,"
                + "description text not null,"
                + "graph text not null"
                + ");";

        final String url = getUrl();

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.createStatement().executeUpdate(tableLvl);
        } catch (SQLException e) {
            System.out.println("init: " + e.getMessage());
        }
        
        //TODO - insert a couple default graphs
    }

    public static String getUrl() {
        return "jdbc:sqlite:" + getDatabasePath();
    }

    public static String getDatabasePath() {
        return String.format("%s/%s.sqlite", databaseDir, dbName);
    }

    private static Connection connect() {
        // SQLite connection string
        String url = getUrl();
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("connect: " + e.getMessage());
        }
        return conn;
    }

    // delete a graph completely
    public static void deleteDes(long date) {
        String sql = "DELETE FROM " + dbGraphs + " WHERE date = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the design to delete
            pstmt.setLong(1, date);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("delete: " + e.getMessage());
        }
    }


    public static ResultSet fetch(){
        final String sql;
        // should do this with switch and case but what ever
        sql = "SELECT date, title, description, graph FROM " + dbGraphs;

        try {
            Connection conn = connect();
            final Statement stmt  = conn.createStatement();
            final ResultSet rs    = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static long insertGraph(String name, String description, String data) {
        String sql = "INSERT INTO " + dbGraphs + "(date,title,description,graph) VALUES(?,?,?,?)";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            final long time = System.currentTimeMillis();
            pstmt.setLong(1, time);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setString(4, data);
            pstmt.executeUpdate();
            return time;
        } catch (SQLException e) {
            System.out.println("insertlevel: " + e.getMessage());
            return -1;
        }
    }
}






