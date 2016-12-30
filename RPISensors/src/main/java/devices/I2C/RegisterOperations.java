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
		return Conversion.bytes2MSBToShort(readBytes(reg,2));
	}
	
	/**
	 * readShortLSBfirst-	Reads a single short value from the designated device starting at the specified register
	 * 						in Least Significant Byte First order
	 * @param 		reg
	 * @return		the short that was read
	 */
	public short readShortLSBfirst(Register reg)
	{
		return Conversion.bytes2LSBToShort(readBytes(reg,2));
	}
	
	/**
	 * readByte 	- Reads a single byte from the designated device and register 
	 * @param 		reg
	 * @return 		the value read
	 */
    public byte readByte(Register reg)
    {
        try {
            return busDevice.read(reg.getAddress());
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
    	int startAddr = reg.getAddress();
        try {
            return busDevice.read(startAddr,count);
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
}