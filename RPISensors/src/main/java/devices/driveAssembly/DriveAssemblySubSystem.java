package devices.driveAssembly;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import hardwareAbstractionLayer.Wiring;
import devices.motors.Encoder;
import devices.motors.DCMotor;
import devices.motors.EncoderFeedbackMotor;
import devices.motors.Motor;
import subsystems.SubSystem;
import subsystems.SubSystemState;

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
        super(SubSystemType.DRIVE_ASSEMBLY);

        Encoder leftEncoder = new Encoder(Wiring.getLeftMainMotorEncoderPins(),1d/427.5d, false);
        Encoder rightEncoder = new Encoder(Wiring.getRightMainMotorEncoderPins(),1d/427.5d, false);

        GpioPinDigitalOutput[] leftPins = Wiring.getLeftMainMotorPins();
        Motor left = new DCMotor(leftPins[0], leftPins[1]);
        GpioPinDigitalOutput[] rightPins = Wiring.getRightMainMotorPins();
        Motor right = new DCMotor(rightPins[0], rightPins[1]);

        Motor leftEncodedMotor = new EncoderFeedbackMotor(leftEncoder,left,KP,KI,KD,SAMPLE_RATE,true);
        Motor rightEncodedMotor = new EncoderFeedbackMotor(rightEncoder,right,KP,KI,KD,SAMPLE_RATE,false);

        driveAssembly = new TankDriveAssembly(leftEncodedMotor,rightEncodedMotor);
    }

    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);
        remoteDriveAssembly = new RemoteDriveAssemblyImpl(driveAssembly);
        this.setSubSysState(SubSystemState.RUNNING);
        return this.getSubSysState();
    }

    @Override
    public SubSystemState shutdown()
    {
        if(this.getSubSysState() != SubSystemState.RUNNING) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STOPPING);
        remoteDriveAssembly.unbind();
        remoteDriveAssembly = null;
        this.setSubSysState(SubSystemState.IDLE);
        return this.getSubSysState();
    }
}
