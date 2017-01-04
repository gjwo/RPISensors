package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.I2CImplementation;
import sensors.interfaces.CurrentMeter;
import sensors.interfaces.VoltageMeter;
import sensors.models.SensorPackage;

/**
 * RPISensors - sensors.Implementations
 * Created by MAWood on 04/01/2017.
 */
public class INA219 extends SensorPackage implements CurrentMeter, VoltageMeter
{
    private final I2CImplementation i2cImpl;
    private final CurrentMeter currentMeter;
    private final VoltageMeter busVoltageMeter;

    public INA219(I2CImplementation i2cImpl, int sampleRate, int sampleSize)
    {
        super(sampleRate,4);
        this.i2cImpl = i2cImpl;

        // prepare the i2c device for the data to be gathered
        setupINA219();

        // modules that will gather each piece of data
        currentMeter = new INA219CurrentMeter(this.i2cImpl, sampleSize);
        busVoltageMeter = new INA219BusVoltageMeter(this.i2cImpl, sampleSize);
    }

    private void setupINA219()
    {

    }

    @Override
    public void updateData()
    {

    }

    @Override
    public TimestampedData1f getLatestCurrent() {return currentMeter.getLatestCurrent();}

    @Override
    public TimestampedData1f getAvgCurrent() {return currentMeter.getAvgCurrent();}

    @Override
    public TimestampedData1f getCurrentData(int i) {return currentMeter.getCurrentData(i);}

    @Override
    public int getCurrentDataCount() {return currentMeter.getCurrentDataCount();}

    @Override
    public TimestampedData1f getLatestVoltage() {return busVoltageMeter.getLatestVoltage();}

    @Override
    public TimestampedData1f getAvgVoltage() {return busVoltageMeter.getAvgVoltage();}

    @Override
    public TimestampedData1f getVoltageData(int i) {return busVoltageMeter.getVoltageData(i);}

    @Override
    public int getVoltageDataCount() {return busVoltageMeter.getVoltageDataCount();}
}
