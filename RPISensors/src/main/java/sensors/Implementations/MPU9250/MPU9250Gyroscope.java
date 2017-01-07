package sensors.Implementations.MPU9250;

import java.io.IOException;
import java.util.Arrays;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;
import deviceHardwareAbstractionLayer.RegisterOperations;
import logging.SystemLog;
import sensors.models.Sensor3D;
import subsystems.SubSystem;

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
    private RegisterOperations ro;
    private MPU9250 parent;
	private GyrScale gyroScale; 
	private GT_DLPF cfgDLPF;
	private final short gyroSensitivity = 131;     // 2^16 LSB / 500dps = 131 LSB/degrees/sec

	public MPU9250Gyroscope(int sampleSize, RegisterOperations ro, MPU9250 parent)
	{
		super(sampleSize);
		gyroScale = GyrScale.GFS_2000DPS;
		this.setDeviceScaling( new Data3f(gyroScale.getRes(), gyroScale.getRes(),gyroScale.getRes())); //held in super class 
		this.ro = ro;
		this.parent = parent;
	}

    // Methods that may need extending by sub classes
    protected void logState()
    {
    	super.logState();
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_MAJOR_STATES, "gyroScale: "+ gyroScale.toString()+ " MinMax: "+gyroScale.minMax+" res: "+gyroScale.getRes() );
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_MAJOR_STATES, "cfgDLPF: "+ cfgDLPF);
    }
	
	
	  /**
	   * Prints the contents of registers used by this class 
	   */
	@Override
	public void printRegisters()
	{
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(MPU9250Registers.CONFIG)); //belongs to MPU9250 but holds control data for Gyroscope
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(MPU9250Registers.GYRO_CONFIG));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(MPU9250Registers.SELF_TEST_X_GYRO));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(MPU9250Registers.SELF_TEST_Y_GYRO));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringByteRegister(MPU9250Registers.SELF_TEST_Z_GYRO));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShort(MPU9250Registers.XG_OFFSET_H));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShort(MPU9250Registers.YG_OFFSET_H));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShort(MPU9250Registers.ZG_OFFSET_H));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShort(MPU9250Registers.GYRO_XOUT_H));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShort(MPU9250Registers.GYRO_YOUT_H));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_REGISTER_SUMMARIES, ro.logStringShort(MPU9250Registers.GYRO_ZOUT_H));
	}
	
	public GT_DLPF getDFLP(){return cfgDLPF;}

	@Override
	public void updateData() throws IOException {
        short registers[];
        //ro.readByteRegister(Registers.GYRO_XOUT_H, 6);  // Read again to trigger
        registers = ro.readShorts(MPU9250Registers.GYRO_XOUT_H,3); //GYRO_XOUT = Gyro_Sensitivity * X_angular_rate
        this.addValue(scale(new TimestampedData3f(registers[0],registers[1],registers[2]))); //value depends on which scale is in use
	}
	

	@Override
	public void configure() throws IOException, InterruptedException
	{
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERFACE_METHODS, "gyro.configure");
        ro.writeByte(MPU9250Registers.GYRO_CONFIG,(byte)(	GyrSelfTest.NONE.bits |			// no self test
        													gyroScale.bits |				// Set full scale range for the gyro to 2000 dps
															GyrFchoiceB.FC_USE_DLPF.bits)); // use DLPF settings 
        logState();
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERFACE_METHODS, "End gyro.configure");
	}
	
	@Override
	public void selfTest() throws InterruptedException {
        byte FS = 0; 

        final int TEST_LENGTH = 200;
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"gyro.selfTest");

         int[] gSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        short[] registers; 

        ro.writeByte(MPU9250Registers.GYRO_CONFIG,(byte)(	GyrSelfTest.NONE.bits |			// no self test
        													GyrScale.GFS_250DPS.bits |		// Set full scale range for the gyro to 250 dps
        													GyrFchoiceB.FC_USE_DLPF.bits)); // use DLPF settings 

        for(int s=0; s<TEST_LENGTH; s++)
        {
            registers = ro.readShorts(MPU9250Registers.GYRO_XOUT_H,3);
            gSum[0] += registers[0];
            gSum[1] += registers[1];
            gSum[2] += registers[2];
        	//System.out.format("reg added [%d, %d, %d] [0x%X, 0x%X, 0x%X]%n",
        	//		registers[0],registers[1],registers[2],registers[0],registers[1],registers[2]);
        }
        short[] gAvg = new short[] {0,0,0};
        for(int i = 0; i<3; i++)
        {
            gAvg[i] = (short) ((short)(gSum[i]/TEST_LENGTH) & 0xFFFF); //average and mask off top bits
        }
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "gAvg average: "+Arrays.toString(gAvg));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, String.format(" [0x%X, 0x%X, 0x%X]%n", gAvg[0], gAvg[1], gAvg[2]));
        
        // Configure the Gyroscope for self-test
       ro.writeByte(MPU9250Registers.GYRO_CONFIG, (byte)(	GyrSelfTest.XYZ.bits |			// Enable self test on all three axes
    		   												GyrScale.GFS_250DPS.bits |		// set gyro range to +/- 250 degrees/s
    		   												GyrFchoiceB.FC_USE_DLPF.bits));	// use DLPF settings
        Thread.sleep(25); // Delay a while to let the device stabilise
        //outputConfigRegisters();
        int[] gSelfTestSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        
        // get average self-test values of gyro and accelerometer
        for(int s=0; s<TEST_LENGTH; s++) 
        {
            registers = ro.readShorts(MPU9250Registers.GYRO_XOUT_H,3);
            gSelfTestSum[0] += registers[0];
            gSelfTestSum[1] += registers[1];
            gSelfTestSum[2] += registers[2];
        }
        
        short[] gSTAvg = new short[] {0,0,0};

        for(int i = 0; i<3; i++)
        {
            gSTAvg[i] = (short) ((short)(gSelfTestSum[i]/TEST_LENGTH) & 0xFFFF); //average and mask off top bits
        }
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "gSTAvg average: "+Arrays.toString(gSTAvg));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, String.format(" [0x%X, 0x%X, 0x%X]%n", gSTAvg[0], gSTAvg[1], gSTAvg[2]));
    	
        // Calculate Gyro accuracy       
        short[] selfTestGyro = new short[3]; //Longer than byte to allow for removal of sign bit as this is unsigned
        selfTestGyro[0] = (short)((short)ro.readByte(MPU9250Registers.SELF_TEST_X_GYRO) & 0xFF);
        selfTestGyro[1] = (short)((short)ro.readByte(MPU9250Registers.SELF_TEST_Y_GYRO) & 0xFF);
        selfTestGyro[2] = (short)((short)ro.readByte(MPU9250Registers.SELF_TEST_Z_GYRO) & 0xFF);
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "Self test Gyro bytes: "+Arrays.toString(selfTestGyro));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, String.format(" [0x%X, 0x%X, 0x%X]%n", selfTestGyro[0], selfTestGyro[1], selfTestGyro[2]));

        float[] factoryTrimGyro = new float[3];
        //TODO: investigate this 1<<FS business
        factoryTrimGyro[0] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[0] - 1f);
        factoryTrimGyro[1] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[1] - 1f);
        factoryTrimGyro[2] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[2] - 1f);
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "factoryTrimGyro (float): "+Arrays.toString(factoryTrimGyro));

        float[] AccuracyGyro = new float[3];
        AccuracyGyro[0] = 100f*(((float)(gSTAvg[0] - gAvg[0]))/factoryTrimGyro[0]-1f);
        AccuracyGyro[1] = 100f*(((float)(gSTAvg[1] - gAvg[1]))/factoryTrimGyro[1]-1f);
        AccuracyGyro[2] = 100f*(((float)(gSTAvg[2] - gAvg[2]))/factoryTrimGyro[2]-1f);
        
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.USER_INFORMATION, "Gyroscope accuracy:(% away from factory values)");
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.USER_INFORMATION, "x: " + AccuracyGyro[0] + "%");
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.USER_INFORMATION, "y: " + AccuracyGyro[1] + "%");
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.USER_INFORMATION, "z: " + AccuracyGyro[2] + "%");


        ro.writeByte(MPU9250Registers.GYRO_CONFIG,(byte)(	GyrSelfTest.NONE.bits |			// no self test
															GyrScale.GFS_250DPS.bits |		// Set full scale range for the gyro to 250 dps (was FS<<3)
															GyrFchoiceB.FC_USE_DLPF.bits)); // use DLPF settings 
        
        Thread.sleep(25); // Delay a while to let the device stabilise

        logState();
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End gyro.selfTest");
	}

	@Override
	public void calibrate() throws InterruptedException
	{
		// part of accelgyrocalMPU9250 in Kris Winer code - this code is only the Gyroscope elements
		SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"gyro.calibrate");
    	
    	// Assumes we are in calibration bits via setCalibrationMode9250();

        // Configure MPU6050 gyro for bias calculation
        ro.writeByte(MPU9250Registers.GYRO_CONFIG, GyrScale.GFS_250DPS.bits);  	// Set gyro full-scale to 250 degrees per second, maximum sensitivity

        short[] readings = parent.operateFIFO(FIFO_Mode.GYRO,40); //get a set of readings via the FIFO (MCU9250 function)
        int readingCount = readings.length;
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "Readings length: " + readingCount);

        int sampleCount =  readingCount / 3; // 6 bytes per sample 3 x 16 bit values
        
        int[] gyroBiasSum = new int[]{0,0,0}; //32 bit to allow for accumulation without overflow
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "sampleCount: "+sampleCount);
        
        //Read FIFO
        for(int s = 0; s < sampleCount; s++)
        {
            gyroBiasSum[0] += readings[s]; // Sum individual signed 16-bit biases to get accumulated signed 32-bit biases
            gyroBiasSum[1] += readings[s+1];
            gyroBiasSum[2] += readings[s+2];
        }
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "Gyro Bias sum: "+Arrays.toString(gyroBiasSum));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, String.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasSum[0],gyroBiasSum[1],gyroBiasSum[2]));
       
        //calculate averages
        short[] gyroBiasAvg = new short[]{0,0,0}; //16 bit average
        gyroBiasAvg[0] = (short)((gyroBiasSum[0] / sampleCount) & 0xffff); //mask out any sign extension
        gyroBiasAvg[1] = (short)((gyroBiasSum[1] / sampleCount) & 0xffff);
        gyroBiasAvg[2] = (short)((gyroBiasSum[2] / sampleCount) & 0xffff);

        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "Gyro Bias average: "+Arrays.toString(gyroBiasAvg));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, String.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvg[0],gyroBiasAvg[1],gyroBiasAvg[2]));
    	
        setHardwareBiases(gyroBiasAvg);
        
        // set super class NineDOF variables
        this.setDeviceBias(new Data3f(	(float) gyroBiasAvg[0]/(float) gyroSensitivity,
        								(float) gyroBiasAvg[1]/(float) gyroSensitivity,
        								(float) gyroBiasAvg[2]/(float) gyroSensitivity));

        logState();
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End gyro.calibrate");
	}
	
    private void setHardwareBiases(short[] gyroBiasAvg)
    {
    	SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"setGyroBiases");
    	//OffsetLSB = X_OFFS_USR * 4 / 2^FS_SEL
    	//OffsetDPS = X_OFFS_USR * 4 / 2^FS_SEL / Gyro_Sensitivity
    	
        short[] gyroBiasAvgLSB = new short[] {0,0,0};
        
        // Construct the gyro biases for push to the hardware gyro bias registers, which are reset to zero upon device startup
        // Divide by 4 to get 32.9 LSB per deg/s to conform to expected bias input format
        // Biases are additive, so change sign on calculated average gyro biases
        
        gyroBiasAvgLSB[0] = (short)(-gyroBiasAvg[0]/4);
        gyroBiasAvgLSB[1] = (short)(-gyroBiasAvg[1]/4);
        gyroBiasAvgLSB[2] = (short)(-gyroBiasAvg[2]/4);
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, "gyroBiasAvgLSB: "+Arrays.toString(gyroBiasAvgLSB));
        SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_VARIABLES, String.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvgLSB[0],gyroBiasAvgLSB[1],gyroBiasAvgLSB[2]));
    	
        // Push gyro biases to hardware registers
    	ro.writeShort(MPU9250Registers.XG_OFFSET_H,gyroBiasAvgLSB[0]);
    	ro.writeShort(MPU9250Registers.YG_OFFSET_H,gyroBiasAvgLSB[1]);
    	ro.writeShort(MPU9250Registers.ZG_OFFSET_H,gyroBiasAvgLSB[2]);
         
    	SystemLog.log(SubSystem.SubSystemType.INSTRUMENTS,SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"End setGyroBiases");
    }
}