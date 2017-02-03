package sensors.Implementations.MPU9250;

import hardwareAbstractionLayer.Device;
import hardwareAbstractionLayer.RegisterOperations;
import logging.SystemLog;
import sensors.models.NineDOF;
import subsystems.SubSystem;

/**
 * MPU-9250 9 degrees of freedom motion sensor implementation
 * @author GJWood
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
 * INT_PIN_CFG
 * INT_ENABLE
 */
public class MPU9250 extends NineDOF
{
    private final RegisterOperations roMPU;
    private final RegisterOperations roAK;

    /**
     * MPU9250 Constructor
     * @param mpu9250		- The IC2 bus for the MCU
     * @param ak8963		- The IC2 bus for the Magnetometer 
     * @param sampleRate	- sample rate in samples per second
     * @param sampleSize	- The number of samples to be captured
     * @throws InterruptedException - Wake up call
     */
    public MPU9250(Device mpu9250, Device ak8963, int sampleRate, int sampleSize) throws InterruptedException
    {
        super(sampleRate,sampleSize);
        // get device
        this.roMPU = new RegisterOperations(mpu9250);
        this.roAK = new RegisterOperations(ak8963);
        gyro = new MPU9250Gyroscope(this.sampleSize, roMPU,this);
        mag = new MPU9250Magnetometer(this.sampleSize, roAK,this);
        accel = new MPU9250Accelerometer(this.sampleSize, roMPU,this);
        therm = new MPU9250Thermometer(this.sampleSize, roMPU,this);
        selfTest();
        calibrateGyroAcc();
        configure();
        mag.configure();
        calibrateMagnetometer();
    }

    /**
     * printRegisters - Prints the contents of registers used by this class
     */
    private void printRegisters()
    {
        roMPU.printByteRegister(MPU9250Registers.CONFIG);
        roMPU.printByteRegister(MPU9250Registers.WOM_THR);
        roMPU.printByteRegister(MPU9250Registers.MOT_DUR);
        roMPU.printByteRegister(MPU9250Registers.ZMOT_THR);
        roMPU.printByteRegister(MPU9250Registers.FIFO_EN);
        roMPU.printByteRegister(MPU9250Registers.I2C_MST_CTRL);
        roMPU.printByteRegister(MPU9250Registers.I2C_MST_STATUS);
        roMPU.printByteRegister(MPU9250Registers.INT_PIN_CFG);
        roMPU.printByteRegister(MPU9250Registers.INT_ENABLE);
        roMPU.printByteRegister(MPU9250Registers.INT_STATUS);
        roMPU.printByteRegister(MPU9250Registers.I2C_MST_DELAY_CTRL);
        roMPU.printByteRegister(MPU9250Registers.SIGNAL_PATH_RESET);
        roMPU.printByteRegister(MPU9250Registers.MOT_DETECT_CTRL);
        roMPU.printByteRegister(MPU9250Registers.USER_CTRL);
        roMPU.printByteRegister(MPU9250Registers.PWR_MGMT_1);
        roMPU.printByteRegister(MPU9250Registers.PWR_MGMT_2);
        roMPU.printByteRegister(MPU9250Registers.WHO_AM_I_MPU9250);
        roMPU.printByteRegister(MPU9250Registers.SMPLRT_DIV);
    }

