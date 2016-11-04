package devices.sensors.interfaces;

/**
 * RPITank
 * Created by MAWood on 07/07/2016.
 */
public interface Thermometer
{
    float getLatestTemperature();
    float getAvgTemperature();
    float getTemperature(int i);
    int getThermometerReadingCount();
    void updateThermometerData() throws Exception;
    void calibrateThermometer();
    void selfTestThermometer();
}
