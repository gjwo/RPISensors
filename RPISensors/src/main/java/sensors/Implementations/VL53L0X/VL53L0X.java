package sensors.Implementations.VL53L0X;

import devices.I2C.I2CImplementation;
import sensors.models.SensorPackage;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.VL53L0X
 * Created by MAWood on 27/12/2016.
 */
public class VL53L0X extends SensorPackage
{
    VL53L0XRanger sensor;

    /**
     * SensorPackage		- Constructor
     *
     * @param sampleRate
     * @param debugLevel
     */
    VL53L0X(I2CImplementation i2cImpl,int sampleRate, int sampleSize, int debugLevel)
    {
        super(sampleRate, debugLevel);
        sensor = new VL53L0XRanger(i2cImpl, sampleSize);
    }

    @Override
    public void updateData()
    {
        try
        {
            sensor.updateData();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
