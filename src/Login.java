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
            }
            else {
                System.out.println(3 - login_attempts + "attempts left.");
            }
//             System.out.println(isSession);      // return value of the login block
        }
        if (!isAdmin) {
            System.out.println("Max login attempts exceeded.");
            System.exit(1);
        }
    }
}
