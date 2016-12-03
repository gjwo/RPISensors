package inertialNavigation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import inertialNavigation.Client.NavRequestType;
public class NavResponder extends Thread
{
    public enum NavResponderMode {SINGLE,STREAM}
    private static final int serverPortNbr = 9876;
    private static final int bufferSize = 256;
    private ArrayList<Client> clients;
    private DatagramSocket socket = null;
    private int debugLevel;
    private Navigate nav;

    public NavResponder(Navigate nav,String name,NavResponderMode mode, int debugLevel) throws IOException {
        super(name);
        clients = new ArrayList<>();
        if (debugLevel >=3) System.out.println("NavResponder Constructor");
        socket = new DatagramSocket(serverPortNbr);
        this.debugLevel = debugLevel;
        this.nav = nav;
    }

    public void run() {
        if (debugLevel >=2) System.out.println("NavResponder run");
        byte[] buf = new byte[bufferSize];
        try
        {
            socket.setSoTimeout(10000);
        } catch (SocketException e)
        {
            e.printStackTrace();
        }
        DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
        while (!Thread.interrupted()) {
            try {
                socket.receive(inPacket); //wait for a client request
                registerClient(inPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(Client client:clients) client.stop();
        socket.close();
        if (debugLevel >=2) System.out.println("End NavResponder run");
    }

    private void registerClient(DatagramPacket packet)
    {
        if (debugLevel >=3) System.out.println("registerClient");
        String received = new String(packet.getData(), 0, packet.getLength());

        //registration packet may optionally contain the type of data required, default TAIT_BRYAN angles - yaw, pitch & roll
        NavRequestType reqType = NavRequestType.TAIT_BRYAN;
        if (received.length()!=0)
            try {reqType = NavRequestType.valueOf(received);}
            catch (IllegalArgumentException e){ reqType = NavRequestType.TAIT_BRYAN;}

        Client client = new Client(packet.getAddress(),packet.getPort(),packet.getAddress().getHostName(), socket, reqType);
        for(Client existingClient:clients) if(client.toString().equals(existingClient.toString())) return;
        new Thread(client).start();
        nav.registerInterest(client);
        clients.add(client);
        System.out.println("Client registered: " + client.toString());
    }
}