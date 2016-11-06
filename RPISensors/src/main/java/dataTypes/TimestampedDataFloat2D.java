package dataTypes;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
public class TimestampedDataFloat2D extends DataFloat2D
{
    public static final long NANOS_PER_SEC = 1000000000;
    public final long nanoTime;

    public TimestampedDataFloat2D(float x, float y, long nanoTime)
    {
        super(x, y);
        this.nanoTime = nanoTime;
    }

    public TimestampedDataFloat2D(float x, float y)
    {
        this(x, y, System.nanoTime());
    }

    public TimestampedDataFloat2D(DataFloat2D data)
    {
        this(data.getX(),data.getY());
    }

    public TimestampedDataFloat2D(TimestampedDataFloat2D data)
    {
        super(data.getX(),data.getY());
        this.nanoTime = data.nanoTime;
    }

    public String toString()
    {
        String format = "%+04.4f";
        return 	" t: " + String.format(format,(float)(nanoTime/NANOS_PER_SEC)) +
                " " + super.toString();
    }

    public static TimestampedDataFloat2D integrate(TimestampedDataFloat2D sampleT, TimestampedDataFloat2D sampleTm1 )
    {
        final float deltaT = (float)(sampleT.nanoTime-sampleTm1.nanoTime)/(float)TimestampedDataFloat3D.NANOS_PER_SEC; // time difference between samples in seconds

        return new TimestampedDataFloat2D(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                (sampleT.getY()+sampleTm1.getY())/2f*deltaT,//Trapezoidal area, average height Y deltaT
                sampleT.nanoTime); // preserve timestamp in result
    }

    public TimestampedDataFloat2D integrate(TimestampedDataFloat2D sampleTm1 )
    {
        return integrate(sampleTm1,this);
    }

    public TimestampedDataFloat2D clone()
    {
        return new TimestampedDataFloat2D(x,y,nanoTime);
    }
}
