package devices.I2C;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import logging.SystemLog;
import subsystems.SubSystem;
import utilities.Conversion;
import utilities.Register;

public class RegisterOperations
{
	private final I2CImplementation busDevice;
	private boolean logReads;
	private boolean logWrites;
	
	/**
	 * Constructor
	 * @param busDevice
	 */
	public RegisterOperations(I2CImplementation busDevice)
	{
		this.busDevice = busDevice;
		this.logReads = false;
		this.logWrites = false;
	}
	
	public void logWrites(boolean log) {this.logWrites = log;}
	public void logReads(boolean log) {this.logReads = log;}
	/**
	 * readInt		-	Reads a single integer value from the designated device starting at the specified register
	 * 					in Most Significant Byte First order
	 * @param 		reg
	 * @return		the short that was read
	 */
	public int readInt(Register reg)
	{
		return Conversion.bytes4MSBToInt(readBytes(reg,4));
	}
	
	/**
	 * readIntLSBfirst	-	Reads a single integer value from the designated device starting at the specified register
	 * 						in Least Significant Byte First order
	 * @param 		reg
	 * @return		the short that was read
	 */
	public int readIntLSBfirst(Register reg)
	{
		return Conversion.bytes4LSBToInt(readBytes(reg,4));
	}
	
	/**
	 * readShort	-	Reads a single short value from the designated device starting at the specified register
	 * 					in Most Significant Byte First order
	 * @param 		reg
	 * @return		the short that was read
	 */
	public short readShort(Register reg)
	{
		short s = Conversion.bytes2MSBToShort(readBytes(reg,2));
		if (logReads) SystemLog.log(SubSystem.SubSystemType.DEVICES,SystemLog.LogLevel.TRACE_HW_EVENTS, Conversion.shortToLogString(reg,s));
		return s;
	}
	
	/**
	 * readShortLSBfirst-	Reads a single short value from the designated device starting at the specified register
	 * 						in Least Significant Byte First order
	 * @param 		reg
	 * @return		the short that was read
	 */
	public short readShortLSBfirst(Register reg)
	{
		short s = Conversion.bytes2LSBToShort(readBytes(reg,2));
		if (logReads) SystemLog.log(SubSystem.SubSystemType.DEVICES,SystemLog.LogLevel.TRACE_HW_EVENTS, Conversion.shortToLogString(reg,s));
		return s;
	}
	
	/**
	 * readByte 	- Reads a single byte from the designated device and register 
	 * @param 		reg
	 * @return 		the value read
	 */
    public byte readByte(Register reg)
    {
        try {
        	byte b = busDevice.read(reg.getAddress());
        	 if (logReads) SystemLog.log(SubSystem.SubSystemType.DEVICES,SystemLog.LogLevel.TRACE_HW_EVENTS, Conversion.byteToLogString(reg,b));
            return b;
        } catch (IOException e) {
            e.printStackTrace();
            return (byte) 0;
        }
    }
    
