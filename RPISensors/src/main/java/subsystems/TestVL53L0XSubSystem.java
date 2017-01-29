package subsystems;

import com.pi4j.io.i2c.I2CBus;
import hardwareAbstractionLayer.Device;
import hardwareAbstractionLayer.Pi4jI2CDevice;
import hardwareAbstractionLayer.Wiring;
import sensors.Implementations.VL53L0X.VL53L0X;
import sensors.interfaces.UpdateListener;

import java.io.IOException;

/**
 * RPISensors - subsystems
 * Created by MAWood on 27/12/2016.
 */
public class TestVL53L0XSubSystem extends TestHarnessSubSystem implements UpdateListener
{
    private VL53L0X vl53L0X;
    public TestVL53L0XSubSystem()
    {
        super();
        try
        {
            I2CBus i2CBus1 = Wiring.getI2CBus1();
            Device device = new Pi4jI2CDevice(i2CBus1.getDevice(0x29));
            vl53L0X = new VL53L0X(device,10,100);
            vl53L0X.registerInterest(this);
            System.out.println("Interest Registered");
            this.setRunnable(vl53L0X);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void dataUpdated()
    {
        System.out.println("Latest: " + vl53L0X.getLatestRange().getX());
    }
}
