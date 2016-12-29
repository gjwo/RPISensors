package sensors.Implementations.VL53L0X;

import devices.I2C.I2CImplementation;
import devices.I2C.RegisterOperations;
import utilities.Register;

/**
 * RPISensors - sensors.Implementations.VL53L0XRanger
 * Created by MAWood on 27/12/2016.
 */
public class VL53L0XRegisterOperations
{
    private RegisterOperations ro; 

    VL53L0XRegisterOperations(I2CImplementation i2CImplementation)
    {
        this.ro = new RegisterOperations(i2CImplementation);
    }

    void writeReg(Register reg, byte value)				{ro.writeByte(reg,value);}
    public void writeBytes(Register reg, byte[] bytes)	{ro.writeBytes(reg,bytes);}
    byte readReg(Register reg) 							{return ro.readByte(reg);}
    byte[] readRegs(Register reg, int count) 			{return ro.readBytes(reg,count);}
    short readReg16Bit(Register r) 						{return ro.readShort(r);}
    int readReg32Bit(Register r) 						{return ro.readInt(r);}
}