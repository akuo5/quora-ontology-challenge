import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ontology {

	private static void readFile(String fileName) {

        BufferedReader reader = null;

		try {
		    File file = new File(fileName);
		    reader = new BufferedReader(new FileReader(fileName));

		    String line;
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }

		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}

    public static void main(String[] args) {
    	readFile("sample_file.txt");
    }

}
