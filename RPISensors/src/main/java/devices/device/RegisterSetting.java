package devices.device;

/**
 * RPISensors - sensors.Implementations.INA219
 * Created by MAWood on 05/01/2017.
 */
public interface RegisterSetting<T extends Number>
{
    T getBits();
    T getMask();
}
