package devices.sensorImplementations.MPU9250;

import devices.I2C.I2CImplementation;
import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData1D;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.NineDOF;

import java.io.IOException;
import java.util.Arrays;

/**
 * MPU 9250 motion sensor
 * Created by MAWood on 17/07/2016 with contributions from G.J.Wood
 * Based on MPU9250_MS5637_t3 Basic Example Code by: Kris Winer date: April 1, 2014
 * https://github.com/kriswiner/MPU-9250/blob/master/MPU9250_MS5637_AHRS_t3.ino
 * 
 * Basic MPU-9250 gyroscope, accelerometer and magnetometer functionality including self test, initialisation, and calibration of the sensor,
 * getting properly scaled accelerometer, gyroscope, and magnetometer data out. This class is independent of the bus implementation, register 
 * addressing etc as this is handled by RegisterOperations 
 */
public class MPU9250 extends NineDOF
{
    private static final AccScale accScale = AccScale.AFS_4G;
    private static final GyrScale gyrScale = GyrScale.GFS_2000DPS;


    private final MPU9250RegisterOperations roMPU;
    private final MPU9250RegisterOperations roAK;


	public MPU9250(I2CImplementation mpu9250,I2CImplementation ak8963,int sampleRate, int sampleSize) throws IOException, InterruptedException
    {
        super(sampleRate,sampleSize);
        // get device
        this.roMPU = new MPU9250RegisterOperations(mpu9250);
        this.roAK = new MPU9250RegisterOperations(ak8963);
        gyro = new MPU9250Gyroscope(sampleSize, sampleSize, roMPU);
        mag = new MPU9250Magnetometer(sampleSize, sampleSize, roAK);
        accel = new MPU9250Accelerometer(sampleSize, sampleSize, roMPU);
        therm = new MPU9250Thermometer(sampleSize, sampleSize, roMPU);
        selfTest();
        calibrateGyroAcc();
        initMPU9250();
        initAK8963();
        calibrateMagnetometer();
    }

