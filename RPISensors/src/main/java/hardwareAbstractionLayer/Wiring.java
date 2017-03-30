package hardwareAbstractionLayer;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import logging.SystemLog;

import java.io.IOException;
import java.util.*;

import static hardwareAbstractionLayer.PinAssignments.*;

/**
 * Wiring           -   a place to keep all the GPIO pin allocations
 * Created by GJWood on 29/01/2017.
 */
public class Wiring
{
    private final static GpioController gpio;
    private static I2CBus i2CBus1;
    private final static TreeMap<Pin,GpioPinDigital> pinMap;
    private static boolean i2cDevices;

    static  //static initialisation code
    {
        i2cDevices  = false;
        i2CBus1 = null;
        gpio = GpioFactory.getInstance();
        pinMap = new TreeMap<>();
        pinMap.put(RaspiPin.GPIO_06,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06,"Positioner Pin 1", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_07,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"Positioner Pin 2", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_25,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25,"Positioner Pin 3", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_27,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27,"Positioner Pin 4", PinState.LOW));
        pinMap.put(MOTOR_LEFT_A,gpio.provisionDigitalOutputPin(MOTOR_LEFT_A, "Left motor A", PinState.LOW));
        pinMap.put(MOTOR_LEFT_B,gpio.provisionDigitalOutputPin(MOTOR_LEFT_B, "Left motor B", PinState.LOW));
        pinMap.put(MOTOR_RIGHT_A,gpio.provisionDigitalOutputPin(MOTOR_RIGHT_A, "Right motor A", PinState.LOW));
        pinMap.put(MOTOR_RIGHT_B,gpio.provisionDigitalOutputPin(MOTOR_RIGHT_B, "Right motor B", PinState.LOW));
        pinMap.put(ENCODER_LEFT_A,gpio.provisionDigitalInputPin(ENCODER_LEFT_A, "Left Encoder 1", PinPullResistance.PULL_DOWN));
        pinMap.put(ENCODER_LEFT_B,gpio.provisionDigitalInputPin(ENCODER_LEFT_B, "Left Encoder 2", PinPullResistance.PULL_DOWN));
        pinMap.put(ENCODER_RIGHT_A,gpio.provisionDigitalInputPin(ENCODER_RIGHT_A, "Right Encoder 1", PinPullResistance.PULL_DOWN));
        pinMap.put(ENCODER_RIGHT_B,gpio.provisionDigitalInputPin(ENCODER_RIGHT_B, "Right Encoder 2", PinPullResistance.PULL_DOWN));
    }

    public static I2CBus getI2CBus1(){return i2CBus1;}

    public static void initialialseI2CBus1()
    {
        try
        {
            i2CBus1 = I2CFactory.getInstance(I2CBus.BUS_1);
            //I2CBus1 has the following GPIO pin outs under Pi4J
            //GPIO 8 = SDA1 physical pin 3
            //GPIO 9 = SCL1 physical pin 5
        } catch (final Exception e)
        {
            //throw new RuntimeException("Failed to create I2C Bus1 in Wiring.",e);
            SystemLog.log(Wiring.class, SystemLog.LogLevel.ERROR, "Failed to create I2C Bus1");
            i2cDevices = false;
        }
        SystemLog.log(Wiring.class, SystemLog.LogLevel.TRACE_INTERNAL_METHODS, "I2C Bus1 initialised");
    }

    public static boolean thereAreI2cDevices() {return i2cDevices;}
    public static void setI2Cdevices(boolean b) {i2cDevices = b;}

    public static void closeI2CBus1()
    {
        try {
            i2CBus1.close();
        } catch (IOException e) {
            SystemLog.log(Wiring.class,SystemLog.LogLevel.ERROR, "IO exception whilst closing i2CBus1");
            // ignore has already been closed!
        }
    }


    public static GpioPinDigitalOutput[] getPositionerPins()
    {
        GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[4];
        pins[0] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_06);
        pins[1] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_07);
        pins[2] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_25);
        pins[3] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_27);
        return pins;
    }
    public static GpioPinDigitalOutput[] getLeftMainMotorPins()
    {
        GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[2];
        pins[0] = (GpioPinDigitalOutput) pinMap.get(MOTOR_LEFT_A);
        pins[1] = (GpioPinDigitalOutput) pinMap.get(MOTOR_LEFT_B);
        return pins;
    }

    public static GpioPinDigitalOutput[] getRightMainMotorPins()
    {
        GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[2];
        pins[0] = (GpioPinDigitalOutput) pinMap.get(MOTOR_RIGHT_A);
        pins[1] = (GpioPinDigitalOutput) pinMap.get(MOTOR_RIGHT_B);
        return pins;
    }

    public static GpioPinDigitalInput[] getLeftMainMotorEncoderPins()
    {
        GpioPinDigitalInput[] pins = new GpioPinDigitalInput[2];
        pins[0] = (GpioPinDigitalInput) pinMap.get(ENCODER_LEFT_A);
        pins[1] = (GpioPinDigitalInput) pinMap.get(ENCODER_LEFT_B);
        return pins;
    }

    public static GpioPinDigitalInput[] getRightMainMotorEncoderPins()
    {
        GpioPinDigitalInput[] pins = new GpioPinDigitalInput[2];
        pins[0] = (GpioPinDigitalInput) pinMap.get(ENCODER_RIGHT_A);
        pins[1] = (GpioPinDigitalInput) pinMap.get(ENCODER_RIGHT_B);
        return pins;
    }

    public static void logGpioPinAllocation()
    {
        SystemLog.log(Wiring.class,SystemLog.LogLevel.TRACE_INTERNAL_METHODS,
                "I2C1 bus on SDA on GPIO03 and SCL on GPIO05 ");
        for (Pin p:pinMap.keySet())
        {
            SystemLog.log(Wiring.class,SystemLog.LogLevel.TRACE_INTERNAL_METHODS,
                    pinMap.get(p).toString());
        }
    }
}