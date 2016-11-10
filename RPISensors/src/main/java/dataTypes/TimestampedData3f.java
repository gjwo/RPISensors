package dataTypes;

/**
 * RPITank
 * Created by MAWood on 11/07/2016.
 */
public class TimestampedData3f extends Data3f
{
    public static final long NANOS_PER_SEC = 1000000000;
    public final long nanoTime;

    public TimestampedData3f(float x, float y, float z, long nanoTime)
    {
        super(x, y, z);
        this.nanoTime = nanoTime;
    }

    public TimestampedData3f(float x, float y, float z)
    {
        this(x, y, z, System.nanoTime());
    }

    public TimestampedData3f(Data3f data)
    {
        this(data.getX(),data.getY(),data.getZ());
    }

    public Data3f unStamp()
    {
        return (new Data3f(this.getX(),this.getY(),this.getZ()));
    }

    public TimestampedData3f(TimestampedData3f data)
    {
        super(data.getX(),data.getY(),data.getZ());
        this.nanoTime = data.nanoTime;
    }

    public String toString()
    {
        String format = "%+04.4f";
        return 	"[" + String.format(format,(float)(nanoTime/(float)NANOS_PER_SEC)) +
                "] " + super.toString();
    }

    public static TimestampedData3f integrate(TimestampedData3f sampleT, TimestampedData3f sampleTm1 )
    {
        final float deltaT = (float)(sampleT.nanoTime-sampleTm1.nanoTime)/(float)TimestampedData3f.NANOS_PER_SEC; // time difference between samples in seconds

        return new TimestampedData3f(
                (sampleT.getX()+sampleTm1.getX())/2f*deltaT,//Trapezoidal area, average height X deltaT
                (sampleT.getY()+sampleTm1.getY())/2f*deltaT,//Trapezoidal area, average height Y deltaT
                (sampleT.getZ()+sampleTm1.getZ())/2f*deltaT,//Trapezoidal area, average height Z deltaT
                sampleT.nanoTime); // preserve timestamp in result
    }

    public TimestampedData3f integrate(TimestampedData3f sampleTm1 )
    {
        return integrate(sampleTm1,this);
    }

    public TimestampedData3f clone()
    {
        return new TimestampedData3f(x,y,z,nanoTime);
    }

}