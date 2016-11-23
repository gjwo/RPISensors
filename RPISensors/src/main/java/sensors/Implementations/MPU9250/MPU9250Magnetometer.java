/**
 * 
 */
package sensors.Implementations.MPU9250;

import java.io.IOException;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;
import sensors.models.NineDOF;
import sensors.models.Sensor3D;

/**
 * @author GJWood
 * MPU 9250 Magnetometer sensor
 * Created by G.J.Wood on 1/11/2016
 * Based on MPU9250_MS5637_t3 Basic Example Code by: Kris Winer date: April 1, 2014
 * https://github.com/kriswiner/MPU-9250/blob/master/MPU9250_MS5637_AHRS_t3.ino
 * #KW references the original code in above location
 * 
 * This class handles the operation of the Magnetometer sensor and is a subclass of Sensor3D, it provides those methods
 * which are hardware specific to the MPU-9250 such as calibration configuring, self test and update
 * This class is independent of the bus implementation, register addressing etc as this is handled by RegisterOperations
 *  
 * Hardware registers controlled by this class
 * 0x00 00 AK8963_WHO_AM_I		- Magnetometer Who Am I should return 48
 * 0x01 01 AK8963_INFO			- Magnetometer Device information
 * 0x02 02 AK8963_ST1			- Magnetometer status data ready status bit 0
 * 0x03 03 AK8963_XOUT			- Magnetometer X axis reading (16 bits little endian)
 * 0x05 05 AK8963_YOUT			- Magnetometer Y axis reading (16 bits little endian)
 * 0x07 06 AK8963_ZOUT			- Magnetometer Z axis reading (16 bits little endian)
 * 0x09 09 AK8963_ST2			- Magnetometer status data overflow bit 3 and data read error status bit 2
 * 0x0A 10 AK8963_CNTL1			- Magnetometer configuration byte 1 Power down (0000), single-measurement (0001), self-test (1000) and Fuse ROM (1111) modes on bits 3:0
 * 0x0B 11 AK8963_CNTL2			- Magnetometer configuration byte 2 Reset bit 0
 * 0x0C 12 AK8963_ASTC			- Magnetometer Self test control
 * 0x10 16 AK8963_ASAX			- Fuse ROM X-axis sensitivity factory adjustment
 * 0x11 17 AK8963_ASAY			- Fuse ROM Y-axis sensitivity factory adjustment
 * 0x12 18 AK8963_ASAZ			- Fuse ROM Z-axis sensitivity factory adjustment
 */
public class MPU9250Magnetometer extends Sensor3D  {

	/**
	 * @param sampleRate
	 * @param sampleSize
	 */
    protected MPU9250RegisterOperations ro;
    protected NineDOF parent;
    private final MagScale magScale = MagScale.MFS_16BIT;
    private final MagMode magMode = MagMode.MM_100HZ;
    private short lastRawMagX;  //updated by updateData() needed during calibration 
    private short lastRawMagY;
    private short lastRawMagZ;
    private TimestampedData3f lastCalibratedReading = new TimestampedData3f(0,0,0);
    private Data3f magCalibration = null; //#KW 271 Hardware factory calibration data from AK8963, sent up in init(* param), used in update()

