package sensors.Implementations.MPU9250;

import devices.I2C.I2CImplementation;
import sensors.models.NineDOF;

import java.io.IOException;
import java.util.Arrays;

/**
 * MPU 9250 motion sensor
 * Created by MAWood on 17/07/2016 with major rewrite by G.J.Wood
 * Based on MPU9250_MS5637_t3 Basic Example Code by: Kris Winer date: April 1, 2014
 * https://github.com/kriswiner/MPU-9250/blob/master/MPU9250_MS5637_AHRS_t3.ino
 * 
 * Basic MPU-9250 gyroscope, accelerometer and magnetometer functionality including self test, initialisation, and calibration of the sensor,
 * getting properly scaled accelerometer, gyroscope, and magnetometer data out. This class is independent of the bus implementation, register 
 * addressing etc as this is handled by RegisterOperations 
 * Hardware registers controlled by this class
 * 0x19  25 SMPLRT_DIV 	- Sample Rate Divider
 * 0x1A  26 CONFIG		- Configuration
 * 0x23  35 FIFO_EN		- First In First Out Enable
 * 0x6A 106 USER_CTRL	- User Control
 * 0x6B 107 PWR_MGMT_1	- Power Management 1
 * 0x6C 108 PWR_MGMT_2	- Power Management 2
 * 0x72 114 FIFO_COUNT	- First In First Out byte count (13bit BigEndian [12:8],[7:0])
 * 0x74 116 FIFO_R_W	- First In First Out byte
 * 0x75 117 WHO_AM_I	- Device Address
 */
public class MPU9250 extends NineDOF
{
    private final MPU9250RegisterOperations roMPU;
    private final MPU9250RegisterOperations roAK;


	public MPU9250(I2CImplementation mpu9250,I2CImplementation ak8963,int sampleRate, int sampleSize) throws IOException, InterruptedException
    {
        super(sampleRate,sampleSize);
        // get device
        this.roMPU = new MPU9250RegisterOperations(mpu9250);
        this.roAK = new MPU9250RegisterOperations(ak8963);
        gyro = new MPU9250Gyroscope(sampleSize, sampleSize, roMPU,this);
        mag = new MPU9250Magnetometer(sampleSize, sampleSize, roAK,this);
        accel = new MPU9250Accelerometer(sampleSize, sampleSize, roMPU,this);
        therm = new MPU9250Thermometer(sampleSize, sampleSize, roMPU,this);
        selfTest();
        calibrateGyroAcc();
        initMPU9250();
        mag.init();
        calibrateMagnetometer();
    }

