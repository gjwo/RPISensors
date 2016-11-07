package sensors.Implementations.MPU9250;

/**
 * RPITank
 * Created by MAWood on 09/07/2016.
 */
enum Registers
{
    AK8963_ADDRESS   (0x0C), // i2c bus address
    
    WHO_AM_I_AK8963  (0x00), // should return (0x48
    INFO             (0x01),
    AK8963_ST1       (0x02),  // data ready status bit 0
    AK8963_XOUT_L    (0x03),  // data
    AK8963_XOUT_H    (0x04),
    AK8963_YOUT_L    (0x05),
    AK8963_YOUT_H    (0x06),
    AK8963_ZOUT_L    (0x07),
    AK8963_ZOUT_H    (0x08),
    AK8963_ST2       (0x09),  // Data overflow bit 3 and data read error status bit 2
    AK8963_CNTL1     (0x0A),  // Power down (0000), single-measurement (0001), self-test (1000) and Fuse ROM (1111) modes on bits 3:0
    AK8963_CNTL2     (0x0B),  // Reset bit 0
    AK8963_ASTC      (0x0C),  // Self test control
    AK8963_I2CDIS    (0x0F),  // I2C disable
    AK8963_ASAX      (0x10),  // Fuse ROM x-axis sensitivity adjustment address
    AK8963_ASAY      (0x11),  // Fuse ROM y-axis sensitivity adjustment address
    AK8963_ASAZ      (0x12),  // Fuse ROM z-axis sensitivity adjustment address
    
    SELF_TEST_X_GYRO (0x00),
    SELF_TEST_Y_GYRO (0x01),
    SELF_TEST_Z_GYRO (0x02),
    
    SELF_TEST_X_ACCEL(0x0D),
    SELF_TEST_Y_ACCEL(0x0E),
    SELF_TEST_Z_ACCEL(0x0F),

