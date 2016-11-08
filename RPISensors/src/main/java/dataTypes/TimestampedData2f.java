package dataTypes;

/**
 * RPITank - devices.sensors.dataTypes
 * Created by MAWood on 18/07/2016.
 */
public class TimestampedData2f extends Data2f
{
    public static final long NANOS_PER_SEC = 1000000000;
    public final long nanoTime;

    public TimestampedData2f(float x, float y, long nanoTime)
    {
        super(x, y);
        this.nanoTime = nanoTime;
    }

    public TimestampedData2f(float x, float y)
    {
        this(x, y, System.nanoTime());
    }

    public TimestampedData2f(Data2f data)
    {
        this(data.getX(),data.getY());
    }

    public TimestampedData2f(TimestampedData2f data)
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

    public static TimestampedData2f integrate(TimestampedData2f sampleT, TimestampedData2f sampleTm1 )
    {
        final float deltaT = (float)(sampleT.nanoTime-sampleTm1.nanoTime)/(float)TimestampedData3f.NANOS_PER_SEC; // time difference between samples in seconds

        return new TimestampedData2f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                (sampleT.getY()+sampleTm1.getY())/2f*deltaT,//Trapezoidal area, average height Y deltaT
                sampleT.nanoTime); // preserve timestamp in result
    }

    public TimestampedData2f integrate(TimestampedData2f sampleTm1 )
    {
        return integrate(sampleTm1,this);
    }

    public TimestampedData2f clone()
    {
        return new TimestampedData2f(x,y,nanoTime);
    }
}
