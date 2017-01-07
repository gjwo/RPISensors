package sensors.interfaces;

import dataTypes.TimestampedData3f;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Magnetometer
{
    TimestampedData3f getLatestGaussianData();
    TimestampedData3f getAvgGauss();
    TimestampedData3f getGaussianData(int i);
    int getMagnetometerReadingCount();
    void updateMagnetometerData();
    void calibrateMagnetometer();
    void selfTestMagnetometer();
    void configMagnetometer() throws InterruptedException;
}
