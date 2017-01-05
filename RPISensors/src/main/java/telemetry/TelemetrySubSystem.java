package telemetry;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import devices.I2C.I2CImplementation;
import devices.I2C.Pi4jI2CDevice;
import sensors.Implementations.INA219.INA219;
import sensors.interfaces.UpdateListener;
import subsystems.SubSystem;
import subsystems.SubSystemState;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * RPISensors - subsystems
 * Created by MAWood on 21/12/2016.
 */
public class TelemetrySubSystem extends SubSystem implements UpdateListener
{
    private I2CBus bus;
    private Thread telemetryThread;
    private INA219 ina219;
    private Telemetry telemetry;

    public TelemetrySubSystem()
    {
        super(SubSystem.SubSystemType.TELEMETRY);
        try
        {
            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
            I2CImplementation i2CImplementation = new Pi4jI2CDevice(bus.getDevice(0x40));
            ina219 = new INA219(i2CImplementation, 10, 100);
            ina219.registerInterest(this);
            telemetry = new Telemetry();
        } catch (I2CFactory.UnsupportedBusNumberException | IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);

        telemetryThread = new Thread(ina219);

        telemetryThread.start();

        this.setSubSysState(SubSystemState.RUNNING);
        return this.getSubSysState();
    }

    @Override
    public SubSystemState shutdown()
    {
        try
        {
            if(this.getSubSysState() != SubSystemState.RUNNING) return this.getSubSysState();
        	this.setSubSysState(SubSystemState.STOPPING);
            telemetryThread.interrupt();
            TimeUnit.SECONDS.sleep(1);
            telemetry.shutdown();
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
    
    @Override
    public void dataUpdated()
    {
    	telemetry.updateBatteryData(ina219.getLatestVoltage(), ina219.getLatestCurrent(), ina219.getLatestPower());
        //System.out.println("Bus voltage: " + ina219.getLatestVoltage().getX() + "V");
        //System.out.println("Current: " + ina219.getLatestCurrent().getX() + "mA");
        //System.out.println("Power: " + ina219.getLatestPower().getX() + "mW");
    }
}