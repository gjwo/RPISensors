package sensors.Implementations.MPU9250;

import java.io.IOException;
import java.util.Arrays;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;
import sensors.models.Sensor3D;

/**
 * MPU 9250 Gyroscope sensor
 * Created by G.J.Wood on 1/11/2016
 * Based on MPU9250_MS5637_t3 Basic Example Code by: Kris Winer date: April 1, 2014
 * https://github.com/kriswiner/MPU-9250/blob/master/MPU9250_MS5637_AHRS_t3.ino
 * 
 * This class handles the operation of the gyroscope sensor and is a subclass of Sensor3D, it provides those methods
 * which are hardware specific to the MPU-9250 such as calibration configuring, self test and update
 * This class is independent of the bus implementation, register addressing etc as this is handled by RegisterOperations
 *  
 * Hardware registers controlled by this class
 * 0x00  0 SELF_TEST_X_GYRO 	- Gyroscope X axis self test byte
 * 0x01  1 SELF_TEST_Y_GYRO 	- Gyroscope Y axis self test byte
 * 0x02  2 SELF_TEST_Y_GYRO 	- Gyroscope Z axis self test byte
 * 0x13 19 XG_OFFSET_H			- Gyroscope X axis bias offset (16 bits big endian)
 * 0x15 21 YG_OFFSET_H			- Gyroscope Y axis bias offset (16 bits big endian)
 * 0x17 23 ZG_OFFSET_H			- Gyroscope Z axis bias offset (16 bits big endian)
 * 0x1B 27 GYRO_CONFIG			- Gyroscope configuration byte
 * 0x43 67 GYRO_XOUT			- Gyroscope X axis reading (16 bits big endian)
 * 0x45 69 GYRO_YOUT			- Gyroscope Y axis reading (16 bits big endian)
 * 0x47 71 GYRO_ZOUT			- Gyroscope Z axis reading (16 bits big endian)
**/
public class MPU9250Gyroscope extends Sensor3D 
{
    protected MPU9250RegisterOperations ro;
    protected MPU9250 parent;
	private GyrScale gyroScale; 
	private GT_DLPF cfgDLPF;

	public MPU9250Gyroscope(int sampleRate, int sampleSize, MPU9250RegisterOperations ro, MPU9250 parent) 
	{
		super(sampleRate, sampleSize);
		gyroScale = GyrScale.GFS_2000DPS;
		this.setValScaling( new Data3f(gyroScale.getRes(), gyroScale.getRes(),gyroScale.getRes()));
		this.ro = ro;
		this.parent = parent;
	}

	  /**
	   * Prints the contents of registers used by this class 
	   */
	@Override
	public void printRegisters()
	{
	   	ro.printByteRegister(Registers.CONFIG); //belongs to MPU9250 but holds control data for Gyroscope
	   	ro.printByteRegister(Registers.GYRO_CONFIG);
	   	ro.printByteRegister(Registers.SELF_TEST_X_GYRO);
	   	ro.printByteRegister(Registers.SELF_TEST_Y_GYRO);
	   	ro.printByteRegister(Registers.SELF_TEST_Z_GYRO);
	   	ro.print16BitRegister(Registers.XG_OFFSET_H);
	   	ro.print16BitRegister(Registers.YG_OFFSET_H);
	   	ro.print16BitRegister(Registers.ZG_OFFSET_H);
	   	ro.print16BitRegister(Registers.GYRO_XOUT_H);
	   	ro.print16BitRegister(Registers.GYRO_YOUT_H);
	   	ro.print16BitRegister(Registers.GYRO_ZOUT_H);
	}
	
	public GT_DLPF getDFLP(){return cfgDLPF;}

	@Override
	public void updateData() throws IOException {
        short registers[];
        //ro.readByteRegister(Registers.GYRO_XOUT_H, 6);  // Read again to trigger
        registers = ro.read16BitRegisters(Registers.GYRO_XOUT_H,3); //GYRO_XOUT = Gyro_Sensitivity * X_angular_rate
        this.addValue(OffsetAndScale(new TimestampedData3f(registers[0],registers[1],registers[2])));
	}
	

	@Override
	public void configure() throws IOException, InterruptedException
	{
        if (debugLevel() >=3) System.out.println("gyro.configure");
        ro.writeByteRegister(Registers.GYRO_CONFIG,(byte)(	GyrSelfTest.NONE.bits |			// no self test
        													gyroScale.bits |				// Set full scale range for the gyro to 2000 dps
															GyrFchoiceB.FC_USE_DLPF.bits)); // use DLPF settings 
        if (debugLevel() >=3) System.out.println("End gyro.configure");
	}
	
