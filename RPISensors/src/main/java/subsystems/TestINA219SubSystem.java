package subsystems;

import com.pi4j.io.i2c.I2CBus;
import hardwareAbstractionLayer.Device;
import hardwareAbstractionLayer.Pi4jI2CDevice;
import hardwareAbstractionLayer.Wiring;
import sensors.Implementations.INA219.INA219;
import sensors.interfaces.UpdateListener;

import java.io.IOException;

/**
 * RPISensors - subsystems
 * Created by MAWood on 04/01/2017.
 */
public class TestINA219SubSystem extends TestHarnessSubSystem implements UpdateListener
{
    private INA219 ina219;

    public TestINA219SubSystem()
    {
        super();
        try
        {
            I2CBus i2CBus1 = Wiring.getI2CBus1();
            Device device = new Pi4jI2CDevice(i2CBus1.getDevice(0x40));
            ina219 = new INA219(device, 10, 100);
            ina219.registerInterest(this);
            this.setRunnable(ina219);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void dataUpdated()
    {
        System.out.println("Bus voltage: " + ina219.getLatestVoltage().getX() + "V");
        System.out.println("Current: " + ina219.getLatestCurrent().getX() + "mA");
        System.out.println("Power: " + ina219.getLatestPower().getX() + "mW");
    }
}
