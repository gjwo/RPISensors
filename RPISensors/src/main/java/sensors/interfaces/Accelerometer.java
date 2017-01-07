package sensors.interfaces;

import dataTypes.TimestampedData3f;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Accelerometer
{
    TimestampedData3f getLatestAcceleration();
    TimestampedData3f getAvgAcceleration();
    TimestampedData3f getAcceleration(int i);
    int getAccelerometerReadingCount();
    void updateAccelerometerData();
    void calibrateAccelerometer();
    void selfTestAccelerometer();
}