    XG_OFFSET_H      (0x13),  // User-defined trim values for gyroscope
    XG_OFFSET_L      (0x14),
    YG_OFFSET_H      (0x15),
    YG_OFFSET_L      (0x16),
    ZG_OFFSET_H      (0x17),
    ZG_OFFSET_L      (0x18),
    SMPLRT_DIV       (0x19),
    CONFIG           (0x1A),
    GYRO_CONFIG      (0x1B),
    ACCEL_CONFIG     (0x1C),
    ACCEL_CONFIG2    (0x1D),
    LP_ACCEL_ODR     (0x1E),
    WOM_THR          (0x1F),
    MOT_DUR          (0x20),  // Duration counter threshold for motion interrupt generation, 1 kHz rate, LSB = 1 ms
    ZMOT_THR         (0x21),  // Zero-motion detection threshold bits [7:0]
    ZRMOT_DUR        (0x22),  // Duration counter threshold for zero motion interrupt generation, 16 Hz rate, LSB = 64 ms
    FIFO_EN          (0x23),
    I2C_MST_CTRL     (0x24),
    I2C_SLV0_ADDR    (0x25),
    I2C_SLV0_REG     (0x26),
    I2C_SLV0_CTRL    (0x27),
    I2C_SLV1_ADDR    (0x28),
    I2C_SLV1_REG     (0x29),
    I2C_SLV1_CTRL    (0x2A),
    I2C_SLV2_ADDR    (0x2B),
    I2C_SLV2_REG     (0x2C),
    I2C_SLV2_CTRL    (0x2D),
    I2C_SLV3_ADDR    (0x2E),
    I2C_SLV3_REG     (0x2F),
    I2C_SLV3_CTRL    (0x30),
    I2C_SLV4_ADDR    (0x31),
    I2C_SLV4_REG     (0x32),
    I2C_SLV4_DO      (0x33),
    I2C_SLV4_CTRL    (0x34),
    I2C_SLV4_DI      (0x35),
    I2C_MST_STATUS   (0x36),
    INT_PIN_CFG      (0x37),
    INT_ENABLE       (0x38),
    DMP_INT_STATUS   (0x39),  // Check DMP interrupt
    INT_STATUS       (0x3A),
    ACCEL_XOUT_H     (0x3B),
    ACCEL_XOUT_L     (0x3C),
    ACCEL_YOUT_H     (0x3D),
    ACCEL_YOUT_L     (0x3E),
    ACCEL_ZOUT_H     (0x3F),
    ACCEL_ZOUT_L     (0x40),
    TEMP_OUT_H       (0x41),
    TEMP_OUT_L       (0x42),
    GYRO_XOUT_H      (0x43),
    GYRO_XOUT_L      (0x44),
    GYRO_YOUT_H      (0x45),
    GYRO_YOUT_L      (0x46),
    GYRO_ZOUT_H      (0x47),
    GYRO_ZOUT_L      (0x48),
    EXT_SENS_DATA_00 (0x49),
    EXT_SENS_DATA_01 (0x4A),
    EXT_SENS_DATA_02 (0x4B),
    EXT_SENS_DATA_03 (0x4C),
    EXT_SENS_DATA_04 (0x4D),
    EXT_SENS_DATA_05 (0x4E),
    EXT_SENS_DATA_06 (0x4F),
    EXT_SENS_DATA_07 (0x50),
    EXT_SENS_DATA_08 (0x51),
    EXT_SENS_DATA_09 (0x52),
    EXT_SENS_DATA_10 (0x53),
    EXT_SENS_DATA_11 (0x54),
    EXT_SENS_DATA_12 (0x55),
    EXT_SENS_DATA_13 (0x56),
    EXT_SENS_DATA_14 (0x57),
    EXT_SENS_DATA_15 (0x58),
    EXT_SENS_DATA_16 (0x59),
    EXT_SENS_DATA_17 (0x5A),
    EXT_SENS_DATA_18 (0x5B),
    EXT_SENS_DATA_19 (0x5C),
    EXT_SENS_DATA_20 (0x5D),
    EXT_SENS_DATA_21 (0x5E),
    EXT_SENS_DATA_22 (0x5F),
    EXT_SENS_DATA_23 (0x60),
    MOT_DETECT_STATUS (0x61),
    I2C_SLV0_DO      (0x63),
    I2C_SLV1_DO      (0x64),
    I2C_SLV2_DO      (0x65),
    I2C_SLV3_DO      (0x66),
    I2C_MST_DELAY_CTRL (0x67),
    SIGNAL_PATH_RESET  (0x68),
    MOT_DETECT_CTRL  (0x69),
    USER_CTRL        (0x6A),  // Bit 7 enable DMP, bit 3 reset DMP
    PWR_MGMT_1       (0x6B), // Device defaults to the SLEEP mode
    PWR_MGMT_2       (0x6C),
    DMP_BANK         (0x6D),  // Activates a specific bank in the DMP
    DMP_RW_PNT       (0x6E),  // Set read/write pointer to a specific start address in specified DMP bank
    DMP_REG          (0x6F),  // Register in DMP from which to read or to which to write
    DMP_REG_1        (0x70),
    DMP_REG_2        (0x71),
    FIFO_COUNTH      (0x72),
    FIFO_COUNTL      (0x73),
    FIFO_R_W         (0x74),
    WHO_AM_I_MPU9250 (0x75), // Should return (0x71
    XA_OFFSET_H      (0x77),
    XA_OFFSET_L      (0x78),
    YA_OFFSET_H      (0x7A),
    YA_OFFSET_L      (0x7B),
    ZA_OFFSET_H      (0x7D),
    ZA_OFFSET_L      (0x7E),


    SELF_TEST_A      (0x10);


    private final int address;
    Registers(int addr)
    {
        this.address = addr;
    }
    public int getAddress()
    {
        return address;
    }
}
// Register setting values
// Convention1 - the 'bits' field holds the required bit settings in the correct bit positions in the register
// Convention2 - the 'bitMask' field holds the bit mask to mask the bits in the correct bit position
// Convention3 - fields intended for 8 8 bit register are held as an 8 bit byte

//MPU9250 general register setting values
enum FIFO_Mode
{
	NONE((byte)0x00),
	GYRO((byte)0x70),
	ACC((byte)0x08),
	GYRO_ACC((byte)0x78);
	
 final byte bits;
 final byte bitMask = (byte) 0x78;
 
