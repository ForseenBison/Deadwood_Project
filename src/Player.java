import java.util.*;

public class Player implements Comparable<Player>{

    private String name;
    private int money;
    private int credit;
    private int rank;
    private String currentLocation;
    private int rToken;
    private int employment; //0 is no role, 1 is off card, 2 is on card
    private String img;



    Player()
    {}

    Player(String newName, int c, int r, String i)
    {
        name=newName;
        money=0;
        credit=c;
        rank=r;
        currentLocation="trailer";
        rToken=0;
        employment=0;
        img= i;
    }

    public int getCredit() {
        return credit;
    }

    public int getMoney() {
        return money;
    }

    public int getRank() {
        return rank;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getName() {
        return name;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRToken() {
        return rToken;
    }

    public void setRToken(int RToken) {
        this.rToken = RToken;
    }

    public int getEmployment() {
        return employment;
    }

    public void setEmployment(int employment) {
        this.employment = employment;
    }

    public void adjmoney(int m)
    {
        money += m;
    }

    public void adjcredit(int c)
    {
        credit+=c;
    }

    public void adjRToken()
    {
        rToken++;
    }

    public void rankUp() {
        Scanner reader = new Scanner(System.in);

        System.out.println("**************************");
        System.out.println("::Rank::Dollars::Credits::");
        System.out.println(":: 2  ::   4   ::   5   ::");
        System.out.println(":: 3  ::  10   ::   10  ::");
        System.out.println(":: 4  ::  18   ::   15  ::");
        System.out.println(":: 5  ::  28   ::   20  ::");
        System.out.println(":: 6  ::  40   ::   25  ::");
        System.out.println("**************************");

        int newRank = -1;
        int paymentChoice = -1;

        if (rank == 6) {
            System.out.println("You are already max Rank!");
        } else {
            while (newRank <= rank || newRank > 6) {
                System.out.println("Please enter new desired rank: ");
                try {
                    newRank = reader.nextInt();
                }
                catch (InputMismatchException a)
                {
                    newRank=-1;
                    reader.nextLine();
                    System.out.println("Please Enter a Number.");
                }
            }

            while (paymentChoice < 1 || paymentChoice > 2) {
                System.out.println("Enter 1 to pay with credit, 2 to pay with Dollars: ");
                try {
                    paymentChoice = reader.nextInt();
                } catch (InputMismatchException a) {
                    System.out.println("Please Enter a Number.");
                    paymentChoice = -1;
                    reader.nextLine();
                }
            }

            if (paymentChoice == 1) {
                if (newRank == 2) {
                    if (credit < 5) {
                        System.out.println("Not enough Credits");
                    } else {
                        rank = 2;
                        credit -= 5;
                        System.out.println("You're new Rank is 2");
                    }
                } else if (newRank == 3) {
                    if (credit < 10) {
                        System.out.println("Not enough Credits");
                    } else {
                        rank = 3;
                        credit -= 10;
                        System.out.println("You're new Rank is 3");
                    }
                } else if (newRank == 4) {
                    if (credit < 15) {
                        System.out.println("Not enough Credits");
                    } else {
                        rank = 4;
                        credit -= 15;
                        System.out.println("You're new Rank is 4");
                    }
                } else if (newRank == 5) {
                    if (credit < 20) {
                        System.out.println("Not enough Credits");
                    } else {
                        rank = 5;
                        credit -= 20;
                        System.out.println("You're new Rank is 5");
                    }
                } else if (newRank == 6) {
                    if (credit < 25) {
                        System.out.println("Not enough Credits");
                    } else {
                        rank = 6;
                        credit -= 25;
                        System.out.println("You're new Rank is 6");
                    }
                }
            } else {
                if (newRank == 2) {
                    if (money < 4) {
                        System.out.println("Not enough money");
                    } else {
                        rank = 2;
                        money -= 4;
                        System.out.println("You're new Rank is 2");
                    }
                } else if (newRank == 3) {
                    if (money < 10) {
                        System.out.println("Not enough money");
                    } else {
                        rank = 3;
                        money -= 10;
                        System.out.println("You're new Rank is 3");
                    }
                } else if (newRank == 4) {
                    if (money < 18) {
                        System.out.println("Not enough money");
                    } else {
                        rank = 4;
                        money -= 18;
                        System.out.println("You're new Rank is 4");
                    }
                } else if (newRank == 5) {
                    if (money < 28) {
                        System.out.println("Not enough money");
                    } else {
                        rank = 5;
                        money -= 28;
                        System.out.println("You're new Rank is 5");
                    }
                } else if (newRank == 6) {
                    if (money < 40) {
                        System.out.println("Not enough money");
                    } else {
                        rank = 6;
                        money -= 40;
                        System.out.println("You're new Rank is 6");
                    }
                }

            }
        }
    }

    public String toString()
    {
        String output="<html>";

        output+=name+"<br/>";
        output+="$"+money+", " + credit + " credits<br/>";
        //output+=credit+" credits<br/>";
        if(employment > 0)
        {
            output += rToken + " rehearsal tokens<br/>";
        }
        output+="Rank "+ rank + "<br/>";
        output+="Location: "+currentLocation+"</html>";

        return output;
    }

    public int getFinalScore()
    {
        return money+credit+(5*rank);
    }

    public int compareTo(Player other)
    {
        return getFinalScore()-other.getFinalScore();
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String endToString()
    {
        String output="";

        output+=name+"\n";
        output+="$"+money+", " + credit + " credits\n";
        //output+=credit+" credits<br/>";
        output+="Rank "+ rank + "\n";
        output+="Final Score: " + getFinalScore()+"\n";
        return output;
    }
}
