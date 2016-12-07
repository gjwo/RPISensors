package main;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import devices.driveAssembly.DriveAssembly;
import devices.driveAssembly.TankDriveAssembly;
import devices.motors.DCMotor;
import devices.motors.Motor;

/**
 * RPISensors - main
 * Created by MAWood on 03/12/2016.
 */
public class MotorTest extends Thread
{

    public static void main(String[] args) throws InterruptedException
    {
        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput RA =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Right motor A", PinState.LOW);
        final GpioPinDigitalOutput RB =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Right motor B", PinState.LOW);
        final GpioPinDigitalOutput LA =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Left motor A", PinState.LOW);
        final GpioPinDigitalOutput LB =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Left motor B", PinState.LOW);

        final GpioPinDigitalInput RH1 =
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, "RH1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput RH2 =
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, "RH2", PinPullResistance.PULL_DOWN);

        RH1.setShutdownOptions(true);
        RH2.setShutdownOptions(true);

        RH1.addListener((GpioPinListenerDigital) event ->
        {
            // display pin state on console
            System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
        RH2.addListener((GpioPinListenerDigital) event ->
        {
            // display pin state on console
            System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });

        Motor left = new DCMotor(LA,LB);
        Motor right = new DCMotor(RA,RB);

        DriveAssembly driveAssembly = new TankDriveAssembly(left,right);


    }
}
