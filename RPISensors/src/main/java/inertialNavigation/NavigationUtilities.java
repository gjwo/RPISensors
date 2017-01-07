package inertialNavigation;

import dataTypes.TimestampedData1f;
import dataTypes.TimestampedData2f;
import dataTypes.TimestampedData3f;

/**
 * 
 * @author GJWood
 *
 */
@SuppressWarnings("WeakerAccess")
public class NavigationUtilities {
    public static final long NANOS_PER_SEC = 1000000000;
    private static final float NANOS_PER_SECF = 1000000000f;

	public NavigationUtilities() {}
	
	/**
	 * Performs a simple Trapezoidal integration over two samples in 1 dimension1
	 * @param sampleT 	- sample at time t
	 * @param sampleTm1	- sample at time t+1
	 * @return			- the integral of the input
	 */
    public static TimestampedData1f integrate(TimestampedData1f sampleT, TimestampedData1f sampleTm1 )
    {
        final float deltaT = ((float)sampleT.getTime())- ((float) sampleTm1.getTime())/NANOS_PER_SECF; // time difference between samples in seconds

        return new TimestampedData1f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                sampleT.getTime()); // preserve timestamp in result
    }
    
	/**
	 * Performs a simple Trapezoidal integration over two samples in 2 dimensions
	 * @param sampleT 	- sample at time t
	 * @param sampleTm1	- sample at time t+1
	 * @return			- the integral of the input
	 */

    public static TimestampedData2f integrate(TimestampedData2f sampleT, TimestampedData2f sampleTm1 )
    {
        final float deltaT = ((float) sampleT.getTime())-((float) sampleTm1.getTime())/NANOS_PER_SECF; // time difference between samples in seconds

        return new TimestampedData2f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                (sampleT.getY()+sampleTm1.getY())/2f*deltaT,//Trapezoidal area, average height Y deltaT
                sampleT.getTime()); // preserve timestamp in result
    }
    
	/**
	 * Performs a simple Trapezoidal integration over two samples in 3 dimensions
	 * @param sampleT 	- sample at time t
	 * @param sampleTm1	- sample at time t+1
	 * @return			- the integral of the input
	 */
    public static TimestampedData3f integrate(TimestampedData3f sampleT, TimestampedData3f sampleTm1 )
    {
        final float deltaT = ((float)sampleT.getTime())-((float)sampleTm1.getTime())/NANOS_PER_SECF; // time difference between samples in seconds

        return new TimestampedData3f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                (sampleT.getY()+sampleTm1.getY())/2f*deltaT,//Trapezoidal area, average height Y deltaT
                (sampleT.getZ()+sampleTm1.getZ())/2f*deltaT,//Trapezoidal area, average height Z deltaT
                sampleT.getTime()); // preserve timestamp in result
    }
}
