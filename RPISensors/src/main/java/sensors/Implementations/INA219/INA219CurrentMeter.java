package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import deviceHardwareAbstractionLayer.RegisterOperations;
import sensors.models.Sensor1D;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219CurrentMeter extends Sensor1D
{
    private final RegisterOperations ro;
    private final INA219Configuration config;

    INA219CurrentMeter(RegisterOperations ro, int sampleSize, INA219Configuration config)
    {
        super(sampleSize);
        this.ro = ro;
        this.config = config;
    }

    @Override
    public void updateData() throws IOException
    {

        int raw = ro.readShort(INA219Registers.CURRENT_MEASURE);
        this.addValue(new TimestampedData1f((float)raw/(float)config.getCurrentDivider()));
    }
}
