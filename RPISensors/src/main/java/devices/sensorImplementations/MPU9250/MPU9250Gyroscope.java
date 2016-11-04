package devices.sensorImplementations.MPU9250;

import devices.dataTypes.TimestampedData3D;
import devices.sensors.Sensor;
import devices.sensors.interfaces.Gyroscope;

public class MPU9250Gyroscope extends Sensor<TimestampedData3D> implements Gyroscope {

	public MPU9250Gyroscope(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TimestampedData3D getLatestRotationalAcceleration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimestampedData3D getRotationalAcceleration(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimestampedData3D getAvgRotationalAcceleration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGyroscopeReadingCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateGyroscopeData() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void calibrateGyroscope() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selfTestGyroscope() {
		// TODO Auto-generated method stub

	}

}
