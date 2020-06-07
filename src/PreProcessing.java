import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PreProcessing {

    // from the last project
	public static String[] process(File file) {

		Scanner s;
        try {
            s = new Scanner(file);
            ArrayList<String> list = new ArrayList<>();

            while (s.hasNext()) {
                String word = s.next();
                word = word.toLowerCase();
                if (!word.matches("([0-9]+)\\.([0-9])+")){
                    word = word.replaceAll("[.,;:?'(){}\\[\\]\"]", "");
                }

                list.add(word);
            }
            String[] ret = new String[list.size()];
            ret = list.toArray(ret);
            return ret;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
            e.printStackTrace();
            System.exit(-10);
        }
        // will not reach
        return null;
    }

}
