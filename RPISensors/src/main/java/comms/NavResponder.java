package comms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NavResponder extends Thread{
	private static final int serverPortNbr = 9876;
	private static final int bufferSize = 256;
	private InetAddress clientAddress;
	private int clientPort;
	private String clientName;
	private boolean clientRegistered;
    private DatagramSocket socket = null;
    private boolean stop = false;
    public enum NavResponderMode {SINGLE,STREAM}
    private NavResponderMode mode;
    private int debugLevel;
 
    public NavResponder() throws IOException {
    this("NavResponder rpi3gjw",NavResponderMode.SINGLE,3);
    }
    
 
    public NavResponder(String name,NavResponderMode mode, int debugLevel) throws IOException {
        super(name);
        this.mode = mode;
        socket = new DatagramSocket(serverPortNbr);
        clientRegistered = false;
        this.debugLevel = debugLevel;
        
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
                    reading = getNextReading();
                    sendReadingToClient(reading);
                    break; //go round and wait for another request
                case STREAM:
                	streamReadingsToClient();
                	break;
                }
               
            } catch (IOException e) {
            	stop = true;
                e.printStackTrace();
            }
        }
        socket.close();
    	if (debugLevel >=2) System.out.println("End NavResponder run");
    }
 
    private String getNextReading() {
    	if (debugLevel >=3) System.out.println("getNextReading");
        String returnValue = "Hello from Nav Responder";
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
       	if (debugLevel >=3) System.out.println("streamReadingsToClient");
       	System.out.println("Can't handle stream yet");
    }
}