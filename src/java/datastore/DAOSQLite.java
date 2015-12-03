package datastore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Car;

/**
 * DAOSQLite Data Access Object for an SQLite database
 *
 * @author John Phillips
 * @version 0.3 on 2015-11-03 revised 2015-11-19
 */
public class DAOSQLite {

    protected final static String DRIVER = "org.sqlite.JDBC";
    protected final static String JDBC = "jdbc:sqlite";

    /**
     * Inserts an record into the database table. Note the use of a
     * parameterized query to prevent SQL Injection attacks.
     *
     * @param car the object to insert
     * @param dbPath the path to the SQLite database
     */
    public static void createRecord(Car car, String dbPath) {
        String q = "insert into user (id, email, phone, date, time, notes) "
                + "values (null, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnectionDAO(dbPath);
                PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, car.getEmail());
            ps.setInt(2, car.getPhone());
            ps.setString(3, car.getDate());
            ps.setString(4, car.getTime());
            ps.setString(5, car.getNotes());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOSQLite.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieve all of the records in the database as a list sorted by
     * email+date+time. This method was replaced by a more advanced method.
     *
     * @param dbPath the path to the SQLite database
     * @return list of objects
     */
//    public static List<User> retrieveAllRecords(String dbPath) {
//        String q = "select * from user order by email, date, time";
//        List<User> list = null;
//        try (Connection conn = getConnectionDAO(dbPath);
//                PreparedStatement ps = conn.prepareStatement(q)) {
//            list = myQuery(conn, ps);
//        } catch (SQLException ex) {
//            Logger.getLogger(DAOSQLite.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//        return list;
//    }
    
    /**
     * This is a much more advanced retrieve method. It can get all of the
     * records from the database or a subset based on the various parameters
     * passed in.
     *
     * @param dbPath the path to the SQLite database
     * @param email - the email of the user/patient
     * @param startdate - the starting date of the readings to show
     * @param enddate - the ending date of the readings to show
     * @param lowhigh - controls which phone levels to show; values include all, low, high, and lowhigh
     * @return list of objects
     */
    public static List<Car> retrieveRecords(String dbPath, String email, String startdate, String enddate, String lowhigh) {
        // Need a better solution to the hard coded low/high values below.
        String q = "select * from car where email like ? and date between ? and ? order by email, date, time";
        if (lowhigh.equalsIgnoreCase("low")) {
            q = "select * from user where email like ? and date between ? and ? and phone < 50 order by email, date, time";
        } else if (lowhigh.equalsIgnoreCase("high")) {
            q = "select * from user where email like ? and date between ? and ? and phone > 200 order by email, date, time";
        } else if (lowhigh.equalsIgnoreCase("lowhigh")) {
            q = "select * from user where email like ? and date between ? and ? and (phone < 50 or phone > 200) order by email, date, time";
        }

        List<Car> list = null;
        try (Connection conn = getConnectionDAO(dbPath);
                PreparedStatement ps = conn.prepareStatement(q)) {
            // the % sign is an sql wildcard so that we can search by just a few letters of the email name
            ps.setString(1, email + "%");
            ps.setString(2, startdate);
            ps.setString(3, enddate);
            System.out.println(q);
            list = myQuery(conn, ps);
        } catch (SQLException ex) {
            Logger.getLogger(DAOSQLite.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Delete a record from the database given its id. Note the use of a
     * parameterized query to prevent SQL Injection attacks.
     *
     * @param id the id of the record to delete
     * @param dbPath the path to the SQLite database
     */
    public static void deleteRecord(int id, String dbPath) {
        String q = "delete from user where id = ?";
        try (Connection conn = getConnectionDAO(dbPath);
                PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOSQLite.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new user table.
     *
     * @param dbPath the path to the SQLite database
     */
    public static void createTable(String dbPath) {
        String q = "create table user ("
                + "id integer not null primary key autoincrement, "
                + "email varchar(20) not null, "
                + "phone integer not null, "
                + "date varchar(10) not null, "
                + "time varchar(10) not null, "
                + "notes varchar(255) null)";
        try (Connection conn = getConnectionDAO(dbPath);
                PreparedStatement ps = conn.prepareStatement(q)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOSQLite.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Drops the user table erasing all of the data.
     *
     * @param dbPath the path to the SQLite database
     */
    public static void dropTable(String dbPath) {
        final String q = "drop table if exists user";
        try (Connection conn = getConnectionDAO(dbPath);
                PreparedStatement ps = conn.prepareStatement(q)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOSQLite.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Populates the table with sample data records.
     *
     * @param dbPath the path to the SQLite database
     */
    public static void populateTable(String dbPath) {
        Car p;
        p = new Car(0, "katie@test.com", 120, "2015-09-01", "03:30", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 220, "2015-10-16", "13:50", "This is an example of a high reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 45, "2015-11-02", "12:30", "This is an example of a low reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 120, "2015-11-05", "15:00", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 120, "2015-11-07", "06:30", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 200, "2015-11-15", "15:00", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 201, "2015-11-17", "06:30", "This is an example of an out of order date.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 40, "2015-11-17", "05:00", "This is an example of an out of order time.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 120, "2015-11-17", "16:30", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 38, "2015-11-04", "15:00", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
        p = new Car(0, "katie@test.com", 240, "2015-11-07", "09:30", "This is an example of a normal reading.");
        DAOSQLite.createRecord(p, dbPath);
    }

    /**
     * A helper method that executes a prepared statement and returns the result
     * set as a list of objects.
     *
     * @param conn a connection to the database
     * @param ps a prepared statement
     * @return list of objects from the result set
     */
    protected static List<Car> myQuery(Connection conn, PreparedStatement ps) {
        List<Car> list = new ArrayList();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                int phone = rs.getInt("phone");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String notes = rs.getString("notes");
                Car p = new Car(id, email, phone, date, time, notes);
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAOSQLite.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Creates a connection to the SQLite database.
     *
     * @param dbPath the path to the SQLite database
     * @return connection to the database
     */
    protected static Connection getConnectionDAO(String dbPath) {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(JDBC + ":" + dbPath);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DAOSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
}
