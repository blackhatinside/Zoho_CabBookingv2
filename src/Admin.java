import java.util.Scanner;

public class Admin {            // DONE
    public String username;
    public String password; // private
    public Scanner scan;    // static

    public Admin() {
        scan = new Scanner(System.in);
    }

    public boolean auth() {
        System.out.println("Enter your username: ");
        username = scan.nextLine();
        System.out.println("Enter your password: ");
        password = scan.nextLine();
//        System.out.println(username);     // your username
//        System.out.println(password);     // your password
        return (username.equals("cyberkid") && password.equals("ckid1234"));
    }
}
