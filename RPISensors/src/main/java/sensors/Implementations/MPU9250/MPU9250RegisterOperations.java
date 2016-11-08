/**
 * 
 */
package sensors.Implementations.MPU9250;

import java.io.IOException;

import dataTypes.DataShort3D;
import devices.I2C.I2CImplementation;

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
	
	public MPU9250RegisterOperations(I2CImplementation device)
	{
		this.busDevice = device; // the device on the IC2 bus that the registers belong to
	}
	
	/**
	 * produces a binary representation of a byte
	 * @param b		- the byte
	 * @return		- A string containing the binary representation
	 */
    public String byteToString(byte b)
    {
    	String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    	return s;  	
    }
    
    /**
     * Prints the name and contents of the register in binary and Hex
     * @param r		- the register to be printed
     */
    public void printByteRegister(Registers r)
    {
    	byte rv = readByteRegister(r);
    	System.out.format("%20s : %8s 0x%X%n",r.name(),byteToString(rv),rv);
    }
   
   /**
    * Reads the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @return		- the value of the register
    */
   byte readByteRegister(Registers r)
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
   byte[] readByteRegisters(Registers r, int byteCount)
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
    * @param register 		- the register to be read (name of first byte)
    * @param pointCount 	- number of short3D data points to be read
    * @return 				- an array of Datashort3D holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the more significant part of the register
    * The registers are then assigned to the x, y, z fields of the dataShort3D array element
    */
   DataShort3D[] readShort3DsfromRegisters(Registers r, int pointCount)
   {
       byte[] rawData = readByteRegisters(r, pointCount*6);
       DataShort3D[] datapoints = new DataShort3D[pointCount];
       for (int i=0;i<pointCount;i++)		
       {
    	   datapoints[i].setX( (short) (((short)rawData[i*2] << 8) | (rawData[(i*2)+1]&0xFF))) ;  // Turn the MSB and LSB into a signed 16-bit value
    	   datapoints[i].setY( (short) (((short)rawData[(i+1)*2] << 8) | (rawData[((i+1)*2)+1]&0xFF))) ;  // Turn the MSB and LSB into a signed 16-bit value
    	   datapoints[i].setZ( (short) (((short)rawData[(i+2)*2] << 8) | (rawData[((i+2)*2)+1]&0xFF))) ;  // Turn the MSB and LSB into a signed 16-bit value
       }
       return datapoints;
   }
   /**
    * Reads the specified number of 16 bit Registers from the device this class is associated with
    * @param register 	- the register to be read (name of first byte)
    * @param regCount 	- number of 16 bit registers to be read
    * @return 			- an array of shorts (16 bit signed values) holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the more significant part of the register 
    */
   short[] read16BitRegisters(Registers r, int regCount)
   {
       byte[] rawData = readByteRegisters(r, regCount*2);
       short[] registers = new short[regCount];
       for (int i=0;i<regCount;i++)		
       {
       	registers[i] = (short) (((short)rawData[i*2] << 8) | (rawData[(i*2)+1]&0xff)) ;  // Turn the MSB and LSB into a signed 16-bit value
       }
       return registers;
   }
   /**
    * Reads the specified number of 16 bit Registers from the device this class is associated with
    * @param register 	- the register to be read (name of first byte)
    * @param regCount 	- number of 16 bit registers to be read
    * @return 			- an array of shorts (16 bit signed values) holding the registers
    * Each registers is constructed from reading and combining 2 bytes, the first byte forms the least significant part of the register 
    */
   short[] read16BitRegistersLittleEndian(Registers r, int regCount)
   {
       byte[] rawData = readByteRegisters(r, regCount*2);
       short[] registers = new short[regCount];
       for (int i=0;i<regCount;i++)		
       {
       	registers[i] = (short) (((short)rawData[i*2+1] << 8) | (rawData[(i*2)]&0xFF)) ;  // Turn the MSB and LSB into a signed 16-bit value
       }
       return registers;
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param rv		- the value to be written to the register
    */
   void writeByteRegister(Registers r, byte rv)
   {
	   byte oldRegVal = readByteRegister(r);
       try {
		busDevice.write(r.getAddress(),rv);
       } catch (IOException e) {
		e.printStackTrace();
       }
       try {
		Thread.sleep(2); // delay to allow register to settle
       } catch (InterruptedException e) {}
	   byte newRegVal = readByteRegister(r);
	   if(newRegVal == rv)
		   System.out.format("%20s : %8s 0x%X -> %8s 0x%X%n",
				   r.name(),byteToString(oldRegVal),oldRegVal,byteToString(newRegVal),newRegVal);

	   else System.out.format("%20s : %8s 0x%X -> %8s 0x%X read as -> %8s 0x%X%n ",
			   r.name(),byteToString(oldRegVal),oldRegVal,byteToString(rv),rv,byteToString(newRegVal),newRegVal);
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param mask	- a byte mask with bits set for the position of the field
    * @param bits	- a byte with the bits set in the correct position for the field to give the required setting 
    * 				  i.e in line with the mask. 
    */
   void writeByteRegisterfield(Registers r, byte mask, byte bits)
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
		Thread.sleep(2); // delay to allow register to settle
       } catch (InterruptedException e) {}
	   byte newRegVal = readByteRegister(r);
	   if(newRegVal == rv)
		   System.out.format("%20s : %8s 0x%X -> %8s 0x%X%n",
				   r.name(),byteToString(oldRegVal),oldRegVal,byteToString(newRegVal),newRegVal);

	   else System.out.format("%20s : %8s 0x%X -> %8s 0x%X read as -> %8s 0x%X%n ",
			   r.name(),byteToString(oldRegVal),oldRegVal,byteToString(rv),rv,byteToString(newRegVal),newRegVal);
   }
   
   /**
    * Writes a byte to the specified byte register from the device this class is associated with
    * @param r		- the register to be read
    * @param rv		- the value to be written to the register
    */
   void write16bitRegister(Registers r, short rv)
   {

       try {
    	   busDevice.write(r.getAddress(),(byte)(((rv)  >> 8) & 0xFF)); //extract and write most significant byte, mask after shift
    	   try {
			Thread.sleep(2);// delay to allow register to settle
    	   } catch (InterruptedException e) {}
    	   busDevice.write(r.getAddress()+1,(byte)((rv) & 0xFF)); //extract and write least significant byte mask without shift
       } catch (IOException e) {
		e.printStackTrace();
       }
       try {
		Thread.sleep(2); // delay to allow register to settle
       } catch (InterruptedException e) {}
   }
   
  /**
   * Prints the contents of pre-selected registers 
   */
  public void outputConfigRegisters()
   {
   	printByteRegister(Registers.CONFIG);
   	printByteRegister(Registers.GYRO_CONFIG);
   	printByteRegister(Registers.ACCEL_CONFIG);
   	printByteRegister(Registers.ACCEL_CONFIG2);
   	printByteRegister(Registers.LP_ACCEL_ODR);
   	printByteRegister(Registers.WOM_THR);
   	printByteRegister(Registers.MOT_DUR);
   	printByteRegister(Registers.ZMOT_THR);
   	printByteRegister(Registers.FIFO_EN);
   	printByteRegister(Registers.I2C_MST_CTRL);
   	printByteRegister(Registers.I2C_MST_STATUS);
   	printByteRegister(Registers.INT_PIN_CFG);
   	printByteRegister(Registers.INT_ENABLE);
   	printByteRegister(Registers.INT_STATUS);
   	printByteRegister(Registers.I2C_MST_DELAY_CTRL);
   	printByteRegister(Registers.SIGNAL_PATH_RESET);
   	printByteRegister(Registers.MOT_DETECT_CTRL);
   	printByteRegister(Registers.USER_CTRL);
   	printByteRegister(Registers.PWR_MGMT_1);
   	printByteRegister(Registers.PWR_MGMT_2);
   	printByteRegister(Registers.WHO_AM_I_MPU9250);
   	printByteRegister(Registers.SMPLRT_DIV);
   }
}