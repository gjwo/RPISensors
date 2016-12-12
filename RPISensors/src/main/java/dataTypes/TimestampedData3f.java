package dataTypes;

import java.io.Serializable;

/**
 * TimestampedData3f - 3 dimensional time stamped floating point data structure
 * Created by MAWood on 18/07/2016, modified by G.J.Wood 10/11/2016
 */
public class TimestampedData3f extends Data3f implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6056118215941025501L;
	public static final long NANOS_PER_SEC = 1000000000;
    public static final float NANOS_PER_SECF = 1000000000f;
    protected final long nanoTime;

    /**
     * TimestampedData3f	- Constructor from 3 scalars and a time
     * @param x
     * @param y
     * @param z
     * @param nanoTime
     */
    public TimestampedData3f(float x, float y, float z, long nanoTime)
    {
        super(x, y, z);
        this.nanoTime = nanoTime;
    }

    /**
     * TimestampedData3f	- Constructor from 3 scalars with a time added internally
     * @param x
     * @param y
     * @param z
     */
    public TimestampedData3f(float x, float y, float z)
    {
        this(x, y, z, System.nanoTime());
    }

    /**
     * TimestampedData3f	- Constructor from Data3f with a time added internally
     * @param data
     */
    public TimestampedData3f(Data3f data)
    {
        this(data.getX(),data.getY(),data.getZ());
    }
    
    public TimestampedData3f()
    {
    	super();
    	this.nanoTime = System.nanoTime();
    }
    /**
     * getTime - get the timestamp
     * @return
     */
    public long getTime()
    {
    	return nanoTime;
    }

    /**
     * unStamp	- return the data without the timestamp
     * @return	base data
     */
    public Data3f unStamp()
    {
        return (new Data3f(this.getX(),this.getY(),this.getZ()));
    }

    /**
     * toString - return a formatted string representation for printing
     */
    public String toString()
    {
        String format = "%08.3f";
        return 	"[" + String.format(format,((float)nanoTime)/NANOS_PER_SECF) +
                "] " + super.toString();
    }

    public String toCSV()
    {
        return 	this.getTime() + "," + super.toCSV();
    }
    
    /**
     * clone	- return a new instance with the same timestamp and values
     */
    public TimestampedData3f clone()
    {
        return new TimestampedData3f(x,y,z,nanoTime);
    }
}