package Scanner;

import devices.motors.AngularPositioner;
import sensors.interfaces.Ranger;

/**
 * RangeScanner -   This class combines a stepper motor and a ranger to generate a 360 degree view
 *                  of the surrounding environment
 *
 * Created by GJWood on 26/01/2017.
 */
public class RangeScanner
{
    private final AngularPositioner angularPositioner;
    private final Ranger ranger;
    RangeScanner(AngularPositioner ap, Ranger r)
    {
        angularPositioner =  ap;
        ranger = r;
    }
}