    private void selfTest() throws IOException, InterruptedException
    {
    	System.out.println("selfTest");
    	//NB gyro config controlled by general register
    	byte c;
        c = roMPU.readByteRegister(Registers.CONFIG); 
        c = (byte) (c &~GT_DLFP.bitMask|GT_DLFP.DLFP11_2.bits);// Set gyro sample rate to 1 kHz and DLPF to 92 Hz
        roMPU.writeByteRegister(Registers.CONFIG,c ); 
        roMPU.writeByteRegister(Registers.SMPLRT_DIV,(byte)0x00); // Internal_Sample_Rate / (1 + SMPLRT_DIV) for all devices
        gyro.selfTest();
        accel.selfTest();
        
        System.out.println("End selfTest");

    }
    private void setCalibrationMode9250() throws IOException, InterruptedException
    {
    	System.out.println("setCalibrationMode");
        // Write a one to bit 7 reset bit; toggle reset device
        roMPU.writeByteRegister(Registers.PWR_MGMT_1,H_Reset.RESET.bits);
        Thread.sleep(100);

        // get stable time source; Auto select clock source to be PLL gyroscope reference if ready
        // else use the internal oscillator, bits 2:0 = 001
        roMPU.writeByteRegister(Registers.PWR_MGMT_1,ClkSel.AUTO.bits);
        roMPU.writeByteRegister(Registers.PWR_MGMT_2,PwrDisable.ALL_ENABLED.bits); //
        Thread.sleep(200);

        // Configure device for bias calculation
        roMPU.writeByteRegister(Registers.INT_ENABLE,(byte) 0x00);   // Disable all interrupts
        roMPU.writeByteRegister(Registers.FIFO_EN,FIFO_Mode.NONE.bits);      // Disable FIFO
        roMPU.writeByteRegister(Registers.PWR_MGMT_1,ClkSel.AUTO.bits);   // Turn on internal clock source
        roMPU.writeByteRegister(Registers.I2C_MST_CTRL,(byte) 0x00); // Disable I2C master
        roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x00);    // Disable FIFO and I2C master modes
        roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x0C);    // Reset FIFO and DMP NB the 0x08 bit is the DMP shown as reserved in docs
        
        Thread.sleep(15);
        
        roMPU.writeByteRegister(Registers.CONFIG,(byte) 0x01);       // Set low-pass filter to 188 Hz
        roMPU.writeByteRegister(Registers.SMPLRT_DIV,(byte) 0x00);   // Set sample rate to 1 kHz = Internal_Sample_Rate / (1 + SMPLRT_DIV)
    }
    
    
    private void calibrateGyroAcc() throws IOException, InterruptedException
    {
    	System.out.println("calibrateGyroAcc");
    	
    	setCalibrationMode9250();
    	accel.calibrate();
    	gyro.calibrate();

    	System.out.println("End calibrateGyroAcc");
    }

    
    private void initMPU9250() throws IOException, InterruptedException
    {
    	System.out.println("initMPU9250");
        // wake up device
        // Clear sleep bits bit (6), enable all sensors
        roMPU.writeByteRegister(Registers.PWR_MGMT_1, (byte)0x00);
        Thread.sleep(100); // Wait for all registers to reset

        // get stable time source
        roMPU.writeByteRegister(Registers.PWR_MGMT_1, ClkSel.AUTO.bits);  // Auto select clock source to be PLL gyroscope reference if ready else
        Thread.sleep(200);

        // Configure Gyro and Thermometer
        // Disable FSYNC and set thermometer and gyro bandwidth to 41 and 42 Hz, respectively;
        // minimum delay time for this setting is 5.9 ms, which means sensor fusion update rates cannot
        // be higher than 1 / 0.0059 = 170 Hz
        // DLPF_CFG = bits 2:0 = 011; this limits the sample rate to 1000 Hz for both
        // With the MPU9250_Pi4j, it is possible to get gyro sample rates of 32 kHz (!), 8 kHz, or 1 kHz
        roMPU.writeByteRegister(Registers.CONFIG, GT_DLFP.DLFP11_3.bits);//set thermometer and gyro bandwidth to 41 and 42 Hz, respectively;

        // Set sample rate = gyroscope output rate/(1 + SMPLRT_DIV)
        roMPU.writeByteRegister(Registers.SMPLRT_DIV, (byte)0x04);  // Use a 200 Hz rate; a rate consistent with the filter update rate
        // determined inset in CONFIG above
        
        gyro.configure();
        accel.configure();

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
    
    public short[] operateFIFO(FIFO_Mode mode, int msPeriod) throws InterruptedException
    {
    	short[] readings = null;
    	
        roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x40);   // Enable FIFO
        roMPU.writeByteRegister(Registers.FIFO_EN, mode.bits);     // Enable accelerometer sensors for FIFO  (max size 512 bytes in MPU-9150)
        Thread.sleep(msPeriod);

        // At end of sample accumulation, turn off FIFO sensor read
        roMPU.writeByteRegister(Registers.FIFO_EN,FIFO_Mode.NONE.bits);  // Disable all sensors for FIFO

        short readingCount = roMPU.read16BitRegisters( Registers.FIFO_COUNTH, 1)[0];

        System.out.println("Read Fifo packetCount: "+readingCount);
        readings = roMPU.read16BitRegisters(Registers.FIFO_R_W,readingCount);
        System.out.println("Readings"+ Arrays.toString(readings));
    	
    	return readings;
    }

}