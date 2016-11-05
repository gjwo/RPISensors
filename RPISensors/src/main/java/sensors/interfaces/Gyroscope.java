package sensors.interfaces;

import dataTypes.TimestampedData3D;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Gyroscope
{
    TimestampedData3D getLatestRotationalAcceleration();
    TimestampedData3D getRotationalAcceleration(int i);
    TimestampedData3D getAvgRotationalAcceleration();
    int getGyroscopeReadingCount();
    void updateGyroscopeData() throws Exception;
    void calibrateGyroscope() throws InterruptedException;
    void selfTestGyroscope();
}
