package devices.motors;

/**
 * Created by GJWood on 26/01/2017.
 */
public interface AngularPositioner
{
    public void calibrateAngularPosition(float angle);
    public float getAngularPosition();
    public void setAngularPosition(float angle);
    public float angularPositionResolution();
}
