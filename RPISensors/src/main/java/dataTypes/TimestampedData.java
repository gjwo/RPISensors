package dataTypes;

/**
 * TimestampedData
 * Created by G.J Wood on 09/11/2016.
 */
public abstract class TimestampedData <E> extends Data <E>
{
    public static final long NANOS_PER_SEC = 1000000000;
    protected final long nanoTime;
    protected Data <E> data;
    
    /**
     * TimestampedData	-	Constructor
     * @param data		-	the data	
     * @param nanoTime	-	a timestamp	
     */    
    public TimestampedData(Data <E> data, long nanoTime)
    {
        this.nanoTime = nanoTime;
        this.data = data;
    }

    /**
     * TimestampedData	-	Constructor
     * @param data		-	the data	
     */    
    public TimestampedData(Data <E> data){this(data, System.nanoTime());}
    
    public Data <E> unStamp(){return data;}
    public long time() {return nanoTime;}
    public void add(Data <E> data){this.data = data.clone();}	//Adds a new data object via clone of the object whilst keeping the original timestamp 

    public String toString()
    {
        String format = "%+08.3f";
        return 	"[" + String.format(format,(float)(nanoTime/(float)NANOS_PER_SEC)) +"] " + data.toString();
    }
    
    // methods that must be implemented when the type of data is known
   public abstract TimestampedData <E> clone();    //clones the timestamp object, including cloning the data
}