import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// Lists all the different types of anime and the number of each
public class AnimeTypeLister
{
    public static final String animedatabase = "AnimeText.txt";

    public static void main (String[] args) throws FileNotFoundException
    {
        Scanner file = new Scanner (new File(animedatabase), "UTF-8");
        int count = 0;
        String[] types = new String[10];
        String line;
        String info[];
        boolean newType = true;
        int numTypes = 0;
        String name;
        int[] numType = new int[9];

        while (file.hasNext())
        {
            line = file.nextLine();
            info = line.split("~");
            String type = info[5];
            name = info[0];
            System.out.println(count + ": " + name + " - " + type);

            for (int i=0; i<10; i++)
                if (type.equals(types[i]))
                    newType = false;

            if (newType == true)
            {
                types[numTypes] = type;
                numTypes++;
            }

            switch(type)
            {
                case "TV":
                    numType[0]++;
                    break;
                case "Movie":
                    numType[1]++;
                    break;
                case "OVA":
                    numType[2]++;
                    break;
                case "Special":
                    numType[3]++;
                    break;
                case "ONA":
                    numType[4]++;
                    break;
                case "Music":
                    numType[5]++;
                    break;
                case "Unknown":
                    numType[6]++;
                    break;
                case "Japanese":
                    numType[7]++;
                    break;
                case ":":
                    numType[8]++;
                    break;                        
            }

            newType = true;
            count++;         
        }

        System.out.println("\n----------------------\nTotal number of anime: " + count);
        System.out.println("\nUnique types:");
        for (int i=0; i<numTypes; i++)
            System.out.println(i+") "+types[i] + " - " + numType[i]);
    }
}