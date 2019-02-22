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
        import com.example.agent_47.helpathandtest1.Catagory;

public class Service extends Monga {
    private Database database = null;
    private Connection connection = null;
    private Catagory catagory = null;
    private Location location = null;

    public static class Row {
        private int id, vote;
        private String name, loc_name, addr, contact, desc, cat_name;
        private double rat, cal_rat;

        public Row(int id, String name, String loc_name, String cat_name,
                   double rat, int vote, String addr, String contact, String desc)
                throws SQLException {
            this.id = id;
            this.name = name;
            this.loc_name = loc_name;
            this.cat_name = cat_name;
            this.rat = rat;
            this.vote = vote;
            this.addr = addr;
            this.contact = contact;
            this.desc = desc;
            this.cal_rat = this.rat / this.vote;
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
        public String getCatagory() {
            return this.cat_name;
        }
        public double getRating() {
            return this.rat;
        }
        public int vote() {
            return this.vote;
        }
        public String getAddress() {
            return this.addr;
        }
        public String getContact() {
            return this.contact;
        }
        public String getDescription() {
            return this.desc;
        }
        public double calculateRating() {
            return this.cal_rat;
        }
        public String toString() {
            return "['" + id + "', '" + name + "', '" + loc_name + "', '" + cat_name + "', '" +
                    cal_rat + "', '" + addr + "', '" + contact + "', '" + desc + "']";
        }
    }

