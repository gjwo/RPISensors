package sensors.Implementations.VL53L0X;

import devices.I2C.I2CImplementation;
import utilities.Register;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import utilities.Conversion;

/**
 * RPISensors - sensors.Implementations.VL53L0XRanger
 * Created by MAWood on 27/12/2016.
 */
public class VL53L0XRegisterOperations
{
    private I2CImplementation busDevice;

    VL53L0XRegisterOperations(I2CImplementation i2CImplementation)
    {
        this.busDevice = i2CImplementation;
    }

    void writeReg(Register reg, int value)
    {
        writeReg(reg.getAddress(), value);
    }

    void writeReg(Register reg, byte value)
    {
        writeReg(reg.getAddress(), value);
    }

    byte readReg(Register r)
    {
        try {
            return busDevice.read(r.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
            return Byte.parseByte(null);
        }
    }
    private byte readReg(int r)
    {
        try {
            return busDevice.read(r);
        } catch (IOException e) {
            e.printStackTrace();
            return Byte.parseByte(null);
        }
    }

    byte[] readRegs(Register r, int count)
    {
        try
		{
			return   busDevice.read(r.getAddress(),count);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
    }

    short readReg16Bit(Register r)
    {	//The lower byte must be masked or the sign bits extend to integer length
        return Conversion.bytes2MSBToShort(readRegs(r,2));
    }

    int readReg32Bit(Register r)
    {
        return Conversion.bytes4MSBToInt( readRegs(r,4));
    }
    
    private void writeReg(int reg, byte value)
    {
        try {
            busDevice.write(reg,value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        	TimeUnit.MILLISECONDS.sleep(2);// delay to allow register to settle
       } catch (InterruptedException ignored) {}
    }
    
    private void writeReg(int reg, int value)
    {
        try {
            busDevice.write(reg,(byte)value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        	TimeUnit.MILLISECONDS.sleep(2);// delay to allow register to settle
        } catch (InterruptedException ignored) {}
    }


}
