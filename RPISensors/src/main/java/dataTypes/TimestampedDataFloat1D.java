package dataTypes;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
public class TimestampedDataFloat1D extends DataFloat1D
{
    public static final long NANOS_PER_SEC = 1000000000;
    public final long nanoTime;

    public TimestampedDataFloat1D(float x, long nanoTime)
    {
        super(x);
        this.nanoTime = nanoTime;
    }

    public TimestampedDataFloat1D(float x)
    {
        this(x, System.nanoTime());
    }

    public TimestampedDataFloat1D(DataFloat3D data)
    {
        this(data.getX());
    }

    public String toString()
    {
        String format = "%+04.4f";
        return 	" t: " + String.format(format,(float)(nanoTime/NANOS_PER_SEC)) +
                " " + super.toString();
    }

    public static TimestampedDataFloat1D integrate(TimestampedDataFloat1D sampleT, TimestampedDataFloat1D sampleTm1 )
    {
        final float deltaT = (float)(sampleT.nanoTime-sampleTm1.nanoTime)/(float)TimestampedDataFloat3D.NANOS_PER_SEC; // time difference between samples in seconds

        return new TimestampedDataFloat1D(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                sampleT.nanoTime); // preserve timestamp in result
    }

    public TimestampedDataFloat1D integrate(TimestampedDataFloat1D sampleTm1)
    {
        return integrate(this,sampleTm1);
    }

    public TimestampedDataFloat1D clone()
    {
        return new TimestampedDataFloat1D(x,nanoTime);
    }
}
