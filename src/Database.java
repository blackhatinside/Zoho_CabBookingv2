import java.sql.*;
//import java.util.logging.Logger;

public class Database {
    private final static String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String DB_USERNAME = "postgres";
    private final static String DB_PASSWORD = "tswnciJ-";
    public static Connection CONNECTION = null;
    PreparedStatement stmt;

    Database() {
        //        Logger logger = new Logger();
        try {
            Class.forName("org.postgresql.Driver");
            CONNECTION = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
//            logger.logException(e);
            System.out.println("Error in JDBC Driver Manager");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error in Connecting to PostgreSQL server." + e);
//            logger.logException(e);
        }
    }

    public Connection getconnection() {
        return CONNECTION;
    }

    void clear() {
        String truncate = "TRUNCATE bookingdetails;";
        String flush = "ALTER SEQUENCE bookingdetails_bookingid_seq RESTART WITH 1";
        try {
            stmt = CONNECTION.prepareStatement(truncate);
            stmt.executeUpdate();
            stmt = CONNECTION.prepareStatement(flush);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnectionActive() {
        try {
            return CONNECTION != null && CONNECTION.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getDisconnect() {
        try {
            CONNECTION.close();
        } catch (Exception e) {

        }
    }


    public void addRow(int id, boolean onLeave, boolean booked, char currentSpot, int amount) {
        try {
//            if (!Database.isConnectionActive()) {
//                System.out.println("Failed to connect to DB! Exiting...");
//                System.exit(0);
//            }
            stmt = CONNECTION.prepareStatement("INSERT INTO bookingDetails VALUES (DEFAULT, ?, ?, ?, ?, ?);");
            stmt.setInt(1, id);     // taxiID
            stmt.setBoolean(2, onLeave);     // taxi present/available
            stmt.setString(3, booked ? "Unavailable" : "Available");   // current booking status
            stmt.setString(4, currentSpot + "");   // current location
            stmt.setInt(5, amount);   // amount earned

            //stmt.setString(3, String.valueOf('A'));    // fromWhere
            //stmt.setString(4, String.valueOf('C'));    // toWhere
            //stmt.setInt(5, 9);     // pickUpTime
            //stmt.setInt(6, 11);    // dropTime

            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " ROWS AFFECTED");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void runQuery(PreparedStatement userQuery) {
        try {
            int rowsAffected = userQuery.executeUpdate();
            // System.out.println(rowsAffected + " ROWS AFFECTED.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void searchTaxi(int taxiID) {
        try {
            //      System.out.println("dssd");
            stmt = Database.CONNECTION.prepareStatement("SELECT * FROM bookingdetails WHERE taxiID = ?");
            stmt.setInt(1, taxiID); // 1 species the first param at index 1 in the preparedStatement

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("All Taxis are idle.");
                return;
            }
            while (rs.next()) {
                System.out.println("Match Found ");
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
                System.out.println(rs.getString(3));
                System.out.println(rs.getString(4));
                System.out.println(rs.getString(5));
            }
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}

/*
	CREATE TABLE IF NOT EXISTS bookingdetails (
	TaxiID INTEGER,
	TaxiStatus BOOLEAN,
	BookingID INTEGER PRIMARY KEY,
	CurrentSpot TEXT,
	FromWhere TEXT,
	ToWhere TEXT,
	PickupTime INTEGER,
	DropTime INTEGER,
	Amount INTEGER);
 */