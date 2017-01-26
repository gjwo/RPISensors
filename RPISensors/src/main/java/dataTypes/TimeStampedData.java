package dataTypes;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import main.Main;

public class TimeStampedData<E> implements TimeStamped, Serializable
{
	private static final long serialVersionUID = -5631520531456943338L;
	private final E data;
	private final Instant timestamp;
	
	//Constructors
	TimeStampedData(E data)
	{
		this(data,Main.getMain().getClock().instant());
	}
	
	private TimeStampedData(E data, Instant time)
	{
		this.data = data;
		this.timestamp = time;
	}
	
	//getters
	E getData() {return data;}
	
    //TimeStamped implementation
    public Instant time() {return timestamp;}
    public long getNano() {return timestamp.getNano();}

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
        return 	"[" +formatter.format( timestamp ) +"] " ;
    }
    
    // general methods
    public String toString() {return getTimeStr() + data.toString();   	
    }
}