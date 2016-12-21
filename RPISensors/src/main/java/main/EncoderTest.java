package main;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * RPISensors - main
 * Created by MAWood on 03/12/2016.
 */
public class EncoderTest extends Thread
{

    public static void main(String[] args) throws InterruptedException
    {
        final GpioController gpio = GpioFactory.getInstance();


        final GpioPinDigitalInput RH1 =
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, "RH1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput RH2 =
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, "RH2", PinPullResistance.PULL_DOWN);

        RH1.setShutdownOptions(true);
        RH2.setShutdownOptions(true);

        RH1.addListener((GpioPinListenerDigital) event ->
        {
            // display pin state on console
            System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
        RH2.addListener((GpioPinListenerDigital) event ->
        {
            // display pin state on console
            System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        });
    }
}
