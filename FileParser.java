import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

// the list of word frequencies, one of the pieces of information every guess gives, is on Notwordle's source code
// this program reformats that list to be more easily readable and parsable
public class FileParser{
    public static void main (String[] args){
        File file = new File("word frequencies.txt");
        // the string where the newly formatted text will be stored
        String s2 = "";

        // open the file and parse it
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)){
            // read file contents into a string
			String s = new String(bis.readAllBytes());
            // each word and its frequency is separated by a ", ", so split them up for easier parsing
			String[] arr = s.split(", ");
			for (int i = 0; i < arr.length; i++){
                // each word is surrounded by ", so remove the one at the beginning
                // the first word has some extra text that will be removed by this as well
                arr[i] = arr[i].substring(arr[i].indexOf("\"")+1);
                // removes the second ", leaving just the word and its frequency with a space in between
                arr[i] = arr[i].substring(0, arr[i].indexOf("\"")) + " " + arr[i].substring(arr[i].indexOf(" ")+1);

                // the last word has an end bracket, so remove that as well
                if (i == arr.length-1)
                    arr[i] = arr[i].substring(0, arr[i].indexOf("}"));

                // add the word to the end string with an end line for easier reading and parsing
                s2 += arr[i] + "\n";
            }
		} catch (FileNotFoundException ex){
			System.err.println("file not found");
		} catch (IOException ex){
			System.err.println("IO exception");
		}

        // write the final string to the old file
        try(FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            //convert string to byte array
            byte[] bytes = s2.getBytes();
            //write byte array to file
            bos.write(bytes);
            bos.close();
            fos.close();
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
			System.err.println("IOException occurred in writeToFile()");
            e.printStackTrace();
        }
    }
}