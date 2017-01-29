package dataTypes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * TimestampedData2f - 2 dimensional time stamped floating point data structure
 * Created by MAWood on 18/07/2016, modified by G.J.Wood 10/11/2016
 */
public class TimestampedData2f extends Data2f
{
	private static final long serialVersionUID = 9128847141367511460L;
	public static final long NANOS_PER_SEC = 1000000000;
    private static final float NANOS_PER_SECF = 1000000000f;
    private Instant instant;


    /**
     * TimestampedData2f    -   Constructor from 2 scalars and a time
     * @param x             -   1st dimension value
     * @param y             -   2nd dimension value
     * @param instant       -   Timestamp
     */
    public TimestampedData2f( float x, float y, Instant instant)
    {
        super (x,y);
        this.instant = instant;
    }

    /**
     * TimestampedData2f	- Constructor from 2 floats, system time is added
     * @param x             - value
     * @param y             - value
     */
    public TimestampedData2f(float x, float y)
    {
        this(x, y, Instant.now());
    }

    /**
      * TimestampedData2f	- Constructor Data2f, system time is added
     * @param data          -   2 dimensional value
     */
    public TimestampedData2f(Data2f data)
    {
        this(data.getX(),data.getY());
    }
    
    /**
     * getTime - get the timestamp
     * @return  - timestamp
     */
    public	long getTime()
    {
    	return instant.getNano()+NANOS_PER_SEC*instant.getEpochSecond();
    }

    /**
     * getInstant
     * @return  The timestamp Instant
     */
    public Instant getInstant() { return instant;}

    /**
     * unStamp	- return the data without the timestamp
     * @return	- base data
     */
    public Data2f unStamp()
    {
        return (new Data2f(this.getX(),this.getY()));
    }

    /**
     * toString - return a formatted string representation for printing
     */
    public String toString()
    {
        String format = "%08.3f";
        return 	" t: " + String.format(format,((float)this.getTime())/NANOS_PER_SECF) +
                " " + super.toString();
    }
    
    /**
     * clone	- return a new instance with the same timestamp and values
     */
     @SuppressWarnings("MethodDoesntCallSuperMethod")
     public TimestampedData2f clone()
    {
        return new TimestampedData2f(x,y,instant);
    }

    /**
     * getTimeStr   -   gets a localised pritable string for the time
     * @return      -   time displayed to the nearest milliscond
     */
    public String getTimeStr()
    {
        DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                .withLocale( Locale.UK )
                .withZone( ZoneId.systemDefault() );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.nnnn");
        return 	"[" +formatter.format( instant ) +"] " ;
    }
}
