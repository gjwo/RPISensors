package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.I2C.I2CImplementation;
import devices.I2C.RegisterOperations;
import logging.SystemLog;
import sensors.interfaces.CurrentMeter;
import sensors.interfaces.VoltageMeter;
import sensors.models.SensorPackage;
import subsystems.SubSystem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * RPISensors - sensors.Implementations
 * Created by MAWood on 04/01/2017.
 */
public class INA219 extends SensorPackage implements CurrentMeter, VoltageMeter
{
    private final RegisterOperations ro;
    private final INA219CurrentMeter currentMeter;
    private final INA219BusVoltageMeter busVoltageMeter;

    public INA219(I2CImplementation i2cImpl, int sampleRate, int sampleSize)
    {
        super(sampleRate,4);
        this.ro = new RegisterOperations(i2cImpl);

        // modules that will gather each piece of data
        currentMeter = new INA219CurrentMeter(this.ro, sampleSize);
        busVoltageMeter = new INA219BusVoltageMeter(this.ro, sampleSize);
    }

    private void writeConfig() throws IOException
    {
        ArrayList<Configuration> configs = new ArrayList<>();

        configs.add(Configuration.BASDC4_12BIT);            // accuracy
        configs.add(Configuration.BUS_VOLTAGE_RANGE_16V);   // voltage range
        configs.add(Configuration.PGA_GAIN_DIV_1);          // voltage precision
        configs.add(Configuration.SADC_128SAMPLES);         // sample rate
        configs.add(Configuration.MODE_BUS_V_CONTINUOUS);   // mode

        short config = 0;
        for(Configuration setting:configs) config |= setting.getValue();
        ro.writeShort(INA219Registers.CONFIGURATION,config);
    }

    @Override
    public void updateData()
    {
        try
        {
            writeConfig();
            busVoltageMeter.updateData();
            currentMeter.updateData();
        } catch (IOException e)
        {
            SystemLog.log(SubSystem.SubSystemType.DEVICES, SystemLog.LogLevel.ERROR,e.getMessage());
        }
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
