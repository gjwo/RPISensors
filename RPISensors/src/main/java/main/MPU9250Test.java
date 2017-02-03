package main;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;

import hardwareAbstractionLayer.Pi4jI2CDevice;
import devices.driveAssembly.RemoteDriveAssemblyImpl;
import devices.driveAssembly.TankDriveAssembly;
import devices.motors.DCMotor;
import devices.motors.Motor;
//import inertialNavigation.NavResponder;
import hardwareAbstractionLayer.Wiring;
import inertialNavigation.Navigate;
import logging.SystemLog;
import sensors.Implementations.MPU9250.MPU9250;
import sensors.interfaces.UpdateListener;
import subsystems.SubSystem;

class MPU9250Test implements UpdateListener{
	/*
	 * debugLevelTester settings
	 * 0	-	No diagnostic prints
	 * 1	-	User instructions, substantive output
	 * 2	-	Normal user output, progress etc
	 * 3	-	Main class methods entry & exit
	 * 4	-	Internal methods entry and exit
	 * 5	-	variable changes
	 * 6	-	Device/Sensor register summaries
	 * 7	-
	 * 8	-	Loop internal variables
	 * 9	-	Hardware writes
	 */
	private MPU9250 mpu9250;
	private Navigate nav;
	private Thread navigator;
	private Thread sensorPackage;
	private I2CBus i2CBus1 = null;
	//private NavResponder navR;

	/**
	 * MPU9250Test	-	Constructor
	 */
	private MPU9250Test()
	{
		SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.TRACE_MAJOR_STATES,"Attempt to get Bus 1");
        try {
        	//final GpioController gpio = GpioFactory.getInstance();
            i2CBus1 = Wiring.getI2CBus1();
			SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.TRACE_MAJOR_STATES,"Bus acquired");
            // sample rate (SR) per second sensor frequency (SF) is 200
            // sample size (SS) needs to be >= SF/SR or readings will be missed
            // overlap gives smoothing as average is over the sample
            this.mpu9250 = new MPU9250(
                    new Pi4jI2CDevice(i2CBus1.getDevice(0x68)), // MPU9250 device device
                    new Pi4jI2CDevice(i2CBus1.getDevice(0x0C)), // ak8963 device
                    200,                                    // sample rate (SR) per second 
                    250                                    // sample size (SS)
			); 					// debug level
			SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.TRACE_MAJOR_STATES,"MPU9250 created");
			SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.TRACE_MAJOR_STATES,"Starting RMI");
    		try {
    			startRMI();
    		} catch (RemoteException e) {
				SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.ERROR, "Interupted whilst starting RMI");
    			e.printStackTrace();
    		}
			SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.TRACE_MAJOR_STATES,"RMI started");


            this.nav = new Navigate(mpu9250);
            //this.navR = new NavResponder(this.nav,"NavResponder rpi3gjw",debugLevelNavResponder);
            
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }	
	}
	
	/**
	 * Main								-	entry point
	 * @param args						-	arg[0] the number of seconds to generate data for
	 */
	public static void main(String[] args)
    {
    	MPU9250Test tester;
    	int runSecs = 30;
    	int arg1;
    	    	
        if (args.length > 0) {
            try {
                arg1 = Integer.parseInt(args[0]);
                if (arg1 >0){runSecs = arg1;}
                else {
                    System.err.println("Argument" + args[0] + " must be > 0, using default of "+ runSecs + " seconds ");
					SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.ERROR,"Argument" + args[0] + " must be > 0, using default of "+ runSecs + " seconds ");
				}
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer, using default of "+ runSecs + " seconds ");
				SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.ERROR,"Argument" + args[0] + " must be an integer, using default of "+ runSecs + " seconds ");
			}
        } else
        {
        	System.out.println("No run time specified, using default of "+ runSecs + " seconds ");
        }

		tester = new MPU9250Test();

		tester.initialiseTester();
		tester.runMotors();
    	try {
			tester.runTests(runSecs);
		} catch (InterruptedException e) {
			SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.ERROR,"Interupted whilst running tests");
			e.printStackTrace();
		}
		try {
			tester.shutdownTester();
		} catch (InterruptedException e) {
			SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.ERROR,"Interupted whilst shutting down");
			e.printStackTrace();
		}


        System.exit(0);
    }

	private void startRMI() throws RemoteException
	{
		String hostname = "192.168.1.123";
		int port = Registry.REGISTRY_PORT;
		System.setProperty("java.rmi.server.hostname", hostname) ;
		LocateRegistry.createRegistry(port);
	}

	//Tester phases
	private void initialiseTester() {
        this.mpu9250.registerInterest(this);
        //TimeUnit.SECONDS.sleep(3); //Give time to stop movement after mag calibration
        sensorPackage = new Thread(mpu9250);
        sensorPackage.setName("MCU9250 Thread");
        navigator = new Thread(nav);
        navigator.setName("Navigator Thread");
        
	}
	
	private void runTests(int n) throws InterruptedException
	{

		SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.USER_INFORMATION,"SensorPackage starting");
        sensorPackage.start();
        navigator.start();
        //navR.startup();
        TimeUnit.SECONDS.sleep(n); //Collect data for n seconds
	}
	
	private void runMotors() 
	{
        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput RA =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Right motor A", PinState.LOW);
        final GpioPinDigitalOutput RB =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Right motor B", PinState.LOW);
        final GpioPinDigitalOutput LA =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Left motor A", PinState.LOW);
        final GpioPinDigitalOutput LB =
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Left motor B", PinState.LOW);
        Motor left = new DCMotor(LA,LB);
        Motor right = new DCMotor(RA,RB);

        new RemoteDriveAssemblyImpl(new TankDriveAssembly(left,right));
	}
	
	private void shutdownTester() throws InterruptedException
	{
		SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.USER_INFORMATION, "Shutdown NavResponder");
        //navR.interrupt();
		SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.USER_INFORMATION, "Shutdown Navigator");
        navigator.interrupt();
        TimeUnit.SECONDS.sleep(1);
		SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.USER_INFORMATION, "Shutdown Sensor");
        sensorPackage.interrupt();
        TimeUnit.SECONDS.sleep(2);
		SystemLog.log(MPU9250Test.class,SystemLog.LogLevel.USER_INFORMATION, "Shutdown Bus");
		Wiring.closeI2CBus1();
	}
	
	/**
	 * dataUpdated	-	listener for updates to sensor data, commented out while navigate is active
	 */
	@Override
	public void dataUpdated() {
        //System.out.println("### Listener called ###");
        //displaySummaryData();
	}
	
	/**
	 * displaySummaryData	-	displays summary level data from the sensors (averages)
	 */
	@SuppressWarnings("unused")
	private void displaySummaryData()
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
	
	/**
	 * displayAllData	-	displays all available data from the sensors
	 */
	@SuppressWarnings("unused")
	private void displayAllData()
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