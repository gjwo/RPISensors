package main;

import com.pi4j.io.gpio.*;

import devices.encoder.Encoder;

import java.util.concurrent.TimeUnit;

/**
 * RPISensors - main
 * Created by MAWood on 03/12/2016.
 */
public class EncoderTest
{
	private final Encoder leftEncoder;
	private final Encoder rightEncoder;

    public EncoderTest() throws InterruptedException
    {
    	leftEncoder = new Encoder(RaspiPin.GPIO_13,RaspiPin.GPIO_14,"LH",100f);
    	rightEncoder = new Encoder(RaspiPin.GPIO_10,RaspiPin.GPIO_11,"RH",100f);
        while(true)
        {
            TimeUnit.SECONDS.sleep(1);
            System.out.print("Direction: " + leftEncoder.getDirection().name() + " A: " + leftEncoder.getPin1Count() + " B: " + leftEncoder.getPin2Count());
            System.out.println(" Direction: " + rightEncoder.getDirection().name() + " C: " + rightEncoder.getPin1Count() + " D: " + rightEncoder.getPin2Count());
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        new EncoderTest();
    }
}
