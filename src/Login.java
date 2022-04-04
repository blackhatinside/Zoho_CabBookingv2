import java.util.HashMap;
import java.util.Scanner;

public class Login {
    public static boolean isAdmin = false;
    //    static Scanner scan = new Scanner(System.in);
    private static final Admin curr_admin = new Admin();

    public static void admin_login(int maxAttempts) {
        // todo: Admin Login into the application *****DONE*****
        int login_attempts = 0;
        while (login_attempts < maxAttempts && !(isAdmin)) {
            login_attempts++;
            isAdmin = curr_admin.auth();
            if (isAdmin) {
                System.out.println("Login Successful");
            } else {
                System.out.println(3 - login_attempts + "attempts left.");
            }
//             System.out.println(isSession);      // return value of the login block
        }
        if (!isAdmin) {
            System.out.println("Max login attempts exceeded.");
            System.exit(1);
        } else {
            System.out.println("Welcome Back Admin!");
        }
    }

    public static void driver_login(int maxAttempts) {
        Scanner scan = new Scanner(System.in);
        int login_attempts = 0;
        boolean isDriver = false;
        String driverUsername = "", driverPassword = "";
        while (login_attempts++ < maxAttempts && (!isDriver)) {
            System.out.println("Enter your username: ");
            driverUsername = scan.nextLine();
            System.out.println("Enter your password: ");
            driverPassword = scan.nextLine();
            HashMap<String, String> hp = FileUtils.HashMapFromTextFile();
            isDriver = driverPassword.equals(hp.getOrDefault(driverUsername, ""));
            if (isDriver) {
                System.out.println("Login Successful");
            } else {
                System.out.println(3 - login_attempts + "attempts left.");
            }
//             System.out.println(isSession);      // return value of the login block
        }
        if (!isDriver) {
            System.out.println("Max login attempts exceeded.");
            // System.exit(1);
            return;
        } else {
            System.out.println("Welcome Back " + driverUsername);
        }
    }
}
