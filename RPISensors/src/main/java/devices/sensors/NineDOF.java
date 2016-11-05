package devices.sensors;

import java.io.IOException;

import devices.dataTypes.Data1D;
import devices.dataTypes.Data3D;
import devices.dataTypes.TimestampedData1D;
import devices.dataTypes.TimestampedData3D;
import devices.sensors.interfaces.Accelerometer;
import devices.sensors.interfaces.Gyroscope;
import devices.sensors.interfaces.Magnetometer;
import devices.sensors.interfaces.Thermometer;

public abstract class NineDOF extends SensorPackage implements Accelerometer, Gyroscope, Magnetometer, Thermometer
{
	protected Sensor<TimestampedData3D,Data3D> mag;
	protected Sensor<TimestampedData3D,Data3D> accel;
	protected Sensor<TimestampedData3D,Data3D> gyro;
	protected Sensor<TimestampedData1D,Data1D> therm;

	protected NineDOF(int sampleRate, int sampleSize) {
		super(sampleRate);
		// TODO Auto-generated constructor stub
	}

    public void calibrateMagnetometer() throws InterruptedException, IOException
    {
    	mag.calibrate();
    }

    public void updateAccelerometerData() throws IOException
    {
    	accel.updateData();
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

	public TimestampedData3D getLatestAcceleration() {
		return accel.getLatestValue();
	}

	public TimestampedData3D getAvgAcceleration() {
		
		return accel.getAvgValue();
	}

	public TimestampedData3D getAcceleration(int i) {
		return accel.getValue(i);
	}

	public int getAccelerometerReadingCount() {
		return accel.getReadingCount();
	}

	public void calibrateAccelerometer() throws InterruptedException{
		try {
			accel.calibrate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selfTestAccelerometer() {
		try {
			accel.selfTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TimestampedData3D getLatestRotationalAcceleration() {
		return gyro.getLatestValue();
	}

	public TimestampedData3D getRotationalAcceleration(int i) {
		return gyro.getValue(i);
	}

	public int getGyroscopeReadingCount() {
		return gyro.getReadingCount();
	}

	public void calibrateGyroscope() throws InterruptedException{
		try {
			gyro.calibrate();
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

	public TimestampedData3D getLatestGaussianData() {
		return mag.getLatestValue();
	}

	public TimestampedData3D getGaussianData(int i) {
		return mag.getValue(i);
	}

	public int getMagnetometerReadingCount() {
		return mag.getReadingCount();
	}

	public void selfTestMagnetometer() {
		try {
			mag.selfTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public float getLatestTemperature() {
		return therm.getLatestValue().getX();
	}

	public float getTemperature(int i) {
		return therm.getValue(i).getX();
	}

	public int getThermometerReadingCount() {
		return therm.getReadingCount();
	}

	public void calibrateThermometer() {
		try {
			therm.calibrate();
		} catch( InterruptedException|IOException e) {
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

	public TimestampedData3D getAvgRotationalAcceleration() {
		return gyro.getAvgValue();
	}

	public TimestampedData3D getAvgGauss() {
		return mag.getAvgValue();
	}	
	public float getAvgTemperature() {
		return therm.getAvgValue().getX();
	}

	public void initMagnetometer() throws InterruptedException, IOException {
		mag.init();
	}
}
