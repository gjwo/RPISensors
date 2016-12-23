package devices.motors;

import devices.controller.PIDControlled;

/**
 * RPITank
 * Created by MAWood on 02/07/2016.
 */
public interface Motor extends PIDControlled
{
    void setOutput(float speed);
    float getSpeed();

    void stop();
}