    /**
     * selfTest - Triggers self test for all the sensors which support it on this device
     * @throws InterruptedException - If sleep was interrupted
     */
    private void selfTest() throws InterruptedException
    {
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"MPU-9250.selfTest");
        //NB gyro config controlled by general register
    	/*
    	byte c;
        c = roMPU.readByteRegister(Registers.CONFIG); 
        c = (byte) (c &~GT_DLPF.bitMask|GT_DLPF.F01BW0092.bits);// Set gyro sample rate to 1 kHz and DLPF to 92 Hz
        roMPU.writeByteRegister(Registers.CONFIG,c ); */
        roMPU.writeBytefield(MPU9250Registers.CONFIG, GT_DLPF.bitMask, GT_DLPF.F01BW0092.bits);
        roMPU.writeByte(MPU9250Registers.SMPLRT_DIV,SampleRateDiv.NONE.bits); // Internal_Sample_Rate / (1 + SMPLRT_DIV) for all devices
        gyro.selfTest();
        accel.selfTest();

        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End MPU-9250.selfTest");
    }

    /**
     * setCalibrationMode9250 - puts the device into calibrate mode
     * @throws InterruptedException - If sleep was interrupted
     */
    private void setCalibrationMode() throws InterruptedException
    {
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"setCalibrationMode");
        // Write a one to bit 7 reset bit; toggle reset device
        roMPU.writeByte(MPU9250Registers.PWR_MGMT_1,H_Reset.RESET.bits);
        Thread.sleep(100);

        // get stable time source; Auto select clock source to be PLL gyroscope reference if ready
        // else use the internal oscillator, bits 2:0 = 001
        roMPU.writeByte(MPU9250Registers.PWR_MGMT_1,ClkSel.AUTO.bits);
        roMPU.writeByte(MPU9250Registers.PWR_MGMT_2,PwrDisable.ALL_ENABLED.bits); //
        Thread.sleep(200);

        // Configure device for bias calculation
        roMPU.writeByte(MPU9250Registers.INT_ENABLE,(byte) 0x00);   // Disable all interrupts
        roMPU.writeByte(MPU9250Registers.FIFO_EN,FIFO_Mode.NONE.bits);      // Disable FIFO
        roMPU.writeByte(MPU9250Registers.PWR_MGMT_1,ClkSel.AUTO.bits);   // Turn on internal clock source
        roMPU.writeByte(MPU9250Registers.I2C_MST_CTRL,(byte) 0x00); // Disable device master
        //roMPU.writeByteRegister(Registers.USER_CTRL,(byte) 0x00);    // Disable FIFO and device master modes
        //Thread.sleep(20);
        roMPU.writeByte(MPU9250Registers.USER_CTRL,(byte) 0x0C);    // Reset FIFO and DMP NB the 0x08 bit is the DMP shown as reserved in docs

        Thread.sleep(15);

        roMPU.writeByte(MPU9250Registers.CONFIG, GT_DLPF.F01BW0184.bits);       // Set low-pass filter to 188 Hz
        roMPU.writeByte(MPU9250Registers.SMPLRT_DIV,SampleRateDiv.NONE.bits);   // Set sample rate to 1 kHz = Internal_Sample_Rate / (1 + SMPLRT_DIV)
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"End setCalibrationMode");
    }

    /**
     * calibrateGyroAcc - puts the device into calibrate mode then calibrates the Gyroscope and Accelerometer
     * @throws InterruptedException - If sleep was interrupted
     */
    private void calibrateGyroAcc() throws InterruptedException
    {
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"calibrateGyroAcc");

        setCalibrationMode();
        accel.calibrate();
        gyro.calibrate();

        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"End calibrateGyroAcc");
    }

    /**
     * configure - Configures the MPU9250 device for normal use and also any sensors that support the configure method
     * @throws InterruptedException - If sleep was interrupted
     */
    private void configure() throws InterruptedException
    {
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"MPU-9250.configure");

        roMPU.writeByte(MPU9250Registers.PWR_MGMT_1, H_Reset.DEFAULT.bits); // wake up device, Clear sleep bits bit (6), enable all sensors
        Thread.sleep(100); // Wait for all registers to reset

        // get stable time source
        roMPU.writeByte(MPU9250Registers.PWR_MGMT_1, ClkSel.AUTO.bits);  // Auto select clock source to be PLL gyroscope reference if ready else
        Thread.sleep(200);

        // Configure Gyro and Thermometer
        // Disable FSYNC and set thermometer and gyro bandwidth to 41 and 42 Hz, respectively;
        // minimum delay time for this setting is 5.9 ms, which means sensor fusion update rates cannot
        // be higher than 1 / 0.0059 = 170 Hz
        // DLPF_CFG = bits 2:0 = 011; this limits the sample rate to 1000 Hz for both
        // With the MPU9250_Pi4j, it is possible to get gyro sample rates of 32 kHz (!), 8 kHz, or 1 kHz
        roMPU.writeByte(MPU9250Registers.CONFIG, GT_DLPF.F01BW0041.bits);//set thermometer and gyro bandwidth to 41 and 42 Hz, respectively;

        // Set sample rate = gyroscope output rate/(1 + SMPLRT_DIV)
        roMPU.writeByte(MPU9250Registers.SMPLRT_DIV, SampleRateDiv.HZ200.bits);  // Use a 200 Hz rate; a rate consistent with the filter update rate
        // determined inset in CONFIG above

        gyro.configure();
        accel.configure();

        // The accelerometer, gyro, and thermometer are set to 1 kHz sample rates,
        // but all these rates are further reduced by a factor of 5 to 200 Hz because of the SMPLRT_DIV setting

        // Configure Interrupts and Bypass Enable
        // Set interrupt pin active high, push-pull, hold interrupt pin level HIGH until interrupt cleared,
        // clear on read of INT_STATUS, and enable I2C_BYPASS_EN so additional chips
        // can join the device bus and all can be controlled by the Arduino as master
        //ro.writeByteRegister(Registers.INT_PIN_CFG.getValue(), (byte)0x12);  // INT is 50 microsecond pulse and any read to clear
        roMPU.writeByte(MPU9250Registers.INT_PIN_CFG, (byte)0x22);  // INT is 50 microsecond pulse and any read to clear - as per MPUBASICAHRS_T3
        roMPU.writeByte(MPU9250Registers.INT_ENABLE, (byte)0x01);  // Enable data ready (bit 0) interrupt

        printRegisters();
        gyro.printRegisters();
        accel.printRegisters();
        mag.printRegisters();
        therm.printRegisters();

        Thread.sleep(100);
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End MPU-9250.configure");
    }

    /**
     * operateFIFO - Sets up the FIFO in the mode requested, captures data for a period, then shuts down the FIFO and returns the data 
     * @param mode		- see the definition of FIFO_Mode
     * @param msPeriod	- capture period in milliseconds
     * @return			- the captured information in a array of signed 16bit shorts
     * @throws InterruptedException - If sleep was interrupted
     */
    public short[] operateFIFO(FIFO_Mode mode, int msPeriod) throws InterruptedException
    {
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"MPU-9250.operateFIFO");
        roMPU.writeByte(MPU9250Registers.USER_CTRL,(byte) 0x40);   // Enable FIFO
        roMPU.writeByte(MPU9250Registers.FIFO_EN, mode.bits);     // Enable accelerometer sensors for FIFO  (max size 512 bytes in MPU-9150)
        Thread.sleep(msPeriod);

        // At end of sample accumulation, turn off FIFO sensor read
        roMPU.writeByte(MPU9250Registers.FIFO_EN,FIFO_Mode.NONE.bits);  // Disable all sensors for FIFO

        int byteCount = roMPU.readShorts( MPU9250Registers.FIFO_COUNTH, 1)[0];
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_VARIABLES, "Read Fifo byte count: " + byteCount);
        int readingCount = byteCount/2;
        short[]readings = new short[readingCount];
        byte high,low;
        for (int i = 0; i<readingCount; i++)
        {
            high = roMPU.readByte(MPU9250Registers.FIFO_R_W);
            low = roMPU.readByte(MPU9250Registers.FIFO_R_W);
            readings[i] = (short) ((high << 8) | (low&0xFF)) ;  // Turn the MSB and LSB into a signed 16-bit value
            //System.out.format("%d: [0x%X, 0x%X] 0x%X %d%n", i,high,low, readings[i],readings[i]);
        }
        SystemLog.log(this.getClass(),SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"End MPU-9250.operateFIFO");
        return readings;
    }

    /**
     * configMagnetometer           - configure the sensor
     * @throws InterruptedException - If sleep was interrupted
     */
    @Override
    public void configMagnetometer() throws InterruptedException {
        mag.configure();
    }
}