package subsystems;

import com.pi4j.io.gpio.*;
import devices.driveAssembly.DriveAssembly;
import devices.driveAssembly.RemoteDriveAssembly;
import devices.driveAssembly.RemoteDriveAssemblyImpl;
import devices.driveAssembly.TankDriveAssembly;
import devices.motors.DCMotor;
import devices.motors.Motor;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public class DriveAssemblySubSystem extends SubSystem
{

    private RemoteDriveAssemblyImpl remoteDriveAssembly;
    private final DriveAssembly driveAssembly;

    public DriveAssemblySubSystem()
    {
        super();

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

        driveAssembly = new TankDriveAssembly(left,right);
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
