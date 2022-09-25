import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.*;
import javax.swing.JOptionPane;


public class BoardGUI extends JFrame
{
    // JLabels
    JLabel boardlabel;
    //JLabel playerlabel;
    JLabel mLabel;
    JLabel mCurrentPlayerLabel;
    JLabel mPlayersLabel;
    JLabel[] mAllPlayers;

    //Cards
    JLabel[] cardsOnBoard;

    JLabel[] playersOnBoard;

    //JButtons
    JButton bAct;
    JButton bRehearse;
    JButton bMove;
    JButton bRank;
    JButton bRole;
    JButton bEnd;

    // JLayered Pane
    JLayeredPane bPane;

    Board  game;

    // Constructor
    public BoardGUI(Board game)
    {

        // Set the title of the JFrame
        super("Deadwood");
        // Set the exit option for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create the JLayeredPane to hold the display, cards, dice and buttons
        bPane = getLayeredPane();

        // Create the deadwood board
        boardlabel = new JLabel();
        ImageIcon icon =  new ImageIcon("board.jpg");
        boardlabel.setIcon(icon);
        boardlabel.setBounds(0,0,icon.getIconWidth(),icon.getIconHeight());

        // Add the board to the lowest layer
        bPane.add(boardlabel, Integer.valueOf(0));

        // Set the size of the GUI
        setSize(icon.getIconWidth()+200,icon.getIconHeight());

        // Create the Menu for action buttons
        mLabel = new JLabel("MENU");
        mLabel.setBounds(icon.getIconWidth()+60,0,150,20);
        bPane.add(mLabel, Integer.valueOf(2));

        // Create Action buttons
        bAct = new JButton("ACT");
        bAct.setBackground(Color.white);
        bAct.setBounds(icon.getIconWidth()+10, 30,150, 20);
        bAct.addMouseListener(new boardMouseListener());

        bRehearse = new JButton("REHEARSE");
        bRehearse.setBackground(Color.white);
        bRehearse.setBounds(icon.getIconWidth()+10,60,150, 20);
        bRehearse.addMouseListener(new boardMouseListener());

        bMove = new JButton("MOVE");
        bMove.setBackground(Color.white);
        bMove.setBounds(icon.getIconWidth()+10,90,150, 20);
        bMove.addMouseListener(new boardMouseListener());

        bRank = new JButton("RANK UP");
        bRank.setBackground(Color.white);
        bRank.setBounds(icon.getIconWidth()+10,120,150, 20);
        bRank.addMouseListener(new boardMouseListener());

        bRole = new JButton("PICK A ROLE");
        bRole.setBackground(Color.white);
        bRole.setBounds(icon.getIconWidth()+10,150,150, 20);
        bRole.addMouseListener(new boardMouseListener());

        bEnd = new JButton("END TURN");
        bEnd.setBackground(Color.white);
        bEnd.setBounds(icon.getIconWidth()+10,180,150, 20);
        bEnd.addMouseListener(new boardMouseListener());

        // Place the action buttons in the top layer
        bPane.add(bAct, Integer.valueOf(2));
        bPane.add(bRehearse, Integer.valueOf(2));
        bPane.add(bMove, Integer.valueOf(2));
        bPane.add(bRank, Integer.valueOf(2));
        bPane.add(bRole, Integer.valueOf(2));
        bPane.add(bEnd, Integer.valueOf(2));

        mCurrentPlayerLabel = new JLabel("CURRENT PLAYER\n");
        mCurrentPlayerLabel.setBounds(icon.getIconWidth()+25, 210, 150, 40);
        bPane.add(mCurrentPlayerLabel, Integer.valueOf(2));

        mPlayersLabel = new JLabel("PLAYERS");
        mPlayersLabel.setBounds(10, icon.getIconHeight()+2, 100, 20);
        bPane.add(mPlayersLabel, Integer.valueOf(2));







        cardsOnBoard = new JLabel[10];
        this.game = game;

        /* Add a scene card to this room
        cardTS = new JLabel();
        ImageIcon cIcon =  new ImageIcon("CardBack.jpg");
        cardTS.setIcon(cIcon);
        cardTS.setBounds(20,65,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        cardTS.setOpaque(true);

        // Add the card to the lower layer
        bPane.add(cardTS, Integer.valueOf(1));

        // Add a dice to represent a player.
        // Role for Crusty the prospector. The x and y co-ordiantes are taken from Board.xml file
        playerlabel = new JLabel();
        ImageIcon pIcon = new ImageIcon("r3.png");
        playerlabel.setIcon(pIcon);
        //playerlabel.setBounds(114,227,pIcon.getIconWidth(),pIcon.getIconHeight());
        playerlabel.setBounds(114,227,46,46);
        playerlabel.setVisible(false);
        bPane.add(playerlabel,Integer.valueOf(3)); */
    }


    // This class implements Mouse Events

    class boardMouseListener implements MouseListener
    {
        // Code for the different button clicks
        public void mouseClicked(MouseEvent e) {

            if (e.getSource()== bAct){
                //playerlabel.setVisible(true);
                //System.out.println("Acting is Selected\n");
                game.act(game.getPlayerID());
            }
            else if (e.getSource()== bRehearse){
                //System.out.println("Rehearse is Selected\n");
                game.rehearse(game.getPlayerID());
            }
            else if (e.getSource()== bMove){
                //System.out.println("Move is Selected\n");
                game.movePlayer(game.getPlayerID());

            }
            else if(e.getSource()== bRank){
                //System.out.println("rank up my guy");
                game.rankUp(game.getPlayerID());
            }
            else if(e.getSource()==bRole)
            {
                //System.out.println("Pick a role is selected");
                game.pickRole(game.getPlayerID());
            }
            else if (e.getSource()==bEnd)
            {
                //System.out.println("End turn called");
                game.setTerminate(false);
            }
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
    }
}
