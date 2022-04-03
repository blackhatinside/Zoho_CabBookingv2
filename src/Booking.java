/*

Total points : 6 (A,B,C,D,E,F) forming circular array, each 15 KM apart (distance can also vary if needed)
no. of taxis : 4 (can scale if needed)
taxis starting point: A (point 0)
100 rs for first 5 KM and cumulative interest of 10 rs for every additional 5 kms
pickup time - in hours only

When a customer books a Taxi, a free taxi at that point is allocated
-If no free taxi is available at that point, a free taxi at the nearest point is allocated.
-If two taxis are free at the same point, one with lower earning is allocated
-If no taxi is free at that time, booking is rejected

*/

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Booking {

    static int totalCabs = 6;
    static int n = totalCabs * 2;   // to make searching for nearest cab as O(n) time
    static char[] cabList = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F'};
    //    stores the distance of all cities from point 0 in both f/w and b/w directions
//    static int[] dist = new int[n];
    static int[] dist = new int[]{0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165};

    public static void bookTaxi(int customerID, char pickupPoint, char dropPoint, int pickupTime, List<Driver> freeTaxis) {
        // to find nearest
        int min = 999;

        //distance between pickup and drop
        int distanceBetweenPickUpAndDrop = 0;   // 0

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

        boolean loopFlag = true;

        for (Driver t : freeTaxis) {
            // A B C D E F
            // distance between customer and taxi
            int distanceBetweenCustomerAndTaxi = 1000;
            loopFlag = true;
            for (int i = 0; i < n && loopFlag; i++) {
                if (cabList[i] == t.currentSpot) {
                    int pathBeginsDistance = dist[i];
                    for (int j = i; j < n; j++) {
                        if (cabList[j] == pickupPoint) {
                            int pathEndsDistance = dist[j];
                            distanceBetweenCustomerAndTaxi = Math.min(pathEndsDistance - pathBeginsDistance, distanceBetweenCustomerAndTaxi);
                            loopFlag = false;
                            break;
                        }
                    }
                }
            }
            loopFlag = true;
            for (int i = 0; i < n && loopFlag; i++) {
                if (cabList[i] == pickupPoint) {
                    int pathBeginsDistance = dist[i];
                    for (int j = i; j < n; j++) {
                        if (cabList[j] == t.currentSpot) {
                            int pathEndsDistance = dist[j];
                            distanceBetweenCustomerAndTaxi = Math.min(pathEndsDistance - pathBeginsDistance, distanceBetweenCustomerAndTaxi);
                            loopFlag = false;
                            break;
                        }
                    }
                }
            }

            if (distanceBetweenCustomerAndTaxi < min) {

                bookedDriver = t;
                // distance between pickup and drop
                distanceBetweenPickUpAndDrop = 1000;
                loopFlag = true;
                for (int i = 0; i < n && loopFlag; i++) {
                    if (cabList[i] == dropPoint) {
                        int pathBeginsDistance = dist[i];
                        for (int j = i; j < n; j++) {
                            if (cabList[j] == pickupPoint) {
                                int pathEndsDistance = dist[j];
                                distanceBetweenPickUpAndDrop = Math.min(pathEndsDistance - pathBeginsDistance, distanceBetweenPickUpAndDrop);
                                loopFlag = false;
                                break;
                            }
                        }
                    }
                }
                loopFlag = true;
                for (int i = 0; i < n && loopFlag; i++) {
                    if (cabList[i] == pickupPoint) {
                        int pathBeginsDistance = dist[i];
                        for (int j = i; j < n; j++) {
                            if (cabList[j] == dropPoint) {
                                int pathEndsDistance = dist[j];
                                distanceBetweenPickUpAndDrop = Math.min(pathEndsDistance - pathBeginsDistance, distanceBetweenPickUpAndDrop);
                                loopFlag = false;
                                break;
                            }
                        }
                    }
                }
                // earning = 100 + cumulative((PickUp&Drop-5) * 10 @interestRate of $10 per 5kms)
                earning = 100;
                // System.out.println("Your Distance: " + distanceBetweenPickUpAndDrop);
                int remainingDistance = distanceBetweenPickUpAndDrop - 5;
                int currentCost = 100;
                while (remainingDistance - 5 >= 0) {
                    // System.out.println("Your Distance: " + remainingDistance + " " + earning);
                    currentCost += 10;
                    remainingDistance -= 5;
                    earning += currentCost;
                }
                if (remainingDistance != 0) {
                    earning += currentCost / remainingDistance;
                }
                // drop time calculation
                int dropTime = pickupTime + distanceBetweenPickUpAndDrop / 15;

                // when taxi will be free next
                nextFreeTime = dropTime;

                // taxi will be at drop point after trip
                nextSpot = dropPoint;

                // creating trip detail
                tripDetail =
                        customerID + "          " + pickupPoint + "      " + dropPoint + "       " + pickupTime + "  " +
                                "        " + dropTime + "           " + earning + "           ";
                min = distanceBetweenCustomerAndTaxi;
            }

        }

        // setting corresponding details to allotted taxi
        assert bookedDriver != null;
        bookedDriver.setDetails(true, nextSpot, nextFreeTime, bookedDriver.totalEarnings + earning, tripDetail);
        // BOOKED SUCCESSFULLY
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

    public static List<Driver> putLeave(List<Driver> taxis, int id, String reason) {   // apply leave using Taxi ID
//        for (int i = 0; i < taxis.size();)
        taxis.get(id).onLeave = true;
        taxis.get(id).reasonLeave = reason;
        return taxis;
    }

    public static List<Driver> getFreeTaxis(List<Driver> taxis, int pickupTime, char pickupPoint) {
        List<Driver> freeTaxis = new ArrayList<>();
        for (Driver t : taxis) {
            //taxi should be free
            //taxi should have enough time to reach customer before pickUpTime
            int distanceBetweenCurrentSpotAndPickup = 0;
            boolean loopFlag;
            loopFlag = true;
            for (int i = 0; i < n && loopFlag; i++) {
                if (cabList[i] == t.currentSpot) {
                    int pathBeginsDistance = dist[i];
                    for (int j = i; j < n; j++) {
                        if (cabList[j] == pickupPoint) {
                            int pathEndsDistance = dist[j];
                            distanceBetweenCurrentSpotAndPickup = Math.min(pathEndsDistance - pathBeginsDistance, distanceBetweenCurrentSpotAndPickup);
                            loopFlag = false;
                            break;
                        }
                    }
                }
            }
            loopFlag = true;
            for (int i = 0; i < n && loopFlag; i++) {
                if (cabList[i] == pickupPoint) {
                    int pathBeginsDistance = dist[i];
                    for (int j = i; j < n; j++) {
                        if (cabList[j] == t.currentSpot) {
                            int pathEndsDistance = dist[j];
                            distanceBetweenCurrentSpotAndPickup = Math.min(pathEndsDistance - pathBeginsDistance, distanceBetweenCurrentSpotAndPickup);
                            loopFlag = false;
                            break;
                        }
                    }
                }
            }
            System.out.println(t.id + ": " + t.onLeave);
            if (t.freeTime <= pickupTime && (distanceBetweenCurrentSpotAndPickup <= pickupTime - t.freeTime && t.onLeave == false))
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
        Scanner scan2 = new Scanner(System.in);
        scan2.useDelimiter("");
        int id = 1;
        boolean loopFlag = true;

        while (loopFlag) {
            System.out.println("0 - > Book Taxi");
            System.out.println("1 - > Print Taxi details");
            System.out.println("2 - > Apply Leave");
            System.out.println("3 - > Exit");
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
            System.out.println(
                    choice == 0 ? "Booking Taxi" :
                            choice == 1 ? "Printing Taxi Details" :
                                    choice == 2 ? "Applying Leave" :
                                            choice == 3 ? "Exiting..." :
                                                    "Invalid Input");
            switch (choice) {
                case 0: {
                    //get details from customers

                    //int customerID = id;
                    System.out.println("Choose a Pickup point (A - F):");
                    char pickupPoint = scan.next().charAt(0);
                    if (!(pickupPoint >= 65 && pickupPoint <= 70)) {
                        System.out.println("Invalid Pickup point!");
                        break;
                    }
                    System.out.println("Enter Drop point (A - F): ");
                    char dropPoint = scan.next().charAt(0);
                    if (!(dropPoint >= 65 && dropPoint <= 70)) {
                        System.out.println("Invalid Drop point!");
                        break;
                    }
                    System.out.println("Enter Pickup time: ");
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
                    // 3,4,2 -> 2,3,4

                    //get free Taxi nearest to us
                    bookTaxi(id, pickupPoint, dropPoint, pickupTime, freeTaxis);
                    id++;
                    break;
                }
                case 1: {
                    //two functions to print details
                    for (Driver t : taxis)
                        t.printTaxiDetails();
                    // for (Driver t : taxis)
                        // t.printAllDetails();
                    break;
                }
                case 2: {
                    Login.driver_login(3);
                    System.out.println("Enter your Cab ID: ");
                    int taxiID = scan.nextInt();
                    assert  (taxiID < 0 && taxiID >= n);
                    System.out.println("Enter the reason: ");
                    String reasonLeave = scan.nextLine();
                    taxis = Booking.putLeave(taxis, taxiID, reasonLeave);
                    break;
                }
                case 3: {
                    loopFlag = false;
                    break;
                }
                // default:
                // return;
            }
        }
    }
}
