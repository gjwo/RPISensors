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

    class TimedEvent
    {
    	private final Instant eTime;
    	private final PinState state;
    	private final GpioPin pin;
    	TimedEvent(PinState s, GpioPin p)
    	{
    		this.eTime = Instant.now(clock);
    		this.state = s;
    		this.pin = p;
    	}
		Instant geteTime() {return eTime;}
		PinState getState() {return state;} 
		public String toString()
		{
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss.nnnnnnnnn").withZone(ZoneId.systemDefault());
			return "Event at " + formatter.format(eTime) + " on pin " + pin.getName() + " to state " + state.getName();
		}
    }
    
    private final CircularArrayRing <TimedEvent> pinEvents;
    private final Instant start;
    private final String name;
    private long pin1EventCount;
    private long pin2EventCount;
    private long lastPin1EventCount;
    private long lastPin2EventCount;
    private Direction direction;
    private float speed;
    private float lastSpeed;
    private float distance;
    private float lastDistance;
    private Duration speedCalcDuration;
    private Duration lastSpeedCalcDuration;
    private Duration distanceDuration;
    private Duration lastDistanceDuration;
    private float rotationsPerMeter;
    private final Clock clock;
	
	public Encoder(Pin p1, Pin p2, String n, float rpm)
	{
		this.clock = new NanoClock();
		this.pinEvents = new CircularArrayRing<>(1000);
		this.start =  Instant.now(clock);
		this.name = n;
        this.direction = Direction.FORWARDS;
        this.speed = 0f;
        this.lastSpeed = speed;
        this.speedCalcDuration = Duration.ofSeconds(1);
        this.lastSpeedCalcDuration = Duration.ofSeconds(1);
        this.distance = 0f;
        this.lastDistance = 0f;
        this.rotationsPerMeter = rpm;
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput pin1 =
                gpio.provisionDigitalInputPin(p1, name+"1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput pin2 =
                gpio.provisionDigitalInputPin(p2, name+"2", PinPullResistance.PULL_DOWN);

        pin1.setShutdownOptions(true);
        pin2.setShutdownOptions(true);
        /*pin1.addListener((GpioPinListenerDigital) event ->
        {
        	pin1EventCount++;
            if(lastPin2EventCount == pin2EventCount) direction = Direction.FORWARDS;
            lastPin2EventCount = pin2EventCount;
            pin1Events.add(new timedEvent(event.getState()));
        });
        pin2.addListener((GpioPinListenerDigital) event ->
        {
        	pin2EventCount++;
            if(lastPin1EventCount == pin1EventCount) direction = Direction.BACKWARDS;
            lastPin1EventCount = pin1EventCount;
            pin2Events.add(new timedEvent(event.getState()));
        });*/
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

	//Calculations
	public float calculateDistanceSince(Instant t1)
	{
		lastDistance = distance;
		lastDistanceDuration = distanceDuration;
		speedCalcDuration = Duration.between(t1,Instant.now(clock));
		//TODO
		return distance;
	}
	
	public float calculateAverageSpeedSince(Instant t1)
	{
		if (Duration.between(t1,Instant.now(clock)).toMillis()<1) return -1;
		calculateDistanceSince(t1);
		lastSpeed = speed;
		lastSpeedCalcDuration = speedCalcDuration;
		speedCalcDuration = Duration.between(t1,Instant.now(clock));
		speed = distance/ ((float)speedCalcDuration.toMillis()/1000);
		return speed;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		pinEvents.add(new TimedEvent(event.getState(), event.getPin()));
	}
	
	public void printEvents()
	{
		Iterator it = pinEvents.iterator();
		while (it.hasNext())
		{
			System.out.println(it.next().toString());
		}
	}
}