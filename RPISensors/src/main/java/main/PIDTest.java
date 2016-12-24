package main;

import com.pi4j.io.gpio.*;
import devices.driveAssembly.DriveAssembly;
import devices.driveAssembly.EncoderFeedbackPIDControlledDriveAssembly;
import devices.driveAssembly.TankDriveAssembly;
import devices.encoder.Encoder;
import devices.motors.DCMotor;
import devices.motors.EncoderFeedbackMotor;
import devices.motors.Motor;

import java.util.concurrent.TimeUnit;

/**
 * RPISensors - main
 * Created by MAWood on 23/12/2016.
 */
public class PIDTest
{
    private final Encoder leftEncoder;
    private final Encoder rightEncoder;

    private static final double KP = 0.1;
    private static final double KI = 0.25;
    private static final double KD = 0.3;

    private static final float SAMPLE_RATE = 20;

    private final DriveAssembly da;

    private PIDTest() throws InterruptedException
    {
        leftEncoder = new Encoder(RaspiPin.GPIO_14,RaspiPin.GPIO_13,"LH",1d/427.5d, false);
        rightEncoder = new Encoder(RaspiPin.GPIO_01,RaspiPin.GPIO_00,"RH",1d/427.5d, false);

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput RA =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Right motor A", PinState.LOW);
        final GpioPinDigitalOutput RB =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Right motor B", PinState.LOW);
        final GpioPinDigitalOutput LA =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Left motor A", PinState.LOW);
        final GpioPinDigitalOutput LB =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Left motor B", PinState.LOW);
        Motor left = new DCMotor(LA,LB);
        Motor right = new DCMotor(RA,RB);

        System.out.println("Set Point,Input,Output/2");

        Motor leftEncodedMotor = new EncoderFeedbackMotor(leftEncoder,left,KP,KI,KD,SAMPLE_RATE,true);
        Motor rightEncodedMotor = new EncoderFeedbackMotor(rightEncoder,right,KP,KI,KD,SAMPLE_RATE,false, true);

        da = new TankDriveAssembly(leftEncodedMotor,rightEncodedMotor);


        da.setDirection(180);

        da.setSpeed(0.4f);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Directions, left: " + leftEncoder.getDirection().name() + " right: " + rightEncoder.getDirection());
        da.setSpeed(0.1f);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Directions, left: " + leftEncoder.getDirection().name() + " right: " + rightEncoder.getDirection());
        /*da.setSpeed(0.2f);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Directions, left: " + leftEncoder.getDirection().name() + " right: " + rightEncoder.getDirection());
        da.setSpeed(0.1f);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Directions, left: " + leftEncoder.getDirection().name() + " right: " + rightEncoder.getDirection());
        */da.setSpeed(0f);

        da.stop();
    }

    public static void main(String[] args) throws InterruptedException
    {

        /*final GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput a = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "1", PinPullResistance.PULL_DOWN);
        GpioPinDigitalInput b = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "2", PinPullResistance.PULL_DOWN);

        while(true)
        {
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("A: " + a.isHigh() + " B: " + b.isHigh());
        }*/

        new PIDTest();
    }
}
