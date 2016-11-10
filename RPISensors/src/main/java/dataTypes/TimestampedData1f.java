package dataTypes;

/**
 * TimestampedData1f - 1 dimension time stamped floating point data structure
 * Created by MAWood on 18/07/2016, modified by G.J.Wood 10/11/2016
 */
public class TimestampedData1f extends Data1f
{
    public final long NANOS_PER_SEC = 1000000000;
    public static final float NANOS_PER_SECF = 1000000000f;
    protected final long nanoTime;

    /**
     * TimestampedData1f	- Constructor
     * @param x
     * @param nanoTime
     */
    public TimestampedData1f(float x, long nanoTime)
    {
        super(x);
        this.nanoTime = nanoTime;
    }
    
    /**
     * TimestampedData1f	- Constructor
     * @param x
     */
    public TimestampedData1f(float x)
    {
        this(x, System.nanoTime());
    }

    /**
     * TimestampedData1f	- Constructor
     * @param data
     */
    public TimestampedData1f(Data3f data)
    {
        this(data.getX());
    }
    
    /**
     * getTime - get the timestamp
     * @return
     */
    public	long getTime()
    {
    	return nanoTime;
    }
    
    /**
     * unStamp	- return the data without the timestamp
     * @return	base data
     */
    public Data1f unStamp()
    {
        return (new Data1f(this.getX()));
    }

    /**
     * toString - return a formatted string representation for printing
     */
    public String toString()
    {
        String format = "%8.4f";
        return 	" t: " + String.format(format,((float)nanoTime)/NANOS_PER_SECF) +
                " " + super.toString();
    }

    /**
     * clone	- return a new instance with the same timestamp and values
     */
    public TimestampedData1f clone()
    {
        return new TimestampedData1f(x,nanoTime);
    }
}
