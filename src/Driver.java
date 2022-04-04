import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    static int taxiCount = 0; // taxi number
    int id; // driverID
    boolean onLeave = false;    //taxi driver on leave or not
    boolean booked; //taxi booked or not
    char currentSpot; //where taxi is now
    int freeTime; // when taxi becomes free
    int totalEarnings; // total earnings of taxi
    String reasonLeave = ""; // reason for taking leave if any
    List<String> trips; // all details of all trips by this taxi

    private static PreparedStatement stmt = null;
    Database db = new Database();
    public Connection conn = Database.CONNECTION;
    ;

    public Driver() throws SQLException {
        booked = false;
        currentSpot = 'A';//start point A
//        freeTime = 6; // initial available time of all Taxis
        freeTime = 0; // initial available time of all Taxis
        totalEarnings = 0;
        trips = new ArrayList<>();
        taxiCount = taxiCount + 1; // everytime new taxi is created a new id will be assigned
        id = taxiCount;
    }


    public void setDetails(boolean booked, char currentSpot, int freeTime, int totalEarnings, String tripDetail) {
        this.booked = booked;
        this.currentSpot = currentSpot;
        this.freeTime = freeTime;
        this.totalEarnings = totalEarnings;
        this.trips.add(tripDetail);
        Booking.db.addRow(id, onLeave, booked, currentSpot, totalEarnings);
    }

    public void printAllDetails() {
        //print all trips details
        System.out.println("Taxi - " + this.id + " Total Earnings - " + this.totalEarnings);
        System.out.println("TaxiID    BookingID    From    To    PickupTime    DropTime    Amount");
        for (String trip : trips) {
            System.out.println(id + "          " + trip);
        }
        System.out.println("--------------------------------------------------------------------------------------");
    }

    public void printTaxiDetails() {
        //print total earning and taxi details like current location and free time
        System.out.println("Taxi - " + this.id + " Total Earnings - " + this.totalEarnings + " Current spot - " + this.currentSpot + " Available After - " + this.freeTime);
    }
}