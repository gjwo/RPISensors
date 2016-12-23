package devices.encoder;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import dataTypes.CircularArrayRing;
import dataTypes.NanoClock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class Encoder implements GpioPinListenerDigital
{
    public enum Direction
    {
        FORWARDS,
        BACKWARDS
    }

    public class TimedEncoderEvent
    {
    	private final Instant eTime;
    	private final PinState state;
    	private final GpioPin pin;
    	TimedEncoderEvent(PinState s, GpioPin p)
    	{
    		this.eTime = Instant.now(clock);
    		this.state = s;
    		this.pin = p;
    	}
    	//Getters
		public Instant getTime() {return eTime;}
		public PinState getState() {return state;} 
		public GpioPin getPin() {return pin;}
		
		public String toString()
		{
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss.nnnnnnnnn").withZone(ZoneId.systemDefault());
			return "Event at " + formatter.format(eTime) + " on pin " + pin.getName() + " to state " + state.getName();
		}
		
    }
    
    public class TimedDirectionEvent
    {
    	private final Instant eTime;
    	private final Direction direction;
    	private final long eCount;
    	TimedDirectionEvent(Instant t, Direction d, long c)
    	{
    		this.eTime = t;
    		this.direction = d;
    		this.eCount = c;
    	}
    	//Getters
		public Instant getTime() {return eTime;}
		public Direction getDirection() {return direction;} 
		public long getCount() {return eCount;} 
		
		public String toString()
		{
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss.nnnnnnnnn").withZone(ZoneId.systemDefault());
			return "Direction change at " + formatter.format(eTime) + " after "+ eCount+" events to " + direction.name();
		}
    }
    
    class DirectionChange extends Exception
    {
		private static final long serialVersionUID = 1L;  	
    }
    
    private final long DEFAULT_INTERVAL_MS = 100; 
    private final CircularArrayRing <TimedEncoderEvent> pinEvents;
    private CircularArrayRing <TimedDirectionEvent> directionEvents;
    private final Instant start;
    private final String name;
    private long pin1EventCount;
    private long pin2EventCount;
    private long lastPin1EventCount;
    private long lastPin2EventCount;
    private Direction direction;
    private Direction lastDirection;
    private float speed;
    private float lastSpeed;
    private float distance;
    private float lastDistance;
    private Instant time;
    private Instant lastTime;
    private Duration speedCalcDuration;
    private Duration lastSpeedCalcDuration;
    private Duration distanceDuration;
    private Duration lastDistanceDuration;
    private float motorRotationsPerMetre;
    private float motorRotationsPerTrackWheelR;
    private float TrackWheelRotationsPerMetre;
    
    private final Clock clock;
	
	public Encoder(Pin p1, Pin p2, String n, float rpm)
	{
		this.clock = new NanoClock();
		this.pinEvents = new CircularArrayRing<>(1000);
		this.directionEvents = new CircularArrayRing<>(1000);
		this.start =  Instant.now(clock);
		this.name = n;
        this.direction = Direction.FORWARDS;
        this.speed = 0f;
        this.lastSpeed = speed;
        this.speedCalcDuration = Duration.ofSeconds(1);
        this.lastSpeedCalcDuration = Duration.ofSeconds(1);
        this.distance = 0f;
        this.lastDistance = 0f;
        this.TrackWheelRotationsPerMetre = 5.7f;
        this.motorRotationsPerTrackWheelR = 75f;
        this.motorRotationsPerMetre = rpm;
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput pin1 =
                gpio.provisionDigitalInputPin(p1, name+"1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput pin2 =
                gpio.provisionDigitalInputPin(p2, name+"2", PinPullResistance.PULL_DOWN);

        pin1.setShutdownOptions(true);
        pin2.setShutdownOptions(true);
        pin1.addListener(this);
        pin2.addListener(this);
	}

	//Getters
	public long getPin1Count() {return pin1EventCount;}
	public long getPin2Count() {return pin2EventCount;}
	public Direction getDirection()	{return direction;}
	public Instant getStart() {return start;}
	public String getName() {return name;}
	public long getLastPin1Count() {return lastPin1EventCount;}
	public long getLastPin2Count() {return lastPin2EventCount;}
	public float getSpeed()	{return speed;}
	public float getLastSpeed()	{return lastSpeed;}
	public float getDistance() {return distance;}
	public float getLastDistance() {return lastDistance;}
	public Duration getSpeedCalcDuration() {return speedCalcDuration;}
	public Duration getLastSpeedCalcDuration() {return lastSpeedCalcDuration;}
	public Duration getDistanceDuration() {return distanceDuration;}
	public Duration getLastDistanceDuration() {return lastDistanceDuration;}
	public TimedDirectionEvent getLastDirectionChange() {return directionEvents.get(0);}
	public TimedEncoderEvent getLastPinEvent() {return pinEvents.get(0);}

	public float getMotorRotationsPerMetre()
	{
		return motorRotationsPerMetre;
	}

	public void setMotorRotationsPerMetre(float motorRotationsPerMetre)
	{
		this.motorRotationsPerMetre = motorRotationsPerMetre;
	}

	public float getMotorRotationsPerTrackWheelR()
	{
		return motorRotationsPerTrackWheelR;
	}

	public void setMotorRotationsPerTrackWheelR(float motorRotationsPerTrackWheelR)
	{
		this.motorRotationsPerTrackWheelR = motorRotationsPerTrackWheelR;
	}

	public long getDEFAULT_INTERVAL_MS()
	{
		return DEFAULT_INTERVAL_MS;
	}

	public Instant getTime()
	{
		return time;
	}

	public Instant getLastTime()
	{
		return lastTime;
	}

	public float getTrackWheelRotationsPerMetre()
	{
		return TrackWheelRotationsPerMetre;
	}

	public Clock getClock()
	{
		return clock;
	}

	//Calculations
	public void calcDirectionChanges()
	{
		direction = Direction.FORWARDS;
		lastDirection = direction;
		TimedEncoderEvent event, lastEvent;
		Iterator<TimedEncoderEvent> it = pinEvents.iterator();
		long eventCount = 0;
		directionEvents = new CircularArrayRing<>(1000);
		if (!it.hasNext()) return;
		lastEvent = it.next();
		while (it.hasNext())
		{
			event = it.next();
			eventCount++;
			if (event.pin.getName().equals(lastEvent.pin.getName()))
			{
				direction = (lastDirection==Direction.FORWARDS?Direction.BACKWARDS:Direction.FORWARDS);
				lastDirection = direction;
				directionEvents.add(new TimedDirectionEvent(event.eTime,direction,eventCount));

			}
			lastEvent = event;
		}
		direction = Direction.FORWARDS;
		if (directionEvents.size()>0) direction = directionEvents.get(0).getDirection();
		if (directionEvents.size()>1) lastDirection = directionEvents.get(1).getDirection(); else lastDirection = direction;
	}

	public float calculateDistanceSince(Instant t1) throws DirectionChange
	{
		if (directionEvents.size()>0)if ( directionEvents.get(0).eTime.isAfter(t1)) throw new DirectionChange();		
		lastDistance = distance;
		float rotations = (float)CountPinEventsSince(t1)/4f;
		distance = rotations /  motorRotationsPerMetre;
		return distance;
	}
	
	public float calculateAverageSpeedSince(Instant t1) throws DirectionChange
	{
		if (Duration.between(t1,Instant.now(clock)).toMillis()<1) return -1f;
		calculateDistanceSince(t1);
		lastSpeed = speed;
		lastSpeedCalcDuration = speedCalcDuration;
		speedCalcDuration = Duration.between(t1,Instant.now(clock));
		speed = distance/ ((float)speedCalcDuration.toMillis()/1000f);
		lastTime = t1;
		return speed;
	}

	public float calculateSpeed()
	{
		time = Instant.now(clock);
		Instant startTime = time.minusMillis(DEFAULT_INTERVAL_MS);
		if (directionEvents.size()>0) if (directionEvents.get(0).getTime().isAfter(startTime)) startTime = directionEvents.get(0).getTime().plusMillis(1);
		try
		{
			return calculateAverageSpeedSince(startTime);
		} catch (DirectionChange e)
		{
			return -999f; //this shouldn't happen
		}
	}
	
	public void calculate()
	{
		calcDirectionChanges();
		calculateSpeed();
		//distance is calculated to work out speed
	}
	
	public long CountPinEventsSince(Instant periodStart)
	{
		Iterator<TimedEncoderEvent> it = pinEvents.iterator();
		TimedEncoderEvent event;
		long count = 0;
		while (it.hasNext())
		{
			event = it.next();
			if(event.eTime.isAfter(periodStart) )
			{
				count++ ;
			}
			else return count;
		}
		return count;
	}
	
	public float distanceSinceDirectionChange()
	{
		if (directionEvents.size()>0)
		{
			long count = directionEvents.get(0).getCount();
			return (float)count / 4 / motorRotationsPerMetre;
		} else return 0f;
	}
	
	// Listener
	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		pinEvents.add(new TimedEncoderEvent(event.getState(), event.getPin()));
	}
	
	public void printPinEvents()
	{
		Iterator<TimedEncoderEvent> it = pinEvents.iterator();
		while (it.hasNext())
		{
			System.out.println(it.next().toString());
		}
	}
	
	//print Methods
	public void printRecentPinEvents(long periodMilliSecs)
	{
		if (periodMilliSecs<=0) return;
		Instant periodStart = Instant.now(clock).minusMillis(periodMilliSecs);
		Iterator<TimedEncoderEvent> it = pinEvents.iterator();
		TimedEncoderEvent event;
		while (it.hasNext())
		{
			event = it.next();
			if(event.eTime.isAfter(periodStart) )
			{
				System.out.println(event.toString());
			}
			else return;
		}
	}
	
	public void printDirectionChanges(long periodMilliSecs)
	{
		if (periodMilliSecs<=0) return;
		TimedDirectionEvent event;
		Instant periodStart = Instant.now(clock).minusMillis(periodMilliSecs);
		Iterator<TimedDirectionEvent> it = directionEvents.iterator();
		while (it.hasNext())
		{
			event = it.next();
			if(event.eTime.isAfter(periodStart) )
			{
				System.out.println(event.toString());
			}
			else return;
		}
	}
	
	public void printPinEventsSinceDirectionChange()
	{
		long eventCount = 0;
		Iterator<TimedEncoderEvent> it = pinEvents.iterator();
		TimedEncoderEvent event, lastEvent, firstEvent;
		if (!it.hasNext()) return;
		lastEvent = it.next();
		firstEvent = lastEvent;
		eventCount++;
		while (it.hasNext())
		{
			event = it.next();
			eventCount++;
			if (event.pin.getName().equals(lastEvent.pin.getName()))
			{
				Duration d = Duration.between(event.eTime,firstEvent.eTime);
				System.out.println("Direction change at " + event.toString());
				System.out.println("Duration: " + d.toMillis() + "ms Events: " + eventCount + " Motor Rotations: " + eventCount/4 
						+ " Track Wheel Rotations: " + (float)eventCount/4f/75f);
				printRecentPinEvents(d.toMillis());
				return; //Turning point found
			}
			lastEvent = event;
		}
	}
}