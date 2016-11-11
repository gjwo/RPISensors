package inertialNavigation;

import java.io.IOException;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import dataTypes.TimestampedData3f;
import com.pi4j.io.i2c.I2CBus;
import devices.I2C.Pi4jI2CDevice;
import sensors.Implementations.MPU9250.MPU9250;
import sensors.interfaces.SensorUpdateListener;


public class Navigate implements Runnable, SensorUpdateListener{
	static Navigate nav ;
	I2CBus bus;
	private MPU9250 mpu9250;
	private static final int SAMPLE_RATE = 10; //sample at 10 Hertz
	private static final int SAMPLE_SIZE = 100; 
	private static final long DELTA_T = 1000000000L/SAMPLE_RATE; // average time difference in between readings in nano seconds
	private Boolean dataReady;
	private int debugLevel;
	
	public static int getSampleRate() {return SAMPLE_RATE;}
	public static long getDeltaT() {return DELTA_T;}
	public int debugLevel() {return debugLevel;}
	
	/**
	 * Navigate - Constructor to use from another class's main program that sets up the and starts the MPU-9250
	 * @param mpu9250
	 */
	public Navigate(MPU9250 mpu9250, int debugLevel)
	{
		dataReady  = false;
        this.mpu9250 = mpu9250;
		this.mpu9250.registerInterest(this);
		this.debugLevel = debugLevel;
    }
	
	/**
	 * run		- This is the thread run loop, it gets the data (if ready) and processes it
	 */
    @Override
    public void run()
    {
    	TimestampedData3f ajustedGyr, ajustedMag;
    	while(!Thread.interrupted())
        {
            if(dataReady) 
            try
            {    
        		dataReady = false;
            	Instruments.setMagnetometer( mpu9250.getLatestGaussianData());
                Instruments.setAccelerometer(mpu9250.getLatestAcceleration());
                Instruments.setGyroscope(mpu9250.getLatestRotationalAcceleration());
                
            	// Examples of calling the filters, READ BEFORE USING!!		!!!
            	// sensors x (y)-axis of the accelerometer is aligned with the y (x)-axis of the magnetometer;
            	// the magnetometer z-axis (+ down) is opposite to z-axis (+ up) of accelerometer and gyro!
            	// We have to make some allowance for this orientation mismatch in feeding the output to the quaternion filter.
            	// For the MPU-9250, we have chosen a magnetic rotation that keeps the sensor forward along the x-axis just like
            	// in the LSM9DS0 sensor. This rotation can be modified to allow any convenient orientation convention.
            	// This is ok by aircraft orientation standards!  
            	// Pass gyro rate as rad/s
            	//  MadgwickQuaternionUpdate(ax, ay, az, gx*PI/180.0f, gy*PI/180.0f, gz*PI/180.0f,  my,  mx, mz);

                ajustedGyr = new TimestampedData3f(Instruments.getGyroscope());
                ajustedGyr.setX(Instruments.getGyroscope().getX()*(float)Math.PI/180.0f); //Pass gyro rate as rad/s
                ajustedGyr.setY(Instruments.getGyroscope().getY()*(float)Math.PI/180.0f);
                ajustedGyr.setZ(Instruments.getGyroscope().getZ()*(float)Math.PI/180.0f);
                ajustedMag = new TimestampedData3f(Instruments.getMagnetometer());
                ajustedMag.setX(Instruments.getMagnetometer().getY()); //swap X and Y, Z stays the same
                ajustedMag.setY(Instruments.getMagnetometer().getX());

                SensorFusion.MadgwickQuaternionUpdate(Instruments.getAccelerometer(),ajustedGyr,ajustedMag,((float)(DELTA_T)/((float)TimestampedData3f.NANOS_PER_SEC)));
                if (debugLevel >=1)
                {
                	System.out.println(	"A " + mpu9250.getAvgAcceleration().toString()+
                						" G " + mpu9250.getAvgRotationalAcceleration().unStamp().toString()+
                						" M "  + mpu9250.getAvgGauss().unStamp().toString()+
                						" | Y,P&R: " + Instruments.getAngles().toString());
                }
                
                Thread.sleep(5);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }       
        }
    }
    
    /**
     * dataUpdated - This is the Sensor Update Listener method, sets a flag for the thead's run loop
     */
	@Override
	public void dataUpdated() {dataReady = true;}
	
	/**
	 * main			- For use in stand alone mode, currently not used the class is initiated from MPU9250Test
	 * @param args
	 */
	public static void main(String[] args)
    {
		MPU9250 mpu9250;
        I2CBus bus = null;
        int debugLevel = 2;

    	try
    	{
    		if (debugLevel>=2) System.out.println("Start Navigate main()");
        	//final GpioController gpio = GpioFactory.getInstance();
            bus = I2CFactory.getInstance(I2CBus.BUS_1); 
            if (debugLevel>=2) System.out.println("Bus acquired");
            mpu9250 = new MPU9250(
                    new Pi4jI2CDevice(bus.getDevice(0x68)), // MPU9250 I2C device
                    new Pi4jI2CDevice(bus.getDevice(0x0C)), // ak8963 I2C 
                    SAMPLE_RATE,                                     // sample rate per second
                    SAMPLE_SIZE,  									// sample size
                    debugLevel);
            if (debugLevel>=2) System.out.println("MPU9250 created");
    		nav = new Navigate(mpu9250,debugLevel);
            nav.mpu9250.registerInterest(nav);
            Thread sensor = new Thread(nav.mpu9250);
            sensor.start();
            final int n = 15;
            Thread.sleep(1000*n); //Collect data for n seconds
            if (debugLevel>=2) System.out.println("Shutdown Sensor");
            sensor.interrupt();
            Thread.sleep(1000);
            if (debugLevel>=2) System.out.println("Shutdown Bus");
            nav.bus.close();
            if (debugLevel>=2) System.out.println("Stop Navigate main()");   
        } catch (InterruptedException | IOException | UnsupportedBusNumberException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}