    public Service(Database database, Location location, Catagory catagory) throws SQLException {
        this.database = database;
        this.location = location;
        this.catagory = catagory;
        this.connection = database.getConnection();
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS service (" +
                "id integer primary key, " +
                "name text, " +
                "loc_id integer, " +
                "cat_id integer, " +
                "rat float, " +
                "vote integer, " +
                "addr text, " +
                "contact text, " +
                "desc text)";

        Statement st = connection.createStatement();
        st.execute(sql);
    }

    private boolean isColumn(String col_name) {
        if(col_name.equals("id")) return true;
        if(col_name.equals("name")) return true;
        if(col_name.equals("loc_id")) return true;
        if(col_name.equals("cat_id")) return true;
        if(col_name.equals("rat")) return true;
        if(col_name.equals("vote")) return true;
        if(col_name.equals("addr")) return true;
        if(col_name.equals("contact")) return true;
        if(col_name.equals("desc")) return true;
        return false;
    }

    public boolean exists(String name, int loc_id) throws SQLException {
        String sql = "SELECT id FROM service WHERE name = ? AND loc_id = ?";
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
        String sql = "SELECT id FROM service WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        return (pst.executeQuery()).next();
    }

    public boolean insert(String name, String loc_name, String cat_name, String addr, String contact, String desc) throws SQLException {
        int loc_id = location.getId(loc_name);
        int cat_id = catagory.getId(cat_name);
        if(loc_id == NULL_ID || cat_id == NULL_ID || exists(name, loc_id)) return false;

        double rat = 0.0;
        int vote = 0;
        String sql = "INSERT INTO service " +
                "(name, loc_id, cat_id, rat, vote, addr, contact, desc) values " +
                "(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);

        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        pst.setInt(3, cat_id);
        pst.setDouble(4, rat);
        pst.setInt(5, vote);
        pst.setString(6, addr);
        pst.setString(7, contact);
        pst.setString(8, desc);

        return (pst.executeUpdate() != 0);
    }

    public boolean delete(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "DELETE FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean update(String old_name, String old_loc_name, String new_name, String new_loc_name) throws SQLException {
        int old_loc_id = location.getId(old_loc_name);
        int new_loc_id = location.getId(new_loc_name);
        if(old_loc_id == NULL_ID || new_loc_id == NULL_ID || exists(new_name, new_loc_id)) return false;
        String sql = "UPDATE service SET name = ?, loc_id = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, new_name.toLowerCase());
        pst.setInt(2, new_loc_id);
        pst.setString(3, old_name.toLowerCase());
        pst.setInt(4, old_loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean update(String name, String loc_name, int new_id) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "UPDATE service SET id = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, new_id);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean updateCatagory(String name, String loc_name, String new_cat_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        int new_cat_id = catagory.getId(new_cat_name);
        if(loc_id == NULL_ID || new_cat_id == NULL_ID) return false;
        String sql = "UPDATE service SET cat_id = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, new_cat_id);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    private boolean updateRating(String name, String loc_name, double new_rat) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "UPDATE service SET rat = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setDouble(1, new_rat);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    private boolean updateVote(String name, String loc_name, int new_vote) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "UPDATE service SET vote = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, new_vote);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean updateAddress(String name, String loc_name, String new_addr) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "UPDATE service SET addr = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, new_addr);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean updateContact(String name, String loc_name, String new_contact) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "UPDATE service SET contact = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, new_contact);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public boolean updateDescription(String name, String loc_name, String new_desc) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return false;
        String sql = "UPDATE service SET desc = ? WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, new_desc);
        pst.setString(2, name.toLowerCase());
        pst.setInt(3, loc_id);
        return (pst.executeUpdate() != 0);
    }

    public int getId(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return NULL_ID;
        String sql = "SELECT id FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("id") : NULL_ID;
    }

    public String getName(int id) throws SQLException {
        String sql = "SELECT name FROM service WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getString("name") : null;
    }

    private int getLocationId(int id) throws SQLException {
        String sql = "SELECT loc_id FROM service WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("loc_id") : NULL_ID;
    }

    public String getLocation(int id) throws SQLException {
        int loc_id = getLocationId(id);
        return (loc_id == NULL_ID) ? null : location.getName(loc_id);
    }

    private int getCatagoryId(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return NULL_ID;
        String sql = "SELECT cat_id FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("cat_id") : NULL_ID;
    }

    public String getCatagory(String name, String loc_name) throws SQLException {
        int cat_id = getCatagoryId(name, loc_name);
        return (cat_id == NULL_ID) ? null : catagory.getName(cat_id);
    }

    public double getRating(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return ZERO;
        String sql = "SELECT rat FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getDouble("rat") : ZERO;
    }

    public int getVote(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return 0;
        String sql = "SELECT vote FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getInt("vote") : 0;
    }

    public String getAddress(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return null;
        String sql = "SELECT addr FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getString("addr") : null;
    }

    public String getContact(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return null;
        String sql = "SELECT contact FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getString("contact") : null;
    }

    public String getDescription(String name, String loc_name) throws SQLException {
        int loc_id = location.getId(loc_name);
        if(loc_id == NULL_ID) return null;
        String sql = "SELECT desc FROM service WHERE name = ? AND loc_id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, name.toLowerCase());
        pst.setInt(2, loc_id);
        ResultSet rs = pst.executeQuery();
        return (rs.next()) ? rs.getString("desc") : null;
    }

    private ResultSet getColumn(String col_name) throws SQLException {
        if(!isColumn(col_name)) return null;
        String sql = "SELECT " + col_name + " FROM service";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    private ResultSet getColumn(String col_name, int limit) throws SQLException {
        if(limit <= 0 || !isColumn(col_name)) return null;
        String sql = "SELECT " + col_name + " FROM service LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        return pst.executeQuery();
    }

    private ResultSet getColumnDistinct(String col_name) throws SQLException {
        if(!isColumn(col_name)) return null;
        String sql = "SELECT DISTINCT " + col_name + " FROM service";
        Statement st = connection.createStatement();
        return st.executeQuery(sql);
    }

    private ResultSet getColumnDistinct(String col_name, int limit) throws SQLException {
        if(limit <= 0 || !isColumn(col_name)) return null;
        String sql = "SELECT DISTINCT " + col_name + " FROM service LIMIT ?";
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

    public String[] getDistinctNames(int limit) throws SQLException {
        ResultSet rs = getColumnDistinct("name", limit);
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

    public String[] getDistinctLocations(int limit) throws SQLException {
        ResultSet rs = getColumnDistinct("loc_id", limit);
        if(rs == null) return null;
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(location.getName(rs.getInt("loc_id")));
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public String[] getDistinctCatagories() throws SQLException {
        ResultSet rs = getColumnDistinct("cat_id");
        if(rs == null) return null;
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(catagory.getName(rs.getInt("cat_id")));
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows() throws SQLException {
        String sql = "SELECT id, name, loc_id, cat_id, rat, vote, addr, contact, desc FROM service";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(rs.getInt("id"), rs.getString("name"),
                    location.getName(rs.getInt("loc_id")), catagory.getName(rs.getInt("cat_id")),
                    rs.getDouble("rat"), rs.getInt("vote"), rs.getString("addr"),
                    rs.getString("contact"), rs.getString("desc"));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public Row[] getRows(int limit) throws SQLException {
        if(limit <= 0) return null;

        String sql = "SELECT id, name, loc_id, cat_id, rat, vote, addr, contact, desc FROM service LIMIT ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, limit);
        ResultSet rs = pst.executeQuery();
        List<Row> list = new ArrayList<Row>();
        while(rs.next()) {
            Row row = new Row(rs.getInt("id"), rs.getString("name"),
                    location.getName(rs.getInt("loc_id")), catagory.getName(rs.getInt("cat_id")),
                    rs.getDouble("rat"), rs.getInt("vote"), rs.getString("addr"),
                    rs.getString("contact"), rs.getString("desc"));
            list.add(row);
        }
        Row[] array = new Row[list.size()];
        return list.toArray(array);
    }

    public void print() throws SQLException {
        Row[] rows = getRows();
        System.out.println("TABLE \"service\":");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        for(int i=0; i<rows.length; i++) System.out.println(rows[i]);
    }

    public boolean addRating(String name, String loc_name, double add_rat) throws SQLException {
        if(add_rat > MAX_RAT || add_rat < 0.00 || !exists(name, loc_name)) return false;
        updateRating(name, loc_name, getRating(name, loc_name) + add_rat);
        updateVote(name, loc_name, getVote(name, loc_name) + 1);
        return true;
    }

    public boolean addRating(String name, String loc_name, double add_rat, int no_of_user) throws SQLException {
        if((1 > no_of_user) || (add_rat / no_of_user) > MAX_RAT || add_rat < 0.00 || !exists(name, loc_name)) return false;
        updateRating(name, loc_name, getRating(name, loc_name) + add_rat);
        updateVote(name, loc_name, getVote(name, loc_name) + no_of_user);
        return true;
    }

    public double calculateRating(String name, String loc_name) throws SQLException {
        if(!exists(name, loc_name)) return 0.00;
        return (getRating(name, loc_name)/(double)getVote(name, loc_name));
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS service";
        Statement st = connection.createStatement();
        st.execute(sql);
    }
}

