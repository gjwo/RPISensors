package inertialNavigation;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import dataTypes.Data3f;
import dataTypes.TimestampedData3f;

/**
 * @author GJWood
 *
 */
public class Instruments {
	
	//Time of last instrument update
	private static Instant updatedTimestamp = Instant.now();
	
	//data from individual sensors
	private static TimestampedData3f magnetometer = new TimestampedData3f(0,0,0);
	private static TimestampedData3f accelerometer = new TimestampedData3f(0,0,0);
	private static TimestampedData3f gyroscope = new TimestampedData3f(0,0,0);
	
	//Fused  data from several sensors
	private static Quaternion quaternion = new Quaternion(); 
	private static Data3f TaitBryanAnglesR = new Data3f(0,0,0); //in radians yaw not adjusted 360 or for location declination
	private static Data3f TaitBryanAnglesD = new Data3f(0,0,0); //in degrees yaw adjusted 360 and for location declination
	
	//in degrees adjusted for location and yaw to read 0-360
	private static float yaw = 0; 	//Yaw is the angle between SensorPackage x-axis and Earth magnetic North (or true North if corrected for local declination, looking down on the sensor positive yaw is counterclockwise.
	private static float pitch = 0; //Pitch is angle between sensor x-axis and Earth ground plane, toward the Earth is positive, up toward the sky is negative.
	private static float roll = 0; 	//Roll is angle between sensor y-axis and Earth ground plane, y-axis up is positive roll.
									//Alternate names for the same things are heading, attitude and bank 
	
	private static Data3f linearAcceleration = new Data3f(0,0,0);
	
	// getters
	public static Instant getTimestamp(){return updatedTimestamp;}
	public static ZonedDateTime getDateTime() {return updatedTimestamp.atZone(ZoneId.of("GB"));}
	public static float getYaw() {return yaw;}
	public static float getPitch() {return pitch;}
	public static float getRoll() {return roll;}
	public static float getHeading() {return getYaw();}	
	public static float getAttitude() {return getPitch();}
	public static float getBank() {return getRoll();}
	public static Instant getUpdatedTimestamp() {return updatedTimestamp;}
	public static Quaternion getQuaternion() {return quaternion;}
	public static Data3f getTaitBryanAnglesR() {return TaitBryanAnglesR;}
	public static Data3f getTaitBryanAnglesD() {return TaitBryanAnglesD;}
	public static TimestampedData3f getMagnetometer() {return magnetometer;}
	public static TimestampedData3f getAccelerometer() {return accelerometer;}
	public static TimestampedData3f getGyroscope() {return gyroscope;}
	public static TimestampedData3f getAngles(){return new TimestampedData3f(yaw,pitch,roll);}	
	public static Data3f getLinearAcceleration() {return linearAcceleration;}

	//Setters
	public static void setMagnetometer(TimestampedData3f magnetometer) {Instruments.magnetometer = magnetometer;}
	public static void setAccelerometer(TimestampedData3f accelerometer) {Instruments.accelerometer = accelerometer;}
	public static void setGyroscope(TimestampedData3f gyroscope) {Instruments.gyroscope = gyroscope;}

	//public static void setYaw(float yaw) {Instruments.yaw = yaw;}
	//public static void setHeading(float heading) {Instruments.yaw = heading;}
	//public static void setAttitude(float attitude) {Instruments.pitch = attitude;}
	//public static void setPitch(float pitch) {Instruments.pitch = pitch;}
	//public static void setBank(float bank) {Instruments.roll = bank;}
	//public static void setRoll(float roll) {Instruments.roll = roll;}
	
	public static void printInstruments()
	{
		//TBD
	}
	
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
	public static void updateInstruments(Quaternion q)
	{		
		updatedTimestamp = Instant.now();
		quaternion = q;
		TaitBryanAnglesR = q.toTaitBryanAngles();
	    
	    yaw   = (float) Math.toDegrees(TaitBryanAnglesR.getX()); 	//radians to degrees		// #KW L634
	    pitch = (float) Math.toDegrees(TaitBryanAnglesR.getY()); 	//radians to degrees		// #KW L633
	    roll  = (float) Math.toDegrees(TaitBryanAnglesR.getZ()); 	//radians to degrees		// #KW L637

	    // #KW L635 yaw   -= 13.8; // Declination at Danville, California is 13 degrees 48 minutes and 47 seconds on 2014-04-04
	    yaw   += -44.0f/60.0f; // Declination at Letchworth England is minus O degrees and 44 Seconds on 2016-07-11
	    if(yaw < 0) yaw   += 360.0f; // Ensure heading stays between 0 and 360
	    TaitBryanAnglesD = new Data3f(yaw, pitch, roll);
	    updatelinearAcceleration(quaternion);
	}
	
	private static void updatelinearAcceleration(Quaternion q)
	{
	    float a31 =   2.0f * (q.w * q.x + q.y * q.z);
	    float a32 =   2.0f * (q.x * q.z - q.w * q.y);
	    float a33 =   q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z;
	    linearAcceleration.setX( accelerometer.getX() + a31);
	    linearAcceleration.setY(accelerometer.getY() + a32);
	    linearAcceleration.setZ(accelerometer.getZ() - a33);

	}
}