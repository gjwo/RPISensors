package comms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import inertialNavigation.Instruments;
import inertialNavigation.Navigate;
import sensors.interfaces.UpdateListener;

public class NavResponder extends Thread implements UpdateListener
{
	private static final int serverPortNbr = 9876;
	private static final int bufferSize = 256;
	private InetAddress clientAddress;
	private int clientPort;
	private String clientName;
	private boolean clientRegistered;
    private DatagramSocket socket = null;
    private boolean stop = false;
    private volatile boolean dataReady;
    public enum NavResponderMode {SINGLE,STREAM}
    private NavResponderMode mode;
    private int debugLevel;
    private Navigate nav;
 
    public NavResponder(Navigate nav) throws IOException {
    this(nav,"NavResponder rpi3gjw",NavResponderMode.SINGLE,3);
    }
    
 
    public NavResponder(Navigate nav,String name,NavResponderMode mode, int debugLevel) throws IOException {
        super(name);
    	if (debugLevel >=3) System.out.println("NavResponder Constructor");
        this.mode = mode;
        socket = new DatagramSocket(serverPortNbr);
        clientRegistered = false;
        this.debugLevel = debugLevel;
        this.dataReady = false;
        this.nav = nav;
        this.nav.registerInterest(this);
    }

    public void stopNavResponder(){stop = true;}
 
    public void run() {
    	if (debugLevel >=2) System.out.println("NavResponder run");
    	String reading;
        byte[] buf = new byte[bufferSize];
    	DatagramPacket inPacket = new DatagramPacket(buf, buf.length);    	
        while (!Thread.interrupted()&&!stop) {
            try {
            	socket.receive(inPacket); //wait for a client request
                if (!clientRegistered)
                {
                	registerClient(inPacket);
                }
                //assuming only one client for now
                switch(mode)	
                {
                case SINGLE:
                	if (dataReady)
                	{
                        reading = getNextReading();
                        sendReadingToClient(reading);
                	}
                    break; //go round and wait for another request
                case STREAM:
                	streamReadingsToClient(); //doesn't come back until stop signal
                	break;
                }
               
            } catch (IOException e) {
            	stop = true;
                e.printStackTrace();
            }
        }
        sendReadingToClient("STOP,0,0,0,0");
        socket.close();
    	if (debugLevel >=2) System.out.println("End NavResponder run");
    }
 
    private String getNextReading() {
    	if (debugLevel >=3) System.out.println("getNextReading");
    	dataReady = false;
        String returnValue = "Angles,"+Instruments.getAngles().toCSV();
        return returnValue;
    }
    
    private void registerClient(DatagramPacket packet)
    {
    	if (debugLevel >=3) System.out.println("registerClient");
        clientAddress = packet.getAddress();
        clientPort = packet.getPort();
        clientName = clientAddress.getHostName();
        clientRegistered = true;
        System.out.println("Client registered: "+clientName+" Address: "+ clientAddress.getHostAddress()+" Port: "+ clientPort);
    }
    private void sendReadingToClient(String reading)
    {
    	if (debugLevel >=3) System.out.println("sendReadingToClient");

        byte[] outBuf = new byte[bufferSize];
        
        outBuf = reading.getBytes();
        // send the response to the client at "address" and "port"
    	DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length);
        outPacket = new DatagramPacket(outBuf, outBuf.length, clientAddress, clientPort);
        try {
			socket.send(outPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    private void streamReadingsToClient()
    {
    	String reading = null;
       	if (debugLevel >=3) System.out.println("streamReadingsToClient");
       	while (!stop)
       	{
       		if (dataReady && !stop)
       		{
       			reading = getNextReading();
       			sendReadingToClient(reading);
       		} else
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					stop = true;
			       	if (debugLevel >=5) System.out.println("streamReadingsToClient interrupted");
				}
       	}
       	if (debugLevel >=3) System.out.println("End streamReadingsToClient");
    }

    //update listener
	public void dataUpdated() {dataReady=true;}
}