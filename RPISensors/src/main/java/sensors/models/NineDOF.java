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

	protected NineDOF(int sampleRate, int sampleSize) {
		super(sampleRate);
		// TODO Auto-generated constructor stub
	}

    public void calibrateAccelerometer() throws InterruptedException{
		try {
			accel.calibrate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void calibrateGyroscope() throws InterruptedException{
		try {
			gyro.calibrate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void calibrateMagnetometer() throws InterruptedException, IOException
    {
    	mag.calibrate();
    }

    public void calibrateThermometer() {
		try {
			therm.calibrate();
		} catch( InterruptedException|IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public TimestampedDataFloat3D getAcceleration(int i) {
		return accel.getValue(i);
	}

	public int getAccelerometerReadingCount() {
		return accel.getReadingCount();
	}

	public TimestampedDataFloat3D getAvgAcceleration() {
		
		return accel.getAvgValue();
	}

	public TimestampedDataFloat3D getAvgGauss() {
		return mag.getAvgValue();
	}

	public TimestampedDataFloat3D getAvgRotationalAcceleration() {
		return gyro.getAvgValue();
	}

	public float getAvgTemperature() {
		return therm.getAvgValue().getX();
	}

	public TimestampedDataFloat3D getGaussianData(int i) {
		return mag.getValue(i);
	}

	public int getGyroscopeReadingCount() {
		return gyro.getReadingCount();
	}

	public TimestampedDataFloat3D getLatestAcceleration() {
		return accel.getLatestValue();
	}

	public TimestampedDataFloat3D getLatestGaussianData() {
		return mag.getLatestValue();
	}

	public TimestampedDataFloat3D getLatestRotationalAcceleration() {
		return gyro.getLatestValue();
	}

	public float getLatestTemperature() {
		return therm.getLatestValue().getX();
	}

	public int getMagnetometerReadingCount() {
		return mag.getReadingCount();
	}

	public TimestampedDataFloat3D getRotationalAcceleration(int i) {
		return gyro.getValue(i);
	}

	public float getTemperature(int i) {
		return therm.getValue(i).getX();
	}

	public int getThermometerReadingCount() {
		return therm.getReadingCount();
	}

	public void initMagnetometer() throws InterruptedException, IOException {
		mag.init();
	}

	public void selfTestAccelerometer() {
		try {
			accel.selfTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selfTestGyroscope() {
		try {
			gyro.selfTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selfTestMagnetometer() {
		try {
			mag.selfTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void selfTestThermometer() {
		try {
			therm.selfTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateAccelerometerData() throws IOException
    {
    	accel.updateData();
    }

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

	public void updateGyroscopeData() throws IOException
    {
    	gyro.updateData();
    }	
	public void updateMagnetometerData() throws IOException
    {
    	mag.updateData();
    }

	public void updateThermometerData() throws IOException
    {
    	therm.updateData();
    }
}
