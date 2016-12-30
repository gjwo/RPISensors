package dataTypes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import main.Main;

/**
 * TimestampedData
 * Created by G.J Wood on 09/11/2016.
 */
public abstract class TimestampedData <E> extends Data <E>
{
    public static final long NANOS_PER_SEC = 1000000000;
    protected final Instant instant;
    protected Data <E> data;
    
    /**
     * TimestampedData	-	Constructor
     * @param data		-	the data	
     * @param nanoTime	-	a timestamp	
     */    
    public TimestampedData(Data <E> data, Instant instant)
    {
        this.instant = instant;
        this.data = data;
    }

    /**
     * TimestampedData	-	Constructor
     * @param data		-	the data	
     */    
    public TimestampedData(Data <E> data){this(data, Main.getMain().getClock().instant());}
    
    public Data <E> unStamp(){return data;}
    public Instant time() {return instant;}
    public void add(Data <E> data){this.data = data.clone();}	//Adds a new data object via clone of the object whilst keeping the original timestamp 

    public String toString()
    {
        DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
		                 .withLocale( Locale.UK )
		                 .withZone( ZoneId.systemDefault() );
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.nnnn");
        String timeStr = formatter.format( instant );
        return 	"[" +timeStr +"] " + data.toString();
    }
    
    // methods that must be implemented when the type of data is known
   public abstract TimestampedData <E> clone();    //clones the timestamp object, including cloning the data
}