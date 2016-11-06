package sensors.interfaces;

import java.io.IOException;

import dataTypes.TimestampedDataFloat3D;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Magnetometer
{
    TimestampedDataFloat3D getLatestGaussianData();
    TimestampedDataFloat3D getAvgGauss();
    TimestampedDataFloat3D getGaussianData(int i);
    int getMagnetometerReadingCount();
    void updateMagnetometerData() throws Exception;
    void calibrateMagnetometer() throws InterruptedException, IOException;
    void selfTestMagnetometer();
    void initMagnetometer() throws InterruptedException, IOException;
}
