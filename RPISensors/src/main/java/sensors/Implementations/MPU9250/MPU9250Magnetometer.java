package sensors.Implementations.MPU9250;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;
import hardwareAbstractionLayer.RegisterOperations;
import logging.SystemLog;
import sensors.models.Sensor3D;
import subsystems.SubSystem;

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

    private final RegisterOperations ro;
    private final MPU9250 parent;
    private final MagScale magScale = MagScale.MFS_16BIT;
    private final MagMode magMode = MagMode.MM_100HZ;
    private short lastRawMagX;  //updated by updateData() needed during calibration 
    private short lastRawMagY;
    private short lastRawMagZ;
    private TimestampedData3f lastCalibratedReading = new TimestampedData3f(0,0,0);
    private Data3f magCalibration = null; //#KW 271 Hardware factory calibration data from AK8963, sent up in init(* param), used in update()

	/**
	 * MPU9250Magnetometer	- 	sensor implementation for this device
	 * @param sampleSize	-	How many sample readings can be held
	 * @param ro			-	Register Operations abstraction for this device
	 * @param parent		- 	encapsulating object usually a sensor package
	 */
	public MPU9250Magnetometer(int sampleSize, RegisterOperations ro, MPU9250 parent ) {
		super(sampleSize);
		this.ro = ro;
		this.parent = parent;
	}
	
    protected void logState()
    {
    	super.logState();
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, "magScale: "+ magScale.toString()+" res: "+magScale.res );
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, "magMode: "+ magMode.toString() + " sampleCount: "+ magMode.sampleCount);
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, "lastRawMagX: "+lastRawMagX+" lastRawMagY: "+lastRawMagY+" lastRawMagZ: "+lastRawMagZ);
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, "lastCalibratedReading: "+lastCalibratedReading.toString());
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_MAJOR_STATES, "magCalibration: "+magCalibration.toString());
    }

	  /**
	   * Prints the contents of registers used by this class 
	   */
	@Override
	public void printRegisters()
	{
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_WHO_AM_I));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_INFO));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_CNTL1));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_CNTL2));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_ASTC));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_ASAX));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_ASAY));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_ASAZ));
	   	//these registers must be read in this order to clear the read flag
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_ST1));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShortLSBfirst(AK8963Registers.AK8963_XOUT_L));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShortLSBfirst(AK8963Registers.AK8963_YOUT_L));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShortLSBfirst(AK8963Registers.AK8963_ZOUT_L));
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(AK8963Registers.AK8963_ST2));
	}
	
	/**
	 * updateData	-	Get a data sample from the device, store in Circular ring array
	 * 
	 * This method is time critical as it is part of the main real time processing loop for the sensor
     */
    @Override
	public void updateData()
    { //#KW loop() L490 calls - readMagData L812
        byte dataReady = (byte)(ro.readByte(AK8963Registers.AK8963_ST1) & 0x01); //DRDY - Data ready bit0 1 = data is ready
        if (dataReady == 0) return; //no data ready
        
        // #KW 494 readMagData - data is ready, read it NB bug fix here read was starting from ST1 not XOUT_L
        byte[] buffer = ro.readBytes(AK8963Registers.AK8963_XOUT_L, 7); // #KW L815 6 data bytes x,y,z 16 bits stored as little Endian (L/H)
        // Check if magnetic sensor overflow set, if not then report data	
        //roAK.readByteRegister(Registers.AK8963_ST2);// Data overflow bit 3 and data read error status bit 2
        byte status2 = buffer[6]; // Status2 register must be read as part of data read to show device data has been read
        if((status2 & 0x08) == 0) //#KW 817 bit3 HOFL: Magnetic sensor overflow is normal (no Overflow), data is valid
        {   //#KW L818-820
        	lastRawMagX = (short) ((buffer[1] << 8) | (buffer[0]&0xFF)); // Turn the MSB and LSB into a signed 16-bit value
        	lastRawMagY = (short) ((buffer[3] << 8) | (buffer[2]&0xFF)); // Data stored as little Endian
        	lastRawMagZ = (short) ((buffer[5] << 8) | (buffer[4]&0xFF)); // mask to prevent sign extension in LSB (bug fix)
	
        	//the stored calibration results is applied here as there is no hardware correction stored in the hardware via calibration
        	//#KW L496-L501. scale() does the multiplication by magScale L499-501
       		lastCalibratedReading = scale(new TimestampedData3f(	lastRawMagX*magScale.res*magCalibration.getX() - getDeviceBias().getX(),
       																lastRawMagY*magScale.res*magCalibration.getY() - getDeviceBias().getY(),
       																lastRawMagZ*magScale.res*magCalibration.getZ() - getDeviceBias().getZ()));
        	this.addValue(lastCalibratedReading); //store the result
        }
	}
	
	// No self Test
	
	/**
	 * Configure -	sets up device for normal operation
	 * 
	 * The method is equivalent to #KW initAK8963 L832
	 */
	@Override
	public void configure() throws InterruptedException {
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"configure mag AK8963");
        // First extract the factory calibration for each magnetometer axis

        ro.writeByte(AK8963Registers.AK8963_CNTL1,(byte) 0x00); // #KW 836 Power down magnetometer
        Thread.sleep(10);
        ro.writeByte(AK8963Registers.AK8963_CNTL1, (byte)0x0F); // #KW 838 Enter Fuse ROM access bits
        Thread.sleep(10);
        byte rawData[] = ro.readBytes(AK8963Registers.AK8963_ASAX, 3);  // Read the x-, y-, and z-axis calibration values
        this.magCalibration = new Data3f(	((float)(rawData[0] - 128))/256f + 1f,   // #KW 841-843 Return x-axis sensitivity adjustment values, etc.
        									((float)(rawData[1] - 128))/256f + 1f,
        									((float)(rawData[2] - 128))/256f + 1f);
        
        ro.writeByte(AK8963Registers.AK8963_CNTL1, (byte)0x00); // #KW 844 Power down magnetometer
        Thread.sleep(10);
        // Configure the magnetometer for continuous read and highest resolution
        // set Mscale bit 4 to 1 (0) to enable 16 (14) bit resolution in CNTL1 register,
        // and enable continuous bits data acquisition Mmode (bits [3:0]), 0010 for 8 Hz and 0110 for 100 Hz sample rates
        // set to MagScale.MFS_16BIT.bits and MagMode.MM_100HZ set as final lines 48 & 49. register write should be 0x16
        ro.writeByte(AK8963Registers.AK8963_CNTL1, (byte)(magScale.bits | magMode.bits)); // #KW 849 Set magnetometer data resolution and sample ODR ####16bit already shifted
        Thread.sleep(10);
        logState();
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End configure mag initAK8963");
	}

	@Override
	public void calibrate() throws  InterruptedException{
		// #KW L1064 magcalMPU9250
		SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"calibrate mag initAK8963");

        int  bias[] = {0, 0, 0}, scale[] = {0, 0, 0};
        short max[] = {(short)-32767, (short)-32767, (short)-32767},
        		min[] = {(short)32767, (short)32767, (short)32767},
        		temp[] = {0, 0, 0};

        SystemLog.log(this.getClass(),SystemLog.LogLevel.USER_INSTRUCTION, "Magnetometer Calibration: Wave device in a figure eight until done!");

        // #KW L1073 shoot for ~fifteen seconds of mag data
        for(int i = 0; i < magMode.sampleCount; i++) {
            updateData();  // Read the mag data
            temp[0] = lastRawMagX;
            temp[1] = lastRawMagY;
            temp[2] = lastRawMagZ;
            for (int j = 0; j < 3; j++) {
                if(temp[j] > max[j]) max[j] = temp[j];
                if(temp[j] < min[j]) min[j] = temp[j];
            }
            if(magMode == MagMode.MM_8HZ) Thread.sleep(135);  // at 8 Hz ODR, new mag data is available every 125 ms
            if(magMode == MagMode.MM_100HZ) Thread.sleep(12);  // at 100 Hz ODR, new mag data is available every 10 ms
        }
        
        // #KW L1090 Get hard iron correction
        bias[0]  = (max[0] + min[0])/2;  // get average x mag bias in counts
        bias[1]  = (max[1] + min[1])/2;  // get average y mag bias in counts
        bias[2]  = (max[2] + min[2])/2;  // get average z mag bias in counts

        this.setDeviceBias(new Data3f(	((float) bias[0])*this.magScale.res*magCalibration.getX(), // save mag biases in G for main program
        								((float) bias[1])*this.magScale.res*magCalibration.getY(),	// deviceBias was dest1 in Kris Winer code
        								((float) bias[2])*this.magScale.res*magCalibration.getZ()));
                
        // #KW L1099 Get soft iron correction estimate
        scale[0]  = (max[0] - min[0])/2;  // get average x axis max chord length in counts
        scale[1]  = (max[1] - min[1])/2;  // get average y axis max chord length in counts
        scale[2]  = (max[2] - min[2])/2;  // get average z axis max chord length in counts

        float avgRad = (float) (scale[0] + scale[1] + scale[2]) / 3.0f; // #KW L1104-5

        this.setDeviceScaling(new Data3f(avgRad/((float)scale[0]), // #KW L1107-9 save mag scale for main program
        								avgRad/((float)scale[1]), // deviceScale was pass by ref dest2 in Kris Winer code
        								avgRad/((float)scale[2])));

        logState();
        printRegisters();
        SystemLog.log(this.getClass(),SystemLog.LogLevel.USER_INSTRUCTION, "Magnetometer Calibration: Finished");
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End calibrate mag initAK8963");
	}
}