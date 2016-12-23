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
    	Long period = 200L;
    	leftEncoder = new Encoder(RaspiPin.GPIO_13,RaspiPin.GPIO_14,"LH",427.5f);
    	rightEncoder = new Encoder(RaspiPin.GPIO_10,RaspiPin.GPIO_11,"RH",427.5f);
        while(true)
        {
            TimeUnit.MILLISECONDS.sleep(period);
            //System.out.print("Direction: " + leftEncoder.getDirection().name() + " A: " + leftEncoder.getPin1Count() + " B: " + leftEncoder.getPin2Count());
            //System.out.println(" Direction: " + rightEncoder.getDirection().name() + " C: " + rightEncoder.getPin1Count() + " D: " + rightEncoder.getPin2Count());
            //System.out.println("Changes in Direction");
            //leftEncoder.printDirectionChanges(period*1000);
            //System.out.println("Events in Period");           
            //leftEncoder.printRecentEvents(period);
            leftEncoder.calculate();
            rightEncoder.calculate();
            String ts = leftEncoder.getTime().toString().substring(17, 27);
            System.out.print("Time: " + ts + " "+leftEncoder.getDirection().name()+ " L D:" + leftEncoder.getDistance() + " L S:"+leftEncoder.getSpeed());
            System.out.println(" | " + rightEncoder.getDirection().name() + " R D:" + rightEncoder.getDistance() + " R S:"+rightEncoder.getSpeed());
            //leftEncoder.printDirectionChanges(period);
            //leftEncoder.printPinEventsSinceDirectionChange();
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        new EncoderTest();
    }
}
