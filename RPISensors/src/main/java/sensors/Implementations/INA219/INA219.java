package sensors.Implementations.INA219;

import dataTypes.TimestampedData1f;
import devices.device.Device;
import devices.device.RegisterOperations;
import logging.SystemLog;
import sensors.interfaces.CurrentMeter;
import sensors.interfaces.PowerMeter;
import sensors.interfaces.VoltageMeter;
import sensors.models.SensorPackage;
import subsystems.SubSystem;

import java.io.IOException;

/**
 * RPISensors - sensors.Implementations
 * Created by MAWood on 04/01/2017.
 */
public class INA219 extends SensorPackage implements CurrentMeter, VoltageMeter, PowerMeter
{
    private final RegisterOperations ro;
    private final INA219CurrentMeter currentMeter;
    private final INA219BusVoltageMeter busVoltageMeter;
    private final INA219PowerMeter powerMeter;

    private final INA219Configuration config;

    public INA219(Device i2cImpl, int sampleRate, int sampleSize)
    {
        super(sampleRate,4);
        this.ro = new RegisterOperations(i2cImpl);
        ro.logWrites(true);


        config = new INA219Configuration(
                INA219Configuration.BusVoltageRange.BUS_VOLTAGE_RANGE_16V,
                INA219Configuration.CurrentGain.PGA_GAIN_DIV_8,
                INA219Configuration.AdcResolution.BASDC4_12BIT,
                INA219Configuration.AveragingSetting.SADC_128SAMPLES,
                INA219Configuration.OperatingMode.MODE_SH_BUS_V_CONTINUOUS);
        try
        {
            writeConfig();
        } catch (IOException e)
        {
            SystemLog.log(SubSystem.SubSystemType.DEVICES, SystemLog.LogLevel.ERROR,e.getMessage());
        }

        // modules that will gather each piece of data
        currentMeter = new INA219CurrentMeter(this.ro, sampleSize,config);
        busVoltageMeter = new INA219BusVoltageMeter(this.ro, sampleSize);
        powerMeter = new INA219PowerMeter(this.ro,sampleSize,config);
    }

    private void writeConfig() throws IOException
    {
        ro.writeShort(INA219Registers.CALIBRATION,config.getCalibrationValue());
        ro.writeShort(INA219Registers.CONFIGURATION,config.getValue());
    }

    @Override
    public void updateData()
    {
        try
        {
            busVoltageMeter.updateData();
            currentMeter.updateData();
            powerMeter.updateData();
        } catch (IOException e)
        {
            SystemLog.log(SubSystem.SubSystemType.DEVICES, SystemLog.LogLevel.ERROR,e.getMessage());
        }
    }

    @Override
    public TimestampedData1f getLatestCurrent() {return currentMeter.getLatestValue();}

    @Override
    public TimestampedData1f getAvgCurrent() {return currentMeter.getAvgValue();}

    @Override
    public TimestampedData1f getCurrentData(int i) {return currentMeter.getValue(i);}

    @Override
    public int getCurrentDataCount() {return currentMeter.getReadingCount();}

    @Override
    public TimestampedData1f getLatestVoltage() {return busVoltageMeter.getLatestValue();}

    @Override
    public TimestampedData1f getAvgVoltage() {return busVoltageMeter.getAvgValue();}

    @Override
    public TimestampedData1f getVoltageData(int i) {return busVoltageMeter.getValue(i);}

    @Override
    public int getVoltageDataCount() {return busVoltageMeter.getReadingCount();}

    @Override
    public TimestampedData1f getLatestPower()
    {
        return powerMeter.getLatestValue();
    }

    @Override
    public TimestampedData1f getAvgPower()
    {
        return powerMeter.getAvgValue();
    }

    @Override
    public TimestampedData1f getPowerData(int i)
    {
        return powerMeter.getValue(i);
    }

    @Override
    public int getPowerDataCount()
    {
        return powerMeter.getReadingCount();
    }
}