 FIFO_Mode(byte bits)  { this.bits = bits; }
}
//PWR_MGMT_1 contains H_Reset and CLKSEL (and some other standby stuff)
enum H_Reset
{
	RESET((byte)0x80); //1 – Reset the internal registers and restores the default settings. Write a 1 to	set the reset, the bit will auto clear.
	byte bits;
	byte bitmask = (byte) 0x80;
	H_Reset(byte hr){bits = hr;}
}
enum ClkSel
{
	INT20MHZ((byte)0x00), //Internal 20MHz oscillator
	AUTO((byte)0x01);	  //Auto selects the best available clock source – PLL if ready, else use the Internal oscillator
	
 final byte bits;
 final byte bitMask = (byte) 0x07;
 
 ClkSel(byte bits)  { this.bits = bits; }
}
//PWR_MGMT_2 contains disable bits for the Gyroscope and Accelerometer
enum PwrDisable
{	//Acc X(bit5), Y(bit4), Z(bit3) and Gyro X(bit2),Y(bit1), Z(bit0). Bit set to 1 = disables element
	ALL_ENABLED((byte)0x00), //Internal 20MHz oscillator
	ALL_DISABLED((byte)0x3F);	  //Auto selects the best available clock source – PLL if ready, else use the Internal oscillator
	
 final byte bits;
 final byte bitMask = (byte) 0x3F;
 
 PwrDisable(byte bits)  { this.bits = bits; }
}

// Accelerometer register setting values
enum AccSelfTest
{
	NONE((byte)0x00),   // normal mode, no self testing 
	X_ONLY((byte)0x80), // Self test X axis only
	Y_ONLY((byte)0x40), // Self test Y axis only
	Z_ONLY((byte)0x20), // Self test Z axis only
	XYZ((byte)0xE0);    // Self test X, Y & Z axes

	final byte bits;
	final byte bitmask = (byte)0xE0;
	
	AccSelfTest(byte st){bits=st;};
}

enum AccScale
{
    AFS_2G((byte)0x00,2),
    AFS_4G((byte)0x08,4),
    AFS_8G((byte)0x10,8),
    AFS_16G((byte)0x18,16);

    final byte bits;
    final int minMax;
    final byte bitMask = (byte) 0x18;
    
    AccScale(byte value, int minMax)
    {
        this.bits = value;
        this.minMax = minMax;
    }
    
    public float getRes() {return minMax/32768.0f;}
}
enum A_DLFP
{
	// Accelerometer Configuration 2 (MPU-9250 0x1D)
	// The data output rate of the DLPF filter block can be further reduced by a factor of 1/(1+SMPLRT_DIV),
	// where SMPLRT_DIV is an 8-bit integer. Following is a small subset of ODRs that are configurable for the
	// accelerometer in the normal mode in this manner (Hz):
	// 3.91, 7.81, 15.63, 31.25, 62.50, 125, 250, 500, 1K
	// ACCEL_FCHOICE_B is ACCEL_CONFIG_2 bit 3(the inverted version of accel_fchoice as described in the table below).
	// The literals represent choices of ACCEL_FCHOICE + A_DLPF_CFG. So ADLPF1_2 would be 
	// the pattern '010' in the lowest 3 bits of ACCEL_CONFIG2
	ADLPF0_X((byte)0, 1046f,  4,  0.503f, 300), //DLPF bits not relevant (bits)
	ADLPF1_0((byte)0, 218.1f, 1,  1.88f, 300),  //DLPF bits are relevant (bits)
	ADLPF1_1((byte)1, 218.1f, 1,  1.88f, 300),
	ADLPF1_2((byte)2,  99f,   1,  2.88f, 300),
	ADLPF1_3((byte)3,  44.8f, 1,  4.88f, 300),
	ADLPF1_4((byte)4,  21.2f, 1,  8.87f, 300),
	ADLPF1_5((byte)5,  10.2f, 1, 16.83f, 300),
	ADLPF1_6((byte)6,   5.05f,1, 32.48f, 300),	
	ADLPF1_7((byte)7, 420f,   1,  1.38f, 300);	byte bits; 
	
	final float accelBandWidthHz;
	final int rateKHz;
	final float  delayMs;
	final int noiseDensity ;
    final byte bitMask = (byte) 0x07;

