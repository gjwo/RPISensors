/**
 * 
 */
package sensors.Implementations.MPU9250;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dataTypes.Data3s;
import devices.I2C.I2CImplementation;
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
public class MPU9250RegisterOperations {
	private I2CImplementation busDevice;
	private int debugLevel;
	
	public MPU9250RegisterOperations(I2CImplementation device,int debugLevel)
	{
		this.busDevice = device; // the device on the IC2 bus that the registers belong to
		this.debugLevel = debugLevel;
	}
	
    /**
     * Prints the name and contents of the register in binary and Hex
     * @param r		- the register to be printed
     */
    public void printByteRegister(Register r)
    {
    	byte rv = readByteRegister(r);
    	
    	System.out.print(Conversion.byteToLogString(r,rv));
    }
    /**
     * Prints the name and contents of the  16 bit register in binary and Hex
     * @param r		- the register to be printed
     */
    public void print16BitRegister(Register r)
    {
    	short[] rv = read16BitRegisters(r,1);
    	System.out.print(Conversion.shortToLogString(r,rv[0]));
    }
   
    /**
     * Prints the name and contents of the little endian 16 bit register in binary and Hex
     * @param r		- the register to be printed
     */
    public void print16BitRegisterLittleEndian(Register r)
    {
    	short[] rv = read16BitRegistersLittleEndian(r,1);
    	System.out.print(Conversion.shortToLogString(r,rv[0]));
    }
   
  /**
    * Reads the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @return		- the value of the register
    */
   byte readByteRegister(Register r)
   {
	   try {
		return busDevice.read(r.getAddress());
	   } catch (IOException e) {
		   e.printStackTrace();
		   return (byte)0xFF;
	   }
   }
   
   /**
    * Reads the specified number of byte Registers from the device this class is associated with
    * @param r 			- the first register to be read
    * @param byteCount 	- number of bytes to be read
    * @return			- an array of the bytes read from the registers
    */
   byte[] readByteRegisters(Register r, int byteCount)
   {
	   try {
		return busDevice.read(r.getAddress(),byteCount);
	   } catch (IOException e) {
		   e.printStackTrace();
		   return null;
	   }
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
       byte[] rawData = readByteRegisters(r, pointCount*6);
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
   short[] read16BitRegisters(Register r, int regCount)
   {	//The lower byte must be masked or the sign bits extend to integer length
       byte[] rawData = readByteRegisters(r, regCount*2);
       return Conversion.bytesMSBToShorts(rawData);
   }
   /**
    * Reads the specified number of 16 bit Registers from the device this class is associated with
    * @param r 	- the register to be read (name of first byte)
    * @param regCount 	- number of 16 bit registers to be read
    * @return 			- an array of shorts (16 bit signed values) holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the least significant part of the register 
    */
   short[] read16BitRegistersLittleEndian(Register r, int regCount)
   {
       byte[] rawData = readByteRegisters(r, regCount*2);
       return Conversion.bytesLSBToShorts(rawData);
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param rv		- the value to be written to the register
    */
   void writeByteRegister(Register r, byte rv)
   {
	   byte oldRegVal = readByteRegister(r);
       try {
		busDevice.write(r.getAddress(),rv);
       } catch (IOException e) {
		e.printStackTrace();
       }
       try {
		TimeUnit.MILLISECONDS.sleep(2); // delay to allow register to settle
       } catch (InterruptedException ignored) {}
       if (debugLevel >=9)
       {
    	   byte newRegVal = readByteRegister(r);
    	   if(newRegVal == rv)
		   System.out.format("%20s : %8s 0x%X -> %8s 0x%X%n",
				   	r.getName(),Conversion.byteToBitString(oldRegVal),oldRegVal,Conversion.byteToBitString(newRegVal),newRegVal);

    	   else System.out.format("%20s : %8s 0x%X -> %8s 0x%X read as -> %8s 0x%X%n ",
    			   r.getName(),Conversion.byteToBitString(oldRegVal),oldRegVal,Conversion.byteToBitString(rv),rv,Conversion.byteToBitString(newRegVal),newRegVal);
       }
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param mask	- a byte mask with bits set for the position of the field
    * @param bits	- a byte with the bits set in the correct position for the field to give the required setting 
    * 				  i.e in line with the mask. 
    */
   void writeByteRegisterfield(Register r, byte mask, byte bits)
   {
	   byte rv = 0;
	   byte oldRegVal = readByteRegister(r);
	   rv = (byte) ((oldRegVal & ~mask)|bits);
       try {
		busDevice.write(r.getAddress(),rv);
       } catch (IOException e) {
		e.printStackTrace();
       }
       try {
    	   TimeUnit.MILLISECONDS.sleep(2); // delay to allow register to settle
       } catch (InterruptedException ignored) {}
       if (debugLevel >=9)
       {
    	   byte newRegVal = readByteRegister(r);
    	   if(newRegVal == rv)
    		   System.out.format("%20s : %8s 0x%X -> %8s 0x%X%n",
    				   				r.getName(),Conversion.byteToBitString(oldRegVal),
    				   				oldRegVal,Conversion.byteToBitString(newRegVal),
    				   				newRegVal);

    	   else System.out.format("%20s : %8s 0x%X -> %8s 0x%X read as -> %8s 0x%X%n ",
    			   					r.getName(),Conversion.byteToBitString(oldRegVal),oldRegVal,
    			   					Conversion.byteToBitString(rv),rv,Conversion.byteToBitString(newRegVal),
    			   					newRegVal);
       }	   
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param rv		- the value to be written to the register
    */
   void write16bitRegister(Register r, short rv)
   {

       try {
    	   busDevice.write(r.getAddress(),(byte)(((rv)  >> 8) & 0xFF)); //extract and write most significant byte, mask after shift
    	   try {
			TimeUnit.MILLISECONDS.sleep(2);// delay to allow register to settle
    	   } catch (InterruptedException ignored) {}
    	   busDevice.write(r.getAddress()+1,(byte)((rv) & 0xFF)); //extract and write least significant byte mask without shift
       } catch (IOException e) {
		e.printStackTrace();
       }
       try {
		TimeUnit.MILLISECONDS.sleep(2); // delay to allow register to settle
       } catch (InterruptedException ignored) {}
   }
}