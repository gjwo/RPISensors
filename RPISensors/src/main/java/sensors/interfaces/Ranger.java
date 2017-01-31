package sensors.interfaces;

import dataTypes.TimestampedData1f;

/**
 * RPISensors - sensors.interfaces
 * Created by MAWood on 27/12/2016.
 */
public interface Ranger
{
    TimestampedData1f getLatestRange();
    TimestampedData1f getAvgRange();
    TimestampedData1f getRangeData(int i);
    int getRangeDataCount();
    int getRangingTimeBudget();
}
