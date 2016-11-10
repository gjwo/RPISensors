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
}