	A_DLFP(byte b, float abw, int rate, float delay, int noise)
	{
		bits = b; 
	    accelBandWidthHz = abw;
	    rateKHz = rate;
	    delayMs = delay;
	    noiseDensity = noise;
	}
}
// Gyroscope (and Thermometer) register setting values
enum GyrSelfTest
{
	NONE((byte)0x00),   // normal mode, no self testing 
	X_ONLY((byte)0x80), // Self test X axis only
	Y_ONLY((byte)0x40), // Self test Y axis only
	Z_ONLY((byte)0x20), // Self test Z axis only
	XYZ((byte)0xE0);    // Self test X, Y & Z axes

	final byte bits;
	final byte bitmask = (byte)0xE0;
	
	GyrSelfTest(byte st){bits=st;};
}
enum GyrScale
{
    GFS_250DPS((byte)0x00,250),  //Gyro Full Scale Select: 250dps
    GFS_500DPS((byte)0x08,500),  //Gyro Full Scale Select: 500dps
    GFS_1000DPS((byte)0x10,1000),//Gyro Full Scale Select: 1000dps
    GFS_2000DPS((byte)0x18,2000);//Gyro Full Scale Select: 2000dps

    final byte bits;
    final int minMax;
    final byte bitMask = (byte) 0x18;
    
    GyrScale(byte bits, int minMax)
    {
        this.bits = bits;
        this.minMax = minMax;
    }
    
    public float getRes() {return minMax/32768.0f;}
}

enum GT_DLFP
{
	//The DLPF is configured by DLPF_CFG, when FCHOICE_B [1:0] = 2b’00. The gyroscope and
	//temperature sensor are filtered according to the bits of DLPF_CFG and FCHOICE_B as shown in
	//the table below. Note that FCHOICE mentioned in the table below is the inverted bits of
	//FCHOICE_B (e.g. FCHOICE=2b’00 is same as FCHOICE_B=2b’11). 
	
	DLFPx0_x((byte)0,8800, 0.064f, 32, 4000, 0.04f), //DLPF bits not relevant (bits)
	DLFP01_x((byte)0,3600, 0.11f, 32, 4000, 0.04f),
	DLFP11_0((byte)0,250, 0.97f, 8, 4000, 0.04f),
	DLFP11_1((byte)1,184, 2.9f, 1, 188, 1.9f),
	DLFP11_2((byte)2,92, 3.9f, 1, 98, 2.8f),
	DLFP11_3((byte)3,41, 5.9f, 1, 42, 4.8f),
	DLFP11_4((byte)4,20, 9.9f, 1, 20, 8.3f),
	DLFP11_5((byte)5,10, 17.85f, 1, 10, 13.4f),
	DLFP11_6((byte)6,5, 33.48f, 1, 5, 18.6f),
	DLFP11_7((byte)7,3600, 0.17f, 8, 4000, 0.04f);
	
    final byte bits;
    final int gyroBandWidth;
    final float gyroDelay;
    final int gyroFs;
    final int thermBandwidth;
    final float thermDelay;
    final byte bitMask = (byte) 0x07;
    
	GT_DLFP(byte b, int gbw, float gd,int gf, int tbw, float tf)
	{
		bits = b; 
	    gyroBandWidth = gbw;
	    gyroDelay = gd;
	    gyroFs = gf;
	    thermBandwidth =tbw;
	    thermDelay = tf;
	}
}

//Magnetometer register setting values
//note all magnetometer 16 bit quantities are stored littleEndian
enum MagMode
{
 MM_100HZ   ((byte)0x06,1500), // 6 for 100 Hz continuous magnetometer data read
 MM_8HZ	 ((byte)0x02,128); // 2 for 8 Hz, continuous magnetometer data read

 final byte mode;
 final int sampleCount;
 
 MagMode(byte mode,int sampleCount)
 {
 	this.mode = mode;
 	this.sampleCount = sampleCount;
 }  
}

enum MagScale
{
 MFS_14BIT((byte)0x00,10f*4912f/8190f), //mscale val = 0, 14 bit
 MFS_16BIT((byte)0x10,10f*4912f/32760f); //mscale val = 1, 16 bit

 final byte bits;
 final float res;
 final byte bitMask = (byte) 0x10;
 
 MagScale(byte bits, float res)
 {
     this.bits = bits;
     this.res = res;
 }
}
