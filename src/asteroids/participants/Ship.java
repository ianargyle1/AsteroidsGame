package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import asteroids.destroyers.*;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import asteroids.game.Controller;
import sounds.SoundDemo;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    /** Game controller */
    private Controller controller;

    private boolean showFlame;

    /** The ship's outline while accelerating */
    private Shape flameShip;

    private boolean accelerating;

    private Clip thrustSound = createClip("/sounds/thrust.wav");
    private Clip shootSound = createClip("/sounds/fire.wav");
    private Clip shipDestroy = createClip("/sounds/bangShip.wav");

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        this.showFlame = true;
        setPosition(x, y);
        setRotation(direction);

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;

        poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -5);
        poly.lineTo(-25, 0);
        poly.lineTo(-14, 5);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        this.flameShip = poly;

        // Schedule an acceleration in two seconds
        new ParticipantCountdownTimer(this, "move", 2000);
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    @Override
    protected Shape getOutline ()
    {
        {
            if (this.accelerating)
            {
                this.showFlame = (!this.showFlame);
                if (this.showFlame)
                {
                    return this.flameShip;
                }
                return this.outline;
            }
            return this.outline;
        }
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        applyFriction(SHIP_FRICTION);
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);
        this.accelerating = true;

        soundLoop(thrustSound);

    }

    public void coast ()
    {
        this.accelerating = false;
        loopEnd(thrustSound);

    }

    /**
     * Shoots bullets
     */
    public void shoot ()
    {
        if (this.controller.getBullets() <= 8)
        {
            Bullet bullet = new otherBullet(getXNose(), getYNose(), getRotation());
            bullet.setVelocity(15, getRotation());
            controller.addParticipant(bullet);
            playSound(shootSound);
        }
    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);

            playSound(shipDestroy);

            loopEnd(thrustSound);

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();

            this.controller.addParticipant(new debris(getX(), getY(), 20.0));
            this.controller.addParticipant(new debris(getX(), getY(), 20.0));
            this.controller.addParticipant(new debris(getX(), getY(), 5.0));
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        // if (payload.equals("move"))
        // {
        // accelerate();
        // new ParticipantCountdownTimer(this, "move", 200);
        // }
    }
}
