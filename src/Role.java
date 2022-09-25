public class Role implements Comparable<Role> {

    private String title;
    private String description;
    private int minRank;
    private Player actor;
    private int[] coord;

    Role(String title, String description, int minRank, int[] coord)
    {
        this.title = title;
        this.description = description;
        this.minRank = minRank;
        this.actor = null;
        this.coord=coord;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String t)
    {
        this.title = t;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String d)
    {
        this.description = d;
    }

    public int getMinRank()
    {
        return this.minRank;
    }

    public void setMinRank(int mr)
    {
        this.minRank = mr;
    }

    public Player getActor()
    {
        return actor;
    }

    public void setActor(Player act)
    {
        this.actor = act;
    }

    public String toString()
    {
        String output="";
        output+="Title: "+title+"\n";
        output+="Line: "+description+"\n";
        output+="Minimum Rank Needed: "+minRank+"\n";
        return output;
    }

    public int[] getCoord() {
        return coord;
    }

    public int compareTo (Role other)
    {
        return this.minRank-other.minRank;
    }
}
