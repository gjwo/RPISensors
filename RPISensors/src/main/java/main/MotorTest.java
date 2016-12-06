package main;

import com.pi4j.io.gpio.*;
import devices.driveAssembly.DriveAssembly;
import devices.driveAssembly.TankDriveAssembly;
import devices.motors.DCMotor;
import devices.motors.Motor;

/**
 * RPISensors - main
 * Created by MAWood on 03/12/2016.
 */
public class MotorTest
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

        Motor left = new DCMotor(LA,LB);
        Motor right = new DCMotor(RA,RB);

        DriveAssembly DA = new TankDriveAssembly(left,right);
        Thread.sleep(100);
        DA.setDirection(0f);
        DA.setSpeed(1f);
        Thread.sleep(1000);
        DA.stop();
    }
}
