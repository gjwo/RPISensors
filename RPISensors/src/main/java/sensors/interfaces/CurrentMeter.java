package sensors.interfaces;

import dataTypes.TimestampedData1f;

/**
 * RPISensors - sensors.interfaces
 * Created by MAWood on 04/01/2017.
 */
public interface CurrentMeter
{
    TimestampedData1f getLatestCurrent();
    TimestampedData1f getAvgCurrent();
    TimestampedData1f getCurrentData(int i);
    int getCurrentDataCount();
}
