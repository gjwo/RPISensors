package sensors.interfaces;

import java.io.IOException;

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
    void updateMagnetometerData() throws Exception;
    void calibrateMagnetometer() throws InterruptedException, IOException;
    void selfTestMagnetometer();
    void configMagnetometer() throws InterruptedException, IOException;
}
