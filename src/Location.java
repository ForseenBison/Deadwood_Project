import javax.swing.*;
import java.awt.*;

public class Location {

    private int totalShots;
    private int currentShot;
    private Scene scene;
    private boolean visited;
    private Role[] offCards;
    private String name;
    protected String[] neighbors;
    public int[] coords;
    private JLabel[] takes;


    Location(String name, String[] neighbors, int totalShots, Role[] offCards, int[] coords, JLabel[] takes) {
        this.name = name;
        this.neighbors = neighbors;
        this.totalShots = totalShots;
        this.offCards = offCards;
        this.coords = coords.clone();
        this.takes = takes;

        currentShot = 0;
        scene = null;
        visited = false;
    }

    Location(String name, String[] neighbors, int[] coords) {
        this.name = name;
        this.neighbors = neighbors;
        this.coords = coords;

        takes = null;
        totalShots = -1;
        currentShot = -1;
        scene = null;
        visited = false;
        offCards = null;
    }

    public int getCurrentShot() {
        return currentShot;
    }

    public int getTotalShots() {
        return totalShots;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setCurrentShot(int currentShot) {
        this.currentShot = currentShot;
    }

    public Role[] getOffCards() {
        return offCards;
    }

    public Scene getScene() {
        return scene;
    }

    public void adjcurrentShot() {
        currentShot++;
    }

    public String getName() {
        return name;
    }

    public boolean isVisited() {
        return visited;
    }

    public void flipCard(JLabel x) {
        Icon icon = new ImageIcon(scene.getImg());
        x.setIcon(icon);
    }

    public void setOffCards(Role[] offCards) {
        this.offCards = offCards;
    }

    public int[] getCoords() {
        return coords;
    }

    public void resetMarkers()
    {
        for(JLabel t : takes)
        {
            t.setVisible(true);
        }
    }

    public void removeMarker()
    {
        takes[currentShot].setVisible(false);
    }

}