package comms;
import java.io.*;
import java.net.*;

public class NavClient {
	private static final int serverPortNbr = 9876;

	public NavClient() {
		// TODO Auto-generated constructor stub
	}
    public static void main(String[] args) throws IOException {
    	 
        if (args.length != 1) {
             System.out.println("Usage: java NavClient <hostname>");
             return;
        }
 
            // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
 
            // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPortNbr);
        socket.send(packet);
        while (true)
        {
                        // get response
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
 
                    // display response
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Data: " + received);
        }
        //socket.close();
    }
}