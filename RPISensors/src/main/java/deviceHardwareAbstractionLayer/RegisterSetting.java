package deviceHardwareAbstractionLayer;

/**
 * RPISensors - devices.Device.RegisterSetting
 * Created by MAWood on 05/01/2017.
 */
public interface RegisterSetting<T extends Number>
{
    T getBits();
    T getMask();
}
