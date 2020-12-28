package asteroids.participants;

import asteroids.destroyers.AlienDestroyer;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public abstract class Bullet extends Participant
{
    private Shape outline;

    public Bullet (double x, double y, double direction)
    {
        setPosition(x, y);
        setVelocity(15, direction);
        outline = new Ellipse2D.Double(0, 0, 1, 1);
        new ParticipantCountdownTimer(this, this, 1000);
    }

    protected Shape getOutline ()
    {
        return this.outline;
    }

    public void countdownComplete (Object payload)
    {
        Participant.expire(this);
    }
}
