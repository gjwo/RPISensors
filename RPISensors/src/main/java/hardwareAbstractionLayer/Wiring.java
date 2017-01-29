package hardwareAbstractionLayer;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import logging.SystemLog;
import subsystems.SubSystem;

import java.io.IOException;

/**
 * Wiring           -   a place to keep all the GPIO pin allocations
 * Created by GJWood on 29/01/2017.
 */
public class Wiring
{
    private final static GpioController gpio;
    private final static I2CBus i2CBus1;

    static  //static initialisation code
    {
        gpio = GpioFactory.getInstance();
        try
        {
            i2CBus1 = I2CFactory.getInstance(I2CBus.BUS_1);
            //I2CBus1 has the following GPIO pin outs under Pi4J
            //GPIO 8 = SDA1 physical pin 3
            //GPIO 9 = SCL1 physical pin 5
        }catch(final Exception e){throw new RuntimeException("Failed to create I2C Bus1 in Wiring.",e); }
    }

    public static I2CBus getI2CBus1(){return i2CBus1;}

    public static void closeI2CBus1()
    {
        try {
            i2CBus1.close();
        } catch (IOException e) {
            SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.ERROR, "IO exception whilst closing i2CBus1");
            // ignore has already been closed!
        }
    }

    public static GpioPinDigitalOutput[] getPositionerPins()
    {
        GpioPinDigitalOutput[] positionerPins = new GpioPinDigitalOutput[4];
        positionerPins[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06,"Positioner Pin 1");
        positionerPins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"Positioner Pin 2");
        positionerPins[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25,"Positioner Pin 3");
        positionerPins[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27,"Positioner Pin 4");
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