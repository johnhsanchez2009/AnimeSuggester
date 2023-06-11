import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// Counts the total number of anime in the database
public class AnimeCounter
{
   public static final String animedatabase = "AnimeText.txt";
   
   public static void main (String[] args) throws FileNotFoundException
   {
      Scanner file = new Scanner (new File(animedatabase), "UTF-8");
      int count = 0;
      
      while (file.hasNext())
      {
         System.out.println(count + ": " + file.nextLine());
         count++;         
      }
      
      System.out.println("\n----------------------\nTotal number of anime: " + count);
   }
}