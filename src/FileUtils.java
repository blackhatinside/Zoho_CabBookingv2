import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    final static String filePath = "passwords.txt";

    public static HashMap<String, String> HashMapFromTextFile() {

        HashMap<String, String> map = new HashMap<String, String>();
        BufferedReader br = null;

        try {

            // create file object
            try {
                File file = new File(filePath);
                // create BufferedReader object from the File
                br = new BufferedReader(new FileReader(file));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String line = null;

            // read file line by line
            while ((line = br.readLine()) != null) {

                // split the line by :
                String[] parts = line.split(":");

                // first part is name, second is number
                String name = parts[0].trim();
                String number = parts[1].trim();

                // put name, number in HashMap if they are
                // not empty
                if (!name.equals("") && !number.equals(""))
                    map.put(name, number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Always close the BufferedReader
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
                ;
            }
        }

        return map;
    }
}
