package devices.encoder;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import dataTypes.NanoClock;
import devices.controller.PIDInputProvider;

public class Encoder implements GpioPinListenerDigital, PIDInputProvider
{
	public enum Direction
	{
		CLOCKWISE,
		ANTI_CLOCKWISE
	}
	private final Clock clock;
	private final GpioPinDigitalInput a;
	private final GpioPinDigitalInput b;
	
	private Direction direction;
	private Direction lastDirection;
	private double metresPerRotation;
	private double velocity;
	private double displacement;
	private double totalDisplacement;

	private final boolean reversed;

	private volatile long rotations;
	private Instant lastTime;
	
	public Encoder(Pin a, Pin b, String name, double metersPerRotation, boolean reversed)
	{
		this.clock = new NanoClock();
		this.reversed = reversed;
        this.direction = Direction.CLOCKWISE;
        this.metresPerRotation = metersPerRotation;
        this.rotations= 0;
        this.velocity = 0;
        this.displacement = 0;
        this.totalDisplacement = 0;
        lastTime = Instant.now(clock);
        
        final GpioController gpio = GpioFactory.getInstance();
		this.a = gpio.provisionDigitalInputPin(a, name+"1", PinPullResistance.PULL_DOWN);
        this.b = gpio.provisionDigitalInputPin(b, name+"2", PinPullResistance.PULL_DOWN);
        this.a.setShutdownOptions(true);
        this.b.setShutdownOptions(true);
        
        // do this last so interrupts cannot be received before its ready
        this.a.addListener(this);
	}

	private void calculate()
	{
		displacement = rotations*metresPerRotation;
		totalDisplacement += displacement;
		Instant t = Instant.now(clock);
		velocity = displacement/((double)ChronoUnit.NANOS.between(lastTime, t)/1000000000d);
		rotations = 0;
		lastTime = t;
	}
	
	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		if(event.getEdge() == PinEdge.RISING)
		{
			rotations+= reversed?(direction==Direction.CLOCKWISE?-1:1):(direction==Direction.CLOCKWISE?1:-1);
			Direction perceived = b.isHigh()? Direction.CLOCKWISE : Direction.ANTI_CLOCKWISE;
			if(perceived == lastDirection) 
			{
				direction = perceived;
			} 
			else lastDirection = perceived;
		}
	}

	@Override
	public double getInput()
	{
		calculate();
		return velocity;
	}

	public double getVelocity() {return velocity;}
	public double getDisplacement() {return displacement;}
	public void resetTotalDisplacement() {this.totalDisplacement = 0;}
	public double getTotalDisplacement() {return totalDisplacement;}
	public Instant getLastCalcTime(){return lastTime;}
	public Direction getDirection() {return direction;}
}