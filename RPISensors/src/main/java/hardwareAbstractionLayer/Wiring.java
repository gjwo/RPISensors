package hardwareAbstractionLayer;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import logging.SystemLog;
import subsystems.SubSystem;

import java.io.IOException;
import java.util.HashMap;

/**
 * Wiring           -   a place to keep all the GPIO pin allocations
 * Created by GJWood on 29/01/2017.
 */
public class Wiring
{
    private final static GpioController gpio;
    private final static I2CBus i2CBus1;
    private final static HashMap<Pin,GpioPinDigital> pinMap;

    static  //static initialisation code
    {
        gpio = GpioFactory.getInstance();
        pinMap = new HashMap<>(30);
        pinMap.put(RaspiPin.GPIO_06,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06,"Positioner Pin 1", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_07,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"Positioner Pin 2", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_25,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25,"Positioner Pin 3", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_27,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27,"Positioner Pin 4", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_02,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Right motor A", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_03,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Right motor B", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_05,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Right motor A", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_04,gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Right motor B", PinState.LOW));
        pinMap.put(RaspiPin.GPIO_14,gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, "LH1", PinPullResistance.PULL_DOWN));
        pinMap.put(RaspiPin.GPIO_15,gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "LH2", PinPullResistance.PULL_DOWN));
        pinMap.put(RaspiPin.GPIO_01,gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "RH1", PinPullResistance.PULL_DOWN));
        pinMap.put(RaspiPin.GPIO_26,gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "RH2", PinPullResistance.PULL_DOWN));
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
        pins[0] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_02);
        pins[1] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_03);
        return pins;
    }

    public static GpioPinDigitalOutput[] getRightMainMotorPins()
    {
        GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[2];
        pins[0] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_05);
        pins[1] = (GpioPinDigitalOutput) pinMap.get(RaspiPin.GPIO_04);
        return pins;
    }

    public static GpioPinDigitalInput[] getLeftMainMotorEncoderPins()
    {
        GpioPinDigitalInput[] pins = new GpioPinDigitalInput[2];
        pins[0] = (GpioPinDigitalInput) pinMap.get(RaspiPin.GPIO_14);
        pins[1] = (GpioPinDigitalInput) pinMap.get(RaspiPin.GPIO_15);
        return pins;
    }

    public static GpioPinDigitalInput[] getRightMainMotorEncoderPins()
    {
        GpioPinDigitalInput[] pins = new GpioPinDigitalInput[2];
        pins[0] = (GpioPinDigitalInput) pinMap.get(RaspiPin.GPIO_01);
        pins[1] = (GpioPinDigitalInput) pinMap.get(RaspiPin.GPIO_26);
        return pins;
    }

    public static void logGpioPinAllocation()
    {
        for (Pin p:pinMap.keySet())
        {
            SystemLog.log(SubSystem.SubSystemType.SUBSYSTEM_MANAGER,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,
                    pinMap.get(p).toString());
        }
    }
}