package hardwareAbstractionLayer;

import com.pi4j.io.gpio.*;

/**
 * Wiring           -   a place to keep all the GPIO pin allocations
 * Created by GJWood on 29/01/2017.
 */
public class Wiring
{
    private final static GpioController gpio = GpioFactory.getInstance();

    public static GpioPinDigitalOutput[] getPositionerPins()
    {
        GpioPinDigitalOutput[] positionerPins = new GpioPinDigitalOutput[4];
        positionerPins[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06,"Positioner Pin 1");
        positionerPins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"Positioner Pin 2");
        positionerPins[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08,"Positioner Pin 3");
        positionerPins[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09,"Positioner Pin 4");
        return positionerPins;
    }
    public static GpioPinDigitalOutput[] getLeftMainMotorPins()
    {
        GpioPinDigitalOutput[] motorPins = new GpioPinDigitalOutput[2];
        motorPins[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Right motor A", PinState.LOW);
        motorPins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Right motor B", PinState.LOW);
        return motorPins;
    }

    public static GpioPinDigitalOutput[] getRightMainMotorPins()
    {
        GpioPinDigitalOutput[] motorPins = new GpioPinDigitalOutput[2];
        motorPins[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Right motor A", PinState.LOW);
        motorPins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Right motor B", PinState.LOW);
        return motorPins;
    }

    public static GpioPinDigitalInput[] getLeftMainMotorEncoderPins()
    {
        GpioPinDigitalInput[] encoderPins = new GpioPinDigitalInput[2];
        encoderPins[0]= gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, "LH"+"1", PinPullResistance.PULL_DOWN);
        encoderPins[1]= gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, "LH"+"2", PinPullResistance.PULL_DOWN);
        return encoderPins;
    }

    public static GpioPinDigitalInput[] getRightMainMotorEncoderPins()
    {
        GpioPinDigitalInput[] encoderPins = new GpioPinDigitalInput[2];
        encoderPins[0]= gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "RH"+"1", PinPullResistance.PULL_DOWN);
        encoderPins[1]= gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "RH"+"2", PinPullResistance.PULL_DOWN);
        return encoderPins;
    }
}