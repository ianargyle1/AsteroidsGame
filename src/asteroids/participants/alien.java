package asteroids.participants;

import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class alien extends Participant implements AsteroidDestroyer, ShipDestroyer
{
    public Clip createClip (String soundFile)
    {
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
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
    
    private Clip saucerBig = createClip("/sounds/saucerBig.wav");
    private Clip saucerSmall = createClip("/sounds/saucerSmall.wav");
    private Clip fire = createClip("/sounds/fire.wav");
    private Clip bangAlienShip = createClip("/sounds/bangAlienShip.wav");

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
    
    private Shape outline;
    private int size;
    private Controller controller;
    boolean changeDirection = false;

    public alien (int s, Controller c)
    {
        this.size = s;
        this.controller = c;

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20, 0);
        poly.lineTo(9, 9);
        poly.lineTo(-9, 9);
        poly.lineTo(-20, 0);
        poly.lineTo(20, 0);
        poly.lineTo(-20, 0);
        poly.lineTo(-9, -9);
        poly.lineTo(9, -9);
        poly.lineTo(-9, -9);
        poly.lineTo(-5, -17);
        poly.lineTo(5, -17);
        poly.lineTo(9, -9);
        poly.closePath();
        this.outline = poly;

        double scale = Constants.ALIENSHIP_SCALE[s];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        new ParticipantCountdownTimer(this, "shoot", 1500);

        new ParticipantCountdownTimer(this, "change", 1000);
        if (getSize() == 1)
        {
            if (saucerBig.isRunning()) {
                saucerBig.stop();
              }
            saucerBig.setFramePosition(0);
            saucerBig.loop(-1);
        }
        else
        {
            if (saucerSmall.isRunning()) {
                saucerSmall.stop();
              }
            saucerSmall.setFramePosition(0);
            saucerSmall.loop(-1);
        }
    }

    protected Shape getOutline ()
    {
        return this.outline;
    }

    public int getSize ()
    {
        return this.size;
    }

    public void countdownComplete (Object payload)
    {
        if ("shoot".equals(payload))
        {
            Ship ship = this.controller.getShip();
            if (ship != null)
            {
                fireBullet();
                playSound(fire);
                new ParticipantCountdownTimer(this, "shoot", 1500);
            }
        }
        else if ("change".equals(payload))
        {
            this.changeDirection = true;
        }
    }

    public void move ()
    {
        super.move();
        if (this.changeDirection)
        {
            this.changeDirection = false;
            if (Math.cos(getDirection()) > 0)
            {
                setDirection(Constants.RANDOM.nextInt(3) - 1);
            }
            else
            {
                setDirection(3.141592653589793D + Constants.RANDOM.nextInt(3) - 1);
            }
            new ParticipantCountdownTimer(this, "change", 1000);
        }
    }

    public void fireBullet ()
    {
        alienBullet bullet = new otherAlienBullet(getX(), getY(), getShootingDirectionToShip());
        bullet.setSpeed(15);
        controller.addParticipant(bullet);
        playSound(fire);
    }

    public double getShootingDirectionToShip ()
    {
        if (this.size == 1)
        {
            return Constants.RANDOM.nextDouble() * 2 * 3.141592653589793;
        }
        Ship ship = this.controller.getShip();
        double deltaX = ship.getX() - getX();
        double deltaY = ship.getY() - getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double direction = Math.acos(deltaX / distance);
        direction = deltaY > 0 ? direction : -direction;
        double delta = 0.08726646259971647D;
        return Constants.RANDOM.nextDouble() * 2 * delta + direction - delta;
    }

    public void remove ()
    {
        Participant.expire(this);
        if (getSize() == 1)
        {
            if (saucerBig.isRunning()) {
                saucerBig.stop();
              }
        }
        else
        {
            if (saucerSmall.isRunning()) {
                saucerSmall.stop();
              }
        }
    }

    public void collidedWith (Participant p)
    {
        if ((p instanceof AlienDestroyer))
        {
            remove();
            playSound(bangAlienShip);

            this.controller.addParticipant(new debris(getX(), getY(), 10 * (this.size + 1)));
            this.controller.addParticipant(new debris(getX(), getY(), 10 * (this.size + 1)));
            this.controller.addParticipant(new debris(getX(), getY(), 10 * (this.size + 1)));
            this.controller.addParticipant(new debris(getX(), getY(), 10 * (this.size + 1)));
            this.controller.addParticipant(new debris(getX(), getY(), 5 * (this.size + 1)));
            this.controller.addParticipant(new debris(getX(), getY(), 5 * (this.size + 1)));

            this.controller.alienShipDestroyed(this.size);
        }
    }
}
