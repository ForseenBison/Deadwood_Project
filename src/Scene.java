import javax.swing.*;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;

public class Scene
{
    private String title;
    private String description;
    private int budget;
    private Role[] onCardRoles;
    private String img;

    Scene(String title, String description, int budget, Role[] onCardRoles, String img)
    {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.onCardRoles = onCardRoles;
        this.img=img;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }


    public int getBudget()
    {
        return budget;
    }

    public Role[] getRoles()
    {
        return onCardRoles;
    }

    public Player[] getActors()
    {
        //System.out.println("yeet");
        Player[] test= new Player[10];
        return test;
    }

    public void payout(BoardGUI view)
    {
        int ix=0;
        int [] pay = new int[budget];
        int randomNum;
        boolean isPlayer=false;
        while(ix<budget)
        {
            randomNum = ThreadLocalRandom.current().nextInt(1, 6 + 1);
            pay[ix]=randomNum;
            ix++;
        }
        Arrays.sort(pay);
        Arrays.sort(onCardRoles, Collections.reverseOrder());

        ix=budget- 1;
        int roleix = onCardRoles.length;



        for(Role x : onCardRoles)
        {
            if(x.getActor()!=null)
            {
                isPlayer=true;
            }
        }

        String message="";

        if(isPlayer)
        {
            while (ix > -1)
            {
                //System.out.println(roleix + ":::::" + onCardRoles.length);
                if (onCardRoles[roleix % onCardRoles.length].getActor() == null)
                {
                    roleix++;

                }
                else
                {
                    onCardRoles[roleix % onCardRoles.length].getActor().adjmoney(pay[ix]);
                    message+=onCardRoles[roleix % onCardRoles.length].getActor().getName() + " receives $" + pay[ix]+"\n";
                    ix--;
                    roleix++;
                }
            }
        }
        else
        {
            message+="There are no Players on the Card.";
        }
        JOptionPane.showMessageDialog(view, message, "", JOptionPane.INFORMATION_MESSAGE);

    }

    public void setOnCardRoles(Role[] onCardRoles)
    {
        this.onCardRoles = onCardRoles;
    }

    public Role[] getOnCardRoles() {
        return onCardRoles;
    }

    public String getImg() {
        return img;
    }
}
