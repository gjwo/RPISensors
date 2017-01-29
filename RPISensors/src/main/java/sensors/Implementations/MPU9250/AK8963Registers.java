package sensors.Implementations.MPU9250;

import deviceHardwareAbstractionLayer.Register;

/**
 * AK8963Registers  -   Register addesses for the AK8963
 * Created by GJWood on 08/01/2017.
 */
public enum AK8963Registers implements Register
{
    AK8963_ADDRESS   (0x0C), // i2c bus address

    AK8963_WHO_AM_I  (0x00), // should return (0x48
    AK8963_INFO      (0x01),
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
    AK8963_I2CDIS    (0x0F),  // device disable
    AK8963_ASAX      (0x10),  // Fuse ROM x-axis sensitivity adjustment address
    AK8963_ASAY      (0x11),  // Fuse ROM y-axis sensitivity adjustment address
    AK8963_ASAZ      (0x12);  // Fuse ROM z-axis sensitivity adjustment address

    private final int address;
    AK8963Registers(int addr)
    {
        this.address = addr;
    }
    @Override
    public int getAddress() {return this.address;}

    @Override
    public String getName() {return this.name();}
}

//Magnetometer register setting values
//note all magnetometer 16 bit quantities are stored littleEndian
enum MagMode
{
    MM_100HZ   ((byte)0x06,1500), // 6 for 100 Hz ODR continuous magnetometer data read,new mag data is available every 10 ms
    MM_8HZ	 ((byte)0x02,128); // 2 for 8 Hz ODR, continuous magnetometer data read, new mag data is available every 125 ms

    final byte bits;
    final int sampleCount;
    final static byte bitmask = (byte) 0x06;

    MagMode(byte mode,int sampleCount)
    {
        this.bits = mode;
        this.sampleCount = sampleCount;
    }
}

enum MagScale
{ //#KW L722
    MFS_14BIT((byte) 0x00, 10f * 4912f / 8190f),  // #KW L728 mscale val = 0, 14 bit, 5.99755 #KW L234 comment says 0.6 mG per LSB
    MFS_16BIT((byte) 0x10, 10f * 4912f / 32760f); // #KW L731 mscale val = 1, 16 bit, 1.49939 #KW L235 comment says 0.15 mG per LSB

    final byte bits;
    final float res;
    final byte bitMask = (byte) 0x10;

    MagScale(byte bits, float res)
    {
        this.bits = bits;
        this.res = res;
    }
}