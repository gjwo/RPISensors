package devices.I2C;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 05/01/2017.
 */
public interface RegisterSetting<T extends Number>
{
    T getValue();
    T getMask();
}