    private void selfTest() throws IOException, InterruptedException
    {
    	System.out.println("selfTest");
        byte FS = 0; 

        roMPU.writeByteRegister(Registers.SMPLRT_DIV,(byte)0x00); // Set gyro sample rate to 1 kHz
        roMPU.writeByteRegister(Registers.CONFIG,(byte)0x02); // Set gyro sample rate to 1 kHz and DLPF to 92 Hz
        roMPU.writeByteRegister(Registers.GYRO_CONFIG,GyrScale.GFS_250DPS.getValue()); // Set full scale range for the gyro to 250 dps (was FS<<3) 
        roMPU.writeByteRegister(Registers.ACCEL_CONFIG,(byte)AccScale.AFS_2G.getValue());// Set full scale range for the accelerometer to 2 g (was FS<<3 )
        roMPU.writeByteRegister(Registers.ACCEL_CONFIG2,(byte)0x02); // Set accelerometer rate to 1 kHz and bandwidth to 92 Hz
        final int TEST_LENGTH = 200;

        int[] aSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        int[] gSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        short[] registers; 
        for(int s=0; s<TEST_LENGTH; s++)
        {
            //System.out.print("aAvg acc: "+Arrays.toString(aAvg));
            registers = roMPU.read16BitRegisters(Registers.ACCEL_XOUT_H,3);
            aSum[0] += registers[0];
            aSum[1] += registers[1];
            aSum[2] += registers[2];
        	//System.out.format("reg added [%d, %d, %d] [0x%X, 0x%X, 0x%X]%n",
        	//		registers[0],registers[1],registers[2],registers[0],registers[1],registers[2]);

            //System.out.print("gAvg acc: "+Arrays.toString(gAvg));
            registers = roMPU.read16BitRegisters(Registers.GYRO_XOUT_H,3);
            gSum[0] += registers[0];
            gSum[1] += registers[1];
            gSum[2] += registers[2];
        	//System.out.format("reg added [%d, %d, %d] [0x%X, 0x%X, 0x%X]%n",
        	//		registers[0],registers[1],registers[2],registers[0],registers[1],registers[2]);
        }
        short[] aAvg = new short[] {0,0,0};
        short[] gAvg = new short[] {0,0,0};
        for(int i = 0; i<3; i++)
        {
            aAvg[i] = (short) ((short)(aSum[i]/TEST_LENGTH) & (short)0xFFFF); //average and mask off top bits
            gAvg[i] = (short) ((short)(gSum[i]/TEST_LENGTH) & (short)0xFFFF); //average and mask off top bits
        }

        System.out.print("aAvg average: "+Arrays.toString(aAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n", aAvg[0], aAvg[1], aAvg[2]);
        System.out.print("gAvg average: "+Arrays.toString(gAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n", gAvg[0], gAvg[1], gAvg[2]);
        
        // Configure the accelerometer for self-test
        roMPU.writeByteRegister(Registers.ACCEL_CONFIG, (byte)(0xE0 | AccScale.AFS_2G.getValue())); // Enable self test on all three axes and set accelerometer range to +/- 2 g
        roMPU.writeByteRegister(Registers.GYRO_CONFIG, (byte)(0xE0 | GyrScale.GFS_250DPS.getValue()));// Enable self test on all three axes and set gyro range to +/- 250 degrees/s
        Thread.sleep(25); // Delay a while to let the device stabilise
        //outputConfigRegisters();
        int[] aSelfTestSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        int[] gSelfTestSum = new int[] {0,0,0}; //32 bit integer to accumulate and avoid overflow
        
        // get average self-test values of gyro and accelerometer
        for(int s=0; s<TEST_LENGTH; s++) 
        {
            registers = roMPU.read16BitRegisters(Registers.ACCEL_XOUT_H,3);
            aSelfTestSum[0] += registers[0];
            aSelfTestSum[1] += registers[1];
            aSelfTestSum[2] += registers[2];

            registers = roMPU.read16BitRegisters(Registers.GYRO_XOUT_H,3);
            gSelfTestSum[0] += registers[0];
            gSelfTestSum[1] += registers[1];
            gSelfTestSum[2] += registers[2];
        }
        
        short[] aSTAvg = new short[] {0,0,0};
        short[] gSTAvg = new short[] {0,0,0};

        for(int i = 0; i<3; i++)
        {
            aSTAvg[i] = (short) ((short)(aSelfTestSum[i]/TEST_LENGTH) & (short)0xFFFF); //average and mask off top bits
            gSTAvg[i] = (short) ((short)(gSelfTestSum[i]/TEST_LENGTH) & (short)0xFFFF); //average and mask off top bits
        }
        System.out.print("aSTAvg average: "+Arrays.toString(aSTAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n", aSTAvg[0], aSTAvg[1], aSTAvg[2]);
        System.out.print("gSTAvg average: "+Arrays.toString(gSTAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n", aSTAvg[0], aSTAvg[1], aSTAvg[2]);


        // Calculate Accelerometer accuracy
        short[] selfTestAccel = new short[3]; //Longer than byte to allow for removal of sign bit as this is unsigned
        selfTestAccel[0] = (short)((short)roMPU.readByteRegister(Registers.SELF_TEST_X_ACCEL) & 0xFF);
        selfTestAccel[1] = (short)((short)roMPU.readByteRegister(Registers.SELF_TEST_Y_ACCEL) & 0xFF);
        selfTestAccel[2] = (short)((short)roMPU.readByteRegister(Registers.SELF_TEST_Z_ACCEL) & 0xFF);
        System.out.print("Self test Accel bytes: "+Arrays.toString(selfTestAccel));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n", selfTestAccel[0], selfTestAccel[1], selfTestAccel[2]);
        
        float[] factoryTrimAccel = new float[3];
        factoryTrimAccel[0] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestAccel[0] - 1f);
        factoryTrimAccel[1] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestAccel[1] - 1f);
        factoryTrimAccel[2] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestAccel[2] - 1f);
        System.out.println("factoryTrimAcc (float): "+Arrays.toString(factoryTrimAccel)); 

        float[] AccuracyAccel = new float[3];
        AccuracyAccel[0] = 100f*(((float)(aSTAvg[0] - aAvg[0]))/factoryTrimAccel[0]-1f);
        AccuracyAccel[1] = 100f*(((float)(aSTAvg[1] - aAvg[1]))/factoryTrimAccel[1]-1f);
        AccuracyAccel[2] = 100f*(((float)(aSTAvg[2] - aAvg[2]))/factoryTrimAccel[2]-1f);

        System.out.println("Accelerometer accuracy:(% away from factory values)");
        System.out.println("x: " + AccuracyAccel[0] + "%");
        System.out.println("y: " + AccuracyAccel[1] + "%");
        System.out.println("z: " + AccuracyAccel[2] + "%");
               
        // Calculate Gyro accuracy       
        short[] selfTestGyro = new short[3]; //Longer than byte to allow for removal of sign bit as this is unsigned
        selfTestGyro[0] = (short)((short)roMPU.readByteRegister(Registers.SELF_TEST_X_GYRO) & 0xFF);
        selfTestGyro[1] = (short)((short)roMPU.readByteRegister(Registers.SELF_TEST_Y_GYRO) & 0xFF);
        selfTestGyro[2] = (short)((short)roMPU.readByteRegister(Registers.SELF_TEST_Z_GYRO) & 0xFF);
        System.out.println("Self test Gyro bytes: "+Arrays.toString(selfTestGyro));        

        float[] factoryTrimGyro = new float[3];
        factoryTrimGyro[0] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[0] - 1f);
        factoryTrimGyro[1] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[1] - 1f);
        factoryTrimGyro[2] = (float)(2620/1<<FS)*(float)Math.pow(1.01,(float)selfTestGyro[2] - 1f);
        System.out.println("factoryTrimGyro (float): "+Arrays.toString(factoryTrimAccel)); 

        float[] AccuracyGyro = new float[3];
        AccuracyGyro[0] = 100f*(((float)(gSTAvg[0] - gAvg[0]))/factoryTrimGyro[0]-1f);
        AccuracyGyro[1] = 100f*(((float)(gSTAvg[1] - gAvg[1]))/factoryTrimGyro[1]-1f);
        AccuracyGyro[2] = 100f*(((float)(gSTAvg[2] - gAvg[2]))/factoryTrimGyro[2]-1f);
        
        System.out.println("Gyroscope accuracy:(% away from factory values)");
        System.out.println("x: " + AccuracyGyro[0] + "%");
        System.out.println("y: " + AccuracyGyro[1] + "%");
        System.out.println("z: " + AccuracyGyro[2] + "%");

        roMPU.writeByteRegister(Registers.ACCEL_CONFIG, (byte)(0x00 | AccScale.AFS_2G.getValue())); //Clear self test mode and set accelerometer range to +/- 2 g
        roMPU.writeByteRegister(Registers.GYRO_CONFIG,  (byte)(0x00 | GyrScale.GFS_250DPS.getValue())); //Clear self test mode and set gyro range to +/- 250 degrees/s
        
        Thread.sleep(25); // Delay a while to let the device stabilise

        System.out.println("End selfTest");
    }

    private void calibrateGyroAcc() throws IOException, InterruptedException
    {
    	System.out.println("calibrateGyroAcc");
        // Write a one to bit 7 reset bit; toggle reset device
        roMPU.writeByteRegister(Registers.PWR_MGMT_1,(byte)0x80);
        Thread.sleep(100);

        // get stable time source; Auto select clock source to be PLL gyroscope reference if ready
        // else use the internal oscillator, bits 2:0 = 001
        roMPU.writeByteRegister(Registers.PWR_MGMT_1,(byte)0x01);
        roMPU.writeByteRegister(Registers.PWR_MGMT_2,(byte)0x00);
        Thread.sleep(200);


        // Configure device for bias calculation
        roMPU.writeByteRegister(Registers.INT_ENABLE,(byte) 0x00);   // Disable all interrupts
        roMPU.writeByteRegister(Registers.FIFO_EN,(byte) 0x00);      // Disable FIFO
        roMPU.writeByteRegister(Registers.PWR_MGMT_1,(byte) 0x00);   // Turn on internal clock source
        roMPU.writeByteRegister(Registers.I2C_MST_CTRL,(byte) 0x00); // Disable I2C master
        roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x00);    // Disable FIFO and I2C master modes
        roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x0C);    // Reset FIFO and DMP NB the 0x08 bit is the DMP shown as reserved in docs
        Thread.sleep(15);

        // Configure MPU6050 gyro and accelerometer for bias calculation
        roMPU.writeByteRegister(Registers.CONFIG,(byte) 0x01);       // Set low-pass filter to 188 Hz
        roMPU.writeByteRegister(Registers.SMPLRT_DIV,(byte) 0x00);   // Set sample rate to 1 kHz
        roMPU.writeByteRegister(Registers.GYRO_CONFIG,(byte) 0x00);  // Set gyro full-scale to 250 degrees per second, maximum sensitivity
        roMPU.writeByteRegister(Registers.ACCEL_CONFIG,(byte) 0x00); // Set accelerometer full-scale to 2 g, maximum sensitivity


        // Configure FIFO to capture accelerometer and gyro data for bias calculation
        roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x40);   // Enable FIFO
        roMPU.writeByteRegister(Registers.FIFO_EN,(byte) 0x78);     // Enable gyro x,y,z and accelerometer sensors for FIFO  (max size 512 bytes in MPU-9150)
        Thread.sleep(40); // accumulate 40 samples in 40 milliseconds = 480 bytes

        // At end of sample accumulation, turn off FIFO sensor read
        roMPU.writeByteRegister(Registers.FIFO_EN,(byte) 0x00);        // Disable gyro and accelerometer sensors for FIFO

        short packetCount = roMPU.read16BitRegisters( Registers.FIFO_COUNTH, 1)[0];
        int sampleCount =  packetCount / 12; // 12 bytes per sample 6 x 16 bit values

        int[] accelBiasSum = new int[]{0,0,0}; //32 bit to allow for accumulation without overflow
        int[] gyroBiasSum = new int[]{0,0,0}; //32 bit to allow for accumulation without overflow
        short[] tempBias;
        System.out.println("Read Fifo packetCount: "+packetCount);
        
        //Read FIFO
        for(int s = 0; s < sampleCount; s++)
        {
            tempBias = roMPU.read16BitRegisters(Registers.FIFO_R_W,6); //12 bytes
            //System.out.print("bias sample bytes: "+Arrays.toString(tempBias));
        	//System.out.format(" [0x%X, 0x%X, 0x%X, 0x%X, 0x%X, 0x%X]%n",tempBias[0],tempBias[1],tempBias[2],tempBias[3],tempBias[4],tempBias[5]);
            
            accelBiasSum[0] += tempBias[0]; // Sum individual signed 16-bit biases to get accumulated signed 32-bit biases
            accelBiasSum[1] += tempBias[1];
            accelBiasSum[2] += tempBias[2];
            gyroBiasSum[0] += tempBias[3];
            gyroBiasSum[1] += tempBias[4];
            gyroBiasSum[2] += tempBias[5];
        }
        
        //calculate averages
        short[] accelBiasAvg = new short[]{0,0,0}; //16 bit average
        short[] gyroBiasAvg = new short[]{0,0,0}; //16 bit average
        accelBiasAvg[0] = (short)((accelBiasSum[0] / sampleCount) & 0xffff); // Normalise sums to get average count biases
        accelBiasAvg[1] = (short)((accelBiasSum[1] / sampleCount) & 0xffff); 
        accelBiasAvg[2] = (short)((accelBiasSum[2] / sampleCount) & 0xffff); 
        gyroBiasAvg[0] = (short)((gyroBiasSum[0] / sampleCount) & 0xffff);
        gyroBiasAvg[1] = (short)((gyroBiasSum[1] / sampleCount) & 0xffff);
        gyroBiasAvg[2] = (short)((gyroBiasSum[2] / sampleCount) & 0xffff);
        
        System.out.print("Accel Bias average: "+Arrays.toString(accelBiasAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n",accelBiasAvg[0],accelBiasAvg[1],accelBiasAvg[2]);

        System.out.print("Gyro Bias average: "+Arrays.toString(gyroBiasAvg));
    	System.out.format(" [0x%X, 0x%X, 0x%X]%n",gyroBiasAvg[0],gyroBiasAvg[1],gyroBiasAvg[2]);
    	
        //setGyroBiases(gyroBiasAvg);
        //setAccelerometerBiases(accelBiasAvg);
        
    	System.out.println("End calibrateGyroAcc");
    }

    
    private void initMPU9250() throws IOException, InterruptedException
    {
    	System.out.println("initMPU9250");
        // wake up device
        // Clear sleep mode bit (6), enable all sensors
        roMPU.writeByteRegister(Registers.PWR_MGMT_1, (byte)0x00);
        Thread.sleep(100); // Wait for all registers to reset

        // get stable time source
        roMPU.writeByteRegister(Registers.PWR_MGMT_1, (byte)0x01);  // Auto select clock source to be PLL gyroscope reference if ready else
        Thread.sleep(200);

        // Configure Gyro and Thermometer
        // Disable FSYNC and set thermometer and gyro bandwidth to 41 and 42 Hz, respectively;
        // minimum delay time for this setting is 5.9 ms, which means sensor fusion update rates cannot
        // be higher than 1 / 0.0059 = 170 Hz
        // DLPF_CFG = bits 2:0 = 011; this limits the sample rate to 1000 Hz for both
        // With the MPU9250_Pi4j, it is possible to get gyro sample rates of 32 kHz (!), 8 kHz, or 1 kHz
        roMPU.writeByteRegister(Registers.CONFIG, (byte)0x03);

        // Set sample rate = gyroscope output rate/(1 + SMPLRT_DIV)
        roMPU.writeByteRegister(Registers.SMPLRT_DIV, (byte)0x04);  // Use a 200 Hz rate; a rate consistent with the filter update rate
        // determined inset in CONFIG above

        // Set gyroscope full scale range
        // Range selects FS_SEL and AFS_SEL are 0 - 3, so 2-bit values are left-shifted into positions 4:3 (not in java!)
        byte c = roMPU.readByteRegister(Registers.GYRO_CONFIG); // get current GYRO_CONFIG register value
        c = (byte)(c & ~0xE0); // Clear self-test bits [7:5]  ####
        c = (byte)(c & ~0x02); // Clear Fchoice bits [1:0]
        c = (byte)(c & ~0x18); // Clear AFS bits [4:3]
        c = (byte)(c | gyrScale.getValue() ); // Set full scale range for the gyro GFS_2000DP = 0x18 = 24 #### does not require shifting!!!!
        c = (byte)(c | 0x00); // Set Fchoice for the gyro to 11 by writing its inverse to bits 1:0 of GYRO_CONFIG
        roMPU.writeByteRegister(Registers.GYRO_CONFIG, c ); // Write new GYRO_CONFIG value to register

        // Set accelerometer full-scale range configuration
        c = roMPU.readByteRegister(Registers.ACCEL_CONFIG); // get current ACCEL_CONFIG register value
        c = (byte)(c & ~0xE0); // Clear self-test bits [7:5] ####
        c = (byte)(c & ~0x18);  // Clear AFS bits [4:3]
        c = (byte)(c | accScale.getValue() ); // Set full scale range for the accelerometer #### does not require shifting!!!!
        roMPU.writeByteRegister(Registers.ACCEL_CONFIG, c); // Write new ACCEL_CONFIG register value

        // Set accelerometer sample rate configuration
        // It is possible to get a 4 kHz sample rate from the accelerometer by choosing 1 for
        // accel_fchoice_b bit [3]; in this case the bandwidth is 1.13 kHz
        c = roMPU.readByteRegister(Registers.ACCEL_CONFIG2); // get current ACCEL_CONFIG2 register value
        c = (byte)(c & ~0x0F); // Clear accel_fchoice_b (bit 3) and A_DLPFG (bits [2:0])
        c = (byte)(c | 0x03);  // Set accelerometer rate to 1 kHz and bandwidth to 41 Hz 
        roMPU.writeByteRegister(Registers.ACCEL_CONFIG2, c); // Write new ACCEL_CONFIG2 register value

        // The accelerometer, gyro, and thermometer are set to 1 kHz sample rates,
        // but all these rates are further reduced by a factor of 5 to 200 Hz because of the SMPLRT_DIV setting

        // Configure Interrupts and Bypass Enable
        // Set interrupt pin active high, push-pull, hold interrupt pin level HIGH until interrupt cleared,
        // clear on read of INT_STATUS, and enable I2C_BYPASS_EN so additional chips
        // can join the I2C bus and all can be controlled by the Arduino as master
        //ro.writeByteRegister(Registers.INT_PIN_CFG.getValue(), (byte)0x12);  // INT is 50 microsecond pulse and any read to clear
        roMPU.writeByteRegister(Registers.INT_PIN_CFG, (byte)0x22);  // INT is 50 microsecond pulse and any read to clear - as per MPUBASICAHRS_T3
        roMPU.writeByteRegister(Registers.INT_ENABLE, (byte)0x01);  // Enable data ready (bit 0) interrupt
        //roMPU.outputConfigRegisters();
        Thread.sleep(100);
    	System.out.println("End initMPU9250");
    }

    private void initAK8963() throws InterruptedException, IOException
    {
    }
	@Override
    public void calibrateMagnetometer() throws InterruptedException, IOException
    {
    	mag.calibrate();
    }

    @Override
    public void updateAccelerometerData() throws IOException
    {
    	accel.updateData();
    }

    @Override
    public void updateGyroscopeData() throws IOException
    {
    	gyro.updateData();
    }

    @Override
    public void updateMagnetometerData() throws IOException
    {
    	mag.updateData();
    }

    @Override
    public void updateThermometerData() throws Exception
    {
    	therm.updateData();
    }

	@Override
	public TimestampedData3D getLatestAcceleration() {
		return accel.getLatestValue();
	}

	@Override
	public TimestampedData3D getAvgAcceleration() {
		
		return accel.getAvgValue();
	}

	@Override
	public TimestampedData3D getAcceleration(int i) {
		return accel.getValue(i);
	}

	@Override
	public int getAccelerometerReadingCount() {
		return accel.getReadingCount();
	}

	@Override
	public void calibrateAccelerometer() {
		accel.calibrate();
	}

	@Override
	public void selfTestAccelerometer() {
		accel.selfTest();
		
	}

	@Override
	public TimestampedData3D getLatestRotationalAcceleration() {
		return gyro.getLatestValue();
	}

	@Override
	public TimestampedData3D getRotationalAcceleration(int i) {
		return gyro.getValue(i);
	}

	@Override
	public int getGyroscopeReadingCount() {
		return gyro.getReadingCount();
	}

	@Override
	public void calibrateGyroscope() {
		gyro.calibrate();
	}

	@Override
	public void selfTestGyroscope() {
		gyro.selfTest();
	}

	@Override
	public TimestampedData3D getLatestGaussianData() {
		return mag.getLatestValue();
	}

	@Override
	public TimestampedData3D getGaussianData(int i) {
		return mag.getValue(i);
	}

	@Override
	public int getMagnetometerReadingCount() {
		return mag.getReadingCount();
	}

	@Override
	public void selfTestMagnetometer() {
		mag.selfTest();
	}

	@Override
	public float getLatestTemperature() {
		return therm.getLatestValue().getX();
	}

	@Override
	public float getTemperature(int i) {
		return therm.getValue(i).getX();
	}

	@Override
	public int getThermometerReadingCount() {
		return therm.getReadingCount();
	}

	@Override
	public void calibrateThermometer() {
		therm.calibrate();
	}

	@Override
	public void selfTestThermometer() {
		therm.selfTest();
	}

	@Override
	public void updateData() {
		try {
			gyro.updateData();
			mag.updateData();
			accel.updateData();
			therm.updateData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TimestampedData3D getAvgRotationalAcceleration() {
		return gyro.getAvgValue();
	}

	@Override
	public TimestampedData3D getAvgGauss() {
		return mag.getAvgValue();
	}

	@Override
	public float getAvgTemperature() {
		return therm.getAvgValue().getX();
	}

	@Override
	public void initMagnetometer() throws InterruptedException, IOException {
		mag.init();
	}
}