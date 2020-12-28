package asteroids.participants;

import asteroids.game.Constants;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import java.awt.Shape;
import java.awt.geom.Path2D;

public class debris
  extends Participant
{
  private Shape outline;
  
  public debris(double x, double y, double size)
  {
    double noise = Constants.RANDOM.nextDouble() * 10.0 - 5.0;
    
    Path2D.Double line = new Path2D.Double();
    line.moveTo(0.0, -size / 2.0);
    line.lineTo(0.0, size / 2.0);
    
    setRotation(2 * Math.PI * Constants.RANDOM.nextDouble());
    setPosition(x + noise, y + noise);
    setVelocity(Constants.RANDOM.nextDouble(), Constants.RANDOM.nextDouble() * 2.0 * Math.PI);
    
    this.outline = line;
    
    new ParticipantCountdownTimer(this, this, 1500 + (int)(Constants.RANDOM.nextDouble() * 500.0));
  }
  
  protected Shape getOutline()
  {
    return this.outline;
  }
  
  public void countdownComplete(Object payload)
  {
    Participant.expire(this);
  }
  
  public void collidedWith(Participant p) {}
}
