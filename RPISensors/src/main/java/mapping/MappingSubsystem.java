package mapping;

import com.pi4j.io.gpio.*;
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
@SuppressWarnings("FieldCanBeLocal")
public class MappingSubsystem extends SubSystem
{
    private final int BY48_STEPPER_CYCLES_PER_ROTATION = 512;
    private AngularPositioner angularPositioner;
    private VL53L0X ranger;
    private RangeScanner rangeScanner;
    private I2CBus bus;
    private Device rangerDevice;
    private GpioPinDigitalOutput[] positionerPins;
    private final GpioController gpio;

    /**
     * MappingSubsystem -   Constructor
     */
    public MappingSubsystem()
    {
        super(SubSystemType.MAPPING);
        gpio = GpioFactory.getInstance();
    }

    // SubSystem interface methods
    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);

        // set up the ranger in this case a VL3LOX on the IC2 bus
        try
        {
            bus = I2CFactory.getInstance(I2CBus.BUS_1);
            rangerDevice = new Pi4jI2CDevice(bus.getDevice(0x29));
        } catch (I2CFactory.UnsupportedBusNumberException | IOException e)
        {
            e.printStackTrace();
        }
        ranger = new VL53L0X(rangerDevice,10,100);

        // set up the positioner, in this case a GPIO controlled BY48 stepper motor
        positionerPins = new GpioPinDigitalOutput[4];
        positionerPins[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06,"Positioner Pin 1");
        positionerPins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"Positioner Pin 2");
        positionerPins[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08,"Positioner Pin 3");
        positionerPins[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09,"Positioner Pin 4");
        angularPositioner = new StepperMotor(positionerPins,BY48_STEPPER_CYCLES_PER_ROTATION);

        // initialise the range scanner with the two devices
        rangeScanner = new RangeScanner(angularPositioner,ranger,60); //scan at 1 rotation per second

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
