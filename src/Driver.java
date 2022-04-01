import java.util.ArrayList;
import java.util.List;

public class Driver {

    static int taxiCount = 0; // taxi number
    int id;
    boolean booked; //taxi booked or not
    char currentSpot; //where taxi is now
    int freeTime; // when taxi becomes free
    int totalEarnings; // total earnings of taxi
    List<String> trips; // all details of all trips by this taxi

    public Driver() {
        booked = false;
        currentSpot = 'A';//start point A
        freeTime = 6;//example 6 AM
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
