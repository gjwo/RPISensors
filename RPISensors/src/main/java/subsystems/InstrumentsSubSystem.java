package subsystems;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import devices.I2C.Pi4jI2CDevice;
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
        super();
    }

    @Override
    public SubSystemState startup()
    {
        if(this.getCurrentState() != SubSystemState.IDLE) return this.getCurrentState();
        state = SubSystemState.STARTING;
        try
        {
            bus = I2CFactory.getInstance(I2CBus.BUS_1);

            mpu9250 = new MPU9250(
                    new Pi4jI2CDevice(bus.getDevice(0x68)), // MPU9250 I2C device
                    new Pi4jI2CDevice(bus.getDevice(0x0C)), // ak8963 I2C
                    200,                                    // sample rate (SR) per second
                    250,									// sample size (SS)
                    SENSOR_DEBUG_LEVEL); 					// debug level
            nav = new Navigate(mpu9250, NAVIGATE_DEBUG_LEVEL);
            mpuThread = new Thread(mpu9250);
            navThread = new Thread(nav);

            mpuThread.start();
            navThread.start();

            state = SubSystemState.RUNNING;
        } catch (I2CFactory.UnsupportedBusNumberException | IOException | InterruptedException e)
        {
            state = SubSystemState.ERROR;
            e.printStackTrace();
        }
        return this.getCurrentState();
    }

    @Override
    public SubSystemState shutdown()
    {
        try
        {
            state = SubSystemState.STOPPING;
            if(this.getCurrentState() != SubSystemState.RUNNING) return this.getCurrentState();
            navThread.interrupt();
            TimeUnit.SECONDS.sleep(1);
            nav.shutdown();
            mpuThread.interrupt();
            TimeUnit.SECONDS.sleep(2);
            bus.close();
        } catch (InterruptedException | IOException e)
        {
            state = SubSystemState.ERROR;
            e.printStackTrace();
        }
        state = SubSystemState.IDLE;
        return this.getCurrentState();
    }
}
