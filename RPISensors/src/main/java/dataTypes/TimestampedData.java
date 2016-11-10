package dataTypes;

/**
 * TimestampedData
 * Created by G.J Wood on 09/11/2016.
 */
public abstract class TimestampedData <E> extends Data1 <E>
{
    public static final long NANOS_PER_SEC = 1000000000;
    public final long nanoTime;

    public TimestampedData(TimestampedData <E> tsd)
    {
        super(tsd.unStamp());
        this.nanoTime = tsd.time();
    }
    
    public TimestampedData(E data, long nanoTime)
    {
        super(data);
        this.nanoTime = nanoTime;
    }

    public TimestampedData(E data)
    {
        this(data, System.nanoTime());
    }

    public TimestampedData()
    {
        this(null, System.nanoTime());
    }

    public E unStamp()
    {
        return x;
    }
    public long time()
    {
        return nanoTime;
    }

    public String toString()
    {
        String format = "%+04.4f";
        return 	"[" + String.format(format,(float)(nanoTime/(float)NANOS_PER_SEC)) +
                "] " + super.toString();
    }

    public TimestampedData <E> clone()
    {
       // return new TimestampedData<E> (this);
    	return null;
    }
}