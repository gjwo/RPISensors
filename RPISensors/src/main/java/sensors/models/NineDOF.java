package sensors.models;

import java.io.IOException;

import dataTypes.DataFloat1D;
import dataTypes.TimestampedDataFloat1D;
import dataTypes.TimestampedDataFloat3D;
import sensors.interfaces.Accelerometer;
import sensors.interfaces.Gyroscope;
import sensors.interfaces.Magnetometer;
import sensors.interfaces.Thermometer;

public abstract class NineDOF extends SensorPackage implements Accelerometer, Gyroscope, Magnetometer, Thermometer
{
	protected Sensor3D mag;
	protected Sensor3D accel;
	protected Sensor3D gyro;
	protected Sensor<TimestampedDataFloat1D,DataFloat1D> therm;

	protected NineDOF(int sampleRate, int sampleSize) 
	{
		super(sampleRate);
	}

	// Get average named sensor values
	public TimestampedDataFloat3D getAvgAcceleration() {return accel.getAvgValue();}

	public TimestampedDataFloat3D getAvgGauss() {return mag.getAvgValue();}

	public TimestampedDataFloat3D getAvgRotationalAcceleration() {return gyro.getAvgValue();}

	public float getAvgTemperature() {return therm.getAvgValue().getX();}

	// Get latest named sensor values
	public TimestampedDataFloat3D getLatestAcceleration() {return accel.getLatestValue();}

	public TimestampedDataFloat3D getLatestGaussianData() {return mag.getLatestValue();}

	public TimestampedDataFloat3D getLatestRotationalAcceleration() {return gyro.getLatestValue();}

	public float getLatestTemperature() {return therm.getLatestValue().getX();}
	
	// Get specific named sensor values
    public TimestampedDataFloat3D getAcceleration(int i) {return accel.getValue(i);}

	public TimestampedDataFloat3D getGaussianData(int i) {return mag.getValue(i);}

	public TimestampedDataFloat3D getRotationalAcceleration(int i) {return gyro.getValue(i);}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void calibrateGyroscope()
    {
		try {
			gyro.calibrate();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void calibrateMagnetometer()
    {
    	try {
			mag.calibrate();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void calibrateThermometer() {
		try {
			therm.calibrate();
		} catch( InterruptedException|IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//initialise sensors for normal operation
	public void initMagnetometer()
	{
		try {
			mag.init();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selfTestAccelerometer() {
		try {
			accel.selfTest();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Self test sensors
	public void selfTestGyroscope() {
		try {
			gyro.selfTest();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selfTestMagnetometer() {
		try {
			mag.selfTest();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void selfTestThermometer() {
		try {
			therm.selfTest();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Update data from sensors
	public void updateAccelerometerData()
    {
    	try {
			accel.updateData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public void updateGyroscopeData()
    {
    	try {
			gyro.updateData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
	public void updateMagnetometerData()
    {
    	try {
			mag.updateData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public void updateThermometerData()
    {
    	try {
			therm.updateData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
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
}
