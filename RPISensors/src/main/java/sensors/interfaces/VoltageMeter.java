package sensors.interfaces;

import dataTypes.TimestampedData1f;

/**
 * RPISensors - sensors.interfaces
 * Created by MAWood on 04/01/2017.
 */
public interface VoltageMeter
{
    TimestampedData1f getLatestVoltage();
    TimestampedData1f getAvgVoltage();
    TimestampedData1f getVoltageData(int i);
    int getVoltageDataCount();
}
