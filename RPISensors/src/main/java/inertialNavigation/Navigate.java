package inertialNavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import dataTypes.TimestampedData3f;
import com.pi4j.io.i2c.I2CBus;
import devices.I2C.Pi4jI2CDevice;
import sensors.Implementations.MPU9250.MPU9250;
import sensors.interfaces.UpdateListener;


public class Navigate implements Runnable, UpdateListener{
	static Navigate nav ;
	static private final float nanosPerSecf = ((float)TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
	private I2CBus bus;
	private MPU9250 mpu9250;
	private static final int SAMPLE_RATE = 10; //sample at 10 Hertz
	private static final int SAMPLE_SIZE = 100; 
	private static final long DELTA_T = 1000000000L/SAMPLE_RATE; // average time difference in between readings in nano seconds
	private volatile Boolean dataReady;
	private int debugLevel;
	private float deltaTSec;			// integration interval for both filter schemes time difference fractions of a second
	private long lastUpdateNanoS; 		// used to calculate integration interval using nanotime
	private long nowNanoS;              // used to calculate integration interval using nanotime
	private float sumDeltas;          	//Total time between calculations
	private int countDeltas;			//number of calculations
	private float calculationFrequency;	//calculation frequency in Hz
	private long lastDisplayNanoS;		//used to calculate when to display
	private long displayFrequencyHz;	//display frequency in Hertz
	private boolean stop, dataValid;
    private volatile ArrayList<UpdateListener> listeners;
    
	
	public static int getSampleRate() {return SAMPLE_RATE;}
	public static long getDeltaT() {return DELTA_T;}
	public int debugLevel() {return debugLevel;}
	
	/**
	 * Navigate - Constructor to use from another class's main program that sets up the and starts the MPU-9250
	 * @param mpu9250
	 */
	public Navigate(MPU9250 mpu9250, int debugLevel)
	{
		this.stop = false;
		this.dataReady  = false;
		this.dataValid = false;
        this.mpu9250 = mpu9250;
		this.mpu9250.registerInterest(this);
		this.debugLevel = debugLevel;
		this.deltaTSec = 0.0f;
		this.sumDeltas = 0.0f;
		this.countDeltas = 0;
		this.nowNanoS =  System.nanoTime();
		this.lastUpdateNanoS =  nowNanoS;  	//stop the first iteration having a massive delta
		this.lastDisplayNanoS = nowNanoS;
		this.displayFrequencyHz = 2;		//refresh the display every 1/2 a second
		this.listeners = new ArrayList<UpdateListener>();
    }
	
	/**
	 * run		- This is the thread run loop, it gets the data (if ready) and processes it
	 */
    @Override
    public void run()
    {	//#KW L471 - this maps to part of the loop, in this code getting the data is done in a different thread, which prompts this thread to fetch results
    	TimestampedData3f adjustedAcc, adjustedGyr, adjustedMag;
    	while(!Thread.interrupted()&&!stop)
        {
            try
            {    
                if(dataReady) 
                {	//Store the latest data
	        		dataReady = false;
	            	Instruments.setMagnetometer( mpu9250.getLatestGaussianData()); 		// #KW L492-501 done elsewhere, get the results
	                Instruments.setAccelerometer(mpu9250.getLatestAcceleration());		// #KW L478-481 done elsewhere, get the results
	                Instruments.setGyroscope(mpu9250.getLatestRotationalAcceleration());// #KW L485-488 done elsewhere, get the results
	                dataValid = true;
                }
                if (dataValid) // must have at least one value to start calculations
                {
	                // new data or not recalulate the quaternion every 1 ms
	                
	                //Calculate integration interval
	                nowNanoS = System.nanoTime();
	                deltaTSec = ((float)nowNanoS-lastUpdateNanoS)/nanosPerSecf; // #KW L506
	                lastUpdateNanoS = nowNanoS;									// #KW L507
	                //calculate measurement frequency
	                sumDeltas +=deltaTSec;										// #KW L509
	                countDeltas++;												// #KW L510
	                calculationFrequency = countDeltas/sumDeltas;
	                
	            	// #KW L512 Examples of calling the filters, READ BEFORE USING!!		!!!
	            	// sensors x (y)-axis of the accelerometer is aligned with the y (x)-axis of the magnetometer;
	            	// the magnetometer z-axis (+ down) is opposite to z-axis (+ up) of accelerometer and gyro!
	            	// We have to make some allowance for this orientation mismatch in feeding the output to the quaternion filter.
	            	// For the MPU-9250, we have chosen a magnetic rotation that keeps the sensor forward along the x-axis just like
	            	// in the LSM9DS0 sensor. This rotation can be modified to allow any convenient orientation convention.
	            	// This is ok by aircraft orientation standards!  
	            	// Pass gyro rate as rad/s
	            	// MadgwickQuaternionUpdate(-ax, ay, az, gx*PI/180.0f, -gy*PI/180.0f, -gz*PI/180.0f,  my,  -mx, mz); #KW L521
	
	                adjustedAcc = new TimestampedData3f(Instruments.getAccelerometer());//preserve the timestamp set y & z
	                adjustedAcc.setX(-adjustedAcc.getX());								//-ax
	                
	                adjustedGyr = new TimestampedData3f(Instruments.getGyroscope()); 	//preserve the timestamp
	                adjustedGyr.setX(adjustedGyr.getX()*(float)Math.PI/180.0f); 		//Pass gyro rate as rad/s
	                adjustedGyr.setY(-adjustedGyr.getY()*(float)Math.PI/180.0f);		//-gy
	                adjustedGyr.setZ(-adjustedGyr.getZ()*(float)Math.PI/180.0f);		//-gz
	                
	                adjustedMag = new TimestampedData3f(Instruments.getMagnetometer()); //set timestamp and Z
	                adjustedMag.setX(adjustedMag.getY()); 								//swap X and Y, Z stays the same
	                adjustedMag.setY(-adjustedMag.getX());
	
	                SensorFusion.MadgwickQuaternionUpdate(Instruments.getAccelerometer(),adjustedGyr,adjustedMag,deltaTSec); // #KW L921
	                if(((float)nowNanoS-lastDisplayNanoS)/nanosPerSecf >= 1f/displayFrequencyHz)
	                {
	                	lastDisplayNanoS = nowNanoS;
	                    if (debugLevel >=1)
	                    {
	                    	System.out.print(	"A " + mpu9250.getAvgAcceleration().toString()+
	                    						" G " + mpu9250.getAvgRotationalAcceleration().unStamp().toString()+
	                    						" M "  + mpu9250.getAvgGauss().unStamp().toString()+
	                    						" | Y,P&R: " + Instruments.getAngles().toString());
	                    	System.out.format(	" Freq: %5.1fHz %dk calcs%n",calculationFrequency,countDeltas/1000);
	                    }
	
	                }
                    for(UpdateListener listener:listeners) listener.dataUpdated();
                }
                TimeUnit.MICROSECONDS.sleep(900);// allow for 0.1ms calculation time in the loop to give 1ms interval	            
            } catch (InterruptedException e)
            {
                //close down signal
            	stop = true;
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
    public void registerInterest(UpdateListener listener)
    {
        this.listeners.add(listener);
    }

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