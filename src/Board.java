import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Scanner;
import java.util.*;


//IMports off of GUI


public class Board {

    /*
    * Missing some sort of data structure to store locations relative to eachother. if your on one set what other set
    * can you go to etc. probably use a gragh data structure or some sort of ArrayList/treeNode. will look into as we
    * implement.
    * */

    private static Board instance = null;
    private int currentPlayer;
    private Player[] players;
    private int day;
    private Scene[] deck;
    private Location[] locations;
    private Document boarddoc;
    private Document carddoc;
    private BoardGUI view;
    private boolean terminate;



    private Board()
    {
        //GUI testing
        view = new BoardGUI(this);
        view.setVisible(true);

        // Take input from the user about number of players
        //JOptionPane.showInputDialog(view, "How many players, Choose a number between 2-8?");

        Scanner reader = new Scanner (System.in);
        int numPlayers=-1;
        String interpreter = "";
        while(numPlayers<2 || numPlayers>8)
        {

            try
            {
                interpreter = JOptionPane.showInputDialog(view, "Choose a number of players between 2 and 8");
                numPlayers = Integer.parseInt(interpreter);

            }
            catch (NumberFormatException a)
            {
                JOptionPane.showMessageDialog(view, "Enter a number between 2 and 8.","Error", JOptionPane.ERROR_MESSAGE);
                numPlayers = -1;
            }
        }

        day = 0;

        players= new Player[numPlayers];
        view.playersOnBoard = new JLabel[numPlayers];
        view.mAllPlayers = new JLabel[numPlayers];

        int i = 0;
        String[] colors = {"b1.png","c1.png","g1.png", "o1.png", "p1.png", "r1.png", "v1.png", "y1.png"};
        while(i < numPlayers)
        {
            String name = "";
            String token = colors[i];

            //System.out.print("Please enter a player name: ");
            //name = reader.next();

            while(name.equals(""))
            {
                name = JOptionPane.showInputDialog(view, "Enter a name for player "+(i+1));
                if(name == null) name = "";
            }

            if(numPlayers < 4)
            {
                players[i] = new Player(name, 0, 1, token); //SET LIKE THIS FOR TESTING
                day = 1;
            }
            else if(numPlayers == 4)
            {
                players[i] = new Player(name, 0, 1, token);
            }
            else if(numPlayers == 5)
            {
                players[i] = new Player(name, 2, 1, token);
            }
            else if(numPlayers == 6)
            {
                players[i] = new Player(name, 4, 1, token);
            }
            else if(numPlayers > 6)
            {
                token=token.replace('1','2');
                players[i] = new Player(name, 0, 2, token);
            }

            i++;
        }

        //old location of making players appear

        //System.out.println();

        i = 0;
        String playersStr="";
        while(i < numPlayers)
        {
            playersStr+="Player "+ (i+1) + ": " + players[i].getName()+"\n";


            view.mAllPlayers[i] = new JLabel(players[i].toString());
            view.mAllPlayers[i].setBounds(10+(225*i), view.boardlabel.getIcon().getIconHeight()+15, 200, 100);
            view.bPane.add(view.mAllPlayers[i], Integer.valueOf(2));
            i++;
        }

        JOptionPane.showMessageDialog(view, playersStr,"Players", JOptionPane.INFORMATION_MESSAGE);

        //System.out.println();

        Random rand = new Random();
        currentPlayer = rand.nextInt(numPlayers);
        //System.out.println("Player "+ players[currentPlayer].getName() +" goes first!");

        JOptionPane.showMessageDialog(view, "Player "+ players[currentPlayer].getName() +" goes first!", "", JOptionPane.INFORMATION_MESSAGE);

        //System.out.println();

        deck = new Scene[40];

        locations = new Location[12];

        try
        {
            boarddoc = getDocFromFile("board.xml");
        } catch (Exception ex){
            System.out.println("");
        }

        readBoard(boarddoc);

        try {
            carddoc = getDocFromFile("cards.xml");
        } catch (Exception ex){
            System.out.println("");
        }

        readCard(carddoc);



            /*playerlabel = new JLabel();
        ImageIcon pIcon = new ImageIcon("r3.png");
        playerlabel.setIcon(pIcon);
        //playerlabel.setBounds(114,227,pIcon.getIconWidth(),pIcon.getIconHeight());
        playerlabel.setBounds(114,227,46,46);
        playerlabel.setVisible(false);
        bPane.add(playerlabel,Integer.valueOf(3)); */
    }

    public static Board getInstance()
    {
        if(instance == null)
            instance = new Board();
        return instance;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }

    public int getPlayerID()
    {
        return currentPlayer;
    }

    //public void setCurrentPlayer(int currentPlayer) {
    //    this.currentPlayer = currentPlayer;
    //}

    public void nextPlayer()
    {
        currentPlayer++;
        currentPlayer=currentPlayer%players.length;
        return;
    }


