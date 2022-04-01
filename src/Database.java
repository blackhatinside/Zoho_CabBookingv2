import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.logging.Logger;

public class Database {
    private static Connection connection=null;
    private final static String DB_URL="jdbc:postgresql://localhost:5432/cabbooking";
    private final static String DB_USERNAME="postgres";
    private final static String DB_PASSWORD="tswnciJ-";

    public static Connection getConnection(){
//        Logger logger = new Logger();
        try {
            Class.forName("org.postgresql.Driver");
            connection= DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
        } catch (ClassNotFoundException e) {
//            logger.logException(e);
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Problem in Connecting to Database");
//            logger.logException(e);
        }
        return connection;
    }
    public static boolean isConnectionActive(){
        try {
            return connection!=null && connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void getDisconnect(){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}