	@Override
	public void selfTest() throws InterruptedException {
        byte FS = 0; 

        final int TEST_LENGTH = 200;
        if (debugLevel() >=3) System.out.println("gyro.selfTest");

         int[] gSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        short[] registers; 

        ro.writeByteRegister(Registers.GYRO_CONFIG,(byte)(	GyrSelfTest.NONE.bits |			// no self test
        													GyrScale.GFS_250DPS.bits |		// Set full scale range for the gyro to 250 dps
        													GyrFchoiceB.FC_USE_DLPF.bits)); // use DLPF settings 

        for(int s=0; s<TEST_LENGTH; s++)
        {
            registers = ro.read16BitRegisters(Registers.GYRO_XOUT_H,3);
            gSum[0] += registers[0];
            gSum[1] += registers[1];
            gSum[2] += registers[2];
        	//System.out.format("reg added [%d, %d, %d] [0x%X, 0x%X, 0x%X]%n",
        	//		registers[0],registers[1],registers[2],registers[0],registers[1],registers[2]);
        }
        short[] gAvg = new short[] {0,0,0};
        for(int i = 0; i<3; i++)
        {
            gAvg[i] = (short) ((short)(gSum[i]/TEST_LENGTH) & (short)0xFFFF); //average and mask off top bits
        }
        if (debugLevel() >=5) System.out.print("gAvg average: "+Arrays.toString(gAvg));
        if (debugLevel() >=5) System.out.format(" [0x%X, 0x%X, 0x%X]%n", gAvg[0], gAvg[1], gAvg[2]);
        
        // Configure the Gyroscope for self-test
       ro.writeByteRegister(Registers.GYRO_CONFIG, (byte)(	GyrSelfTest.XYZ.bits |			// Enable self test on all three axes
    		   												GyrScale.GFS_250DPS.bits |		// set gyro range to +/- 250 degrees/s
    		   												GyrFchoiceB.FC_USE_DLPF.bits));	// use DLPF settings
        Thread.sleep(25); // Delay a while to let the device stabilise
        //outputConfigRegisters();
        int[] gSelfTestSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        
        // get average self-test values of gyro and accelerometer
        for(int s=0; s<TEST_LENGTH; s++) 
        {
            registers = ro.read16BitRegisters(Registers.GYRO_XOUT_H,3);
            gSelfTestSum[0] += registers[0];
            gSelfTestSum[1] += registers[1];
            gSelfTestSum[2] += registers[2];
        }
        
        short[] gSTAvg = new short[] {0,0,0};

        for(int i = 0; i<3; i++)
        {
            gSTAvg[i] = (short) ((short)(gSelfTestSum[i]/TEST_LENGTH) & (short)0xFFFF); //average and mask off top bits
        }
        if (debugLevel() >=5) System.out.print("gSTAvg average: "+Arrays.toString(gSTAvg));
        if (debugLevel() >=5) System.out.format(" [0x%X, 0x%X, 0x%X]%n", gSTAvg[0], gSTAvg[1], gSTAvg[2]);
    	
        // Calculate Gyro accuracy       
        short[] selfTestGyro = new short[3]; //Longer than byte to allow for removal of sign bit as this is unsigned
        selfTestGyro[0] = (short)((short)ro.readByteRegister(Registers.SELF_TEST_X_GYRO) & 0xFF);
        selfTestGyro[1] = (short)((short)ro.readByteRegister(Registers.SELF_TEST_Y_GYRO) & 0xFF);
        selfTestGyro[2] = (short)((short)ro.readByteRegister(Registers.SELF_TEST_Z_GYRO) & 0xFF);
        if (debugLevel() >=5) System.out.print("Self test Gyro bytes: "+Arrays.toString(selfTestGyro));        
        if (debugLevel() >=5) System.out.format(" [0x%X, 0x%X, 0x%X]%n", selfTestGyro[0], selfTestGyro[1], selfTestGyro[2]);

        float[] factoryTrimGyro = new float[3];
        factoryTrimGyro[0] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[0] - 1f);
        factoryTrimGyro[1] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[1] - 1f);
        factoryTrimGyro[2] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[2] - 1f);
        if (debugLevel() >=5) System.out.println("factoryTrimGyro (float): "+Arrays.toString(factoryTrimGyro)); 

        float[] AccuracyGyro = new float[3];
        AccuracyGyro[0] = 100f*(((float)(gSTAvg[0] - gAvg[0]))/factoryTrimGyro[0]-1f);
        AccuracyGyro[1] = 100f*(((float)(gSTAvg[1] - gAvg[1]))/factoryTrimGyro[1]-1f);
        AccuracyGyro[2] = 100f*(((float)(gSTAvg[2] - gAvg[2]))/factoryTrimGyro[2]-1f);
        
        if (debugLevel() >=2) 
        {
        	System.out.println("Gyroscope accuracy:(% away from factory values)");
        	System.out.println("x: " + AccuracyGyro[0] + "%");
        	System.out.println("y: " + AccuracyGyro[1] + "%");
        	System.out.println("z: " + AccuracyGyro[2] + "%");
        }

        ro.writeByteRegister(Registers.GYRO_CONFIG,(byte)(	GyrSelfTest.NONE.bits |			// no self test
															GyrScale.GFS_250DPS.bits |		// Set full scale range for the gyro to 250 dps (was FS<<3)
															GyrFchoiceB.FC_USE_DLPF.bits)); // use DLPF settings 
        
        Thread.sleep(25); // Delay a while to let the device stabilise

        if (debugLevel() >=3) System.out.println("End gyro.selfTest");
	}

	@Override
	public void calibrate() throws InterruptedException
	{
		if (debugLevel() >=3) System.out.println("gyro.calibrate");
    	
    	// Assumes we are in calibration bits via setCalibrationMode9250();

        // Configure MPU6050 gyro for bias calculation
        ro.writeByteRegister(Registers.GYRO_CONFIG,(byte) GyrScale.GFS_250DPS.bits);  	// Set gyro full-scale to 250 degrees per second, maximum sensitivity

        short[] readings = parent.operateFIFO(FIFO_Mode.GYRO,40);
        int readingCount = readings.length;
        if (debugLevel() >=5) System.out.println("Readings length: " + readingCount);

        int sampleCount =  readingCount / 3; // 6 bytes per sample 3 x 16 bit values
        
        int[] gyroBiasSum = new int[]{0,0,0}; //32 bit to allow for accumulation without overflow
        if (debugLevel() >=5) System.out.println("sampleCount: "+sampleCount);
        
        //Read FIFO
        for(int s = 0; s < sampleCount; s++)
        {
            gyroBiasSum[0] += readings[s]; // Sum individual signed 16-bit biases to get accumulated signed 32-bit biases
            gyroBiasSum[1] += readings[s+1];
            gyroBiasSum[2] += readings[s+2];
        }
        if (debugLevel() >=5) System.out.print("Gyro Bias sum: "+Arrays.toString(gyroBiasSum));
        if (debugLevel() >=5) System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasSum[0],gyroBiasSum[1],gyroBiasSum[2]);
       
        //calculate averages
        short[] gyroBiasAvg = new short[]{0,0,0}; //16 bit average
        gyroBiasAvg[0] = (short)((gyroBiasSum[0] / sampleCount) & 0xffff);
        gyroBiasAvg[1] = (short)((gyroBiasSum[1] / sampleCount) & 0xffff);
        gyroBiasAvg[2] = (short)((gyroBiasSum[2] / sampleCount) & 0xffff);

        if (debugLevel() >=5) System.out.print("Gyro Bias average: "+Arrays.toString(gyroBiasAvg));
        if (debugLevel() >=5) System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvg[0],gyroBiasAvg[1],gyroBiasAvg[2]);
    	
        setGyroBiases(gyroBiasAvg);
        
        if (debugLevel() >=3) System.out.println("End gyro.calibrate");
	}
	
    private void setGyroBiases(short[] gyroBiasAvg)
    {
    	if (debugLevel() >=4) System.out.println("setGyroBiases");
    	//OffsetLSB = X_OFFS_USR * 4 / 2^FS_SEL
    	//OffsetDPS = X_OFFS_USR * 4 / 2^FS_SEL / Gyro_Sensitivity
    	
        short[] gyroBiasAvgLSB = new short[] {0,0,0};
        
        // Construct the gyro biases for push to the hardware gyro bias registers, which are reset to zero upon device startup
        // Divide by 4 to get 32.9 LSB per deg/s to conform to expected bias input format
        // Biases are additive, so change sign on calculated average gyro biases
        
        gyroBiasAvgLSB[0] = (short)(-gyroBiasAvg[0]/4);
        gyroBiasAvgLSB[1] = (short)(-gyroBiasAvg[1]/4);
        gyroBiasAvgLSB[2] = (short)(-gyroBiasAvg[2]/4);
        if (debugLevel() >=5) System.out.print("gyroBiasAvgLSB: "+Arrays.toString(gyroBiasAvgLSB));
        if (debugLevel() >=5) System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvgLSB[0],gyroBiasAvgLSB[1],gyroBiasAvgLSB[2]);
    	
        // Push gyro biases to hardware registers
    	ro.write16bitRegister(Registers.XG_OFFSET_H,gyroBiasAvgLSB[0]);
    	ro.write16bitRegister(Registers.YG_OFFSET_H,gyroBiasAvgLSB[1]);
    	ro.write16bitRegister(Registers.ZG_OFFSET_H,gyroBiasAvgLSB[2]);
        /* 
        // set super class NineDOF variables
        short gyrosensitivity = 131;     // 2^16 LSB / 500dps = 131 LSB/degrees/sec
        this.setValBias(new Data3f(	(float) gyroBiasAvg[0]/(float) gyrosensitivity,
        							(float) gyroBiasAvg[1]/(float) gyrosensitivity,
        							(float) gyroBiasAvg[2]/(float) gyrosensitivity));
        //System.out.println("gyrBias (float): "+Arrays.toString(gyrBias));
         * 
         */
    	if (debugLevel() >=4) System.out.println("End setGyroBiases");
    }
}
