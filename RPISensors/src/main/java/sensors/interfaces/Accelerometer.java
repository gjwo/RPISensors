package sensors.interfaces;

import dataTypes.TimestampedDataFloat3D;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Accelerometer
{
    TimestampedDataFloat3D getLatestAcceleration();
    TimestampedDataFloat3D getAvgAcceleration();
    TimestampedDataFloat3D getAcceleration(int i);
    int getAccelerometerReadingCount();
    void updateAccelerometerData() throws Exception;
    void calibrateAccelerometer() throws InterruptedException;
    void selfTestAccelerometer();
}
