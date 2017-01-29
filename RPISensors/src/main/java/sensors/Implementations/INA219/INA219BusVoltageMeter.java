package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import hardwareAbstractionLayer.RegisterOperations;
import sensors.models.Sensor1D;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 04/01/2017.
 */
public class INA219BusVoltageMeter extends Sensor1D
{
    private final RegisterOperations ro;

    INA219BusVoltageMeter(RegisterOperations ro, int sampleSize)
    {
        super(sampleSize);
        this.ro = ro;
    }

    @Override
    public void updateData() {
        int raw = ro.readShort(INA219Registers.BUS_VOLTAGE_MEASURE);
        if (((raw&2)==2) && ((raw&1)==0))
        {	
        	// data is ready and hasn't overflowed
            this.addValue(new TimestampedData1f((float)((raw>>3)*4)*0.001f));
        }
    }
}
