package hardwareAbstractionLayer;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * RPISensors
 * Created by Matthew Wood on 30/03/2017.
 */
class PinAssignments
{
    static final Pin MOTOR_LEFT_A = RaspiPin.GPIO_22;
    static final Pin MOTOR_LEFT_B = RaspiPin.GPIO_23;
    static final Pin MOTOR_RIGHT_A = RaspiPin.GPIO_24;
    static final Pin MOTOR_RIGHT_B = RaspiPin.GPIO_25;

    static final Pin ENCODER_LEFT_A = RaspiPin.GPIO_27;
    static final Pin ENCODER_LEFT_B = RaspiPin.GPIO_28;
    static final Pin ENCODER_RIGHT_A = RaspiPin.GPIO_04;
    static final Pin ENCODER_RIGHT_B = RaspiPin.GPIO_05;
}
