package inertialNavigation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import sensors.interfaces.UpdateListener;

public class NavResponder extends Thread
{
	private static final int serverPortNbr = 9876;
	private static final int bufferSize = 256;
	private ArrayList<Client> clients;
    private DatagramSocket socket = null;
    private boolean stop;
    public enum NavResponderMode {SINGLE,STREAM}
    private int debugLevel;
    private Navigate nav;
 
    public NavResponder(Navigate nav) throws IOException {
    this(nav,"NavResponder rpi3gjw",NavResponderMode.SINGLE,3);
    }
    
 
    public NavResponder(Navigate nav,String name,NavResponderMode mode, int debugLevel) throws IOException {
        super(name);
        clients = new ArrayList<>();
    	if (debugLevel >=3) System.out.println("NavResponder Constructor");
        socket = new DatagramSocket(serverPortNbr);
        this.debugLevel = debugLevel;
        this.nav = nav;
        this.stop = false;
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
                registerClient(inPacket);
            } catch (IOException e) {
            	stop = true;
                e.printStackTrace();
            }
        }
        socket.close();
    	if (debugLevel >=2) System.out.println("End NavResponder run");
    }
    
    private void registerClient(DatagramPacket packet)
    {
    	if (debugLevel >=3) System.out.println("registerClient");
    	Client client = new Client(packet.getAddress(),packet.getPort(),packet.getAddress().getHostName(), socket);
    	for(Client existingClient:clients) if(client.toString().equals(existingClient.toString())) return;
    	new Thread(client).start();
    	nav.registerInterest(client);
        clients.add(client);
        System.out.println("Client registered: " + client.toString());
    }
}