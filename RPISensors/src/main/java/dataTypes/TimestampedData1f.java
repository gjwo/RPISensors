package dataTypes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

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
    private Instant instant;



    public TimestampedData1f( float x, Instant instant)
    {
        super (x);
        this.instant = instant;
    }
    /**
     * TimestampedData1f	- Constructor
     * @param x - value
     */
    public TimestampedData1f(float x)
    {
        this(x, Instant.now() );
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
    	return instant.getNano()+NANOS_PER_SEC*instant.getEpochSecond();
    }

    /**
     * getInstant
     * @return  the timestamp instant
     */
    public Instant getInstant(){return instant;}
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
        return 	" t: " + String.format(format,((float)this.getTime())/NANOS_PER_SECF) +
                " " + super.toString();
    }

    /**
     * clone	- return a new instance with the same timestamp and values
     */
    public TimestampedData1f clone()
    {
        return new TimestampedData1f(x,instant);
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
