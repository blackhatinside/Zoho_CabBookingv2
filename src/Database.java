import java.sql.*;
//import java.util.logging.Logger;

public class Database {
    private static Connection connection = null;
    private final static String DB_URL = "jdbc:postgresql://localhost:5432/cabbooking";
    private final static String DB_USERNAME = "postgres";
    private final static String DB_PASSWORD = "tswnciJ-";

    Database() {
        //        Logger logger = new Logger();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
//            logger.logException(e);
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error in Connecting to PostgreSQL server.");
//            logger.logException(e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static boolean isConnectionActive() {
        try {
            return connection != null && connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void getDisconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void runQuery(String userQuery) {
        try {
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(userQuery);
            System.out.println(rowsAffected + " ROWS AFFECTED.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void fetchTables(String userQuery) {

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