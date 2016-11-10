package inertialNavigation;

import dataTypes.TimestampedData3f;

/**
 * 
 * @author GJWood
 *
 */
public class NavigationUtilities {

	public NavigationUtilities() {}
	
	/**
	 * Performs a simple Trapezoidal integration over two samples
	 * @param sampleT 	- sample at time t
	 * @param sampleTm1	- sample at time t+1
	 * @return
	 */
    public static TimestampedData3f integrate(TimestampedData3f sampleT, TimestampedData3f sampleTm1 )
    {
        final float deltaT = ((float)sampleT.nanoTime-sampleTm1.nanoTime)/((float) TimestampedData3f.NANOS_PER_SEC); // time difference between samples in seconds

        return new TimestampedData3f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                (sampleT.getY()+sampleTm1.getY())/2f*deltaT,//Trapezoidal area, average height Y deltaT
                (sampleT.getZ()+sampleTm1.getZ())/2f*deltaT,//Trapezoidal area, average height Z deltaT
                sampleT.nanoTime); // preserve timestamp in result
    }
}
