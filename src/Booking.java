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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Booking {
    static Database db;
    static int totalCabs = 6;
    static int n = totalCabs * 2;   // to make searching for nearest cab as O(n) time
    static char[] cabList = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F'};
    //    stores the distance of all cities from point 0 in both f/w and b/w directions
//    static int[] dist = new int[n];
    static int[] dist = new int[]{0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165};

    public static void main(String[] args) {

        int n = 4;  // number of drivers

        // todo: Connect to DB ----- DONE
        db = new Database();
        // static method call
        db.clear(true);   // to empty the existing DB   true: clears DB
        // todo: Check if current user is admin or not  ----- DONE
        Login.admin_login(3);
        boolean isCurrentUserAdmin = Login.isAdmin;
        System.out.println("Current Admin Status: " + isCurrentUserAdmin);

        // todo: path finding using Dijkstra ----- DONE

        // todo: prepared statements to make DB ----- DONE

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
            System.out.println("3 - > Search Taxi (by ID):");
            System.out.println("4 - > Exit");
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
                                            choice == 3 ? "Searching..." :
                                                    choice == 4 ? "Exiting..." :
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
                    System.out.println("Enter Pickup time (in hours 0 - 23): ");
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
                    //for (Driver t : taxis)    // extra details
                        //t.printAllDetails();  // extra details
                    break;
                }
                case 2: {
                    int taxiID = 1;
                    boolean loginFromFile = false;
                    int loginStatus = Login.driver_login(3, loginFromFile);
                    System.out.println("DEBUG Login Status: " + loginStatus);
                    System.out.println("DEBUG Login from File: " + loginFromFile);
                    if (loginStatus == -1) {
                        break;
                    }
                    if (!loginFromFile) {
                        taxiID = loginStatus;
                    } else {
                        System.out.println("Enter your Taxi ID: ");
                        while (taxiID < 1 || taxiID > 4) {
                            System.out.println("Enter ID (1 - 4): ");
                            taxiID = scan.nextInt();
                        }
                    }
                    scan.nextLine();
//                    assert (!(taxiID <= 0 && taxiID >= n));
                    System.out.println("Enter the reason: ");
                    String reasonLeave = scan.nextLine();
                    taxis = Booking.putLeave(taxis, taxiID - 1, reasonLeave);   // zero indexing
                    break;
                }
                case 3: {
                    System.out.println("Search any active taxi ID: ");
                    int taxiID = 0;
                    do {
                        System.out.println("Enter ID (1 - 4): ");
                        taxiID = scan.nextInt();
                    } while (taxiID < 1 && taxiID > 4);
                    scan.nextLine();
//                    assert (!(taxiID <= 0 && taxiID >= n));
                    db.searchTaxi(taxiID);
                    break;
                }
                default: {
                    continue;
                }
                case 4: {
                    loopFlag = false;
                    break;
                }
            }
        }
        db.getDisconnect();   // close database connection
    }

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

        boolean loopFlag;

        for (Driver t : freeTaxis) {
            if (t.onLeave) {
                continue;
            }
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
        // bookedDriver.addRow();
        System.out.println("Taxi " + bookedDriver.id + " booked");

    }

    public static List<Driver> createTaxis(int n) {
        List<Driver> taxis = new ArrayList<>();
        // clear all previous leave applications
        String userQuery0 = "TRUNCATE leaveapplications;";
        String userQuery = "INSERT INTO leaveApplications (taxiID, reason) VALUES(?, ?);";
        // create taxis
        try {
            PreparedStatement stmt = Database.getConnection().prepareStatement(userQuery0);
            int rowsAffected = stmt.executeUpdate();
            // System.out.println(rowsAffected + " ROWS AFFECTED");
            stmt = Database.getConnection().prepareStatement(userQuery);;
            for (int i = 1; i <= n; i++) {
                Driver t = new Driver();
                stmt.setInt(1, i);
                stmt.setString(2, "");
                rowsAffected = stmt.executeUpdate();
                // System.out.println(rowsAffected + "ROWS AFFECTED");
                taxis.add(t);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taxis;
    }

    public static List<Driver> putLeave(List<Driver> taxis, int id, String reason) {   // apply leave using Taxi ID
//        for (int i = 0; i < taxis.size();)
        taxis.get(id).onLeave = true;
        taxis.get(id).reasonLeave = reason;

        String userQuery = "UPDATE leaveapplications SET reason = ? WHERE taxiID = ?;";
        try {
            PreparedStatement stmt = Database.getConnection().prepareStatement(userQuery);
            stmt.setString(1, reason);
            stmt.setInt(2,id + 1);
            int rowsAffected = stmt.executeUpdate();
//            System.out.println(rowsAffected + " ROWS AFFECTED");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taxis;
    }

    public static List<Driver> getFreeTaxis(List<Driver> taxis, int pickupTime, char pickupPoint) {
        List<Driver> freeTaxis = new ArrayList<>();
        for (Driver t : taxis) {
//            if (t.onLeave) {
//                continue;
//            }
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
            System.out.println("Taxi " + t.id + " : " + (t.onLeave ? ("Absent: " + t.reasonLeave) : "Available"));
//            System.out.println("DEBUG: " + t.onLeave);
            if ((t.freeTime <= pickupTime) && ((distanceBetweenCurrentSpotAndPickup <= pickupTime - t.freeTime) && (!t.onLeave)))
                freeTaxis.add(t);

        }
        return freeTaxis;
    }


}


