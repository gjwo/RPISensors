package sensors.interfaces;

import dataTypes.TimestampedDataFloat3D;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Gyroscope
{
    TimestampedDataFloat3D getLatestRotationalAcceleration();
    TimestampedDataFloat3D getRotationalAcceleration(int i);
    TimestampedDataFloat3D getAvgRotationalAcceleration();
    int getGyroscopeReadingCount();
    void updateGyroscopeData() throws Exception;
    void calibrateGyroscope() throws InterruptedException;
    void selfTestGyroscope();
}
