package devices.motors;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import devices.controller.PIDInputProvider;
import main.Main;

/**
 * Encoder	Class handling the device encoding rotation in a motor including calculating
 * 			displacement and speed
 * Written by M.A.Wood
 */
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
	private double metresPerRotation;
	private double velocity;
	private double displacement;
	private double totalDisplacement;
	private final boolean reversed;

	private volatile PinState lastBState;

	private volatile long rotations;
	private Instant lastTime;

	/**
	 * Encoder	- 	Constructor
	 * @param a						GPIO pin for hall detector A
	 * @param b						GPIO pin for hall detector A
	 * @param name					Name root for pin names
	 * @param metersPerRotation		How far each rotation of th emoter move the vehicle
	 * @param reversed				True if forward rotation moves the vehicle backwards
	 */
	public Encoder(Pin a, Pin b, String name, double metersPerRotation, boolean reversed)
	{
		this.clock = Main.getMain().getClock();
		this.reversed = reversed;
        this.direction = Direction.CLOCKWISE;
        this.metresPerRotation = metersPerRotation;
        this.rotations= 0;
        this.velocity = 0;
        this.displacement = 0;
        this.totalDisplacement = 0;
        lastTime = Instant.now(clock);
		lastBState = PinState.LOW;
        
        final GpioController gpio = GpioFactory.getInstance();
		this.a = gpio.provisionDigitalInputPin(a, name+"1", PinPullResistance.PULL_DOWN);
        this.b = gpio.provisionDigitalInputPin(b, name+"2", PinPullResistance.PULL_DOWN);
        this.a.setShutdownOptions(true);
        this.b.setShutdownOptions(true);
        
        // do this last so interrupts cannot be received before its ready
        this.a.addListener(this);
        this.b.addListener(this);
	}

	/**
	 * Calculate -	calculate displacement, total displacement and velocity based on rotations,
	 * 				reset rotation count.
	 */
	private void calculate()
	{
		displacement = rotations*metresPerRotation;
		totalDisplacement += displacement;
		Instant t = Instant.now(clock);
		velocity = displacement/((double)ChronoUnit.NANOS.between(lastTime, t)/1000000000d);
		rotations = 0;
		lastTime = t;
	}

	// GpioPinListenerDigital
	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
	{
		if(event.getPin() == b)
		{
			lastBState = event.getState();
		}else
		{
			if(event.getEdge() == PinEdge.RISING)
			{
				direction = lastBState==PinState.HIGH? Direction.CLOCKWISE : Direction.ANTI_CLOCKWISE;
				rotations+= reversed?(direction==Direction.CLOCKWISE?-1:1):(direction==Direction.CLOCKWISE?1:-1);
			/*Direction perceived = b.isHigh()? Direction.CLOCKWISE : Direction.ANTI_CLOCKWISE;
			if(perceived == lastDirection)
			{
				direction = perceived;
			}
			else lastDirection = perceived;*/
			}
		}
	}
	// PIDInputProvider
	@Override
	public double getInput()
	{
		calculate();
		return velocity;
	}

	// getters
	public double getVelocity() {return velocity;}
	public double getDisplacement() {return displacement;}
	public void resetTotalDisplacement() {this.totalDisplacement = 0;}
	public double getTotalDisplacement() {return totalDisplacement;}
	public Instant getLastCalcTime(){return lastTime;}
	public Direction getDirection() {return direction;}
}