    public void endGame()
    {
        String message="";
        message+="******GAME OVER******\n";
        //Arrays.sort(players, Collections.reverseOrder());

        message+="Ranking:\n";

        for(int i=0; i<players.length; i++)
        {
            message+=(i+1)+". "+players[i].endToString()+"\n";
        }

        JOptionPane.showMessageDialog(view, message, "", JOptionPane.INFORMATION_MESSAGE);
    }

    private Document getDocFromFile(String filename) throws ParserConfigurationException
    {
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = null;

            try {
                doc = db.parse(filename);
            } catch (Exception ex) {
                System.out.println("XML parse failure");
                ex.printStackTrace();
            }
            return doc;
        }
    }

    private  void readBoard(Document d)
    {
        Element root = d.getDocumentElement();
        NodeList locations = root.getElementsByTagName("set");

        String locationName;
        String[] setNeighbors = null;
        int[] locationCoord= new int[4];
        Role[] setRoles = null;
        int setShots = -1;
        int[][] takeCoord = new int[0][0];
        JLabel[] shotMarkers=null;


        //for loop that creates the set locations and puts them into the location array for Board
        for(int i = 0; i < locations.getLength(); i++)
        {
            Node location = locations.item(i);
            locationName = location.getAttributes().getNamedItem("name").getNodeValue();
            //System.out.println(locationName);
            NodeList children = location.getChildNodes();
            for(int j = 0; j < children.getLength(); j++)
            {
                Node sub = children.item(j);
                if("neighbors".equals(sub.getNodeName()))
                {
                    Element neighbors =  (Element) sub;
                    NodeList n = neighbors.getElementsByTagName("neighbor");

                    setNeighbors = new String[n.getLength()];

                    for(int k = 0; k < n.getLength(); k++)
                    {
                        Node place = n.item(k);
                        setNeighbors[k] = place.getAttributes().getNamedItem("name").getNodeValue();
                        //System.out.println("->" + place.getAttributes().getNamedItem("name").getNodeValue());
                    }
                }

                else if("area".equals(sub.getNodeName()))
                {
                    locationCoord=new int[4];
                    locationCoord[0]=Integer.parseInt(sub.getAttributes().getNamedItem("x").getNodeValue());
                    locationCoord[1]=Integer.parseInt(sub.getAttributes().getNamedItem("y").getNodeValue());
                    locationCoord[2]=Integer.parseInt(sub.getAttributes().getNamedItem("h").getNodeValue());
                    locationCoord[3]=Integer.parseInt(sub.getAttributes().getNamedItem("w").getNodeValue());

                    //System.out.println("Location:"+locationCoord[0]+"::"+locationCoord[1]);
                }

                else if("takes".equals(sub.getNodeName()))
                {
                    Element takes = (Element) sub;
                    NodeList n = takes.getElementsByTagName("take");
                    setShots = n.getLength();
                    takeCoord = new int[setShots][];
                    shotMarkers = new JLabel[setShots];
                    ImageIcon image = new ImageIcon("shot.png");

                    for(int k=0; k<setShots; k++)
                    {
                        Node take = n.item(k);
                        Node areas = take.getFirstChild();
                        takeCoord[k]=new int[4];

                        //System.out.println(areas.getAttributes().getNamedItem("x").getNodeValue());

                        takeCoord [k][0] = Integer.parseInt(areas.getAttributes().getNamedItem("x").getNodeValue());
                        takeCoord [k][1] = Integer.parseInt(areas.getAttributes().getNamedItem("y").getNodeValue());
                        takeCoord [k][2] = Integer.parseInt(areas.getAttributes().getNamedItem("h").getNodeValue());
                        takeCoord [k][3] = Integer.parseInt(areas.getAttributes().getNamedItem("w").getNodeValue());

                        shotMarkers[k] = new JLabel();
                        shotMarkers[k].setIcon(image);
                        shotMarkers[k].setBounds(takeCoord[k][0], takeCoord[k][1], 42, 42);
                        view.bPane.add(shotMarkers[k], Integer.valueOf(2));
                        shotMarkers[k].setVisible(true);


                                /*playerlabel = new JLabel();
        ImageIcon pIcon = new ImageIcon("r3.png");
        playerlabel.setIcon(pIcon);
        //playerlabel.setBounds(114,227,pIcon.getIconWidth(),pIcon.getIconHeight());
        playerlabel.setBounds(114,227,46,46);
        playerlabel.setVisible(false);
        bPane.add(playerlabel,Integer.valueOf(3)); */
                        //System.out.println("Location-shotCoord:"+takeCoord[k][0]+"::"+takeCoord[k][1]);
                    }
                    //System.out.println("-->Takes: "+setShots);
                }
                else if("parts".equals(sub.getNodeName()))
                {
                    Element parts = (Element) sub;
                    NodeList n = parts.getElementsByTagName("part");
                    NodeList flavorText = parts.getElementsByTagName("line");
                    NodeList areas = parts.getElementsByTagName("area");
                    setRoles = new Role[n.getLength()];
                    String roleTitle;
                    int roleRank;
                    String roleFlavor;
                    int[] roleC;

                    for(int k = 0; k <n.getLength(); k++)
                    {
                        Node role = n.item(k);
                        roleC = new int[4];
                        //System.out.println("--->Role title: "+role.getAttributes().getNamedItem("name").getNodeValue());
                        //System.out.println("--->Role rank: "+role.getAttributes().getNamedItem("level").getNodeValue());
                        Node text = flavorText.item(k);
                        Node area = areas.item(k);
                        //System.out.println("--->Role flavor: "+ text.getTextContent());

                        roleTitle = role.getAttributes().getNamedItem("name").getNodeValue();
                        roleRank = Integer.parseInt(role.getAttributes().getNamedItem("level").getNodeValue());
                        roleFlavor = text.getTextContent();

                        roleC[0] = Integer.parseInt(area.getAttributes().getNamedItem("x").getNodeValue());
                        roleC[1] = Integer.parseInt(area.getAttributes().getNamedItem("y").getNodeValue());
                        roleC[2] = Integer.parseInt(area.getAttributes().getNamedItem("h").getNodeValue());
                        roleC[3] = Integer.parseInt(area.getAttributes().getNamedItem("w").getNodeValue());

                        /*
                        System.out.println(roleTitle);
                        System.out.println(roleC[0]+","+roleC[1]);*/

                        setRoles[k] = new Role(roleTitle, roleFlavor, roleRank,roleC);
                    }
                }

            }

            this.locations[i] = new Location(locationName, setNeighbors, setShots, setRoles, locationCoord, shotMarkers);
        }

        locations = root.getElementsByTagName("trailer");
        Element location = (Element) locations.item(0);
        NodeList neighbors = location.getElementsByTagName("neighbor");
        setNeighbors = new String[neighbors.getLength()];
        for(int i = 0; i < neighbors.getLength();i++)
        {
            Node neighbor = neighbors.item(i);
            //System.out.println("-->"+neighbor.getAttributes().getNamedItem("name").getNodeValue());
            setNeighbors[i] = neighbor.getAttributes().getNamedItem("name").getNodeValue();
        }

        NodeList areas = location.getElementsByTagName("area");
        Node area = areas.item(0);

        locationCoord=new int[4];
        locationCoord[0]=Integer.parseInt(area.getAttributes().getNamedItem("x").getNodeValue());
        locationCoord[1]=Integer.parseInt(area.getAttributes().getNamedItem("y").getNodeValue());
        locationCoord[2]=Integer.parseInt(area.getAttributes().getNamedItem("h").getNodeValue());
        locationCoord[3]=Integer.parseInt(area.getAttributes().getNamedItem("w").getNodeValue());

        //System.out.println("Location-trailer:"+locationCoord[0]+"::"+locationCoord[1]);

        this.locations[10] = new Location("trailer", setNeighbors, locationCoord);

        locations = root.getElementsByTagName("office");
        location = (Element) locations.item(0);
        neighbors = location.getElementsByTagName("neighbor");
        setNeighbors = new String[neighbors.getLength()];
        for(int i = 0; i < neighbors.getLength();i++)
        {
            Node neighbor = neighbors.item(i);
            //System.out.println("-->"+neighbor.getAttributes().getNamedItem("name").getNodeValue());
            setNeighbors[i] = neighbor.getAttributes().getNamedItem("name").getNodeValue();
        }

        areas = location.getElementsByTagName("area");
        area = areas.item(0);

        locationCoord= new int[4];
        locationCoord[0]=Integer.parseInt(area.getAttributes().getNamedItem("x").getNodeValue());
        locationCoord[1]=Integer.parseInt(area.getAttributes().getNamedItem("y").getNodeValue());
        locationCoord[2]=Integer.parseInt(area.getAttributes().getNamedItem("h").getNodeValue());
        locationCoord[3]=Integer.parseInt(area.getAttributes().getNamedItem("w").getNodeValue());

        //System.out.println("Location-office:"+locationCoord[0]+"::"+locationCoord[1]);

        this.locations[11] = new Location("office", setNeighbors, locationCoord);
    }

    private void readCard(Document d)
    {
        Element root = d.getDocumentElement();
        NodeList cardList = root.getElementsByTagName("card");
        String cardName = null;
        String cardImg = null;
        String cardDesc = null;
        int cardBudg = 0;
        Role[] cardRoles = null;
        String roleName = null;
        int roleRank = 0;
        String roleDesc = null;
        int[] roleCoord;

        for(int i = 0; i <cardList.getLength(); i++)
        {
            Node card = cardList.item(i);
            cardName = card.getAttributes().getNamedItem("name").getNodeValue();
            cardImg =  card.getAttributes().getNamedItem("img").getNodeValue();
            cardBudg = Integer.parseInt(card.getAttributes().getNamedItem("budget").getNodeValue());
            //System.out.println(cardName+":"+cardBudg);

            Element deepReader = (Element) card;
            NodeList details = deepReader.getElementsByTagName("scene");
            cardDesc = "Scene " + details.item(0).getAttributes().getNamedItem("number").getNodeValue()+ ": ";
            cardDesc += details.item(0).getTextContent();

            //System.out.println(cardDesc);

            details = deepReader.getElementsByTagName("part");

            //System.out.println(details.getLength()+"---");

            cardRoles = new Role[details.getLength()];

            NodeList flavors = deepReader.getElementsByTagName("line");
            NodeList roleC = deepReader.getElementsByTagName("area");

            for(int j = 0; j < details.getLength(); j++)
            {
                roleName = details.item(j).getAttributes().getNamedItem("name").getNodeValue();
                roleRank = Integer.parseInt(details.item(j).getAttributes().getNamedItem("level").getNodeValue());
                roleDesc = flavors.item(j).getTextContent();

                roleCoord=new int[4];
                roleCoord[0] = Integer.parseInt(roleC.item(j).getAttributes().getNamedItem("x").getNodeValue());
                roleCoord[1] = Integer.parseInt(roleC.item(j).getAttributes().getNamedItem("y").getNodeValue());
                roleCoord[2] = Integer.parseInt(roleC.item(j).getAttributes().getNamedItem("h").getNodeValue());
                roleCoord[3] = Integer.parseInt(roleC.item(j).getAttributes().getNamedItem("w").getNodeValue());


                cardRoles[j] = new Role(roleName, roleDesc, roleRank, roleCoord);
                //System.out.println(roleName);
                //System.out.println(roleRank);
                //System.out.println(roleDesc);
                //System.out.println(roleCoord[0]+":::"+roleCoord[1]);
            }

            deck[i] = new Scene(cardName, cardDesc, cardBudg, cardRoles, cardImg);
        }
    }

    public boolean roundCheck()
    {
        int count=0;
        int i;
        for(i=0; i<10; i++)
        {
            Location test = locations[i];
            if(test.getCurrentShot()<test.getTotalShots())
            {
                count++;
            }
        }
        if(count>1) //_CHANGE BACK TO > 1
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void newRound()
    {
        day++;
        Random rand = new Random();
        int[] locCoord = new int[4];
        Collections.shuffle(Arrays.asList(deck));

        for(int i = 0; i < 10; i++) {
            if((day>1 && players.length>=4)||(day>2 && players.length<4))
            {
                view.bPane.remove(view.cardsOnBoard[i]);
            }
            Scene temp = deck[i];
            locations[i].setScene(temp);
            locations[i].setCurrentShot(0);
            view.cardsOnBoard[i] = new JLabel();
            ImageIcon icon = new ImageIcon("CardBack.jpg");
            view.cardsOnBoard[i].setIcon(icon);

            locCoord = locations[i].getCoords();
            view.cardsOnBoard[i].setBounds(locCoord[0], locCoord[1], icon.getIconWidth()+2, icon.getIconHeight());
            view.cardsOnBoard[i].setOpaque(true);
            view.bPane.add(view.cardsOnBoard[i], Integer.valueOf(1));

            locations[i].resetMarkers();

            //System.out.println("Location: "+locations[i].getName());
            //System.out.println("x:"+locCoord[0]+" y: "+locCoord[1]);
        }
        /* Add a scene card to this room
        cardTS = new JLabel();
        ImageIcon cIcon =  new ImageIcon("CardBack.jpg");
        cardTS.setIcon(cIcon);
        cardTS.setBounds(20,65,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        cardTS.setOpaque(true);

        // Add the card to the lower layer
        bPane.add(cardTS, Integer.valueOf(1));*/



        for(Player p : players)
        {
            p.setCurrentLocation("trailer");
            p.setEmployment(0);
            p.setRToken(0);
        }

        int[] trailerCoords=locations[10].getCoords();


        for(int i = 0; i < players.length; i++)
        {
            if((day>1 && players.length>=4)||(day>2 && players.length<4))
            {
                view.playersOnBoard[i].setVisible(false);
                view.bPane.remove(view.playersOnBoard[i]);
            }
            view.playersOnBoard[i]= new JLabel();
            ImageIcon pIcon = new ImageIcon(players[i].getImg());
            view.playersOnBoard[i].setIcon(pIcon);
            view.playersOnBoard[i].setBounds(trailerCoords[0]+pIcon.getIconWidth()*i/2, trailerCoords[1], pIcon.getIconWidth(), pIcon.getIconHeight());
            view.playersOnBoard[i].setVisible(true);
            view.bPane.add(view.playersOnBoard[i], Integer.valueOf(3));
        }

        Location l = null;
        //clean all the role info on board
        for(int i = 0; i < 10; i ++)
        {
            l = locations[i];
            for(Role r : l.getScene().getOnCardRoles())
            {
                r.setActor(null);
            }
            for(Role r: l.getOffCards())
            {
                r.setActor(null);
            }
        }
    }

    public int getDay()
    {
        return day;
    }

    public Location[] getLocations()
    {
        return locations;
    }

    public void setLocations(Location[] locations)
    {
        this.locations = locations;
    }

    public void overwiteCurrentPlayer(Player newPlayer)
    {
        players[currentPlayer]=newPlayer;
    }

    public BoardGUI getView() {
        return view;
    }

    public void movePlayer(int playerid)
    {
        Player player = players[playerid];
        //System.out.println(player.getName());
        Location currLoc = getLocation(player.getCurrentLocation());

        String message = "";
        int response = -1;

        //JButton[] options = new JButton[currLoc.neighbors.length];

        for(int i = 0; i < currLoc.neighbors.length; i++)
        {
            message += (i+1)+". "+currLoc.neighbors[i]+"\n";
        }
        while(response == -1)
        {
            try {
                response = Integer.parseInt(JOptionPane.showInputDialog(view, message + "Enter a Location"));
            } catch (NumberFormatException a) {
                JOptionPane.showMessageDialog(view, "Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                response = -1;
            }

            if(response < -1 || response > currLoc.neighbors.length)
            {
                JOptionPane.showMessageDialog(view, "Please enter a number between 0 and "+(currLoc.neighbors.length-1), "Error", JOptionPane.ERROR_MESSAGE );
                response = -1;
            }
        }

        player.setCurrentLocation(currLoc.neighbors[response-1]);
        for(int i=0; i<12; i++)
        {
            //System.out.println(currentPlayer.getCurrentLocation());
            //System.out.println(allLocations[ix].getName());
            if(player.getCurrentLocation().equalsIgnoreCase(locations[i].getName()))
            {
                currLoc=locations[i];
                //locationPtr=i;
                //System.out.println("You are now at "+currentLocation.getName()+".");
                if(!currLoc.isVisited())
                {
                    if(i<10)
                    {
                        currLoc.flipCard(view.cardsOnBoard[i]);
                    }
                }
            }
        }

        //updates
        players[playerid] = player;
        int[] newCoords = currLoc.getCoords();
        view.playersOnBoard[playerid].setBounds(newCoords[0]+10*playerid, newCoords[1], 40, 40);
        view.bMove.setVisible(false);

        if(players[playerid].getCurrentLocation().equalsIgnoreCase("office")) //casting check before move
        {
            view.bRank.setVisible(true);
            view.bRole.setVisible(false);
        }
        else if(players[playerid].getCurrentLocation().equalsIgnoreCase("trailer"))
        {
            view.bRole.setVisible(false);
            view.bRank.setVisible(false);
        }
        else
        {
            view.bRank.setVisible(false);
            if(getLocation(players[playerid].getCurrentLocation()).getCurrentShot()>=getLocation(players[playerid].getCurrentLocation()).getTotalShots())
            {
                view.bRole.setVisible(false);
            }
            else
            {
                view.bRole.setVisible(true);
            }
        }

        view.mAllPlayers[playerid].setText(players[playerid].toString());

    }

    public void rankUp(int playerID)
    {
        //players[playerID].rankUp();

        //Scanner reader = new Scanner(System.in);

        String rankDisplay="";
        rankDisplay+="**************************\n";
        rankDisplay+="::Rank::Dollars::Credits::\n";
        rankDisplay+=":: 2  ::   4   ::   5   ::\n";
        rankDisplay+=":: 3  ::  10   ::   10  ::\n";
        rankDisplay+=":: 4  ::  18   ::   15  ::\n";
        rankDisplay+=":: 5  ::  28   ::   20  ::\n";
        rankDisplay+=":: 6  ::  40   ::   25  ::\n";
        rankDisplay+="**************************\n";

        int newRank = -1;
        int paymentChoice = -1;
        int oldRank = players[playerID].getRank();

        if (players[playerID].getRank() == 6)
        {
            JOptionPane.showMessageDialog(view, "You are already Max Rank!", "Error", JOptionPane.ERROR_MESSAGE );
        }
        else
        {
            while (newRank <= players[playerID].getRank() || newRank > 6)
            {
                //System.out.println("Please enter new desired rank: ");
                try
                {
                    newRank = Integer.parseInt(JOptionPane.showInputDialog(view, rankDisplay + "New Desired Rank: "));
                }
                catch (NumberFormatException a)
                {
                    newRank=-1;
                    JOptionPane.showMessageDialog(view, "Please Enter a Number.", "Error", JOptionPane.ERROR_MESSAGE );
                }
            }

            while (paymentChoice < 1 || paymentChoice > 2)
            {
                try
                {
                    paymentChoice = Integer.parseInt(JOptionPane.showInputDialog(view, "Enter 1 to pay with credit, you have 2 to pay with Dollars."));
                }
                catch (NumberFormatException a)
                {
                    JOptionPane.showMessageDialog(view, "Please Enter a Number.", "Error", JOptionPane.ERROR_MESSAGE );
                    paymentChoice = -1;
                }
            }

            if (paymentChoice == 1) {
                if (newRank == 2) {
                    if (players[playerID].getCredit() < 5)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Credits", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(2);
                        players[playerID].adjcredit(-5);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 2!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 3)
                {
                    if (players[playerID].getCredit() < 10)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Credits", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(3);
                        players[playerID].adjcredit(-10);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 3!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 4)
                {
                    if (players[playerID].getCredit() < 15)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Credits", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(4);
                        players[playerID].adjcredit(-15);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 4!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 5)
                {
                    if (players[playerID].getCredit() < 20)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Credits", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(5);
                        players[playerID].adjcredit(-20);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 5!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 6)
                {
                    if (players[playerID].getCredit() < 25)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Credits", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(6);
                        players[playerID].adjcredit(-25);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 6!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            else
            {
                if (newRank == 2)
                {
                    if (players[playerID].getMoney() < 4)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Money", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(2);
                        players[playerID].adjmoney(-4);
                        //System.out.println("You're new Rank is 2");
                        JOptionPane.showMessageDialog(view, "Your new Rank is 2!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 3)
                {
                    if (players[playerID].getMoney() < 10)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Money", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(3);
                        players[playerID].adjmoney(-10);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 3!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 4)
                {
                    if (players[playerID].getMoney() < 18)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Money", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(4);
                        players[playerID].adjmoney(-18);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 4!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 5)
                {
                    if (players[playerID].getMoney() < 28)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Money", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(5);
                        players[playerID].adjmoney(-28);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 5!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else if (newRank == 6)
                {
                    if (players[playerID].getMoney() < 40)
                    {
                        JOptionPane.showMessageDialog(view, "Not enough Money", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        players[playerID].setRank(6);
                        players[playerID].adjmoney(-40);
                        JOptionPane.showMessageDialog(view, "Your new Rank is 6!", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            }
        }

        //Reset dice to appropriate rank
        int[] castingOfficeCoords = locations[11].getCoords();
        //System.out.println("offic coords: \nx: "+castingOfficeCoords[0]+" y: "+castingOfficeCoords[1]);
        //view.playersOnBoard[playerID]= new JLabel();
        String img = players[playerID].getImg().replace(((char)(oldRank+48)), ((char)(players[playerID].getRank()+48)));
        //System.out.println(img);
        ImageIcon pIcon = new ImageIcon(img);
        players[playerID].setImg(img);
        view.playersOnBoard[playerID].setIcon(pIcon);
        view.playersOnBoard[playerID].setBounds(castingOfficeCoords[0]+pIcon.getIconWidth()*playerID/2, castingOfficeCoords[1], pIcon.getIconWidth(), pIcon.getIconHeight());
        view.playersOnBoard[playerID].setVisible(true);
        view.bPane.add(view.playersOnBoard[playerID], Integer.valueOf(3));

        view.mAllPlayers[playerID].setText(players[playerID].toString());

        //System.out.println(view.playersOnBoard[playerID].getHorizontalAlignment()+":::"+view.playersOnBoard[playerID].getVerticalAlignment()+"\n"+castingOfficeCoords[0]+pIcon.getIconWidth()*playerID/2+":::"+castingOfficeCoords[1]);
    }

    public Location getLocation(String strLoc)
    {
        for(int ix=0; ix<12; ix++)
        {
            //System.out.println("Player: "+currentPlayer.getCurrentLocation());
            //System.out.println("Location: "+allLocations[ix].getName());
            if(strLoc.equalsIgnoreCase(locations[ix].getName()))
            {
                return locations[ix];
            }
        }
        return locations[12];
    }

    public void pickRole(int playerid)
    {
        view.bRole.setVisible(false);
        String input = "BUFFER";

        //PLAYER IS TAKING A ROLE
        //System.out.println("***"+currentLocation.getName()+"***");
        //System.out.println("***"+currentLocation.getScene().getTitle()+"***");
        Role[] offCard = getLocation(players[playerid].getCurrentLocation()).getOffCards();

        /*for(Role r : offCard)
        {
            System.out.println(r.getTitle());
            int[] tempCoords = r.getCoord();
            for(int i : tempCoords)
            {
                //System.out.print(i+" ");
            }
            //System.out.println();
        }*/
        Role[] onCard = getLocation(players[playerid].getCurrentLocation()).getScene().getRoles();
        int choice = -10;
        String onOrOff = "BUFFER";

        while (choice == -10)
        {
            onOrOff = "BUFFER";
            while (!(onOrOff.equalsIgnoreCase("Scene")) && !(onOrOff.equalsIgnoreCase("Set")))
            {
                //System.out.println("Would you like to work on the Scene Card? Or on the set? (\"Scene\"/\"Set\")");
                onOrOff = JOptionPane.showInputDialog(view,"Would you like to work on the Scene Card? Or on the set? (\"Scene\"/\"Set\")");
                if(onOrOff == null) onOrOff = "BUFFER";
            }
            if (onOrOff.equalsIgnoreCase("Set"))
            {
                String output ="";
                output+="Set Roles:\n";
                output+="-1: I don't want any role\n";
                output+="0: Go back to Scene or Set\n";
                for (int i = 0; i < offCard.length; i++)//go through all off card roles
                {
                    if (offCard[i].getActor() == null)
                    {
                        output+=(i + 1) + ": " + offCard[i].toString()+"\n";
                    }
                }
                while (choice < -1 || choice > offCard.length)
                {
                    //System.out.println("Please Make your Selection:");
                    try
                    {
                        choice = Integer.parseInt(JOptionPane.showInputDialog(view, output+"Please make your selection: "));
                    }
                    catch (NumberFormatException a)
                    {
                        //System.out.println("Please enter a number");
                        JOptionPane.showMessageDialog(view, "Please Enter a Number", "Error", JOptionPane.ERROR_MESSAGE );
                        choice = -10;
                    }
                }
                if (choice == -1)
                {
                    //Lets you out of the while loop
                    view.bRole.setVisible(true);
                }
                else if (choice == 0)
                {
                    //lets you loop though the menu.
                    choice = -10;
                }
                else if (players[playerid].getRank() < offCard[choice - 1].getMinRank())
                {
                    JOptionPane.showMessageDialog(view, "Rank not high enough, make another selection.", "Error", JOptionPane.ERROR_MESSAGE );
                    choice = -10;
                }
                else if (offCard[choice - 1].getActor() != null)
                {
                    JOptionPane.showMessageDialog(view, "Role already taken, please make another selection", "Error", JOptionPane.ERROR_MESSAGE );
                    choice = -10;
                }
            }
            else
            {
                String output ="";
                output+="Scene Roles:\n";
                output+="-1: I don't want any role\n";
                output+="0: Go back to Scene or Set\n";
                for (int i = 0; i < onCard.length; i++)
                {
                    if (onCard[i].getActor() == null)
                    {
                        output+=(i + 1) + ": " + onCard[i].toString()+"\n";
                    }
                }
                while (choice < -1 || choice > onCard.length + 1)
                {
                    //System.out.println("Please make your Selection:");
                    try
                    {
                        choice = Integer.parseInt(JOptionPane.showInputDialog(view, output+"Please make your selection: "));
                    }
                    catch (NumberFormatException a)
                    {
                        //System.out.println("Please enter a number");
                        JOptionPane.showMessageDialog(view, "Please Enter a Number", "Error", JOptionPane.ERROR_MESSAGE );
                        choice = -10;
                    }
                }

                if (choice == -1)//Choose no role
                {
                    //Lets you though the while loop
                    view.bRole.setVisible(true);
                }
                else if (choice == 0)//Go back though while loop
                {
                    //lets you loop though the menu.
                    choice = -10;
                }
                else if (players[playerid].getRank() < onCard[choice - 1].getMinRank())//Rank isn't high enough
                {
                    JOptionPane.showMessageDialog(view, "Rank not high enough, make another selection.", "Error", JOptionPane.ERROR_MESSAGE );
                    choice = -10;
                }
                else if (onCard[choice - 1].getActor() != null)
                {
                    JOptionPane.showMessageDialog(view, "Role already taken, please make another selection", "Error", JOptionPane.ERROR_MESSAGE );
                    choice = -10;
                }
            }
        }

        if (choice == -1)
        {
            //don't take any role
        }
        else if (onOrOff.equalsIgnoreCase("Set"))
        {
            //check if has rank for role
            offCard[choice - 1].setActor(players[playerid]);
            getLocation(players[playerid].getCurrentLocation()).setOffCards(offCard);
            players[playerid].setEmployment(1);
            int [] offCardCoord = offCard[choice-1].getCoord();
            view.playersOnBoard[playerid].setBounds( offCardCoord[0], offCardCoord[1],40,40);

            //System.out.println(offCard[choice-1].getTitle());
            //System.out.println("coords: "+offCardCoord[0]+","+offCardCoord[1]);
        }
        else
        {
            onCard[choice - 1].setActor(players[playerid]);
            getLocation(players[playerid].getCurrentLocation()).getScene().setOnCardRoles(onCard);
            players[playerid].setEmployment(2);
            int[] onCardCoords = onCard[choice-1].getCoord();
            int[] locCoords = getLocation(players[playerid].getCurrentLocation()).getCoords();

            view.playersOnBoard[playerid].setBounds( onCardCoords[0]+locCoords[0], onCardCoords[1]+locCoords[1],40,40);

        }

        view.mAllPlayers[playerid].setText(players[playerid].toString());
    }

    public void act(int playerid) {
        Scene currentScene = getLocation(players[playerid].getCurrentLocation()).getScene();

        Random rand = new Random();

        int prof = rand.nextInt(6) + 1;
        //prof=6; For debugging
        String message = "";

        prof += players[playerid].getRToken();
        //System.out.println("With " + players[playerid].getRToken() + " rehearsal tokens your final score is: " + prof);
        message +="With " + players[playerid].getRToken() + " rehearsal tokens your final roll is " + prof + "\n";

        if (prof >= currentScene.getBudget())//You succeed Role
        {
            //System.out.println("You Successfully performed the role!");
            message += "You Successfully performed the role!\n";

            getLocation(players[playerid].getCurrentLocation()).removeMarker();
            getLocation(players[playerid].getCurrentLocation()).adjcurrentShot();



            if (players[playerid].getEmployment() == 1)//Off-Card
            {
                //System.out.println("You're working on the Set so you get 1 dollar and 1 credit.");
                message += "You're working on the Set so you get 1 dollar and 1 credit.\n";
                players[playerid].adjmoney(1);
                players[playerid].adjcredit(1);
            }
            else//Player is on Card
            {
                //System.out.println("You're working on the Scene so you get 2 Credits");
                message += "You're working on the Scene so you get 2 credits\n";
                players[playerid].adjcredit(2);
            }

            JOptionPane.showMessageDialog(view, message, "", JOptionPane.INFORMATION_MESSAGE);

            //System.out.println("This Scene is at: " + getLocation(players[playerid].getCurrentLocation()).getCurrentShot() + "/" +getLocation(players[playerid].getCurrentLocation()).getTotalShots() + " shot tokens");
            if (getLocation(players[playerid].getCurrentLocation()).getCurrentShot() >= getLocation(players[playerid].getCurrentLocation()).getTotalShots())
            {
                //System.out.println("This Card is completed!");
                currentScene.payout(view);

                dumpCard(getLocation(players[playerid].getCurrentLocation()));

                //reset everyone's rehearsal tokens
                for (Role x : getLocation(players[playerid].getCurrentLocation()).getOffCards())
                {
                    if (x.getActor() != null)
                    {
                        x.getActor().setEmployment(0);
                        x.getActor().setRToken(0);
                    }
                }
                for (Role x : getLocation(players[playerid].getCurrentLocation()).getScene().getRoles())
                {
                    if (x.getActor() != null)
                    {
                        x.getActor().setEmployment(0);
                        x.getActor().setRToken(0);
                    }
                }
                //reset everyone's employment
            }
        }
        else//You Fail role
        {
            //System.out.println("You did not successfully perform the role");
            message += "You did not succesfully perform the role\n";
            if (players[playerid].getEmployment() == 1)//Off-Card
            {
                //System.out.println("But you're working on the Set so you still get 1 dollar.");
                message += "But you're working on the set so you still get 1 dollar.";
                players[playerid].adjmoney(1);
            }
            else//Player is on Card
            {
                //System.out.println("Unfortunately since you're working on the Scene you don't get anything.");
                message += "Unfortunately since you're working on the Scene you don't get anything.\n";
            }
            JOptionPane.showMessageDialog(view, message, "", JOptionPane.INFORMATION_MESSAGE);
        }



        view.bAct.setVisible(false);
        view.bRehearse.setVisible(false);
        view.bEnd.setVisible(true);

        view.mAllPlayers[playerid].setText(players[playerid].toString());
    }

    public void rehearse(int playerid)
    {
        players[playerid].adjRToken();
        Location loc = getLocation(players[playerid].getCurrentLocation());
        if(players[playerid].getRToken()+1 >= loc.getScene().getBudget())
        {
            act(playerid);
        }
        else
        {
            JOptionPane.showMessageDialog(view, "You've gained one Rehearsal token!\n You now have "+players[playerid].getRToken(), "", JOptionPane.INFORMATION_MESSAGE);
        }
        view.bEnd.setVisible(true);
        view.bAct.setVisible(false);
        view.bRehearse.setVisible(false);

        view.mAllPlayers[playerid].setText(players[playerid].toString());
    }

    public boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public void dumpCard(Location loc)
    {
        for(int ix=0; ix<12; ix++)
        {
            //System.out.println("Player: "+currentPlayer.getCurrentLocation());
            //System.out.println("Location: "+allLocations[ix].getName());
            if(loc.getName().equalsIgnoreCase(locations[ix].getName()))
            {
                view.cardsOnBoard[ix].setVisible(false);
            }
        }
    }
}
