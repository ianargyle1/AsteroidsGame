package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.Iterator;
import javax.swing.*;
import asteroids.participants.Ship;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;
    
    private String score;
    private int lives;
    private Ship shipLives;
    private String level;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        score = "";
        level = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
        shipLives = new Ship(0, 0, -Math.PI/2, null);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }
    
    public void showScore(int s) {
        score = "" + s;
    }
    
    public void showLives(int l) {
        lives = l;
    }
    
    public void showLevel(int le) {
        level = "" + le;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);
        

        // Draw each participant in its proper place
        Iterator<Participant> iter = controller.getParticipants();
        while (iter.hasNext())
        {
            iter.next().draw(g);
        }
        
        g.setFont(new Font("SansSerif", 0, 30));
        g.drawString(score, 30, 40);
        
        g.drawString(level, 750 - g.getFontMetrics().stringWidth(this.level) - 50, 50);

        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
        
        double start = 30;
        for (int i = 0; i < lives; i++)
        {
          shipLives.setPosition(start + 10, 70);
          shipLives.move();
          shipLives.draw(g);
          start = start + 30;
        }
        
    }
}
