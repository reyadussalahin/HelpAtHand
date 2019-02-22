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
        import com.example.agent_47.helpathandtest1.Location;

public class Path extends Monga {
    private Database database = null;
    private Connection connection = null;
    private Location location = null;

    public static class Row {
        private String from_name, to_name;
        private double dist;

        public Row(String from_name, String to_name, double dist) {
            this.from_name = from_name;
            this.to_name = to_name;
            this.dist = dist;
        }
        public String from() {
            return this.from_name;
        }
        public String to() {
            return this.to_name;
        }
        public double getDistance() {
            return this.dist;
        }
        public String toString() {
            return "['" + from_name + "', '" + to_name + "', '" + dist + "']";
        }
    }

    public Path(Database database, Location location) throws SQLException {
        this.database = database;
        this.connection = database.getConnection();
        this.location = location;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS path (" +
                "from_id integer, " +
                "to_id integer, " +
                "dist float)";
        Statement st = connection.createStatement();
        st.execute(sql);
    }

    private boolean isColumn(String col_name) {
        if(col_name.equals("from_id")) return true;
        if(col_name.equals("to_id")) return true;
        if(col_name.equals("dist")) return true;
        return false;
    }

    public boolean exists(int from_id, int to_id) throws SQLException {
        if(from_id > to_id) {
            int temp = from_id;
            from_id = to_id;
            to_id = temp;
        }
        String sql = "SELECT dist FROM path WHERE from_id = ? AND to_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, from_id);
        pst.setInt(2, to_id);
        return (pst.executeQuery()).next();
    }

    public boolean exists(String from_name, String to_name) throws SQLException {
        int from_id = location.getId(from_name);
        int to_id = location.getId(to_name);
        if(from_id == NULL_ID || to_id == NULL_ID) return false;
        return exists(from_id, to_id);
    }

    public boolean insert(String from_name, String to_name, double dist) throws SQLException {
        if(dist < ZERO) return false;

        int from_id = location.getId(from_name);
        int to_id = location.getId(to_name);
        if((from_id == NULL_ID) || (to_id == NULL_ID) || exists(from_id, to_id)) return false;

        if(from_id > to_id) {
            int temp = from_id;
            from_id = to_id;
            to_id = temp;
        }

        String sql = "INSERT INTO path (from_id, to_id, dist) VALUES(?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, from_id);
        pst.setInt(2, to_id);
        pst.setDouble(3, dist);
        return (pst.executeUpdate() != 0);
    }

    public boolean delete(String from_name, String to_name) throws SQLException {
        int from_id = location.getId(from_name);
        int to_id = location.getId(to_name);
        if((from_id == NULL_ID) || (to_id == NULL_ID)) return false;

        if(from_id > to_id) {
            int temp = from_id;
            from_id = to_id;
            to_id = temp;
        }

        String sql = "DELETE FROM path WHERE from_id = ? AND to_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, from_id);
        pst.setInt(2, to_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean updateDistance(String from_name, String to_name, double new_dist) throws SQLException {
        int from_id = location.getId(from_name);
        int to_id = location.getId(to_name);
        if(new_dist < ZERO || (from_id == NULL_ID) || (to_id == NULL_ID)) return false;

        if(from_id > to_id) {
            int temp = from_id;
            from_id = to_id;
            to_id = temp;
        }

        String sql = "UPDATE path SET dist = ? WHERE from_id = ? AND to_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setDouble(1, new_dist);
        pst.setInt(2, from_id);
        pst.setInt(3, to_id);
        return (pst.executeUpdate() != 0);
    }

    public double getDistance(int from_id, int to_id) throws SQLException {
        if((from_id == NULL_ID) || (to_id == NULL_ID)) return DIST_INF;

        if(from_id > to_id) {
            int temp = from_id;
            from_id = to_id;
            to_id = temp;
        }

        String sql = "SELECT dist FROM path WHERE from_id = ? AND to_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, from_id);
        pst.setInt(2, to_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getDouble("dist") : DIST_INF;
    }

    public double getDistance(String from_name, String to_name) throws SQLException {
        int from_id = location.getId(from_name);
        int to_id = location.getId(to_name);
        return getDistance(from_id, to_id);
    }

    public Row[] getRows() throws SQLException {
        String sql = "SELECT from_id, to_id, dist FROM path";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(location.getName(rs.getInt("from_id")), location.getName(rs.getInt("to_id")), rs.getDouble("dist"));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows(int limit) throws SQLException {
        if(limit <= 0) return null;

        String sql = "SELECT from_id, to_id, dist FROM path LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        ResultSet rs = pst.executeQuery();
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(location.getName(rs.getInt("from_id")), location.getName(rs.getInt("to_id")), rs.getDouble("dist"));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public void print() throws SQLException {
        Row[] rows = getRows();
        System.out.println("TABLE \"path\":");
        System.out.println("------------------------------------------------------------------");
        for(int i=0; i<rows.length; i++) System.out.println(rows[i]);
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS path";
        Statement st = connection.createStatement();
        st.execute(sql);
    }
}
