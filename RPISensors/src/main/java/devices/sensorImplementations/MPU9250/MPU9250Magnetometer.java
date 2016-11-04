/**
 * 
 */
package devices.sensorImplementations.MPU9250;

import java.io.IOException;

import devices.dataTypes.TimestampedData3D;
import devices.sensors.Sensor;

/**
 * @author GJWood
 *
 */
public class MPU9250Magnetometer extends Sensor<TimestampedData3D> implements devices.sensors.interfaces.Magnetometer {

	/**
	 * @param sampleRate
	 * @param sampleSize
	 */
	public MPU9250Magnetometer(int sampleRate, int sampleSize, MPU9250RegisterOperations ro) {
		super(sampleRate, sampleSize, ro);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getLatestGaussianData()
	 */
	@Override
	public TimestampedData3D getLatestGaussianData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getGaussianData(int)
	 */
	@Override
	public TimestampedData3D getGaussianData(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#getMagnetometerReadingCount()
	 */
	@Override
	public int getMagnetometerReadingCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see devices.sensors.interfaces.Magnetometer#updateMagnetometerData()
	 */
	@Override
	public void updateMagnetometerData() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see devices.sensors.dataTypes.Sensor1D#updateData()
	 */
	@Override
	protected void updateData() throws IOException {
		// TODO Auto-generated method stub

	}

}
