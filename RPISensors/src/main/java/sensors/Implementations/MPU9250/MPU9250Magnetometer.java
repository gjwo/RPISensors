/**
 * 
 */
package sensors.Implementations.MPU9250;

import java.io.IOException;

import dataTypes.DataFloat3D;
import dataTypes.TimestampedDataFloat3D;
import sensors.models.Sensor3D;

/**
 * @author GJWood
 *
 */
public class MPU9250Magnetometer extends Sensor3D  {

	/**
	 * @param sampleRate
	 * @param sampleSize
	 */
    private static final MagScale magScale = MagScale.MFS_16BIT;
    private static final MagMode magMode = MagMode.MAG_MODE_100HZ;
    private short lastRawMagX;  //needed during calibration
    private short lastRawMagY;
    private short lastRawMagZ;

	public MPU9250Magnetometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getLatestGaussianData()
	 */
    @Override
	public void updateData() {
    	TimestampedDataFloat3D raw;
        byte dataReady = (byte)(ro.readByteRegister(Registers.AK8963_ST1) & 0x01); //DRDY - Data ready bit0 1 = data is ready
        if (dataReady == 0) return; //no data ready
        
        // data is ready, read it NB bug fix here read was starting from ST1 not XOUT_L
        byte[] buffer = ro.readByteRegisters(Registers.AK8963_XOUT_L, 7); //6 data bytes x,y,z 16 bits stored as little Endian (L/H)
        
        // Check if magnetic sensor overflow set, if not then report data	
        //roAK.readByteRegister(Registers.AK8963_ST2);// Data overflow bit 3 and data read error status bit 2
        byte status2 = buffer[6]; // Status2 register must be read as part of data read to show device data has been read
        if((status2 & 0x08) == 0) //bit3 HOFL: Magnetic sensor overflow is normal (no Overflow), data is valid
        { 

        		lastRawMagX = (short) ((buffer[1] << 8) | buffer[0]); // Turn the MSB and LSB into a signed 16-bit value
        		lastRawMagY = (short) ((buffer[3] << 8) | buffer[2]); // Data stored as little Endian
        		lastRawMagZ = (short) ((buffer[5] << 8) | buffer[4]);
        	raw = new TimestampedDataFloat3D(lastRawMagX,lastRawMagY,lastRawMagZ);
            this.addValue(OffsetAndScale(raw));
        }
	}

	/* (non-Javadoc)
	 * @see devices.sensors.dataTypes.Sensor1D#updateData()
	 */

	@Override
	public void calibrate() throws  InterruptedException{
    	System.out.println("calibrateMag");

        int  mag_bias[] = {0, 0, 0}, mag_scale[] = {0, 0, 0};
        short mag_max[] = {(short)0x8000, (short)0x8000, (short)0x8000},
        		mag_min[] = {(short)0x7FFF, (short)0x7FFF, (short)0x7FFF},
        		mag_temp[] = {0, 0, 0};

        System.out.println("Mag Calibration: Wave device in a figure eight until done!");
        Thread.sleep(4000);

        // shoot for ~fifteen seconds of mag data
        for(int ii = 0; ii < magMode.sampleCount; ii++) {
            updateData();  // Read the mag data
            mag_temp[0] = (short) lastRawMagX;
            mag_temp[1] = (short) lastRawMagY;
            mag_temp[2] = (short) lastRawMagZ;
            for (int jj = 0; jj < 3; jj++) {
                if(mag_temp[jj] > mag_max[jj]) mag_max[jj] = mag_temp[jj];
                if(mag_temp[jj] < mag_min[jj]) mag_min[jj] = mag_temp[jj];
            }
            if(magMode == MagMode.MAG_MODE_8HZ) Thread.sleep(135);  // at 8 Hz ODR, new mag data is available every 125 ms
            if(magMode == MagMode.MAG_MODE_100HZ) Thread.sleep(12);  // at 100 Hz ODR, new mag data is available every 10 ms
        }
        // Get hard iron correction
        mag_bias[0]  = (mag_max[0] + mag_min[0])/2;  // get average x mag bias in counts
        mag_bias[1]  = (mag_max[1] + mag_min[1])/2;  // get average y mag bias in counts
        mag_bias[2]  = (mag_max[2] + mag_min[2])/2;  // get average z mag bias in counts

        //!!!!!!!!!!!!!!!  may need another look   as 2 different values of magScaling
        
        this.setValBias(new DataFloat3D(	(float) mag_bias[0]*magScale.res* valScaling.getX(),  // save mag biases in G for main program
        							(float) mag_bias[1]*magScale.res* valScaling.getY(),
        							(float) mag_bias[2]*magScale.res* valScaling.getZ()));

        // Get soft iron correction estimate
        mag_scale[0]  = (mag_max[0] - mag_min[0])/2;  // get average x axis max chord length in counts
        mag_scale[1]  = (mag_max[1] - mag_min[1])/2;  // get average y axis max chord length in counts
        mag_scale[2]  = (mag_max[2] - mag_min[2])/2;  // get average z axis max chord length in counts

        float avg_rad = mag_scale[0] + mag_scale[1] + mag_scale[2];
        avg_rad /= 3.0;

        this.setValScaling(new DataFloat3D(	avg_rad/((float)mag_scale[0]),
        								avg_rad/((float)mag_scale[1]),
        								avg_rad/((float)mag_scale[2])));
        //!!!!!!!!!!!!!!!  may need another look   as 2 different values of magScaling

        System.out.println("End calibrateMag");
	}

	@Override
	public void selfTest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() throws InterruptedException, IOException {
    	System.out.println("initAK8963");
        // First extract the factory calibration for each magnetometer axis

        ro.writeByteRegister(Registers.AK8963_CNTL1,(byte) 0x00); // Power down magnetometer
        Thread.sleep(10);
        ro.writeByteRegister(Registers.AK8963_CNTL1, (byte)0x0F); // Enter Fuse ROM access mode
        Thread.sleep(10);
        byte rawData[] = ro.readByteRegisters(Registers.AK8963_ASAX, 3);  // Read the x-, y-, and z-axis calibration values
        this.setValScaling(new DataFloat3D(	(float)(rawData[0] - 128)/256f + 1f,   // Return x-axis sensitivity adjustment values, etc.
        								(float)(rawData[1] - 128)/256f + 1f,
        								(float)(rawData[2] - 128)/256f + 1f));
        
        ro.writeByteRegister(Registers.AK8963_CNTL1, (byte)0x00); // Power down magnetometer
        Thread.sleep(10);
        // Configure the magnetometer for continuous read and highest resolution
        // set Mscale bit 4 to 1 (0) to enable 16 (14) bit resolution in CNTL1 register,
        // and enable continuous mode data acquisition Mmode (bits [3:0]), 0010 for 8 Hz and 0110 for 100 Hz sample rates
        ro.writeByteRegister(Registers.AK8963_CNTL1, (byte)(MagScale.MFS_16BIT.bits | magMode.mode)); // Set magnetometer data resolution and sample ODR ####16bit already shifted
        Thread.sleep(10);
    	System.out.println("End initAK8963");
	}

}
