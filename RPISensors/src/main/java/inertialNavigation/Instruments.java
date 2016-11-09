package inertialNavigation;

import dataTypes.TimestampedData3f;

/**
 * @author GJWood
 *
 */
public class Instruments {
	
	//Fused  data from several sensors
	private static float yaw = 0; //Yaw is the angle between SensorPackage x-axis and Earth magnetic North (or true North if corrected for local declination, looking down on the sensor positive yaw is counterclockwise.
	private static float pitch = 0; //Pitch is angle between sensor x-axis and Earth ground plane, toward the Earth is positive, up toward the sky is negative.
	private static float roll = 0; //Roll is angle between sensor y-axis and Earth ground plane, y-axis up is positive roll.
	
	private static float speed = 0;
	
	//Data computed from integrating sensor information
	private static float heading = 0;
	private static float attitude = 0;
	private static float bank = 0;
	
	private static TimestampedData3f position = new TimestampedData3f(0,0,0);
	
	//data from individual sensors
	private static TimestampedData3f magnetometer = new TimestampedData3f(0,0,0);
	private static TimestampedData3f accelerometer = new TimestampedData3f(0,0,0);
	private static TimestampedData3f gyroscope = new TimestampedData3f(0,0,0);
	
	// getters
	public static float getYaw() {return yaw;}
	public static float getPitch() {return pitch;}
	public static float getRoll() {return roll;}
	public static float getSpeed() {return speed;}
	public static float getHeading() {return heading;}
	public static TimestampedData3f getPosition() {return position;}
	public static TimestampedData3f getMagnetometer() {return magnetometer;}
	public static TimestampedData3f getAccelerometer() {return accelerometer;}
	public static TimestampedData3f getGyroscope() {return gyroscope;}
	public static TimestampedData3f getAngles(){return new TimestampedData3f(yaw,pitch,roll);}

	//Setters
	public static float getAttitude() {return attitude;}
	public static float getBank() {return bank;}
	public static void setBank(float bank) {Instruments.bank = bank;}
	public static void setAttitude(float attitude) {Instruments.attitude = attitude;}
	public static void setPitch(float pitch) {Instruments.pitch = pitch;}
	public static void setHeading(float heading) {Instruments.heading = heading;}
	public static void setPosition(TimestampedData3f position) {Instruments.position = position;}
	public static void setMagnetometer(TimestampedData3f magnetometer) {Instruments.magnetometer = magnetometer;}
	public static void setAccelerometer(TimestampedData3f accellerometer) {Instruments.accelerometer = accellerometer;}
	public static void setGyroscope(TimestampedData3f gyroscope) {Instruments.gyroscope = gyroscope;}

	/**
	 * Update output acceleration variables Yaw, Pitch and Roll based on fused sensor data
	 * <p>
	 * these are Tait-Bryan angles, commonly used in aircraft orientation.
	 * In this coordinate system, the positive z-axis is down toward Earth.
	 * Yaw is the angle between SensorPackage x-axis and Earth magnetic North (or true North if corrected for local declination,
	 * looking down on the sensor positive yaw is counterclockwise.
	 * Pitch is angle between sensor x-axis and Earth ground plane, toward the Earth is positive, up toward the sky is negative.
	 * Roll is angle between sensor y-axis and Earth ground plane, y-axis up is positive roll.
	 * <p>
	 * These arise from the definition of the homogeneous rotation matrix constructed from quaternions.
	 * Tait-Bryan angles as well as Euler angles are non-commutative; that is, the get the correct orientation the rotations must be
	 * applied in the correct order which for this configuration is yaw, pitch, and then roll.
	 * For more see http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles which has additional links.
	 * 
	 * @param q a quaternion containing the fused input data - see https://en.wikipedia.org/wiki/Quaternion
	 */
	public static void updateYawPitchRoll(Quaternion q)
	{
		    yaw   = (float)Math.atan2(2.0f * (q.b * q.c + q.a * q.d), q.a * q.a + q.b * q.b - q.c * q.c - q.d * q.d);   
		    pitch = -(float)Math.asin(2.0f * (q.b * q.d - q.a * q.c));
		    roll  = (float)Math.atan2(2.0f * (q.a * q.b + q.c * q.d), q.a * q.a - q.b * q.b - q.c * q.c + q.d * q.d);
		    pitch *= 180.0f / (float)Math.PI; //radians to degrees
		    yaw   *= 180.0f / (float)Math.PI; //radians to degrees
		    //yaw   -= 13.8; // Declination at Danville, California is 13 degrees 48 minutes and 47 seconds on 2014-04-04
		    yaw   -= -44f/60f; // Declination at Letchworth England is minus O degrees and 44 Seconds on 2016-07-11
		    roll  *= 180.0f / (float)Math.PI; //radians to degrees
	}

}
