package sensors.interfaces;

import dataTypes.TimestampedData3f;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Gyroscope
{
    TimestampedData3f getLatestRotationalAcceleration();
    TimestampedData3f getRotationalAcceleration(int i);
    TimestampedData3f getAvgRotationalAcceleration();
    int getGyroscopeReadingCount();
    void updateGyroscopeData() throws Exception;
    void calibrateGyroscope() throws InterruptedException;
    void selfTestGyroscope();
}
