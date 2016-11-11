package sensors.models;

import java.io.IOException;

import dataTypes.Data1f;
import dataTypes.TimestampedData1f;
import dataTypes.TimestampedData3f;
import sensors.interfaces.Accelerometer;
import sensors.interfaces.Gyroscope;
import sensors.interfaces.Magnetometer;
import sensors.interfaces.Thermometer;

public abstract class NineDOF extends SensorPackage implements Accelerometer, Gyroscope, Magnetometer, Thermometer
{
	protected Sensor3D mag;
	protected Sensor3D accel;
	protected Sensor3D gyro;
	protected Sensor<TimestampedData1f,Data1f> therm;
	protected int sampleSize;

	protected NineDOF(int sampleRate, int sampleSize, int debugLevel) 
	{
		super(sampleRate,debugLevel);
		this.sampleSize = sampleSize;		
	}

	// Get average named sensor values
	public TimestampedData3f getAvgAcceleration() {return accel.getAvgValue();}
	public TimestampedData3f getAvgGauss() {return mag.getAvgValue();}
	public TimestampedData3f getAvgRotationalAcceleration() {return gyro.getAvgValue();}
	public float getAvgTemperature() {return therm.getAvgValue().getX();}
	// Get latest named sensor values
	public TimestampedData3f getLatestAcceleration() {return accel.getLatestValue();}
	public TimestampedData3f getLatestGaussianData() {return mag.getLatestValue();}
	public TimestampedData3f getLatestRotationalAcceleration() {return gyro.getLatestValue();}
	public float getLatestTemperature() {return therm.getLatestValue().getX();}
	// Get specific named sensor values
    public TimestampedData3f getAcceleration(int i) {return accel.getValue(i);}
	public TimestampedData3f getGaussianData(int i) {return mag.getValue(i);}
	public TimestampedData3f getRotationalAcceleration(int i) {return gyro.getValue(i);}
	public float getTemperature(int i) {return therm.getValue(i).getX();}
	// Get named sensor reading counts
	public int getAccelerometerReadingCount() {return accel.getReadingCount();}
	public int getGyroscopeReadingCount() {return gyro.getReadingCount();}
	public int getMagnetometerReadingCount() {return mag.getReadingCount();}
	public int getThermometerReadingCount() {return therm.getReadingCount();}
	
	//calibrate sensors
    public void calibrateAccelerometer()
    {
		try {
			accel.calibrate();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

    public void calibrateGyroscope()
    {
		try {
			gyro.calibrate();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

    public void calibrateMagnetometer()
    {
    	try {
			mag.calibrate();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void calibrateThermometer() {
		try {
			therm.calibrate();
		} catch( InterruptedException|IOException e) {
			e.printStackTrace();
		}
	}

	//initialise sensors for normal operation
	public void initMagnetometer()
	{
		try {
			mag.init();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	//Self test sensors
	public void selfTestAccelerometer() {
		try {
			accel.selfTest();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selfTestGyroscope() {
		try {
			gyro.selfTest();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selfTestMagnetometer() {
		try {
			mag.selfTest();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void selfTestThermometer() {
		try {
			therm.selfTest();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	//Update data from sensors
	
	//Update all sensors
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
	
	public void updateAccelerometerData()
    {
    	try {
			accel.updateData();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void updateGyroscopeData()
    {
    	try {
			gyro.updateData();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void updateMagnetometerData()
    {
    	try {
			mag.updateData();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void updateThermometerData()
    {
    	try {
			therm.updateData();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}