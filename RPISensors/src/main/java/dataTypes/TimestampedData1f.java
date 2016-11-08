package dataTypes;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
public class TimestampedData1f extends Data1f
{
    public static final long NANOS_PER_SEC = 1000000000;
    public final long nanoTime;

    public TimestampedData1f(float x, long nanoTime)
    {
        super(x);
        this.nanoTime = nanoTime;
    }

    public TimestampedData1f(float x)
    {
        this(x, System.nanoTime());
    }

    public TimestampedData1f(Data3f data)
    {
        this(data.getX());
    }

    public String toString()
    {
        String format = "%+04.4f";
        return 	" t: " + String.format(format,(float)(nanoTime/NANOS_PER_SEC)) +
                " " + super.toString();
    }

    public static TimestampedData1f integrate(TimestampedData1f sampleT, TimestampedData1f sampleTm1 )
    {
        final float deltaT = (float)(sampleT.nanoTime-sampleTm1.nanoTime)/(float)TimestampedData3f.NANOS_PER_SEC; // time difference between samples in seconds

        return new TimestampedData1f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                sampleT.nanoTime); // preserve timestamp in result
    }

    public TimestampedData1f integrate(TimestampedData1f sampleTm1)
    {
        return integrate(this,sampleTm1);
    }

    public TimestampedData1f clone()
    {
        return new TimestampedData1f(x,nanoTime);
    }
}
