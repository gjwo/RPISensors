package inertialNavigation;

import dataTypes.Data3f;

/**
 * Quaternion - w simple 4 value data type used for navigation calculations
 * @author GJWood
 * @version 1.0
 * 
 */
public class Quaternion 
{
	public float w,x,y,z;
	
	/**
	 * Quaternion	- Constructor from 4 scalar values
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 */
	public Quaternion(float a,float b, float c, float d)
	{
		this.w = a;
		this.x = b;
		this.y = c;
		this.z = d;
	}
	
	/**
	 * Quaternion	-	Constructor from yaw, pitch and roll (Eulerian angles)
	 * @param yaw
	 * @param pitch
	 * @param roll
	 */
	public Quaternion( float yaw,float pitch, float roll)
	{
		float t0 = (float)Math.cos(yaw * 0.5f);
		float t1 = (float)Math.sin(yaw * 0.5f);
		float t2 = (float)Math.cos(roll * 0.5f);
		float t3 = (float)Math.sin(roll * 0.5f);
		float t4 = (float)Math.cos(pitch * 0.5f);
		float t5 = (float)Math.sin(pitch * 0.5f);
		w = t0 * t2 * t4 + t1 * t3 * t5;
		x = t0 * t3 * t4 - t1 * t2 * t5;
		y = t0 * t2 * t5 + t1 * t3 * t4;
		z = t1 * t2 * t4 - t0 * t3 * t5;		
	}

	/**
	 * Quaternion	- Constructor from an array of 4 values
	 * @param data
	 */
	public Quaternion(float[] data){this(data[0], data[1], data[2], data[3]);}

	/**
	 * Quaternion	- blank Constructor
	 */
	public Quaternion(){this(0,0,0,0);}
	
	/**
	 * toEulerianAngle	-	Convert a quaternion back into a set of Eulerian angles
	 * 	
	 * @return	a class containing yaw pitch and roll
	 */
	public Data3f toEulerianAngles()
	{
		float ysqr = y * y;
		float t0 = -2.0f * (ysqr + z * z) + 1.0f;
		float t1 = +2.0f * (x * y - w * z);
		float t2 = -2.0f * (x * z + w * y);
		float t3 = +2.0f * (y * z - w * x);
		float t4 = -2.0f * (x * x + ysqr) + 1.0f;

		t2 = t2 > 1.0f ? 1.0f : t2;		//Deal with singularity
		t2 = t2 < -1.0f ? -1.0f : t2;	//Deal with singularity
		
		float yaw = (float)Math.atan2(t1, t0); 	//yaw
		float pitch = (float)Math.asin(t2); 	//pitch
		float roll = (float)Math.atan2(t3, t4); //roll

		return new Data3f(yaw, pitch, roll);
	}
	
	/**
	 * toTaitBryanAngle	-	Convert a quaternion back into a set of Tait-Bryan angles
	 * @return	-	structure of Yaw, Pitch and Roll in degrees
	 */
	public Data3f toTaitBryanAngles()
	{
	    float a12 =   2.0f * (x * y + w * z);					// #KW L625 
	    float a22 =   w * w + x * x - y * y - z * z;
	    float a31 =   2.0f * (w * x + y * z);
	    float a32 =   2.0f * (x * z - w * y);
	    float a33 =   w * w - x * x - y * y + z * z;
	    
		a32 = a32 > 1.0f ? 1.0f : a32;		//Deal with singularity
		a32 = a32 < -1.0f ? -1.0f : a32;	//Deal with singularity
	    
	    float pitch = (float) -Math.asin(a32);					// #KW L630
	    float roll  = (float) Math.atan2(a31, a33);
	    float yaw   = (float) Math.atan2(a12, a22);
	    
	    return new Data3f(yaw,pitch,roll);
	}
	
	/**
	 * normalise	- normalise by dividing each value by the square root of the sum of the squares of the values
	 */
	public void normalize()
	{
		float norm;
		// Normalise accelerometer measurement
		norm = (float)Math.sqrt(w*w + x*x + y*y+ z*z);
		if (norm == 0.0f)
			throw new ArithmeticException(); // handle NaN
		norm = 1f / norm;
		w *= norm;
		x *= norm;
		y *= norm;
		z *= norm;
	}
	
	/**
	 * setAll	- set all values based on scalar parameters
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setAll(float a,float b, float c, float d)
	{
		this.w = a;
		this.x = b;
		this.y = c;
		this.z = d;
	}
	
    /**
     * toString - return w formatted string representation for printing
     */
	public String toString()
	{
		final String format = "%+07.3f ";
		return 	"[ " + String.format(format,w)+ String.format(format,x)+ String.format(format,y)+ String.format(format,z)+"]";
	}
}