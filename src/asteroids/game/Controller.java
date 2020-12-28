package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import asteroids.participants.Asteroid;
import asteroids.participants.Ship;
import asteroids.participants.alien;
import asteroids.game.Participant;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives left */
    private int lives;

    /** The game display */
    private Display display;

    /** Is the user accelerating */
    private boolean accelerating;
    /** Is the user turning right */
    private boolean turningRight;
    /** Is the user turning left */
    private boolean turningLeft;
    /** Is the user shooting */
    private boolean shooting;
    /** Keeps track of score */
    private int score;
    /** Keeps track of level */
    private int level;

    private Timer theTimer;

    private String playSound;
    
    private alien Alien;

    private Clip beat1Clip = createClip("/sounds/beat1.wav");
    private Clip beat2Clip = createClip("/sounds/beat2.wav");

    public Clip createClip (String soundFile)
    {
        // Opening the sound file this way will work no matter how the
        // project is exported. The only restriction is that the
        // sound files must be stored in a package.
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            // Create and return a Clip that will play a sound file. There are
            // various reasons that the creation attempt could fail. If it
            // fails, return null.
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }

    public void playSound (Clip clip)
    {
        try
        {
            if (clip.isRunning())
            {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
        catch (Exception e)
        {

        }
    }

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        theTimer = new Timer(1000, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        playSound = "beat1";
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    public int getBullets ()
    {
        return pstate.numBullets();
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
        level = 1;
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");

        scheduleTransition((int) Math.round(5000 * (Constants.RANDOM.nextDouble() + 1)));
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(200), Constants.RANDOM.nextInt(200), 3, this));
        addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(200), Constants.RANDOM.nextInt(251) + 550, 3, this));
        addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(251) + 550, Constants.RANDOM.nextInt(200), 3, this));
        addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(251) + 550, Constants.RANDOM.nextInt(251) + 550, 3, this));
        for (int i = 0; i < level; i++)
        {
            int corner = Constants.RANDOM.nextInt(4);
            if (corner == 0) {
                addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(200), Constants.RANDOM.nextInt(200), 3, this));
            }
            else if (corner == 1) {
                addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(200), Constants.RANDOM.nextInt(251) + 550, 3, this));
            }
            else if (corner == 2) {
                addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(251) + 550, Constants.RANDOM.nextInt(200), 3, this));
            }
            else if (corner == 3) {
                addParticipant(new Asteroid(Constants.RANDOM.nextInt(4), 2, Constants.RANDOM.nextInt(251) + 550, Constants.RANDOM.nextInt(251) + 550, 3, this));
            }
        }
    }
    
    private void placeAlienShip()
    {
      Participant.expire(Alien);
      if (this.level > 1)
      {
        int alienShipSize = this.level == 2 ? 1 : 0;
        Alien = new alien(alienShipSize, this);
        Alien.setPosition(0, 750 * Constants.RANDOM.nextDouble());
        Alien.setVelocity(5 - alienShipSize, Constants.RANDOM.nextInt(2) * 3.141592653589793D);
        addParticipant(Alien);
      }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
        if (Alien != null)
        {
          Participant.expire(Alien);
          Alien.remove();
          Alien = null;
        }
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        theTimer.stop();
        theTimer.setDelay(1000);
        playSound = "beat1";
        theTimer.start();
        // Clear the screen
        clear();

        // Plac asteroids
        placeAsteroids();

        // Set score, lives, and level to their default values
        score = 0;
        lives = 3;
        level = 1;

        // Place the ship
        placeShip();

        // // Reset statistics
        // lives = 1;

        display.showScore(score);
        display.showLives(lives);
        display.showLevel(level);

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        lives -= 1;
        display.showLives(lives);
        // Null out the ship
        ship = null;

        // Display a legend
        display.setLegend("Ouch!");

        theTimer.stop();

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    public void alienShipDestroyed (int size)
    {
        score += Constants.ALIENSHIP_SCORE[size];

        Alien = null;
        if (this.ship != null)
        {
            scheduleTransition((int) Math.round(5000 * (Constants.RANDOM.nextDouble() + 1)));
        }
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed (int size)
    {
        score += Constants.ASTEROID_SCORE[size];
        display.showScore(score);
        if (pstate.countAsteroids() == 0)
        {
            theTimer.stop();
            scheduleTransition(END_DELAY);
        }
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    private void nextScreen ()
    {
        clear();
        this.level += 1;
        placeAsteroids();
        placeShip();
        display.showLevel(level);
        theTimer.setDelay(900);
        theTimer.restart();
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }
        else if (e.getSource() == theTimer)
        {
            if (playSound == "beat1")
            {
                playSound = "beat2";
                playSound(beat2Clip);
            }
            else
            {
                playSound = "beat1";
                playSound(beat1Clip);
            }
            theTimer.setDelay(Math.max(300, theTimer.getDelay() - 9));
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();
            if (ship != null && accelerating)
            {
                ship.accelerate();
            }
            if (ship != null && turningRight)
            {
                ship.turnRight();
            }
            if (ship != null && turningLeft)
            {
                ship.turnLeft();
            }
            if (ship != null && shooting)
            {
                ship.shoot();
            }

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
            {
                finalScreen();
            }
            else if (pstate.countAsteroids() == 0)
            {
                theTimer.setDelay(1000);
                nextScreen();
            }
            else if (ship == null)
            {
                placeShip();
            }
            else if (Alien == null) {
                placeAlienShip();
            }
        }
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
        {
            turningRight = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
        {
            turningLeft = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
        {
            accelerating = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S)
        {
            shooting = true;
        }
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
        {
            turningRight = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
        {
            turningLeft = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
        {
            ship.coast();
            accelerating = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S)
        {
            shooting = false;
        }
    }

    public void asteroidHit (int size)
    {
        score += Constants.ASTEROID_SCORE[size];
        display.showScore(score);
        if (pstate.countAsteroids() == 0)
        {
            theTimer.stop();
            scheduleTransition(END_DELAY);
        }
    }
}
