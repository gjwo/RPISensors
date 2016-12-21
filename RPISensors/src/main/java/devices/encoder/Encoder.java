package devices.encoder;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Encoder
{
//RaspiPin.GPIO_13
//RaspiPin.GPIO_14
	public Encoder(Pin p1, Pin p2)
	{
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput RH1 =
                gpio.provisionDigitalInputPin(p1, "RH1", PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput RH2 =
                gpio.provisionDigitalInputPin(p2, "RH2", PinPullResistance.PULL_DOWN);

        RH1.setShutdownOptions(true);
        RH2.setShutdownOptions(true);
	}

}
