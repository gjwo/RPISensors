package deviceHardwareAbstractionLayer;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

/**
 * Pi4jI2CDevice   -    provides basic operations for communicating with a device
 *                      using Pi4j over an IC2 bus
 * Created by MAWood on 17/07/2016.
 */
public class Pi4jI2CDevice implements Device
{
    private final I2CDevice device;

    public Pi4jI2CDevice(I2CDevice device)
    {
        this.device = device;
    }

    @Override
    public byte read(int registerAddress) throws IOException
    {
        return (byte) device.read(registerAddress);
    }

    @Override
    public byte[] read(int registerAddress, int count) throws IOException
    {
        byte[] buffer = new byte[count];
        device.read(registerAddress,buffer,0,count);
        return buffer;
    }

    @Override
    public void write(int registerAddress, byte data) throws IOException
    {
        device.write(registerAddress,data);
    }
    @Override
    public void write(int registerAddress, byte[] data) throws IOException
    {
        device.write(registerAddress,data);
    }
}