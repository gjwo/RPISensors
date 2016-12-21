package devices.encoder;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.time.Instant;

public class Encoder
{
//RaspiPin.GPIO_13
//RaspiPin.GPIO_14
    public enum Direction
    {
        FORWARDS,
        BACKWARDS
    }
    Instant start;
    private long pin1Count;
    private long pin2Count;
    private long lastPin1Count;
    private long lastPin2Count;

    Direction direction;
	
	public Encoder(Pin p1, Pin p2)
	{
		start =  Instant.now();
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput RH1 =
                gpio.provisionDigitalInputPin(p1, "RH1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput RH2 =
                gpio.provisionDigitalInputPin(p2, "RH2", PinPullResistance.PULL_DOWN);

        RH1.setShutdownOptions(true);
        RH2.setShutdownOptions(true);
        direction = Direction.FORWARDS;

        RH1.addListener((GpioPinListenerDigital) event ->
        {
            // display pin state on console
        	pin1Count++;
            if(lastPin2Count == pin2Count) direction = Direction.FORWARDS;
            lastPin2Count = pin2Count;
            //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
        RH2.addListener((GpioPinListenerDigital) event ->
        {
        	pin2Count++;
            if(lastPin1Count == pin1Count) direction = Direction.BACKWARDS;
            lastPin1Count = pin1Count;
            // display pin state on console
            //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
	}

	//Getters
	public long getPin1Count() {return pin1Count;}
	public long getPin2Count() {return pin2Count;}
	public Direction getDirection()	{return direction;}
	public Instant getStart() {return start;}
}