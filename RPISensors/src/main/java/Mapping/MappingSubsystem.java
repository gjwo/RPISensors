package Mapping;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import deviceHardwareAbstractionLayer.Device;
import deviceHardwareAbstractionLayer.Pi4jI2CDevice;
import devices.motors.AngularPositioner;
import devices.motors.StepperMotor;
import sensors.Implementations.VL53L0X.VL53L0X;
import subsystems.SubSystem;
import subsystems.SubSystemState;

import java.io.IOException;

/**
 * MappingSubsystem
 * Created by GJWood on 27/01/2017.
 */
public class MappingSubsystem extends SubSystem
{
    private AngularPositioner angularPositioner;
    private VL53L0X ranger;
    private RangeScanner rangeScanner;
    private I2CBus bus;
    private Device rangerDevice;

    /**
     * MappingSubsystem -   Constructor
     */
    protected MappingSubsystem()
    {
        super(SubSystemType.MAPPING);
    }

    // SubSystem interface methods
    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);
        try
        {
            bus = I2CFactory.getInstance(I2CBus.BUS_1);
            rangerDevice = new Pi4jI2CDevice(bus.getDevice(0x29));
        } catch (I2CFactory.UnsupportedBusNumberException | IOException e)
        {
            e.printStackTrace();
        }
        ranger = new VL53L0X(rangerDevice,10,100);
        angularPositioner = new StepperMotor();
        rangeScanner = new RangeScanner(angularPositioner,ranger,60);
        this.setSubSysState(SubSystemState.RUNNING);
        return this.getSubSysState();

    }

    @Override
    public SubSystemState shutdown()
    {
        if(this.getSubSysState() != SubSystemState.RUNNING) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STOPPING);
        rangeScanner.unbind();
        rangeScanner = null;
        this.setSubSysState(SubSystemState.IDLE);
        return this.getSubSysState();
    }
}
