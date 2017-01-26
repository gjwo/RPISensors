package dataTypes;

import java.io.Serializable;
import java.time.Instant;

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
    private static final float NANOS_PER_SECF = 1000000000f;
    private Instant instant;


    /**
     * TimestampedData3f	- Constructor from 3 scalars with a time added internally
     * @param x             - value
     * @param y             - value
     * @param z             - value
     */
    public TimestampedData3f(float x, float y, float z)
    {
        this(x, y, z, Instant.now());
    }

    /**
     * TimestampedData2f    -   Constructor from 2 scalars and a time
     * @param x             -   1st dimension value
     * @param y             -   2nd dimension value
     * @param z             -   3rd dimension value
     * @param instant       -   Timestamp
     */
    public TimestampedData3f( float x, float y, float z, Instant instant)
    {
        super (x,y,z);
        this.instant = instant;
    }

    /**
     * TimestampedData3f	- Constructor from Data3f with a time added internally
     * @param data          - 3 dimensional value
     */
    public TimestampedData3f(Data3f data)
    {
        this(data.getX(),data.getY(),data.getZ());
    }

    public TimestampedData3f()
    {
        super();
        instant = Instant.now();
    }

    /**
     * getInstant
     * @return  The timestamp Instant
     */
    public Instant getInstant() { return instant;}

    /**
     * getTime  - get the timestamp
     * @return  - timestamp
     */
    public long getTime()
    {
    	return instant.getNano();
    }

    /**
     * unStamp	- return the data without the timestamp
     * @return	- base data
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
        return 	"[" + String.format(format,((float)instant.getNano())/NANOS_PER_SECF) +
                "] " + super.toString();
    }

    public String toCSV()
    {
        return 	this.getTime() + "," + super.toCSV();
    }
    
    /**
     * clone	- return a new instance with the same timestamp and values
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TimestampedData3f clone()
    {
        return new TimestampedData3f(x,y,z,instant);
    }
}