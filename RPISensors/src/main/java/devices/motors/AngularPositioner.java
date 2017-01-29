package devices.motors;

/**
 * AngularPositioner    -   Abstract version of a stepper like device
 * Created by GJWood on 26/01/2017.
 */
public interface AngularPositioner
{
    void calibrateAngularPosition(float angle);
    float getAngularPosition();
    void setAngularPosition(float angle);
    float angularPositionResolution();
}
