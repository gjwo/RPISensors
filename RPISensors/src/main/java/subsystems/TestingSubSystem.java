package subsystems;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import devices.I2C.I2CImplementation;
import devices.I2C.Pi4jI2CDevice;
import sensors.Implementations.VL53L0X.VL53L0X;
import sensors.Implementations.VL53L0X.VL53L0XRanger;
import sensors.interfaces.UpdateListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * RPISensors - subsystems
 * Created by MAWood on 27/12/2016.
 */
public class TestingSubSystem extends SubSystem implements UpdateListener
{
    private VL53L0X vl53L0X;
    private Thread thread;
    public TestingSubSystem()
    {
        super(SubSystemType.TESTING);
        try
        {
            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
            I2CImplementation i2CImplementation = new Pi4jI2CDevice(bus.getDevice(0x29));
            vl53L0X = new VL53L0X(i2CImplementation,10,100);
            vl53L0X.registerInterest(this);
        } catch (I2CFactory.UnsupportedBusNumberException | IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public SubSystemState startup()
    {
        if(getSubSysState() != SubSystemState.IDLE) return getSubSysState();
        setSubSysState(SubSystemState.STARTING);
        thread = new Thread(vl53L0X);
        thread.start();
        setSubSysState(SubSystemState.RUNNING);
        return getSubSysState();
    }

    @Override
    public SubSystemState shutdown()
    {
        if(getSubSysState() != SubSystemState.RUNNING) return getSubSysState();
        setSubSysState(SubSystemState.STOPPING);
        thread.interrupt();
        setSubSysState(SubSystemState.IDLE);
        return getSubSysState();
    }

    @Override
    public void dataUpdated()
    {
        System.out.println("Latest: " + vl53L0X.getLatestRange().getX());
    }
}
