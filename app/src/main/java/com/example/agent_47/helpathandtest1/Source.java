package com.example.agent_47.helpathandtest1;

/**
 * Created by Agent-47 on 07-Nov-17.
 */

        import java.sql.SQLException;
        import java.sql.Connection;
        import java.sql.Statement;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;

        import java.util.List;
        import java.util.ArrayList;

        import com.example.agent_47.helpathandtest1.Monga;
        import com.example.agent_47.helpathandtest1.Database;
        import com.example.agent_47.helpathandtest1.Location;

public class Source extends Monga {
    private Database database = null;
    private Connection connection = null;
    private Location location = null;

    public static class Row {
        int id;
        String name, loc_name;

        public Row(int id, String name, String loc_name) {
            this.id = id;
            this.name = name;
            this.loc_name = loc_name;
        }
        public int getId() {
            return this.id;
        }
        public String getName() {
            return this.name;
        }
        public String getLocation() {
            return this.loc_name;
        }
        public String toString() {
            return "['" + id + "', '" + name + "', '" + loc_name + "']";
        }
    }

    public Source(Database database, Location location) throws SQLException {
        this.database = database;
        this.connection = database.getConnection();
        this.location = location;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS source (" +
                "id integer primary key, " +
                "name text, " +
                "loc_id integer)";
        Statement st = connection.createStatement();
        st.execute(sql);
    }

    private boolean isColumn(String col_name) throws SQLException {
        if(col_name.equals("id")) return true;
        if(col_name.equals("name")) return true;
        if(col_name.equals("loc_id")) return true;
        return false;
    }

    public boolean exists(String name, int loc_id) throws SQLException {
        String sql = "SELECT id FROM source WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        return (pst.executeQuery()).next();
    }

    public boolean exists(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        return (loc_id == NULL_ID) ? false : exists(name, loc_id);
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT id FROM source WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        return (pst.executeQuery()).next();
    }

    public boolean insert(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID || exists(name, loc_id)) return false;
        String sql = "INSERT INTO source (name, loc_id) VALUES (?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean delete(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "DELETE FROM source WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean update(String old_name, String old_loc_name, String new_name, String new_loc_name) throws SQLException {
        int old_loc_id = location.getId(old_loc_name);
        int new_loc_id = location.getId(new_loc_name);
        if(old_loc_id == NULL_ID || new_loc_id == NULL_ID || exists(new_name, new_loc_id)) return false;
        String sql = "UPDATE source SET name = ?, loc_id = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, new_name.toLowerCase());
        pst.setInt(2, new_loc_id);
        pst.setString(3, old_name.toLowerCase());
        pst.setInt(4, old_loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean update(String name, String loc_name, int new_id) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID || exists(new_id)) return false;
        String sql = "UPDATE source SET id = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, new_id);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public int getId(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return NULL_ID;
        String sql = "SELECT id FROM source WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("id") : NULL_ID;
    }

    public String getName(int id) throws SQLException {
        String sql = "SELECT name FROM source WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ?  rs.getString("name") : null;
    }

    private int getLocationId(int id) throws SQLException {
        String sql = "SELECT loc_id FROM source WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("loc_id") : NULL_ID;
    }

    public String getLocation(int id) throws SQLException {
        int loc_id = getLocationId(id);
        return (loc_id == NULL_ID) ? null : location.getName(loc_id);
    }

    private ResultSet getColumn(String col_name) throws SQLException {
        if(!isColumn(col_name)) return null;
        String sql = "SELECT " + col_name + " FROM source";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    private ResultSet getColumn(String col_name, int limit) throws SQLException {
        if(limit <= 0 || !isColumn(col_name)) return null;
        String sql = "SELECT " + col_name + " FROM source LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        return pst.executeQuery();
    }

    private ResultSet getColumnDistinct(String col_name) throws SQLException {
        if(!isColumn(col_name)) return null;
        String sql = "SELECT DISTINCT " + col_name + " FROM source";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    private ResultSet getColumnDistinct(String col_name, int limit) throws SQLException {
        if(limit <= 0 || !isColumn(col_name)) return null;
        String sql = "SELECT DISTINCT " + col_name + " FROM source LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        return pst.executeQuery();
    }

    public int[] getIds() throws SQLException {
        ResultSet rs = getColumn("id");
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
        if(limit <= 0) return null;
        ResultSet rs = getColumn("id", limit);
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

    public String[] getDistinctNames() throws SQLException {
        ResultSet rs = getColumnDistinct("name");
        if(rs == null) return null;
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(rs.getString("name"));
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public String[] getDistinctLocations() throws SQLException {
        ResultSet rs = getColumnDistinct("loc_id");
        if(rs == null) return null;
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(location.getName(rs.getInt("loc_id")));
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows() throws SQLException {
        String sql = "SELECT id, name, loc_id FROM source";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(rs.getInt("id"), rs.getString("name"), location.getName(rs.getInt("loc_id")));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows(int limit) throws SQLException {
        if(limit <= 0) return null;
        String sql = "SELECT id, name, loc_id FROM source LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        ResultSet rs = pst.executeQuery();
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(rs.getInt("id"), rs.getString("name"), location.getName(rs.getInt("loc_id")));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public void print() throws SQLException {
        Row[] rows = getRows();
        System.out.println("TABLE \"source\":");
        System.out.println("------------------------------------------------------------------");
        for(int i=0; i<rows.length; i++) System.out.println(rows[i]);
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS source";
        Statement st = connection.createStatement();
        st.execute(sql);
    }
}

