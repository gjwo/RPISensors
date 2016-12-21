package main;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * RPISensors - main
 * Created by MAWood on 03/12/2016.
 */
public class EncoderTest
{

    int one = 0;
    int two = 0;

    int lastOne = 0;
    int lastTwo = 0;

    Instant start;

    Direction direction;

    enum Direction
    {
        FORWARDS,
        BACKWARDS
    }

    public EncoderTest() throws InterruptedException
    {
        final GpioController gpio = GpioFactory.getInstance();


        final GpioPinDigitalInput RH1 =
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, "RH1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput RH2 =
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, "RH2", PinPullResistance.PULL_DOWN);

        RH1.setShutdownOptions(true);
        RH2.setShutdownOptions(true);
        direction = Direction.FORWARDS;

        RH1.addListener((GpioPinListenerDigital) event ->
        {
            // display pin state on console
            one++;
            if(lastTwo == two) direction = Direction.FORWARDS;
            lastTwo = two;
            //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
        RH2.addListener((GpioPinListenerDigital) event ->
        {
            two++;
            if(lastOne == one) direction = Direction.BACKWARDS;
            lastOne = one;
            // display pin state on console
            //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
        while(true)
        {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Direction: " + direction.name() + " A: " + one + " B: " + two);
            one = 0;
            two = 0;
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        new EncoderTest();
    }
}
