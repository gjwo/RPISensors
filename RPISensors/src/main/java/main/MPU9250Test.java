package main;

import java.io.IOException;

//import com.pi4j.io.gpio.GpioController;
//import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import devices.I2C.Pi4jI2CDevice;
import inertialNavigation.Navigate;
import sensors.Implementations.MPU9250.MPU9250;
import sensors.interfaces.SensorUpdateListener;

class MPU9250Test implements SensorUpdateListener{
	MPU9250 mpu9250;
	Navigate nav;

	private MPU9250Test(MPU9250 mpu9250, Navigate nav)
	{
		this.mpu9250 = mpu9250;
		this.nav = nav;
        mpu9250.registerInterest(this);
	}
    public static void main(String[] args)
    {
     	Thread navigator;
    	Thread sensorPackage;
    	MPU9250 mpu9250;
    	Navigate nav;
    	
    	System.out.println("Start MPU9250Test main()");
        I2CBus bus = null;
    	//System.out.println("Attempt to get Bus 1");
        try {
        	//final GpioController gpio = GpioFactory.getInstance();
            bus = I2CFactory.getInstance(I2CBus.BUS_1); 
            System.out.println("Bus acquired");
            mpu9250 = new MPU9250(
                    new Pi4jI2CDevice(bus.getDevice(0x68)), // MPU9250 I2C device
                    new Pi4jI2CDevice(bus.getDevice(0x0C)), // ak8963 I2C 
                    200,                                    // sample rate per second
                    100); 									// sample size
            System.out.println("MPU9250 created");
            nav = new Navigate(mpu9250);
            
            sensorPackage = new Thread(mpu9250);
            navigator = new Thread(nav);
            sensorPackage.start();
            navigator.start();
        	new MPU9250Test(mpu9250,nav);
           
            Thread.sleep(1000*15); //Collect data for n seconds
            
            System.out.println("Shutdown Navigator");
            navigator.interrupt();
            Thread.sleep(1000);
            System.out.println("Shutdown Sensor");
            sensorPackage.interrupt();
            Thread.sleep(1000);
            System.out.println("Shutdown Bus");
            try {
				bus.close();
			} catch (IOException e) {
				// ignore has already been closed! 
			}
        } catch (I2CFactory.UnsupportedBusNumberException | InterruptedException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

	@Override
	public void dataUpdated() {
        //System.out.println("### Listener called ###");
        //displaySummaryData();
	}
	
	public void displaySummaryData()
	{
        int ac = mpu9250.getAccelerometerReadingCount();
        int gc = mpu9250.getGyroscopeReadingCount();
        int mc = mpu9250.getMagnetometerReadingCount();
        System.out.println(	"A("+ac+") " + mpu9250.getAvgAcceleration().toString()+
        					" G("+gc+") " + mpu9250.getAvgRotationalAcceleration().unStamp().toString()+
        					" M("+mc+") "  + mpu9250.getAvgGauss().unStamp().toString());
        /*
        int tc = mpu9250.getThermometerReadingCount();
        System.out.println("ThermReadingCount "+tc);
        System.out.println(" Average Temperature: " + mpu9250.getAvgTemperature() + " C");
        */
	}
	
	public void displayAllData()
	{
        int ac = mpu9250.getAccelerometerReadingCount();
        System.out.println("AccReadingCount "+ac);

        for(int i = ac -1; i>=0; i--)
        {
            System.out.println(" A: " + mpu9250.getAcceleration(i).toString());
        }
        System.out.println("Average Acceleration " + mpu9250.getAvgAcceleration().toString());
        
        int gc = mpu9250.getGyroscopeReadingCount();
        System.out.println("GyroReadingCount "+gc);
        for(int i = gc -1; i>=0; i--)
        {
            System.out.println("G: " + mpu9250.getRotationalAcceleration(i).toString());
        }
        System.out.println("Average Rotation " + mpu9250.getAvgRotationalAcceleration().toString());
        
        int mc = mpu9250.getMagnetometerReadingCount();
        System.out.println("MagReadingCount "+mc);
        for(int i = mc -1; i>=0; i--)
        {
           System.out.println(" M: " + mpu9250.getGaussianData(i).toString());
        }
        System.out.println("Average Gauss " + mpu9250.getAvgGauss().toString());
        
        int tc = mpu9250.getThermometerReadingCount();
        System.out.println("ThermReadingCount "+tc);
        System.out.println(" Average Temperature: " + mpu9250.getAvgTemperature() + " C");
	}
}
