package com.example.agent_47.helpathandtest1;

/**
 * Created by Agent-47 on 07-Nov-17.
 */


        import java.sql.Driver;
        import java.sql.DriverManager;
        import java.sql.Connection;
        import java.sql.SQLException;

        import com.example.agent_47.helpathandtest1.Monga;



public class Database extends Monga {
    private String database_file_path = null;
    private Connection connection = null;

    public Database(String database_file_path) throws SQLException {
        try {
            DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
            this.database_file_path = database_file_path;
            String url = "jdbc:sqldroid:" + database_file_path;
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDatabaseFilePath() {
        return this.database_file_path;
    }

    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public void close() throws SQLException {
        if(connection != null) {
            connection.close();
        }
    }
}

