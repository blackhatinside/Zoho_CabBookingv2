/*
The are 6 points(A,B,C,D,E,F) 15 KM apart 60 min travel between each, n taxis all taxis at A starting
100 rs for first 5 KM and then 10 for each of the further KMs, rate from pickup to drop only
pickup time example : 9 hrs, 15 hrs

When a customer books a Taxi, a free taxi at that point is allocated
-If no free taxi is available at that point, a free taxi at the nearest point is allocated.
-If two taxis are free at the same point, one with lower earning is allocated
-If no taxi is free at that time, booking is rejected


Input 1:
Customer ID: 1
Pickup Point: A
Drop Point: B
Pickup Time: 9

Output 1:
Taxi can be allotted.
Taxi-1 is allotted

*/

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Booking {

    public static void bookTaxi(int customerID, char pickupPoint, char dropPoint, int pickupTime, List<Driver> freeTaxis) {
        // to find nearest
        int min = 999;

        //distance between pickup and drop
        int distanceBetweenPickUpAndDrop;   // 0

        //this trip earning
        int earning = 0;

        //when taxi will be free next
        int nextFreeTime = 0;

        //where taxi is after trip is over
        char nextSpot = 'Z';

        //booked taxi
        Driver bookedDriver = null;

        //all details of current trip as string
        String tripDetail = "";

        for (Driver t : freeTaxis) {
            int distanceBetweenCustomerAndTaxi = Math.abs((t.currentSpot - '0') - (pickupPoint - '0')) * 15;
            if (distanceBetweenCustomerAndTaxi < min) {
                bookedDriver = t;
                //distance between pickup and drop = (drop - pickup) * 15KM
                distanceBetweenPickUpAndDrop = Math.abs((dropPoint - '0') - (pickupPoint - '0')) * 15;
                //trip earning = 100 + (distanceBetweenPickUpAndDrop-5) * 10
                earning = (distanceBetweenPickUpAndDrop - 5) * 10 + 100;

                //drop time calculation
                int dropTime = pickupTime + distanceBetweenPickUpAndDrop / 15;

                //when taxi will be free next
                nextFreeTime = dropTime;

                //taxi will be at drop point after trip
                nextSpot = dropPoint;

                // creating trip detail
                tripDetail = customerID + "          " + pickupPoint + "      " + dropPoint + "       " + pickupTime + "          " + dropTime + "           " + earning;
                min = distanceBetweenCustomerAndTaxi;
            }

        }

        //setting corresponding details to allotted taxi
        assert bookedDriver != null;
        bookedDriver.setDetails(true, nextSpot, nextFreeTime, bookedDriver.totalEarnings + earning, tripDetail);
        //BOOKED SUCCESSFULLY
        System.out.println("Taxi " + bookedDriver.id + " booked");

    }

    public static List<Driver> createTaxis(int n) {
        List<Driver> taxis = new ArrayList<>();
        // create taxis
        for (int i = 1; i <= n; i++) {
            Driver t = new Driver();
            taxis.add(t);
        }
        return taxis;
    }

    public static List<Driver> getFreeTaxis(List<Driver> taxis, int pickupTime, char pickupPoint) {
        List<Driver> freeTaxis = new ArrayList<>();
        for (Driver t : taxis) {
            //taxi should be free
            //taxi should have enough time to reach customer before pickUpTime
            if (t.freeTime <= pickupTime && (Math.abs((t.currentSpot - '0') - (pickupPoint - '0')) <= pickupTime - t.freeTime))
                freeTaxis.add(t);

        }
        return freeTaxis;
    }


    public static void main(String[] args) {

        int n = 4;  // number of drivers

        // todo: Connect to DB ----- DONE
        Connection conn = Database.getConnection();     // static method call

        // todo: Check if current user is admin or not  ----- DONE
        Login.admin_login(3);
        boolean isCurrentUserAdmin = Login.isAdmin;
        System.out.println("Current Admin Status: " + isCurrentUserAdmin);

        // todo: path finding using Dijkstra ----- DONE

        // todo: prepared statements to make DB ----- DONE


        Database.getDisconnect();   // close database connection

        System.out.println("Enter number of taxis: ");

        //create n taxis
        List<Driver> taxis = createTaxis(n);

        Scanner scan = new Scanner(System.in);
        int id = 1;

        while (true) {
            System.out.println("0 - > Book Taxi");
            System.out.println("1 - > Print Taxi details");
//            int choice = s.nextInt();
            int choice;
            do {
                System.out.println("Please enter a positive number!");
                while (!scan.hasNextInt()) {
                    System.out.println("That's not a number!");
                    scan.next(); // this is important!
                }
                choice = scan.nextInt();
            } while (choice < 0);
            System.out.println(choice == 0 ? "Booking Taxi" : choice == 1
                    ? "Printing Taxi Details" : "Invalid Input");
            switch (choice) {
                case 0: {
                    //get details from customers

                    //int customerID = id;
                    System.out.println("Enter Pickup point");
                    char pickupPoint = scan.next().charAt(0);
                    System.out.println("Enter Drop point");
                    char dropPoint = scan.next().charAt(0);
                    System.out.println("Enter Pickup time");
                    int pickupTime = scan.nextInt();

                    //check if pickup and drop points are valid
                    if (pickupPoint < 'A' || pickupPoint > 'F' || dropPoint < 'A' || dropPoint > 'F') {
                        System.out.println("Valid pickup and drop are A, B, C, D, E, F. Exiting");
                        return;
                    }

                    // get all free taxis that can reach customer on or before pickup time
                    List<Driver> freeTaxis = getFreeTaxis(taxis, pickupTime, pickupPoint);
                    //no free taxi means we cannot allot, exit!
                    if (freeTaxis.size() == 0) {
                        System.out.println("No Taxi can be allotted. Exiting");
                        return;
                    }
                    //sort taxis based on earnings
                    freeTaxis.sort(Comparator.comparingInt(a -> a.totalEarnings));
                    // 3,4,2 - > 2,3,4

                    //get free Taxi nearest to us
                    bookTaxi(id, pickupPoint, dropPoint, pickupTime, freeTaxis);
                    id++;
                    break;
                }
                case 1: {
                    //two functions to print details
                    for (Driver t : taxis)
                        t.printTaxiDetails();
                    for (Driver t : taxis)
                        t.printAllDetails();
                    break;
                }
                default:
                    return;
            }
        }
    }
}
