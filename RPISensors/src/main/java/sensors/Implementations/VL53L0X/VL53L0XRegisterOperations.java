package sensors.Implementations.VL53L0X;

import devices.I2C.I2CImplementation;
import sensors.Implementations.VL53L0X.VL53L0XConstants.Registers;

import java.io.IOException;

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

    void writeReg(Registers reg, int value)
    {
        writeReg(reg.getAddress(), value);
    }

    void writeReg(Registers reg, byte value)
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
            Thread.sleep(2); // delay to allow register to settle
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
            Thread.sleep(2); // delay to allow register to settle
        } catch (InterruptedException ignored) {}
    }

    byte readReg(Registers r)
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

    byte[] readRegs(Registers r, int count)
    {
        byte[] output = new byte[count];
        for(int i = 0; i<count; i++)
        {
            output[i] = readReg(r.getAddress()+i);
        }
        return output;
    }

    short readReg16Bit(Registers r)
    {	//The lower byte must be masked or the sign bits extend to integer length
        byte[] rawData;
        try
        {
            rawData = busDevice.read(r.getAddress(),2);
            return(short) (((short)rawData[0] << 8) | (rawData[1]&0xff)) ;  // Turn the MSB and LSB into a signed 16-bit value
        } catch (IOException e)
        {
            e.printStackTrace();
            return Short.parseShort(null);
        }
    }

    int readReg32Bit(Registers r)
    {
        byte[] rawData;
        try
        {
            rawData = busDevice.read(r.getAddress(),4);
            return ((rawData[0] << 24)
                  | (rawData[1]&0xff)<<16
                  | (rawData[2]&0xff)<<8
                  | (rawData[3]&0xff));
        } catch (IOException e)
        {
            e.printStackTrace();
            return Integer.parseInt(null);
        }
    }
}
