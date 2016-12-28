package sensors.Implementations.VL53L0X;

import devices.I2C.I2CImplementation;
import sensors.Implementations.VL53L0X.VL53L0XRegisters;

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

    void writeReg(VL53L0XRegisters reg, int value)
    {
        writeReg(reg.getAddress(), value);
    }

    void writeReg(VL53L0XRegisters reg, byte value)
    {
        writeReg(reg.getAddress(), value);
    }

    void writeReg(int reg, byte value)
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
    void writeReg(int reg, int value)
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

    byte readReg(VL53L0XRegisters r)
    {
        try {
            return busDevice.read(r.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
            return Byte.parseByte(null);
        }
    }
    byte readReg(int r)
    {
        try {
            return busDevice.read(r);
        } catch (IOException e) {
            e.printStackTrace();
            return Byte.parseByte(null);
        }
    }

    byte[] readRegs(VL53L0XRegisters r, int count)
    {
        byte[] output = new byte[count];
        for(int i = 0; i<count; i++)
        {
            output[i] = readReg(r.getAddress()+i);
        }
        return output;
    }

    short readReg16Bit(VL53L0XRegisters r)
    {	//The lower byte must be masked or the sign bits extend to integer length
        try
        {
            return Conversion.bytes2MSBToShort(busDevice.read(r.getAddress(),2));
         } catch (IOException e)
        {
            e.printStackTrace();
            return Short.parseShort(null);
        }
    }

    int readReg32Bit(VL53L0XRegisters r)
    {
        try
        {
             return Conversion.bytes4MSBToInt( busDevice.read(r.getAddress(),4));
        } catch (IOException e)
        {
            e.printStackTrace();
            return Integer.parseInt(null);
        }
    }
}
