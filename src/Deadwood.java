import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Deadwood {
    public static void main(String args[]) {
        Board game = Board.getInstance();

        game.newRound();
        while (game.getDay() < 5) {
            Player currentPlayer = game.getCurrentPlayer();
            JOptionPane.showMessageDialog(game.getView(), "######## BEGINNING OF DAY " + game.getDay() + " ########","", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("######## BEGINNING OF DAY " + game.getDay() + " ########");
            while (game.roundCheck()) {
                takeTurn(game, currentPlayer);
                game.overwiteCurrentPlayer(currentPlayer);
                game.nextPlayer();
                currentPlayer = game.getCurrentPlayer();
            }
            JOptionPane.showMessageDialog(game.getView(), "######## END OF ROUND ########","", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("######## END OF ROUND ########");
            game.newRound();
        }

        game.endGame();
        game.getView().setVisible(false);
        game.getView().dispose();
        //System.out.println("End of Game");
        return;

    }

    private static void takeTurn(Board game, Player currentPlayer) {
        //BEGINING OF TURN IMPLEMENTATION
        game.setTerminate(true);
        //System.out.println("#########");
        //System.out.print("It's your turn ");
        //System.out.println(currentPlayer.toString());
        //Scanner sc = new Scanner(System.in);
        //String input = "BUFFER";
        //Location currentLocation = null;
        //Location[] allLocations = game.getLocations();
        int locationPtr = -1;

        //IF PLAYER HAS NO ROLE

        JOptionPane.showMessageDialog(game.getView(), "It is "+currentPlayer.getName()+"'s turn.","", JOptionPane.INFORMATION_MESSAGE);

        game.getView().mCurrentPlayerLabel.setText("<html>CURRENT PLAYER<br/>"+currentPlayer.getName()+"</html>");

        if (currentPlayer.getEmployment() == 0)//No Role
        {
            game.getView().bRehearse.setVisible(false);
            game.getView().bAct.setVisible(false);
            game.getView().bMove.setVisible(true);
            game.getView().bEnd.setVisible(true);
            //1st RANK UP CHECK


            if (currentPlayer.getCurrentLocation().equalsIgnoreCase("office")) //casting check before move
            {
                game.getView().bRank.setVisible(true);
                game.getView().bRole.setVisible(false);
            }
            else if (currentPlayer.getCurrentLocation().equalsIgnoreCase("trailer")) {
                game.getView().bRank.setVisible(false);
                game.getView().bRole.setVisible(false);

            }
            else
            {
                game.getView().bRank.setVisible(false);
                if (game.getLocation(currentPlayer.getCurrentLocation()).getCurrentShot() >= game.getLocation(currentPlayer.getCurrentLocation()).getTotalShots()) {
                    game.getView().bRole.setVisible(false);
                }
                else
                {
                    game.getView().bRole.setVisible(true);
                }
            }
        }
        else //Start turn with role
        {
            game.getView().bRehearse.setVisible(true);
            game.getView().bAct.setVisible(true);
            game.getView().bMove.setVisible(false);
            game.getView().bRole.setVisible(false);
            game.getView().bRank.setVisible(false);
            game.getView().bEnd.setVisible(false);
        }


        while (game.getTerminate()) {
            //System.out.println("Looping" + currentPlayer.getName());
            //TimeUnit.SECONDS.sleep(1);
            System.out.print("");
        }


        //System.out.println("End Of Turn");
        //System.out.println("***********\n");


    }
}