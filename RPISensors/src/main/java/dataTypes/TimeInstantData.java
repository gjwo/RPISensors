package dataTypes;

import java.io.Serializable;
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
public abstract class TimeInstantData <E> extends Data <E> implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3141678301674560641L;
    protected final Instant instant;
    protected Data <E> data;
    
    /**
     * TimeInstantData	-	Constructor
     * @param data		-	the data	
     * @param nanoTime	-	a timestamp	
     */    
    public TimeInstantData(Data <E> data, Instant instant)
    {
        this.instant = instant;
        this.data = data;
    }

    /**
     * TimeInstantData	-	Constructor
     * @param data		-	the data	
     */    
    public TimeInstantData(Data <E> data){this(data, Main.getMain().getClock().instant());}
    
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
   public abstract TimeInstantData <E> clone();    //clones the timestamp object, including cloning the data
}