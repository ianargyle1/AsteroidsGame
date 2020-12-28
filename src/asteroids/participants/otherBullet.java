package asteroids.participants;

import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Participant;

public class otherBullet extends Bullet implements AsteroidDestroyer, AlienDestroyer
{
    public otherBullet (double x, double y, double direction)
    {
        super(x, y, direction);
    }

    @Override
    public void collidedWith (Participant p)
    {
        Participant.expire(this);
    }
}