    /**
     * readBytes 	- Reads multiple bytes from the designated device and register
     * @param 		reg
     * @param 		count of bytes to be read
     * @return		a byte array of the values read
     */
    public byte[] readBytes(Register reg, int count)
    {
    	if (count <= 0) return null;
    	byte[] bytes;
    	int startAddr = reg.getAddress();
        try {
        	bytes = busDevice.read(startAddr,count);
            if (logReads) 
            {
				for (byte aByte : bytes)
				{
					SystemLog.log(SubSystem.SubSystemType.DEVICES, SystemLog.LogLevel.TRACE_HW_EVENTS, Conversion.byteToLogString(reg, aByte));
				}
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * writeInt 	-	Writes a short to the designated device and register
     * 					in Most Significant Byte First order
     * @param 		reg
     * @param 		value to be written
     */
    public void writeInt(Register reg, int value)
    {
    	writeBytes(reg, Conversion.intTo4BytesMSB(value));
    }
    
    /**
     * writeIntLSBfirst	-	Writes a short to the designated device and register
     * 						in Least Significant Byte First order
     * @param 		reg
     * @param 		value to be written
     */
    public void writeIntLSBfirst(Register reg, int value)
    {
    	writeBytes(reg, Conversion.intTo4BytesLSB(value));
    }
    
    /**
     * writeByte 	-	Writes a short to the designated device and register
     * 					in Most Significant Byte First order
     * @param 		reg
     * @param 		value to be written
     */
    public void writeShort(Register reg, short value)
    {
    	writeBytes(reg, Conversion.shortTo2BytesMSB(value));
    }

    /**
     * writeByteLSBfirst-	Writes a short to the designated device and register
     * 						in Least Significant Byte First order
     * @param 		reg
     * @param 		value to be written
     */
    public void writeShortLSBfirst(Register reg, short value)
    {
    	writeBytes(reg, Conversion.shortTo2BytesLSB(value));
    }

    /**
     * Reads the specified number of 16 bit Registers from the device this class is associated with
     * @param r 	- the register to be read (name of first byte)
     * @param regCount 	- number of 16 bit registers to be read
     * @return 			- an array of shorts (16 bit signed values) holding the registers
     * Each registers is constructed from reading and combining 2 bytes, the first byte forms the more significant part of the register 
     */
    public short[] readShorts(Register r, int regCount)
    {	
        return Conversion.bytesMSBToShorts(readBytes(r, regCount*2));
    }
    /**
     * Reads the specified number of 16 bit Registers from the device this class is associated with
     * @param r 	- the register to be read (name of first byte)
     * @param regCount 	- number of 16 bit registers to be read
     * @return 			- an array of shorts (16 bit signed values) holding the registers
     * Each registers is constructed from reading and combining 2 bytes, the first byte forms the least significant part of the register 
     */
    public short[] readShortsLSBfirst(Register r, int regCount)
    {
        return Conversion.bytesLSBToShorts(readBytes(r, regCount*2));
    }
    

    /**
     * writeByte 	-	Writes a single byte to the designated device and register
     * @param 		reg
     * @param 		value to be written
     */
    public void writeByte(Register reg, byte value)
    {

        try {
        	byte oldRegVal = 0;
        	if (logWrites) oldRegVal = busDevice.read(reg.getAddress());
            busDevice.write(reg.getAddress(),value);
      	    if (logWrites) SystemLog.log(SubSystem.SubSystemType.DEVICES,SystemLog.LogLevel.TRACE_HW_WRITES, Conversion.byteToLogString(reg,oldRegVal,value,readByte(reg)));
       } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        	TimeUnit.MILLISECONDS.sleep(2);// delay to allow register to settle
       } catch (InterruptedException ignored) {}
    }

    /**
     * writeBytes 	-	Writes multiple to the designated device and register
     * @param 		reg
     * @param 		bytes to be written
     */
    public void writeBytes(Register reg, byte[] bytes)
    {
    	int startAddr = reg.getAddress();
    	byte[] oldRegVals = null;
    	byte[] newRegVals = new byte[bytes.length];
        try {
          	if (logWrites) oldRegVals = busDevice.read(reg.getAddress(),bytes.length);
            busDevice.write(startAddr,bytes);
            newRegVals = busDevice.read(startAddr,bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (logWrites) 
        {
	        for (int i = 0; i<bytes.length; i++)
	        {
	      	    SystemLog.log(SubSystem.SubSystemType.DEVICES,SystemLog.LogLevel.TRACE_HW_WRITES, Conversion.byteToLogString(reg,oldRegVals[i],bytes[i],newRegVals[i]));
	        }
        }
        try {
        	TimeUnit.MILLISECONDS.sleep(2);// delay to allow registers to settle
        } catch (InterruptedException ignored) {}
    }
    
    /**
     * Writes a byte to the specified byte register from the device this class is associated with
     * @param r		- the register to be read
     * @param mask	- a byte mask with bits set for the position of the field
     * @param bits	- a byte with the bits set in the correct position for the field to give the required setting 
     * 				  i.e in line with the mask. 
     */
    public void writeBytefield(Register r, byte mask, byte bits)
    {
 	   byte rv;
 	   byte oldRegVal = readByte(r);
 	   rv = (byte) ((oldRegVal & ~mask)|bits);
 	   writeByte(r, rv);
    }
    
    /**
     * Prints the name and contents of the register in binary and Hex
     * @param r		- the register to be printed
     */
    public void printByteRegister(Register r)
    {
    	System.out.print(Conversion.byteToLogString(r,readByte(r)));
    }
    /**
     * Prints the name and contents of the  16 bit register in binary and Hex
     * @param r		- the register to be printed
     */
    public void printShort(Register r)
    {
    	System.out.print(Conversion.shortToLogString(r,readShort(r)));
    }
   
    /**
     * Prints the name and contents of the little endian 16 bit register in binary and Hex
     * @param r		- the register to be printed
     */
    public void printShortLSBfirst(Register r)
    {
    	System.out.print(Conversion.shortToLogString(r,readShortLSBfirst(r)));
    }

}