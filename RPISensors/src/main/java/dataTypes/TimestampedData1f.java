package dataTypes;

/**
 * TimestampedData1f - 1 dimension time stamped floating point data structure
 * Created by MAWood on 18/07/2016, modified by G.J.Wood 10/11/2016
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public class TimestampedData1f extends Data1f
{
 	private static final long serialVersionUID = -1083356277253192515L;
	public final long NANOS_PER_SEC = 1000000000;
    private static final float NANOS_PER_SECF = 1000000000f;
    private final long nanoTime;

    /**
     * TimestampedData1f	- Constructor
     * @param x             - value
     * @param nanoTime      - Timestamp
     */
    public TimestampedData1f(float x, long nanoTime)
    {
        super(x);
        this.nanoTime = nanoTime;
    }
    
    /**
     * TimestampedData1f	- Constructor
     * @param x - value
     */
    public TimestampedData1f(float x)
    {
        this(x, System.nanoTime());
    }

    /**
     * TimestampedData1f	- Constructor
     * @param data          - 3 dimensional data (only X used)
     */
    public TimestampedData1f(Data3f data)
    {
        this(data.getX());
    }
    
    /**
     * getTime  - get the timestamp
     * @return  - timestamp
     */
    public	long getTime()
    {
    	return nanoTime;
    }
    
    /**
     * unStamp	- return the data without the timestamp
     * @return	- base data
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
        String format = "%08.3f";
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
