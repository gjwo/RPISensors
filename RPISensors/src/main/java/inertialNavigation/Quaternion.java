package inertialNavigation;
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
	 * Quaternion	- blank Constructor
	 */
	public Quaternion()
	{
		this(0,0,0,0);
	}
	
	/**
	 * Quaternion	- Constructor from an array of 4 values
	 * @param data
	 */
	public Quaternion(float[] data)
	{
		this(data[0], data[1], data[2], data[3]);
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