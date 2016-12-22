package devices.encoder;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import dataTypes.CircularArrayRing;

import java.time.Duration;
import java.time.Instant;

public class Encoder
{
    public enum Direction
    {
        FORWARDS,
        BACKWARDS
    }

    class timedEvent
    {
    	private final Instant eTime;
    	private final PinState state;
    	timedEvent(PinState s)
    	{
    		this.eTime = Instant.now();
    		this.state = s;
    	}
		Instant geteTime() {return eTime;}
		PinState getState() {return state;} 	
    }
    
    private final CircularArrayRing <timedEvent> pin1Events;
    private final CircularArrayRing <timedEvent> pin2Events;
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
	
	public Encoder(Pin p1, Pin p2, String n, float rpm)
	{
		this.pin1Events = new CircularArrayRing<>(500); 
		this.pin2Events = new CircularArrayRing<>(500); 
		this.start =  Instant.now();
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
        final GpioPinDigitalInput RH1 =
                gpio.provisionDigitalInputPin(p1, name+"1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput RH2 =
                gpio.provisionDigitalInputPin(p2, name+"2", PinPullResistance.PULL_DOWN);

        RH1.setShutdownOptions(true);
        RH2.setShutdownOptions(true);
        RH1.addListener((GpioPinListenerDigital) event ->
        {
        	pin1EventCount++;
            if(lastPin2EventCount == pin2EventCount) direction = Direction.FORWARDS;
            lastPin2EventCount = pin2EventCount;
            pin1Events.add(new timedEvent(event.getState()));
        });
        RH2.addListener((GpioPinListenerDigital) event ->
        {
        	pin2EventCount++;
            if(lastPin1EventCount == pin1EventCount) direction = Direction.BACKWARDS;
            lastPin1EventCount = pin1EventCount;
            pin2Events.add(new timedEvent(event.getState()));
        });
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
		speedCalcDuration = Duration.between(t1,Instant.now());
		//TODO
		return distance;
	}
	
	public float calculateAverageSpeedSince(Instant t1)
	{
		if (Duration.between(t1,Instant.now()).toMillis()<1) return -1;
		calculateDistanceSince(t1);
		lastSpeed = speed;
		lastSpeedCalcDuration = speedCalcDuration;
		speedCalcDuration = Duration.between(t1,Instant.now());
		speed = distance/ ((float)speedCalcDuration.toMillis()/1000);
		return speed;
	}
}