package sensors.interfaces;

import dataTypes.TimestampedData1f;

/**
 * RPISensors - sensors.interfaces
 * Created by MAWood on 04/01/2017.
 */
public interface PowerMeter
{
    TimestampedData1f getLatestPower();
    TimestampedData1f getAvgPower();
    TimestampedData1f getPowerData(int i);
    int getPowerDataCount();
}
