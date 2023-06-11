public class Anime
{
    private String name;
    private String genre[];
    private String studio;
    private int year;
    private double rating;
    private String type;
    // Change based on minimum year
    private double minYear = 1917;
    private int urating = 10;

    // Anime object constructor
    public Anime(String n, String g [], String s, int y, double r, String t)
    {
        name = n;
        genre = g;
        studio = s;
        year = y;
        rating = r;
        type = t;
    }

    // Returns  of anime
    String getName()
    {
        return name;
    }

    // Returns genre(s) of anime
    String[] getGenre()
    {
        return genre;
    }

    // Returns studio of anime
    String getStudio()
    {
        return studio;
    }

    // Returns release year of anime
    int getYear ()
    {
        return year;
    }

    // Returns MAL rating of anime
    double getRating()
    {
        return rating;
    }

    // Returns type of anime
    String getType()
    {
        return type;
    }

    // Returns user rating of anime
    int getURating()
    {
        return urating;
    }

    // Sets name of anime
    void setName(String a)
    {
        name = a;
    }

    // Sets genre(s) of anime
    void setGenre(String[] a)
    {
        genre = a;
    }

    // Sets studio of anime
    void setStudio(String a)
    {
        studio = a;
    }

    // Sets release year of anime
    void setYear (int a)
    {
        year = a;
    }

    // Sets MAL rating of anime
    void setRating (double a)
    {
        rating = a;
    }

    // Sets type of anime
    void setType (String a)
    {
        type = a;
    }

    // Sets user rating of anime
    void setURating (int a)
    {
        urating = a;
    }

    // Removes duplicates from list of strings
    String[] removeDup (String[] s)
    {
        String[] dirty = s;
        String[] clean = new String[dirty.length];

        for (int i=0; i<dirty.length; i++)
        {
            for (int j=i+1; j<dirty.length; j++)
            {
                if (dirty[i]!=null && dirty[j]!=null)
                    if (dirty[i].equals(dirty[j]))
                        dirty[j] = null;
            }

            if (dirty[i]!=null)
                clean[i] = dirty[i];
        }

        return clean;
    }

    String[] union (Anime b)
    {
        boolean seen = false;
        boolean cont = true;
        int ucount = 0;
        int count =0;
        int length;
        length = this.getGenre().length + b.getGenre().length;
        String[] union = new String[length];

        for(int k=0; k<this.getGenre().length; k++)
        {
            union[k] = this.getGenre()[k];
            ucount++;
        }

        for(int i=0; i<b.getGenre().length; i++)
        {
            for(int j=0; union[j]!=null && cont==true; j++)
            {
                if (union[j].equals(b.getGenre()[i]))
                {
                    seen=true;
                }      
            }
            if(!seen)
            {
                union[ucount] = b.getGenre()[i];
                ucount++;
                cont=false;
            }
            cont = true;
            seen=false;
        }

        return union;
    }

    String[] intersection (Anime b)
    {
        int length = this.getGenre().length + b.getGenre().length;
        String[] intersection = new String[length];
        int icount = 0;
        String[] temp = new String[length];

        for(int k=0; k<this.getGenre().length; k++)
        {
            temp[k] = this.getGenre()[k];
        }

        for(int i=0; i<b.getGenre().length; i++)
        {
            for(int j=0; temp[j]!=null; j++)
            {
                if(temp[j].equals(b.getGenre()[i]))
                {
                    intersection[icount] = temp[j];
                    icount++;
                }
            }
        }

        return intersection;
    }

    double compareGen (Anime a, Anime b)
    {
        int ucount = 0;
        int icount = 0;
        String[] union = null;
        union = a.union(b);
        String[] intersection;
        intersection = a.intersection(b);

        for (int i=0; i<union.length; i++){
            if(union[i] != null)
                ucount++;
        }

        for (int j=0; j<intersection.length; j++){
            if(intersection[j] != null)
                icount++;
        }

        return ((double)icount/(double)ucount);
    }

    // Compares 2 strings
    double compare (String a, String b)
    {
        if (a.equals(b))
            return 1;
        else
            return 0;
    }

    double yearFactor(Anime a)
    {
        double output;
        output = 1-(Math.abs(this.year-a.getYear())/(2017-minYear));
        return output;
    }

    // Note: genre is weighed twice as much
    double distance (Anime a)
    {
        double dist;
        double total;
        total = Math.pow(2*compareGen(this, a),2) + Math.pow(compare(this.studio,a.getStudio()),2) 
        + Math.pow(this.yearFactor(a),2) + Math.pow(.5*(1-(this.rating-a.getRating()/10)),2) 
        + Math.pow(.5*compare(this.type, a.getType()), 2);
        dist = (urating/10.0)*Math.sqrt (total);
        return dist;
    }
}