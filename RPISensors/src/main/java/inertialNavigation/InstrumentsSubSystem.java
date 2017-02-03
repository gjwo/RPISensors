package inertialNavigation;

import com.pi4j.io.i2c.I2CBus;
import hardwareAbstractionLayer.Pi4jI2CDevice;
import hardwareAbstractionLayer.Wiring;
import sensors.Implementations.MPU9250.MPU9250;
import subsystems.SubSystem;
import subsystems.SubSystemState;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public class InstrumentsSubSystem extends SubSystem
{
    private Navigate nav;
    private I2CBus i2CBus1;
    private MPU9250 mpu9250;
    private Thread navThread;
    private Thread mpuThread;
    private static final int SENSOR_DEBUG_LEVEL = 1;
    private static final int NAVIGATE_DEBUG_LEVEL = 0;

    public InstrumentsSubSystem()
    {
        super(SubSystemType.INSTRUMENTS);
    }

    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);
        try
        {
            i2CBus1 = Wiring.getI2CBus1();

            mpu9250 = new MPU9250(
                    new Pi4jI2CDevice(i2CBus1.getDevice(0x68)), // MPU9250 device device
                    new Pi4jI2CDevice(i2CBus1.getDevice(0x0C)), // ak8963 device
                    200,                                    // sample rate (SR) per second
                    250                                    // sample size (SS)
            ); 					// debug level
            nav = new Navigate(mpu9250);
            mpuThread = new Thread(mpu9250);
            navThread = new Thread(nav);

            mpuThread.start();
            navThread.start();

            this.setSubSysState(SubSystemState.RUNNING);
        } catch (IOException | InterruptedException e)
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
            //TimeUnit.SECONDS.sleep(2);
            //i2CBus1.close();
            this.setSubSysState(SubSystemState.IDLE);
            Wiring.closeI2CBus1();
        } catch (InterruptedException e)
        {
        	this.setSubSysState(SubSystemState.ERROR);
            e.printStackTrace();
        }
        return this.getSubSysState();
    }
}
