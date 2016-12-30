/**
 * 
 */
package sensors.Implementations.MPU9250;

import dataTypes.Data3s;
import devices.I2C.I2CImplementation;
import devices.I2C.RegisterOperations;
import utilities.Conversion;
import utilities.Register;

/**
 * Register Operations
 * 
 * @author GJWood
 * @version 1.0
 * 
 * The module hides all details about manipulating the registers of a device and
 * provides a simple interface to access them, and display them for debugging.
 */
class MPU9250RegisterOperations {
	private I2CImplementation busDevice;
	private int debugLevel;
	RegisterOperations ro;
	
	/**
	 * MPU9250RegisterOperations - Constructor
	 * @param device
	 * @param debugLevel
	 */
	MPU9250RegisterOperations(I2CImplementation device,int debugLevel)
	{
		this.busDevice = device; // the device on the IC2 bus that the registers belong to
		this.debugLevel = debugLevel;
		this.ro = new RegisterOperations(this.busDevice);
	}
	
    /**
     * Prints the name and contents of the register in binary and Hex
     * @param r		- the register to be printed
     */
    void printByteRegister(Register r)
    {
    	System.out.print(Conversion.byteToLogString(r,ro.readByte(r)));
    }
    /**
     * Prints the name and contents of the  16 bit register in binary and Hex
     * @param r		- the register to be printed
     */
    void printShort(Register r)
    {
    	System.out.print(Conversion.shortToLogString(r,ro.readShort(r)));
    }
   
    /**
     * Prints the name and contents of the little endian 16 bit register in binary and Hex
     * @param r		- the register to be printed
     */
    void printShortLSBfirst(Register r)
    {
    	System.out.print(Conversion.shortToLogString(r,ro.readShortLSBfirst(r)));
    }
   
  /**
    * Reads the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @return		- the value of the register
    */
   byte readByte(Register r)
   {
	   return ro.readByte(r);
   }
   
   /**
    * Reads the specified number of byte Registers from the device this class is associated with
    * @param r 			- the first register to be read
    * @param byteCount 	- number of bytes to be read
    * @return			- an array of the bytes read from the registers
    */
   byte[] readBytes(Register r, int byteCount)
   {
	   return ro.readBytes(r,byteCount);
   }

   /**
    * Reads the specified number of 16 bit 3 dimensional data points from the device this class is associated with
    * @param r 		- the register to be read (name of first byte)
    * @param pointCount 	- number of short3D data points to be read
    * @return 				- an array of Datashort3D holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the more significant part of the register
    * The registers are then assigned to the x, y, z fields of the dataShort3D array element
    */
   Data3s[] readShort3DsfromRegisters(Register r, int pointCount)
   {	//The lower byte must be masked or the sign bits extend to integer length
       byte[] rawData = ro.readBytes(r, pointCount*6);
       short[] values = Conversion.bytesMSBToShorts(rawData);// Turn the MSB and LSB into a signed 16-bit value
       Data3s[] datapoints = new Data3s[pointCount];
       for (int i=0;i<pointCount;i++)		
       {
    	   datapoints[i].setX(values[i*3]);
    	   datapoints[i].setY(values[i*3+1]);
    	   datapoints[i].setZ(values[i*3+2]);
       }
       return datapoints;
   }
   /**
    * Reads the specified number of 16 bit Registers from the device this class is associated with
    * @param r 	- the register to be read (name of first byte)
    * @param regCount 	- number of 16 bit registers to be read
    * @return 			- an array of shorts (16 bit signed values) holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the more significant part of the register 
    */
   short[] readShorts(Register r, int regCount)
   {	
       return Conversion.bytesMSBToShorts(ro.readBytes(r, regCount*2));
   }
   /**
    * Reads the specified number of 16 bit Registers from the device this class is associated with
    * @param r 	- the register to be read (name of first byte)
    * @param regCount 	- number of 16 bit registers to be read
    * @return 			- an array of shorts (16 bit signed values) holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the least significant part of the register 
    */
   short[] readShortsLSBfirst(Register r, int regCount)
   {
       return Conversion.bytesLSBToShorts(ro.readBytes(r, regCount*2));
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param rv		- the value to be written to the register
    */
   void writeByte(Register r, byte rv)
   {
	   byte oldRegVal = ro.readByte(r);
	   ro.writeByte(r, rv);
	   if (debugLevel >=9) System.out.print(Conversion.byteToLogString(r,oldRegVal,rv,readByte(r)));
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param mask	- a byte mask with bits set for the position of the field
    * @param bits	- a byte with the bits set in the correct position for the field to give the required setting 
    * 				  i.e in line with the mask. 
    */
   void writeBytefield(Register r, byte mask, byte bits)
   {
	   byte rv = 0;
	   byte oldRegVal = ro.readByte(r);
	   rv = (byte) ((oldRegVal & ~mask)|bits);
	   ro.writeByte(r, rv);
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param rv		- the value to be written to the register
    */
   void writeShort(Register r, short rv)
   {
	   ro.writeShort(r, rv);
   }
}