	public MPU9250Magnetometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro, NineDOF parent ) {
		super(sampleRate, sampleSize);
		this.ro = ro;
		this.parent = parent;
	}
	
    public void printState()
    {
    	super.printState();
    	System.out.println("magScale: "+ magScale.toString()+" res: "+magScale.res );
    	System.out.println("magMode: "+ magMode.toString() + " sampleCount: "+ magMode.sampleCount);
    	System.out.println("lastRawMagX: "+lastRawMagX+" lastRawMagY: "+lastRawMagY+" lastRawMagZ: "+lastRawMagZ);
    	System.out.println("lastCalibratedReading: "+lastCalibratedReading.toString());
    	System.out.println("magCalibration: "+magCalibration.toString());    	
    }

	  /**
	   * Prints the contents of registers used by this class 
	   */
	@Override
	public void printRegisters()
	{
	   	ro.printByteRegister(Registers.AK8963_WHO_AM_I);
	   	ro.printByteRegister(Registers.AK8963_INFO);
	   	ro.printByteRegister(Registers.AK8963_ST1);
	   	ro.printByteRegister(Registers.AK8963_ST2);
	   	ro.printByteRegister(Registers.AK8963_CNTL1);
	   	ro.printByteRegister(Registers.AK8963_CNTL2);
	   	ro.printByteRegister(Registers.AK8963_ASTC);
	   	ro.printByteRegister(Registers.AK8963_ASAX);
	   	ro.printByteRegister(Registers.AK8963_ASAY);
	   	ro.printByteRegister(Registers.AK8963_ASAZ);
	   	ro.print16BitRegisterLittleEndian(Registers.AK8963_XOUT_L);
	   	ro.print16BitRegisterLittleEndian(Registers.AK8963_YOUT_L);
	   	ro.print16BitRegisterLittleEndian(Registers.AK8963_ZOUT_L);
	}
	
	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getLatestGaussianData()
	 */
    @Override
	public void updateData()
    { //#KW loop() L490 calls - readMagData L812
        byte dataReady = (byte)(ro.readByteRegister(Registers.AK8963_ST1) & 0x01); //DRDY - Data ready bit0 1 = data is ready
        if (dataReady == 0) return; //no data ready
        
        // #KW 494 readMagData - data is ready, read it NB bug fix here read was starting from ST1 not XOUT_L
        byte[] buffer = ro.readByteRegisters(Registers.AK8963_XOUT_L, 7); // #KW L815 6 data bytes x,y,z 16 bits stored as little Endian (L/H)
        // Check if magnetic sensor overflow set, if not then report data	
        //roAK.readByteRegister(Registers.AK8963_ST2);// Data overflow bit 3 and data read error status bit 2
        byte status2 = buffer[6]; // Status2 register must be read as part of data read to show device data has been read
        if((status2 & 0x08) == 0) //#KW 817 bit3 HOFL: Magnetic sensor overflow is normal (no Overflow), data is valid
        {   //#KW L818-820
        	lastRawMagX = (short) ((buffer[1] << 8) | buffer[0]); // Turn the MSB and LSB into a signed 16-bit value
        	lastRawMagY = (short) ((buffer[3] << 8) | buffer[2]); // Data stored as little Endian
        	lastRawMagZ = (short) ((buffer[5] << 8) | buffer[4]);
	
        	//the stored calibration results is applied here as there is no hardware correction stored in the hardware via calibration
        	//#KW L496-L501. scale() does the multiplication by magScale L499-501
       		lastCalibratedReading = scale(new TimestampedData3f(	lastRawMagX*magScale.res*magCalibration.getX() - getDeviceBias().getX(),
       																lastRawMagY*magScale.res*magCalibration.getY() - getDeviceBias().getY(),
       																lastRawMagZ*magScale.res*magCalibration.getZ() - getDeviceBias().getZ()));
        	this.addValue(lastCalibratedReading); //store the result
        }
	}

	/* (non-Javadoc)
	 * @see devices.sensors.dataTypes.Sensor1D#updateData()
	 */

	@Override
	public void calibrate() throws  InterruptedException{
		// #KW L1064 magcalMPU9250
		if (debugLevel() >=3) System.out.println("calibrateMag");

        int  bias[] = {0, 0, 0}, scale[] = {0, 0, 0};
        short max[] = {(short)-32767, (short)-32767, (short)-32767},
        		min[] = {(short)32767, (short)32767, (short)32767},
        		temp[] = {0, 0, 0};

        if (debugLevel() >=1) System.out.println("Mag Calibration: Wave device in a figure eight until done!");
        Thread.sleep(2000);

        // #KW 1073 shoot for ~fifteen seconds of mag data
        for(int i = 0; i < magMode.sampleCount; i++) {
            updateData();  // Read the mag data
            temp[0] = (short) lastRawMagX;
            temp[1] = (short) lastRawMagY;
            temp[2] = (short) lastRawMagZ;
            for (int j = 0; j < 3; j++) {
                if(temp[j] > max[j]) max[j] = temp[j];
                if(temp[j] < min[j]) min[j] = temp[j];
            }
            if(magMode == MagMode.MM_8HZ) Thread.sleep(135);  // at 8 Hz ODR, new mag data is available every 125 ms
            if(magMode == MagMode.MM_100HZ) Thread.sleep(12);  // at 100 Hz ODR, new mag data is available every 10 ms
        }
        if (debugLevel() >=1) System.out.println("Mag Calibration: Finished");
        
        // #KW 1090 Get hard iron correction
        bias[0]  = (max[0] + min[0])/2;  // get average x mag bias in counts
        bias[1]  = (max[1] + min[1])/2;  // get average y mag bias in counts
        bias[2]  = (max[2] + min[2])/2;  // get average z mag bias in counts

        
        this.setDeviceBias(new Data3f(	((float) bias[0])*this.magScale.res*magCalibration.getX(), // save mag biases in G for main program
        								((float) bias[1])*this.magScale.res*magCalibration.getY(),	// deviceBias was dest1 in Kris Winer code
        								((float) bias[2])*this.magScale.res*magCalibration.getZ()));
        
        if (debugLevel() >=4) System.out.println("Devicebias: "+ this.getDeviceBias().toString());
        
        // #KW1099 Get soft iron correction estimate
        scale[0]  = (max[0] - min[0])/2;  // get average x axis max chord length in counts
        scale[1]  = (max[1] - min[1])/2;  // get average y axis max chord length in counts
        scale[2]  = (max[2] - min[2])/2;  // get average z axis max chord length in counts

        float avgRad = (float) (scale[0] + scale[1] + scale[2]) / 3.0f; // #KW 1104-5

        this.setDeviceScaling(new Data3f(avgRad/((float)scale[0]), // #KW1107-9 save mag scale for main program
        								avgRad/((float)scale[1]), // deviceScale was pass by ref dest2 in Kris Winer code
        								avgRad/((float)scale[2])));

        if (debugLevel() >=3) printState();
        if (debugLevel() >=3) System.out.println("End calibrateMag");
	}
	
	// No self Test
	
	/**
	 * Configure -	sets up device for normal operation
	 * 
	 * The method is equivalent to #KW initAK8963 L832
	 */
	@Override
	public void configure() throws InterruptedException, IOException {
		if (debugLevel() >=3) System.out.println("initAK8963");
        // First extract the factory calibration for each magnetometer axis

        ro.writeByteRegister(Registers.AK8963_CNTL1,(byte) 0x00); // #KW 836 Power down magnetometer
        Thread.sleep(10);
        ro.writeByteRegister(Registers.AK8963_CNTL1, (byte)0x0F); // #KW 838 Enter Fuse ROM access bits
        Thread.sleep(10);
        byte rawData[] = ro.readByteRegisters(Registers.AK8963_ASAX, 3);  // Read the x-, y-, and z-axis calibration values
        this.magCalibration = new Data3f(	((float)(rawData[0] - 128))/256f + 1f,   // #KW 841-843 Return x-axis sensitivity adjustment values, etc.
        									((float)(rawData[1] - 128))/256f + 1f,
        									((float)(rawData[2] - 128))/256f + 1f);
        
        ro.writeByteRegister(Registers.AK8963_CNTL1, (byte)0x00); // #KW 844 Power down magnetometer
        Thread.sleep(10);
        // Configure the magnetometer for continuous read and highest resolution
        // set Mscale bit 4 to 1 (0) to enable 16 (14) bit resolution in CNTL1 register,
        // and enable continuous bits data acquisition Mmode (bits [3:0]), 0010 for 8 Hz and 0110 for 100 Hz sample rates
        // set to MagScale.MFS_16BIT.bits and MagMode.MM_100HZ set as final lines 48 & 49. register write should be 0x16
        ro.writeByteRegister(Registers.AK8963_CNTL1, (byte)(magScale.bits | magMode.bits)); // #KW 849 Set magnetometer data resolution and sample ODR ####16bit already shifted
        Thread.sleep(10);
        if (debugLevel() >=3) printState();
        if (debugLevel() >=3) System.out.println("End initAK8963");
	}
}