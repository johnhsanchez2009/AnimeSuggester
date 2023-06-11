import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class AnimeSuggestion {

    // Text document name
    public static final String animedatabase = "AnimeText.txt";
    // Max allowable size of user list
    public static final int MAX_NUM_SEEN = 50;
    // The number of anime in the database
    public static final int DATABASE_SIZE = 14033;
    // The number of anime in the input list
    public static final int INITIAL_USER_INPUT_LIST_SIZE = 1;

    public static void main(String[] args) throws FileNotFoundException {
        int count = 0;
        int countI = 0;
        String[] info;
        Scanner file = new Scanner (new File(animedatabase), "UTF-8");
        String line;
        String[] category;
        Anime[] database = new Anime[DATABASE_SIZE];
        Anime[] input = new Anime[INITIAL_USER_INPUT_LIST_SIZE];

        String yearTemp;
        int year;
        int minYear=2017;

        double rating;

        String[] genreAll;
        String[] studioAll;        

        // Build input list
        String[] inputNames = new String[INITIAL_USER_INPUT_LIST_SIZE];

        System.out.println("Building the database...\n");

        int firstCharCount = 0;
        char first = ' ';
        // Build database and gather data
        while (file.hasNext()){
            line = file.nextLine();
            info = line.split("~");

            // Removes extra blank space from first entry
            if (firstCharCount == 0)
                first = info[0].charAt(0);

            if (info[0].charAt(0) == first)
                info[0] = info[0].substring(1);
            firstCharCount++;

            // Prints anime name for debugging
            // System.out.println("-"+info[0]+"-");

            // Add the anime categories
            category = info[1].split(",");
            for (int i=0; i<category.length; i++){
                category[i] = category[i].replaceAll("\\s","");
            }

            for (int i=0; i<DATABASE_SIZE; i++) {
                info[2] = info[2].replaceAll("\\s", "");
            }

            // Prints anime categories for debugging
            //for(int i=0; i<category.length; i++){
            //System.out.println(category[i]);
            //}

            // Find minYear
            yearTemp = info[3];
            yearTemp = yearTemp.replaceAll("\\s","");
            try
            {
                year = Integer.parseInt(yearTemp);
            }
            catch (NumberFormatException e)
            {
                year = 0;
            }
            if (year < 1900)
                year = 2017;

            if(year<minYear && year!=0)
                minYear=year;

            // Add the anime rating
            rating = Double.parseDouble(info[4]);

            Anime temp = new Anime(info[0], category, info[2], year, rating
                , info[5]);
            database[count] = temp;
            count++;

            for (int i=0; i<INITIAL_USER_INPUT_LIST_SIZE; i++)
            {
                if (info[0].equals(inputNames[i]))
                {
                    Anime temp2 = new Anime (info[0], category, info[2], year, rating, info[5]);
                    input[i] = temp;
                }
            }
        }

        // Uncomment for minYear
        // System.out.println (minYear);

        genreAll = sumG (database);
        studioAll = sumS (database);
        // Prints anime year for debugging
        /*for (int i=0; i<DATABASE_SIZE; i++)
        if (database[i].getYear() > 2018 || database[i].getYear() < 1900)
        System.out.println(i+": "+database[i].getName()+" - "+database[i].getYear());*/

        shell(database, DATABASE_SIZE);
        // Prints anime studio for debugging
        /*for(int i=0; i<studioAll.length; i++){
            if (studioAll[i]!=null)
                System.out.println (studioAll[i]);
        }*/
    }

    // Creates list of all genres in database
    public static String[] sumG (Anime[] database)
    {
        Anime gAll = database[0];
        String[] temp = database[0].getGenre();
        gAll.setGenre(temp);

        for(int i=1; i<database.length; i++)
        {
            temp = gAll.union(database[i]);
            gAll.setGenre(temp);
        }

        gAll.removeDup(gAll.getGenre());

        return gAll.getGenre();
    }

    // Creates list of all studios in database
    public static String[] sumS (Anime[] database)
    {
        String[] sAll = new String[DATABASE_SIZE];

        for (int i=0; i<sAll.length; i++)
        {
            sAll[i] = database[i].getStudio();
        }

        database[0].removeDup(sAll);

        return sAll;
    }

    public static Anime[] randomizer(Anime[] a)
    {
        int num = 0;
        Anime[] temp = new Anime[DATABASE_SIZE];
        Random rand = new Random();
        int random = 0;
        int[] generatedNums = new int[DATABASE_SIZE];
        boolean repeat = false;

        // Initialize array to all -1
        for (int i=0; i<DATABASE_SIZE; i++)
            generatedNums[i] = -1;

        while (num < DATABASE_SIZE)
        {
            repeat = false;
            // Generate random int
            random = rand.nextInt(DATABASE_SIZE);
            // Check for duplicates
            for (int i=0; i<DATABASE_SIZE; i++)
                if (generatedNums[i] == random)
                    repeat = true;

            // If no repeat, update arrays
            if (repeat == false)
            {
                generatedNums[num] = random;
                temp[num] = a[random];
                num++;
            }
        }
        return temp;
    }

    // K-nearest neighbor algorithm - returns top x (x is variable top)
    // Note that we look at maxDist because of how distance is calculated in Anime.java
    public static Anime[] nearestNeighbor (Anime[] database, Anime input, int seen)
    {
        Anime[] suggestion = new Anime[DATABASE_SIZE];
        Anime[] temp = new Anime[seen];
        double[] maxDist= new double[DATABASE_SIZE];

        // Copy database to suggestion
        for (int i=0; i<DATABASE_SIZE; i++){
            Anime a = new Anime (database[i].getName(), database[i].getGenre(), database[i].getStudio(), 
                    database[i].getYear(), database[i].getRating(), database[i].getType());
            suggestion[i] = a;
        }

        // Separate input anime from anime database
        for (int i=0; i<seen; i++){

            for (int j=0; j<DATABASE_SIZE; j++){
                try {
                    if (suggestion[j].getName().equals(input.getName())){
                        Anime a = suggestion[seen-i];
                        temp[i] = suggestion[j];
                        suggestion[seen-i] = null;
                        suggestion[j] = a;
                    }
                } catch (NullPointerException e) {}
            }
        }

        // Calculate all distances
        for (int i=0; i<seen; i++){
            for (int j=0; j<DATABASE_SIZE; j++){
                try 
                {
                    if (suggestion[j]!=null){
                        maxDist[j] = temp[i].distance(suggestion[j]);
                    } 
                }catch (NullPointerException e)  {}      

            }
        }

        for (int i=0; i<seen; i++){
            for(int j=DATABASE_SIZE-1; j>=0; j--){
                for (int k=1; k<j; k++){
                    if(suggestion[k]!=null)
                    {
                        if (maxDist[k-1] < maxDist[k]){
                            double m = maxDist[k-1];
                            Anime b = suggestion[k-1];
                            maxDist[k-1] = maxDist[k];
                            suggestion[k-1] = suggestion[k];
                            maxDist[k] = m;
                            suggestion[k] = b;
                        }
                    }
                }    
            }
        }

        return suggestion;
    }    

    public static Anime[] means (Anime[] input, int k, String anime)
    {
        Anime[] suggestions = null;
        Anime[] groups = new Anime[k];
        Random rand = new Random();

        // Initialize random starting points for groups
        for (int i = 0; i<k; i++)
        {
            groups[i].setYear(rand.nextInt(18)+1999);
            //groups[i].setStudio(a);
        }

        return suggestions;
    }

    public static void shell(Anime[] a, int b)
    {
        Scanner scan = new Scanner(System.in);
        // Store user responses
        String response = "";
        int len = b;
        boolean quit = false;
        // Fun little easter egg
        boolean tsundere = false;

        // Creates array of options based on user's search
        Anime[] animeOptions = new Anime[b];
        // Stores number of matches for user search
        int numOptions = 0;
        int count = 0;

        // Stores anime user has decided to use for searching
        Anime[] seen = new Anime[MAX_NUM_SEEN];
        // Keeps track of number of anime user has decided to use for searching
        int numSeen = 0;

        // USER INSTRUCTIONS
        System.out.println("Hello!\nWelcome to Nick & John's\nSuper Happy Mega Fun Heart Lovey-Dovey Anime Suggester!");
        options(tsundere);
        System.out.println("\nTo begin, type in one of the above commands.");

        while (true)
        {
            if (tsundere == false)
                System.out.println("\nWhat would you like to do?");
            else
                System.out.println("\nSo you're still here, huh?\nWell I guess if you're still here, I have no choice.\nWhat do you want to do next?");
            
            // Read input from user
            response = scan.nextLine();
            response = response.toLowerCase();

            switch (response)
            {
                case "quit": 
                    quit = true;
                    break;
                case "search": 
                    seen = search(a, seen, len, numSeen, tsundere);
                    break;
                case "remove": 
                    seen = remove(seen, numSeen, tsundere);
                    break;
                case "options":
                    options(tsundere);
                    break;
                case "view":
                    view(seen, numSeen, tsundere);
                    break;
                case "run":
                    run(a, seen, numSeen, tsundere);
                    break;
                case "clear":
                    clear(seen, numSeen, tsundere);
                    numSeen = 0;
                    break;
                case "tsundere":
                    tsundere = true;
                    break;
                case "normal":
                    tsundere = false;
                    break;
                default: 
                    if (tsundere == false)
                        System.out.println("Error: invalid input.");
                    else
                        System.out.println("What are you trying to pull you idiot! That's not a valid input!\nUgh! Why do you have to be such an idiot all the time!"); 
            }

            numSeen = 0;
            for (int i=0; i<MAX_NUM_SEEN; i++)
                if (seen[i] != null)
                    numSeen++;

            if (quit == true)
            {
                if (tsundere == false)
                    System.out.println("Goodbye!");
                else
                    System.out.println("You're leaving already?\nDon't get the wrong idea!\nIt's n-not like I w-wanted you to stay or anything...\nBut you better come back!\nI mean, b-because otherwise...\nWho's going to use my amazing program?");
                return;
            }

            if (numSeen != 0)
            {
                // Prints the user's anime selections thus far
                System.out.println("\nYour list of anime so far is:\n");
                for(int i=0; i<numSeen; i++)
                    System.out.println(seen[i].getName() + " - " + seen[i].getURating());               
            }
        }
    }

    public static Anime[] search (Anime[] a, Anime[] options, int b, int c, boolean t)
    {
        if (t == false)
        {
            // User instructions
            System.out.println("You have decided to search for anime.");
            System.out.println("\nWhatever you type in next, this program will search our database for it.");
            System.out.println("It could be a single word, a whole title, or even a few letters.");
            System.out.println("Please keep in mind though, that some special characters might not be compatible with the database.");
            System.out.println("You may enter a maximum of 50 anime in your list.");
        }
        else
        {
            System.out.println("Well you big dummy, it looks like you want to search for anime.");
            System.out.println("I know you're kind of stupid, so I guess I'll help you out just this once.");
            System.out.println("But don't get the wrong idea!");
            System.out.println("It's not because I l-like you or anything!");

            System.out.println("\nWhatever you type in next, my amazing program will search our huge database for it.");
            System.out.println("It could be a single word, a whole title, or even a few letters.");
            System.out.println("The size doesn't really matter.");
            System.out.println("W-wait! I didn't mean it like that!");
            System.out.println("Y-you p-pervert!");

            System.out.println("\nAnyway, try not to forget that some special characters might not be compatible with the database.");
            System.out.println("Also, you can only add up to 50 anime in your list.\nIf that's not enough then that's really sad.\nI mean jeez! How boring can you be?");
            System.out.println("D-did you get all that, because I am NOT going to repeat it!");
        }

        String response = "";
        int len = b;
        Scanner scan = new Scanner(System.in);
        Anime[] animeOptions = new Anime[len];
        int numOptions = 0;
        int count = 0;
        Anime[] seen = options;
        int numSeen = c;
        int rating = 0;
        boolean validRating = false;

        if (t == false)
            System.out.println("\nYou can enter \"quit\" if you changed your mind.\nOtherwise, please enter the name of an anime to search:");
        else
            System.out.println("\nI know how indecisive you can be, so just type \"quit\" if you want to stop searching.\nOtherwise, just hurry up and enter the name of an anime to search:");
        while(true)
        {
            // Read input from user
            response = scan.nextLine();
            response = response.toLowerCase();

            if (response.equals("quit"))
                return(seen);

            // Search database for name of anime
            for (int i=0; i<len; i++)
            {
                if (a[i].getName().toLowerCase().contains(response))
                {
                    animeOptions[numOptions] = a[i];
                    numOptions++;
                }
            }

            // Checks if search results list is empty
            if (animeOptions[0] == null)
                if (t == false)
                    System.out.println("Sorry, no results were found matching your search. Please try again.");
                else
                    System.out.println("What are you? Stupid? We don't have that anime! Try searching for something we actually HAVE!\nIdiot!");
            else
            {
                if (t == false)
                    // Print results of search    
                    System.out.println("\nYour options are:\n");
                else
                    System.out.println("\nHere's what my amazing code came up with.\nFeel free to praise me!\n");
                for (int i=0; i<numOptions; i++)
                {
                    System.out.println(count + ": " + animeOptions[i].getName());
                    count++;
                }

                // Allows users to add search results to the list of seen anime
                while(true)
                {
                    if (t == false)
                    {
                        System.out.println("\nPlease enter the number next to an anime to add it to your list.");
                        System.out.println("Enter \"done\" at any time to start another search");
                    }
                    else
                    {
                        System.out.println("\nWasn't that incredible?\nDon't worry, it's only natural to want to praise me.\nJust don't forget to make good use of my code by typing one of those numbers next an anime.\nThat'll add it to your list, in case you were too stupid to tell.");
                        System.out.println("Of course, if you ARE satisfied, you can just type \"done\" at any time to start another search.");
                    }
                    response = scan.nextLine();
                    // If user types "quit", exits shell
                    if(response.toLowerCase().equals("quit"))
                        return seen;
                    // If user types "done", breaks loop and allows them to search again
                    if(response.toLowerCase().equals("done"))
                        break;
                    try 
                    {
                        // Checks to make sure a valid number has been entered
                        if (Integer.parseInt(response) < count && numSeen < MAX_NUM_SEEN && Integer.parseInt(response) > -1)
                        {
                            // Checks if seen[] is empty
                            if (numSeen > 0)
                            {  
                                int oldNumSeen = numSeen;
                                boolean duplicate = false;
                                // Compares user selction with everything in the seen[] array to prevent duplicates
                                for (int i=0; i<oldNumSeen; i++)
                                    if (seen[i].getName().equals(animeOptions[Integer.parseInt(response)].getName()))
                                        duplicate = true;

                                // Checks for duplicates
                                if (duplicate == true)
                                    if (t == false)
                                        System.out.println("Error: You have already selected that anime.");
                                    else
                                        System.out.println("Are you an idiot?\nYou already added that anime to your list! BAKA!");
                                else
                                {
                                    // If number is valid, adds selected anime to array
                                    seen[numSeen] = animeOptions[Integer.parseInt(response)];
                                    numSeen++;

                                    // Allows users to enter a rating
                                    while (validRating == false)
                                    {
                                        if (t == false) 
                                            System.out.println("\nNow please rate this anime on a scale from 1 to 10, with 10 being the highest.");
                                        else
                                            System.out.println("\nWow! I'm actually impressed you made it this far!\nThere's just one more thing left: you have to rate the anime on a scale from 1 to 10.\nTen is obviously the highest, in case you were too stupid to figure that out.");

                                        response = scan.nextLine();

                                        try
                                        {
                                            // Checks if rating is valid
                                            if (Integer.parseInt(response) > 0 && Integer.parseInt(response) < 11)
                                            {
                                                rating = Integer.parseInt(response);
                                                validRating = true;
                                                seen[numSeen-1].setURating(rating);
                                            }
                                            else
                                            if (t == false)
                                                System.out.println("\nError: Please enter an integer between 1 and 10");
                                            else
                                                System.out.println("\nAre you an idiot?\nI said a number between 1 and 10!\nThis isn't that difficult!\nI can't believe you're this stupid!");
                                        } catch (NumberFormatException e)
                                        {
                                            if (t == false)
                                                System.out.println("Error: Invalid entry. Please enter a number between 1 and 10.");
                                            else
                                                System.out.println("Incredible! You really are a complete moron!\nI said a NUMBER between 1 and 10!\nBut you couldn't even get the number part right!\nIDIOT!");
                                        }
                                    }
                                }   

                            }
                            else
                            {
                                // If number is valid, adds it to array
                                seen[numSeen] = animeOptions[Integer.parseInt(response)];
                                numSeen++;

                                // Allows users to enter a rating
                                while (validRating == false)
                                {
                                    if (t == false)
                                        System.out.println("\nNow please rate this anime on a scale from 1 to 10, with 10 being the highest.");
                                    else
                                        System.out.println("\nWow! I'm actually impressed you made it this far!\nThere's just one more thing left: you have to rate the anime on a scale from 1 to 10.\nTen is obviously the highest, in case you were too stupid to figure that out.");

                                    response = scan.nextLine();

                                    try
                                    {
                                        if (Integer.parseInt(response) > 0 && Integer.parseInt(response) < 11) // checks if rating is valid
                                        {
                                            rating = Integer.parseInt(response);
                                            validRating = true;
                                            seen[numSeen-1].setURating(rating);
                                        }
                                        else
                                        if (t == false)
                                            System.out.println("\nError: Please enter an integer between 1 and 10");
                                        else
                                            System.out.println("\nAre you an idiot?\nI said a number between 1 and 10!\nThis isn't that difficult!\nI can't believe you're this stupid!");
                                    } catch (NumberFormatException e)
                                    {
                                        if (t == false)
                                            System.out.println("Error: Invalid entry. Please enter a number between 1 and 10.");
                                        else
                                            System.out.println("Incredible! You really are a complete moron!\nI said a NUMBER between 1 and 10!\nBut you couldn't even get the number part right!\nIDIOT!");
                                    }
                                }
                            }
                        }
                        else if (numSeen < MAX_NUM_SEEN)
                            if (t == false)
                                System.out.println("Error: Invalid entry. Please enter a number next to an anime that has been printed.");
                            else
                                System.out.println("ANTA BAKA? Enter one of the numbers that appears on the screen!\nJeez, you really are an idiot!");
                        else
                        if (t == false)
                            System.out.println("Error: Your list is full. Please delete an anime before adding more.");
                        else
                            System.out.println("Your list is already full!\nJeez, how stupid can you be!");
                    } catch (NumberFormatException e)
                    {
                        if (t ==false)
                            System.out.println("Error: Invalid entry. Please enter a number next to an anime that has been printed."); 
                        else
                            System.out.println("Jeez! Just how much of an IDIOT are you?\nThat's not even a number!");
                    }
                    validRating = false;
                }

                if (t == false)
                    // Prints the user's anime selections thus far
                    System.out.println("\nYour list of anime so far is:\n");
                else
                    System.out.println("\nHere's your list so far, and it's all thanks to my AMAZING programming skills:");
                for(int i=0; i<numSeen; i++)
                    System.out.println(seen[i].getName() + " - " + seen[i].getURating());
                if (t == true)
                    System.out.println("Feel free to shower me with praise!");

                if (t == false)
                {
                    System.out.println("\nPlease enter another anime name to search again.");
                    System.out.println("Or enter \"quit\" to stop searching.");
                }
                else
                {
                    System.out.println("\nIsn't my program simply AMAZING!");
                    System.out.println("I hope you realize now just how superior I am compared to an idiot like you!");
                    System.out.println("\nI realize you probably can't get enough of my genius, so feel free to search for more anime.");
                    System.out.println("Of course, if your tiny brain is on overload, you can always type \"quit\". Of course, that would just make you a coward.");
                }   
                // Resets items necessary for another search
                for(int i=0; i<numOptions; i++)
                    animeOptions[i] = null;
                numOptions = 0;
                count = 0;
                validRating = false;

                //System.out.println("Rating: " + rating);
            }

  
        }
    }

    // Removes anime from the user's list
    public static Anime[] remove (Anime [] a, int b, boolean t)
    {
        if (t == false)
        {
            System.out.println("You have decided to remove anime from your list."); // user instructions
            System.out.println("\nPlease follow the instructions below to remove items properly.");
        }
        else
        {
            System.out.println("See! I knew you'd mess something up you IDIOT!");
            System.out.println("\nGuess I'll have to tell you how to remove anime properly.");
            System.out.println("You better listen up though!\nI can't have you messing up my masterful code!");
        }

        Anime[] seen = a;
        int numSeen = b;
        Scanner scan = new Scanner(System.in);
        String response = "";

        if (numSeen > 0)
        {
            while(true)
            {
                if (t == false)
                    // Print current list
                    System.out.println("\nHere is your list so far:\n");
                else
                    System.out.println("\nHere's the list you managed to mess up:\n");
                for(int i=0; i<numSeen; i++)
                    System.out.println(i + ": " + seen[i].getName());

                if (t == false)
                {
                    System.out.println("\nPlease enter the number of the anime you would like to remove.");
                    System.out.println("Or enter \"done\" to stop removing anime");
                }
                else
                {
                    System.out.println("\nNow I'll keep this really simple so your tiny brain can handle it.");
                    System.out.println("All you have to do is enter the number next to the anime you want to remove.");
                    System.out.println("Or, if you managed to fix this mess that YOU caused, enter \"done\".");
                    System.out.println("Got it?");
                    System.out.println("Now try not to mess this up!");
                }

                // Read input from user
                response = scan.nextLine();
                response = response.toLowerCase();
                if (response.equals("done"))
                    break;

                try
                {
                    if (Integer.parseInt(response) > -1 && Integer.parseInt(response) < numSeen)
                    {
                        int deleted = Integer.parseInt(response);
                        // Delete selected anime
                        seen[deleted] = null;

                        // If deleted anime is last in array
                        if (deleted == (numSeen - 1))
                            numSeen--;
                        // If deleted anime is first in array
                        else if (deleted == 0)
                        {
                            for (int i=0; i<numSeen; i++)
                                seen[i] = seen[i+1];
                            numSeen--;
                        }
                        // If deleted anime is in middle of list
                        else
                        {
                            for (int i=deleted; i<numSeen; i++)
                                seen[i] = seen[i+1];
                            numSeen--;
                        }
                    }
                    else
                    {
                        if (t == false)
                            System.out.println("Error: Please enter a number next to one of the anime.");
                        else
                            System.out.println("Jeez! The numbers are on the freaking screen!\nCan't you read?");
                    }
                } catch (NumberFormatException e)
                {
                    if (t == false)
                        System.out.println("Error: Invalid entry. Please enter a number.");
                    else
                        System.out.println("Are you kidding!\nI said to enter a NUMBER!\nA NUMBER!\nJUST HOW STUPID CAN YOU BE!");
                }
            }
        }
        // If user tries to reomove from an empty array
        else
        if (t == false)
            System.out.println("Error: your list is already empty.");
        else
            System.out.println("What are you? Stupid?\nYour list is EMPTY!\nYou can't remove something from an empty list!\nBAKA!");
        return seen;
    }

    public static void options(boolean t)
    {
        if (t == false)
        {
            System.out.println("Here are your options:\n");
            System.out.println("\"quit\": Exits the program");
            System.out.println("\"search\": Lets you search for anime to add to your list");
            System.out.println("\"remove\": Lets you remove anime from your list");
            System.out.println("\"options\": Displays the controls");
            System.out.println("\"view\": Displays the current list of anime");
            System.out.println("\"run\": Runs the program to give suggestions based on the current anime in the list");
            System.out.println("\"clear\": Clears the user's anime list");
            System.out.println("\nHope this helped!");
        }
        else
        {
            System.out.println("Jeez!\nI knew you'd need my help!");
            System.out.println("You better listen up, because I'm only going to say this once!\nHere are the controls:\n");
            System.out.println("\"quit\": Exits the program");
            System.out.println("\"search\": Lets you search for anime to add to your list");
            System.out.println("\"remove\": Lets you remove anime from your list");
            System.out.println("\"options\": Displays the controls");
            System.out.println("\"view\": Displays the current list of anime");
            System.out.println("\"run\": Runs the program to give suggestions based on the current anime in the list");
            System.out.println("\"clear\": Clears the user's anime list");
            System.out.println("\nThey're pretty self-explanatory, but maybe you'd better write them down or something.");
            System.out.println("Anyway, don't ask me again.\nI mean, it's not like I HATED helping you or anything...\nB-but don't get the wrong idea!\nI mean...\nIt's not like I l-liked helping you either.\nDummy.");
        }
        return;
    }

    public static void view(Anime[] a, int b, boolean t)
    {
        if (b == 0)
            if (t == false)
                System.out.println("Your list is currently empty.");
            else
                System.out.println("Are you a complete idiot?\nYour list is EMPTY!\nIDIOT!");
        else
            for (int i=0; i<b; i++)
                System.out.println(a[i].getName() + " - " + a[i].getURating());
        return;
    }

    public static Anime[] run(Anime[] a, Anime[] b, int c, boolean t) // takes user list of seen anime and suggests new anime
    {
        // Checks if user list is empty
        if (c == 0)
        {
            if (t == false)
                System.out.println("Error: Your list is empty. Please add items to your list before attempting to run the suggestion algorithm.");
            else
                System.out.println("What are you, stupid or something? I can't suggest any new anime if don't tell me what you like!\nGo back and add some anime to your list first!\nD-dummy!");
            return null;
        }
        // If list is not empty, runs code
        else
        {
            if (t == false)
                System.out.println("We will now search our database for other anime you might like. Please wait a moment.");
            else
                System.out.println("Get ready to see how amazing my program is!\nIn no time at all, I'll have a bunch of new anime for you to try!");

            // Number of outputs
            int num = 3*c;
            //System.out.println(num);
            Anime[] output = new Anime[num];
            String[] out1 = new String[num];
            double[] out2 = new double[num];

            for (int i=0; i<c; i++)
                try
                {
                    for (int j=0; j<3; j++){
                        output = nearestNeighbor(a, b[i], c);
                        out1[3*i+j] = output[j].getName();
                        out2[3*i+j] = b[i].distance((output[j]));
                        //System.out.println(out1[3*i+j] + " - " + out2[3*i+j]);
                    }
                } catch (NullPointerException e) {}

            // Remove duplicates
            for (int i=0; i<num; i++)
            {
                for (int j=i+1; j<num; j++)
                {
                    if (out1[i]!=null && out1[j]!=null)
                        if (out1[i].equals(out1[j])){
                            if (out2[i]<out2[j]){
                                out1[i] = null;
                                out2[i] = 0;
                            }
                            else{
                                out1[j] = null;
                                out2[j] = 0;
                            }
                        }
                }
            }

            // Remove suggestions that are already on list
            int count = 0;
            for (int i=0; i<c; i++){
                for (int j=0; j<num; j++){
                    if (b[i].getName().equals(out1[j])){
                        out1[j]=null;
                        out2[j]=0;
                        count++;
                    }
                }
            }

            // Sort
            for(int j=num-1; j>=0; j--){
                for (int k=1; k<j+1; k++){
                    if (out2[k-1] < out2[k]){
                        double m = out2[k-1];
                        String z = out1[k-1];
                        out2[k-1] = out2[k];
                        out1[k-1] = out1[k];
                        out2[k] = m;
                        out1[k] = z;
                    }
                }
            }

            // Print results
            if (t == false)
                System.out.println("\nHere are the suggestions the algorithm came up with:\n");
            else
                System.out.println("\nAlright, here's what my genius algorithm came up with!\nI guarantee that they'll all become your new favorite shows!\n");

            System.out.println("\n-------------\n");
            for (int i=0; i<num; i++){
                if (out1[i]!=null && i<30)
                    System.out.println((i+1) + ".  " + out1[i] + " - " + out2[i]);
            }
            System.out.println("Number suggested that is already on list: " + count);

        }
        return null;
    }

    public static void clear (Anime[] a, int b, boolean t) // clears the entire list of anime
    {
        // Prints error message if list is empty
        if (b == 0)
            if (t == false)
                System.out.println("Error: List is already empty.");
            else
                System.out.println("Anta Baka? The list is already empty you moron!");
        else
        {
            if (t == false)
                System.out.println("\nYour anime list will now be cleared.");
            else
                System.out.println("\nJeez! I just knew you'd mess this up!\nGuess I have no choice. I'll delete your anime list for you.");

            // Deletes all user entries
            for (int i=0; i<b; i++)
                a[i] = null;

            if (t == false)
                System.out.println("\nYour anime list has been successfully cleared.");
            else
                System.out.println("\nThere! Your list is all clear again!\nJust try not to mess it up again.");
        }
    }

}