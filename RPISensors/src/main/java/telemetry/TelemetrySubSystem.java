package telemetry;

import com.pi4j.io.i2c.I2CBus;
import hardwareAbstractionLayer.Device;
import hardwareAbstractionLayer.Pi4jI2CDevice;
import hardwareAbstractionLayer.Wiring;
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
    private Thread telemetryThread;
    private INA219 ina219;
    private Telemetry telemetry;
    private final I2CBus i2CBus1;
    private Device device;

    public TelemetrySubSystem()
    {
        super(SubSystem.SubSystemType.TELEMETRY);
        i2CBus1 = Wiring.getI2CBus1();
        telemetry = new Telemetry();
    }

    @Override
    public SubSystemState startup()
    {
        if(this.getSubSysState() != SubSystemState.IDLE) return this.getSubSysState();
        this.setSubSysState(SubSystemState.STARTING);
        try
        {
            device = new Pi4jI2CDevice(i2CBus1.getDevice(0x40));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        ina219 = new INA219(device, 10, 100);
        ina219.registerInterest(this);

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
            this.setSubSysState(SubSystemState.IDLE);
        } catch (InterruptedException e)
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