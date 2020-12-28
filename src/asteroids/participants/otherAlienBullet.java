package asteroids.participants;

import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Participant;

public class otherAlienBullet extends alienBullet implements AsteroidDestroyer, ShipDestroyer
{
    public otherAlienBullet (double x, double y, double direction)
    {
        super(x, y, direction);
    }

    @Override
    public void collidedWith (Participant p)
    {
        Participant.expire(this);
    }
}
