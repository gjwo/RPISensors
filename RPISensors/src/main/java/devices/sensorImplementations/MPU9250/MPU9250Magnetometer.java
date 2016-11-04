/**
 * 
 */
package devices.sensorImplementations.MPU9250;

import java.io.IOException;
import java.util.Arrays;

import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.Sensor;
import devices.sensors.interfaces.Magnetometer;

/**
 * @author GJWood
 *
 */
public class MPU9250Magnetometer extends Sensor<TimestampedData3D,Data3D> implements Magnetometer {

	/**
	 * @param sampleRate
	 * @param sampleSize
	 */
    private static final MagScale magScale = MagScale.MFS_16BIT;
    private static final MagMode magMode = MagMode.MAG_MODE_100HZ;

	public MPU9250Magnetometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getLatestGaussianData()
	 */
	@Override
	public TimestampedData3D getLatestGaussianData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getGaussianData(int)
	 */
	@Override
	public TimestampedData3D getGaussianData(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getMagnetometerReadingCount()
	 */
	@Override
	public int getMagnetometerReadingCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#updateMagnetometerData()
	 */
    @Override
	public void updateMagnetometerData() throws Exception {
    	TimestampedData3D raw, adjusted;
        byte dataReady = (byte)(ro.readByteRegister(Registers.AK8963_ST1) & 0x01); //DRDY - Data ready bit0 1 = data is ready
        if (dataReady == 0) return; //no data ready
        
        // data is ready, read it NB bug fix here read was starting from ST1 not XOUT_L
        byte[] buffer = ro.readByteRegisters(Registers.AK8963_XOUT_L, 7); //6 data bytes x,y,z 16 bits stored as little Endian (L/H)

        //roAK.readByteRegister(Registers.AK8963_ST2);// Data overflow bit 3 and data read error status bit 2
        byte status2 = buffer[6]; // Status2 register must be read as part of data read to show device data has been read
        if((status2 & 0x08) == 0) //bit3 HOFL: Magnetic sensor overflow is normal (no Overflow), data is valid
        { // Check if magnetic sensor overflow set, if not then report data
        	raw = new TimestampedData3D(	(short) ((buffer[1] << 8) | buffer[0]), // Turn the MSB and LSB into a signed 16-bit value
        									(short) ((buffer[3] << 8) | buffer[2]), // Data stored as little Endian
        									(short) ((buffer[5] << 8) | buffer[4]));
            
            adjusted = raw.clone();
            
            x *= magScale.getRes()* magScaling[0];
            y *= magScale.getRes()* magScaling[1];
            z *= magScale.getRes()* magScaling[2];

            x -= magBias[0];
            y -= magBias[1];
            z -= magBias[2];

            this.addValue(new TimestampedData3D(x,y,z));
	}

	/* (non-Javadoc)
	 * @see devices.sensors.dataTypes.Sensor1D#updateData()
	 */

	@Override
	public TimestampedData3D getAvgGauss() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void calibrateMagnetometer() {
    	System.out.println("calibrateMag");

        int  mag_bias[] = {0, 0, 0}, mag_scale[] = {0, 0, 0};
        short mag_max[] = {(short)0x8000, (short)0x8000, (short)0x8000},
        		mag_min[] = {(short)0x7FFF, (short)0x7FFF, (short)0x7FFF},
        		mag_temp[] = {0, 0, 0};

        System.out.println("Mag Calibration: Wave device in a figure eight until done!");
        Thread.sleep(4000);

        // shoot for ~fifteen seconds of mag data
        for(int ii = 0; ii < magMode.getSampleCount(); ii++) {
            updateMagnetometerData();  // Read the mag data
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
        
        mag.setValBias(new Data3D(	(float) mag_bias[0]*magScale.getRes()* magScaling[0],  // save mag biases in G for main program
        							(float) mag_bias[1]*magScale.getRes()* magScaling[1],
        							(float) mag_bias[2]*magScale.getRes()* magScaling[2]));

        // Get soft iron correction estimate
        mag_scale[0]  = (mag_max[0] - mag_min[0])/2;  // get average x axis max chord length in counts
        mag_scale[1]  = (mag_max[1] - mag_min[1])/2;  // get average y axis max chord length in counts
        mag_scale[2]  = (mag_max[2] - mag_min[2])/2;  // get average z axis max chord length in counts

        float avg_rad = mag_scale[0] + mag_scale[1] + mag_scale[2];
        avg_rad /= 3.0;

        mag.setValScaling(new Data3D(	avg_rad/((float)mag_scale[0]),
        								avg_rad/((float)mag_scale[1]),
        								avg_rad/((float)mag_scale[2])));
        //!!!!!!!!!!!!!!!  may need another look   as 2 different values of magScaling

        System.out.println("End calibrateMag");
	}

	@Override
	public void selfTestMagnetometer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initMagnetometer() throws InterruptedException, IOException {
    	System.out.println("initAK8963");
        // First extract the factory calibration for each magnetometer axis

        ro.writeByteRegister(Registers.AK8963_CNTL,(byte) 0x00); // Power down magnetometer
        Thread.sleep(10);
        ro.writeByteRegister(Registers.AK8963_CNTL, (byte)0x0F); // Enter Fuse ROM access mode
        Thread.sleep(10);
        byte rawData[] = ro.readByteRegisters(Registers.AK8963_ASAX, 3);  // Read the x-, y-, and z-axis calibration values
        this.setValScaling(new Data3D(	(float)(rawData[0] - 128)/256f + 1f,   // Return x-axis sensitivity adjustment values, etc.
        								(float)(rawData[1] - 128)/256f + 1f,
        								(float)(rawData[2] - 128)/256f + 1f));
        
        ro.writeByteRegister(Registers.AK8963_CNTL, (byte)0x00); // Power down magnetometer
        Thread.sleep(10);
        // Configure the magnetometer for continuous read and highest resolution
        // set Mscale bit 4 to 1 (0) to enable 16 (14) bit resolution in CNTL register,
        // and enable continuous mode data acquisition Mmode (bits [3:0]), 0010 for 8 Hz and 0110 for 100 Hz sample rates
        ro.writeByteRegister(Registers.AK8963_CNTL, (byte)(MagScale.MFS_16BIT.getValue() << 4 | magMode.getMode())); // Set magnetometer data resolution and sample ODR
        Thread.sleep(10);
    	System.out.println("End initAK8963");
	}
    public void setAccelerometerBiases(short[] accelBiasAvg)
    {
        // Construct the accelerometer biases for push to the hardware accelerometer bias registers. These registers contain
        // factory trim values which must be added to the calculated accelerometer biases; on boot up these registers will hold
        // non-zero values. In addition, bit 0 of the lower byte must be preserved since it is used for temperature
        // compensation calculations. Accelerometer bias registers expect bias input as 2048 LSB per g, so that
        // the accelerometer biases calculated above must be divided by 8.
        // XA_OFFSET is a 15 bit quantity with bits 14:7 in the high byte and 6:0 in the low byte with temperature compensation in bit0
        // so having got it in a 16 bit short, and having preserved the bottom bit, the number must be shifted right by 1 or divide by 2
        // to give the correct value for calculations. After calculations it must be shifted left by 1 or multiplied by 2 to get
        // the bytes correct, then the preserved bit0 can be put back before the bytes are written to registers
    	System.out.println("setAccelerometerBiases");

        short accelSensitivity = 16384;  // = 16384 LSB/g - OK in short max 32,767
        if(accelBiasAvg[2] > 0) {accelBiasAvg[2] -= accelSensitivity;}  // Remove gravity from the z-axis accelerometer bias calculation
        else {accelBiasAvg[2] += accelSensitivity;}
    	System.out.format("z adjusted for gravity %d 0x%X%n",accelBiasAvg[2],accelBiasAvg[2]);
       
        short[] accelBiasReg = ro.read16BitRegisters( Registers.XA_OFFSET_H, 3);
        System.out.print("accelBiasReg with temp compensation bit: "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);

        int mask = 0x01; // Define mask for temperature compensation bit 0 of lower byte of accelerometer bias registers
        byte[] mask_bit = new byte[]{0, 0, 0}; // Define array to hold mask bit for each accelerometer bias axis

        for(int s = 0; s < 3; s++) {
            if((accelBiasReg[s] & mask)==1) mask_bit[s] = 0x01; // If temperature compensation bit is set, record that fact in mask_bit
            //divide accelBiasReg by 2 to remove the bottom bit and preserve any sign (java has no unsigned 16 bit numbers)
            accelBiasReg[s] /=2;
        }
        System.out.print("accelBiasReg without temp compensation bit: "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);
        
        // Construct total accelerometer bias, including calculated average accelerometer bias from above
        for (int i = 0; i<3; i++)
        {
        	accelBiasReg[i] -= (accelBiasAvg[i]/8); // Subtract calculated averaged accelerometer bias scaled to 2048 LSB/g (16 g full scale)
        	accelBiasReg[i] *=2; //multiply by two to leave the bottom bit clear and but all the bits in the correct bytes
        }
        System.out.print("(accelBiasReg - biasAvg/8)*2 (16bit): "+Arrays.toString(accelBiasReg));
    	System.out.format(" [0x%X, 0x%X, 0x%X] %n",accelBiasReg[0],accelBiasReg[1],accelBiasReg[2]);

        byte[] buffer = new byte[6];
        
        // XA_OFFSET is a 15 bit quantity with bits 14:7 in the high byte and 6:0 in the low byte with temperature compensation in bit0

        buffer[0] = (byte)((accelBiasReg[0] >> 8) & 0xFF); //Shift down and mask top 8 bits
        buffer[1] = (byte)((accelBiasReg[0])      & 0xFE); //copy bits 7-1 clear bit 0
        buffer[1] = (byte)(buffer[1] | mask_bit[0]); // preserve temperature compensation bit when writing back to accelerometer bias registers
        buffer[2] = (byte)((accelBiasReg[1] >> 8) & 0xFF); //Shift down and mask top 8 bits
        buffer[3] = (byte)((accelBiasReg[1])      & 0xFE); //copy bits 7-1 clear bit 0
        buffer[3] = (byte)(buffer[3] | mask_bit[1]); // preserve temperature compensation bit when writing back to accelerometer bias registers
        buffer[4] = (byte)((accelBiasReg[2] >> 8) & 0xFF); //Shift down and mask top 8 bits
        buffer[5] = (byte)((accelBiasReg[2])      & 0xFE); //copy bits 7-1 clear bit 0
        buffer[5] = (byte)(buffer[5] | mask_bit[2]); // preserve temperature compensation bit when writing back to accelerometer bias registers
        System.out.print("accelBiasReg bytes: "+Arrays.toString(buffer));
    	System.out.format(" [0x%X, 0x%X, 0x%X, 0x%X, 0x%X, 0x%X]%n",buffer[0],buffer[1],buffer[2],buffer[3],buffer[4],buffer[5]);

        // Apparently this is not working for the acceleration biases in the MPU-9250
        // Are we handling the temperature correction bit properly? - see comments above
    	
        // Push accelerometer biases to hardware registers  	
        ro.writeByteRegister(Registers.XA_OFFSET_H, buffer[0]);
        ro.writeByteRegister(Registers.XA_OFFSET_L, buffer[1]);
        ro.writeByteRegister(Registers.YA_OFFSET_H, buffer[2]);
        ro.writeByteRegister(Registers.YA_OFFSET_L, buffer[3]);
        ro.writeByteRegister(Registers.ZA_OFFSET_H, buffer[4]);
        ro.writeByteRegister(Registers.ZA_OFFSET_L, buffer[5]);
        
        // set super class NineDOF variables
        this.setValBias(new Data3D( 	(float)accelBiasAvg[0]/(float)accelSensitivity,
        								(float)accelBiasAvg[1]/(float)accelSensitivity,
        								(float)accelBiasAvg[2]/(float)accelSensitivity));
        System.out.println("accelBias (float): "+Arrays.toString(buffer));
    	System.out.println("End setAccelerometerBiases");
    }

}
