package com.example.agent_47.helpathandtest1;

/**
 * Created by Agent-47 on 07-Nov-17.
 */

        import java.sql.Connection;
        import java.sql.Statement;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;

        import java.util.List;
        import java.util.ArrayList;

        import com.example.agent_47.helpathandtest1.Monga;
        import com.example.agent_47.helpathandtest1.Database;

public class Location extends Monga {
    private Connection connection = null;

    public static class Row {
        private int id;
        private String name;

        public Row(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public int getId() {
            return this.id;
        }
        public String getName() {
            return this.name;
        }
        public String toString() {
            return "['" + id + "', '" + name + "']";
        }
    }

    public Location(Database db) throws SQLException {
        this.connection = db.getConnection();
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS location (" +
                "id integer primary key, " +
                "name text)";
        Statement st = connection.createStatement();
        st.execute(sql);
    }

    private boolean isColumn(String col_name) {
        if(col_name.equals("id")) return true;
        if(col_name.equals("name")) return true;
        return false;
    }

    public boolean exists(String name) throws SQLException {
        String sql = "SELECT id FROM location WHERE name = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        return (pst.executeQuery()).next();
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT id FROM location WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        return (pst.executeQuery()).next();
    }

    public boolean insert(String name) throws SQLException {
        if(exists(name)) return false;
        String sql = "INSERT INTO location (name) VALUES (?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        return (pst.executeUpdate() != 0);
    }

    public boolean delete(String name) throws SQLException {
        String sql = "DELETE FROM location WHERE name = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        return (pst.executeUpdate() != 0);
    }

    public boolean update(String old_name, String new_name) throws SQLException {
        if(exists(new_name)) return false;
        String sql = "UPDATE location SET name = ? WHERE name = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, new_name.toLowerCase());
        pst.setString(2, old_name.toLowerCase());
        return (pst.executeUpdate() != 0);
    }

    public boolean update(String name, int new_id) throws SQLException {
        if(exists(new_id)) return false;
        String sql = "UPDATE location SET id = ? WHERE name = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, new_id);
        pst.setString(2, name.toLowerCase());
        return (pst.executeUpdate() != 0);
    }

    public int getId(String name) throws SQLException {
        String sql = "SELECT id FROM location WHERE name = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("id") : NULL_ID;
    }

    public String getName(int id) throws SQLException {
        String sql = "SELECT name FROM location WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getString("name") : null;
    }

    private ResultSet getColumn(String col_name) throws SQLException {
        if(!isColumn(col_name)) return null;

        String sql = "SELECT " + col_name + " FROM location";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    private ResultSet getColumn(String col_name, int limit) throws SQLException {
        if(limit <= 0 || !isColumn(col_name)) return null;

        String sql = "SELECT " + col_name + " FROM location LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        return pst.executeQuery();
    }

    public int[] getIds() throws SQLException {
        ResultSet rs = getColumn("id");
        if(rs == null) return null;
        List<Integer> list = new ArrayList<Integer>();
        while(rs.next()) {
            list.add(rs.getInt("id"));
        }
        int[] array = new int[list.size()];
        int i = 0;
        for(int id: list) {
            array[i++] = id;
        }
        return array;
    }

    public int[] getIds(int limit) throws SQLException {
        ResultSet rs = getColumn("id", limit);
        if(rs == null) return null;
        List<Integer> list = new ArrayList<Integer>();
        while(rs.next()) {
            list.add(rs.getInt("id"));
        }
        int[] array = new int[list.size()];
        int i = 0;
        for(int id: list) {
            array[i++] = id;
        }
        return array;
    }

    public String[] getNames() throws SQLException {
        ResultSet rs = getColumn("name");
        if(rs == null) return null;
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(rs.getString("name"));
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public String[] getNames(int limit) throws SQLException {
        ResultSet rs = getColumn("name", limit);
        if(rs == null) return null;
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(rs.getString("name"));
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows() throws SQLException {
        String sql = "SELECT id, name FROM location";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(rs.getInt("id"), rs.getString("name"));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows(int limit) throws SQLException {
        if(limit <= 0) return null;
        String sql = "SELECT id, name FROM location LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        ResultSet rs = pst.executeQuery();
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(rs.getInt("id"), rs.getString("name"));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public void print() throws SQLException {
        Row[] array = getRows();
        System.out.println("TABLE \"location\":");
        System.out.println("--------------------------------------------------------------------------------");
        for(int i=0; i<array.length; i++) System.out.println(array[i]);
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS location";
        Statement st = connection.createStatement();
        st.execute(sql);
    }
}

