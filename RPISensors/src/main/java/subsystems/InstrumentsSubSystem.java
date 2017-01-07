package subsystems;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import deviceHardwareAbstractionLayer.Pi4jI2CDevice;
import inertialNavigation.Navigate;
import sensors.Implementations.MPU9250.MPU9250;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public class InstrumentsSubSystem extends SubSystem
{
    private Navigate nav;
    private I2CBus bus;
    private MPU9250 mpu9250;
    private Thread navThread;
    private Thread mpuThread;
    private static final int SENSOR_DEBUG_LEVEL = 1;
    private static final int NAVIGATE_DEBUG_LEVEL = 0;

    public InstrumentsSubSystem()
    {
        super(SubSystem.SubSystemType.INSTRUMENTS);
    }

    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);
        try
        {
            bus = I2CFactory.getInstance(I2CBus.BUS_1);

            mpu9250 = new MPU9250(
                    new Pi4jI2CDevice(bus.getDevice(0x68)), // MPU9250 device device
                    new Pi4jI2CDevice(bus.getDevice(0x0C)), // ak8963 device
                    200,                                    // sample rate (SR) per second
                    250                                    // sample size (SS)
            ); 					// debug level
            nav = new Navigate(mpu9250, NAVIGATE_DEBUG_LEVEL);
            mpuThread = new Thread(mpu9250);
            navThread = new Thread(nav);

            mpuThread.start();
            navThread.start();

            this.setSubSysState(SubSystemState.RUNNING);
        } catch (I2CFactory.UnsupportedBusNumberException | IOException | InterruptedException e)
        {
        	this.setSubSysState(SubSystemState.ERROR);
            e.printStackTrace();
        }
        return this.getSubSysState();
    }

    @Override
    public SubSystemState shutdown()
    {
        try
        {
            if(this.getSubSysState() != SubSystemState.RUNNING) return this.getSubSysState();
        	this.setSubSysState(SubSystemState.STOPPING);
            navThread.interrupt();
            TimeUnit.SECONDS.sleep(1);
            nav.shutdown();
            mpuThread.interrupt();
            TimeUnit.SECONDS.sleep(2);
            bus.close();
            this.setSubSysState(SubSystemState.IDLE);
        } catch (InterruptedException | IOException e)
        {
        	this.setSubSysState(SubSystemState.ERROR);
            e.printStackTrace();
        }
        return this.getSubSysState();
    }
}
