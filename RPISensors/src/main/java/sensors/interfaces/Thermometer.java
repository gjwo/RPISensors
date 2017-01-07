package sensors.interfaces;

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
    void updateThermometerData();
    void calibrateThermometer();
    void selfTestThermometer();
}
