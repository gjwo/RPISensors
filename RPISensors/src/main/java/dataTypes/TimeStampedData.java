package dataTypes;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import main.Main;

public abstract class TimeStampedData<E> implements TimeStamped, Serializable
{
    private static final long serialVersionUID = -5631520531456943338L;
    private final long NANOS_PER_SEC = 1000000000;
    private E data;
    private final Instant timestamp;

    //Constructors
    public TimeStampedData(E data)
    {
        this(data, Main.getMain().getClock().instant());
    }

    public TimeStampedData(E data, Instant time)
    {
        this.data = data;
        this.timestamp = time;
    }

    //getters
    public E getData()
    {
        return data;
    }
    //TimeStamped implementation
    public Instant time()
    {
        return timestamp;
    }
    public long getNano()
    {
        return timestamp.getNano() + NANOS_PER_SEC * timestamp.getEpochSecond();
    }
    /**
     * getTimeStr   -   gets a localised printable string for the time
     *
     * @return -   time displayed to the nearest millisecond
     */
    public String getTimeStr()
    {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.UK)
                .withZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.nnnn");
        return "[" + formatter.format(timestamp) + "] ";
    }

    // setter
    public void setData(E data)
    {
        this.data = data;
    }

    public abstract TimeStampedData<E> clone();
}