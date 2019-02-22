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
        import java.util.Arrays;

        import com.example.agent_47.helpathandtest1.Database;
        import com.example.agent_47.helpathandtest1.Source;
        import com.example.agent_47.helpathandtest1.Service;
        import com.example.agent_47.helpathandtest1.Location;
        import com.example.agent_47.helpathandtest1.Catagory;
        import com.example.agent_47.helpathandtest1.Path;

public class Request extends Monga {
    private Database database = null;
    private Connection connection = null;
    private Source source = null;
    private Service service = null;
    private Catagory catagory = null;
    private Location location = null;
    private Path path = null;

    public static class Help implements Comparable<Help> {
        private int vote;
        private double dist, rat, cal_rat;
        private String name, loc_name, addr, contact, desc;

        public Help(String name, String loc_name, double rat, int vote, String addr, String contact, String desc, double dist) {
            this.name = name;
            this.loc_name = loc_name;
            this.rat = rat;
            this.vote = vote;
            this.addr = addr;
            this.contact = contact;
            this.desc = desc;
            this.dist = dist;
            this.cal_rat = this.rat / this.vote;
        }
        public String getName() {
            return this.name;
        }
        public String getLocation() {
            return this.loc_name;
        }
        public double getRating() {
            return this.rat;
        }
        public int getVote() {
            return this.vote;
        }
        public double calculateRating() {
            return this.cal_rat;
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
        public double getDistance() {
            return this.dist;
        }
        public int compareTo(Help help) {
            if(Math.abs(this.getDistance() - help.getDistance()) < ZERO_PLUS_X) {
                if(Math.abs(this.getRating() * help.getVote() - help.getRating() * this.getVote()) < ZERO_PLUS_X) return 0;
                if(this.getRating() * help.getVote() > help.getRating() * this.getVote()) return -1;
                return 1;
            }
            if(this.getDistance() < help.getDistance()) return -1;
            return 1;
        }
        public String toString() {
            return "['" + name + "', '" + loc_name + "', '" + cal_rat + "', '" + addr + "', '" + contact + "', '" + desc + "', '" + dist + "']";
        }
    }

    public Request(Database database, Source source, Service service, Path path, Location location, Catagory catagory) throws SQLException {
        this.database = database;
        this.connection = database.getConnection();
        this.source = source;
        this.service = service;
        this.path = path;
        this.location = location;
        this.catagory = catagory;
    }

    public Database getDatabase() {
        return this.database;
    }

    public Service getService() {
        return this.service;
    }

    public Help[] getHelps(String from_name, String cat_name) throws SQLException {
        int from_id = location.getId(from_name);
        int cat_id = catagory.getId(cat_name);
        if(from_id == NULL_ID || cat_id == NULL_ID) return null;

        String sql = "SELECT name, loc_id, rat, vote, addr, contact, desc " +
                "FROM service " +
                "WHERE cat_id = ?";

        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, cat_id);
        ResultSet rs = pst.executeQuery();
        List<Help> list = new ArrayList<Help>();
        while(rs.next()) {
            int to_id = rs.getInt("loc_id");
            if(!path.exists(from_id, to_id)) continue;
            Help help = new Help(rs.getString("name"), location.getName(to_id), rs.getDouble("rat"), rs.getInt("vote"),
                    rs.getString("addr"), rs.getString("contact"), rs.getString("desc"), path.getDistance(from_id, to_id));
            list.add(help);
        }
        Help[] array = new Help[list.size()];
        array = list.toArray(array);
        Arrays.sort(array);
        return array;
    }

    public boolean addRating(String name, String loc_name, double rat, int no_of_user) throws SQLException {
        return service.addRating(name, loc_name, rat, no_of_user);
    }

    public boolean addRating(String name, String loc_name, double rat) throws SQLException {
        return service.addRating(name, loc_name, rat);
    }
}

