package subsystems;

import com.pi4j.io.gpio.*;
import devices.driveAssembly.DriveAssembly;
import devices.driveAssembly.RemoteDriveAssembly;
import devices.driveAssembly.RemoteDriveAssemblyImpl;
import devices.driveAssembly.TankDriveAssembly;
import devices.encoder.Encoder;
import devices.motors.DCMotor;
import devices.motors.EncoderFeedbackMotor;
import devices.motors.Motor;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public class DriveAssemblySubSystem extends SubSystem
{

    private RemoteDriveAssemblyImpl remoteDriveAssembly;
    private final DriveAssembly driveAssembly;

    private static final double KP = 0.1;
    private static final double KI = 0.25;
    private static final double KD = 0.3;

    private static final float SAMPLE_RATE = 20;

    public DriveAssemblySubSystem()
    {
        super();


        Encoder leftEncoder = new Encoder(RaspiPin.GPIO_14,RaspiPin.GPIO_13,"LH",1d/427.5d, false);
        Encoder rightEncoder = new Encoder(RaspiPin.GPIO_01,RaspiPin.GPIO_26,"RH",1d/427.5d, false);

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

        Motor leftEncodedMotor = new EncoderFeedbackMotor(leftEncoder,left,KP,KI,KD,SAMPLE_RATE,true);
        Motor rightEncodedMotor = new EncoderFeedbackMotor(rightEncoder,right,KP,KI,KD,SAMPLE_RATE,false);

        driveAssembly = new TankDriveAssembly(leftEncodedMotor,rightEncodedMotor);
    }

    @Override
    public SubSystemState startup()
    {
        if(this.getCurrentState() != SubSystemState.IDLE) return this.getCurrentState();
        this.state = SubSystemState.STARTING;
        remoteDriveAssembly = new RemoteDriveAssemblyImpl(driveAssembly);
        this.state = SubSystemState.RUNNING;
        return this.getCurrentState();
    }

    @Override
    public SubSystemState shutdown()
    {
        if(this.getCurrentState() != SubSystemState.RUNNING) return this.getCurrentState();
        this.state = SubSystemState.STOPPING;
        remoteDriveAssembly.unbind();
        this.state = SubSystemState.IDLE;
        return this.getCurrentState();
    }
}
