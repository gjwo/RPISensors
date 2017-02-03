package mapping;

import com.pi4j.io.i2c.I2CBus;
import hardwareAbstractionLayer.Device;
import hardwareAbstractionLayer.Pi4jI2CDevice;
import hardwareAbstractionLayer.Wiring;
import devices.motors.AngularPositioner;
import devices.motors.StepperMotor;
import logging.SystemLog;
import sensors.Implementations.VL53L0X.VL53L0X;
import subsystems.SubSystem;
import subsystems.SubSystemState;

import java.io.IOException;

/**
 * MappingSubsystem
 * Created by GJWood on 27/01/2017.
 */
@SuppressWarnings("FieldCanBeLocal")
public class MappingSubsystem extends SubSystem
{
    private final int BY48_STEPPER_CYCLES_PER_ROTATION = 512;
    private AngularPositioner angularPositioner;
    private VL53L0X ranger;
    private Thread rangerThread;
    private RangeScanner rangeScanner;
    private I2CBus i2CBus1;
    private Device rangerDevice;

    /**
     * MappingSubsystem -   Constructor
     */
    public MappingSubsystem()
    {
        super(SubSystemType.MAPPING);
    }

    // SubSystem interface methods
    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);

        // set up the ranger in this case a VL3LOX on the IC2 i2CBus1
        try
        {
            i2CBus1 = Wiring.getI2CBus1();
            rangerDevice = new Pi4jI2CDevice(i2CBus1.getDevice(0x29));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        ranger = new VL53L0X(rangerDevice,10,100);
        rangerThread = new Thread(ranger);
        rangerThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES,"Ranger initialised");
        // set up the positioner, in this case a GPIO controlled BY48 stepper motor

        angularPositioner = new StepperMotor(Wiring.getPositionerPins(),BY48_STEPPER_CYCLES_PER_ROTATION);
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES,"Positioner initialised");

        // initialise the range scanner with the two devices
        rangeScanner = new RangeScanner(angularPositioner,ranger,12); //scan at 1 rotation in 5 seconds

        this.setSubSysState(SubSystemState.RUNNING);
        return this.getSubSysState();

    }

    @Override
    public SubSystemState shutdown()
    {
        if(this.getSubSysState() != SubSystemState.RUNNING) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STOPPING);
        rangerThread.interrupt();
        rangeScanner.unbind();
        rangeScanner = null;
        this.setSubSysState(SubSystemState.IDLE);
        return this.getSubSysState();
    }
}
