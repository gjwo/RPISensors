package inertialNavigation;

import dataTypes.Data3f;

/**
 * Quaternion - a simple 4 value data type used for navigation calculations
 * @author GJWood
 * @version 1.0
 * 
 */
public class Quaternion 
{
	public float a,b,c,d;
	
	/**
	 * Quaternion	- Constructor from 4 scalar values
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public Quaternion(float a,float b, float c, float d)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	/**
	 * Quaternion	-	Constructor from yaw, pitch and roll
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
		this.a = t0 * t2 * t4 + t1 * t3 * t5;
		this.a = t0 * t3 * t4 - t1 * t2 * t5;
		this.a = t0 * t2 * t5 + t1 * t3 * t4;
		this.a = t1 * t2 * t4 - t0 * t3 * t5;		
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
	 * toEulerianAngle	-	Convert a quaternion back into a Eulerian angle
	 * @return
	 */
	public Data3f toEulerianAngle()
	{
		float ysqr = this.c * this.c;
		float t0 = -2.0f * (ysqr + this.d * this.d) + 1.0f;
		float t1 = +2.0f * (this.b * this.c - this.a * this.d);
		float t2 = -2.0f * (this.b * this.d + this.a * this.c);
		float t3 = +2.0f * (this.c * this.d - this.a * this.b);
		float t4 = -2.0f * (this.b * this.b + ysqr) + 1.0f;

		t2 = t2 > 1.0f ? 1.0f : t2;
		t2 = t2 < -1.0f ? -1.0f : t2;

		return new Data3f(	(float)Math.atan2(t1, t0), //yaw
							(float)Math.sin(t2), //pitch
							(float)Math.atan2(t3, t4)); //roll
	}	
	
	/**
	 * normalise	- normalise by dividing each value by the square root of the sum of the squares of the values
	 */
	public void normalize()
	{
		float norm;
		// Normalise accelerometer measurement
		norm = (float)Math.sqrt(a*a + b*b + c*c+ d*d);
		if (norm == 0.0f)
			throw new ArithmeticException(); // handle NaN
		norm = 1f / norm;
		a *= norm;
		b *= norm;
		c *= norm;
		d *= norm;
	}
	
	/**
	 * setAll	- set all values based on scalar parameters
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public void setAll(float a,float b, float c, float d)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
    /**
     * toString - return a formatted string representation for printing
     */
	public String toString()
	{
		final String format = "%+07.3f ";
		return 	"[ " + String.format(format,a)+ String.format(format,b)+ String.format(format,c)+ String.format(format,d)+"]";
	}
}